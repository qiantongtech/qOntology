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
  <div class="app-container enum-page" ref="app-container">
    <GuideTip tip-id="om/semantic/enum.list" />
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
      </template>

      <template #actions-data>
        <el-button
          v-hasPermi="['om:enumeration:type:add']"
          type="primary"
          plain
          @click="handleAdd"
          @mousedown="(e) => e.preventDefault()"
          ><i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:enumeration:type:remove']"
          type="danger"
          plain
          :disabled="multiple || deleting"
          @click="handleDelete()"
          @mousedown="(e) => e.preventDefault()"
          ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 枚举类型有数据列表权限时展示为跳转链接。 -->
        <template #enumType="{ row }">
          <router-link
            v-if="auth.hasPermi('om:enumeration:data:list')"
            :to="{ name: 'OmEnumData', params: { versionId, enumId: row.enumId } }"
            class="link-type"
            >{{ row.enumType }}
          </router-link>
          <span v-else>{{ row.enumType || "-" }}</span>
        </template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['om:enumeration:type:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情</el-button
          >
          <el-popover
            v-if="auth.hasPermiOr(['om:enumeration:type:edit', 'om:enumeration:type:remove'])"
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              v-if="auth.hasPermi('om:enumeration:type:edit')"
              link
              type="primary"
              icon="Edit"
              :disabled="saving"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('om:enumeration:type:remove')"
              link
              type="danger"
              icon="Delete"
              :disabled="deleting"
              @click="handleDelete(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>
    <el-dialog
      v-model="open"
      :title="title"
      width="800px"
      :append-to="$refs['app-container']"
      :close-on-press-escape="!saving"
      :before-close="handleBeforeClose"
      draggable
      @closed="clearValidation"
    >
      <el-form
        ref="typeRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        @submit.prevent="submitForm"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="枚举名称" prop="enumName">
              <el-input
                v-model="form.enumName"
                placeholder="请输入枚举名称"
                :maxlength="128"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举类型" prop="enumType">
              <el-input
                v-model="form.enumType"
                placeholder="请输入枚举类型"
                :maxlength="128"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                  >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                class="character-count"
                type="textarea"
                :rows="3"
                :maxlength="500"
                show-word-limit
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="submitForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="openDetail"
      class="enum-detail-dialog"
      title="枚举类型详情"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <el-form
        :model="detail"
        label-width="90px"
        label-position="right"
        class="enum-detail-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="编号">
              <div class="form-readonly">{{ detail.enumId }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举名称">
              <div class="form-readonly">{{ detail.enumName ?? "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举类型">
              <div class="form-readonly">{{ detail.enumType ?? "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <div class="form-readonly">
                <dict-tag
                  :options="sys_normal_disable"
                  :value="detail.status"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建人">
              <div class="form-readonly">{{ detail.createBy || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建时间">
              <div class="form-readonly">
                {{ parseTime(detail.createTime) || "-" }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <div class="form-readonly textarea">
                {{ detail.remark || "-" }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="openDetail = false">关闭</el-button>
      </template>
    </el-dialog>
    <el-dialog
      v-model="upload.open"
      title="枚举类型导入"
      width="500px"
      :append-to="$refs['app-container']"
      :close-on-press-escape="!upload.isUploading"
      :show-close="!upload.isUploading"
    >
      <el-upload
        ref="uploadRef"
        :action="upload.url"
        :headers="upload.headers"
        :data="{ updateSupport: upload.updateSupport }"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :disabled="upload.isUploading"
        :before-upload="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-error="handleFileError"
      >
        <el-button>选择 Excel 文件</el-button>
        <template #tip>
          <div class="el-upload__tip">请使用导入模板。</div>
        </template>
      </el-upload>
      <el-checkbox v-model="upload.updateSupport">更新已有数据</el-checkbox>
      <el-button link type="primary" @click="importTemplate"
        >下载模板</el-button
      >
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="upload.isUploading" @click="upload.open = false"
            >取 消</el-button
          >
          <el-button
            type="primary"
            :loading="upload.isUploading"
            @click="submitFileForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OmEnumType">
import {
  onBeforeUnmount,
  ref,
  reactive,
  toRefs,
  nextTick,
  watch,
  getCurrentInstance,
} from "vue";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import {
  listType,
  getType,
  addType,
  updateType,
  delType,
} from "@/api/om/enumeration/type";
import { getToken } from "@/utils/auth";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");
const { versionId, requireVersion } = useOntologyVersion();
const tableRef = ref();
const typeRef = ref();
const uploadRef = ref();
const open = ref(false);
const openDetail = ref(false);
const saving = ref(false);
const deleting = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");
const detail = ref({});
const { creatorPhones } = useCreatorPhones();

/** 版本号缺失时直接返回空列表，避免无版本上下文发起无效查询。 */
function fetchEnumTypes(params) {
  if (!versionId.value) {
    return Promise.resolve({ data: { rows: [], total: 0 } });
  }
  return listType({ ...params, versionId: versionId.value });
}

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "enumId",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    { label: "编号", prop: "enumId", slot: "text", width: 80, minWidth: 80, align: "left", sortable: true },
    { label: "枚举名称", prop: "enumName", slot: "text", minWidth: 150, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "枚举类型", prop: "enumType", slot: "enumType", minWidth: 150, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "状态", prop: "status", dict: "sys_normal_disable", width: 90, minWidth: 90 },
    { label: "创建人", prop: "createBy", slot: "creator", width: 130, minWidth: 130, align: "left", headerAlign: "left" },
    { label: "创建时间", prop: "createTime", date: true, width: 160, minWidth: 160, align: "left", sortable: true },
    { label: "备注", prop: "remark", slot: "text", minWidth: 200, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "操作", slot: "handle", width: 170, minWidth: 170, fixed: "right" },
  ],
  func: fetchEnumTypes,
  params: {
    enumName: null,
    enumType: null,
    status: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "枚举名称", prop: "enumName", component: { is: "input" } },
    { label: "枚举类型", prop: "enumType", component: { is: "input" } },
    { label: "状态", prop: "status", component: { is: "select", options: sys_normal_disable } },
  ],
});
const data = reactive({
  form: {},
  rules: {
    enumName: [
      {
        required: true,
        whitespace: true,
        message: "枚举名称不能为空",
        trigger: "blur",
      },
      { max: 128, message: "枚举名称不能超过128个字符", trigger: "blur" },
    ],
    enumType: [
      {
        required: true,
        whitespace: true,
        message: "枚举类型不能为空",
        trigger: "blur",
      },
      { max: 128, message: "枚举类型不能超过128个字符", trigger: "blur" },
      {
        pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
        message: "须以字母开头，仅允许字母、数字和下划线",
        trigger: "blur",
      },
    ],
    status: [{ required: true, message: "请选择状态", trigger: "change" }],
    remark: [{ max: 512, message: "备注不能超过512个字符", trigger: "blur" }],
  },
});
const { form, rules } = toRefs(data);
const upload = reactive({
  open: false,
  isUploading: false,
  updateSupport: false,
  headers: { Authorization: "Bearer " + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + "/om/type/importData",
});
let contextRequest = 0;
let detailRequest = 0;

/** 刷新当前页列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/** 重置枚举类型表单字段。 */
function reset() {
  form.value = {
    enumId: undefined,
    versionId: versionId.value,
    enumName: "",
    enumType: "",
    status: "0",
    remark: "",
  };
  clearValidation();
}

/** 清除表单校验提示。 */
function clearValidation() {
  typeRef.value?.clearValidate();
}

/**
 * 保存期间拦截弹窗关闭回调。
 * @param {Function} done el-dialog 的 before-close 完成回调
 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 关闭新增/修改弹窗；保存中禁止关闭。 */
function cancel() {
  if (saving.value) return;
  open.value = false;
  reset();
}

/**
 * 多选框选中数据，同步批量删除按钮的禁用状态。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.enumId);
  multiple.value = !selection.length;
}

/** 新增按钮操作。 */
async function handleAdd() {
  if (!requireVersion()) return;
  ++detailRequest;
  reset();
  title.value = "新增枚举类型";
  open.value = true;
  await nextTick();
  clearValidation();
}

/**
 * 修改按钮操作。
 * @param {Object} [row] 当前行数据，缺省时取首个多选编号
 */
async function handleUpdate(row) {
  const request = ++detailRequest;
  const response = await getType(row?.enumId ?? ids.value[0]);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning("记录已不存在，请刷新");
    return;
  }
  reset();
  form.value = { ...form.value, ...response.data };
  title.value = "修改枚举类型";
  open.value = true;
  await nextTick();
  clearValidation();
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
async function handleDetail(row) {
  const request = ++detailRequest;
  const response = await getType(row.enumId);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning("记录已不存在，请刷新");
    return;
  }
  detail.value = response.data;
  openDetail.value = true;
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value) return;
  const valid = await typeRef.value.validate().catch(() => false);
  if (
    !valid ||
    saving.value ||
    submittingVersion !== versionId.value ||
    !open.value
  )
    return;
  saving.value = true;
  try {
    const payload = {
      versionId: submittingVersion,
      enumName: form.value.enumName.trim(),
      enumType: form.value.enumType.trim(),
      status: form.value.status,
      remark: (form.value.remark || "").trim(),
    };
    if (form.value.enumId != null) {
      payload.enumId = form.value.enumId;
      await updateType(payload);
    } else {
      await addType(payload);
    }
    if (submittingVersion !== versionId.value) return;
    proxy.$modal.msgSuccess(
      form.value.enumId != null ? "修改成功" : "新增成功"
    );
    open.value = false;
    refreshList();
  } finally {
    saving.value = false;
  }
}

/**
 * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
 * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
 */
async function handleDelete(row) {
  const context = contextRequest;
  const deletingVersion = versionId.value;
  const recordIds = row ? [row.enumId] : [...ids.value];
  if (!recordIds.length || deleting.value) return;
  try {
    await proxy.$modal.confirm(
      '是否确认删除枚举编号为"' + recordIds.join(",") + '"的数据项？'
    );
  } catch {
    return;
  }
  if (
    deleting.value ||
    context !== contextRequest ||
    deletingVersion !== versionId.value
  )
    return;
  deleting.value = true;
  try {
    await delType(recordIds.join(","));
    if (context !== contextRequest || deletingVersion !== versionId.value)
      return;
    refreshList();
    proxy.$modal.msgSuccess("删除成功");
  } finally {
    deleting.value = false;
  }
}

/** 打开导入弹窗。 */
function handleImport() {
  upload.open = true;
}

/** 下载导入模板。 */
function importTemplate() {
  proxy.download("om/type/importTemplate", {}, "枚举类型模板.xlsx");
}

/** 上传开始前标记导入中状态。 */
function handleFileUploadProgress() {
  upload.isUploading = true;
  return true;
}

/** 导入失败时释放上传中状态并提示。 */
function handleFileError() {
  upload.isUploading = false;
  proxy.$modal.msgError("导入失败，请重试");
}

/**
 * 处理导入接口响应，失败时保留弹窗便于重试。
 * @param {Object} response 导入接口返回体
 */
function handleFileSuccess(response) {
  upload.isUploading = false;
  if (response.code !== 200) {
    proxy.$modal.msgError(response.msg || "导入失败");
    return;
  }
  upload.open = false;
  uploadRef.value?.clearFiles();
  proxy.$modal.msgSuccess(response.msg || "导入成功");
  refreshList();
}

/** 触发已选文件的上传提交。 */
function submitFileForm() {
  uploadRef.value?.submit();
}

// 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
watch(versionId, async () => {
  ++contextRequest;
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  upload.open = false;
  handleSelectionChange([]);
  tableStore.params.enumName = null;
  tableStore.params.enumType = null;
  tableStore.params.status = null;
  await nextTick();
  tableRef.value?.resetQuery();
});
onBeforeUnmount(() => {
  ++contextRequest;
});
</script>

<style scoped lang="scss">
.enum-detail-form {
  padding: 0;
}

.form-readonly {
  box-sizing: border-box;
  width: 100%;
  border: 1px solid #f1f1f1;
  padding: 0 10px;
  min-height: 34px;
  background-color: #fcfcfc;
  border-radius: 2px;
  color: #333;
  display: flex;
  align-items: center;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-all;
}

.form-readonly.textarea {
  min-height: 85px;
  display: block;
}

.enum-page :deep(.enum-detail-dialog .el-form-item) {
  margin-bottom: 16px;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__header) {
  padding: 9px 20px;
  background: #f8f8f8;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__body) {
  padding: 20px 40px !important;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #f1f1f1;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 12px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .enum-page :deep(.enum-detail-dialog .el-dialog__body) {
    padding: 20px 16px !important;
  }
}

.enum-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.enum-page :deep(.el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 12px;
}

.character-count :deep(.el-input__count)::after {
  content: " 个字符";
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}

.enum-page :deep(.el-input-number) {
  width: 100%;
}
</style>
