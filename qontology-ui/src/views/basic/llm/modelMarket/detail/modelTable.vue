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
  <qt-table v-bind="tableStore" ref="tableRef">
    <template #type="{ row }">
      <dict-tag :options="ai_model_tag" :value="row.type" />
    </template>
    <template #isEnable="{ row }">
      <el-switch
        v-model="row.status"
        :active-value="1"
        :inactive-value="0"
        @change="changeModel(row.status, row)"
      />
    </template>
    <template #handle>
      <el-button link type="primary" icon="Edit" :disabled="true">编辑</el-button>
      <el-button link type="danger" icon="Delete" :disabled="true">删除</el-button>
    </template>
  </qt-table>
</template>

<script setup>
import { changeModelEnable, getModelPage } from "@/api/basic/llm/myModel/myModel.js";

const { proxy } = getCurrentInstance();
const { ai_model_tag } = proxy.useDict("ai_model_tag");

const props = defineProps({
  keyId: {
    type: Number,
    default: null,
  },
});

const tableRef = ref();

/**
 * 包装接口函数，将后端返回的 res.data.list 适配为 qt-table 期望的 res.data.rows。
 * qt-table 内部的 getList 只识别 res.data.rows 字段。
 */
function fetchModelList(params) {
  return getModelPage(params).then((res) => ({
    ...res,
    data: {
      rows: res.data.list || [],
      total: res.data.total || 0,
    },
  }));
}

const tableStore = reactive({
  config: {
    sort: true,
    initResquest: false,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
    },
  },
  columns: [
    { label: "编号", prop: "id", align: "center", width: 85, sortable: true },
    { label: "模型名称", prop: "name", align: "center", sortable: true },
    { label: "模型类型", prop: "type", slot: "type", align: "center" },
    { label: "启用/禁用", prop: "isEnable", slot: "isEnable", align: "center" },
    {
      label: "操作",
      slot: "handle",
      align: "center",
      fixed: "right",
      width: 240,
    },
  ],
  func: fetchModelList,
  params: {
    keyId: null,
    workspaceId: null,
    code: null,
    name: null,
    description: null,
    createTime: null,
  },
});

/**
 * 大模型管理
 * @param isEnable 是否启用
 * @param row 行数据
 */
function changeModel(isEnable, row) {
  let data = {
    keyId: props.keyId,
    name: row.name,
    status: isEnable,
  };

  const text = isEnable === 0 ? "关闭" : "启用";
  proxy.$modal
    .confirm('确认"' + text + '""' + row.name + '"模型吗?')
    .then(function () {
      return changeModelEnable(data);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === 0 ? 1 : 0;
    });
}

watch(
  () => props.keyId,
  (val) => {
    if (val) {
      tableStore.params.keyId = val;
      tableRef.value?.getList();
    }
  },
  { immediate: true }
);
</script>

<style scoped lang="scss"></style>
