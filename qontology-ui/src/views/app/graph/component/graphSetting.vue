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
    <div :class="['graphSetting', { toggle: toggle }]">
        <div v-if="false" class="toggle" @click="handleToggle">
            <span class="toggle-text">{{ toggle ? '收起' : '展开' }}</span>
        </div>
        <!--		<div class="setting-title">-->
        <!--			<span>图谱设置</span>-->
        <!--		</div>-->
        <div class="border-item-head">
            <span class="head-title">图谱设置 </span>
        </div>
        <div class="setting-content">
            <el-form
                class="btn-style"
                :model="config"
                ref="configRef"
                :inline="true"
                label-width="75px"
                @submit.prevent
            >
                <el-form-item label="布局模式:" prop="layout">
                    <el-select
                        class="setting-form-width"
                        v-model="config.layout"
                        placeholder="请选择布局模式"
                        @change="syncConfig('layout', $event)"
                    >
                        <el-option
                            v-for="dict in layout_options"
                            :key="dict.value"
                            :label="dict.label"
                            :value="dict.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="实体大小:" prop="entitySize">
                    <el-slider
                        class="slider-config"
                        v-model="config.entitySize"
                        :min="10"
                        :max="24"
                        @change="syncConfig('entitySize', $event)"
                    />
                    <span>24</span>
                </el-form-item>
                <el-form-item label="连线宽度:" prop="lineWidth">
                    <el-slider
                        class="slider-config"
                        v-model="config.lineWidth"
                        :min="1"
                        :max="5"
                        @change="syncConfig('lineWidth', $event)"
                    />
                    <span>5</span>
                </el-form-item>
                <template v-if="config.layout !== 'force'">
                    <el-form-item label="加载动画:" prop="loadAnimation">
                        <el-select
                            class="setting-form-width"
                            v-model="config.loadAnimation"
                            placeholder="请选择加载动画"
                            @change="syncConfig('loadAnimation', $event)"
                        >
                            <el-option
                                v-for="dict in loadAnimation_options"
                                :key="dict.value"
                                :label="dict.label"
                                :value="dict.value"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="加载时间:" prop="loadTime">
                        <el-input
                            min="500"
                            max="5000"
                            type="number"
                            class="setting-form-width"
                            v-model="config.loadTime"
                            placeholder="请输入加载时间"
                            @blur="syncConfig('loadTime', $event)"
                        >
                            <template #append>ms</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item label="图谱特效:" prop="effect">
                        <el-select
                            class="setting-form-width"
                            v-model="config.effect"
                            placeholder="请选择图谱特效"
                            @change="syncConfig('effect', $event)"
                        >
                            <el-option
                                v-for="dict in effect_options"
                                :key="dict.value"
                                :label="dict.label"
                                :value="dict.value"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                </template>
                <el-form-item label="窗口设置:" prop="tool"> </el-form-item>
                <div class="tools-wrap">
                    <div class="tools">
                        <div
                            :class="['tool-item', 'tool' + index, 'tool-hover-wrap']"
                            v-for="(tool, index) in tools_options.slice(0, 4)"
                            :key="index"
                            @click="tool.fn"
                        >
                            <div class="img-wrap">
                                <img :src="getImage(tool.key)" alt="" class="default-img" />
                                <img :src="getImage(tool.key + 'Write')" alt="" class="hover-img" />
                            </div>
                            <span :title="tool.label"></span>
                        </div>
                    </div>
                    <div v-if="config.layout !== 'force'" class="tools-btn tools">
                        <div
                            :class="['tool-btn', 'tool' + index, 'tool-hover-wrap']"
                            v-for="(tool, index) in tools_options.slice(4, 8)"
                            :key="index"
                            @click="tool.fn"
                        >
                            <div class="img-wrap">
                                <img :src="getImage(tool.key)" class="default-img" alt="" />
                                <img :src="getImage(tool.key + 'Write')" class="hover-img" alt="" />
                            </div>
                            <span :title="tool.label">{{ tool.label }}</span>
                        </div>
                    </div>
                </div>
            </el-form>
        </div>
    </div>
