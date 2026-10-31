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
  <div class="graphView" v-loading="appLoading">
    <div class="head-title" v-if="!componentProps.embedded">
      <div class="name">{{ taskInfo.name }}</div>
    </div>
    <el-container class="wrap-container">
      <div :class="['gragh-wrap', { isfull: isfull }]">
        <div class="control-tree">
          <el-tree
            node-key="id"
            :props="props"
            show-checkbox
            highlight-current
            @check="handleCheck"
            @node-click="handleNodeClick"
            ref="treeRef"
            :data="treeData"
            :default-checked-keys="checkedKeys"
            default-expand-all
            :filter-node-method="filterNode"
          >
            <template #default="{ node }">
              <span class="ellipsis" :title="`${node.label}`">{{
                node.label
              }}</span>
            </template>
          </el-tree>
        </div>
        <div class="toolbar" ref="toolbarRef">
          <template v-for="item in toolbar" :key="item.id">
            <el-tooltip
              class="box-item"
              effect="light"
              :content="item.tip"
              placement="bottom"
            >
              <div class="toolbar-item" @click="toolbarClick(item)">
                <img :src="getAssetsFile(item.icon)" alt="" />
              </div>
            </el-tooltip>
          </template>
          <div class="search" v-if="searchShow">
            <el-autocomplete
              ref="autocompleteRef"
              v-model="searchVal"
              style="width: 240px"
              :fetch-suggestions="querySearchAsync"
              placeholder="请输入实体名称或关系信息"
              @select="searchSelect"
              clearable
            >
              <template #suffix>
                <el-icon class="el-input__icon">
                  <Search />
                </el-icon>
              </template>
              <template #default="{ item }">
                <template v-if="(item.fullName || item.name).length > 16">
                  <el-tooltip
                    :content="item.fullName || item.name"
                    placement="right"
                  >
                    {{ item.name }}
                  </el-tooltip>
                </template>
                <template v-else>
                  {{ item.name }}
                </template>
              </template>
            </el-autocomplete>
          </div>
        </div>
        <!--  ----TODO 图例 --------------  -->
        <!--                <div class="tool" @click="toolShow = !toolShow">-->
        <!--                    <div class="tool-mask" v-if="toolShow">-->
        <!--                        <div class="mask-item" v-for="item in toolData" :key="item.label">-->
        <!--                            <div-->
        <!--                                class="mask-icon"-->
        <!--                                :style="{-->
        <!--                                    background: item.icon,-->
        <!--                                    border: '2px solid ' + 'rgba(0,0,0, 0.3)'-->
        <!--                                }"-->
        <!--                            ></div>-->
        <!--                            <div class="mask-label ellipsis" :title="item.label">-->
        <!--                                {{ item.label }}-->
        <!--                            </div>-->
        <!--                        </div>-->
        <!--                    </div>-->
        <!--                </div>-->
        <!-- 视图容器 -->
        <div
          :class="['gragh-container', { detailShow: detailShow }]"
          id="gragh-container"
        ></div>
        <transition name="el-zoom-in-right">
          <div class="details-dialog" v-if="detailShow">
            <div class="details-title">
              <div class="title-label">
                <el-icon class="icon" @click="detailClose">
                  <Close />
                </el-icon>
                <span
                  @dblclick="!releaseStatus && (isEditName = true)"
                  class="label ellipsis"
                  :title="currentNodeData.name"
                  v-if="!isEditName"
                  >{{
                    currentNodeData.name + "&nbsp;(" + currentNodeData.id + ")"
                  }}</span
                >
                <el-input
                  v-model="currentNodeData.name"
                  autofocus="true"
                  placeholder="请输入实体名称"
                  clearable
                  v-else
                  @blur="updateName"
                />
              </div>
              <div class="title-slot">
                <el-button
                  :disabled="releaseStatus"
                  size="small"
                  type="danger"
                  icon="Delete"
                  @click="handleDel"
                  >删除
                </el-button>
              </div>
            </div>
            <div class="details-con">
              <el-collapse v-model="collapseAct" @change="collapseChange">
                <el-collapse-item title="属性信息" name="1">
                  <template #title>
                    <div class="collapse-title">属性信息</div>
                  </template>
                  <div class="collapse-con">
                    <el-table
                      stripe
                      height="150px"
                      v-loading="attrLoading"
                      :data="attrData"
                    >
                      <el-table-column
                        label="属性名称"
                        align="center"
                        prop="name"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      />
                      <el-table-column
                        label="数据类型"
                        prop="dataType"
                        align="center"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      >
                        <template #default="scope">
                          <dict-tag
                            :options="ext_data_type"
                            :value="scope.row.dataType"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column
                        label="属性值"
                        prop="dataValue"
                        align="center"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      >
                        <template #default="scope">
                          {{ scope.row.dataValue ? scope.row.dataValue : "-" }}
                        </template>
                      </el-table-column>
                      <el-table-column
                        label="操作"
                        class-name="small-padding fixed-width"
                        fixed="right"
                        align="center"
                        width="120"
                      >
                        <template #default="scope">
                          <el-button
                            :disabled="releaseStatus"
                            link
                            type="primary"
                            icon="Edit"
                            @click="attrUpdate(scope.row)"
                            >修改
                          </el-button>
                          <el-button
                            :disabled="releaseStatus"
                            link
                            type="danger"
                            icon="Delete"
                            @click="attrDelete(scope.row)"
                            >删除
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </el-collapse-item>
                <el-collapse-item title="关联三元组" name="2">
                  <template #title>
                    <div class="collapse-title">关联三元组</div>
                  </template>
                  <div class="collapse-con">
                    <el-table
                      stripe
                      height="200px"
                      v-loading="tripletLoading"
                      :data="tripletData"
                    >
                      <el-table-column
                        label="起点"
                        key="startName"
                        prop="startName"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      />
                      <el-table-column
                        label="关系"
                        key="name"
                        prop="name"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      >
                        <template #default="scope">
                          <span
                            v-if="isRelationshipEdit !== scope.$index"
                            @dblclick="
                              !releaseStatus &&
                                (isRelationshipEdit = scope.$index)
                            "
                            >{{ scope.row.name }}</span
                          >
                          <el-input
                            v-model="scope.row.name"
                            autofocus="true"
                            placeholder="请输入关系名称"
                            clearable
                            v-else
                            @blur="updateRelationship"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column
                        label="终点"
                        key="endName"
                        prop="endName"
                        :show-overflow-tooltip="{ effect: 'light' }"
                      />
                      <el-table-column
                        label="操作"
                        class-name="small-padding fixed-width"
                        fixed="right"
                      >
                        <template #default="scope">
                          <el-button
                            :disabled="releaseStatus"
                            link
                            type="danger"
                            icon="Delete"
                            @click="tripletDelete(scope.row)"
                            >删除
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </el-collapse-item>

                <el-collapse-item title="标签选择" name="3">
                  <template #title>
                    <div class="collapse-title">标签选择</div>
                  </template>
                  <div>
                    <!--                                        <el-tag-->
                    <!--                                            v-for="tag in tagList"-->
                    <!--                                            :key="tag.id"-->
                    <!--                                            :disable-transitions="false"-->
                    <!--                                            closable-->
                    <!--                                            @close="handleClose(tag)"-->
                    <!--                                            :style="{ margin: '5px 5px 0px 0px' }"-->
                    <!--                                        >-->
                    <!--                                            {{ tag.name }}-->
                    <!--                                        </el-tag>-->
                    <el-button
                      size="small"
                      @click="openTagMultiple"
                      :style="{ 'margin-top': '5px' }"
                    >
                      + 绑定标签
                    </el-button>
                  </div>
                </el-collapse-item>
                <template v-if="currentNodeData.entityType == 1">
                  <el-collapse-item title="关联数据源" name="4">
                    <template #title>
                      <div class="collapse-title">关联数据源</div>
                    </template>
                    <div class="collapse-con1">
                      <div
                        style="
                          display: grid;
                          grid-template-columns: 1fr 1fr;
                          margin-bottom: 8px;
                        "
                      >
                        <div style="text-align: left">
                          数据库名称:
                          {{ dataSource.database.type }}
                        </div>
                        <div style="text-align: left">
                          数据库地址: {{ dataSource.database.host }}
                        </div>
                      </div>
                      <div
                        style="
                          display: grid;
                          grid-template-columns: 1fr 1fr;
                          margin-bottom: 8px;
                        "
                      >
                        <div style="text-align: left">
                          数据库名称:
                          {{ dataSource.database.databaseName }}
                        </div>
                        <div style="text-align: left">
                          表名称: {{ dataSource.database.tableName }}
                        </div>
                      </div>
                      <el-table
                        height="200px"
                        v-loading="dataSourceLoading"
                        :data="dataSource.tableData"
                      >
                        <el-table-column
                          label="表字段"
                          align="center"
                          prop="field"
                          :show-overflow-tooltip="{ effect: 'light' }"
                        />
                        <el-table-column
                          label="数据"
                          align="center"
                          prop="value"
                        >
                          <template #default="scope">
                            {{ scope.row.value ? scope.row.value : "-" }}
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-collapse-item>
                </template>
                <template v-if="currentNodeData.entityType == 2">
                  <el-collapse-item title="关联分段" name="4">
                    <template #title>
                      <div class="collapse-title">关联分段</div>
                    </template>
                    <div class="collapse-con1">
                      <div v-for="(item, index) in textList" :key="index">
                        <span
                          v-if="
                            currentNodeData.textIds &&
                            currentNodeData.textIds
                              .split(',')
                              .includes(item.id.toString())
                          "
                          >{{ item.text }}</span
                        >
                        <!--                        <span v-if="currentNodeData.paragraphIndex && currentNodeData.paragraphIndex === item.paragraphIndex">{{ item.text }}</span>-->
                      </div>
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="关联文件" name="5">
                    <template #title>
                      <div class="collapse-title">关联文件</div>
                    </template>
                    <div class="collapse-con1">
                      <div v-for="(doc, index) in docList" :key="index">
                        <div
                          v-if="currentNodeData.docId == doc.id"
                          class="docItem"
                        >
                          <td
                            class="docTd"
                            :title="doc.name"
                            @click="previewRefactoring(doc)"
                            v-track="{
                              type: 'preview',
                              documentId: doc.id,
                              module: 'kg',
                            }"
                          >
                            {{ doc.name }}
                          </td>
                          <td style="text-align: right">
                            {{ parseTime(doc.createTime, "{y}-{m}-{d}") }}
                          </td>
                        </div>
                      </div>
                    </div>
                  </el-collapse-item>
                </template>
              </el-collapse>
            </div>
          </div>
        </transition>
        <!-- 属性信息修改 -->
        <el-dialog
          class="attr-dialog"
          title="属性信息"
          v-model="attrVisible"
          width="500px"
          :append-to="$refs['app-container']"
          draggable
          destroy-on-close
        >
          <el-form ref="attrRef" :model="attrForm" label-width="80px">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item
                  label="属性值"
                  prop="name"
                  :rules="{ required: true }"
                  v-if="attrForm.dictTypeId != null"
                >
                  <el-select
                    v-model="attrForm.dataValue"
                    class="el-form-input-width"
                  >
                    <el-option
                      v-for="item in dictDataOption"
                      :key="item.dictLabel"
                      :label="item.dictLabel"
                      :value="item.dictLabel"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="属性值"
                  prop="name"
                  :rules="{ required: true }"
                  v-else
                >
                  <el-input
                    v-model="attrForm.dataValue"
                    placeholder="请输入属性值"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="attrFormCancel">取 消</el-button>
              <el-button type="primary" @click="attrFormSubmit"
                >确 定
              </el-button>
            </div>
          </template>
        </el-dialog>
        <!--  弹框  -->
        <AddEntity
          ref="nodeRef"
          :title="addTitle"
          :graphId="taskInfo"
          @handle-refresh="handleRefresh"
        ></AddEntity>
        <AddRelationship
          ref="edgeRef"
          :title="addTitle"
          :graphId="taskInfo"
          @handle-refresh="handleRefresh"
        ></AddRelationship>
        <!--                <TagMultiple ref="tagMultipleRef" @confirm="handleTagSingleConfirm" />-->

        <!-- 右侧弹框 -->
        <NodeDialog
          ref="nodeDialogRef"
          @dialogClose="nodeDialogClose"
          @handleRefresh="handleRefresh"
        />
      </div>
    </el-container>
  </div>
