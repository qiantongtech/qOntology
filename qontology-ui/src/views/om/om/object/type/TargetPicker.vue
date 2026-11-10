<!--
  Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

  This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

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
  <el-dialog
    :model-value="modelValue"
    title="导入建模目标"
    width="1000px"
    :append-to="appendTo"
    draggable
    destroy-on-close
    @update:model-value="emit('update:modelValue', $event)"
  >
    <qt-search-bar
      :items="searchStore.items"
      :params="tableStore.params"
      :table-ref="tableRef"
    />

    <qt-table v-bind="tableStore" ref="tableRef">
      <template #priority="{ row }">
        <dict-tag :options="om_target_priority" :value="row.priority" />
      </template>
      <template #acceptStatus="{ row }">
        <dict-tag :options="om_accept_status" :value="row.acceptStatus" />
      </template>
      <template #relatedElementCount="{ row }">
        <qt-tag-group
          :items="row.relatedElementTags || []"
          type="none"
          :max-count="2"
        />
      </template>
      <template #creator="{ row }">
        <div class="flex-column overflow-hidden">
          <span class="text-ellipsis">{{ row.createBy || "-" }}</span>
          <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{
            creatorPhones[row.creatorId]
          }}</span>
        </div>
      </template>
      <template #createTime="{ row }">{{
        proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
      }}</template>
      <template #empty>
        <div class="emptyBg target-empty">
          <img
            src="@/assets/common/images/empty-state/noData.png"
            alt="暂无记录"
          />
          <p>暂无记录</p>
        </div>
      </template>
    </qt-table>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emit('update:modelValue', false)">取 消</el-button>
        <el-button type="primary" :disabled="!selected.length" @click="confirm"
          >确 定</el-button
        >
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { computed, getCurrentInstance, reactive, ref, watch } from "vue";
import { listTypeTargetOptions } from "@/api/om/object/type";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { withTargetRelatedElements } from "@/utils/targetRelatedElements";
const props = defineProps({
  versionId: { type: [String, Number], required: true },
  modelValue: { type: Boolean, default: false },
  selectedTargets: { type: Array, default: () => [] },
  loadOptions: { type: Function, default: listTypeTargetOptions },
  appendTo: { type: [String, Object], default: "body" },
});
const emit = defineEmits(["update:modelValue", "confirm"]);
const { proxy } = getCurrentInstance();
const { om_target_priority, om_accept_status } = proxy.useDict(
  "om_target_priority",
  "om_accept_status"
);
const { creatorPhones } = useCreatorPhones();
const tableRef = ref();
const selected = ref([]);
const existingIds = computed(
  () => new Set(props.selectedTargets.map((target) => String(target.id)))
);

/** 已在本体中关联过的目标不可再次勾选。 */
function selectable(row) {
  return !existingIds.value.has(String(row.id));
}

/**
 * 取数：合并分页与搜索条件后请求建模目标选项，版本号缺失时直接返回空列表。
 * @param {Object} params QtTable 合并后的查询参数
 * @returns {Promise<Object>} 列表接口返回体
 */
function loadRows(params) {
  if (!props.versionId)
    return Promise.resolve({ data: { rows: [], total: 0 } });
  return props.loadOptions(params).then(async (response) => {
    // 关联元素与建设目标列表页保持一致，补充对象类型/对象类型关系标签。
    response.data.rows = await withTargetRelatedElements(
      response.data.rows || [],
      props.versionId
    );
    return response;
  });
}

/** 请求前裁剪并规范化搜索条件。 */
function formatParams(params) {
  return {
    ...params,
    question: params.question?.trim() || undefined,
    questionSource: params.questionSource?.trim() || undefined,
    versionId: props.versionId,
  };
}

/** 统一将 id 转为字符串，保证 rowKey 与已选判断一致。 */
function formatData(data) {
  return (data || []).map((row) => ({ ...row, id: String(row.id) }));
}

function handleSelectionChange(selection) {
  selected.value = selection.filter(selectable);
}

/** 点击整行切换勾选状态，复选框自身点击与不可选行不处理。 */
function handleRowClick(row, column, event) {
  if (!selectable(row) || event?.target?.closest(".el-checkbox")) return;
  const checked = selected.value.some(
    (target) => String(target.id) === String(row.id)
  );
  tableRef.value?.elTableRef?.toggleRowSelection(row, !checked);
}

const searchStore = reactive({
  items: [
    { label: "目标问题", prop: "question", component: { is: "input" } },
    { label: "问题来源", prop: "questionSource", component: { is: "input" } },
  ],
});

// 表格配置：多选 + 保留跨页选择，分页与 loading 统一交给 QtTable。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      rowKey: "id",
      stripe: true,
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
      onRowClick: handleRowClick,
    },
  },
  params: { question: "", questionSource: "" },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center",
      reserveSelection: true,
      selectable,
    },
    {
      label: "编号",
      prop: "id",
      width: 60,
      sortable: true,
      sortMethod: (a, b) => Number(a.id) - Number(b.id),
    },
    {
      label: "目标问题",
      prop: "question",
      minWidth: 300,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "问题来源",
      prop: "questionSource",
      width: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "业务场景",
      prop: "businessScenarios",
      width: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "关联元素",
      slot: "relatedElementCount",
      minWidth: 240,
      align: "left",
    },
    { label: "优先级", prop: "priority", slot: "priority", width: 100 },
  ],
  events: { formatParams, formatData },
  func: loadRows,
});

function confirm() {
  if (!selected.value.length) return;
  emit("confirm", selected.value.filter(selectable));
  emit("update:modelValue", false);
}

// 每次打开重置搜索条件；destroy-on-close 会重建 QtTable 并自动首查。
watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) return;
    tableStore.params.question = "";
    tableStore.params.questionSource = "";
    selected.value = [];
  }
);
</script>
<style scoped lang="scss">
.target-empty {
  padding: 20px 0;
}
.target-empty img {
  width: 100px;
  height: 100px;
}
</style>
