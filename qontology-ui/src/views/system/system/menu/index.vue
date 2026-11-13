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
    <!-- 菜单树沿用统一搜索及表格组件；不分页，避免父子节点被拆到不同页。 -->
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
          @query="applyFilters"
          @reset="applyFilters"
        />
      </template>
      <template #actions-data>
        <el-button
          v-hasPermi="['system:menu:add']"
          type="primary"
          plain
          @click="handleAdd()"
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
          <svg-icon :icon-class="isExpandAll ? 'toggle' : 'expand'" />
          <span>{{ isExpandAll ? ' 折叠' : ' 展开' }}</span>
        </el-button>
      </template>
      <qt-table v-bind="tableStore" ref="tableRef">
        <template #text="{ row, column_data }">{{ row[column_data.prop] === '' ? '-' : row[column_data.prop] }}</template>
        <template #icon="{ row }">
          <svg-icon v-if="row.icon" :icon-class="row.icon" />
        </template>
        <template #status="{ row }">
          <el-switch
            v-model="row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(row)"
          />
        </template>
        <template #handle="{ row }">
          <el-button
              v-hasPermi="['system:menu:add']"
              link
              type="primary"
              icon="Plus"
              @click="handleAdd(row)"
          >新增子级</el-button>
          <el-button
            v-hasPermi="['system:menu:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
          >详情</el-button>
          <el-popover
            placement="bottom"
            :width="100"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                  v-hasPermi="['system:menu:edit']"
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(row)"
              >修改</el-button>
              <el-button
                  v-hasPermi="['system:menu:remove']"
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.status === '0'"
                  @click="handleDelete(row)"
              >删除</el-button>
            </div>
          </el-popover>
        </template>
        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="暂无记录" />
            <p>暂无记录</p>
          </div>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增、修改共用独立弹窗组件，保存成功后刷新当前菜单树。 -->
    <MenuFormDialog ref="menuDialogRef" @success="getList" />

    <!-- 菜单详情对话框：字段显隐条件与新增表单一致，按菜单类型展示。 -->
    <el-dialog
      title="菜单详情"
      v-model="openDetail"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">菜单详情</span>
      </template>
      <el-form :model="detailForm" label-width="100px" class="column-form">
        <qt-form-item label="上级菜单">
          <div class="form-readonly">{{ parentMenuName }}</div>
        </qt-form-item>
        <qt-form-item label="菜单类型">
          <div class="form-readonly">{{ menuTypeLabels[detailForm.menuType] || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType !== 'F'" label="菜单图标">
          <div class="form-readonly">
            <svg-icon v-if="detailForm.icon" :icon-class="detailForm.icon" />
            {{ detailForm.icon || "-" }}
          </div>
        </qt-form-item>
        <qt-form-item label="显示排序">
          <div class="form-readonly">{{ detailForm.orderNum ?? "-" }}</div>
        </qt-form-item>
        <qt-form-item label="菜单名称">
          <div class="form-readonly">{{ detailForm.menuName || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType === 'C'" label="路由名称">
          <div class="form-readonly">{{ detailForm.routeName || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType !== 'F'" label="路由地址">
          <div class="form-readonly">{{ detailForm.path || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType !== 'F'" label="是否外链">
          <div class="form-readonly">{{ detailForm.isFrame === "0" ? "是" : "否" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType === 'C'" label="组件路径">
          <div class="form-readonly">{{ detailForm.component || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType !== 'M'" label="权限字符">
          <div class="form-readonly">{{ detailForm.perms || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType === 'C'" label="路由参数">
          <div class="form-readonly">{{ detailForm.query || "-" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType === 'C'" label="是否缓存">
          <div class="form-readonly">{{ detailForm.isCache === "0" ? "缓存" : "不缓存" }}</div>
        </qt-form-item>
        <qt-form-item v-if="detailForm.menuType !== 'F'" label="显示状态">
          <dict-tag
            :options="sys_show_hide"
            :value="detailForm.visible"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="菜单状态">
          <dict-tag
            :options="sys_normal_disable"
            :value="detailForm.status"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="创建时间">
          <div class="form-readonly">
            {{ parseTime(detailForm.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDetail">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Menu">
import { computed, getCurrentInstance, onMounted, reactive, ref } from "vue";

import { delMenu, getMenu, listMenu, updateMenu } from "@/api/system/system/menu.js";

import MenuFormDialog from "./components/menuFormDialog.vue";

const { proxy } = getCurrentInstance();

// 字典：sys_normal_disable 用于菜单正常/停用状态展示；sys_show_hide 用于详情显示状态展示
const { sys_show_hide, sys_normal_disable } = proxy.useDict("sys_show_hide", "sys_normal_disable");

// 菜单类型编码与新增表单的单选文案一致
const menuTypeLabels = { M: "目录", C: "菜单", F: "按钮" };

const menuList = ref([]);
const menuFlatList = ref([]);
const menuOptions = ref([]);
const menuDialogRef = ref();
const isExpandAll = ref(true);
const defaultSort = { prop: 'orderNum', order: 'ascending' };
const tableRef = ref();
const openDetail = ref(false);
const detailForm = ref({});

/** 详情中的上级菜单名称：0 为主类目，其余按 menuId 从完整菜单列表回查。 */
const parentMenuName = computed(() => {
  const parentId = detailForm.value.parentId;
  if (parentId === 0) return "主类目";
  const parent = menuFlatList.value.find((item) => item.menuId === parentId);
  return parent?.menuName || "-";
});

let appliedMenuName = '';
let appliedStatus;

// 公共表格负责加载及列显隐，菜单树全量请求后在页面内转换为父子层级。
const tableStore = reactive({
  config: {
    notPagination: true,
    sort: true,
    table: {
      rowKey: 'menuId',
      stripe: true,
      defaultExpandAll: true,
      treeProps: { children: 'children' },
      defaultSort
    }
  },
  columns: [
    {
      label: '菜单名称',
      prop: 'menuName',
      slot: 'text',
      minWidth: 250,
      align: 'left',
      showOverflowTooltip: { effect: 'light' }
    },
    { label: '图标', prop: 'icon', slot: 'icon', width: 100, minWidth: 100, align: 'center' },
    { label: '路由地址', prop: 'path', slot: 'text', minWidth: 150, align: 'left', showOverflowTooltip: { effect: 'light' } },
    { label: '权限标识', prop: 'perms', slot: 'text', minWidth: 160, align: 'left', showOverflowTooltip: { effect: 'light' } },
    { label: '组件路径', prop: 'component', slot: 'text', minWidth: 180, align: 'left', showOverflowTooltip: { effect: 'light' } },
    { label: '状态', prop: 'status', slot: 'status', width: 80, minWidth: 80, align: 'center' },
    { label: '排序', prop: 'orderNum', slot: 'text', width: 150, minWidth: 150, align: 'center', sortable: true },
    { label: '创建时间', prop: 'createTime', width: 160, minWidth: 160, align: 'center', date: true, sortable: true },
    { label: '操作', slot: 'handle', width: 240, minWidth: 240, align: 'center', fixed: 'right' }
  ],
  func: listMenu,
  params: { menuName: '', status: undefined },
  events: { formatParams: formatListParams, formatData: formatListData }
});

// 查询项沿用菜单名称与状态；状态选项来自 sys_normal_disable 字典。
const searchStore = computed(() => ({
  items: [
    { label: '菜单名称', prop: 'menuName', component: { is: 'input' } },
    {
      label: '状态',
      prop: 'status',
      component: { is: 'select', options: sys_normal_disable.value, clearable: true }
    }
  ]
}));

/** 提交名称及状态筛选，后续刷新和排序均沿用已提交条件。 */
function applyFilters() {
  appliedMenuName = (tableStore.params.menuName || '').trim();
  // 状态 0 为正常值，仅在清空选择时取消状态筛选。
  const status = tableStore.params.status;
  appliedStatus = status == null || status === '' ? undefined : status;
}

/** 菜单树必须全量加载，取消排序时恢复显示顺序升序。 */
function formatListParams(params) {
  return {
    ...params,
    menuName: appliedMenuName,
    status: appliedStatus,
    pageNum: 1,
    pageSize: -1,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order
  };
}

/** 接口返回扁平菜单数组（response.data），转换为父子树结构后交给表格展示。 */
function formatListData(rows) {
  menuList.value = proxy.handleTree(rows || [], 'menuId');
  return menuList.value;
}

/** 新增、修改和删除后刷新当前菜单树，保留已提交筛选和展开状态。 */
function getList() {
  tableRef.value?.getList();
  // 菜单增删改后同步刷新上级菜单选项，避免选项名称或层级停留在旧状态。
  loadMenuOptions();
}

/** 上级菜单选项始终加载完整菜单树，避免当前筛选导致可选父级缺失；保留平铺副本用于详情回查上级名称。 */
async function loadMenuOptions() {
  const response = await listMenu();
  const rows = response.data || [];
  menuFlatList.value = rows;
  menuOptions.value = [{ menuId: 0, menuName: '主类目', children: proxy.handleTree(rows, 'menuId') }];
}

/** 新增菜单；从行内进入时预选该行作为父级。 */
async function handleAdd(row) {
  await loadMenuOptions();
  menuDialogRef.value.openDialog({
    title: '新增菜单',
    parentId: row?.menuId ?? 0,
    menuOptions: menuOptions.value
  });
}

/** 修改时由独立弹窗请求最新详情，确保表单回显服务端数据。 */
async function handleUpdate(row) {
  await loadMenuOptions();
  menuDialogRef.value.openDialog({ title: '修改菜单', menuId: row.menuId, menuOptions: menuOptions.value });
}

/** 详情按钮操作：请求最新菜单数据后打开只读详情弹窗。 */
function handleDetail(row) {
  getMenu(row.menuId).then((response) => {
    detailForm.value = response.data || {};
    openDetail.value = true;
  });
}

/** 关闭详情弹窗并清空详情数据。 */
function cancelDetail() {
  openDetail.value = false;
  detailForm.value = {};
}

/** 删除菜单，二次确认后调用接口并刷新菜单树。 */
function handleDelete(row) {
  proxy.$modal
    .confirm(`是否确认删除名称为"${row.menuName}"的数据项?`)
    .then(() => delMenu(row.menuId))
    .then(() => {
      proxy.$modal.msgSuccess('删除成功');
      getList();
    })
    .catch(() => {});
}

/** 菜单状态修改：二次确认后提交，取消或失败时回滚开关状态。 */
function handleStatusChange(row) {
  const text = row.status === '0' ? '启用' : '停用';
  proxy.$modal
    .confirm('确认要"' + text + '""' + row.menuName + '"菜单吗?')
    .then(() => updateMenu(row))
    .then(() => {
      proxy.$modal.msgSuccess(text + '成功');
    })
    .catch(function () {
      row.status = row.status === '0' ? '1' : '0';
    });
}

/** 遍历当前菜单树切换展开状态，不重新请求接口，后续刷新沿用同一默认展开值。 */
function toggleExpandAll() {
  isExpandAll.value = !isExpandAll.value;
  tableStore.config.table.defaultExpandAll = isExpandAll.value;
  const toggleRows = (rows) => {
    rows.forEach((row) => {
      tableRef.value?.elTableRef?.toggleRowExpansion(row, isExpandAll.value);
      if (row.children?.length) toggleRows(row.children);
    });
  };
  toggleRows(menuList.value);
}

// 首次进入加载完整上级菜单选项，列表查询仍由公共表格负责。
onMounted(loadMenuOptions);
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
</style>
