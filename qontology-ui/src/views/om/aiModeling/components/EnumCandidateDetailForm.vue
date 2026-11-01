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
  <el-form label-width="90px" label-position="right" class="enum-detail-form">
    <el-form-item v-for="item in fields" :key="item.prop" :label="item.label">
      <div class="form-readonly" :class="{ textarea: item.prop === 'remark' }">
        <dict-tag
          v-if="item.prop === 'status'"
          :options="statusOptions"
          :value="row.status || '0'"
        />
        <template v-else>{{ displayValue(item.prop) }}</template>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
const props = defineProps({
  row: { type: Object, default: () => ({}) },
  fields: { type: Array, default: () => [] },
  statusOptions: { type: Array, default: () => [] },
});
const { proxy } = getCurrentInstance();
function displayValue(prop) {
  if (["createTime", "updateTime"].includes(prop)) {
    return proxy.parseTime(props.row[prop]) || "-";
  }
  return props.row[prop] ?? "-";
}
</script>

<style scoped>
.form-readonly {
  box-sizing: border-box;
  width: 100%;
  min-height: 34px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  border: 1px solid #f1f1f1;
  background: #fcfcfc;
  white-space: pre-wrap;
  word-break: break-all;
}
.form-readonly.textarea {
  min-height: 85px;
  display: block;
  padding-top: 8px;
}
</style>
