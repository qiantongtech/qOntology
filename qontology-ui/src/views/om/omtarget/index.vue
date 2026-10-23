<!--
  Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

  This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

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
    <GuideTip tip-id="om/target.list"/>
    <div class="pagecont-top" v-show="showSearch">
      <el-form class="btn-style" :model="queryParams" ref="queryRef" :inline="true" label-width="75px"
               v-show="showSearch" @submit.prevent>
        <el-form-item label="目标问题" prop="question">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.question"
              placeholder="请输入目标问题"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select
              class="el-form-input-width"
              v-model="queryParams.priority"
              placeholder="请选择优先级"
              clearable
          >
            <el-option v-for="item in om_target_priority" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>

        <el-form-item label="验证状态" prop="acceptStatus">
          <el-select
              class="el-form-input-width"
              v-model="queryParams.acceptStatus"
              placeholder="请选择验证状态"
              clearable
          >
            <el-option v-for="item in om_accept_status" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button plain type="primary" @click="handleQuery" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button type="primary" plain @click="handleAdd" v-hasPermi="['om:target:target:add']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['om:target:target:remove']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </div>
      </div>
      <el-table stripe v-loading="loading" :data="targetList" @selection-change="handleSelectionChange"
                :default-sort="defaultSort" @sort-change="handleSortChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" align="center" prop="id" width="80"
                         sortable="custom" :sort-orders="['descending', 'ascending']"/>
        <el-table-column v-if="getColumnVisibility(1)" label="目标问题" align="left" prop="question"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.question || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="问题来源" align="left" prop="questionSource" width="250"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.questionSource || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="业务场景" align="left" prop="businessScenarios" width="250"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.businessScenarios || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="优先级" align="center" prop="priority" width="100">
          <template #default="scope">
            <dict-tag :options="om_target_priority" :value="scope.row.priority"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(5)" label="验证状态" align="center" prop="acceptStatus" width="100">
          <template #default="scope">
            <dict-tag :options="om_accept_status" :value="scope.row.acceptStatus"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(6)" label="关联元素" align="center" prop="objectTypeNum" width="100">
          <template #default="scope">
            {{ scope.row.relatedElementCount || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(7)" label="操作" align="center"
                         class-name="small-padding fixed-width" fixed="right" width="240">
          <template #default="scope">
            <el-button link type="primary" icon="view" @click="routeTo('/om/'+versionId+'/target/detail',scope.row)"
                       v-hasPermi="['om:target:target:query']">详情
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                       v-hasPermi="['om:target:target:edit']">修改
            </el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                       v-hasPermi="['om:target:target:remove']">删除
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt=""/>
            <p>暂无记录</p>
          </div>
        </template>
      </el-table>

      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </div>

    <!-- 添加或修改建模目标对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" :append-to="$refs['app-container']" draggable>
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="targetRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="目标问题" prop="question">
              <el-input v-model="form.question" type="textarea" placeholder="请输入目标问题"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="问题来源" prop="questionSource">
              <el-input v-model="form.questionSource" type="textarea" placeholder="请输入问题来源"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="业务场景" prop="businessScenarios">
              <el-input v-model="form.businessScenarios" type="textarea" placeholder="请输入业务场景"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="验收标准" prop="acceptCriterion">
              <el-input v-model="form.acceptCriterion" type="textarea" placeholder="请输入验收标准"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优先级" prop="priority">
              <el-radio-group v-model="form.priority">
                <el-radio
                    v-for="dict in om_target_priority"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Target">
import {addTarget, delTarget, getTarget, listTarget, updateTarget} from "@/api/om/omtarget/target";
import {useRoute, useRouter} from 'vue-router';

const {proxy} = getCurrentInstance();
const {om_accept_status, om_target_priority} = proxy.useDict('om_accept_status', 'om_target_priority');
const route = useRoute();
const router = useRouter();

// 列显隐信息
const columns = ref([
  {key: 0, label: "编号", visible: true},
  {key: 1, label: "目标问题", visible: true},
  {key: 2, label: "问题来源", visible: true},
  {key: 3, label: "业务场景", visible: true},
  {key: 4, label: "优先级", visible: true},
  {key: 5, label: "验证状态", visible: true},
  {key: 6, label: "关联元素", visible: true},
  {key: 7, label: "操作", visible: true}
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find(col => col.key === key);
  // 如果没有找到对应列配置，默认显示
  if (!column) return true;
  // 如果找到对应列配置，根据visible属性来控制显示
  return column.visible;
};

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const defaultSort = ref({prop: "id", order: "descending"});
const versionId = ref(route.params.versionId || null);
const targetList = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    versionId: null,
    question: null,
    questionSource: null,
    businessScenarios: null,
    priority: null,
    acceptCriterion: null,
    acceptStatus: null,
    objectTypeNum: null,
    objectAttributeNum: null,
    objectRelationshipNum: null,
    createTime: null,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order
  },
  rules: {
    question: [{required: true, message: "目标问题不能为空", trigger: "blur"}],
    questionSource: [{required: true, message: "问题来源不能为空", trigger: "blur"}],
    businessScenarios: [{required: true, message: "业务场景不能为空", trigger: "blur"}],
    acceptCriterion: [{required: true, message: "验收标准不能为空", trigger: "blur"}],
    priority: [{required: true, message: "优先级不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

watch(
    () => route.params.versionId,
    () => {
      versionId.value = route.params.versionId || null;
      getList();
    },
    { immediate: true }
);

/** 查询建模目标列表 */
function getList() {
  loading.value = true;
  queryParams.value.versionId = versionId.value;
  listTarget(queryParams.value).then(response => {
    targetList.value = response.data.rows;
    total.value = response.data.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    versionId: 1,
    question: null,
    questionSource: null,
    businessScenarios: null,
    priority: "3",
    acceptCriterion: null,
    acceptStatus: null,
    objectTypeNum: null,
    objectAttributeNum: null,
    objectRelationshipNum: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm("targetRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}


/** 排序触发事件 */
function handleSortChange(column, prop, order) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增建模目标";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getTarget(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改建模目标";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["targetRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateTarget(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      } else {
        addTarget(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除建模目标编号为"' + _ids + '"的数据项？').then(function () {
    return delTarget(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

function routeTo(link, row) {
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: {
          id: row.id
        }
      });
    }
  }
}
</script>
