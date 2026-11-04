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
  <div class="app-container term-page" ref="app-container">
    <GuideTip tip-id="om/semantic/term.list" />
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
      </template>

      <template #actions-data>
        <el-button
          v-hasPermi="['om:term:term:add']"
          type="primary"
          plain
          @click="handleAdd"
          @mousedown="(e) => e.preventDefault()"
          ><i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:term:term:remove']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting"
          @click="handleDelete()"
          @mousedown="(e) => e.preventDefault()"
          ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['om:term:term:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情</el-button
          >
          <el-popover
            v-if="auth.hasPermiOr(['om:term:term:edit', 'om:term:term:remove'])"
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              v-if="auth.hasPermi('om:term:term:edit')"
              link
              type="primary"
              icon="Edit"
              :disabled="saving"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('om:term:term:remove')"
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
      :title="dialogTitle"
      width="800px"
      :append-to="$refs['app-container']"
      :close-on-press-escape="!saving"
      :before-close="handleBeforeClose"
      draggable
      @closed="clearValidation"
    >
      <el-form
        ref="termRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        @submit.prevent="submitForm"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="stdName">
              <el-input
                v-model="form.stdName"
                placeholder="请输入名称"
                maxlength="256个字符"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="英文名称" prop="enName">
              <el-input
                v-model="form.enName"
                placeholder="请输入英文名称"
                maxlength="256个字符"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="别名" prop="alias">
              <div class="flex gap-2 alias-tags">
                <el-tag
                  v-for="alias in aliasTags"
                  :key="alias"
                  :title="alias"
                  closable
                  :disable-transitions="false"
                  @close="removeAlias(alias)"
                >
                  {{ alias }}
                </el-tag>
                <el-input
                  v-if="aliasInputVisible"
                  ref="aliasInputRef"
                  v-model="aliasInputValue"
                  class="w-20 alias-input"
                  size="small"
                  placeholder="请输入别名"
                  :maxlength="1024"
                  :disabled="saving"
                  @keydown.enter.stop.prevent="confirmAlias"
                  @blur="confirmAlias"
                />
                <el-button
                  v-else
                  class="button-new-tag"
                  size="small"
                  :disabled="saving"
                  @click="showAliasInput"
                >
                  + 添加别名
                </el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="缩写" prop="abbr">
              <el-input
                v-model="form.abbr"
                placeholder="请输入缩写"
                maxlength="256个字符"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="来源" prop="source">
              <el-input
                v-model="form.source"
                class="term-character-count"
                type="textarea"
                :rows="3"
                placeholder="请输入来源"
                :maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                class="term-character-count"
                type="textarea"
                :rows="4"
                placeholder="请输入描述"
                :maxlength="500"
                show-word-limit
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
      v-model="detailOpen"
      class="term-detail-dialog"
      title="术语详情"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <el-form
        class="term-detail-form"
        :model="detail"
        label-width="90px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col v-for="field in detailFields" :key="field.prop" :span="24">
            <el-form-item :label="field.label" :prop="field.prop">
              <div
                v-if="field.prop === 'alias'"
                class="form-readonly alias-tags"
              >
                <el-tag
                  v-for="alias in detailAliases"
                  :key="alias"
                  :title="alias"
                  >{{ alias }}</el-tag
                >
                <span v-if="!detailAliases.length">-</span>
              </div>
              <div
                v-else-if="field.prop === 'createTime'"
                class="form-readonly"
              >
                {{ parseTime(detail.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
              </div>
              <div
                v-else
                class="form-readonly"
                :class="{
                  textarea: ['source', 'description'].includes(field.prop),
                }"
              >
                {{ detail[field.prop] || "-" }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onBeforeUnmount, computed, nextTick, watch, reactive, ref } from "vue";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  listTerm,
  getTerm,
  addTerm,
  updateTerm,
  delTerm,
} from "@/api/om/term/term";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

defineOptions({ name: "OmTerm" });
const { versionId, requireVersion } = useOntologyVersion();
const termRef = ref();
const tableRef = ref();
const saving = ref(false);
const deleting = ref(false);
const open = ref(false);
const detailOpen = ref(false);
const dialogTitle = ref("新增术语");
const selectedIds = ref([]);
const detail = ref({});
const { creatorPhones } = useCreatorPhones();

/** 版本号缺失时直接返回空列表，避免无版本上下文发起无效查询。 */
function fetchTerms(params) {
  if (!versionId.value) {
    return Promise.resolve({ data: { rows: [], total: 0 } });
  }
  return listTerm({ ...params, versionId: versionId.value });
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
    { type: "selection", width: 55, minWidth: 55 },
    { label: "编号", prop: "id", slot: "text", width: 80, minWidth: 80, align: "left", sortable: true },
    { label: "名称", prop: "stdName", slot: "text", minWidth: 140, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "描述", prop: "description", slot: "text", minWidth: 240, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "英文名称", prop: "enName", slot: "text", minWidth: 150, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "别名", prop: "alias", slot: "text", minWidth: 160, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "缩写", prop: "abbr", slot: "text", minWidth: 120, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "来源", prop: "source", slot: "text", minWidth: 180, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "创建人", prop: "createBy", slot: "creator", width: 130, minWidth: 130, align: "left", headerAlign: "left" },
    { label: "创建时间", prop: "createTime", date: true, width: 160, minWidth: 160, align: "left", sortable: true },
    { label: "操作", slot: "handle", width: 170, minWidth: 170, fixed: "right" },
  ],
  func: fetchTerms,
  params: {
    stdName: null,
    enName: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "名称", prop: "stdName", component: { is: "input" } },
    { label: "英文名称", prop: "enName", component: { is: "input" } },
  ],
});
const detailFields = [
  { prop: "id", label: "编号" },
  { prop: "stdName", label: "名称" },
  { prop: "enName", label: "英文名称" },
  { prop: "alias", label: "别名" },
  { prop: "abbr", label: "缩写" },
  { prop: "source", label: "来源" },
  { prop: "description", label: "描述" },
  { prop: "createBy", label: "创建人" },
  { prop: "createTime", label: "创建时间" },
];

