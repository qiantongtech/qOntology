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
  <div class="app-container model-file-page" ref="app-container">
    <el-container>
      <DeptTree
        ref="deptTreeRef"
        :deptOptions="categoryOptions"
        v-model:leftWidth="leftWidth"
        :placeholder="'请输入分类名称'"
        @node-click="handleNodeClick"
      />
      <el-main>
        <GuideTip tip-id="basic/file/manager.list"/>
        <div class="pagecont-top" v-show="showSearch">
          <el-form
            :model="queryParams"
            ref="queryRef"
            :inline="true"
            v-show="showSearch"
            class="search-form btn-style"
            @submit.prevent
          >
            <el-form-item label="文件名称" prop="fileName">
              <el-input
                class="el-form-input-width"
                v-model="queryParams.fileName"
                placeholder="请输入文件名称"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                plain
                type="primary"
                @click="handleQuery"
                @mousedown="(e) => e.preventDefault()"
              >
                <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
              </el-button>
              <el-button
                @click="resetQuery"
                @mousedown="(e) => e.preventDefault()"
              >
                <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="content-panel" :class="{ 'has-search': showSearch }">
          <div class="toolbar-row">
            <div class="btn-style">
              <el-button
                type="primary"
                plain
                @click="handleAdd"
                v-hasPermi="['basic:file:manager:add']"
                @mousedown="(e) => e.preventDefault()"
              >
                <i class="iconfont-mini icon-xinzeng mr5"></i>新增
              </el-button>

            </div>
            <right-toolbar
              v-model:showSearch="showSearch"
              @queryTable="getList"
              :columns="columns"
            />
          </div>

          <div class="pagecont-bottom" v-loading="loading">
            <el-table
              :data="fileList"
              @selection-change="handleSelectionChange"
              stripe
              :default-sort="defaultSort"
              @sort-change="handleSortChange"
            >
              <el-table-column
                v-if="getColumnVisibility(1)"
                label="编号"
                align="center"
                prop="id"
                width="80"
                sortable="custom"
                :sort-orders="['descending', 'ascending']"
              />
              <el-table-column
                v-if="getColumnVisibility(2)"
                label="文件名称"
                align="left"
                prop="fileName"
                min-width="200"
                :show-overflow-tooltip="{ effect: 'light' }"
              >
                <template #default="scope">
                  <div class="file-name-cell">
                    <span class="file-name-text clickable" @click="handlePreview(scope.row)">{{ scope.row.fileName || "-" }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                v-if="getColumnVisibility(3)"
                label="文件描述"
                align="left"
                prop="description"
                min-width="220"
                :show-overflow-tooltip="{ effect: 'light' }"
              />
              <el-table-column v-if="getColumnVisibility(4)" label="分类" align="center" prop="categoryName" width="120" :show-overflow-tooltip="{ effect: 'light' }" />
              <el-table-column v-if="getColumnVisibility(5)" label="文件大小" align="center" prop="fileSize" width="120">
                <template #default="scope">{{ formatSize(scope.row.fileSize) }}</template>
              </el-table-column>
              <el-table-column v-if="getColumnVisibility(7)" label="创建人" align="center" prop="createBy" width="100" />
              <el-table-column
                v-if="getColumnVisibility(8)"
                label="创建时间"
                align="center"
                prop="createTime"
                width="160"
                sortable="custom"
                :sort-orders="['descending', 'ascending']"
              >
                <template #default="scope">
                  <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right" width="240">
                <template #default="scope">
                  <el-button link type="primary" icon="View" @click="handlePreview(scope.row)">预览</el-button>
                  <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)">下载</el-button>
                  <el-popover placement="bottom" :width="120" trigger="click" popper-class="more-popover">
                    <template #reference>
                      <el-button link type="primary" icon="ArrowDown">更多</el-button>
                    </template>
                    <div class="more-btn-group">
                      <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['basic:file:manager:edit']">修改</el-button>
                      <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['basic:file:manager:remove']">删除</el-button>
                    </div>
                  </el-popover>
                </template>
              </el-table-column>
              <template #empty>
                <div class="emptyBg">
                  <img src="@/assets/system/images/no_data/noData.png" alt="" />
                  <p>暂无记录</p>
                </div>
              </template>
            </el-table>

            <div class="pagefy" v-if="total > 0">
              <pagination
                :total="total"
                v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"
              />
            </div>
          </div>
        </div>
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
      <el-form ref="fileRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="数据分类" prop="categoryId">
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
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上传文件" prop="filePath">
              <FileUpload2
                v-model="form.filePath"
                :fileName="form.fileName"
                limit=1
                :fileType="['txt', 'pdf', 'html', 'xlsx', 'xls', 'docx', 'csv', 'md', 'mdx', 'htm', 'markdown']"
                @update:fileSize="(val) => (form.fileSize = val)"
                @update:fileExt="(val) => (form.fileSuffix = val)"
                @update:fileName="updateFormFileName"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="文件名称" prop="fileName">
              <el-input v-model="form.fileName" placeholder="请输入文件名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="文件描述" prop="description">
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
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
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
          </el-col>
        </el-row>
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
import { ArrowDown } from "@element-plus/icons-vue";
import DeptTree from "@/components/DeptTree";
import FileUpload2 from "@/components/FileUpload2";
import { formatSize } from "@/utils/index";
import { filePreview } from "@/utils/kkFileView";

