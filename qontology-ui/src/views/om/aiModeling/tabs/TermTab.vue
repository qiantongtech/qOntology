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
  <div ref="pageRef" class="candidate-tab-page">
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="wrapConfig"
    >
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          :disabled="!selectedIds.length"
          @click="adoptBatch"
          @mousedown="(e) => e.preventDefault()"
        >
          <template #icon>
            <svg-icon icon-class="adopt" />
          </template>
          批量采纳
        </el-button>
        <el-button
          type="danger"
          icon="Delete"
          plain
          :loading="batchDeleteLoading"
          :disabled="!selectedIds.length || deleting"
          @click="remove()"
          @mousedown="(e) => e.preventDefault()"
        >
          批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 术语信息与外部 term/index 一致：主行“名称（英文名）”，副行描述。 -->
        <template #termInfo="{ row }">
          <div class="qt-table-info">
            <div class="qt-table-info__main">
              <span
                class="info-title"
                :title="`${row.stdName || '-'}（${row.enName || '-'}）`"
              >{{ row.stdName || "-" }}（{{ row.enName || "-" }}）</span>
            </div>
            <div class="info-desc" :title="row.description">
              {{ row.description || "-" }}
            </div>
          </div>
        </template>
        <template #alias="{ row }">{{ row.alias || "-" }}</template>
        <template #abbr="{ row }">{{ row.abbr || "-" }}</template>
        <template #source="{ row }">{{ row.source || "-" }}</template>
        <template #createTime="{ row }">{{
          proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "—"
        }}</template>
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
        <template #adoptStatus="{ row }">
          <dict-tag :options="om_ai_adopt_status" :value="row.adoptStatus" />
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            :disabled="row.adoptStatus === 1"
            @click="adoptOne(row)"
            >
            <template #icon>
              <svg-icon icon-class="adopt" />
            </template>
            采纳
          </el-button>
          <el-button
            link
            type="primary"
            icon="Document"
            @click="showEvidence(row)"
            >来源</el-button
          >
          <el-popover
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              link
              type="primary"
              icon="View"
              @click="openDetail(row)"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="edit(row)"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              :loading="deleteLoading[row.id]"
              :disabled="deleting"
              @click="remove(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 修改术语对话框：字段、提示语与多行别名录入对齐外部 term/index。 -->
    <el-dialog
      v-model="open"
      title="修改术语"
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
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
        @submit.prevent="save"
      >
        <qt-form-item
          class="row-full"

          label="术语名称"
          prop="stdName"
        >
          <el-input v-model="form.stdName" placeholder="请输入术语名称" :maxlength="256" />
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
            :maxlength="256"
          />
        </qt-form-item>
        <qt-form-item
          class="row-full"
          label="术语别名"
          prop="alias"
        >
          <div class="alias-list">
            <div v-for="(item, index) in aliasRows" :key="item.id" class="alias-row">
              <el-input
                v-model="item.value"
                placeholder="请输入术语别名"
                :maxlength="1024"
                :disabled="saving"
                @input="syncAliases"
                @keydown.enter.stop.prevent="addAliasRow"
              />
            </div>
          </div>
        </qt-form-item>
        <qt-form-item
          class="row-full"
          label="业务缩写"
          prop="abbr"
        >
          <el-input v-model="form.abbr" placeholder="请输入业务缩写" :maxlength="256" />
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
            :maxlength="256"
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
            :maxlength="256"
            show-word-limit
          />
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" :disabled="saving" @click="open = false">取 消</el-button>
          <el-button type="primary" size="mini" :loading="saving" @click="save">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 术语详情对话框：字段顺序与标签对齐外部 term/index（候选无状态项）。 -->
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
          <el-col v-for="field in fields" :key="field.prop" :span="24">
            <el-form-item :label="field.label" :prop="field.prop">
              <div v-if="field.prop === 'alias'" class="form-readonly">
                {{ detailAliases.join(", ") || "-" }}
              </div>
              <div
                v-else-if="field.prop === 'createTime'"
                class="form-readonly"
              >
                {{ proxy.parseTime(detail.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
              </div>
              <div v-else-if="field.prop === 'createBy'" class="form-readonly">
                {{ detail.createBy || "-" }}
              </div>
              <div v-else-if="field.prop === 'contact'" class="form-readonly">
                {{ (detail.creatorId != null && creatorPhones[String(detail.creatorId)]) ? creatorPhones[String(detail.creatorId)] : "-" }}
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
        <el-button @click="detailOpen = false">关 闭</el-button>
      </template>
    </el-dialog>

    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import { updateCandidate } from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status } = proxy.useDict("om_ai_adopt_status");

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const detailOpen = ref(false);
const saving = ref(false);
const form = ref({});
const detail = ref({});

/**
 * 收集采纳前缺失的必填项名称，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
function required(row) {
  return [
    ["stdName", "名称"],
    ["enName", "英文名称"],
    ["alias", "别名"],
    ["abbr", "缩写"],
    ["source", "来源"],
  ]
    .filter(([key]) => !row[key]?.trim())
    .map(([, label]) => label);
}

const tab = useCandidateTab("term", props.taskId, required);
const {
  tableRef,
  fetchRows,
  selectedIds,
  evidenceVisible,
  evidences,
  refresh,
  select: handleSelectionChange,
  adoptOne,
  adoptBatch,
  deleting,
  deleteLoading,
  batchDeleteLoading,
  remove,
  showEvidence,
} = tab;

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 搜索区由 QtWrap 管理，复用操作栏的搜索显隐按钮。
 */
// 候选列表搜索：字段与接口契约一致，查询和重置由 QtSearchBar 回到第一页。
const searchStore = reactive({
  items: [
    { label: "术语名称", prop: "stdName", component: { is: "input" } },
    { label: "采纳状态", prop: "adoptStatus", component: { is: "select", options: om_ai_adopt_status } },
  ],
});

const wrapConfig = {
  fullContent: false,
  searchShadow: false,
};

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  params: { stdName: null, adoptStatus: null },
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
      align: "center",
      selectable: () => true,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 80,
      minWidth: 80,
      align: "center",
      fixed: "left",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "术语信息",
      slot: "termInfo",
      minWidth: 320,
      align: "left",
    },
    {
      label: "术语别名",
      prop: "alias",
      slot: "alias",
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "业务缩写",
      prop: "abbr",
      slot: "abbr",
      minWidth: 120,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "来源",
      prop: "source",
      slot: "source",
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      slot: "adoptStatus",
      width: 110,
      minWidth: 110,
      align: "center",
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
      slot: "createTime",
      width: 150,
      minWidth: 150,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 240,
      minWidth: 240,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
});

