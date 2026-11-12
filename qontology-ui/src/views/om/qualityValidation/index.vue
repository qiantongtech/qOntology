<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

 qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 See the LICENSE file in the project root for full license information.
-->

<template>
  <div ref="pageRef" class="app-container quality-validation-page">
    <GuideTip tip-id="om/qualityValidation.list" />
    <div class="pagecont-bottom">
      <div class="section-header">
        <div class="section-title">
          <span class="block-marker" />
          <span>质量验证</span>
        </div>
        <div class="header-actions btn-style">
          <el-button type="primary" @click="showDeveloping">
            <el-icon><VideoPlay /></el-icon>
            开始验证
          </el-button>
          <el-button type="primary" plain @click="showDeveloping">
            <el-icon><Download /></el-icon>
            导出验证报告
          </el-button>
        </div>
      </div>

      <section class="overview-panel">
        <div class="overview-head">
          <div class="overview-title">
            <!-- <span class="block-marker" />
            <span>质量总览</span> -->
          </div>
          <span class="verify-time">上次验证时间：{{ lastVerifyTime }}</span>
        </div>
        <div class="overview-main">
          <div class="gauge-wrap">
            <div ref="gaugeRef" class="gauge-chart"></div>
            <el-tag :type="overallRating.tagType" effect="light" round>
              质量评级：{{ overallRating.label }}
            </el-tag>
          </div>
          <div class="metric-grid">
            <div
              v-for="metric in metrics"
              :key="metric.label"
              class="metric-card"
            >
              <div class="metric-value" :style="{ color: metric.color }">
                {{ metric.value
                }}<span class="metric-unit">{{ metric.unit }}</span>
              </div>
              <div class="metric-label">{{ metric.label }}</div>
              <div v-if="metric.sub" class="metric-sub">{{ metric.sub }}</div>
            </div>
          </div>
        </div>
        <div class="ai-summary">
          <div class="ai-badge">
            <svg-icon icon-class="ai-assisted-modeling" class="ai-icon" />
            <span>AI 综合结论</span>
          </div>
          <p class="ai-text">{{ aiSummary }}</p>
        </div>
      </section>

      <div class="validation-grid">
        <section class="validation-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-index">一、</span>
              <span>模型结构数据自检</span>
            </div>
            <el-progress
              class="panel-score"
              type="circle"
              :width="44"
              :stroke-width="4"
              :percentage="structureScore"
              :color="scoreColor(structureScore)"
              :format="(p) => String(p)"
            />
          </div>
          <div class="panel-subhead">
            <span class="block-marker" />
            <span>验证结果</span>
          </div>
          <div class="summary-line">
            <span>验证项 12 项，发现问题 <b class="danger-text">2</b> 个</span>
            <dict-tag :options="qualityStatusOptions" value="abnormal" />
          </div>
          <el-table
            :data="structureRows"
            border
            size="small"
            class="summary-table"
          >
            <el-table-column
              prop="name"
              label="验证项"
              min-width="116"
              :show-overflow-tooltip="tooltipOptions"
            />
            <el-table-column label="结果" width="112" align="center">
              <template #default="{ row }">
                <dict-tag
                  :options="validationResultOptions"
                  :value="row.issueCount ? 'issue' : 'passed'"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="issueCount"
              label="问题数量"
              width="88"
              align="center"
            >
              <template #default="{ row }">
                <span :class="{ 'danger-text': row.issueCount }">{{
                  row.issueCount
                }}</span>
              </template>
            </el-table-column>
          </el-table>

          <div class="panel-subhead">
            <span class="block-marker" />
            <span>问题明细</span>
            <span class="sub-count">{{ structureIssues.length }}</span>
          </div>
          <div class="issue-list">
            <article
              v-for="(item, index) in structureIssues"
              :key="`${item.title}-${index}`"
              :class="['issue-card', `sev-${item.severity}`]"
            >
              <div class="issue-head">
                <span
                  ><el-icon :color="severityMap[item.severity].color"
                    ><InfoFilled /></el-icon
                  >{{ item.title }}
                  <span class="issue-category">{{ item.category }}</span></span
                >
                <dict-tag :options="severityOptions" :value="item.severity" />
              </div>
              <el-tooltip
                :content="item.description"
                effect="light"
                placement="top-start"
                :disabled="!overflowState[`structure-${index}`]"
              >
                <p class="two-line-text" :data-tip-key="`structure-${index}`">
                  {{ item.description }}
                </p>
              </el-tooltip>
              <div class="issue-foot">
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="goHandle(item)"
                >
                  去处理<el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </article>
          </div>
        </section>

        <section class="validation-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-index">二、</span>
              <span>建模目标检查</span>
            </div>
            <el-progress
              class="panel-score"
              type="circle"
              :width="44"
              :stroke-width="4"
              :percentage="targetScore"
              :color="scoreColor(targetScore)"
              :format="(p) => String(p)"
            />
          </div>
          <div class="panel-subhead">
            <span class="block-marker" />
            <span>验证结果</span>
          </div>
          <div class="summary-line">
            <span>建模目标 245 个，已覆盖 245 个，覆盖率 100%</span>
            <dict-tag :options="qualityStatusOptions" value="normal" />
          </div>
          <div class="panel-subhead">
            <span class="block-marker" />
            <span>目标覆盖明细</span>
            <span class="sub-count normal">{{ targetChecks.length }}</span>
          </div>
          <div class="target-list">
            <article
              v-for="item in targetChecks"
              :key="item.title"
              class="target-item"
            >
              <div class="target-item-head">
                <el-icon><CircleCheckFilled /></el-icon>
                <span class="target-name">{{ item.title }}</span>
                <dict-tag :options="om_accept_status" value="1" />
              </div>
              <div class="target-item-stats">{{ item.stats }}</div>
            </article>
          </div>
        </section>

        <section class="validation-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-index">三、</span>
              <span>约束数据检查</span>
            </div>
            <el-progress
              class="panel-score"
              type="circle"
              :width="44"
              :stroke-width="4"
              :percentage="constraintScore"
              :color="scoreColor(constraintScore)"
              :format="(p) => String(p)"
            />
          </div>
          <div class="panel-subhead">
            <span class="block-marker" />
            <span>验证结果</span>
          </div>
          <div class="summary-line">
            <span>验证项 4 项，发现问题 <b class="danger-text">2</b> 个</span>
            <dict-tag :options="qualityStatusOptions" value="abnormal" />
          </div>
          <el-table
            :data="constraintRows"
            border
            size="small"
            class="summary-table"
          >
            <el-table-column
              prop="name"
              label="验证项"
              min-width="116"
              :show-overflow-tooltip="tooltipOptions"
            />
            <el-table-column label="结果" width="112" align="center">
              <template #default="{ row }">
                <dict-tag
                  :options="validationResultOptions"
                  :value="row.issueCount ? 'issue' : 'passed'"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="issueCount"
              label="问题数量"
              width="88"
              align="center"
            >
              <template #default="{ row }">
                <span :class="{ 'danger-text': row.issueCount }">{{
                  row.issueCount
                }}</span>
              </template>
            </el-table-column>
          </el-table>

          <div class="panel-subhead">
            <span class="block-marker" />
            <span>问题明细</span>
            <span class="sub-count">{{ constraintChecks.length }}</span>
          </div>
          <div class="constraint-list">
            <article
              v-for="(item, index) in constraintChecks"
              :key="`${item.title}-${index}`"
              :class="['constraint-card', `sev-${item.severity}`]"
            >
              <div class="issue-head">
                <span
                  ><el-icon :color="severityMap[item.severity].color"
                    ><InfoFilled /></el-icon
                  >{{ item.title }}</span
                >
                <dict-tag :options="severityOptions" :value="item.severity" />
              </div>
              <el-tooltip
                :content="item.description"
                effect="light"
                placement="top-start"
                :disabled="!overflowState[`constraint-${index}`]"
              >
                <p class="two-line-text" :data-tip-key="`constraint-${index}`">
                  {{ item.description }}
                </p>
              </el-tooltip>
              <div class="issue-foot">
                <span v-if="item.source" class="constraint-source"
                  >来源：{{ item.source }}</span
                >
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="goHandle(item)"
                >
                  去处理<el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </article>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  getCurrentInstance,
  nextTick,
  onBeforeUnmount,
  onMounted,
  onUpdated,
  ref,
} from "vue";
import { useRoute, useRouter } from "vue-router";
import * as echarts from "echarts";
import {
  ArrowRight,
  Download,
  InfoFilled,
  VideoPlay,
} from "@element-plus/icons-vue";

