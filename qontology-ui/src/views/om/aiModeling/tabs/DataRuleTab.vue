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
  <div ref="pageRef" class="candidate-tab-page data-rule-page">
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
          row[column_data.prop] ?? "-"
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
        <template #type="{ row }">
          <dict-tag :options="om_data_rule_type" :value="row.type" />
        </template>
        <template #level="{ row }">
          <dict-tag :options="om_rule_level" :value="row.level" />
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
              @click="openForm(row, true)"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="openForm(row, false)"
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

    <el-dialog
      v-model="open"
      :title="readonly ? '数据规则详情' : '修改数据规则'"
      width="860px"
      :append-to="pageRef"
      draggable
      destroy-on-close
    >
      <guide-tip-panel
        v-if="!readonly"
        type="hint"
        class="mb15"
        allow-wrap
        title="面向属性取值维护数据校验规则，配置校验类型、约束级别与失败条件，保障业务数据符合预期格式与范围。"
      />
      <!-- 详情弹窗只读风格 -->
      <el-form v-if="readonly" label-width="120px" class="rule-form">
        <div class="h2-title">基本信息</div>
        <el-form-item v-for="field in basicDetailFields" :key="field.label" :label="field.label">
          <dict-tag v-if="field.dictOptions" :options="field.dictOptions" :value="field.dictValue" />
          <div v-else class="form-readonly" :class="{ textarea: field.multiline }">{{ field.value || '-' }}</div>
        </el-form-item>
        <div class="h2-title">执行条件</div>
        <el-form-item v-for="field in conditionDetailFields" :key="field.label" :label="field.label">
          <div class="form-readonly" :class="{ textarea: field.multiline }">{{ field.value || '-' }}</div>
        </el-form-item>
      </el-form>
      <!-- 修改弹窗可编辑风格 -->
      <el-form
        v-else
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="rule-form"
      >
        <div class="h2-title">基本信息</div>
        <qt-form-item label="规则名称" prop="name" required>
          <el-input
            v-model="form.name"
            placeholder="请输入规则名称"
            maxlength="128"
          />
        </qt-form-item>
        <qt-form-item label="类型" prop="type" required >
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option
              v-for="i in om_data_rule_type"
              :key="i.value"
              :label="i.label"
              :value="i.value"
            />
          </el-select>
        </qt-form-item>
        <qt-form-item label="级别" prop="level" required >
          <el-radio-group v-model="form.level">
            <el-radio v-for="i in om_rule_level" :key="i.value" :value="i.value">{{
              i.label
            }}</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="描述" prop="description" >
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            :maxlength="256"
            show-word-limit
          />
        </qt-form-item>
        <div class="h2-title">执行条件</div>
        <qt-form-item label="条件关系" prop="conditionRelation" required >
          <el-radio-group v-model="form.conditionRelation">
            <el-radio value="OR">任一满足（OR）</el-radio>
            <el-radio value="AND">全部满足（AND）</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="失败条件" required >
          <div class="conditions">
            <div v-for="(condition, index) in form.conditionItems" :key="condition.key" class="condition-row">
              <div class="condition-label">条件 {{ index + 1 }}：</div>
              <div class="condition-fields">
                <el-tree-select
                  v-model="condition.objectCandidateId"
                  :data="objectTree"
                  :props="treeProps"
                  value-key="id"
                  check-strictly
                  filterable
                  clearable
                  placeholder="请选择对象类型"
                  class="condition-object"
                  @change="handleConditionObjectChange(condition)"
                />
                <el-select
                  v-model="condition.attributeCandidateId"
                  filterable
                  :disabled="!condition.objectCandidateId"
                  :placeholder="attributePlaceholder"
                  class="condition-attribute"
                  @change="condition.value = ''"
                >
                  <el-option v-for="item in getConditionAttributes(condition)" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                <span v-if="form.type === 'REQUIRED'" class="condition-operator">为空</span>
                <template v-else>
                  <el-select v-if="form.type === 'RANGE'" v-model="condition.operator" class="condition-comparison">
                    <el-option v-for="item in om_rule_operator" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                  <span v-else class="condition-operator">{{ conditionOperatorLabel }}</span>
                  <el-select v-if="form.type === 'FORMAT'" v-model="condition.value" placeholder="请选择日期格式" class="condition-value" clearable>
                    <el-option v-for="format in dateFormatOptions" :key="format" :label="format" :value="format" />
                  </el-select>
                  <el-input v-else v-model="condition.value" :placeholder="conditionValuePlaceholder" class="condition-value" />
                </template>
              </div>
              <div class="condition-actions">
                <el-button link type="danger" icon="Delete" :disabled="form.conditionItems.length === 1" @click="removeCondition(index)" />
                <el-button v-if="index === form.conditionItems.length - 1" link type="primary" icon="Plus" @click="addCondition">添加条件</el-button>
              </div>
            </div>
          </div>
        </qt-form-item>
        <qt-form-item label="失败提示" prop="failureMessage" tip="校验失败时向用户展示的提示文案。">
          <el-input
            v-model="form.failureMessage"
            placeholder="请输入失败提示"
            :maxlength="256"
          />
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="readonly" @click="open = false">关 闭</el-button>
          <template v-else>
            <el-button @click="open = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>
