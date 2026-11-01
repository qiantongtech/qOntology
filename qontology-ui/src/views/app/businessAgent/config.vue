<template>
  <div class="config-page app-container">
    <div class="workspace">
      <main class="setting-panel">
        <div class="panel-head"><div class="h2-titles">智能体配置</div></div>
        <div class="panel-body">
          <el-form :model="form" label-width="96px" class="agent-form">
            <el-form-item label="智能体名称" required
              ><el-input v-model="form.name" maxlength="30" show-word-limit
            /></el-form-item>
            <el-form-item label="关联项目" required
              ><el-select v-model="form.projectId" @change="loadVersions"
                ><el-option
                  v-for="item in projectOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue" /></el-select
            ></el-form-item>
            <el-form-item label="本体版本" required
              ><el-select v-model="form.versionId"
                ><el-option
                  v-for="item in versionOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue" /></el-select
            ></el-form-item>
            <el-form-item label="功能描述"
              ><el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                maxlength="200"
                show-word-limit
            /></el-form-item>
            <el-form-item label="业务目标"
              ><el-input v-model="form.goal" type="textarea" :rows="3"
            /></el-form-item>
            <el-form-item label="开场提示"
              ><el-input v-model="form.welcome"
            /></el-form-item>
          </el-form>

          <el-divider />
          <div class="h2-titles">业务能力</div>
          <div class="ability-list">
            <div
              v-for="group in abilityGroups"
              :key="group.title"
              class="ability-group"
            >
              <div class="ability-head">
                <span :style="{ color: group.color, background: group.bg }"
                  ><component :is="group.icon"
                /></span>
                <div>
                  <strong>{{ group.title }}</strong
                  ><small>{{ group.desc }}</small>
                </div>
              </div>
              <div
                v-for="item in group.items"
                :key="item.id"
                class="ability-row"
              >
                <el-checkbox v-model="item.enabled"
                  ><b>{{ item.name }}</b
                  ><small>{{ item.description }}</small></el-checkbox
                ><dict-tag
                  :options="abilityStatusOptions"
                  :value="item.enabled ? 'enabled' : 'disabled'"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="panel-foot">
          <el-button type="primary" @click="saveDraft">保存</el-button>
        </div>
      </main>

      <aside class="debug-panel">
        <div class="debug-title"><div class="h2-titles">调试与预览</div></div>
        <div ref="debugMessages" class="messages">
          <div v-if="!messages.length" class="empty-debug">
            <ChatLineSquare /><strong>在下方输入业务问题</strong>
            <p>测试对象识别、本体查询和业务能力调用效果</p>
            <button
              v-for="item in examples"
              :key="item"
              @click="debugInput = item"
            >
              {{ item }}
            </button>
          </div>
          <div
            v-for="message in messages"
            :key="message.id"
            class="message"
            :class="message.role"
          >
            <span
              ><component :is="message.role === 'user' ? UserFilled : Cpu"
            /></span>
            <div>
              <p>{{ message.text }}</p>
              <div v-if="message.sources" class="source-box">
                <b>本体依据</b
                ><small v-for="source in message.sources" :key="source">{{
                  source
                }}</small>
              </div>
            </div>
          </div>
        </div>
        <div class="debug-foot">
          <div class="composer" :class="{ focused: debugFocused }">
            <el-input
              v-model="debugInput"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              resize="none"
              placeholder="请输入业务问题（Enter 发送）"
              @focus="debugFocused = true"
              @blur="debugFocused = false"
              @keydown.enter.exact.prevent="sendDebug"
            />
            <div class="composer-tools">
              <span>使用当前配置进行模拟，不写入业务数据</span>
              <el-button
                class="send-button"
                type="primary"
                circle
                :icon="Promotion"
                :loading="debugging"
                @click="sendDebug"
              />
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";
import {
  ChatLineSquare,
  Cpu,
  DataAnalysis,
  Promotion,
  Search,
  Tools,
  UserFilled,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { projectDict } from "@/api/basic/project/project";
import { getProjectVersionDict } from "@/api/basic/project/projectVersion";
const route = useRoute();
const projectOptions = ref([]);
const versionOptions = ref([]);
const debugInput = ref("");
const debugFocused = ref(false);
const messages = ref([]);
const debugging = ref(false);
const debugMessages = ref(null);
const abilityStatusOptions = [
  { label: "已启用", value: "enabled", elTagType: "success", elTagClass: "" },
  { label: "未启用", value: "disabled", elTagType: "info", elTagClass: "" },
];
const form = reactive({
  name: route.params.id ? "业务对象查询助手" : "",
  description: "面向业务人员查询项目对象和关联关系，并提供可追溯的本体依据。",
  goal: "理解业务问题，调用项目本体与系统能力生成准确、可解释的业务结果。",
  welcome: "你好，我可以帮助你查询业务对象、分析关系并定位本体依据。",
  mode: "confirm",
  projectId: "",
  versionId: "",
  objectTypes: ["core", "related", "event"],
});
const abilityGroups = reactive([
  {
    title: "本体检索",
    desc: "对象、属性、关系与对象集查询",
    icon: Search,
    color: "#2666fb",
    bg: "#edf3ff",
    items: [
      {
        id: 1,
        name: "对象条件查询",
        description: "按对象类型与属性条件检索实例",
        enabled: true,
      },
      {
        id: 2,
        name: "关系路径追溯",
        description: "沿本体关系查询上下游对象",
        enabled: true,
      },
      {
        id: 3,
        name: "对象集查询",
        description: "调用项目已配置的对象集",
        enabled: true,
      },
    ],
  },
  {
    title: "本体分析",
    desc: "指标、规则与关系影响分析",
    icon: DataAnalysis,
    color: "#7b66e8",
    bg: "#f3f0ff",
    items: [
      {
        id: 4,
        name: "对象指标统计",
        description: "按业务维度统计对象指标",
        enabled: true,
      },
      {
        id: 5,
        name: "约束规则校验",
        description: "使用本体约束检查对象数据",
        enabled: true,
      },
      {
        id: 6,
        name: "关系影响分析",
        description: "分析多级关系影响范围",
        enabled: false,
      },
    ],
  },
  {
    title: "系统协同",
    desc: "复用平台任务、清单与消息服务",
    icon: Tools,
    color: "#f29a45",
    bg: "#fff6eb",
    items: [
      {
        id: 7,
        name: "生成分析清单",
        description: "整理查询结果和来源依据",
        enabled: true,
      },
      {
        id: 8,
        name: "创建跟进任务",
        description: "创建关联来源对象的任务",
        enabled: true,
      },
      {
        id: 9,
        name: "发送站内提醒",
        description: "向指定角色发送业务提醒",
        enabled: false,
      },
    ],
  },
]);
const examples = [
  "查询最近新增的核心对象",
  "分析某对象的上下游关系",
  "找出未通过规则校验的数据",
];
const selectedProjectLabel = computed(
  () =>
    projectOptions.value.find(
      (x) => String(x.dictValue) === String(form.projectId)
    )?.dictLabel || "请选择项目"
);
const selectedVersionLabel = computed(
  () =>
    versionOptions.value.find(
      (x) => String(x.dictValue) === String(form.versionId)
    )?.dictLabel || "请选择本体版本"
);
async function loadProjects() {
  try {
    const res = await projectDict();
    projectOptions.value = res?.data || [];
  } catch {
    projectOptions.value = [];
  }
  if (!projectOptions.value.length)
    projectOptions.value = [{ dictValue: "demo", dictLabel: "示例业务项目" }];
  form.projectId = projectOptions.value[0].dictValue;
  await loadVersions(form.projectId);
}
async function loadVersions(id) {
  try {
    const res = await getProjectVersionDict(id);
    versionOptions.value = res?.data || [];
  } catch {
    versionOptions.value = [];
  }
  if (!versionOptions.value.length)
    versionOptions.value = [{ dictValue: "v1", dictLabel: "项目本体 v1.0.0" }];
  form.versionId = versionOptions.value[0].dictValue;
}
function saveDraft() {
  ElMessage.success("保存成功");
}
function sendDebug() {
  const text = debugInput.value.trim();
  if (!text || debugging.value) return;
  messages.value.push({ id: Date.now(), role: "user", text });
  debugInput.value = "";
  debugging.value = true;
  setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      role: "assistant",
      text: "已根据当前项目本体完成模拟查询。识别到相关业务对象与关系，并调用已启用能力生成结果。",
      sources: [
        "项目：" + selectedProjectLabel.value,
        "本体版本：" + selectedVersionLabel.value,
        "能力：对象条件查询、关系路径追溯",
      ],
    });
    debugging.value = false;
    nextTick(
      () =>
        debugMessages.value &&
        (debugMessages.value.scrollTop = debugMessages.value.scrollHeight)
    );
  }, 600);
}
onMounted(loadProjects);
</script>

