<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
   *
  Software Name: qKnow Knowledge Platform (Business Edition)
  Software Copyright Registration No. 15980140
   *
  [RIGHTS AND LICENSE STATEMENT]
  This file contains non-public commercial source code of which Jiangsu Qiantong
  Technology Co., Ltd. lawfully possesses complete intellectual property rights.
   *
  Access and use are limited to entities or individuals who have signed a valid
  commercial license agreement, within the scope stipulated in the agreement.
  The "accessibility" of this source code is premised on lawful authorization
  and does not constitute any form of transfer of intellectual property rights
  or implied licensing.
   *
  [PROHIBITIONS]
  Unless explicitly agreed in the license agreement, the following acts in any
  form are strictly prohibited:
  1. Copying, disseminating, disclosing, selling, renting, or redistributing
  this source code;
  2. Providing the software's functionality to third parties via SaaS, PaaS,
  cloud hosting, or other means;
  3. Using this software or its derivative versions to develop products that
  compete with the Right Holder;
  4. Providing or displaying this source code or related technical information
  to unauthorized third parties;
  5. Tampering with, circumventing, or destroying copyright notices, license
  verifications, or other technical protection measures.
   *
  [LEGAL LIABILITY]
  Any unauthorized use constitutes an infringement of trade secrets and
  intellectual property rights.
   *
  The Right Holder will strictly pursue liability for breach of contract and
  infringement in accordance with the commercial agreement and laws such as
  the "Copyright Law of the People's Republic of China" and the "Anti-Unfair
  Competition Law".
   *
  ============================================================================
   *
  Copyright (c) 2026 江苏千桐科技有限公司
   *
  软件名称：qKnow 智能体构建平台（商业版） | 软著登字第15980140号
   *
  【权利与授权声明】
  本文件属于江苏千桐科技有限公司依法享有完全知识产权的非公开商业源代码。
  仅限已签署有效商业授权合同的单位或个人在约定范围内查阅和使用。
  源代码的“可访问性”均以合法授权为前提，不构成任何形式的知识产权转让或默示授权。
   *
  【禁止事项】
  除授权合同明确约定外，严禁任何形式的：
  1. 复制、传播、披露、出售、出租或再分发本源代码；
  2. 通过 SaaS、PaaS、云托管等方式向第三方提供本软件功能；
  3. 将本软件或其衍生版本用于开发与权利人构成竞争的产品；
  4. 向未授权第三方提供或展示本源代码或相关技术信息；
  5. 篡改、规避或破坏版权标识、授权校验及其他技术保护措施。
   *
  【法律责任】
  任何未经授权的利用行为，均构成对商业秘密及知识产权的侵害。
  权利人将依据商业合同及《中华人民共和国著作权法》《反不正当竞争法》
  等法律法规，严厉追究违约与侵权责任。
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
                        v-for="dict in kg_event_type"
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
    import { listEvent } from '@/api/kg/event/event';
    import { TimeLine } from '@/utils/graph/visTimeLine';

    const route = useRoute();
    const { proxy } = getCurrentInstance();
    const { kg_event_type } = proxy.useDict('kg_event_type');
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
