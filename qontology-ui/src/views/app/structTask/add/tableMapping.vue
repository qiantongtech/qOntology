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

<!-- TableMapping.vue -->
<template>
  <div>
    <div class="h2-titles mb15">
      表映射
      <!-- <guide-tip-panel
        type="plain"
        title="导入表之前需确保数据库连接信息正确"
      /> -->
    </div>
    <div class="justify-between mb15">
      <el-row :gutter="15">
        <el-col :span="1.5" class="header-actions">
          <el-button type="primary" plain @click="onImportTable" icon="Upload">
            导入表
          </el-button>
        </el-col>
      </el-row>
    </div>
    <qt-table v-bind="tableStore" ref="tableRef">
      <template #status="{ row }">
        <dict-tag :options="app_mapping_status" :value="row.status" />
      </template>
      <template #handle="{ row }">
        <el-button link type="primary" icon="Edit" @click="onMappingClick(row)"
          >映射</el-button
        >
        <el-button link type="danger" icon="Delete" @click="onDeleteClick(row)"
          >删除</el-button
        >
      </template>
    </qt-table>
  </div>
</template>

<script setup>
const { proxy } = getCurrentInstance();
const { app_mapping_status } = proxy.useDict("app_mapping_status");

const props = defineProps({
  tableData: {
    type: Array,
    default: () => [],
  },
  mappingStatusOptions: {
    type: Array,
    default: () => [],
  },
});

const tableRef = ref();
const tableStore = reactive({
  config: {
    notPagination: true,
    table: { maxHeight: 410, rowKey: "tableName", stripe: true },
  },
  columns: [
    { label: "表名", prop: "tableName", minWidth: 220, align: "left" },
    { label: "表显示名称", prop: "tableComment", minWidth: 260, align: "left" },
    { label: "对应对象类型", prop: "operate", minWidth: 260, align: "left" },
    { label: "状态", prop: "status", slot: "status", width: 180 },
    { label: "操作", slot: "handle", width: 180, fixed: "right" },
  ],
  func: () => Promise.resolve({ data: props.tableData || [] }),
  params: {},
});

watch(
  () => props.tableData,
  () => nextTick(() => tableRef.value?.getList()),
  { deep: true }
);

const emit = defineEmits(["importTable", "mappingClick", "deleteClick"]);

const onImportTable = () => {
  emit("importTable");
};

const onMappingClick = (row) => {
  emit("mappingClick", row);
};

const onDeleteClick = (row) => {
  emit("deleteClick", row);
};
</script>

<style scoped lang="scss">
.header-actions {
  display: flex;
  align-items: center;
}
.tip-text {
  margin-left: var(--qt-space-4);
  color: var(--qt-text-secondary);
  font-size: var(--qt-font-size-small);
}
</style>
