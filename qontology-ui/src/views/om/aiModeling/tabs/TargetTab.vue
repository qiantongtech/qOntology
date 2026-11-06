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
        <!-- 关联元素与 index 列表口径一致：有标签数据用标签组，候选暂无标签时回退展示数量。 -->
        <template #relatedElementCount="{ row }">
          <qt-tag-group
            v-if="row.relatedElementTags?.length"
            :items="row.relatedElementTags"
            type="none"
            :max-count="2"
          />
          <span v-else>{{ row.relatedElementCount ?? 0 }}</span>
        </template>
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
        <template #priority="{ row }">
          <dict-tag
            :options="om_target_priority"
            :value="String(row.priority || 1)"
          />
        </template>
        <template #acceptStatus="{ row }">
          <dict-tag :options="om_accept_status" :value="row.acceptStatus || '0'" />
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
              @click="openDetail(row)"
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
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改建模目标对话框 -->
    <el-dialog
      v-model="open"
      title="修改建模目标"
      width="800px"
      :append-to="pageRef"
      :close-on-press-escape="!saving"
      draggable
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="目标问题" prop="question">
          <el-input
            v-model="form.question"
            type="textarea"
            :maxlength="256"
            show-word-limit
            placeholder="请输入目标问题"
          />
        </el-form-item>
        <el-form-item label="问题来源" prop="questionSource">
          <el-input
            v-model="form.questionSource"
            type="textarea"
            :maxlength="256"
            show-word-limit
            placeholder="请输入问题来源"
          />
        </el-form-item>
        <el-form-item label="业务场景" prop="businessScenarios">
          <el-input
            v-model="form.businessScenarios"
            type="textarea"
            :maxlength="256"
            show-word-limit
            placeholder="请输入业务场景"
          />
        </el-form-item>
        <el-form-item label="验收标准" prop="acceptCriterion">
          <el-input
            v-model="form.acceptCriterion"
            type="textarea"
            :maxlength="256"
            show-word-limit
            placeholder="请输入验收标准"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio
              v-for="dict in om_target_priority"
              :key="dict.value"
              :value="Number(dict.value)"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="open = false">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="save"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 建模目标详情对话框 -->
    <el-dialog
      v-model="detailOpen"
      title="建模目标详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form label-width="90px">
        <el-form-item
          v-for="field in detailFields"
          :key="field.prop"
          :label="field.label"
        >
          <div class="form-readonly" :class="{ textarea: field.textarea }">
            <template v-if="field.prop === 'priority'">
              <dict-tag
                :options="om_target_priority"
                :value="String(detail.priority || 1)"
              />
            </template>
            <template v-else-if="field.prop === 'acceptStatus'">
              <dict-tag
                :options="om_accept_status"
                :value="detail.acceptStatus || '0'"
              />
            </template>
            <template v-else-if="field.prop === 'createBy'">
              {{ detail.createBy || "-" }}
            </template>
            <template v-else-if="field.prop === 'contact'">
              {{ (detail.creatorId != null && creatorPhones[String(detail.creatorId)]) ? creatorPhones[String(detail.creatorId)] : "-" }}
            </template>
            <template v-else>{{ detail[field.prop] ?? "-" }}</template>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { updateCandidate } from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, om_target_priority, om_accept_status } =
  proxy.useDict("om_ai_adopt_status", "om_target_priority", "om_accept_status");

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const detailOpen = ref(false);
const saving = ref(false);
const detail = ref({});
const form = ref({});

/**
 * 收集采纳前缺失的必填项名称，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
function required(row) {
  return [
    ["question", "目标问题"],
    ["questionSource", "问题来源"],
    ["businessScenarios", "业务场景"],
    ["acceptCriterion", "验收标准"],
    ["priority", "优先级"],
  ]
    .filter(([key]) => !row[key])
    .map(([, label]) => label);
}

const tab = useCandidateTab("target", props.taskId, required);
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
 * 关联元素口径与外部 omtarget/index 一致：后端已分别返回对象候选数与关系候选数，
 * 这里拼成「对象类型N / 对象类型关系N」两个标签，交给 qt-tag-group 渲染。
 * @param {Object} params QtTable 合并分页、排序后的查询参数
 * @returns {Promise<Object>} 补全标签后的候选列表响应
 */
async function loadTargets(params) {
  const response = await fetchRows(params);
  response.data.rows = (response.data.rows || []).map((row) => ({
    ...row,
    relatedElementTags: [
      `对象类型${row.objectElementCount ?? "-"}`,
      `对象类型关系${row.relationElementCount ?? "-"}`,
    ],
  }));
  return response;
}

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 页签内又没有搜索区，所以同时关闭操作栏的“隐藏搜索”按钮。
 */
const wrapConfig = { fullContent: false, actions: { table: { search: false } } };

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
      label: "目标问题",
      prop: "question",
      slot: "text",
      minWidth: 300,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "问题来源",
      prop: "questionSource",
      slot: "text",
      width: 250,
      minWidth: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "业务场景",
      prop: "businessScenarios",
      slot: "text",
      width: 250,
      minWidth: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "关联元素",
      prop: "relatedElementCount",
      slot: "relatedElementCount",
      minWidth: 240,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "优先级",
      prop: "priority",
      slot: "priority",
      width: 100,
      minWidth: 100,
      align: "center",
    },
    {
      label: "验证状态",
      prop: "acceptStatus",
      slot: "acceptStatus",
      width: 100,
      minWidth: 100,
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
      width: 180,
      minWidth: 180,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 240,
      minWidth: 240,
      align: "center",
      fixed: "right",
    },
  ],
  func: loadTargets,
});

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
  { prop: "contact", label: "联系方式" },
  { prop: "createTime", label: "创建时间" },
];

/**
 * 打开详情弹窗。
 * @param {Object} row 当前行数据
 */
function openDetail(row) {
  detail.value = row;
  detailOpen.value = true;
}

/**
 * 打开修改弹窗，优先级统一转成数字以匹配单选项。
 * @param {Object} row 当前行数据
 */
function edit(row) {
  form.value = { ...row, priority: Number(row.priority || 1) };
  open.value = true;
}

/** 提交修改，提交期间锁定确定按钮避免重复入库。 */
async function save() {
  if (saving.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
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
    refresh();
  } finally {
    saving.value = false;
  }
}
</script>