const { proxy } = getCurrentInstance();
const fileList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const open = ref(false);
const title = ref("");
const total = ref(0);
const defaultSort = ref({ prop: "createTime", order: "descending" });
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const categoryOptions = ref([]);
const leftWidth = ref(280);

// 图片预览相关
const imageViewerVisible = ref(false);
const imageViewerList = ref([]);

// 列显隐信息
const columns = ref([
  { key: 1, label: "编号", visible: true },
  { key: 2, label: "文件名称", visible: true },
  { key: 3, label: "文件描述", visible: true },
  { key: 4, label: "数据分类", visible: true },
  { key: 5, label: "文件大小", visible: true },
  { key: 6, label: "备注", visible: true },
  { key: 7, label: "创建人", visible: true },
  { key: 8, label: "创建时间", visible: true },
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find((col) => col.key === key);
  return column ? column.visible : true;
};

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    fileName: undefined,
    categoryId: undefined,
    fileType: undefined,
    ids: undefined,
    orderByColumn: "createTime",
    isAsc: "desc",
  },
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

const { queryParams, form, rules } = toRefs(data);

/** 查询模型数据文件列表 */
function getList() {
  loading.value = true;
  listModelDataFile(queryParams.value).then((response) => {
    fileList.value = response.data?.rows || [];
    total.value = response.data?.total || 0;
    loading.value = false;
  });
}

/** 查询分类树结构 */
function getCategoryTree() {
  getFileTypes().then((response) => {
    categoryOptions.value = proxy.handleTree(response.data || [], "id", "parentId");
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
  queryParams.value.ids = categoryIds;
  handleQuery();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 排序触发事件 */
function handleSortChange(column) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order === "ascending" ? "asc" : "desc";
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.ids = undefined;
  proxy.$refs.deptTreeRef?.resetTree();
  handleQuery();
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
      getList();
    });
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const fileIds = row.id || ids.value;
  proxy.$modal
    .confirm(`是否确认删除文件？`)
    .then(() => delModelDataFile(fileIds))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      getList();
    })
    .catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 获取文件图标 */
function getFileIcon(fileName) {
  if (!fileName) return "example";
  const suffix = fileName
    .substring(fileName.lastIndexOf(".") + 1)
    .toLowerCase();
  if (["doc", "docx"].includes(suffix)) return "documentation";
  if (["xls", "xlsx"].includes(suffix)) return "excel";
  if (["pdf"].includes(suffix)) return "pdf";
  if (["zip", "rar"].includes(suffix)) return "zip";
  return "example";
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
    const previewUrl = "/model/modelDataFile/public/preview/" + row.id + "/" + fileName;
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
  getList();
  getCategoryTree();
});
</script>

<style scoped lang="scss">
.model-file-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 124px);

  :deep(.el-container) {
    height: 100%;
  }
}

.el-main {
  overflow: hidden;
  padding: 2px 0;
}

.pagecont-top {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 2px;
  padding: 16px 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 24px;

  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 0;
  }
  :deep(.el-form-item__label) {
    font-size: 14px;
    color: #333;
    padding-right: 8px;
  }
}

.content-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
  border-radius: 2px;
  padding: 12px 20px 0;

  &.has-search {
    margin-top: 15px;
  }
}

.toolbar-row {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 0 12px;
}

.pagecont-bottom {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
  background-color: transparent;
  border-radius: 0;
  box-shadow: none;
  margin-top: 0;
  padding: 0;
}

.pagefy {
  flex-shrink: 0;
  height: 52px;
  background: transparent;
  border-top: 1px solid #f0f0f0;
  line-height: 52px;
  margin: 0;
  padding: 0 18px 0 0;

  :deep(.pagination-container) {
    margin-top: 0;
  }
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .file-type-icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  .file-name-text {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    word-break: break-all;
    line-height: 1.5em;
    max-height: 3em;

    &.clickable {
      color: var(--el-color-primary);
      cursor: pointer;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.more-btn-group {
  display: flex;
  flex-direction: column;
  align-items: center;

  .el-button {
    margin-left: 0;
    width: 100%;
  }
}

:deep(.more-popover) {
  padding: 8px 0;
  min-width: 80px !important;
}
.char-count :deep(.el-input__count)::after {
  content: "个字符";
}

.mr5 {
  margin-right: 5px;
}
.mb15 {
  margin-bottom: 15px;
}
</style>
