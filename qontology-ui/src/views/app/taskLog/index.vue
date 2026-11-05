<!--
  Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

  This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

  qOntology is licensed under Apache License 2.0 with additional qOntology terms.
  See the LICENSE file in the project root for full license information.
-->

<template>
  <div ref="appContainer" class="app-container">
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <template #actions-data>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          @mousedown="(event) => event.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #taskType="{ row }">
          <dict-tag :options="app_task_log_type" :value="row.taskType" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="app_log_status" :value="row.status" />
        </template>
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{
              row.createBy || "-"
            }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{
              creatorPhones[row.creatorId]
            }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="View"
            @click="showDetail(row)"
          >查看步骤</el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <el-dialog
      v-model="detailOpen"
      :append-to="appContainer"
      width="800px"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          抽取步骤
        </span>
      </template>
      <el-table
        v-loading="detailLoading"
        :data="details"
        height="48vh"
        stripe
        @sort-change="handleDetailSortChange"
      >
        <el-table-column label="编号" type="index" width="70" align="center">
          <template #default="scope">
            {{ (detailParams.pageNum - 1) * detailParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column
          label="执行步骤"
          prop="step"
          min-width="430"
          align="left"
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">{{ scope.row.step || "-" }}</template>
        </el-table-column>
        <el-table-column
          label="执行时间"
          prop="createTime"
          width="190"
          align="center"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        >
          <template #default="scope">
            {{ proxy.parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="detailTotal > 0"
        v-model:limit="detailParams.pageSize"
        v-model:page="detailParams.pageNum"
        :total="detailTotal"
        @pagination="getDetails"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AppTaskLog">
import { delTaskLog, getDetailPage, listTaskLog } from "@/api/app/taskLog";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { app_task_log_type, app_log_status } = proxy.useDict(
  "app_task_log_type",
  "app_log_status",
);
const { creatorPhones } = useCreatorPhones();

const appContainer = ref();
const tableRef = ref();
const detailLoading = ref(false);
const detailOpen = ref(false);
const details = ref([]);
const ids = ref([]);
const multiple = ref(true);
const detailTotal = ref(0);
const currentLogId = ref();

const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    { label: "编号", prop: "id", width: 100, minWidth: 100, sortable: "custom" },
    {
      label: "任务名称",
      prop: "taskName",
      minWidth: 260,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "执行类型", prop: "taskType", slot: "taskType", width: 150 },
    { label: "执行状态", prop: "status", slot: "status", width: 120 },
    { label: "开始时间", prop: "startTime", date: true, width: 160, sortable: "custom" },
    { label: "结束时间", prop: "endTime", date: true, width: 160, sortable: "custom" },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 140,
      align: "left",
      headerAlign: "left",
    },
    { label: "创建时间", prop: "createTime", date: true, width: 160, sortable: "custom" },
    { label: "操作", slot: "handle", fixed: "right", width: 130, minWidth: 130 },
  ],
  func: listTaskLog,
  params: {
    taskName: null,
    taskType: null,
    status: null,
  },
});

const searchStore = reactive({
  items: [
    { label: "任务名称", prop: "taskName", component: { is: "input" } },
    {
      label: "执行类型",
      prop: "taskType",
      component: { is: "select", options: app_task_log_type },
    },
    {
      label: "执行状态",
      prop: "status",
      component: { is: "select", options: app_log_status },
    },
  ],
});

const detailParams = reactive({
  pageNum: 1,
  pageSize: 10,
  logId: undefined,
  orderByColumn: "createTime",
  isAsc: "descending",
});

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

async function getDetails() {
  detailLoading.value = true;
  detailParams.logId = currentLogId.value;
  try {
    const { data } = await getDetailPage(detailParams);
    details.value = data.rows || [];
    detailTotal.value = data.total || 0;
    detailOpen.value = true;
  } finally {
    detailLoading.value = false;
  }
}

function showDetail(row) {
  currentLogId.value = row.id;
  detailParams.pageNum = 1;
  getDetails();
}

function handleDetailSortChange({ prop, order }) {
  detailParams.orderByColumn = order ? prop : "createTime";
  detailParams.isAsc = order || "descending";
  getDetails();
}

function handleDelete() {
  proxy.$modal
    .confirm(`是否确认删除抽取日志编号为“${ids.value.join(",")}”的数据项？`)
    .then(() => delTaskLog(ids.value.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      tableRef.value?.getList();
    })
    .catch(() => {});
}
</script>
