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
        v-if="visible('question')"
        prop="question"
        label="目标问题"
        min-width="260"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.question || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('questionSource')"
        prop="questionSource"
        label="问题来源"
        width="220"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.questionSource || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('businessScenarios')"
        prop="businessScenarios"
        label="业务场景"
        width="220"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.businessScenarios || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('priority')"
        label="优先级"
        width="100"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_target_priority"
            :value="String(row.priority || 1)" /></template
      ></el-table-column>
      <el-table-column
        v-if="visible('acceptStatus')"
        label="验证状态"
        width="100"
        align="center"
        ><template #default="{ row }"
          ><dict-tag
            :options="om_accept_status"
            :value="row.acceptStatus || '0'" /></template
      ></el-table-column>
      <el-table-column
        v-if="visible('relatedElementCount')"
        prop="relatedElementCount"
        label="关联元素"
        width="100"
        align="center"
        ><template #default="{ row }">{{
          row.relatedElementCount ?? 0
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
                @click="
                  detail = row;
                  detailOpen = true;
                "
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
          <img src="@/assets/system/images/no_data/noData.png" />
          <p>暂无记录</p>
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
      title="修改建模目标"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="目标问题" prop="question"
          ><el-input
            v-model="form.question"
            type="textarea"
            maxlength="200个字符"
            show-word-limit
            placeholder="请输入目标问题"
        /></el-form-item>
        <el-form-item label="问题来源" prop="questionSource"
          ><el-input
            v-model="form.questionSource"
            type="textarea"
            maxlength="200个字符"
            show-word-limit
            placeholder="请输入问题来源"
        /></el-form-item>
        <el-form-item label="业务场景" prop="businessScenarios"
          ><el-input
            v-model="form.businessScenarios"
            type="textarea"
            maxlength="200个字符"
            show-word-limit
            placeholder="请输入业务场景"
        /></el-form-item>
        <el-form-item label="验收标准" prop="acceptCriterion"
          ><el-input
            v-model="form.acceptCriterion"
            type="textarea"
            maxlength="200个字符"
            show-word-limit
            placeholder="请输入验收标准"
        /></el-form-item>
        <el-form-item label="优先级" prop="priority"
          ><el-radio-group v-model="form.priority"
            ><el-radio
              v-for="dict in om_target_priority"
              :key="dict.value"
              :value="Number(dict.value)"
              >{{ dict.label }}</el-radio
            ></el-radio-group
          ></el-form-item
        >
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button @click="open = false">取 消</el-button
          ><el-button type="primary" @click="save">确 定</el-button>
        </div></template
      >
    </el-dialog>
    <el-dialog
      v-model="detailOpen"
      title="建模目标详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form label-width="90px"
        ><el-form-item
          v-for="field in detailFields"
          :key="field.prop"
          :label="field.label"
          ><div class="form-readonly" :class="{ textarea: field.textarea }">
            <template v-if="field.prop === 'priority'"
              ><dict-tag
                :options="om_target_priority"
                :value="String(detail.priority || 1)" /></template
            ><template v-else-if="field.prop === 'acceptStatus'"
              ><dict-tag
                :options="om_accept_status"
                :value="detail.acceptStatus || '0'" /></template
            ><template v-else>{{ detail[field.prop] ?? "-" }}</template>
          </div></el-form-item
        ></el-form
      >
      <template #footer
        ><el-button @click="detailOpen = false">关闭</el-button></template
      >
    </el-dialog>
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { updateCandidate } from "@/api/om/aiModeling";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, om_target_priority, om_accept_status } =
  proxy.useDict("om_ai_adopt_status", "om_target_priority", "om_accept_status");
const pageRef = ref();
const formRef = ref();
const open = ref(false);
const detailOpen = ref(false);
const detail = ref({});
const form = ref({});
const columns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "question", label: "目标问题", visible: true },
  { key: "questionSource", label: "问题来源", visible: true },
  { key: "businessScenarios", label: "业务场景", visible: true },
  { key: "priority", label: "优先级", visible: true },
  { key: "acceptStatus", label: "验证状态", visible: true },
  { key: "relatedElementCount", label: "关联元素", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (key) =>
  columns.value.find((item) => item.key === key)?.visible !== false;

const required = (row) =>
  [
    ["question", "目标问题"],
    ["questionSource", "问题来源"],
    ["businessScenarios", "业务场景"],
    ["acceptCriterion", "验收标准"],
    ["priority", "优先级"],
  ]
    .filter(([key]) => !row[key])
    .map(([, label]) => label);
const tab = useCandidateTab("target", props.taskId, required);
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
} = tab;
const rules = {
  question: [{ required: true, message: "目标问题不能为空" }],
  questionSource: [{ required: true, message: "问题来源不能为空" }],
  businessScenarios: [{ required: true, message: "业务场景不能为空" }],
  acceptCriterion: [{ required: true, message: "验收标准不能为空" }],
  priority: [{ required: true, message: "优先级不能为空" }],
};
const detailFields = [
  { prop: "id", label: "编号" },
  { prop: "question", label: "目标问题", textarea: true },
  { prop: "questionSource", label: "问题来源", textarea: true },
  { prop: "businessScenarios", label: "业务场景", textarea: true },
  { prop: "acceptCriterion", label: "验收标准", textarea: true },
  { prop: "priority", label: "优先级" },
  { prop: "acceptStatus", label: "验证状态" },
  { prop: "relatedElementCount", label: "关联元素" },
  { prop: "createBy", label: "创建人" },
  { prop: "createTime", label: "创建时间" },
];
function edit(row) {
  form.value = { ...row, priority: Number(row.priority || 1) };
  open.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  await updateCandidate("target", {
    id: form.value.id,
    taskId: form.value.taskId,
    question: form.value.question,
    questionSource: form.value.questionSource,
    businessScenarios: form.value.businessScenarios,
    priority: form.value.priority,
    acceptCriterion: form.value.acceptCriterion,
  });
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  load();
}
load();
</script>

