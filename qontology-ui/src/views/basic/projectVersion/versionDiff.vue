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

<!--
  @file 项目版本对比页面
  @description 选择基准版本与对比版本，展示两个版本的基础信息与建模数量指标差异。
-->

<template>
  <div v-loading="loading" class="app-container">
    <!--  版本信息  -->
    <div class="version-info">
      <!--   基准版本（左侧）   -->
      <div class="border-item">
        <div class="border-item-head">
          <span class="h2-titles">基准版本</span>
          <el-select
              v-model="currentVersion"
              placeholder="请选择版本"
              @change="currentVersionChange"
              class="version-select"
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
          <div class="infotop version-info-body">
            <el-row :gutter="2" class="version-info-row">
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
            <el-row :gutter="2">
              <el-col :span="24">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">版本描述</div>
                  <div class="infotop-row-value">
                    <el-tooltip
                        :content="currentVersionDiffStats.projectVersion?.description || '-'"
                        placement="top"
                        effect="light"
                        :disabled="!currentVersionDiffStats.projectVersion?.description"
                    >
                      <span class="text-ellipsis">{{ currentVersionDiffStats.projectVersion?.description || "-" }}</span>
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
              @change="compareVersionChange"
              class="version-select"
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
          <div class="infotop version-info-body">
            <el-row :gutter="2" class="version-info-row">
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
                    >
                      <span class="text-ellipsis">{{ compareVersionDiffStats.projectVersion?.description || "-" }}</span>
                    </el-tooltip>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>

    <!-- 版本差异明细 -->
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
                <el-table-column label="模块名称" align="center">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量/率" align="center">
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
                <el-table-column label="模块名称" align="center">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量/率" align="center">
                  <template #default="scope">
                    <span v-if="scope.row.field === 'omTargetCoveredRate'">{{ scope.row.fieldValue + '%' }}</span>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                    <em v-if="scope.row.trend === 'up'" class="trend-arrow trend-up">▲</em>
                    <em v-else-if="scope.row.trend === 'down'" class="trend-arrow trend-down">▼</em>
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
/**
 * @file 项目版本对比页面
 * @description 选择基准版本与对比版本，展示两个版本的基础信息与建模数量指标差异。
 */
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";

import {parseTime} from "@/utils/anivia.js";
import {getProjectVersionDict, getVersionDiffStats} from "@/api/basic/project/projectVersion";

const route = useRoute();

const loading = ref(true);

const projectId = route.query.projectId || "";  // 项目ID
const currentVersion = ref("");  // 基准版本
const compareVersion = ref("");  // 对比版本
const projectVersionDict = ref([]);  // 项目版本字典

const currentVersionDiffStats = ref({projectVersion: {}});  // 基准版本模型
const compareVersionDiffStats = ref({projectVersion: {}});  // 对比版本模型

const diffResult = ref({
  current: [],
  compare: [],
});

// 参与对比的建模数量指标：字段名 -> 展示名称
const FIELD_CONFIG_MAP = {
  "omTargetCount": {"name": "建模目标"},
  "omTargetCoveredRate": {"name": "建模目标覆盖率"},
  "termCount": {"name": "术语"},
  "enumerationCount": {"name": "枚举"},
  "objectTypeCount": {"name": "对象类型"},
  "objectTypeAttributeCount": {"name": "对象类型属性"},
  "relationCount": {"name": "对象类型关系"},
  "modelAxiomCount": {"name": "模型公理"},
  "dataRuleCount": {"name": "数据规则"},
};

/**
 * 切换基准版本，拉取该版本统计并刷新差异计算。
 * @param {string} val 选中的基准版本编码
 * @returns {Promise<void>}
 */
async function currentVersionChange(val) {
  loading.value = true;
  currentVersion.value = val;
  const res = await getVersionDiffStats(currentVersion.value);
  currentVersionDiffStats.value = res.data || {projectVersion: {}};
  diffData();
  loading.value = false;
}

/**
 * 切换对比版本，拉取该版本统计并刷新差异计算。
 * @param {string} val 选中的对比版本编码
 * @returns {Promise<void>}
 */
async function compareVersionChange(val) {
  loading.value = true;
  compareVersion.value = val;
  const res = await getVersionDiffStats(compareVersion.value);
  compareVersionDiffStats.value = res.data || {projectVersion: {}};
  diffData();
  loading.value = false;
}

/**
 * 初始化页面数据：读取路由参数中的版本、加载项目版本字典并拉取两个版本的统计。
 * @returns {Promise<void>}
 */
async function initData() {
  if (route.path === '/basic/project/detail/versionDiff') {
    currentVersion.value = route.query.currentVersion || "";
  } else {
    currentVersion.value = route.params.versionId || "";
  }

  compareVersion.value = route.query.compareVersion || "";
  getProjectVersionDict(projectId).then((res) => {
    projectVersionDict.value = res.data;
  });

  const tasks = [];
  if (currentVersion.value) {
    tasks.push(getVersionDiffStats(currentVersion.value).then((res) => {
      currentVersionDiffStats.value = res.data || {projectVersion: {}};
    }));
  }
  if (compareVersion.value) {
    tasks.push(getVersionDiffStats(compareVersion.value).then((res) => {
      compareVersionDiffStats.value = res.data || {projectVersion: {}};
    }));
  }
  await Promise.all(tasks);

  diffData();
  loading.value = false;
}