</template>
<script setup name="GraphSetting">
    // const emit = defineEmits(["toolLeft", "toolRight", "toolUp", "toolDown", "toolZoomIn", "toolZoomOut", "toolRotateLeft", "toolRotateRight"]);
    const emit = defineEmits(['updateSetting', 'handleToolClick']);
    const props = defineProps({
        setting: {
            type: Object,
            required: true,
            default: () => {
                return {
                    layout: 'force-g6',
                    entitySize: 10,
                    lineWidth: 1,
                    tool: '',
                    loadAnimation: 'none',
                    loadTime: 1000,
                    effect: 'circle'
                };
            }
        }
    });
    const getImage = (key) => {
        return new URL(`/src/assets/kg/explore/tool_${key}.svg`, import.meta.url).href;
    };
    const gaphSetting = reactive({
        toggle: true,
        config: {
            layout: 'force-g6',
            entitySize: 10,
            lineWidth: 1,
            tool: '',
            loadAnimation: 'none',
            loadTime: 1000,
            effect: 'circle'
        },
        layout_options: [
            {
                label: '力导向布局(默认布局)',
                value: 'force-g6'
            },
            {
                label: '网络布局',
                value: 'grid'
            },
            {
                label: '层次布局',
                value: 'radial'
            },
            {
                label: '环形布局',
                value: 'circular'
            },
            {
                label: '同心圆布局',
                value: 'concentric'
            },
            // {
            //   label: "mds布局",
            //   value: "mds",
            // },
            {
                label: '力导向布局(Vis)',
                value: 'force'
            }
        ],
        entitySizeMarks: {
            10: '10',
            24: '24'
        },
        lineWidthMarks: {
            1: '1',
            5: '5'
        },
        loadAnimation_options: [
            {
                label: '默认',
                value: 'none'
            },
            {
                label: '线性匀速',
                value: 'linear'
            },
            {
                label: '缓入(正弦)',
                value: 'ease-in-sine'
            },
            {
                label: '缓入(三次方)',
                value: 'ease-in-cubic'
            }
        ],
        effect_options: [
            {
                label: '默认',
                value: 'circle'
            },
            {
                label: '呼吸效果',
                value: 'breathing-circle'
            },
            {
                label: '涟漪效果',
                value: 'ripple-circle'
            }
        ],
        tools_options: [
            {
                key: 'left',
                label: '左移',
                fn: () => {
                    emit('handleToolClick', 'toolLeft');
                }
            },
            {
                key: 'up',
                label: '上移',
                fn: () => {
                    emit('handleToolClick', 'toolUp');
                }
            },
            {
                key: 'right',
                label: '右移',
                fn: () => {
                    emit('handleToolClick', 'toolRight');
                }
            },
            {
                key: 'down',
                label: '下移',
                fn: () => {
                    emit('handleToolClick', 'toolDown');
                }
            },
            {
                key: 'big',
                label: '放大',
                fn: () => {
                    emit('handleToolClick', 'toolZoomIn');
                }
            },
            {
                key: 'small',
                label: '缩小',
                fn: () => {
                    emit('handleToolClick', 'toolZoomOut');
                }
            },
            {
                key: 'rightCircle',
                label: '顺时针旋转',
                fn: () => {
                    emit('handleToolClick', 'toolRotateRight');
                }
            },
            {
                key: 'leftCircle',
                label: '逆时针旋转',
                fn: () => {
                    emit('handleToolClick', 'toolRotateLeft');
                }
            }
        ]
    });
    const { toggle, layout_options, tools_options, loadAnimation_options, effect_options } =
        toRefs(gaphSetting);
    const config = reactive({
        ...toRefs(props.setting) // 保持与父组件数据的响应式关联
    });
    const syncConfig = (label, e) => {
        console.log('🚀 ~ syncConfig ~ label, e:', label, e);
        // 力导向布局重置g6配置
        if (e == 'force') {
            config.loadAnimation = 'none';
            config.loadTime = 1000;
            config.effect = 'circle';
        }
        if (label == 'loadTime') {
            const num = Number(config.loadTime);
            if (config.loadTime === '' || isNaN(num)) {
                config.loadTime = 1000;
            } else if (num < 500) {
                config.loadTime = 500;
            } else if (num > 5000) {
                config.loadTime = 5000;
            } else {
                config.loadTime = num;
            }
        }
        emit('updateSetting', label, { ...config }); // 传递最新的表单对象
    };
    const handleToggle = () => {
        gaphSetting.toggle = !gaphSetting.toggle;
    };
    defineExpose({ gaphSetting, toggle });
