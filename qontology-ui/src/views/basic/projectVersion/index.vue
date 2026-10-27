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
    <div class="pagecont-top" v-show="showSearch">
      <el-form class="btn-style" :model="queryParams" ref="queryRef" :inline="true" label-width="75px"
               v-show="showSearch" @submit.prevent>
        <el-form-item label="版本号" prop="version">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.version"
              placeholder="请输入版本号"
              clearable
              @keyup.enter="handleQuery"
          />
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
            <el-button type="primary" plain @click="handleAdd" v-hasPermi="['basic:project:projectversion:add']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['basic:project:projectversion:remove']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </div>
      </div>
      <el-table stripe v-loading="loading" :data="projectVersionList" @selection-change="handleSelectionChange"
                :default-sort="defaultSort" @sort-change="handleSortChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" align="center" prop="id" width="80"
                         sortable="custom" :sort-orders="['descending', 'ascending']"/>
        <el-table-column v-if="getColumnVisibility(2)" label="版本号" align="left" prop="version" width="200">
          <template #default="scope">
            <span>{{ scope.row.version || '-' }}</span>
            <el-tag v-if="Number(versionId) === scope.row.id" type="primary" style="margin-left: 5px">
              当前版本
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="描述" align="left" prop="description"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="基准版本" align="left" prop="baseVersion" width="180">
          <template #default="scope">
            {{ scope.row.baseVersion || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(6)" label="创建人" align="center" prop="createBy" width="100">
          <template #default="scope">
            {{ scope.row.createBy || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(8)" label="创建时间" align="center" prop="createTime" width="160"
                         sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(9)" label="操作" align="center"
                         class-name="small-padding fixed-width" fixed="right" width="240">
          <template #default="scope">
            <el-button link type="primary" icon="view"
                       @click="routeTo('/om/projectVersion/projectVersionDetail',scope.row)"
                       v-hasPermi="['basic:project:projectversion:diff']">版本对比
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                       v-hasPermi="['basic:project:projectversion:edit']">修改
            </el-button>
            <el-popover placement="bottom" :width="150" trigger="click">
              <template #reference>
                <el-button link type="primary" icon="ArrowDown">更多</el-button>
              </template>
              <div class="more-actions">
                <el-button link type="primary" icon="view" @click="handleDetail(scope.row)"
                           v-hasPermi="['basic:project:projectversion:query']">详情
                </el-button>
                <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['basic:project:projectversion:remove']">删除
                </el-button>
              </div>
            </el-popover>

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

    <!-- 添加或修改项目版本对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" :append-to="$refs['app-container']" draggable>
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="projectVersionRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入版本号"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入描述"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="基准版本" prop="baseVersion">
              <el-input disabled v-model="form.baseVersion" placeholder="请输入基准版本"/>
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

    <!-- 项目版本详情对话框 -->
    <el-dialog :title="title" v-model="openDetail" width="800px" :append-to="$refs['app-container']" draggable>
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="projectVersionRef" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="版本号" prop="version">
              <div class="form-readonly">
                {{ form.version }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div class="form-readonly textarea">
                {{ form.description }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="基准版本" prop="baseVersion">
              <div class="form-readonly">
                {{ form.baseVersion }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectVersion">
import {
  addProjectVersion,
  delProjectVersion,
  getProjectVersion,
  listProjectVersion,
  updateProjectVersion
} from "@/api/basic/project/projectVersion.js";
import {parseTime} from "@/utils/anivia";
import {useRoute, useRouter} from "vue-router";

const {proxy} = getCurrentInstance();
const route = useRoute();
const router = useRouter();

const projectVersionList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 1, label: "项目id", visible: true},
  {key: 2, label: "版本号", visible: true},
  {key: 3, label: "描述", visible: true},
  {key: 4, label: "基准版本", visible: true},
  {key: 6, label: "创建人", visible: true},
  {key: 8, label: "创建时间", visible: true},
  {key: 12, label: "备注", visible: true}
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
const defaultSort = ref({prop: "createTime", order: "descending"});

const versionId = ref(route.params.versionId || null);
const currentVersion = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectId: null,
    version: null,
    description: null,
    baseVersion: null,
    createTime: null,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order
  },
  rules: {
    projectId: [{required: true, message: "项目id不能为空", trigger: "blur"}],
    version: [{required: true, message: "版本号不能为空", trigger: "blur"}],
    delFlag: [{required: true, message: "删除标志不能为空", trigger: "blur"}],
    createTime: [{required: true, message: "创建时间不能为空", trigger: "blur"}],
    updateTime: [{required: true, message: "更新时间不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

watch(
    () => route.params.versionId,
    async () => {
      versionId.value = route.params.versionId || null;
      if (!versionId.value) {
        loading.value = false;
        return;
      }
      const response = await getProjectVersion(versionId.value);
      currentVersion.value = response.data;
      getList();
    },
    {immediate: true}
);

/** 查询项目版本列表 */
function getList() {
  loading.value = true;
  queryParams.value.projectId = currentVersion.value.projectId;
  listProjectVersion(queryParams.value).then(response => {
    projectVersionList.value = response.data.rows;
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
    projectId: currentVersion.value.projectId,
    version: null,
    description: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
    baseVersion: currentVersion.value.version,
    baseVersionId: currentVersion.value.id,

  };
  proxy.resetForm("projectVersionRef");
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
  single.value = selection.length != 1;
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
  title.value = "添加项目版本";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getProjectVersion(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改项目版本";
  });
}


/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value
  getProjectVersion(_id).then(response => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "项目版本详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectVersionRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProjectVersion(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      } else {
        addProjectVersion(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除项目版本编号为"' + _ids + '"的数据项？').then(function () {
    return delProjectVersion(_ids);
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

<style scoped lang="scss">
.more-actions {
  width: 100px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;

  .el-button {
    width: 100%;
    justify-content: center;
    margin-left: 0;
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
