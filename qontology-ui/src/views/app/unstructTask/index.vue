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
    <div v-show="showSearch" class="pagecont-top">
      <el-form
        ref="queryRef"
        class="btn-style"
        :inline="true"
        :model="queryParams"
        label-width="75px"
        @submit.prevent
      >
        <el-form-item label="任务名称" prop="name"
          ><el-input
            v-model="queryParams.name"
            class="el-form-input-width"
            clearable
            placeholder="请输入任务名称"
            @keyup.enter="handleQuery"
        /></el-form-item>
        <el-form-item label="任务状态" prop="status"
          ><el-select
            v-model="queryParams.status"
            clearable
            placeholder="请选择任务状态"
            style="width: 200px"
            ><el-option
              v-for="item in app_task_status"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)" /></el-select
        ></el-form-item>
        <el-form-item label="发布状态" prop="publishStatus"
          ><el-select
            v-model="queryParams.publishStatus"
            clearable
            placeholder="请选择发布状态"
            style="width: 200px"
            ><el-option
              v-for="item in publish_status"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)" /></el-select
        ></el-form-item>
        <el-form-item label="创建时间"
          ><el-date-picker
            v-model="createTimeRange"
            class="el-form-input-width"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        /></el-form-item>
        <el-form-item
          ><el-button
            plain
            type="primary"
            @click="handleQuery"
            @mousedown="(event) => event.preventDefault()"
            ><i class="iconfont-mini icon-a-zu22377 mr5" />查询</el-button
          ><el-button
            @click="resetQuery"
            @mousedown="(event) => event.preventDefault()"
            ><i class="iconfont-mini icon-a-zu22378 mr5" />重置</el-button
          ></el-form-item
        >
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style"
          ><el-col :span="1.5"
            ><el-button
              v-hasPermi="['app:appUnstructTask:unstructtask:add']"
              plain
              type="primary"
              @click="handleAdd"
              @mousedown="(event) => event.preventDefault()"
              ><i class="iconfont-mini icon-xinzeng mr5" />新增</el-button
            ><el-button
              :disabled="!ids.length"
              plain
              type="danger"
              @click="handleDelete()"
              @mousedown="(event) => event.preventDefault()"
              ><i class="iconfont-mini icon-shanchu-huise mr5" />删除</el-button
            ></el-col
          >
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:showSearch="showSearch"
            :columns="columns"
            @queryTable="getList"
          />
        </div>
      </div>
      <el-table
        v-loading="loading"
        :data="taskList"
        :default-sort="defaultSort"
        stripe
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column
          v-if="columnVisible(1)"
          align="center"
          label="编号"
          prop="id"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="80"
        />
        <el-table-column
          v-if="columnVisible(2)"
          align="left"
          label="任务名称"
          prop="name"
          width="250"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="scope">{{
            scope.row.name || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(3)"
          align="left"
          label="描述"
          prop="remark"
          min-width="260"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="scope">{{
            scope.row.remark || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(4)"
          align="center"
          label="任务状态"
          prop="status"
          width="100"
          ><template #default="scope"
            ><dict-tag
              :options="app_task_status"
              :value="scope.row.status" /></template
        ></el-table-column>
        <el-table-column
          v-if="columnVisible(5)"
          align="center"
          label="发布状态"
          prop="publishStatus"
          width="100"
          ><template #default="scope"
            ><dict-tag
              :options="publish_status"
              :value="scope.row.publishStatus" /></template
        ></el-table-column>
        <el-table-column
          v-if="columnVisible(6)"
          align="center"
          label="发布人"
          prop="publishBy"
          width="100"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="scope">{{
            scope.row.publishBy || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(7)"
          align="center"
          label="发布时间"
          prop="publishTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="180"
          ><template #default="scope">{{
            scope.row.publishTime
              ? parseTime(scope.row.publishTime, "{y}-{m}-{d} {h}:{i}")
              : "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(8)"
          align="center"
          label="创建人"
          prop="createBy"
          width="100"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="scope">{{
            scope.row.createBy || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(9)"
          align="center"
          label="创建时间"
          prop="createTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="180"
          ><template #default="scope">{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
          }}</template></el-table-column
        >
        <el-table-column
          v-if="columnVisible(10)"
          align="center"
          fixed="right"
          label="操作"
          width="280"
          ><template #default="scope">
            <el-button
              :disabled="scope.row.publishStatus === 1 || isRunning(scope.row)"
              icon="VideoPlay"
              link
              type="primary"
              @click="handleExecute(scope.row)"
              >执行</el-button
            >
            <el-button
              :disabled="scope.row.status !== 2"
              icon="View"
              link
              type="primary"
              @click="handleResult(scope.row)"
              >抽取结果</el-button
            >
            <el-popover placement="bottom" :width="100" trigger="click"
              ><template #reference
                ><el-button icon="ArrowDown" link type="primary"
                  >更多</el-button
                ></template
              >
              <div style="width: 90px" class="butgdlist">
                <el-button
                  icon="List"
                  link
                  type="primary"
                  style="padding-left: 25px"
                  @click="showLogs(scope.row)"
                  >执行日志</el-button
                ><el-button
                  :disabled="
                    scope.row.publishStatus === 1 || isRunning(scope.row)
                  "
                  icon="Edit"
                  link
                  type="primary"
                  style="padding-left: 30px"
                  @click="handleEdit(scope.row)"
                  >修改</el-button
                ><el-button
                  :disabled="
                    scope.row.publishStatus === 1 || isRunning(scope.row)
                  "
                  icon="Delete"
                  link
                  type="danger"
                  style="padding-left: 30px"
                  @click="handleDelete(scope.row)"
                  >删除</el-button
                >
              </div></el-popover
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

    <el-dialog
      v-model="open"
      :append-to="appContainer"
      draggable
      width="800px"
      @closed="resetForm"
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
              <el-table
                v-loading="loading"
                :data="form.sourceFiles"
                height="300px"
                stripe
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
                :rows="3"
                maxlength="512个字符"
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
const { app_task_status, publish_status } = proxy.useDict(
  "app_task_status",
  "publish_status"
);
const appContainer = ref();
const queryRef = ref();
const taskRef = ref();
const fileMultipleRef = ref();
const taskLogDialogRef = ref();
const loading = ref(false);
const submitting = ref(false);
const showSearch = ref(true);
const open = ref(false);
const title = ref("");
const taskList = ref([]);
const ids = ref([]);
const total = ref(0);
const createTimeRange = ref([]);
const form = ref({});
const defaultSort = { prop: "createTime", order: "descending" };
const columns = ref([
  { key: 1, label: "编号", visible: true },
  { key: 2, label: "任务名称", visible: true },
  { key: 3, label: "描述", visible: true },
  { key: 4, label: "任务状态", visible: true },
  { key: 5, label: "发布状态", visible: true },
  { key: 6, label: "发布人", visible: true },
  { key: 7, label: "发布时间", visible: true },
  { key: 8, label: "创建人", visible: true },
  { key: 9, label: "创建时间", visible: true },
  { key: 10, label: "操作", visible: true },
]);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  status: undefined,
  publishStatus: undefined,
  orderByColumn: "createTime",
  isAsc: "descending",
  params: {},
});
const rules = {
  name: [{ required: true, message: "任务名称不能为空", trigger: "blur" }],
  sourceFiles: [
    {
      required: true,
      validator: (_, value, callback) =>
        value?.length ? callback() : callback(new Error("添加文件不能为空")),
      trigger: "change",
    },
  ],
  ttlFilePath: [
    { required: true, message: "请上传TTL本体文件", trigger: "change" },
  ],
};

/** 判断列表列是否显示。 */ function columnVisible(key) {
  return columns.value.find((item) => item.key === key)?.visible ?? true;
}
/** 查询非结构化抽取任务。 */ function getList() {
  loading.value = true;
  const params = { ...queryParams, params: {} };
  if (createTimeRange.value?.length) {
    params.params.beginCreateTime = createTimeRange.value[0];
    params.params.endCreateTime = createTimeRange.value[1] + " 23:59:59";
  }
  listUnstructTask(params)
    .then(({ data }) => {
      taskList.value = data.rows;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}
/** 执行列表查询。 */ function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
/** 重置列表查询。 */ function resetQuery() {
  createTimeRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 处理列表排序。 */ function handleSortChange(column) {
  queryParams.orderByColumn = column.prop;
  queryParams.isAsc = column.order;
  getList();
}
/** 记录列表多选结果。 */ function handleSelectionChange(rows) {
  ids.value = rows.map((item) => item.id);
}
/** 重置任务表单。 */ function resetForm() {
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
/** 打开新增任务弹框。 */ function handleAdd() {
  resetForm();
  title.value = "新增非结构化抽取任务";
  open.value = true;
}
/** 打开修改任务弹框。 */ function handleEdit(row) {
  if (isRunning(row)) {
    proxy.$modal.msgWarning(
      Number(row.status) === 4 ? "任务已在队列中" : "任务执行中"
    );
    return;
  }
  resetForm();
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
/** 打开文件选择弹框。 */ function selectFiles() {
  fileMultipleRef.value.open(form.value.sourceFiles);
}
/** 接收文件选择结果。 */ function confirmFiles(rows) {
  form.value.sourceFiles = rows.map((item) => ({
    id: item.id,
    fileName: item.fileName,
  }));
  taskRef.value?.validateField("sourceFiles");
}
/** 删除已选文件。 */ function removeSourceFile(index) {
  form.value.sourceFiles.splice(index, 1);
  taskRef.value?.validateField("sourceFiles");
}
/** 提交新增或修改任务。 */ function submitForm() {
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
        getList();
      })
      .finally(() => {
        submitting.value = false;
      });
  });
}
/** 判断任务是否执行中或队列中（不可执行/修改/删除）。 */ function isRunning(
  row
) {
  return [1, 4].includes(Number(row.status));
}
/** 执行抽取任务。 */ function handleExecute(row) {
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
      getList();
    })
    .catch(() => {});
}
/** 打开任务执行日志。 */ function showLogs(row) {
  taskLogDialogRef.value.show(row.id, 1);
}
/** 查看非结构化抽取结果。 */ function handleResult(row) {
  router.push({
    path: "/app/extractResults",
    query: {
      id: row.id,
      name: row.name,
      pageType: "2",
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
    },
  });
}
/** 删除任务。 */ function handleDelete(row) {
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
        getList();
      })
      .catch(() => {});
    return;
  }
  const targetIds = ids.value;
  const runningRows = (taskList.value || []).filter(
    (item) => ids.value.includes(item.id) && isRunning(item)
  );
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
      getList();
    })
    .catch(() => {});
}

onMounted(getList);
</script>

<style lang="scss" scoped>
.hint-style {
  display: flex;
  align-items: center;
  .icon {
    color: #efbd47;
    cursor: pointer;
  }
  .text {
    color: #efbd47;
    cursor: pointer;
    font-size: 12px;
  }
}
</style>
