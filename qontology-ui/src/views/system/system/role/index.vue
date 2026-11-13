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
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <!-- 查询条件区域：创建时间经 dateRange 临时字段转换后传给后端 -->
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['system:role:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:role:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['system:role:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 状态列保留启用/停用开关交互 -->
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
            v-hasPermi="['system:role:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情
          </el-button>
          <el-button
            v-if="row.roleId !== 1"
            link
            type="primary"
            @click="handleUpdate(row)"
            v-hasPermi="['system:role:edit']"
          >
            <i class="iconfont-mini icon-a-xiugaixianxing"></i>
            修改</el-button
          >
          <el-button
            v-if="row.roleId !== 1"
            link
            type="danger"
            :disabled="row.status === '0'"
            @click="handleDelete(row)"
            v-hasPermi="['system:role:remove']"
          >
            <i class="iconfont-mini icon-a-shanchuxianxing"></i>
            删除</el-button
          >
          <el-popover
            v-if="row.roleId !== 1 && hasPermi(['system:role:edit'])"
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
                link
                type="primary"
                @click="handleDataScope(row)"
                v-hasPermi="['system:role:edit']"
              >
                <i class="iconfont-mini icon-a-mimaxianxing"></i>
                数据权限</el-button
              >
              <el-button
                link
                type="primary"
                @click="handleAuthUser(row)"
                v-hasPermi="['system:role:edit']"
              >
                <i class="iconfont-mini icon-a-yonghuzhanghaoxianxing"></i
                >分配用户</el-button
              >
            </div>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增或修改角色配置对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="roleRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
      >
        <qt-form-item label="角色名称" class="row-full" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </qt-form-item>
        <qt-form-item label="权限字符" prop="roleKey" class="row-full" tip="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasRole('admin')`)">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </qt-form-item>
        <qt-form-item label="角色顺序" class="row-full" prop="roleSort">
          <el-input-number
            class="role-sort-input"
            v-model="form.roleSort"
            controls-position="right"
            :min="0"
          />
        </qt-form-item>
        <qt-form-item label="状态" class="row-full">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="菜单权限" class="row-full">
          <el-checkbox
            v-model="menuExpand"
            @change="handleCheckedTreeExpand($event, 'menu')"
            >展开/折叠</el-checkbox
          >
          <el-checkbox
            v-model="menuNodeAll"
            @change="handleCheckedTreeNodeAll($event, 'menu')"
            >全选/全不选</el-checkbox
          >
          <el-checkbox
            v-model="form.menuCheckStrictly"
            @change="handleCheckedTreeConnect($event, 'menu')"
            >父子联动</el-checkbox
          >
          <el-tree
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            ref="menuRef"
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          ></el-tree>
        </qt-form-item>
        <qt-form-item label="备注" class="row-full">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
            maxlength="256个字符"
            show-word-limit
          ></el-input>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 角色详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form
        ref="roleDetailRef"
        :model="form"
        label-width="90px"
        class="column-form"
      >
        <qt-form-item label="角色名称" prop="roleName">
          <div class="form-readonly">{{ form.roleName || '-' }}</div>
        </qt-form-item>
        <qt-form-item label="权限字符" prop="roleKey">
          <div class="form-readonly">{{ form.roleKey || '-' }}</div>
        </qt-form-item>
        <qt-form-item label="角色顺序" prop="roleSort">
          <div class="form-readonly">{{ form.roleSort ?? '-' }}</div>
        </qt-form-item>
        <qt-form-item label="状态" prop="status">
          <dict-tag
            :options="sys_normal_disable"
            :value="form.status"
            class="con-value access-tag"
          />
        </qt-form-item>
        <!-- 菜单权限沿用编辑弹窗的树结构，节点全部禁用仅作只读展示 -->
        <qt-form-item label="菜单权限" class="row-full">
          <el-tree
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            check-strictly
            default-expand-all
            node-key="id"
            ref="detailMenuRef"
            :props="{ label: 'label', children: 'children', disabled: () => true }"
            empty-text="加载中，请稍候"
          ></el-tree>
        </qt-form-item>
        <qt-form-item label="备注" prop="remark" class="row-full">
          <div class="form-readonly textarea">{{ form.remark || '-' }}</div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色数据权限对话框 -->
    <el-dialog
      :title="title"
      v-model="openDataScope"
      width="500px"
      append-to-body
    >
      <el-form :model="form" label-width="80px">
        <qt-form-item label="角色名称">
          <el-input v-model="form.roleName" :disabled="true" />
        </qt-form-item>
        <qt-form-item label="权限字符">
          <el-input v-model="form.roleKey" :disabled="true" />
        </qt-form-item>
        <qt-form-item label="权限范围">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </qt-form-item>
        <qt-form-item label="数据权限" v-show="form.dataScope == 2">
          <el-checkbox
            v-model="deptExpand"
            @change="handleCheckedTreeExpand($event, 'dept')"
            >展开/折叠</el-checkbox
          >
          <el-checkbox
            v-model="deptNodeAll"
            @change="handleCheckedTreeNodeAll($event, 'dept')"
            >全选/全不选</el-checkbox
          >
          <el-checkbox
            v-model="form.deptCheckStrictly"
            @change="handleCheckedTreeConnect($event, 'dept')"
            >父子联动</el-checkbox
          >
          <el-tree
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            default-expand-all
            ref="deptRef"
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          ></el-tree>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataScope">确 定</el-button>
          <el-button @click="cancelDataScope">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Role">
import {
  addRole,
  changeRoleStatus,
  dataScope,
  delRole,
  deptTreeSelect,
  getRole,
  listRole,
  updateRole
} from '@/api/system/system/role.js';
import {
  roleMenuTreeselect,
  treeselect as menuTreeselect
} from '@/api/system/system/menu.js';
import useUserStore from '@/store/system/user';

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const open = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const selectedRows = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref('');
const menuOptions = ref([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const deptExpand = ref(true);
const deptNodeAll = ref(false);
const deptOptions = ref([]);
const openDataScope = ref(false);
const menuRef = ref(null);
const deptRef = ref(null);
const detailMenuRef = ref(null);

/** 数据范围选项*/
const dataScopeOptions = ref([
  { value: '1', label: '全部数据权限' },
  { value: '2', label: '自定数据权限' },
  { value: '3', label: '本部门数据权限' },
  { value: '4', label: '本部门及以下数据权限' },
  { value: '5', label: '仅本人数据权限' }
]);

/**
 * 表格实例
 *
 * getList()    刷新当前页
 * resetQuery() 回第一页并重新查询
 */
const tableRef = ref(null);

/**
 * 查询参数
 *
 * dateRange 为创建时间查询控件的临时字段，请求前经 addDateRange 转换
 */
const tableParams = reactive({
  roleName: undefined,
  roleKey: undefined,
  status: undefined,
  dateRange: []
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: '角色名称',
      prop: 'roleName',
      component: { is: 'input', clearable: true }
    },
    {
      label: '权限字符',
      prop: 'roleKey',
      component: { is: 'input', clearable: true }
    },
    {
      label: '状态',
      prop: 'status',
      component: { is: 'select', options: sys_normal_disable, clearable: true }
    },
    {
      label: '创建时间',
      prop: 'dateRange',
      component: {
        is: 'date-picker',
        type: 'daterange',
        valueFormat: 'YYYY-MM-DD',
        rangeSeparator: '-',
        startPlaceholder: '开始日期',
        endPlaceholder: '结束日期',
        clearable: true
      }
    }
  ]
});

/**
 * QtTable 统一配置
 */
const tableStore = reactive({
  config: {
    // 启用表头排序，首次请求默认按创建时间倒序
    sort: true,
    table: {
      stripe: true,
      rowKey: 'roleId',
      defaultSort: { prop: 'createTime', order: 'descending' },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: 'selection', width: 55, minWidth: 55, align: 'center' },
    { label: '编号',width: 80, minWidth: 80, prop: 'roleId', align: 'center', sortable: true },
    {
      label: '角色名称',
      prop: 'roleName',
      align: 'center',
      showOverflowTooltip: { effect: 'light' }
    },
    {
      label: '权限字符',
      prop: 'roleKey',
      align: 'center',
      showOverflowTooltip: { effect: 'light' }
    },
    { label: '显示顺序', prop: 'roleSort', align: 'center', hide: true },
    { label: '状态', prop: 'status', align: 'center', slot: 'status' },
    {
      label: '创建时间',
      prop: 'createTime',
      align: 'center',
      width: 160,
      minWidth: 160,
      date: true,
      sortable: true
    },
    {
      label: '操作',
      align: 'center',
      fixed: 'right',
      width: 280,
      minWidth: 280,
      slot: 'handle'
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

const data = reactive({
  form: {},
  rules: {
    roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
    roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
    roleSort: [{ required: true, message: '角色顺序不能为空', trigger: 'blur' }]
  }
});

const { form, rules } = toRefs(data);

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const { dateRange, ...requestParams } = params;

  // addDateRange 会转成后端约定的 params.beginTime / params.endTime
  const response = await listRole(proxy.addDateRange(requestParams, dateRange));

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，新增/修改/删除成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 删除按钮操作
 * @param {Object} [row] 行数据；无值时为批量删除
 */
function handleDelete(row) {
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.roleId 区分）
  if (row?.roleId != null) {
    const roleIds = row.roleId;
    proxy.$modal
      .confirm('是否确认删除编号为"' + roleIds + '"的数据项?')
      .then(function () {
        return delRole(roleIds);
      })
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .catch(() => {});
    return;
  }

  // 批量删除：根据状态（'0'=启用不可删除，'1'=禁用可删除）判断
  const rows = selectedRows.value;
  if (!rows.length) return;
  const deletableRows = rows.filter((item) => item.status === '1');
  const undeletableCount = rows.length - deletableRows.length;
  const deletableIds = deletableRows.map((item) => item.roleId);
  proxy.$modal
    .confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    )
    .then(function () {
      if (!deletableIds.length) {
        proxy.$modal.msgWarning('无可删除的数据');
        return;
      }
      return delRole(deletableIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

/**
 * 导出按钮操作：使用当前查询条件（不含日期临时字段）
 */
function handleExport() {
  const { dateRange, ...requestParams } = tableParams;
  proxy.download(
    'system/role/export',
    { ...requestParams },
    `role_${new Date().getTime()}.xlsx`
  );
}

/**
 * 多选框选中数据
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.roleId);
  selectedRows.value = selection;
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/**
 * 角色状态修改
 * @param {Object} row 行数据
 */
function handleStatusChange(row) {
  let text = row.status === '0' ? '启用' : '停用';
  proxy.$modal
    .confirm('确认要"' + text + '""' + row.roleName + '"角色吗?')
    .then(function () {
      return changeRoleStatus(row.roleId, row.status);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + '成功');
    })
    .catch(function () {
      row.status = row.status === '0' ? '1' : '0';
    });
}

/**
 * 更多操作
 * @param {String} command 命令标识
 * @param {Object} row 行数据
 */
function handleCommand(command, row) {
  switch (command) {
    case 'handleDataScope':
      handleDataScope(row);
      break;
    case 'handleAuthUser':
      handleAuthUser(row);
      break;
    default:
      break;
  }
}

/**
 * 分配用户：跳转已分配用户页
 * @param {Object} row 行数据
 */
function handleAuthUser(row) {
  router.push('/system/role-auth/user/' + row.roleId);
}

/**
 * 查询菜单树结构
 */
function getMenuTreeselect() {
  menuTreeselect().then((response) => {
    menuOptions.value = response.data;
  });
}

/**
 * 所有部门节点数据
 * @returns {Array} 选中与半选中的部门节点合并结果
 */
function getDeptAllCheckedKeys() {
  // 目前被选中的部门节点
  let checkedKeys = deptRef.value.getCheckedKeys();
  // 半选中的部门节点
  let halfCheckedKeys = deptRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}

/**
 * 重置新增的表单以及其他数据
 */
function reset() {
  if (menuRef.value != undefined) {
    menuRef.value.setCheckedKeys([]);
  }
  menuExpand.value = false;
  menuNodeAll.value = false;
  deptExpand.value = true;
  deptNodeAll.value = false;
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: '0',
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined
  };
  proxy.resetForm('roleRef');
}

/**
 * 添加角色
 */
function handleAdd() {
  reset();
  getMenuTreeselect();
  open.value = true;
  title.value = '新增角色';
}

/**
 * 修改角色
 * @param {Object} [row] 行数据；无值时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const roleId = row?.roleId || ids.value;
  const roleMenu = getRoleMenuTreeselect(roleId);
  getRole(roleId).then((response) => {
    form.value = response.data;
    form.value.roleSort = Number(form.value.roleSort);
    open.value = true;
    nextTick(() => {
      roleMenu.then((res) => {
        let checkedKeys = res.checkedKeys;
        checkedKeys.forEach((v) => {
          nextTick(() => {
            menuRef.value.setChecked(v, true, false);
          });
        });
      });
    });
    title.value = '修改角色';
  });
}

/**
 * 根据角色ID查询菜单树结构
 * @param {number|string} roleId 角色ID
 * @returns {Promise} 菜单树响应
 */
function getRoleMenuTreeselect(roleId) {
  return roleMenuTreeselect(roleId).then((response) => {
    menuOptions.value = response.menus;
    return response;
  });
}

/**
 * 根据角色ID查询部门树结构
 * @param {number|string} roleId 角色ID
 * @returns {Promise} 部门树响应
 */
function getDeptTree(roleId) {
  return deptTreeSelect(roleId).then((response) => {
    deptOptions.value = response.depts;
    return response;
  });
}

/**
 * 树权限（展开/折叠）
 * @param {boolean} value 是否展开
 * @param {String} type 树类型：menu / dept
 */
function handleCheckedTreeExpand(value, type) {
  if (type == 'menu') {
    let treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  } else if (type == 'dept') {
    let treeList = deptOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
}

/**
 * 树权限（全选/全不选）
 * @param {boolean} value 是否全选
 * @param {String} type 树类型：menu / dept
 */
function handleCheckedTreeNodeAll(value, type) {
  if (type == 'menu') {
    menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  } else if (type == 'dept') {
    deptRef.value.setCheckedNodes(value ? deptOptions.value : []);
  }
}

/**
 * 树权限（父子联动）
 * @param {boolean} value 是否联动
 * @param {String} type 树类型：menu / dept
 */
function handleCheckedTreeConnect(value, type) {
  if (type == 'menu') {
    form.value.menuCheckStrictly = value ? true : false;
  } else if (type == 'dept') {
    form.value.deptCheckStrictly = value ? true : false;
  }
}

/**
 * 所有菜单节点数据
 * @returns {Array} 选中与半选中的菜单节点合并结果
 */
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}

/**
 * 提交按钮（新增/修改角色）
 */
function submitForm() {
  proxy.$refs['roleRef'].validate((valid) => {
    if (valid) {
      if (form.value.roleId != undefined) {
        form.value.menuIds = getMenuAllCheckedKeys();
        updateRole(form.value).then((response) => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        form.value.menuIds = getMenuAllCheckedKeys();
        addRole(form.value).then((response) => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/**
 * 取消按钮
 */
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

/**
 * 详情角色：复用 getRole 和角色菜单树回显只读信息
 * @param {Object} [row] 行数据；无值时取多选框第一条
 */
function handleDetail(row) {
  reset();
  const roleId = row?.roleId || ids.value;
  getRole(roleId).then((response) => {
    form.value = response.data;
    form.value.roleSort = Number(form.value.roleSort);
    openDetail.value = true;
    title.value = '角色详情';
  });
  const roleMenu = getRoleMenuTreeselect(roleId);
  roleMenu.then((res) => {
    nextTick(() => {
      detailMenuRef.value?.setCheckedKeys(res.checkedKeys);
    });
  });
}

/**
 * 选择角色权限范围触发
 * @param {String} value 权限范围值
 */
function dataScopeSelectChange(value) {
  if (value !== '2') {
    deptRef.value.setCheckedKeys([]);
  }
}

/**
 * 分配数据权限操作
 * @param {Object} row 行数据
 */
function handleDataScope(row) {
  reset();
  const deptTreeSelect = getDeptTree(row.roleId);
  getRole(row.roleId).then((response) => {
    form.value = response.data;
    openDataScope.value = true;
    nextTick(() => {
      deptTreeSelect.then((res) => {
        nextTick(() => {
          if (deptRef.value) {
            deptRef.value.setCheckedKeys(res.checkedKeys);
          }
        });
      });
    });
    title.value = '分配数据权限';
  });
}

/**
 * 提交按钮（数据权限）
 */
function submitDataScope() {
  if (form.value.roleId != undefined) {
    form.value.deptIds = getDeptAllCheckedKeys();
    dataScope(form.value).then((response) => {
      proxy.$modal.msgSuccess('修改成功');
      openDataScope.value = false;
      getList();
    });
  }
}

/**
 * 取消按钮（数据权限）
 */
function cancelDataScope() {
  openDataScope.value = false;
  reset();
}

/**
 * 判断当前用户是否拥有指定权限
 * @param {string|Array} permission 权限标识或标识数组
 * @returns {boolean} 是否拥有权限
 */
function hasPermi(permission) {
  // 从 store 或 localStorage 获取用户权限列表
  const permissions = useUserStore().permissions;

  if (Array.isArray(permission)) {
    return permission.some((p) => permissions.includes(p));
  } else {
    return permissions.includes(permission);
  }
}
</script>

<style scoped lang="scss">
// 角色顺序输入框占满表单项宽度
.role-sort-input {
  width: 100%;
}

// 权限字符标签提示图标沿用原灰色
.label-info-icon {
  color: var(--el-text-color-secondary);
}
</style>
