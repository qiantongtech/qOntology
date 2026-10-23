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
    <component :is="type" v-bind="linkProps()">
        <slot />
    </component>
</template>

<script setup>
    import { isExternal } from '@/utils/validate';
    import {useRoute} from 'vue-router';

    const route = useRoute();

    const props = defineProps({
        to: {
            type: [String, Object],
            required: true
        }
    });

    const isExt = computed(() => {
        return isExternal(props.to);
    });

    const type = computed(() => {
        if (isExt.value) {
            return 'a';
        }
        return 'router-link';
    });

    function resolveParams(path) {
        return path.replace(/:(\w+)/g, (match, key) => {
            const value = route.params[key];
            return value == null ? match : encodeURIComponent(String(value));
        });
    }

    function linkProps() {
        if (isExt.value) {
            return {
                href: props.to,
                target: '_blank',
                rel: 'noopener'
            };
        }
        return {
            // 菜单地址包含 :versionId 时，链接本身也要使用当前版本，
            // 避免 router-link 与菜单点击处理分别跳到两个地址。
            to: typeof props.to === 'string'
                ? resolveParams(props.to)
                : {...props.to, ...(props.to.path ? {path: resolveParams(props.to.path)} : {})}
        };
    }
</script>
