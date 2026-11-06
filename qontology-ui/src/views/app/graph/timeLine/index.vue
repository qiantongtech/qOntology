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

 Business License: https://community.qontology.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->

<template>
  <div class="timeline-view" v-loading="loading">
    <div class="pagecont-top">
      <qt-search-bar
        :items="searchItems"
        :params="searchParams"
        @query="handleQuery"
        @reset="resetQuery"
      />
    </div>
    <div class="view-wrap">
      <!-- 左侧弹框 -->
      <line-dialog
        ref="lineDialogRef"
        :data="lineData"
        @handle-entity-click="handleEntityClick"
        @handle-event-click="handleEventClick"
        @reset-data="getList"
      />
      <!-- 图例 -->
      <div :class="['tool']" @click="toolShow = !toolShow">
        <div class="tool-mask" v-if="toolShow">
          <div class="mask-item" v-for="item in toolData" :key="item.label">
            <div
              class="mask-icon"
              :style="{ '--graph-legend-color': item.icon }"
            ></div>
            <div class="mask-label ellipsis" :title="item.label">
              {{ item.label }}
            </div>
          </div>
        </div>
      </div>
      <!-- 时间线 -->
      <div
        v-loading="graghLoading"
        class="timeline-container"
        ref="timelineRef"
      ></div>
      <!-- 右侧弹框 -->
      <node-dialog
        ref="nodeDialogRef"
        @dialog-close="nodeDialogClose"
        @handle-refresh="handleRefresh"
      />
    </div>
  </div>
</template>

<script setup name="TimeLineView">
// 引入组件
import LineDialog from "../component/lineDialog.vue";
import NodeDialog from "../component/nodeDialog.vue";
// 综合接口
import { getGraph, getDynamicEntityList } from "@/api/app/graph";
import { getUnstructTaskTextList } from "@/api/app/unstructTask";
import { listEvent } from "@/api/app/event/event";
// 引入gragh方法
import { G6Network } from "@/utils/graph/g6Network";
import { pickColorByEntityId } from "@/utils/graph/entityColor";

const route = useRoute();
const { proxy } = getCurrentInstance();
const { app_event_type } = proxy.useDict("app_event_type");
const loading = ref(false);

// 搜索参数（qt-search-bar 要求 params 为 reactive 对象）
const searchParams = reactive({
  type: "",
  dateRange: [],
});

const searchItems = computed(() => [
  {
    label: "事件类型",
    prop: "type",
    component: {
      is: "select",
      options: app_event_type.value.map((d) => ({
        label: d.label,
        value: d.value,
      })),
      placeholder: "请选择事件类型",
    },
  },
  {
    label: "发生时间",
    prop: "dateRange",
    component: {
      is: "date-picker",
      type: "daterange",
      rangeSeparator: "-",
      startPlaceholder: "开始日期",
      endPlaceholder: "结束日期",
    },
  },
]);

const data = reactive({
  form: {},
  lineData: [],
});
const { lineData } = toRefs(data);

/** 查询图谱模型管理列表 */
function getList() {
  loading.value = true;
  let param = {
    type: searchParams.type,
    graphId: route.params.kgId,
  };
  if (searchParams.dateRange && searchParams.dateRange.length > 0) {
    param.occurBeginTime = searchParams.dateRange[0];
    param.occurEndTime = searchParams.dateRange[1];
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
/** 重置按钮操作（qt-search-bar 内部已 resetFields 清空 params） */
function resetQuery() {
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
      label: "其他",
      icon: "#7DBFFA",
    },
  ],
  // 图谱参数-unselectAll方法异常
  // graph: null,
  nodes: [],
  edges: [],
  graphData: [],
  currentNodeData: null,
  // 搜索数据
  searchData: [],
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
  graphData,
} = toRefs(relationData);

// 关联分段和关联文件
const textList = ref([]);
const docList = ref([]);

