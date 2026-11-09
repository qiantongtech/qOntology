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
  <div v-loading="loading" class="app-container project-detail-page">
    <!-- 使用自由布局保留项目专属字段；返回行为由本页接管，确保关闭详情后回到项目列表。 -->
    <detail-info
      v-if="detail.id != null"
      :data="detail"
      :header="{
        className: 'clearfixs',
        idKey: 'id',
        nameKey: 'name',
        statusKey: 'status',
        statusOptions: project_status,
        routerBack: false,
      }"
      :items="detailItems"
      mode="free"
      @back="handleReturn"
    >
    </detail-info>
    <el-empty
      v-if="!loading && detail.id == null"
      :description="emptyMessage"
    />
    <!-- 缺少项目版本列表权限时不展示页签，也不发起版本查询。 -->
    <div
      v-if="detail.id != null && auth.hasPermi(versionListPermission)"
      class="pagecont-bottom"
    >
      <el-tabs v-model="activeName">
        <el-tab-pane label="项目版本" name="version">
          <!-- 直接复用项目版本页面；内嵌模式固定查询当前项目，并保留版本增删改查能力。 -->
          <ProjectVersion
            :key="detail.id"
            embedded
            :project-id="detail.id"
            :current-version="detail.currentVersion"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup name="BasicProjectDetail">
import { computed, getCurrentInstance, onBeforeUnmount, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { getProject } from "@/api/basic/project/project";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import ProjectVersion from "@/views/basic/projectVersion/index.vue";

// 页签仅项目版本一项，权限与版本管理菜单保持一致。
const versionListPermission = "basic:project:projectversion:list";

const { proxy } = getCurrentInstance();
const { project_status } = proxy.useDict("project_status");
const route = useRoute();
const { creatorPhones } = useCreatorPhones();
const detail = ref({});
const loading = ref(false);
const activeName = ref("version");
const emptyMessage = ref("项目已不存在");

// 自由布局完整声明概要字段，避免通用组件默认追加与项目无关的字段。
const detailItems = computed(() => [
  { label: "项目名称", key: "name" },
  { label: "项目领域", key: "domain" },
  { label: "命名空间", key: "namespace" },
  { label: "责任人", key: "responsiblePersonName",formatter: formatCreator },
  {
    label: "包含范围",
    key: "includeRange",
    span: 24,
    className: "mt2 mb2",
    ellipsisClass: "ellipsis-2",
  },
  {
    label: "排除范围",
    key: "excludeRange",
    span: 24,
    className: "mt2 mb2",
    ellipsisClass: "ellipsis-2",
  },
]);

/** 复用用户电话关联结果，创建人与联系电话用斜杠分隔，未维护电话时仅显示姓名。 */
function formatCreator(name, record) {
  const phone = creatorPhones.value[record.creatorId];
  return phone ? `${name || "-"} / ${phone}` : name || "-";
}

// 记录请求版本，防止切换项目后旧的详情响应回写页面。
let requestVersion = 0;

/** 跟随路由重新加载项目概要，切换项目时清空旧数据并重置页签。 */
watch(
  () => route.params.projectId,
  async (projectId) => {
    const request = ++requestVersion;
    detail.value = {};
    activeName.value = "version";
    emptyMessage.value = "项目已不存在";
    loading.value = false;
    if (!projectId) return;
    loading.value = true;
    try {
      const response = await getProject(projectId);
      // 仅采纳当前项目的最新详情响应，忽略切换前仍未完成的请求。
      if (request !== requestVersion) return;
      detail.value = response.data || {};
    } catch {
      if (request === requestVersion)
        emptyMessage.value = "详情加载失败，请刷新重试";
    } finally {
      if (request === requestVersion) loading.value = false;
    }
  },
  { immediate: true }
);

// 离开详情后使在途请求失效，避免销毁后继续回写页面状态。
onBeforeUnmount(() => {
  ++requestVersion;
});

/** 关闭当前详情页签，并返回固定的项目列表入口。 */
function handleReturn() {
  proxy.$tab.closeOpenPage({ path: "/basic/project" });
}
</script>

<style lang="scss" scoped>
// 页签共用详情容器的内边距，搜索栏与操作栏、表格保持左对齐。
.project-detail-page :deep(.qt-wrap--search) {
  padding: 0;
}

// 图标尺寸对齐列表项目名称前的图标，便于在详情概要中快速识别项目。
.project-detail-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
  vertical-align: middle;
}
</style>
