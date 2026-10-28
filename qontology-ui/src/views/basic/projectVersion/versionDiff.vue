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
  <div class="app-container" v-loading="loading">
    <!--  版本信息  -->
    <div class="version-info">
      <!--   基准版本（左侧）   -->
      <div class="border-item">
        <div class="border-item-head">
          <span class="h2-titles">基准版本</span>
          <el-select
              v-model="currentVersion"
              placeholder="请选择版本"
              class="version-select"
              @change="currentVersionChange"
          >
            <el-option
                v-for="version in projectVersionDict"
                :key="version.dictValue"
                :label="version.dictLabel"
                :value="version.dictValue"
            />
          </el-select>
        </div>
        <div class="border-item-body">
          <div class="infotop">
            <el-row :gutter="2" style="margin-bottom: 5px">
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建人</div>
                  <div class="infotop-row-value">
                    {{ currentVersionDiffStats.projectVersion?.createBy || "-" }}
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建时间</div>
                  <div class="infotop-row-value">{{
                      parseTime(currentVersionDiffStats.projectVersion?.createTime, "{y}-{m}-{d} {h}:{i}")
                    }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">版本描述</div>
                  <div class="infotop-row-value">
                    <el-tooltip
                        :content="currentVersionDiffStats.projectVersion?.description || '-'"
                        placement="top"
                        effect="light"
                        :disabled="!currentVersionDiffStats.projectVersion?.description"
                        popper-class="tooltip-desc"
                    >
                      <span>{{ currentVersionDiffStats.projectVersion?.description || "-" }}</span>
                    </el-tooltip>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <!--   对比版本（右侧）   -->
      <div class="border-item">
        <div class="border-item-head">
          <span class="h2-titles">对比版本</span>
          <el-select
              v-model="compareVersion"
              placeholder="请选择版本"
              class="version-select"
              @change="compareVersionChange"
          >
            <el-option
                v-for="version in projectVersionDict"
                :key="version.dictValue"
                :label="version.dictLabel"
                :value="version.dictValue"
            />
          </el-select>
        </div>
        <div class="border-item-body">
          <div class="infotop">
            <el-row :gutter="2" style="margin-bottom: 5px">
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建人</div>
                  <div class="infotop-row-value">
                    {{ compareVersionDiffStats.projectVersion?.createBy || "-" }}
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建时间</div>
                  <div class="infotop-row-value">
                    {{ parseTime(compareVersionDiffStats.projectVersion?.createTime, "{y}-{m}-{d} {h}:{i}") }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">版本描述</div>
                  <div class="infotop-row-value">
                    <el-tooltip
                        :content="compareVersionDiffStats.projectVersion?.description"
                        placement="top"
                        effect="light"
                        :disabled="!compareVersionDiffStats.projectVersion?.description"
                        popper-class="tooltip-desc"
                    >
                      <span>{{ compareVersionDiffStats.projectVersion?.description || "-" }}</span>
                    </el-tooltip>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>

    <!-- 参数定义 -->
    <div class="version-info diff-section">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">版本差异明细</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="模块名称" align="center">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="数量/率" align="center">
                  <template #default="scope">
                    <span v-if="scope.row.field === 'omTargetCoveredRate'">{{ scope.row.fieldValue + '%' }}</span>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">版本差异明细</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.compare" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="模块名称" align="center">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="数量/率" align="center">
                  <template #default="scope">
                    <span v-if="scope.row.field === 'omTargetCoveredRate'">{{ scope.row.fieldValue + '%' }}</span>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ModelVersionDiff">
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {parseTime} from "@/utils/anivia.js";

import {getProjectVersionDict, getVersionDiffStats} from "@/api/basic/project/projectVersion";

const route = useRoute();

const loading = ref(true)

const projectId = route.query.projectId || "";// 项目ID
const currentVersion = ref("");// 基准版本
const compareVersion = ref("");// 对比版本
const projectVersionDict = ref([]);// 项目版本字典

const currentVersionDiffStats = ref({projectVersion: {}});// 基准版本模型
const compareVersionDiffStats = ref({projectVersion: {}});// 对比版本模型

const diffResult = ref({
  current: [],
  compare: [],
});

const dictMap = {
  "authTypeDict": [
    {"label": "无鉴权", "value": "NONE", "listClass": "primary"},
    {"label": "固定 Token / API Key", "value": "FIXED", "listClass": "primary"},
    {"label": "动态 Token API", "value": "DYNAMIC", "listClass": "primary"},
  ],
  "authMethodDict": [
    {"label": "Bearer Token", "value": "bearer", "listClass": "primary"},
    {"label": "API Key", "value": "apiKey", "listClass": "primary"},
    {"label": "动态 Token API", "value": "DYNAMIC", "listClass": "primary"},
  ],
  "injectPositionDict": [
    {"label": "Header", "value": "Header", "listClass": "primary"},
    {"label": "Query", "value": "Query", "listClass": "primary"},
  ],
}


const fieldConfigMap = {
  "omTargetCount": {"name": "建模目标"},
  "omTargetCoveredRate": {"name": "建模目标覆盖率"},
  "termCount": {"name": "术语"},
  "enumerationCount": {"name": "枚举"},
  "objectTypeCount": {"name": "对象类型"},
  "objectTypeAttributeCount": {"name": "对象类型属性"},
  "relationCount": {"name": "对象类型关系"  },
  "modelAxiomCount": {"name": "模型公理"},
  "dataRuleCount": {"name": "数据规则"},
}

// 基准版本改变
async function currentVersionChange(val) {
  loading.value = true;
  currentVersion.value = val;
  const res = await getVersionDiffStats(currentVersion.value);
  currentVersionDiffStats.value = res.data;
  diffData();
  loading.value = false;
}

// 对比版本改变
async function compareVersionChange(val) {
  loading.value = true;
  compareVersion.value = val;
  const res = await getVersionDiffStats(compareVersion.value);
  compareVersionDiffStats.value = res.data;
  diffData();
  loading.value = false;
}

// 初始化数据
async function initData() {
  currentVersion.value = route.params.versionId || "";
  compareVersion.value = route.query.compareVersion || "";
  getProjectVersionDict(projectId).then((res) => {
    projectVersionDict.value = res.data;
  })

  const tasks = [];
  if (currentVersion.value) {
    tasks.push(getVersionDiffStats(currentVersion.value).then((res) => {
      currentVersionDiffStats.value = res.data || {projectVersion: {}};
    }))
  }
  if (compareVersion.value) {
    tasks.push(getVersionDiffStats(compareVersion.value).then((res) => {
      compareVersionDiffStats.value = res.data || {projectVersion: {}};
    }))
  }
  await Promise.all(tasks);

  diffData();
  loading.value = false;
}

// 表格行样式
function tableRowClassName(row) {
  return row.row.changeType;
}

// 对比数据
function diffData() {
  let a = currentVersionDiffStats.value || {};
  let b = compareVersionDiffStats.value || {};

  const allFields = [...new Set([...Object.keys(fieldConfigMap)])];
  const current = [];
  const compare = [];

  for (const field of allFields) {
    let fieldConfig = fieldConfigMap[field];
    if (!fieldConfig) {
      continue;
    }

    const hasA = Object.prototype.hasOwnProperty.call(a, field);
    const hasB = Object.prototype.hasOwnProperty.call(b, field);
    let valA = hasA ? a[field] : "";
    let valB = hasB ? b[field] : "";
    if (!valA && !valB) {
      current.push({field, fieldValue: 0, changeType: "", ...fieldConfig})
      compare.push({field, fieldValue: 0, changeType: "", ...fieldConfig})
      continue;
    }
    const isEqual = JSON.stringify(valA) === JSON.stringify(valB);
    let typeA, typeB;
    if (valA && valB) {
      if (!isEqual) {
        typeA = "row-yellow";
        typeB = "row-yellow";
      }
    } else if (valA && !valB) {
      // a有 b无
      typeA = "row-yellow";
      typeB = "row-yellow";
    } else {
      // a无 b有
      typeA = "row-yellow";
      typeB = "row-yellow";
    }
    current.push({field, fieldValue: valA, changeType: typeA, ...fieldConfig})
    compare.push({field, fieldValue: valB, changeType: typeB, ...fieldConfig})
  }
  diffResult.value = {current, compare};
}

// 获取字典选项
function getDictOptions(dictName) {
  if (!dictName) return []
  // 返回对应字典数组，不存在返回空数组
  return dictMap[dictName] || []
}

onMounted(() => {
  initData();
});

</script>

<style lang="scss" scoped>
.border-item {
  width: 100%;
  background: #fff;
  border-radius: 2px;
  padding: 0 16px 12px;
  margin-bottom: 8px;

  .border-item-head {
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #dfe3e9;
    margin: 0 -16px;
    padding: 0 16px;

    .head-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }

  .border-item-body {
    padding: 12px 0 0;
    background-color: #fff;
  }
}

.version-select {
  width: 200px;
}

.version-info {
  width: 100%;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
  flex-shrink: 0;
  overflow: auto;

  .border-item {
    flex: 1;
    min-width: 480px;

    &:first-child {
      padding-right: 12px;
    }

    &:last-child {
      padding-left: 12px;
    }
  }
}

// 版本差异明细：纵向撑满剩余视口
.diff-section {
  flex: 1;
  min-height: 320px;

  .border-item {
    display: flex;
    flex-direction: column;
    min-height: 0;

    .border-item-body {
      flex: 1;
      min-height: 0;
      display: flex;
    }
  }

  .version-compare {
    flex: 1;
    min-height: 0;

    .table-wrap {
      display: flex;
      flex-direction: column;

      :deep(.el-table) {
        flex: 1;
        height: 100%;
        min-height: 0;

        .el-scrollbar,
        .el-scrollbar__wrap,
        // 行数不足以撑满时，让行高均分撑满表格区域
        .el-scrollbar__view,
        .el-table__body {
          height: 100%;
        }
      }
    }
  }
}

.version-compare {
  width: 100%;
  display: flex;
  gap: 0;
  position: relative;
  overflow: hidden;
  flex-wrap: wrap;

  .table-wrap {
    flex: 1;
    overflow: hidden;
    min-width: 480px;

    &:first-child {
      padding-right: 16px;
      //border-right: 1px solid #dfe3e9;
    }

    &:last-child {
      padding-left: 16px;
    }
  }
}
</style>

<style>
.el-table .row-yellow {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.el-table .row-green {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}

.el-table .row-red {
  --el-table-tr-bg-color: var(--el-color-danger-light-9);
}
</style>
