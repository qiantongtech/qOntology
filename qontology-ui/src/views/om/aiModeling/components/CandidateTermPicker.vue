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
      :model="tableParams"
      :inline="true"
      @submit.prevent="search"
    >
      <el-form-item label="术语名称"
        ><el-input
          v-model="tableParams.stdName"
          class="el-form-input-width"
          placeholder="请输入术语名称"
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
    <qt-table v-bind="tableStore" ref="tableRef">
      <template #termInfo="{ row }"><TermInfoCell :row="row" /></template>
      <template #text="{ row, column_data }">{{
        row[column_data.prop] === "" ? "-" : row[column_data.prop]
      }}</template>
    </qt-table>
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
import TermInfoCell from "@/views/om/semantic/term/TermInfoCell.vue";
const props = defineProps({
  modelValue: Boolean,
  taskId: { type: Number, required: true },
  appendTo: { type: [String, Object], default: "body" },
});
const emit = defineEmits(["update:modelValue", "confirm"]);
const visible = ref(false),
  loading = ref(false),
  selected = ref(),
  tableRef = ref();
// 展示字段与对象类型页「从术语表中选择」弹窗保持一致：编号 + 术语信息（标准名/英文名/描述）+ 以下列，无末尾状态列。
const fields = [
  { prop: "alias", label: "术语别名", width: 160 },
  { prop: "abbr", label: "业务缩写", width: 120 },
  { prop: "source", label: "来源", width: 180 },
];
// 查询条件：术语名称直接作为 qt-table 的 params 透传给取数方法
const tableParams = reactive({ stdName: "" });

/**
 * QtTable 取数适配器
 *
 * 弹窗 destroy-on-close，每次打开时 qt-table 重新挂载并自动请求第一页。
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  loading.value = true;
  try {
    const response = await listCandidates("term", {
      taskId: props.taskId,
      ...params,
    });
    return {
      data: {
        rows: response.data?.rows || [],
        total: Number(response.data?.total || 0),
      },
    };
  } finally {
    loading.value = false;
  }
}

/**
 * QtTable 统一配置（写法参考 views/basic/project/index.vue）
 */
const tableStore = reactive({
  config: {
    table: {
      stripe: true,
      rowKey: "id",
      highlightCurrentRow: true,
      onCurrentChange: handleCurrentChange,
      onRowClick: selectRow,
    },
  },
  columns: [
    {
      label: "编号",
      prop: "id",
      width: 60,
      align: "center",
      sortable: true,
      sortMethod: (a, b) => Number(a.id) - Number(b.id),
    },
    { label: "术语信息", minWidth: 300, align: "left", slot: "termInfo" },
    ...fields.map((field) => ({
      label: field.label,
      prop: field.prop,
      minWidth: field.width,
      align: "left",
      showOverflowTooltip: { effect: "light" },
      slot: "text",
    })),
  ],
  func: requestTableData,
  params: tableParams,
  events: {
    // 数据重新加载后清空当前选中行，与原 load() 行为一致
    onDataLoaded: () => {
      selected.value = undefined;
    },
  },
});

watch(
  () => props.modelValue,
  (value) => {
    visible.value = value;
  }
);
watch(visible, (value) => emit("update:modelValue", value));
/** 查询：回到第一页并重新加载 */
function search() {
  return tableRef.value?.resetQuery();
}
function reset() {
  tableParams.stdName = "";
  return search();
}
/** 单选高亮：点击行时同步 el-table 的当前行 */
function handleCurrentChange(row) {
  selected.value = row;
}
function selectRow(row) {
  tableRef.value?.elTableRef?.setCurrentRow(row);
}
function confirm() {
  emit("confirm", selected.value);
  visible.value = false;
}
</script>

