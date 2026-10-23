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
  <el-dialog :model-value="modelValue" title="导入建模目标" width="1100px" :append-to="appendTo"
             :close-on-click-modal="false" draggable destroy-on-close @update:model-value="emit('update:modelValue', $event)"
  >
    <el-form class="btn-style" :model="query" :inline="true" @submit.prevent="search">
      <el-form-item label="目标问题">
        <el-input v-model="query.question" class="el-form-input-width" placeholder="请输入目标问题" clearable/>
      </el-form-item>
      <el-form-item label="问题来源">
        <el-input v-model="query.questionSource" class="el-form-input-width" placeholder="请输入问题来源" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain native-type="submit"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询</el-button>
        <el-button @click="resetSearch"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
      </el-form-item>
    </el-form>
    <el-table ref="tableRef" v-loading="loading" :data="rows" row-key="id" stripe
              @selection-change="selectionChanged" @row-click="handleRowClick"
    >
      <el-table-column type="selection" width="55" align="center" reserve-selection :selectable="selectable"/>
      <el-table-column prop="id" label="编号" width="80" align="center"/>
      <el-table-column prop="question" label="目标问题" min-width="240" :show-overflow-tooltip="{effect: 'light'}"/>
      <el-table-column prop="questionSource" label="问题来源" min-width="140" :show-overflow-tooltip="{effect: 'light'}"/>
      <el-table-column prop="businessScenarios" label="业务场景" min-width="140" :show-overflow-tooltip="{effect: 'light'}"/>
      <el-table-column prop="priority" label="优先级" width="90" align="center">
        <template #default="{row}"><dict-tag :options="om_target_priority" :value="row.priority"/></template>
      </el-table-column>
      <el-table-column prop="relatedElementCount" label="关联元素" width="100" align="center"/>
      <template #empty>
        <div class="emptyBg target-empty">
          <img src="@/assets/system/images/no_data/noData.png" alt="暂无建模目标"/>
          <p>暂无建模目标</p>
        </div>
      </template>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="getList"/>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="emit('update:modelValue', false)">取 消</el-button>
        <el-button type="primary" size="mini" :disabled="!selected.length || loading" @click="confirm">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import {computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch} from 'vue';
import {listTypeTargetOptions} from '@/api/om/object/type';
const props = defineProps({
  versionId: {type: [String, Number], required: true},
  modelValue: {type: Boolean, default: false},
  selectedTargets: {type: Array, default: () => []},
  loadOptions: {type: Function, default: listTypeTargetOptions},
  appendTo: {type: [String, Object], default: 'body'}
});
const emit = defineEmits(['update:modelValue', 'confirm']);
const {proxy} = getCurrentInstance();
const {om_target_priority} = proxy.useDict('om_target_priority');
const tableRef = ref();
const rows = ref([]);
const total = ref(0);
const loading = ref(false);
const selected = ref([]);
const query = reactive({question: '', questionSource: '', pageNum: 1, pageSize: 10});
const applied = reactive({question: '', questionSource: ''});
const existingIds = computed(() => new Set(props.selectedTargets.map(target => String(target.id))));
let requestId = 0;
function selectable(row) { return !existingIds.value.has(String(row.id)); }
function selectionChanged(selection) { selected.value = selection.filter(selectable); }
function handleRowClick(row, column, event) {
  if (loading.value || !selectable(row) || event?.target?.closest('.el-checkbox')) return;
  const checked = selected.value.some(target => String(target.id) === String(row.id));
  tableRef.value?.toggleRowSelection(row, !checked);
}
async function getList() {
  const request = ++requestId;
  if (!props.versionId) return;
  loading.value = true;
  try {
    const response = await props.loadOptions({...applied, versionId: props.versionId, pageNum: query.pageNum, pageSize: query.pageSize});
    if (request !== requestId || !props.modelValue) return;
    rows.value = (response.data?.rows || []).map(row => ({...row, id: String(row.id)}));
    total.value = Number(response.data?.total || 0);
    const lastPage = Math.max(1, Math.ceil(total.value / query.pageSize));
    if (query.pageNum > lastPage) {
      query.pageNum = lastPage;
      return await getList();
    }
  } finally {
    if (request === requestId) loading.value = false;
  }
}
function search() {
  applied.question = query.question.trim();
  applied.questionSource = query.questionSource.trim();
  query.pageNum = 1;
  return getList();
}
function resetSearch() {
  query.question = '';
  query.questionSource = '';
  return search();
}
function confirm() {
  if (loading.value || !selected.value.length) return;
  emit('confirm', selected.value.filter(selectable));
  emit('update:modelValue', false);
}
watch(() => props.modelValue, async visible => {
  const request = ++requestId;
  if (!visible) { loading.value = false; return; }
  rows.value = [];
  total.value = 0;
  selected.value = [];
  await nextTick();
  if (request !== requestId || !props.modelValue) return;
  tableRef.value?.clearSelection();
  resetSearch();
});
onBeforeUnmount(() => { ++requestId; });
</script>
<style scoped lang="scss">
.target-empty { padding: 20px 0; }
.target-empty img { width: 100px; height: 100px; }
</style>
