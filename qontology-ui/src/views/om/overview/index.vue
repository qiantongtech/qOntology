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
  <div class="app-container overview-page">

    <!-- 顶部统计卡片 -->
    <div class="metric-grid">
      <article v-for="card in metricCards" :key="card.label" class="metric-card">
        <div class="metric-copy">
          <div class="metric-label">{{ card.label }}</div>
          <div class="metric-value">
            {{ card.value }}<small v-if="card.unit">{{ card.unit }}</small>
          </div>

          <div v-if="card.foot.type === 'stats'" class="metric-foot metric-foot--stats">
            <template v-for="(stat, index) in card.foot.stats" :key="stat.label">
              <span class="stat-item">
                {{ stat.label }}&nbsp;<b :style="{ color: stat.color }">{{ stat.value }}</b>
              </span>
              <em v-if="index < card.foot.stats.length - 1" class="stat-divider">·</em>
            </template>
          </div>

          <div v-else-if="card.foot.type === 'quality'" class="metric-foot metric-foot--quality">
            <span class="quality-item">
              <el-icon class="quality-icon quality-icon--info"><InfoFilled/></el-icon>
              提示&nbsp;<b class="quality-info">{{ card.foot.info }}</b>
            </span>
            <span class="quality-item">
              <el-icon class="quality-icon quality-icon--warning"><WarningFilled/></el-icon>
              警告&nbsp;<b class="quality-warning">{{ card.foot.warning }}</b>
            </span>
            <span class="quality-item">
              <el-icon class="quality-icon quality-icon--error"><CircleCloseFilled/></el-icon>
              错误&nbsp;<b class="quality-error">{{ card.foot.error }}</b>
            </span>
          </div>

          <div v-else-if="card.foot.type === 'trend'" class="metric-foot">
            周同比&nbsp;<em :class="{ down: !card.foot.up }">{{ card.foot.trend }} {{ card.foot.up ? "▲" : "▼" }}</em>
          </div>

          <div v-else class="metric-foot metric-foot--note">
            <i class="version-dot"/>{{ card.foot.note }}
          </div>
        </div>
        <div class="metric-badge" :style="{ background: `linear-gradient(135deg, ${card.color}, ${card.color}b3)` }">
          <el-icon :size="22">
            <component :is="card.icon"/>
          </el-icon>
        </div>
      </article>
    </div>

    <!-- 中部：项目情况 / 语义资产概览 / 增长趋势 -->
    <div class="triple-grid">
      <section class="dashboard-section perf-section">
        <div class="section-header">
          <span class="section-marker"/>
          <span class="section-title">项目情况</span>
        </div>
        <div class="perf-body">
          <div class="perf-head">
            <div class="perf-logo">
              <el-icon :size="20">
                <Share/>
              </el-icon>
            </div>
            <div class="perf-head-copy">
              <div class="perf-name">{{ perf.name }}</div>
              <div class="perf-desc" :title="perf.desc">描述：{{ perf.desc }}</div>
            </div>
          </div>
          <ul class="perf-list">
            <li>
              <span class="perf-item-label perf-label--blue"><el-icon><CollectionTag/></el-icon>包含范围</span>
              <el-tooltip effect="light" :content="perf.includeRange" placement="top-start">
                <span class="perf-domain">{{ perf.includeRange }}</span>
              </el-tooltip>
            </li>
            <li>
              <span class="perf-item-label perf-label--orange"><el-icon><Filter/></el-icon>排除范围</span>
              <el-tooltip effect="light" :content="perf.excludeRange" placement="top-start">
                <span class="perf-domain">{{ perf.excludeRange }}</span>
              </el-tooltip>
            </li>
            <li>
              <span class="perf-item-label perf-label--teal"><el-icon><Link/></el-icon>命名空间</span>
              <span class="perf-namespace" :title="perf.namespace">{{ perf.namespace }}</span>
            </li>
            <li>
              <span class="perf-item-label perf-label--purple"><el-icon><Folder/></el-icon>项目领域</span>
              <span class="perf-domain">{{ perf.domain }}</span>
            </li>
          </ul>
        </div>
      </section>

      <section class="dashboard-section chart-section">
        <div class="section-header">
          <span class="section-marker"/>
          <span class="section-title">语义资产概览</span>
        </div>
        <div ref="donutRef" class="chart-canvas"/>
      </section>

      <section class="dashboard-section chart-section">
        <div class="section-toolbar">
          <div class="section-header">
            <span class="section-marker"/>
            <span class="section-title">本体元素增长趋势</span>
          </div>
          <div class="range-toggle">
            <button type="button" :class="{ active: trendDays === 7 }" @click="changeTrend(7)">近7天</button>
            <button type="button" :class="{ active: trendDays === 30 }" @click="changeTrend(30)">近30天</button>
          </div>
        </div>
        <div ref="trendRef" class="chart-canvas"/>
      </section>
    </div>

    <!-- 底部：导入导出任务 / 本体结构预览 -->
    <div class="bottom-grid">
      <section class="dashboard-section task-section">
        <div class="section-toolbar">
          <div class="section-header">
            <span class="section-marker"/>
            <span class="section-title">导入导出任务</span>
          </div>
        </div>
        <el-table :data="pagedTasks" class="task-table">
          <el-table-column label="序号" width="80" align="center">
            <template #default="{ $index }">{{ $index + (taskPage - 1) * taskPageSize + 1 }}</template>
          </el-table-column>
          <el-table-column prop="type" label="任务类型" align="center"/>
          <el-table-column prop="fileName" label="文件名称" align="center"
                           :show-overflow-tooltip="{ effect: 'light' }"/>
          <el-table-column prop="status" label="任务状态" width="110" align="center">
            <template #default="{ row }">
              <dict-tag :options="sys_common_status" :value="row.status"/>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="执行时间" width="200" align="center"/>
        </el-table>
      </section>

      <section class="dashboard-section chart-section graph-section">
        <div class="section-header">
          <span class="section-marker"/>
          <span class="section-title">本体结构预览</span>
        </div>
        <div ref="graphRef" class="chart-canvas"/>
      </section>
    </div>

    <!-- License agreement pop-up window -->
    <el-dialog
        v-model="licenseDialogVisible"
        :title="license.title"
        :close-on-click-modal="false"
        :show-close="false"
        @close="handleLicenseClose"
    >
      <div class="license-content">
        <p v-for="item in license.text.intro" :key="item" class="license-paragraph">
          {{ item }}
        </p>

        <div v-for="(item, index) in license.text.terms" :key="item.title" class="license-term">
          <div class="license-term-title">
            <span class="license-term-index">{{ index + 1 }}</span>
            <span>{{ item.title }}</span>
          </div>
          <p v-for="desc in item.desc" :key="desc" class="license-term-desc">
            {{ desc }}
          </p>
        </div>

        <div class="license-notice">
          <span>{{ license.text.notice.before }}</span>
          <a :href="license.text.notice.link" target="_blank" rel="noopener noreferrer">
            {{ license.text.notice.linkText }}
          </a>
          <span>{{ license.text.notice.after }}</span>
        </div>

        <p v-for="item in license.text.footer" :key="item" class="license-footer-text">
          {{ item }}
        </p>
      </div>
      <template #footer>
        <el-button @click="toPolicy">商用授权</el-button>
        <el-button
            type="primary"
            class="license-aware-btn"
            :disabled="licenseCountdown > 0"
            @click="handleLicenseClose"
        >
          {{ licenseCountdown > 0 ? `我已知晓 (${licenseCountdown}s)` : "我已知晓" }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Index">
// eslint-disable-next-line no-unused-vars
import {computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import * as echarts from "echarts";
import {
  Aim,
  Box,
  CircleCloseFilled,
  CollectionTag,
  Connection,
  DataLine,
  Filter,
  Folder,
  InfoFilled,
  Link,
  PriceTag,
  Share,
  Tickets,
  WarningFilled
} from "@element-plus/icons-vue";
const {proxy} = getCurrentInstance();
const {sys_common_status} = proxy.useDict('sys_common_status');

/* ---------------------------------- 顶部统计卡片 ---------------------------------- */
// 以下统计均为首页演示数据，接入真实接口时替换为接口返回值。
const metricCards = [
  {
    label: "本体元素",
    value: "473",
    icon: Box,
    color: "#3478f6",
    foot: {
      type: "stats",
      stats: [
        {label: "对象类型", value: 12, color: "#3478f6"},
        {label: "对象类型属性", value: 210, color: "#35bfc3"},
        {label: "对象类型关系", value: 168, color: "#f29a45"}
      ]
    }
  },
  {
    label: "质量问题",
    value: "5",
    icon: WarningFilled,
    color: "#f29a45",
    foot: {type: "quality", info: 3, warning: 5, error: 0}
  },
  {
    label: "建模目标个数",
    value: "523",
    icon: Connection,
    color: "#37bec2",
    foot: {type: "trend", trend: "10%", up: true}
  },
  {
    label: "建模目标覆盖率",
    value: "88",
    unit: "%",
    icon: Aim,
    color: "#35bfc3",
    foot: {type: "trend", trend: "12%", up: true}
  },
  {
    label: "术语个数",
    value: "126",
    icon: PriceTag,
    color: "#eb5a99",
    foot: {type: "trend", trend: "3%", up: false}
  },
  {
    label: "枚举个数",
    value: "108",
    icon: DataLine,
    color: "#448fe9",
    foot: {type: "trend", trend: "15%", up: true}
  },
  {
    label: "数据类型数",
    value: "76",
    icon: CollectionTag,
    color: "#448fe9",
    foot: {type: "trend", trend: "8%", up: false}
  },
  {
    label: "约束规则数",
    value: "37",
    icon: Tickets,
    color: "#6688e9",
    foot: {type: "trend", trend: "12%", up: true}
  }
];

/* ------------------------------------ 项目情况 ------------------------------------ */
const perf = {
  name: "供应链采购与仓储协同本体",
  desc: "面向企业采购与仓储物流协同场景。",
  includeRange: "涵盖组织、供应商、采购方、物流服务商、物料、原材料、零部件、成品、采购订单、入库单和出库单等对象，以及相关术语、枚举、属性和关系。重点描述采购订单管理、供应商供货、物料采购、到货入库、出库发运、收货方关联及零部件与成品的组成关系。",
  excludeRange: "暂不包含财务核算、发票与付款结算、合同审批、人力资源、生产工序与排程、设备运维及运输路线优化等业务。不涉及业务系统中的实际单据处理、库存计算和流程执行。",
  namespace: "https://example.com/supply-chain#",
  domain: "供应链"
};

/* ---------------------------------- 图表通用逻辑 ---------------------------------- */
const donutRef = ref(null);
const trendRef = ref(null);
const graphRef = ref(null);
let donutChart = null;
let trendChart = null;
let graphChart = null;
let resizeObserver = null;

function resizeCharts() {
  [donutChart, trendChart, graphChart].forEach(chart => {
    if (chart && !chart.isDisposed?.()) chart.resize();
  });
}

/* -------------------------------- 语义资产概览环图 -------------------------------- */
// 样式对齐 system/index 的「本体数据分布」饼图：渐变配色、外侧占比标签、
// 右侧竖向图例、中心 100% 标题（Sharp 字体）与"数量/占比"提示框。
const assetOverviewData = [
  {name: "对象类型", value: 78, color: "#f5b942"},
  {name: "属性", value: 368, color: "#2f73f5"},
  {name: "关系", value: 126, color: "#36c0c2"}
];

// 渐变配色对，与「本体数据分布」饼图保持同一取色逻辑。
const donutGradientList = [
  {c1: "#1E60FB", c2: "#5D8EFE"},
  {c1: "#6CD8D0", c2: "#1DC7B5"},
  {c1: "#F9D370", c2: "#F7BD26"},
  {c1: "#B28AE9", c2: "#9358E3"},
  {c1: "#EA7283", c2: "#F53D57"}
];

function initDonut() {
  if (!donutRef.value) return;
  donutChart = echarts.init(donutRef.value);
  const pieData = assetOverviewData.map(item => ({
    value: item.value,
    legendname: item.name,
    name: item.name
  }));
  donutChart.setOption({
    title: [
      {
        text: "100%",
        textStyle: {
          fontSize: 35,
          color: "rgba(0,0,0,0.65)",
          fontFamily: "Sharp",
        },
        textAlign: "center",
        x: "34.5%",
        y: "41%",
      },
    ],
    tooltip: {
      trigger: "item",
      formatter: params =>
          params.seriesName +
          "</br>" +
          params.marker +
          "" +
          params.data.legendname +
          "</br>" +
          "数量：" +
          params.data.value +
          "</br>" +
          "占比：" +
          params.percent +
          "%",
    },
    legend: {
      type: "scroll",
      orient: "vertical",
      left: "75%",
      align: "left",
      top: "10%",
      itemWidth: 8,
      itemHeight: 8,
      icon: "circle",
      formatter: name => `{a|${name}}`,
      textStyle: {
        lineHeight: 25,
        padding: [0, 0, 0, 0],
        rich: {
          a: {
            fontSize: 14,
            width: 46,
          },
        },
      },
    },
    series: [
      {
        name: "语义资产",
        type: "pie",
        center: ["35%", "50%"],
        radius: ["50%", "72%"],
        clockwise: false,
        avoidLabelOverlap: false,
        label: {
          formatter: "{d}%",
          position: "outside",
        },
        itemStyle: {
          normal: {
            color: params => {
              const pair = donutGradientList[params.dataIndex % donutGradientList.length];
              return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                {offset: 0, color: pair.c1},
                {offset: 1, color: pair.c2},
              ]);
            },
          },
        },
        data: pieData,
      },
    ],
  });
}

