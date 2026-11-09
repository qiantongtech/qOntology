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
  <div ref="pageRef" class="app-container term-page">
    <GuideTip tip-id="om/semantic/term.list" />
    <qt-wrap :columns="tableColumns" :table-ref="listActions">
      <template #search>
        <qt-search-bar
          :items="searchItems"
          :params="queryParams"
          @query="handleQuery"
          @reset="handleSearchReset"
        />
      </template>
      <template #actions-data>
        <el-button
          v-hasPermi="['om:term:term:add']"
          type="primary"
          plain
          @click="handleAdd"
          ><i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:term:term:remove']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting"
          @click="handleDelete()"
          ><i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>
      <div v-loading="loading">
        <qt-table
          ref="tableRef"
          :columns="tableColumns"
          :config="tableConfig"
          :events="tableEvents"
          :func="readTableRows"
        >
          <template #termInfo="{ row }">
            <TermInfoCell :row="row" />
          </template>
          <template #alias="{ row }">{{ row.alias || "-" }}</template>
          <template #abbr="{ row }">{{ row.abbr || "-" }}</template>
          <template #source="{ row }">{{ row.source || "-" }}</template>
          <template #status="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :loading="statusSaving[row.id] === true"
              :disabled="
                !auth.hasPermi('om:term:term:edit') ||
                statusSaving[row.id] === true
              "
              :aria-label="`${row.stdName}状态`"
              @change="(value) => handleStatusChange(row, value)"
            />
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
          <template #createTime="scope">{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
          }}</template>
          <template #action="{ row }">
            <el-button
              v-hasPermi="['om:term:term:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              v-hasPermi="['om:term:term:edit']"
              link
              type="primary"
              icon="Edit"
              :disabled="Number(row.status) === 1"
              @click="handleUpdate(row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['om:term:term:remove']"
              link
              type="danger"
              icon="Delete"
              :disabled="deleting || Number(row.status) === 1"
              @click="handleDelete(row)"
              >删除
            </el-button>
          </template>
        </qt-table>
      </div>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </qt-wrap>
    <el-dialog
      v-model="open"
      :title="dialogTitle"
      width="800px"
      :append-to="pageRef"
      :close-on-press-escape="!saving"
      :before-close="handleBeforeClose"
      draggable
      @closed="clearValidation"
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="维护标准术语、英文名称和业务释义，统一业务概念的表达，为本体对象与属性提供一致的命名依据。"
      />
      <el-form
        ref="termRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
        @submit.prevent="submitForm"
      >
        <qt-form-item
          class="row-full"

          label="术语名称"
          prop="stdName"
        >
          <el-input
            v-model="form.stdName"
            placeholder="请输入术语名称"
            maxlength="256个字符"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="英文名称"
          prop="enName"
        >
          <el-input
            v-model="form.enName"
            :formatter="formatEnglishName"
            :parser="formatEnglishName"
            placeholder="请输入英文名称，以英文字母开头，后续支持英文字母、数字和下划线"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="术语别名"
          prop="alias"
        >
          <el-input
            v-model="form.alias"
            placeholder="请输入术语别名，多个别名用逗号（，）分隔"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="业务缩写"
          prop="abbr"
        >
          <el-input
            v-model="form.abbr"
            placeholder="请输入业务缩写"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="form.status" :disabled="saving">
            <el-radio
              v-for="dict in project_status"
              :key="Number(dict.value)"
              :value="Number(dict.value)"
            >
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="来源"
          prop="source"
        >
          <el-input
            v-model="form.source"
            class="term-character-count"
            type="textarea"
            :rows="3"
            placeholder="请输入来源"
            maxlength="256个字符"
            show-word-limit
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="描述"
          prop="description"
        >
          <el-input
            v-model="form.description"
            class="term-character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            maxlength="256个字符"
            show-word-limit
          />
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="detailOpen"
      class="term-detail-dialog"
      title="术语详情"
      width="800px"
      :append-to="pageRef"
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
              <div v-if="field.prop === 'alias'" class="form-readonly">
                {{ detailAliases.join("，") || "-" }}
              </div>
              <dict-tag
                v-else-if="field.prop === 'status'"
                :options="project_status"
                :value="detail.status"
              />
              <div
                v-else-if="field.prop === 'createTime'"
                class="form-readonly"
              >
                {{ parseTime(detail.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
              </div>
              <div
                v-else-if="field.prop === 'creatorPhone'"
                class="form-readonly"
              >
                {{ creatorPhones[detail.creatorId] || "-" }}
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
import TermInfoCell from "./TermInfoCell.vue";
import { onBeforeUnmount, computed, nextTick, watch, reactive, ref } from "vue";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  listTerm,
  getTerm,
  addTerm,
  updateTerm,
  updateTermStatus,
  delTerm,
} from "@/api/om/term/term";

import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

defineOptions({ name: "OmTerm" });
const { creatorPhones } = useCreatorPhones();
const statusSaving = reactive({});
const { versionId, requireVersion } = useOntologyVersion();
const { proxy } = getCurrentInstance();
const { project_status } = proxy.useDict("project_status");
const pageRef = ref();
const termRef = ref();
const tableRef = ref();
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const open = ref(false);
const detailOpen = ref(false);
const dialogTitle = ref("新增术语");
const termList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const selectedRows = ref([]);
const detail = ref({});
const defaultSort = ref({ prop: "createTime", order: "descending" });
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  stdName: "",
  enName: "",
  status: null,
  orderByColumn: defaultSort.value.prop,
  isAsc: defaultSort.value.order,
});
const appliedFilters = reactive({ stdName: "", enName: "", status: null });
const detailFields = [
  { prop: "stdName", label: "术语名称" },
  { prop: "enName", label: "英文名称" },
  { prop: "alias", label: "术语别名" },
  { prop: "abbr", label: "业务缩写" },
  { prop: "status", label: "状态" },
  { prop: "source", label: "来源" },
  { prop: "description", label: "描述" },
  { prop: "createBy", label: "创建人" },
  { prop: "creatorPhone", label: "联系方式" },
  { prop: "createTime", label: "创建时间" },
];