</template>

<script setup name="KEresult">
import AddEntity from "@/views/app/graph/graphExploration/addEntity.vue";
import AddRelationship from "@/views/app/graph/graphExploration/addRelationship.vue";
import "vis-network/dist/dist/vis-network.min.css";
// 智能体构建平台
import { useFullscreen } from "@vueuse/core";
// 非结构化接口
// import {
//     getTaskTextList,
//     updateUnStructTaskPublishStatus
// } from '@/api/ext/extUnstructTask/unstructTask';
// 结构化接口
// import {
//     getAttributeInformation,
//     updateNodeAttribute,
//     updateStructTaskPublishStatus
// } from '@/api/ext/extStructTask/extStruct';
// import { getTableDataByDataId } from '@/api/ext/extDatasource/datasource';
// 综合接口
import {
  getGraph,
  deleteNodeAttributeById,
  deleteRelationshipById,
  deleteNode,
  addNode,
  addTripletRel,
} from "@/api/app/graph";
import { getUnstructTaskTextList } from "@/api/app/unstructTask";
// 概念列表
// import { listSchema } from '@/api/ext/extSchema/schema';
// import { entity, updateEntityTags, deleteEntity } from '@/api/kg/manage/tagEntity.js';
import { watch } from "vue";
// import TagMultiple from '@/views/kg/manage/tag/selection/tagMultiple.vue';
// import { listType, getType, delType, addType, updateType } from '@/api/kg/dict/type';
// import { listData, getData, delData, addData, updateData } from '@/api/kg/dict/data';
import { filePreview } from "@/utils/kkFileView.js";

import NodeDialog from "../component/nodeDialog.vue";
import { G6Network } from "@/utils/graph/g6Network";

const getAssetsFile = (url) => {
  return new URL(`/src/assets/ke/images/${url}`, import.meta.url).href;
};

