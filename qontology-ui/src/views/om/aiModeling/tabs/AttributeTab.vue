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
    <div class="justify-between mb15">
      <el-row :gutter="15" class="btn-style">
        <el-col :span="1.5"
          ><el-button
            type="primary"
            plain
            :disabled="!selectedIds.length"
            @click="adoptBatch"
            ><i class="iconfont-mini icon-queren mr5"></i>批量采纳</el-button
          ></el-col
        >
        <el-col :span="1.5"
          ><el-button
            type="danger"
            plain
            :disabled="!selectedIds.length"
            @click="remove()"
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button
          ></el-col
        >
      </el-row>
      <right-toolbar :search="false" :columns="columns" @queryTable="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="rows"
      row-key="id"
      stripe
      @sort-change="handleSortChange"
      :default-sort="{ prop: 'id', order: 'descending' }"
      @selection-change="select"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
        :selectable="selectable"
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
        label="属性名称"
        width="240"
        fixed="left"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.name || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('code')"
        prop="code"
        label="属性代码"
        width="190"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.code || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('objectType')"
        label="所属对象类型"
        width="180"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          objectName(row.objectCandidateId)
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('description')"
        prop="description"
        label="描述"
        min-width="260"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.description || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('dataType')"
        label="数据类型"
        width="150"
        align="center"
        ><template #default="{ row }"
          ><el-tag
            :type="row.typeCategory === 2 ? 'success' : 'primary'"
            effect="light"
            >{{ dataTypeLabel(row.typeCategory) }}</el-tag
          ></template
        ></el-table-column
      >
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
        prop="createBy"
        label="创建人"
        width="120"
        align="center"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.createBy || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('createTime')"
        prop="createTime"
        label="创建时间"
        width="180"
        align="center"
        sortable="custom"
        :sort-orders="['descending', 'ascending', null]"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('operation')"
        label="操作"
        width="200"
        align="center"
        fixed="right"
        class-name="small-padding fixed-width"
      >
        <template #default="{ row }">
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
          <el-popover placement="bottom" :width="120" trigger="click">
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="butgdlist">
              <el-button
                link
                type="primary"
                icon="View"
                :disabled="row.adoptStatus === 1"
                @click="showDetail(row)"
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
                :disabled="row.adoptStatus === 1"
                @click="remove(row)"
                >删除</el-button
              >
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" alt="暂无属性" />
          <p>暂无属性</p>
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
      title="修改属性"
      width="800px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
      @closed="formRef?.clearValidate()"
    >
      <el-form
        ref="formRef"
        class="attribute-dialog-form"
        :model="form"
        :rules="rules"
        label-width="110px"
      >
        <div class="h2-title">基础信息</div>
        <el-form-item label="所属对象类型" prop="objectCandidateId"
          ><el-select
            v-model="form.objectCandidateId"
            placeholder="请选择所属对象类型"
            style="width: 100%"
            @change="loadTypeOptions"
            ><el-option
              v-for="item in objects"
              :key="item.id"
              :label="item.name"
              :value="item.id" /></el-select
        ></el-form-item>
        <el-form-item label="属性名称" prop="name"
          ><el-input
            v-model="form.name"
            placeholder="请输入属性名称"
            maxlength="128个字符"
        /></el-form-item>
        <el-form-item label="属性代码" prop="code"
          ><el-input
            v-model="form.code"
            placeholder="例如 purchase_amount"
            maxlength="128个字符"
        /></el-form-item>
        <el-form-item label="描述" prop="description"
          ><el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            maxlength="500"
            show-word-limit
        /></el-form-item>
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型" prop="typeCategory"
          ><el-radio-group
            v-model="form.typeCategory"
            @change="changeTypeCategory"
            ><el-radio
              v-for="option in dataTypeOptions"
              :key="option.value"
              :label="option.value"
              >{{ option.label }}</el-radio
            ></el-radio-group
          ></el-form-item
        >
        <el-form-item
          v-if="form.typeCategory === 1"
          label="基础数据类型"
          prop="basicTypeCode"
          ><el-select
            v-model="form.basicTypeCode"
            placeholder="请选择基础数据类型"
            style="width: 100%"
            clearable
            @change="clearTypeConfig"
            ><el-option
              v-for="item in basic_data_type"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)" /></el-select
        ></el-form-item>
        <el-form-item v-else label="业务数据类型" prop="businessTypeId"
          ><el-select
            v-model="businessKey"
            placeholder="请选择业务数据类型"
            style="width: 100%"
            filterable
            clearable
            ><template #label
              ><span class="business-type-label"
                >{{ selectedBusinessType?.name || "-" }}
                <el-tag
                  :type="
                    form.businessTypeSource === 'PROJECT' ? 'primary' : 'info'
                  "
                  size="small"
                  >{{
                    form.businessTypeSource === "PROJECT" ? "当前项目" : "全局"
                  }}</el-tag
                ></span
              ></template
            ><el-option-group
              v-for="group in businessTypeGroups"
              :key="group.source"
              :label="group.label"
              ><el-option
                v-for="item in group.options"
                :key="group.source + ':' + item.id"
                :label="item.name"
                :value="
                  group.source + ':' + item.id
                " /></el-option-group></el-select
        ></el-form-item>
        <template v-if="form.typeCategory === 2 && form.businessTypeId"
          ><el-form-item label="基础数据类型"
            ><div class="form-readonly">
              {{ basicTypeLabel(effectiveConfig.basicTypeCode) }}
            </div></el-form-item
          ></template
        >
        <AttributeTypeConfig
          v-if="form.typeCategory === 1 || form.businessTypeId"
          :model="effectiveConfig"
          :readonly="form.typeCategory === 2"
          :enum-options="enumOptions"
          :enum-loading="enumsLoading"
          :enum-error="enumsError"
          @enum-open="loadEnums"
        />
        <el-form-item
          v-if="form.typeCategory === 2 && selectedBusinessType?.description"
          label="类型描述"
          ><div class="form-readonly textarea">
            {{ selectedBusinessType.description }}
          </div></el-form-item
        >
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="open = false">取 消</el-button
          ><el-button type="primary" size="mini" @click="save">确 定</el-button>
        </div></template
      >
    </el-dialog>

    <el-dialog
      v-model="detailOpen"
      title="属性详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form class="attribute-dialog-form" label-width="110px">
        <div class="h2-title">基础信息</div>
        <el-form-item label="所属对象类型"
          ><div class="form-readonly">
            {{ objectName(detail.objectCandidateId) }}
          </div></el-form-item
        >
        <el-form-item label="属性名称"
          ><div class="form-readonly">
            {{ detail.name || "-" }}
          </div></el-form-item
        >
        <el-form-item label="属性代码"
          ><div class="form-readonly">
            {{ detail.code || "-" }}
          </div></el-form-item
        >
        <el-form-item label="描述"
          ><div class="form-readonly textarea">
            {{ detail.description || "-" }}
          </div></el-form-item
        >
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型"
          ><div class="form-readonly">
            {{ dataTypeLabel(detail.typeCategory) }}
          </div></el-form-item
        >
        <el-form-item label="类型配置"
          ><div class="form-readonly">
            {{ typeSummary(detail) }}
          </div></el-form-item
        >
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div></template
      >
    </el-dialog>
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import {
  listAiAttributeBusinessTypes,
  listAiAttributeEnums,
  listCandidates,
  updateCandidate,
} from "@/api/om/aiModeling";
import AttributeTypeConfig from "../../om/object/type/detail/AttributeTypeConfig.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, om_attribute_data_type, basic_data_type } =
  proxy.useDict(
    "om_ai_adopt_status",
    "om_attribute_data_type",
    "basic_data_type"
  );
