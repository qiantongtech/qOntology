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

<!--<template>-->
<!--    <div class="app-container">-->
<!--        <div class="pagecont-one">-->
<!--            <Graph />-->
<!--        </div>-->
<!--    </div>-->
<!--</template>-->
<!--<script setup name="ExploreIndex">-->
<!--    import Graph from './graph/index.vue';-->
<!--</script>-->
<!--<style lang="scss" scoped>-->
<!--    .app-container {-->
<!--        display: flex;-->
<!--        flex-direction: column;-->
<!--        .pagecont-one {-->
<!--            flex: 1;-->
<!--            min-height: 0;-->
<!--            display: flex;-->
<!--            flex-direction: column;-->
<!--            border-radius: 2px;-->
<!--            :deep(.graphView) {-->
<!--                flex: 1;-->
<!--                min-height: 0;-->
<!--            }-->
<!--        }-->
<!--    }-->
<!--</style>-->


<template>
  <div class="app-container" ref="app-container">
    <GuideTip tip-id="kg/graphExploration" />
    <div class="pagecont-one">
      <div class="page-tabs">
        <el-tabs v-model="pageAct" @tab-click="tabsClick">
          <el-tab-pane label="常规视图" name="Graph"> </el-tab-pane>
          <el-tab-pane label="关系视图" name="Relation"> </el-tab-pane>
          <el-tab-pane label="时序视图" name="TimeLine"> </el-tab-pane>
          <el-tab-pane label="表格视图" name="Table"> </el-tab-pane>
        </el-tabs>
      </div>
      <Graph ref="graph" v-if="pageAct === 'Graph'" />
      <RelationView ref="relationView" v-if="pageAct === 'Relation'" />
<!--      <TimeLineView ref="timeLineView" v-if="pageAct === 'TimeLine'" />-->
      <TableView ref="tableView" v-if="pageAct === 'Table'" />
    </div>
  </div>
</template>
<script setup name="ExploreIndex">
import Graph from './graph/index.vue';
import RelationView from './relation/index.vue';
import TableView from './table/index.vue';
// import TimeLineView from './timeLine/index.vue';
const router = useRouter();
const route = useRoute();
const pageAct = ref('');
const tabsClick = (tab) => {
  router.push({
    name: tab.paneName
  });
};
watch(
    () => route.name,
    (val) => {
      console.log(val);
      pageAct.value = val;
    },
    {
      immediate: true
    }
);
</script>
<style lang="scss" scoped>
.app-container {
  display: flex;
  flex-direction: column;
  .pagecont-one {
    flex: 1;
    border-radius: 2px;
    :deep(.page-tabs) {
      background: #fff;
      .el-tabs--top .el-tabs__item.is-top:nth-child(2) {
        padding-left: 36px;
      }
      .el-tabs__nav-wrap:after {
        height: 1px;
        background-color: #e8e8e8;
      }
      .el-tabs__item {
        height: 50px;
      }
      .el-tabs__item:hover,
      .el-tabs__item.is-active,
      .el-tabs__active-bar {
        color: #135afb;
      }
      .el-tabs__header {
        margin: 0;
      }
      .el-tab-pane {
        padding: 0 0;
      }
    }
    :deep(.view-form) {
      background-color: #fff;
      padding: 15px 15px 0;
      margin-bottom: 15px;
    }
    :deep(.view-main) {
      background-color: #fff;
      padding: 20px;
    }
  }
}
</style>