/* --------------------------------- 近N天增长趋势柱图 -------------------------------- */
const trendDays = ref(7);
const trendPalette = [
  {name: "对象类型", color: "#2f73f5"},
  {name: "关系", color: "#36c0c2"},
  {name: "属性", color: "#f5a623"}
];

function buildTrendData(days) {
  const labels = [];
  const series = {"对象类型": [], "关系": [], "属性": []};
  const base = new Date();
  for (let i = days - 1; i >= 0; i--) {
    const day = new Date(base);
    day.setDate(day.getDate() - i);
    labels.push(
        `${String(day.getMonth() + 1).padStart(2, "0")}.${String(day.getDate()).padStart(2, "0")}`
    );
    // 确定性伪随机，保证每次渲染图形稳定。
    series["对象类型"].push(Math.round(430 + 250 * Math.sin(i / 2.6) + (i * 53) % 190));
    series["关系"].push(Math.round(310 + 200 * Math.cos(i / 2.2) + (i * 37) % 140));
    series["属性"].push(Math.round(250 + 170 * Math.sin(i / 3.1 + 1.2) + (i * 29) % 120));
  }
  return {labels, series};
}

function trendYAxisMax(series) {
  const max = Math.max(...Object.values(series).flat());
  return Math.max(200, Math.ceil(max / 200) * 200);
}

