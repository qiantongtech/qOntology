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
  <div class="node-dialog">
    <transition name="el-zoom-in-right">
      <div class="details-dialog" v-if="show">
        <div class="details-title">
          <div class="title-label">
            <!--            <el-icon class="icon" @click="dialogClose('close')">-->
            <!--              <Close />-->
            <!--            </el-icon>-->
            <span
              @dblclick="!releaseStatus && (isEditName = true)"
              class="label ellipsis"
              :title="currNodeData.name"
              v-if="!isEditName"
              >{{
                "实体详情 - " + currNodeData.name + " (" + currNodeData.id + ")"
              }}</span
            >
            <el-input
              v-model="currNodeData.name"
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
              @click="handleNodeDelete"
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
                        :options="basic_data_type"
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
                      {{ scope.row.dataLabel ? scope.row.dataLabel : "-" }}
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
                        style="margin-left: 5px"
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
                          !releaseStatus && (isRelationshipEdit = scope.$index)
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
                        >删除</el-button
                      >
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>

            <!--                        <el-collapse-item title="标签选择" name="3">-->
            <!--                            <template #title>-->
            <!--                                <div class="collapse-title">标签选择</div>-->
            <!--                            </template>-->
            <!--                            <div>-->
            <!--                                <el-tag-->
            <!--                                    v-for="tag in tagList"-->
            <!--                                    :key="tag.id"-->
            <!--                                    :disable-transitions="false"-->
            <!--                                    closable-->
            <!--                                    @close="handleClose(tag)"-->
            <!--                                    :style="{ margin: '5px 5px 0px 0px' }"-->
            <!--                                >-->
            <!--                                    {{ tag.name }}-->
            <!--                                </el-tag>-->
            <!--                                <el-button-->
            <!--                                    size="small"-->
            <!--                                    @click="openTagMultiple"-->
            <!--                                    :style="{ 'margin-top': '5px' }"-->
            <!--                                >-->
            <!--                                    + 绑定标签-->
            <!--                                </el-button>-->
            <!--                            </div>-->
            <!--                        </el-collapse-item>-->
            <template v-if="currNodeData.entityType == 1">
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
                      :show-overflow-tooltip="{ effect: 'light' }"
                    >
                      <template #default="scope">
                        {{ scope.row.value ? scope.row.value : "-" }}
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-collapse-item>
            </template>
            <template v-if="currNodeData.entityType == 2">
              <el-collapse-item title="关联分段" name="4">
                <template #title>
                  <div class="collapse-title">关联分段</div>
                </template>
                <div class="collapse-con1">
                  <div v-for="(item, index) in textList" :key="index">
                    <span
                      v-if="isAssociatedText(currNodeData.textIds, item.id)"
                      >{{ item.text }}</span
                    >
                  </div>
                </div>
              </el-collapse-item>
              <el-collapse-item title="关联文件" name="5">
                <template #title>
                  <div class="collapse-title">关联文件</div>
                </template>
                <div class="collapse-con1">
                  <div v-for="(doc, index) in docList" :key="index">
                    <div v-if="currNodeData.docId == doc.id" class="docItem">
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
                :multiple="attrForm.multipleFlag === 1"
              >
                <el-option
                  v-for="item in dictDataOption"
                  :key="item.dictLabel"
                  :label="item.dictLabel"
                  :value="item.dictValue"
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
          <el-button type="primary" @click="attrFormSubmit">确 定</el-button>
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
    <!--        <TagMultiple ref="tagMultipleRef" @confirm="handleTagSingleConfirm" />-->
  </div>
</template>
<script setup name="NodeDialog">
// 非结构化接口
// import { getTaskTextList } from '@/api/ext/extUnstructTask/unstructTask';
// 结构化接口
// import {
//     getAttributeInformation,
//     updateNodeAttributeById
// } from '@/api/ext/extStructTask/extStruct';
// import { getTableDataByDataId } from '@/api/ext/extDatasource/datasource';
// 综合接口
import {
  deleteNodeAttributeById,
  deleteRelationshipById,
  deleteNode,
  addNode,
  addTripletRel,
  getNodeDatasource,
} from "@/api/app/graph";
// import { entity, updateEntityTags, deleteEntity } from '@/api/kg/manage/tagEntity.js';
// import { listType } from '@/api/kg/dict/type';
// import { listData } from '@/api/kg/dict/data';