/** 按实体所属任务查询关联分段和关联文件 */
function getTextListAndDocList(node) {
  textList.value = [];
  docList.value = [];
  nodeDialogRef.value?.setAssociations([], []);
  if (!node || node.entityType != 2 || !node.taskId) {
    return;
  }
  const requestTaskId = node.taskId;
  getUnstructTaskTextList({ taskId: requestTaskId }).then((response) => {
    // 请求返回时若已切换到其它实体则丢弃结果
    if (
      currentNodeData.value?.taskId &&
      String(currentNodeData.value.taskId) !== String(requestTaskId)
    ) {
      return;
    }
    if (response && response.code === 200) {
      textList.value = response.data?.textListByTaskId || [];
      docList.value = (response.data?.docListByTaskId || []).map(
        (document) => ({
          id: document.id,
          name: document.fileName,
          path: document.filePath,
          createTime: document.createTime,
        })
      );
      nodeDialogRef.value?.setAssociations(textList.value, docList.value);
    }
  });
}

/** 打开实体详情弹框，并传入关联分段和关联文件 */
function openNodeDialog(node) {
  getTextListAndDocList(node);
  nodeDialogRef.value.dialogOpen({
    currNodeData: node,
    edgesData: edges.value,
    taskInfo: { id: "", name: "时序视图", pageType: "0" },
    textList: textList.value,
    docList: docList.value,
  });
}

// 初始化加载数据
const loadData = async () => {
  // await getSchemaList();
  await getGraphData();
};

