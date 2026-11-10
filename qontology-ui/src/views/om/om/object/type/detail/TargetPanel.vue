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
  <div v-loading="loading">
    <qt-wrap
      :columns="columns"
      :table-ref="listActions"
      :config="{ fullContent: false, actions: { table: { search: false } } }"
    >
      <template #actions-data>
        <guide-tip-panel type="plain" class="target-note">
          <template #title-append>
            <span
              >展示当前对象类型关联的建模目标，便于核对该类型的设计是否符合目标要求。</span
            >
          </template>
        </guide-tip-panel>
      </template>
      <qt-table
        ref="tableRef"
        :columns="columns"
        :config="tableConfig"
        :events="tableEvents"
        :func="readTableRows"
      >
        <template #relatedElementCount="{ row }">
          <qt-tag-group
            :items="row.relatedElementTags || []"
            type="none"
            :max-count="2"
          />
        </template>
        <template #priority="{ row }"
          ><dict-tag :options="om_target_priority" :value="row.priority"
        /></template>
        <template #acceptStatus="{ row }"
          ><dict-tag :options="om_accept_status" :value="row.acceptStatus"
        /></template>
      </qt-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </qt-wrap>
  </div>
</template>
<script setup>
import { InfoFilled } from "@element-plus/icons-vue";
import { getCurrentInstance, onBeforeUnmount, reactive, ref, watch } from "vue";
import { pageTypeTargets } from "@/api/om/object/type";
import { withTargetRelatedElements } from "@/utils/targetRelatedElements";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";

const props = defineProps({
  objectTypeId: { type: [String, Number], required: true },
});
const { proxy } = getCurrentInstance();
const { om_target_priority, om_accept_status } = proxy.useDict(
  "om_target_priority",
  "om_accept_status"
);
const { versionId } = useOntologyVersion();
const targets = ref([]);
const tableRef = ref();
const loading = ref(false);
const total = ref(0);
const columns = reactive([
  { label: "编号", prop: "id", width: 60, sortable: "custom" },
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
  { label: "验证状态", prop: "acceptStatus", slot: "acceptStatus", width: 100 },
]);
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  orderByColumn: "id",
  isAsc: "descending",
});
const listActions = { getList };
const tableConfig = {
  notPagination: true,
  sort: true,
  table: {
    stripe: true,
    rowKey: "id",
    defaultSort: { prop: "id", order: "descending" },
  },
};
const tableEvents = {
  onSortChange: ({ orderByColumn, isAsc }) =>
    handleSortChange({ prop: orderByColumn, order: isAsc }),
};
function readTableRows() {
  return Promise.resolve({ data: targets.value });
}
watch(targets, () => tableRef.value?.getList());
let requestId = 0;
async function getList() {
  const request = ++requestId;
  loading.value = true;
  try {
    const response = await pageTypeTargets(props.objectTypeId, { ...query });
    if (request !== requestId) return;
    const count = response.data.total;
    const maxPage = Math.max(1, Math.ceil(count / query.pageSize));
    if (query.pageNum > maxPage) {
      query.pageNum = maxPage;
      return await getList();
    }
    const rows = await withTargetRelatedElements(
      response.data.rows || [],
      versionId.value
    );
    if (request !== requestId) return;
    total.value = count;
    targets.value = rows;
  } catch {
    if (request === requestId) {
      targets.value = [];
      total.value = 0;
    }
  } finally {
    if (request === requestId) loading.value = false;
  }
}
function handleSortChange({ prop, order }) {
  query.orderByColumn = order ? prop : "id";
  query.isAsc = order || "descending";
  query.pageNum = 1;
  getList();
}
watch(
  () => props.objectTypeId,
  () => {
    query.pageNum = 1;
    targets.value = [];
    total.value = 0;
    getList();
  },
  { immediate: true }
);
onBeforeUnmount(() => {
  ++requestId;
});
</script>

<style scoped lang="scss">
.target-note.guide-tip.plain :deep(.tip-header .tip-title) {
  gap: 6px;
}
.target-note-icon {
  flex-shrink: 0;
  font-size: 14px;
}
</style>