const emptyForm = () => ({
  id: undefined,
  versionId: versionId.value,
  status: 1,
  stdName: "",
  enName: "",
  alias: "",
  abbr: "",
  source: "",
  description: "",
});
const form = ref(emptyForm());
const detailAliases = computed(() => parseAliases(detail.value.alias));

function formatEnglishName(value) {
  return String(value ?? "")
    .replace(/[^A-Za-z0-9_]/g, "")
    .replace(/^[^A-Za-z]+/, "");
}

const rules = {
  status: [
    {
      required: true,
      type: "number",
      enum: [0, 1],
      message: "请选择状态",
      trigger: "change",
    },
  ],
  stdName: [
    {
      required: true,
      whitespace: true,
      message: "术语名称不能为空",
      trigger: "blur",
    },
    { max: 256, message: "术语名称长度不能超过256个字符", trigger: "blur" },
  ],
  enName: [
    {
      required: true,
      whitespace: true,
      message: "英文名称不能为空",
      trigger: "blur",
    },
    { max: 256, message: "英文名称长度不能超过256个字符", trigger: "blur" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "英文名称须以英文字母开头，后续仅支持英文字母、数字和下划线",
      trigger: ["blur", "change"],
    },
  ],
  alias: [
    {
      required: true,
      whitespace: true,
      message: "术语别名不能为空",
      trigger: "blur",
    },
    { max: 1024, message: "术语别名长度不能超过1024个字符", trigger: "blur" },
  ],
  abbr: [
    {
      required: true,
      whitespace: true,
      message: "业务缩写不能为空",
      trigger: "blur",
    },
    { max: 256, message: "业务缩写长度不能超过256个字符", trigger: "blur" },
  ],
  source: [
    {
      required: true,
      whitespace: true,
      message: "来源不能为空",
      trigger: "blur",
    },
    { max: 256, message: "来源长度不能超过256个字符", trigger: "blur" },
  ],
  description: [
    { max: 256, message: "描述长度不能超过256个字符", trigger: "blur" },
  ],
};
// 列表数据由原有请求逻辑管理，保留版本隔离、请求顺序及删除后的分页处理。
const tableColumns = reactive([
  {
    type: "selection",
    width: 55,
    align: "center",
  },
  {
    label: "编号",
    prop: "id",
    width: 80,
    align: "center",
    sortable: true,
  },
  {
    label: "术语信息",
    minWidth: 320,
    align: "left",
    slot: "termInfo",
  },
  {
    label: "术语别名",
    prop: "alias",
    minWidth: 160,
    align: "left",
    showOverflowTooltip: { effect: "light" },
    slot: "alias",
  },
  {
    label: "业务缩写",
    prop: "abbr",
    minWidth: 120,
    align: "left",
    showOverflowTooltip: { effect: "light" },
    slot: "abbr",
  },
  {
    label: "来源",
    prop: "source",
    minWidth: 180,
    align: "left",
    showOverflowTooltip: { effect: "light" },
    slot: "source",
  },
  { label: "状态", prop: "status", slot: "status", width: 90, align: "center" },
  {
    label: "创建人",
    prop: "createBy",
    slot: "creator",
    width: 130,
    align: "left",
    headerAlign: "left",
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "创建时间",
    prop: "createTime",
    width: 150,
    align: "center",
    sortable: true,
    slot: "createTime",
  },
  {
    label: "操作",
    width: 240,
    align: "center",
    fixed: "right",
    className: "small-padding fixed-width",
    slot: "action",
  },
]);
const searchItems = computed(() => [
  { label: "术语名称", prop: "stdName", component: { is: "input" } },
  { label: "英文名称", prop: "enName", component: { is: "input" } },
  {
    label: "状态",
    prop: "status",
    component: { is: "select", options: project_status.value },
  },
]);
const listActions = { getList };
const tableConfig = {
  notPagination: true,
  sort: true,
  table: {
    stripe: true,
    rowKey: "id",
    defaultSort: defaultSort.value,
    onSelectionChange: handleSelectionChange,
  },
};
const tableEvents = {
  onSortChange(params) {
    handleSortChange({ prop: params.orderByColumn, order: params.isAsc });
  },
};
function readTableRows() {
  return Promise.resolve({ data: termList.value });
}
watch(termList, () => tableRef.value?.getList());
function handleSearchReset() {
  handleQuery();
}

