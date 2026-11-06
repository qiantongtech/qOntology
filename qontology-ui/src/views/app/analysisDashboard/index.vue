<template>
  <div class="analysis-dashboard app-container">
    <header class="page-header">
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
            ><button
              :class="{ active: period === 30 }"
              @click="changePeriod(30)"
            >
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
    </header>

    <div class="metric-grid">
      <article
        v-for="card in metricCards"
        :key="card.label"
        class="metric-card"
      >
        <div class="metric-label">{{ card.label }}</div>
        <div class="metric-value">
          {{ card.value }}<small>{{ card.unit }}</small>
        </div>
        <div class="metric-foot">
          <span>{{ card.note }}</span>
          <em :class="card.up ? 'trend-up' : 'trend-down'">{{ card.trend }}</em>
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
          <!-- <el-button text type="primary" @click="graphExpanded = !graphExpanded"
            >{{ graphExpanded ? "收起详情" : "查看全图" }} <ArrowRight
          /></el-button> -->
        </div>
        <div class="graph-body">
          <div ref="graphRef" class="chart-canvas graph-chart"></div>
          <aside class="object-detail" :class="{ expanded: graphExpanded }">
            <div class="object-title">
              <span class="object-icon"><Cpu /></span>
              <div class="object-info">
                <span class="object-name">核心业务对象 OBJ-2048</span>
                <span class="object-type">对象类型：核心对象类型</span>
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
    note: "较上个版本新增 6 类：",
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
    note: "已使用关系 42 类：",
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
    note: "较上周新增 38 个：",
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
    note: "待处理问题 37 项：",
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
        textStyle: { color: "#68768a", fontSize: 14 },
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
              name: { fontSize: 14, color: "#8995a7" },
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
    {
      name: "项目本体",
      category: 0,
      x: 140,
      y: 150,
      itemStyle: {
        color: "#e8f1ff",
        borderColor: "#2f73f5",
        borderWidth: 1.6,
      },
      label: { color: "#1d4ed8" },
    },
    {
      name: "对象类型",
      category: 1,
      x: 300,
      y: 150,
      symbolSize: 62,
      itemStyle: {
        color: "#2f73f5",
        shadowColor: "rgba(47,115,245,.35)",
        shadowBlur: 14,
      },
      label: { color: "#fff", fontWeight: 600 },
    },
    {
      name: "关联对象类型",
      category: 1,
      x: 190,
      y: 240,
      itemStyle: {
        color: "#e6f8f6",
        borderColor: "#37bec2",
        borderWidth: 1.6,
      },
      label: { color: "#0f8a86" },
    },
    {
      name: "对象属性",
      category: 2,
      x: 410,
      y: 240,
      itemStyle: {
        color: "#eef1ff",
        borderColor: "#7b6cf0",
        borderWidth: 1.6,
      },
      label: { color: "#4a3fb8" },
    },
    {
      name: "关系类型",
      category: 3,
      x: 300,
      y: 50,
      itemStyle: {
        color: "#fff3e6",
        borderColor: "#f29a45",
        borderWidth: 1.6,
      },
      label: { color: "#b26112" },
    },
    {
      name: "业务对象集",
      category: 4,
      x: 460,
      y: 150,
      itemStyle: {
        color: "#fff0f2",
        borderColor: "#ef6b75",
        borderWidth: 1.6,
      },
      label: { color: "#c43d4a" },
    },
    {
      name: "约束规则",
      category: 3,
      x: 170,
      y: 55,
      itemStyle: {
        color: "#fff3e6",
        borderColor: "#f29a45",
        borderWidth: 1.6,
      },
      label: { color: "#b26112" },
    },
  ];
  graphChart.setOption(
    {
      animationDuration: 900,
      animationEasing: "cubicOut",
      color: ["#3478f6", "#35bfc3", "#7b6cf0", "#f29a45", "#ef6b75"],
      tooltip: { show: false },
      legend: {
        bottom: 2,
        data: ["本体", "对象类型", "属性", "关系/规则", "对象集"],
        textStyle: { fontSize: 14, color: "#748196" },
        itemWidth: 10,
        itemHeight: 10,
      },
      series: [
        {
          type: "graph",
          layout: "none",
          roam: false,
          top: 18,
          right: "18%",
          bottom: 38,
          left: "18%",
          symbol: "circle",
          symbolSize: 56,
          edgeSymbol: ["none", "arrow"],
          edgeSymbolSize: 8,
          categories: [
            { name: "本体" },
            { name: "对象类型" },
            { name: "属性" },
            { name: "关系/规则" },
            { name: "对象集" },
          ],
          data: nodes,
          links: [
            { source: 0, target: 1, value: "定义" },
            { source: 0, target: 2, value: "定义" },
            { source: 1, target: 3, value: "拥有属性" },
            { source: 1, target: 4, value: "关联" },
            { source: 1, target: 5, value: "组成" },
            { source: 1, target: 6, value: "受约束于" },
          ],
          label: { show: true, position: "inside", fontSize: 12 },
          edgeLabel: {
            show: true,
            formatter: "{c}",
            fontSize: 14,
            color: "#606266",
            padding: [1, 3],
            borderRadius: 2,
          },
          lineStyle: { color: "#606266", width: 1.4, curveness: 0.14 },
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
/*
 * 设计 Token 对齐首页 (system/index.vue)
 * 字号体系：30px(Sharp数字) → 20px(页面大标题) → 16px(板块标题/500) → 14px(正文/列表/辅助)
 * 颜色：标题 rgba(0,0,0,0.85) | 正文 rgba(0,0,0,0.65) | 辅助 var(--qt-text-secondary);
 * 分隔线：板块 #e8e8e8 | 卡片内 #e2e2e2
 * 圆角：2px | 板块marker：6×16px 主色 3px圆角
 */
.analysis-dashboard {
  min-height: calc(100vh - 84px);
  padding: 15px;
  background: #f0f2f5;
  color: var(--qt-text-primary);
  font-family: PingFang SC;
  box-sizing: border-box;
}
/* 顶部合一块的白容器 */
.page-header {
  background: #fff;
  border-radius: 2px;
  margin-bottom: 15px;
}
.dashboard-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 50px;
  padding: 15px;
}
.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;
}
.heading-icon {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #3478f6, #638ae9);
  // box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
}
.heading-icon svg {
  width: 21px;
}
.page-heading h1 {
  margin: 0;
  color: var(--qt-text-primary);
  font-size: 16px;
  font-weight: 600;
  line-height: 22px;
}
.page-heading p {
  margin: 4px 0 0;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
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
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
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
  font-size: 14px;
}
.range-toggle {
  display: inline-flex;
  overflow: hidden;
  height: 32px;
  border: 1px solid #e4e7ed;
  border-radius: 2px;
}
.range-toggle button {
  padding: 0 12px;
  border: 0;
  background: #fff;
  color: rgba(0, 0, 0, 0.65);
  font-size: 14px;
  line-height: 32px;
  cursor: pointer;
}
.range-toggle button + button {
  border-left: 1px solid #e4e7ed;
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
  padding: 15px;
  color: var(--qt-text-secondary);
  border-top: 1px solid #e8e8e8;
  font-size: 14px;
  line-height: 22px;
}
.scope-strip span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.scope-strip svg {
  width: 14px;
  color: var(--qt-element-fixed-primary);
}
.scope-strip b {
  color: var(--qt-text-primary);
  font-weight: 500;
}
.scope-strip > i {
  width: 1px;
  height: 15px;
  background: #e8e8e8;
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
  gap: 15px;
  margin-bottom: 15px;
}
.metric-card {
  position: relative;
  min-height: 128px;
  padding: 16px 17px 13px;
  overflow: hidden;
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
}
.metric-label {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.metric-value {
  margin-top: 5px;
  color: #000000;
  font-family: Sharp;
  font-size: 30px;
  line-height: 38px;
  font-weight: 600;
}
.metric-value small {
  margin-left: 4px;
  color: rgba(0, 0, 0, 0.65);
  font-family: PingFang SC;
  font-size: 14px;
  font-weight: 400;
}
.metric-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #e2e2e2;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.metric-foot em {
  color: var(--qt-text-primary);
  font-style: normal;
  font-size: 14px;
  margin: 0 10px;
  position: relative;
}

.metric-foot .trend-up::after,
.metric-foot .trend-down::after {
  content: "";
  position: absolute;
  right: -16px;
  width: 0;
  height: 12px;
  border: 6px solid transparent;
}

.metric-foot .trend-up::after {
  top: 0px;
  border-bottom-color: #f5222d;
}

.metric-foot .trend-down::after {
  top: 6px;
  border-top-color: #52c41a;
}

.metric-foot--stats {
  overflow: hidden;
  gap: 0;
}
.metric-badge {
  position: absolute;
  top: 17px;
  right: 17px;
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 8px;
  color: #fff;
  // box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
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
  color: var(--qt-text-primary);
  font-size: 16px;
  font-family: PingFang SC;
  font-weight: 500;
}
.section-subtitle {
  margin-left: 10px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
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
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
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
  border-top: 1px solid #e2e2e2;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.distribution-foot b {
  color: var(--el-color-primary);
  font-weight: 500;
}
.graph-section {
  padding-bottom: 12px;
}
.graph-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 230px;
  height: 300px;
}
.graph-chart {
  height: 300px;
}
.object-detail {
  padding: 14px;
  border-left: 1px solid #e2e2e2;
  background: #fff;
  height: 250px;
}
.object-title {
  display: flex;
  align-items: center;
  gap: 9px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}
.object-icon {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 4px;
  color: #fff;
  background: #3478f6;
}
.object-icon svg {
  width: 18px;
}
.object-title .object-info {
  display: flex;
  flex-direction: column;
}
.object-title .object-name {
  color: var(--qt-text-primary);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.object-title .object-type {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.object-detail dl {
  margin: 10px 0;
}
.object-detail dl div {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 30px;
  font-size: 14px;
  line-height: 22px;
}
.object-detail dt {
  color: var(--qt-text-secondary);
}
.object-detail dd {
  margin: 0;
  color: rgba(0, 0, 0, 0.65);
}
.danger-text {
  color: #f5222d !important;
}
.relation-path {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px;
  color: rgba(0, 0, 0, 0.65);
  background: #eef4ff;
  font-size: 14px;
  line-height: 22px;
}
.relation-path svg {
  width: 10px;
}
.relation-path b {
  color: var(--el-color-primary);
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
  border-radius: 2px;
  color: rgba(0, 0, 0, 0.65);
  background: #f0f2f5;
  font-size: 14px;
  line-height: 22px;
}
.rank-1 {
  color: #fff;
  background: var(--el-color-primary);
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
  color: var(--qt-text-primary);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.rule-main b {
  color: rgba(0, 0, 0, 0.65);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.rule-main p {
  margin: 3px 0 6px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.rule-progress {
  height: 4px;
  overflow: hidden;
  border-radius: 2px;
  background: #e2e2e2;
}
.rule-progress i {
  display: block;
  height: 100%;
  border-radius: 2px;
  background: linear-gradient(90deg, #3478f6, #69a3ff);
}
.rule-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  padding: 10px;
  border: 1px solid #f7dba6;
  border-radius: 2px;
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
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.rule-summary p {
  margin: 3px 0 0;
  color: #a28c68;
  font-size: 14px;
  line-height: 22px;
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
  font-size: 14px;
}
.dashboard-table :deep(.el-table__header th) {
  background: #f1f1f5;
  color: #666666;
  font-weight: 600;
  font-size: 14px;
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
  border-radius: 2px;
  color: #fff;
}
.object-cell svg {
  width: 15px;
}
.object-cell strong {
  display: block;
  color: var(--qt-text-primary);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.object-cell small {
  display: block;
  margin-top: 2px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
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
