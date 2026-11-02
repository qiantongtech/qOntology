<template>
  <div class="analysis-dashboard app-container">
    <section class="dashboard-toolbar">
      <div class="page-heading">
        <div class="heading-icon"><DataAnalysis /></div>
        <div>
          <h1>项目本体分析看板</h1>
          <p>基于项目绑定本体，分析对象、关系、规则与实例数据</p>
        </div>
      </div>
      <div class="toolbar-filters">
        <div class="filter-item">
          <el-select v-model="selectedProjectId" @change="handleProjectChange"
            ><el-option
              v-for="item in projectOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
          /></el-select>
        </div>
        <div class="range-toggle">
          <button :class="{ active: period === 7 }" @click="changePeriod(7)">
            近7天</button
          ><button :class="{ active: period === 30 }" @click="changePeriod(30)">
            近30天
          </button>
        </div>
        <el-button :icon="Refresh" size="mini" @click="refreshCharts"
          >刷新</el-button
        >
      </div>
    </section>

    <div class="scope-strip">
      <span
        ><CollectionTag />当前项目：<b>{{ selectedProjectLabel }}</b></span
      ><i></i>
      <span
        ><Connection />项目版本：<b>{{ boundOntologyVersion }}</b></span
      ><i></i>
      <span
        ><DataAnalysis />分析对象集：<b>{{ scopeLabel }}</b></span
      ><i></i>
      <span><CircleCheck />数据更新时间：<b>今天 10:42</b></span>
      <dict-tag class="scope-status" :options="dataStatusOptions" value="1" />
    </div>

    <div class="metric-grid">
      <article
        v-for="card in metricCards"
        :key="card.label"
        class="metric-card"
        :class="{ selected: activeMetric === card.key }"
        @click="activeMetric = card.key"
      >
        <div class="metric-label">{{ card.label }}</div>
        <div class="metric-value">
          {{ card.value }}<small>{{ card.unit }}</small>
        </div>
        <div class="metric-foot">
          <span>{{ card.note }}</span
          ><em :class="{ down: !card.up }"
            >{{ card.trend }} {{ card.up ? "▲" : "▼" }}</em
          >
        </div>
        <div
          class="metric-badge"
          :style="{
            background: `linear-gradient(135deg, ${card.color}, ${card.color}b3)`,
          }"
        >
          <component :is="card.icon" />
        </div>
      </article>
    </div>

    <div class="primary-grid">
      <section class="dashboard-section trend-section">
        <div class="section-toolbar">
          <div class="section-header">
            <span class="section-marker"></span
            ><span class="section-title">实例数据变化趋势</span
            ><span class="section-subtitle">按当前项目与对象集统计</span>
          </div>
          <div class="legend-summary">
            <span><i class="blue"></i>实例总量</span
            ><span><i class="orange"></i>新增实例</span
            ><span><i class="red"></i>变更实例</span>
          </div>
        </div>
        <div ref="trendRef" class="chart-canvas trend-chart"></div>
      </section>
      <section class="dashboard-section distribution-section">
        <div class="section-header">
          <span class="section-marker"></span
          ><span class="section-title">对象类型实例分布</span>
        </div>
        <div ref="donutRef" class="chart-canvas donut-chart"></div>
        <div class="distribution-foot">
          <span>有实例的对象类型 <b>8/12</b></span
          ><span>待归类实例 <b>3</b></span>
        </div>
      </section>
    </div>

    <div class="secondary-grid">
      <section class="dashboard-section graph-section">
        <div class="section-toolbar">
          <div class="section-header">
            <span class="section-marker"></span
            ><span class="section-title">本体语义关系网络</span
            ><span class="section-subtitle">沿本体关系展开 2 跳</span>
          </div>
          <el-button text type="primary" @click="graphExpanded = !graphExpanded"
            >{{ graphExpanded ? "收起详情" : "查看全图" }} <ArrowRight
          /></el-button>
        </div>
        <div class="graph-body">
          <div ref="graphRef" class="chart-canvas graph-chart"></div>
          <aside class="object-detail" :class="{ expanded: graphExpanded }">
            <div class="object-title">
              <span class="object-icon"><Cpu /></span>
              <div>
                <strong>核心业务对象 OBJ-2048</strong>
                <p>对象类型：核心对象类型</p>
              </div>
            </div>
            <dl>
              <div>
                <dt>数据状态</dt>
                <dd><dict-tag :options="deviceStatusOptions" value="2" /></dd>
              </div>
              <div>
                <dt>所属对象集</dt>
                <dd>核心业务对象集</dd>
              </div>
              <div>
                <dt>规则异常</dt>
                <dd class="danger-text">3 项</dd>
              </div>
              <div>
                <dt>待处理问题</dt>
                <dd>2 项</dd>
              </div>
            </dl>
          </aside>
        </div>
      </section>
      <section class="dashboard-section rule-section">
        <div class="section-header">
          <span class="section-marker"></span
          ><span class="section-title">规则命中排行</span>
        </div>
        <div class="rule-list">
          <div
            v-for="(rule, index) in rules"
            :key="rule.name"
            class="rule-item"
          >
            <span class="rule-rank" :class="`rank-${index + 1}`">{{
              index + 1
            }}</span>
            <div class="rule-main">
              <div>
                <strong>{{ rule.name }}</strong
                ><b>{{ rule.count }} 次</b>
              </div>
              <p>{{ rule.scope }}</p>
              <div class="rule-progress">
                <i :style="{ width: rule.percent + '%' }"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="rule-summary">
          <WarningFilled />
          <div>
            <strong>今日新增 6 条质量问题</strong>
            <p>其中 2 条为高优先级问题</p>
          </div>
          <el-button type="primary" plain size="small">查看问题</el-button>
        </div>
      </section>
    </div>

    <section class="dashboard-section table-section">
      <div class="section-toolbar">
        <div class="section-header">
          <span class="section-marker"></span
          ><span class="section-title">本体数据问题明细</span
          ><span class="section-subtitle">由约束规则与质量校验自动识别</span>
        </div>
        <div class="table-actions">
          <el-input
            v-model="keyword"
            :prefix-icon="Search"
            placeholder="搜索对象名称或标识"
            clearable
          /><el-select v-model="riskFilter"
            ><el-option label="全部风险" value="all" /><el-option
              label="高风险"
              value="high" /><el-option
              label="中风险"
              value="medium" /></el-select
          ><el-button type="primary" size="mini" :icon="Download"
            >导出</el-button
          >
        </div>
      </div>
      <el-table :data="filteredRows" class="dashboard-table" height="250">
        <el-table-column prop="code" label="对象标识" width="130" />
        <el-table-column prop="name" label="对象名称" min-width="160"
          ><template #default="{ row }"
            ><div class="object-cell">
              <span :style="{ background: row.color }"
                ><component :is="row.icon"
              /></span>
              <div>
                <strong>{{ row.name }}</strong
                ><small>{{ row.type }}</small>
              </div>
            </div></template
          ></el-table-column
        >
        <el-table-column prop="line" label="所属业务范围" min-width="130" />
        <el-table-column label="风险等级" width="100" align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="riskStatusOptions"
              :value="row.risk" /></template
        ></el-table-column>
        <el-table-column
          prop="rule"
          label="命中规则"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column prop="time" label="最近发生时间" width="155" />
        <el-table-column label="处理状态" width="100" align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="handleStatusOptions"
              :value="row.status" /></template
        ></el-table-column>
        <el-table-column label="操作" width="110" align="center"
          ><template #default
            ><el-button link type="primary">查看对象</el-button></template
          ></el-table-column
        >
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from "vue";
import * as echarts from "echarts";
import { projectDict } from "@/api/basic/project/project";
import { getProjectVersionDict } from "@/api/basic/project/projectVersion";
import {
  ArrowRight,
  Box,
  CircleCheck,
  CollectionTag,
  Connection,
  Cpu,
  DataAnalysis,
  Download,
  Monitor,
  Refresh,
  Search,
  Setting,
  Tools,
  TrendCharts,
  WarningFilled,
} from "@element-plus/icons-vue";

