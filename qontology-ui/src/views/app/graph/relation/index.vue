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
  <div class="relation-view" v-loading="loading">
    <!-- 左侧弹框 -->
    <graph-setting
      ref="graphSettingRef"
      :setting="settingValue"
      @update-setting="handleUpdateSetting"
      @handle-tool-click="handleToolClick"
    />
    <!-- 图例 -->
    <div
      :class="[
        'tool',
        { 'is-collapsed': graphSettingRef?.graphSetting?.toggle },
      ]"
      @click="toolShow = !toolShow"
    >
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
    <!-- 视图容器 -->
    <div
      id="graph-container"
      class="relation-container"
      ref="relationRef"
    ></div>
    <!-- 右侧弹框 -->
    <node-dialog
      ref="nodeDialogRef"
      @dialog-close="nodeDialogClose"
      @handle-refresh="handleRefresh"
    />
  </div>
</template>
<script setup name="RelationView">
// 引入组件
import GraphSetting from "../component/graphSetting.vue";
import NodeDialog from "../component/nodeDialog.vue";
// 接口
import { getGraph } from "@/api/app/graph";
import { getUnstructTaskTextList } from "@/api/app/unstructTask";
// 引入graph方法
import { VisNetwork, DEFAULT_OPTIONS } from "@/utils/graph/visNetwork";
import { G6Network } from "@/utils/graph/g6Network";
import { pickColorByEntityId } from "@/utils/graph/entityColor";
const route = useRoute();
const nodeDialogRef = ref();