function initTrend() {
  if (!trendRef.value) return;
  trendChart = echarts.init(trendRef.value);
  const data = buildTrendData(trendDays.value);
  trendChart.setOption(buildTrendOption(data));
}

function buildTrendOption(data) {
  return {
    animationDuration: 900,
    animationEasing: "cubicOut",
    animationDurationUpdate: 620,
    animationEasingUpdate: "cubicInOut",
    grid: {left: 34, right: 12, top: 34, bottom: 24, containLabel: true},
    legend: {
      top: 2,
      right: 2,
      data: trendPalette.map(item => item.name),
      icon: "circle",
      itemWidth: 7,
      itemHeight: 7,
      textStyle: {fontSize: 14}
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {type: "line", lineStyle: {color: "#c7d3e6"}},
      backgroundColor: "rgba(255,255,255,.96)",
      borderColor: "#dfe7f1",
      borderWidth: 1,
      padding: [8, 10],
      textStyle: {color: "#4b5a6f", fontSize: 12},
      valueFormatter: value => `${value} 个`
    },
    xAxis: {
      type: "category",
      data: data.labels,
      axisTick: {show: false},
      axisLine: {lineStyle: {color: "#d8e0ea"}},
      axisLabel: {color: "#8995a6", fontSize: 10}
    },
    yAxis: {
      type: "value",
      min: 0,
      max: trendYAxisMax(data.series),
      splitNumber: 4,
      axisLine: {show: false},
      axisTick: {show: false},
      axisLabel: {color: "#8995a6", fontSize: 9},
      splitLine: {lineStyle: {color: "#edf1f6"}}
    },
    series: trendPalette.map(item => ({
      name: item.name,
      type: "line",
      smooth: true,
      symbol: "circle",
      symbolSize: 6,
      showSymbol: false,
      lineStyle: {width: 2, color: item.color},
      itemStyle: {color: item.color},
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {offset: 0, color: `${item.color}33`},
          {offset: 1, color: `${item.color}00`}
        ])
      },
      data: data.series[item.name]
    }))
  };
}