const selectedProjectId = ref("");
const scope = ref("all");
const period = ref(7);
const activeMetric = ref("instances");
const graphExpanded = ref(false);
const keyword = ref("");
const riskFilter = ref("all");
const trendRef = ref(null);
const donutRef = ref(null);
const graphRef = ref(null);
let trendChart;
let donutChart;
let graphChart;
const fallbackProjects = [
  { dictValue: "demo-project-a", dictLabel: "示例业务项目 A" },
  { dictValue: "demo-project-b", dictLabel: "示例业务项目 B" },
  { dictValue: "demo-project-c", dictLabel: "示例业务项目 C" },
];
const projectOptions = ref([]);
const boundOntologyVersion = ref("未配置版本");
const scopeOptions = [
  { label: "全部对象实例", value: "all" },
  { label: "核心对象集", value: "core" },
  { label: "规则异常对象集", value: "exception" },
  { label: "最近变更对象集", value: "changed" },
];
const scopeLabel = computed(
  () => scopeOptions.find((item) => item.value === scope.value)?.label
);
const selectedProjectLabel = computed(
  () =>
    projectOptions.value.find(
      (item) => String(item.dictValue) === String(selectedProjectId.value)
    )?.dictLabel || "当前项目"
);
async function loadProjects() {
  try {
    const response = await projectDict();
    projectOptions.value =
      Array.isArray(response?.data) && response.data.length
        ? response.data
        : fallbackProjects;
  } catch {
    projectOptions.value = fallbackProjects;
  }
  selectedProjectId.value = projectOptions.value[0]?.dictValue || "";
  await handleProjectChange(selectedProjectId.value);
}
async function handleProjectChange(projectId) {
  if (!projectId || String(projectId).startsWith("demo-")) {
    boundOntologyVersion.value = "项目本体 V1.0.0";
    refreshCharts();
    return;
  }
  try {
    const response = await getProjectVersionDict(projectId);
    boundOntologyVersion.value =
      response?.data?.[0]?.dictLabel || "暂无已发布版本";
  } catch {
    boundOntologyVersion.value = "暂无已发布版本";
  }
  refreshCharts();
}
const dataStatusOptions = [
  { label: "数据正常", value: "1", elTagType: "success", elTagClass: "" },
];
const deviceStatusOptions = [
  { label: "有效", value: "1", elTagType: "success", elTagClass: "" },
  { label: "待校验", value: "2", elTagType: "warning", elTagClass: "" },
];
const riskStatusOptions = [
  { label: "高风险", value: "high", elTagType: "danger", elTagClass: "" },
  { label: "中风险", value: "medium", elTagType: "warning", elTagClass: "" },
  { label: "低风险", value: "low", elTagType: "info", elTagClass: "" },
];
const handleStatusOptions = [
  { label: "待处理", value: "pending", elTagType: "danger", elTagClass: "" },
  { label: "处理中", value: "doing", elTagType: "warning", elTagClass: "" },
  { label: "已处理", value: "done", elTagType: "success", elTagClass: "" },
];
const metricCards = [
  {
    key: "types",
    label: "对象类型",
    value: "128",
    unit: "类",
    note: "较上个版本新增 6 类",
    trend: "1.6%",
    up: true,
    color: "#3478f6",
    icon: Box,
  },
  {
    key: "relations",
    label: "关系类型",
    value: "46",
    unit: "类",
    note: "已使用关系 42 类",
    trend: "0.8%",
    up: true,
    color: "#35bfc3",
    icon: CircleCheck,
  },
  {
    key: "instances",
    label: "对象实例",
    value: "2,486",
    unit: "个",
    note: "较上周新增 38 个",
    trend: "1.6%",
    up: true,
    color: "#f29a45",
    icon: WarningFilled,
  },
  {
    key: "quality",
    label: "规则校验通过率",
    value: "96.8",
    unit: "%",
    note: "待处理问题 37 项",
    trend: "0.8%",
    up: true,
    color: "#7b6cf0",
    icon: Tools,
  },
];
const rules = [
  {
    name: "必填属性完整性校验",
    scope: "全部对象类型 · 必填属性",
    count: 28,
    percent: 92,
  },
  {
    name: "关系基数约束校验",
    scope: "对象关系 · 基数约束",
    count: 21,
    percent: 73,
  },
  {
    name: "唯一标识重复校验",
    scope: "对象实例 · 唯一标识",
    count: 17,
    percent: 58,
  },
  {
    name: "枚举值范围一致性",
    scope: "枚举属性 · 取值范围",
    count: 12,
    percent: 42,
  },
];
const rows = [
  {
    code: "OBJ-2048",
    name: "核心业务对象 #2048",
    type: "核心对象类型",
    line: "核心对象集",
    risk: "high",
    rule: "必填属性完整性校验",
    time: "2026-11-01 10:32",
    status: "pending",
    color: "#3478f6",
    icon: Cpu,
  },
  {
    code: "OBJ-1086",
    name: "关联业务对象 #1086",
    type: "关联对象类型 A",
    line: "核心对象集",
    risk: "medium",
    rule: "关系基数约束校验",
    time: "2026-11-01 10:18",
    status: "doing",
    color: "#7b6cf0",
    icon: Setting,
  },
  {
    code: "OBJ-3021",
    name: "业务对象实例 #3021",
    type: "关联对象类型 B",
    line: "规则异常对象集",
    risk: "high",
    rule: "唯一标识重复校验",
    time: "2026-11-01 09:46",
    status: "pending",
    color: "#ef6b75",
    icon: Monitor,
  },
  {
    code: "OBJ-7712",
    name: "业务对象实例 #7712",
    type: "扩展对象类型",
    line: "最近变更对象集",
    risk: "low",
    rule: "枚举值范围一致性",
    time: "2026-11-01 09:25",
    status: "done",
    color: "#35bfc3",
    icon: TrendCharts,
  },
  {
    code: "OBJ-6203",
    name: "业务对象实例 #6203",
    type: "扩展对象类型",
    line: "最近变更对象集",
    risk: "medium",
    rule: "属性数据类型校验",
    time: "2026-11-01 08:57",
    status: "doing",
    color: "#f29a45",
    icon: DataAnalysis,
  },
];
const filteredRows = computed(() =>
  rows.filter(
    (row) =>
      (riskFilter.value === "all" || row.risk === riskFilter.value) &&
      (!keyword.value || `${row.code}${row.name}`.includes(keyword.value))
  )
);

