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
      :config="wrapConfig"
      :columns="tableStore.columns"
      :table-ref="tableRef"
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
        :title="readonly ? '模型公理详情' : '修改模型公理'"
        width="860px"
        :append-to="pageRef"
        :close-on-press-escape="!saving"
        :before-close="beforeClose"
        draggable
        destroy-on-close
        @closed="handleDialogClosed"
      >
      <guide-tip-panel
        v-if="!readonly"
        type="hint"
        class="mb15"
        allow-wrap
        title="为对象类型及其属性、关系设定逻辑约束，选择公理类型并配置适用范围与约束强度，保证模型的一致性与可校验性。"
      />
      <!-- 详情弹窗只读风格 -->
      <el-form v-if="readonly" label-width="120px" class="column-form">
        <el-form-item
          v-for="field in detailFields"
          :key="field.label"
          :label="field.label"
          :class="{ 'row-full': field.multiline }"
        >
          <dict-tag v-if="field.dictOptions" :options="field.dictOptions" :value="field.dictValue" />
          <div v-else class="form-readonly" :class="{ textarea: field.multiline }">{{ field.value || '-' }}</div>
        </el-form-item>
        <AttributeTypeConfig
          v-if="form.type === 'PROPERTY_RANGE'"
          :model="detailConfig"
          readonly
          :enum-options="enumOptions"
        />
      </el-form>
      <!-- 修改弹窗可编辑风格 -->
      <el-form
        v-else
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="rule-form"
        @submit.prevent="save"
      >
        <div class="h2-title">基本信息</div>
        <qt-form-item label="公理名称" prop="name" required tip="填写公理的业务名称，便于在列表中识别与检索。">
          <el-input
            v-model="form.name"
            placeholder="请输入公理名称"
            maxlength="128"
          />
        </qt-form-item>
        <qt-form-item label="类型" prop="type" required >
          <el-select
            v-model="form.type"
            placeholder="请选择类型"
            @change="handleTypeChange"
          >
            <el-option
              v-for="i in om_axiom_type"
              :key="i.value"
              :label="i.label"
              :value="i.value"
            />
          </el-select>
        </qt-form-item>
        <qt-form-item label="级别" prop="level" required tip="设置公理的约束强度，决定校验失败时的处理方式。">
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
        <div class="h2-title">公理表达</div>
        <template v-if="form.type === 'DOMAIN_RANGE'">
          <qt-form-item label="关系" prop="relationName" required tip="填写该约束作用的关系名称。">
            <el-input
              v-model="form.relationName"
              placeholder="请输入关系名称"
              maxlength="128"
              clearable
            />
          </qt-form-item>
          <qt-form-item label="Domain" prop="domainCandidateId" required tip="选择关系起点（定义域）对应的对象类型。">
            <el-tree-select
              v-model="form.domainCandidateId"
              :data="objectTree"
              :props="treeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              placeholder="请选择对象类型"
              @change="handleDomainChange"
            />
          </qt-form-item>
          <qt-form-item label="Range" prop="rangeCandidateId" required tip="选择关系终点（值域）对应的对象类型。">
            <el-tree-select
              v-model="form.rangeCandidateId"
              :data="rangeObjectTree"
              :props="treeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              placeholder="请选择对象类型"
            />
          </qt-form-item>
        </template>
        <template v-else>
          <qt-form-item :label="firstTypeLabel" prop="objectCandidateId" required tip="选择该公理作用的主体对象类型。">
            <el-tree-select
              v-model="form.objectCandidateId"
              :data="objectTree"
              :props="treeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              placeholder="请选择对象类型"
              @change="handleObjectChange"
            />
          </qt-form-item>
          <qt-form-item
            v-if="form.type !== 'PROPERTY_RANGE'"
            :label="secondTypeLabel"
            prop="otherObjectCandidateId"
            required
            tip="选择参与公理表达的另一对象类型。"
          >
            <el-tree-select
              v-model="form.otherObjectCandidateId"
              :data="otherObjectTree"
              :props="treeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              placeholder="请选择对象类型"
            />
          </qt-form-item>
          <template v-else>
          <qt-form-item label="对象类型属性" prop="attributeCandidateId" required tip="选择当前对象类型下需要约束的属性。">
            <el-select
              v-model="form.attributeCandidateId"
              filterable
              clearable
              placeholder="请选择当前对象类型的属性"
            >
              <el-option
                v-for="i in objectAttributes"
                :key="i.id"
                :label="i.name"
                :value="i.id"
              />
            </el-select>
          </qt-form-item>
          <qt-form-item label="数据类型" prop="typeCategory" required tip="选择属性取值的基础数据类型或业务数据类型。">
            <el-radio-group v-model="form.typeCategory" @change="handleTypeCategoryChange">
              <el-radio v-for="item in om_attribute_data_type" :key="item.value" :value="Number(item.value)">{{ item.label }}</el-radio>
            </el-radio-group>
          </qt-form-item>
          <qt-form-item v-if="form.typeCategory === 1" label="基础数据类型" prop="basicTypeCode" required tip="选择属性对应的基础数据类型。">
            <el-select v-model="form.basicTypeCode" placeholder="请选择基础数据类型" clearable>
              <el-option v-for="item in basic_data_type" :key="item.value" :label="item.label" :value="Number(item.value)" />
            </el-select>
          </qt-form-item>
          <qt-form-item v-else label="业务数据类型" prop="businessTypeId" required tip="按当前项目与全局两个分类展开，选择属性关联的业务数据类型。">
            <el-tree-select
              v-model="businessKey"
              :data="businessTypeTree"
              :props="businessTypeTreeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              default-expand-all
              placeholder="请选择业务数据类型"
            />
          </qt-form-item>
          <el-form-item v-if="form.typeCategory === 2 && selectedBusinessType" label="基础数据类型">
            <div class="form-readonly">{{ basicTypeLabel(effectiveConfig.basicTypeCode) }}</div>
          </el-form-item>
          <AttributeTypeConfig
            v-if="form.typeCategory === 1 || selectedBusinessType"
            :model="effectiveConfig"
            :readonly="readonly || form.typeCategory === 2"
            :enum-options="enumOptions"
            :enum-loading="enumsLoading"
            @enum-open="loadEnums"
          />
          <el-form-item v-if="form.typeCategory === 2 && selectedBusinessType?.description" label="类型描述">
            <div class="form-readonly textarea">{{ selectedBusinessType.description }}</div>
          </el-form-item>
          </template>
        </template>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="readonly" @click="open = false">关 闭</el-button>
          <template v-else>
            <el-button @click="open = false">取 消</el-button>
            <el-button
              type="primary"
              :loading="saving"
              @click="save"
              >确 定</el-button
            >
          </template>
        </div>
      </template>
    </el-dialog>
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>
<script setup>
import {
  listCandidates,
  getCandidate,
  updateCandidate,
  listAiAttributeBusinessTypes,
  listAiAttributeEnums,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import AttributeTypeConfig from "../../om/object/type/detail/AttributeTypeConfig.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const {
  om_axiom_type,
  om_rule_level,
  om_ai_adopt_status,
  om_attribute_data_type,
  basic_data_type,
} = proxy.useDict(
  "om_axiom_type",
  "om_rule_level",
  "om_ai_adopt_status",
  "om_attribute_data_type",
  "basic_data_type"
);

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const readonly = ref(false);
const form = ref({});
const objects = ref([]);
const attributes = ref([]);
const businessTypes = ref([]);
const enumOptions = ref([]);
const enumsLoading = ref(false);

