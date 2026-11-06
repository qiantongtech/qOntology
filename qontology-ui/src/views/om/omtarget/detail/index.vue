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
  <div class="app-container" ref="app-container">
    <detail-info
      :data="targetDetail"
      :header="{ className: 'clearfixs', idKey: 'id', nameKey: 'question', statusKey: 'acceptStatus', statusOptions: om_accept_status, routerBack: false }"
      :items="detailItems"
      mode="free"
      @back="goBack"
    >
      <template #priority><dict-tag :options="om_target_priority" :value="targetDetail.priority" /></template>
      <template #relatedElements><qt-tag-group :items="targetDetail.relatedElementTags || []" type="none" :max-count="2" /></template>
    </detail-info>

    <div class="pagecont-bottom">
      <el-tabs v-model="activeName" class="demo-tabs">
        <el-tab-pane label="对象类型" name="objectType">
          <object-type-comp v-if="targetDetail.id" :targetDetail="targetDetail"></object-type-comp>
        </el-tab-pane>
        <el-tab-pane label="对象类型关系" name="objectRelationship">
          <object-relationship-comp v-if="targetDetail.id" :targetDetail="targetDetail"></object-relationship-comp>
        </el-tab-pane>
        <el-tab-pane label="验证任务" name="acceptTask">
          <accept-task-comp v-if="targetDetail.id" :targetDetail="targetDetail" @updateAcceptStatus="updateAcceptStatus"></accept-task-comp>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup name="Target">
import { onBeforeUnmount } from 'vue';
import { useCreatorPhones } from '@/utils/useCreatorPhones';
import { withTargetRelatedElements } from '@/utils/targetRelatedElements';
import {getTarget} from "@/api/om/omtarget/target";
import {useRoute, useRouter} from 'vue-router';
import ObjectRelationshipComp from "@/views/om/omtarget/detail/objectRelationshipComp.vue";
import ObjectTypeComp from "@/views/om/omtarget/detail/objectTypeComp.vue";
import AcceptTaskComp from "@/views/om/omtarget/detail/acceptTaskComp.vue";

const {proxy} = getCurrentInstance();
const {om_accept_status, om_target_priority} = proxy.useDict('om_accept_status', 'om_target_priority');

const router = useRouter();
const { creatorPhones } = useCreatorPhones();
const route = useRoute();

const data = reactive({
  targetDetail: {},
  form: {},
});
const detailItems = [
  { label: '问题来源', key: 'questionSource' },
  { label: '业务场景', key: 'businessScenarios' },
  { label: '优先级', key: 'priority', slot: 'priority' },
  { label: '验收标准', key: 'acceptCriterion', span: 24, className: 'mt2 mb2' },
  { label: '创建人', key: 'createBy', formatter: (name, record) => {
    const phone = creatorPhones.value[record.creatorId];
    return phone ? `${name || '-'} / ${phone}` : name || '-';
  } },
  { label: '创建时间', key: 'createTime', type: 'time' },
  { label: '更新时间', key: 'updateTime', type: 'time' },
];
const activeName = ref('objectType');
const {targetDetail} = toRefs(data);

let detailRequest = 0;
let id = route.query.id || route.params.id;

// 监听 id 变化
watch(
    () => [route.query.id, route.params.id, route.params.versionId],
    () => {
      id = route.query.id || route.params.id;
      targetDetail.value = {};
      activeName.value = 'objectType';
      getTargetDetailById();
    },
    {immediate: true}  // `immediate` 为 true 表示页面加载时也会立即执行一次 watch
);

async function getTargetDetailById() {
  const request = ++detailRequest;
  if (!id) { targetDetail.value = {}; return; }
  try {
    const response = await getTarget(id);
    if (request !== detailRequest) return;
    if (!response.data) { targetDetail.value = {}; return; }
    const [record] = await withTargetRelatedElements([response.data], route.params.versionId);
    if (request !== detailRequest) return;
    targetDetail.value = record;
  } catch {
    if (request === detailRequest) targetDetail.value = {};
  }
}
function goBack() { proxy.$tab.closeOpenPage({ path: router.currentRoute.value.path.replace('/detail', '') }); }

function updateAcceptStatus(val) {
  targetDetail.value.acceptStatus = val;
  getTargetDetailById();
}
onBeforeUnmount(() => { ++detailRequest; });
</script>
