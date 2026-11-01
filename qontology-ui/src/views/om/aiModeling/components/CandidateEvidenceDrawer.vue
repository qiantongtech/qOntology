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
  <el-dialog
    v-model="visible"
    title="数据来源"
    width="800px"
    :close-on-click-modal="false"
    draggable
  >
    <el-card
      v-for="item in evidences"
      :key="item.id"
      class="mb15"
      shadow="hover"
    >
      <template #header>
        <span>{{ item.file_name || "来源文件" }}</span>
        <el-tag class="ml10" size="small">{{ location(item) }}</el-tag>
      </template>
      <div class="evidence-content">{{ item.content }}</div>
    </el-card>
    <div v-if="!evidences.length" class="emptyBg">
      <img src="@/assets/system/images/no_data/noData.png" alt="暂无来源证据" />
      <p>暂无来源证据</p>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关 闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
const props = defineProps({
  modelValue: Boolean,
  evidences: { type: Array, default: () => [] },
});
const emit = defineEmits(["update:modelValue"]);
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

/** 格式化候选数据在原始文件中的位置。 */
function location(item) {
  return (
    [
      item.page_no && `第${item.page_no}页`,
      item.chapter_path,
      item.paragraph_no && `第${item.paragraph_no}段`,
      item.row_no && `行${item.row_no}`,
      item.column_no && `列${item.column_no}`,
    ]
      .filter(Boolean)
      .join(" / ") || `证据块${item.block_no || "-"}`
  );
}
</script>

<style scoped>
.evidence-content {
  white-space: pre-wrap;
}
</style>
