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
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button
          ></el-col
        ><el-col :span="1.5"
          ><el-button
            class="toggle-expand-all"
            type="primary"
            plain
            @click="toggleExpandAll"
            ><svg-icon
              :icon-class="isExpandAll ? 'toggle' : 'expand'"
            /><span>{{ isExpandAll ? "折叠" : "展开" }}</span></el-button
          ></el-col
        ></el-row
      >
      <right-toolbar :search="false" :columns="columns" @queryTable="load" />
    </div>
    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="tree"
      row-key="id"
      stripe
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @sort-change="handleSortChange"
      :default-sort="{ prop: 'createTime', order: 'descending' }"
      @selection-change="select"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
        :selectable="(row) => row.adoptStatus !== 1"
      />
      <el-table-column
        v-if="visible('name')"
        prop="name"
        label="对象类型名称"
        width="250"
        fixed="left"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.name || "-"
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
        v-if="visible('orderNum')"
        prop="orderNum"
        label="显示顺序"
        width="100"
        align="center"
        ><template #default="{ row }">{{
          row.orderNum ?? "-"
        }}</template></el-table-column
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
    </el-table>
    <el-dialog
      v-model="open"
      title="修改对象类型"
      width="1100px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col v-if="!editingRoot" :span="24"
            ><el-form-item label="上级对象类型"
              ><el-tree-select
                v-model="form.parentCandidateId"
                :data="parentOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                value-key="id"
                placeholder="请选择上级对象类型，留空则为顶级对象类型"
                check-strictly
                clearable
                style="width: 100%" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="对象类型名称" prop="name"
              ><div class="name-input">
                <el-input
                  v-model="form.name"
                  maxlength="128个字符"
                  placeholder="请输入对象类型名称"
                /><el-button type="primary" @click="termPickerOpen = true"
                  >从术语表中选择</el-button
                >
              </div></el-form-item
            ></el-col
          >
          <el-col :span="24"
            ><el-form-item label="显示顺序"
              ><el-input-number
                v-model="form.orderNum"
                :min="0"
                :precision="0"
                controls-position="right"
                style="width: 100%" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="描述"
              ><el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                maxlength="512个字符"
                show-word-limit
                placeholder="请输入对象类型描述" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="关联建模目标" prop="targetIds"
              ><el-button type="primary" plain @click="targetPickerOpen = true"
                >导入建模目标</el-button
              ></el-form-item
            >
            <div class="target-table">
              <TargetTable
                :targets="selectedTargets"
                editable
                @remove="removeTarget"
              /></div
          ></el-col>
        </el-row>
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button @click="open = false">取 消</el-button
          ><el-button type="primary" @click="save">确 定</el-button>
        </div></template
      >
    </el-dialog>
    <CandidateTargetPicker
      v-model="targetPickerOpen"
      :task-id="taskId"
      :selected-targets="selectedTargets"
      :append-to="pageRef"
      @confirm="addTargets"
    />
    <CandidateTermPicker
      v-model="termPickerOpen"
      :task-id="taskId"
      :append-to="pageRef"
      @confirm="applyTerm"
    />
    <el-dialog
      v-model="detailOpen"
      title="对象类型详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form label-width="110px"
        ><el-form-item label="对象类型名称"
          ><div class="form-readonly">
            {{ detail.name || "-" }}
          </div></el-form-item
        ><el-form-item v-if="hasParent(detail)" label="上级对象类型"
          ><div class="form-readonly">
            {{ objectName(detail.parentCandidateId) }}
          </div></el-form-item
        ><el-form-item label="显示顺序"
          ><div class="form-readonly">
            {{ detail.orderNum ?? "-" }}
          </div></el-form-item
        ><el-form-item label="描述"
          ><div class="form-readonly textarea">
            {{ detail.description || "-" }}
          </div></el-form-item
        ></el-form
      ><template #footer
        ><el-button @click="detailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>
<script setup>
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import {
  getCandidateTargets,
  saveCandidateTargets,
  updateCandidate,
} from "@/api/om/aiModeling";
import TargetTable from "../../om/object/type/TargetTable.vue";
import CandidateTargetPicker from "../components/CandidateTargetPicker.vue";
import CandidateTermPicker from "../components/CandidateTermPicker.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status } = proxy.useDict("om_ai_adopt_status");
const pageRef = ref();
const formRef = ref();
const open = ref(false);
const detailOpen = ref(false);
const targetPickerOpen = ref(false);
const termPickerOpen = ref(false);
const form = ref({});
const columns = ref([
  { key: "name", label: "对象类型名称", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "orderNum", label: "显示顺序", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (key) =>
  columns.value.find((item) => item.key === key)?.visible !== false;

const detail = ref({});
const selectedTargets = ref([]);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const editingRoot = ref(false);
const required = (row) => (!row.name?.trim() ? ["对象类型名称"] : []);
const {
  rows,
  loading,
  selectedIds,
  evidenceVisible,
  evidences,
  load,
  select,
  handleSortChange,
  adoptOne,
  adoptBatch,
  remove,
  showEvidence,
} = useCandidateTab("object", props.taskId, required, { pageSize: -1 });
const buildTree = (items) => {
  const nodes = new Map(
    items.map((item) => [String(item.id), { ...item, children: [] }])
  );
  const roots = [];
  for (const node of nodes.values()) {
    const parent = nodes.get(String(node.parentCandidateId));
    if (parent && parent !== node) parent.children.push(node);
    else roots.push(node);
  }
  return roots;
};
const tree = computed(() => buildTree(rows.value));
const parentOptions = computed(() =>
  buildTree(rows.value.filter((item) => item.id !== form.value.id))
);
const rules = {
  name: [{ required: true, message: "对象类型名称不能为空" }],
  targetIds: [
    {
      type: "array",
      required: true,
      min: 1,
      message: "请至少关联一个建模目标",
      trigger: "change",
    },
  ],
};
const objectName = (id) =>
  rows.value.find((item) => item.id === id)?.name || "-";
const hasParent = (row) =>
  row.parentCandidateId != null && String(row.parentCandidateId) !== "0";
async function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  await nextTick();
  refreshTable.value = true;
}
function addTargets(targets) {
  selectedTargets.value = [...selectedTargets.value, ...targets];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}
function removeTarget(row) {
  selectedTargets.value = selectedTargets.value.filter(
    (item) => String(item.id) !== String(row.id)
  );
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}
function applyTerm(term) {
  form.value.name = term.stdName || term.name || "";
  if (!form.value.description) form.value.description = term.description || "";
}
async function edit(row) {
  form.value = { ...row, orderNum: row.orderNum ?? 0, targetIds: [] };
  editingRoot.value =
    row.parentCandidateId == null || String(row.parentCandidateId) === "0";
  const response = await getCandidateTargets("object", row.id);
  selectedTargets.value = response.data || [];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
  open.value = true;
}
async function showDetail(row) {
  detail.value = row;
  detailOpen.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  await updateCandidate("object", {
    id: form.value.id,
    taskId: props.taskId,
    name: form.value.name,
    description: form.value.description,
    parentCandidateId: form.value.parentCandidateId,
    orderNum: form.value.orderNum,
  });
  await saveCandidateTargets("object", form.value.id, form.value.targetIds);
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  load();
}
load();
</script>

<style scoped lang="scss">
.name-input {
  display: flex;
  gap: 12px;
  width: 100%;
}

.target-table {
  margin: 0 0 10px 120px;
}

.candidate-tab-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

@media (max-width: 600px) {
  .target-table {
    margin-left: 0;
  }

  .name-input {
    flex-wrap: wrap;
  }
}
</style>