const pageRef = ref();
const formRef = ref();
const open = ref(false);
const detailOpen = ref(false);
const form = ref({});
const columns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "name", label: "属性名称", visible: true },
  { key: "code", label: "属性代码", visible: true },
  { key: "objectType", label: "所属对象类型", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "dataType", label: "数据类型", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (key) =>
  columns.value.find((item) => item.key === key)?.visible !== false;

const detail = ref({});
const objects = ref([]);
const businessTypes = ref([]);
const enumOptions = ref([]);
const enumsLoading = ref(false);
const enumsError = ref("");
const dataTypeOptions = computed(() =>
  om_attribute_data_type.value.map((item) => ({
    label: item.label,
    value: Number(item.value),
  }))
);
const businessTypeGroups = computed(() =>
  [
    { source: "PROJECT", label: "当前项目" },
    { source: "GLOBAL", label: "全局" },
  ]
    .map((group) => ({
      ...group,
      options: businessTypes.value.filter(
        (item) => item.businessTypeSource === group.source
      ),
    }))
    .filter((group) => group.options.length)
);
const selectedBusinessType = computed(() =>
  businessTypes.value.find(
    (item) =>
      String(item.id) === String(form.value.businessTypeId) &&
      item.businessTypeSource === (form.value.businessTypeSource || "GLOBAL")
  )
);
const effectiveConfig = computed(() =>
  form.value.typeCategory === 2 && selectedBusinessType.value
    ? {
        ...selectedBusinessType.value,
        basicTypeCode: Number(selectedBusinessType.value.dataType),
      }
    : form.value
);
const businessKey = computed({
  get: () =>
    form.value.businessTypeId
      ? `${form.value.businessTypeSource || "GLOBAL"}:${
          form.value.businessTypeId
        }`
      : undefined,
  set: (value) => {
    const parts = (value || "").split(":");
    form.value.businessTypeSource = parts[0] || "GLOBAL";
    form.value.businessTypeId = parts[1] || undefined;
  },
});
const required = (row) => {
  const missing = [];
  if (!row.objectCandidateId) missing.push("所属对象类型");
  if (!row.name?.trim()) missing.push("属性名称");
  if (!row.code?.trim()) missing.push("属性代码");
  if (![1, 2].includes(Number(row.typeCategory))) missing.push("数据类型");
  if (Number(row.typeCategory) === 1 && row.basicTypeCode == null)
    missing.push("基础数据类型");
  if (Number(row.typeCategory) === 2 && !row.businessTypeId)
    missing.push("业务数据类型");
  return missing;
};
const {
  rows,
  total,
  loading,
  selectedIds,
  query,
  evidenceVisible,
  evidences,
  load,
  select,
  handleSortChange,
  adoptOne,
  adoptBatch,
  remove,
  showEvidence,
} = useCandidateTab("attribute", props.taskId, required);
const rules = {
  objectCandidateId: [
    { required: true, message: "请选择所属对象类型", trigger: "change" },
  ],
  name: [
    {
      required: true,
      whitespace: true,
      message: "属性名称不能为空",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "属性代码不能为空", trigger: "blur" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "须以字母开头，仅允许字母、数字和下划线",
      trigger: "blur",
    },
  ],
  typeCategory: [
    { required: true, message: "请选择数据类型", trigger: "change" },
  ],
  basicTypeCode: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.typeCategory === 1 && value == null
            ? new Error("请选择基础数据类型")
            : undefined
        ),
      trigger: "change",
    },
  ],
  businessTypeId: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.typeCategory === 2 && !value
            ? new Error("请选择业务数据类型")
            : undefined
        ),
      trigger: "change",
    },
  ],
};
const selectable = (row) => row.adoptStatus !== 1;
const objectName = (id) =>
  objects.value.find((item) => item.id === id)?.name || "-";