defineOptions({ name: "OmQualityValidation" });

/* 语义颜色统一读取系统变量，与 variables.scss 中的定义保持一致。 */
function readToken(name, fallback) {
  const value = getComputedStyle(document.documentElement)
    .getPropertyValue(name)
    .trim();
  return value || fallback;
}

const statusColors = {
  success: readToken("--qt-status-success", "#67C23A"),
  warning: readToken("--qt-status-warning", "#E6A23C"),
  danger: readToken("--qt-status-danger", "#F56C6C"),
  info: readToken("--qt-status-info", "#909399"),
};
const textPrimary = readToken("--qt-element-fixed-primary", "#303133");
const textSecondary = readToken("--qt-text-secondary", "#909399");
const primaryColor = readToken("--el-color-primary", "#2666FB");

const { proxy } = getCurrentInstance();
const { om_accept_status } = proxy.useDict("om_accept_status");
const route = useRoute();
const router = useRouter();
const tooltipOptions = { effect: "light", placement: "top-start" };
const qualityStatusOptions = [
  { value: "normal", label: "正常", elTagType: "success", elTagClass: "" },
  { value: "abnormal", label: "异常", elTagType: "warning", elTagClass: "" },
];
const validationResultOptions = [
  { value: "passed", label: "已通过", elTagType: "success", elTagClass: "" },
  { value: "issue", label: "存在问题", elTagType: "danger", elTagClass: "" },
];