function changeTrend(days) {
  if (trendDays.value === days) return;
  trendDays.value = days;
  const data = buildTrendData(days);
  trendChart?.setOption({
    xAxis: {data: data.labels},
    yAxis: {max: trendYAxisMax(data.series)},
    series: trendPalette.map(item => ({name: item.name, data: data.series[item.name]}))
  });
}

/* ---------------------------------- 本体结构预览图 ---------------------------------- */
function initGraph() {
  if (!graphRef.value) return;
  graphChart = echarts.init(graphRef.value);
  graphChart.setOption({
    animationDuration: 900,
    animationEasing: "cubicOut",
    tooltip: {show: false},
    series: [
      {
        type: "graph",
        layout: "none",
        roam: false,
        symbol: "circle",
        symbolSize: 56,
        label: {show: true, position: "inside", fontSize: 12},
        edgeSymbol: ["none", "arrow"],
        edgeSymbolSize: 8,
        edgeLabel: {
          show: true,
          fontSize: 10,
          color: "#78859a",
          backgroundColor: "#fff",
          padding: [1, 3],
          borderRadius: 2,
          formatter: "{c}"
        },
        lineStyle: {color: "#b8c6da", width: 1.4, curveness: 0.14},
        data: [
          {
            name: "商品分类",
            x: 280,
            y: 42,
            itemStyle: {color: "#eef1ff", borderColor: "#7b6cf0", borderWidth: 1.6},
            label: {color: "#4a3fb8"}
          },
          {
            name: "客户",
            x: 52,
            y: 152,
            itemStyle: {color: "#e8f1ff", borderColor: "#2f73f5", borderWidth: 1.6},
            label: {color: "#1d4ed8"}
          },
          {
            name: "订单",
            x: 280,
            y: 152,
            itemStyle: {color: "#2f73f5", shadowColor: "rgba(47,115,245,.35)", shadowBlur: 14},
            label: {color: "#fff", fontWeight: 600}
          },
          {
            name: "商品",
            x: 508,
            y: 152,
            itemStyle: {color: "#fff3e6", borderColor: "#f29a45", borderWidth: 1.6},
            label: {color: "#b26112"}
          },
          {
            name: "购买单",
            x: 280,
            y: 262,
            itemStyle: {color: "#e6f8f6", borderColor: "#37bec2", borderWidth: 1.6},
            label: {color: "#0f8a86"}
          }
        ],
        links: [
          {source: "客户", target: "订单", value: "购买"},
          {source: "订单", target: "商品分类", value: "属于"},
          {source: "订单", target: "商品", value: "包含"},
          {source: "购买单", target: "商品", value: "供应"}
        ]
      }
    ]
  });
}

