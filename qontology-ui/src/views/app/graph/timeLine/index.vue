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
            @submit.prevent
        >
            <el-form-item label="事件类型" prop="name">
                <el-select
                    class="el-form-input-width"
                    v-model="queryParams.type"
                    placeholder="请选择事件类型"
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
                @handleEventClick="handleEventClick"
                @resetData="getList"
            />
            <!-- 图例 -->
            <div :class="['tool']" @click="toolShow = !toolShow">
                <div class="tool-mask" v-if="toolShow">
                    <div class="mask-item" v-for="item in toolData" :key="item.label">
                        <div
                            class="mask-icon"
                            :style="{
                                background: item.icon,
                                border: '2px solid ' + 'rgba(0,0,0, 0.3)'
                            }"
                        ></div>
                        <div class="mask-label ellipsis" :title="item.label">
                            {{ item.label }}
                        </div>
                    </div>
                </div>
            </div>
            <!-- 时间线 -->
            <div v-loading="graghLoading" class="timeline-container" ref="timelineRef"></div>
            <!-- 右侧弹框 -->
            <NodeDialog
                ref="nodeDialogRef"
                @dialogClose="nodeDialogClose"
                @handleRefresh="handleRefresh"
            />
        </div>
    </div>
</template>

<script setup name="TimeLineView">
    // 引入组件
    import LineDialog from '../component/lineDialog.vue';
    import NodeDialog from '../component/nodeDialog.vue';
    // 综合接口
    import { getGraph, getDynamicEntityList } from '@/api/app/graph';
    import { listSchema } from '@/api/ext/extSchema/schema';
    import { listEvent } from '@/api/kg/event/event';
    // 引入gragh方法
    import { VisNetwork } from '@/utils/graph/visNetwork';
    import { G6Network } from '@/utils/graph/g6Network';

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
            ...queryParams.value,
            graphId: route.params.kgId
        };
        if (dateRange.value && dateRange.value.length > 0) {
            param.occurBeginTime = dateRange.value[0];
            param.occurEndTime = dateRange.value[1];
        }
        listEvent(param)
            .then((res) => {
                lineData.value = res.data.rows;
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
        getGraphData(null);
        handleQuery();
    }
    // 时间线参数
    // #region视图容器方法
    const timelineRef = ref();
    const nodeDialogRef = ref();
    let relationGraph = null;
    const relationData = reactive({
        graghLoading: false,
        // schema&tool图例
        schemaList: { data: { rows: [] } },
        toolShow: false,
        toolData: [
            {
                label: '其他',
                icon: '#7DBFFA'
            }
        ],
        // 图谱参数-unselectAll方法异常
        // graph: null,
        nodes: [],
        edges: [],
        graphData: [],
        currentNodeData: null,
        // 搜索数据
        searchData: []
    });
    const {
        schemaList,
        toolShow,
        toolData,
        graghLoading,
        nodes,
        edges,
        currentNodeData,
        searchData,
        graphData
    } = toRefs(relationData);
    // 初始化加载数据
    const loadData = async () => {
        await getSchemaList();
        await getGraphData();
    };

    const handleRefresh = () => {
        toolbarClick({
            id: 'reset'
        });
    };
    const toolbarClick = async (item) => {
        const animation = {
            duration: 500,
            easing: 'linear'
        };

        switch (item.id) {
            case 'full-screen': {
                toggle();
                isfull.value = !isfull.value;
                break;
            }
            case 'zoom-in': {
                const zoom = relationGraph.instance.getZoom();
                relationGraph.instance.zoomTo(zoom + 0.2, animation);
                break;
            }
            case 'zoom-out': {
                const zoom = relationGraph.instance.getZoom();
                relationGraph.instance.zoomTo(zoom - 0.2, animation);
                break;
            }
            case 'offset-left': {
                relationGraph.instance.translateBy([-300, 0], {
                    duration: 300,
                    easing: 'easeCubic'
                });
                break;
            }

            case 'offset-right': {
                relationGraph.instance.translateBy([300, 0], {
                    duration: 300,
                    easing: 'easeCubic'
                });
                break;
            }
            case 'auto-fit': {
                // 自适应视图
                relationGraph.instance.fitView(); // 20px 的边距
                break;
            }
            case 'export': {
                // 导出图片
                try {
                    const dataURL = await relationGraph.instance.toDataURL('image/png');
                    const link = document.createElement('a');
                    link.href = dataURL;
                    link.setAttribute('download', taskInfo.value.name + '抽取结果.png');
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                } catch (error) {
                    console.error('导出失败:', error);
                }
                break;
            }
            case 'reset': {
                if (relationGraph !== null) {
                    relationGraph.destroy();
                    relationGraph = null;
                }
                // 关闭弹框
                // detailShow.value = false;
                currentNodeData.value = null;
                // 重新加载数据
                // let params = {
                //   entityType: taskInfo.value.pageType,
                //   entityId: taskInfo.value.id,
                // };
                // getGraphData(params);
                getGraphData();

                // 根据taskId获取段落数据和文档
                // if (taskInfo.value.pageType == "2" || taskInfo.value.pageType == "0") {
                //   getTextListAndDocList(params);
                // }
                break;
            }
            case 'search': {
                searchShow.value = !searchShow.value;
                break;
            }
        }
    };
    const getGraphData = async () => {
        graghLoading.value = true;
        const params = {
            entityType: 0,
            graphId: route.params.kgId
        };
        getGraph(params)
            .then((response) => {
                let data = response.data;
                graphData.value = data;
                // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
                nodes.value = data.entities.map((item) => {
                    let row = schemaList.value.data.rows.find((i) => item.schemaId == i.id);
                    return {
                        ...item,
                        id: item.id + '',
                        label: item.name.length > 6 ? item.name.substring(0, 6) + '...' : item.name,
                        rowBackground: row ? row.color : '#315790',
                        rowBorder: row ? `${row.color}4d` : '#7dbffa4d',
                        color: {
                            background: row ? row.color : '#315790',
                            border: row ? `${row.color}4d` : '#7dbffa4d'
                        },
                        borderWidth: row ? 15 : 0,
                        step: row ? 1 : 0
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
                // 搜索值
                searchData.value = nodes.value.map((item) => {
                    return { ...item, value: item.name, name: item.name };
                });
            })
            .finally(() => {
                initG6Gragh({ nodes: nodes.value, edges: edges.value });
                graghLoading.value = false;
            });
    };
    const settingValue = reactive({
        layout: 'force-g6',
        entitySize: 10,
        lineWidth: 1,
        tool: '',
        loadAnimation: 'none',
        loadTime: '1000',
        effect: 'circle'
    });
    const initG6Gragh = (option) => {
        if (relationGraph) {
            relationGraph.destroy();
            relationGraph = null;
        }
        let data = {
            nodes: option.nodes.map((item) => {
                return {
                    ...item,
                    id: item.id + '',
                    size: settingValue.entitySize * 2,
                    halo: true,
                    style: {
                        fill: item.rowBackground,
                        haloLineWidth: item.borderWidth
                    }
                };
            }),
            edges: option.edges.map((item) => {
                return {
                    ...item,
                    lineWidth: settingValue.lineWidth || 1,
                    id: 'edge' + item.id,
                    name: item.relationType, //关系
                    source: item.startId + '', //头部实体
                    target: item.endId + '' //尾部实体
                };
            })
        };
        let options = {
            layout: {
                type: 'd3-force',
                link: {
                    distance: () => {
                        return 100;
                    },
                    strength: () => {
                        return 0.2;
                    }
                },
                manyBody: {
                    strength: () => {
                        return -100;
                    }
                }
            },
            behaviors: [
                'zoom-canvas',
                'drag-canvas',
                settingValue.layout == 'force-g6'
                    ? data.nodes.length <= 200
                        ? {
                              type: 'drag-element-force',
                              animation: false
                          }
                        : data.nodes.length <= 5000
                          ? {
                                type: 'drag-element' // 普通拖拽，性能更好
                            }
                          : {}
                    : data.nodes.length <= 5000
                      ? {
                            type: 'drag-element' // 普通拖拽，性能更好
                        }
                      : {},
                {
                    type: 'hover-activate',
                    enable: (event) => event.targetType === 'node',
                    degree: 0, // 👈🏻 Activate relations.
                    state: 'highlight'
                },
                {
                    type: 'click-select',
                    key: 'clickControl',
                    state: 'click',
                    onClick: (d) => {
                        console.log(d, '===dd');
                        if (d.target.nodeName === 'document') {
                            nodeDialogRef.value.dialogClose();
                            currentNodeData.value = null;
                        }
                        if (d.target.type == 'node') {
                            currentNodeData.value = nodes.value.find((e) => e.id == d.target.id);
                            console.log(
                                '---------currentNodeData.value-------',
                                currentNodeData.value
                            );
                            let param = {
                                currNodeData: currentNodeData.value,
                                edgesData: edges.value,
                                taskInfo: { id: '', name: '图谱探索', pageType: '0' }
                            };
                            nodeDialogRef.value.dialogOpen(param);
                        }
                    }
                }
            ],
            ...option
        };
        console.log('---------data---------', data);
        console.log('---------options---------', options);
        relationGraph = new G6Network(timelineRef.value, data, options);
    };

    const initGragh = () => {
        let data = {
            nodes: nodes.value,
            edges: edges.value
        };
        relationGraph = new VisNetwork(timelineRef.value, data, {});
        relationGraph.moveTo({ scale: 1.1 });
        relationGraph.on('hoverNode', function (t) {
            let node = relationGraph.graphData.nodes.get(t.node);
            node.borderWidth = 15;
            node.size = 20;
            // 选中后的相邻节点hover颜色
            node.color.hover = {
                background: node.step != -1 ? node.rowBackground : '#89b8ff',
                border: node.step != -1 ? node.rowBorder : '#89b8ff4d'
            };
            relationGraph.graphData.nodes.update(node);
        });
        relationGraph.on('blurNode', function (t) {
            let node = relationGraph.graphData.nodes.get(t.node);
            // 普通节点无border
            node.borderWidth = node.step == 0 ? 0 : 15;
            // 选中节点不变
            node.size = node.step < 2 ? 10 : 20;
            relationGraph.graphData.nodes.update(node);
        });
        relationGraph.on('click', function (d) {
            console.log(d, data, relationGraph.graphData.nodes, '===dd');
            // 恢复颜色
            relationGraph.resetColor();
            if (d.nodes.length == 0) {
                nodeDialogRef.value.dialogClose();
                currentNodeData.value = null;
                // 恢复视角
                setTimeout(() => {
                    relationGraph.moveTo({ scale: 1.1 });
                }, 300);
            } else {
                currentNodeData.value = nodes.value.find((e) => e.id == d.nodes[0]);
                // 添加临时节点
                relationGraph.nodes('add', {
                    id: '-node',
                    color: 'rgba(255, 255, 255, 0)'
                });
                relationGraph.edges('add', {
                    id: '-edge',
                    from: currentNodeData.value.id,
                    to: '-node',
                    color: 'rgba(255, 255, 255, 0)'
                });
                setTimeout(() => {
                    // 移除临时节点
                    relationGraph.nodes('remove', { id: '-node' });
                    relationGraph.edges('remove', { id: '-edge' });
                }, 500);
                // 定位至中心
                setTimeout(() => {
                    relationGraph.zoomBy(currentNodeData.value);
                }, 300);
                /** 高亮临节点-暂时不要了
       // d.edges.forEach((e) => {
       //   let edge = edges.value.find((f) => f.id == e);
       //   console.log(edge, "===edge");
       //   if (edge) {
       //     let conNodes = nodes.value.filter((f) => f.id == edge.from || f.id == edge.to);
       //     if (conNodes.length > 0) {
       //       conNodes.forEach((item) => {
       //         graphData.value.nodes.update([
       //           {
       //             id: item.id,
       //             borderWidth: 15,
       //             step: -1,
       //           },
       //         ]);
       //       });
       //     }
       //   }
       // }); */
                relationGraph.nodes('update', [
                    {
                        id: d.nodes[0],
                        size: 20,
                        borderWidth: 15,
                        step: 2,
                        color: {
                            highlight: {
                                background: currentNodeData.value.rowBackground,
                                border: currentNodeData.value.rowBorder
                            }
                        }
                    }
                ]);
                console.log('---------currentNodeData.value-------', currentNodeData.value);
                let param = {
                    currNodeData: currentNodeData.value,
                    edgesData: edges.value,
                    taskInfo: { id: '', name: '图谱探索', pageType: '0' }
                };
                nodeDialogRef.value.dialogOpen(param);
            }
        });
    };

    async function getSchemaList() {
        // 获取schema&tool图例
        schemaList.value = await listSchema({ pageNum: 1, pageSize: 10000 });
        schemaList.value.data.rows.map((item) => {
            toolData.value.push({
                label: item.name,
                icon: item.color
            });
        });
    }
    // 左侧时间轴-点击实体
    const oldId = ref(null);
    function handleEntityClick(data) {
        let node = nodes.value.find((e) => e.id == data.entityId);
        if (!node) {
            proxy.$modal.msgWarning('该节点不存在！');
            return;
        }
        const filterEmit = (data) => {
            if (oldId.value) {
                relationGraph.resetColor();
            }
            oldId.value = data.id;
            // 修改颜色及定位
            relationGraph.nodes('update', [
                {
                    id: data.id,
                    color: {
                        background: '#0006ff',
                        border: 'rgba(0, 6, 255, 0.3)'
                    }
                }
            ]);
            relationGraph.zoomBy(data);
            // 弹框已展示，调整内容
            if (nodeDialogRef.value.show) {
                currentNodeData.value = nodes.value.find((e) => e.id == data.id);
                let param = {
                    currNodeData: currentNodeData.value,
                    edgesData: edges.value,
                    taskInfo: { id: '', name: '图谱探索', pageType: '0' }
                };
                nodeDialogRef.value.dialogOpen(param);
            }
        };
        filterEmit(node);
    }

    function handleEventClick(data) {
        console.log('🚀 ~ handleEventClick ~ data:', data);
        console.log('🚀 ~ nodes:', nodes.value);
        if (data == null) {
            getGraphData();
            return;
        }
        // 使用 map 方法提取 id 值
        const idsArray = data.map((item) => item.entityId);
        getDynamicEntityList({ entityIds: idsArray })
            .then((response) => {
                let data = response.data;
                graphData.value = data;
                // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
                nodes.value = data.entities.map((item) => {
                    let row = schemaList.value.data.rows.find((i) => item.schemaId == i.id);
                    return {
                        ...item,
                        id: item.id + '',
                        label: item.name.length > 6 ? item.name.substring(0, 6) + '...' : item.name,
                        rowBackground: row ? row.color : '#315790',
                        rowBorder: row ? `${row.color}4d` : '#7dbffa4d',
                        color: {
                            background: row ? row.color : '#315790',
                            border: row ? `${row.color}4d` : '#7dbffa4d'
                        },
                        borderWidth: row ? 15 : 0,
                        step: row ? 1 : 0
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
                // 搜索值
                searchData.value = nodes.value.map((item) => {
                    return { ...item, value: item.name, name: item.name };
                });
            })
            .finally(() => {
                // initGragh();
                initG6Gragh({ nodes: nodes.value, edges: edges.value });
                graghLoading.value = false;
            });
    }

    // 右侧弹框-关闭
    function nodeDialogClose() {
        currentNodeData.value = null;
        relationGraph.resetColor();
        // 恢复视角
        setTimeout(() => {
            relationGraph.moveTo({ scale: 1.1 });
        }, 300);
    }
    onMounted(() => {
        getList();
        loadData();
    });

    // 组件卸载时销毁 G6 实例
    onBeforeUnmount(() => {
        if (relationGraph) {
            relationGraph.destroy();
            relationGraph = null;
        }
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
        .tool {
            cursor: pointer;
            position: absolute;
            left: 365px;
            bottom: 10px;
            width: 30px;
            height: 30px;
            background: url('@/assets/ke/images/tool.png') no-repeat;
            background-size: 100% 100%;
            z-index: 1;
            transition: all 0.3s ease;

            .tool-mask {
                width: 151px;
                max-height: 214px;
                background: #1c376a;
                border-radius: 4px 4px 4px 4px;
                border: 1px solid #448fff;
                position: absolute;
                bottom: calc(100% + 8px);
                left: 0;
                box-sizing: border-box;
                padding: 20px;
                overflow: hidden auto;

                &::-webkit-scrollbar {
                    width: 0px;
                }

                .mask-item {
                    display: flex;
                    align-items: center;
                    margin-bottom: 12px;

                    .mask-icon {
                        width: 18px;
                        height: 18px;
                        border-radius: 50%;
                        margin-right: 8px;
                    }

                    .mask-label {
                        max-width: 100px;
                        line-height: 1;
                        font-family: PingFangSCs;
                        font-size: 13px;
                        color: #d7e3fa;
                    }
                }
            }
        }
    }
</style>
