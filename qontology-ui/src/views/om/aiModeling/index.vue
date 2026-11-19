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
  <div class="app-container" ref="app-container">
    <GuideTip tip-id="ontology/aiModeling.list" />

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
          v-hasPermi="['om:aiModeling:add']"
          type="primary"
          plain
          @click="openCreateDialog"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:aiModeling:remove']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting"
          @click="removeTasks()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <template #status="{ row }">
          <dict-tag :options="om_ai_task_status" :value="row.status" />
        </template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
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
            v-hasPermi="['om:aiModeling:reparse']"
            link
            type="primary"
            icon="Refresh"
            :disabled="row.status === 1 || reparsing"
            @click="reparseTask(row)"
          >重新解析</el-button>
          <el-button
            v-hasPermi="['om:aiModeling:list']"
            link
            type="primary"
            icon="View"
            @click="openDetail(row)"
          >详情</el-button>
          <el-popover
            v-if="
              auth.hasPermiOr(['om:aiModeling:edit', 'om:aiModeling:remove'])
            "
            placement="bottom"
            :width="150"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                  v-if="auth.hasPermi('om:aiModeling:edit')"
                  link
                  type="primary"
                  icon="Edit"
                  :disabled="row.status === 1"
                  @click="openEditDialog(row)"
              >修改</el-button>
              <el-button
                  v-if="auth.hasPermi('om:aiModeling:remove')"
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.status === 1 || deleting"
                  @click="removeTasks(row)"
              >删除</el-button>
            </div>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 修改任务弹框：基础信息 + 文件 -->
    <el-dialog
      v-model="editOpen"
      title="修改AI辅助建模任务"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
      :close-on-press-escape="!editSaving"
      :before-close="handleEditBeforeClose"
      @closed="clearEditValidation"
    >
      <el-form
        v-loading="editLoading"
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
        class="ai-task-form"
      >
        <guide-tip-panel
          type="hint"
          class="mb15"
          allow-wrap
          title="维护任务基本信息与待解析文档，修改后如需重新提取候选，请保存后点击列表“重新解析”。"
        />
        <qt-form-item label="任务名称" prop="taskName">
          <el-input
            v-model="editForm.taskName"
            placeholder="请输入任务名称"
            maxlength="100"
          />
        </qt-form-item>
        <qt-form-item label="任务描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
            maxlength="256个字符"
            show-word-limit
          />
        </qt-form-item>
        <qt-form-item label="文件来源" prop="fileSource">
          <el-radio-group
            v-model="editForm.fileSource"
            disabled
            @change="handleEditFileSourceChange"
          >
            <el-radio value="UPLOAD">上传文件</el-radio>
            <el-radio value="SYSTEM">系统文件</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item
          v-if="editForm.fileSource === 'UPLOAD'"
          label="上传文件"
          prop="filePaths"
        >
          <FileUpload2
            v-model="editForm.filePaths"
            :file-name="editForm.fileNames"
            :limit="5"
            :file-size="5"
            :file-type="aiFileTypes"
            disabled
            relative-url
            @update:file-info-list="(files) => (editForm.files = files)"
          />
        </qt-form-item>
        <qt-form-item v-else label="系统文件" prop="systemFiles">
          <div class="system-file-picker">
            <el-button
              type="primary"
              plain
              disabled
              @click="openEditSystemFileSelector"
            >选择系统文件</el-button>
            <div v-if="editForm.systemFiles.length" class="selected-file-list">
              <div
                v-for="file in editForm.systemFiles"
                :key="file.id || file.fileUrl"
                class="selected-file-item"
              >
                <span :title="file.fileName">{{ file.fileName }}</span>
                <el-button
                  link
                  type="danger"
                  disabled
                  icon="Delete"
                  title="移除文件"
                  @click="removeEditSystemFile(file)"
                ></el-button>
              </div>
            </div>
          </div>
          <SystemFileMultiple
            ref="editSystemFileSelectorRef"
            :append-to="$refs['app-container']"
            @confirm="handleEditSystemFilesSelected"
          />
        </qt-form-item>
      </el-form>
      <template #footer>
        <el-button :disabled="editSaving" @click="editOpen = false">
          取消
        </el-button>
        <el-button type="primary" :loading="editSaving" @click="submitEdit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AiModeling">