// 质量评分：>=90 优秀，>=75 良好，>=60 一般，其余较差
const overallScore = 78;
const structureScore = 83;
const targetScore = 100;
const constraintScore = 50;
const lastVerifyTime = "2026-10-26 10:26";

const severityMap = {
  severe: { label: "严重", tagType: "danger", color: statusColors.danger },
  moderate: { label: "一般", tagType: "warning", color: statusColors.warning },
  minor: { label: "提示", tagType: "info", color: statusColors.info },
};

// 严重程度字典（dict-tag 组件使用）
const severityOptions = [
  { value: "severe", label: "严重", elTagType: "danger", elTagClass: "" },
  { value: "moderate", label: "一般", elTagType: "warning", elTagClass: "" },
  { value: "minor", label: "提示", elTagType: "info", elTagClass: "" },
];

const metrics = [
  { label: "验证项总数", value: "16", unit: "项", color: textPrimary },
  { label: "验证通过率", value: "75", unit: "%", color: statusColors.warning },
  {
    label: "发现问题",
    value: "4",
    unit: "个",
    color: statusColors.danger,
    sub: "严重 2 · 一般 2",
  },
  { label: "目标覆盖率", value: "100", unit: "%", color: statusColors.success },
];

const aiSummary =
  "本次 AI 质量验证共检查 16 项验证项与 245 个建模目标，发现 4 类问题（严重 2 类、一般 2 类），建模目标覆盖率 100%。模型结构存在等价冲突风险，建议优先处理；枚举值与格式约束问题可前往对应模块快速修复。整体建模质量一般，修复严重问题后评分预计可提升至 90 分以上。";

function scoreColor(score) {
  if (score >= 90) return statusColors.success;
  if (score >= 75) return primaryColor;
  if (score >= 60) return statusColors.warning;
  return statusColors.danger;
}

function ratingOf(score) {
  if (score >= 90) return { label: "优秀", tagType: "success" };
  if (score >= 75) return { label: "良好", tagType: "primary" };
  if (score >= 60) return { label: "一般", tagType: "warning" };
  return { label: "较差", tagType: "danger" };
}

const overallRating = ratingOf(overallScore);

function goHandle(item) {
  // if (!item.route) return;
  // router.push(`/om/${route.params.versionId}/${item.route}`);
  proxy.$modal.msgWarning("功能开发中！");
}
const pageRef = ref();
const overflowState = ref({});

function updateOverflowState() {
  nextTick(() => {
    pageRef.value?.querySelectorAll(".two-line-text").forEach((element) => {
      const range = document.createRange();
      range.selectNodeContents(element);
      const lineCount = range.getClientRects().length;
      const isOverflowing =
        element.scrollHeight > element.clientHeight + 1 || lineCount > 2;
      const key = element.dataset.tipKey;
      overflowState.value[key] = Boolean(
        element.textContent?.trim() && isOverflowing
      );
    });
  });
}

