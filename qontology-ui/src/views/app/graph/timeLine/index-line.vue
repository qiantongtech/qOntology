<!--
Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

qOntology is licensed under Apache License 2.0 with additional qOntology terms.
You may use qOntology for commercial purposes, but you may not remove, hide,
modify, or replace the qOntology logo, copyright notices, license notices,
or attribution information without a separate commercial license.

White-label use, OEM distribution, rebranding, or presenting qOntology as
another product requires separate commercial authorization from
Jiangsu Qiantong Technology Co., Ltd.

Business License: https://community.qknow.ai/business/policy.html
See the LICENSE file in the project root for full license information.
-->

<template>
    <div class="timeLineView" v-loading="loading">
        <el-form
            class="view-form"
            :model="queryParams"
            ref="queryRef"
            :inline="true"
            label-width="80px"
            @submit.prevent
        >
            <el-form-item label="事件类型" prop="name">
                <el-select
                    class="el-form-input-width"
                    v-model="queryParams.type"
                    placeholder="类型"
                    clearable
                >
                    <el-option
                        v-for="dict in app_event_type"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="发生时间">
                <el-date-picker
                    class="el-form-input-width"
                    v-model="dateRange"
                    value-format="YYYY-MM-DD"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                ></el-date-picker>
            </el-form-item>

            <el-form-item>
                <el-button
                    plain
                    type="primary"
                    @click="handleQuery"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                </el-button>
                <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
                    <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
                </el-button>
            </el-form-item>
        </el-form>
        <div class="view-wrap">
            <!-- 左侧弹框 -->
            <LineDialog
                ref="lineDialogRef"
                :data="lineData"
                @handleEntityClick="handleEntityClick"
            />
            <!-- 时间线 -->
            <div class="timeline-container" ref="timelineRef"></div>
            <!-- 右侧弹框 -->
            <NodeDialog ref="nodeDialogRef" @dialogClose="nodeDialogClose" />
        </div>
    </div>
</template>

<script setup name="TimeLineView">
    // 引入组件
    import LineDialog from '../component/lineDialog.vue';
    import NodeDialog from '../component/nodeDialog.vue';
    // 综合接口
    import { getGraph } from '@/api/app/graph';
    import { listEvent } from '@/api/app/event/event';
    import { TimeLine } from '@/utils/graph/visTimeLine';

    const route = useRoute();
    const { proxy } = getCurrentInstance();
    const { app_event_type } = proxy.useDict('app_event_type');
    const loading = ref(false);
    const data = reactive({
        form: {},
        queryParams: {
            type: ''
        },
        dateRange: [],
        rules: {
            name: [{ required: true, message: '任务名称不能为空', trigger: 'blur' }]
        },
        lineData: []
    });
    const { queryParams, dateRange, lineData } = toRefs(data);

    /** 查询图谱模型管理列表 */
    function getList() {
        loading.value = true;
        let param = {
            ...queryParams.value
        };
        if (dateRange.value && dateRange.value.length > 0) {
            param.occurBeginTime = dateRange.value[0];
            param.occurEndTime = dateRange.value[1];
        }
        listEvent(param).then((res) => {
            lineData.value = res.data.rows;
        });
        getGraph({ entityType: 0, graphId: route.params.kgId })
            .then((response) => {
                loading.value = false;
                let data = response.data;
                // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
                nodes.value = data.entities
                    .filter((item) => item.createTime)
                    .map((item) => {
                        return {
                            ...item,
                            id: item.id + '',
                            label:
                                item.name.length > 6 ? item.name.substring(0, 6) + '...' : item.name
                        };
                    });
                edges.value = [];
                data.relationships.map((item) => {
                    if (
                        data.entities.some((node) => node.id == item.startId) &&
                        data.entities.some((node) => node.id == item.endId)
                    ) {
                        edges.value.push({
                            ...item,
                            id: item.id + '',
                            name: item.relationType,
                            label: item.relationType, //关系
                            from: item.startId + '', //头部实体
                            to: item.endId + '' //尾部实体
                        });
                    }
                });
                initTimeLine();
            })
            .finally(() => {
                loading.value = false;
            });
    }
    /** 搜索按钮操作 */
    function handleQuery() {
        getList();
    }
    /** 重置按钮操作 */
    function resetQuery() {
        queryParams.value = {
            ...queryParams.value,
            type: ''
        };
        dateRange.value = [];
        proxy.resetForm('queryRef');
        handleQuery();
    }
    // 时间线参数
    const timelineRef = ref();
    const nodeDialogRef = ref();
    const timelineStore = reactive({
        timelineGraph: null,
        nodes: [],
        edges: [],
        currentNodeData: null
    });
    const { timelineGraph, nodes, edges, currentNodeData } = toRefs(timelineStore);
    // 初始化时间线
    function initTimeLine() {
        let data = {
            nodes: nodes.value,
            edges: edges.value
        };
        let options = {};
        timelineGraph.value = new TimeLine(timelineRef.value, data, options);
        timelineEvent();
    }
    // 初始化时间线事件
    function timelineEvent() {
        timelineGraph.value.on('click', function (e) {
            if (e.item) {
                currentNodeData.value = nodes.value.find((item) => `node-${item.id}` == e.item);
                let param = {
                    currNodeData: currentNodeData.value,
                    edgesData: edges.value,
                    taskInfo: { id: '', name: '图谱探索', pageType: '0' }
                };
                nodeDialogRef.value.dialogOpen(param);
            } else {
                currentNodeData.value = null;
                nodeDialogRef.value.dialogClose();
            }
            console.log('🚀 ~ timelineEvent ~ e:', e, currentNodeData.value);
        });
    }
    // 左侧时间轴-点击实体
    function handleEntityClick(data) {
        console.log('🚀 ~ handleEntityClick ~ data:', data);
        timelineGraph.value.instance.setSelection(`node-${45}`);
        timelineGraph.value.instance.focus(`node-${45}`, {
            zoom: true
        });
    }
    // 右侧弹框-关闭
    function nodeDialogClose() {
        timelineGraph.value.instance.setSelection([]);
    }
    onMounted(() => {
        getList();
    });
</script>

<style scoped lang="scss">
    .timeLineView {
        height: auto;
        .view-wrap {
            display: flex;
            height: 64vh;
            position: relative;
            :deep(.timeline-container) {
                flex: 1;
                height: 100%;
                background: #fff url('@/assets/ke/images/bg1.png') no-repeat;
                background-size: 100% 100%;
                .vis-time-axis .vis-text {
                    color: #fff;
                }
            }
        }
    }
</style>
