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
  <div ref="pageRef" class="candidate-tab-page enum-page">
    <template v-if="!currentType">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style"
          ><el-col :span="1.5"
            ><el-button
              type="primary"
              plain
              :disabled="!selectedIds.length"
              @click="adoptBatch"
              >批量采纳</el-button
            ></el-col
          ><el-col :span="1.5"
            ><el-button
              type="danger"
              plain
              :disabled="!selectedIds.length"
              @click="remove()"
              ><i class="iconfont-mini icon-shanchu-huise mr5"></i
              >删除</el-button
            ></el-col
          ></el-row
        >
        <right-toolbar
          :search="false"
          :columns="typeColumns"
          @queryTable="loadTypes"
        />
      </div>
      <el-table
        v-loading="loading"
        :data="rows"
        stripe
        :default-sort="{ prop: 'id', order: 'descending' }"
        @selection-change="select"
        @sort-change="handleSortChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
          :selectable="(row) => row.adoptStatus !== 1"
        />
        <el-table-column
          v-if="visible(typeColumns, 'id')"
          prop="id"
          label="编号"
          width="80"
          align="center"
          sortable="custom"
        />
        <el-table-column
          v-if="visible(typeColumns, 'enumName')"
          prop="enumName"
          label="枚举名称"
          min-width="150"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="{ row }">{{
            row.enumName || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="visible(typeColumns, 'enumType')"
          prop="enumType"
          label="枚举类型"
          min-width="160"
          :show-overflow-tooltip="{ effect: 'light' }"
          ><template #default="{ row }"
            ><el-button link type="primary" @click="openValues(row)">{{
              row.enumType || "-"
            }}</el-button></template
          ></el-table-column
        >
        <el-table-column
          v-if="visible(typeColumns, 'status')"
          label="状态"
          width="100"
          align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="sys_normal_disable"
              :value="row.status || '0'" /></template
        ></el-table-column>
        <el-table-column
          v-if="visible(typeColumns, 'adoptStatus')"
          label="采纳状态"
          width="110"
          align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="om_ai_adopt_status"
              :value="row.adoptStatus" /></template
        ></el-table-column>
        <el-table-column
          v-if="visible(typeColumns, 'createBy')"
          prop="createBy"
          label="创建人"
          width="120"
          align="center"
          ><template #default="{ row }">{{
            row.createBy || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="visible(typeColumns, 'createTime')"
          prop="createTime"
          label="创建时间"
          width="170"
          align="center"
          sortable="custom"
          ><template #default="{ row }">{{
            proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
          }}</template></el-table-column
        >
        <el-table-column
          v-if="visible(typeColumns, 'operation')"
          label="操作"
          width="260"
          align="center"
          fixed="right"
          class-name="small-padding fixed-width"
          ><template #default="{ row }"
            ><el-button
              link
              type="primary"
              icon="Check"
              :disabled="row.adoptStatus === 1"
              @click="adoptOne(row)"
              >采纳</el-button
            ><el-button
              link
              type="primary"
              icon="Document"
              @click="showEvidence(row)"
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
                  @click="showTypeDetail(row)"
                  >详情</el-button
                ><el-button
                  link
                  type="primary"
                  icon="Edit"
                  :disabled="row.adoptStatus === 1"
                  @click="editType(row)"
                  >修改</el-button
                ><el-button
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.adoptStatus === 1"
                  @click="remove(row)"
                  >删除</el-button
                >
              </div></el-popover
            ></template
          ></el-table-column
        >
        <template #empty
          ><div class="emptyBg">
            <img
              src="@/assets/system/images/no_data/noData.png"
              alt="暂无记录"
            />
            <p>暂无记录</p>
          </div></template
        >
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        @pagination="loadTypes"
      />
    </template>

    <template v-else>
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style"
          ><el-col :span="1.5"
            ><el-button
              type="danger"
              plain
              :disabled="!valueSelectedIds.length"
              @click="removeValue()"
              ><i class="iconfont-mini icon-shanchu-huise mr5"></i
              >删除</el-button
            ></el-col
          >
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Close" @click="closeValues"
              >关闭</el-button
            >
          </el-col> </el-row
        ><right-toolbar
          :search="false"
          :columns="valueColumns"
          @queryTable="loadValues"
        />
      </div>
      <el-table
        v-loading="valueLoading"
        :data="valueRows"
        stripe
        :default-sort="{ prop: 'enumSort', order: 'ascending' }"
        @selection-change="selectValues"
        @sort-change="handleValueSort"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
          :selectable="(row) => row.adoptStatus !== 1"
        /><el-table-column
          v-if="visible(valueColumns, 'id')"
          prop="id"
          label="编号"
          width="80"
          align="center"
          sortable="custom"
        /><el-table-column
          v-if="visible(valueColumns, 'enumLabel')"
          prop="enumLabel"
          label="枚举标签"
          min-width="150"
          ><template #default="{ row }">{{
            row.enumLabel || "-"
          }}</template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'enumValue')"
          prop="enumValue"
          label="枚举键值"
          min-width="150"
          ><template #default="{ row }">{{
            row.enumValue || "-"
          }}</template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'enumSort')"
          prop="enumSort"
          label="排序"
          width="100"
          align="center"
          sortable="custom"
        /><el-table-column
          v-if="visible(valueColumns, 'status')"
          label="状态"
          width="100"
          align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="sys_normal_disable"
              :value="row.status || '0'" /></template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'adoptStatus')"
          label="采纳状态"
          width="110"
          align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="om_ai_adopt_status"
              :value="row.adoptStatus" /></template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'createBy')"
          prop="createBy"
          label="创建人"
          width="120"
          align="center"
          ><template #default="{ row }">{{
            row.createBy || "-"
          }}</template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'createTime')"
          prop="createTime"
          label="创建时间"
          width="170"
          sortable="custom"
          align="center"
          ><template #default="{ row }">{{
            proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
          }}</template></el-table-column
        ><el-table-column
          v-if="visible(valueColumns, 'operation')"
          label="操作"
          width="200"
          align="center"
          fixed="right"
          class-name="small-padding fixed-width"
          ><template #default="{ row }"
            ><el-button
              link
              type="primary"
              icon="Document"
              @click="showValueEvidence(row)"
              >来源</el-button
            >
            <el-button
              link
              type="primary"
              icon="View"
              @click="showValueDetail(row)"
              >详情</el-button
            >
            <el-popover placement="bottom" :width="120" trigger="click">
              <template #reference
                ><el-button link type="primary" icon="ArrowDown"
                  >更多</el-button
                ></template
              >
              <div class="butgdlist">
                <el-button
                  link
                  type="primary"
                  icon="Edit"
                  :disabled="row.adoptStatus === 1"
                  @click="editValue(row)"
                  >修改</el-button
                >
                <el-button
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.adoptStatus === 1"
                  @click="removeValue(row)"
                  >删除</el-button
                >
              </div>
            </el-popover></template
          ></el-table-column
        > </el-table
      ><pagination
        v-show="valueTotal > 0"
        :total="valueTotal"
        v-model:page="valueQuery.pageNum"
        v-model:limit="valueQuery.pageSize"
        @pagination="loadValues"
      />
    </template>

    <el-dialog
      v-model="typeOpen"
      :title="typeForm.id ? '修改枚举类型' : '新增枚举类型'"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="90px"
        ><el-form-item label="枚举名称" prop="enumName"
          ><el-input
            v-model="typeForm.enumName"
            placeholder="请输入枚举名称"
            maxlength="128个字符" /></el-form-item
        ><el-form-item label="枚举类型" prop="enumType"
          ><el-input
            v-model="typeForm.enumType"
            placeholder="请输入枚举类型"
            maxlength="100个字符" /></el-form-item
        ><el-form-item label="状态" prop="status"
          ><el-radio-group v-model="typeForm.status"
            ><el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            ></el-radio-group
          ></el-form-item
        ><el-form-item label="备注" prop="remark"
          ><el-input
            v-model="typeForm.remark"
            type="textarea"
            :rows="3"
            maxlength="512个字符"
            show-word-limit
            placeholder="请输入备注" /></el-form-item></el-form
      ><template #footer
        ><div class="dialog-footer">
          <el-button @click="typeOpen = false">取 消</el-button
          ><el-button type="primary" @click="saveType">确 定</el-button>
        </div></template
      ></el-dialog
    >
    <el-dialog
      v-model="typeDetailOpen"
      title="枚举类型详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><DetailForm
        :row="typeDetail"
        :fields="typeDetailFields"
        :status-options="sys_normal_disable"
      /><template #footer
        ><el-button @click="typeDetailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <el-dialog
      v-model="valueOpen"
      :title="valueForm.id ? '修改枚举数据' : '新增枚举数据'"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form
        ref="valueFormRef"
        :model="valueForm"
        :rules="valueRules"
        label-width="90px"
        ><el-form-item label="枚举类型"
          ><el-input
            :model-value="currentType?.enumType || '-'"
            disabled /></el-form-item
        ><el-form-item label="枚举标签" prop="enumLabel"
          ><el-input
            v-model="valueForm.enumLabel"
            placeholder="请输入枚举标签"
            maxlength="128个字符" /></el-form-item
        ><el-form-item label="枚举键值" prop="enumValue"
          ><el-input
            v-model="valueForm.enumValue"
            placeholder="请输入枚举键值"
            maxlength="128个字符" /></el-form-item
        ><el-form-item label="排序" prop="enumSort"
          ><el-input-number
            v-model="valueForm.enumSort"
            :min="0"
            controls-position="right" /></el-form-item
        ><el-form-item label="状态" prop="status"
          ><el-radio-group v-model="valueForm.status"
            ><el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            ></el-radio-group
          ></el-form-item
        ><el-form-item label="备注" prop="remark"
          ><el-input
            v-model="valueForm.remark"
            type="textarea"
            :rows="3"
            maxlength="512个字符"
            show-word-limit
            placeholder="请输入备注" /></el-form-item></el-form
      ><template #footer
        ><div class="dialog-footer">
          <el-button @click="valueOpen = false">取 消</el-button
          ><el-button type="primary" @click="saveValue">确 定</el-button>
        </div></template
      ></el-dialog
    >
    <el-dialog
      v-model="valueDetailOpen"
      title="枚举数据详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><DetailForm
        :row="valueDetail"
        :fields="valueDetailFields"
        :status-options="sys_normal_disable"
      /><template #footer
        ><el-button @click="valueDetailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <CandidateEvidenceDrawer
      v-model="evidenceVisible"
      :evidences="evidences"
    /><CandidateEvidenceDrawer
      v-model="valueEvidenceVisible"
      :evidences="valueEvidences"
    />
  </div>
