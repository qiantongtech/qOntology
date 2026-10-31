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
            ><i class="iconfont-mini icon-queren mr5"></i>批量采纳</el-button
          ></el-col
        ><el-col :span="1.5"
          ><el-button
            type="danger"
            plain
            :disabled="!selectedIds.length"
            @click="remove()"
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button
          ></el-col
        ></el-row
      >
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
        v-if="visible('startType')"
        label="起点类型"
        min-width="150"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          objectName(row.startObjectCandidateId)
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('name')"
        prop="name"
        label="关系名称"
        min-width="160"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.name || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('endType')"
        label="终点类型"
        min-width="150"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          objectName(row.endObjectCandidateId)
        }}</template></el-table-column
      >
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
        v-if="visible('reversibleFlag')"
        label="是否可逆"
        width="100"
        align="center"
        ><template #default="{ row }">{{
          reversibleLabel(row.reversibleFlag)
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('reverseName')"
        prop="reverseName"
        label="反向名称"
        min-width="150"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.reverseName || "-"
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
      <template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" alt="暂无关系" />
          <p>暂无关系</p>
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
      title="修改关系"
      width="1100px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
      @closed="formRef?.clearValidate()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24"
            ><el-form-item label="起点类型" prop="startObjectCandidateId"
              ><el-select
                v-model="form.startObjectCandidateId"
                placeholder="请选择起点类型"
                clearable
                style="width: 100%"
                ><el-option
                  v-for="item in objects"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id" /></el-select></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="关系名称" prop="name"
              ><el-input
                v-model="form.name"
                placeholder="请输入关系名称"
                maxlength="128个字符" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="终点类型" prop="endObjectCandidateId"
              ><el-select
                v-model="form.endObjectCandidateId"
                placeholder="请选择终点类型"
                clearable
                style="width: 100%"
                ><el-option
                  v-for="item in objects"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id" /></el-select></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="是否可逆" prop="reversibleFlag"
              ><el-radio-group
                v-model="form.reversibleFlag"
                @change="(value) => value !== 1 && (form.reverseName = '')"
                ><el-radio
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :label="dict.value === 'Y' ? 1 : 0"
                  >{{ dict.label }}</el-radio
                ></el-radio-group
              ></el-form-item
            ></el-col
          >
          <el-col v-if="form.reversibleFlag === 1" :span="24"
            ><el-form-item label="反向名称" prop="reverseName" required
              ><el-input
                v-model="form.reverseName"
                placeholder="请输入反向名称"
                maxlength="128个字符" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="描述" prop="description"
              ><el-input
                v-model="form.description"
                class="character-count"
                type="textarea"
                :rows="4"
                placeholder="请输入描述"
                maxlength="512个字符"
                show-word-limit /></el-form-item
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
              />
            </div>
          </el-col>
        </el-row>
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="open = false">取 消</el-button
          ><el-button type="primary" size="mini" @click="save">确 定</el-button>
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
    <el-dialog
      v-model="detailOpen"
      title="关系详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form label-width="110px"
        ><el-form-item label="起点类型"
          ><div class="form-readonly">
            {{ objectName(detail.startObjectCandidateId) }}
          </div></el-form-item
        ><el-form-item label="关系名称"
          ><div class="form-readonly">
            {{ detail.name || "-" }}
          </div></el-form-item
        ><el-form-item label="终点类型"
          ><div class="form-readonly">
            {{ objectName(detail.endObjectCandidateId) }}
          </div></el-form-item
        ><el-form-item label="是否可逆"
          ><div class="form-readonly">
            {{ reversibleLabel(detail.reversibleFlag) }}
          </div></el-form-item
        ><el-form-item v-if="detail.reversibleFlag === 1" label="反向名称"
          ><div class="form-readonly">
            {{ detail.reverseName || "-" }}
          </div></el-form-item
        ><el-form-item label="描述"
          ><div class="form-readonly textarea">
            {{ detail.description || "-" }}
          </div></el-form-item
        ></el-form
      ><template #footer
        ><div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div></template
      ></el-dialog
    >
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { getCurrentInstance, nextTick, ref } from "vue";
import {
  getCandidateTargets,
  listCandidates,
  saveCandidateTargets,
  updateCandidate,
} from "@/api/om/aiModeling";
import TargetTable from "../../om/object/type/TargetTable.vue";
import CandidateTargetPicker from "../components/CandidateTargetPicker.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, sys_yes_no } = proxy.useDict(
  "om_ai_adopt_status",
  "sys_yes_no"
);
const pageRef = ref();
const formRef = ref();
const open = ref(false);
const detailOpen = ref(false);
const form = ref({});
const columns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "startType", label: "起点类型", visible: true },
  { key: "name", label: "关系名称", visible: true },
  { key: "endType", label: "终点类型", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "reversibleFlag", label: "是否可逆", visible: true },
  { key: "reverseName", label: "反向名称", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (key) =>
  columns.value.find((item) => item.key === key)?.visible !== false;

const detail = ref({});
const objects = ref([]);
const targetPickerOpen = ref(false);
const selectedTargets = ref([]);
const required = (row) => {
  const missing = [];
  if (!row.startObjectCandidateId) missing.push("起点类型");
  if (!row.name?.trim()) missing.push("关系名称");
  if (!row.endObjectCandidateId) missing.push("终点类型");
  if (![0, 1].includes(Number(row.reversibleFlag))) missing.push("是否可逆");
  if (Number(row.reversibleFlag) === 1 && !row.reverseName?.trim())
    missing.push("反向名称");
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
} = useCandidateTab("relation", props.taskId, required);
const rules = {
  startObjectCandidateId: [
    { required: true, message: "请选择起点类型", trigger: "change" },
  ],
  name: [
    {
      required: true,
      whitespace: true,
      message: "关系名称不能为空",
      trigger: "blur",
    },
    { max: 128, message: "关系名称不能超过128个字符", trigger: "blur" },
  ],
  endObjectCandidateId: [
    { required: true, message: "请选择终点类型", trigger: "change" },
  ],
  reversibleFlag: [
    { required: true, message: "请选择是否可逆", trigger: "change" },
  ],
  reverseName: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.reversibleFlag === 1 && !value?.trim()
            ? new Error("可逆关系的反向名称不能为空")
            : undefined
        ),
      trigger: "blur",
    },
  ],
  description: [
    { max: 512, message: "关系描述不能超过512个字符", trigger: "blur" },
  ],
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
  objects.value.find((item) => item.id === id)?.name || "-";