let listRequest = 0;
let contextRequest = 0;
let detailRequest = 0;

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

function confirmAlias() {
  const values = (form.value.alias || "").split(/[,，]/).map((value) => value.trim());
  if (!values.length || values.some((value) => !value)) {
    ElMessage.warning("术语别名每一项都不能为空，请完善后提交");
    return false;
  }
  if (new Set(values).size !== values.length) {
    ElMessage.warning("术语别名不能重复，请检查后提交");
    return false;
  }
  const value = values.join("，");
  if (value.length > 1024) {
    ElMessage.warning("术语别名总长度不能超过1024个字符");
    return false;
  }
  form.value.alias = value;
  return true;
}

async function handleStatusChange(row, value) {
  const previous = value === 1 ? 0 : 1;
  const submittingVersion = versionId.value;
  const text = value === 1 ? "启用" : "禁用";
  statusSaving[row.id] = true;
  try {
    await ElMessageBox.confirm(
      `确认要${text}术语“${row.stdName}”吗？`,
      "状态确认",
      { type: "warning" }
    );
    if (submittingVersion !== versionId.value) {
      row.status = previous;
      return;
    }
    await updateTermStatus({
      id: row.id,
      versionId: submittingVersion,
      status: value,
    });
    if (submittingVersion !== versionId.value) return;
    ElMessage.success("操作成功");
    await getList();
  } catch {
    row.status = previous;
  } finally {
    delete statusSaving[row.id];
  }
}

async function getList() {
  const requestId = ++listRequest;
  if (!versionId.value) {
    termList.value = [];
    total.value = 0;
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const response = await listTerm({
      ...queryParams,
      ...appliedFilters,
      versionId: versionId.value,
    });
    if (requestId !== listRequest) return;
    termList.value = response.data.rows;
    total.value = response.data.total;
    selectedIds.value = [];
    selectedRows.value = [];
    tableRef.value?.elTableRef?.clearSelection();
  } finally {
    if (requestId === listRequest) loading.value = false;
  }
}

