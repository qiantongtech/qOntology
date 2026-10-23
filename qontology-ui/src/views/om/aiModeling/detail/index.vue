<template>
  <div class="app-container">
    <div class="pagecont-top" style="padding-bottom: 15px">
      <div class="infotop">
        <div class="infotop-title mb15">
          <div class="task-item">
            <span class="task-id">{{ task.id }}</span
            ><span class="task-name">{{ task.name }}</span
            ><dict-tag :options="taskStatusOptions" :value="task.status" />
          </div>
          <el-row :gutter="10" class="btn-style" style="margin-left: auto"
            ><el-col :span="1.5"
              ><el-button
                type="primary"
                size="small"
                plain
                @click="goBack"
                @mousedown="(e) => e.preventDefault()"
              >
                <svg-icon
                  style="width: 12px; height: 12px; margin-right: 3px"
                  iconClass="fhs"
                />
                返回</el-button
              ></el-col
            ><el-col :span="1.5"
              ><el-button type="primary" size="small" @click="developing"
                >一键采纳</el-button
              ></el-col
            ></el-row
          >
        </div>
        <el-row :gutter="3" class="info-row"
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">任务编号</div>
              <div class="infotop-row-value">{{ task.code }}</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">建模类型</div>
              <div class="infotop-row-value">文档辅助建模</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">任务状态</div>
              <div class="infotop-row-value">
                <dict-tag :options="taskStatusOptions" :value="task.status" />
              </div>
            </div> </el-col
        ></el-row>
        <el-row :gutter="3" class="info-row"
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">任务名称</div>
              <div class="infotop-row-value">{{ task.name }}</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">文档名称</div>
              <div class="infotop-row-value">{{ task.documentName }}</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">创建人</div>
              <div class="infotop-row-value">{{ task.creator }}</div>
            </div>
          </el-col></el-row
        >
        <el-row :gutter="3" class="info-row"
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">创建时间</div>
              <div class="infotop-row-value">{{ task.createdAt }}</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">解析时间</div>
              <div class="infotop-row-value">{{ task.parsedAt }}</div>
            </div> </el-col
          ><el-col :span="8">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">候选项数量</div>
              <div class="infotop-row-value">{{ task.candidateCount }}</div>
            </div>
          </el-col></el-row
        >
        <el-row :gutter="3"
          ><el-col :span="24">
            <div class="infotop-row border-top">
              <div class="infotop-row-lable">任务描述</div>
              <div class="infotop-row-value">{{ task.description }}</div>
            </div>
          </el-col></el-row
        >
      </div>
    </div>
    <div class="pagecont-bottom">
      <el-tabs v-model="activeName"
        ><el-tab-pane
          v-for="tab in tabs"
          :key="tab.name"
          :label="tab.label"
          :name="tab.name"
      /></el-tabs>
      <component :is="activeComponent" />
    </div>
  </div>
</template>
<script setup name="AiModelingDetail">
import Terms from "../tabs/Terms.vue";
import Goals from "../tabs/Goals.vue";
import ObjectTypes from "../tabs/ObjectTypes.vue";
import Properties from "../tabs/Properties.vue";
import Relations from "../tabs/Relations.vue";
import Enumerations from "../tabs/Enumerations.vue";
import Rules from "../tabs/Rules.vue";
const { proxy } = getCurrentInstance();
const route = useRoute();
const taskStatusOptions = [
  { value: "running", label: "执行中", elTagType: "warning" },
  { value: "success", label: "执行成功", elTagType: "success" },
  { value: "failed", label: "执行失败", elTagType: "danger" },
];
const taskList = [
  {
    id: 1,
    code: "AI_MODEL_202610251225",
    name: "供应链领域本体构建",
    description:
      "根据供应链业务文档识别术语、对象类型、属性及关系，生成候选本体模型内容。",
    status: "success",
    creator: "吴同",
    createdAt: "2026-10-25 12:25",
    parsedAt: "2026-10-25 12:26",
    documentName: "供应链领域业务说明.docx",
    candidateCount: "32",
  },
  {
    id: 2,
    code: "AI_MODEL_202610241225",
    name: "采购管理本体构建",
    description: "根据采购业务领域文档生成候选本体模型内容。",
    status: "running",
    creator: "吴同",
    createdAt: "2026-10-24 12:25",
    parsedAt: "-",
    documentName: "采购管理规范.docx",
    candidateCount: "-",
  },
  {
    id: 3,
    code: "AI_MODEL_202610231225",
    name: "设备管理本体构建",
    description: "根据设备管理领域文档生成候选本体模型内容。",
    status: "failed",
    creator: "吴同",
    createdAt: "2026-10-23 12:25",
    parsedAt: "2026-10-23 12:26",
    documentName: "设备管理规范.pdf",
    candidateCount: "0",
  },
  {
    id: 4,
    code: "AI_MODEL_202610221225",
    name: "客户管理本体构建",
    description: "根据客户业务领域文档生成候选本体模型内容。",
    status: "success",
    creator: "吴同",
    createdAt: "2026-10-22 12:25",
    parsedAt: "2026-10-22 12:27",
    documentName: "客户管理说明.docx",
    candidateCount: "28",
  },
];
const task = computed(
  () =>
    taskList.find((item) => item.id === Number(route.params.id)) || taskList[0]
);
const tabs = [
  { name: "terms", label: "候选术语", component: Terms },
  { name: "goals", label: "候选建模目标", component: Goals },
  { name: "objects", label: "候选对象类型", component: ObjectTypes },
  { name: "properties", label: "候选属性", component: Properties },
  { name: "relations", label: "候选对象类型关系", component: Relations },
  { name: "enums", label: "候选枚举值", component: Enumerations },
  { name: "rules", label: "候选约束规则", component: Rules },
];
const activeName = ref("terms");
const activeComponent = computed(
  () => tabs.find((tab) => tab.name === activeName.value).component
);
function developing() {
  proxy.$modal.msgWarning("功能开发中");
}
function goBack() {
  proxy.$router.push(`/om/${route.params.versionId}/aiModeling`);
}
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

.info-row {
  margin-bottom: 3px;
}
</style>
