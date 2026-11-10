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
    <div v-loading="loading" class="app-container object-type-detail-page">
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
                    <el-tab-pane label="对象类型属性" name="attributes">
                        <AttributePanel
                            v-if="detail.id"
                            :key="String(detail.id)"
                            :object-type-id="String(detail.id)"
                            :version-id="detail.versionId"
                            @changed="refreshAttributeCount"
                        />
                    </el-tab-pane>
                    <el-tab-pane label="建模目标" name="goals" lazy>
                        <TargetPanel v-if="detail.id" :key="String(detail.id)" :object-type-id="String(detail.id)" />
                    </el-tab-pane>
                </el-tabs>
            </div>
        </template>
    </div>
</template>

<script setup>
    import { onBeforeUnmount, ref, watch } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { ElMessage } from 'element-plus';
    import { getType } from '@/api/om/object/type';
    import TargetPanel from '@/views/om/om/object/type/detail/TargetPanel.vue';
    import AttributePanel from '@/views/om/om/object/type/detail/AttributePanel.vue';
    import { useCreatorPhones } from '@/utils/useCreatorPhones';

    defineOptions({ name: 'OmObjectTypeDetail' });
    const route = useRoute();
    const router = useRouter();
    const { creatorPhones } = useCreatorPhones();
    const detail = ref({});
    const loading = ref(false);
    const loadFailed = ref(false);
    const   activeTab = ref('attributes');
    const descriptionOverflow = ref(false);
    let requestId = 0;

    const detailItems = [
        { label: '属性数量', key: 'attributeCount' },
        { label: '关系数量', key: 'relationCount' },
        { label: '创建人', key: 'createBy', formatter: (name, record) => {
            const phone = creatorPhones.value[record.creatorId];
            return phone ? `${name || '-'} / ${phone}` : name || '-';
        } },
        { label: '描述', key: 'description', span: 24, className: 'mt2 mb2', slot: 'description' },
        { label: '创建时间', key: 'createTime', type: 'time' },
        { label: '更新时间', key: 'updateTime', type: 'time' },
        { label: '更新人', key: 'updateBy', formatter: (name, record) => {
            const phone = creatorPhones.value[record.creatorId];
            return phone ? `${name || '-'} / ${phone}` : name || '-';
        } },

    ];
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
            ElMessage.warning('对象类型编号无效');
            return;
        }
        loading.value = true;
        try {
            const response = await getType(id);
            if (request !== requestId) return;
            if (!response.data || String(response.data.versionId) !== String(route.params.versionId)) {
                loadFailed.value = true;
                ElMessage.warning('对象类型不存在或已删除');
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
        let currentPath = route.path;
        let previousUrl;
        if (currentPath.includes('target/detail/object/type/detail')) {
            previousUrl = `/om/${route.params.versionId}/target/detail`;
            // 还原进入时的建模目标 id，否则目标详情页因缺少 query.id 而不加载数据
            router.push({ path: previousUrl, query: { id: route.query.targetId } });
            return;
        }
        router.push({ path: `/om/${route.params.versionId}/ontology/objectType`, query: {} });
    }
    async function refreshAttributeCount() {
        const request = requestId;
        const id = route.params.id;
        const response = await getType(id);
        if (request === requestId && id === route.params.id && response.data) {
            detail.value.attributeCount = response.data.attributeCount;
        }
    }
    watch(
        () => [route.params.id, route.params.versionId],
        () => {
            activeTab.value = 'attributes';
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
