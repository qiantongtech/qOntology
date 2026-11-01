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
  <div v-loading="loading" class="app-container file-detail-page">
    <!-- 与枚举详情保持一致：统一详情头、三列概要、整行描述及独立审计信息行。 -->
    <detail-info
        :data="detail"
        :header="{
        className: 'clearfixs',
        idKey: 'id',
        nameKey: 'fileName',
        routerBack: false
      }"
        :items="detailItems"
        mode="free"
        @back="handleReturn"
    >
      <template #fileSize>
        <span class="ellipsis">{{ detail.fileSize == null ? '—' : formatSize(detail.fileSize) }}</span>
      </template>
    </detail-info>
    <el-empty v-if="!loading && detail.id == null" :description="emptyMessage"/>
  </div>
</template>

<script setup name="BasicFileDetail">
import {getCurrentInstance, onBeforeUnmount, ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import {getModelDataFile, getFileTypes} from '@/api/basic/file/basicFile.js';
import {formatSize} from '@/utils/index';

const {proxy} = getCurrentInstance();
const route = useRoute();
const detail = ref({});
const loading = ref(false);
const emptyMessage = ref('文件已不存在');
let requestVersion = 0;
// 概要字段按枚举详情的行结构排列，描述单独占一行并保留上下间距。
const detailItems = [
  {label: '文件分类', key: 'categoryName'},
  {label: '文件类型', key: 'fileSuffix', formatter: value => value ? value.toUpperCase() : '—'},
  {label: '文件大小', key: 'fileSize', slot: 'fileSize'},
  {label: '描述', key: 'description', span: 24, className: 'mt2 mb2 file-description'},
  {label: '创建人', key: 'createBy'},
  {label: '创建时间', key: 'createTime', type: 'time'},
  {label: '更新时间', key: 'updateTime', type: 'time'},
];

/** 路由切换时清空旧详情；详情接口未返回分类名称时，通过分类列表补齐。 */
watch(() => route.params.fileId, async fileId => {
  const request = ++requestVersion;
  detail.value = {};
  emptyMessage.value = '文件已不存在';
  if (!fileId) return;
  loading.value = true;
  try {
    const response = await getModelDataFile(fileId);
    if (request !== requestVersion) return;
    detail.value = response.data || {};
    if (detail.value.id != null && !detail.value.categoryName) {
      const categories = await getFileTypes();
      if (request !== requestVersion) return;
      detail.value.categoryName = (categories.data || []).find(item => String(item.id) === String(detail.value.categoryId))?.name;
    }
  } catch {
    if (request === requestVersion) emptyMessage.value = '详情加载失败，请刷新重试';
  } finally {
    if (request === requestVersion) loading.value = false;
  }
}, {immediate: true});

// 离开页面后忽略尚未返回的详情请求。
onBeforeUnmount(() => {
  ++requestVersion;
});

/** 关闭详情页签并回到当前菜单下的文件列表，与详情路由的 activeMenu 保持一致。 */
function handleReturn() {
  proxy.$tab.closeOpenPage({path: '/basic/file/fileManager'});
}
</script>
