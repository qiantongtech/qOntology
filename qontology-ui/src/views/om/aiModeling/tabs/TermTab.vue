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
        ></el-row
      >
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
        v-if="visible('stdName')"
        prop="stdName"
        label="名称"
        min-width="140"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.stdName || "-"
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
        v-if="visible('enName')"
        prop="enName"
        label="英文名称"
        min-width="150"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.enName || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('alias')"
        prop="alias"
        label="别名"
        min-width="160"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.alias || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('abbr')"
        prop="abbr"
        label="缩写"
        min-width="120"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.abbr || "-"
        }}</template></el-table-column
      >
      <el-table-column
        v-if="visible('source')"
        prop="source"
        label="来源"
        min-width="180"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row.source || "-"
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
      title="修改术语"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form ref="formRef" :model="form" :rules="rules" label-width="90px"
        ><el-form-item label="名称" prop="stdName"
          ><el-input
            v-model="form.stdName"
            maxlength="256"
            placeholder="请输入名称" /></el-form-item
        ><el-form-item label="英文名称" prop="enName"
          ><el-input
            v-model="form.enName"
            maxlength="256"
            placeholder="请输入英文名称" /></el-form-item
        ><el-form-item label="别名" prop="alias"
          ><div class="flex gap-2 alias-tags">
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
          </div></el-form-item
        ><el-form-item label="缩写" prop="abbr"
          ><el-input
            v-model="form.abbr"
            maxlength="256"
            placeholder="请输入缩写" /></el-form-item
        ><el-form-item label="来源" prop="source"
          ><el-input
            v-model="form.source"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请输入来源" /></el-form-item
        ><el-form-item label="描述"
          ><el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入描述" /></el-form-item></el-form
      ><template #footer
        ><div class="dialog-footer">
          <el-button @click="open = false">取 消</el-button
          ><el-button type="primary" @click="save">确 定</el-button>
        </div></template
      ></el-dialog
    >
    <el-dialog
      v-model="detailOpen"
      title="术语详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form label-width="90px"
        ><el-form-item
          v-for="field in fields"
          :key="field.prop"
          :label="field.label"
          ><div v-if="field.prop === 'alias'" class="form-readonly alias-tags">
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
          </div></el-form-item
        ></el-form
      ><template #footer
        ><el-button @click="detailOpen = false">关闭</el-button></template
      ></el-dialog
    ><CandidateEvidenceDrawer
      v-model="evidenceVisible"
      :evidences="evidences"
    />
  </div>
</template>
<script setup>
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import { updateCandidate } from "@/api/om/aiModeling";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status } = proxy.useDict("om_ai_adopt_status");
const pageRef = ref();
const formRef = ref();
const aliasInputRef = ref();
const aliasInputVisible = ref(false);
const aliasInputValue = ref("");
const open = ref(false);
const detailOpen = ref(false);
const form = ref({});
const columns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "stdName", label: "名称", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "enName", label: "英文名称", visible: true },
  { key: "alias", label: "别名", visible: true },
  { key: "abbr", label: "缩写", visible: true },
  { key: "source", label: "来源", visible: true },
  { key: "adoptStatus", label: "采纳状态", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);
const visible = (key) =>
  columns.value.find((item) => item.key === key)?.visible !== false;

const detail = ref({});
const parseAliases = (value) => [
  ...new Set(
    (value || "")
      .split(/[,，]/)
      .map((item) => item.trim())
      .filter(Boolean)
  ),
];
const aliasTags = computed(() => parseAliases(form.value.alias));
async function showAliasInput() {
  aliasInputVisible.value = true;
  await nextTick();
  aliasInputRef.value?.focus();
}
function confirmAlias() {
  const items = [
    ...new Set([...aliasTags.value, ...parseAliases(aliasInputValue.value)]),
  ];
  form.value.alias = items.join("，");
  aliasInputValue.value = "";
  aliasInputVisible.value = false;
}
function removeAlias(item) {
  form.value.alias = aliasTags.value
    .filter((value) => value !== item)
    .join("，");
}
const required = (row) =>
  [
    ["stdName", "名称"],
    ["enName", "英文名称"],
    ["alias", "别名"],
    ["abbr", "缩写"],
    ["source", "来源"],
  ]
    .filter(([key]) => !row[key]?.trim())
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
} = useCandidateTab("term", props.taskId, required);
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
function edit(row) {
  form.value = { ...row };
  open.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
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
  load();
}
load();
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

