<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

 qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 You may use qOntology for commercial purposes, but you may not remove, hide,
 modify, or replace the qOntology logo, copyright notices, license notices,
 or attribution information without a separate commercial license.

 White-label use, OEM distribution, rebranding, or presenting qOntology as
 another product requires separate commercial authorization from
 Jiangsu Qiantong Technology Co., Ltd.

 Business License: https://community.qontology.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->
<template>
  <div class="app-container" ref="app-container">
    <!-- 新用户引导独占整行，说明模型接入用途及配置顺序。 -->
    <GuideTip tip-id="basic/llm/modelMarket.list"/>
    <div class="pagecont-top">
      <!-- 卡片列表提供统一查询接口；字段重置由公共搜索栏负责，查询与重置均从第一页加载。 -->
      <QtSearchBar
        :items="searchItems"
        :params="queryParams"
        :config="{collapsedCount: 4}"
        :table-ref="searchActions"
      />
    </div>

    <div class="pagecont-main" v-loading="loading">
      <Card :data="keyList" @handleUpdate="handleUpdate" @routeTo="routeTo"/>
      <div v-if="!loading && keyList.length === 0" class="emptyBg">
        <img src="@/assets/common/images/empty-state/noData.png" alt="" />
        <p>暂无记录</p>
      </div>
    </div>

    <div v-if="total > 0" class="pagecont-bottom">
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 地址密钥列表 -->
    <el-dialog
      class="key-config-dialog"
      :title="keyTableTitle"
      v-model="showKeyOpen"
      width="800px"
      :append-to="$refs['app-container']"
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ keyTableTitle }}
        </span>
      </template>

      <el-form
        ref="handleRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-button
              type="primary"
              style="margin-bottom: 15px"
              @click="addItem"
              plain
              >新增密钥</el-button>
            <el-table :data="form.platformKeyList" style="width: 100%">
              <el-table-column label="名称" align="center" prop="name">
                <template #default="scope">
                  <el-input
                    v-model="scope.row.name"
                    type="text"
                    placeholder="请输入名称"
                  />
                </template>
              </el-table-column>

              <el-table-column
                v-if="getColumnVisibility(1)"
                label="API地址"
                align="center"
                prop="url"
              >
                <template #default="scope">
                  <el-input
                    v-model="scope.row.url"
                    type="text"
                    placeholder="请填写API地址"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="getColumnVisibility(2)"
                label="密钥"
                align="center"
                prop="apiKey"
              >
                <template #default="scope">
                  <el-input
                    v-model="scope.row.apiKey"
                    type="text"
                    placeholder="请填写密钥"
                  />
                </template>
              </el-table-column>
              <!-- 操作列 -->
              <el-table-column align="center" width="110px" label="操作">
                <template #default="scope">
                  <el-button
                    type="danger"
                    @click="deleteItem(scope.$index, scope.row)"
                    plain
                    >删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="handleClose">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Key">
import {
  listByPlatform,
  listKey,
  submitBatch,
} from "@/api/basic/llm/modelMarket/key.js";
import Card from "@/views/basic/llm/modelMarket/card.vue";
import QtSearchBar from "@/components/QtSearchBar/index.vue";

const { proxy } = getCurrentInstance();
const {ai_model_platform, ai_model_tag} = proxy.useDict('ai_model_platform', 'ai_model_tag');

// 列显隐信息
const columns = ref([
  { key: 1, label: "API 地址", visible: true },
  { key: 2, label: "密钥", visible: true },
]);
const getColumnVisibility = (key) => {
  const column = columns.value.find((col) => col.key === key);
  // 如果没有找到对应列配置，默认显示
  if (!column) return true;
  // 如果找到对应列配置，根据visible属性来控制显示
  return column.visible;
};

const showKeyOpen = ref(false);
const loading = ref(true);
const ids = ref([]);
const total = ref(0);
const keyTableTitle = ref("");
const currentPlatform = ref("");
const keyList = ref([]);
const router = useRouter();
const data = reactive({
  form: {
    platformKeyList: [],
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    name: null,
    apiKey: null,
    platform: null,
    platformTag: null,
    url: null,
    status: null,
    createTime: null,
  },
  rules: {
    apiKey: [{ required: true, message: "密钥不能为空", trigger: "blur" }],
    url: [{ required: true, message: "API地址不能为空", trigger: "blur" }],
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
  },
  modelList: [],
});

const { queryParams, form, rules } = toRefs(data);

// 适配公共搜索栏的表格接口，卡片页复用其查询流程，避免事件与自动查询重复请求。
const searchActions = {getList, resetQuery: handleQuery};

