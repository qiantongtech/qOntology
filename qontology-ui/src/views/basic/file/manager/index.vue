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
    <GuideTip tip-id="basic/file/manager.list" />
    <el-container>
      <DeptTree
        ref="deptTreeRef"
        :deptOptions="categoryOptions"
        v-model:leftWidth="leftWidth"
        :placeholder="'请输入分类名称'"
        @node-click="handleNodeClick"
      />
      <el-main>
        <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
          <template #search>
            <qt-search-bar
              :items="searchItems"
              :params="tableStore.params"
              :table-ref="tableRef"
              @reset="handleReset"
            />
          </template>

          <template #actions-data>
            <el-button
              type="primary"
              plain
              @click="handleAdd"
              v-hasPermi="['basic:file:manager:add']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </template>

          <qt-table v-bind="tableStore" ref="tableRef">
            <template #fileName="{ row }">
              <el-link
                type="primary"
                :underline="false"
                class="text-ellipsis"
                @click="handlePreview(row)"
                >{{ row.fileName || "-" }}</el-link
              >
            </template>
            <template #fileSize="{ row }">{{ formatSize(row.fileSize) }}</template>
            <template #action="{ row }">
              <el-button
                link
                type="primary"
                icon="View"
                @click="handlePreview(row)"
                >预览</el-button
              >
              <el-button
                link
                type="primary"
                icon="Download"
                @click="handleDownload(row)"
                >下载</el-button
              >
              <el-popover
                placement="bottom"
                :width="120"
                trigger="click"
                popper-class="handle-popover"
              >
                <template #reference>
                  <el-button link type="primary" icon="ArrowDown"
                    >更多</el-button
                  >
                </template>
                <el-button
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(row)"
                  v-hasPermi="['basic:file:manager:edit']"
                  >修改</el-button
                >
                <el-button
                  link
                  type="danger"
                  icon="Delete"
                  @click="handleDelete(row)"
                  v-hasPermi="['basic:file:manager:remove']"
                  >删除</el-button
                >
              </el-popover>
            </template>
          </qt-table>
        </qt-wrap>
      </el-main>
    </el-container>

    <!-- 添加或修改模型数据文件对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      append-to="body"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form
        ref="fileRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="column-form"
      >
        <el-form-item label="数据分类" prop="categoryId" class="row-full">
          <el-tree-select
            v-model="form.categoryId"
            :data="categoryOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            value-key="id"
            placeholder="请选择数据分类"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="上传文件" prop="filePath" class="row-full">
          <FileUpload2
            v-model="form.filePath"
            :fileName="form.fileName"
            limit="1"
            :fileType="[
              'txt',
              'pdf',
              'html',
              'xlsx',
              'xls',
              'docx',
              'csv',
              'md',
              'mdx',
              'htm',
              'markdown',
            ]"
            @update:fileSize="(val) => (form.fileSize = val)"
            @update:fileExt="(val) => (form.fileSuffix = val)"
            @update:fileName="updateFormFileName"
          />
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName" class="row-full">
          <el-input v-model="form.fileName" placeholder="请输入文件名称" />
        </el-form-item>
        <el-form-item label="文件描述" prop="description" class="row-full">
          <el-input
            class="char-count"
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入文件描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark" class="row-full">
          <el-input
            class="char-count"
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imageViewerVisible"
      :url-list="imageViewerList"
      @close="closeImageViewer"
    />
  </div>
</template>

<script setup name="BasicFile">
import { getCurrentInstance, reactive, ref, toRefs, onMounted } from "vue";
import {
  addModelDataFile,
  delModelDataFile,
  getModelDataFile,
  listModelDataFile,
  updateModelDataFile,
  getFileTypes,
} from "@/api/basic/file/basicFile.js";
import DeptTree from "@/components/DeptTree";
import FileUpload2 from "@/components/FileUpload2";
import { formatSize } from "@/utils/index";
import { filePreview } from "@/utils/kkFileView";

const { proxy } = getCurrentInstance();
const deptTreeRef = ref(null);
const tableRef = ref(null);
const open = ref(false);
const title = ref("");
const categoryOptions = ref([]);
const leftWidth = ref(280);

// 图片预览相关
const imageViewerVisible = ref(false);
const imageViewerList = ref([]);

const searchItems = [
  { label: "文件名称", prop: "fileName", component: { is: "input" } },
];

// el-table 透出的排序方向是 ascending/descending，接口约定为 asc/desc
function formatParams(params) {
  const order = { ascending: "asc", descending: "desc" }[params.isAsc];
  return order ? { ...params, isAsc: order } : params;
}