</script>
<style lang="scss" scoped>
    .graphSetting {
        // position: absolute;
        // top: 0;
        // left: -345px;
        width: 345px;
        height: 100%;
        margin-right: 10px;
        background: #fff;
        border: 1px solid rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        z-index: 1;
        &.toggle {
            left: 0;
        }
        .toggle {
            cursor: pointer;
            user-select: none;
            position: absolute;
            right: -20px;
            top: -1px;
            width: 20px;
            font-size: 12px;
            text-align: center;
            line-height: 1.2;
            background: #fff;
            border-radius: 0 4px 4px 0;
        }
        //.setting-title {
        //	display: flex;
        //	align-items: center;
        //	font-family: PingFang SC;
        //	font-weight: 500;
        //	font-size: 16px;
        //	color: var(--el-color-primary);
        //	height: 50px;
        //	padding: 0 20px;
        //	border-bottom: 1px solid #e8e8e8;
        //	&::before {
        //		display: inline-block;
        //		content: "";
        //		width: 3px;
        //		height: 16px;
        //		border-radius: 3px;
        //		background: var(--el-color-primary);
        //		margin-right: 10px;
        //	}
        //}

        .border-item-head {
            height: 50px;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e8e8e8;
        }

        .head-title {
            font-size: 16px;
            color: rgba(0, 0, 0, 0.85);
            display: flex;
            align-items: center;
            font-family: PingFang SC;
            font-weight: 500;

            &::before {
                display: inline-block;
                content: '';
                width: 6px;
                height: 16px;
                border-radius: 3px;
                background: var(--el-color-primary);
                margin-right: 8px;
            }
        }
        :deep(.setting-content) {
            height: calc(100% - 50px);
            padding: 20px;
            font-size: 14px;
            overflow: hidden auto;
            &::-webkit-scrollbar {
                width: 0px;
            }
            .el-form-item {
                margin-bottom: 20px;
                margin-right: 0;
            }
            .el-form-item__label {
                color: #262626;
            }
            .setting-form-width {
                width: 228px;
            }
            .slider-config {
                width: 195px;
                margin-right: 15px;
            }
            .tools-wrap {
                width: 100%;
                text-align: center;
                margin-bottom: 30px;
                .tools {
                    height: 80px;
                    //background: url("@/assets/kg/explore/tool_all.svg") no-repeat;
                    background-size: 100% 100%;
                    margin: 12px 21px 15px 20px;
                    position: relative;
                    .tool-item {
                        cursor: pointer;
                        text-align: center;
                        line-height: 30px;
                        border-radius: 4px;
                        background: #f3f7ff;
                        position: relative;
                        //opacity: 0;
                        &.tool0 {
                            position: absolute;
                            top: -30px;
                            left: -20px;
                            width: 76px;
                            height: 115px;
                            img {
                                position: absolute;
                                top: 50%;
                                left: 50%;
                                width: 14.18px;
                                height: 20px;
                                // 关键：反向偏移50%，实现绝对居中
                                transform: translate(-50%, -50%);
                                object-fit: contain;
                            }
                        }
                        &.tool1 {
                            position: absolute;
                            top: -30px;
                            left: 62px;
                            width: 138px;
                            height: 54px;
                            img {
                                position: absolute;
                                top: 50%;
                                left: 50%;
                                width: 20px;
                                height: 14.48px;
                                // 关键：反向偏移50%，实现绝对居中
                                transform: translate(-50%, -50%);
                                object-fit: contain;
                            }
                        }
                        &.tool2 {
                            position: absolute;
                            top: -30px;
                            left: 206px;
                            width: 76px;
                            height: 115px;
                            img {
                                position: absolute;
                                top: 50%;
                                left: 50%;
                                width: 14.48px;
                                height: 20px;
                                // 关键：反向偏移50%，实现绝对居中
                                transform: translate(-50%, -50%);
                                object-fit: contain;
                            }
                        }
                        &.tool3 {
                            position: absolute;
                            top: 30px;
                            left: 62px;
                            width: 138px;
                            height: 54px;
                            img {
                                position: absolute;
                                top: 50%;
                                left: 50%;
                                width: 20px;
                                height: 14.48px;
                                // 关键：反向偏移50%，实现绝对居中
                                transform: translate(-50%, -50%);
                                object-fit: contain;
                            }
                        }
                        span {
                            display: inline-block;
                            width: 100%;
                            height: 100%;
                            font-size: 12px;
                        }
                    }

                    .tool-hover-wrap {
                        .img-wrap {
                            .default-img {
                                display: block;
                            }
                            .hover-img {
                                display: none; /* 初始隐藏 */
                            }
                        }
                    }

                    .tool-hover-wrap:hover {
                        background-color: #3367fc; /* 悬浮时div背景色（可自定义） */
                    }
                    .tool-hover-wrap:hover .hover-img {
                        display: block; /* 显示悬浮图片 */
                    }
                    .tool-hover-wrap:hover .default-img {
                        display: none; /* 隐藏默认图片 */
                    }
                }
                .tools-btn {
                    width: 307px;
                    height: 55px;
                    margin-left: 1px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    .tool-btn {
                        background: #f3f7ff;
                        margin-right: 7px;
                        width: 25%;
                        height: 58px;
                        position: relative;
                        cursor: pointer;
                        pointer-events: auto !important;
                        & > * {
                            pointer-events: none;
                        }
                        img {
                            position: absolute;
                            top: 50%;
                            left: 50%;
                            // 关键：反向偏移50%，实现绝对居中
                            transform: translate(-50%, -50%);
                            object-fit: contain;
                            cursor: pointer;
                            width: auto; /* 自定义大小 */
                            height: auto; /* 保持比例 */
                        }
                        span {
                            position: absolute;
                            top: 50px;
                            left: 0;
                            right: 0;
                            display: inline-block;
                            font-family: PingFang SC;
                            font-weight: 400;
                            font-size: 12px;
                            color: #595959;
                            line-height: 22px;
                            margin-top: 10px;
                        }
                    }
                }
            }
        }
    }
</style>
