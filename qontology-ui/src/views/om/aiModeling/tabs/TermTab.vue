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

    <!-- 添加或修改术语对话框 -->
    <el-dialog
      v-model="open"
      title="修改术语"
      width="800px"
      :append-to="pageRef"
      :close-on-press-escape="!saving"
      draggable
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
      >
        <el-form-item label="名称" prop="stdName">
          <el-input
            v-model="form.stdName"
            :maxlength="200"
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="英文名称" prop="enName">
          <el-input
            v-model="form.enName"
            :maxlength="200"
            placeholder="请输入英文名称"
          />
        </el-form-item>
        <el-form-item label="别名" prop="alias">
          <div class="flex gap-2 alias-tags">
            <el-tag
              v-for="item in aliasTags"
              :key="item"
              closable
              @close="removeAlias(item)"
              >{{ item }}</el-tag
            ><el-input
              v-if="aliasInputVisible"
              ref="aliasInputRef"
              v-model="aliasInputValue"
              class="w-20 alias-input"
              size="small"
              placeholder="请输入别名"
              @keydown.enter.stop.prevent="confirmAlias"
              @blur="confirmAlias"
            /><el-button
              v-else
              class="button-new-tag"
              size="small"
              @click="showAliasInput"
              >+ 添加别名</el-button
            >
          </div>
        </el-form-item>
        <el-form-item label="缩写" prop="abbr">
          <el-input
            v-model="form.abbr"
            :maxlength="200"
            placeholder="请输入缩写"
          />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input
            v-model="form.source"
            type="textarea"
            :rows="3"
            :maxlength="200"
            show-word-limit
            placeholder="请输入来源"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            :maxlength="500"
            show-word-limit
            placeholder="请输入描述"
          />
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

    <!-- 术语详情对话框 -->
    <el-dialog
      v-model="detailOpen"
      title="术语详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form label-width="90px">
        <el-form-item
          v-for="field in fields"
          :key="field.prop"
          :label="field.label"
        >
          <div v-if="field.prop === 'alias'" class="form-readonly alias-tags">
            <el-tag v-for="item in parseAliases(detail.alias)" :key="item">{{
              item
            }}</el-tag
            ><span v-if="!parseAliases(detail.alias).length">-</span>
          </div>
          <div
            v-else
            class="form-readonly"
            :class="{
              textarea: ['source', 'description'].includes(field.prop),
            }"
          >
            {{
              field.prop === "createTime"
                ? proxy.parseTime(detail.createTime, "{y}-{m}-{d} {h}:{i}") ||
                  "-"
                : detail[field.prop] || "-"
            }}
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
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import { updateCandidate } from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status } = proxy.useDict("om_ai_adopt_status");

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const aliasInputRef = ref();
const aliasInputVisible = ref(false);
const aliasInputValue = ref("");
const open = ref(false);
const detailOpen = ref(false);
const saving = ref(false);
const form = ref({});
const detail = ref({});

/**
 * 收集采纳前缺失的必填项名称，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
function required(row) {
  return [
    ["stdName", "名称"],
    ["enName", "英文名称"],
    ["alias", "别名"],
    ["abbr", "缩写"],
    ["source", "来源"],
  ]
    .filter(([key]) => !row[key]?.trim())
    .map(([, label]) => label);
}

const tab = useCandidateTab("term", props.taskId, required);
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
      prop: "stdName",
      slot: "text",
      width: 160,
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "英文名称",
      prop: "enName",
      slot: "text",
      width: 150,
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "别名",
      prop: "alias",
      slot: "text",
      width: 160,
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "缩写",
      prop: "abbr",
      slot: "text",
      width: 120,
      minWidth: 120,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "来源",
      prop: "source",
      slot: "text",
      width: 180,
      minWidth: 180,
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

const rules = {
  stdName: [{ required: true, message: "名称不能为空" }],
  enName: [{ required: true, message: "英文名称不能为空" }],
  alias: [{ required: true, message: "别名不能为空" }],
  abbr: [{ required: true, message: "缩写不能为空" }],
  source: [{ required: true, message: "来源不能为空" }],
};
const fields = [
  { prop: "id", label: "编号" },
  { prop: "stdName", label: "名称" },
  { prop: "enName", label: "英文名称" },
  { prop: "alias", label: "别名" },
  { prop: "abbr", label: "缩写" },
  { prop: "source", label: "来源" },
  { prop: "description", label: "描述" },
  { prop: "createBy", label: "创建人" },
  { prop: "createTime", label: "创建时间" },
];

/**
 * 拆分别名字符串，兼容中英文逗号并去重去空。
 * @param {string} value 原始别名文本
 * @returns {Array<string>} 别名列表
 */
const parseAliases = (value) => [
  ...new Set(
    (value || "")
      .split(/[,，]/)
      .map((item) => item.trim())
      .filter(Boolean)
  ),
];
const aliasTags = computed(() => parseAliases(form.value.alias));

/** 展示别名输入框并自动聚焦。 */
async function showAliasInput() {
  aliasInputVisible.value = true;
  await nextTick();
  aliasInputRef.value?.focus();
}

/** 确认新增别名，合并已有标签后写回表单。 */
function confirmAlias() {
  const items = [
    ...new Set([...aliasTags.value, ...parseAliases(aliasInputValue.value)]),
  ];
  form.value.alias = items.join("，");
  aliasInputValue.value = "";
  aliasInputVisible.value = false;
}

/**
 * 删除指定别名标签。
 * @param {string} item 待删除的别名
 */
function removeAlias(item) {
  form.value.alias = aliasTags.value
    .filter((value) => value !== item)
    .join("，");
}

/**
 * 打开详情弹窗。
 * @param {Object} row 当前行数据
 */
function openDetail(row) {
  detail.value = row;
  detailOpen.value = true;
}

/**
 * 打开修改弹窗。
 * @param {Object} row 当前行数据
 */
function edit(row) {
  form.value = { ...row };
  open.value = true;
}

/** 提交修改，提交期间锁定确定按钮避免重复入库。 */
async function save() {
  if (saving.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    await updateCandidate("term", {
      id: form.value.id,
      taskId: props.taskId,
      stdName: form.value.stdName,
      enName: form.value.enName,
      alias: form.value.alias,
      abbr: form.value.abbr,
      source: form.value.source,
      description: form.value.description,
    });
    proxy.$modal.msgSuccess("修改成功");
    open.value = false;
    refresh();
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.alias-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.alias-input {
  width: 180px;
}
</style>
