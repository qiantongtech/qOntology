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
    <div class="justify-between mb15">
      <el-row :gutter="15" class="btn-style">
        <el-col :span="1.5"
          ><el-button
            type="primary"
            plain
            :disabled="!selected.length"
            @click="adoptBatch"
            ><i class="iconfont-mini icon-queren mr5"></i>批量采纳</el-button
          ></el-col
        >
        <el-col :span="1.5"
          ><el-button
            type="danger"
            plain
            :disabled="!selected.length"
            @click="removeRows()"
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button
          ></el-col
        >
      </el-row>
      <right-toolbar :search="false" :columns="columns" @queryTable="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="rows"
      stripe
      row-key="id"
      @selection-change="selected = $event"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
        :selectable="(row) => row.adoptStatus !== 1"
      />
      <el-table-column
        v-if="visible('id')"
        prop="id"
        label="编号"
        width="80"
        align="center"
        fixed="left"
        sortable="custom"
        :sort-orders="['descending', 'ascending', null]"
        :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column
        v-if="visible('name')"
        prop="name"
        label="名称"
        min-width="200"
        :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column
        v-if="visible('description')"
        prop="description"
        label="描述"
        min-width="240"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.description || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('type')"
        label="公理类型"
        width="150"
        align="center"
        ><template #default="{ row }"
          ><dict-tag :options="om_axiom_type" :value="row.type" /></template
      ></el-table-column>
      <el-table-column
        v-if="visible('level')"
        label="级别"
        width="90"
        align="center"
        ><template #default="{ row }"
          ><dict-tag :options="om_rule_level" :value="row.level" /></template
      ></el-table-column>
      <el-table-column
        v-if="visible('adoptStatus')"
        label="采纳状态"
        width="110"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_ai_adopt_status"
            :value="row.adoptStatus" /></template
      ></el-table-column>
      <el-table-column
        v-if="visible('createBy')"
        label="创建人"
        width="120"
        align="center"
        ><template #default="{ row }">{{
          row.createBy || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('createTime')"
        label="创建时间"
        width="180"
        align="center"
        sortable
        ><template #default="{ row }">{{
          proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('operation')"
        label="操作"
        width="245"
        align="center"
        fixed="right"
      >
        <template #default="{ row }"
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
        >
      </el-table-column>
      <template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" />
          <p>暂无模型公理</p>
        </div></template
      >
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="load"
    />
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
import AttributeTypeConfig from "../../om/object/type/detail/AttributeTypeConfig.vue";
import { listAiAttributeBusinessTypes, listAiAttributeEnums } from "@/api/om/aiModeling";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_axiom_type, om_rule_level, om_ai_adopt_status, om_attribute_data_type, basic_data_type } = proxy.useDict(
  "om_axiom_type",
  "om_rule_level",
  "om_ai_adopt_status",
  "om_attribute_data_type",
  "basic_data_type"
);
const loading = ref(false),
  rows = ref([]),
  total = ref(0),
  selected = ref([]),
  open = ref(false),
  readonly = ref(false),
  form = ref({}),
  objects = ref([]),
  attributes = ref([]),
  businessTypes = ref([]),
  enumOptions = ref([]),
  enumsLoading = ref(false),
  evidenceOpen = ref(false),
  evidenceRows = ref([]),
  evidenceTitle = ref("");
const query = reactive({ pageNum: 1, pageSize: 10, taskId: props.taskId });
const columns = reactive([
  { key: "id", label: "编号", visible: true },
  { key: "name", label: "名称", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "type", label: "公理类型", visible: true },
  { key: "level", label: "级别", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (k) => columns.find((i) => i.key === k)?.visible !== false;
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
async function load() {
  loading.value = true;
  try {
    const [a, o, at] = await Promise.all([
      listCandidates("axiom", query),
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
    rows.value = a.data.rows || [];
    total.value = a.data.total || 0;
    objects.value = o.data.rows || [];
    attributes.value = at.data.rows || [];
  } finally {
    loading.value = false;
  }
}
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
  load();
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
async function adopt(row) {
  await proxy.$modal.confirm("确认采纳该模型公理吗？");
  await adoptCandidate("axiom", row.id);
  proxy.$modal.msgSuccess("采纳成功");
  load();
}
async function adoptBatch() {
  for (const r of selected.value) await adoptCandidate("axiom", r.id);
  proxy.$modal.msgSuccess("批量采纳完成");
  load();
}
async function removeRows(row) {
  const ids = row ? [row.id] : selected.value.map((i) => i.id);
  await proxy.$modal.confirm("确认删除选中的模型公理吗？");
  await deleteCandidate("axiom", ids.join(","));
  load();
}
async function source(row) {
  evidenceTitle.value = `${row.name || "模型公理"} - 数据来源`;
  evidenceRows.value = (await getCandidateEvidence("axiom", row.id)).data || [];
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
.business-type-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
</style>