const dataTypeLabel = (value) =>
  dataTypeOptions.value.find((item) => item.value === Number(value))?.label ||
  "-";
const basicTypeLabel = (value) =>
  basic_data_type.value.find((item) => Number(item.value) === Number(value))
    ?.label || "-";
const typeSummary = (row) => {
  if (Number(row.typeCategory) !== 2) return basicTypeLabel(row.basicTypeCode);
  const item = businessTypes.value.find(
    (type) =>
      String(type.id) === String(row.businessTypeId) &&
      type.businessTypeSource === (row.businessTypeSource || "GLOBAL")
  );
  return item
    ? `${item.name}（${
        item.businessTypeSource === "PROJECT" ? "当前项目" : "全局"
      }）`
    : "-";
};
function normalize(row) {
  return {
    ...row,
    typeCategory:
      row.typeCategory == null ? undefined : Number(row.typeCategory),
    basicTypeCode:
      row.basicTypeCode == null ? undefined : Number(row.basicTypeCode),
    decimalPlaces: row.decimalPlaces == null ? 2 : Number(row.decimalPlaces),
    businessTypeId:
      row.businessTypeId == null ? undefined : String(row.businessTypeId),
    businessTypeSource: row.businessTypeSource || "GLOBAL",
  };
}
function clearTypeConfig() {
  Object.assign(form.value, {
    unit: "",
    decimalPlaces: 2,
    format: "",
    enumType: undefined,
    enumSource: "PROJECT",
  });
}
function changeTypeCategory() {
  Object.assign(form.value, {
    basicTypeCode: undefined,
    businessTypeId: undefined,
    businessTypeSource: "GLOBAL",
  });
  clearTypeConfig();
  nextTick(() => formRef.value?.clearValidate());
}
async function loadObjects() {
  const response = await listCandidates("object", {
    taskId: props.taskId,
    pageNum: 1,
    pageSize: 1000,
  });
  objects.value = response.data?.rows || [];
}
async function loadTypeOptions() {
  businessTypes.value = [];
  enumOptions.value = [];
  await Promise.allSettled(
    ["PROJECT", "GLOBAL"].map(async (source) => {
      const response = await listAiAttributeBusinessTypes(props.taskId, source);
      businessTypes.value.push(
        ...(response.data || []).map((item) => ({
          ...item,
          id: String(item.id),
          businessTypeSource: source,
        }))
      );
    })
  );
}
async function loadEnums() {
  if (enumsLoading.value) return;
  enumsLoading.value = true;
  enumsError.value = "";
  try {
    enumOptions.value = [];
    await Promise.allSettled(
      ["PROJECT", "GLOBAL"].map(async (source) => {
        try {
          const response = await listAiAttributeEnums(props.taskId, source);
          enumOptions.value.push(
            ...(response.data || []).map((item) => ({
              ...item,
              enumSource: source,
            }))
          );
        } catch {
          enumsError.value = "部分枚举加载失败，可重新展开重试";
        }
      })
    );
  } finally {
    enumsLoading.value = false;
  }
}
async function edit(row) {
  form.value = normalize(row);
  open.value = true;
  await loadTypeOptions();
  await loadEnums();
  nextTick(() => formRef.value?.clearValidate());
}
function showDetail(row) {
  detail.value = normalize(row);
  detailOpen.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  const data = {
    ...form.value,
    taskId: props.taskId,
    businessTypeId:
      form.value.typeCategory === 2 ? Number(form.value.businessTypeId) : null,
    businessTypeSource:
      form.value.typeCategory === 2 ? form.value.businessTypeSource : null,
    basicTypeCode:
      form.value.typeCategory === 1 ? form.value.basicTypeCode : null,
  };
  await updateCandidate("attribute", data);
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  await load();
}
loadObjects();
load();
</script>
<style scoped>
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
.business-type-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.attribute-dialog-form :deep(.el-form-item__label) {
  white-space: nowrap;
  flex-shrink: 0;
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