function handleSortChange({ prop, order }) {
  queryParams.orderByColumn = order ? prop : defaultSort.value.prop;
  queryParams.isAsc = order || defaultSort.value.order;
  queryParams.pageNum = 1;
  getList();
}

function handleQuery() {
  appliedFilters.stdName = queryParams.stdName.trim();
  appliedFilters.enName = queryParams.enName.trim();
  appliedFilters.status = queryParams.status === "" ? null : queryParams.status;
  queryParams.pageNum = 1;
  getList();
}

function handleSelectionChange(selection) {
  selectedRows.value = selection;
  selectedIds.value = selection.map((row) => row.id);
}

function clearValidation() {
  termRef.value?.clearValidate();
}

async function handleAdd() {
  if (!requireVersion()) return;
  ++detailRequest;
  form.value = emptyForm();
  dialogTitle.value = "新增术语";
  open.value = true;
  await nextTick();
  clearValidation();
}

async function loadTerm(row, isDetail) {
  const requestId = ++detailRequest;
  const response = await getTerm(row.id);
  if (requestId !== detailRequest) return;
  if (isDetail) {
    detail.value = response.data;
    detailOpen.value = true;
  } else {
    form.value = { ...emptyForm(), ...response.data, alias: parseAliases(response.data.alias).join("，") };
    dialogTitle.value = "修改术语";
    open.value = true;
    await nextTick();
    clearValidation();
  }
}

function handleUpdate(row) {
  return loadTerm(row, false);
}

function handleDetail(row) {
  return loadTerm(row, true);
}

function handleBeforeClose(done) {
  if (!saving.value) done();
}

function cancel() {
  if (!saving.value) open.value = false;
}

async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value) return;
  if (!confirmAlias()) return;
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
      status: form.value.status,
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
    await getList();
  } finally {
    saving.value = false;
  }
}

/** 术语是否可删除：启用中（status=1）不允许删除，需先停用。 */
function canDeleteTerm(row) {
  return !!row && Number(row.status) === 0;
}

async function handleDelete(row) {
  const context = contextRequest;
  const deletingVersion = versionId.value;
  if (deleting.value) return;

  let ids;
  let confirmText;
  if (row) {
    // 单条删除：启用中的术语直接拦截。
    if (!canDeleteTerm(row)) {
      ElMessage.warning("启用中的术语不允许删除，请先停用后再删除");
      return;
    }
    ids = [row.id];
    confirmText = `是否确认删除编号为${row.id} 的数据项？`;
  } else {
    // 批量删除：仅删除停用（可删）部分，启用中的计入不可删除；即使可删数为 0 也统一弹“可删除X/不可删除Y”确认框。
    const canDeleteRows = selectedRows.value.filter(canDeleteTerm);
    const cannotDeleteCount = selectedRows.value.length - canDeleteRows.length;
    ids = canDeleteRows.map((item) => item.id);
    confirmText = cannotDeleteCount
      ? `可删除${canDeleteRows.length}个，不可删除${cannotDeleteCount}个，是否删除可删除部分？`
      : `是否确认删除选中的${canDeleteRows.length}个术语？`;
  }

  try {
    await proxy.$modal.confirm(confirmText);
  } catch {
    return;
  }
  // 确认后可删部分为空（如全部启用）时不再发起删除请求。
  if (!ids.length) return;
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
    if (queryParams.pageNum > 1 && termList.value.length <= ids.length)
      queryParams.pageNum -= 1;
    await getList();
  } finally {
    deleting.value = false;
  }
}

watch(
  versionId,
  () => {
    ++contextRequest;
    ++detailRequest;
    open.value = false;
    detailOpen.value = false;
    selectedIds.value = [];
    selectedRows.value = [];
    termList.value = [];
    queryParams.pageNum = 1;
    getList();
  },
  { immediate: true }
);
onBeforeUnmount(() => {
  ++contextRequest;
});
</script>

<style scoped lang="scss">
.term-detail-form {
  padding: 0;
}

.term-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.term-character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}
</style>
