<!--
 /*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */
-->

<template>
    <div>
        <!--    <svg-icon :icon-class="isFullscreen ? 'exit-fullscreen-new' : 'fullscreen_new_icon'" @click="toggle" :style="{width:'20px',height:'20px',paddingTop:'2px'}"/>-->

        <i
            :class="
                isFullscreen
                    ? 'iconfont icon-fullscreen-exit-line'
                    : 'iconfont icon-a-quanpingxianxing'
            "
            style="font-size: 20px"
            @click="toggle"
        ></i>
    </div>
</template>

<script setup>
    import { useFullscreen } from '@vueuse/core';

    const { isFullscreen, enter, exit, toggle } = useFullscreen();
</script>

<style lang="scss" scoped>
    .screenfull-svg {
        display: inline-block;
        cursor: pointer;
        fill: #5a5e66;
        width: 20px;
        height: 20px;
        vertical-align: 10px;
    }
</style>