const emptyForm = () => ({
  id: undefined,
  versionId: versionId.value,
  stdName: "",
  enName: "",
  alias: "",
  abbr: "",
  source: "",
  description: "",
});
const form = ref(emptyForm());
const aliasInputRef = ref();
const aliasInputVisible = ref(false);
const aliasInputValue = ref("");
const aliasTags = computed(() => parseAliases(form.value.alias));
const detailAliases = computed(() => parseAliases(detail.value.alias));
const rules = {
  stdName: [
    {
      required: true,
      whitespace: true,
      message: "名称不能为空",
      trigger: "blur",
    },
    { max: 256, message: "名称长度不能超过256个字符", trigger: "blur" },
  ],
  enName: [
    {
      required: true,
      whitespace: true,
      message: "英文名称不能为空",
      trigger: "blur",
    },
    { max: 256, message: "英文名称长度不能超过256个字符", trigger: "blur" },
  ],
  alias: [
    {
      required: true,
      whitespace: true,
      message: "别名不能为空",
      trigger: "blur",
    },
    { max: 1024, message: "别名长度不能超过1024个字符", trigger: "blur" },
  ],
  abbr: [
    {
      required: true,
      whitespace: true,
      message: "缩写不能为空",
      trigger: "blur",
    },
    { max: 256, message: "缩写长度不能超过256个字符", trigger: "blur" },
  ],
  source: [
    {
      required: true,
      whitespace: true,
      message: "来源不能为空",
      trigger: "blur",
    },
    { max: 200, message: "来源长度不能超过200个字符", trigger: "blur" },
  ],
  description: [
    { max: 500, message: "描述长度不能超过500个字符", trigger: "blur" },
  ],
};
let contextRequest = 0;
let detailRequest = 0;

/**
 * 将别名字符串按中英文逗号拆分为去重后的数组。
 * @param {string} [value] 别名原文
 * @returns {Array<string>} 去重后的别名列表
 */
function parseAliases(value) {
  return [
    ...new Set(
      (value || "")
        .split(/[,，]/)
        .map((alias) => alias.trim())
        .filter(Boolean)
    ),
  ];
}

/** 收起别名内联输入框并清空草稿。 */
function resetAliasInput() {
  aliasInputVisible.value = false;
  aliasInputValue.value = "";
}

/** 展示别名输入框并聚焦。 */
async function showAliasInput() {
  aliasInputVisible.value = true;
  await nextTick();
  aliasInputRef.value?.focus();
}

/**
 * 确认新增别名，超长按提示拦截。
 * @param {KeyboardEvent} [event] 回车事件，输入法组字中时不提交
 * @returns {boolean} 是否成功写入别名
 */
function confirmAlias(event) {
  if (event?.isComposing || saving.value) return false;
  const aliases = [
    ...new Set([...aliasTags.value, ...parseAliases(aliasInputValue.value)]),
  ];
  const value = aliases.join("，");
  if (value.length > 1024) {
    ElMessage.warning("别名总长度不能超过1024个字符");
    return false;
  }
  form.value.alias = value;
  resetAliasInput();
  termRef.value?.validateField("alias").catch(() => {});
  return true;
}

