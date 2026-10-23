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
  <div class="app-container" ref="app-container">
    <div class="pagecont-top" v-show="showSearch" style="padding-bottom:15px">
      <div class="infotop">
        <div class="infotop-title mb15">
          <div class="task-item">
            <!-- 正方形编号 -->
            <div class="task-id">
              {{ targetDetail.id || "-" }}
            </div>
            <!-- 名称 -->
            <div class="task-name">
              <el-tooltip
                  :content="targetDetail.question || ''"
                  placement="top"
                  effect="light"
                  :disabled="!textOverflowMap.question"
              >
                <span
                    class="ellipsis-text"
                    @mouseenter="(event) => checkTextOverflow(event, 'question')"
                >
                  {{ targetDetail.question || "" }}
                </span>
              </el-tooltip>
            </div>
            <div>
              <dict-tag :options="om_accept_status" :value="targetDetail.acceptStatus"/>
            </div>

          </div>
          <el-row :gutter="15" class="btn-style" style="margin-left: auto">
            <el-col :span="1.5">
              <el-button
                  type="primary"
                  size="small"
                  class="fhbtn"
                  plain
                  @click="goBack"
                  @mousedown="(e) => e.preventDefault()"
              >
                <svg-icon
                    style="width: 12px; height: 12px; margin-right: 3px"
                    :iconClass="'fhs'"
                />
                返回
              </el-button>
            </el-col>
          </el-row>
        </div>

        <el-row :gutter="5" style="margin-bottom: 5px">
          <el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">问题来源</div>
              <div class="infotop-row-value">
                {{ targetDetail.questionSource || '-' }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">业务场景</div>
              <div class="infotop-row-value">
                {{ targetDetail.businessScenarios || '-' }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">优先级</div>
              <div class="infotop-row-value">
                <dict-tag :options="om_target_priority" :value="targetDetail.priority"/>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="5" style="margin-bottom: 5px">
          <el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">关联元素</div>
              <div class="infotop-row-value">
                {{
                  targetDetail.relatedElementCount || '-'
                }}
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="5">
          <el-col :span="24">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">验收标准</div>
              <div class="infotop-row-value">
                {{ targetDetail.acceptCriterion || '-' }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="pagecont-bottom">
      <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="对象类型" name="objectType">
          <object-type-comp v-if="targetDetail.id" :targetDetail="targetDetail"></object-type-comp>
        </el-tab-pane>
        <el-tab-pane label="对象类型关系" name="objectRelationship">
          <object-relationship-comp v-if="targetDetail.id" :targetDetail="targetDetail"></object-relationship-comp>
        </el-tab-pane>
        <el-tab-pane label="验证任务" name="acceptTask">
          <accept-task-comp v-if="targetDetail.id" :targetDetail="targetDetail" @updateAcceptStatus="updateAcceptStatus"></accept-task-comp>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup name="Target">
import {getTarget} from "@/api/om/omtarget/target";
import {useRoute, useRouter} from 'vue-router';
import ObjectRelationshipComp from "@/views/om/omtarget/detail/objectRelationshipComp.vue";
import ObjectTypeComp from "@/views/om/omtarget/detail/objectTypeComp.vue";
import AcceptTaskComp from "@/views/om/omtarget/detail/acceptTaskComp.vue";

const {proxy} = getCurrentInstance();
const {om_accept_status, om_target_priority} = proxy.useDict('om_accept_status', 'om_target_priority');

const router = useRouter();
const route = useRoute();

const data = reactive({
  targetDetail: {},
  form: {},
});
const textOverflowMap = reactive({});

const activeName = ref('objectType');
const showSearch = ref(true);
const {targetDetail, rules} = toRefs(data);

let id = route.query.id || 1;

// 监听 id 变化
watch(
    () => route.query.id,
    (newId) => {
      id = newId || 1;  // 如果 id 为空，使用默认值 1
      getTargetDetailById();
    },
    {immediate: true}  // `immediate` 为 true 表示页面加载时也会立即执行一次 watch
);

function handleClick(tab, event) {
  console.log(tab, event)
}

/** 复杂详情页面上方表单查询 */
function getTargetDetailById() {
  const _id = id;
  getTarget(_id).then(response => {
    targetDetail.value = response.data;
  });
}

function checkTextOverflow (event, field) {
  const element = event.currentTarget;
  if (element) {
    textOverflowMap[field] = element.scrollWidth > element.clientWidth;
  }
}

function goBack(){
  let path = router.currentRoute.value.path;
  path = path.replace('/detail', '');
  router.push({
    path: path
  });
  reset();
}
getTargetDetailById();

function updateAcceptStatus(val) {
  console.log("val+++++++++++++",val)
  targetDetail.value.acceptStatus = val;
}
</script>

<style scoped lang="scss">
.task-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;

  .task-id {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    padding: 2px;
    background-color: #2666fb;
    color: #fff;
    font-size: 14px;
    border-radius: 0;
    aspect-ratio: 1 / 1;
    min-width: 20px;
    min-height: 20px;
  }
}

.fhbtn {
  .svg-icon {
    font-size: 12px;
    margin-right: 3px;
    vertical-align: middle;
    margin-top: -3px;
  }

  &:hover {
    .svg-icon {
      filter: brightness(0) invert(1) !important;
    }
  }
}
</style>