/* ----------------------------------- 导入导出任务 ---------------------------------- */
const taskStatusCycle = ["0", "0", "0", "1", "0", "0", "0", "0", "1", "0"];
// 演示数据：接入真实接口时替换为导入导出任务列表接口。
const allTasks = Array.from({length: 45}, (_, index) => ({
  id: index + 1,
  type: index % 3 === 2 ? "导入" : "导出",
  fileName: "conceru.RDF",
  status: taskStatusCycle[index % taskStatusCycle.length],
  time: `2026-03-0${(index % 12) + 1} 12:12`
}));

const taskQuery = reactive({status: "", name: ""});
const taskPage = ref(1);
const taskPageSize = 5;

const filteredTasks = computed(() =>
    allTasks.filter(
        task =>
            (!taskQuery.status || task.status === taskQuery.status) &&
            (!taskQuery.name || task.fileName.includes(taskQuery.name.trim()))
    )
);

const pagedTasks = computed(() =>
    filteredTasks.value.slice((taskPage.value - 1) * taskPageSize, taskPage.value * taskPageSize)
);

watch(
    () => [taskQuery.status, taskQuery.name],
    () => {
      taskPage.value = 1;
    }
);

function handleSearch() {
  taskPage.value = 1;
}

function handleReset() {
  taskQuery.status = "";
  taskQuery.name = "";
  taskPage.value = 1;
}