<style scoped lang="scss">
.config-page {
  height: calc(100vh - 84px);
  padding: 15px;
  background: #f2f4f7;
  box-sizing: border-box;
}
.workspace {
  display: grid;
  grid-template-columns: minmax(600px, 1fr) minmax(440px, 0.78fr);
  gap: 15px;
  height: 100%;
}
.setting-panel,
.debug-panel {
  margin-bottom: 0px;
  min-height: 0;
  background: #fff;
}
.setting-panel {
  display: flex;
  flex-direction: column;
}
.panel-head {
  display: flex;
  align-items: center;
  height: 54px;
  flex: none;
  padding: 0 20px;
  border-bottom: 1px solid #ebeef5;
}
.panel-head .h2-titles {
  margin: 0;
}
.panel-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 18px 20px;
}
.panel-foot {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
  flex: none;
}
.panel-foot :deep(.el-button) {
  height: 32px;
  margin-left: 0;
  border-radius: 2px;
}
.h2-titles {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
}
.h2-titles:before {
  display: inline-block;
  width: 6px;
  height: 16px;
  margin-right: 8px;
  border-radius: 3px;
  background: #2666fb;
  content: "";
}
.agent-form {
  max-width: 760px;
}
.agent-form .el-select {
  width: 100%;
}
.agent-form :deep(.el-input__wrapper),
.agent-form :deep(.el-select__wrapper),
.agent-form :deep(.el-textarea__inner) {
  border-radius: 2px;
}
.section-block {
  margin-top: 26px;
}
.ontology-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 5px 0 14px;
  padding: 12px 14px;
  border: 1px solid #e1e6ee;
  background: #f7f8fa;
}
.ontology-card > div {
  display: flex;
  align-items: center;
  gap: 9px;
}
.ontology-card svg {
  width: 18px;
  color: #2666fb;
}
.ontology-card b,
.ontology-card small {
  display: block;
}
.ontology-card b {
  color: #303133;
  font-size: 14px;
}
.ontology-card small {
  margin-top: 3px;
  color: #909399;
  font-size: 12px;
}
.sub-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 9px;
  color: #606266;
  font-size: 13px;
}
.object-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.object-list :deep(.el-checkbox) {
  width: 100%;
  margin: 0;
  border-radius: 2px;
}
.object-list svg {
  width: 15px;
  margin-right: 6px;
  color: #2666fb;
}
.ability-list {
  display: grid;
  gap: 12px;
}
.ability-group {
  border: 1px solid #e1e6ee;
}
.ability-head {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 10px 12px;
  background: #f7f8fa;
  border-bottom: 1px solid #e7ebf1;
}
.ability-head > span {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 5px;
}
.ability-head svg {
  width: 16px;
}
.ability-head strong,
.ability-head small {
  display: block;
}
.ability-head strong {
  color: #303133;
  font-size: 14px;
}
.ability-head small {
  margin-top: 3px;
  color: #909399;
  font-size: 12px;
}
.ability-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 13px;
  border-bottom: 1px solid #edf0f4;
}
.ability-row:last-child {
  border-bottom: 0;
}
.ability-row :deep(.el-checkbox__label) b,
.ability-row :deep(.el-checkbox__label) small {
  display: block;
}
.ability-row :deep(.el-checkbox__label) b {
  color: #4a5568;
  font-size: 14px;
}
.ability-row :deep(.el-checkbox__label) small {
  margin-top: 3px;
  color: #909399;
  font-size: 12px;
}
.debug-panel {
  display: flex;
  flex-direction: column;
}
.debug-title {
  display: flex;
  align-items: center;
  height: 54px;
  flex: none;
  padding: 0 20px;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}
