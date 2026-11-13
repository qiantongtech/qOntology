<!--
 /*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */
-->

<template>
  <div class="app-container" ref="app-container">
    <!-- 部门树沿用统一搜索及表格组件；不分页，避免父子部门被拆到不同页。 -->
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
            :items="searchStore.items"
            :params="tableStore.params"
            :table-ref="tableRef"
            :config="{collapsedCount: 4}"
            @query="applyFilters"
            @reset="applyFilters"
        />
      </template>
      <template #actions-data>
        <el-button v-hasPermi="['system:dept:add']" type="primary" plain @click="handleAdd()"
                   @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
            class="toggle-expand-all"
            type="primary"
            plain
            @click="toggleExpandAll"
        >
          <svg-icon :icon-class="isExpandAll ? 'toggle' : 'expand'"/>
          <span>{{ isExpandAll ? ' 折叠' : ' 展开' }}</span>
        </el-button>
      </template>
      <qt-table v-bind="tableStore" ref="tableRef">
        <template #text="{row, column_data}">{{ row[column_data.prop] }}</template>
        <template #status="{row}">
          <el-switch
              v-model="row.status"
              active-value="0"
              inactive-value="1"
              @change="handleStatusChange(row)"
          />
        </template>
        <template #handle="{row}">
          <el-button
              v-hasPermi="['system:dept:add']"
              link
              type="primary"
              icon="Plus"
              @click="handleAdd(row)"
          >新增
          </el-button>
          <el-button
              v-hasPermi="['system:dept:edit']"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
          >修改
          </el-button>
          <!-- 公司顶级部门不允许删除，沿用原页面按 parentId 隐藏入口的逻辑。 -->
          <el-button
              v-if="row.parentId != 0"
              v-hasPermi="['system:dept:remove']"
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(row)"
          >删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增、修改共用独立组件，保存成功后刷新当前部门树。 -->
    <DeptFormDialog ref="deptDialogRef" @success="getList"/>
  </div>
</template>

<script setup name="SystemDept">
import {computed, getCurrentInstance, onMounted, reactive, ref} from "vue";
import {delDept, listDept, listDeptExcludeChild, updateDept} from "@/api/system/system/dept.js";

import DeptFormDialog from "./components/deptFormDialog.vue";

const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict("sys_normal_disable");

const deptList = ref([]);
const deptOptions = ref([]);
const deptDialogRef = ref();
const isExpandAll = ref(true);
const tableRef = ref();

let appliedName = '';
let appliedStatus;

// 公共表格负责加载及列显隐，部门树全量请求后在页面内转换为父子层级。
const tableStore = reactive({
  config: {
    notPagination: true,
    sort: true,
    table: {
      rowKey: 'deptId',
      stripe: true,
      defaultExpandAll: true,
      treeProps: {children: 'children'}
    }
  },
  // 业务字段依次为部门名称、负责人、联系电话、邮箱、状态、排序、创建时间，操作固定在末尾。
  columns: [
    {
      label: '部门名称',
      prop: 'deptName',
      slot: 'text',
      minWidth: 250,
      align: 'left',
      showOverflowTooltip: {effect: 'light'}
    },
    {label: '负责人', prop: 'leader', slot: 'text', minWidth: 120, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '联系电话', prop: 'phone', slot: 'text', minWidth: 130, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '邮箱', prop: 'email', slot: 'text', minWidth: 160, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '状态', prop: 'status', slot: 'status', width: 80, minWidth: 80, align: 'center'},
    {label: '排序', prop: 'orderNum', slot: 'text', width: 150, minWidth: 150, sortable: true},
    {label: '创建时间', prop: 'createTime', date: true, width: 160, minWidth: 160, sortable: true},
    {label: '操作', slot: 'handle', width: 240, minWidth: 240, align: 'center', fixed: 'right'}
  ],
  func: listDept,
  params: {deptName: '', status: undefined},
  events: {formatParams: formatListParams, formatData: formatListData}
});