function statusClass(status) {
  if (status === "成功") return "success";
  if (status === "失败") return "danger";
  return "running";
}

/* ---------------------------------- License 弹窗 ---------------------------------- */
const license = {
  title: "开源协议",
  text: {
    intro: [
      " qOntology 依据 Apache License 2.0 提供，并受以下针对  qOntology 的特定附加条件约束：",
      "在本协议中，“ qOntology” 是指整个软件项目，包括但不限于其前端、后端、API 及相关文档的全部或部分内容。",
      "在本协议中，“制作方” 是指江苏千桐科技有限公司。"
    ],
    terms: [
      {
        title:
            "您可以将  qOntology 用于商业目的，但不得移除、隐藏或修改  qOntology 中显示的  qOntology 标识、版权声明、许可声明或归属信息。",
        desc: [
          "如果您希望移除、隐藏、遮蔽、替换或修改上述标识、版权声明、许可声明或归属信息，包括但不限于白标使用、OEM 分发、品牌重塑或将  qOntology 作为其他产品展示，您必须向制作方另行申请商业许可。"
        ]
      },
      {
        title: "作为贡献者，您应当同意：",
        desc: [
          "a. 制作方可根据需要调整开源协议，使其更为严格或宽松。",
          "b. 您贡献的代码可能被用于商业目的，包括但不限于其云服务业务运营。"
        ]
      }
    ],
    notice: {
      before: "除上述特定条件外，其他所有权利和限制均遵循 Apache License 2.0 协议。有关 Apache License 2.0 的详细信息，请访问 ",
      link: "http://www.apache.org/licenses/LICENSE-2.0",
      linkText: "http://www.apache.org/licenses/LICENSE-2.0",
      after: "。"
    },
    footer: ["本产品的交互设计受外观专利保护。", "© 2026 江苏千桐科技有限公司"]
  }
};

const LICENSE_CACHE_KEY = " qKnow_license_acknowledged";
const LICENSE_COUNTDOWN_SECONDS = 10;
const licenseDialogVisible = ref(false);
const licenseCountdown = ref(0);
let licenseCountdownTimer = null;

function clearLicenseCountdown() {
  if (licenseCountdownTimer) {
    clearInterval(licenseCountdownTimer);
    licenseCountdownTimer = null;
  }
}

function startLicenseCountdown() {
  clearLicenseCountdown();
  licenseCountdown.value = LICENSE_COUNTDOWN_SECONDS;
  licenseCountdownTimer = setInterval(() => {
    if (licenseCountdown.value <= 1) {
      clearLicenseCountdown();
      licenseCountdown.value = 0;
      return;
    }
    licenseCountdown.value -= 1;
  }, 1000);
}

function checkLicense() {
  const acknowledged = localStorage.getItem(LICENSE_CACHE_KEY);
  if (!acknowledged) {
    licenseDialogVisible.value = true;
    startLicenseCountdown();
  }
}

function handleLicenseClose() {
  clearLicenseCountdown();
  localStorage.setItem(LICENSE_CACHE_KEY, "true");
  licenseDialogVisible.value = false;
}

function toPolicy() {
  window.open("https://community.qontology.ai/business/policy.html");
}

/* ------------------------------------ 生命周期 ------------------------------------ */
onMounted(() => {
  nextTick(() => {
    initDonut();
    initTrend();
    initGraph();
    resizeObserver = new ResizeObserver(resizeCharts);
    [donutRef, trendRef, graphRef].forEach(canvasRef => {
      if (canvasRef.value) resizeObserver.observe(canvasRef.value);
    });
  });
  window.addEventListener("resize", resizeCharts);
  checkLicense();
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts);
  resizeObserver?.disconnect();
  [donutChart, trendChart, graphChart].forEach(chart => chart?.dispose());
  donutChart = trendChart = graphChart = null;
  clearLicenseCountdown();
});
</script>