function changePeriod(value) {
  period.value = value;
  initTrend();
}
function refreshCharts() {
  nextTick(() => {
    initTrend();
    initDonut();
    initGraph();
  });
}
function initTrend() {
  if (!trendRef.value) return;
  trendChart ||= echarts.init(trendRef.value);
  const labels =
    period.value === 7
      ? ["10.26", "10.27", "10.28", "10.29", "10.30", "10.31", "11.01"]
      : Array.from({ length: 10 }, (_, i) => `10.${3 + i * 3}`);
  trendChart.setOption(
    {
      color: ["#3478f6", "#f29a45", "#ef6b75"],
      tooltip: { trigger: "axis" },
      grid: { left: 42, right: 22, top: 36, bottom: 30 },
      legend: { show: false },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: labels,
        axisLine: { lineStyle: { color: "#dfe5ed" } },
        axisLabel: { color: "#8491a5" },
      },
      yAxis: {
        type: "value",
        min: 0,
        max: 2500,
        splitLine: { lineStyle: { color: "#edf1f6" } },
        axisLabel: { color: "#8491a5" },
      },
      series: [
        {
          name: "实例总量",
          type: "line",
          smooth: true,
          symbol: "circle",
          symbolSize: 6,
          data: labels.map(
            (_, i) => 2310 + [18, 25, 14, 36, 41, 52, 66, 58, 72, 81][i]
          ),
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "rgba(52,120,246,.22)" },
              { offset: 1, color: "rgba(52,120,246,0)" },
            ]),
          },
        },
        {
          name: "新增实例",
          type: "line",
          smooth: true,
          data: labels.map(
            (_, i) => [34, 29, 42, 38, 31, 26, 37, 32, 28, 25][i]
          ),
        },
        {
          name: "变更实例",
          type: "line",
          smooth: true,
          data: labels.map((_, i) => [11, 8, 14, 9, 12, 7, 10, 8, 6, 9][i]),
        },
      ],
    },
    true
  );
}
function initDonut() {
  if (!donutRef.value) return;
  donutChart ||= echarts.init(donutRef.value);
  donutChart.setOption(
    {
      color: ["#3478f6", "#35bfc3", "#f5bf42", "#7b6cf0", "#ef6b75"],
      tooltip: { trigger: "item" },
      legend: {
        orient: "vertical",
        right: 8,
        top: "middle",
        itemWidth: 8,
        itemHeight: 8,
        textStyle: { color: "#68768a", fontSize: 11 },
      },
      series: [
        {
          type: "pie",
          radius: ["48%", "68%"],
          center: ["35%", "48%"],
          avoidLabelOverlap: true,
          label: { show: false },
          data: [
            { value: 862, name: "核心对象类型" },
            { value: 614, name: "关联对象类型" },
            { value: 483, name: "业务事件类型" },
            { value: 329, name: "基础资料类型" },
            { value: 198, name: "扩展对象类型" },
          ],
        },
        {
          type: "pie",
          radius: ["0%", "0%"],
          center: ["35%", "48%"],
          label: {
            show: true,
            position: "center",
            formatter: "{value|2,486}\n{name|对象实例}",
            rich: {
              value: {
                fontSize: 25,
                fontWeight: 600,
                color: "#25344a",
                lineHeight: 34,
              },
              name: { fontSize: 11, color: "#8995a7" },
            },
          },
          data: [{ value: 1 }],
        },
      ],
    },
    true
  );
}
function initGraph() {
  if (!graphRef.value) return;
  graphChart ||= echarts.init(graphRef.value);
  const nodes = [
    { name: "项目本体", category: 0, symbolSize: 54 },
    { name: "核心对象类型", category: 1, symbolSize: 62 },
    { name: "关联对象类型 A", category: 1, symbolSize: 50 },
    { name: "对象属性", category: 2, symbolSize: 44 },
    { name: "关系类型", category: 3, symbolSize: 48 },
    { name: "业务对象集", category: 4, symbolSize: 48 },
    { name: "约束规则", category: 3, symbolSize: 46 },
  ];
  graphChart.setOption(
    {
      color: ["#3478f6", "#35bfc3", "#7b6cf0", "#f29a45", "#ef6b75"],
      tooltip: {},
      legend: {
        bottom: 2,
        data: ["本体", "对象类型", "属性", "关系/规则", "对象集"],
        textStyle: { fontSize: 10, color: "#748196" },
        itemWidth: 10,
        itemHeight: 10,
      },
      series: [
        {
          type: "graph",
          layout: "force",
          roam: true,
          categories: [
            { name: "本体" },
            { name: "对象类型" },
            { name: "属性" },
            { name: "关系/规则" },
            { name: "对象集" },
          ],
          data: nodes,
          links: [
            { source: 0, target: 1, name: "定义" },
            { source: 0, target: 2, name: "定义" },
            { source: 1, target: 3, name: "拥有属性" },
            { source: 1, target: 4, name: "关联" },
            { source: 1, target: 5, name: "组成" },
            { source: 1, target: 6, name: "受约束于" },
          ],
          force: { repulsion: 220, edgeLength: [70, 110] },
          label: { show: true, fontSize: 10, color: "#344258" },
          edgeLabel: {
            show: true,
            formatter: (params) => params.data?.name || "",
            fontSize: 9,
            color: "#8a96a8",
          },
          lineStyle: { color: "#aebfdd", curveness: 0.08 },
        },
      ],
    },
    true
  );
}
function resizeCharts() {
  [trendChart, donutChart, graphChart].forEach((chart) => chart?.resize());
}
onMounted(async () => {
  await loadProjects();
  window.addEventListener("resize", resizeCharts);
});
onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts);
  [trendChart, donutChart, graphChart].forEach((chart) => chart?.dispose());
});
</script>
 
