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
      />
      <!-- 时间线 -->
      <div class="timeline-container" ref="timelineRef"></div>
      <!-- 右侧弹框 -->
      <node-dialog ref="nodeDialogRef" @dialog-close="nodeDialogClose" />
    </div>
  </div>
</template>

<script setup name="TimeLineView">
// 引入组件
import LineDialog from "../component/lineDialog.vue";
import NodeDialog from "../component/nodeDialog.vue";
// 综合接口
import { getGraph } from "@/api/app/graph";
import { listEvent } from "@/api/app/event/event";
import { TimeLine } from "@/utils/graph/visTimeLine";

const route = useRoute();
const { proxy } = getCurrentInstance();
const { app_event_type } = proxy.useDict("app_event_type");
const loading = ref(false);

// 搜索参数（qt-search-bar 要求 params 为 reactive 对象，控件直接 v-model 其属性）
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
  };
  if (searchParams.dateRange && searchParams.dateRange.length > 0) {
    param.occurBeginTime = searchParams.dateRange[0];
    param.occurEndTime = searchParams.dateRange[1];
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
            id: item.id + "",
            label:
              item.name.length > 6
                ? item.name.substring(0, 6) + "..."
                : item.name,
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
/** 重置按钮操作（qt-search-bar 已自动清空 params） */
function resetQuery() {
  handleQuery();
}
// 时间线参数
const timelineRef = ref();
const nodeDialogRef = ref();
const timelineStore = reactive({
  timelineGraph: null,
  nodes: [],
  edges: [],
  currentNodeData: null,
});
const { timelineGraph, nodes, edges, currentNodeData } = toRefs(timelineStore);
// 初始化时间线
function initTimeLine() {
  let data = {
    nodes: nodes.value,
    edges: edges.value,
  };
  let options = {};
  timelineGraph.value = new TimeLine(timelineRef.value, data, options);
  timelineEvent();
}
// 初始化时间线事件
function timelineEvent() {
  timelineGraph.value.on("click", function (e) {
    if (e.item) {
      currentNodeData.value = nodes.value.find(
        (item) => `node-${item.id}` == e.item
      );
      let param = {
        currNodeData: currentNodeData.value,
        edgesData: edges.value,
        taskInfo: { id: "", name: "图谱探索", pageType: "0" },
      };
      nodeDialogRef.value.dialogOpen(param);
    } else {
      currentNodeData.value = null;
      nodeDialogRef.value.dialogClose();
    }
  });
}
// 左侧时间轴-点击实体
function handleEntityClick() {
  timelineGraph.value.instance.setSelection(`node-${45}`);
  timelineGraph.value.instance.focus(`node-${45}`, {
    zoom: true,
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
}
</style>
