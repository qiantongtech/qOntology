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
          v-hasPermi="['monitor:operlog:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleClean"
          v-hasPermi="['monitor:operlog:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>清空
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['monitor:operlog:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 消耗时间列在数值后拼接单位 -->
        <template #costTime="{ row }">
          <span>{{ row.costTime }}毫秒</span>
        </template>

        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            v-hasPermi="['monitor:operlog:query']"
            >详情</el-button
          >
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 操作日志详情对话框 -->
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
        ref="operlogDetailRef"
        :model="form"
        label-width="80px"
        class="column-form"
      >
        <qt-form-item label="系统模块" prop="title">
          <div class="form-readonly">{{ form.title || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="操作类型" prop="businessType">
          <dict-tag
            :options="sys_oper_type"
            :value="form.businessType"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="操作人员" prop="operName">
          <div class="form-readonly">{{ form.operName || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="操作地址" prop="operIp">
          <div class="form-readonly">{{ form.operIp || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="操作地点" prop="operLocation">
          <div class="form-readonly">{{ form.operLocation || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="请求方式" prop="requestMethod">
          <div class="form-readonly">{{ form.requestMethod || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="请求地址" prop="operUrl">
          <div class="form-readonly">{{ form.operUrl || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="消耗时间" prop="costTime">
          <div class="form-readonly">
            {{ form.costTime != null ? form.costTime + "毫秒" : "-" }}
          </div>
        </qt-form-item>
        <qt-form-item label="操作状态" prop="status">
          <dict-tag
            :options="sys_common_status"
            :value="form.status"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="操作时间" prop="operTime">
          <div class="form-readonly">
            {{ parseTime(form.operTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </qt-form-item>
        <!-- 类名、请求参数与返回结果为长文本，使用 textarea 修饰类整行展示。 -->
        <qt-form-item label="操作方法" prop="method" class="row-full">
          <div class="form-readonly textarea">{{ form.method || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="请求参数" prop="operParam" class="row-full">
          <div class="form-readonly textarea">{{ form.operParam || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="返回参数" prop="jsonResult" class="row-full">
          <div class="form-readonly textarea">{{ form.jsonResult || "-" }}</div>
        </qt-form-item>
        <qt-form-item
          v-if="form.status === 1"
          label="异常信息"
          prop="errorMsg"
          class="row-full"
        >
          <div class="form-readonly textarea">{{ form.errorMsg || "-" }}</div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Operlog">
import {
  list,
  delOperlog,
  cleanOperlog
} from "@/api/system/monitor/operlog.js";

// 当前组件实例，用于字典、弹窗提示、日期参数处理、文件下载等全局方法
const { proxy } = getCurrentInstance();

// 字典：sys_oper_type 操作类型，sys_common_status 操作状态
const { sys_oper_type, sys_common_status } = proxy.useDict(
  "sys_oper_type",
  "sys_common_status"
);

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
  operIp: undefined,
  title: undefined,
  operName: undefined,
  businessType: undefined,
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
      label: "操作地址",
      prop: "operIp",
      component: {
        is: "input"
      }
    },
    {
      label: "系统模块",
      prop: "title",
      component: {
        is: "input"
      }
    },
    {
      label: "操作人员",
      prop: "operName",
      component: {
        is: "input"
      }
    },
    {
      label: "类型",
      prop: "businessType",
      component: {
        is: "select",
        options: sys_oper_type,
        placeholder: "操作类型"
      }
    },
    {
      label: "状态",
      prop: "status",
      component: {
        is: "select",
        options: sys_common_status,
        placeholder: "操作状态"
      }
    },
    {
      label: "操作时间",
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
      rowKey: "operId",
      defaultSort: { prop: "operTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: "selection",
      width: 50,
      minWidth: 50,
      align: "center"
    },
    {
      label: "编号",
      prop: "operId",
      align: "center",
      width: 80, minWidth: 80,
      sortable: true
    },
    {
      label: "系统模块",
      prop: "title",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "操作类型",
      prop: "businessType",
      align: "center",
      dict: "sys_oper_type"
    },
    {
      label: "操作人员",
      prop: "operName",
      width: 110,
      minWidth: 110,
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      },
      sortable: true,
      sortOrders: ["descending", "ascending"]
    },
    {
      label: "操作地址",
      prop: "operIp",
      width: 130,
      minWidth: 130,
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "消耗时间",
      prop: "costTime",
      width: 110,
      minWidth: 110,
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      },
      sortable: true,
      sortOrders: ["descending", "ascending"],
      slot: "costTime"
    },
    {
      label: "操作状态",
      prop: "status",
      align: "center",
      dict: "sys_common_status",
      sortable: true
    },
    {
      label: "操作时间",
      prop: "operTime",
      width: 160,
      minWidth: 160,
      align: "center",
      date: true,
      sortable: true,
      sortOrders: ["descending", "ascending"]
    },
    {
      label: "操作",
      fixed: "right",
      width: 100,
      minWidth: 100,
      align: "center",
      slot: "handle"
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

const openDetail = ref(false);
const form = ref({});
const ids = ref([]);
const title = ref("");

// true 表示没有选择数据，不能执行批量删除
const multiple = ref(true);

/**
 * 表格多选事件
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.operId);
  multiple.value = !selection.length;
}

/**
 * 详情按钮操作，使用行数据打开操作日志详情弹窗
 *
 * @param {Object} row 行数据
 */
function handleDetail(row) {
  form.value = row;
  title.value = "操作日志详细";
  openDetail.value = true;
}

/**
 * 关闭详情弹窗并清空已展示的日志数据
 */
function cancel() {
  openDetail.value = false;
  form.value = {};
}

/**
 * 删除按钮操作
 *
 * @param {Object} [row] 行数据；顶部按钮触发时为批量删除
 */
function handleDelete(row) {
  const operIds = row?.operId ?? ids.value;
  proxy.$modal
    .confirm("是否确认删除编号为\"" + operIds + "\"的数据项?")
    .then(function () {
      return delOperlog(operIds);
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
    .confirm("是否确认清空所有操作日志数据项?")
    .then(function () {
      return cleanOperlog();
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("清空成功");
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
    "monitor/operlog/export",
    {
      ...requestParams
    },
    `config_${new Date().getTime()}.xlsx`
  );
}
</script>

<style scoped lang="scss">
/* 详情弹窗复用全局 column-form / form-readonly 样式，无页面级私有样式。 */
</style>