// 界面路由入参
const { proxy } = getCurrentInstance();
const route = useRoute();

const componentProps = defineProps({
  embedded: { type: Boolean, default: false },
  taskInfoSource: { type: Object, default: () => ({}) },
  graphDataSource: {
    type: Object,
    default: () => ({ entities: [], relationships: [] }),
  },
  releaseStatusSource: { type: Boolean, default: false },
  textListSource: { type: Array, default: () => [] },
  docListSource: { type: Array, default: () => [] },
});
const emit = defineEmits(["refresh"]);

const { basic_data_type } = proxy.useDict("basic_data_type");
const router = useRouter();
// 1结构化 2非结构化 3故障 0探索,
const taskInfo = ref({ id: "", name: "常规视图", pageType: "0" });
const selectedIds = ref([]);
// 实体标签列表
const tagList = ref([]);

const dictTypeList = ref([]);
const dictDataList = ref([]);
const dictDataOption = ref([]);

let relationGraph = null;
const nodeDialogRef = ref();

watch(
  () => router.currentRoute.value.query.pageType,
  (val) => {
    if (val && !componentProps.embedded) {
      taskInfo.value = router.currentRoute.value.query;
      taskInfo.value.id = taskInfo.value.id - 0;
    }
  },
  { immediate: true }
);

watch(
  () => componentProps.taskInfoSource,
  (val) => {
    if (componentProps.embedded) {
      taskInfo.value = { ...val };
    }
  },
  { immediate: true, deep: true }
);

/** 查询字典类型列表 */
// function getDictTypeList() {
//     const params = {
//         graphId: route.params.kgId,
//         status: '0'
//     };
//     listType(params).then((response) => {
//         dictTypeList.value = response.data.rows;
//     });
// }

/** 查询字典管理列表 */
// function getDictDataList() {
//     const params = {
//         graphId: route.params.kgId
//     };
//     listData(params).then((response) => {
//         dictDataList.value = response.data.rows;
//     });
// }

// #region 点击事件
const releaseStatus = ref(false);
watch(
  () => componentProps.releaseStatusSource,
  (val) => {
    if (componentProps.embedded) {
      releaseStatus.value = val;
    }
  },
  { immediate: true }
);
const isEditName = ref(false);
const isRelationshipEdit = ref(-1);
const updateName = () => {
  appLoading.value = true;
  isEditName.value = false;
  addNode([
    {
      id: currentNodeData.value.id,
      name: currentNodeData.value.name,
      entityType: currentNodeData.value.entityType,
    },
  ]).then((res) => {
    appLoading.value = false;
    if (res.code === 200) {
      proxy.$modal.msgSuccess("修改成功");
      //查询抽取结果
      let params = {
        entityType: taskInfo.value.pageType,
        entityId: taskInfo.value.id,
      };
      getGraphData(params);
    }
  });
};

const updateRelationship = () => {
  appLoading.value = true;
  isRelationshipEdit.value = -1;
  const data = tripletData.value.map((item) => {
    return {
      entityId1: item.startId,
      entityName1: item.startName,
      relationshipType: item.name,
      entityId2: item.endId,
      entityName2: item.endName,
      id: item.id,
    };
  });
  addTripletRel(data).then((res) => {
    appLoading.value = false;
    if (res.code === 200) {
      proxy.$modal.msgSuccess("修改成功");
      //查询抽取结果
      let params = {
        entityType: taskInfo.value.pageType,
        entityId: taskInfo.value.id,
      };
      getGraphData(params);
    }
  });
};

// 编辑实体
const nodeRef = ref();
const edgeRef = ref();
const addTitle = ref("");
// const handleAddEntity = () => {
//     nodeRef.value.openDialog(nodes.value);
//     addTitle.value = '编辑实体';
// };
// 编辑三元组
// const handleAddRelationship = () => {
//     edgeRef.value.openDialog(edges.value);
//     addTitle.value = '编辑三元组';
// };
const handleRefresh = () => {
  toolbarClick({
    id: "reset",
  });
};
// #endregion

// #region 初始化抽取
const appLoading = ref(false);
let graph = null;
let graphData = ref({
  nodes: [],
  edges: [],
});
const nodes = ref([]);
const edges = ref([]);
const schemaList = ref({ data: { rows: [] } });

/**
 * 节点配色：按 entity.id 哈希分配颜色，每个实体独立取色，
 * 同一实体跨渲染保持同一颜色。15 色循环，深蓝/深绿/红橙等与深蓝背景高对比。
 */
const ENTITY_COLOR_PALETTE = [
  "#3b68c2", // 深蓝
  "#289470", // 深青绿
  "#b58200", // 暗金
  "#b93c32", // 暗红（你图里故障红，优先这个）
  "#2a8cb8", // 暗湖蓝
  "#6b4296", // 暗紫
  "#c46518", // 暗橙
  "#0e706e", // 墨青
  "#a83f70", // 暗玫粉
  "#4068a8", // 灰调蓝
  "#358252", // 灰调绿
  "#b16c27", // 土橙
  "#7257a0", // 灰紫
  "#2d78b5", // 钢蓝
  "#378a46", // 墨绿
];

/** 用 entity 的 id（数字或字符串）哈希到调色板下标。 */
function pickColorByEntityId(entityId) {
  const palette = ENTITY_COLOR_PALETTE;
  // id 可能是字符串数字、纯数字或带前缀，统一取字符码累加
  let hash = 0;
  const s = String(entityId || "");
  for (let i = 0; i < s.length; i++) {
    hash = (hash * 31 + s.charCodeAt(i)) | 0;
  }
  const idx = Math.abs(hash) % palette.length;
  return palette[idx];
}

/** 查询图谱数据；作为抽取结果子组件时通知父页面统一查询。 */
function getGraphData(params) {
  if (componentProps.embedded) {
    emit("refresh");
    return;
  }
  appLoading.value = true;
  params.graphId = route.params.kgId;
  getGraph(params)
    .then((response) => renderGraphData(response.data))
    .finally(() => {
      appLoading.value = false;
    });
}