/**
 * 移除指定别名。
 * @param {string} alias 待移除的别名
 */
function removeAlias(alias) {
  if (saving.value) return;
  form.value.alias = aliasTags.value
    .filter((value) => value !== alias)
    .join("，");
  termRef.value?.validateField("alias").catch(() => {});
}

/** 刷新当前页列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/**
 * 多选框选中数据，供顶部批量删除使用。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map((row) => row.id);
}

/** 清除表单校验提示与别名输入态。 */
function clearValidation() {
  termRef.value?.clearValidate();
  resetAliasInput();
}

/** 新增按钮操作。 */
async function handleAdd() {
  if (!requireVersion()) return;
  ++detailRequest;
  form.value = emptyForm();
  dialogTitle.value = "新增术语";
  open.value = true;
  await nextTick();
  clearValidation();
}

/**
 * 拉取术语详情并打开对应弹窗。
 * @param {Object} row 当前行数据
 * @param {boolean} isDetail true 打开详情弹窗，false 打开修改弹窗
 */
async function loadTerm(row, isDetail) {
  const requestId = ++detailRequest;
  const response = await getTerm(row.id);
  if (requestId !== detailRequest) return;
  if (isDetail) {
    detail.value = response.data;
    detailOpen.value = true;
  } else {
    form.value = { ...emptyForm(), ...response.data };
    dialogTitle.value = "修改术语";
    open.value = true;
    await nextTick();
    clearValidation();
  }
}

/**
 * 修改按钮操作。
 * @param {Object} row 当前行数据
 */
function handleUpdate(row) {
  return loadTerm(row, false);
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
function handleDetail(row) {
  return loadTerm(row, true);
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
  if (!saving.value) open.value = false;
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value) return;
  if (aliasInputVisible.value && !confirmAlias()) return;
  const valid = await termRef.value.validate().catch(() => false);
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
      stdName: (form.value.stdName ?? "").trim(),
      enName: (form.value.enName ?? "").trim(),
      alias: (form.value.alias ?? "").trim(),
      abbr: (form.value.abbr ?? "").trim(),
      source: (form.value.source ?? "").trim(),
      description: (form.value.description ?? "").trim(),
    };
    if (form.value.id != null) {
      payload.id = form.value.id;
      await updateTerm(payload);
    } else {
      await addTerm(payload);
    }
    if (submittingVersion !== versionId.value) return;
    ElMessage.success(form.value.id != null ? "修改成功" : "新增成功");
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
  if (deleting.value) return;
  const ids = row ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  try {
    await ElMessageBox.confirm(
      row
        ? `确认删除术语“${row.stdName}”吗？`
        : `确认删除选中的 ${ids.length} 条术语吗？`,
      "删除确认",
      {
        type: "warning",
        confirmButtonText: "删除",
        cancelButtonText: "取消",
      }
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
    await delTerm(ids.join(","));
    if (context !== contextRequest || deletingVersion !== versionId.value)
      return;
    ElMessage.success("删除成功");
    refreshList();
  } finally {
    deleting.value = false;
  }
}

// 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
watch(versionId, async () => {
  ++contextRequest;
  ++detailRequest;
  open.value = false;
  detailOpen.value = false;
  selectedIds.value = [];
  tableStore.params.stdName = null;
  tableStore.params.enName = null;
  await nextTick();
  tableRef.value?.resetQuery();
});
onBeforeUnmount(() => {
  ++contextRequest;
});
</script>

<style scoped lang="scss">
.alias-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.alias-tags .el-tag {
  max-width: 100%;
}

.alias-tags :deep(.el-tag__content) {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

.alias-input {
  width: 160px;
}

.form-readonly.alias-tags {
  padding-top: 4px;
  padding-bottom: 4px;
}

.term-detail-form {
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

.term-page :deep(.term-detail-dialog .el-form-item) {
  margin-bottom: 16px;
}

.term-page :deep(.term-detail-dialog .el-dialog__header) {
  padding: 9px 20px;
  background: #f8f8f8;
}

.term-page :deep(.term-detail-dialog .el-dialog__body) {
  padding: 20px 40px !important;
}

.term-page :deep(.term-detail-dialog .el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #f1f1f1;
}

.term-page :deep(.term-detail-dialog .el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 12px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .term-page :deep(.term-detail-dialog .el-dialog__body) {
    padding: 20px 16px !important;
  }
}

.term-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.term-character-count :deep(.el-input__count)::after {
  content: " 个字符";
}

.term-character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}
</style>
