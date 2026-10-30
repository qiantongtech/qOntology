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
    <div v-show="showSearch" class="pagecont-top">
      <el-form
        ref="queryRef"
        class="btn-style"
        :model="query"
        :inline="true"
        @submit.prevent
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input
            v-model="query.taskName"
            class="el-form-input-width"
            placeholder="请输入任务名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select
            v-model="query.status"
            class="el-form-input-width"
            placeholder="请选择任务状态"
            clearable
          >
            <el-option
              v-for="option in om_ai_task_status"
              :key="option.value"
              :label="option.label"
              :value="Number(option.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="handleQuery">
            <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetQuery">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['om:aiModeling:add']"
              type="primary"
              plain
              @click="openCreateDialog"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['om:aiModeling:remove']"
              type="danger"
              plain
              :disabled="!selectedIds.length"
              @click="removeTasks()"
            >
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:show-search="showSearch"
            :columns="columns"
            @queryTable="loadTasks"
          />
        </div>
      </div>

      <el-table
        v-loading="loading"
        stripe
        :data="rows"
        :default-sort="defaultSort"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
          :selectable="(row) => row.status !== 1"
        />
        <el-table-column
          v-if="isColumnVisible('id')"
          prop="id"
          label="编号"
          width="80"
          align="center"
          sortable="custom"
          :sort-orders="['descending', 'ascending', null]"
        />
        <el-table-column
          v-if="isColumnVisible('taskName')"
          prop="taskName"
          label="任务名称"
          min-width="180"
        />
        <el-table-column
          v-if="isColumnVisible('description')"
          prop="description"
          label="描述"
          min-width="220"
          :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
          v-if="isColumnVisible('status')"
          label="任务状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <dict-tag :options="om_ai_task_status" :value="row.status" />
          </template>
        </el-table-column>
        <el-table-column
          v-if="isColumnVisible('failureReason')"
          label="失败原因"
          min-width="160"
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="{ row }">{{ row.failureReason || "-" }}</template>
        </el-table-column>
        <el-table-column
          v-if="isColumnVisible('candidateCount')"
          label="候选数量"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            {{ row.candidateCount ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="isColumnVisible('createBy')"
          prop="createBy"
          label="创建人"
          width="110"
        />
        <el-table-column
          v-if="isColumnVisible('createTime')"
          label="创建时间"
          prop="createTime"
          width="170"
          align="center"
          sortable="custom"
        >
          <template #default="{ row }">{{
            parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
          }}</template>
        </el-table-column>
        <el-table-column
          v-if="isColumnVisible('operation')"
          label="操作"
          width="280"
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              v-hasPermi="['om:aiModeling:reparse']"
              link
              type="primary"
              icon="Refresh"
              :disabled="row.status === 1"
              @click="reparseTask(row)"
            >
              重新解析
            </el-button>
            <el-button
              v-hasPermi="['om:aiModeling:list']"
              link
              type="primary"
              icon="View"
              @click="openDetail(row)"
            >
              详情
            </el-button>
            <el-popover
              v-if="
                auth.hasPermiOr(['om:aiModeling:edit', 'om:aiModeling:remove'])
              "
              placement="bottom"
              :width="150"
              trigger="click"
            >
              <template #reference>
                <el-button link type="primary" icon="ArrowDown">
                  更多
                </el-button>
              </template>
              <div class="butgdlist">
                <el-button
                  v-if="auth.hasPermi('om:aiModeling:edit')"
                  link
                  type="primary"
                  icon="Edit"
                  :disabled="row.status === 1"
                  @click="handleMoreCommand('edit', row)"
                >
                  修改
                </el-button>
                <el-button
                  v-if="auth.hasPermi('om:aiModeling:remove')"
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.status === 1"
                  @click="handleMoreCommand('delete', row)"
                >
                  删除
                </el-button>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <template #empty>
          <div class="emptyBg">
            <img
              src="@/assets/system/images/no_data/noData.png"
              alt="暂无记录"
            />
            <p>暂无记录</p>
          </div>
        </template>
      </el-table>

      <pagination
        v-show="total > 0"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        :total="total"
        @pagination="loadTasks"
      />
    </div>
  </div>
</template>

<script setup name="AiModeling">
import { useRouter } from "vue-router";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import { deleteAiTask, listAiTasks, reparseAiTask } from "@/api/om/aiModeling";
import auth from "@/plugins/auth";

