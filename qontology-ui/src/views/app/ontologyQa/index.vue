<template>
  <div class="ontology-qa">
    <aside class="session-panel">
      <el-button
        class="session-new-btn"
        type="primary"
        :icon="Plus"
        @click="resetChat"
        >新建对话</el-button
      >
      <div class="session-search">
        <Search />
        <input v-model="sessionSearch" placeholder="搜索历史会话" />
      </div>
      <div class="session-list">
        <div
          v-for="item in filteredSessions"
          :key="item.id"
          class="session-item"
          :class="{ active: item.id === activeSession }"
          @click="selectSession(item.id)"
        >
          <ChatDotRound />
          <span>{{ item.title }}</span>
          <small>{{ item.time }}</small>
        </div>
        <div v-if="!filteredSessions.length" class="empty-sessions">
          暂无匹配会话
        </div>
      </div>
    </aside>

    <main class="chat-panel">
      <header class="chat-header">
        <div class="header-title">
          <div class="assistant-avatar"><Cpu /></div>
          <div>
            <h1>本体知识助手</h1>
            <p>基于知识图谱的语义检索与关系推理</p>
          </div>
        </div>
        <div class="header-actions">
          <div class="header-project">
            <span class="header-project-label">当前项目</span>
            <el-select
              v-model="selectedProjectId"
              class="scope-select"
              @change="handleProjectChange"
            >
              <el-option
                v-for="item in projectOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              />
            </el-select>
          </div>
        </div>
      </header>

      <div v-if="showScope" class="scope-detail">
        <span v-for="tag in scopeTags" :key="tag"><i></i>{{ tag }}</span>
      </div>

      <section ref="messageList" class="message-list">
        <div v-if="messages.length === 0" class="welcome-state">
          <div class="welcome-orbit">
            <div class="orbit-dot dot-a"></div>
            <div class="orbit-dot dot-b"></div>
            <div class="assistant-avatar large"><Cpu /></div>
          </div>
          <h2>你好，我是本体知识助手</h2>
          <p>
            我可以帮你探索实体关系、定位业务规则，或从知识图谱中找到可信答案。
          </p>
          <div class="suggestion-grid">
            <button
              v-for="question in suggestions"
              :key="question"
              @click="ask(question)"
            >
              <span>{{ question }}</span
              ><ArrowRight />
            </button>
          </div>
        </div>
        <template v-else>
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-row"
            :class="message.role"
          >
            <div v-if="message.role === 'assistant'" class="message-avatar">
              <Cpu />
            </div>
            <div class="message-content">
              <div class="message-meta">
                <strong>{{
                  message.role === "user" ? "我" : "本体知识助手"
                }}</strong
                ><span>{{ message.time }}</span>
              </div>
              <div class="message-bubble" :class="{ loading: message.loading }">
                <template v-if="message.loading"
                  ><span class="loading-dots"><i></i><i></i><i></i></span>
                  正在检索本体关系…</template
                >
                <template v-else>
                  <p>{{ message.text }}</p>
                  <span v-if="message.streaming" class="stream-caret"></span>
                  <div
                    v-if="
                      message.role === 'assistant' &&
                      message.entities &&
                      !message.streaming
                    "
                    class="answer-extra"
                  >
                    <div class="answer-label"><Share /> 关联实体</div>
                    <div class="entity-links">
                      <span v-for="entity in message.entities" :key="entity">{{
                        entity
                      }}</span>
                    </div>
                    <div class="reasoning-strip">
                      <div class="answer-label"><Cpu /> 本体推理路径</div>
                      <div class="reasoning-steps">
                        <span
                          v-for="(step, stepIndex) in message.steps"
                          :key="step"
                          ><b>{{ stepIndex + 1 }}</b
                          >{{ step }}</span
                        >
                      </div>
                    </div>
                    <div class="source-block">
                      <div class="answer-label">
                        <CollectionTag /> 回答依据
                        <small
                          >已引用 {{ message.sources.length }} 条本体来源</small
                        >
                      </div>
                      <div
                        v-for="source in message.sources"
                        :key="source.title"
                        class="source-item"
                      >
                        <span class="source-type">{{ source.type }}</span>
                        <div>
                          <strong>{{ source.title }}</strong>
                          <p>{{ source.detail }}</p>
                        </div>
                        <ArrowRight />
                      </div>
                    </div>
                    <div class="answer-foot">
                      <span
                        ><CircleCheck /> 置信度 {{ message.confidence }}%</span
                      ><span>来源：{{ knowledgeBaseLabel }}</span>
                    </div>
                  </div>
                </template>
              </div>
            </div>
            <div v-if="message.role === 'user'" class="user-avatar">
              <UserFilled />
            </div>
          </div>
        </template>
      </section>

      <footer class="composer-wrap">
        <div class="quick-prompts">
          <span>试试这样问</span
          ><button
            v-for="question in quickPrompts"
            :key="question"
            @click="ask(question)"
          >
            {{ question }}
          </button>
        </div>
        <div class="composer" :class="{ focused: inputFocused }">
          <el-input
            v-model="draft"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 4 }"
            resize="none"
            placeholder="输入问题，探索本体中的知识关系…"
            @focus="inputFocused = true"
            @blur="inputFocused = false"
            @keydown.enter.exact.prevent="sendMessage"
          />
          <div class="composer-tools">
            <span>Enter 发送 · Shift + Enter 换行</span
            ><el-button
              class="send-button"
              type="primary"
              :icon="Promotion"
              :disabled="!draft.trim() || isLoading"
              @click="sendMessage"
            />
          </div>
        </div>
      </footer>
    </main>

    <aside class="insight-panel">
      <div class="insight-scroll">
        <div class="insight-body">
          <div class="insight-card health-card">
            <div class="card-top">
              <span class="card-title"><i></i>知识健康度</span>
            </div>
            <div class="health-score">
              <strong>94</strong><span>/ 100</span>
              <div class="health-ring"><i></i></div>
            </div>
            <div class="health-note"><CircleCheck /> 最近同步于 10 分钟前</div>
          </div>
          <div class="insight-card">
            <div class="card-top">
              <span class="card-title"><i></i>当前本体概览</span>
              <button class="icon-action" @click="showEntities = !showEntities">
                <ArrowDown :class="{ rotate: showEntities }" />
              </button>
            </div>
            <div class="stat-grid">
              <div><strong>128</strong><span>实体类型</span></div>
              <div><strong>46</strong><span>关系类型</span></div>
              <div><strong>2.4k</strong><span>实例数量</span></div>
              <div><strong>12</strong><span>业务规则</span></div>
            </div>
            <div v-if="showEntities" class="entity-list">
              <span v-for="entity in ontologyEntities" :key="entity.name"
                ><i :style="{ background: entity.color }"></i>{{ entity.name
                }}<b>{{ entity.count }}</b></span
              >
            </div>
          </div>
          <div class="insight-card relation-card">
            <div class="card-top">
              <span class="card-title"><i></i>关系路径预览</span>
              <el-button text>展开 <ArrowRight /></el-button>
            </div>
            <div ref="relationGraphRef" class="relation-graph"></div>
          </div>
          <div class="insight-card coverage-card">
            <div class="card-top">
              <span class="card-title"><i></i>本体覆盖概况</span>
            </div>
            <div class="coverage-row"><span>结构化实体</span><b>82%</b></div>
            <div class="coverage-track"><i style="width: 82%"></i></div>
            <div class="coverage-row"><span>业务规则</span><b>68%</b></div>
            <div class="coverage-track purple-track">
              <i style="width: 68%"></i>
            </div>
            <div class="coverage-row"><span>来源可追溯</span><b>96%</b></div>
            <div class="coverage-track green-track">
              <i style="width: 96%"></i>
            </div>
          </div>
          <div class="insight-card activity-card">
            <div class="card-top">
              <span class="card-title"><i></i>最近更新</span>
              <el-button text>查看全部 <ArrowRight /></el-button>
            </div>
            <div class="activity-item">
              <span class="activity-icon blue"><Refresh /></span>
              <div>
                <strong>知识库同步完成</strong>
                <p>新增 18 个实体，更新 42 条关系</p>
              </div>
              <time>10分钟前</time>
            </div>
            <div class="activity-item">
              <span class="activity-icon purple"><Document /></span>
              <div>
                <strong>规则文档已解析</strong>
                <p>《车辆检修规程 V3.2》</p>
              </div>
              <time>昨天</time>
            </div>
          </div>
          <!-- <div class="insight-tip">
            <InfoFilled />
            <div>
              <strong>让答案更可靠</strong>
              <p>选择合适的知识范围，助手会沿着本体关系进行多跳推理。</p>
            </div>
          </div> -->
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import {
  computed,
  getCurrentInstance,
  nextTick,
  onBeforeUnmount,
  onMounted,
  ref,
} from "vue";
import * as echarts from "echarts";
import { projectDict } from "@/api/basic/project/project";
import { getProjectVersionDict } from "@/api/basic/project/projectVersion";
import {
  ArrowDown,
  ArrowRight,
  ChatDotRound,
  CircleCheck,
  CollectionTag,
  Cpu,
  DataAnalysis,
  Document,
  Plus,
  Promotion,
  Refresh,
  Search,
  Share,
  UserFilled,
} from "@element-plus/icons-vue";

