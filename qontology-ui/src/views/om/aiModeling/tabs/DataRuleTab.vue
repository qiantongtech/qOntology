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
    <div class="justify-between mb15">
      <el-row :gutter="15" class="btn-style"
        ><el-col :span="1.5"
          ><el-button
            type="primary"
            plain
            :disabled="!selected.length"
            @click="adoptBatch"
            ><i class="iconfont-mini icon-queren mr5"></i>批量采纳</el-button
          ></el-col
        ><el-col :span="1.5"
          ><el-button
            type="danger"
            plain
            :disabled="!selected.length"
            @click="removeRows()"
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button
          ></el-col
        ></el-row
      ><right-toolbar :search="false" :columns="columns" @queryTable="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="rows"
      stripe
      row-key="id"
      @selection-change="selected = $event"
      ><el-table-column
        type="selection"
        width="55"
        align="center"
        :selectable="(r) => r.adoptStatus !== 1"
      /><el-table-column
        v-if="visible('id')"
        prop="id"
        label="编号"
        width="80"
        align="center"
        fixed="left"
        sortable="custom"
        :sort-orders="['descending', 'ascending', null]"
        :show-overflow-tooltip="{ effect: 'light' }"
      /><el-table-column
        v-if="visible('name')"
        prop="name"
        label="名称"
        min-width="200"
        :show-overflow-tooltip="{ effect: 'light' }"
      /><el-table-column
        v-if="visible('description')"
        label="描述"
        min-width="240"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.description || "-"
        }}</template></el-table-column
      ><el-table-column
        v-if="visible('type')"
        label="规则类型"
        width="150"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_data_rule_type"
            :value="row.type" /></template></el-table-column
      ><el-table-column
        v-if="visible('level')"
        label="级别"
        width="90"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_rule_level"
            :value="row.level" /></template></el-table-column
      ><el-table-column
        v-if="visible('adoptStatus')"
        label="采纳状态"
        width="110"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_ai_adopt_status"
            :value="row.adoptStatus" /></template></el-table-column
      ><el-table-column
        v-if="visible('createBy')"
        label="创建人"
        width="120"
        align="center"
        ><template #default="{ row }">{{
          row.createBy || "-"
        }}</template></el-table-column
      ><el-table-column
        v-if="visible('createTime')"
        label="创建时间"
        width="180"
        align="center"
        sortable
        ><template #default="{ row }">{{
          proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
        }}</template></el-table-column
      ><el-table-column
        v-if="visible('operation')"
        label="操作"
        width="245"
        align="center"
        fixed="right"
        ><template #default="{ row }"
          ><el-button
            link
            type="primary"
            icon="Check"
            :disabled="row.adoptStatus === 1"
            @click="adopt(row)"
            >采纳</el-button
          ><el-button link type="primary" icon="Document" @click="source(row)"
            >来源</el-button
          ><el-popover placement="bottom" :width="120" trigger="click"
            ><template #reference
              ><el-button link type="primary" icon="ArrowDown"
                >更多</el-button
              ></template
            >
            <div class="butgdlist">
              <el-button
                link
                type="primary"
                icon="View"
                @click="openForm(row, true)"
                >详情</el-button
              ><el-button
                link
                type="primary"
                icon="Edit"
                :disabled="row.adoptStatus === 1"
                @click="openForm(row, false)"
                >修改</el-button
              ><el-button
                link
                type="danger"
                icon="Delete"
                :disabled="row.adoptStatus === 1"
                @click="removeRows(row)"
                >删除</el-button
              >
            </div></el-popover
          ></template
        ></el-table-column
      ><template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" />
          <p>暂无数据规则</p>
        </div></template
      ></el-table
    >
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="load"
    />
    <el-dialog
      v-model="open"
      :title="readonly ? '数据规则详情' : '修改数据规则'"
      width="860px"
      :append-to="pageRef"
      draggable
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :disabled="readonly"
        label-width="120px"
        class="rule-form"
      >
        <div class="h2-title">基本信息</div>
        <el-form-item label="名称" prop="name" required>
          <el-input
            v-model="form.name"
            placeholder="请输入数据规则名称"
            maxlength="128"
          />
        </el-form-item>
        <el-form-item label="类型" prop="type" required>
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option
              v-for="i in om_data_rule_type"
              :key="i.value"
              :label="i.label"
              :value="i.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="级别" prop="level" required>
          <el-radio-group v-model="form.level">
            <el-radio v-for="i in om_rule_level" :key="i.value" :value="i.value">{{
              i.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            :maxlength="512"
            show-word-limit
          />
        </el-form-item>
        <div class="h2-title">执行条件</div>
        <el-form-item label="条件关系" prop="conditionRelation" required>
          <el-radio-group v-model="form.conditionRelation">
            <el-radio value="OR">任一满足（OR）</el-radio>
            <el-radio value="AND">全部满足（AND）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="失败条件" required>
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
        </el-form-item>
        <el-form-item label="失败提示" prop="failureMessage">
          <el-input
            v-model="form.failureMessage"
            placeholder="请输入失败提示"
            :maxlength="512"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="open = false">取 消</el-button>
          <el-button
            v-if="!readonly"
            type="primary"
            size="mini"
            @click="save"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
    <CandidateEvidenceDrawer
      v-model="evidenceOpen"
      :title="evidenceTitle"
      :rows="evidenceRows"
    />
  </div>
</template>
<script setup>
import {
  listCandidates,
  getCandidate,
  updateCandidate,
  deleteCandidate,
  adoptCandidate,
  getCandidateEvidence,
} from "@/api/om/aiModeling";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { listAiAttributeBusinessTypes } from "@/api/om/aiModeling";
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
const loading = ref(false),
  rows = ref([]),
  total = ref(0),
  selected = ref([]),
  open = ref(false),
  readonly = ref(false),
  form = ref({ conditionItems: [{}] }),
  objects = ref([]),
  attributes = ref([]),
  businessTypes = ref([]),
  evidenceOpen = ref(false),
  evidenceRows = ref([]),
  evidenceTitle = ref("");
const query = reactive({ pageNum: 1, pageSize: 10, taskId: props.taskId });
const columns = reactive([
  { key: "id", label: "编号", visible: true },
  { key: "name", label: "名称", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "type", label: "规则类型", visible: true },
  { key: "level", label: "级别", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (k) => columns.find((i) => i.key === k)?.visible !== false;
const formRef = ref();
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
  return { key: globalThis.crypto.randomUUID(), objectCandidateId: source.objectCandidateId, attributeCandidateId: source.attributeCandidateId, operator: source.operator || "LT", value: source.value ?? "" };
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
async function load() {
  loading.value = true;
  try {
    const [r, o, a] = await Promise.all([
      listCandidates("data_rule", query),
      listCandidates("object", {
        taskId: props.taskId,
        pageNum: 1,
        pageSize: 1000,
      }),
      listCandidates("attribute", {
        taskId: props.taskId,
        pageNum: 1,
        pageSize: 1000,
      }),
    ]);
    rows.value = r.data.rows || [];
    total.value = r.data.total || 0;
    objects.value = o.data.rows || [];
    attributes.value = a.data.rows || [];
    await loadBusinessTypes();
  } finally {
    loading.value = false;
  }
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
  load();
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
async function adopt(row) {
  await proxy.$modal.confirm("确认采纳该数据规则吗？");
  await adoptCandidate("data_rule", row.id);
  proxy.$modal.msgSuccess("采纳成功");
  load();
}
async function adoptBatch() {
  for (const r of selected.value) await adoptCandidate("data_rule", r.id);
  proxy.$modal.msgSuccess("批量采纳完成");
  load();
}
async function removeRows(row) {
  const ids = row ? [row.id] : selected.value.map((i) => i.id);
  await proxy.$modal.confirm("确认删除选中的数据规则吗？");
  await deleteCandidate("data_rule", ids.join(","));
  load();
}
async function source(row) {
  evidenceTitle.value = `${row.name || "数据规则"} - 数据来源`;
  evidenceRows.value =
    (await getCandidateEvidence("data_rule", row.id)).data || [];
  evidenceOpen.value = true;
}
load();
</script>

<style scoped>
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
