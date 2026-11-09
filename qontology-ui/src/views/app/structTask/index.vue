<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

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
    <GuideTip tip-id="kg/ext/structTask.list" />

    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="routeTo(`/app/structTask/add`)"
          v-hasPermi="['app:appStructTask:struct:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete()"
          v-hasPermi="['app:appStructTask:struct:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #taskIcon>
          <img :src="structuredTaskIcon" alt="" class="info-icon" />
        </template>
        <!-- 负责人沿用姓名、灰色电话的双行样式，联系电话复用公共用户关联方法。 -->
        <template #responsibleUser="{ row }">
          <div class="flex-column overflow-hidden">
            <span
              class="text-ellipsis"
              :title="row.responsiblePersonName || ''"
              >{{ row.responsiblePersonName || "-" }}</span
            >
            <span
              v-if="creatorPhones[row.responsibleUserId]"
              class="desc-text"
              >{{ creatorPhones[row.responsibleUserId] }}</span
            >
          </div>
        </template>
        <!--     运行控制   -->
        <template #runControl="{ row }">
          <div class="flex-column run-control">
            <div class="flex-center mb2">
              <span class="black-label mr5">发布状态:</span>
              <el-switch
                v-model="row.publishStatus"
                :active-value="1"
                :inactive-value="0"
                @change="(value) => handleEnableChange(row, value)"
              />
            </div>
            <div class="flex-center">
              <span class="black-label mr5">调度状态:</span>
              <el-switch
                v-model="row.schedulingStatus"
                :active-value="0"
                :inactive-value="1"
                @change="(value) => handleStatusChange(row, value)"
              />
            </div>
          </div>
        </template>
        <!--最近执行信息-->
        <template #lastExecute="{ row }">
          <div class="flex-column">
            <dict-tag
              class="execute-tag"
              :options="app_task_status"
              :value="row.status"
              @click="showLogs(row)"
            />
            <div class="last-execute-info">
              <div class="last-execute-info__row struct-task-execution">
                <span class="last-execute-info__value">{{
                  row.lastExecuteTime
                    ? proxy.parseTime(
                        row.lastExecuteTime,
                        "{y}-{m}-{d} {h}:{i}"
                      )
                    : "-"
                }}</span>
                <span class="last-execute-info__value"
                  >耗时：{{ formatDuration(row.executeDuration) }}</span
                >
              </div>
            </div>
          </div>
        </template>

        <template #updateStrategy="{ row }">
          <div class="flex-column fz14">
            <div class="flex-center">
              <dict-tag :options="app_update_type" :value="row.updateType" />
            </div>
            <div class="flex-center mt7">
              <el-icon class="mr5 struct-task__clock"><Clock /></el-icon>
              <span
                class="text-ellipsis cron-text"
                :title="cronToZh(row.updateFrequency)"
                >{{ cronToZh(row.updateFrequency) || "-" }}</span
              >
            </div>
          </div>
        </template>

        <template #action="{ row }">
          <el-button
            link
            type="primary"
            icon="View"
            :disabled="row.status != 2"
            @click="handleResult(row)"
            >抽取结果</el-button
          >
          <el-button link type="primary" icon="View" @click="handleDetail(row)"
            >详情</el-button
          >
          <el-popover
            placement="bottom"
            :width="150"
            popper-class="struct-task-action-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                link
                type="primary"
                icon="VideoPlay"
                :disabled="row.publishStatus == 1 || isRunning(row)"
                @click="handleRun(row)"
                >执行一次</el-button
              >
              <el-button
                link
                type="primary"
                icon="Edit"
                :disabled="row.publishStatus == 1 || isRunning(row)"
                @click="handleUpdate(row)"
                v-hasPermi="['app:appStructTask:struct:edit']"
                >修改任务</el-button
              >
              <el-button
                link
                type="danger"
                icon="Delete"
                :disabled="row.publishStatus == 1 || isRunning(row)"
                @click="handleDelete(row)"
                v-hasPermi="['app:appStructTask:struct:remove']"
                >删除任务</el-button
              >
            </div>
          </el-popover>
        </template>

        <template #publishBy="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.publishBy || ''">{{
              row.publishBy || "-"
            }}</span>
            <span v-if="creatorPhones[row.publisherId]" class="desc-text">{{
              creatorPhones[row.publisherId]
            }}</span>
          </div>
        </template>
      </qt-table>
    </qt-wrap>

    <TaskLogDialog ref="taskLogDialogRef" :append-to="appContainer" />
  </div>
</template>

