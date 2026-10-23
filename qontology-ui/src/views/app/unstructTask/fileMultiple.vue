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
    <el-form
      ref="queryRef"
      class="btn-style"
      :inline="true"
      :model="queryParams"
      label-width="68px"
      @submit.prevent
    >
      <el-form-item label="文件名称" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          clearable
          placeholder="请输入文件名称"
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          plain
          type="primary"
          @click="handleQuery"
          @mousedown="(event) => event.preventDefault()"
        >
          <i class="iconfont-mini icon-a-zu22377 mr5" />查询
        </el-button>
        <el-button
          @click="resetQuery"
          @mousedown="(event) => event.preventDefault()"
        >
          <i class="iconfont-mini icon-a-zu22378 mr5" />重置
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      ref="multipleTableRef"
      v-loading="loading"
      :data="dataList"
      height="420px"
      reserve-selection
      row-key="id"
      stripe
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
    >
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column
        align="left"
        label="知识分类名称"
        prop="categoryName"
        width="200"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="scope">{{
          scope.row.categoryName || "-"
        }}</template></el-table-column
      >
      <el-table-column
        align="left"
        label="文件名称"
        prop="fileName"
        width="300"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="scope">{{
          scope.row.fileName || "-"
        }}</template></el-table-column
      >
      <el-table-column
        align="center"
        label="文件描述"
        prop="description"
        width="250"
        :show-overflow-tooltip="{ effect: 'light' }"
        ><template #default="scope">{{
          scope.row.description || "-"
        }}</template></el-table-column
      >
      <el-table-column align="center" label="创建人" prop="createBy"
        ><template #default="scope">{{
          scope.row.createBy || "-"
        }}</template></el-table-column
      >
      <el-table-column
        align="center"
        label="创建时间"
        prop="createTime"
        width="150"
        ><template #default="scope">{{
          parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
        }}</template></el-table-column
      >
      <!-- <el-table-column align="center" label="备注" prop="remark"
        ><template #default="scope">{{
          scope.row.remark || "-"
        }}</template></el-table-column
      > -->
      <template #empty
        ><div class="emptyBg">
          <img src="@/assets/system/images/no_data/noData.png" alt="" />
          <p>暂无记录</p>
        </div></template
      >
    </el-table>
    <pagination
      v-show="total > 0"
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />
    <template #footer
      ><div class="dialog-footer">
        <el-button size="mini" @click="cancel">取 消</el-button
        ><el-button type="primary" size="mini" @click="confirm"
          >确 定</el-button
        >
      </div></template
    >
  </el-dialog>
</template>

<script setup name="AppFileMultiple">
import { listModelDataFile } from "@/api/basic/file/basicFile";

const { proxy } = getCurrentInstance();
defineProps({
  appendTo: {
    type: [Object, String],
    default: undefined,
  },
});
const emit = defineEmits(["confirm", "cancel"]);
const visible = ref(false);
const loading = ref(false);
const total = ref(0);
const dataList = ref([]);
const selectedRows = ref([]);
const automaticSelection = ref(false);
const multipleTableRef = ref();
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  fileName: undefined,
  status: 1,
  orderByColumn: "createTime",
  isAsc: "descending",
});

/** 查询文件列表。 */
function getList() {
  loading.value = true;
  listModelDataFile(queryParams)
    .then(({ data }) => {
      dataList.value = data.rows;
      total.value = data.total;
      automaticSelection.value = true;
      nextTick(() => {
        selectedRows.value.forEach((row) => {
          const currentRow = dataList.value.find((item) => item.id === row.id);
          if (currentRow)
            multipleTableRef.value?.toggleRowSelection(currentRow, true);
        });
        automaticSelection.value = false;
      });
    })
    .finally(() => {
      loading.value = false;
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
  multipleTableRef.value?.toggleRowSelection(row);
}

/** 执行文件查询。 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置文件查询条件。 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 重置查询条件与选中状态。 */
function rest() {
  selectedRows.value = [];
  queryParams.pageNum = 1;
  proxy.resetForm("queryRef");
}

/** 打开文件选择弹框并回显已选文件。 */
function open(rows = []) {
  selectedRows.value = Array.isArray(rows) ? [...rows] : [];
  visible.value = true;
  getList();
}

/** 关闭文件选择弹框。 */
function cancel() {
  multipleTableRef.value?.clearSelection();
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