/** 将父页面或接口传入的图谱数据渲染到画布。 */
function renderGraphData(sourceData) {
  if (relationGraph) {
    relationGraph.destroy();
    relationGraph = null;
  }
  const data = {
    entities: sourceData?.entities || [],
    relationships: sourceData?.relationships || [],
  };

  // step：普通：0，特殊颜色：1，选中：2，相邻节点：-1
  nodes.value = data.entities.map((item) => {
    const entityName = item.name || "";
    const fill = pickColorByEntityId(item.id);
    return {
      ...item,
      id: item.id + "",
      name: entityName,
      label:
        entityName.length > 6 ? entityName.substring(0, 6) + "..." : entityName,
      rowBackground: fill,
      rowBorder: fill + "4d",
      color: {
        background: fill,
        border: fill + "4d",
      },
      borderWidth: 0,
      step: 0,
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
  graphData.value.nodes = nodes.value;
  graphData.value.edges = edges.value;
  // 搜索值 - 展示三元组信息和单个实体
  const maxLength = 16; // 设置最大显示长度
  const tripletData = edges.value
    .map((item) => {
      // 找到关系对应的起始实体和结束实体
      const startNode = nodes.value.find((node) => node.id == item.startId);
      const endNode = nodes.value.find((node) => node.id == item.endId);

      if (startNode && endNode) {
        // 构建三元组展示文本
        const tripletText = `${startNode.name} ${item.name} ${endNode.name}`;
        // 截断文本
        const displayText =
          tripletText.length > maxLength
            ? tripletText.substring(0, maxLength) + "..."
            : tripletText;
        return {
          ...item,
          value: tripletText, // 用于搜索匹配（完整文本）
          name: displayText, // 用于下拉框显示（截断文本）
          fullName: tripletText, // 保存完整文本
          type: "triplet",
          // 保存起始实体信息，用于点击时定位
          startNodeId: startNode.id,
          startNodeName: startNode.name,
          // 为三元组添加唯一标识
          id: "triplet_" + item.id,
        };
      }
      return null;
    })
    .filter(Boolean); // 过滤掉无效的三元组

  // 找到所有有三元组关系的实体ID
  const entityIdsWithTriplet = new Set();
  edges.value.forEach((item) => {
    entityIdsWithTriplet.add(item.startId);
    entityIdsWithTriplet.add(item.endId);
  });

  // 构建单个实体的搜索数据
  const singleEntityData = nodes.value
    .filter((node) => {
      // 只包含没有三元组关系的实体
      return !entityIdsWithTriplet.has(node.id);
    })
    .map((node) => {
      // 截断文本
      const displayText =
        node.name.length > maxLength
          ? node.name.substring(0, maxLength) + "..."
          : node.name;
      return {
        ...node,
        value: node.name, // 用于搜索匹配（完整文本）
        name: displayText, // 用于下拉框显示（截断文本）
        fullName: node.name, // 保存完整文本
        type: "entity",
        // 为实体添加唯一标识
        id: "entity_" + node.id,
      };
    });

  // 合并三元组数据和单个实体数据
  searchData.value = [...tripletData, ...singleEntityData];
  console.log("-----数据----", graphData.value);
  // 设置发布状态：已发布不能修改和不能删除，除了图谱探索页
  if (!componentProps.embedded && taskInfo.value.pageType == "0") {
    releaseStatus.value = false;
  } else if (!componentProps.embedded) {
    releaseStatus.value =
      nodes.value.length && nodes.value[0].releaseStatus === 1 ? true : false;
  }
  //设置画布
  // setGraph(graphData.value);
  initG6Gragh(graphData.value);
  if (nodes.value.length !== 0) {
    // 初始化树结构
    initTree();
  }
}

function initG6Gragh(option) {
  let data = {
    nodes: option.nodes.map((item) => {
      return {
        ...item,
        id: item.id + "",
        size: 20,
        halo: true,
        style: {
          fill: item.rowBackground,
          haloLineWidth: item.borderWidth,
        },
        states: [], // 初始状态为空
      };
    }),
    edges: option.edges.map((item) => {
      // 计算重复边的偏移量，解决重叠
      const sameEdges = option.edges.filter(
        (e) => e.startId === item.startId && e.endId === item.endId
      );
      const offsetIndex = sameEdges.findIndex((e) => e.id === item.id);
      const offset = (offsetIndex - (sameEdges.length - 1) / 2) * 8;

      return {
        ...item,
        lineWidth: 2,
        id: "edge" + item.id,
        name: item.relationType,
        source: item.startId + "",
        target: item.endId + "",
        states: [],
        offset: offset,
        // ========== 曲线配置写在style里，封装类可识别 ==========
        style: {
          stroke: "#40a9ff", // [表情]必加！没有就看不见线
          lineWidth: 2,
          type: "arc", // 曲线类型
          radius: 25, // 曲线弧度
          labelCfg: {
            autoRotate: true,
            refY: 8,
            style: { fill: "#333" },
          },
        },
      };
    }),
  };

  let options = {
    layout: {
      type: "d3-force",
      link: {
        distance: () => 100,
        strength: () => 0.2,
      },
      manyBody: {
        strength: () => -180,
      },
    },
    behaviors: [
      "zoom-canvas",
      "drag-canvas",
      // "drag-element",
      // {
      //   type: "drag-element-force",
      //   animation: false,
      // },
      // 根据节点数量动态选择拖拽行为
      data.nodes.length <= 200
        ? {
            type: "drag-element-force",
          }
        : data.nodes.length <= 5000
        ? {
            type: "drag-element", // 普通拖拽，性能更好
          }
        : {},
      {
        type: "hover-activate",
        enable: (event) => event.targetType === "node",
        degree: 0,
        state: "highlight",
      },
      {
        type: "click-select",
        key: "clickControl",
        state: "click",
        onClick: (d) => {
          console.log(d, "===点击事件===");
          if (d.target.nodeName === "document") {
            nodeDialogRef.value.dialogClose();
            currentNodeData.value = null;
          }
          if (d.target.type == "node") {
            handleNodeClickId(d.target.id);
          }
        },
      },
    ],
  };

  relationGraph = new G6Network("gragh-container", data, options);
  relationGraph.triggerNodeClick = function (nodeId) {
    handleNodeClickId(nodeId);
  };
}

/** 打开实体详情，并向详情组件传递任务及关联数据。 */
function handleNodeClickId(nodeId) {
  currentNodeData.value = nodes.value.find((e) => e.id == nodeId);
  if (
    !componentProps.embedded &&
    currentNodeData.value?.entityType == 2 &&
    currentNodeData.value.taskId
  ) {
    getTextListAndDocList({ entityId: currentNodeData.value.taskId });
  }
  relationGraph.zoomBy(currentNodeData.value);

  let param = {
    currNodeData: currentNodeData.value,
    edgesData: edges.value,
    taskInfo: taskInfo.value,
    releaseStatus: releaseStatus.value,
    textList: textList.value || [],
    docList: docList.value || [],
  };
  nodeDialogRef.value.dialogOpen(param);
}

// #region 右侧弹框方法
const nodeDialogClose = () => {
  relationGraph.resetColor();
};

// tool图例
const toolShow = ref(false);
const toolData = ref([]);
// #region 右侧弹框数据
// 折叠面板
const currentNodeData = ref();
const detailShow = ref(false);
const detailClose = () => {
  detailShow.value = false;
  nodeDialogRef.value?.dialogClose();
  nodeDialogRef.value?.setAssociations([], []);
  currentNodeData.value = null;
  relationGraph.resetColor();
  graph.unselectAll();
  // 恢复视角
  setTimeout(() => {
    graph.moveTo({ scale: 1.1 });
  }, 300);
};
// #endregion

onMounted(async () => {
  // getDictTypeList();
  // getDictDataList();
  // 获取schema&tool图例
  // schemaList.value = await listSchema({
  //     pageNum: 1,
  //     pageSize: 10000,
  //     graphId: graphId.value
  // });
  schemaList.value.data.rows.map((item) => {
    toolData.value.push({
      label: item.name,
      icon: item.color,
    });
  });
  if (componentProps.embedded) {
    renderGraphData(componentProps.graphDataSource);
    return;
  }
  const params = {
    entityType: taskInfo.value.pageType,
    entityId: taskInfo.value.id,
  };
  getGraphData(params);
  if (taskInfo.value.pageType == "2") {
    getTextListAndDocList(params);
  }
});

watch(
  () => componentProps.graphDataSource,
  (val) => {
    if (componentProps.embedded) {
      nextTick(() => renderGraphData(val));
    }
  },
  { deep: true }
);

onBeforeUnmount(() => {
  // 销毁 G6 实例，释放内存
  if (relationGraph) {
    relationGraph.destroy();
    relationGraph = null;
  }
  // 清理 graphData
  graphData.value = { nodes: [], edges: [] };
  console.log("G6实例已销毁");
});

const searchShow = ref(false);
const searchVal = ref("");
const searchData = ref([]);
const querySearchAsync = (queryString, cb) => {
  const results = queryString
    ? searchData.value.filter(createFilter(queryString.trim()))
    : searchData.value;
  cb(results);
};
const createFilter = (queryString) => {
  return (restaurant) => {
    return (
      restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) !== -1
    );
  };
};
const autocompleteRef = ref(null);
const searchSelect = (item) => {
  filterEmit(item);
  // 让输入框失去焦点，使光标消失
  if (autocompleteRef.value) {
    autocompleteRef.value.blur();
  }
};
const isfull = ref(false);
const toolbarRef = ref();
const toolbar = ref([
  {
    id: "zoom-out",
    icon: "toolbar (1).png",
    tip: "缩小",
  },
  {
    id: "zoom-in",
    icon: "toolbar (9).png",
    tip: "放大",
  },
  {
    id: "full-screen",
    icon: "toolbar (8).png",
    isFull: false,
    tip: "全屏",
  },
  {
    id: "offset-left",
    icon: "toolbar (7).png",
    tip: "左移",
  },
  {
    id: "offset-right",
    icon: "toolbar (6).png",
    tip: "右移",
  },
  //   {
  //     id: "undo",
  //     icon: "RefreshLeft",
  //     tip: "撤销",
  //   },
  //   {
  //     id: "redo",
  //     icon: "RefreshRight",
  //     tip: "重做",
  //   },
  {
    id: "auto-fit",
    icon: "toolbar (2).png",
    tip: "恢复视角",
  },
  {
    id: "export",
    icon: "toolbar (3).png",
    tip: "导出",
  },
  {
    id: "reset",
    icon: "toolbar (4).png",
    tip: "重置",
  },
  {
    id: "search",
    icon: "toolbar (5).png",
    tip: "搜索",
  },
]);
const { toggle } = useFullscreen();
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
      detailShow.value = false;
      currentNodeData.value = null;

      // 重新加载数据
      let params = {
        entityType: taskInfo.value.pageType,
        entityId: taskInfo.value.id,
      };
      getGraphData(params);

      // 根据taskId获取段落数据和文档
      if (taskInfo.value.pageType == "2") {
        getTextListAndDocList(params);
      }
      break;
    }
    case "search": {
      searchShow.value = !searchShow.value;
      break;
    }
  }
};