<script setup name="ExtStruct">
import {
  listExtStruct,
  delExtStruct,
  runStructTask,
  strutCancelReleaseByTaskId,
  strutReleaseByTaskId,
} from "@/api/app/structTask";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { cronToZh } from "@/utils/cronUtils";
import { Clock } from "@element-plus/icons-vue";
import TaskLogDialog from "@/views/app/taskLog/taskLogDialog.vue";
import structuredTaskIcon from "@/assets/images/structured-extraction.svg";

const { proxy } = getCurrentInstance();
const { creatorPhones } = useCreatorPhones();
const { app_task_status, publish_status, app_update_type } = proxy.useDict(
  "app_task_status",
  "publish_status",
  "app_update_type"
);

const tableRef = ref();
const appContainer = ref();
const taskLogDialogRef = ref();
const selectedRows = ref([]);
const multiple = ref(true);
const router = useRouter();

/** 创建时间区间转换为后端约定的 params.beginCreateTime / endCreateTime */
function formatParams(params) {
  const { createTime, ...query } = params;
  const [begin, end] = Array.isArray(createTime) ? createTime : [];
  if (!begin || !end) return query;
  const endDate = new Date(end);
  endDate.setDate(endDate.getDate() + 1);
  return {
    ...query,
    params: {
      beginCreateTime: begin,
      endCreateTime: proxy.parseTime(endDate, "{y}-{m}-{d}"),
    },
  };
}

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
    {
      type: "selection",
      width: 55,
      minWidth: 55,
    },
    {
      label: "编号",
      prop: "id",
      width: 60,
      sortable: "custom",
      sortableKey: "id",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "任务信息",
      minWidth: 250,
      align: "left",
      info: {
        iconSlot: "taskIcon",
        title: "name",
        desc: "description",
        click: (row) => handleDetail(row),
      },
    },
    {
      label: "最近执行信息",
      width: 170,
      slot: "lastExecute",
      align: "left",
    },
    {
      label: "更新策略",
      width: 140,
      slot: "updateStrategy",
      align: "left",
    },
    { label: "运行控制", slot: "runControl", width: 140 },
    {
      label: "负责人",
      slot: "responsibleUser",
      width: 120,
      align: "left",
      headerAlign: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "发布人",
      width: 120,
      align: "left",
      slot: "publishBy",
    },
    {
      label: "发布时间",
      prop: "publishTime",
      date: true,
      width: 150,
      sortable: "custom",
      sortableKey: "publish_time",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 150,
      sortable: "custom",
      sortableKey: "create_time",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "操作",
      slot: "action",
      width: 280,
      fixed: "right",
    },
  ],
  func: listExtStruct,
  params: {
    name: null,
    status: null,
    publishStatus: null,
    createTime: null,
  },
  events: {
    formatParams,
    formatData,
    onPageCurrentChange: rememberPage,
    onPageSizeChange: rememberPage,
  },
});

// 记录当前分页，供抽取结果页返回时还原列表位置
const currentPage = reactive({ pageNum: 1, pageSize: 10 });

function rememberPage(params) {
  Object.assign(currentPage, {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
  });
}

const searchStore = reactive({
  items: [
    { label: "任务名称", prop: "name", component: { is: "input" } },
    {
      label: "执行状态",
      prop: "status",
      component: { is: "select", options: app_task_status },
    },
    {
      label: "发布状态",
      prop: "publishStatus",
      component: { is: "select", options: publish_status },
    },
    {
      label: "创建时间",
      prop: "createTime",
      component: {
        is: "date-picker",
        type: "daterange",
        rangeSeparator: "-",
        startPlaceholder: "开始日期",
        endPlaceholder: "结束日期",
        clearable: false,
      },
    },
  ],
});

