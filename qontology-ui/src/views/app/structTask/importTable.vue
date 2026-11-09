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
  <!-- 导入表 -->
  <el-dialog
    title="导入表"
    v-model="visible"
    width="1000px"
    top="5vh"
    draggable
    destroy-on-close
    @close="handleClose"
  >
    <qt-search-bar :items="searchItems" :params="queryParams" :table-ref="tableRef" />
    <qt-table v-bind="tableStore" ref="tableRef" />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="handleImportTable">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { getDaDatasourceTableList } from "@/api/basic/datasource/daDatasource";
const visible = ref(false);
const tables = ref([]);
const { proxy } = getCurrentInstance();
const tableRef = ref();

const queryParams = reactive({
  tableName: undefined,
});
const searchItems = [{ label: "表名称", prop: "tableName", component: { is: "input" } }];

const tableStore = reactive({
  config: {
    initResquest: false,
    autoPagination: true,
    table: {
      height: 480,
      stripe: true,
      rowKey: "tableName",
      onRowClick: clickRow,
      onSelectionChange: handleSelectionChange,
    },
    pagination: { notAutoScroll: true },
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    {
      label: "表名称",
      prop: "tableName",
      minWidth: 320,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "表描述",
      prop: "tableComment",
      minWidth: 320,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
  ],
  func: loadTableData,
  params: queryParams,
});

const emit = defineEmits(["ok", "importDataTable", "closeDialog"]);

const handleClose = () => {
  emit("closeDialog");
};

const dataSourceId = ref(null);
/** 查询参数列表 */
function show(val) {
  dataSourceId.value = val.dataSourceId;
  visible.value = true;
  nextTick(() => tableRef.value?.resetQuery());
}

/** 单击选择行 */
function clickRow(row) {
  tableRef.value?.elTableRef?.toggleRowSelection(row);
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  tables.value = selection.map((item) => item.tableName);
}

/** 查询表数据并交给 qt-table 统一分页、加载和空状态。 */
async function loadTableData(params) {
  const res = await getDaDatasourceTableList({
    dataSourceId: dataSourceId.value,
    tableName: params.tableName,
  });
  return { data: res.data || [] };
}

/** 导入按钮操作 */
function handleImportTable() {
  const tableNames = tables.value.join(",");
  if (tableNames == "") {
    proxy.$modal.msgError("请选择要导入的表");
    return;
  }
  const dataTableList =
    tableRef.value?.elTableRef
      ?.getSelectionRows()
      ?.filter((item) => tables.value.includes(item.tableName)) || [];
  emit("importDataTable", dataTableList);
  visible.value = false;
  emit("ok");
}

defineExpose({
  show,
});
</script>
