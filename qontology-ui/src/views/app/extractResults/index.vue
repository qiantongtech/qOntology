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
  <div class="app-container" ref="appContainer" v-loading="loading">
    <div class="head-title">
      <div class="name">{{ taskInfo.name }}</div>
      <div class="btns">
        <el-button icon="Back" @click="handleBack">返回</el-button>
        <el-button
          v-if="!releaseStatus"
          :disabled="graphData.entities.length === 0"
          icon="Upload"
          type="primary"
          @click="handleRelease"
        >
          发布
        </el-button>
        <el-button
          v-else
          :disabled="graphData.entities.length === 0"
          icon="Upload"
          type="primary"
          @click="handleCancelRelease"
        >
          取消发布
        </el-button>
      </div>
    </div>
    <Graph
      embedded
      :task-info-source="taskInfo"
      :graph-data-source="graphData"
      :release-status-source="releaseStatus"
      :text-list-source="textList"
      :doc-list-source="docList"
      @refresh="getExtractionResult"
    />
  </div>
</template>

<script setup name="AppExtractResults">
import Graph from "@/views/app/graph/graph/index.vue";
import { getGraph } from "@/api/app/graph";
import {
  strutReleaseByTaskId,
  strutCancelReleaseByTaskId,
} from "@/api/app/structTask";
import {
  cancelReleaseUnstructTask,
  getUnstructTaskTextList,
  releaseUnstructTask,
} from "@/api/app/unstructTask";

const { proxy } = getCurrentInstance();
const route = useRoute();

const loading = ref(false);
const releaseStatus = ref(false);
const graphData = ref({ entities: [], relationships: [] });
const textList = ref([]);
const docList = ref([]);
const isStructured = computed(() => String(route.query.pageType) === "1");
const taskInfo = ref({
  id: Number(route.query.id),
  name: route.query.name,
  pageType: route.query.pageType,
  pageNum: route.query.pageNum,
  pageSize: route.query.pageSize,
});

/** 查询结构化或非结构化抽取结果及其关联内容。 */
async function getExtractionResult() {
  const taskId = taskInfo.value.id;
  const pageType = taskInfo.value.pageType;
  loading.value = true;
  try {
    const graphResponse = await getGraph({
      entityType: pageType,
      entityId: taskId,
    });
    graphData.value = {
      entities: graphResponse.data?.entities || [],
      relationships: graphResponse.data?.relationships || [],
    };
    releaseStatus.value = graphData.value.entities.some(
      (entity) => entity.releaseStatus === 1
    );

    if (isStructured.value) {
      textList.value = [];
      docList.value = [];
      return;
    }
    const textResponse = await getUnstructTaskTextList({
      taskId,
    });
    textList.value = textResponse.data?.textListByTaskId || [];
    docList.value = (textResponse.data?.docListByTaskId || []).map(
      (document) => ({
        ...document,
        name: document.fileName,
        path: document.filePath,
      })
    );
  } finally {
    loading.value = false;
  }
}

/** 关闭当前页签并返回当前类型的抽取任务列表。 */
function handleBack() {
  proxy.$tab.closeOpenPage({
    path: isStructured.value ? "/app/structTask" : "/app/unstructTask",
    query: {
      pageNum: taskInfo.value.pageNum,
      pageSize: taskInfo.value.pageSize,
    },
  });
}

/** 发布当前结构化或非结构化抽取结果。 */
function handleRelease() {
  proxy.$modal.confirm("是否确认发布？").then(() => {
    const request = isStructured.value
      ? strutReleaseByTaskId({ taskId: taskInfo.value.id })
      : releaseUnstructTask(taskInfo.value.id);
    request.then((response) => {
      if (response && response.code === 200) {
        releaseStatus.value = true;
        proxy.$modal.msgSuccess("发布成功");
      }
    });
  });
}

/** 取消发布当前结构化或非结构化抽取结果。 */
function handleCancelRelease() {
  proxy.$modal.confirm("是否确认取消发布？").then(() => {
    const request = isStructured.value
      ? strutCancelReleaseByTaskId({ taskId: taskInfo.value.id })
      : cancelReleaseUnstructTask(taskInfo.value.id);
    request.then((response) => {
      if (response && response.code === 200) {
        releaseStatus.value = false;
        proxy.$modal.msgSuccess("取消发布成功");
      }
    });
  });
}

onMounted(getExtractionResult);
</script>

<style scoped lang="scss">
.app-container {
  .head-title {
    height: 46px;
    background: #fff;
    padding: 0 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;

    .name {
      font-family: PingFang SC;
      font-size: 16px;
      display: flex;
      align-items: center;

      &::before {
        content: "";
        display: inline-block;
        background: var(--el-color-primary);
        width: 6px;
        height: 16px;
        border-radius: 2px;
        margin-right: 10px;
      }
    }

    .btns {
      float: right;

      .el-button {
        height: 28px;
      }
    }
  }
}
</style>
