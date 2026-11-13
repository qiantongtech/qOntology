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
    <!-- 复用详情页头部组件：编号徽标 + 昵称 + 状态字典，返回行为由本页接管，关闭后回到用户列表。 -->
    <detail-info
      v-if="form.userId != null"
      :data="form"
      :header="{
        className: 'clearfixs',
        idKey: 'userId',
        nameKey: 'nickName',
        statusKey: 'status',
        statusOptions: sys_normal_disable,
        routerBack: false,
      }"
      :items="detailItems"
      mode="free"
      @back="close"
    >
      <!-- 提交按钮置于头部返回按钮之后，与底部提交共用同一保存逻辑。 -->
      <template #btn-right>
        <el-button type="primary" @click="submitForm()" icon="Edit">
          提交</el-button>
      </template>
    </detail-info>

    <div class="pagecont-bottom">
      <el-tabs v-model="activeName">
        <el-tab-pane label="角色信息" name="roleInfo">
          <qt-table v-bind="tableStore" ref="tableRef">
            <template #index="{ $index }">
              <span>{{ (pagination.pageNum - 1) * pagination.pageSize + $index + 1 }}</span>
            </template>
          </qt-table>
        </el-tab-pane>
      </el-tabs>
<!--      <h4 class="form-header h4">角色信息</h4>-->
<!--      -->

<!--      <div class="auth-role-footer">-->
<!--        <el-button type="primary" @click="submitForm()">提交</el-button>-->
<!--      </div>-->
    </div>
  </div>
</template>

<script setup name="AuthRole">
import { getAuthRole, updateAuthRole } from '@/api/system/system/user.js';

const route = useRoute();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const roleIds = ref([]);
const form = ref({
  nickName: undefined,
  userName: undefined,
  userId: undefined
});
const activeName = ref('roleInfo');


// 自由布局声明用户专属字段，与项目详情页一致，避免组件默认追加无关审计项。
const detailItems = [
  { label: "登录账号", key: "userName", span: 8 },
  { label: "手机号码", key: "phonenumber", span: 8 },
  { label: "邮箱", key: "email", span: 8 },
  { label: "所属部门", key: "dept.deptName", span: 8 ,className: "mt2"},
  { label: "创建时间", key: "createTime", type: "time", span: 8,className: "mt2" }
];

/**
 * 表格实例
 *
 * getList()    刷新当前页
 * resetQuery() 回第一页并重新查询
 */
const tableRef = ref(null);

const userId = route.params && route.params.userId;

// 角色数据一次性全量返回，翻页时复用缓存，与原实现一致不重复请求
let rolesCache = null;

// 已分配角色回显只在首次加载后执行一次，避免翻页覆盖用户新勾选
let selectionRestored = false;

/**
 * 前端分页参数，用于序号列的全局行号计算
 */
const pagination = reactive({
  pageNum: 1,
  pageSize: 10
});

/**
 * QtTable 统一配置
 *
 * autoPagination 为前端分页：一次取回全部角色，由组件按页切片展示
 */
const tableStore = reactive({
  config: {
    sort: false,
    autoPagination: true,
    table: {
      stripe: true,
      height: '500px',
      rowKey: 'roleId',
      onRowClick: clickRow,
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: 'selection',
      width: 55,
      minWidth: 55,
      align: 'center',
      reserveSelection: true
    },
    { label: '序号', width: 55, minWidth: 55, align: 'center', slot: 'index' },
    { label: '编号', prop: 'roleId', align: 'center' },
    { label: '角色名称', prop: 'roleName', align: 'center' },
    { label: '权限字符', prop: 'roleKey', align: 'center' },
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
  params: {},
  events: {
    onDataLoaded: handleDataLoaded,
    onPageCurrentChange: handlePageChange,
    onPageSizeChange: handlePageChange
  }
});

/**
 * QtTable 数据请求适配器
 *
 * 接口一次返回用户与该用户全部角色，基本信息在此一并回填
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData() {
  if (!userId) {
    return { data: { rows: [], total: 0 } };
  }
  if (!rolesCache) {
    const response = await getAuthRole(userId);
    form.value = response.user;
    rolesCache = response.roles ?? [];
  }

  return {
    data: {
      rows: rolesCache,
      total: rolesCache.length
    }
  };
}

/**
 * 列表加载完成后回显已分配角色（flag 为 true 的行选中）
 * @param {Array} data 当前全量行数据
 */
function handleDataLoaded(data) {
  if (selectionRestored) return;
  selectionRestored = true;
  nextTick(() => {
    data.forEach((row) => {
      if (row.flag) {
        tableRef.value?.elTableRef?.toggleRowSelection(row, true);
      }
    });
  });
}

/**
 * 分页变化回调：记录当前页码与每页条数供序号列计算
 * @param {Object} params 含 pageNum、pageSize 的分页参数
 */
function handlePageChange(params) {
  pagination.pageNum = params.pageNum;
  pagination.pageSize = params.pageSize;
}

/**
 * 单击选中行数据
 * @param {Object} row 行数据
 */
function clickRow(row) {
  tableRef.value?.elTableRef?.toggleRowSelection(row);
}

/**
 * 多选框选中数据
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  roleIds.value = selection.map((item) => item.roleId);
}

/**
 * 关闭当前页签，返回固定的用户列表入口（头部返回按钮与提交成功后共用）。
 */
function close() {
  const obj = { path: '/system/user' };
  proxy.$tab.closeOpenPage(obj);
}

/**
 * 提交按钮：保存分配的角色
 */
function submitForm() {
  const rIds = roleIds.value.join(',');
  updateAuthRole({ userId: form.value.userId, roleIds: rIds }).then(() => {
    proxy.$modal.msgSuccess('授权成功');
    close();
  });
}
</script>

<style scoped lang="scss">
// 提交按钮保持居中展示
.auth-role-footer {
  margin-top: 15px;
  text-align: center;
}
</style>
