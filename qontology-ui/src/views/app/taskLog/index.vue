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
  <div ref="appContainer" class="app-container">
    <div v-show="showSearch" class="pagecont-top">
      <el-form
        ref="queryRef"
        class="btn-style"
        :inline="true"
        :model="queryParams"
        label-width="75px"
        @submit.prevent
      >
        <el-form-item label="任务名称" prop="taskName"
          ><el-input
            v-model="queryParams.taskName"
            class="el-form-input-width"
            clearable
            placeholder="请输入任务名称"
            @keyup.enter="handleQuery"
        /></el-form-item>
        <el-form-item label="执行类型" prop="taskType"
          ><el-select
            v-model="queryParams.taskType"
            clearable
            placeholder="请选择执行类型"
            style="width: 200px"
            ><el-option
              v-for="item in app_task_log_type"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)" /></el-select
        ></el-form-item>
        <el-form-item label="执行状态" prop="status"
          ><el-select
            v-model="queryParams.status"
            clearable
            placeholder="请选择执行状态"
            style="width: 200px"
            ><el-option
              v-for="item in app_log_status"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)" /></el-select
        ></el-form-item>
        <el-form-item
          ><el-button
            plain
            type="primary"
            @click="handleQuery"
            @mousedown="(event) => event.preventDefault()"
            ><i class="iconfont-mini icon-a-zu22377 mr5" />查询</el-button
          ><el-button
            @click="resetQuery"
            @mousedown="(event) => event.preventDefault()"
            ><i class="iconfont-mini icon-a-zu22378 mr5" />重置</el-button
          ></el-form-item
        >
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style"
          ><el-col :span="1.5"
            ><el-button
              :disabled="!ids.length"
              icon="Delete"
              plain
              type="danger"
              @click="handleDelete"
              @mousedown="(event) => event.preventDefault()"
              >删除</el-button
            ></el-col
          ></el-row
        >
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:showSearch="showSearch"
            :columns="columns"
            @queryTable="getList"
          />
        </div>
      </div>
      <el-table
        v-loading="loading"
        :data="rows"
        :default-sort="defaultSort"
        :tooltip-options="tooltipOptions"
        stripe
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column align="center" type="selection" width="55" />
        <el-table-column
          v-if="columnVisible(1)"
          align="center"
          label="编号"
          prop="id"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="100"
        />
        <el-table-column
          v-if="columnVisible(2)"
          align="left"
          label="任务名称"
          prop="taskName"
          width="260"
          ><template #default="scope">{{
            scope.row.taskName || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(3)"
          align="center"
          label="执行类型"
          prop="taskType"
          width="150"
          ><template #default="scope"
            ><dict-tag
              :options="app_task_log_type"
              :value="scope.row.taskType" /></template
        ></el-table-column>
        <el-table-column
          v-if="columnVisible(4)"
          align="center"
          label="执行状态"
          prop="status"
          ><template #default="scope"
            ><dict-tag
              :options="app_log_status"
              :value="scope.row.status" /></template
        ></el-table-column>
        <el-table-column
          v-if="columnVisible(5)"
          align="center"
          label="开始时间"
          prop="startTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="160"
          ><template #default="scope">{{
            parseTime(scope.row.startTime, "{y}-{m}-{d} {h}:{i}")
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(6)"
          align="center"
          label="结束时间"
          prop="endTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="160"
          ><template #default="scope">{{
            parseTime(scope.row.endTime, "{y}-{m}-{d} {h}:{i}")
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(7)"
          align="center"
          label="创建人"
          prop="createBy"
          width="160"
          ><template #default="scope">{{
            scope.row.createBy || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(8)"
          align="center"
          label="创建时间"
          prop="createTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="160"
          ><template #default="scope">{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(9)"
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
          label="操作"
          width="200"
          ><template #default="scope"
            ><el-button
              icon="View"
              link
              type="primary"
              @click="showDetail(scope.row)"
              >查看步骤</el-button
            ></template
          ></el-table-column
        >
        <template #empty
          ><div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="" />
            <p>暂无记录</p>
          </div></template
        >
      </el-table>
      <pagination
        v-show="total > 0"
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNum"
        :total="total"
        @pagination="getList"
      />
    </div>

    <el-dialog
      v-model="detailOpen"
      :append-to="appContainer"
      draggable
      width="800px"
    >
      <template #header
        ><span class="el-dialog__title">抽取步骤</span></template
      >
      <el-table
        v-loading="detailLoading"
        :data="details"
        height="48vh"
        stripe
        @sort-change="handleDetailSortChange"
      >
        <el-table-column align="center" label="编号" type="index" width="70"
          ><template #default="scope">{{
            (detailParams.pageNum - 1) * detailParams.pageSize +
            scope.$index +
            1
          }}</template></el-table-column
        >
        <el-table-column
          align="left"
          label="执行步骤"
          prop="step"
          min-width="430"
          ><template #default="scope">{{
            scope.row.step || "-"
          }}</template></el-table-column
        >
        <el-table-column
          align="center"
          label="执行时间"
          prop="createTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="190"
          ><template #default="scope">{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
          }}</template></el-table-column
        >
        <template #empty
          ><div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="" />
            <p>暂无记录</p>
          </div></template
        >
      </el-table>
      <pagination
        v-show="detailTotal > 0"
        v-model:limit="detailParams.pageSize"
        v-model:page="detailParams.pageNum"
        :total="detailTotal"
        @pagination="getDetails"
      />
      <template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div></template
      >
    </el-dialog>
  </div>