import { useRouter } from "vue-router";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import FileUpload2 from "@/components/FileUpload2";
import SystemFileMultiple from "@/views/app/unstructTask/fileMultiple.vue";
import {
  deleteAiTask,
  getAiFiles,
  getAiTask,
  listAiTasks,
  reparseAiTask,
  updateAiTask,
  updateAiTaskFiles,
} from "@/api/om/aiModeling";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { creatorPhones } = useCreatorPhones();
const router = useRouter();
const { versionId, requireVersion } = useOntologyVersion();
const { om_ai_task_status } = proxy.useDict("om_ai_task_status");
const aiFileTypes = [
  "txt",
  "pdf",
  "html",
  "xlsx",
  "xls",
  "docx",
  "csv",
  "md",
  "mdx",
  "htm",
  "markdown",
  "ttl",
];

const tableRef = ref();
const selectedIds = ref([]);
const deleting = ref(false);
const reparsing = ref(false);

/**
 * 分页查询AI辅助建模任务；版本号缺失时返回空列表，避免无版本上下文发起无效查询。
 * @param {Object} params QtTable 传入的查询参数
 * @returns {Promise<Object>} 列表接口返回体
 */
function fetchTasks(params) {
  if (!versionId.value) {
    return Promise.resolve({ data: { rows: [], total: 0 } });
  }
  return listAiTasks({ ...params, versionId: versionId.value });
}

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
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
      selectable: (row) => row.status !== 1,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 60,
      minWidth: 60,
      align: "center",
      sortable: true,
    },
    {
      label: "任务名称",
      prop: "taskName",
      slot: "text",
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "任务状态",
      prop: "status",
      slot: "status",
      width: 120,
      minWidth: 120,
    },
    {
      label: "失败原因",
      prop: "failureReason",
      slot: "text",
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "候选数量",
      prop: "candidateCount",
      slot: "text",
      width: 100,
      minWidth: 100,
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 130,
      minWidth: 130,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      minWidth: 160,
      align: "center",
      sortable: true,
    },
    {
      label: "操作",
      slot: "handle",
      width: 280,
      minWidth: 280,
      fixed: "right",
    },
  ],
  func: fetchTasks,
  params: {
    taskName: null,
    status: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "任务名称", prop: "taskName", component: { is: "input" } },
    {
      label: "任务状态",
      prop: "status",
      component: { is: "select", options: om_ai_task_status },
    },
  ],
});

// 版本切换后重置分页并刷新列表；首查由 QtTable 挂载时自动执行。
watch(versionId, async () => {
  await nextTick();
  tableRef.value?.resetQuery();
});

/** 刷新当前任务列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/** 进入三步式任务创建页面。 */
function openCreateDialog() {
  if (!requireVersion()) return;
  router.push(`/om/${versionId.value}/aiModeling/add`);
}

/** 弹框相关状态与表单。 */
const editOpen = ref(false);
const editSaving = ref(false);
const editLoading = ref(false);
const editFormRef = ref();
const editSystemFileSelectorRef = ref();
const editForm = reactive({
  id: null,
  taskName: "",
  description: "",
  fileSource: "UPLOAD",
  filePaths: "",
  fileNames: [],
  files: [],
  systemFiles: [],
});
const editRules = computed(() => ({
  taskName: [{ required: true, message: "请输入任务名称", trigger: "blur" }],
  fileSource: [{ required: true, message: "请选择文件来源", trigger: "change" }],
  filePaths: [
    {
      required: editForm.fileSource === "UPLOAD",
      message: "请至少上传一个文件",
      trigger: "change",
    },
  ],
  systemFiles: [{
    type: "array",
    required: editForm.fileSource === "SYSTEM",
    min: 1,
    message: "请至少选择一个系统文件",
    trigger: "change",
  }],
}));
let editOriginalFiles = [];
let editOriginalBasicInfo = { taskName: "", description: "" };
let editOriginalFileSource = "UPLOAD";

/**
 * 拉取任务基础信息与已上传文件后打开修改弹窗。
 * @param {Object} row 当前行数据
 */