// 过滤树
const filterText = ref("");
const treeRef = ref();
watch(filterText, (val) => {
  treeRef.value && treeRef.value.filter(val);
});

// 树结构
const props = {
  label: "label",
  children: "children",
};
let treeData = ref([]);
let checkedKeys = ref([]);
const initTree = () => {
  // 按照概念分类组织实体
  const conceptMap = new Map();
  const otherEntities = [];

  // 遍历所有实体，按照概念分类
  nodes.value.forEach((item) => {
    if (item.schemaId) {
      // 找到对应的概念
      const concept = schemaList.value.data.rows.find(
        (schema) => schema.id == item.schemaId
      );
      if (concept) {
        // 如果概念不存在于映射中，创建一个新的概念节点
        if (!conceptMap.has(concept.id)) {
          conceptMap.set(concept.id, {
            id: `concept_${concept.id}`,
            label: concept.name,
            children: [],
          });
        }
        // 将实体添加到对应概念的子节点中
        conceptMap.get(concept.id).children.push({
          id: item.id,
          label: item.name,
        });
      } else {
        // 找不到对应的概念，放入其他
        otherEntities.push({
          id: item.id,
          label: item.name,
        });
      }
    } else {
      // 没有schemaId，放入其他
      otherEntities.push({
        id: item.id,
        label: item.name,
      });
    }
  });

  // 构建树结构
  treeData.value = [];

  // 添加概念节点
  conceptMap.forEach((conceptNode) => {
    treeData.value.push(conceptNode);
  });

  // 添加其他节点
  if (otherEntities.length > 0) {
    treeData.value.push({
      id: "other",
      label: "其他",
      children: otherEntities,
    });
  }

  // 获取所有节点的ID
  const getAllIds = (treeData) => {
    let ids = [];
    for (const node of treeData) {
      ids.push(node.id); // 将当前节点的id加入数组
      if (node.children && node.children.length > 0) {
        ids = ids.concat(getAllIds(node.children)); // 递归处理子节点
      }
    }
    return ids;
  };
  checkedKeys.value = getAllIds(treeData.value);
  // console.log(checkedKeys.value);
};

// 更新搜索数据
const updateSearchData = () => {
  // 构建当前可见的节点ID集合
  const visibleNodeIds = new Set();

  // 直接从 checkedKeys 中筛选出实体节点的ID
  // 不依赖于树结构的遍历，确保所有被勾选的实体节点都能被找到
  const checkedEntityIds = checkedKeys.value.filter((id) => /^\d+$/.test(id));
  checkedEntityIds.forEach((id) => visibleNodeIds.add(id));

  // 重新构建搜索数据
  const maxLength = 16; // 设置最大显示长度
  const tripletData = [];
  const singleEntityData = [];
  const processedEntityIds = new Set();

  // 处理三元组
  edges.value.forEach((item) => {
    const startNodeVisible = visibleNodeIds.has(item.startId);
    const endNodeVisible = visibleNodeIds.has(item.endId);

    if (startNodeVisible && endNodeVisible) {
      // 两个节点都可见，添加三元组
      const startNode = nodes.value.find((node) => node.id == item.startId);
      const endNode = nodes.value.find((node) => node.id == item.endId);

      if (startNode && endNode) {
        const tripletText = `${startNode.name} ${item.name} ${endNode.name}`;
        const displayText =
          tripletText.length > maxLength
            ? tripletText.substring(0, maxLength) + "..."
            : tripletText;

        tripletData.push({
          ...item,
          value: tripletText,
          name: displayText,
          fullName: tripletText,
          type: "triplet",
          startNodeId: startNode.id,
          startNodeName: startNode.name,
          id: "triplet_" + item.id,
        });

        // 标记这两个实体已经在三元组中处理过
        processedEntityIds.add(item.startId);
        processedEntityIds.add(item.endId);
      }
    }
  });

  // 处理所有可见的实体节点，确保每个被勾选的实体都能显示
  visibleNodeIds.forEach((nodeId) => {
    const node = nodes.value.find((n) => n.id == nodeId);
    if (node) {
      // 检查该实体是否已经在三元组中处理过
      if (!processedEntityIds.has(nodeId)) {
        // 不管是否有其他三元组，只要被勾选就作为单个实体添加
        // 这样可以确保所有被勾选的实体都能在下拉框中显示
        const displayText =
          node.name.length > maxLength
            ? node.name.substring(0, maxLength) + "..."
            : node.name;

        singleEntityData.push({
          ...node,
          value: node.name,
          name: displayText,
          fullName: node.name,
          type: "entity",
          id: "entity_" + node.id,
        });
      }
    }
  });

  // 合并三元组数据和单个实体数据
  searchData.value = [...tripletData, ...singleEntityData];
};

