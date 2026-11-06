<template>
  <div ref="pageRef" class="app-container agent-page">
    <div v-show="showSearch" class="pagecont-top search-row">
      <el-form
        ref="queryRef"
        class="btn-style"
        :model="query"
        :inline="true"
        @submit.prevent
      >
        <el-form-item label="智能体名称" prop="keyword">
          <el-input
            v-model="query.keyword"
            class="el-form-input-width"
            placeholder="请输入智能体名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-select
            v-model="query.projectId"
            class="el-form-input-width"
            placeholder="请选择所属项目"
            clearable
          >
            <el-option
              v-for="item in projectOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-select
            v-model="query.status"
            class="el-form-input-width"
            placeholder="请选择发布状态"
            clearable
          >
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
            <el-option label="已停用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="handleQuery"
            ><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询</el-button
          >
          <el-button @click="resetQuery"
            ><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button
          >
        </el-form-item>
        <el-form-item style="float: right; margin-right: auto">
          <el-button type="primary" plain @click="createAgent"
            ><i class="iconfont-mini icon-xinzeng mr5"></i>新增智能体</el-button
          >
        </el-form-item>
      </el-form>
    </div>

    <div class="agent-body">
      <div class="agent-list">
        <article
          v-for="agent in pagedAgents"
          :key="agent.id"
          class="agent-card"
        >
          <div class="card-top">
            <div class="agent-info">
              <span class="agent-logo" :class="agent.theme"
                ><component :is="agent.icon"
              /></span>
              <div>
                <div class="name-line">
                  <h3>{{ agent.name }}</h3>
                </div>
                <div class="tag-line">
                  <el-tag v-for="tag in agent.tags" :key="tag">{{
                    tag
                  }}</el-tag>
                </div>
              </div>
            </div>
            <dict-tag
              class="card-status"
              :options="agentStatusOptions"
              :value="agent.status"
            />
          </div>
          <el-tooltip
            :content="agent.description"
            effect="light"
            placement="top-start"
            popper-class="overview-text-tooltip"
          >
            <p class="description">{{ agent.description }}</p>
          </el-tooltip>
          <div class="card-actions">
            <el-button
              link
              type="primary"
              :icon="Promotion"
              @click="publishAgent(agent)"
              >{{ agent.status === "published" ? "停用" : "发布" }}</el-button
            ><el-divider direction="vertical" /><el-button
              link
              type="primary"
              :icon="View"
              @click="openDetail(agent)"
              >详情</el-button
            ><el-divider direction="vertical" /><el-button
              link
              type="primary"
              :icon="Edit"
              @click="configure(agent)"
              >修改</el-button
            ><el-divider direction="vertical" /><el-button
              link
              type="danger"
              :icon="Delete"
              @click="removeAgent(agent)"
              >删除</el-button
            >
          </div>
        </article>
      </div>

      <div class="pagecont-bottom">
        <pagination
          v-show="filteredAgents.length > 0"
          v-model:page="query.pageNum"
          v-model:limit="query.pageSize"
          :total="filteredAgents.length"
          :page-sizes="[9, 18, 27, 36]"
        />
      </div>
    </div>

    <!-- 智能体详情弹框 -->
    <el-dialog
      v-model="detailOpen"
      class="agent-detail-dialog"
      title="智能体详情"
      width="640px"
      :append-to="pageRef"
      draggable
    >
      <el-form
        class="agent-detail-form"
        :model="detailAgent"
        label-width="90px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="智能体名称">
              <div class="form-readonly">{{ detailAgent?.name || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布状态">
              <div class="form-readonly">
                <dict-tag
                  :options="agentStatusOptions"
                  :value="detailAgent?.status"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属项目">
              <div class="form-readonly">{{ detailAgent?.project || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="本体版本">
              <div class="form-readonly">
                {{ detailAgent?.ontology || "-" }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="能力范围">
              <div class="form-readonly">{{ detailAgent?.scope || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="能力标签">
              <div class="form-readonly alias-tags">
                <el-tag v-for="tag in detailAgent?.tags || []" :key="tag">{{
                  tag
                }}</el-tag>
                <span v-if="!detailAgent?.tags?.length">-</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="功能描述">
              <div class="form-readonly textarea">
                {{ detailAgent?.description || "-" }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import {
  CircleCheck,
  Connection,
  DataAnalysis,
  Delete,
  Document,
  Edit,
  Operation,
  Promotion,
  Setting,
  TrendCharts,
  View,
  Warning,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { projectDict } from "@/api/basic/project/project";
const router = useRouter();
const queryRef = ref();
const pageRef = ref();
const projectOptions = ref([]);
const showSearch = ref(true);
const query = reactive({
  keyword: "",
  projectId: "",
  status: "",
  pageNum: 1,
  pageSize: 9,
});
const agentStatusOptions = [
  { label: "已发布", value: "published", elTagType: "primary", elTagClass: "" },
  { label: "草稿", value: "draft", elTagType: "warning", elTagClass: "" },
  { label: "已停用", value: "disabled", elTagType: "info", elTagClass: "" },
];
const agents = ref([
  {
    id: 1,
    name: "业务对象查询助手",
    description:
      "面向业务人员查询项目中的业务对象、属性与关联关系，支持按业务语义自由组合检索条件，结果附带可追溯的本体依据与数据来源说明，让每一次查询都有据可查。",
    project: "供应链采购与仓储协同本体",
    projectId: "1",
    ontology: "v1.0.0",
    scope: "对象查询、关系追溯、指标统计",
    status: "published",
    tags: ["对象查询", "关系追溯", "指标统计"],
    icon: DataAnalysis,
    theme: "blue",
  },
  {
    id: 2,
    name: "本体规则巡检智能体",
    description:
      "基于项目本体中的约束规则定期巡检对象实例数据，自动定位不一致、缺失与冲突问题，生成问题清单并支持按规则维度统计质量分布，支撑数据治理闭环。",
    project: "安全生产管理本体",
    projectId: "2",
    ontology: "v2.1.0",
    scope: "约束校验、异常识别",
    status: "published",
    tags: ["约束校验", "异常识别"],
    icon: CircleCheck,
    theme: "green",
  },
  {
    id: 3,
    name: "客户关系分析助手",
    description:
      "分析客户、商机、服务记录关联，构建客户画像，挖掘高价值线索并给出跟进建议，支撑销售精准获客。",
    project: "客户关系管理本体",
    projectId: "3",
    ontology: "v1.3.0",
    scope: "关系分析、对象画像、线索汇总",
    status: "draft",
    tags: ["关系分析", "对象画像", "线索汇总"],
    icon: Promotion,
    theme: "purple",
  },
  {
    id: 4,
    name: "项目任务协同智能体",
    description:
      "梳理项目、任务、人员协作关系，识别进度与资源风险，生成任务清单并跟踪闭环。",
    project: "项目管理任务本体",
    projectId: "4",
    ontology: "v1.0.0",
    scope: "进度汇总",
    status: "published",
    tags: ["进度汇总"],
    icon: Setting,
    theme: "orange",
  },
  {
    id: 5,
    name: "对象关系追溯助手",
    description:
      "逐级追溯业务对象上下游关联链路，展示关系路径与数据来源，用于合规审计和影响分析。",
    project: "企业经营分析本体",
    projectId: "5",
    ontology: "v1.2.0",
    scope: "多跳查询、关系路径、来源依据",
    status: "published",
    tags: ["多跳查询", "关系路径", "来源依据"],
    icon: Connection,
    theme: "cyan",
  },
  {
    id: 6,
    name: "业务事件分析智能体",
    description:
      "识别项目本体中的业务事件与状态变化，按时间、对象与事件类型等多维度聚合分析，输出趋势研判结论并对异常波动进行自动提醒。",
    project: "经营活动管理本体",
    projectId: "6",
    ontology: "v1.0.0",
    scope: "事件识别、状态分析",
    status: "disabled",
    tags: ["事件识别", "状态分析"],
    icon: Operation,
    theme: "red",
  },
  {
    id: 7,
    name: "合同风险审查助手",
    description:
      "面向合同管理场景，自动识别合同条款中的关键要素与潜在风险点，结合本体中的合规规则逐条生成审查意见，并给出可落地的条款修改建议。",
    project: "经营活动管理本体",
    projectId: "6",
    ontology: "v1.0.0",
    scope: "条款识别、风险预警、合规校验",
    status: "published",
    tags: ["条款识别", "风险预警", "合规校验"],
    icon: Document,
    theme: "purple",
  },
  {
    id: 8,
    name: "供应商评估智能体",
    description:
      "汇聚供应商交付及时率、质量合格率与服务响应数据，依据本体评估模型自动计算综合得分并生成供应商画像，辅助采购分级与优胜劣汰决策。",
    project: "供应链采购与仓储协同本体",
    projectId: "1",
    ontology: "v1.0.0",
    scope: "绩效评估、风险画像",
    status: "draft",
    tags: ["绩效评估", "风险画像"],
    icon: TrendCharts,
    theme: "green",
  },
  {
    id: 9,
    name: "设备故障诊断助手",
    description:
      "基于设备台账与实时运行数据，沿故障本体逐级推理可能的故障原因链路，快速定位异常部件并输出包含备件清单与工时建议的维修方案。",
    project: "安全生产管理本体",
    projectId: "2",
    ontology: "v2.1.0",
    scope: "故障定位、原因分析、维修建议",
    status: "published",
    tags: ["故障定位", "原因分析", "维修建议"],
    icon: Warning,
    theme: "red",
  },
]);
const filteredAgents = computed(() =>
  agents.value.filter(
    (item) =>
      (!query.keyword ||
        (item.name + item.description).includes(query.keyword)) &&
      (!query.projectId ||
        String(item.projectId) === String(query.projectId)) &&
      (!query.status || item.status === query.status)
  )
);
const pagedAgents = computed(() =>
  filteredAgents.value.slice(
    (query.pageNum - 1) * query.pageSize,
    query.pageNum * query.pageSize
  )
);
function handleQuery() {
  query.pageNum = 1;
}
function resetQuery() {
  queryRef.value?.resetFields();
  query.pageNum = 1;
}
function createAgent() {
  router.push("/app/businessAgent/config");
}
function configure(agent) {
  router.push("/app/businessAgent/config/" + agent.id);
}
const detailOpen = ref(false);
const detailAgent = ref(null);
function openDetail(agent) {
  detailAgent.value = agent;
  detailOpen.value = true;
}
function publishAgent(agent) {
  agent.status = agent.status === "published" ? "disabled" : "published";
  ElMessage.success(agent.status === "published" ? "发布成功" : "已停用");
}
function removeAgent(agent) {
  ElMessageBox.confirm(`是否确认删除"${agent.name}"？`, "提示", {
    type: "warning",
  })
    .then(() => {
      agents.value = agents.value.filter((item) => item.id !== agent.id);
      ElMessage.success("删除成功");
    })
    .catch(() => {});
}
onMounted(async () => {
  try {
    const res = await projectDict();
    projectOptions.value = res?.data || [];
  } catch {
    projectOptions.value = [];
  }
});
</script>

<style scoped lang="scss">
.agent-page {
  display: flex;
  min-height: calc(100vh - 84px);
  flex-direction: column;
  padding: 15px 15px 0;
  background: #f0f2f5;
  box-sizing: border-box;
}
.search-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 15px;
}
.search-row :deep(.el-form) {
  flex: 1;
  min-width: 0;
}

.agent-body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding-bottom: 75px;
}
.pagecont-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60px;
  flex: none;
  margin: 0;
  padding: 0 18px 0 0;
  border-radius: 2px 2px 2px 2px;
  background: #ffffff;
  line-height: 60px;
}
.pagecont-bottom :deep(.pagination-container) {
  margin-top: 0;
  padding: 14px 16px;
}
.agent-list {
  display: grid;
  flex: 1;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
  align-content: start;
  padding: 16px;
  border-radius: 2px;
  background: #ffffff;
}
.agent-card {
  display: flex;
  min-height: 190px;
  flex-direction: column;
  padding: 18px 20px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 2px;
  background: #fff;
  box-sizing: border-box;
}
// .agent-card:hover {
//   border-color: #b9cff8;
//   box-shadow: 0 3px 12px rgba(42, 91, 165, 0.08);
// }
.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
.card-status {
  flex: none;
  margin-left: 10px;
}
.agent-info {
  display: flex;
  gap: 13px;
}
.agent-logo {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  flex: none;
  border-radius: 8px;
  color: #fff;
  background: #2666fb;
}
.agent-logo svg {
  width: 24px;
}
.agent-logo.green {
  background: #35b99b;
}
.agent-logo.purple {
  background: #7a62e8;
}
.agent-logo.orange {
  background: #f3a13d;
}
.agent-logo.cyan {
  background: #29a8ba;
}
.agent-logo.red {
  background: #eb6664;
}
.name-line {
  display: flex;
  align-items: center;
  gap: 10px;
}
.name-line h3 {
  margin: 0;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 600;
  line-height: 22px;
}
.tag-line {
  display: flex;
  gap: 6px;
  margin-top: 5px;
}

.description {
  display: -webkit-box;
  min-height: 40px;
  margin: 15px 0;
  padding: 0;
  overflow: hidden;
  color: var(--qt-text-regular);
  font-size: 14px;
  font-weight: 400;
  line-height: 1.6;
  text-overflow: ellipsis;
  word-break: break-all;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  cursor: default;
}
.card-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
  font-size: 14px;
}
.card-actions :deep(.el-divider--vertical) {
  margin: 0 6px;
}
.card-actions :deep(.el-button) {
  font-size: 14px;
  padding: 4px 0;
}
.agent-detail-form {
  padding: 0;
}
.form-readonly {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 34px;
  padding: 0 10px;
  border: 1px solid #f1f1f1;
  border-radius: 2px;
  color: #303133;
  font-size: 14px;
  background-color: #fcfcfc;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-all;
}
.form-readonly.textarea {
  display: block;
  min-height: 85px;
}
.form-readonly.alias-tags {
  padding-top: 4px;
  padding-bottom: 4px;
}
.alias-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.alias-tags .el-tag {
  max-width: 100%;
}
.alias-tags :deep(.el-tag__content) {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}
.agent-page :deep(.agent-detail-dialog .el-form-item) {
  margin-bottom: 16px;
}
.agent-page :deep(.agent-detail-dialog .el-dialog__header) {
  padding: 9px 20px;
  background: #f8f8f8;
}
.agent-page :deep(.agent-detail-dialog .el-dialog__body) {
  padding: 20px 40px !important;
}
.agent-page :deep(.agent-detail-dialog .el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #e8e8e8;
}
.agent-page :deep(.agent-detail-dialog .el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 16px;
  font-size: 14px;
}

@media (max-width: 1500px) {
  .agent-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 1100px) {
  .agent-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 700px) {
  .agent-list {
    grid-template-columns: 1fr;
  }
}
</style>
