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
    <GuideTip tip-id="kg/ext/unstructTask.list" />

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
          v-hasPermi="['app:appUnstructTask:unstructtask:add']"
          plain
          type="primary"
          @click="handleAdd"
          @mousedown="(event) => event.preventDefault()"
          ><i class="iconfont-mini icon-xinzeng mr5" />新增</el-button
        ><el-button
          :disabled="multiple"
          plain
          type="danger"
          @click="handleDelete()"
          @mousedown="(event) => event.preventDefault()"
          ><i class="iconfont-mini icon-shanchu-huise mr5" />删除</el-button
        >
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #taskStatus="{ row }">
          <div class="flex-center task-status">
            <el-switch
              v-model="row.validFlag"
              @change="(value) => handleValidFlagChange(row, value)"
            />
          </div>
        </template>

        <template #action="{ row }">
          <el-button
            :disabled="row.publishStatus === 1 || isRunning(row)"
            icon="VideoPlay"
            link
            type="primary"
            @click="handleExecute(row)"
            >执行</el-button
          ><el-button
            :disabled="row.status !== 2"
            icon="View"
            link
            type="primary"
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
              <el-button icon="ArrowDown" link type="primary" @click.stop
                >更多</el-button
              >
            </template>
            <div class="task-action-list">
              <el-button icon="List" link type="primary" @click="showLogs(row)"
                >执行日志</el-button
              ><el-button
                class="btn-icon-offset"
                :disabled="row.publishStatus === 1 || isRunning(row)"
                icon="Edit"
                link
                type="primary"
                @click="handleEdit(row)"
                >修改</el-button
              ><el-button
                class="btn-icon-offset"
                :disabled="row.publishStatus === 1 || isRunning(row)"
                icon="Delete"
                link
                type="danger"
                @click="handleDelete(row)"
                >删除</el-button
              >
            </div>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <el-dialog
      v-model="open"
      :append-to="appContainer"
      draggable
      width="800px"
      @closed="resetTaskForm"
    >
      <template #header
        ><span class="el-dialog__title">{{ title }}</span></template
      >
      <el-form
        ref="taskRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20"
          ><el-col :span="24"
            ><el-form-item label="任务名称" prop="name"
              ><el-input
                v-model="form.name"
                maxlength="256"
                placeholder="请输入任务名称" /></el-form-item></el-col
        ></el-row>
        <el-row :gutter="20"
          ><el-col :span="24"
            ><el-form-item label="导入文件" prop="sourceFiles"
              ><el-button
                :disabled="form.status && form.status !== 0"
                plain
                type="primary"
                @click="selectFiles"
                ><i
                  class="iconfont icon-upload-cloud-line mr5"
                />导入文件</el-button
              ></el-form-item
            >
            <div style="margin: 0 0 10px 80px">
              <el-table :data="form.sourceFiles" height="300px" stripe
                ><el-table-column
                  label="文件名称"
                  prop="fileName"
                  width="450"
                  :show-overflow-tooltip="{ effect: 'light' }"
                  ><template #default="scope">{{
                    scope.row.fileName || "-"
                  }}</template></el-table-column
                ><el-table-column
                  align="center"
                  label="操作"
                  class-name="small-padding fixed-width"
                  ><template #default="scope"
                    ><el-button
                      :disabled="form.status && form.status !== 0"
                      icon="Delete"
                      link
                      type="danger"
                      @click="removeSourceFile(scope.$index)"
                      >删除</el-button
                    ></template
                  ></el-table-column
                ><template #empty
                  ><div class="emptyBg">
                    <img
                      src="@/assets/system/images/no_data/noData.png"
                      alt=""
                      style="width: 100px; height: 100px"
                    />
                    <p>暂无记录</p>
                  </div></template
                ></el-table
              >
            </div></el-col
          ></el-row
        >
        <el-row :gutter="20"
          ><el-col :span="24"
            ><el-form-item label="本体文件" prop="ttlFilePath"
              ><FileUpload
                v-model="form.ttlFilePath"
                v-model:file-name="form.ttlFileName"
                :file-size="20"
                :file-type="['ttl']"
                :limit="1" /></el-form-item></el-col
        ></el-row>
        <el-row :gutter="20"
          ><el-col :span="24"
            ><el-form-item label="描述" prop="remark"
              ><el-input
                v-model="form.remark"
                :maxlength="512"
                :rows="3"
                show-word-limit
                type="textarea"
                placeholder="请输入描述" /></el-form-item></el-col
        ></el-row>
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="open = false">取 消</el-button
          ><el-button
            :loading="submitting"
            type="primary"
            size="mini"
            @click="submitForm"
            >确 定</el-button
          >
        </div></template
      >
    </el-dialog>

    <FileMultiple
      ref="fileMultipleRef"
      :append-to="appContainer"
      @confirm="confirmFiles"
    />
    <TaskLogDialog ref="taskLogDialogRef" :append-to="appContainer" />
  </div>
