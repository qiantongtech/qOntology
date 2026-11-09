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

      <!-- <template #actions-data>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          @mousedown="(event) => event.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template> -->

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
          <el-button link type="primary" icon="View" @click="showDetail(row)"
            >查看步骤</el-button
          >
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
      <qt-table v-bind="detailTableStore" ref="detailTableRef" />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
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
  "app_log_status"
);
const { creatorPhones } = useCreatorPhones();

const appContainer = ref();
const tableRef = ref();
const detailOpen = ref(false);
const detailTableRef = ref();
const ids = ref([]);
const multiple = ref(true);
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
    // { type: "selection", width: 55, minWidth: 55 },
    {
      label: "编号",
      prop: "id",
      width: 60,
      minWidth: 60,
      sortable: "custom",
    },
    {
      label: "任务名称",
      prop: "taskName",
      minWidth: 300,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "执行类型", prop: "taskType", slot: "taskType", width: 150 },
    { label: "执行状态", prop: "status", slot: "status", width: 120 },
    {
      label: "开始时间",
      prop: "startTime",
      date: true,
      width: 160,
      sortable: "custom",
    },
    {
      label: "结束时间",
      prop: "endTime",
      date: true,
      width: 160,
      sortable: "custom",
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 120,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      sortable: "custom",
    },
    {
      label: "操作",
      slot: "handle",
      fixed: "right",
      width: 240,
      minWidth: 240,
    },
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

const detailTableStore = reactive({
  config: {
    initResquest: false,
    sort: true,
    table: {
      stripe: true,
      defaultSort: { prop: "createTime", order: "descending" },
    },
    pagination: { notAutoScroll: true },
  },
  columns: [
    { label: "编号", prop: "id", width: 60, sortable: "custom" },
    {
      label: "执行步骤",
      prop: "step",
      minWidth: 330,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "执行时间",
      prop: "createTime",
      width: 190,
      sortable: "custom",
      date: "{y}-{m}-{d} {h}:{i}",
    },
  ],
  func: getDetailPage,
  params: { logId: undefined },
});

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

function showDetail(row) {
  currentLogId.value = row.id;
  detailTableStore.params.logId = currentLogId.value;
  detailOpen.value = true;
  nextTick(() => detailTableRef.value?.resetQuery());
}

function handleDelete() {
  if (!ids.value.length) {
    proxy.$modal.msgWarning("请选择要删除的日志");
    return;
  }
  proxy.$modal
    .confirm(`是否确认删除选中的 ${ids.value.length} 条抽取日志？`)
    .then(() => delTaskLog(ids.value.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      tableRef.value?.getList();
    })
    .catch(() => {});
}
</script>
