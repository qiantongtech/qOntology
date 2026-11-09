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
  <div
    :class="props.embedded ? 'project-version-embedded' : 'app-container'"
    ref="app-container"
  >
    <GuideTip v-if="!props.embedded" tip-id="basic/projectVersion.list" />

    <!-- 独立页面使用完整布局，内嵌模式由详情页提供外层卡片。 -->
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="{
        fullContent: !props.embedded,
        searchShadow: !props.embedded,
        actions: { table: { search: !props.embedded } },
      }"
    >
      <!-- 详情页只关心当前项目的全部版本，不展示版本号和状态筛选。 -->
      <template v-if="!props.embedded" #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['basic:project:projectversion:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['basic:project:projectversion:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 版本号列保留“当前版本”标签。 -->
        <template #version="{ row }">
          <span class="text-ellipsis" :title="row.version || ''">{{
            row.version || "-"
          }}</span>
          <el-tag v-if="isCurrentVersion(row)" type="primary" class="ml4">
            当前版本
          </el-tag>
        </template>
        <!-- 状态列放在创建人之前，启停交互与术语管理一致。 -->
        <template #status="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :loading="statusSaving[row.id] === true"
            :disabled="
              !auth.hasPermi('basic:project:projectversion:edit') ||
              statusSaving[row.id] === true
            "
            :aria-label="`${row.version}状态`"
            @change="(value) => handleStatusChange(row, value)"
          />
        </template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{
              row.createBy || "-"
            }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{
              creatorPhones[row.creatorId]
            }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            @click="handleDiff(row)"
            v-hasPermi="['basic:project:projectversion:diff']"
          >
            <svg-icon
              icon-class="meta-version"
              class="handle-svg-icon"
            />版本对比</el-button
          >
          <el-button
            link
            type="primary"
            icon="view"
            @click="handleDetail(row)"
            v-hasPermi="['basic:project:projectversion:query']"
            >详情</el-button
          >
          <el-popover
            v-if="
              auth.hasPermiOr([
                'basic:project:projectversion:edit',
                'basic:project:projectversion:remove',
              ])
            "
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              v-if="auth.hasPermi('basic:project:projectversion:edit')"
              link
              type="primary"
              icon="Edit"
              :disabled="row.status === 1"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('basic:project:projectversion:remove')"
              link
              type="danger"
              icon="Delete"
              :disabled="row.status === 1"
              @click="handleDelete(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改项目版本对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="维护项目版本，填写版本号、状态与描述，并关联基准版本，支持本体模型的版本管理与差异对比。"
      />
      <el-form
        ref="projectVersionRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="version-form"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <qt-form-item label="版本号" prop="version" tip="填写本版本的标识，例如 V1.0，用于区分不同版本。">
              <el-input v-model="form.version" placeholder="请输入版本号" />
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="状态" prop="status" >
              <el-radio-group v-model="form.status">
                <el-radio
                    v-for="dict in project_status"
                    :key="Number(dict.value)"
                    :value="Number(dict.value)"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="描述" prop="description" >
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="请输入描述"
                maxlength="256 个字符"
                show-word-limit
              />
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="基准版本" prop="baseVersion" tip="当前版本所基于的版本，新建首个版本时可为空。">
              <el-input
                disabled
                v-model="form.baseVersion"
                placeholder="请输入基准版本"
              />
            </qt-form-item>
          </el-col>

        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="submitForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 项目版本详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="版本号" prop="version">
          <div class="form-readonly">
            {{ form.version }}
          </div>
        </el-form-item>
        <el-form-item label="基准版本" prop="baseVersion">
          <div class="form-readonly">
            {{ form.baseVersion || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <div class="form-readonly textarea">
            {{ form.description }}
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <dict-tag :options="project_status" :value="form.status" />
        </el-form-item>
        <el-form-item label="创建人" prop="createBy">
          <div class="form-readonly">
            {{ form.createBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="联系方式">
          <div class="form-readonly">
            {{ creatorPhones[form.creatorId] || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <div class="form-readonly">
            {{ form.createTime }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectVersion">
import { useRoute, useRouter } from "vue-router";
import {
  addProjectVersion,
  delProjectVersion,
  getProjectVersion,
  listProjectVersion,
  updateProjectVersion,
  updateProjectVersionStatus,
} from "@/api/basic/project/projectVersion.js";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { creatorPhones, formatCreator } = useCreatorPhones();
const { project_status } = proxy.useDict("project_status");
const route = useRoute();
const router = useRouter();

// 项目详情页以页签形式内嵌本页，固定项目编号与项目当前版本号；不传参数时保持路由驱动的独立页面行为。
const props = defineProps({
  embedded: { type: Boolean, default: false },
  projectId: { type: [String, Number], default: undefined },
  currentVersion: { type: String, default: "" },
});

const tableRef = ref();
const projectVersionRef = ref();
const statusSaving = reactive({});

// 表格配置
const tableStore = reactive({
  config: {
    sort: true,
    // 首查延迟到项目编号落定之后（独立页面取路由 versionId，内嵌模式取 projectId 入参），由 watch 手动触发。
    initResquest: false,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 80,
      minWidth: 80,
      align: "center",
      sortable: true,
    },
    {
      label: "版本号",
      prop: "version",
      slot: "version",
      width: 200,
      minWidth: 200,
      align: "left",
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "基准版本",
      prop: "baseVersion",
      slot: "text",
      width: 180,
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "状态",
      prop: "status",
      slot: "status",
      width: 90,
      minWidth: 90,
      align: "center",
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 130,
      minWidth: 130,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      minWidth: 160,
      align: "left",
      sortable: true,
    },
    {
      label: "操作",
      slot: "handle",
      width: 240,
      minWidth: 240,
      fixed: "right",
    },
  ],
  func: loadProjectVersionList,
  params: {
    projectId: props.embedded ? props.projectId ?? null : null,
    version: null,
    status: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "版本号", prop: "version", component: { is: "input" } },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: project_status },
    },
  ],
});

const open = ref(false);
const openDetail = ref(false);
const saving = ref(false);
const ids = ref([]);
const multiple = ref(true);
const selectedRows = ref([]);
const title = ref("");

const versionId = ref(route.params.versionId || null);
// 独立页面当前路由对应的版本记录；命名避开内嵌模式的 currentVersion 版本号入参。
const routeVersion = ref(null);

/** 内嵌模式缺少项目编号时直接返回空页，避免接口忽略空筛选后返回全部项目的版本。 */
function loadProjectVersionList(params) {
  if (!params.projectId)
    return Promise.resolve({ data: { rows: [], total: 0 } });
  return listProjectVersion(params);
}

/** 独立页面按导航栏当前版本标记，项目详情页按项目概要的当前版本号标记。 */
function isCurrentVersion(row) {
  if (props.embedded) return false;
  return Number(versionId.value) === Number(row.id);
}

const data = reactive({
  form: {},
  rules: {
    version: [{ required: true, message: "版本号不能为空", trigger: "blur" }],
    status: [
      {
        required: true,
        type: "number",
        enum: [0, 1],
        message: "请选择状态",
        trigger: "change",
      },
    ],
  },
});

const { form, rules } = toRefs(data);

// 独立页面：路由版本变化时先取当前版本信息，落定 projectId 后再刷新列表。
watch(
  () => route.params.versionId,
  async () => {
    if (props.embedded) return;
    versionId.value = route.params.versionId || null;
    if (!versionId.value) return;
    const response = await getProjectVersion(versionId.value);
    routeVersion.value = response.data;
    tableStore.params.projectId = routeVersion.value.projectId;
    await nextTick();
    tableRef.value?.resetQuery();
  },
  { immediate: true }
);

// 内嵌页面：项目编号由项目详情传入，变更时重置分页并重新拉取当前项目的版本。
watch(
  () => props.projectId,
  async (projectId) => {
    if (!props.embedded || !projectId) return;
    tableStore.params.projectId = projectId;
    await nextTick();
    tableRef.value?.resetQuery();
  },
  { immediate: true }
);

/** 关闭新增/修改与详情弹窗并重置表单。 */
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

/** 重置表单字段；独立页面基准版本取当前路由版本，详情页取项目当前版本。 */
function reset() {
  form.value = {
    id: null,
    projectId: props.embedded ? props.projectId : routeVersion.value?.projectId,
    version: null,
    description: null,
    status: 1,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
    baseVersion: props.embedded
      ? props.currentVersion || null
      : routeVersion.value?.version,
    baseVersionId: props.embedded ? null : routeVersion.value?.id,
  };
  projectVersionRef.value?.resetFields();
}

/**
 * 多选框选中数据，同步批量删除按钮的禁用状态。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  selectedRows.value = selection;
  multiple.value = !selection.length;
}

/**
 * 启用/禁用项目版本，确认后只提交状态；取消或请求失败时回滚开关。
 * @param {Object} row 当前行数据
 * @param {Number} value 切换后的状态：0 禁用，1 启用
 */
async function handleStatusChange(row, value) {
  const previous = value === 1 ? 0 : 1;
  const text = value === 1 ? "启用" : "禁用";
  statusSaving[row.id] = true;
  try {
    await proxy.$modal.confirm(`确认要${text}项目版本“${row.version}”吗？`);
    await updateProjectVersionStatus({ id: row.id, status: value });
  } catch {
    row.status = previous;
    return;
  } finally {
    delete statusSaving[row.id];
  }
  proxy.$modal.msgSuccess("操作成功");
  tableRef.value?.getList();
  notifyProjectVersionChanged();
}

/** 新增按钮操作。 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增项目版本";
}

/**
 * 修改按钮操作。
 * @param {Object} row 当前行数据
 */
function handleUpdate(row) {
  reset();
  const targetId = row.id || ids.value[0];
  getProjectVersion(targetId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改项目版本";
  });
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
function handleDetail(row) {
  reset();
  const targetId = row.id || ids.value[0];
  getProjectVersion(targetId).then((response) => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "项目版本详情";
  });
}

/** 通知 Navbar 刷新项目版本下拉选项数据。 */
function notifyProjectVersionChanged() {
  window.dispatchEvent(new CustomEvent("project-version-changed"));
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
async function submitForm() {
  if (saving.value) return;
  const valid = await projectVersionRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    if (form.value.id != null) {
      await updateProjectVersion(form.value);
      proxy.$modal.msgSuccess("修改成功");
    } else {
      await addProjectVersion(form.value);
      proxy.$modal.msgSuccess("新增成功");
    }
    open.value = false;
    tableRef.value?.getList();
    notifyProjectVersionChanged();
  } catch {
    // 接口错误由全局请求层提示；保留弹窗草稿，允许用户修改后重新提交。
  } finally {
    saving.value = false;
  }
}

/**
 * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
 * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
 */
function handleDelete(row) {
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.id 区分）
  if (row?.id != null) {
    const deleteIds = row.id;
    proxy.$modal
      .confirm('是否确认删除项目版本编号为"' + deleteIds + '"的数据项？')
      .then(function () {
        return delProjectVersion(deleteIds);
      })
      .then(() => {
        tableRef.value?.getList();
        notifyProjectVersionChanged();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {});
    return;
  }

  // 批量删除：根据状态（1=启用不可删除，0=禁用可删除）判断
  const rows = selectedRows.value;
  if (!rows.length) return;
  const deletableRows = rows.filter((item) => item.status === 0);
  const undeletableCount = rows.length - deletableRows.length;
  const deletableIds = deletableRows.map((item) => item.id);
  proxy.$modal
    .confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    )
    .then(function () {
      if (!deletableIds.length) {
        proxy.$modal.msgWarning("无可删除的数据");
        return;
      }
      return delProjectVersion(deletableIds);
    })
    .then(() => {
      tableRef.value?.getList();
      notifyProjectVersionChanged();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 跳转版本对比页，携带项目编号与待对比版本编号。
 * 项目详情页没有导航栏当前版本，改以点击的版本作为基准版本，并标记来源供对比页返回详情页。
 * @param {Object} row 当前行数据
 */
function handleDiff(row) {
  console.log(route.path);
  let versionId = props.embedded ? row.id : route.params.versionId;
  let projectId = props.embedded ? props.projectId : routeVersion.value.projectId;
  let compareVersion = row.id;
  if(route.path === '/basic/project/detail'){
    router.push({
    path: `/basic/project/detail/versionDiff`,
    query: {
      projectId: projectId,
      currentVersion: versionId,
      compareVersion: compareVersion
    },
  });
  } else{
    router.push({
      path: `/om/${props.embedded ? row.id : versionId}/projectVersion/diff`,
      query: {
        projectId: projectId,
        ...(props.embedded
          ? { from: "projectDetail" }
          : { compareVersion: compareVersion }),
      },
    });
  }

}
</script>

<style scoped lang="scss">
.handle-svg-icon {
  /* 操作列图标固定 12*12 */
  width: 12px;
  height: 12px;
  margin-right: 4px;
  vertical-align: -0.125em;
  /* 跟随按钮文字颜色（含 hover / active） */
  fill: currentColor;
  color: inherit;
}

/* 对齐对象类型新增页的间隔：带描述提示的表单项底部留白，避免说明文字贴到下一字段。 */
.version-form :deep(.el-form-item.has-tip) {
  margin-bottom: 25px;
}
</style>