<style scoped lang="scss">
.overview-page {
  background: #f0f2f5;
  color: #263449;
  box-sizing: border-box;
}

.overview-page > .metric-grid,
.overview-page > .triple-grid,
.overview-page > .bottom-grid {
  animation: section-rise 0.55s ease backwards;
}

.overview-page > .triple-grid {
  animation-delay: 0.08s;
}

.overview-page > .bottom-grid {
  animation-delay: 0.16s;
}

/* ------------------------------- 卡片与区块通用样式 ------------------------------- */
.dashboard-section {
  min-width: 0;
  padding: 0 20px 16px;
  background: #fff;
  border-radius: 2px;
  box-sizing: border-box;
}

.section-toolbar,
.section-header {
  display: flex;
  align-items: center;
  min-height: 50px;
  min-width: 0;
  white-space: nowrap;
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
  font-family: "PingFang SC", sans-serif;
  font-size: 16px;
  font-weight: 500;
  line-height: 24px;
}

.section-subtitle {
  margin-left: 10px;
  color: #929dad;
  font-size: 12px;
  font-weight: 400;
}

/* ----------------------------------- 统计卡片 ----------------------------------- */
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
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
  animation: metric-card-in 0.55s ease backwards;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.metric-grid .metric-card:nth-child(2n) {
  animation-delay: 0.06s;
}

.metric-grid .metric-card:nth-child(2n + 1) {
  animation-delay: 0.03s;
}

.metric-grid .metric-card:nth-child(n + 5) {
  animation-delay: 0.12s;
}

.metric-card:hover {
  transform: translateY(-2px);
}

.metric-label {
  color: #7b8798;
  font-size: 14px;
}

.metric-value {
  margin-top: 5px;
  color: #1e2d42;
  font-family: Sharp;
  font-size: 28px;
  line-height: 38px;
  font-weight: 600;
}

.metric-value small {
  margin-left: 4px;
  color: #657388;
  font-size: 14px;
  font-weight: 400;
}

.metric-foot {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #edf0f4;
  color: #758196;
  font-size: 12px;
  white-space: nowrap;
}

.metric-foot em {
  color: #32b8bb;
  font-style: normal;
}

.metric-foot em.down {
  color: #ef6b75;
}

.metric-foot--stats {
  overflow: hidden;
  gap: 0;
}

.stat-item {
  flex: 0 1 auto;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-item b {
  font-weight: 500;
}

.stat-divider {
  flex: 0 0 auto;
  margin: 0 6px;
  color: #c3ccd9;
  font-style: normal;
}

.metric-foot--quality {
  gap: 14px;
}

.quality-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.quality-item b {
  color: #566477;
  font-weight: 500;
}

.quality-item .quality-warning {
  color: #e4a239;
}

.quality-item .quality-info {
  color: #3478ed;
}

.quality-item .quality-error {
  color: #ed6975;
}

.quality-icon--info {
  color: #3478ed;
}

.quality-icon--error {
  color: #ed6975;
}

.quality-icon--warning {
  color: #e4a239;
}

.metric-foot--note {
  gap: 6px;
}

.version-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3478f6;
}

.metric-badge {
  position: absolute;
  top: 17px;
  right: 17px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
}

/* ---------------------------------- 中部三列区块 ---------------------------------- */
.triple-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.triple-grid > * {
  min-width: 0;
}

.triple-grid .perf-section {
  display: flex;
  flex-direction: column;
}

.perf-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 4px;
}

.perf-head {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 6px 0 12px;
  border-bottom: 1px solid #edf0f4;
}

.perf-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 42px;
  width: 42px;
  height: 42px;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(135deg, #3478f6, #6688e9);
  box-shadow: 0 6px 14px rgba(45, 105, 223, 0.17);
}

.perf-head-copy {
  flex: 1;
  min-width: 0;
}

.perf-name {
  color: #1e2d42;
  font-size: 15px;
  font-weight: 600;
  line-height: 22px;
}

.perf-desc {
  margin-top: 4px;
  overflow: hidden;
  color: #8a95a5;
  font-size: 12px;
  line-height: 18px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.perf-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin: 0;
  padding: 0;
  list-style: none;
}

