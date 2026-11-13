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
  <!-- 授权用户 -->
  <el-dialog
    title="选择用户"
    v-model="visible"
    width="900px"
    top="5vh"
    append-to-body
  >
    <qt-search-bar
      :items="searchStore.items"
      :params="tableStore.params"
      :table-ref="tableRef"
    />
    <qt-table v-bind="tableStore" ref="tableRef" />
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSelectUser">确 定</el-button>
        <el-button @click="visible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="SelectUser">
import {
  authUserSelectAll,
  unallocatedUserList
} from '@/api/system/system/role.js';

const props = defineProps({
  roleId: {
    type: [Number, String]
  }
});

const { proxy } = getCurrentInstance();

const visible = ref(false);
const userIds = ref([]);

/**
 * 表格实例
 *
 * 弹窗内使用，config.initResquest 为 false，打开弹窗时由 show() 触发查询
 */
const tableRef = ref(null);

/**
 * 查询参数
 *
 * roleId 在弹窗打开时由父组件传入的角色 ID 填充
 */
const tableParams = reactive({
  roleId: undefined,
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
    sort: false,
    initResquest: false,
    table: {
      stripe: true,
      height: '260px',
      rowKey: 'userId',
      onRowClick: clickRow,
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
      date: true
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
  const response = await unallocatedUserList(params);

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

// 显示弹框
function show() {
  tableParams.roleId = props.roleId;
  visible.value = true;
  // 弹窗内容首次渲染后才能拿到表格实例，统一在下一帧回第一页查询
  nextTick(() => tableRef.value?.resetQuery());
}

/** 选择行 */
function clickRow(row) {
  tableRef.value?.elTableRef?.toggleRowSelection(row);
}

// 多选框选中数据
function handleSelectionChange(selection) {
  userIds.value = selection.map((item) => item.userId);
}

const emit = defineEmits(['ok']);

/** 选择授权用户操作 */
function handleSelectUser() {
  const roleId = tableParams.roleId;
  const uIds = userIds.value.join(',');
  if (uIds == '') {
    proxy.$modal.msgError('请选择要分配的用户');
    return;
  }
  authUserSelectAll({ roleId: roleId, userIds: uIds }).then((res) => {
    proxy.$modal.msgSuccess(res.msg);
    visible.value = false;
    emit('ok');
  });
}

defineExpose({
  show
});
</script>