const { proxy } = getCurrentInstance();
const { qa_sync_status } = proxy.useDict("qa_sync_status");
const qaStatusOptions = [
  { label: "良好", value: "1", elTagType: "success", elTagClass: "" },
  { label: "一般", value: "2", elTagType: "warning", elTagClass: "" },
  { label: "差", value: "3", elTagType: "info", elTagClass: "" },
];
const fallbackProjects = [
  { dictValue: "demo-rail", dictLabel: "轨道交通设备项目" },
  { dictValue: "demo-energy", dictLabel: "能源运维知识项目" },
  { dictValue: "demo-general", dictLabel: "通用业务本体项目" },
];
const projectOptions = ref([]);
const selectedProjectId = ref("");
const boundOntologyVersion = ref("未配置版本");
const knowledgeBaseLabel = computed(
  () =>
    projectOptions.value.find(
      (item) => String(item.dictValue) === String(selectedProjectId.value)
    )?.dictLabel || "当前项目"
);
const currentScopeCount = computed(() =>
  String(selectedProjectId.value).includes("rail")
    ? "2,486"
    : String(selectedProjectId.value).includes("energy")
    ? "1,842"
    : "3,120"
);
const relationCount = computed(() =>
  String(selectedProjectId.value).includes("rail")
    ? "6,832"
    : String(selectedProjectId.value).includes("energy")
    ? "4,206"
    : "8,912"
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
    boundOntologyVersion.value = "设备本体 V1.0.0";
    return;
  }
  try {
    const response = await getProjectVersionDict(projectId);
    boundOntologyVersion.value =
      response?.data?.[0]?.dictLabel || "暂无已发布版本";
  } catch {
    boundOntologyVersion.value = "暂无已发布版本";
  }
}
const scopeTags = ["设备资产", "故障诊断", "维保工单", "零部件", "安全规范"];
const suggestions = [
  "设备A的常见故障有哪些？",
  "查询车门系统的上下游关系",
  "哪些设备需要季度检修？",
  "解释“牵引变流器”的业务定义",
];
const quickPrompts = ["查找设备关系", "分析故障原因", "查看检修规则"];
const ontologyEntities = [
  { name: "设备资产", count: "846", color: "#3478f6" },
  { name: "故障类型", count: "328", color: "#8a63e8" },
  { name: "维保工单", count: "612", color: "#19a88b" },
  { name: "零部件", count: "700", color: "#f0a23a" },
];
const sessionSearch = ref("");
const activeSession = ref(1);
const sessions = ref([
  { id: 1, title: "牵引变流器故障分析", time: "刚刚" },
  { id: 2, title: "设备检修关系查询", time: "昨天" },
  { id: 3, title: "车门系统业务定义", time: "10月28日" },
]);
const filteredSessions = computed(() =>
  sessions.value.filter((item) => item.title.includes(sessionSearch.value))
);
const messages = ref([]);
const draft = ref("");
const isLoading = ref(false);
const inputFocused = ref(false);
const showEntities = ref(true);
const showScope = ref(false);
const messageList = ref(null);