// 递归获取节点的所有子节点ID
const getAllChildIds = (nodeId) => {
  const childIds = [];

  // 查找当前节点
  const findNode = (nodes) => {
    for (const node of nodes) {
      if (node.id === nodeId) {
        return node;
      }
      if (node.children && node.children.length > 0) {
        const found = findNode(node.children);
        if (found) {
          return found;
        }
      }
    }
    return null;
  };

  const node = findNode(treeData.value);

  // 递归收集子节点ID
  const collectChildIds = (node) => {
    if (node.children && node.children.length > 0) {
      for (const child of node.children) {
        childIds.push(child.id);
        collectChildIds(child);
      }
    }
  };

  if (node) {
    collectChildIds(node);
  }

  return childIds;
};

const handleCheck = (data, checked) => {
  const id = data.id;
  console.log(id);
  console.log(checked);

  // 更新 checkedKeys.value 为最新的勾选状态
  checkedKeys.value = checked.checkedKeys;

  if (id !== "parent1") {
    // 获取当前节点及其所有子节点的ID
    const allIds = [id, ...getAllChildIds(id)];

    if (checked.checkedKeys.includes(id)) {
      // 显示节点及相连的边
      allIds.forEach((nodeId) => {
        // 只处理实体节点（数字ID），跳过概念节点
        if (/^\d+$/.test(nodeId)) {
          relationGraph.instance.showElement(nodeId);

          // 显示所有连接到该节点的边
          const connectedEdges =
            relationGraph.instance.getRelatedEdgesData(nodeId);
          connectedEdges.forEach((edge) => {
            relationGraph.instance.showElement(edge.id);
          });
        }
      });
    } else {
      // 隐藏节点及相连的边
      allIds.forEach((nodeId) => {
        // 只处理实体节点（数字ID），跳过概念节点
        if (/^\d+$/.test(nodeId)) {
          relationGraph.instance.hideElement(nodeId);

          // 隐藏所有连接到该节点的边
          const connectedEdges =
            relationGraph.instance.getRelatedEdgesData(nodeId);
          connectedEdges.forEach((edge) => {
            relationGraph.instance.hideElement(edge.id);
          });
        }
      });
    }
  } else {
    if (checked.checkedKeys.length !== 0) {
      // 显示所有节点和边
      nodes.value.forEach((node) => {
        relationGraph.instance.showElement(node.id);
        // 显示所有连接到该节点的边
        const connectedEdges = relationGraph.instance.getRelatedEdgesData(
          node.id
        );
        connectedEdges.forEach((edge) => {
          relationGraph.instance.showElement(edge.id);
        });
      });
    } else {
      // 隐藏所有节点和边
      nodes.value.forEach((node) => {
        relationGraph.instance.hideElement(node.id);
        // 隐藏所有连接到该节点的边
        const connectedEdges = relationGraph.instance.getRelatedEdgesData(
          node.id
        );
        connectedEdges.forEach((edge) => {
          relationGraph.instance.hideElement(edge.id);
        });
      });
    }
  }

  // 更新搜索数据
  updateSearchData();
};
const handleNodeClick = (data) => {
  console.log(data.id);
  filterEmit(data);
};
const oldId = ref(null);
const filterEmit = (data) => {
  if (/^\d+$/.test(data.id)) {
    selectedIds.value.push(Number(data.id));
  }

  if (oldId.value) {
    relationGraph.resetColor();
  }

  oldId.value = data.id;

  // 处理概念节点或其他节点（非实体节点）
  if (
    data.id.startsWith("concept_") ||
    data.id === "other" ||
    data.id == "parent1" ||
    data.id == "parent2"
  )
    return;

  // 处理三元组类型
  if (data.type === "triplet") {
    // 找到起始实体
    const startNode = nodes.value.find((node) => node.id == data.startNodeId);

    if (startNode) {
      // 定位到起始实体
      relationGraph.zoomBy(startNode);
      // 点击起始实体，显示其详情
      handleNodeClickId(startNode.id);
    }
  }
  // 处理实体类型（树节点点击或单个实体搜索结果）
  else {
    // 处理单个实体搜索结果（id可能带有entity_前缀）
    let entityId = data.id;
    if (data.id.startsWith("entity_")) {
      entityId = data.id.replace("entity_", "");
    }

    // 找到对应的实体
    const entity = nodes.value.find((node) => node.id == entityId);
    if (entity) {
      // 定位到该节点
      relationGraph.zoomBy(entity);
      // 点击实体，显示其详情
      handleNodeClickId(entity.id);
    }
  }
};
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.includes(value);
};