// #region视图容器方法
const relationRef = ref();
let relationGraph = null;
const relationData = reactive({
  loading: false,
  // schema&tool图例
  schemaList: { data: { rows: [] } },
  toolShow: false,
  toolData: [
    {
      label: "其他",
      icon: "var(--qt-graph-node-default)",
    },
  ],
  // 图谱参数-unselectAll方法异常
  // graph: null,
  nodes: [],
  edges: [],
  currentNodeData: null,
  // 搜索数据
  searchData: [],
});
const {
  schemaList,
  toolShow,
  toolData,
  loading,
  nodes,
  edges,
  currentNodeData,
  searchData,
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
    taskInfo: { id: "", name: "关系视图", pageType: "0" },
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
  loading.value = true;
  const params = { entityType: 0, graphId: route.params.kgId };
  getGraph(params)
    .then((response) => {
      let data = response.data;
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
          });
        }
      });
      // 搜索值
      searchData.value = nodes.value.map((item) => {
        return { ...item, value: item.name, name: item.name };
      });
    })
    .finally(() => {
      initGraph();
      loading.value = false;
    });
};
const initGraph = () => {
  if (relationGraph) {
    relationGraph.destroy();
    relationGraph = null;
  }
  nextTick(() => {
    if (settingValue.layout == "force") {
      initVisGraph();
    } else {
      initG6Graph();
    }
  });
};
const initVisGraph = () => {
  let data = {
    nodes: nodes.value.map((item) => {
      return {
        ...item,
        // vis
        id: item.id + "",
        label:
          item.name.length > 6 ? item.name.substring(0, 6) + "..." : item.name,
      };
    }),
    edges: edges.value.map((item) => {
      return {
        ...item,
        // vis
        id: item.id + "",
        label: item.relationType,
        from: item.startId + "",
        to: item.endId + "",
      };
    }),
  };
  let options = {
    nodes: {
      ...DEFAULT_OPTIONS.nodes,
      size: settingValue.entitySize,
    },
    edges: {
      ...DEFAULT_OPTIONS.edges,
      width: settingValue.lineWidth,
    },
  };

  relationGraph = new VisNetwork(relationRef.value, data, options);
  relationGraph.moveTo({ scale: 1.1 });
  relationGraph.on("hoverNode", function (t) {
    let node = relationGraph.graphData.nodes.get(t.node);
    const graphStyle = getComputedStyle(document.documentElement);
    node.borderWidth = 15;
    node.size = settingValue.entitySize + 10;
    // 选中后的相邻节点hover颜色
    node.color.hover = {
      background: node.step != -1 ? node.rowBackground : graphStyle.getPropertyValue("--qt-graph-node-default").trim(),
      border: node.step != -1 ? node.rowBorder : graphStyle.getPropertyValue("--qt-graph-node-default-border").trim(),
    };
    relationGraph.nodes("update", node);
  });
  relationGraph.on("blurNode", function (t) {
    let node = relationGraph.graphData.nodes.get(t.node);
    // 普通节点无border
    node.borderWidth = node.step == 0 ? 0 : 15;
    // 选中节点不变
    node.size = null;
    relationGraph.nodes("update", node);
  });
  relationGraph.on("click", function (d) {
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
      relationGraph.nodes("add", {
        id: "-node",
        color: "rgba(255, 255, 255, 0)",
      });
      relationGraph.edges("add", {
        id: "-edge",
        from: currentNodeData.value.id,
        to: "-node",
        color: "rgba(255, 255, 255, 0)",
      });
      setTimeout(() => {
        // 移除临时节点
        relationGraph.nodes("remove", { id: "-node" });
        relationGraph.edges("remove", { id: "-edge" });
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
      relationGraph.nodes("update", [
        {
          id: d.nodes[0],
          size: 20,
          borderWidth: 15,
          step: 2,
          color: {
            highlight: {
              background: currentNodeData.value.rowBackground,
              border: currentNodeData.value.rowBorder,
            },
          },
        },
      ]);
      openNodeDialog(currentNodeData.value);
    }
  });
};
const initG6Graph = (option) => {
  let data = {
    nodes: nodes.value.map((item) => {
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
    edges: edges.value.map((item) => {
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
  let layoutOption = {
    "force-g6": {
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
    circular: { type: "circular", nodeSize: 30, nodeSpacing: 20 },
    grid: { type: "grid" },
    force: { type: "force", preventOverlap: true },
    radial: {
      type: "radial",
      preventOverlap: true,
      nodeSize: 100,
      nodeSpacing: 300,
      unitRadius: 160,
      linkDistance: 220,
    },
    concentric: {
      type: "concentric",
      preventOverlap: true,
      // equidistant: true,
      nodeSize: 30,
      nodeSpacing: 10,
      sweep: 5,
      node: { style: { labelFontSize: 8 } },
    },
    mds: {
      type: "mds",
      nodeSize: 32,
      linkDistance: 200,
    },
  };
  let options = {
    layout: layoutOption[settingValue.layout],
    // node: {
    // 	...layoutOption[settingValue.layout].node,
    // },
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
            if (d.target.type == "node") {
              handleNodeClickId(d.target.id);
            }
          }
        },
      },
    ],
    ...option,
  };
  relationGraph = new G6Network("graph-container", data, options);
  relationGraph.triggerNodeClick = function (nodeId) {
    handleNodeClickId(nodeId);
  };
};

// 独立的点击处理函数
function handleNodeClickId(nodeId) {
  currentNodeData.value = nodes.value.find((e) => e.id == nodeId);
  relationGraph.zoomBy(currentNodeData.value);

  openNodeDialog(currentNodeData.value);
}

// #region左侧弹框方法
const graphSettingRef = ref({
  toggle: false,
});
const settingValue = reactive({
  layout: "force-g6",
  entitySize: 10,
  lineWidth: 1,
  tool: "",
  loadAnimation: "none",
  loadTime: "1000",
  effect: "circle",
});
const handleUpdateSetting = (type, newData) => {
  Object.assign(settingValue, newData);
  if (type == "layout") {
    initGraph();
  } else {
    if (newData.layout == "force") {
      visUpdate(type, newData);
    } else {
      g6Update(type, newData);
    }
  }
};
const visUpdate = (type, newData) => {
  const position = relationGraph.instance.getViewPosition();
  const zoom = relationGraph.instance.getScale();
  const animation = {
    duration: 500,
    easingFunction: "linear",
  };
  switch (type) {
    case "entitySize": {
      let option = {
        nodes: {
          size: newData.entitySize,
        },
      };
      relationGraph.updateLayout(option);
      break;
    }
    case "lineWidth": {
      let option = {
        edges: {
          width: newData.lineWidth,
        },
      };
      relationGraph.updateLayout(option);
      break;
    }
    case "toolUp": {
      relationGraph.moveTo({
        position: { x: position.x, y: position.y - 300 },
        animation,
      });
      break;
    }
    case "toolRight": {
      relationGraph.moveTo({
        position: { x: position.x + 300, y: position.y },
        animation,
      });
      break;
    }
    case "toolDown": {
      relationGraph.moveTo({
        position: { x: position.x, y: position.y + 300 },
        animation,
      });
      break;
    }
    case "toolLeft": {
      relationGraph.moveTo({
        position: { x: position.x - 300, y: position.y },
        animation,
      });
      break;
    }
    case "toolZoomIn": {
      relationGraph.moveTo({
        scale: zoom + 0.2,
        animation,
      });
      break;
    }
    case "toolZoomOut": {
      relationGraph.moveTo({
        scale: zoom - 0.2,
        animation,
      });
      break;
    }
    case "toolRotateRight": {
      relationGraph.moveTo({
        scale: zoom + 0.2,
        animation,
      });
      break;
    }
    case "toolRotateLeft": {
      relationGraph.moveTo({
        scale: zoom - 0.2,
        animation,
      });
      break;
    }
  }
};
const g6Update = async (type, newData) => {
  const animation = {
    duration: 500,
    easing: "linear",
  };
  switch (type) {
    case "entitySize": {
      const size = newData.entitySize * 2;
      relationGraph.instance.updateData({
        nodes: nodes.value.map((item) => {
          return {
            id: item.id + "",
            size,
          };
        }),
      });
      relationGraph.instance.draw();
      break;
    }
    case "lineWidth": {
      const lineWidth = newData.lineWidth;
      relationGraph.instance.updateData({
        edges: edges.value.map((item) => {
          return {
            id: "edge" + item.id,
            lineWidth,
          };
        }),
      });
      relationGraph.instance.draw();
      break;
    }
    case "toolUp": {
      relationGraph.instance.translateBy([0, -50], animation);
      break;
    }
    case "toolRight": {
      relationGraph.instance.translateBy([50, 0], animation);
      break;
    }
    case "toolDown": {
      relationGraph.instance.translateBy([0, 50], animation);
      break;
    }
    case "toolLeft": {
      relationGraph.instance.translateBy([-50, 0], animation);
      break;
    }
    case "toolZoomIn": {
      relationGraph.instance.zoomBy(1.2, animation);
      break;
    }
    case "toolZoomOut": {
      relationGraph.instance.zoomBy(0.8, animation);
      break;
    }
    case "toolRotateRight": {
      relationGraph.instance.rotateBy(-10, animation);
      break;
    }
    case "toolRotateLeft": {
      relationGraph.instance.rotateBy(10, animation);
      break;
    }
    case "loadAnimation":
    case "loadTime": {
      const duration = newData.loadTime;
      const easing = newData.loadAnimation;
      relationGraph.destroy();
      relationGraph = null;
      initG6Graph({
        animation: {
          duration,
          easing,
        },
      });
      await relationGraph.instance.render();
      break;
    }
    case "effect": {
      const type = newData.effect;
      relationGraph.instance.setOptions({
        node: {
          ...relationGraph.instance.options.node,
          type,
        },
      });
      relationGraph.instance.render();
      break;
    }
  }
};
const handleToolClick = (e) => {
  if (settingValue.layout == "force") {
    visUpdate(e);
  } else {
    g6Update(e);
  }
};
// #endregion
// #region 右侧弹框方法
const nodeDialogClose = () => {
  currentNodeData.value = null;
  textList.value = [];
  docList.value = [];
  nodeDialogRef.value?.setAssociations([], []);
  relationGraph.resetColor();
  relationGraph.instance.unselectAll();
  // 恢复视角
  setTimeout(() => {
    relationGraph.moveTo({ scale: 1.1 });
  }, 300);
};
// #endregion

// 组件卸载时销毁 G6 实例
onBeforeUnmount(() => {
  if (relationGraph) {
    relationGraph.destroy();
    relationGraph = null;
  }
});

onMounted(() => {
  loadData();
});
</script>
<style lang="scss" scoped>
.relation-view {
  margin-top: var(--qt-space-4);
  height: 70vh;
  position: relative;
  display: flex;
  overflow: hidden;
  .relation-container {
    flex: 1;
    background: var(--qt-surface-background) url("@/assets/ke/images/bg1.png")
      no-repeat;
    background-size: 100% 100%;
  }

  .tool {
    cursor: pointer;
    position: absolute;
    left: 10px;
    bottom: 10px;
    width: 30px;
    height: 30px;
    background: url("@/assets/ke/images/tool.png") no-repeat;
    background-size: 100% 100%;
    z-index: 1;
    transition: all 0.3s ease;

    &.is-collapsed {
      left: 365px;
    }

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
          font-size: var(--qt-font-size-small);
          color: var(--qt-graph-panel-text);
        }
      }
    }
  }
}
</style>
