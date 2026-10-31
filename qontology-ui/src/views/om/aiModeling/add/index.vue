<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 
 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
 
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
  <div class="app-container wizard-page" style="background-color: #f0f2f5">
    <div class="custom-card">
      <div class="steps-inner">
        <ul class="zl-step">
          <li
            v-for="(item, index) in steps"
            :key="item.name"
            :class="{
              statusEnd: activeStep === index,
              prevStep: index < activeStep,
              cur: index > activeStep,
            }"
          >
            <div
              class="step-circle"
              :class="{
                active: activeStep === index,
                prev: index < activeStep,
              }"
            >
              <span>{{ index + 1 }}</span>
            </div>
            <span class="step-name">{{ item.name }}</span>
          </li>
        </ul>
      </div>
    </div>

    <div class="pagecont-top">
      <div class="infotop">
        <div class="main wizard-content">
          <section v-show="activeStep === 0" class="base-step">
            <div class="h2-titles">基础信息</div>
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="120px"
              class="base-form"
            >
              <el-form-item label="任务名称" prop="taskName">
                <el-input
                  v-model="form.taskName"
                  placeholder="请输入任务名称"
                  maxlength="100"
                />
              </el-form-item>
              <el-form-item label="任务描述">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入任务描述"
                  maxlength="500个字符"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item label="上传文件" prop="files">
                <AiFileUpload
                  v-model="form.files"
                  :disabled="submitting || isEditMode"
                  :limit="5"
                  :file-size="20"
                />
              </el-form-item>
            </el-form>
          </section>

          <section v-if="activeStep === 1" class="analysis-step">
            <div class="analysis-hero">
              <div class="ai-document-icon">
                <svg-icon icon-class="AI辅助建模" />
              </div>
              <div class="analysis-main">
                <div class="analysis-heading">
                  <div class="analysis-title">{{ analysisTitle }}</div>
                  <div
                    class="analysis-percentage"
                    :class="{
                      'is-success': task.status === 2,
                      'is-error': task.status === 3,
                    }"
                  >
                    {{ overallProgress }}%
                  </div>
                </div>
                <el-progress
                  :percentage="overallProgress"
                  :status="progressStatus"
                  :stroke-width="7"
                  :show-text="false"
                />
                <div class="analysis-tip">
                  {{ analysisTip }}
                </div>
              </div>
              <div class="analysis-stats">
                <div class="analysis-stat-list">
                  <div class="analysis-stat-item">
                    <strong>{{ analysisFileCount }}</strong>
                    <span>个文件</span>
                  </div>
                  <div class="analysis-stat-item">
                    <strong>{{ analysisEvidenceCount }}</strong>
                    <span>个证据块</span>
                  </div>
                  <div class="analysis-stat-item">
                    <strong>{{ analysisDuration }}</strong>
                    <span>总耗时</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="process-panel">
              <div class="h2-titles">AI解析过程</div>
              <el-timeline v-if="visibleStages.length" class="process-timeline">
                <el-timeline-item
                  v-for="stage in visibleStages"
                  :key="stage.id"
                  :timestamp="formatStageTime(stage)"
                  :type="timelineType(stage.status)"
                  placement="top"
                >
                  <el-card shadow="never" class="stage-card">
                    <div class="stage-heading">
                      <span>{{ stage.stageName }}</span>
                      <dict-tag
                        :options="om_ai_stage_status"
                        :value="stage.status"
                      />
                    </div>
                    <div class="stage-summary">
                      {{
                        stage.errorMessage ||
                        stage.summary ||
                        stageDefaultText(stage)
                      }}
                    </div>
                    <el-collapse
                      v-if="stage.reasoningContent || stage.modelName"
                      v-model="expandedReasoning"
                      class="reasoning-collapse"
                    >
                      <el-collapse-item :name="String(stage.id)">
                        <template #title>
                          <span class="reasoning-title">
                            <el-icon class="reasoning-icon"
                              ><ChatLineSquare
                            /></el-icon>
                            {{
                              stage.reasoningContent
                                ? "查看思考过程"
                                : "模型正在思考..."
                            }}
                          </span>
                        </template>
                        <div
                          :ref="(el) => bindReasoningContainer(stage.id, el)"
                          class="reasoning-content"
                        >
                          {{
                            stage.reasoningContent ||
                            "正在等待首个思考内容分片..."
                          }}
                        </div>
                      </el-collapse-item>
                    </el-collapse>
                    <div
                      v-if="
                        stage.startedTime || stage.modelName || stage.durationMs
                      "
                      class="stage-meta"
                    >
                      <span v-if="stage.modelName"
                        >模型：{{ stage.modelName }}</span
                      >
                      <span v-if="stage.durationMs"
                        >耗时：{{ formatDuration(stage.durationMs) }}</span
                      >
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty
                v-else
                :description="
                  submitting
                    ? '正在上传文件并初始化解析任务，请稍候'
                    : '正在初始化解析阶段，请稍候'
                "
                :image-size="90"
              />
            </div>
          </section>

          <section v-if="activeStep === 2" class="review-step">
            <div class="review-summary">
              <div>
                解析完成，共生成
                <b>{{ task.candidateCount || 0 }}</b> 条待审核候选数据。
              </div>
              <el-button
                v-hasPermi="['om:aiModeling:adopt']"
                type="primary"
                @click="adoptEverything"
                >一键采纳</el-button
              >
            </div>
            <el-tabs v-model="activeTab">
              <el-tab-pane
                v-for="tab in tabs"
                :key="tab.name"
                :label="tab.label"
                :name="tab.name"
              />
            </el-tabs>
            <component
              :is="currentTab.component"
              :key="currentTab.name"
              :task-id="taskId"
            />
          </section>
        </div>

        <div class="button-style">
          <template v-if="activeStep === 0">
            <el-button type="primary" @click="goList">返回列表</el-button>
            <el-button :loading="submitting" @click="saveAndContinue"
              >下一步</el-button
            >
          </template>
          <template v-else-if="activeStep === 1">
            <!-- <el-button @click="goList">取消</el-button> -->
            <el-button
              type="primary"
              plain
              :disabled="submitting || !taskId"
              @click="goList"
              >后端运行</el-button
            >
            <el-button
              type="primary"
              :disabled="task.status !== 2"
              @click="activeStep = 2"
              >下一步</el-button
            >
          </template>
          <template v-else>
            <el-button type="primary" @click="goList">返回列表</el-button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="AiModelingAdd">