// 字典异步加载后同步更新选项，平台及能力继续沿用卡片使用的字典。
const searchItems = computed(() => [
  {label: '模型名称', prop: 'name', component: {is: 'input'}},
  {label: '模型平台', prop: 'platform', component: {is: 'select', options: ai_model_platform.value}},
  {label: '模型能力', prop: 'platformTag', component: {is: 'select', options: ai_model_tag.value}},
  // 使用数值状态，确保未配置的 0 不会在查询或重置时被误判为空。
  {label: '配置状态', prop: 'status', component: {is: 'select', options: [
    {label: '未配置', value: 0}, {label: '已配置', value: 1}
  ]}}
]);

// 仅采纳最新查询结果，避免快速切换条件时旧响应覆盖当前卡片。
let listRequest = 0;

/** 按当前名称、平台、模型能力及配置状态查询卡片，失败时结束加载并清空旧结果。 */
async function getList() {
  const request = ++listRequest;
  loading.value = true;
  try {
    const response = await listKey({...queryParams.value, name: queryParams.value.name?.trim() || undefined});
    if (request !== listRequest) return;
    keyList.value = response.data.rows || [];
    total.value = response.data.total || 0;
  } catch {
    if (request === listRequest) {
      keyList.value = [];
      total.value = 0;
    }
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    workspaceId: null,
    name: null,
    apiKey: null,
    platform: null,
    url: null,
    status: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
    keyUrl: "",
    desc: "",
  };
  proxy.resetForm("keyRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  if (row.platform === "Ollama") {
    columns.value[0].visible = true;
    columns.value[1].visible = false;
    rules.url = [
      { required: true, message: "API地址不能为空", trigger: "blur" },
    ];
  } else if (row.platform === "OpenAI") {
    columns.value[0].visible = true;
    columns.value[1].visible = true;
    rules.url = [
      { required: true, message: "API地址不能为空", trigger: "blur" },
    ];
  } else {
    columns.value[0].visible = false;
    columns.value[1].visible = true;
    delete rules.url;
  }
  keyTableTitle.value = "密钥设置";
  currentPlatform.value = row.platform;
  console.log("form.value", form.value);
  listByPlatform(row.platform).then((response) => {
    form.value.platformKeyList = response.data;
    showKeyOpen.value = true;
  });
}

const addItem = () => {
  form.value.platformKeyList.push(itemData());
};

const itemData = () => {
  return {
    platform: currentPlatform.value,
    name: "",
    apiKey: "",
    url: "",
  };
};

function handleClose() {
  reset();
  showKeyOpen.value = false;
}

// 删除操作
function deleteItem(index, row) {
  if (row.id) {
    proxy.$modal
      .confirm(`是否确认删除名称为【${row.name}】的密钥？`)
      .then(() => {
        // 根据索引删除数据
        form.value.platformKeyList.splice(index, 1);
        ids.value.push(row.id);
        // 最后一条清空不删掉
        form.value.platformKeyList.length === 0 &&
          form.value.platformKeyList.push(itemData());
      })
      .catch(() => {});
  } else {
    // 根据索引删除数据
    form.value.platformKeyList.splice(index, 1);
    // 最后一条清空不删掉
    form.value.tableData.length === 0 && form.value.tableData.push(itemData());
  }
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["handleRef"].validate(async (valid) => {
    if (valid) {
      const data = {
        platform: currentPlatform.value,
        keyList: form.value.platformKeyList,
      };
      submitBatch(data).then(() => {
        proxy.$modal.msgSuccess("设置成功");
        showKeyOpen.value = false;
        getList();
      });
    }
  });
}

/** 从提供方卡片进入模型详情，按平台加载可用模型。 */
function routeTo(_link, row) {
  const link = "/basic/llm/modelMarket/detail";
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return;
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: {
          platform: row.platform,
        },
      });
    }
  }
}

// 离开页面后使在途查询失效，避免销毁后继续更新卡片。
onBeforeUnmount(() => { ++listRequest; });
getList();
</script>

<style scoped lang="scss">
.app-container {
  padding-bottom: 45px;
  overflow-x: hidden;

  .pagecont-bottom {
    position: fixed;
    bottom: 0;
    width: 100%;
    left: 0;
    height: 60px;
    background: #ffffff;
    border-radius: 2px 2px 2px 2px;
    line-height: 60px;
    margin: 0;
    padding: 0 18px 0px 0;
    flex: none;

    .pagination-container {
      margin-top: 0;
    }
  }
}

:deep(.no-change-link) {
  color: var(--el-color-primary) !important; // 固定蓝色（Element主色）
  text-decoration: none !important; // 强制无下划线
  &:hover,
  &:active,
  &:visited {
    color: var(
      --el-color-primary
    ) !important; // 悬浮/点击/已访问 都保持蓝色不变
    text-decoration: none !important; // 任何状态都无下划线
  }

  cursor: pointer; // 保留鼠标手型，提示可点击
}

:deep(.key-config-dialog .el-dialog__body) {
  overflow: auto;
  padding: 20px 40px 5px 40px !important;
}
// 内容区撑满搜索栏与分页之间的剩余高度，无数据时页面不塌缩。
.pagecont-main {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.emptyBg {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 2px;
}

</style>