.perf-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 14px;
  font-size: 13px;
  min-width: 0;
}

.perf-item-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex: 0 0 auto;
  color: #566477;
}

.perf-label--blue {
  --perf-color: #3478f6;
}

.perf-label--orange {
  --perf-color: #f29a45;
}

.perf-label--teal {
  --perf-color: #35bfc3;
}

.perf-label--purple {
  --perf-color: #7b6cf0;
}

.perf-item-label .el-icon {
  color: var(--perf-color);
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-width: 0;
}

.soft-tag {
  padding: 2px 10px;
  border: 1px solid #cfe0fb;
  border-radius: 3px;
  background: #f0f6ff;
  color: #3a6fd8;
  font-size: 12px;
  line-height: 20px;
  white-space: nowrap;
}

.perf-namespace {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  color: #3478ed;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.perf-domain {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  color: #566477;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------------------------------- 图表区块 ---------------------------------- */
.chart-section {
  height: 312px;
  padding-bottom: 12px;
}

.chart-canvas {
  width: 100%;
  height: 248px;
  min-width: 0;
}

.range-toggle {
  display: inline-flex;
  overflow: hidden;
  border: 1px solid #d7e0ec;
  border-radius: 3px;
}

.range-toggle button {
  padding: 5px 12px;
  border: none;
  outline: none;
  background: #fff;
  color: #657388;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.range-toggle button + button {
  border-left: 1px solid #d7e0ec;
}

.range-toggle button.active {
  background: var(--el-color-primary);
  color: #fff;
}

/* ---------------------------------- 底部两列区块 ---------------------------------- */
.bottom-grid {
  display: grid;
  /* 与中部三列保持一致的列宽与间隔，保证分隔缝对齐 */
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 15px;
}

.bottom-grid > * {
  min-width: 0;
}

/* 导入导出任务跨前两列，右侧本体结构预览对齐中部最后一个组件 */
.bottom-grid .task-section {
  grid-column: span 2;
}

.task-section {
  display: flex;
  flex-direction: column;
  height: 344px;
  padding-bottom: 12px;
}

.task-section .section-toolbar {
  flex-wrap: wrap;
}

.task-filter {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-label {
  color: #566477;
  font-size: 13px;
  white-space: nowrap;
}

.filter-status {
  width: 110px;
}

.filter-name {
  width: 150px;
}

.task-table {
  flex: 1;
  min-height: 0;
}

.task-table :deep(.el-table__header th) {
  background: #f3f5f8;
  color: #566477;
  font-weight: 600;
}

.task-table :deep(.el-table__cell) {
  padding: 5px 0;
}

.status-tag {
  display: inline-block;
  min-width: 40px;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
  line-height: 18px;
}

.status-tag.success {
  color: #3db049;
  background: #ebf9ee;
}

.status-tag.danger {
  color: #ed6975;
  background: #fff0f2;
}

.status-tag.running {
  color: #3478ed;
  background: #eaf2ff;
}

.task-pagination {
  display: flex;
  justify-content: center;
  padding-top: 10px;
}

.bottom-grid .chart-section {
  height: 344px;
  padding-bottom: 8px;
}

.bottom-grid .chart-canvas {
  height: 284px;
}

.graph-section {
  padding-bottom: 8px;
}

/* ------------------------------------ 动画 ------------------------------------ */
@keyframes section-rise {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes metric-card-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (prefers-reduced-motion: reduce) {
  .overview-page > .metric-grid,
  .overview-page > .triple-grid,
  .overview-page > .bottom-grid,
  .metric-card {
    animation: none !important;
    transition: none !important;
  }
}

/* ------------------------------------ 响应式 ------------------------------------ */
@media (max-width: 1400px) {
  .triple-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .triple-grid .perf-section {
    grid-column: span 2;
  }
}

@media (max-width: 1200px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .metric-grid,
  .triple-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }

  .triple-grid .perf-section {
    grid-column: auto;
  }

  .bottom-grid .task-section {
    grid-column: auto;
  }

  .chart-section,
  .task-section {
    height: auto;
    min-height: 312px;
  }
}
</style>