const rules = {
  stdName: [
    { required: true, whitespace: true, message: "术语名称不能为空", trigger: "blur" },
    { max: 256, message: "术语名称长度不能超过256个字符", trigger: "blur" },
  ],
  enName: [
    { required: true, whitespace: true, message: "英文名称不能为空", trigger: "blur" },
    { max: 256, message: "英文名称长度不能超过256个字符", trigger: "blur" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "英文名称须以英文字母开头，后续仅支持英文字母、数字和下划线",
      trigger: ["blur", "change"],
    },
  ],
  alias: [
    { required: true, whitespace: true, message: "术语别名不能为空", trigger: "blur" },
    { max: 1024, message: "术语别名长度不能超过1024个字符", trigger: "blur" },
  ],
  abbr: [
    { required: true, whitespace: true, message: "业务缩写不能为空", trigger: "blur" },
    { max: 256, message: "业务缩写长度不能超过256个字符", trigger: "blur" },
  ],
  source: [
    { required: true, whitespace: true, message: "来源不能为空", trigger: "blur" },
    { max: 256, message: "来源长度不能超过256个字符", trigger: "blur" },
  ],
  description: [
    { max: 256, message: "描述长度不能超过256个字符", trigger: "blur" },
  ],
};
const fields = [
  { prop: "stdName", label: "术语名称" },
  { prop: "enName", label: "英文名称" },
  { prop: "alias", label: "术语别名" },
  { prop: "abbr", label: "业务缩写" },
  { prop: "source", label: "来源" },
  { prop: "description", label: "描述" },
  { prop: "createBy", label: "创建人" },
  { prop: "contact", label: "联系方式" },
  { prop: "createTime", label: "创建时间" },
];