<script setup>
import { createRowKey } from "@/utils/rowKey";
import {
  listCandidates,
  getCandidate,
  updateCandidate,
  listAiAttributeBusinessTypes,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const {
  om_data_rule_type,
  om_rule_level,
  om_rule_operator,
  om_ai_adopt_status,
} = proxy.useDict(
  "om_data_rule_type",
  "om_rule_level",
  "om_rule_operator",
  "om_ai_adopt_status"
);

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const readonly = ref(false);
const form = ref({ conditionItems: [{}] });
const objects = ref([]);
const attributes = ref([]);
const businessTypes = ref([]);

/**
 * 数据规则采纳不做前置必填拦截，保持与迁移前一致的“可直接采纳”行为。
 * @returns {Array<string>} 恒为空数组
 */
const required = () => [];

const tab = useCandidateTab("data_rule", props.taskId, required);
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
    { label: "规则名称", prop: "name", component: { is: "input" } },
    { label: "规则类型", prop: "type", component: { is: "select", options: om_data_rule_type } },
    { label: "采纳状态", prop: "adoptStatus", component: { is: "select", options: om_ai_adopt_status } },
  ],
});

const wrapConfig = {
  fullContent: false,
  searchShadow: false,
};

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  params: { name: null, type: null, adoptStatus: null },
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
      label: "规则名称",
      prop: "name",
      slot: "text",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 240,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "规则类型",
      prop: "type",
      slot: "type",
      width: 150,
      minWidth: 150,
      align: "center",
    },
    {
      label: "级别",
      prop: "level",
      slot: "level",
      width: 90,
      minWidth: 90,
      align: "center",
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
      date: true,
      width: 160,
      minWidth: 160,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 250,
      minWidth: 250,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
});

