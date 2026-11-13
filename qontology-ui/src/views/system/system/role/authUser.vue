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
      <!-- 查询条件区域 -->
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="openSelectUser"
          v-hasPermi="['system:role:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>添加用户
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="cancelAuthUserAll"
          v-hasPermi="['system:role:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量取消授权
        </el-button>
        <el-button
          type="warning"
          plain
          icon="Close"
          @click="handleClose"
        >
          关闭
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            @click="cancelAuthUser(row)"
            v-hasPermi="['system:role:remove']"
            >取消授权</el-button
          >
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增授权弹窗：确认后回第一页重新查询已分配用户 -->
    <select-user ref="selectRef" :roleId="tableParams.roleId" @ok="handleSelectOk" />
  </div>
</template>

<script setup name="AuthUser">
import {
  allocatedUserList,
  authUserCancel,
  authUserCancelAll
} from '@/api/system/system/role.js';
import selectUser from './selectUser.vue';

const route = useRoute();
const { proxy } = getCurrentInstance();

const multiple = ref(true);
const userIds = ref([]);

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
 * roleId 来自路由，固定查询该角色已分配用户
 */
const tableParams = reactive({
  roleId: route.params.roleId,
  userName: undefined,
  phonenumber: undefined
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: '用户名称',
      prop: 'userName',
      component: { is: 'input', clearable: true }
    },
    {
      label: '手机号码',
      prop: 'phonenumber',
      component: { is: 'input', clearable: true }
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
      rowKey: 'userId',
      defaultSort: { prop: 'createTime', order: 'descending' },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: 'selection', width: 55, minWidth: 55, align: 'center' },
    {
      label: '用户名称',
      prop: 'userName',
      align: 'left',
      showOverflowTooltip: { effect: 'light' }
    },
    {
      label: '用户昵称',
      prop: 'nickName',
      align: 'left',
      showOverflowTooltip: { effect: 'light' }
    },
    {
      label: '邮箱',
      prop: 'email',
      align: 'left',
      showOverflowTooltip: { effect: 'light' }
    },
    {
      label: '手机',
      prop: 'phonenumber',
      align: 'left',
      showOverflowTooltip: { effect: 'light' }
    },
    { label: '状态', prop: 'status', align: 'center', dict: 'sys_normal_disable' },
    {
      label: '创建时间',
      prop: 'createTime',
      align: 'center',
      width: 180,
      minWidth: 180,
      date: true,
      sortable: true
    },
    {
      label: '操作',
      align: 'center',
      fixed: 'right',
      width: 100,
      minWidth: 100,
      slot: 'handle'
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  const response = await allocatedUserList(params);

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，取消授权成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 返回按钮：关闭当前页并回到角色列表
 */
function handleClose() {
  const obj = { path: '/system/role' };
  proxy.$tab.closeOpenPage(obj);
}

/**
 * 多选框选中数据
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  userIds.value = selection.map((item) => item.userId);
  multiple.value = !selection.length;
}

/**
 * 打开授权用户表弹窗
 */
function openSelectUser() {
  proxy.$refs['selectRef'].show();
}

/**
 * 新增授权成功回调：回第一页重新查询
 */
function handleSelectOk() {
  tableRef.value?.resetQuery();
}

/**
 * 取消授权按钮操作
 * @param {Object} row 行数据
 */
function cancelAuthUser(row) {
  proxy.$modal
    .confirm('确认要取消该用户"' + row.userName + '"角色吗？')
    .then(function () {
      return authUserCancel({ userId: row.userId, roleId: tableParams.roleId });
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('取消授权成功');
    })
    .catch(() => {});
}

/**
 * 批量取消授权按钮操作
 */
function cancelAuthUserAll() {
  const roleId = tableParams.roleId;
  const uIds = userIds.value.join(',');
  proxy.$modal
    .confirm('是否取消选中用户授权数据项?')
    .then(function () {
      return authUserCancelAll({ roleId: roleId, userIds: uIds });
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('取消授权成功');
    })
    .catch(() => {});
}
</script>
