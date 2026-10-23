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
  <div>
    <div class="justify-end mb15">
      <right-toolbar :search="false" :columns="visibleColumns" @queryTable="emit('refresh')" />
    </div>
    <el-table :data="rows" row-key="id" stripe height="48vh">
      <el-table-column v-for="column in visibleColumns.filter(item => item.visible)" :key="column.prop"
                       :prop="column.prop" :label="column.label" :min-width="column.width"
                       :align="column.prop === 'operation' ? 'center' : 'left'"
                       :fixed="column.prop === 'operation' ? 'right' : undefined"
                       :show-overflow-tooltip="{ effect: 'light' }"
      >
        <template #default="{ row }">{{ row[column.prop] ?? '-' }}</template>
      </el-table-column>
      <template #empty>
        <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" :alt="emptyText" /><p>{{ emptyText }}</p></div>
      </template>
    </el-table>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue';
const props = defineProps({
  columns: {type: Array, required: true},
  rows: {type: Array, default: () => []},
  emptyText: {type: String, default: '暂无记录'}
});
const emit = defineEmits(['refresh']);
const visibleColumns = ref([]);
watch(() => props.columns, columns => {
  visibleColumns.value = columns.map((column, key) => ({...column, key, visible: true}));
}, {immediate: true});
</script>