// 查询草稿与已提交条件分离，刷新和排序不自动应用尚未提交的输入。
const searchStore = computed(() => ({
  items: [
    {label: '部门名称', prop: 'deptName', component: {is: 'input'}},
    {label: '状态', prop: 'status', component: {is: 'select', options: sys_normal_disable.value}}
  ]
}));

/** 提交名称及状态筛选，后续刷新和排序均沿用已提交条件。 */
function applyFilters() {
  appliedName = (tableStore.params.deptName || '').trim();
  // 状态 "0" 为合法取值，仅在清空时取消状态筛选。
  const status = tableStore.params.status;
  appliedStatus = status === '' || status == null ? undefined : status;
}

/** 部门树必须全量加载，不走分页。 */
function formatListParams(params) {
  return {
    ...params,
    deptName: appliedName,
    status: appliedStatus,
    pageNum: 1,
    pageSize: -1
  };
}

/** 平铺部门列表转换为父子树结构，供表格树形展示。 */
function formatListData(rows) {
  deptList.value = proxy.handleTree(rows, 'deptId');
  return deptList.value;
}

/** 新增、修改和删除后刷新当前部门树，保留已提交筛选和展开状态。 */
function getList() {
  tableRef.value?.getList();
  // 部门增删改后同步刷新上级部门选项，避免选项名称或层级停留在旧状态。
  loadDeptOptions();
}

/** 新增弹窗的上级部门选项始终加载完整部门树，避免当前筛选导致可选父级缺失。 */
async function loadDeptOptions() {
  const response = await listDept();
  deptOptions.value = proxy.handleTree(response.data, 'deptId');
}

/** 修改弹窗排除自身及子部门，避免把上级设置为自己的下级。 */
async function loadDeptExcludeOptions(deptId) {
  const response = await listDeptExcludeChild(deptId);
  deptOptions.value = proxy.handleTree(response.data, 'deptId');
}

/** 新增部门；从行内进入时预选该行作为父级（新增子部门），否则由弹窗内选择。 */
async function handleAdd(row) {
  await loadDeptOptions();
  deptDialogRef.value.openDialog({
    title: '新增部门',
    parentId: row?.deptId,
    deptOptions: deptOptions.value
  });
}

/** 修改时刷新可选上级并打开弹窗，详情由弹窗请求最新数据。 */
async function handleUpdate(row) {
  await loadDeptExcludeOptions(row.deptId);
  deptDialogRef.value.openDialog({title: '修改部门', deptId: row.deptId, deptOptions: deptOptions.value});
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal
      .confirm(`是否确认删除名称为“${row.deptName}”的数据项?`)
      .then(function () {
        return delDept(row.deptId);
      })
      .then(() => {
        proxy.$modal.msgSuccess('删除成功');
        getList();
      })
      .catch(() => {
      });
}

/** 部门状态修改：二次确认后提交，取消或失败时回滚开关状态。 */
function handleStatusChange(row) {
  const text = row.status === '0' ? '启用' : '停用';
  proxy.$modal
      .confirm('确认要"' + text + '""' + row.deptName + '"吗?')
      .then(function () {
        return updateDept(row);
      })
      .then(() => {
        proxy.$modal.msgSuccess(text + '成功');
      })
      .catch(function () {
        row.status = row.status === '0' ? '1' : '0';
      });
}

/** 遍历当前部门树切换展开状态，不重新请求接口，后续刷新沿用同一默认展开值。 */
function toggleExpandAll() {
  isExpandAll.value = !isExpandAll.value;
  tableStore.config.table.defaultExpandAll = isExpandAll.value;
  const toggleRows = rows => {
    rows.forEach(row => {
      tableRef.value?.elTableRef?.toggleRowExpansion(row, isExpandAll.value);
      if (row.children?.length) toggleRows(row.children);
    });
  };
  toggleRows(deptList.value);
}

// 首次进入加载完整上级部门选项，列表查询仍由公共表格负责。
onMounted(loadDeptOptions);
</script>

<style scoped lang="scss">
.toggle-expand-all {
  .svg-icon {
    font-size: 12px;
    margin-right: 6px;
  }
}

.mr5 {
  margin-right: 5px;
}

.mb15 {
  margin-bottom: 15px;
}
</style>