const handleDel = () => {
  const id = currentNodeData.value.id;
  proxy.$modal
    .confirm(`是否确认删除实体为【${currentNodeData.value.name}】的数据项？`)
    .then(() => {
      // 隐藏弹框，过滤树，删除节点
      detailClose();
      treeData.value[0].children = treeData.value[0].children.filter(
        (e) => e.id !== id
      );

      // 从 G6 中删除节点和关联的边
      relationGraph.instance.removeData({
        nodes: [id],
        edges: edges.value
          .filter((e) => e.startId === id || e.endId === id)
          .map((e) => e.id),
      });

      // 过滤数据
      nodes.value = nodes.value.filter((e) => e.id !== id);
      edges.value = edges.value.filter((e) => e.from !== id && e.to !== id);

      // 接口
      deleteNode(id).then((response) => {
        console.log("-------删除成功------", response);
        proxy.$modal.msgSuccess("删除成功");
      });
    })
    .catch((err) => {
      console.log(err);
    });
};
const collapseAct = ref(["1", "2", "3", "4", "5"]);
const collapseChange = (val) => {
  console.log(val);
};
// 属性信息
// const getAttrData = (node) => {
//     attrLoading.value = true;
//     attrData.value = [];
//     let attributeIdList = [];
//     let attributeList = [];
//     for (let key in node) {
//         if (key.includes('attributeId')) {
//             attributeIdList.push(key);
//             attributeList.push({ key: key, value: node[key] });
//         }
//     }
//     if (attributeIdList.length > 0) {
//         let attributeIdString = attributeIdList.join(',');
//         getAttributeInformation(attributeIdString).then((res) => {
//             if (res && res.code == 200) {
//                 res.data.forEach((item) => {
//                     attributeList.forEach((att) => {
//                         if (att.key == 'attributeId' + item.id) {
//                             //处理字典类型的数据
//                             if (item.dictTypeId != null) {
//                                 //匹配对应的类型
//                                 const typeData = dictTypeList.value.find(
//                                     (dict) => dict.id == item.dictTypeId
//                                 );
//                                 //根据类型匹配对应的数据
//                                 const dictData = dictDataList.value.find(
//                                     (dict) =>
//                                         dict.dictType == typeData.dictType &&
//                                         dict.dictValue == att.value
//                                 );
//                                 item.dataValue = dictData.dictLabel;
//                             } else {
//                                 item.dataValue = att.value;
//                             }
//                         }
//                     });
//                 });
//                 attrData.value = res.data;
//                 attrLoading.value = false;
//             }
//         });
//     } else {
//         attrLoading.value = false;
//     }
// };
const attrLoading = ref(false);
const attrData = ref([]);
const attrVisible = ref(false);
const attrForm = ref({});
const attrUpdate = (row) => {
  attrVisible.value = true;
  //处理字典类型的数据
  if (row.dictTypeId != null) {
    //匹配对应的类型
    const typeData = dictTypeList.value.find(
      (dict) => dict.id == row.dictTypeId
    );
    //根据类型匹配对应的数据
    dictDataOption.value = dictDataList.value.filter(
      (dict) => dict.dictType == typeData.dictType
    );
  }
  attrForm.value = row;
};
const attrDelete = (row) => {
  proxy.$modal
    .confirm(`是否确认删除属性名称为【${row.name}】的数据项？`)
    .then(() => {
      let node = currentNodeData.value;
      deleteNodeAttributeById({
        nodeId: node.id,
        attributeKey: "attribute_id_" + row.id,
      }).then((res) => {
        if (res && res.code == 200) {
          attrData.value = attrData.value.filter((e) => e.id != row.id);
          proxy.$modal.msgSuccess("删除成功");
        }
      });
    });
};
const attrFormCancel = () => {
  attrForm.value = {};
  attrVisible.value = false;
  proxy.resetForm("attrRef");
};
const attrFormSubmit = () => {
  let node = currentNodeData.value;
  const data = dictDataOption.value.find(
    (dict) => dict.dictLabel == attrForm.value.dataValue
  );
  // updateNodeAttribute({
  //     taskId: node.taskId,
  //     tableName: node.tableName,
  //     dataId: node.dataId,
  //     extractType: node.extractType,
  //     attributeKey: 'attribute_id_' + attrForm.value.id,
  //     attributeValue: data.dictValue,
  //     primaryKey: attrForm.value.nameCode,
  //     primaryKeyData: data.dictValue
  // }).then((res) => {
  //     if (res && res.code == 200) {
  //         proxy.$modal.msgSuccess('修改成功');
  //     }
  // });
  attrForm.value = {};
  attrVisible.value = false;
  proxy.resetForm("attrRef");
};
// 关联三元组
const getTripletData = (id) => {
  tripletData.value = edges.value.filter(
    (e) => e.startId == id || e.endId == id
  );
};
const tripletLoading = ref(false);
const tripletData = ref([]);
const tripletDelete = (row) => {
  const id = row.id.replace(/edge/g, "");
  proxy.$modal
    .confirm(`是否确认删除当前三元组？`)
    .then(() => {
      // 从 G6 中删除边
      relationGraph.instance.removeData({
        edges: [row.id],
      });

      // 过滤数据
      edges.value = edges.value.filter((e) => e.id !== row.id);
      getTripletData(currentNodeData.value.id);

      // 接口
      deleteRelationshipById(id).then((response) => {
        console.log("-------删除成功------", response);
        proxy.$modal.msgSuccess("删除成功");
      });
    })
    .catch(() => {});
};

//关联数据源
const dataSource = ref({
  database: {},
  dataTable: [],
});
const dataSourceLoading = ref(false);
// const getDataSorceData = (val) => {
//     dataSourceLoading.value = true;
//     dataSource.value = {
//         database: {},
//         dataTable: []
//     };
//     if (!val.id || !val.primaryKey || !val.tableName) {
//         dataSourceLoading.value = false;
//         return;
//     }
//     getTableDataByDataId(val).then((res) => {
//         if (res && res.code == 200) {
//             dataSource.value = res.data;
//             dataSourceLoading.value = false;
//         }
//     });
// };

//关联分段和关联文件
const textList = ref(null);
const docList = ref(null);

watch(
  () => componentProps.textListSource,
  (val) => {
    if (componentProps.embedded) {
      textList.value = val;
    }
  },
  { immediate: true, deep: true }
);

watch(
  () => componentProps.docListSource,
  (val) => {
    if (componentProps.embedded) {
      docList.value = val;
    }
  },
  { immediate: true, deep: true }
);

