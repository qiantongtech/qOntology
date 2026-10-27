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
    <el-table
        :data="targets"
        :class="{ 'target-detail-table': !editable }"
        row-key="id"
        stripe
        :default-sort="editable ? undefined : { prop: 'createTime', order: 'descending' }"
        @sort-change="emit('sort-change', $event)"
    >
        <el-table-column
            v-if="visible('id')"
            prop="id"
            label="编号"
            width="85"
            align="center"
            :sortable="editable ? false : 'custom'"
            :sort-orders="['descending', 'ascending', null]"
        />
        <el-table-column
            v-if="visible('question')"
            prop="question"
            label="目标问题"
            :min-width="editable ? 220 : 360"
            :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
            v-if="visible('questionSource')"
            prop="questionSource"
            label="问题来源"
            :min-width="editable ? 130 : 180"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
            <template #default="{ row }">{{ row.questionSource || '-' }}</template>
        </el-table-column>
        <el-table-column
            v-if="visible('businessScenarios')"
            prop="businessScenarios"
            label="业务场景"
            :min-width="editable ? 130 : 220"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
            <template #default="{ row }">{{ row.businessScenarios || '-' }}</template>
        </el-table-column>
        <el-table-column
            v-if="visible('priority')"
            prop="priority"
            label="优先级"
            :width="editable ? 90 : 120"
            align="center"
        >
            <template #default="{ row }"><dict-tag :options="om_target_priority" :value="row.priority" /></template>
        </el-table-column>
        <el-table-column
            v-if="visible('relatedElementCount')"
            prop="relatedElementCount"
            label="关联元素"
            :width="editable ? 100 : 130"
            align="center"
        >
            <template #default="{ row }">{{ row.relatedElementCount ?? 0 }}</template>
        </el-table-column>
        <el-table-column
            v-if="!editable && visible('acceptStatus')"
            prop="acceptStatus"
            label="验证状态"
            width="130"
            align="center"
        >
            <template #default="{ row }"><dict-tag :options="om_accept_status" :value="row.acceptStatus" /></template>
        </el-table-column>
        <el-table-column
            v-if="!editable && visible('createBy')"
            prop="createBy"
            label="创建人"
            width="120"
            align="center"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
            <template #default="{ row }">{{ row.createBy || '-' }}</template>
        </el-table-column>
        <el-table-column
            v-if="!editable && visible('createTime')"
            prop="createTime"
            label="创建时间"
            width="170"
            align="center"
            sortable="custom"
            :sort-orders="['descending', 'ascending', null]"
        >
            <template #default="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
        </el-table-column>
        <el-table-column
            v-if="editable"
            label="操作"
            width="100"
            align="center"
            fixed="right"
            class-name="small-padding fixed-width"
        >
            <template #default="{ row }">
                <el-button link type="danger" icon="Delete" :disabled="disabled" @click="emit('remove', row)">
                    删除
                </el-button>
            </template>
        </el-table-column>
        <template #empty>
            <div class="emptyBg" :class="{ 'target-empty': editable }">
                <img src="@/assets/system/images/no_data/noData.png" alt="暂无建模目标" />
                <p>暂无建模目标</p>
            </div>
        </template>
    </el-table>
</template>
<script setup>
    import { getCurrentInstance } from 'vue';
    const props = defineProps({
        targets: { type: Array, default: () => [] },
        columns: { type: Array, default: () => [] },
        editable: { type: Boolean, default: false },
        disabled: { type: Boolean, default: false }
    });
    const visible = (prop) =>
        !props.columns.length || props.columns.some((column) => column.prop === prop && column.visible);
    const emit = defineEmits(['remove', 'sort-change']);
    const { proxy } = getCurrentInstance();
    const { om_target_priority, om_accept_status } = proxy.useDict('om_target_priority', 'om_accept_status');
</script>
<style scoped lang="scss">
    .target-detail-table :deep(.el-table__body .el-table__cell) {
        padding: 12px 0 !important;
    }
    .target-detail-table :deep(.cell) {
        padding-right: 16px;
        padding-left: 16px;
        line-height: 24px;
    }
    .target-empty {
        padding: 20px 0;
    }
    .target-empty img {
        width: 100px;
        height: 100px;
    }
</style>
