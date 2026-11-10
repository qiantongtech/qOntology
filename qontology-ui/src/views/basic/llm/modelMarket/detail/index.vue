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
  <div class="app-container" ref="app-container">
    <detail-info
      :data="detailData"
      :header="{
        className: 'clearfixs',
        idKey: 'id',
        nameKey: 'name',
        statusKey: 'status',
        statusOptions: ai_apikey_status,
        routerBack: false,
      }"
      :items="detailItems"
      mode="free"
      @back="handleReturn"
    />

    <div class="pagecont-bottom">
      <el-tabs v-model="activeName" class="demo-tabs" @tab-change="tabChange">
        <el-tab-pane
          v-for="item in platformKeyList"
          :key="item.id"
          :label="item.name"
          :name="item.id"
        ></el-tab-pane>
      </el-tabs>
      <ModelTable :keyId="keyId" />
    </div>
  </div>
</template>

<script setup name="ModelMarketDetail">
import {
  listByPlatform,
  getByPlatform,
} from "@/api/basic/llm/modelMarket/key.js";
import { useRoute } from "vue-router";
import ModelTable from "./modelTable.vue";

const { proxy } = getCurrentInstance();
const { ai_apikey_status, ai_model_platform } = proxy.useDict(
  "ai_apikey_status",
  "ai_model_platform"
);

const activeName = ref("");
const platformKeyList = ref([]);

let keyId = 0;
const route = useRoute();

// 详情概要字段：布局与枚举详情页一致，描述独占一行
// 字典 ref 在 script 中需 .value 解包后再传入子组件
const detailItems = computed(() => [
  { label: "模型提供方", key: "platform", dictOptions: ai_model_platform.value },
  { label: "支持语言", value: "中/英文" },
  { label: "创建人", key: "createBy" },
  { label: "创建时间", className: "mt2", key: "createTime", type: "time" },
  { label: "更新人", className: "mt2", key: "updateBy" },
  { label: "更新时间", className: "mt2", key: "updateTime", type: "time" },
  {
    label: "描述",
    key: "description",
    span: 24,
    className: "mt2",
    formatter: (value) => value?.trim() || "-",
  },
]);
// 监听 id 变化
watch(
  () => route.query.platform,
  (newPlatform) => {
    queryByPlatform(newPlatform);
    queryInfoByPlatform(newPlatform);
  },
  { immediate: true }
);
const data = reactive({
  modelDetail: {},
  form: {},
});

const { modelDetail } = toRefs(data);

// 编号、创建人、创建/更新时间等审计字段在 listByPlatform 的 key 记录中，
// getByPlatform（平台信息）不返回，两份数据合并后传给详情组件
const modelAudit = ref({});
// 平台信息接口中的审计字段为空，审计数据需最后合并，避免被 null 覆盖。
const detailData = computed(() => ({ ...modelDetail.value, ...modelAudit.value }));

function queryByPlatform(platform) {
  if (!platform) {
    return;
  }
  listByPlatform(platform).then((response) => {
    const keyList = Array.isArray(response.data) ? response.data : [];
    platformKeyList.value = keyList;
    const first = keyList[0] || {};
    keyId = first.id || 0;
    activeName.value = first.id || "";
    modelAudit.value = {
      id: first.id,
      createBy: first.createBy,
      createTime: first.createTime,
      updateTime: first.updateTime,
    };
  });
}

function queryInfoByPlatform(platform) {
  if (!platform) {
    return;
  }
  getByPlatform(platform).then((response) => {
    modelDetail.value = response.data;
  });
}

function tabChange() {
  keyId = activeName.value;
}

function handleReturn() {
  const path = route.path;
  if (path.indexOf("/basic/llm/myModel/detail") > -1) {
    proxy.$router.push({
      path: "/basic/llm/myModel",
    });
  } else {
    proxy.$router.push({
      path: "/basic/llm/modelMarket",
    });
  }
}

</script>