/** 查询任务关联的分段和文件；嵌入模式由父页面统一查询。 */
function getTextListAndDocList(val) {
  if (componentProps.embedded) {
    return;
  }
  textList.value = null;
  docList.value = null;
  nodeDialogRef.value?.setAssociations([], []);
  const requestTaskId = val.entityId;
  getUnstructTaskTextList({ taskId: requestTaskId }).then((response) => {
    if (
      currentNodeData.value?.taskId &&
      String(currentNodeData.value.taskId) !== String(requestTaskId) &&
      String(taskInfo.value.id) !== String(requestTaskId)
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

//关联文件-预览
function previewRefactoring(row) {
  let path = import.meta.env.VITE_APP_BASE_API + "/profile" + row.path;
  filePreview(path);
}

// #endregion

const tagMultipleRef = ref(null);

function openTagMultiple() {
  // 调用子组件暴露的 open 方法，可传入默认选中的标签（可选，这里不传则默认无选中）
  tagMultipleRef.value.open(tagList.value);
}

/** 接收子组件选中的标签数据（子组件触发 confirm 事件时执行） */
// function handleTagSingleConfirm(selectedTag) {
//     // selectedTag 是子组件回传的选中标签对象（包含 id、name、graphId 等所有字段）
//     console.log('选中的标签数据：', selectedTag);
//     const updateTanEntityData = ref({
//         entityId: entId,
//         graphId: graphId,
//         tagIds: selectedTag
//     });
//     // 执行更新实体标签操作
//     updateTags(updateTanEntityData.value);
// }

// 查询当前实体标签
// function getEntityTags() {
//     entity(entId.value, graphId.value)
//         .then((response) => {
//             tagList.value = response.data;
//             console.log('tagList.value-----', tagList.value);
//         })
//         .catch(() => {});
// }

// 更新当前实体标签
// function updateTags(data) {
//     updateEntityTags(data)
//         .then((response) => {
//             // 调用标签查询
//             getEntityTags();
//             proxy.$modal.msgSuccess('添加成功');
//         })
//         .catch(() => {});
// }

// 删除标签
// const handleClose = (tag) => {
//     const delTanEntityData = ref({
//         entityId: entId,
//         graphId: graphId,
//         tagId: tag.id
//     });
//     // 删除标签调用更新标签方法
//     proxy.$modal
//         .confirm('是否确认删除"' + tag.name + '"标签？')
//         .then(function () {
//             return deleteEntity(delTanEntityData.value);
//         })
//         .then(() => {
//             proxy.$modal.msgSuccess('删除成功');
//             // 调用标签查询
//             getEntityTags();
//         })
//         .catch(() => {});
// };
</script>

<style lang="scss">
.fileDialog {
  height: 90%;
  width: 85%;

  .el-dialog__body {
    height: calc(100% - 42px) !important;
  }
}
</style>
<style scoped lang="scss">
.graphView {
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.el-zoom-in-right-enter-active,
.el-zoom-in-right-leave-active {
  opacity: 1;
  transform: translateX(0);
  transform-origin: center right;
  transition: var(--el-transition-md-fade);
}

.el-zoom-in-right-enter-from,
.el-zoom-in-right-leave-active {
  opacity: 0;
  transform: translateX(100%);
}

.app-container {
  height: calc(100% - 30px);
  display: flex;
  flex-direction: column;

  .head-title {
    flex-shrink: 0;
    height: 46px;
    background: #fff;
    padding: 0px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .name {
      font-family: PingFang SC;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
      display: flex;
      align-items: center;
      font-weight: 500;

      &::before {
        content: "";
        display: inline-block;
        background-color: #2666fb;
        width: 5px;
        height: 20px;
        margin-right: 10px;
        border-radius: 2px;
      }
    }

    .btns {
      float: right;

      .el-button {
        height: 28px;
      }
    }
  }

  .wrap-container {
    height: 100%;
  }

  .gragh-wrap {
    position: relative;
    width: 100%;
    height: 94%;

    &.isfull {
      position: fixed;
      width: 100vw;
      height: 100vh;
      left: 0;
      top: 0;
      z-index: 3000;
    }

    .control-tree {
      position: absolute;
      top: 10px;
      bottom: 10px;
      left: 10px;
      width: 250px;
      height: calc(100% - 20px);
      background: radial-gradient(
        100% 0% at 50% 50%,
        #1c3668 0%,
        rgba(0, 15, 39, 0.27) 100%
      );
      border: 1px solid var(--el-color-primary);
      border-radius: 4px;
      overflow: hidden auto;
      padding: 10px;
      z-index: 1;

      &::-webkit-scrollbar {
        width: 0;
      }

      :deep(.el-tree) {
        --el-tree-text-color: #fff;
        background: transparent;

        .el-tree-node__content:hover {
          background-color: var(--el-color-primary);

          .el-checkbox__input {
            &.is-checked {
              .el-checkbox__inner {
                background: url("@/assets/ke/images/check-fff.png") no-repeat;

                &::after {
                  border-color: transparent;
                }
              }
            }

            .el-checkbox__inner {
              background: url("@/assets/ke/images/check-no-fff.png") no-repeat;
            }
          }
        }

        .el-tree-node.is-checked {
          .el-checkbox__inner {
            background: url("@/assets/ke/images/check.png") no-repeat;

            &::after {
              border-color: transparent;
            }
          }
        }

        .el-tree-node.is-current {
          .el-checkbox__input {
            &.is-checked {
              .el-checkbox__inner {
                background: url("@/assets/ke/images/check-fff.png") no-repeat;

                &::after {
                  border-color: transparent;
                }
              }
            }

            .el-checkbox__inner {
              background: url("@/assets/ke/images/check-no-fff.png") no-repeat;
            }
          }
        }

        .el-tree-node:focus > .el-tree-node__content {
          background-color: var(--el-color-primary);
        }

        .el-checkbox__inner {
          background: url("@/assets/ke/images/check-no.png") no-repeat;
          border: none;
        }

        .el-tree-node__content > label.el-checkbox {
          margin-right: 10px;
        }
      }

      :deep(
          .el-tree--highlight-current
            .el-tree-node.is-current
            > .el-tree-node__content
        ) {
        background-color: var(--el-color-primary) !important;
      }
    }

    .toolbar {
      position: absolute;
      top: 10px;
      right: unset;
      bottom: unset;
      left: calc(250px + 10px + 15px);
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 2px;
      box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.4);
      // opacity: 0.65;
      z-index: 1;

      .toolbar-item {
        display: inline-block;
        width: 34px;
        height: 32px;
        cursor: pointer;
        box-sizing: content-box;
        margin-right: 5px;

        &:hover {
          background-color: rgba(255, 255, 255, 0.2);
        }

        img {
          width: 100%;
          height: 100%;
        }
      }

      .search {
        :deep(.el-input__wrapper) {
          background: transparent;

          .el-input__inner {
            color: #fff;
          }
        }
      }
    }

    .tool {
      cursor: pointer;
      position: absolute;
      left: calc(250px + 10px + 15px);
      bottom: 10px;
      width: 50px;
      height: 50px;
      background: url("@/assets/ke/images/tool.png") no-repeat;
      background-size: 100% 100%;
      z-index: 1;

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

    .gragh-container {
      width: 100%;
      height: 100%;
      background: #fff url("@/assets/ke/images/bg1.png") no-repeat;
      background-size: 100% 100%;
      transition: width 0.3s;

      &.detailShow {
        width: calc(100% - 400px);
      }

      :deep(.g6-toolbar) {
        .g6-toolbar-item {
          fill: #7dbffa;

          &:hover {
            background-color: rgba(255, 255, 255, 0.2);
          }
        }
      }
    }

    .details-dialog {
      position: absolute;
      top: 0;
      right: 0;
      width: 400px;
      height: 100%;
      background: #fff;
      border: 1px solid rgba(0, 0, 0, 0.1);

      .details-title {
        height: 42px;
        padding: 0 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #e8e8e8;

        .title-label {
          display: flex;
          align-items: center;

          .icon {
            cursor: pointer;
          }

          .label {
            width: 270px;
            margin-left: 12px;
            color: var(--el-color-primary);
          }
        }

        //.title-slot {}
      }

      .details-con {
        height: calc(100% - 42px);
        overflow: hidden auto;
        padding: 10px 10px;
        --el-border-color-lighter: #ddd;

        &::-webkit-scrollbar {
          width: 2px;
        }

        :deep(.el-collapse) {
          border: none;

          .el-collapse-item__wrap {
            border: none;
          }

          .el-collapse-item {
            margin-bottom: 10px;
          }
        }

        :deep(.el-collapse-item__header) {
          border: none;
          height: 30px;
        }

        :deep(.el-collapse-item__content) {
          padding-bottom: 0px;
        }

        .collapse-title {
          font-family: Pingfang Sc;
          font-size: 16px;
          font-weight: 600;
          // padding: 0 10px;
          display: flex;
          align-items: center;

          &::before {
            display: inline-block;
            content: "";
            width: 6px;
            height: 16px;
            border-radius: 3px;
            background: var(--el-color-primary);
            margin-right: 10px;
          }
        }

        .collapse-con {
          padding: 5px 0;

          :deep(
              .el-descriptions__label.el-descriptions__cell.is-bordered-label
            ) {
            width: 100px;
          }
        }

        .collapse-con1 {
          padding: 5px 5px;

          :deep(
              .el-descriptions__label.el-descriptions__cell.is-bordered-label
            ) {
            width: 100px;
          }

          .docItem {
            display: flex;
            justify-content: space-between;

            .docTd {
              text-align: left;
              width: 80%;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              cursor: pointer;
            }
          }
        }
      }
    }
  }

  :deep(.attr-dialog) {
    &:not(.is-fullscreen) {
      margin-top: 30vh !important;
    }

    .el-dialog__body {
      height: 150px;
    }
  }
}
</style>
