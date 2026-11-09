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
    <GuideTip tip-id="basic/file/category.list"/>
    <!-- 分类树沿用统一搜索及表格组件；不分页，避免父子节点被拆到不同页。 -->
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
        <el-button v-hasPermi="['basic:file:category:add']" type="primary" plain @click="handleAdd()"
                   @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button class="toggle-expand-all" type="primary" plain @click="toggleExpandAll">
          <svg-icon :icon-class="isExpandAll ? 'toggle' : 'expand'"/>
          <span>{{ isExpandAll ? ' 折叠' : ' 展开' }}</span>
        </el-button>
      </template>
      <qt-table v-bind="tableStore" ref="tableRef">
        <template #text="{row, column_data}">{{ row[column_data.prop] }}</template>
        <template #status="{ row }">
          <el-switch
              v-model="row.status"
              :active-value="0"
              :inactive-value="1"
              @change="value => handleStatusChange(row, value)"
          >
          </el-switch>
        </template>
        <!-- 沿用责任人双行样式，创建人电话通过用户接口关联，未维护时只显示姓名。 -->
        <template #creator="{row}">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #createTime="{row}">
          {{row.createTime === '-' ? '-' : (parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-') }}
        </template>
        <template #handle="{row}">
          <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
          <el-button v-hasPermi="['basic:file:category:edit']" link type="primary" icon="Edit"
                     :disabled="row.status === 0"
                     @click="handleUpdate(row)"
          >修改
          </el-button>
          <!-- 低频操作收起到更多菜单，启用中的分类与参考页一致禁止删除。 -->
          <el-popover
            placement="bottom"
            :width="150"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                v-hasPermi="['basic:file:category:add']"
                link
                type="primary"
                icon="Plus"
                @click="handleAdd(row)"
                >新增子级</el-button
              >
              <el-button
                v-hasPermi="['basic:file:category:remove']"
                link
                type="danger"
                icon="Delete"
                :disabled="row.status === 0"
                @click="handleDelete(row)"
                >删除</el-button
              >
            </div>
          </el-popover>
        </template>
        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="暂无记录"/>
            <p>暂无记录</p>
          </div>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增、修改及详情共用独立组件，保存成功后刷新当前分类树。 -->
    <CategoryFormDialog ref="categoryDialogRef" @success="getList"/>
  </div>
</template>

<script setup name="BasicFileCategory">
import {computed, getCurrentInstance, onMounted, reactive, ref} from "vue";
import {
  delModelDataCategory,
  listModelDataCategory, updateModelDataCategory,
} from "@/api/basic/file/basicFileCategory.js";

import CategoryFormDialog from "./components/categoryFormDialog.vue";

import {useCreatorPhones} from '@/utils/useCreatorPhones';

const {proxy} = getCurrentInstance();

const categoryList = ref([]);
const categoryOptions = ref([]);
const categoryDialogRef = ref();
const isExpandAll = ref(true);
const defaultSort = {prop: 'orderNum', order: 'ascending'};
const tableRef = ref();
const {creatorPhones} = useCreatorPhones();
import {parseTime} from "@/utils/anivia";

let appliedName = '';
let appliedParentId;

// 公共表格负责加载及列显隐，分类树全量请求后在页面内转换为父子层级。
const tableStore = reactive({
  config: {
    notPagination: true,
    sort: true,
    table: {
      rowKey: 'id',
      height: '60vh',
      stripe: true,
      defaultExpandAll: true,
      treeProps: {children: 'children', hasChildren: 'hasChildren'},
      defaultSort
    }
  },
  // 业务字段依次为文件分类名称、描述、状态、排序、创建人、创建时间，操作固定在末尾。
  columns: [
    {
      label: '文件分类名称',
      prop: 'name',
      slot: 'text',
      minWidth: 250,
      align: 'left',
      showOverflowTooltip: {effect: 'light'}
    },
    {label: '描述', prop: 'remark', slot: 'text', minWidth: 220, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {
      label: "状态", prop: "status", slot: "status",
      width: 100, minWidth: 100,
    },
    {label: '排序', prop: 'orderNum', slot: 'text', width: 150, sortable: true},
    {label: '创建人', prop: 'createBy', slot: 'creator', width: 130, align: 'left', headerAlign: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '创建时间', prop: 'createTime', slot: 'createTime', width: 160, sortable: true},
    {label: '操作', slot: 'handle', width: 240, minWidth: 240, align: 'center', fixed: 'right'}
  ],
  func: listModelDataCategory,
  params: {name: '', parentId: undefined},
  events: {formatParams: formatListParams, formatData: formatListData}
});

// 默认展开顶级分类及下一层节点，深层分类由用户按需展开；选项异步加载后同步更新。
const parentExpandedKeys = computed(() => categoryOptions.value.flatMap(node => [
  node.id, ...(node.children || []).map(child => child.id)
]));

// 查询草稿与已提交条件分离，刷新和排序不自动应用尚未提交的输入。
const searchStore = computed(() => ({
  items: [
    {label: '文件分类名称', prop: 'name', component: {is: 'input'}},
    // 选项始终取完整分类树；严格选择允许选择任意层级，顶级分类对应 parentId=0。
    {
      label: '父级分类', prop: 'parentId', component: {
        is: 'tree-select', data: categoryOptions.value,
        props: {value: 'id', label: 'name', children: 'children'},
        valueKey: 'id', checkStrictly: true, defaultExpandedKeys: parentExpandedKeys.value
      }
    }
  ]
}));

/** 提交名称及父级分类筛选，后续刷新和排序均沿用已提交条件。 */
function applyFilters() {
  appliedName = (tableStore.params.name || '').trim();
  // 保留顶级分类的编号 0，清空选择时才取消父级筛选。
  const parentId = tableStore.params.parentId;
  appliedParentId = parentId == null || parentId === '' ? undefined : parentId;
}

/** 分类树必须全量加载，取消排序时恢复显示顺序升序。 */
function formatListParams(params) {
  return {
    ...params,
    name: appliedName,
    parentId: appliedParentId,
    pageNum: 1,
    pageSize: -1,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order
  };
}

/** 转换树结构并规范文本空值，显示顺序为零时仍正常展示。 */
function formatListData(rows) {
  const fields = ['name', 'orderNum', 'remark', 'status', 'createBy', 'createTime'];
  const records = rows.map(row => {
    const record = {...row};
    fields.forEach(field => {
      if (record[field] == null || record[field] === '') record[field] = '-';
    });
    return record;
  });
  categoryList.value = proxy.handleTree(records, 'id', 'parentId');
  return categoryList.value;
}

/** 新增、修改和删除后刷新当前分类树，保留已提交筛选和展开状态。 */
function getList() {
  tableRef.value?.getList();
  // 分类增删改后同步刷新筛选选项，避免选项名称或层级停留在旧状态。
  loadCategoryOptions();
}

/** 父级筛选和编辑选项始终加载完整分类树，避免当前筛选导致可选父级缺失。 */
async function loadCategoryOptions() {
  const response = await listModelDataCategory({
    pageNum: 1,
    pageSize: -1,
    orderByColumn: defaultSort.prop,
    isAsc: defaultSort.order
  });
  const rows = response.data?.rows || response.data || [];
  categoryOptions.value = [{id: 0, name: '顶级分类', children: proxy.handleTree(rows, 'id', 'parentId')}];
}

/** 新增分类；从行内进入时预选该行作为父级。 */
async function handleAdd(row) {
  await loadCategoryOptions();
  categoryDialogRef.value.openDialog({
    title: '新增文件分类',
    parentId: row?.id ?? 0,
    categoryOptions: categoryOptions.value
  });
}

/** 修改时由独立弹窗请求最新详情，确保表单回显服务端数据。 */
async function handleUpdate(row) {
  await loadCategoryOptions();
  categoryDialogRef.value.openDialog({title: '修改文件分类', id: row.id, categoryOptions: categoryOptions.value});
}

/** 详情沿用同一字段布局，以只读模式展示分类信息。 */
async function handleDetail(row) {
  await loadCategoryOptions();
  categoryDialogRef.value.openDialog({
    title: '文件分类详情',
    id: row.id,
    readonly: true,
    categoryOptions: categoryOptions.value
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal
      .confirm(`是否确认删除分类“${row.name}”？`)
      .then(() => delModelDataCategory(row.id))
      .then(() => {
        proxy.$modal.msgSuccess("删除成功");
        getList();
      })
      .catch(() => {
      });
}

/** 遍历当前分类树切换展开状态，不重新请求接口，后续刷新沿用同一默认展开值。 */
function toggleExpandAll() {
  isExpandAll.value = !isExpandAll.value;
  tableStore.config.table.defaultExpandAll = isExpandAll.value;
  const toggleRows = rows => {
    rows.forEach(row => {
      tableRef.value?.elTableRef?.toggleRowExpansion(row, isExpandAll.value);
      if (row.children?.length) toggleRows(row.children);
    });
  };
  toggleRows(categoryList.value);
}
async function handleStatusChange(row, value) {

  let data = {
    ...row,
    status: value
  }
  proxy.$modal
      .confirm(`确认要${value === 0 ? "启用" : "禁用"}文件分类“${row.name}”吗？`)
      .then(function () {
        return updateModelDataCategory(data);
      })
      .then(() => {
        proxy.$modal.msgSuccess("操作成功");
      })
      .catch(function () {
        row.status = row.status === 1 ? 0 : 1;
      });
}

// 首次进入加载完整父级选项，列表查询仍由公共表格负责。
onMounted(loadCategoryOptions);
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
