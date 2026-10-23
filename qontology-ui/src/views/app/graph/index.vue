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
    <div class="app-container">
        <div class="pagecont-one">
            <Graph />
        </div>
    </div>
</template>
<script setup name="ExploreIndex">
    import Graph from './graph/index.vue';
</script>
<style lang="scss" scoped>
    .app-container {
        display: flex;
        flex-direction: column;
        .pagecont-one {
            flex: 1;
            min-height: 0;
            display: flex;
            flex-direction: column;
            border-radius: 2px;
            :deep(.graphView) {
                flex: 1;
                min-height: 0;
            }
        }
    }
</style>