async function openEditDialog(row) {
  if (!requireVersion()) return;
  editForm.id = row.id;
  editOpen.value = true;
  editLoading.value = true;
  try {
    const [taskResponse, fileResponse] = await Promise.all([
      getAiTask(row.id),
      getAiFiles(row.id),
    ]);
    const task = taskResponse.data || {};
    const files = fileResponse.data || [];
    editForm.fileSource = task.fileSource || "UPLOAD";
    editForm.taskName = task.taskName || "";
    editForm.description = task.description || "";
    editForm.filePaths = files.map((file) => file.fileUrl).join(",");
    editForm.fileNames = files.map((file) => file.fileName);
    editForm.files = files.map((file) => ({
      fileName: file.fileName,
      fileUrl: file.fileUrl,
      fileExt: file.fileExt,
      fileSize: file.fileSize,
      sourceFileId: file.sourceFileId,
    }));
    editForm.systemFiles = editForm.fileSource === "SYSTEM"
      ? files.map((file) => ({
          id: file.sourceFileId,
          fileName: file.fileName,
          filePath: file.fileUrl,
          fileUrl: file.fileUrl,
          fileExt: file.fileExt,
          fileSize: file.fileSize,
        }))
      : [];
    editOriginalFiles = files.map((file) => ({ ...file }));
    editOriginalFileSource = editForm.fileSource;
    editOriginalBasicInfo = {
      taskName: task.taskName || "",
      description: task.description || "",
    };
  } finally {
    editLoading.value = false;
  }
}

function handleEditFileSourceChange() {
  editForm.filePaths = "";
  editForm.fileNames = [];
  editForm.files = [];
  editForm.systemFiles = [];
  editFormRef.value?.clearValidate(["filePaths", "systemFiles"]);
}

function openEditSystemFileSelector() {
  editSystemFileSelectorRef.value?.open(editForm.systemFiles);
}

function handleEditSystemFilesSelected(files) {
  if (files.length > 5) {
    proxy.$modal.msgWarning("最多只能选择 5 个系统文件");
    return;
  }
  const unsupported = files.find((file) => {
    const path = file.fileName || file.filePath || "";
    const extension = path.includes(".") ? path.split(".").pop().toLowerCase() : "";
    return !aiFileTypes.includes(extension);
  });
  if (unsupported) {
    proxy.$modal.msgWarning(`文件“${unsupported.fileName}”的格式不支持，请重新选择`);
    return;
  }
  editForm.systemFiles = files.map((file) => ({
    ...file,
    sourceFileId: file.id,
    fileUrl: file.filePath || file.fileUrl,
    fileExt: file.fileExt || (file.fileName || "").split(".").pop().toLowerCase(),
  }));
  editFormRef.value?.validateField("systemFiles").catch(() => {});
}

function removeEditSystemFile(file) {
  editForm.systemFiles = editForm.systemFiles.filter(
    (item) => (item.id || item.fileUrl) !== (file.id || file.fileUrl)
  );
}

/** 弹窗关闭后重置表单与校验提示。 */
function clearEditValidation() {
  editFormRef.value?.resetFields();
}

/**
 * 保存期间拦截弹窗关闭回调。
 * @param {Function} done el-dialog 的 before-close 完成回调
 */
function handleEditBeforeClose(done) {
  if (editSaving.value) return;
  done();
}

