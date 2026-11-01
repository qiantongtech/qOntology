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
    title="从术语表中选择"
    width="1000px"
    :append-to="appendTo"
    :close-on-click-modal="false"
    draggable
    destroy-on-close
  >
    <el-form
      class="btn-style"
      :model="query"
      :inline="true"
      @submit.prevent="search"
    >
      <el-form-item label="标准名称"
        ><el-input
          v-model="query.stdName"
          class="el-form-input-width"
          placeholder="请输入标准名称"
          clearable
          @keyup.enter="search"
      /></el-form-item>
      <el-form-item
        ><el-button type="primary" plain native-type="submit"
          ><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询</el-button
        ><el-button @click="reset"
          ><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button
        ></el-form-item
      >
    </el-form>
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="rows"
      row-key="id"
      highlight-current-row
      stripe
      @current-change="selected = $event"
      @row-click="selectRow"
    >
      <el-table-column
        type="index"
        label="编号"
        sortable
        width="65"
        align="center"
      />
      <el-table-column
        v-for="field in fields"
        :key="field.prop"
        :prop="field.prop"
        :label="field.label"
        :min-width="field.width"
        align="left"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="{ row }">{{
          row[field.prop] || "-"
        }}</template></el-table-column
      >
      <template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" alt="暂无术语" />
          <p>暂无术语</p>
        </div></template
      >
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="load"
    />
    <template #footer
      ><div class="dialog-footer">
        <el-button size="mini" @click="visible = false">取 消</el-button
        ><el-button
          type="primary"
          size="mini"
          :disabled="!selected || loading"
          @click="confirm"
          >确 定</el-button
        >
      </div></template
    >
  </el-dialog>
</template>
<script setup>
import { reactive, ref, watch } from "vue";
import { listCandidates } from "@/api/om/aiModeling";
const props = defineProps({
  modelValue: Boolean,
  taskId: { type: Number, required: true },
  appendTo: { type: [String, Object], default: "body" },
});
const emit = defineEmits(["update:modelValue", "confirm"]);
const visible = ref(false),
  loading = ref(false),
  rows = ref([]),
  total = ref(0),
  selected = ref(),
  tableRef = ref();
const query = reactive({ pageNum: 1, pageSize: 10, stdName: "" });
const fields = [
  { prop: "stdName", label: "标准名称", width: 130 },
  { prop: "enName", label: "英文名称", width: 130 },
  { prop: "alias", label: "别名", width: 140 },
  { prop: "abbr", label: "缩写", width: 100 },
  { prop: "source", label: "来源", width: 140 },
  { prop: "description", label: "描述", width: 200 },
];
watch(
  () => props.modelValue,
  (value) => {
    visible.value = value;
    if (value) {
      query.pageNum = 1;
      load();
    }
  }
);
watch(visible, (value) => emit("update:modelValue", value));
async function load() {
  loading.value = true;
  try {
    const response = await listCandidates("term", {
      taskId: props.taskId,
      ...query,
    });
    rows.value = response.data?.rows || [];
    total.value = Number(response.data?.total || 0);
    selected.value = undefined;
  } finally {
    loading.value = false;
  }
}
function search() {
  query.pageNum = 1;
  return load();
}
function reset() {
  query.stdName = "";
  return search();
}
function selectRow(row) {
  tableRef.value?.setCurrentRow(row);
}
function confirm() {
  emit("confirm", selected.value);
  visible.value = false;
}
</script>

