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
        <template #lastExecute="{ row }">
          <div class="flex-column">
            <dict-tag
              class="execute-tag"
              :options="app_task_status"
              :value="row.status"
              @click="showLogs(row)"
            />
            <div class="last-execute-info">
              <div class="last-execute-info__row">
                <span class="last-execute-info__value">{{
                  row.lastExecuteTime ? timeAgo(row.lastExecuteTime) : "-"
                }}</span>
                <span class="ml10 last-execute-info__value">{{
                  formatDuration(row.executeDuration)
                }}</span>
              </div>
            </div>
          </div>
        </template>

        <template #taskStatus="{ row }">
          <div class="flex-center task-status">
            <el-switch
              v-model="row.publishStatus"
              :active-value="1"
              :inactive-value="0"
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
          >
            <template #reference>
              <el-button icon="ArrowDown" link type="primary" @click.stop
                >更多</el-button
              >
            </template>
            <div class="task-action-list">
              <el-button icon="List" link type="primary" @click="showLogs(row)">
                执行日志
              </el-button>
              <el-button
                class="btn-icon-offset"
                icon="view"
                link
                type="primary"
                @click="handleDetail(row)"
              >
                详情
              </el-button>
              <el-button
                class="btn-icon-offset"
                :disabled="row.publishStatus === 1 || isRunning(row)"
                icon="Edit"
                link
                type="primary"
                @click="handleEdit(row)"
              >
                修改
              </el-button>
              <el-button
                class="btn-icon-offset"
                :disabled="row.publishStatus === 1 || isRunning(row)"
                icon="Delete"
                link
                type="danger"
                @click="handleDelete(row)"
              >
                删除</el-button
              >
            </div>
          </el-popover>
        </template>
        <template #publishBy="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{
              row.publishBy || "-"
            }}</span>
            <span v-if="creatorPhones[row.publisherId]" class="desc-text">{{
              creatorPhones[row.publisherId]
            }}</span>
          </div>
        </template>
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
      </qt-table>
    </qt-wrap>

    <el-dialog
      v-model="open"
      :append-to="appContainer"
      draggable
      width="800px"
      @closed="resetTaskForm"
    >
      <template #header>
        <span class="el-dialog__title">{{ title }}</span>
      </template>
      <el-form
        ref="taskRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <qt-form-item label="任务名称" prop="name">
              <el-input
                v-model="form.name"
                maxlength="256"
                placeholder="请输入任务名称"
              />
            </qt-form-item>
            <qt-form-item
              label="责任人"
              prop="responsibleUserId"
              class="row-full"
            >
              <el-select
                v-model="form.responsibleUserId"
                placeholder="请选择责任人"
                clearable
                filterable
                @change="handleResponsibleUserChange"
              >
                <el-option
                  v-for="user in userOptions"
                  :key="user.value"
                  :label="user.label"
                  :value="user.value"
                />
              </el-select>
            </qt-form-item>
            <qt-form-item
              label="联系电话"
              prop="responsiblePhone"
              class="row-full"
            >
              <el-input
                :model-value="form.responsiblePhone"
                disabled
                placeholder="请输入联系电话"
              />
            </qt-form-item>
            <qt-form-item label="导入文件" prop="sourceFiles">
              <el-button
                :disabled="form.status && form.status !== 0"
                plain
                type="primary"
                @click="selectFiles"
              >
                <i class="iconfont icon-upload-cloud-line mr5" />导入文件
              </el-button>
            </qt-form-item>
            <div class="unstruct-task__source-files">
              <qt-table v-bind="sourceFileTableStore" ref="sourceFileTableRef">
                <template #fileName="{ row }">
                  {{ row.fileName || "-" }}
                </template>
                <template #action="{ $index }">
                  <el-button
                    :disabled="form.status && form.status !== 0"
                    icon="Delete"
                    link
                    type="danger"
                    @click="removeSourceFile($index)"
                    >删除
                  </el-button>
                </template>
              </qt-table>
            </div>
            <qt-form-item label="本体文件" prop="ttlFilePath">
              <FileUpload
                v-model="form.ttlFilePath"
                v-model:file-name="form.ttlFileName"
                :file-size="20"
                :file-type="['ttl']"
                :limit="1"
              />
            </qt-form-item>
            <qt-form-item label="描述" prop="remark">
              <el-input
                v-model="form.remark"
                maxlength="256个字符"
                :rows="3"
                show-word-limit
                type="textarea"
                placeholder="请输入描述"
              />
            </qt-form-item>
          </el-col>
        </el-row>
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

    <!-- 非结构化抽取任务详情对话框 -->
    <el-dialog
      v-model="openDetail"
      :append-to="appContainer"
      draggable
      width="800px"
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ detailTitle }}
        </span>
      </template>
      <el-form :model="detailForm" label-width="80px" class="column-form">
        <el-form-item label="任务名称" prop="name" class="row-full">
          <div class="form-readonly">
            {{ detailForm.name || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <dict-tag
            class="con-value"
            :options="app_task_status"
            :value="detailForm.status"
          />
        </el-form-item>
        <el-form-item label="发布状态">
          <div class="form-readonly">
            {{ detailForm.publishStatus === 1 ? "已发布" : "未发布" }}
          </div>
        </el-form-item>
        <el-form-item label="责任人">
          <div class="form-readonly">
            {{ detailForm.responsiblePersonName || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="联系电话">
          <div class="form-readonly">
            {{ detailForm.responsiblePhone || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="最后执行">
          <div class="form-readonly">
            {{ formatDetailTime(detailForm.lastExecuteTime) }}
          </div>
        </el-form-item>
        <el-form-item label="执行时长">
          <div class="form-readonly">
            {{ formatDuration(detailForm.executeDuration) }}
          </div>
        </el-form-item>
        <el-form-item label="本体文件" prop="ttlFileName" class="row-full">
          <div class="form-readonly">
            {{ detailForm.ttlFileName || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="导入文件" class="row-full">
          <div class="form-readonly detail-file-list">
            <template
              v-if="detailForm.sourceFiles && detailForm.sourceFiles.length"
            >
              <el-tag
                v-for="file in detailForm.sourceFiles"
                :key="file.docId"
                type="info"
                class="detail-file-tag"
                :title="file.docName"
              >
                {{ file.docName || "-" }}
              </el-tag>
            </template>
            <span v-else>—</span>
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="remark" class="row-full">
          <div class="form-readonly textarea">
            {{ detailForm.remark || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="发布人">
          <div class="form-readonly">
            {{ detailForm.publishBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="发布时间">
          <div class="form-readonly">
            {{ formatDetailTime(detailForm.publishTime) }}
          </div>
        </el-form-item>
        <el-form-item label="修改人" prop="updateBy">
          <div class="form-readonly">
            {{ detailForm.updateBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="修改时间" prop="updateTime">
          <div class="form-readonly">
            {{ formatDetailTime(detailForm.updateTime) }}
          </div>
        </el-form-item>
        <el-form-item label="创建人" prop="createBy">
          <div class="form-readonly">
            {{ detailForm.createBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <div class="form-readonly">
            {{ formatDetailTime(detailForm.createTime) }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="openDetail = false">关 闭</el-button>
        </div>
      </template>
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
  cancelReleaseUnstructTask,
  delUnstructTask,
  executeUnstructTask,
  getUnstructTask,
  listUnstructTask,
  releaseUnstructTask,
  updateUnstructTask,
} from "@/api/app/unstructTask";
import FileMultiple from "./fileMultiple.vue";
import TaskLogDialog from "@/views/app/taskLog/taskLogDialog.vue";
import { timeAgo } from "@/utils/time";
import { useCreatorPhones } from "@/utils/useCreatorPhones.js";
import { useRouter } from "vue-router";
import { listUserAll } from "@/api/system/system/user.js";
import { ref } from "vue";

const { proxy } = getCurrentInstance();
const router = useRouter();
const { creatorPhones } = useCreatorPhones();
const { app_task_status } = proxy.useDict("app_task_status");
const appContainer = ref();
const tableRef = ref();
const sourceFileTableRef = ref();
const taskRef = ref();
const fileMultipleRef = ref();
const taskLogDialogRef = ref();
const submitting = ref(false);
const open = ref(false);
const title = ref("");
const openDetail = ref(false);
const detailTitle = ref("");
const detailForm = ref({});
const selectedRows = ref([]);
const multiple = ref(true);
const form = ref({});
const userOptions = ref([]);

const rules = {
  name: [{ required: true, message: "请输入任务名称", trigger: "blur" }],
  responsibleUserId: [
    { required: true, message: "请选择责任人", trigger: "change" },
  ],
  responsiblePhone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
  ],
  sourceFiles: [
    {
      required: true,
      type: "array",
      min: 1,
      message: "请选择导入文件",
      trigger: "change",
    },
  ],
  ttlFilePath: [
    { required: true, message: "请上传本体文件", trigger: "change" },
  ],
};

function refreshSourceFileTable() {
  nextTick(() => sourceFileTableRef.value?.getList());
}
// 记录当前分页，供抽取结果页返回时还原列表位置
const currentPage = reactive({ pageNum: 1, pageSize: 10 });
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
    { type: "selection", width: 55 },
    {
      label: "编号",
      prop: "id",
      width: 70,
      sortable: "custom",
      sortableKey: "id",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "任务信息",
      minWidth: 300,
      align: "left",
      info: {
        title: "name",
        desc: "remark",
        click: (row) => handleEdit(row),
      },
    },
    {
      label: "执行情况",
      width: 150,
      slot: "lastExecute",
      align: "left",
    },
    { label: "发布状态", slot: "taskStatus", width: 90 },
    {
      label: "发布人",
      width: 110,
      align: "left",
      slot: "publishBy",
    },
    {
      label: "负责人",
      width: 110,
      slot: "responsibleUser",
      align: "left",
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
    { label: "操作", slot: "action", width: 230, fixed: "right" },
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
const sourceFileTableStore = reactive({
  config: {
    notPagination: true,
    initResquest: false,
    table: {
      stripe: true,
      rowKey: "id",
    },
  },
  columns: [
    {
      label: "文件名称",
      prop: "fileName",
      slot: "fileName",
      width: 450,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "操作", slot: "action", align: "center" },
  ],
  func: () => Promise.resolve({ data: form.value.sourceFiles || [] }),
});
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

onMounted(async () => {
  await loadUserOptions();
});

function rememberPage(params) {
  Object.assign(currentPage, {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
  });
}

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

// 负责人选择变化时更新电话（按字符串比对，兼容数字/字符串类型）
function handleResponsibleUserChange(value) {
  const user = userOptions.value.find(
    (user) => String(user.value) === String(value)
  );
  form.value.responsiblePhone = user?.phone || "";
  form.value.responsiblePersonName = user?.label || "";
}

/** 加载系统用户选项，电话取自用户资料；只采纳当前弹窗的最新请求。 */
async function loadUserOptions() {
  userOptions.value = [];
  const response = await listUserAll();
  const rows = response.rows || response.data?.rows || [];
  userOptions.value = rows
    .filter((user) => user.userId != null)
    .map((user) => ({
      // value 保持数字类型，与后端返回的 responsibleUserId 一致，避免 el-select 回显失配
      value: user.userId,
      label: user.nickName || user.userName || String(user.userId),
      phone: user.phonenumber || "",
    }));
}

/** 最近执行时间、执行耗时、抽取成果、发布人/负责人电话后端暂未返回，先按约定字段名占位。 */
function formatDuration(value) {
  if (value === null || value === undefined || value === "") return "-";
  const seconds = Math.max(Math.round(Number(value)), 0);
  if (seconds < 60) return `${seconds}秒`;
  return `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

/** 详情时间统一展示到秒，兼容接口返回的 ISO 时间字符串。 */
function formatDetailTime(value) {
  return value ? proxy.parseTime(value, "{y}-{m}-{d} {h}:{i}:{s}") || "-" : "-";
}

/** 任务状态开关：接口未返回 validFlag 时按启用展示，publishStatus 为空时归一为未发布(0)。 */
function formatData(rows) {
  return rows.map((row) => ({
    ...row,
    validFlag: row.validFlag ?? true,
    publishStatus: row.publishStatus ?? 0,
  }));
}

/** 任务状态（启用/停用）开关，后端启用/停用接口待提供。 */
function handleValidFlagChange(row, value) {
  if (value === 0) {
    proxy.$modal.confirm("是否确认取消发布？").then(() => {
      const request = cancelReleaseUnstructTask(row.id);
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
      const request = releaseUnstructTask(row.id);
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
    responsibleUserId: null,
    responsiblePersonName: "",
    responsiblePhone: "",
    sourceFiles: [],
  };
  proxy.resetForm("taskRef");
}

/** 打开新增任务弹框。 */
function handleAdd() {
  resetTaskForm();
  title.value = "新增非结构化抽取任务";
  open.value = true;
  refreshSourceFileTable();
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
    // 详情未返回电话时，从用户选项中按责任人id兜底获取
    const matchedUser = userOptions.value.find(
      (user) => String(user.value) === String(data.responsibleUserId)
    );
    form.value = {
      id: data.id,
      name: data.name,
      ttlFileName: data.ttlFileName,
      ttlFilePath: data.ttlFilePath,
      remark: data.remark,
      status: data.status,
      // 后端返回数字型 userId，与下拉选项按同一类型回显
      responsibleUserId:
        data.responsibleUserId == null || data.responsibleUserId === ""
          ? null
          : Number(data.responsibleUserId),
      responsiblePersonName:
        data.responsiblePersonName || matchedUser?.label || "",
      responsiblePhone: data.responsiblePhone || matchedUser?.phone || "",
      sourceFiles: (data.relPageReqVOS || []).map((item) => ({
        id: item.docId,
        fileName: item.docName,
      })),
    };
    title.value = "修改非结构化抽取任务";
    open.value = true;
    refreshSourceFileTable();
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
  refreshSourceFileTable();
}

/** 删除已选文件。 */
function removeSourceFile(index) {
  form.value.sourceFiles.splice(index, 1);
  taskRef.value?.validateField("sourceFiles");
  refreshSourceFileTable();
}

// FileUpload 未派发 el.form.change，上传/删除后手动触发本体文件校验以刷新错误态
watch(
  () => form.value.ttlFilePath,
  () => taskRef.value?.validateField("ttlFilePath")
);

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
      responsiblePersonName: form.value.responsiblePersonName,
      // 提交时归一为数字，与后端字段类型保持一致
      responsibleUserId:
        form.value.responsibleUserId == null ||
        form.value.responsibleUserId === ""
          ? null
          : Number(form.value.responsibleUserId),
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

/** 打开非结构化抽取任务详情弹窗。 */
function handleDetail(row) {
  getUnstructTask(row.id).then(({ data }) => {
    // 详情未返回电话时，从用户选项中按责任人id兜底获取
    const matchedUser = userOptions.value.find(
      (user) => String(user.value) === String(data.responsibleUserId)
    );
    detailForm.value = {
      ...data,
      publishStatus: data.publishStatus ?? 0,
      responsiblePhone: data.responsiblePhone || matchedUser?.phone || "",
      sourceFiles: (data.relPageReqVOS || []).map((item) => ({
        docId: item.docId,
        docName: item.docName,
      })),
    };
    detailTitle.value = "非结构化抽取任务详情";
    openDetail.value = true;
  });
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
.unstruct-task__source-files {
  margin: 0 0 10px 80px;
}

.task-status {
  justify-content: center;
}

.execute-tag {
  align-self: flex-start;
  cursor: pointer;
}

// 详情弹窗内的导入文件标签，允许换行展示。
.detail-file-list {
  flex-wrap: wrap;
  gap: 6px;
  padding: 6px 10px;
}
.detail-file-tag {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