const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
    },
  },
  params: {
    fileName: undefined,
    categoryId: undefined,
    fileType: undefined,
    ids: undefined,
  },
  func: listModelDataFile,
  events: { formatParams },
  columns: [
    {
      label: "编号",
      prop: "id",
      width: 80,
      sortable: "custom",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "文件名称",
      slot: "fileName",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "文件描述",
      prop: "description",
      minWidth: 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "分类",
      prop: "categoryName",
      width: 120,
      showOverflowTooltip: { effect: "light" },
    },
    { label: "文件大小", slot: "fileSize", width: 120 },
    { label: "创建人", prop: "createBy", width: 100 },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 160,
      sortable: "custom",
      sortOrders: ["descending", "ascending"],
    },
    { label: "操作", slot: "action", width: 240, fixed: "right" },
  ],
});

const data = reactive({
  form: {},
  rules: {
    categoryId: [
      { required: true, message: "数据分类不能为空", trigger: "change" },
    ],
    fileName: [
      { required: true, message: "文件名称不能为空", trigger: "change" },
    ],
    filePath: [
      { required: true, message: "文件路径不能为空", trigger: "blur" },
    ],
  },
});

const { form, rules } = toRefs(data);

/** 查询分类树结构 */
function getCategoryTree() {
  getFileTypes().then((response) => {
    categoryOptions.value = proxy.handleTree(
      response.data || [],
      "id",
      "parentId"
    );
  });
}

/** 节点单击事件 */
function handleNodeClick(data) {
  let categoryIds = [];
  const collectIds = (node) => {
    categoryIds.push(node.id);
    if (node.children) node.children.forEach((child) => collectIds(child));
  };
  collectIds(data);
  tableStore.params.ids = categoryIds;
  tableRef.value?.resetQuery();
}

/** 重置按钮操作 */
function handleReset() {
  deptTreeRef.value?.resetTree();
  tableStore.params.ids = undefined;
}

/** 重置表单 */
function reset() {
  form.value = {
    id: undefined,
    categoryId: undefined,
    fileName: "",
    fileSuffix: "",
    fileType: "OTHER",
    fileSize: 0,
    filePath: "",
    storageType: "LOCAL",
    description: "",
    remark: "",
  };
  proxy.resetForm("fileRef");
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  title.value = "新增文件";
  open.value = true;
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  const response = await getModelDataFile(row.id);
  form.value = response.data;
  // 文件名称统一为字符串，供 el-input 正常回显
  form.value.fileName = form.value.fileName || "";
  title.value = "修改文件";
  open.value = true;
}

/** 更新表单文件名 */
function updateFormFileName(name) {
  form.value.fileName = name;
}

/** 提交表单 */
function submitForm() {
  proxy.$refs["fileRef"].validate((valid) => {
    if (!valid) return;
    const data = { ...form.value };
    if (Array.isArray(data.fileName)) {
      data.fileName = data.fileName.join(",");
    }
    const action = data.id ? updateModelDataFile : addModelDataFile;
    action(data).then(() => {
      proxy.$modal.msgSuccess(data.id ? "修改成功" : "新增成功");
      open.value = false;
      tableRef.value?.getList();
    });
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal
    .confirm(`是否确认删除文件？`)
    .then(() => delModelDataFile(row.id))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      tableRef.value?.getList();
    })
    .catch(() => {});
}

/** 判断是否为图片 */
function isImage(suffix) {
  if (!suffix) return false;
  return ["jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"].includes(
    suffix.toLowerCase()
  );
}

/** 预览按钮操作 */
function handlePreview(row) {
  if (!row.filePath) {
    proxy.$modal.msgError("文件地址为空，无法预览");
    return;
  }

  if (isImage(row.fileSuffix)) {
    const url = row.filePath.startsWith("http")
      ? row.filePath
      : import.meta.env.VITE_APP_BASE_API + row.filePath;
    imageViewerList.value = [url];
    imageViewerVisible.value = true;
  } else {
    // 使用公开预览接口，拼接文件名以帮助 kkFileView 识别类型
    const fileName = row.filePath.substring(row.filePath.lastIndexOf("/") + 1);
    const previewUrl =
      "/model/modelDataFile/public/preview/" + row.id + "/" + fileName;
    filePreview(previewUrl);
  }
}

/** 关闭图片预览 */
function closeImageViewer() {
  imageViewerVisible.value = false;
  imageViewerList.value = [];
}

/** 下载按钮操作 */
function handleDownload(row) {
  if (!row.filePath) {
    proxy.$modal.msgError("文件地址为空，无法下载");
    return;
  }
  const name = row.fileName || "下载文件";
  const url = import.meta.env.VITE_APP_BASE_API + row.filePath;

  fetch(url)
    .then((res) => res.blob())
    .then((blob) => {
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(link.href);
    })
    .catch(() => {
      proxy.$modal.msgError("下载失败");
    });
}

onMounted(() => {
  getCategoryTree();
});
</script>

<style scoped lang="scss">
.el-main {
  overflow: hidden;
  padding: 0;
}

.char-count :deep(.el-input__count)::after {
  content: "个字符";
}
</style>
