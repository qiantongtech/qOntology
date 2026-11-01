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
    <div
        :class="[
            'guide-tip',
            normalizedType,
            {
                'no-border': props.noBorder,
                'single-line-tip': isSingleLine,
                'allow-wrap': props.allowWrap
            }
        ]"
    >
        <div class="tip-header">
            <span class="tip-title">
                <svg-icon
                    v-if="normalizedType === 'warning'"
                    iconClass="warning"
                    class="tip-icon"
                />
                <el-icon v-if="normalizedType === 'danger'" class="tip-icon">
                    <InfoFilled />
                </el-icon>
                <svg-icon
                    v-if="normalizedType === 'remind'"
                    iconClass="remind"
                    class="tip-icon"
                />
                <el-icon
                    v-if="normalizedType === 'primary' || normalizedType === 'hint'"
                    class="tip-icon"
                >
                    <InfoFilled />
                </el-icon>
                <span v-if="title" v-html="title"></span>
                <slot name="title-append"></slot>
            </span>
            <slot name="append"></slot>
            <div v-if="showActions" class="header-buttons">
                <el-button
                    v-if="normalizedType !== 'danger'"
                    class="btn-never-show"
                    @click="$emit('neverShow')"
                >
                    不再展示
                </el-button>
                <el-button :class="['btn-close', normalizedType]" @click="$emit('close')">
                    关闭
                </el-button>
            </div>
        </div>
        <div
            v-if="content || $slots.default"
            class="tip-content"
            @click="$emit('contentClick', $event)"
        >
            <span v-if="content" v-html="content"></span>
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
    import { computed } from 'vue';
    import { InfoFilled } from '@element-plus/icons-vue';

    const props = defineProps({
        title: { type: String, default: '' },
        content: { type: String, default: '' },
        type: { type: String, default: 'info' },
        showActions: { type: Boolean, default: false },
        noBorder: { type: Boolean, default: false },
        singleLine: { type: Boolean, default: false },
        allowWrap: { type: Boolean, default: false }
    });

    defineEmits(['neverShow', 'close', 'contentClick']);

    const normalizedType = computed(() => props.type || 'info');
    const isSingleLine = computed(
        () => !props.allowWrap && (props.singleLine || props.type === 'hint')
    );
</script>

<style lang="less" scoped>
    @font-title: 'PingFang SC', 'Microsoft YaHei';
    @font-content: 'Microsoft YaHei';

    .guide-tip {
        width: 100%;
        padding: 8px 19px 8px 19px;
        position: relative;
        border-radius: 2px;

        &.remind {
            background-color: #fff7e6;
            border: 1px solid #ffe58f;
        }

        &.warning {
            background-color: #fceaea;
            border: 1px solid #ffacae;
        }

        &.danger {
            background-color: #ffacae;
            border: 1px solid #fe4f4f;

            .tip-icon {
                color: #fe4f4f;
            }
        }

        &.info {
            background-color: #fff;
            border: 1px solid #ddd;
        }

        &.primary {
            background-color: var(--el-color-primary-light-9);
            border: 1px solid var(--el-color-primary-light-5);

            .tip-icon {
                color: var(--el-color-primary);
            }
        }

        &.hint {
            background-color: var(--el-color-primary-light-9);
            border: 1px solid var(--el-color-primary-light-5);
            width: fit-content;

            .tip-header .tip-title {
                font-weight: var(--qt-font-weight-regular);
                color: var(--qt-text-regular);
            }

            .tip-icon {
                color: var(--el-color-primary);
            }
        }

        // 表格左上方提示专用：无图标、无背景、无边框，仅展示纯文本，自带自动换行。
        &.plain {
            background-color: transparent;
            border: none;
            width: fit-content;
            max-width: 100%;
            padding: 0px 0;

            .tip-header .tip-title {
                font-weight: var(--qt-font-weight-regular);
                color: var(--qt-text-regular);
                white-space: normal;
                overflow-wrap: anywhere;
            }
        }

        &.no-border {
            border: none;
        }

        .tip-header {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .tip-title {
                font-family: @font-title;
                font-size: 14px;
                color: var(--qt-text-regular);
                line-height: 24px;
                display: flex;
                align-items: center;

                .tip-icon {
                    font-size: 16px;
                    margin-right: 7px;
                }
            }

            .header-buttons {
                display: flex;
                margin-top: 2px;

                .el-button {
                    height: 22px;
                    font-family: PingFangSC-Regular, PingFangSC-Regular;
                    font-weight: normal;
                    font-size: 12px;
                    line-height: 18px;
                    text-align: left;
                    text-transform: none;
                    padding: 2px 4px;
                    border-radius: 2px;

                    &.btn-never-show {
                        background-color: #fff;
                        color: var(--qt-text-regular);
                        border: 1px solid #ffffff;
                    }

                    &.btn-close.remind {
                        background-color: #f88825;
                        color: var(--qt-text-inverse);
                        border: none;
                        padding: 4px 7px;
                        margin-left: 9px;
                    }

                    &.btn-close.warning,
                    &.btn-close.danger {
                        background-color: #fe4f4f;
                        color: var(--qt-text-inverse);
                        border: none;
                        padding: 4px 7px;
                        margin-left: 9px;
                    }
                }
            }
        }

        .tip-content {
            font-family: @font-content;
            font-weight: 400;
            font-size: 14px;
            color: var(--qt-text-regular);
            line-height: 22px;
            margin-top: 0px;
            padding-left: 23px;
            cursor: default;
        }

        &.single-line-tip {
            width: 100% !important;

            .tip-header {
                .tip-title {
                    flex: 1;
                    min-width: 0;
                    span:not(.tip-icon) {
                        display: block;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                    }
                }
            }
        }

        &.allow-wrap {
            width: 100% !important;

            .tip-header .tip-title {
                min-width: 0;
                align-items: flex-start;

                .tip-icon {
                    flex-shrink: 0;
                    margin-top: 4px;
                }

                > span:not(.tip-icon) {
                    min-width: 0;
                    white-space: normal;
                    overflow-wrap: anywhere;
                }
            }
        }
    }
</style>
