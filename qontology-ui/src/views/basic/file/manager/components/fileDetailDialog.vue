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
    title="文件详情"
    width="800px"
    append-to="body"
    draggable
    :close-on-click-modal="false"
  >
    <el-form
      v-loading="loading"
      :model="detail"
      label-width="110px"
      class="column-form file-detail-form"
    >
      <qt-form-item class="row-full" label="文件名称">
        <div class="form-readonly">{{ detail.fileName || "-" }}</div>
      </qt-form-item>
      <qt-form-item class="row-full" label="文件分类">
        <!-- 与列表页文件分类列保持一致的标签展示形式。 -->
        <el-tag
          v-if="categoryName !== '-'"
          class="file-category-tag"
          type="primary"
          :title="categoryName"
          >{{ categoryName }}
        </el-tag>
        <span v-else>—</span>
      </qt-form-item>
      <qt-form-item class="row-full" label="文件类型">
        <div class="form-readonly">{{ fileType }}</div>
      </qt-form-item>
      <qt-form-item class="row-full" label="文件大小">
        <div class="form-readonly">{{ fileSize }}</div>
      </qt-form-item>
      <qt-form-item class="row-full" label="描述">
        <div class="form-readonly textarea">
          {{ detail.description || "-" }}
        </div>
      </qt-form-item>
      <qt-form-item class="row-full" label="创建人">
        <div class="form-readonly">{{ creatorText }}</div>
      </qt-form-item>
      <qt-form-item class="row-full" label="创建时间">
        <div class="form-readonly">
          {{ proxy.parseTime(detail.createTime) || "-" }}
        </div>
      </qt-form-item>
      <qt-form-item class="row-full" label="更新时间">
        <div class="form-readonly">
          {{ proxy.parseTime(detail.updateTime) || "-" }}
        </div>
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关 闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="FileDetailDialog">
import { computed, getCurrentInstance, ref } from "vue";
import { getModelDataFile } from "@/api/basic/file/basicFile.js";
import { formatSize } from "@/utils/index";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const props = defineProps({
  categoryOptions: { type: Array, default: () => [] },
});
const { proxy } = getCurrentInstance();
const visible = ref(false);
const loading = ref(false);
const detail = ref({});
// 创建人沿用“姓名/电话”单行格式，与分类详情弹框保持一致。
const { formatCreator } = useCreatorPhones();
let openRequest = 0;

const categoryName = computed(() => {
  const findName = (nodes) => {
    for (const node of nodes || []) {
      if (String(node.id) === String(detail.value.categoryId)) return node.name;
      const name = findName(node.children);
      if (name) return name;
    }
    return "";
  };
  return detail.value.categoryName || findName(props.categoryOptions) || "-";
});
const fileType = computed(() =>
  detail.value.fileSuffix ? detail.value.fileSuffix.toUpperCase() : "-"
);
const creatorText = computed(() => formatCreator(detail.value));
const fileSize = computed(() =>
  detail.value.fileSize == null ? "-" : formatSize(detail.value.fileSize)
);

/** 每次打开均加载最新详情，只采纳最后一次点击对应的响应。 */
async function openDialog(row) {
  const request = ++openRequest;
  detail.value = {};
  visible.value = true;
  loading.value = true;
  try {
    const response = await getModelDataFile(row.id);
    if (request !== openRequest || !visible.value) return;
    if (!response.data) {
      visible.value = false;
      proxy.$modal.msgWarning("文件已不存在，请刷新");
      return;
    }
    detail.value = response.data;
  } finally {
    if (request === openRequest) loading.value = false;
  }
}

defineExpose({ openDialog });
</script>