import AddEntity from "./model/addEntity.vue";
import { listAttributes } from "@/api/om/object/attribute";
import AddRelationship from "./model/addRelationship.vue";
// import TagMultiple from '@/views/kg/manage/tag/selection/tagMultiple.vue';
import { filePreview, filePreviewUrl } from "@/utils/kkFileView.js";
const route = useRoute();
const { proxy } = getCurrentInstance();
const { basic_data_type } = proxy.useDict("basic_data_type");

let graphData = ref({
  nodes: [],
  edges: [],
});
// #region 右侧弹框数据
const emit = defineEmits(["dialogClose", "handleNodeDelete", "handleRefresh"]);
const nodeStore = reactive({
  show: false,
  currNodeData: {},
  edgesData: [],
  // 1结构化 2非结构化 3故障 0探索,
  taskInfo: { id: "", name: "图谱探索", pageType: "0" },
  // 是否发布 taskInfo.pageType == 0 为false
  releaseStatus: false,
});
const { currNodeData, show, edgesData, taskInfo, releaseStatus } =
  toRefs(nodeStore);
/** 打开节点详情弹框。 */
const dialogOpen = (param) => {
  currNodeData.value = {};
  if (param && param.currNodeData && param.edgesData && param.taskInfo) {
    show.value = true;
    currNodeData.value = param.currNodeData;
    edgesData.value = param.edgesData;
    taskInfo.value = param.taskInfo;
    releaseStatus.value = Boolean(param.releaseStatus);
    textList.value = param.textList || [];
    docList.value = param.docList || [];
    loadData();
  } else {
    proxy.$modal.msgWarning("节点弹框入参为空");
  }
};
const dialogClose = (close) => {
  show.value = false;
  if (close) {
    emit("dialogClose");
  }
};

/** 异步加载完成后更新当前详情面板的来源内容。 */
const setAssociations = (texts, documents) => {
  textList.value = texts || [];
  docList.value = documents || [];
};
// #endregion
// 初始化数据
function loadData() {
  // getDictTypeList();
  // getDictDataList();
  // 获取标签
  // getEntityTags();
  // 获取属性信息
  getAttrData(currNodeData.value);
  // 获取关联三元组
  getTripletData(currNodeData.value.id);
  // 获取数据源
  if (currNodeData.value.entityType == 1) {
    getDataSorceData({
      nodeId: currNodeData.value.id,
      id: currNodeData.value.databaseId,
      dataId: currNodeData.value.dataId,
      primaryKey: currNodeData.value.primaryKey,
      primaryKeyData: currNodeData.value.primaryKeyData,
      tableName: currNodeData.value.tableName,
    });
  }
}

