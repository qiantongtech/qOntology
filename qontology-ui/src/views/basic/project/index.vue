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
    <GuideTip tip-id="basic/project.list" />

    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['basic:project:project:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['basic:project:project:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>
      <qt-table v-bind="tableStore" ref="tableRef">
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="Download"
            @click="handleOwlExport(row)"
            >导出</el-button
          >
          <el-button
            link
            type="primary"
            icon="view"
            @click="handleDetail(row)"
            v-hasPermi="['basic:project:project:query']"
            >详情</el-button
          >
          <el-popover
            v-if="
              auth.hasPermiOr([
                'basic:project:project:edit',
                'basic:project:project:remove',
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
              v-if="auth.hasPermi('basic:project:project:edit')"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('basic:project:project:remove')"
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

    <el-dialog
      v-model="owlExportOpen"
      title="导出"
      width="700px"
      :append-to="$refs['app-container']"
      :close-on-click-modal="false"
      :close-on-press-escape="!owlExporting"
      :show-close="!owlExporting"
      :before-close="closeOwlExport"
      draggable
    >
      <el-form
        ref="owlExportRef"
        :model="owlExportForm"
        :rules="owlExportRules"
        label-width="120px"
        @submit.prevent="submitOwlExport"
      >
        <el-form-item label="导出格式" prop="format">
          <el-radio-group
            v-model="owlExportForm.format"
            :validate-event="false"
            :disabled="owlExporting"
          >
            <el-radio value="TURTLE">Turtle（.ttl）</el-radio>
            <el-radio value="XML" disabled>RDF/XML（.xml）</el-radio>
            <el-radio value="JSONLD" disabled>JSON-LD（.jsonld）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName">
          <el-input
            v-model="owlExportForm.fileName"
            placeholder="请输入文件名称"
            maxlength="128"
            :validate-event="false"
            :disabled="owlExporting"
            ><template #append>{{
              owlExportForm.format === "TURTLE" ? ".ttl" : ".owl"
            }}</template></el-input
          >
        </el-form-item>
        <el-form-item label="导出版本" prop="versionId">
          <el-select
            v-model="owlExportForm.versionId"
            placeholder="请选择导出版本"
            style="width: 100%"
            :loading="owlVersionsLoading"
            :disabled="owlExporting || owlVersionsLoading"
            :validate-event="false"
            no-data-text="当前项目暂无可导出版本"
            clearable
          >
            <el-option
              v-for="version in owlVersions"
              :key="version.dictValue"
              :label="version.dictLabel"
              :value="String(version.dictValue)"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button :disabled="owlExporting" @click="owlExportOpen = false"
          >取 消</el-button
        >
        <el-button
          type="primary"
          :loading="owlExporting"
          :disabled="owlVersionsLoading || !owlVersions.length"
          @click="submitOwlExport"
          >确 定</el-button
        >
      </template>
    </el-dialog>

    <!-- 添加或修改项目对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form
        ref="projectRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="column-form"
        @submit.prevent
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目领域" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入项目领域" />
        </el-form-item>
        <qt-form-item
          label="命名空间"
          prop="namespace"
          class="row-full"
          tip="命名空间用于唯一标识本体的术语来源，遵循 W3C 标准。例如：https://example.com"
        >
          <el-input v-model="form.namespace" placeholder="请输入命名空间" />
        </qt-form-item>
        <el-form-item label="包含范围" prop="includeRange" class="row-full">
          <el-input
            v-model="form.includeRange"
            type="textarea"
            placeholder="请输入包含范围"
            maxlength="500 个字符"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排除范围" prop="excludeRange" class="row-full">
          <el-input
            v-model="form.excludeRange"
            type="textarea"
            placeholder="请输入排除范围"
            maxlength="500 个字符"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述" prop="description" class="row-full">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
            maxlength="500 个字符"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in project_status"
              :key="Number(dict.value)"
              :value="Number(dict.value)"
              >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="初始版本" prop="currentVersion">
          <el-input
            :disabled="dialogMode === 'edit'"
            v-model="form.currentVersion"
            placeholder="请输入初始版本"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
    <!-- 项目详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form
        ref="projectRef"
        :model="form"
        label-width="80px"
        class="column-form"
      >
        <el-form-item label="项目名称" prop="name">
          <div class="form-readonly">
            {{ form.name }}
          </div>
        </el-form-item>
        <el-form-item label="项目领域" prop="domain">
          <div class="form-readonly">
            {{ form.domain }}
          </div>
        </el-form-item>
        <el-form-item label="命名空间" prop="namespace" class="row-full">
          <div class="form-readonly">
            {{ form.namespace }}
          </div>
        </el-form-item>
        <el-form-item label="包含范围" prop="includeRange" class="row-full">
          <div class="form-readonly textarea">
            {{ form.includeRange }}
          </div>
        </el-form-item>
        <el-form-item label="排除范围" prop="excludeRange" class="row-full">
          <div class="form-readonly textarea">
            {{ form.excludeRange }}
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description" class="row-full">
          <div class="form-readonly textarea">
            {{ form.description }}
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <dict-tag
            :options="project_status"
            :value="form.status"
            class="con-value access-tag"
          />
        </el-form-item>
        <el-form-item label="当前版本" prop="currentVersion">
          <div class="form-readonly">
            {{ form.currentVersion }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Project">
import {
  addProject,
  delProject,
  getProject,
  listProject,
  updateProject,
  listOwlExportVersions,
  exportProjectOntology,
} from "@/api/basic/project/project";
import { saveAs } from "file-saver";
import { onBeforeUnmount } from "vue";
import auth from "@/plugins/auth";

const { proxy } = getCurrentInstance();
const { project_status } = proxy.useDict("project_status");

const tableRef = ref();
const owlExportOpen = ref(false);
const owlExportRef = ref();
const owlExporting = ref(false);
const owlVersionsLoading = ref(false);
const owlVersions = ref([]);
const owlExportProjectId = ref();
const owlExportForm = reactive({
  fileName: "",
  versionId: "",
  format: "TURTLE",
});
const owlExportRules = {
  fileName: [
    {
      required: true,
      whitespace: true,
      message: "文件名称不能为空",
      trigger: "blur",
    },
    { max: 128, message: "文件名称不能超过128个字符", trigger: "blur" },
    {
      validator: (_rule, value, callback) => {
        callback(
          /[\\/:*?"<>|\x00-\x1f\x7f]/.test(value || "")
            ? new Error("文件名称不能包含路径分隔符或特殊字符")
            : undefined
        );
      },
      trigger: "blur",
    },
  ],
  versionId: [{ required: true, message: "请选择导出版本", trigger: "change" }],
  format: [{ required: true, message: "请选择导出格式", trigger: "change" }],
};
let owlExportRequest = 0;

async function handleOwlExport(row) {
  if (owlExporting.value) return;
  const request = ++owlExportRequest;
  owlExportProjectId.value = row.id;
  Object.assign(owlExportForm, {
    fileName: row.name || "",
    versionId: "",
    format: "TURTLE",
  });
  owlVersions.value = [];
  owlExportOpen.value = true;
  owlVersionsLoading.value = true;
  nextTick(() => owlExportRef.value?.clearValidate());
  try {
    const response = await listOwlExportVersions(row.id);
    if (request !== owlExportRequest || !owlExportOpen.value) return;
    owlVersions.value = response?.data || [];
    // 接口按版本 ID 倒序返回，首项为最新创建的版本。
    owlExportForm.versionId = owlVersions.value.length
      ? String(owlVersions.value[0].dictValue)
      : "";
  } finally {
    if (request === owlExportRequest) owlVersionsLoading.value = false;
  }
}

function closeOwlExport(done) {
  if (!owlExporting.value) done();
}

async function submitOwlExport() {
  if (owlExporting.value || owlVersionsLoading.value) return;
  const request = owlExportRequest;
  if (
    !(await owlExportRef.value.validate().catch(() => false)) ||
    owlExporting.value ||
    request !== owlExportRequest ||
    !owlExportOpen.value
  )
    return;
  const extension = owlExportForm.format === "TURTLE" ? ".ttl" : ".owl";
  const fileName =
    owlExportForm.fileName.trim().replace(/\.(owl|ttl)$/i, "") + extension;
  const selectedProject = owlExportProjectId.value;
  const payload = {
    fileName: owlExportForm.fileName.trim(),
    versionId: owlExportForm.versionId,
    format: owlExportForm.format,
  };
  owlExporting.value = true;
  try {
    const blob = await exportProjectOntology(selectedProject, payload);
    if (request !== owlExportRequest || !blob) return;
    if ((blob.type || "").includes("json")) {
      const error = JSON.parse(await blob.text());
      proxy.$modal.msgError(error.msg || "导出失败");
      return;
    }
    saveAs(blob, fileName);
    proxy.$modal.msgSuccess("导出成功");
    owlExportOpen.value = false;
  } finally {
    if (request === owlExportRequest) owlExporting.value = false;
  }
}

onBeforeUnmount(() => {
  ++owlExportRequest;
});

// 表格配置
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55 },
    { label: "编号", prop: "id", width: 80, align: "left", sortable: true },
    {
      label: "项目名称",
      prop: "name",
      width: 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      width: 240,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "项目领域",
      prop: "domain",
      width: 120,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "命名空间",
      prop: "namespace",
      width: 240,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "状态",
      prop: "status",
      dict: "project_status",
      width: 100,
      align: "center",
    },
    { label: "创建人", prop: "createBy", width: 100, align: "left" },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      align: "left",
      sortable: true,
    },
    {
      label: "操作",
      slot: "handle",
      width: 240,
      align: "left",
      fixed: "right",
    },
  ],
  func: listProject,
  params: {
    name: null,
    domain: null,
    namespace: null,
    status: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "项目名称", prop: "name", component: { is: "input" } },
    { label: "项目领域", prop: "domain", component: { is: "input" } },
    { label: "命名空间", prop: "namespace", component: { is: "input" } },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: project_status },
    },
  ],
});