.debug-title .h2-titles {
  margin: 0;
}
.messages {
  flex: 1;
  overflow: auto;
  padding: 18px;
  background: #fafbfc;
}
.empty-debug {
  display: flex;
  height: 100%;
  min-height: 300px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}
.empty-debug > svg {
  width: 50px;
  height: 50px;
  color: #c4cada;
}
.empty-debug strong {
  margin-top: 14px;
  color: #606266;
  font-size: 13px;
}
.empty-debug p {
  color: #909399;
  font-size: 14px;
}
.empty-debug button {
  width: 290px;
  margin-top: 7px;
  padding: 8px 10px;
  border: 1px solid #dfe4ec;
  border-radius: 2px;
  color: #606266;
  background: #fff;
  font-size: 14px;
  cursor: pointer;
}
.empty-debug button:hover {
  border-color: #9cb8f3;
  color: #2666fb;
}
.message {
  display: flex;
  gap: 9px;
  margin-bottom: 16px;
}
.message.user {
  flex-direction: row-reverse;
}
.message > span {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  flex: none;
  border-radius: 4px;
  color: #fff;
  background: #2666fb;
}
.message > span svg {
  width: 16px;
}
.message > div {
  max-width: 86%;
}
.message p {
  margin: 0;
  padding: 10px 12px;
  border: 1px solid #e1e6ee;
  border-radius: 2px;
  color: #4f5c70;
  background: #fff;
  font-size: 14px;
  line-height: 1.7;
}
.message.user p {
  color: #fff;
  border-color: #2666fb;
  background: #2666fb;
}
.source-box {
  margin-top: 7px;
  padding: 9px 11px;
  border: 1px solid #dce7fb;
  background: #f6f9ff;
}
.source-box b,
.source-box small {
  display: block;
}
.source-box b {
  color: #46638f;
  font-size: 12px;
}
.source-box small {
  margin-top: 4px;
  color: #7a879a;
  font-size: 12px;
}
.debug-foot {
  flex: none;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
}
.composer {
  padding: 10px 11px 7px 14px;
  border: 1px solid #e0e7f1;
  border-radius: 9px;
  background: #fff;
  box-shadow: 0 5px 14px #385f9808;
  transition: 0.2s;
}
.composer.focused {
  border-color: #a6c3fc;
  box-shadow: 0 0 0 3px #2666fb12;
}
.composer :deep(.el-textarea__inner) {
  padding: 0;
  border: 0;
  box-shadow: none;
  color: #4c5870;
  font-size: 14px;
}
.composer-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 5px;
  color: #abb4c1;
  font-size: 12px;
}
.send-button {
  width: 30px;
  height: 30px;
  border: 0;
  box-shadow: 0 5px 11px #2666fb2b;
}
.send-button :deep(svg) {
  width: 15px;
}
@media (max-width: 1100px) {
  .workspace {
    grid-template-columns: 1fr;
  }
  .debug-panel {
    display: none;
  }
}
.workspace {
  grid-template-columns: minmax(480px, 0.7fr) minmax(620px, 1.3fr);
}
.messages {
  background: #fff;
}
.empty-debug > svg {
  width: 62px;
  height: 62px;
  color: #d4d9e3;
}
.empty-debug strong {
  margin-top: 18px;
  color: #303133;
  font-size: 13px;
}
.empty-debug p {
  margin: 8px 0 18px;
}
.empty-debug button {
  width: 300px;
  height: 34px;
  margin-top: 8px;
}
</style>
