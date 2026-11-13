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
  <div class="app-container ai-modeling-page">
    <detail-info
      :data="task"
      :header="{
        className: 'clearfixs',
        idKey: 'id',
        nameKey: 'taskName',
        statusKey: 'status',
        statusOptions: om_ai_task_status,
        routerBack: false,
      }"
      :items="detailItems"
      mode="free"
      @back="goBack"
    >
      <template #btn-right>
        <el-button
          v-hasPermi="['om:aiModeling:adopt']"
          type="primary"
          class="mr10 adopt-all-button"
          :disabled="!canAdopt"
          @click="adoptEverything"
        >
          <template #icon>
            <svg-icon icon-class="adopt" />
          </template>
          一键采纳
        </el-button>
      </template>
      <template #documents>
        <div
          v-if="files.length"
          class="document-names"
          :title="documentNames"
        >
          {{ documentNames }}
        </div>
        <span v-else>-</span>
      </template>
    </detail-info>

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
import TargetTab from "@/views/om/aiModeling/tabs/TargetTab.vue";
import TermTab from "@/views/om/aiModeling/tabs/TermTab.vue";
import EnumTab from "@/views/om/aiModeling/tabs/EnumTab.vue";
import ObjectTab from "@/views/om/aiModeling/tabs/ObjectTab.vue";
import AttributeTab from "@/views/om/aiModeling/tabs/AttributeTab.vue";
import RelationTab from "@/views/om/aiModeling/tabs/RelationTab.vue";
import AxiomTab from "@/views/om/aiModeling/tabs/AxiomTab.vue";
import DataRuleTab from "@/views/om/aiModeling/tabs/DataRuleTab.vue";
import TimelineTab from "@/views/om/aiModeling/tabs/TimelineTab.vue";
import { adoptAll, getAiFiles, getAiTask } from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
// 详情页创建人与列表一致，姓名后拼接联系电话。
const { formatCreator } = useCreatorPhones();
const route = useRoute();
const router = useRouter();
const { versionId } = useOntologyVersion();
const taskId = Number(route.params.id);
const { om_ai_task_status } = proxy.useDict("om_ai_task_status");

const task = ref({});
const files = ref([]);
const activeTab = ref("timeline");
const documentNames = computed(() =>
  files.value.map((file) => file.fileName).filter(Boolean).join("，") || "-"
);

/** 详情页签与系统正式菜单字段保持一致。 */
const tabs = [
  { name: "timeline", label: "解析过程", component: TimelineTab },
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
  () => tabs.find((tab) => tab.name === activeTab.value) || tabs[0]
);
const canAdopt = computed(
  () => task.value.status === 2 && Number(task.value.candidateCount || 0) > 0
);
/** 顶部信息表沿用建模目标详情分组：基础信息、整行描述及创建更新信息。 */
const detailItems = [
  { label: "开始时间", key: "startedTime", type: "time" },
  { label: "完成时间", key: "finishedTime", type: "time" },
  { label: "候选数量", key: "candidateCount", formatter: (val) => val ?? 0 },
  { label: "任务描述", key: "description", span: 24, className: "mt2 mb2" },
  { label: "文档名称", slot: "documents", span: 16, className: "mb2" },
  {
    label: "更新人",
    key: "updateBy",
    formatter: (_name, record) => formatCreator(record),
    className: "mb2",
  },
  {
    label: "创建人",
    key: "createBy",
    formatter: (_name, record) => formatCreator(record),
  },
  { label: "创建时间", key: "createTime", type: "time" },
  { label: "更新时间", key: "updateTime", type: "time" },
];
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
/* 文档名称使用逗号连接并保持单行，超出单元格后省略，悬浮显示完整内容。 */
.document-names {
  display: block;
  width: 100%;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 22px;
}
/* 顶部主操作与返回图标匹配视觉尺寸；列表操作继续沿用紧凑图标尺寸。
 * 仅调整图标容器，不改变共用 SVG 画布及按钮文字字号。
 */
.ai-modeling-page :deep(.adopt-all-button > .el-icon) {
  font-size: var(--el-font-size-base);
}
</style>