import { useRoute, useRouter } from "vue-router";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import AiFileUpload from "../components/AiFileUpload.vue";
import TargetTab from "../tabs/TargetTab.vue";
import TermTab from "../tabs/TermTab.vue";
import EnumTab from "../tabs/EnumTab.vue";
import ObjectTab from "../tabs/ObjectTab.vue";
import AttributeTab from "../tabs/AttributeTab.vue";
import RelationTab from "../tabs/RelationTab.vue";
import AxiomTab from "../tabs/AxiomTab.vue";
import DataRuleTab from "../tabs/DataRuleTab.vue";
import {
  adoptAll,
  createAiTask,
  getAiFiles,
  getAiTask,
  updateAiTask,
} from "@/api/om/aiModeling";
import { getToken } from "@/utils/auth";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const { versionId, requireVersion } = useOntologyVersion();
const { om_ai_stage_status } = proxy.useDict("om_ai_stage_status");

const steps = [{ name: "基础信息" }, { name: "AI解析" }, { name: "结果审核" }];
const activeStep = ref(0);
const submitting = ref(false);
const taskId = ref(null);
const task = ref({});
const stages = ref([]);
const activeTab = ref("target");
const expandedReasoning = ref([]);
const reasoningContainers = new Map();
let streamController = null;

const form = reactive({ taskName: "", description: "", files: [] });
const isEditMode = computed(() => Boolean(Number(route.query.id)));
const rules = computed(() => ({
  taskName: [{ required: true, message: "请输入任务名称", trigger: "blur" }],
  files: isEditMode.value
    ? []
    : [
        {
          validator: (_, value, callback) =>
            value?.length ? callback() : callback(new Error("请上传文件")),
          trigger: "change",
        },
      ],
  files: [{ required: true, message: "上传文件不能为空", trigger: "change" }],
}));