/**
 * 模型公理采纳不做前置必填拦截，保持与迁移前一致的“可直接采纳”行为。
 * @returns {Array<string>} 恒为空数组
 */
const required = () => [];

const tab = useCandidateTab("axiom", props.taskId, required);
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
const wrapConfig = {
  fullContent: false,
  searchShadow: false,
};

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "公理名称", prop: "name", component: { is: "input" } },
    {
      label: "公理类型",
      prop: "type",
      component: { is: "select", options: om_axiom_type },
    },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      component: { is: "select", options: om_ai_adopt_status },
    },
  ],
});

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
      label: "公理名称",
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
      label: "公理类型",
      prop: "type",
      dict: "om_axiom_type",
      width: 150,
      minWidth: 150,
      align: "center",
    },
    {
      label: "级别",
      prop: "level",
      dict: "om_rule_level",
      width: 90,
      minWidth: 90,
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
      align: "left",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      slot: "adoptStatus",
      width: 90,
      minWidth: 90,
      align: "center",
      fixed: "right",
    },
    {
      label: "操作",
      slot: "handle",
      width: 210,
      minWidth: 210,
      fixed: "right",
    },
  ],
  func: fetchRows,
  params: {
    name: null,
    type: null,
    adoptStatus: null,
  },
});

const treeProps = { label: "name", value: "id", children: "children" };
const objectTree = computed(() => proxy.handleTree(objects.value.map((item) => ({ ...item })), "id", "parentCandidateId"));
const otherObjectTree = computed(() => proxy.handleTree(objects.value.map((item) => ({ ...item, disabled: item.id === form.value.objectCandidateId })), "id", "parentCandidateId"));
const rangeObjectTree = computed(() => proxy.handleTree(objects.value.map((item) => ({ ...item, disabled: item.id === form.value.domainCandidateId })), "id", "parentCandidateId"));
const firstTypeLabel = computed(() => form.value.type === "SUBCLASS" ? "子类型" : form.value.type === "PROPERTY_RANGE" ? "对象类型" : "对象类型A");
const secondTypeLabel = computed(() => form.value.type === "SUBCLASS" ? "父类型" : "对象类型B");
const objectAttributes = computed(() =>
  attributes.value.filter(
    (i) => i.objectCandidateId === form.value.objectCandidateId
  )
);
const selectedBusinessType = computed(() => businessTypes.value.find((item) => String(item.id) === String(form.value.businessTypeId) && item.businessTypeSource === (form.value.businessTypeSource || "GLOBAL")));
const effectiveConfig = computed(() => form.value.typeCategory === 2 && selectedBusinessType.value ? { ...selectedBusinessType.value, basicTypeCode: Number(selectedBusinessType.value.dataType) } : form.value);
const businessKey = computed({
  get: () => form.value.businessTypeId ? `${form.value.businessTypeSource || "GLOBAL"}:${form.value.businessTypeId}` : undefined,
  set: (value) => {
    const [source, id] = (value || "").split(":");
    form.value.businessTypeSource = source || "GLOBAL";
    form.value.businessTypeId = id || undefined;
  },
});

