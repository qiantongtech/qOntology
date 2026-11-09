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
    v-model="visible"
    title="知识文件"
    width="1200px"
    :append-to="appendTo"
    draggable
    destroy-on-close
    @close="cancel"
  >
    <qt-search-bar
      :items="searchItems"
      :params="queryParams"
      :table-ref="multipleTableRef"
    />

    <qt-table v-bind="tableStore" ref="multipleTableRef">
      <template #fileSize="{ row }">{{ formatSize(row.fileSize) }}</template>
      <template #createTime="{ row }">{{
        parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
      }}</template>
    </qt-table>
    <template #footer
      ><div class="dialog-footer">
        <el-button @click="cancel">取 消</el-button
        ><el-button type="primary" @click="confirm">确 定</el-button>
      </div></template
    >
  </el-dialog>
</template>

<script setup name="AppFileMultiple">
import { listModelDataFile } from "@/api/basic/file/basicFile";
import { formatSize } from "@/utils/index";

const { proxy } = getCurrentInstance();
defineProps({
  appendTo: {
    type: [Object, String],
    default: undefined,
  },
});
const emit = defineEmits(["confirm", "cancel"]);
const visible = ref(false);
const dataList = ref([]);
const selectedRows = ref([]);
const automaticSelection = ref(false);
const multipleTableRef = ref();
const queryParams = reactive({
  fileName: undefined,
  status: 1,
  orderByColumn: "createTime",
  isAsc: "descending",
});
const searchItems = [
  { label: "文件名称", prop: "fileName", component: { is: "input" } },
];
const tableStore = reactive({
  config: {
    initResquest: false,
    table: {
      height: 420,
      stripe: true,
      rowKey: "id",
      onSelectionChange: handleSelectionChange,
      onRowClick: handleRowClick,
    },
    pagination: { notAutoScroll: true },
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    { label: "编号", prop: "id", width: 60, minWidth: 60 },
    {
      label: "知识分类名称",
      prop: "categoryName",
      width: 150,
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "文件名称",
      prop: "fileName",
      minWidth: 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "文件描述",
      prop: "description",
      minWidth: 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "文件大小",
      prop: "fileSize",
      slot: "fileSize",
      width: 110,
      minWidth: 110,
    },
    { label: "创建人", prop: "createBy", minWidth: 100 },
    {
      label: "创建时间",
      prop: "createTime",
      slot: "createTime",
      width: 150,
      minWidth: 150,
    },
  ],
  func: listModelDataFile,
  params: queryParams,
  events: {
    formatData: (rows) => {
      automaticSelection.value = true;
      return rows;
    },
    onDataLoaded: restoreSelection,
  },
});

/** 分页刷新后回显跨页勾选。 */
function restoreSelection(rows) {
  dataList.value = rows;
  nextTick(() => {
    selectedRows.value.forEach((row) => {
      const currentRow = rows.find((item) => item.id === row.id);
      if (currentRow)
        multipleTableRef.value?.elTableRef?.toggleRowSelection(
          currentRow,
          true
        );
    });
    automaticSelection.value = false;
  });
}

/** 记录跨页选择结果。 */
function handleSelectionChange(selection) {
  if (automaticSelection.value) return;
  const currentIds = new Set(dataList.value.map((item) => item.id));
  const selectedIds = new Set(selection.map((item) => item.id));
  selectedRows.value = selectedRows.value.filter(
    (item) => !currentIds.has(item.id) || selectedIds.has(item.id)
  );
  selection.forEach((item) => {
    if (!selectedRows.value.some((selected) => selected.id === item.id))
      selectedRows.value.push(item);
  });
}

/** 点击行时切换选中状态。 */
function handleRowClick(row) {
  multipleTableRef.value?.elTableRef?.toggleRowSelection(row);
}

/** 重置查询条件与选中状态。 */
function rest() {
  selectedRows.value = [];
  queryParams.fileName = undefined;
}

/** 打开文件选择弹框并回显已选文件。 */
function open(rows = []) {
  selectedRows.value = Array.isArray(rows) ? [...rows] : [];
  visible.value = true;
  nextTick(() => multipleTableRef.value?.resetQuery());
}

/** 关闭文件选择弹框。 */
function cancel() {
  multipleTableRef.value?.elTableRef?.clearSelection();
  rest();
  visible.value = false;
  emit("cancel");
}

/** 确认选择文件。 */
function confirm() {
  if (!selectedRows.value.length) {
    proxy.$modal.msgWarning("未选择数据！");
    return;
  }
  emit("confirm", [...selectedRows.value]);
  rest();
  visible.value = false;
}

defineExpose({ open });
</script>
