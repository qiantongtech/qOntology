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
  <div class="graph-table">
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <qt-table v-bind="tableStore" ref="tableRef" />
    </qt-wrap>
  </div>
</template>

<script setup name="TableView">
import { getTableGraph } from "@/api/app/graph/index";

const route = useRoute();
const tableRef = ref();

/** 适配图谱表格接口数据结构。 */
async function requestTableData(params) {
  const { createTime, ...query } = params;
  const [beginTime, endTime] = Array.isArray(createTime) ? createTime : [];
  const response = await getTableGraph({
    ...query,
    graphId: route.params.kgId,
    beginTime,
    endTime,
  });
  return {
    data: {
      rows: response.data?.relationships || [],
      total: response.data?.total || 0,
    },
  };
}

const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
    },
  },
  columns: [
    {
      label: "编号",
      prop: "id",
      width: 60,
      sortable: "custom",
      sortableKey: "id",
    },
    { label: "主体", prop: "startName", align: "left", minWidth: 200 },
    {
      label: "主体概念",
      prop: "startSchemaName",
      align: "left",
      minWidth: 150,
    },
    { label: "谓词/关系", prop: "relationType", align: "left", minWidth: 150 },
    { label: "客体", prop: "endName", align: "left", minWidth: 200 },
    { label: "客体概念", prop: "endSchemaName", align: "left", minWidth: 150 },
    {
      label: "创建时间",
      prop: "createTime",
      date: "{y}-{m}-{d}",
      width: 180,
      minWidth: 180,
      sortable: "custom",
    },
  ],
  func: requestTableData,
  params: {
    startEntityName: null,
    startSchemaName: null,
    relationName: null,
    endEntityName: null,
    endSchemaName: null,
    createTime: null,
  },
});

const searchStore = reactive({
  items: [
    {
      label: "主体",
      prop: "startEntityName",
      component: { is: "input", placeholder: "请输入主体名称" },
    },
    {
      label: "主体概念",
      prop: "startSchemaName",
      component: { is: "input", placeholder: "请输入主体概念" },
    },
    {
      label: "谓词/关系",
      prop: "relationName",
      component: { is: "input", placeholder: "请输入谓词/关系" },
    },
    {
      label: "客体",
      prop: "endEntityName",
      component: { is: "input", placeholder: "请输入客体名称" },
    },
    {
      label: "客体概念",
      prop: "endSchemaName",
      component: { is: "input", placeholder: "请输入客体概念" },
    },
    {
      label: "创建时间",
      prop: "createTime",
      component: {
        is: "date-picker",
        type: "daterange",
        valueFormat: "YYYY-MM-DD",
        rangeSeparator: "-",
        startPlaceholder: "开始日期",
        endPlaceholder: "结束日期",
      },
    },
  ],
});
</script>

<style scoped lang="scss">
.graph-table {
  min-height: 100%;
}
</style>