const tabs = [
  { name: "target", label: "建模目标", component: TargetTab },
  { name: "term", label: "术语管理", component: TermTab },
  { name: "enum_type", label: "枚举管理", component: EnumTab },
  { name: "object", label: "对象类型", component: ObjectTab },
  { name: "attribute", label: "对象类型属性", component: AttributeTab },
  { name: "relation", label: "对象类型关系", component: RelationTab },
  { name: "axiom", label: "模型公理", component: AxiomTab },
  { name: "rule", label: "数据规则", component: DataRuleTab },
];
const currentTab = computed(
  () => tabs.find((item) => item.name === activeTab.value) || tabs[0]
);
const visibleStages = computed(() => {
  const started = stages.value.filter((item) => item.status !== 0);
  return started.length ? started : stages.value.slice(0, 1);
});
const completedStageCount = computed(
  () => stages.value.filter((item) => item.status === 2).length
);
const fileParseStage = computed(() =>
  stages.value.find((item) => item.stageCode === "FILE_PARSE")
);
const analysisFileCount = computed(() =>
  Number(fileParseStage.value?.inputCount || form.files.length || 0)
);
const analysisEvidenceCount = computed(() =>
  Number(fileParseStage.value?.outputCount || 0)
);
const analysisDuration = computed(() => {
  const duration = stages.value.reduce(
    (total, stage) => total + Number(stage.durationMs || 0),
    0
  );
  return duration > 0 ? formatDuration(duration) : "-";
});
const overallProgress = computed(() => {
  if (task.value.status === 2) return 100;
  if (!stages.value.length) return taskId.value ? 3 : 0;
  const total = stages.value.length;
  const running = stages.value.find((item) => item.status === 1);
  const runningProgress = Number(running?.progress || 10) / 100;
  return Math.min(
    99,
    Math.round(((completedStageCount.value + runningProgress) / total) * 100)
  );
});
const progressStatus = computed(() =>
  task.value.status === 2
    ? "success"
    : task.value.status === 3
    ? "exception"
    : undefined
);
const analysisTitle = computed(() =>
  task.value.status === 2
    ? "AI解析完成"
    : task.value.status === 3
    ? "AI解析失败"
    : "AI正在解析文档..."
);
const analysisTip = computed(() => {
  if (task.value.status === 2)
    return "解析结果已生成，请点击下一步进入人工审核与采纳。";
  if (task.value.status === 3)
    return task.value.failureReason || "解析失败，请返回列表后重新解析。";
  return "解析可能耗时较长，您可以留在当前页面查看进度，或点击“后端运行”返回列表。";
});

async function saveAndContinue() {
  if (!requireVersion()) return;
  const valid = await proxy.$refs.formRef.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEditMode.value) {
      await updateAiTask({
        id: taskId.value,
        taskName: form.taskName,
        description: form.description || "",
        versionId: versionId.value,
      });
      proxy.$modal.msgSuccess("修改成功");
      activeStep.value = 1;
      submitting.value = false;
      startAnalysisStream();
      return;
    }
    const data = new FormData();
    data.append("taskName", form.taskName);
    data.append("description", form.description || "");
    data.append("versionId", versionId.value);
    form.files
      .filter((file) => file.raw)
      .forEach((file) => data.append("files", file.raw));
    const response = await createAiTask(data);
    taskId.value = Number(response.data);
    router.replace({ path: route.path, query: { taskId: taskId.value } });
    activeStep.value = 1;
    submitting.value = false;
    startAnalysisStream();
  } finally {
    submitting.value = false;
  }
}