const treeProps = { label: "name", value: "id", children: "children" };
const objectTree = computed(() => proxy.handleTree(objects.value.map((item) => ({ ...item })), "id", "parentCandidateId"));
const dateFormatOptions = ["yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd", "yyyy年MM月dd日"];
const attributePlaceholder = computed(() => form.value.type === "RANGE" ? "请选择整数或小数属性" : form.value.type === "FORMAT" ? "请选择日期属性" : "请选择属性");
const conditionValuePlaceholder = computed(() => form.value.type === "ENUM" ? "请输入枚举值" : "条件值");
const conditionOperatorLabel = computed(() => form.value.type === "ENUM" ? "不属于" : "不匹配");
const rules = {
  name: [{ required: true, whitespace: true, message: "数据规则名称不能为空", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
  level: [{ required: true, message: "请选择级别", trigger: "change" }],
  conditionRelation: [{ required: true, message: "请选择条件关系", trigger: "change" }],
};
function createCondition(source = {}) {
  return { key: createRowKey(), objectCandidateId: source.objectCandidateId, attributeCandidateId: source.attributeCandidateId, operator: source.operator || "LT", value: source.value ?? "" };
}
function getBasicTypeCode(attribute) {
  if (Number(attribute.typeCategory) !== 2) return Number(attribute.basicTypeCode);
  const businessType = businessTypes.value.find((item) => String(item.id) === String(attribute.businessTypeId) && item.businessTypeSource === (attribute.businessTypeSource || "GLOBAL"));
  return businessType ? Number(businessType.dataType) : undefined;
}
function getConditionAttributes(condition) {
  const options = attributes.value.filter((item) => item.objectCandidateId === condition.objectCandidateId);
  if (form.value.type === "RANGE") return options.filter((item) => [1, 2].includes(getBasicTypeCode(item)));
  if (form.value.type === "FORMAT") return options.filter((item) => getBasicTypeCode(item) === 4);
  return options;
}
async function openForm(row, view) {
  readonly.value = view;
  const d = (await getCandidate("data_rule", row.id)).data;
  const conditions = JSON.parse(d.conditions || "[]");
  form.value = { ...d, conditionItems: (conditions.length ? conditions : [{}]).map(createCondition) };
  open.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  const validationMessage = validateConditions();
  if (validationMessage) { proxy.$modal.msgWarning(validationMessage); return; }
  await updateCandidate("data_rule", {
    ...form.value,
    conditions: JSON.stringify(form.value.conditionItems),
  });
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  refresh();
}
async function loadBusinessTypes() {
  businessTypes.value = [];
  await Promise.allSettled(["PROJECT", "GLOBAL"].map(async (source) => {
    const response = await listAiAttributeBusinessTypes(props.taskId, source);
    businessTypes.value.push(...(response.data || []).map((item) => ({ ...item, businessTypeSource: source })));
  }));
}
function handleTypeChange() {
  form.value.conditionItems = [createCondition()];
  nextTick(() => formRef.value?.clearValidate());
}
function handleConditionObjectChange(condition) {
  condition.attributeCandidateId = undefined;
  condition.value = "";
}
function addCondition() { form.value.conditionItems.push(createCondition()); }
function removeCondition(index) { if (form.value.conditionItems.length > 1) form.value.conditionItems.splice(index, 1); }
function validateConditions() {
  for (const [index, condition] of form.value.conditionItems.entries()) {
    const prefix = `第 ${index + 1} 条失败条件：`;
    if (!condition.objectCandidateId || !condition.attributeCandidateId) return prefix + "请选择对象类型和属性";
    if (form.value.type === "RANGE" && (!condition.operator || String(condition.value).trim() === "")) return prefix + "请选择比较符并填写数值";
    if (form.value.type === "FORMAT" && !dateFormatOptions.includes(condition.value)) return prefix + "请选择日期格式";
    if (form.value.type === "ENUM" && !String(condition.value).trim()) return prefix + "请输入枚举值";
  }
  return "";
}

/**
 * 按规则类型返回条件比较符文案。
 */
function conditionOperator(type, operator) {
  if (type === 'REQUIRED') return '为空';
  if (type === 'ENUM') return '不属于';
  if (type === 'FORMAT') return '不匹配';
  return proxy.selectDictLabel(om_rule_operator.value, operator);
}

/** 将条件关系字典值转为展示文案。 */
function conditionRelationLabel(value) {
  return {OR: '任一满足（OR）', AND: '全部满足（AND）'}[value] || '';
}

/**
 * 将失败条件拼为“条件N：操作 值”的多行文本。
 */
function detailConditions(record) {
  return (record.conditionItems || []).map((condition, index) => {
    const operator = conditionOperator(record.type, condition.operator);
    const value = record.type === 'REQUIRED' || !condition.value ? '' : ` ${condition.value}`;
    const obj = objects.value.find((item) => item.id === condition.objectCandidateId)?.name || condition.objectCandidateId || '';
    const attr = attributes.value.find((item) => item.id === condition.attributeCandidateId)?.name || condition.attributeCandidateId || '';
    return `条件 ${index + 1}：${obj} - ${attr} ${operator}${value}`;
  }).join('\n');
}

// 详情弹窗展示字段
const basicDetailFields = computed(() => [
  {label: '规则名称', value: form.value.name},
  {label: '规则类型', value: proxy.selectDictLabel(om_data_rule_type.value, form.value.type), dictOptions: om_data_rule_type.value, dictValue: form.value.type},
  {label: '级别', value: proxy.selectDictLabel(om_rule_level.value, form.value.level), dictOptions: om_rule_level.value, dictValue: form.value.level},
  {label: '描述', value: form.value.description, multiline: true},
  {label: '创建人', value: form.value.createBy || '-'},
  {label: '联系方式', value: creatorPhones.value[form.value.creatorId] || '-'},
  {label: '创建时间', value: proxy.parseTime(form.value.createTime, '{y}-{m}-{d} {h}:{i}')}
]);
const conditionDetailFields = computed(() => [
  {label: '条件关系', value: conditionRelationLabel(form.value.conditionRelation)},
  {label: '失败条件', value: detailConditions(form.value), multiline: true},
  {label: '失败提示', value: form.value.failureMessage}
]);

/** 加载对象、属性与业务类型候选，供数据规则弹窗的条件构建器使用。 */
async function loadSupportData() {
  const [o, a] = await Promise.all([
    listCandidates("object", { taskId: props.taskId, pageNum: 1, pageSize: 1000 }),
    listCandidates("attribute", { taskId: props.taskId, pageNum: 1, pageSize: 1000 }),
  ]);
  objects.value = o.data?.rows || [];
  attributes.value = a.data?.rows || [];
  await loadBusinessTypes();
}
loadSupportData();
</script>

<style scoped>
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

.rule-form {
  min-height: 390px;
}

.rule-form .el-select {
  width: 100%;
}

.h2-title {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  font-family: PingFang SC;
  font-weight: 500;
  margin: 8px 0;
}
.h2-title::before {
  display: inline-block;
  content: "";
  width: 6px;
  height: 16px;
  border-radius: 3px;
  background: var(--el-color-primary);
  margin-right: 8px;
}

.character-count :deep(.el-input__count) {
  background: transparent;
}

.character-count :deep(.el-input__count)::after {
  content: "个字符";
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}

.conditions {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 10px 12px;
  background: #f5f8ff;
  border: 1px solid #d9e2ff;
  border-radius: 4px;
}
.condition-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.condition-label { flex: 0 0 62px; }
.condition-label, .condition-operator { color: #606266; font-size: 14px; line-height: 32px; white-space: nowrap; }
.condition-fields { display: flex; align-items: center; flex: 1 1 330px; min-width: 0; gap: 10px; flex-wrap: wrap; }
.condition-fields > .condition-object, .condition-fields > .condition-attribute { flex: 1 1 140px; width: auto; min-width: 0; }
.condition-fields > .condition-comparison { flex: 0 1 110px; width: 110px; min-width: 90px; }
.condition-fields > .condition-value { flex: 1 1 180px; min-width: 0; }
.condition-actions { display: flex; align-items: center; flex: 0 0 120px; gap: 12px; }
.condition-actions .el-button + .el-button { margin-left: 0; }
</style>
