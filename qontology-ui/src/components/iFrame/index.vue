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
    <div v-loading="loading" :style="'height:' + height">
        <iframe :src="url" frameborder="no" style="width: 100%; height: 100%" scrolling="auto" />
    </div>
</template>

<script setup>
    const props = defineProps({
        src: {
            type: String,
            required: true
        }
    });

    const height = ref(document.documentElement.clientHeight - 94.5 + 'px;');
    const loading = ref(true);
    const url = computed(() => props.src);

    onMounted(() => {
        setTimeout(() => {
            loading.value = false;
        }, 300);
        window.onresize = function temp() {
            height.value = document.documentElement.clientHeight - 94.5 + 'px;';
        };
    });
</script>