const open = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");
const dialogMode = ref("add"); // add, edit, detail

// 命名空间需符合 W3C 的 URI 格式：以 http/https 开头的绝对 URI，且不含空白字符
const namespaceUriPattern = /^https?:\/\/[^\s/?#]+(?:[/?#]\S*)?$/i;

const data = reactive({
  form: {},
  rules: {
    name: [{ required: true, message: "项目名称不能为空", trigger: "blur" }],
    domain: [{ required: true, message: "项目领域不能为空", trigger: "blur" }],
    namespace: [
      { required: true, message: "命名空间不能为空", trigger: "blur" },
      {
        validator: (_rule, value, callback) => {
          callback(
            namespaceUriPattern.test(value || "")
              ? undefined
              : new Error(
                  "命名空间需符合 W3C 的 URI 格式，例如：https://example.com/ontology#"
                )
          );
        },
        trigger: "blur",
      },
    ],
    status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
    currentVersion: [
      { required: true, message: "初始版本不能为空", trigger: "blur" },
    ],
    includeRange: [
      { required: true, message: "包含范围不能为空", trigger: "blur" },
    ],
    excludeRange: [
      { required: true, message: "排除范围不能为空", trigger: "blur" },
    ],
    description: [{ required: true, message: "描述不能为空", trigger: "blur" }],
  },
});

const { form, rules } = toRefs(data);

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    domain: null,
    namespace: null,
    status: 1,
    currentVersion: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
  };
  proxy.resetForm("projectRef");
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  dialogMode.value = "add";
  title.value = "新增项目";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getProject(_id).then((response) => {
    form.value = response.data;
    open.value = true;
    dialogMode.value = "edit";
    title.value = "修改项目";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getProject(_id).then((response) => {
    form.value = response.data;
    openDetail.value = true;
    dialogMode.value = "detail";
    title.value = "项目详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectRef"].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        updateProject(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            tableRef.value.getList();
          })
          .catch((error) => {});
      } else {
        addProject(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            tableRef.value.getList();
          })
          .catch((error) => {});
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除项目编号为"' + _ids + '"的数据项？')
    .then(function () {
      return delProject(_ids);
    })
    .then(() => {
      tableRef.value.getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
</script>