<style lang="scss" scoped>
.analysis-dashboard {
  min-height: calc(100vh - 84px);
  padding: 15px;
  background: #f2f4f7;
  color: #26344a;
  font-family: "PingFang SC", sans-serif;
  box-sizing: border-box;
}
.dashboard-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 68px;
  margin-bottom: 1px;
  padding: 0 20px;
  background: #fff;
  border-radius: 2px 2px 0 0;
}
.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;
}
.heading-icon {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #3478f6, #638ae9);
  box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
}
.heading-icon svg {
  width: 21px;
}
.page-heading h1 {
  margin: 0;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 600;
}
.page-heading p {
  margin: 5px 0 0;
  color: #8a95a5;
  font-size: 12px;
}
.toolbar-filters {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}
.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.filter-item > span {
  color: #758196;
  font-size: 11px;
}
.filter-item .el-select {
  width: 190px;
}
.filter-item:nth-child(2) .el-select {
  width: 130px;
}
.filter-item :deep(.el-select__wrapper) {
  min-height: 32px;
  border-radius: 2px;
  font-size: 13px;
}
.range-toggle {
  display: inline-flex;
  overflow: hidden;
  height: 32px;
  border: 1px solid #d7e0ec;
  border-radius: 3px;
}
.range-toggle button {
  padding: 0 12px;
  border: 0;
  background: #fff;
  color: #657388;
  font-size: 12px;
  cursor: pointer;
}
.range-toggle button + button {
  border-left: 1px solid #d7e0ec;
}
.range-toggle button.active {
  color: #fff;
  background: var(--el-color-primary);
}
.scope-strip {
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 42px;
  margin-bottom: 15px;
  padding: 0 20px;
  color: #7b8798;
  background: #fff;
  border-top: 1px solid #edf0f4;
  font-size: 12px;
}
.scope-strip span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.scope-strip svg {
  width: 14px;
  color: #3478f6;
}
.scope-strip b {
  color: #4c5a70;
  font-weight: 500;
}
.scope-strip > i {
  width: 1px;
  height: 15px;
  background: #e2e7ee;
}
.scope-status {
  margin-left: auto;
}
.scope-status :deep(.el-tag),
:deep(.el-tag) {
  border-radius: 2px;
}
.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 15px;
}
.metric-card {
  position: relative;
  min-height: 128px;
  padding: 16px 17px 13px;
  overflow: hidden;
  border: 1px solid transparent;
  border-radius: 2px;
  background: #fff;
  cursor: pointer;
  box-sizing: border-box;
  transition: 0.2s;
}
.metric-card:hover,
.metric-card.selected {
  border-color: #b9cff8;
  box-shadow: 0 5px 15px rgba(42, 91, 165, 0.08);
}
.metric-label {
  color: #7b8798;
  font-size: 14px;
}
.metric-value {
  margin-top: 5px;
  color: #1e2d42;
  font-family: Sharp, "PingFang SC", sans-serif;
  font-size: 28px;
  line-height: 38px;
  font-weight: 600;
}
.metric-value small {
  margin-left: 4px;
  color: #657388;
  font-size: 13px;
  font-weight: 400;
}
.metric-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #edf0f4;
  color: #758196;
  font-size: 12px;
}
.metric-foot em {
  color: #32b8bb;
  font-style: normal;
}
.metric-foot em.down {
  color: #ef6b75;
}
.metric-badge {
  position: absolute;
  top: 17px;
  right: 17px;
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
}
.metric-badge svg {
  width: 22px;
}
.primary-grid,
.secondary-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
}
.secondary-grid {
  grid-template-columns: 2fr 1fr;
}
.dashboard-section {
  min-width: 0;
  padding: 0 20px 16px;
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
}
.section-header,
.section-toolbar {
  display: flex;
  align-items: center;
  min-height: 50px;
}
.section-toolbar {
  justify-content: space-between;
  gap: 16px;
  margin: 0 0 12px;
  border-bottom: 1px solid #e8e8e8;
}
.dashboard-section > .section-header {
  margin: 0 -20px 12px;
  padding: 0 20px;
  border-bottom: 1px solid #e8e8e8;
}
.section-marker {
  width: 6px;
  height: 16px;
  margin-right: 8px;
  border-radius: 3px;
  background: var(--el-color-primary);
}
.section-title {
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
}
.section-subtitle {
  margin-left: 10px;
  color: #929dad;
  font-size: 12px;
}
.chart-canvas {
  width: 100%;
}
.trend-chart,
.donut-chart {
  height: 275px;
}
.legend-summary {
  display: flex;
  gap: 14px;
  color: #748196;
  font-size: 11px;
}
.legend-summary span {
  display: flex;
  align-items: center;
  gap: 5px;
}
.legend-summary i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}
.legend-summary .blue {
  background: #3478f6;
}
.legend-summary .orange {
  background: #f29a45;
}
.legend-summary .red {
  background: #ef6b75;
}
.distribution-foot {
  display: flex;
  justify-content: space-around;
  margin-top: -8px;
  padding-top: 9px;
  border-top: 1px solid #edf0f4;
  color: #7d899b;
  font-size: 11px;
}
.distribution-foot b {
  color: #3478f6;
  font-weight: 500;
}
.graph-section {
  padding-bottom: 12px;
}
.graph-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 230px;
  height: 286px;
}
.graph-chart {
  height: 286px;
}
.object-detail {
  padding: 14px;
  border-left: 1px solid #edf0f4;
  background: #fafbfc;
  transition: 0.2s;
}
.object-title {
  display: flex;
  gap: 9px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8edf3;
}
.object-icon {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 6px;
  color: #fff;
  background: #3478f6;
}
.object-icon svg {
  width: 18px;
}
.object-title strong {
  color: #2f3d52;
  font-size: 12px;
}
.object-title p {
  margin: 4px 0 0;
  color: #929dad;
  font-size: 10px;
}
.object-detail dl {
  margin: 10px 0;
}
.object-detail dl div {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 30px;
  font-size: 11px;
}
.object-detail dt {
  color: #8a96a8;
}
.object-detail dd {
  margin: 0;
  color: #526075;
}
.danger-text {
  color: #ef6b75 !important;
}
.relation-path {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px;
  color: #708096;
  background: #eef4ff;
  font-size: 9px;
}
.relation-path svg {
  width: 10px;
}
.relation-path b {
  color: #3478f6;
  font-weight: 500;
}
.rule-list {
  padding-top: 2px;
}
.rule-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
.rule-rank {
  display: grid;
  place-items: center;
  width: 23px;
  height: 23px;
  flex: 0 0 auto;
  border-radius: 4px;
  color: #6e7b8e;
  background: #eef1f5;
  font-size: 11px;
}
.rank-1 {
  color: #fff;
  background: #3478f6;
}
.rank-2 {
  color: #fff;
  background: #35bfc3;
}
.rank-3 {
  color: #fff;
  background: #f29a45;
}
.rule-main {
  flex: 1;
  min-width: 0;
}
.rule-main > div {
  display: flex;
  justify-content: space-between;
}
.rule-main strong {
  color: #445267;
  font-size: 11px;
}
.rule-main b {
  color: #6b788b;
  font-size: 10px;
  font-weight: 500;
}
.rule-main p {
  margin: 3px 0 6px;
  color: #9aa4b3;
  font-size: 9px;
}
.rule-progress {
  height: 4px;
  overflow: hidden;
  border-radius: 3px;
  background: #edf1f6;
}
.rule-progress i {
  display: block;
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #3478f6, #69a3ff);
}
.rule-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  padding: 10px;
  border: 1px solid #f7dba6;
  border-radius: 3px;
  background: #fffaf0;
}
.rule-summary > svg {
  width: 17px;
  color: #e8a136;
}
.rule-summary div {
  flex: 1;
}
.rule-summary strong {
  display: block;
  color: #7d6030;
  font-size: 11px;
}
.rule-summary p {
  margin: 3px 0 0;
  color: #a28c68;
  font-size: 9px;
}
.table-section {
  padding-bottom: 12px;
}
.table-actions {
  display: flex;
  gap: 8px;
}
.table-actions .el-input {
  width: 220px;
}
.table-actions .el-select {
  width: 110px;
}
.table-actions :deep(.el-input__wrapper),
.table-actions :deep(.el-select__wrapper) {
  min-height: 32px;
  border-radius: 2px;
}
.dashboard-table :deep(.el-table__header th) {
  background: #f3f5f8;
  color: #566477;
  font-weight: 600;
}
.dashboard-table :deep(.el-table__cell) {
  padding: 6px 0;
}
.object-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}
.object-cell > span {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: 5px;
  color: #fff;
}
.object-cell svg {
  width: 15px;
}
.object-cell strong {
  display: block;
  color: #3e4d63;
  font-size: 12px;
}
.object-cell small {
  display: block;
  margin-top: 2px;
  color: #99a4b3;
  font-size: 9px;
}
@media (max-width: 1400px) {
  .toolbar-filters .filter-item > span {
    display: none;
  }
  .primary-grid,
  .secondary-grid {
    grid-template-columns: 1.6fr 1fr;
  }
  .filter-item .el-select {
    width: 160px;
  }
}
@media (max-width: 1100px) {
  .dashboard-toolbar {
    align-items: flex-start;
    gap: 14px;
    padding: 14px 18px;
  }
  .toolbar-filters {
    flex-wrap: wrap;
    justify-content: flex-end;
  }
  .metric-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .primary-grid,
  .secondary-grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 700px) {
  .analysis-dashboard {
    padding: 10px;
  }
  .dashboard-toolbar {
    flex-direction: column;
  }
  .toolbar-filters {
    justify-content: flex-start;
  }
  .filter-item .el-select {
    width: 180px;
  }
  .metric-grid {
    grid-template-columns: 1fr;
  }
  .scope-strip {
    overflow: auto;
    white-space: nowrap;
  }
  .graph-body {
    grid-template-columns: 1fr;
  }
  .object-detail {
    display: none;
  }
  .table-actions .el-input {
    width: 160px;
  }
}
</style>