const { proxy } = getCurrentInstance();
const router = useRouter();
const { versionId, requireVersion } = useOntologyVersion();
const { om_ai_task_status } = proxy.useDict("om_ai_task_status");

const loading = ref(false);
const showSearch = ref(true);
const rows = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const defaultSort = ref({ prop: "createTime", order: "descending" });

/** 列显隐配置，与系统列表页右侧工具栏保持一致。 */
const columns = ref([
  { key: "id", label: "编号", visible: true },
  { key: "taskName", label: "任务名称", visible: true },
  { key: "description", label: "描述", visible: true },
  { key: "status", label: "任务状态", visible: true },
  { key: "failureReason", label: "失败原因", visible: true },
  { key: "candidateCount", label: "候选数量", visible: true },
  { key: "createBy", label: "创建人", visible: true },
  { key: "createTime", label: "创建时间", visible: true },
  { key: "operation", label: "操作", visible: true },
]);

/** 判断指定列表列是否显示。 */
function isColumnVisible(key) {
  return columns.value.find((column) => column.key === key)?.visible ?? true;
}

const query = reactive({
  taskName: "",
  status: null,
  pageNum: 1,
  pageSize: 10,
  orderByColumn: defaultSort.value.prop,
  isAsc: defaultSort.value.order,
});

/** 查询任务分页列表。 */
async function loadTasks() {
  if (!versionId.value) {
    rows.value = [];
    total.value = 0;
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const response = await listAiTasks({
      ...query,
      versionId: versionId.value,
    });
    rows.value = response.data?.rows || [];
    total.value = Number(response.data?.total || 0);
  } finally {
    loading.value = false;
  }
}

/** 按当前查询条件查询任务。 */
function handleQuery() {
  query.pageNum = 1;
  loadTasks();
}

/** 重置查询条件。 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 进入三步式任务创建页面。 */
function openCreateDialog() {
  if (!requireVersion()) return;
  router.push(`/om/${versionId.value}/aiModeling/add`);
}

/** 进入与新增一致的三步式任务修改页面。 */
function openEditDialog(row) {
  if (!requireVersion()) return;
  router.push({
    path: `/om/${versionId.value}/aiModeling/add`,
    query: { id: row.id },
  });
}

/** 打开任务详情页。 */
function openDetail(row) {
  if (!requireVersion()) return;
  router.push(`/om/${versionId.value}/aiModeling/detail/${row.id}`);
}

/** 处理更多菜单中的修改、删除操作。 */
function handleMoreCommand(command, row) {
  if (command === "edit" && auth.hasPermi("om:aiModeling:edit")) {
    openEditDialog(row);
    return;
  }
  if (command === "delete" && auth.hasPermi("om:aiModeling:remove")) {
    removeTasks(row);
  }
}

/** 记录勾选行 id。 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map((row) => row.id);
}

/** 列头排序变化后更新排序参数并重新查询。 */
function handleSortChange({ prop, order }) {
  query.orderByColumn = order ? prop : defaultSort.value.prop;
  query.isAsc = order || defaultSort.value.order;
  query.pageNum = 1;
  loadTasks();
}

/** 删除未执行中的任务，支持勾选批量删除。 */
function removeTasks(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids.length) return;
  const target = row ? `任务“${row.taskName}”` : `选中的 ${ids.length} 条任务`;
  proxy.$modal
    .confirm(`确认删除${target}？`)
    .then(() => deleteAiTask(ids.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      if (query.pageNum > 1 && rows.value.length <= ids.length)
        query.pageNum -= 1;
      loadTasks();
    });
}

/** 清空旧结果并对任务执行全量解析。 */
function reparseTask(row) {
  proxy.$modal
    .confirm("重新解析会清除旧候选结果和解析过程，是否继续？")
    .then(() => reparseAiTask(row.id))
    .then(() => {
      proxy.$modal.msgSuccess("已重新开始解析");
      loadTasks();
    });
}

watch(
  versionId,
  (value) => {
    if (value) loadTasks();
    else {
      rows.value = [];
      total.value = 0;
    }
  },
  { immediate: true }
);
const refreshTimer = setInterval(() => {
  if (rows.value.some((item) => item.status === 1)) loadTasks();
}, 3000);
onUnmounted(() => clearInterval(refreshTimer));
</script>

<style scoped lang="scss">
/* 与系统列表页“更多”操作浮层保持一致。 */
.butgdlist {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.butgdlist .el-button {
  width: 100%;
  justify-content: center;
  margin-left: 0;
}
</style>