/**
 * 拆分别名字符串，兼容中英文逗号并去重去空。
 * @param {string} value 原始别名文本
 * @returns {Array<string>} 别名列表
 */
function parseAliases(value) {
  return [
    ...new Set(
      (value || "")
        .split(/[,，]/)
        .map((item) => item.trim())
        .filter(Boolean),
    ),
  ];
}
const detailAliases = computed(() => parseAliases(detail.value.alias));

// 编辑草稿按行维护别名，接口仍沿用逗号分隔的 alias 字段。
let nextAliasId = 0;
const aliasRows = ref([{ id: nextAliasId++, value: "" }]);

/** 英文名称仅保留字母、数字和下划线，且必须以字母开头。 */
function formatEnglishName(value) {
  return String(value ?? "")
    .replace(/[^A-Za-z0-9_]/g, "")
    .replace(/^[^A-Za-z]+/, "");
}

/** 依据当前表单别名重设多行别名输入。 */
function resetAliasInput() {
  const values = parseAliases(form.value.alias);
  aliasRows.value = (values.length ? values : [""]).map((value) => ({
    id: nextAliasId++,
    value,
  }));
}

/** 将各别名列合并写回表单字符串。 */
function syncAliases() {
  form.value.alias = aliasRows.value
    .map((item) => item.value.trim())
    .filter(Boolean)
    .join("，");
}

/** 追加一行别名输入。 */
function addAliasRow() {
  if (saving.value) return;
  aliasRows.value.push({ id: nextAliasId++, value: "" });
}

/**
 * 删除指定行别名，至少保留一行。
 * @param {number} index 待删除行下标
 */
function removeAliasRow(index) {
  if (saving.value) return;
  aliasRows.value.splice(index, 1);
  if (!aliasRows.value.length)
    aliasRows.value.push({ id: nextAliasId++, value: "" });
  syncAliases();
  formRef.value?.validateField("alias").catch(() => {});
}

/** 校验并合并别名，写回表单；不通过时给出提示。 */
function confirmAlias() {
  const values = aliasRows.value.flatMap((item) =>
    item.value.split(/[,，]/).map((value) => value.trim()),
  );

  if (new Set(values).size !== values.length) {
    proxy.$modal.msgWarning("术语别名不能重复，请检查后提交");
    return false;
  }
  const value = values.join("，");
  if (value.length > 1024) {
    proxy.$modal.msgWarning("术语别名总长度不能超过1024个字符");
    return false;
  }
  form.value.alias = value;
  return true;
}

/** 弹窗关闭动画结束后清理校验与别名草稿。 */
function clearValidation() {
  formRef.value?.clearValidate();
  resetAliasInput();
}

/** 保存中禁止关闭弹窗。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/**
 * 打开详情弹窗。
 * @param {Object} row 当前行数据
 */
function openDetail(row) {
  detail.value = row;
  detailOpen.value = true;
}

/**
 * 打开修改弹窗，并按当前别名重设多行输入。
 * @param {Object} row 当前行数据
 */
function edit(row) {
  form.value = { ...row };
  resetAliasInput();
  open.value = true;
  nextTick(() => formRef.value?.clearValidate());
}

/** 提交修改，先校验别名再提交，提交期间锁定确定按钮避免重复入库。 */
async function save() {
  if (saving.value) return;
  if (!confirmAlias()) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    await updateCandidate("term", {
      id: form.value.id,
      taskId: props.taskId,
      stdName: (form.value.stdName ?? "").trim(),
      enName: (form.value.enName ?? "").trim(),
      alias: (form.value.alias ?? "").trim(),
      abbr: (form.value.abbr ?? "").trim(),
      source: (form.value.source ?? "").trim(),
      description: (form.value.description ?? "").trim(),
    });
    proxy.$modal.msgSuccess("修改成功");
    open.value = false;
    refresh();
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped lang="scss">
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

.alias-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
}

.alias-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.alias-row .el-input {
  flex: 1;
  min-width: 0;
}

.alias-row .el-button {
  flex-shrink: 0;
  margin-left: 0;
  padding: 8px 10px;
}

.term-detail-form {
  padding: 0;
}

.term-character-count :deep(.el-input__count)::after {
  content: " 个字符";
}

.term-character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}
</style>
