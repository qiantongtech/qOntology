<template>
  <div class="application-center app-container">
    <section class="overview-panel">
      <div class="overview-banner">
        <div class="banner-content">
          <span class="h1-titles">统一汇聚本体应用能力</span>
          <span class="p-titles"
            >连接项目本体、授权数据与业务场景，持续沉淀可复用的智能应用</span
          >
        </div>
      </div>
      <div class="module-3">
        <div
          v-for="(item, index) in statistics"
          :key="index"
          class="module-item"
        >
          <div class="module-item-t">
            <div class="module-item-t-l">
              <div class="name">{{ item.name }}</div>
              <span>{{ item.value }}</span>
            </div>
            <img :src="item.img" class="module-item-t-r" alt="" />
          </div>
          <div class="module-item-border"></div>
          <div class="module-item-data">
            <span>{{ item.footerLabel }}</span>
            <span
              :class="
                item.up === null
                  ? 'data-equal'
                  : item.up
                  ? 'data-up'
                  : 'data-down'
              "
              >{{ item.speed }}{{ item.suffix }}</span
            >
          </div>
        </div>
      </div>
    </section>

    <section class="content-panel">
      <div class="section-header">
        <div>
          <div class="h2-titles">核心应用</div>
        </div>
        <el-button link type="primary" @click="scrollToAll"
          >查看全部<ArrowRight
        /></el-button>
      </div>
      <div class="featured-grid">
        <article
          v-for="item in featuredApplications"
          :key="item.id"
          class="featured-card"
          @click="openApplication(item)"
        >
          <div class="featured-card__top">
            <div class="featured-title">
              <img :src="item.image" class="application-icon" alt="" />
              <div>
                <h3>{{ item.name }}</h3>
                <div class="tag-list">
                  <el-tag v-for="tag in item.tags" :key="tag">{{ tag }}</el-tag>
                </div>
              </div>
            </div>
            <dict-tag :options="publish_status" :value="item.status" />
          </div>
          <p>{{ item.description }}</p>
          <div class="featured-card__footer">
            <el-button type="primary" @click.stop="openApplication(item)"
              >立即使用</el-button
            ><el-button plain @click.stop="showDetail(item)"
              >查看详情</el-button
            >
          </div>
        </article>
      </div>
    </section>

    <section ref="allApplicationsRef" class="content-panel">
      <div class="application-toolbar pagecont-all-top">
        <div class="toolbar-left">
          <div class="h2-titles">全部应用</div>
        </div>
        <div class="toolbar-right">
          <el-input
            v-model="keyword"
            :prefix-icon="Search"
            placeholder="搜索应用名称"
            clearable
          />
        </div>
      </div>
      <div class="application-grid">
        <article
          v-for="item in filteredApplications"
          :key="item.id"
          class="application-card"
        >
          <div class="application-card__head">
            <div class="application-title">
              <img :src="item.image" class="application-icon" alt="" />
              <div>
                <h3>{{ item.name }}</h3>
                <div class="tag-list compact">
                  <el-tag v-for="tag in item.tags" :key="tag">{{ tag }}</el-tag>
                </div>
              </div>
            </div>
            <dict-tag :options="publish_status" :value="item.status" />
          </div>
          <p class="application-description">{{ item.description }}</p>
          <div class="application-card__footer">
            <el-button
              v-if="item.status === '1'"
              type="primary"
              @click="openApplication(item)"
              >立即使用</el-button
            ><el-button v-else type="primary" disabled>未发布</el-button
            ><el-button @click="showDetail(item)">查看详情</el-button>
          </div>
        </article>
        <el-empty
          v-if="!filteredApplications.length"
          description="暂无匹配应用"
        />
      </div>
    </section>

    <el-dialog
      v-model="detailVisible"
      width="640px"
      :title="currentApp?.name || '应用详情'"
    >
      <div v-if="currentApp" class="detail-content">
        <div class="detail-head">
          <img :src="currentApp.image" class="application-icon" alt="" />
          <div>
            <div class="detail-name">
              <strong>{{ currentApp.name }}</strong
              ><dict-tag :options="publish_status" :value="currentApp.status" />
            </div>
            <p>{{ currentApp.description }}</p>
          </div>
        </div>
        <div class="detail-block">
          <div class="h2-titles">本体应用能力</div>
          <div class="detail-features">
            <span v-for="feature in currentApp.features" :key="feature"
              ><CircleCheck />{{ feature }}</span
            >
          </div>
        </div>
        <div class="detail-block">
          <div class="h2-titles">应用范围</div>
          <div class="detail-scope">
            <Collection />{{ currentApp.ontology }}
          </div>
        </div>
      </div>
      <template #footer
        ><el-button @click="detailVisible = false">关闭</el-button
        ><el-button
          v-if="currentApp?.status === '1'"
          type="primary"
          @click="openApplication(currentApp)"
          >进入应用</el-button
        ></template
      >
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, ref } from "vue";
import { useRouter } from "vue-router";
import {
  ArrowRight,
  CircleCheck,
  Collection,
  Search,
} from "@element-plus/icons-vue";