</template>

<script setup name="AppUnstructTask">
import {
  addUnstructTask,
  delUnstructTask,
  executeUnstructTask,
  getUnstructTask,
  listUnstructTask,
  updateUnstructTask,
} from "@/api/app/unstructTask";
import FileMultiple from "./fileMultiple.vue";
import TaskLogDialog from "@/views/app/taskLog/taskLogDialog.vue";

const { proxy } = getCurrentInstance();
const router = useRouter();
const { app_task_status } = proxy.useDict("app_task_status");
const appContainer = ref();
const tableRef = ref();
const taskRef = ref();
const fileMultipleRef = ref();
const taskLogDialogRef = ref();
const submitting = ref(false);
const open = ref(false);
const title = ref("");
const selectedRows = ref([]);
const multiple = ref(true);
const form = ref({});

/** 创建时间区间转换为后端约定的 params.beginCreateTime / endCreateTime。 */
function formatParams(params) {
  const { createTime, ...query } = params;
  const [begin, end] = Array.isArray(createTime) ? createTime : [];
  if (!begin || !end) return query;
  return {
    ...query,
    params: { beginCreateTime: begin, endCreateTime: `${end} 23:59:59` },
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
        desc: "remark",
        click: (row) => handleEdit(row),
      },
    },
    {
      label: "执行情况",
      minWidth: 180,
      align: "left",
      list: [
        { dict: "app_task_status", prop: "status" },
        { prop: (row) => formatExecuteTime(row.lastExecuteTime) },
        { prop: (row) => formatDuration(row.executeDuration) },
      ],
    },
    // {
    //   label: "抽取成果",
    //   minWidth: 150,
    //   align: "left",
    //   list: [
    //     { label: "实体数", prop: (row) => formatCount(row.entityCount) },
    //     { label: "关系数", prop: (row) => formatCount(row.relationCount) },
    //     { label: "处理文件数", prop: (row) => formatCount(row.docCount) },
    //   ],
    // },
    { label: "任务状态", slot: "taskStatus", width: 100 },
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
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      sortable: "custom",
      sortableKey: "create_time",
      sortOrders: ["descending", "ascending"],
    },
    { label: "操作", slot: "action", width: 240, fixed: "right" },
  ],
  func: listUnstructTask,
  params: {
    name: null,
    status: null,
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
      label: "任务状态",
      prop: "status",
      component: { is: "select", options: app_task_status },
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

/** 最近执行时间、执行耗时、抽取成果、发布人/负责人电话后端暂未返回，先按约定字段名占位。 */
function formatExecuteTime(value) {
  return value ? proxy.parseTime(value, "{y}-{m}-{d} {h}:{i}") : "-";
}

function formatDuration(value) {
  if (value === null || value === undefined || value === "") return "-";
  const seconds = Math.max(Math.round(Number(value)), 0);
  if (seconds < 60) return `${seconds}秒`;
  return `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

/** 计数为 0 时 QtTable 会把假值渲染成 "-"，这里统一转成字符串。 */
function formatCount(value) {
  return value === null || value === undefined || value === ""
    ? "-"
    : `${value}`;
}

/** 任务状态开关：接口未返回 validFlag 时按启用展示。 */
function formatData(rows) {
  return rows.map((row) => ({ ...row, validFlag: row.validFlag ?? true }));
}

/** 任务状态（启用/停用）开关，后端启用/停用接口待提供。 */
function handleValidFlagChange(row, value) {
  proxy.$modal.msgWarning("后端暂未提供任务启用/停用接口，操作未生效");
  row.validFlag = !value;
}

/** 记录列表多选结果。 */
function handleSelectionChange(rows) {
  selectedRows.value = rows;
  multiple.value = !rows.length;
}

/** 重置任务表单。 */
function resetTaskForm() {
  form.value = {
    id: null,
    name: "",
    ttlFileName: "",
    ttlFilePath: "",
    remark: "",
    status: 0,
    sourceFiles: [],
  };
  proxy.resetForm("taskRef");
}

/** 打开新增任务弹框。 */
function handleAdd() {
  resetTaskForm();
  title.value = "新增非结构化抽取任务";
  open.value = true;
}

/** 打开修改任务弹框。 */
function handleEdit(row) {
  if (isRunning(row)) {
    proxy.$modal.msgWarning(
      Number(row.status) === 4 ? "任务已在队列中" : "任务执行中"
    );
    return;
  }
  resetTaskForm();
  getUnstructTask(row.id).then(({ data }) => {
    form.value = {
      id: data.id,
      name: data.name,
      ttlFileName: data.ttlFileName,
      ttlFilePath: data.ttlFilePath,
      remark: data.remark,
      status: data.status,
      sourceFiles: (data.relPageReqVOS || []).map((item) => ({
        id: item.docId,
        fileName: item.docName,
      })),
    };
    title.value = "修改非结构化抽取任务";
    open.value = true;
  });
}

/** 打开文件选择弹框。 */
function selectFiles() {
  fileMultipleRef.value.open(form.value.sourceFiles);
}

/** 接收文件选择结果。 */
function confirmFiles(rows) {
  form.value.sourceFiles = rows.map((item) => ({
    id: item.id,
    fileName: item.fileName,
  }));
  taskRef.value?.validateField("sourceFiles");
}

/** 删除已选文件。 */
function removeSourceFile(index) {
  form.value.sourceFiles.splice(index, 1);
  taskRef.value?.validateField("sourceFiles");
}

/** 提交新增或修改任务。 */
function submitForm() {
  taskRef.value.validate((valid) => {
    if (!valid) return;
    const data = {
      id: form.value.id,
      name: form.value.name,
      ttlFileName: form.value.ttlFileName,
      ttlFilePath: form.value.ttlFilePath,
      remark: form.value.remark,
      params: {
        docIds: form.value.sourceFiles.map((item) => item.id).join(","),
      },
    };
    submitting.value = true;
    (data.id ? updateUnstructTask(data) : addUnstructTask(data))
      .then(() => {
        proxy.$modal.msgSuccess(data.id ? "修改成功" : "新增成功");
        open.value = false;
        tableRef.value.getList();
      })
      .finally(() => {
        submitting.value = false;
      });
  });
}

/** 判断任务是否执行中或队列中（不可执行/修改/删除）。 */
function isRunning(row) {
  return [1, 4].includes(Number(row.status));
}

/** 执行抽取任务。 */
function handleExecute(row) {
  if (isRunning(row)) {
    proxy.$modal.msgWarning(
      Number(row.status) === 4 ? "任务已在队列中" : "任务执行中"
    );
    return;
  }
  proxy.$modal
    .confirm(`是否确认执行任务“${row.name}”？`)
    .then(() => executeUnstructTask(row.id))
    .then(() => {
      proxy.$modal.msgSuccess("任务已进入抽取队列");
      tableRef.value.getList();
    })
    .catch(() => {});
}

/** 打开任务执行日志。 */
function showLogs(row) {
  taskLogDialogRef.value.show(row.id, 1);
}

/** 查看非结构化抽取结果。 */
function handleResult(row) {
  router.push({
    path: "/app/extractResults",
    query: {
      id: row.id,
      name: row.name,
      pageType: "2",
      pageNum: currentPage.pageNum,
      pageSize: currentPage.pageSize,
    },
  });
}

/** 删除任务。 */
function handleDelete(row) {
  if (row) {
    if (isRunning(row)) {
      proxy.$modal.msgWarning(
        Number(row.status) === 4 ? "任务已在队列中" : "任务执行中"
      );
      return;
    }
    proxy.$modal
      .confirm(`是否确认删除任务“${row.name}”？`)
      .then(() => delUnstructTask(String(row.id)))
      .then(() => {
        proxy.$modal.msgSuccess("删除成功");
        tableRef.value.getList();
      })
      .catch(() => {});
    return;
  }
  const targetIds = selectedRows.value.map((item) => item.id);
  const runningRows = selectedRows.value.filter(isRunning);
  const deletableIds = targetIds.filter(
    (id) => !runningRows.some((item) => item.id === id)
  );
  if (!deletableIds.length) {
    proxy.$modal.msgWarning("选中的任务为进行中或队列中，无法删除");
    return;
  }
  const confirmText = runningRows.length
    ? `可删除${deletableIds.length}个，不可删除${runningRows.length}个，是否删除可删部分？`
    : `是否确认删除选中的 ${deletableIds.length} 个任务？`;
  proxy.$modal
    .confirm(confirmText)
    .then(() => delUnstructTask(deletableIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      tableRef.value.getList();
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
.task-status {
  justify-content: center;
}
</style>
