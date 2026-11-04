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
    <GuideTip tip-id="basic/projectVersion.list" />

    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
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
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 版本号列保留“当前版本”标签。 -->
        <template #version="{ row }">
          <span class="text-ellipsis" :title="row.version || ''">{{
            row.version || "-"
          }}</span>
          <el-tag
            v-if="Number(versionId) === Number(row.id)"
            type="primary"
            class="ml4"
          >
            当前版本
          </el-tag>
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
            <svg-icon icon-class="meta-version" class="handle-svg-icon" />版本对比</el-button
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
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('basic:project:projectversion:remove')"
              link
              type="danger"
              icon="Delete"
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
      <el-form
        ref="projectVersionRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入版本号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="请输入描述"
                maxlength="200 个字符"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="基准版本" prop="baseVersion">
              <el-input
                disabled
                v-model="form.baseVersion"
                placeholder="请输入基准版本"
              />
            </el-form-item>
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
      <el-form :model="form" label-width="80px" class="column-form">
        <el-form-item label="版本号" prop="version">
          <div class="form-readonly">
            {{ form.version }}
          </div>
        </el-form-item>
        <el-form-item label="基准版本" prop="baseVersion">
          <div class="form-readonly">
            {{ form.baseVersion || "—" }}
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description" class="row-full">
          <div class="form-readonly textarea">
            {{ form.description }}
          </div>
        </el-form-item>
        <el-form-item label="创建人" prop="createBy">
          <div class="form-readonly">
            {{ formatCreator(form) }}
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
} from "@/api/basic/project/projectVersion.js";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { creatorPhones, formatCreator } = useCreatorPhones();
const route = useRoute();
const router = useRouter();

const tableRef = ref();
const projectVersionRef = ref();

// 表格配置
const tableStore = reactive({
  config: {
    sort: true,
    // 首查延迟到路由 versionId 解析出 projectId 之后，由 watch 手动触发。
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
  func: listProjectVersion,
  params: {
    projectId: null,
    version: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [{ label: "版本号", prop: "version", component: { is: "input" } }],
});

const open = ref(false);
const openDetail = ref(false);
const saving = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");

const versionId = ref(route.params.versionId || null);
const currentVersion = ref(null);

const data = reactive({
  form: {},
  rules: {
    version: [{ required: true, message: "版本号不能为空", trigger: "blur" }],
  },
});

const { form, rules } = toRefs(data);

// 路由版本变化时先取当前版本信息，落定 projectId 后再刷新列表。
watch(
  () => route.params.versionId,
  async () => {
    versionId.value = route.params.versionId || null;
    if (!versionId.value) return;
    const response = await getProjectVersion(versionId.value);
    currentVersion.value = response.data;
    tableStore.params.projectId = currentVersion.value.projectId;
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

/** 重置表单字段，基准版本默认取当前路由版本。 */
function reset() {
  form.value = {
    id: null,
    projectId: currentVersion.value?.projectId,
    version: null,
    description: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
    baseVersion: currentVersion.value?.version,
    baseVersionId: currentVersion.value?.id,
  };
  projectVersionRef.value?.resetFields();
}

/**
 * 多选框选中数据，同步批量删除按钮的禁用状态。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
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
  const deleteIds = row?.id ?? ids.value;
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
}

/**
 * 跳转版本对比页，携带项目编号与待对比版本编号。
 * @param {Object} row 当前行数据
 */
function handleDiff(row) {
  router.push({
    path: `/om/${versionId.value}/projectVersion/diff`,
    query: {
      projectId: currentVersion.value.projectId,
      compareVersion: row.id,
    },
  });
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
</style>