import questionImage from "@/assets/system/images/entrance/bot.png";
import agentImage from "@/assets/system/images/entrance/plugin.png";
import analysisImage from "@/assets/system/images/entrance/struct.png";
import graphImage from "@/assets/system/images/entrance/graph.png";
import simulationImage from "@/assets/system/images/entrance/unstruct.png";
import builderImage from "@/assets/system/images/entrance/kb.png";
import statisticImage1 from "@/assets/system/images/index/1.png";
import statisticImage2 from "@/assets/system/images/index/2.png";
import statisticImage3 from "@/assets/system/images/index/3.png";
import statisticImage4 from "@/assets/system/images/index/4.png";
import statisticImage5 from "@/assets/system/images/index/5.png";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { publish_status } = proxy.useDict("publish_status");
const category = ref("all");
const keyword = ref("");
const detailVisible = ref(false);
const currentApp = ref(null);
const allApplicationsRef = ref(null);
const categories = [
  { label: "全部应用", value: "all" },
  { label: "智能交互", value: "interaction" },
  { label: "分析决策", value: "analysis" },
  { label: "业务协同", value: "collaboration" },
  { label: "应用建设", value: "construction" },
];
const applications = [
  {
    id: 1,
    name: "智能问答",
    type: "interaction",
    status: "1",
    route: "/app/ontologyQa",
    image: questionImage,
    tags: ["智能问答", "本体检索"],
    ontology: "对象、关系与属性语义",
    description:
      "基于项目本体和授权数据回答业务问题，并展示对象、关系与数据来源依据。",
    features: ["自然语言业务问答", "对象关系语义识别", "答案依据可追溯"],
  },
  {
    id: 2,
    name: "业务智能体",
    type: "collaboration",
    status: "1",
    route: "/app/businessAgent",
    image: agentImage,
    tags: ["智能体", "业务协同"],
    ontology: "查询、函数与业务动作",
    description:
      "根据业务目标调用本体查询、分析能力和系统服务，在授权范围内协助完成任务。",
    features: ["项目本体能力配置", "业务目标智能识别", "对话调试与预览"],
  },
  {
    id: 3,
    name: "分析看板",
    type: "analysis",
    status: "1",
    route: "/app/analysisDashboard",
    image: analysisImage,
    tags: ["分析决策", "业务指标"],
    ontology: "对象集、指标与质量规则",
    description:
      "基于项目绑定本体、对象集和指标构建分析视图，展示业务状态与异常变化。",
    features: ["通用项目指标分析", "对象与关系分布", "规则质量异常趋势"],
  },
  {
    id: 4,
    name: "知识图谱",
    type: "analysis",
    status: "1",
    route: "/app/graph",
    image: graphImage,
    tags: ["知识图谱", "关系探索"],
    ontology: "实例对象与关系网络",
    description:
      "以图形方式浏览项目本体实例和关系网络，支持对象定位、路径查询与详情查看。",
    features: ["对象关系可视化", "多跳关系路径探索", "对象详情联动"],
  },
  {
    id: 5,
    name: "场景推演",
    type: "analysis",
    status: "0",
    route: "",
    image: simulationImage,
    tags: ["场景推演", "方案比较"],
    ontology: "业务假设与影响关系",
    description:
      "在隔离环境中调整业务假设和本体对象参数，对比不同方案对业务指标的影响。",
    features: ["业务假设参数配置", "多方案结果对比", "影响关系路径分析"],
  },
  {
    id: 6,
    name: "应用构建",
    type: "construction",
    status: "0",
    route: "",
    image: builderImage,
    tags: ["应用构建", "页面编排"],
    ontology: "对象视图、表单与动作",
    description:
      "组合对象视图、分析图表、业务表单和系统动作，构建面向业务人员的操作页面。",
    features: ["对象组件灵活编排", "表单图表组合", "应用发布管理"],
  },
];
const featuredApplications = computed(() =>
  applications.filter((item) => item.status === "1")
);
const onlineCount = computed(
  () => applications.filter((item) => item.status === "1").length
);
const statistics = computed(() => [
  {
    name: "全部应用",
    value: applications.length,
    img: statisticImage1,
    footerLabel: "覆盖业务场景：",
    speed: 5,
    suffix: "类",
    up: null,
  },
  {
    name: "已发布应用",
    value: onlineCount.value,
    img: statisticImage2,
    footerLabel: "应用发布率：",
    speed: 67,
    suffix: "%",
    up: true,
  },
  {
    name: "智能交互",
    value: categoryCount("interaction"),
    img: statisticImage3,
    footerLabel: "本体问答应用：",
    speed: 1,
    suffix: "个",
    up: null,
  },
  {
    name: "分析决策",
    value: categoryCount("analysis"),
    img: statisticImage4,
    footerLabel: "分析类应用：",
    speed: 3,
    suffix: "个",
    up: null,
  },
  {
    name: "业务协同",
    value: categoryCount("collaboration"),
    img: statisticImage5,
    footerLabel: "协同类应用：",
    speed: 1,
    suffix: "个",
    up: null,
  },
  {
    name: "未发布应用",
    value: applications.length - onlineCount.value,
    img: statisticImage2,
    footerLabel: "待发布应用：",
    speed: 2,
    suffix: "个",
    up: null,
  },
]);
const filteredApplications = computed(() => {
  const searchText = keyword.value.trim();
  return applications.filter(
    (item) =>
      (category.value === "all" || item.type === category.value) &&
      (!searchText ||
        (item.name + item.description + item.tags.join("")).includes(
          searchText
        ))
  );
});
function categoryCount(value) {
  return value === "all"
    ? applications.length
    : applications.filter((item) => item.type === value).length;
}
function openApplication(item) {
  if (item.route) router.push(item.route);
}
function showDetail(item) {
  currentApp.value = item;
  detailVisible.value = true;
}
function scrollToAll() {
  allApplicationsRef.value?.scrollIntoView({
    behavior: "smooth",
    block: "start",
  });
}
</script>

