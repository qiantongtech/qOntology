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
    title="实体-单选"
    v-model="visible"
    width="1200px"
    draggable
    destroy-on-close
  >
    <qt-search-bar
      :items="searchItems"
      :params="queryParams"
      :table-ref="tableRef"
    />
    <qt-table v-bind="tableStore" ref="tableRef" />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="confirm"> 确 定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="GraphEntitySingle">
import { getGraphPage } from "@/api/app/graph";
const { proxy } = getCurrentInstance();

const props = defineProps({
  graphId: {
    default: {},
  },
  entityType: { default: null },
});
const id = computed(() => props.graphId?.id);
const entityType = computed(() => props.entityType ?? props.graphId?.pageType);
const queryParams = reactive({
  entityId: id.value,
  entityType: entityType.value,
  name: null,
});
const searchItems = [
  { label: "实体名称", prop: "name", component: { is: "input" } },
];

// -------------------------------------------
const visible = ref(false);
// 定义单选数据
const single = ref();
const restoringSelection = ref(false);
// 当前界面table
const tableRef = ref();
const tableStore = reactive({
  config: {
    initResquest: false,
    table: {
      stripe: true,
      height: 400,
      rowKey: "id",
      highlightCurrentRow: true,
      onCurrentChange: handleCurrentChange,
    },
    pagination: { notAutoScroll: true },
  },
  columns: [
    { label: "名称", prop: "name", align: "left", headerAlign: "left" },
    { label: "描述", prop: "description", align: "left", headerAlign: "left" },
    {
      label: "创建时间",
      prop: "createTime",
      width: 180,
      minWidth: 180,
      date: "{y}-{m}-{d}",
    },
  ],
  func: (params) =>
    getGraphPage(params).then((response) => ({
      ...response,
      data: {
        rows: response.data.list || response.data.rows || [],
        total: response.data.total || 0,
      },
    })),
  params: queryParams,
  events: {
    formatData: (rows) => {
      restoringSelection.value = true;
      return rows;
    },
    onDataLoaded: restoreCurrentRow,
  },
});

const emit = defineEmits(["confirm"]);

/** 单选选中事件 */
function handleCurrentChange(selection) {
  if (selection && !restoringSelection.value) {
    single.value = selection;
  }
}

/**
 * 设置当前行
 * @param {Object} row 行对象
 * @returns 更改选中对象
 */
function setCurrentRow(row, rows) {
  if (row) {
    const match = rows.find((item) => item.id == row.id);
    if (match) {
      restoringSelection.value = true;
      tableRef.value?.elTableRef?.setCurrentRow(match);
      restoringSelection.value = false;
    }
  }
}

/**
 * 打开选择框
 * @param {Array} val 选中的对象数组
 */
function open(val) {
  visible.value = true;
  single.value = val;
  restoringSelection.value = true;
  tableRef.value?.elTableRef?.setCurrentRow();
  queryParams.entityId = id.value;
  queryParams.entityType = entityType.value;
  queryParams.name = null;
  nextTick(() => tableRef.value?.resetQuery());
}

/** 取消选择。 */
function cancel() {
  queryParams.name = null;
  single.value = undefined;
  visible.value = false;
}

/**
 * 确定按钮
 * @description 确定按钮时，emit confirm 事件，以便父组件接收到选中的数据
 */
function confirm() {
  if (!single.value) {
    proxy.$modal.msgWarning("请选择数据！");
    return;
  }
  emit("confirm", single.value);
  visible.value = false;
}

function restoreCurrentRow(rows) {
  nextTick(() => {
    setCurrentRow(single.value, rows);
    restoringSelection.value = false;
  });
}

defineExpose({ open });
</script>