/** 将执行耗时转换为秒或分秒展示，缺少数据时使用占位符。 */
function formatDuration(value) {
  if (value === null || value === undefined || value === "") return "-";
  const seconds = Math.max(Math.round(Number(value)), 0);
  if (seconds < 60) return `${seconds}秒`;
  return `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

/** 发布状态以后端 publishStatus 为准并归一为数字，供 el-switch 按数字回显；调度沿用 0 开启、1 关闭的接口约定。 */
function formatData(rows) {
  return rows.map((row) => ({
    ...row,
    publishStatus: Number(row.publishStatus ?? 0),
    schedulingStatus: Number(row.schedulingStatus ?? 1),
  }));
}

// 多选框选中数据
function handleSelectionChange(selection) {
  selectedRows.value = selection;
  multiple.value = !selection.length;
}

/** 任务关闭时拦截调度操作，并恢复开关变更前的状态。 */
function handleStatusChange(row, value) {
  if (row.publishStatus !== 1) {
    row.schedulingStatus = value === 0 ? 1 : 0;
    proxy.$modal.msgWarning("请先开启任务状态");
  }
}

/** 调度上线期间禁止关闭任务，需先下线调度后再关闭。 */
function handleEnableChange(row, value) {
  if (value === 0) {
    proxy.$modal.confirm("是否确认取消发布？").then(() => {
      const request = strutCancelReleaseByTaskId({ taskId: row.id });
      request
        .then((response) => {
          if (response && response.code === 200) {
            proxy.$modal.msgSuccess("取消发布成功");
            tableRef.value.getList();
          }
        })
        .catch(() => {
          row.publishStatus = !value;
          proxy.$modal.msgError("取消发布失败");
        });
    });
  } else {
    proxy.$modal.confirm("是否确认发布？").then(() => {
      const request = strutReleaseByTaskId({ taskId: row.id });
      request
        .then((response) => {
          if (response && response.code === 200) {
            proxy.$modal.msgSuccess("发布成功");
            tableRef.value.getList();
          }
        })
        .catch(() => {
          row.publishStatus = !value;
          proxy.$modal.msgError("发布失败");
        });
    });
  }
}

/** 点击执行状态标签查看任务日志，结构化任务 taskType 为 0 */
function showLogs(row) {
  taskLogDialogRef.value.show(row.id, 0);
}

/** 判断任务是否执行中或队列中（不可执行/修改/删除） */
function isRunning(row) {
  return [1, 4].includes(Number(row.status));
}

/* 立即执行一次 */
function handleRun(row) {
  proxy.$modal
    .confirm('确认要立即执行一次"' + row.name + '"任务吗?')
    .then(function () {
      return runStructTask(row.id, row.updateType);
    })
    .then(() => {
      proxy.$modal.msgWarning("任务执行中");
      tableRef.value.getList();
    })
    .catch(() => {});
}

/** 修改按钮操作 */
function handleUpdate(row) {
  if (row.status == 1) {
    proxy.$modal.msgWarning("任务执行中");
    return;
  }
  routeTo("/app/structTask/add", row);
}

function handleDetail(row) {
  routeTo("/app/structTask/detail", row);
}

/** 删除按钮操作 */
function canDeleteTask(row) {
  return Number(row.publishStatus) !== 1 && !isRunning(row);
}

function handleDelete(row) {
  if (row) {
    if (Number(row.publishStatus) === 1) {
      proxy.$modal.msgWarning("已发布的任务不可删除");
      return;
    }
    if (isRunning(row)) {
      proxy.$modal.msgWarning(
        row.status == 4 ? "任务已在队列中" : "任务执行中"
      );
      return;
    }

    proxy.$modal
      .confirm(`是否确认删除任务编号为“${row.id}”的数据项？`)
      .then(function () {
        return delExtStruct(row.id);
      })
      .then(() => {
        tableRef.value.getList();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {});
    return;
  }

  const canDeleteRows = selectedRows.value.filter(canDeleteTask);
  const canDeleteIds = canDeleteRows.map((item) => item.id);
  const canDeleteCount = canDeleteRows.length;
  const cannotDeleteCount = selectedRows.value.length - canDeleteCount;

  if (!canDeleteCount) {
    proxy.$modal.msgWarning("已发布或执行中的任务不可删除");
    return;
  }

  const confirmText = cannotDeleteCount
    ? `可删除${canDeleteCount}个，不可删除${cannotDeleteCount}个，是否删除可删部分？`
    : `是否确认删除选中的${canDeleteCount}个任务？`;

  proxy.$modal
    .confirm(confirmText)
    .then(function () {
      return delExtStruct(canDeleteIds);
    })
    .then(() => {
      tableRef.value.getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

// 抽取结果
function handleResult(row) {
  router.push({
    path: `/app/extractResults`,
    query: {
      id: row.id,
      name: row.name,
      pageType: "1",
      pageNum: currentPage.pageNum,
      pageSize: currentPage.pageSize,
    },
  });
}

function routeTo(link, row) {
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return;
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: row ? { taskId: row.id } : {},
      });
    }
  }
}
</script>

<style scoped lang="scss">
.struct-task__clock {
  color: var(--el-color-primary);
}

// 由可交互的外层承接禁用开关的悬浮提示，同时保持行内对齐。
.schedule-switch-wrapper {
  display: inline-flex;
  align-items: center;
}

.run-control {
  align-items: center;
}

.execute-tag {
  align-self: flex-start;
  cursor: pointer;
}

.struct-task-execution {
  flex-direction: column;
  align-items: flex-start;
  margin-top: var(--qt-space-1);
}

:global(.struct-task-action-popover .el-popover__content) {
  text-align: center;
}

:global(.struct-task-action-popover .task-action-list) {
  display: flex;
  flex-direction: column;
  align-items: center;
}

:global(.struct-task-action-popover .el-button) {
  width: 100%;
  margin-left: 0;
  justify-content: center;
}
</style>
