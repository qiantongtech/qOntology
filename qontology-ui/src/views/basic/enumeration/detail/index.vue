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
  <div v-loading="loading" class="app-container enum-detail-page">
    <!-- 使用自由布局保留枚举专属字段；返回行为由本页接管，确保关闭详情后回到枚举列表。 -->
    <detail-info
      :data="detail"
      :header="{
        className: 'clearfixs',
        idKey: 'enumId',
        nameKey: 'enumName',
        statusKey: 'status',
        statusOptions: sys_normal_disable,
        routerBack: false
      }"
      :items="detailItems"
      mode="free"
      @back="handleReturn"
    />
    <el-empty v-if="!loading && detail.enumId == null" :description="emptyMessage"/>
    <!-- 两个标签页分别校验列表权限，仅有业务数据类型权限时也允许查看关联列表。 -->
    <div v-if="detail.enumId != null && (auth.hasPermi('basic:enumeration:data:list') || auth.hasPermi('basic:dataType:datatype:list'))" class="pagecont-bottom">
      <el-tabs v-model="activeName">
        <el-tab-pane v-if="auth.hasPermi('basic:enumeration:data:list')" label="枚举数据" name="data">
          <EnumData :key="detail.enumId" :enum-type="detail.enumType" @data-change="loadDataCount"/>
        </el-tab-pane>
        <!-- 直接复用业务数据类型页面；首次切换时加载，并以内嵌模式管理当前枚举的关联业务数据类型。 -->
        <el-tab-pane v-if="auth.hasPermi('basic:dataType:datatype:list')" label="关联业务数据类型" name="related" lazy>
          <BusinessDataTypes :key="detail.enumId" :enum-type="detail.enumType" embedded/>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup name="BasicEnumDetail">
import {getCurrentInstance, onBeforeUnmount, ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import {getType} from '@/api/basic/enumeration/type';
import {listData} from '@/api/basic/enumeration/data';
import auth from '@/plugins/auth';
import EnumData from '../data/index.vue';
import BusinessDataTypes from '@/views/basic/dataType/index.vue';

const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict('sys_normal_disable');
const route = useRoute();
const detail = ref({});
const loading = ref(false);
const activeName = ref('data');
const emptyMessage = ref('枚举类型已不存在');
// 自由布局完整声明概要字段，避免通用组件默认追加与枚举无关的描述字段。
const detailItems = [
  {label: '枚举名称', key: 'enumName'},
  {label: '枚举类型', key: 'enumType'},
  {label: '枚举数据数', key: 'enumDataCount'},
  {label: '备注', key: 'remark', span: 24, className: 'mt2 mb2'},
  {label: '创建人', key: 'createBy'},
  {label: '创建时间', key: 'createTime', type: 'time'},
  {label: '更新时间', key: 'updateTime', type: 'time'}
];
// 详情与数量分别记录请求版本；数量请求还需校验详情版本，防止切换枚举后旧响应回写。
let requestVersion = 0;
let countRequest = 0;

/** 跟随路由重新加载枚举详情，清空旧数据并选择当前用户可访问的标签页。 */
watch(() => route.params.enumId, async (enumId) => {
  const request = ++requestVersion;
  detail.value = {};
  activeName.value = auth.hasPermi('basic:enumeration:data:list') ? 'data' : 'related';
  emptyMessage.value = '枚举类型已不存在';
  loading.value = false;
  if (!enumId) return;
  loading.value = true;
  try {
    const response = await getType(enumId);
    // 仅采纳当前枚举的最新详情响应，忽略切换前仍未完成的请求。
    if (request !== requestVersion) return;
    detail.value = response.data || {};
    loadDataCount();
  } catch {
    if (request === requestVersion) emptyMessage.value = '详情加载失败，请刷新重试';
  } finally {
    if (request === requestVersion) loading.value = false;
  }
}, {immediate: true});

/** 查询当前枚举的全部数据条数；首次加载和数据增删改成功后调用，不使用表格筛选总数。 */
async function loadDataCount() {
  const request = ++countRequest;
  const version = requestVersion;
  // 缺少关联键或列表权限时不请求数据接口，避免无效查询及权限错误。
  if (!detail.value.enumType || !auth.hasPermi('basic:enumeration:data:list')) return;
  try {
    // 单独查询未筛选的总数，避免标签或状态筛选影响详情概要。
    const response = await listData({enumType: detail.value.enumType, pageNum: 1, pageSize: 1});
    // 同时校验数量请求与详情请求，防止旧计数覆盖新枚举或更新后的计数。
    if (request !== countRequest || version !== requestVersion) return;
    detail.value.enumDataCount = response.data.total;
  } catch {
    // 查询失败时显示占位符，避免将未知数量误显示为零或保留失效的旧计数。
    if (request === countRequest && version === requestVersion) detail.value.enumDataCount = undefined;
  }
}

// 离开详情后使所有在途请求失效，避免销毁后继续回写页面状态。
onBeforeUnmount(() => { ++requestVersion; ++countRequest; });

/** 关闭当前详情页签，并返回固定的枚举列表入口。 */
function handleReturn() {
  proxy.$tab.closeOpenPage({path: '/basic/basicEnum'});
}
</script>