/** 查询字典类型列表 */
const dictTypeList = ref([]);
function getDictTypeList() {
  const params = {
    graphId: route.params.kgId,
    status: "0",
  };
  listType(params).then((response) => {
    dictTypeList.value = response.data.rows;
  });
}
/** 查询字典管理列表 */
const dictDataList = ref([]);
function getDictDataList() {
  const params = {
    graphId: route.params.kgId,
  };
  listData(params).then((response) => {
    dictDataList.value = response.data.rows;
  });
}
// # 修改实体名称
const isEditName = ref(false);
const isRelationshipEdit = ref(-1);
const updateName = () => {
  appLoading.value = true;
  isEditName.value = false;
  addNode([
    {
      id: currNodeData.value.id,
      name: currNodeData.value.name,
      entityType: currNodeData.value.entityType,
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
// # 删除实体
const handleNodeDelete = () => {
  const id = currNodeData.value.id;
  proxy.$modal
    .confirm(`是否确认删除实体为【${currNodeData.value.name}】的数据项？`)
    .then(() => {
      // // 隐藏弹框，过滤树，暂隐node节点
      // detailClose();
      // treeData.value[0].children = treeData.value[0].children.filter((e) => e.id !== id);
      // graphData.value.nodes.update([{ id: id, hidden: true }]);
      // // 过滤数据
      // nodes.value = nodes.value.filter((e) => e.id !== id);
      // edges.value = edges.value.filter((e) => e.from !== id && e.to !== id);
      emit("handleNodeDelete");
      // 接口
      deleteNode(id).then((response) => {
        proxy.$modal.msgSuccess("删除成功");
        handleRefresh();
        dialogClose(true);
      });
    })
    .catch((err) => {});
};
// # 侧边栏展开事件
const collapseAct = ref(["1", "2", "3", "4"]);
const collapseChange = (val) => {};

// #region属性信息
/** Neo4j节点中不作为业务属性展示的技术字段。 */
const nodeTechnicalFields = new Set([
  "id",
  "_id",
  "name",
  "graphId",
  "releaseStatus",
  "creatorId",
  "createTime",
  "createBy",
  "taskId",
  "entityType",
  "docId",
  "paragraphIndex",
  "textIds",
  "schemaName",
  "fragment",
  "label",
  "rowBackground",
  "rowBorder",
  "color",
  "borderWidth",
  "step",
  "databaseId",
  "tableName",
  "primaryKey",
  "primaryKeyData",
  "schemaId",
  "workspaceId",
  "projectId",
  "projectVersionId",
]);

/** 根据Neo4j属性值推断系统数据类型字典值。 */
function inferAttributeDataType(value) {
  if (typeof value === "boolean") {
    return 3;
  }
  if (Number.isInteger(value)) {
    return 1;
  }
  if (typeof value === "number") {
    return 2;
  }
  if (
    typeof value === "string" &&
    /^\d{4}-\d{2}-\d{2}(?:[ T]\d{2}:\d{2}(?::\d{2})?)?$/.test(value)
  ) {
    return 4;
  }
  return 0;
}

/** 将数组和对象属性转换为可展示文本。 */
function formatAttributeValue(value) {
  if (value === null || value === undefined) {
    return "";
  }
  return typeof value === "object" ? JSON.stringify(value) : String(value);
}

/** 从当前Neo4j节点中提取业务属性信息。 */
const getAttrData = async (node) => {
  attrLoading.value = true;
  let attributes = [];
  try {
    if (Number(node.entityType) === 1 && node.schemaId) {
      const response = await listAttributes({
        objectTypeId: node.schemaId,
        versionId: node.projectVersionId,
        pageSize: 100,
      });
      attributes = response.data?.rows || response.data?.list || [];
    }
    if (currNodeData.value.id !== node.id) return;
    attrData.value = Object.entries(node || {})
      .filter(([key]) => !nodeTechnicalFields.has(key))
      .map(([key, value]) => {
        const attribute = attributes.find(
          (item) => String(item.id) === key.replace(/^attribute(?:Id|_id_)/, "")
        );
        return {
          id: key,
          key,
          name: attribute?.name || key,
          dataType: attribute?.dataType ?? inferAttributeDataType(value),
          dataValue: formatAttributeValue(value),
          dataLabel: formatAttributeValue(value),
        };
      });
  } finally {
    attrLoading.value = false;
  }
};
const attrLoading = ref(false);
const attrData = ref([]);
const attrVisible = ref(false);
const attrForm = ref({});
const dictDataOption = ref([]);
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
    .confirm(`是否确认删除属性名称为【${row.name}】的属性项？`)
    .then(() => {
      let node = currNodeData.value;
      deleteNodeAttributeById({
        nodeId: node.id,
        attributeKey: row.key,
      }).then((res) => {
        if (res && res.code == 200) {
          attrData.value = attrData.value.filter((e) => e.id != row.id);
          proxy.$modal.msgSuccess("删除成功");
        }
        dialogClose(true);
        handleRefresh();
      });
    });
};
const attrFormCancel = () => {
  attrForm.value = {};
  attrVisible.value = false;
  proxy.resetForm("attrRef");
};
const attrFormSubmit = () => {
  let attributeValue = attrForm.value.dataValue;
  if (Array.isArray(attrForm.value.dataValue)) {
    attributeValue = attrForm.value.dataValue.join(",");
  }
  const node = currNodeData.value;
  addNode([
    {
      id: node.id,
      name: node.name,
      graphId: node.graphId,
      entityType: node.entityType,
      [attrForm.value.key]: attributeValue,
    },
  ]).then((res) => {
    if (res && res.code == 200) {
      proxy.$modal.msgSuccess("修改成功");
      attrForm.value = {};
      attrVisible.value = false;
      proxy.resetForm("attrRef");
      dialogClose(true);
      handleRefresh();
    }
  });
};
// #endregion
// #region关联三元组
const getTripletData = (id) => {
  tripletData.value = edgesData.value.filter(
    (e) => e.startId == id || e.endId == id
  );
};
const tripletLoading = ref(false);
const tripletData = ref([]);
// const tripletUpdate = () => {};
const tripletDelete = (row) => {
  const id = row.id.replace(/edge/g, "");
  proxy.$modal
    .confirm(`是否确认删除当前三元组？`)
    .then(() => {
      // 暂隐edge节点
      // graphData.value.edges.remove({ id: row.id });
      graphData.value.edges = graphData.value.edges.filter(
        (e) => e.id !== row.id
      );
      // 过滤数据
      // edges.value = edges.value.filter((e) => e.id !== row.id);
      // 加非空判断，避免读取null的id属性
      if (currNodeData && currNodeData.value && currNodeData.value.id) {
        getTripletData(currNodeData.value.id);
      }
      //
      deleteRelationshipById(id).then((response) => {
        proxy.$modal.msgSuccess("删除成功");
        handleRefresh();
        dialogClose(true);
      });
    })
    .catch((err) => {});
};
// #endregion
// #region关联数据源
const dataSource = ref({
  database: {},
  tableData: [],
});
const dataSourceLoading = ref(false);
const getDataSorceData = async (val) => {
  dataSourceLoading.value = true;
  dataSource.value = {
    database: {},
    tableData: [],
  };
  try {
    if (!val.nodeId || !val.id || !val.primaryKey || !val.tableName) {
      return;
    }
    const response = await getNodeDatasource({ nodeId: val.nodeId });
    dataSource.value = response.data || dataSource.value;
  } finally {
    dataSourceLoading.value = false;
  }
};
// #endregion
// #region关联分段和关联文件
const textList = ref([]);
const docList = ref([]);

/** 判断当前实体是否关联指定分段。 */
function isAssociatedText(textIds, textId) {
  if (textIds === null || textIds === undefined || textIds === "") {
    return false;
  }
  const associatedIds = Array.isArray(textIds)
    ? textIds
    : String(textIds).split(",");
  return associatedIds.map(String).includes(String(textId));
}
//关联文件-预览
function previewRefactoring(row) {
  let path = import.meta.env.VITE_APP_BASE_API + "/profile" + row.path;
  filePreview(path);
}
// #endregion
// #region实体标签
const tagMultipleRef = ref(null);
const tagList = ref([]);
// function openTagMultiple() {
//     // 调用子组件暴露的 open 方法，可传入默认选中的标签（可选，这里不传则默认无选中）
//     tagMultipleRef.value.open(tagList.value);
// }

/** 接收子组件选中的标签数据（子组件触发 confirm 事件时执行） */
function handleTagSingleConfirm(selectedTag) {
  // selectedTag 是子组件回传的选中标签对象（包含 id、name、graphId 等所有字段）
  const updateTanEntityData = ref({
    entityId: currNodeData.value.id,
    graphId: route.params.kgId,
    tagIds: selectedTag,
  });
  // 执行更新实体标签操作
  updateTags(updateTanEntityData.value);
}

// 查询当前实体标签
// function getEntityTags() {
//     entity(currNodeData.value.id, route.params.kgId ? route.params.kgId : '')
//         .then((response) => {
//             tagList.value = response.data;
//         })
//         .catch(() => {});
// }

// 更新当前实体标签
// function updateTags(data) {
//     updateEntityTags(data)
//         // eslint-disable-next-line no-unused-vars
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
//         entityId: currNodeData.value.id,
//         graphId: route.params.kgId,
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
// #endregion
// 弹框
const addTitle = ref("");
const handleRefresh = () => {
  //   toolbarClick({
  //     id: "reset",
  //   });
  emit("handleRefresh");
};
defineExpose({ dialogOpen, dialogClose, setAssociations, show });
</script>
<style lang="scss" scoped>
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
    padding: 0 12px 0 0;
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
        //color: var(--el-color-primary);
        font-size: 16px;
        font-weight: 200;
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
      font-size: 14px;
      //font-weight: 600;
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

      :deep(.el-descriptions__label.el-descriptions__cell.is-bordered-label) {
        width: 100px;
      }

      :deep(.el-table .el-table__header th .cell) {
        font-weight: normal;
        color: #333;
        font-family: Pingfang Sc;
        font-size: 14px;
      }
    }

    .collapse-con1 {
      padding: 5px 5px;

      :deep(.el-descriptions__label.el-descriptions__cell.is-bordered-label) {
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
:deep(.attr-dialog) {
  &:not(.is-fullscreen) {
    margin-top: 30vh !important;
  }
  .el-dialog__body {
    height: 150px;
  }
}
</style>