/** 提交修改任务，按需分别更新基础信息与附件，提交期间锁定确定按钮。 */
async function submitEdit() {
  if (!requireVersion() || editSaving.value || editLoading.value) return;
  const valid = await editFormRef.value.validate().catch(() => false);
  if (!valid || editSaving.value || !editOpen.value) return;
  editSaving.value = true;
  try {
    const selectedFiles =
      editForm.fileSource === "SYSTEM" ? editForm.systemFiles : editForm.files;
    const currentUrls = editForm.fileSource === "SYSTEM"
      ? selectedFiles.map((file) => file.fileUrl || file.filePath).filter(Boolean)
      : editForm.filePaths ? editForm.filePaths.split(",") : [];
    const currentPersistedIds =
      editForm.fileSource === editOriginalFileSource
        ? editOriginalFiles
          .filter((file) => currentUrls.includes(file.fileUrl))
          .map((file) => file.id)
        : [];
    const originalUrls =
      editForm.fileSource === editOriginalFileSource
        ? editOriginalFiles.map((file) => file.fileUrl)
        : [];
    const newFiles = selectedFiles.filter(
      (file) =>
        (file.fileUrl || file.filePath) &&
        !originalUrls.includes(file.fileUrl || file.filePath)
    );
    const hasNewFiles = newFiles.length > 0;
    const hasRemovedFiles = editOriginalFiles.some(
      (file) => !currentUrls.includes(file.fileUrl)
    );
    const fileChanged =
      editForm.fileSource !== editOriginalFileSource || hasNewFiles || hasRemovedFiles;
    const basicInfoChanged =
      editForm.taskName !== editOriginalBasicInfo.taskName ||
      editForm.description !== editOriginalBasicInfo.description;

    if (basicInfoChanged) {
      await updateAiTask({
        id: editForm.id,
        taskName: editForm.taskName,
        description: editForm.description || "",
        versionId: versionId.value,
      });
    }

    if (fileChanged) {
      await updateAiTaskFiles({
        taskId: editForm.id,
        fileSource: editForm.fileSource,
        retainedFileIds: currentPersistedIds,
        files: newFiles.map((file) => ({
          sourceFileId:
            editForm.fileSource === "SYSTEM"
              ? file.sourceFileId || file.id
              : null,
          fileName: file.fileName || file.name,
          fileUrl: file.fileUrl || file.filePath || file.url,
          fileExt: file.fileExt,
          fileSize: file.fileSize || file.size || 0,
        })),
      });
      proxy.$modal.msgSuccess('文件已更新，请点击"重新解析"按钮执行解析');
    } else if (basicInfoChanged) {
      proxy.$modal.msgSuccess("修改成功");
    } else {
      proxy.$modal.msgSuccess("未检测到变更");
    }

    editOpen.value = false;
    refreshList();
  } finally {
    editSaving.value = false;
  }
}

/** 打开任务详情页。 */
function openDetail(row) {
  if (!requireVersion()) return;
  router.push(`/om/${versionId.value}/aiModeling/detail/${row.id}`);
}

/**
 * 记录勾选行编号，供顶部批量删除使用。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map((row) => row.id);
}

/**
 * 删除未执行中的任务，支持勾选批量删除。
 * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
 */
async function removeTasks(row) {
  if (deleting.value) return;
  const deleteIds = row ? [row.id] : [...selectedIds.value];
  if (!deleteIds.length) return;
  // 单条删除统一为“是否确认删除编号为X 的数据项？”（与批量删除同为系统提示风格）。
  const confirmText = row
    ? `是否确认删除编号为${row.id} 的数据项？`
    : `确认删除选中的 ${deleteIds.length} 条任务？`;
  try {
    await proxy.$modal.confirm(confirmText);
  } catch {
    return;
  }
  if (deleting.value) return;
  deleting.value = true;
  try {
    await deleteAiTask(deleteIds.join(","));
    proxy.$modal.msgSuccess("删除成功");
    refreshList();
  } finally {
    deleting.value = false;
  }
}

/**
 * 清空旧结果并对任务执行全量解析。
 * @param {Object} row 当前行数据
 */
async function reparseTask(row) {
  if (reparsing.value) return;
  try {
    await proxy.$modal.confirm(
      "重新解析会清除旧候选结果和解析过程，是否继续？"
    );
  } catch {
    return;
  }
  if (reparsing.value) return;
  reparsing.value = true;
  try {
    await reparseAiTask(row.id);
    proxy.$modal.msgSuccess("已重新开始解析");
    refreshList();
  } finally {
    reparsing.value = false;
  }
}

</script>

<style scoped lang="scss">
/* 对齐对象类型新增页的间隔：修改弹窗中带描述提示的表单项底部留白。 */
.ai-task-form :deep(.el-form-item.has-tip) {
  margin-bottom: 25px;
}
.system-file-picker {
  width: 100%;
}
.selected-file-list {
  width: 100%;
  margin-top: 10px;
  border-top: 1px solid #ebeef5;
}
.selected-file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 36px;
  border-bottom: 1px solid #ebeef5;
}
.selected-file-item span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
