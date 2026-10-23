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
  <div v-loading="loading" class="app-container object-relation-detail-page">
    <el-result v-if="error" icon="warning" :title="error">
      <template #extra>
        <el-button @click="returnToList">返回列表</el-button>
        <el-button type="primary" @click="loadDetail">重新加载</el-button>
      </template>
    </el-result>
    <template v-else>
      <div class="pagecont-top" style="padding-bottom: 15px">
        <div class="infotop">
          <div class="infotop-title mb15">
            <div class="task-item">
              <div class="task-id">{{ detail.id ?? '-' }}</div>
              <div class="task-name">
                <el-tooltip :content="detail.name || ''" placement="top" effect="light" :disabled="!nameOverflow">
                  <span class="ellipsis-text" @mouseenter="checkNameOverflow">{{ detail.name || '对象关系详情' }}</span>
                </el-tooltip>
              </div>
            </div>
            <el-row :gutter="15" class="btn-style" style="margin-left: auto">
              <el-col :span="1.5">
                <el-button type="primary" size="small" class="fhbtn" plain @click="returnToList"
                           @mousedown="(event) => event.preventDefault()"
                >
                  <svg-icon icon-class="fhs" style="width: 12px; height: 12px; margin-right: 3px"/>
                  返回
                </el-button>
              </el-col>
            </el-row>
          </div>
          <el-row :gutter="5" class="info-row mb5">
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">起点类型</div>
                <div class="infotop-row-value">{{ detail.startTypeName || '-' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">终点类型</div>
                <div class="infotop-row-value">{{ detail.endTypeName || '-' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">是否可逆</div>
                <div class="infotop-row-value">{{ reversibleLabel(detail.reversibleFlag) }}</div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="5" class="info-row mb5">
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">反向名称</div>
                <div class="infotop-row-value">{{ detail.reverseName || '-' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">创建人</div>
                <div class="infotop-row-value">{{ detail.createBy || '-' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">创建时间</div>
                <div class="infotop-row-value">{{ proxy.parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="5" class="info-row">
            <el-col :span="24">
              <div class="infotop-row border-top">
                <div class="infotop-row-lable">描述</div>
                <div class="infotop-row-value">
                  <span class="description-text" :title="descriptionOverflow ? detail.description : undefined"
                        @mouseenter="checkDescriptionOverflow"
                  >{{ detail.description || '-' }}</span>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="pagecont-bottom">
        <el-tabs v-model="activeTab" class="demo-tabs">
          <el-tab-pane label="建模目标" name="goals">
            <TargetPanel v-if="detail.id" :key="String(detail.id)" :relation-id="String(detail.id)"/>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onBeforeUnmount, ref, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {getRelation} from '@/api/om/object/relation';
import TargetPanel from './TargetPanel.vue';

defineOptions({name: 'OmObjectTypeRelationDetail'});
const route = useRoute();
const router = useRouter();
const {proxy} = getCurrentInstance();
const {sys_yes_no} = proxy.useDict('sys_yes_no');
const reversibleLabel = value => sys_yes_no.value.find(dict => dict.value === (value === 1 ? 'Y' : value === 0 ? 'N' : ''))?.label || '-';
const detail = ref({});
const loading = ref(false);
const error = ref('');
const activeTab = ref('goals');
const nameOverflow = ref(false);
const descriptionOverflow = ref(false);
const returnPath = computed(() => `/om/${route.params.versionId}/ontology/objectRelation`);
let requestId = 0;
function checkNameOverflow(event) {
  const element = event.currentTarget;
  nameOverflow.value = element.scrollWidth > element.clientWidth;
}
function checkDescriptionOverflow(event) {
  const element = event.currentTarget;
  descriptionOverflow.value = element.scrollHeight > element.clientHeight || element.scrollWidth > element.clientWidth;
}
async function loadDetail() {
  const request = ++requestId;
  const id = route.params.id;
  detail.value = {};
  nameOverflow.value = false;
  descriptionOverflow.value = false;
  error.value = '';
  if (typeof id !== 'string' || !/^\d+$/.test(id)) {
    loading.value = false;
    error.value = '对象关系编号无效';
    return;
  }
  loading.value = true;
  try {
    const response = await getRelation(id);
    if (request !== requestId) return;
    if (!response.data || String(response.data.versionId) !== String(route.params.versionId)) { error.value = '对象关系不存在或已删除'; return; }
    detail.value = response.data;
  } catch {
    if (request === requestId) error.value = '对象关系详情加载失败';
  } finally {
    if (request === requestId) loading.value = false;
  }
}
function returnToList() {
  return router.push({path: returnPath.value, query: {}});
}
watch(() => [route.params.id, route.params.versionId], () => { activeTab.value = 'goals'; loadDetail(); }, {immediate: true});
onBeforeUnmount(() => { ++requestId; });
</script>

<style scoped lang="scss">
.task-item {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
  font-weight: 600;

  .task-id {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    flex-shrink: 0;
    padding: 2px;
    background-color: #2666fb;
    color: #fff;
    font-size: 14px;
    border-radius: 0;
    min-width: 20px;
    min-height: 20px;
  }
  .task-name { min-width: 0; }
  .ellipsis-text { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
}
.fhbtn {
  .svg-icon { font-size: 12px; margin-right: 3px; vertical-align: middle; margin-top: -3px; }
  &:hover .svg-icon { filter: brightness(0) invert(1) !important; }
}
.info-row > .el-col { display: flex; }
.object-relation-detail-page .infotop-row { width: 100%; align-items: stretch; min-height: 50px; }
.object-relation-detail-page .infotop-row-lable { flex-shrink: 0; }
.object-relation-detail-page .infotop-row-lable,
.object-relation-detail-page .infotop-row-value { height: auto; min-height: 50px; }
.object-relation-detail-page .infotop-row-value { min-width: 0; overflow-wrap: anywhere; }
.description-text {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: pre-wrap;
}
@media (max-width: 768px) {
  .object-relation-detail-page .infotop-row-lable { width: 110px; }
  .info-row { row-gap: 5px; }
}
</style>