</template>

<script setup name="AppTaskLog">
import { delTaskLog, getDetailPage, listTaskLog } from "@/api/app/taskLog";

const { proxy } = getCurrentInstance();
const { app_task_log_type, app_log_status } = proxy.useDict(
  "app_task_log_type",
  "app_log_status"
);
const appContainer = ref();
const loading = ref(false);
const detailLoading = ref(false);
const showSearch = ref(true);
const detailOpen = ref(false);
const rows = ref([]);
const details = ref([]);
const ids = ref([]);
const total = ref(0);
const detailTotal = ref(0);
const currentLogId = ref();
const defaultSort = { prop: "createTime", order: "descending" };
const tooltipOptions = { effect: "light" };
const columns = ref([
  { key: 1, label: "编号", visible: true },
  { key: 2, label: "任务名称", visible: true },
  { key: 3, label: "执行类型", visible: true },
  { key: 4, label: "执行状态", visible: true },
  { key: 5, label: "开始时间", visible: true },
  { key: 6, label: "结束时间", visible: true },
  { key: 7, label: "创建人", visible: true },
  { key: 8, label: "创建时间", visible: true },
  { key: 9, label: "操作", visible: true },
]);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  taskName: undefined,
  taskType: undefined,
  status: undefined,
  orderByColumn: "createTime",
  isAsc: "descending",
});
const detailParams = reactive({
  pageNum: 1,
  pageSize: 10,
  logId: undefined,
  orderByColumn: "createTime",
  isAsc: "descending",
});

/** 判断日志列表列是否显示。 */ function columnVisible(key) {
  return columns.value.find((item) => item.key === key)?.visible ?? true;
}
/** 查询抽取日志列表。 */ function getList() {
  loading.value = true;
  listTaskLog(queryParams)
    .then(({ data }) => {
      rows.value = data.rows;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}
/** 执行日志查询。 */ function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
/** 重置日志查询条件。 */ function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 处理日志列表排序。 */ function handleSortChange(column) {
  queryParams.orderByColumn = column.prop;
  queryParams.isAsc = column.order;
  getList();
}
/** 记录日志多选结果。 */ function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
}
/** 查询当前日志步骤。 */ function getDetails() {
  detailLoading.value = true;
  detailParams.logId = currentLogId.value;
  getDetailPage(detailParams)
    .then(({ data }) => {
      details.value = data.rows;
      detailTotal.value = data.total;
      detailOpen.value = true;
    })
    .finally(() => {
      detailLoading.value = false;
    });
}
/** 打开抽取步骤弹框。 */ function showDetail(row) {
  currentLogId.value = row.id;
  detailParams.pageNum = 1;
  getDetails();
}
/** 处理抽取步骤排序。 */ function handleDetailSortChange(column) {
  detailParams.orderByColumn = column.prop;
  detailParams.isAsc = column.order;
  getDetails();
}
/** 删除选中的抽取日志。 */ function handleDelete() {
  proxy.$modal
    .confirm(`是否确认删除抽取日志编号为“${ids.value.join(",")}”的数据项？`)
    .then(() => delTaskLog(ids.value.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      getList();
    })
    .catch(() => {});
}

onMounted(getList);
</script>
