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
  <div class="app-container">
    <div class="pagecont-top" style="padding-bottom: 15px">
      <div class="infotop">
        <div class="infotop-title mb15">
          <div class="task-item">
            <span class="task-id">{{ task.id }}</span>
            <span class="task-name">{{ task.taskName }}</span>
            <dict-tag :options="om_ai_task_status" :value="task.status" />
          </div>
          <el-row :gutter="10" class="btn-style task-actions">
            <el-col :span="1.5">
              <el-button
                v-hasPermi="['om:aiModeling:adopt']"
                type="primary"
                size="small"
                :disabled="!canAdopt"
                @click="adoptEverything"
                >一键采纳</el-button
              >
            </el-col>

            <el-col :span="1.5">
              <el-button
                type="primary"
                size="small"
                class="fhbtn"
                plain
                @click="goBack"
                @mousedown="(event) => event.preventDefault()"
              >
                <svg-icon
                  icon-class="fhs"
                  style="width: 12px; height: 12px; margin-right: 3px"
                />
                返回
              </el-button>
            </el-col>
          </el-row>
        </div>
        <el-row :gutter="3" class="info-row">
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">任务名称</div>
              <div class="infotop-row-value">{{ task.taskName || "-" }}</div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">开始时间</div>
              <div class="infotop-row-value">
                {{ proxy.parseTime(task.startedTime) || "-" }}
              </div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">完成时间</div>
              <div class="infotop-row-value">
                {{ proxy.parseTime(task.finishedTime) || "-" }}
              </div>
            </div></el-col
          >
        </el-row>
        <el-row :gutter="3" class="info-row">
          <el-col :span="24"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">任务描述</div>
              <div class="infotop-row-value">{{ task.description || "-" }}</div>
            </div></el-col
          >
        </el-row>
        <el-row :gutter="3" class="info-row">
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">失败原因</div>
              <div class="infotop-row-value">
                {{ task.failureReason || "-" }}
              </div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">候选数量</div>
              <div class="infotop-row-value">
                {{ task.candidateCount ?? 0 }}
              </div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">文档名称</div>
              <div class="infotop-row-value document-list">
                <template v-if="files.length">
                  <div
                    v-for="file in files"
                    :key="file.id || file.fileName"
                    class="document-name"
                    :title="file.fileName"
                  >
                    {{ file.fileName || "-" }}
                  </div>
                </template>
                <template v-else>-</template>
              </div>
            </div></el-col
          >
        </el-row>
        <el-row :gutter="3" class="info-row">
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">创建人</div>
              <div class="infotop-row-value">{{ task.createBy || "-" }}</div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">创建时间</div>
              <div class="infotop-row-value">
                {{ proxy.parseTime(task.createTime) || "-" }}
              </div>
            </div></el-col
          >
          <el-col :span="8"
            ><div class="infotop-row border-top">
              <div class="infotop-row-lable">更新时间</div>
              <div class="infotop-row-value">
                {{ proxy.parseTime(task.updateTime) || "-" }}
              </div>
            </div></el-col
          >
        </el-row>
        <el-alert
          v-if="task.status === 3 && task.failureReason"
          class="mt15"
          type="error"
          :closable="false"
          show-icon
          :title="task.failureReason"
        />
      </div>
    </div>

    <div class="pagecont-bottom">
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
        v-if="currentTab.component"
        :key="currentTab.name"
        :task-id="taskId"
      />
    </div>
  </div>
</template>

<script setup name="AiModelingDetail">
import { useRoute, useRouter } from "vue-router";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import TargetTab from "../tabs/TargetTab.vue";
import TermTab from "../tabs/TermTab.vue";
import EnumTab from "../tabs/EnumTab.vue";
import ObjectTab from "../tabs/ObjectTab.vue";
import AttributeTab from "../tabs/AttributeTab.vue";
import RelationTab from "../tabs/RelationTab.vue";
import AxiomTab from "../tabs/AxiomTab.vue";
import DataRuleTab from "../tabs/DataRuleTab.vue";
import TimelineTab from "../tabs/TimelineTab.vue";
import { adoptAll, getAiFiles, getAiTask } from "@/api/om/aiModeling";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const { versionId } = useOntologyVersion();
const taskId = Number(route.params.id);
const { om_ai_task_status } = proxy.useDict("om_ai_task_status");

const task = ref({});
const files = ref([]);
const activeTab = ref("target");

/** 详情页签与系统正式菜单字段保持一致。 */
const tabs = [
  { name: "target", label: "建模目标", component: TargetTab },
  { name: "term", label: "术语管理", component: TermTab },
  { name: "enum_type", label: "枚举管理", component: EnumTab },
  { name: "object", label: "对象类型", component: ObjectTab },
  { name: "attribute", label: "对象类型属性", component: AttributeTab },
  { name: "relation", label: "对象类型关系", component: RelationTab },
  { name: "axiom", label: "模型公理", component: AxiomTab },
  { name: "rule", label: "数据规则", component: DataRuleTab },
  { name: "timeline", label: "解析过程", component: TimelineTab },
];

const currentTab = computed(
  () => tabs.find((tab) => tab.name === activeTab.value) || tabs[0]
);
const canAdopt = computed(
  () => task.value.status === 2 && Number(task.value.candidateCount || 0) > 0
);
/** 加载任务详情。 */
async function loadDetail() {
  const [taskResponse, fileResponse] = await Promise.all([
    getAiTask(taskId),
    getAiFiles(taskId),
  ]);
  task.value = taskResponse.data;
  files.value = fileResponse.data || [];
}

/** 返回 AI 辅助建模任务列表。 */
function goBack() {
  proxy.$tab.closeOpenPage({ path: `/om/${versionId.value}/aiModeling` });
}

/** 按依赖顺序采纳全部候选数据。 */
async function adoptEverything() {
  try {
    await proxy.$modal.confirm(`确认采纳全部候选数据吗？`);
    const result = await adoptAll(taskId);
    proxy.$modal.msgSuccess(
      `采纳完成：成功 ${result.data.success}，失败 ${result.data.failed}`
    );
  } catch (err) {
    // 用户点取消 或者 接口报错都会进这里
    // 如果需要区分【用户取消】和【接口异常】可以单独判断
    if (err !== "cancel") {
      proxy.$modal.msgError("采纳失败，请稍后重试");
    }
  }
}

loadDetail();
const refreshTimer = setInterval(() => {
  if (task.value.status === 1) loadDetail();
}, 2500);
onUnmounted(() => clearInterval(refreshTimer));
</script>

<style scoped lang="scss">
.task-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.task-id {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  color: #fff;
  background: #2f6bff;
  font-size: 14px;
  font-weight: 500;
}

.task-name {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-actions {
  margin-left: auto;
}

.info-row {
  margin-bottom: 3px;
}

.infotop-row {
  align-items: stretch;
}

.infotop-row-lable,
.infotop-row-value {
  height: auto;
  min-height: 40px;
}

.infotop-row-lable {
  flex-shrink: 0;
}

.document-list {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 4px;
  padding-top: 6px;
  padding-bottom: 6px;
}

.document-name {
  width: 100%;
  overflow-wrap: anywhere;
  line-height: 22px;
}
</style>
