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
  <div class="timeline-tab">
    <el-timeline
      v-if="stages.length"
      class="process-timeline"
      v-loading="loading"
    >
      <el-timeline-item
        v-for="stage in stages"
        :key="stage.id"
        :timestamp="formatStageTime(stage)"
        :type="timelineType(stage.status)"
        placement="top"
      >
        <el-card shadow="never" class="stage-card">
          <div class="stage-heading">
            <span>{{ stage.stageName }}</span>
            <dict-tag :options="om_ai_stage_status" :value="stage.status" />
          </div>
          <div class="stage-summary">
            {{ stage.errorMessage || stage.summary || stageDefaultText(stage) }}
          </div>
          <el-collapse
            v-if="stage.reasoningContent || stage.modelName"
            v-model="expandedReasoning"
            class="reasoning-collapse"
          >
            <el-collapse-item :name="String(stage.id)">
              <template #title>
                <span class="reasoning-title">
                  <el-icon class="reasoning-icon"><ChatLineSquare /></el-icon>
                  {{
                    stage.reasoningContent ? "查看思考过程" : "模型正在思考..."
                  }}
                </span>
              </template>
              <div class="reasoning-content">
                {{ stage.reasoningContent || "正在等待首个思考内容分片..." }}
              </div>
            </el-collapse-item>
          </el-collapse>
          <div
            v-if="stage.startedTime || stage.modelName || stage.durationMs"
            class="stage-meta"
          >
            <span v-if="stage.modelName">模型：{{ stage.modelName }}</span>
            <span v-if="stage.inputCount">输入：{{ stage.inputCount }}</span>
            <span v-if="stage.outputCount">输出：{{ stage.outputCount }}</span>
            <span v-if="stage.durationMs"
              >耗时：{{ formatDuration(stage.durationMs) }}</span
            >
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty
      v-else-if="!loading"
      description="暂无解析过程数据"
      :image-size="90"
    />
  </div>
</template>

<script setup>
import { ChatLineSquare } from "@element-plus/icons-vue";
import { getAiStages } from "@/api/om/aiModeling";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_stage_status } = proxy.useDict("om_ai_stage_status");

const stages = ref([]);
const loading = ref(false);
const expandedReasoning = ref([]);

function formatStageTime(stage) {
  const t = stage.startedTime || stage.createTime;
  if (!t) return "-";
  return proxy.parseTime(t, "{y}-{m}-{d} {h}:{i}:{s}");
}

function formatDuration(ms) {
  if (!ms) return "0ms";
  if (ms < 1000) return `${ms}ms`;
  const seconds = Math.floor(ms / 1000);
  if (seconds < 60) return `${seconds}秒`;
  const minutes = Math.floor(seconds / 60);
  const remain = seconds % 60;
  return `${minutes}分${remain ? remain + "秒" : ""}`;
}

function timelineType(status) {
  return { 1: "warning", 2: "success", 3: "danger" }[status] || "info";
}

function stageDefaultText(stage) {
  const mapping = {
    prepare: "准备阶段：初始化解析任务",
    document_parse: "文档结构化解析中...",
    evidence_index: "证据索引构建中...",
    entity_extract: "实体识别与属性抽取中...",
    relation_extract: "关系与约束抽取中...",
    axiom_generate: "模型公理生成中...",
    rule_generate: "数据规则生成中...",
  };
  return mapping[stage.stageCode] || "执行中...";
}

async function load() {
  if (!props.taskId) return;
  loading.value = true;
  try {
    const response = await getAiStages(props.taskId);
    stages.value = response.data || [];
  } finally {
    loading.value = false;
  }
}

watch(
  () => props.taskId,
  () => load(),
  { immediate: true }
);
</script>

<style scoped lang="scss">
.timeline-tab {
  height: calc(100vh - 280px);
  overflow-y: auto;
  padding: 5px 0;
}
.process-timeline {
  padding-left: 5px;
}
.stage-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
}
.stage-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
  span {
    color: #303133;
  }
}
.stage-summary {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  margin-bottom: 10px;
  white-space: pre-wrap;
}
.reasoning-collapse {
  margin-bottom: 10px;
  background: #f5f7fa;
  border-radius: 6px;
  :deep(.el-collapse-item__header) {
    padding: 0 12px;
    height: 36px;
    line-height: 36px;
    font-size: 13px;
  }
  :deep(.el-collapse-item__content) {
    padding: 8px 12px;
  }
}
.reasoning-title {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}
.reasoning-icon {
  font-size: 15px;
  color: #909399;
}
.reasoning-content {
  color: #606266;
  line-height: 1.6;
  font-size: 13px;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
}
.stage-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  color: #8a8a8a;
  font-size: 13px;
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
  span {
    white-space: nowrap;
  }
}
</style>
