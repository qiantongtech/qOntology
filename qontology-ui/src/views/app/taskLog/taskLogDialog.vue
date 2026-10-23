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
  <el-dialog v-model="logVisible">
    <template #header="{ close, titleId, titleClass }"
      ><span role="heading" aria-level="2" class="el-dialog__title">
        执行日志
      </span></template
    >
    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:showSearch="showSearch"
            :columns="columns"
            :search="false"
            @queryTable="getList"
          />
        </div>
      </div>
      <el-table
        v-loading="loading"
        :data="taskLogList"
        :default-sort="defaultSort"
        :tooltip-options="tooltipOptions"
        stripe
        @sort-change="handleSortChange"
      >
        <el-table-column
          v-if="columnVisible(1)"
          align="center"
          label="编号"
          prop="id"
        />
        <el-table-column
          v-if="columnVisible(2)"
          align="center"
          label="执行状态"
          prop="status"
          ><template #default="scope"
            ><dict-tag
              :options="app_log_status"
              :value="scope.row.status" /></template
        ></el-table-column>
        <el-table-column
          v-if="columnVisible(3)"
          align="center"
          label="执行开始时间"
          prop="startTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="180"
          ><template #default="scope"
            ><span>{{
              parseTime(scope.row.startTime, "{y}-{m}-{d} {h}:{i}:{s}")
            }}</span></template
          ></el-table-column
        >
        <el-table-column
          v-if="columnVisible(4)"
          align="center"
          label="执行结束时间"
          prop="endTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="180"
          ><template #default="scope"
            ><span>{{
              parseTime(scope.row.endTime, "{y}-{m}-{d} {h}:{i}:{s}")
            }}</span></template
          ></el-table-column
        >
        <el-table-column
          align="center"
          class-name="small-padding fixed-width"
          label="操作"
          width="240"
          ><template #default="scope">
            <el-button
              v-if="scope.row.errorMsg"
              icon="View"
              link
              type="primary"
              @click="showError(scope.row)"
              >查看错误日志</el-button
            >
            <el-button
              icon="View"
              link
              type="primary"
              @click="showLogDetail(scope.row)"
              >查看具体步骤</el-button
            >
          </template></el-table-column
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
    <template #footer
      ><div class="dialog-footer">
        <el-button type="primary" size="mini" @click="logVisible = false"
          >关 闭</el-button
        >
      </div></template
    >

    <el-dialog
      v-model="detailVisible"
      width="800px"
      :append-to="appendTo"
      draggable
    >
      <template #header="{ close, titleId, titleClass }"
        ><span role="heading" aria-level="2" class="el-dialog__title">
          操作步骤
        </span></template
      >
      <div class="justify-between mb15">
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:showSearch="showSearch"
            :columns="detailColumns"
            :search="false"
            @queryTable="getLogDetails"
          />
        </div>
      </div>
      <el-table
        v-loading="detailLoading"
        :data="detailList"
        stripe
        @sort-change="handleDetailSortChange"
      >
        <el-table-column
          v-if="detailColumnVisible(1)"
          align="center"
          label="执行步骤"
          prop="step"
          ><template #default="scope">{{
            scope.row.step || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="detailColumnVisible(2)"
          align="center"
          label="执行时间"
          prop="createTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="190"
          ><template #default="scope"
            ><span>{{
              parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}")
            }}</span></template
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
        v-show="detailTotal > 0"
        v-model:limit="detailParams.pageSize"
        v-model:page="detailParams.pageNum"
        :total="detailTotal"
        @pagination="getLogDetails"
      />
      <template #footer
        ><div class="dialog-footer">
          <el-button type="primary" size="mini" @click="detailVisible = false"
            >关 闭</el-button
          >
        </div></template
      >
    </el-dialog>

    <el-dialog
      v-model="errorVisible"
      title="错误日志"
      width="800px"
      :append-to="appendTo"
      destroy-on-close
    >
      <template #header="{ close, titleId, titleClass }"
        ><span role="heading" aria-level="2" class="el-dialog__title">
          错误日志
        </span></template
      >
      <div class="wrap-line">{{ errorMessage }}</div>
      <template #footer
        ><div class="dialog-footer">
          <el-button type="primary" size="mini" @click="errorVisible = false"
            >关 闭</el-button
          >
        </div></template
      >
    </el-dialog>
  </el-dialog>
