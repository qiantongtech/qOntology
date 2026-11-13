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
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['monitor:logininfor:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>删除
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleClean"
          v-hasPermi="['monitor:logininfor:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>清空
        </el-button>
        <el-button
          type="primary"
          plain
          :disabled="single"
          @click="handleUnlock"
          v-hasPermi="['monitor:logininfor:unlock']"
        >
          <i class="iconfont-mini icon-lock-unlock-line mr5"></i>解锁
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['monitor:logininfor:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef" />
    </qt-wrap>
  </div>
</template>

<script setup name="Logininfor">
import {
  list,
  delLogininfor,
  cleanLogininfor,
  unlockLogininfor
} from "@/api/system/monitor/logininfor.js";

// 当前组件实例，用于字典、弹窗提示、日期参数处理、文件下载等全局方法
const { proxy } = getCurrentInstance();

// 字典：sys_common_status 登录状态
const { sys_common_status } = proxy.useDict("sys_common_status");

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
 * QtSearchBar 负责修改条件，QtTable 负责补充分页与排序参数
 */
const tableParams = reactive({
  ipaddr: undefined,
  userName: undefined,
  status: undefined,

  // 日期范围临时字段，请求前经 addDateRange 转换
  dateRange: []
});

/**
 * 查询栏配置，prop 与 tableParams 字段一一对应
 */
const searchStore = reactive({
  items: [
    {
      label: "登录地址",
      prop: "ipaddr",
      component: {
        is: "input"
      }
    },
    {
      label: "用户名称",
      prop: "userName",
      component: {
        is: "input"
      }
    },
    {
      label: "状态",
      prop: "status",
      component: {
        is: "select",
        options: sys_common_status,
        placeholder: "登录状态"
      }
    },
    {
      label: "登录时间",
      prop: "dateRange",
      component: {
        is: "date-picker",
        type: "daterange",
        valueFormat: "YYYY-MM-DD",
        rangeSeparator: "-",
        startPlaceholder: "开始日期",
        endPlaceholder: "结束日期"
      }
    }
  ]
});

/**
 * QtTable 统一配置
 */
const tableStore = reactive({
  config: {
    // 启用组件排序机制，首次请求自动补 orderByColumn / isAsc 默认排序参数
    sort: true,
    table: {
      stripe: true,
      rowKey: "infoId",
      defaultSort: { prop: "loginTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center"
    },
    {
      label: "编号",
      prop: "infoId",
      align: "center",
      width: 80, minWidth: 80,
      sortable: true
    },
    {
      label: "用户名称",
      prop: "userName",
      width: 120,
      minWidth: 120,
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      },
      sortable: true,
      sortOrders: ["descending", "ascending"]
    },
    {
      label: "地址",
      prop: "ipaddr",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "登录地点",
      prop: "loginLocation",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "操作系统",
      prop: "os",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "浏览器",
      prop: "browser",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "描述",
      prop: "msg",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "登录状态",
      prop: "status",
      align: "center",
      dict: "sys_common_status",
      sortable: true
    },
    {
      label: "访问时间",
      prop: "loginTime",
      width: 160,
      minWidth: 160,
      align: "center",
      date: true,
      sortable: true,
      sortOrders: ["descending", "ascending"]
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize + 排序参数
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const {
    dateRange,
    ...requestParams
  } = params;

  // addDateRange 会转成后端参数 params.beginTime、params.endTime
  const response = await list(
    proxy.addDateRange(requestParams, dateRange)
  );

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，删除/清空成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

const ids = ref([]);

// true 表示未选择或选择了多条，不能执行解锁
const single = ref(true);

// true 表示没有选择数据，不能执行批量删除
const multiple = ref(true);

// 选中行的用户名称，解锁接口按用户名处理
const selectName = ref([]);

/**
 * 表格多选事件
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.infoId);
  multiple.value = !selection.length;
  single.value = selection.length !== 1;
  selectName.value = selection.map((item) => item.userName);
}

/**
 * 删除按钮操作
 *
 * @param {Object} [row] 行数据；顶部按钮触发时为批量删除
 */
function handleDelete(row) {
  const infoIds = row?.infoId ?? ids.value;
  proxy.$modal
    .confirm("是否确认删除编号为\"" + infoIds + "\"的数据项?")
    .then(function () {
      return delLogininfor(infoIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 清空按钮操作
 */
function handleClean() {
  proxy.$modal
    .confirm("是否确认清空所有登录日志数据项?")
    .then(function () {
      return cleanLogininfor();
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("清空成功");
    })
    .catch(() => {});
}

/**
 * 解锁按钮操作，解锁选中用户的登录锁定状态
 */
function handleUnlock() {
  const username = selectName.value;
  proxy.$modal
    .confirm("是否确认解锁用户\"" + username + "\"数据项?")
    .then(function () {
      return unlockLogininfor(username);
    })
    .then(() => {
      proxy.$modal.msgSuccess("用户" + username + "解锁成功");
    })
    .catch(() => {});
}

/**
 * 导出按钮操作，沿用原逻辑不携带日期范围
 */
function handleExport() {
  const {
    dateRange,
    ...requestParams
  } = tableParams;
  proxy.download(
    "monitor/logininfor/export",
    {
      ...requestParams
    },
    `config_${new Date().getTime()}.xlsx`
  );
}
</script>