const gaugeRef = ref();
let gaugeChart = null;

const scoreGradients = {
  [statusColors.success]: [
    readToken("--el-color-success-light-3", "#B3E19D"),
    statusColors.success,
  ],
  [primaryColor]: [
    readToken("--el-color-primary-light-3", "#A0CFFF"),
    primaryColor,
  ],
  [statusColors.warning]: [
    readToken("--el-color-warning-light-3", "#F3D19E"),
    statusColors.warning,
  ],
  [statusColors.danger]: [
    readToken("--el-color-danger-light-3", "#FAB6B6"),
    statusColors.danger,
  ],
};

function initGauge() {
  if (!gaugeRef.value) return;
  gaugeChart = echarts.init(gaugeRef.value);
  const color = scoreColor(overallScore);
  const [gradientStart, gradientEnd] = scoreGradients[color];
  gaugeChart.setOption({
    series: [
      {
        type: "gauge",
        radius: "100%",
        startAngle: 210,
        endAngle: -30,
        min: 0,
        max: 100,
        progress: {
          show: true,
          width: 12,
          roundCap: true,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: gradientStart },
              { offset: 1, color: gradientEnd },
            ]),
          },
        },
        axisLine: {
          lineStyle: { width: 12, color: [[1, "rgba(0, 0, 0, 0.06)"]] },
        },
        pointer: { show: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        title: {
          offsetCenter: [0, "32%"],
          fontSize: 12,
          color: textSecondary,
          fontFamily: "PingFang SC",
        },
        detail: {
          valueAnimation: true,
          offsetCenter: [0, "-12%"],
          fontSize: 34,
          fontWeight: 600,
          fontFamily: "PingFang SC",
          color,
          formatter: "{value}",
        },
        data: [{ value: overallScore, name: "综合质量评分" }],
      },
    ],
  });
}

function resizeGauge() {
  if (gaugeChart && !gaugeChart.isDisposed()) gaugeChart.resize();
}

const overflowObserver =
  typeof ResizeObserver === "undefined"
    ? null
    : new ResizeObserver(updateOverflowState);
onMounted(() => {
  updateOverflowState();
  initGauge();
  window.addEventListener("resize", resizeGauge);
  pageRef.value
    ?.querySelectorAll(".two-line-text")
    .forEach((element) => overflowObserver?.observe(element));
});
onUpdated(updateOverflowState);
onBeforeUnmount(() => {
  overflowObserver?.disconnect();
  window.removeEventListener("resize", resizeGauge);
  gaugeChart?.dispose();
  gaugeChart = null;
});

const structureRows = [
  { name: "Domain / Range 检查", issueCount: 0 },
  { name: "等价冲突", issueCount: 2 },
  { name: "子类型检查", issueCount: 0 },
];

const structureIssues = [
  {
    title: "Domain / Range 检查",
    category: "对象类型关系",
    severity: "moderate",
    description:
      "“购买”关系的公理约束结果为“用户”、终点“商品”，但关系类型定义中配置为起点“商品”、终点“用户”。",
    route: "ontology/objectRelation",
  },
  {
    title: "子类型检查",
    category: "对象类型",
    severity: "moderate",
    description:
      "“购买”关系的公理约束结果为“用户”、终点“商品”，但关系类型定义中配置为起点“商品”、终点“用户”。",
    route: "ontology/objectType",
  },
  {
    title: "等价冲突",
    category: "对象类型",
    severity: "severe",
    description:
      "“商品”与“商品备件”被声明为等价，但两者同时存在互斥公理，导致数据语义不同时成立。",
    route: "ontology/objectType",
  },
  {
    title: "子类型检查",
    category: "对象类型",
    severity: "moderate",
    description:
      "“购买”关系的公理约束结果为“用户”、终点“商品”，但关系类型定义中配置为起点“商品”、终点“用户”。",
    route: "ontology/objectType",
  },
];

