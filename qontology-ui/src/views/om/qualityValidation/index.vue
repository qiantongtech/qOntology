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
            <svg-icon icon-class="AI辅助建模" class="ai-icon" />
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
  severe: { label: "严重", tagType: "danger", color: "#F56C6C" },
  moderate: { label: "一般", tagType: "warning", color: "#E6A23C" },
  minor: { label: "提示", tagType: "info", color: "#909399" },
};

// 严重程度字典（dict-tag 组件使用）
const severityOptions = [
  { value: "severe", label: "严重", elTagType: "danger", elTagClass: "" },
  { value: "moderate", label: "一般", elTagType: "warning", elTagClass: "" },
  { value: "minor", label: "提示", elTagType: "info", elTagClass: "" },
];

const metrics = [
  { label: "验证项总数", value: "16", unit: "项", color: "#303133" },
  { label: "验证通过率", value: "75", unit: "%", color: "#E6A23C" },
  {
    label: "发现问题",
    value: "4",
    unit: "个",
    color: "#F56C6C",
    sub: "严重 2 · 一般 2",
  },
  { label: "目标覆盖率", value: "100", unit: "%", color: "#67C23A" },
];

const aiSummary =
  "本次 AI 质量验证共检查 16 项验证项与 245 个建模目标，发现 4 类问题（严重 2 类、一般 2 类），建模目标覆盖率 100%。模型结构存在等价冲突风险，建议优先处理；枚举值与格式约束问题可前往对应模块快速修复。整体建模质量一般，修复严重问题后评分预计可提升至 90 分以上。";

function scoreColor(score) {
  if (score >= 90) return "#67C23A";
  if (score >= 75) return "#409EFF";
  if (score >= 60) return "#E6A23C";
  return "#F56C6C";
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
  "#67C23A": ["#B3E19D", "#67C23A"],
  "#409EFF": ["#A0CFFF", "#409EFF"],
  "#E6A23C": ["#F3D19E", "#E6A23C"],
  "#F56C6C": ["#FAB6B6", "#F56C6C"],
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
          color: "#909399",
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
.quality-validation-page {
  min-width: 1120px;
  font-family: PingFang SC;
  font-size: 14px;
}

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
  color: rgba(0, 0, 0, 0.85);
  font-family: PingFang SC;
  font-weight: 500;
}

.header-actions {
  display: flex;
  flex-shrink: 0;
}

.overview-panel {
  margin-top: 16px;
  padding: 16px 20px;
  border: 1px solid var(--el-border-color);
  background: var(--el-bg-color);
}

.overview-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.overview-title {
  display: flex;
  align-items: center;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
}

.verify-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
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
  gap: 12px;
}

.metric-card {
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-fill-color-light);
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  line-height: 28px;
}

.metric-unit {
  margin-left: 2px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
}

.metric-label {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.metric-sub {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.ai-summary {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-top: 14px;
  padding: 10px 14px;
  border: 1px solid rgba(64, 158, 255, 0.18);
  border-radius: 4px;
  background: linear-gradient(
    90deg,
    rgba(64, 158, 255, 0.06),
    rgba(125, 91, 255, 0.08)
  );
}

.ai-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  color: #5a54d1;
  font-size: 13px;
  font-weight: 600;
  line-height: 20px;
}

.ai-icon {
  font-size: 16px;
}

.ai-text {
  margin: 0;
  color: var(--el-text-color-regular);
  font-size: 12px;
  line-height: 20px;
}

.validation-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.validation-panel {
  min-width: 0;
  padding: 16px;
  border: 1px solid var(--el-border-color);
  background: var(--el-bg-color);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-family: PingFang SC;
  font-weight: 500;
}

.panel-title {
  display: flex;
  align-items: center;
}

.panel-score {
  flex-shrink: 0;
}

.panel-score :deep(.el-progress__text) {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-left: -3px;
}

.panel-index {
  color: var(--el-text-color-regular);
}

.panel-subhead {
  display: flex;
  align-items: center;
  gap: 0;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  font-family: PingFang SC;
  font-weight: 500;
  margin: 14px 0 10px;
}

.panel-subhead .block-marker {
  margin-right: 6px;
}

.panel-subhead .sub-count {
  margin-left: 8px;
  padding: 0 7px;
  border-radius: 9px;
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
  font-size: 12px;
  line-height: 18px;
}

.sub-count.normal {
  background: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.block-marker {
  width: 6px;
  height: 16px;
  flex: 0 0 6px;
  margin-right: 8px;
  border-radius: 3px;
  background: var(--el-color-primary);
}

.summary-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 36px;
  padding: 8px 10px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-regular);
  font-size: 12px;
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
  color: var(--el-color-success);
}

.danger-text {
  color: var(--el-color-danger);
}

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
  max-height: 490px;
}

.issue-list::-webkit-scrollbar,
.constraint-list::-webkit-scrollbar {
  width: 6px;
}

.issue-list::-webkit-scrollbar-thumb,
.constraint-list::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: var(--el-border-color-dark);
}

.issue-list::-webkit-scrollbar-track,
.constraint-list::-webkit-scrollbar-track {
  background: transparent;
}

.issue-card,
.constraint-card,
.target-item {
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

.issue-card.sev-severe,
.constraint-card.sev-severe {
  border-left: 3px solid var(--el-color-danger);
}

.issue-card.sev-moderate,
.constraint-card.sev-moderate {
  border-left: 3px solid var(--el-color-warning);
}

.issue-card.sev-minor,
.constraint-card.sev-minor {
  border-left: 3px solid var(--el-color-info);
}

.issue-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 36px;
  padding: 7px 10px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 14px;
  font-weight: 600;
}

.issue-head > span:first-child {
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 0;
}

.issue-category {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
}

.issue-card p,
.constraint-card p {
  margin: 0;
  padding: 9px 10px;
  color: var(--el-text-color-regular);
  font-size: 12px;
  line-height: 20px;
}

.issue-foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 10px 8px;
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

.target-item {
  padding: 9px 12px;
}

.target-item-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.target-item-head .el-icon {
  color: var(--el-color-success);
  flex-shrink: 0;
}

.target-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}

.target-item-stats {
  margin-top: 6px;
  padding-left: 21px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 18px;
}

.constraint-source {
  color: var(--el-text-color-secondary);
  font-size: 12px;
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