/**
 * 第二步使用独立 POST SSE 接口启动解析并接收进度。
 * 中断页面连接只会停止接收消息，后台任务仍会继续执行。
 */
async function startAnalysisStream() {
  if (!taskId.value) return;
  stopAnalysisStream();
  streamController = new AbortController();

  try {
    const response = await fetch(
      `${import.meta.env.VITE_APP_BASE_API}/om/aiTask/${
        taskId.value
      }/analyze-stream`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${getToken()}`,
          Accept: "text/event-stream",
        },
        signal: streamController.signal,
      }
    );
    if (!response.ok || !response.body) {
      throw new Error(`AI解析流连接失败（${response.status}）`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");
    let buffer = "";
    while (true) {
      const { value, done } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, "\n");
      const events = buffer.split("\n\n");
      buffer = events.pop() || "";
      for (const rawEvent of events) {
        const data = rawEvent
          .split("\n")
          .filter((line) => line.startsWith("data:"))
          .map((line) => line.slice(5).trim())
          .join("\n");
        if (!data) continue;
        const streamEvent = JSON.parse(data);
        if (streamEvent.eventType === "snapshot") {
          task.value = streamEvent.task || {};
          stages.value = streamEvent.stages || [];
        } else if (streamEvent.eventType === "task") {
          task.value = { ...task.value, ...(streamEvent.task || {}) };
        } else if (streamEvent.eventType === "stage" && streamEvent.stage) {
          const index = stages.value.findIndex(
            (item) => item.id === streamEvent.stage.id
          );
          if (index >= 0) {
            stages.value.splice(index, 1, {
              ...stages.value[index],
              ...streamEvent.stage,
            });
          } else {
            stages.value.push(streamEvent.stage);
            stages.value.sort(
              (left, right) => left.sequenceNo - right.sequenceNo
            );
          }
          if (
            streamEvent.stage.status === 1 &&
            !expandedReasoning.value.includes(String(streamEvent.stage.id))
          ) {
            expandedReasoning.value.push(String(streamEvent.stage.id));
          }
          await nextTick();
          scrollReasoningToBottom(streamEvent.stage.id);
        }
      }
    }
  } catch (error) {
    if (error?.name !== "AbortError") {
      proxy.$modal.msgError(
        error?.message || "AI解析连接异常，任务仍会在后台继续执行"
      );
    }
  } finally {
    streamController = null;
  }
}

function stopAnalysisStream() {
  streamController?.abort();
  streamController = null;
}

function runInBackground() {
  // proxy.$modal.msgSuccess("任务将在后台继续解析");
  goList();
}

async function adoptEverything() {
  const result = await adoptAll(taskId.value);
  proxy.$modal.msgSuccess(
    `采纳完成：成功 ${result.data.success}，失败 ${result.data.failed}`
  );
}

function goList() {
  stopAnalysisStream();
  proxy.$tab.closeOpenPage({ path: `/om/${versionId.value}/aiModeling` });
}

function timelineType(status) {
  return { 1: "warning", 2: "success", 3: "danger" }[status] || "info";
}

function stageDefaultText(stage) {
  return stage.status === 0
    ? "等待前置阶段完成"
    : stage.status === 1
    ? "正在分析文档内容并整理结构化结果"
    : "暂无阶段摘要";
}

function bindReasoningContainer(stageId, element) {
  if (element) reasoningContainers.set(stageId, element);
  else reasoningContainers.delete(stageId);
}

function scrollReasoningToBottom(stageId) {
  const container = reasoningContainers.get(stageId);
  if (container) container.scrollTop = container.scrollHeight;
}

function formatStageTime(stage) {
  return proxy.parseTime(stage.startedTime || stage.createTime) || "";
}

function formatDuration(duration) {
  const seconds = Math.max(1, Math.round(Number(duration) / 1000));
  return seconds < 60
    ? `${seconds}秒`
    : `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
}

onUnmounted(stopAnalysisStream);

onMounted(async () => {
  const editTaskId = Number(route.query.id);
  if (editTaskId) {
    taskId.value = editTaskId;
    const [taskResponse, fileResponse] = await Promise.all([
      getAiTask(editTaskId),
      getAiFiles(editTaskId),
    ]);
    task.value = taskResponse.data || {};
    form.files = (fileResponse.data || []).map((file) => ({
      uid: `persisted-${file.id}`,
      name: file.fileName,
      url: file.fileUrl,
      persisted: true,
    }));
    form.taskName = task.value.taskName || "";
    form.description = task.value.description || "";
    return;
  }
  const restoredTaskId = Number(route.query.taskId);
  if (!restoredTaskId) return;
  taskId.value = restoredTaskId;
  activeStep.value = 1;
  startAnalysisStream();
});
</script>

<style scoped lang="scss">
.custom-card {
  width: 100%;
  height: 100px;
  padding: 34px 177px 26px 189px;
  background: #fff;
  box-sizing: border-box;
  margin-bottom: 15px;
  .steps-inner {
    padding: 0 10px;
    padding-left: 20px;
    display: flex;
    width: auto;
    color: #303133;
    transition: 0.3s;
    transform: translateZ(0);
    &::-webkit-scrollbar {
      height: 5px;
    }
    .zl-step {
      list-style: none;
      width: 100%;
      height: 20px;
      padding: 0;
      margin: 20px auto;
      cursor: pointer;
      display: flex;
      align-items: flex-end;
      li {
        position: relative;
        flex: 1;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #d7d8da;
        color: #666;
        font-weight: 500;
        transition: background 0.3s;
        &:first-child {
          z-index: 2;
          clip-path: polygon(
            0 0,
            calc(100% - 20px) 0,
            100% 50%,
            calc(100% - 20px) 100%,
            0 100%
          );
        }
        &:not(:first-child):not(:last-child) {
          margin-left: -10px;
          clip-path: polygon(
            0 0,
            calc(100% - 20px) 0,
            100% 50%,
            calc(100% - 20px) 100%,
            0 100%
          );
          z-index: 1;
          &::before {
            content: "";
            position: absolute;
            left: 0;
            top: 0;
            width: 20px;
            height: 100%;
            background: #fff;
            clip-path: polygon(0 0, 100% 50%, 0 100%);
            z-index: 2;
          }
        }
        &:last-child {
          margin-left: -10px;
          z-index: 0;
          clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);
          &::before {
            content: "";
            position: absolute;
            left: 0;
            top: 0;
            width: 20px;
            height: 100%;
            background: #fff;
            clip-path: polygon(0 0, 100% 50%, 0 100%);
            z-index: 2;
          }
        }
        &.statusEnd {
          background: linear-gradient(270deg, #e9effe 0%, #5589fa 100%);
          color: #2666fb !important;
        }
        &.prevStep {
          background: #e9effe !important;
          font-weight: normal;
          font-size: 16px !important;
          color: #2666fb !important;
        }
        &.cur {
          background: #f1f1f5;
          color: #404040;
          font-weight: 500;
        }
      }
    }
    .step-circle {
      width: 26px;
      height: 26px;
      border-radius: 50%;
      background: #f1f1f5;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      font-weight: bold;
      margin-right: 11px;
      border: 1px solid #b2b2b2;
      flex-shrink: 0;
      transition: all 0.3s;
      &.active {
        background: #2666fb;
        color: #fff;
        border: 1px solid #fff;
      }
      &.prev {
        background: #f1f1f5 !important;
        border: 1px solid #2666fb !important;
        color: #2666fb !important;
      }
    }
    .step-name {
      font-family: PingFang SC, PingFang SC;
      font-weight: 500;
      font-size: 16px;
    }
  }
}
.pagecont-top {
  position: relative;
  height: auto;
  min-height: 74vh;
  padding-bottom: 60px;
}
.infotop {
  min-height: 74vh;
}
.main {
  flex: 1;
  padding: 0 25px;
  background-color: #fff;
}
.wizard-content {
  min-height: calc(100vh - 290px);
}
.h2-titles {
  display: flex;
  align-items: center;
  margin: 8px 0 8px;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
}
.h2-titles::before {
  display: inline-block;
  width: 6px;
  height: 16px;
  margin-right: 8px;
  border-radius: 3px;
  background: var(--el-color-primary);
  content: "";
}
.base-form {
  max-width: 900px;
  margin: 0 auto;
}
.analysis-step {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 325px);
  min-height: 500px;
}
.analysis-hero {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
  width: 100%;
  min-height: 108px;
  background: #fff;
  box-sizing: border-box;
}
.ai-document-icon {
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  margin-right: 18px;
  border-radius: 10px;
  color: #2666fb;
  background: #eaf1ff;
  font-size: 36px;
}
.analysis-main {
  flex: 1;
  min-width: 0;
}
.analysis-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.analysis-title {
  color: #1f2d3d;
  font-size: 17px;
  font-weight: 600;
}
.analysis-percentage {
  margin-left: 18px;
  color: #2666fb;
  font-size: 13px;
  font-weight: 600;
  text-align: right;
}
.analysis-percentage.is-success {
  color: #36b37e;
}
.analysis-percentage.is-error {
  color: #f56c6c;
}
.analysis-tip {
  margin-top: 9px;
  color: #909399;
  font-size: 13px;
  line-height: 1.5;
}
.analysis-stats {
  flex: none;
  width: 245px;
  margin-left: 28px;
  padding-left: 24px;
  border-left: 1px solid #ebeef5;
  box-sizing: border-box;
}
.analysis-stat-list {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.analysis-stat-item {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.analysis-stat-item:not(:last-child)::after {
  position: absolute;
  top: 4px;
  right: 0;
  width: 1px;
  height: 34px;
  background: #ebeef5;
  content: "";
}
.analysis-stat-item strong {
  color: #303133;
  font-size: 17px;
  font-weight: 600;
  line-height: 24px;
}
.analysis-stat-item span {
  margin-top: 2px;
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
}
.process-panel {
  width: 100%;
  flex: 1 1 auto;
  min-height: 0;
  margin: 0;
  padding-right: 8px;
  background: #fff;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.process-panel .h2-titles {
  flex: 0 0 auto;
}
.process-timeline {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding: 4px 8px 10px;
  box-sizing: border-box;
}
.stage-card {
  border-color: #e5e9f2;
  border-radius: 2px;
}
.stage-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  font-weight: 600;
}
.stage-summary {
  color: #606266;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.reasoning-collapse {
  margin-top: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #f7f8fa;
}
.reasoning-collapse :deep(.el-collapse-item__header) {
  height: 40px;
  padding: 0 12px;
  border-bottom: 0;
  background: transparent;
  color: #606266;
  font-weight: 500;
}
.reasoning-collapse :deep(.el-collapse-item__wrap) {
  border-bottom: 0;
  background: transparent;
}
.reasoning-collapse :deep(.el-collapse-item__content) {
  padding: 0 12px 12px;
}
.reasoning-title {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}
.reasoning-icon {
  color: #909399;
}
.reasoning-content {
  max-height: 260px;
  padding: 12px 14px;
  overflow: auto;
  border-top: 1px solid #ebeef5;
  color: #606266;
  background: #fff;
  font-family: Consolas, "Microsoft YaHei", monospace;
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}
.stage-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
.review-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  margin-bottom: 15px;
  border: 1px solid #d9e5ff;
  background: #f5f8ff;
}
.button-style {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  padding: 0 35px 25px 0;
  background: #fff;
  text-align: right;
}
@media (max-width: 1200px) {
  .analysis-hero {
    width: 100%;
  }
  .analysis-stats {
    width: 220px;
    margin-left: 18px;
    padding-left: 18px;
  }
}
</style>