const targetChecks = [
  {
    title: "那些供应商提供物料布料？",
    stats: "对象类型 51 · 对象类型属性 51 · 对象类型关系 51",
  },
  {
    title: "那些供应商提供物料布料？",
    stats: "对象类型 51 · 对象类型属性 51 · 对象类型关系 51",
  },
  {
    title: "那些供应商提供物料布料？",
    stats: "对象类型 51 · 对象类型属性 51 · 对象类型关系 51",
  },
  {
    title: "那些供应商提供物料布料？",
    stats: "对象类型 51 · 对象类型属性 51 · 对象类型关系 51",
  },
];

const constraintRows = [
  { name: "类型约束检查", issueCount: 0 },
  { name: "范围与枚举检查", issueCount: 2 },
  { name: "格式约束检查", issueCount: 0 },
];

const constraintChecks = [
  {
    title: "类型约束检查",
    description: "本体模型定义的“金额”为整数，但数据样例中存在小数。",
    source: "系统（对象类型）—金额（属性）",
    severity: "severe",
    route: "ontology/constraint/data",
  },
  {
    title: "枚举值检查",
    description:
      "本体模型定义的“商品状态”的枚举值为上架、下架，但数据样例中存在预售中。",
    source: "系统（对象类型）—商品状态（属性）",
    severity: "moderate",
    route: "semantic/enum",
  },
  {
    title: "格式约束检查",
    description:
      "本体模型定义的“日期”格式为 YYYY-MM-DD，但数据样例中的日期是 2023/10/01。",
    source: "系统（对象类型）—日期（属性）",
    severity: "moderate",
    route: "ontology/constraint/data",
  },
  {
    title: "枚举值检查",
    description:
      "本体模型定义的“商品状态”的枚举值为上架、下架，但数据样例中存在预售中。",
    source: "系统（对象类型）—商品状态（属性）",
    severity: "moderate",
    route: "semantic/enum",
  },
];

function showDeveloping() {
  proxy.$modal.msgWarning("功能开发中！");
}
</script>

<style scoped lang="scss">
/*
 * 设计 Token 对齐首页 (system/index.vue)
 * 字号体系：30px(Sharp数字) → 16px(板块标题/500~600) → 14px(正文/辅助/描述)
 * 颜色：重点 var(--qt-text-primary) | 正文 var(--qt-text-regular) | 辅助 var(--qt-text-secondary);
 * 分隔线：板块 #e8e8e8 | 卡片内 #e8e8e8
 * 圆角：统一 2px
 * 主色：var(--el-color-primary)
 */
.quality-validation-page {
  min-width: 1120px;
  font-family: PingFang SC;
  font-size: 14px;
  background: #f0f2f5;
}

/* ---- 顶部 section-header ---- */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0;
  font-size: 16px;
  color: var(--qt-text-primary);
  font-family: PingFang SC;
  font-weight: 500;
  line-height: 22px;
}

.header-actions {
  display: flex;
  flex-shrink: 0;
}

/* ---- 质量总览面板 ---- */
.overview-panel {
  margin-top: 16px;
  padding: 16px 20px;
  border-radius: 2px;
  background: #fff;
}

.overview-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.overview-title {
  display: flex;
  align-items: center;
  color: var(--qt-text-primary);
  font-size: 16px;
  font-weight: 500;
  line-height: 22px;
}

.verify-time {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}

.overview-main {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 8px;
}

.gauge-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  width: 200px;
  flex-shrink: 0;
}

.gauge-chart {
  width: 200px;
  height: 130px;
}

.metric-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
}

.metric-card {
  padding: 14px 16px;
  border-radius: 2px;
  background: #fafbfc;
  border: 1px solid #e8e8e8;
  box-sizing: border-box;
}

.metric-value {
  font-family: Sharp;
  font-size: 30px;
  font-weight: 500;
  line-height: 38px;
}

.metric-unit {
  margin-left: 2px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
}

.metric-label {
  margin-top: 4px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}

.metric-sub {
  margin-top: 4px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}

/* ---- AI 结论 ---- */
.ai-summary {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-top: 14px;
  padding: 10px 14px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  background: #fafbfc;
}

.ai-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  color: var(--el-color-primary);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}

.ai-icon {
  font-size: 16px;
}

.ai-text {
  margin: 0;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}

/* ---- 三列验证面板 ---- */
.validation-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 15px;
  margin-top: 16px;
}

.validation-panel {
  min-width: 0;
  padding: 16px;
  border-radius: 2px;
  background: #fff;
  border: 1px solid #e8e8e8;
  box-sizing: border-box;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
  color: var(--qt-text-primary);
  font-size: 16px;
  font-family: PingFang SC;
  font-weight: 500;
  line-height: 22px;
}