<style scoped lang="scss">
/*
 * 设计 Token 对齐首页 (system/index.vue)
 * 字号体系：21px(banner大标题) → 16px(板块标题/500~600) → 14px(正文/辅助/标签/描述)
 * 颜色：重点 rgba(0,0,0,0.85) | 正文 rgba(0,0,0,0.65) | 辅助 #888888
 * 分隔线：板块 #e8e8e8 | 卡片内 #e2e2e2
 * 圆角：统一 2px
 * 主色：var(--el-color-primary)
 */
.application-center {
  min-height: calc(100vh - 84px);
  padding: 15px;
  background: #f0f2f5;
  box-sizing: border-box;
}
/* ---- 顶部 Banner ---- */
.overview-banner {
  position: relative;
  height: 120px;
  padding: 0 40px;
  overflow: hidden;
  background: #eaf3ff url("@/assets/system/images/index/banner.png") right
    center/auto 100% no-repeat;
  box-sizing: border-box;
  margin-bottom: 15px;
  border-radius: 2px;
}
.overview-banner:after {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    rgba(234, 243, 255, 0.99),
    rgba(234, 243, 255, 0.86) 45%,
    rgba(234, 243, 255, 0) 75%
  );
  content: "";
}
.banner-content {
  position: relative;
  z-index: 1;
  display: flex;
  height: 100%;
  flex-direction: column;
  justify-content: center;
}
.banner-content .h1-titles {
  color: rgba(0, 0, 0, 0.85);
  font-family: PingFang SC;
  font-size: 21px;
  font-weight: 600;
  line-height: 29px;
}
.banner-content .p-titles {
  margin-top: 8px;
  color: var(--qt-text-regular);
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
}
/* ---- module-item 统计卡片 ---- */
.module-3 {
  display: flex;
  justify-content: space-between;
  height: 150px;
  margin-bottom: 15px;
  gap: 15px;
}
.module-3 .module-item {
  flex: 1;
  height: 100%;
  border-radius: 2px;
  padding: 20px;
  background: #fff;
}
.module-3 .module-item .module-item-t {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.module-3 .module-item .module-item-t .module-item-t-l {
  font-family: PingFang SC;
  font-size: 14px;
  color: #888888;
  line-height: 22px;
  font-weight: 400;
}
.module-3 .module-item .module-item-t .module-item-t-l .name {
  margin-bottom: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.module-3 .module-item .module-item-t .module-item-t-l span {
  font-family: Sharp;
  font-size: 30px;
  color: rgba(0, 0, 0, 0.85);
}
.module-3 .module-item .module-item-t .module-item-t-r {
  width: 48px;
  height: 48px;
}
.module-3 .module-item .module-item-border {
  height: 1px;
  background: #e2e2e2;
  margin: 20px 0 12px;
}
.module-3 .module-item .module-item-data {
  display: flex;
  align-items: center;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.module-3 .module-item .module-item-data .data-up,
.module-3 .module-item .module-item-data .data-down,
.module-3 .module-item .module-item-data .data-equal {
  font-size: 14px;
  line-height: 22px;
  margin: 0 10px;
  color: rgba(0, 0, 0, 0.85);
  position: relative;
}
.module-3 .module-item .module-item-data .data-up:after {
  content: "";
  position: absolute;
  top: 1px;
  right: -16px;
  width: 0;
  height: 12px;
  border: 6px solid #f5222d;
  border-color: transparent transparent #f5222d transparent;
}
.module-3 .module-item .module-item-data .data-down:after {
  content: "";
  position: absolute;
  top: 4px;
  right: -16px;
  width: 0;
  height: 12px;
  border: 6px solid #52c41a;
  border-color: transparent transparent transparent #52c41a;
}
.application-icon {
  width: 48px;
  height: 48px;
  flex: none;
  object-fit: contain;
}
/* ---- content panel ---- */
.content-panel {
  padding: 18px 20px 20px;
  background: #fff;
  border-radius: 2px;
  margin-bottom: 15px;
}
.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 13px;
  border-bottom: 1px solid #e8e8e8;
}
.section-header .h2-titles {
  margin: 0;
}
.section-header p {
  margin: 6px 0 0 14px;
  color: #888888;
  font-size: 14px;
  line-height: 22px;
}
.section-header :deep(.el-button) {
  height: 24px;
  padding: 0;
  font-size: 14px;
}
.section-header :deep(.el-button svg) {
  width: 13px;
  margin-left: 3px;
}
/* ---- featured & application grid ---- */
.featured-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
}
.featured-card,
.application-card {
  display: flex;
  min-height: 198px;
  flex-direction: column;
  padding: 18px 20px 16px;
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
  border: 1px solid #e8e8e8;
}
.featured-card {
  cursor: pointer;
}
.featured-card__top,
.application-card__head,
.application-card__footer,
.featured-card__footer,
.detail-name {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
.featured-title,
.application-title {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 12px;
}
.featured-card h3,
.application-title h3 {
  margin: 0 0 7px;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
  line-height: 19px;
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.featured-card > p,
.application-description {
  height: 42px;
  margin: 15px 0 14px;
  overflow: hidden;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.featured-card__footer,
.application-card__footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-top: auto;
}
.featured-card__footer :deep(.el-button),
.application-card__footer :deep(.el-button) {
  width: 100%;
  height: 31px;
  margin: 0;
  border-radius: 2px;
  font-size: 14px;
}
/* ---- toolbar ---- */
.application-toolbar.pagecont-all-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0;
  padding-bottom: 13px;
  border-bottom: 1px solid #e8e8e8;
}
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 24px;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
}
.toolbar-right > span {
  color: #888888;
  font-size: 14px;
  line-height: 22px;
}
.application-toolbar :deep(.el-input) {
  width: 220px;
}
.application-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
  margin-top: 14px;
}
.application-card {
  min-height: 202px;
}
.application-title {
  align-items: flex-start;
}
.application-grid > :deep(.el-empty) {
  grid-column: 1/-1;
}
/* ---- 详情弹框 ---- */
.detail-head {
  display: flex;
  gap: 14px;
  padding: 2px 0 18px;
  border-bottom: 1px solid #e8e8e8;
}
.detail-head > div {
  flex: 1;
  min-width: 0;
}
.detail-name {
  justify-content: flex-start;
  gap: 10px;
}
.detail-name strong {
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
  line-height: 22px;
}
.detail-head p {
  margin: 8px 0 0;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.detail-block {
  margin-top: 20px;
}
.detail-block .h2-titles {
  margin-bottom: 12px;
}
.detail-features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.detail-features span,
.detail-scope {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  color: var(--qt-text-regular);
  background: #fff;
  font-size: 14px;
  line-height: 22px;
}
.detail-features svg,
.detail-scope svg {
  width: 14px;
  color: var(--el-color-primary);
}
@media (max-width: 1600px) {
  .featured-grid,
  .application-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (max-width: 1100px) {
  .featured-grid,
  .application-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 760px) {
  .featured-grid,
  .application-grid,
  .application-toolbar {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
    padding: 12px;
  }
  .toolbar-right {
    width: 100%;
  }
  .application-toolbar :deep(.el-input) {
    width: 100%;
  }
}
</style>