const reversibleLabel = (value) =>
  sys_yes_no.value.find(
    (item) => item.value === (Number(value) === 1 ? "Y" : "N")
  )?.label || "-";
async function loadObjects() {
  const response = await listCandidates("object", {
    taskId: props.taskId,
    pageNum: 1,
    pageSize: 1000,
  });
  objects.value = response.data?.rows || [];
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
async function edit(row) {
  form.value = {
    ...row,
    reversibleFlag: Number(row.reversibleFlag ?? 0),
    targetIds: [],
  };
  const response = await getCandidateTargets("relation", row.id);
  selectedTargets.value = response.data || [];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
  open.value = true;
  nextTick(() => formRef.value?.clearValidate());
}
async function showDetail(row) {
  detail.value = { ...row };
  detailOpen.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  await updateCandidate("relation", {
    id: form.value.id,
    taskId: props.taskId,
    name: form.value.name.trim(),
    description: (form.value.description || "").trim(),
    startObjectCandidateId: form.value.startObjectCandidateId,
    endObjectCandidateId: form.value.endObjectCandidateId,
    reversibleFlag: form.value.reversibleFlag,
    reverseName:
      form.value.reversibleFlag === 1 ? form.value.reverseName?.trim() : "",
  });
  await saveCandidateTargets("relation", form.value.id, form.value.targetIds);
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  await load();
}
loadObjects();
load();
</script>

<style scoped lang="scss">
.target-table {
  margin: 0 0 10px 120px;
}
@media (max-width: 600px) {
  .target-table {
    margin-left: 0;
  }
}
.character-count :deep(.el-input__count) {
  background: transparent;
}
</style>