const handleRefresh = () => {
  toolbarClick({
    id: "reset",
  });
};
const toolbarClick = async (item) => {
  const animation = {
    duration: 500,
    easing: "linear",
  };

  switch (item.id) {
    case "full-screen": {
      toggle();
      isfull.value = !isfull.value;
      break;
    }
    case "zoom-in": {
      const zoom = relationGraph.instance.getZoom();
      relationGraph.instance.zoomTo(zoom + 0.2, animation);
      break;
    }
    case "zoom-out": {
      const zoom = relationGraph.instance.getZoom();
      relationGraph.instance.zoomTo(zoom - 0.2, animation);
      break;
    }
    case "offset-left": {
      relationGraph.instance.translateBy([-300, 0], {
        duration: 300,
        easing: "easeCubic",
      });
      break;
    }

    case "offset-right": {
      relationGraph.instance.translateBy([300, 0], {
        duration: 300,
        easing: "easeCubic",
      });
      break;
    }
    case "auto-fit": {
      // 自适应视图
      relationGraph.instance.fitView(); // 20px 的边距
      break;
    }
    case "export": {
      // 导出图片
      try {
        const dataURL = await relationGraph.instance.toDataURL("image/png");
        const link = document.createElement("a");
        link.href = dataURL;
        link.setAttribute("download", taskInfo.value.name + "抽取结果.png");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        console.error("导出失败:", error);
      }
      break;
    }
    case "reset": {
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
    case "search": {
      searchShow.value = !searchShow.value;
      break;
    }
  }
};
const getGraphData = async () => {
  graghLoading.value = true;
  const params = {
    entityType: 0,
    graphId: route.params.kgId,
  };
  getGraph(params)
    .then((response) => {
      let data = response.data;
      graphData.value = data;
      // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
      nodes.value = data.entities.map((item) => {
        const row = schemaList.value.data.rows.find(
          (i) => item.schemaId == i.id
        );
        // 优先用概念配色，无概念颜色时按实体 id 哈希取色，避免默认深蓝与背景同色
        const fill = row?.color || pickColorByEntityId(item.id);
        return {
          ...item,
          id: item.id + "",
          label:
            item.name.length > 6
              ? item.name.substring(0, 6) + "..."
              : item.name,
          rowBackground: fill,
          rowBorder: `${fill}4d`,
          color: {
            background: fill,
            border: `${fill}4d`,
          },
          borderWidth: row ? 15 : 0,
          step: row ? 1 : 0,
          cluster: item.id + "",
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
            id: item.id + "",
            name: item.relationType,
            label: item.relationType, //关系
            from: item.startId + "", //头部实体
            to: item.endId + "", //尾部实体
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
  layout: "force-g6",
  entitySize: 10,
  lineWidth: 1,
  tool: "",
  loadAnimation: "none",
  loadTime: "1000",
  effect: "circle",
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
        id: item.id + "",
        size: settingValue.entitySize * 2,
        halo: true,
        style: {
          fill: item.rowBackground,
          haloLineWidth: item.borderWidth,
        },
      };
    }),
    edges: option.edges.map((item) => {
      return {
        ...item,
        lineWidth: settingValue.lineWidth || 1,
        id: "edge" + item.id,
        name: item.relationType, //关系
        source: item.startId + "", //头部实体
        target: item.endId + "", //尾部实体
      };
    }),
  };
  let options = {
    layout: {
      type: "d3-force",
      link: {
        distance: () => {
          return 100;
        },
        strength: () => {
          return 0.2;
        },
      },
      manyBody: {
        strength: () => {
          return -100;
        },
      },
    },
    behaviors: [
      "zoom-canvas",
      "drag-canvas",
      settingValue.layout == "force-g6"
        ? data.nodes.length <= 200
          ? {
              type: "drag-element-force",
              animation: false,
            }
          : data.nodes.length <= 5000
          ? {
              type: "drag-element", // 普通拖拽，性能更好
            }
          : {}
        : data.nodes.length <= 5000
        ? {
            type: "drag-element", // 普通拖拽，性能更好
          }
        : {},
      {
        type: "hover-activate",
        enable: (event) => event.targetType === "node",
        degree: 0, // 👈🏻 Activate relations.
        state: "highlight",
      },
      {
        type: "click-select",
        key: "clickControl",
        state: "click",
        onClick: (d) => {
          if (d.target.nodeName === "document") {
            nodeDialogRef.value.dialogClose();
            currentNodeData.value = null;
          }
          if (d.target.type == "node") {
            currentNodeData.value = nodes.value.find(
              (e) => e.id == d.target.id
            );
            openNodeDialog(currentNodeData.value);
          }
        },
      },
    ],
    ...option,
  };
  relationGraph = new G6Network(timelineRef.value, data, options);
};

// 左侧时间轴-点击实体
const oldId = ref(null);
function handleEntityClick(data) {
  let node = nodes.value.find((e) => e.id == data.entityId);
  if (!node) {
    proxy.$modal.msgWarning("该节点不存在！");
    return;
  }
  const filterEmit = (node) => {
    if (oldId.value) {
      relationGraph.resetColor();
    }
    oldId.value = node.id;
    // 高亮及定位：vis 直接改样式，G6 走元素状态
    if (typeof relationGraph.nodes === "function") {
      relationGraph.nodes("update", [
        {
          id: node.id,
          color: {
            background: "#0006ff",
            border: "rgba(0, 6, 255, 0.3)",
          },
        },
      ]);
    } else {
      relationGraph.setHighlight(node.id);
    }
    relationGraph.zoomBy(node);
    // 弹框已展示，调整内容
    if (nodeDialogRef.value.show) {
      currentNodeData.value = nodes.value.find((e) => e.id == node.id);
      openNodeDialog(currentNodeData.value);
    }
  };
  filterEmit(node);
}

function handleEventClick(data) {
  // 未勾选任何事件：恢复全量图谱
  if (data == null) {
    getGraphData();
    return;
  }
  // 事件参与实体id（去重）
  const idsArray = [
    ...new Set(
      data
        .map((item) => String(item.entityId ?? item.id))
        .filter((id) => id && id !== "undefined" && id !== "null")
    ),
  ];
  if (idsArray.length === 0) {
    proxy.$modal.msgWarning("该事件没有参与实体！");
    return;
  }
  graghLoading.value = true;
  getDynamicEntityList({ entityIds: idsArray })
    .then((response) => {
      const res = response && response.data;
      if (!res) {
        return;
      }
      const idSet = new Set(idsArray);
      // 只保留当前事件参与的实体，避免无关实体混入
      const entityMap = new Map();
      (res.entities || []).forEach((item) => {
        if (idSet.has(String(item.id))) {
          entityMap.set(String(item.id), item);
        }
      });
      const entityList = [...entityMap.values()];
      graphData.value = { ...res, entities: entityList };
      // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
      nodes.value = entityList.map((item) => {
        const row = schemaList.value.data.rows.find(
          (i) => item.schemaId == i.id
        );
        // 优先用概念配色，无概念颜色时按实体 id 哈希取色，避免默认深蓝与背景同色
        const fill = row?.color || pickColorByEntityId(item.id);
        const name = item.name || "";
        return {
          ...item,
          id: item.id + "",
          label: name.length > 6 ? name.substring(0, 6) + "..." : name,
          rowBackground: fill,
          rowBorder: `${fill}4d`,
          color: {
            background: fill,
            border: `${fill}4d`,
          },
          borderWidth: row ? 15 : 0,
          step: row ? 1 : 0,
          cluster: item.id + "",
        };
      });
      // 只保留两端都在参与实体范围内的关系
      const nodeIdSet = new Set(entityList.map((item) => String(item.id)));
      const edgeMap = new Map();
      (res.relationships || []).forEach((item) => {
        if (
          nodeIdSet.has(String(item.startId)) &&
          nodeIdSet.has(String(item.endId)) &&
          !edgeMap.has(String(item.id))
        ) {
          edgeMap.set(String(item.id), {
            ...item,
            id: item.id + "",
            name: item.relationType,
            label: item.relationType, //关系
            from: item.startId + "", //头部实体
            to: item.endId + "", //尾部实体
          });
        }
      });
      edges.value = [...edgeMap.values()];
      // 搜索值
      searchData.value = nodes.value.map((item) => {
        return { ...item, value: item.name, name: item.name };
      });
      initG6Gragh({ nodes: nodes.value, edges: edges.value });
    })
    .catch(() => {
      proxy.$modal.msgWarning("筛选事件参与实体失败！");
    })
    .finally(() => {
      graghLoading.value = false;
    });
}

// 右侧弹框-关闭
function nodeDialogClose() {
  currentNodeData.value = null;
  oldId.value = null;
  textList.value = [];
  docList.value = [];
  nodeDialogRef.value?.setAssociations([], []);
  relationGraph.resetColor();
  // 恢复视角：vis 用 moveTo，G6 用 fitView
  setTimeout(() => {
    if (typeof relationGraph.moveTo === "function") {
      relationGraph.moveTo({ scale: 1.1 });
    } else {
      relationGraph.fitView();
    }
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
.timeline-view {
  height: auto;
  .view-wrap {
    display: flex;
    height: 64vh;
    position: relative;
    :deep(.timeline-container) {
      flex: 1;
      height: 100%;
      background: var(--qt-surface-background) url("@/assets/ke/images/bg1.png")
        no-repeat;
      background-size: 100% 100%;
      .vis-time-axis .vis-text {
        color: var(--qt-text-inverse);
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
    background: url("@/assets/ke/images/tool.png") no-repeat;
    background-size: 100% 100%;
    z-index: 1;
    transition: all 0.3s ease;

    .tool-mask {
      width: 151px;
      max-height: 214px;
      background: var(--qt-graph-panel-background);
      border-radius: 4px 4px 4px 4px;
      border: 1px solid var(--qt-graph-panel-border);
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
          background: var(--graph-legend-color);
          border: 2px solid var(--qt-overlay-border);
        }

        .mask-label {
          max-width: 100px;
          line-height: 1;
          font-family: var(--qt-font-family);
          font-size: 13px;
          color: var(--qt-graph-panel-text);
        }
      }
    }
  }
}
</style>
