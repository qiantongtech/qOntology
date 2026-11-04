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
  <div ref="pageRef" class="candidate-tab-page model-axiom-page">
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="wrapConfig"
    >
      <template #actions-data>
        <el-button
          type="primary"
          plain
          :disabled="!selectedIds.length"
          @click="adoptBatch"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-queren mr5"></i>批量采纳
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="!selectedIds.length"
          @click="remove()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
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
          <dict-tag :options="om_axiom_type" :value="row.type" />
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
            icon="Check"
            :disabled="row.adoptStatus === 1"
            @click="adoptOne(row)"
            >采纳</el-button
          >
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
              :disabled="row.adoptStatus === 1"
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
            placeholder="请输入模型公理名称"
            maxlength="128"
          />
        </el-form-item>
        <el-form-item label="类型" prop="type" required>
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option
              v-for="i in om_axiom_type"
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
        <div class="h2-title">公理表达</div>
        <template v-if="form.type === 'DOMAIN_RANGE'">
          <el-form-item label="关系" prop="relationName" required>
            <el-input
              v-model="form.relationName"
              placeholder="请输入关系名称"
              maxlength="128"
              clearable
            />
          </el-form-item>
          <el-form-item label="Domain" prop="domainCandidateId" required>
            <el-tree-select
              v-model="form.domainCandidateId"
              :data="objectTree"
              :props="treeProps"
              value-key="id"
              check-strictly
              filterable
              clearable
              placeholder="请选择对象类型"
              @change="form.rangeCandidateId = undefined"
            />
          </el-form-item>
          <el-form-item label="Range" prop="rangeCandidateId" required>
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
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item :label="firstTypeLabel" prop="objectCandidateId" required>
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
          </el-form-item>
          <el-form-item
            v-if="form.type !== 'PROPERTY_RANGE'"
            :label="secondTypeLabel"
            prop="otherObjectCandidateId"
            required
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
          </el-form-item>
          <template v-else>
          <el-form-item label="对象类型属性" prop="attributeCandidateId" required>
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
          </el-form-item>
          <el-form-item label="数据类型" prop="typeCategory" required>
            <el-radio-group v-model="form.typeCategory" @change="handleTypeCategoryChange">
              <el-radio v-for="item in om_attribute_data_type" :key="item.value" :value="Number(item.value)">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="form.typeCategory === 1" label="基础数据类型" prop="basicTypeCode" required>
            <el-select v-model="form.basicTypeCode" placeholder="请选择基础数据类型" clearable>
              <el-option v-for="item in basic_data_type" :key="item.value" :label="item.label" :value="Number(item.value)" />
            </el-select>
          </el-form-item>
          <el-form-item v-else label="业务数据类型" prop="businessTypeId" required>
            <el-select v-model="businessKey" filterable clearable placeholder="请选择业务数据类型">
              <template #label>
                <span class="business-type-label">{{ selectedBusinessType?.name }}
                  <el-tag v-if="selectedBusinessType" :type="selectedBusinessType.businessTypeSource === 'PROJECT' ? 'primary' : 'info'" size="small">{{ selectedBusinessType.businessTypeSource === 'PROJECT' ? '当前项目' : '全局' }}</el-tag>
                </span>
              </template>
              <el-option-group v-for="group in businessTypeGroups" :key="group.source" :label="group.label">
                <el-option v-for="item in group.options" :key="group.source + ':' + item.id" :label="item.name" :value="group.source + ':' + item.id" />
              </el-option-group>
            </el-select>
          </el-form-item>
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
          <el-button @click="open = false">取 消</el-button>
          <el-button
            v-if="!readonly"
            type="primary"
            @click="save"
            >确 定</el-button
          >
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
  remove,
  showEvidence,
} = tab;

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 页签内又没有搜索区，所以同时关闭操作栏的“隐藏搜索”按钮。
 */
const wrapConfig = {
  fullContent: false,
  actions: { table: { search: false } },
};

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
      selectable: (row) => row.adoptStatus !== 1,
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
      label: "名称",
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
      width: 120,
      minWidth: 120,
      align: "center",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 180,
      minWidth: 180,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 200,
      minWidth: 200,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
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
const businessTypeGroups = computed(() => [{ source: "PROJECT", label: "当前项目" }, { source: "GLOBAL", label: "全局" }].map((group) => ({ ...group, options: businessTypes.value.filter((item) => item.businessTypeSource === group.source) })).filter((group) => group.options.length));
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
.business-type-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
</style>