</template>

<script setup name="AppTaskLogDialog">
import { getDetailPage, listTaskLog } from "@/api/app/taskLog";

const { proxy } = getCurrentInstance();
defineProps({
  appendTo: {
    type: [Object, String],
    default: undefined,
  },
});
const { app_log_status } = proxy.useDict("app_log_status");
const logVisible = ref(false);
const detailVisible = ref(false);
const errorVisible = ref(false);
const loading = ref(false);
const detailLoading = ref(false);
const showSearch = ref(true);
const taskLogList = ref([]);
const detailList = ref([]);
const total = ref(0);
const detailTotal = ref(0);
const errorMessage = ref("");
const currentTaskId = ref();
const currentTaskType = ref(1);
const currentLogId = ref();
const defaultSort = { prop: "createTime", order: "descending" };
const tooltipOptions = { effect: "light" };
const columns = ref([
  { key: 1, label: "id", visible: true },
  { key: 2, label: "执行状态", visible: true },
  { key: 3, label: "执行开始时间", visible: true },
  { key: 4, label: "执行结束时间", visible: true },
]);
const detailColumns = ref([
  { key: 1, label: "执行步骤", visible: true },
  { key: 2, label: "执行时间", visible: true },
]);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  taskId: undefined,
  taskType: 1,
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

/** 判断日志列是否显示。 */
function columnVisible(key) {
  return columns.value.find((item) => item.key === key)?.visible ?? true;
}
/** 判断步骤列是否显示。 */
function detailColumnVisible(key) {
  return detailColumns.value.find((item) => item.key === key)?.visible ?? true;
}
/** 查询当前任务执行日志。 */
function getList() {
  loading.value = true;
  queryParams.taskId = currentTaskId.value;
  queryParams.taskType = currentTaskType.value;
  listTaskLog(queryParams)
    .then(({ data }) => {
      taskLogList.value = data.rows;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}
/** 打开执行日志弹框。 */
function show(taskId, taskType = 1) {
  currentTaskId.value = taskId;
  currentTaskType.value = taskType;
  queryParams.pageNum = 1;
  logVisible.value = true;
  getList();
}
/** 查询当前日志执行步骤。 */
function getLogDetails() {
  detailLoading.value = true;
  detailParams.logId = currentLogId.value;
  getDetailPage(detailParams)
    .then(({ data }) => {
      detailList.value = data.rows;
      detailTotal.value = data.total;
      detailVisible.value = true;
    })
    .finally(() => {
      detailLoading.value = false;
    });
}
/** 打开具体步骤弹框。 */
function showLogDetail(row) {
  currentLogId.value = row.id;
  detailParams.pageNum = 1;
  getLogDetails();
}
/** 打开错误日志弹框。 */
function showError(row) {
  errorMessage.value = row.errorMsg;
  errorVisible.value = true;
}
/** 处理日志排序。 */
function handleSortChange(column) {
  queryParams.orderByColumn = column.prop;
  queryParams.isAsc = column.order;
  getList();
}
/** 处理步骤排序。 */
function handleDetailSortChange(column) {
  detailParams.orderByColumn = column.prop;
  detailParams.isAsc = column.order;
  getLogDetails();
}

defineExpose({ show });
</script>

<style scoped>
.wrap-line {
  white-space: pre-wrap;
}
.app-container .pagecont-bottom {
  margin: -10px 0 0 0;
  padding: 0;
}
.app-container .justify-between {
  margin-top: -10px;
  margin-bottom: 10px;
}
</style>