.panel-title {
  display: flex;
  align-items: center;
}

.panel-score {
  flex-shrink: 0;
}

.panel-score :deep(.el-progress__text) {
  font-size: 14px;
  font-weight: 500;
  color: var(--qt-text-primary);
  margin-left: -3px;
}

.panel-index {
  color: var(--qt-text-regular);
}

.panel-subhead {
  display: flex;
  align-items: center;
  gap: 0;
  font-size: 16px;
  color: var(--qt-text-primary);
  font-family: PingFang SC;
  font-weight: 500;
  line-height: 22px;
  margin: 14px 0 10px;
}

.panel-subhead .block-marker {
  margin-right: 8px;
}

.panel-subhead .sub-count {
  margin-left: 8px;
  padding: 0 7px;
  border-radius: 2px;
  background: #fff0f2;
  color: var(--qt-status-danger);
  font-size: 14px;
  line-height: 20px;
}

.sub-count.normal {
  background: #ebf9ee;
  color: var(--qt-status-success);
}

.block-marker {
  width: 6px;
  height: 16px;
  flex: 0 0 6px;
  margin-right: 8px;
  border-radius: 3px;
  background: var(--el-color-primary);
}

/* ---- 验证结果 summary ---- */
.summary-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 36px;
  padding: 8px 10px;
  background: #fafbfc;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}

.summary-table {
  width: 100%;
  margin-top: 8px;
}

.success-text,
.danger-text {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.success-text {
  color: var(--qt-status-success);
}
.danger-text {
  color: var(--qt-status-danger);
}

/* ---- 问题列表 ---- */
.issue-list,
.target-list,
.constraint-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.issue-list,
.constraint-list {
  overflow-y: auto;
  padding-right: 2px;
}

.issue-list {
  max-height: 430px;
}
.constraint-list {
  max-height: 430px;
}

.issue-list::-webkit-scrollbar,
.constraint-list::-webkit-scrollbar {
  width: 6px;
}

.issue-list::-webkit-scrollbar-thumb,
.constraint-list::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: #c0c4cc;
}

.issue-list::-webkit-scrollbar-track,
.constraint-list::-webkit-scrollbar-track {
  background: transparent;
}

.issue-card,
.constraint-card,
.target-item {
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
}

.issue-card.sev-severe,
.constraint-card.sev-severe {
  border-left: 3px solid var(--qt-status-danger);
}

.issue-card.sev-moderate,
.constraint-card.sev-moderate {
  border-left: 3px solid var(--qt-status-warning);
}

.issue-card.sev-minor,
.constraint-card.sev-minor {
  border-left: 3px solid var(--qt-status-info);
}

.issue-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 36px;
  padding: 7px 10px;
  border-bottom: 1px solid #e8e8e8;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: var(--qt-text-primary);
}

.issue-head > span:first-child {
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 0;
}

.issue-category {
  color: var(--qt-text-secondary);
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
}

.issue-card p,
.constraint-card p {
  margin: 0;
  padding: 9px 10px;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}

.issue-foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 10px 8px;
  font-size: 14px;
}

.issue-foot .constraint-source {
  margin-top: 0;
  margin-right: auto;
}

.two-line-text {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

/* ---- 目标列表 ---- */
.target-item {
  padding: 9px 12px;
}

.target-item-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  line-height: 22px;
}

.target-item-head .el-icon {
  color: var(--qt-status-success);
  flex-shrink: 0;
}

.target-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
  color: var(--qt-text-primary);
}

.target-item-stats {
  margin-top: 6px;
  padding-left: 21px;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}

.constraint-source {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}

/* ---- 表格表头覆盖 ---- */
.summary-table :deep(.el-table__header th) {
  background: #f1f1f5;
  color: var(--qt-text-primary);
  font-weight: 500;
  font-size: 14px;
  line-height: 22px;
}

.summary-table :deep(.el-table__cell) {
  font-size: 14px;
  line-height: 22px;
  color: var(--qt-text-regular);
}

@media (max-width: 1440px) {
  .validation-grid {
    gap: 12px;
  }
  .validation-panel {
    padding: 12px;
  }
}
</style>
