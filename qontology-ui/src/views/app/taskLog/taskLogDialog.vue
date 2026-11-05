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
  <el-dialog
    v-model="logVisible"
    title="执行日志"
    width="1000px"
    :append-to="appendTo"
    draggable
  >
    <qt-table v-bind="logTableStore" ref="logTableRef">
      <template #status="{ row }">
        <dict-tag :options="app_log_status" :value="row.status" />
      </template>
      <template #handle="{ row }">
        <el-button
          v-if="row.errorMsg"
          icon="View"
          link
          type="primary"
          @click="showError(row)"
        >查看错误日志</el-button>
        <el-button
          icon="View"
          link
          type="primary"
          @click="showLogDetail(row)"
        >查看具体步骤</el-button>
      </template>
    </qt-table>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="logVisible = false">关 闭</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="detailVisible"
    title="操作步骤"
    width="800px"
    :append-to="appendTo"
    draggable
  >
    <el-table
      v-loading="detailLoading"
      :data="detailList"
      :default-sort="detailDefaultSort"
      stripe
      @sort-change="handleDetailSortChange"
    >
      <el-table-column
        align="left"
        label="执行步骤"
        prop="step"
        min-width="430"
        :show-overflow-tooltip="{ effect: 'light' }"
      >
        <template #default="scope">{{ scope.row.step || "-" }}</template>
      </el-table-column>
      <el-table-column
        align="center"
        label="执行时间"
        prop="createTime"
        sortable="custom"
        :sort-orders="['descending', 'ascending']"
        width="190"
      >
        <template #default="scope">
          {{
            proxy.parseTime(
              scope.row.createTime,
              "{y}-{m}-{d} {h}:{i}:{s}",
            ) || "-"
          }}
        </template>
      </el-table-column>
      <template #empty>
        <div class="emptyBg">
          <img src="@/assets/common/images/empty-state/noData.png" alt="" />
          <p>暂无记录</p>
        </div>
      </template>
    </el-table>
    <pagination
      v-show="detailTotal > 0"
      v-model:limit="detailParams.pageSize"
      v-model:page="detailParams.pageNum"
      :total="detailTotal"
      @pagination="getLogDetails"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="detailVisible = false">关 闭</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="errorVisible"
    title="错误日志"
    width="800px"
    :append-to="appendTo"
    destroy-on-close
  >
    <div class="wrap-line">{{ errorMessage }}</div>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="errorVisible = false">关 闭</el-button>
      </div>
    </template>
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
const detailLoading = ref(false);
const logTableRef = ref();
const detailList = ref([]);
const detailTotal = ref(0);
const errorMessage = ref("");
const currentLogId = ref();
const detailDefaultSort = { prop: "createTime", order: "descending" };

const logTableStore = reactive({
  config: {
    initResquest: false,
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
    },
    pagination: {
      notAutoScroll: true,
    },
  },
  columns: [
    { label: "编号", prop: "id", width: 100, sortable: "custom" },
    { label: "执行状态", prop: "status", slot: "status", width: 130 },
    {
      label: "执行开始时间",
      prop: "startTime",
      date: "{y}-{m}-{d} {h}:{i}:{s}",
      width: 190,
      sortable: "custom",
    },
    {
      label: "执行结束时间",
      prop: "endTime",
      date: "{y}-{m}-{d} {h}:{i}:{s}",
      width: 190,
      sortable: "custom",
    },
    { label: "操作", slot: "handle", fixed: "right", width: 240 },
  ],
  func: listTaskLog,
  params: {
    taskId: undefined,
    taskType: 1,
  },
});

const detailParams = reactive({
  pageNum: 1,
  pageSize: 10,
  logId: undefined,
  orderByColumn: "createTime",
  isAsc: "descending",
});

/** 打开执行日志弹框，并按当前任务重新加载日志。 */
function show(taskId, taskType = 1) {
  logTableStore.params.taskId = taskId;
  logTableStore.params.taskType = taskType;
  logVisible.value = true;
  nextTick(() => logTableRef.value?.resetQuery());
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

function showLogDetail(row) {
  currentLogId.value = row.id;
  detailParams.pageNum = 1;
  detailParams.orderByColumn = "createTime";
  detailParams.isAsc = "descending";
  getLogDetails();
}

function showError(row) {
  errorMessage.value = row.errorMsg;
  errorVisible.value = true;
}

function handleDetailSortChange({ prop, order }) {
  detailParams.orderByColumn = order ? prop : "createTime";
  detailParams.isAsc = order || "descending";
  getLogDetails();
}

defineExpose({ show });
</script>

<style scoped>
.wrap-line {
  white-space: pre-wrap;
}
</style>
