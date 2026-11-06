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
        <template v-if="!loadFailed">
            <detail-info
                :data="detail"
                :header="{ className: 'clearfixs', idKey: 'id', nameKey: 'name', routerBack: false }"
                :items="detailItems"
                mode="free"
                @back="returnToList"
            >
                <template #description>
                    <span
                        class="description-text"
                        :title="descriptionOverflow ? detail.description : undefined"
                        @mouseenter="checkDescriptionOverflow"
                        >{{ detail.description || '-' }}</span>
                </template>
            </detail-info>
            
            <div class="pagecont-bottom">
                <el-tabs v-model="activeTab" class="demo-tabs">
                    <el-tab-pane label="建模目标" name="goals">
                        <TargetPanel v-if="detail.id" :key="String(detail.id)" :relation-id="String(detail.id)" />
                    </el-tab-pane>
                </el-tabs>
            </div>
        </template>
    </div>
</template>

<script setup>
    import { computed, getCurrentInstance, onBeforeUnmount, ref, watch } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { ElMessage } from 'element-plus';
    import { getRelation } from '@/api/om/object/relation';
    import { useCreatorPhones } from '@/utils/useCreatorPhones';
    import TargetPanel from './TargetPanel.vue';

    defineOptions({ name: 'OmObjectTypeRelationDetail' });
    const route = useRoute();
    const router = useRouter();
    const { proxy } = getCurrentInstance();
    const { sys_yes_no } = proxy.useDict('sys_yes_no');
    const { creatorPhones } = useCreatorPhones();
    const reversibleLabel = (value) =>
        sys_yes_no.value.find((dict) => dict.value === (value === 1 ? 'Y' : value === 0 ? 'N' : ''))?.label || '-';
    const detail = ref({});
    const loading = ref(false);
    const loadFailed = ref(false);
    const activeTab = ref('goals');
    const descriptionOverflow = ref(false);
    const returnPath = computed(() => `/om/${route.params.versionId}/ontology/objectRelation`);
    let requestId = 0;
    
    const detailItems = computed(() => [
        { label: '起点类型', key: 'startTypeName' },
        { label: '终点类型', key: 'endTypeName' },
        { label: '是否可逆', key: 'reversibleFlag', formatter: (val) => reversibleLabel(val) },
        { label: '描述', key: 'description', span: 24, className: 'mb2', slot: 'description' },
        { label: '反向名称', key: 'reverseName' },
        { label: '创建人', key: 'createBy', formatter: (name, record) => {
            const phone = creatorPhones.value[record.creatorId];
            return phone ? `${name || '-'} / ${phone}` : name || '-';
        } },
        { label: '创建时间', key: 'createTime', type: 'time' },
    ]);
    function checkDescriptionOverflow(event) {
        const element = event.currentTarget;
        descriptionOverflow.value =
            element.scrollHeight > element.clientHeight || element.scrollWidth > element.clientWidth;
    }
    async function loadDetail() {
        const request = ++requestId;
        const id = route.params.id;
        detail.value = {};
        descriptionOverflow.value = false;
        loadFailed.value = false;
        if (typeof id !== 'string' || !/^\d+$/.test(id)) {
            loading.value = false;
            loadFailed.value = true;
            ElMessage.warning('对象关系编号无效');
            return;
        }
        loading.value = true;
        try {
            const response = await getRelation(id);
            if (request !== requestId) return;
            if (!response.data || String(response.data.versionId) !== String(route.params.versionId)) {
                loadFailed.value = true;
                ElMessage.warning('对象关系不存在或已删除');
                return;
            }
            detail.value = response.data;
        } catch {
            if (request === requestId) loadFailed.value = true;
        } finally {
            if (request === requestId) loading.value = false;
        }
    }
    function returnToList() {
        return router.push({ path: returnPath.value, query: {} });
    }
    watch(
        () => [route.params.id, route.params.versionId],
        () => {
            activeTab.value = 'goals';
            loadDetail();
        },
        { immediate: true }
    );
    onBeforeUnmount(() => {
        ++requestId;
    });
</script>

<style scoped lang="scss">
    .description-text {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: pre-wrap;
    }
</style>
