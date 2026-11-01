<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qKnow Intelligent Agent Building Platform (Open Source Edition).

 qKnow is licensed under Apache License 2.0 with additional qKnow terms.
 You may use qKnow for commercial purposes, but you may not remove, hide,
 modify, or replace the qKnow logo, copyright notices, license notices,
 or attribution information without a separate commercial license.

 White-label use, OEM distribution, rebranding, or presenting qKnow as
 another product requires separate commercial authorization from
 Jiangsu Qiantong Technology Co., Ltd.

 Business License: https://community.qknow.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->

<template>
  <div class="app-container" ref="app-container">
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
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #runControl="{ row }">
          <div class="flex-column flex-column-gap run-control">
            <div class="flex-center">
              <el-switch
                v-model="row.enableStatus"
                :active-value="1"
                :inactive-value="0"
                @change="(value) => handleEnableChange(row, value)"
              />
            </div>
            <div class="flex-center">
              <el-switch
                v-model="row.updateStatus"
                :active-value="0"
                :inactive-value="1"
                @change="handleStatusChange(row)"
              />
            </div>
          </div>
        </template>

        <template #action="{ row }">
          <el-button
            link
            type="primary"
            icon="VideoPlay"
            :disabled="row.publishStatus == 1 || isRunning(row)"
            @click="extraction(row)"
            v-hasPermi="['app:appStructTask:struct:edit']"
            >执行</el-button
          >
          <el-button
            link
            type="primary"
            icon="View"
            :disabled="row.status != 2"
            @click="handleResult(row)"
            >抽取结果</el-button
          >
          <el-popover
            placement="bottom"
            :width="150"
            trigger="click"
            popper-class="handle-popover table-action-popover"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown" @click.stop
                >更多</el-button
              >
            </template>
            <div class="task-action-list">
              <el-button
                link
                type="primary"
                icon="VideoPlay"
                :disabled="
                  row.publishStatus == 1 || row.status != 2 || isRunning(row)
                "
                @click="handleRun(row)"
                >执行一次</el-button
              >
              <el-button
                class="btn-icon-offset"
                link
                type="primary"
                icon="Edit"
                :disabled="row.publishStatus == 1 || isRunning(row)"
                @click="handleUpdate(row)"
                v-hasPermi="['app:appStructTask:struct:edit']"
                >修改</el-button
              >
              <el-button
                class="btn-icon-offset"
                link
                type="danger"
                icon="Delete"
                :disabled="row.publishStatus == 1 || isRunning(row)"
                @click="handleDelete(row)"
                v-hasPermi="['app:appStructTask:struct:remove']"
                >删除</el-button
              >
            </div>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>
  </div>
</template>

<script setup name="ExtStruct">
import {
  listExtStruct,
  delExtStruct,
  extExecuteExtraction,
  runStructTask,
} from "@/api/app/structTask";
import { cronToZh } from "@/utils/cronUtils";

const { proxy } = getCurrentInstance();
const { app_task_status, publish_status } = proxy.useDict(
  "app_task_status",
  "publish_status"
);

const tableRef = ref();
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
      defaultSort: { prop: "create_time", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55 },
    {
      label: "编号",
      prop: "id",
      width: 80,
      sortable: "custom",
      sortableKey: "id",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "任务信息",
      minWidth: 260,
      align: "left",
      info: {
        title: "name",
        desc: "description",
        click: (row) => handleUpdate(row),
      },
    },
    {
      label: "最近执行信息",
      minWidth: 180,
      align: "left",
      list: [
        { dict: "app_task_status", prop: "status" },
        { prop: (row) => formatExecuteTime(row.lastExecuteTime) },
        { prop: (row) => formatDuration(row.executeDuration) },
      ],
    },
    {
      label: "更新策略",
      width: 180,
      align: "left",
      list: [
        { dict: "app_update_type", prop: "updateType" },
        { prop: (row) => cronToZh(row.updateFrequency) },
      ],
    },
    { label: "运行控制", slot: "runControl", width: 120 },
    {
      label: "发布人",
      width: 120,
      align: "left",
      list: [{ prop: "publishBy" }, { prop: "publishPhone" }],
    },
    {
      label: "负责人",
      width: 120,
      align: "left",
      list: [{ prop: "createBy" }, { prop: "createPhone" }],
    },
    {
      label: "发布状态",
      prop: "publishStatus",
      dict: "publish_status",
      width: 100,
    },
    {
      label: "发布时间",
      prop: "publishTime",
      date: true,
      width: 160,
      sortable: "custom",
      sortableKey: "publish_time",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      sortable: "custom",
      sortableKey: "create_time",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "操作",
      slot: "action",
      width: 240,
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

/** 最近执行时间、执行耗时、发布人/负责人电话、任务启用状态后端暂未返回，先按约定字段名占位 */
function formatExecuteTime(value) {
  return value ? proxy.parseTime(value, "{y}-{m}-{d} {h}:{i}") : "-";
}

function formatDuration(value) {
  if (value === null || value === undefined || value === "") return "-";
  const seconds = Math.max(Math.round(Number(value)), 0);
  if (seconds < 60) return `${seconds}秒`;
  return `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

/** 后端暂未返回 enableStatus，列表数据缺省按启用展示 */
function formatData(rows) {
  return rows.map((row) => ({
    ...row,
    enableStatus: Number(row.enableStatus ?? 1),
  }));
}

// 多选框选中数据
function handleSelectionChange(selection) {
  selectedRows.value = selection;
  multiple.value = !selection.length;
}

// 调度状态开关
function handleStatusChange(row) {
  const original = row.updateStatus === 0 ? 1 : 0;
  proxy.$modal.msgWarning("当前可用资源不足，暂不支持操作");
  row.updateStatus = original;
}

// 任务状态（启用/停用）开关，后端启用接口待接入
function handleEnableChange(row, value) {
  proxy.$modal.msgWarning("当前可用资源不足，暂不支持操作");
  row.enableStatus = value === 1 ? 0 : 1;
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

/** 执行抽取 */
function extraction(row) {
  if (row.status == 1) {
    proxy.$modal.msgWarning("任务执行中");
    return;
  }
  proxy.$modal
    .confirm('是否确认执行任务名称为"' + row.name + '"的数据项？')
    .then(function () {})
    .then(() => {
      extExecuteExtraction({ id: row.id }).then((res) => {
        if (res && res.code == 200) {
          proxy.$modal.msgSuccess("操作成功,执行中");
          tableRef.value.getList();
        }
      });
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

/** 删除按钮操作 */
function canDeleteTask(row) {
  return !isRunning(row);
}

function handleDelete(row) {
  if (row) {
    if (!canDeleteTask(row)) {
      proxy.$modal.msgWarning(
        row.status == 4 ? "任务已在队列中" : "任务执行中"
      );
      return;
    }

    proxy.$modal
      .confirm('是否确认删除任务名称为"' + row.name + '"的数据项？')
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
    proxy.$modal.msgWarning("选中的任务为进行中或队列中，无法删除");
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
.run-control {
  align-items: center;
}
</style>
