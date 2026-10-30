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
    <div v-loading="loading">
        <div class="target-toolbar mb15">
            <div class="target-note">
                <el-icon class="target-note-icon"><InfoFilled /></el-icon>
                <span>展示当前对象类型关联的建模目标，便于核对该类型的设计是否符合目标要求。</span>
            </div>
            <right-toolbar :search="false" :columns="columns" @queryTable="getList" />
        </div>
        <TargetTable :targets="targets" :columns="columns" @sort-change="handleSortChange" />
        <pagination
            v-show="total > 0"
            :total="total"
            v-model:page="query.pageNum"
            v-model:limit="query.pageSize"
            :auto-scroll="false"
            @pagination="getList"
        />
    </div>
</template>
<script setup>
    import { onBeforeUnmount, reactive, ref, watch } from 'vue';
    import { InfoFilled } from '@element-plus/icons-vue';
    import { pageTypeTargets } from '@/api/om/object/type';
    import TargetTable from '../TargetTable.vue';
    const props = defineProps({ objectTypeId: { type: [String, Number], required: true } });
    const targets = ref([]);
    const loading = ref(false);
    const total = ref(0);
    const columns = ref(
        [
            { prop: 'id', label: '编号' },
            { prop: 'question', label: '目标问题' },
            { prop: 'questionSource', label: '问题来源' },
            { prop: 'businessScenarios', label: '业务场景' },
            { prop: 'priority', label: '优先级' },
            { prop: 'relatedElementCount', label: '关联元素' },
            { prop: 'acceptStatus', label: '验证状态' },
            { prop: 'createBy', label: '创建人' },
            { prop: 'createTime', label: '创建时间' }
        ].map((column, key) => ({ ...column, key, visible: true }))
    );
    const query = reactive({ pageNum: 1, pageSize: 10, orderByColumn: 'createTime', isAsc: 'descending' });
    let requestId = 0;
    async function getList() {
        const request = ++requestId;
        loading.value = true;
        try {
            const response = await pageTypeTargets(props.objectTypeId, { ...query });
            if (request !== requestId) return;
            total.value = response.data.total;
            const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
            if (query.pageNum > maxPage) {
                query.pageNum = maxPage;
                return await getList();
            }
            targets.value = response.data.rows || [];
        } catch {
            if (request === requestId) {
                targets.value = [];
                total.value = 0;
            }
        } finally {
            if (request === requestId) loading.value = false;
        }
    }
    function handleSortChange({ prop, order }) {
        query.orderByColumn = order ? prop : undefined;
        query.isAsc = order || undefined;
        query.pageNum = 1;
        getList();
    }
    watch(
        () => props.objectTypeId,
        () => {
            query.pageNum = 1;
            targets.value = [];
            total.value = 0;
            getList();
        },
        { immediate: true }
    );
    onBeforeUnmount(() => {
        ++requestId;
    });
</script>

<style scoped lang="scss">
    .target-toolbar {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 12px;
    }
    .target-note {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #909399;
        font-size: 12px;
        line-height: 18px;
    }
    .target-note-icon {
        flex-shrink: 0;
        font-size: 14px;
    }
</style>
