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
    <div class="lineDialog">
        <el-timeline>
            <el-timeline-item
                v-for="(item, index) in lineData"
                :key="index"
                :icon="item.icon"
                :type="'primary'"
                :color="item.color"
                :size="item.size || 'large'"
                :hollow="true"
            >
                <div :class="['line-card']">
                    <div class="line-title">
                        <span class="title-text" :title="item.name">{{ item.name }}</span>
                        <dict-tag :options="kg_event_type" :value="item.type" />
                        <el-tag class="title-toggle" @click="item.toggle = !item.toggle">
                            <span class="toggle-text">{{ item.toggle ? '收起' : '展开' }}</span>
                            <el-icon :class="{ toggle: item.toggle }"><ArrowRight /></el-icon>
                        </el-tag>
                    </div>
                    <div class="line-content">
                        <div class="line-item">
                            <div class="line-label">发生时间:</div>
                            <div class="line-value">{{ item.timestamp }}</div>
                        </div>
                        <div class="line-item">
                            <div class="line-label">发生地点:</div>
                            <div class="line-value">{{ item.occurPlace }}</div>
                        </div>
                        <el-collapse-transition>
                            <div v-if="item.toggle">
                                <div class="line-item">
                                    <div class="line-label">参与实体:</div>
                                    <div class="line-value entity-tags">
                                        <el-tag
                                            v-for="tag in item.entityList"
                                            :key="tag"
                                            @click="handleEntityClick(tag)"
                                            >{{ tag.entityName }}</el-tag
                                        >
                                    </div>
                                </div>
                                <div class="line-item">
                                    <div class="line-label">事件描述:</div>
                                    <div class="line-value">{{ item.description }}</div>
                                </div>

                                <div class="line-item line-item-c">
                                    <div class="line-label">操作:</div>
                                    <div class="line-value">
                                        <el-checkbox
                                            v-model="item.checked"
                                            :label="item"
                                            @change="handleCheckboxChange(item)"
                                            :key="item.id"
                                        >
                                            查看图谱
                                        </el-checkbox>
                                    </div>
                                </div>
                            </div>
                        </el-collapse-transition>
                    </div>
                </div>
            </el-timeline-item>
        </el-timeline>
    </div>
</template>
<script setup name="LineDialog">
    import moment from 'moment';

    const { proxy } = getCurrentInstance();
    const { kg_event_type } = proxy.useDict('kg_event_type');
    const props = defineProps({
        data: {
            type: Array,
            default: () => []
        }
    });
    const emit = defineEmits(['handleEntityClick', 'handleEventClick']);
    const lineStore = reactive({
        lineData: []
    });
    const { lineData } = toRefs(lineStore);
    watch(
        () => props.data,
        (val) => {
            lineData.value = val.map((item) => {
                return {
                    ...item,
                    timestamp: moment(item.occurTime).format('YYYY-MM-DD'),
                    toggle: false,
                    checked: false
                };
            });
        },
        {
            immediate: true
        }
    );
    const handleEntityClick = (tag) => {
        emit('handleEntityClick', tag);
    };
    // 1. 新增：维护选中的项（替换为你的实际循环数据，这里假设是list数组）
    // 注意：如果这是循环中的子组件，需把selectedItems定义在父组件并通过props传递

    // 使用计算属性获取所有选中的项
    const selectedItems = computed(() => {
        return lineData.value.filter((item) => item.checked);
    });
    // 2. 新增：计算合并后的entityList（核心）
    const mergedEntityList = computed(() => {
        // 如果没有选中的项，返回所有项的实体
        if (selectedItems.value.length === 0) {
            // const allEntities = [];
            // lineData.value.forEach(item => {
            //   if (item.entityList && Array.isArray(item.entityList)) {
            //     allEntities.push(...item.entityList);
            //   }
            // });
            // const uniqueEntities = Array.from(
            //     new Map(
            //         allEntities.map(item => [item.entityName || item.id, item])
            //     ).values()
            // );
            // return uniqueEntities;
            return null;
            emit('resetData');
        }

        // 如果有选中的项，只返回选中项的实体
        const allEntities = [];
        selectedItems.value.forEach((item) => {
            if (item.entityList && Array.isArray(item.entityList)) {
                allEntities.push(...item.entityList);
            }
        });
        const uniqueEntities = Array.from(
            new Map(allEntities.map((item) => [item.entityName || item.id, item])).values()
        );
        return uniqueEntities;
    });

    const handleCheckboxChange = (item) => {
        // 每次勾选/取消，都传递合并后的所有数据
        emit('handleEventClick', mergedEntityList.value);
    };

    const handleEventClick = (item) => {
        emit('handleEventClick', item);
    };
</script>
<style lang="scss" scoped>
    .lineDialog {
        height: 100%;
        width: 345px;
        padding: 20px 12px 20px 20px;
        background-color: #fff;
        margin-right: 10px;
        overflow-y: auto;
        &::-webkit-scrollbar {
            width: 0px;
        }
        :deep(.el-timeline) {
            padding: 0;
            .el-timeline-item {
                padding-bottom: 0px;
            }
            .el-timeline-item__tail {
                border-left-width: 1px;
                left: 6px;
                background: #e5e5e5;
            }
            .el-timeline-item__wrapper {
                padding-left: 24px;
            }
            .el-timeline-item__node--large {
                width: 18px;
                height: 18px;
            }
            .el-timeline-item__node.is-hollow {
                border-width: 4px;
            }
        }
        :deep(.line-card) {
            min-height: 24px;
            overflow: hidden;
            font-family: PingFang SC;
            .el-tag.el-tag--default .el-tag__content {
                font-size: 12px !important;
            }
            .line-title {
                display: flex;
                align-items: center;
                font-size: 14px;
                position: relative;
                .title-text {
                    cursor: pointer;
                    font-weight: 600;
                    font-size: 14px;
                    color: #262626;
                    max-width: 100px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    margin-right: 10px;
                }
                .title-toggle {
                    cursor: pointer;
                    user-select: none;
                    position: absolute;
                    right: 0;
                    transform: rotate(0deg);
                    border: 1px solid #2666fb !important;
                    .el-tag__content {
                        display: flex;
                        align-items: center;
                    }
                    .toggle-text {
                        display: inline-block;
                        font-size: 12px !important;
                        line-height: 1;
                        margin-right: 3px;
                    }
                    .el-icon {
                        font-weight: bold;
                        font-size: 10px;
                        transition: transform 0.3s ease-in-out;
                    }
                    .toggle {
                        transform: rotate(90deg);
                    }
                }
            }
            .line-content {
                padding: 10px 0;
                .line-item {
                    display: flex;
                    line-height: 22px;
                    margin-bottom: 10px;
                    font-weight: 400;
                    .line-label {
                        width: 60px;
                        text-align: right;
                        margin-right: 6px;
                        color: #262626;
                    }
                    .line-value {
                        width: calc(100% - 66px);
                        word-break: break-all;
                        color: #595959;
                        text-align: justify;
                    }
                }
                .line-item-c {
                    align-items: center;
                }
            }
            .entity-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 0.5rem;
                .el-tag {
                    cursor: pointer;
                    background: #f4f5f5;
                    color: #909399;
                }
            }
        }
    }
</style>