const businessTypeTreeProps = { label: 'name', value: 'id', children: 'children', disabled: 'disabled' };
const businessTypeTree = computed(() =>
  [
    { source: 'PROJECT', label: '当前项目' },
    { source: 'GLOBAL', label: '全局' }
  ]
    .map((group) => ({
      id: group.source,
      name: group.label,
      disabled: true,
      children: businessTypes.value
        .filter((item) => item.businessTypeSource === group.source)
        .map((item) => ({
          id: `${item.businessTypeSource}:${item.id}`,
          name: item.name,
          businessTypeSource: item.businessTypeSource
        }))
    }))
    .filter((group) => group.children.length)
);
const basicTypeLabel = (value) => proxy.selectDictLabel(basic_data_type.value, value) || "-";
const rules = {
  name: [{ required: true, whitespace: true, message: "模型公理名称不能为空", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
  level: [{ required: true, message: "请选择级别", trigger: "change" }],
  relationName: [{ required: true, whitespace: true, message: "关系名称不能为空", trigger: "blur" }],
  domainCandidateId: [{ required: true, message: "请选择Domain", trigger: "change" }],
  rangeCandidateId: [{ required: true, message: "请选择Range", trigger: "change" }],
  objectCandidateId: [{ required: true, message: "请选择对象类型", trigger: "change" }],
  otherObjectCandidateId: [{ required: true, message: "请选择另一对象类型", trigger: "change" }],
  attributeCandidateId: [{ required: true, message: "请选择对象类型属性", trigger: "change" }],
  typeCategory: [{ required: true, message: "请选择数据类型", trigger: "change" }],
};

async function openForm(row, view) {
  readonly.value = view;
  const data = { ...(await getCandidate("axiom", row.id)).data };
  form.value = {
    ...data,
    typeCategory: data.typeCategory == null ? 1 : Number(data.typeCategory),
    basicTypeCode: data.basicTypeCode == null ? undefined : Number(data.basicTypeCode),
    businessTypeId: data.businessTypeId == null ? undefined : String(data.businessTypeId),
    businessTypeSource: data.businessTypeSource || "GLOBAL",
    decimalPlaces: data.decimalPlaces == null ? 2 : Number(data.decimalPlaces),
  };
  open.value = true;
  await Promise.all([loadTypeOptions(), loadEnums()]);
  nextTick(() => formRef.value?.clearValidate());
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  await updateCandidate("axiom", {
    ...form.value,
    taskId: props.taskId,
    businessTypeId: form.value.typeCategory === 2 ? Number(form.value.businessTypeId) : null,
    businessTypeSource: form.value.typeCategory === 2 ? form.value.businessTypeSource : null,
    basicTypeCode: form.value.typeCategory === 1 ? form.value.basicTypeCode : null,
  });
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  refresh();
}
function handleTypeChange() {
  Object.assign(form.value, { objectCandidateId: undefined, otherObjectCandidateId: undefined, relationName: "", domainCandidateId: undefined, rangeCandidateId: undefined, attributeCandidateId: undefined });
  nextTick(() => formRef.value?.clearValidate());
}
function handleObjectChange() {
  form.value.otherObjectCandidateId = undefined;
  form.value.attributeCandidateId = undefined;
}
function handleTypeCategoryChange() {
  Object.assign(form.value, { basicTypeCode: undefined, businessTypeId: undefined, businessTypeSource: "GLOBAL", unit: "", decimalPlaces: 2, format: "", enumType: undefined, enumSource: "PROJECT" });
  nextTick(() => formRef.value?.clearValidate());
}
async function loadTypeOptions() {
  businessTypes.value = [];
  await Promise.allSettled(["PROJECT", "GLOBAL"].map(async (source) => {
    const response = await listAiAttributeBusinessTypes(props.taskId, source);
    businessTypes.value.push(...(response.data || []).map((item) => ({ ...item, id: String(item.id), businessTypeSource: source })));
  }));
}
async function loadEnums() {
  if (enumsLoading.value) return;
  enumsLoading.value = true;
  try {
    enumOptions.value = [];
    await Promise.allSettled(["PROJECT", "GLOBAL"].map(async (source) => {
      const response = await listAiAttributeEnums(props.taskId, source);
      enumOptions.value.push(...(response.data || []).map((item) => ({ ...item, enumSource: source })));
    }));
  } finally { enumsLoading.value = false; }
}

function handleDomainChange() {
  if (form.value.domainCandidateId && form.value.domainCandidateId === form.value.rangeCandidateId) {
    form.value.rangeCandidateId = undefined;
  }
}

/**
 * 保存期间拦截弹窗关闭回调。
 * @param {Function} done el-dialog 的 before-close 完成回调
 */
function beforeClose(done) {
  if (!saving.value) done();
}

/** 弹窗关闭后清除表单校验提示。 */
function handleDialogClosed() {
  formRef.value?.clearValidate();
}

/**
 * 按公理类型组装详情弹窗的展示字段。
 * @param {Object} record 详情记录
 * @returns {Array<Object>} 展示字段列表
 */
function getDetailFields(record) {
  const typeLabel = proxy.selectDictLabel(om_axiom_type.value, record.type) || '-';
  const levelLabel = proxy.selectDictLabel(om_rule_level.value, record.level) || '-';

  let specificFields = [];
  if (record.type === 'DOMAIN_RANGE') {
    specificFields = [
      { label: '关系', value: record.relationName },
      { label: 'Domain', value: objectTree.value.find(item => item.id === record.domainCandidateId)?.name || record.domainCandidateId || '-' },
      { label: 'Range', value: objectTree.value.find(item => item.id === record.rangeCandidateId)?.name || record.rangeCandidateId || '-' }
    ];
  } else if (record.type === 'PROPERTY_RANGE') {
    const businessType = businessTypes.value.find((item) => String(item.id) === String(record.businessTypeId) && item.businessTypeSource === (record.businessTypeSource || "GLOBAL"));
    specificFields = [
      { label: '对象类型', value: objectTree.value.find(item => item.id === record.objectCandidateId)?.name || record.objectCandidateId || '-' },
      { label: '对象类型属性', value: attributes.value.find(item => item.id === record.attributeCandidateId)?.name || record.attributeCandidateId || '-' },
      { label: '数据类型', value: record.typeCategory === 1 ? '基础数据类型' : '业务数据类型' },
      {
        label: '数据类型',
        value: record.typeCategory === 1
          ? proxy.selectDictLabel(basic_data_type.value, record.basicTypeCode)
          : businessType?.name || record.businessTypeId
      }
    ];
    if (record.typeCategory === 2) {
      specificFields.push({ label: '基础数据类型', value: basicTypeLabel(businessType ? Number(businessType.dataType) : record.basicTypeCode) });
      if (businessType?.description) {
        specificFields.push({ label: '类型描述', value: businessType.description, multiline: true });
      }
    }
  } else {
    const firstType = record.type === 'SUBCLASS' ? '子类型' : '对象类型A';
    const secondType = record.type === 'SUBCLASS' ? '父类型' : '对象类型B';
    specificFields = [
      { label: firstType, value: objectTree.value.find(item => item.id === record.objectCandidateId)?.name || record.objectCandidateId || '-' },
      { label: secondType, value: objectTree.value.find(item => item.id === record.otherObjectCandidateId)?.name || record.otherObjectCandidateId || '-' }
    ];
  }

  return [
    { label: '公理名称', value: record.name },
    { label: '类型', value: typeLabel, dictOptions: om_axiom_type.value, dictValue: record.type },
    { label: '级别', value: levelLabel, dictOptions: om_rule_level.value, dictValue: record.level },
    ...specificFields,
    { label: '描述', value: record.description, multiline: true },
    { label: '创建人', value: record.createBy || '-' },
    { label: '联系方式', value: creatorPhones.value[record.creatorId] || '-' },
    { label: '创建时间', value: proxy.parseTime(record.createTime, '{y}-{m}-{d} {h}:{i}') }
  ];
}

const detailFields = computed(() => getDetailFields(form.value));
const detailConfig = computed(() => effectiveConfig.value);

/** 加载对象与属性候选，供公理表达弹窗的树形下拉与属性下拉使用。 */
async function loadSupportData() {
  const [o, at] = await Promise.all([
    listCandidates("object", { taskId: props.taskId, pageNum: 1, pageSize: 1000 }),
    listCandidates("attribute", { taskId: props.taskId, pageNum: 1, pageSize: 1000 }),
  ]);
  objects.value = o.data?.rows || [];
  attributes.value = at.data?.rows || [];
}
loadSupportData();
</script>

<style scoped lang="scss">
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

.axiom-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}
.rule-form {
  min-height: 390px;
}

.rule-form .el-select {
  width: 100%;
}
/* 对齐对象类型新增页的间隔：带描述提示的表单项底部留白，避免说明文字贴到下一字段。 */
.rule-form :deep(.el-form-item.has-tip) {
  margin-bottom: 25px;
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
</style>
