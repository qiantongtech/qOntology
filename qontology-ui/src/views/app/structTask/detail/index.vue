<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
-->
<template>
  <div ref="appContainer" class="app-container" v-loading="loading">
    <detail-info
      :data="task"
      :header="{
        className: 'clearfixs',
        idKey: 'id',
        nameKey: 'name',
        statusKey: 'publishStatus',
        statusOptions: publish_status,
        routerBack: false,
      }"
      :items="detailItems"
      mode="free"
      time-format="{y}-{m}-{d} {h}:{i}:{s}"
      @back="goBack"
    >
      <template #updateType
        ><dict-tag :options="app_update_type" :value="task.updateType"
      /></template>
    </detail-info>

    <div class="pagecont-bottom">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="执行日志" name="logs">
          <qt-wrap
            v-if="taskId"
            :key="taskId"
            :columns="logTableStore.columns"
            :table-ref="logTableRef"
            :config="{
              fullContent: false,
              actions: { table: { search: false } },
            }"
          >
            <template #actions-data>
              <guide-tip-panel type="plain" title="仅展示当前任务的执行记录" />
            </template>
            <qt-table v-bind="logTableStore" ref="logTableRef">
              <template #status="{ row }">
                <dict-tag :options="app_log_status" :value="row.status" />
              </template>
              <template #handle="{ row }">
                <el-button
                  v-if="row.errorMsg"
                  link
                  type="primary"
                  icon="View"
                  @click="showError(row)"
                  >错误信息</el-button
                >
                <el-button
                  link
                  type="primary"
                  icon="View"
                  @click="showSteps(row)"
                  >执行步骤</el-button
                >
              </template>
            </qt-table>
          </qt-wrap>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog
      v-model="stepsVisible"
      title="执行步骤"
      width="800px"
      :append-to="appContainer"
      draggable
    >
      <qt-table v-bind="stepTableStore" ref="stepTableRef" />
      <template #footer
        ><el-button @click="stepsVisible = false">关闭</el-button></template
      >
    </el-dialog>
    <el-dialog
      v-model="errorVisible"
      title="错误信息"
      width="800px"
      :append-to="appContainer"
    >
      <div class="error-message">{{ errorMessage }}</div>
      <template #footer
        ><el-button @click="errorVisible = false">关闭</el-button></template
      >
    </el-dialog>
  </div>
</template>

<script setup name="AppStructTaskDetail">
import { getExtStruct } from "@/api/app/structTask";
import { getDetailPage, listTaskLog } from "@/api/app/taskLog";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { cronToZh } from "@/utils/cronUtils";

const { proxy } = getCurrentInstance();
const route = useRoute();
const taskId = computed(() => {
  const value = route.query.taskId;
  return Array.isArray(value) ? value[0] : value;
});
const { creatorPhones } = useCreatorPhones();
const { app_update_type, publish_status, app_log_status } = proxy.useDict(
  "app_update_type",
  "publish_status",
  "app_log_status"
);

const appContainer = ref();
const logTableRef = ref();
const stepTableRef = ref();
const task = ref({});
const loading = ref(false);
const activeTab = ref("logs");
const stepsVisible = ref(false);
const errorVisible = ref(false);
const errorMessage = ref("");
const detailItems = [
  { label: "项目版本", key: "projectVersionName" },
  { label: "数据源", key: "datasourceName" },
  { label: "更新类型", slot: "updateType" },
  { label: "描述", key: "description", span: 24, className: "mt2 mb2" },
  {
    label: "负责人",
    key: "responsiblePersonName",
    formatter: (name, record) => {
      const phone = creatorPhones.value[record.responsibleUserId];
      return phone ? `${name || "-"} / ${phone}` : name || "-";
    },
  },
  {
    label: "更新频率",
    formatter: (_, record) => cronToZh(record.updateFrequency) || "-",
  },
  { label: "最近执行时间", key: "lastExecuteTime", type: "time" },
  {
    label: "执行耗时",
    className: "mt2 mb2",
    formatter: (_, record) => formatDuration(record.executeDuration),
  },
  { label: "发布人", key: "publishBy", className: "mt2 mb2" },
  { label: "发布时间", key: "publishTime", type: "time", className: "mt2 mb2" },
  // {
  //   label: "创建人",
  //   key: "createBy",
  //   formatter: (name, record) => {
  //     const phone = creatorPhones.value[record.creatorId];
  //     return phone ? `${name || "-"} / ${phone}` : name || "-";
  //   },
  // },
  // { label: "创建时间", key: "createTime", type: "time" },
  // { label: "更新时间", key: "updateTime", type: "time" },
];

const logTableStore = reactive({
  config: {
    initResquest: false,
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
    },
  },
  columns: [
    { label: "编号", prop: "id", width: 80, sortable: "custom" },
    { label: "执行状态", prop: "status", slot: "status", width: 130 },
    {
      label: "开始时间",
      prop: "startTime",
      date: "{y}-{m}-{d} {h}:{i}:{s}",
      width: 190,
      sortable: "custom",
    },
    {
      label: "结束时间",
      prop: "endTime",
      date: "{y}-{m}-{d} {h}:{i}:{s}",
      width: 190,
      sortable: "custom",
    },
    { label: "操作", slot: "handle", fixed: "right", width: 210 },
  ],
  func: listTaskLog,
  params: { taskId: undefined, taskType: 0 },
});

const stepTableStore = reactive({
  config: {
    initResquest: false,
    sort: true,
    table: { stripe: true, rowKey: "id" },
  },
  columns: [
    { label: "编号", prop: "id", width: 100, sortable: "custom" },
    {
      label: "执行步骤",
      prop: "step",
      minWidth: 380,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "执行时间",
      prop: "createTime",
      date: "{y}-{m}-{d} {h}:{i}:{s}",
      width: 190,
      sortable: "custom",
    },
  ],
  func: getDetailPage,
  params: { logId: undefined },
});

function formatDuration(value) {
  if (value === null || value === undefined || value === "") return "-";
  const seconds = Math.max(Math.round(Number(value)), 0);
  return seconds < 60
    ? `${seconds}秒`
    : `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

function goBack() {
  proxy.$tab.closeOpenPage({ path: "/app/graph/structTask" });
}

async function loadDetail() {
  const currentTaskId = taskId.value;
  task.value = {};
  logTableStore.params.taskId = currentTaskId;
  if (!currentTaskId) return;
  loading.value = true;
  try {
    const response = await getExtStruct(currentTaskId);
    if (taskId.value !== currentTaskId) return;
    task.value = response.data?.structTask || response.data || {};
    if (task.value.id) nextTick(() => logTableRef.value?.resetQuery());
  } finally {
    if (taskId.value === currentTaskId) loading.value = false;
  }
}

function showSteps(row) {
  stepTableStore.params.logId = row.id;
  stepsVisible.value = true;
  nextTick(() => stepTableRef.value?.resetQuery());
}

function showError(row) {
  errorMessage.value = row.errorMsg || "-";
  errorVisible.value = true;
}

watch(taskId, loadDetail, { immediate: true });
</script>

<style scoped lang="scss">
.error-message {
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