/**
 * 表格行样式：差异行返回 row-yellow 类以高亮标记。
 * @param {{ row: Object }} data el-table row-class-name 回调参数
 * @returns {string} 行类名，无差异时为空字符串
 */
function tableRowClassName({ row }) {
  return row.changeType;
}

/**
 * 计算两个版本各数量指标的差异行数据（含差异标色与对比版本趋势）。
 * @returns {void}
 */
function diffData() {
  const a = currentVersionDiffStats.value || {};
  const b = compareVersionDiffStats.value || {};

  const current = [];
  const compare = [];

  for (const [field, fieldConfig] of Object.entries(FIELD_CONFIG_MAP)) {
    const valA = a[field] ?? "";
    const valB = b[field] ?? "";
    // 两边都没值：按 0 展示，不标色
    if (!valA && !valB) {
      current.push({field, fieldValue: 0, changeType: "", ...fieldConfig});
      compare.push({field, fieldValue: 0, changeType: "", trend: "", ...fieldConfig});
      continue;
    }
    // 值不一致（含单边没值）标黄，一致则不标色
    const changeType = JSON.stringify(valA) === JSON.stringify(valB) ? "" : "row-yellow";
    // 对比版本数量相对基准版本数量的趋势：高于=up（绿向上箭头），低于=down（红向下箭头）
    const numA = valA === "" ? 0 : Number(valA);
    const numB = valB === "" ? 0 : Number(valB);
    let trend = "";
    if (numB > numA) {
      trend = "up";
    } else if (numB < numA) {
      trend = "down";
    }
    current.push({field, fieldValue: valA, changeType, ...fieldConfig});
    compare.push({field, fieldValue: valB, changeType, trend, ...fieldConfig});
  }
  diffResult.value = {current, compare};
}

onMounted(() => {
  initData();
});
</script>

<style lang="scss" scoped>
.border-item {
  width: 100%;
  padding: 0 16px;
  margin-bottom: 0;
  background: var(--qt-surface-background);
  border-radius: var(--qt-radius-small);

  .border-item-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 50px;
    margin: 0 -16px;
    padding: 0 16px;
    border-bottom: 1px solid var(--qt-border-color);

    .head-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }

  .border-item-body {
    padding: 12px 0 0;
    background-color: var(--qt-surface-background);
  }
}

.version-select {
  width: 200px;
}

.version-info {
  display: flex;
  flex-wrap: wrap;
  flex-shrink: 0;
  gap: 15px;
  overflow: auto;
  width: 100%;
  margin-bottom: 15px;

  .border-item {
    flex: 1;
    min-width: 480px;

    &:first-child {
      padding-right: 15px;
    }

    &:last-child {
      padding-left: 15px;
    }
  }
}

// 版本信息卡片内容区：固定高度，避免切换版本时布局跳动
.version-info-body {
  height: 100px;
}

// 创建人/创建时间行与版本描述行之间留 2px 分隔
.version-info-row {
  margin-bottom: 2px;
}

// 版本描述单行省略：切断 nowrap 文本 min-content 向上传播，否则长描述会把卡片撑出屏幕
.infotop-row-value {
  min-width: 0;
  overflow: hidden;
}

// 全局固定 40px 行高 + 上下 padding 共 20px，15px 字号的文字会被上下裁切，加高到 44px
.infotop-row-lable,
.infotop-row-value {
  height: 44px;
}

// 版本差异明细：纵向撑满剩余视口
.diff-section {
  flex: 1;
  min-height: 320px;

  .border-item {
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .border-item-body {
    display: flex;
    flex: 1;
    min-height: 0;
  }
}

.version-compare {
  display: flex;
  overflow: hidden;
  width: 100%;

  .table-wrap {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-width: 480px;
    padding: 0 16px;
    overflow: hidden;
  }
}

// 表格撑满卡片剩余高度；行数不足时行高均分
.diff-section :deep(.el-table) {
  flex: 1;
  height: 100%;
  min-height: 0;
}

.diff-section :deep(.el-scrollbar),
.diff-section :deep(.el-scrollbar__wrap),
.diff-section :deep(.el-scrollbar__view),
.diff-section :deep(.el-table__body) {
  height: 100%;
}

// 差异行高亮（row-class-name 挂在 el-table 内部 tr 上，需 :deep 穿透）
:deep(.el-table .row-yellow) {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

// 对比版本数量趋势箭头（语义色对齐全局状态变量）
.trend-arrow {
  margin-left: 4px;
  font-size: 12px;
  font-style: normal;
  vertical-align: middle;
}

.trend-up {
  color: var(--qt-status-success);
}

.trend-down {
  color: var(--qt-status-danger);
}
</style>