function now() {
  return new Intl.DateTimeFormat("zh-CN", {
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date());
}
function selectSession(id) {
  activeSession.value = id;
}
function resetChat() {
  messages.value = [];
  draft.value = "";
  isLoading.value = false;
  activeSession.value = Date.now();
}
function ask(question) {
  draft.value = question;
  nextTick(sendMessage);
}
function sendMessage() {
  const text = draft.value.trim();
  if (!text || isLoading.value) return;
  messages.value.push({ id: Date.now(), role: "user", text, time: now() });
  draft.value = "";
  isLoading.value = true;
  const loadingId = Date.now() + 1;
  messages.value.push({
    id: loadingId,
    role: "assistant",
    loading: true,
    time: now(),
  });
  nextTick(scrollToBottom);
  window.setTimeout(() => {
    const index = messages.value.findIndex((item) => item.id === loadingId);
    if (index < 0) return;
    const answer = buildAnswer(text);
    messages.value[index] = {
      id: loadingId,
      role: "assistant",
      text: "",
      fullText: answer,
      streaming: true,
      time: now(),
      entities: ["牵引变流器", "冷却系统", "故障代码 E-214"],
      confidence: 96,
      steps: [
        "识别问题中的业务实体",
        "沿 hasFault 关系检索故障",
        "匹配处置规则与依据",
      ],
      sources: [
        {
          type: "本体",
          title: "轨道交通设备本体 · 牵引变流器",
          detail: "实体：牵引变流器 / 关系：hasFault / 置信度 0.98",
        },
        {
          type: "规则",
          title: "车辆检修规程 V3.2 · R-024",
          detail: "规则：冷却风机异常时检查冷却回路与绝缘值",
        },
      ],
    };
    let cursor = 0;
    const streamTimer = window.setInterval(() => {
      const currentMessage = messages.value[index];
      if (!currentMessage) {
        window.clearInterval(streamTimer);
        return;
      }
      currentMessage.text += currentMessage.fullText.slice(cursor, cursor + 4);
      cursor += 4;
      nextTick(scrollToBottom);
      if (cursor >= currentMessage.fullText.length) {
        window.clearInterval(streamTimer);
        currentMessage.streaming = false;
        isLoading.value = false;
      }
    }, 38);
    nextTick(scrollToBottom);
  }, 800);
}
function buildAnswer(question) {
  if (question.includes("检修") || question.includes("季度"))
    return "根据“轨道交通设备本体”，牵引变流器、制动控制单元和车门控制器被定义为季度检修对象。它们通过「属于设备类型」关联至车辆电气系统，并由检修规则 R-024 约束周期。";
  if (question.includes("关系") || question.includes("上下游"))
    return "已沿本体关系完成 2 跳检索：车门系统关联 6 个零部件、3 类故障和 2 条维保规则。其中，车门控制器是故障传播路径上的关键实体。";
  return "在当前知识范围内，牵引变流器的高频关联故障是冷却风机异常与直流母线过压。知识图谱显示，故障代码 E-214 通常会触发「检查冷却回路」和「记录绝缘值」两项处置动作。";
}
function scrollToBottom() {
  if (messageList.value)
    messageList.value.scrollTop = messageList.value.scrollHeight;
}

/* ------------------------------ 关系路径预览图谱 ------------------------------ */
// 样式与「本体结构预览」保持一致，仅图谱内容替换为装备本体的关系路径。
const relationGraphRef = ref(null);
let relationChart = null;
let relationResizeObserver = null;

function initRelationGraph() {
  if (!relationGraphRef.value) return;
  relationChart = echarts.init(relationGraphRef.value);
  relationChart.setOption({
    animationDuration: 900,
    animationEasing: "cubicOut",
    tooltip: { show: false },
    series: [
      {
        type: "graph",
        layout: "none",
        roam: false,
        symbol: "circle",
        symbolSize: 56,
        label: { show: true, position: "inside", fontSize: 12 },
        edgeSymbol: ["none", "arrow"],
        edgeSymbolSize: 8,
        edgeLabel: {
          show: true,
          fontSize: 12,
          color: "#606266",
          backgroundColor: "#fff",
          padding: [1, 3],
          borderRadius: 2,
          formatter: "{c}",
        },
        lineStyle: { color: "#606266", width: 1.4, curveness: 0.14 },
        data: [
          {
            name: "型号",
            x: 52,
            y: 46,
            itemStyle: {
              color: "#eef1ff",
              borderColor: "#7b6cf0",
              borderWidth: 1.6,
            },
            label: { color: "#4a3fb8" },
          },
          {
            name: "设备",
            x: 144,
            y: 104,
            itemStyle: {
              color: "#2f73f5",
              shadowColor: "rgba(47,115,245,.35)",
              shadowBlur: 14,
            },
            label: { color: "#fff", fontWeight: 600 },
          },
          {
            name: "故障",
            x: 236,
            y: 46,
            itemStyle: {
              color: "#fff3e6",
              borderColor: "#f29a45",
              borderWidth: 1.6,
            },
            label: { color: "#b26112" },
          },
          {
            name: "维保工单",
            x: 144,
            y: 174,
            itemStyle: {
              color: "#e6f8f6",
              borderColor: "#37bec2",
              borderWidth: 1.6,
            },
            label: { color: "#0f8a86" },
          },
        ],
        links: [
          { source: "设备", target: "型号", value: "生产" },
          { source: "设备", target: "故障", value: "维护" },
          { source: "设备", target: "维保工单", value: "" },
        ],
      },
    ],
  });
}

function resizeRelationGraph() {
  if (relationChart && !relationChart.isDisposed()) relationChart.resize();
}

onMounted(async () => {
  await loadProjects();
  nextTick(() => {
    initRelationGraph();
    relationResizeObserver = new ResizeObserver(resizeRelationGraph);
    if (relationGraphRef.value)
      relationResizeObserver.observe(relationGraphRef.value);
  });
});

onBeforeUnmount(() => {
  relationResizeObserver?.disconnect();
  relationResizeObserver = null;
  relationChart?.dispose();
  relationChart = null;
});
</script>

<style lang="scss" scoped>
/*
 * 设计 Token 对齐首页 (system/index.vue)
 * 字号体系：21px(欢迎大标题) → 16px(板块标题/500~700) → 14px(正文/辅助/标签)
 * 颜色：重点 rgba(0,0,0,0.85) | 正文 rgba(0,0,0,0.65) | 辅助 var(--qt-text-secondary)
 * 分隔线：板块 #e8e8e8 | 卡片内 #e2e2e2
 * 圆角：统一 2px（头像 8px、气泡 12px 例外）
 * 主色：var(--el-color-primary)
 */
.ontology-qa {
  display: flex;
  height: calc(100vh - 94px);
  min-height: 620px;
  overflow: hidden;
  background: #f0f2f5;
  color: rgba(0, 0, 0, 0.85);
  font-family: PingFang SC;
  padding: 15px;
  box-sizing: border-box;
}
/* ---- 左侧会话面板 ---- */
.session-panel {
  width: 260px;
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  border-radius: 2px 0 0 2px;
  padding: 16px 14px 15px;
  box-sizing: border-box;
}
.session-new-btn {
  width: 100%;
  height: 36px;
  margin-bottom: 12px;
  border-radius: 2px;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.session-search {
  display: flex;
  align-items: center;
  gap: 7px;
  margin: 0 0 12px;
  height: 34px;
  padding: 0 10px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  color: var(--qt-text-secondary);
  background: #fff;
}
.session-search svg {
  width: 14px;
}
.session-search input {
  width: 100%;
  border: 0;
  outline: 0;
  background: transparent;
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  line-height: 22px;
}
.session-list {
  flex: 1;
  overflow: auto;
}
.session-item {
  display: grid;
  grid-template-columns: 16px 1fr auto;
  align-items: center;
  gap: 8px;
  min-height: 43px;
  margin: 3px 0;
  padding: 0 10px;
  border-radius: 2px;
  color: var(--qt-text-regular);
  cursor: pointer;
  font-size: 14px;
  line-height: 22px;
  transition: 0.15s;
}
.session-item svg {
  width: 14px;
}
.session-item small {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.session-item:hover {
  background: #f0f5ff;
  color: var(--el-color-primary);
}
.session-item.active {
  color: var(--el-color-primary);
  background: #eef3ff;
  font-weight: 500;
}
.empty-sessions {
  padding: 30px 10px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
  text-align: center;
}
/* ---- 中间聊天主面板 ---- */
.chat-panel {
  display: flex;
  flex: 1;
  min-height: 0;
  min-width: 450px;
  flex-direction: column;
  background: #fff;
  padding-bottom: 0;
  border-radius: 0;
}
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
  padding: 0 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
  box-sizing: border-box;
}
.header-title {
  display: flex;
  gap: 12px;
  align-items: center;
}
.assistant-avatar {
  display: grid;
  place-items: center;
  width: 41px;
  height: 41px;
  flex: 0 0 auto;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(145deg, #2b7de9 0%, #1687ff 26%, #2666fb 100%);
}
.assistant-avatar svg {
  width: 20px;
}
.assistant-avatar.large {
  width: 59px;
  height: 59px;
  border-radius: 18px;
  color: #fff;
  background: linear-gradient(145deg, #2b7de9 0%, #1687ff 26%, #2666fb 100%);
  box-shadow: 0 10px 24px rgba(24, 112, 238, 0.3);
}
.assistant-avatar.large svg {
  width: 31px;
}
.header-title h1 {
  margin: 0;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 600;
  line-height: 22px;
}
.header-title p {
  margin: 4px 0 0;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 13px;
}
.header-project {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-project-label {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.header-actions .scope-select {
  width: 220px;
}
.header-actions .scope-select :deep(.el-select__wrapper) {
  min-height: 30px;
  border-radius: 2px;
  background: #fff;
  font-size: 14px;
  box-shadow: 0 0 0 1px var(--qt-border-color) inset;
}
/* scope-bar */
.scope-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.scope-meta {
  display: flex;
  gap: 6px;
  align-items: center;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.scope-meta svg {
  width: 13px;
  color: var(--el-color-primary);
}
.scope-meta b {
  color: rgba(0, 0, 0, 0.85);
  font-weight: 400;
}
.scope-more {
  margin-left: auto;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.scope-more svg {
  width: 13px;
  margin-left: 3px;
}
.scope-detail {
  display: flex;
  gap: 7px;
  align-items: center;
  padding: 10px 28px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.scope-detail span {
  padding: 3px 8px;
  border-radius: 2px;
  color: var(--qt-text-regular);
  background: #f5f7fa;
  font-size: 14px;
  line-height: 22px;
}
/* message list */
.message-list {
  flex: 1;
  overflow: auto;
  padding: 20px 28px;
  background: #f8fafe;
}
.welcome-state {
  display: flex;
  height: 100%;
  min-height: 380px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}
.welcome-orbit {
  position: relative;
  margin-bottom: 18px;
}
.welcome-orbit::before,
.welcome-orbit::after {
  position: absolute;
  content: "";
  border: 1px solid #dbe5fd;
  border-radius: 50%;
}
.welcome-orbit::before {
  inset: -17px;
  transform: rotate(25deg) scaleX(1.45);
}
.welcome-orbit::after {
  inset: -27px;
  transform: rotate(-35deg) scaleX(1.7);
  opacity: 0.6;
}
.orbit-dot {
  position: absolute;
  z-index: 2;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #24c9f5;
}
.dot-a {
  top: -17px;
  left: -11px;
}
.dot-b {
  right: -20px;
  bottom: -7px;
  background: #176ff2;
}
.welcome-state h2 {
  margin: 8px 0 7px;
  color: rgba(0, 0, 0, 0.85);
  font-size: 21px;
  font-weight: 600;
  line-height: 29px;
}
.welcome-state > p {
  max-width: 420px;
  margin: 0;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.suggestion-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 9px;
  width: min(100%, 500px);
  margin-top: 27px;
}
.suggestion-grid button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 45px;
  padding: 0 13px;
  text-align: left;
  color: var(--qt-text-regular);
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  line-height: 22px;
  transition: 0.15s;
}
.suggestion-grid button:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}
.suggestion-grid svg {
  width: 14px;
  color: var(--qt-text-secondary);
}
/* messages */
.message-row {
  display: flex;
  gap: 10px;
  margin: 0 auto 21px;
  max-width: 780px;
}
.message-row.user {
  justify-content: flex-end;
}
.message-row.user .message-content {
  align-items: flex-end;
}
.message-avatar {
  display: grid;
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(145deg, #2b7de9 0%, #1687ff 26%, #2666fb 100%);
}
.message-avatar svg {
  width: 17px;
}
.user-avatar {
  display: grid;
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  place-items: center;
  border: 1px solid #e8e8e8;
  border-radius: 50%;
  color: var(--el-color-primary);
  background: #fff;
}
.user-avatar svg {
  width: 16px;
}
.message-content {
  display: flex;
  max-width: 78%;
  flex-direction: column;
}
.message-row.assistant .message-content {
  width: min(680px, calc(100% - 40px));
}
.message-meta {
  display: flex;
  gap: 8px;
  align-items: baseline;
  margin: 0 2px 5px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.message-meta strong {
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.message-bubble {
  box-sizing: border-box;
  width: 100%;
  min-height: 46px;
  padding: 12px 14px;
  border: 1px solid #e8e8e8;
  border-radius: 2px 12px 12px 12px;
  color: var(--qt-text-regular);
  background: #fff;
  font-size: 14px;
  line-height: 22px;
}
.message-bubble p {
  margin: 0;
}
.user .message-bubble {
  width: auto;
  min-height: 0;
  border: 0;
  border-radius: 12px 2px 12px 12px;
  color: #fff;
  background: var(--el-color-primary);
}
.message-bubble.loading {
  color: var(--qt-text-secondary);
}
.loading-dots i {
  display: inline-block;
  width: 5px;
  height: 5px;
  margin: 0 2px;
  border-radius: 50%;
  background: #8a63e8;
  animation: blink 1.1s infinite;
}
.loading-dots i:nth-child(2) {
  animation-delay: 0.15s;
}
.loading-dots i:nth-child(3) {
  animation-delay: 0.3s;
}
/* answer extra */
.answer-extra {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid #e8e8e8;
}
.answer-label {
  display: flex;
  gap: 5px;
  align-items: center;
  margin-bottom: 7px;
  color: var(--qt-text-regular);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.answer-label svg {
  width: 13px;
  color: var(--el-color-primary);
}
.entity-links {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.entity-links span {
  padding: 3px 7px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  color: var(--qt-text-regular);
  background: #fff;
  font-size: 14px;
  line-height: 22px;
}
.answer-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.answer-foot svg {
  width: 12px;
  margin-right: 3px;
  vertical-align: -2px;
  color: #1caf85;
}
/* reasoning */
.reasoning-strip {
  margin-top: 11px;
  padding: 9px 10px;
  border-radius: 2px;
  background: #f5f7fa;
}
.reasoning-steps {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.reasoning-steps span {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.reasoning-steps b {
  display: flex;
  place-items: center;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  color: #fff;
  background: var(--el-color-primary);
  font-size: 12px;
  align-items: center;
  justify-content: space-evenly;
}
/* source block */
.source-block {
  margin-top: 11px;
}
.source-block .answer-label small {
  margin-left: auto;
  color: var(--qt-text-secondary);
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
}
.source-item {
  display: flex;
  align-items: flex-start;
  gap: 7px;
  padding: 7px 0;
  border-top: 1px solid #e8e8e8;
}
.source-item .source-type {
  flex: 0 0 auto;
  padding: 2px 4px;
  border-radius: 2px;
  color: var(--el-color-primary);
  background: #eff3ff;
  font-size: 14px;
  line-height: 22px;
}
.source-item > div {
  min-width: 0;
  flex: 1;
}
.source-item strong {
  display: block;
  overflow: hidden;
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.source-item p {
  margin: 2px 0 0;
  overflow: hidden;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.source-item > svg {
  width: 12px;
  margin-top: 4px;
  color: var(--qt-text-secondary);
}
/* composer */
.composer-wrap {
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
}
.quick-prompts {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 9px;
  overflow: auto;
  white-space: nowrap;
}
.quick-prompts > span {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.quick-prompts button {
  padding: 4px 10px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  color: var(--qt-text-regular);
  background: #fff;
  cursor: pointer;
  font-size: 12px;
  line-height: 22px;
  transition: 0.15s;
}
.quick-prompts button:hover {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}
.composer {
  padding: 10px 11px 7px 14px;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  background: #fff;
}
.composer.focused {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px rgba(38, 102, 251, 0.1);
}
.composer :deep(.el-textarea__inner) {
  padding: 0;
  border: 0;
  box-shadow: none;
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  line-height: 22px;
}
.composer-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 5px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.send-button {
  width: 30px;
  height: 30px;
  border: 0;
  border-radius: 2px;
  padding: 0;
  box-shadow: 0 5px 11px rgba(38, 102, 251, 0.17);
}
.send-button :deep(svg) {
  width: 15px;
}
/* ---- 右侧 insight ---- */
.insight-panel {
  display: flex;
  width: 320px;
  height: 100%;
  min-height: 0;
  flex-shrink: 0;
  flex-direction: column;
  overflow: hidden;
  padding: 0;
  border-left: 1px solid #e8e8e8;
  background: #f0f2f5;
  border-radius: 0 2px 2px 0;
  box-sizing: border-box;
}
.insight-scroll {
  flex: 1;
  min-height: 0;
  overflow: auto;
}
.insight-body {
  overflow: hidden;
  margin: 0;
  border-radius: 0;
  background: #fff;
}
.insight-card {
  padding: 14px 15px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.insight-card:last-child {
  border-bottom: 0;
}
.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 46px;
  margin: -14px -15px 12px;
  padding: 0 15px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.card-top .card-title {
  display: flex;
  align-items: center;
  min-width: 0;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
  line-height: 22px;
}
.card-top .card-title i {
  flex-shrink: 0;
  width: 6px;
  height: 16px;
  margin-right: 8px;
  border-radius: 3px;
  background: var(--el-color-primary);
}

.health-score {
  display: flex;
  align-items: baseline;
  margin: 12px 0 6px;
}
.health-score strong {
  color: var(--el-color-primary);
  font-family: Sharp;
  font-size: 30px;
  line-height: 1;
}
.health-score > span {
  margin-left: 4px;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.health-ring {
  width: 44px;
  height: 44px;
  margin-left: auto;
  padding: 4px;
  border-radius: 50%;
  background: conic-gradient(var(--el-color-primary) 0 94%, #e8e8e8 94% 100%);
}
.health-ring i {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #fff;
}
.health-note {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.health-note svg {
  width: 12px;
  margin-right: 3px;
  color: #1caf85;
  vertical-align: -2px;
}
/* stat grid */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 5px;
  margin-top: 12px;
}
.stat-grid div {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 7px 2px;
  border-radius: 2px;
  text-align: center;
  background: #f5f7fa;
}
.stat-grid strong {
  color: rgba(0, 0, 0, 0.85);
  font-family: Sharp;
  font-size: 16px;
  line-height: 22px;
}
.stat-grid span {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
}
.icon-action {
  padding: 3px;
  border: 0;
  color: var(--qt-text-secondary);
  background: transparent;
  cursor: pointer;
}
.icon-action svg {
  width: 13px;
  transition: 0.15s;
}
.icon-action svg.rotate {
  transform: rotate(180deg);
}
.entity-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 9px;
  margin-top: 11px;
  padding-top: 11px;
  border-top: 1px solid #e8e8e8;
}
.entity-list span {
  display: flex;
  align-items: center;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.entity-list i {
  width: 7px;
  height: 7px;
  margin-right: 7px;
  border-radius: 2px;
}
.entity-list b {
  margin-left: auto;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
}
/* relation / insight buttons */
.relation-card .card-top .el-button,
.activity-card .card-top .el-button {
  padding: 0;
  color: var(--el-color-primary);
  font-size: 14px;
  line-height: 22px;
}
.relation-card .card-top .el-button svg,
.activity-card .card-top .el-button svg {
  width: 12px;
  margin-left: 2px;
  vertical-align: -2px;
}
.relation-graph {
  height: 220px;
  margin-top: 4px;
}
/* coverage */
.coverage-card {
  padding-bottom: 14px;
}
.coverage-row {
  display: flex;
  justify-content: space-between;
  margin-top: 13px;
  color: var(--qt-text-regular);
  font-size: 14px;
  line-height: 22px;
}
.coverage-row b {
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}

.coverage-track {
  height: 4px;
  margin-top: 6px;
  overflow: hidden;
  border-radius: 2px;
  background: #e8e8e8;
}
.coverage-track i {
  display: block;
  height: 100%;
  border-radius: 2px;
  background: var(--el-color-primary);
}
.coverage-track.purple-track i {
  background: #8b63dd;
}
.coverage-track.green-track i {
  background: #1cac84;
}
/* activity */
.activity-item {
  display: flex;
  gap: 7px;
  align-items: center;
  margin-top: 14px;
}
.activity-icon {
  display: grid;
  place-items: center;
  width: 35px;
  height: 35px;
  flex: 0 0 auto;
  border-radius: 8px;
}
.activity-icon svg {
  width: 12px;
}
.activity-icon.blue {
  color: var(--el-color-primary);
  background: #eff3ff;
}
.activity-icon.purple {
  color: #8b63dd;
  background: #f4efff;
}
.activity-item > div {
  min-width: 0;
  flex: 1;
}
.activity-item strong {
  display: block;
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
}
.activity-item p {
  margin: 2px 0 0;
  overflow: hidden;
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.activity-item time {
  color: var(--qt-text-secondary);
  font-size: 14px;
  line-height: 22px;
  white-space: nowrap;
}
/* stream caret */
.stream-caret {
  display: inline-block;
  width: 2px;
  height: 14px;
  margin-left: 3px;
  vertical-align: -2px;
  background: var(--el-color-primary);
  animation: caret 0.75s step-end infinite;
}
@keyframes blink {
  0%,
  80%,
  100% {
    opacity: 0.25;
    transform: translateY(0);
  }
  40% {
    opacity: 1;
    transform: translateY(-2px);
  }
}
@keyframes caret {
  0%,
  45% {
    opacity: 1;
  }
  46%,
  100% {
    opacity: 0;
  }
}
/* responsive */
@media (max-width: 1200px) {
  .insight-panel {
    width: 280px;
  }
  .session-panel {
    width: 205px;
  }
  .scope-meta {
    display: none;
  }
}
@media (max-width: 900px) {
  .insight-panel {
    display: none;
  }
  .session-panel {
    width: 200px;
  }
  .message-list {
    padding-left: 5%;
    padding-right: 5%;
  }
  .composer-wrap {
    padding-left: 5%;
    padding-right: 5%;
  }
}
@media (max-width: 650px) {
  .session-panel {
    display: none;
  }
  .chat-panel {
    min-width: 0;
  }
  .chat-header,
  .scope-bar {
    padding-left: 16px;
    padding-right: 16px;
  }
  .header-title p,
  .header-project-label,
  .scope-more {
    display: none;
  }
  .header-actions .scope-select {
    width: 150px;
  }
  .message-list {
    padding: 18px 16px;
  }
  .composer-wrap {
    padding: 0 16px 13px;
  }
  .suggestion-grid {
    grid-template-columns: 1fr;
  }
  .welcome-state h2 {
    font-size: 18px;
  }
  .message-content {
    max-width: 88%;
  }
}
</style>
<style lang="scss">
.el-popper.is-light.overview-text-tooltip {
  max-width: 426px;
  padding: 10px 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  color: var(--el-text-color-primary);
  font-size: 14px;
  line-height: 22px;
  overflow-wrap: anywhere;
  white-space: normal;
}
</style>