</template>

<script setup>
import {
  createCandidate,
  deleteCandidate,
  getCandidateEvidence,
  listCandidates,
  updateCandidate,
} from "@/api/om/aiModeling";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import DetailForm from "../components/EnumCandidateDetailForm.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, sys_normal_disable } = proxy.useDict(
  "om_ai_adopt_status",
  "sys_normal_disable"
);
const pageRef = ref(),
  typeFormRef = ref(),
  valueFormRef = ref();
const currentType = ref(null);
const typeOpen = ref(false),
  typeDetailOpen = ref(false),
  valueOpen = ref(false),
  valueDetailOpen = ref(false);
const typeForm = ref({}),
  typeDetail = ref({}),
  valueForm = ref({}),
  valueDetail = ref({});
const valueRows = ref([]),
  valueTotal = ref(0),
  valueLoading = ref(false),
  valueSelectedIds = ref([]);
const valueEvidenceVisible = ref(false),
  valueEvidences = ref([]);
const typeFilters = reactive({ enumName: "", enumType: "", status: "" });
const valueFilters = reactive({ enumLabel: "", status: "" });
const valueQuery = reactive({
  taskId: props.taskId,
  enumTypeId: null,
  pageNum: 1,
  pageSize: 10,
  orderByColumn: "enumSort",
  isAsc: "ascending",
});
const typeColumns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "enumName", label: "枚举名称", visible: true },
  { key: "enumType", label: "枚举类型", visible: true },
  { key: "status", label: "状态", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const valueColumns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "enumLabel", label: "枚举标签", visible: true },
  { key: "enumValue", label: "枚举键值", visible: true },
  { key: "enumSort", label: "排序", visible: true },
  { key: "status", label: "状态", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const required = (row) =>
  [
    ["enumName", "枚举名称"],
    ["enumType", "枚举类型"],
    ["status", "状态"],
  ]
    .filter(([key]) => row[key] == null || row[key] === "")
    .map(([, label]) => label);
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
} = useCandidateTab("enum_type", props.taskId, required);
const typeRules = {
  enumName: [{ required: true, message: "枚举名称不能为空" }],
  enumType: [
    { required: true, message: "枚举类型不能为空" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "须以字母开头，仅允许字母、数字和下划线",
    },
  ],
  status: [{ required: true, message: "请选择状态" }],
};
const valueRules = {
  enumLabel: [{ required: true, message: "枚举标签不能为空" }],
  enumValue: [{ required: true, message: "枚举键值不能为空" }],
  enumSort: [{ required: true, message: "排序不能为空" }],
  status: [{ required: true, message: "请选择状态" }],
};
const typeDetailFields = [
  { prop: "id", label: "编号" },
  { prop: "enumName", label: "枚举名称" },
  { prop: "enumType", label: "枚举类型" },
  { prop: "status", label: "状态" },
  { prop: "createBy", label: "创建人" },
  { prop: "createTime", label: "创建时间" },
  { prop: "remark", label: "备注" },
];
const valueDetailFields = [
  { prop: "id", label: "编号" },
  { prop: "enumType", label: "枚举类型" },
  { prop: "enumLabel", label: "枚举标签" },
  { prop: "enumValue", label: "枚举键值" },
  { prop: "enumSort", label: "排序" },
  { prop: "status", label: "状态" },
  { prop: "createBy", label: "创建人" },
  { prop: "createTime", label: "创建时间" },
  { prop: "remark", label: "备注" },
];
function visible(columns, key) {
  return columns.find((item) => item.key === key)?.visible !== false;
}
async function loadTypes() {
  Object.assign(query, typeFilters);
  await load();
}
function editType(row) {
  typeForm.value = { ...row, status: row.status || "0" };
  typeOpen.value = true;
}
function showTypeDetail(row) {
  typeDetail.value = row;
  typeDetailOpen.value = true;
}
async function saveType() {
  if (!(await typeFormRef.value.validate().catch(() => false))) return;
  const payload = {
    id: typeForm.value.id,
    taskId: props.taskId,
    enumName: typeForm.value.enumName,
    enumType: typeForm.value.enumType,
    status: typeForm.value.status,
    remark: typeForm.value.remark,
  };
  if (payload.id) await updateCandidate("enum_type", payload);
  else await createCandidate("enum_type", payload);
  proxy.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功");
  typeOpen.value = false;
  loadTypes();
}
function openValues(row) {
  currentType.value = row;
  valueQuery.enumTypeId = row.id;
  valueQuery.pageNum = 1;
  loadValues();
}
function closeValues() {
  currentType.value = null;
  valueRows.value = [];
  valueTotal.value = 0;
}
async function loadValues() {
  if (!currentType.value) return;
  valueLoading.value = true;
  try {
    const response = await listCandidates("enum_data", {
      ...valueQuery,
      ...valueFilters,
    });
    valueRows.value = response.data?.rows || [];
    valueTotal.value = Number(response.data?.total || 0);
    valueSelectedIds.value = [];
  } finally {
    valueLoading.value = false;
  }
}
function handleValueSort({ prop, order }) {
  valueQuery.orderByColumn = order ? prop : "enumSort";
  valueQuery.isAsc = order || "ascending";
  valueQuery.pageNum = 1;
  loadValues();
}
function selectValues(selection) {
  valueSelectedIds.value = selection
    .filter((row) => row.adoptStatus !== 1)
    .map((row) => row.id);
}
function editValue(row) {
  valueForm.value = { ...row, status: row.status || "0" };
  valueOpen.value = true;
}
function showValueDetail(row) {
  valueDetail.value = { ...row, enumType: currentType.value?.enumType };
  valueDetailOpen.value = true;
}
async function saveValue() {
  if (!(await valueFormRef.value.validate().catch(() => false))) return;
  const payload = {
    id: valueForm.value.id,
    taskId: props.taskId,
    enumTypeId: currentType.value.id,
    enumSort: valueForm.value.enumSort,
    enumLabel: valueForm.value.enumLabel,
    enumValue: valueForm.value.enumValue,
    status: valueForm.value.status,
    remark: valueForm.value.remark,
  };
  if (payload.id) await updateCandidate("enum_data", payload);
  else await createCandidate("enum_data", payload);
  proxy.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功");
  valueOpen.value = false;
  loadValues();
}
async function removeValue(row) {
  const ids = row ? [row.id] : valueSelectedIds.value;
  if (!ids.length) return;
  await proxy.$modal.confirm(
    `是否确认删除选中的 ${ids.length} 条候选枚举数据？`
  );
  await deleteCandidate("enum_data", ids.join(","));
  proxy.$modal.msgSuccess("删除成功");
  loadValues();
}
async function showValueEvidence(row) {
  const response = await getCandidateEvidence("enum_data", row.id);
  valueEvidences.value = response.data || [];
  valueEvidenceVisible.value = true;
}
loadTypes();
</script>

<style scoped lang="scss">
.enum-search {
  margin-bottom: 15px;
  padding: 15px;
}
.value-page-title {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}
.butgdlist {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.butgdlist .el-button {
  width: 100%;
  justify-content: center;
  margin-left: 0;
}
:deep(.el-input-number) {
  width: 100%;
}
</style>

