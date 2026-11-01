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
    <!-- 提示独占整行，分类树与列表统一从提示下方开始布局。 -->
    <div class="file-guide-row">
      <GuideTip tip-id="basic/file/manager.list" />
    </div>
    <el-container>
      <DeptTree
        ref="deptTreeRef"
        :dept-options="categoryOptions"
        :default-expand-level="2"
        v-model:leftWidth="leftWidth"
        placeholder="请输入分类名称"
        @node-click="handleNodeClick"
      />
      <el-main>
        <!-- 公共布局统一管理搜索、刷新、列显隐及分页，左侧分类树继续负责分类筛选。 -->
        <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
          <template #search>
            <qt-search-bar
              :items="searchStore.items"
              :params="tableStore.params"
              :table-ref="tableRef"
              @query="applyFilters"
              @reset="resetFilters"
            />
          </template>
          <template #actions-data>
            <el-button
              v-hasPermi="['basic:file:manager:add']"
              type="primary"
              plain
              @click="handleAdd"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </template>
          <qt-table v-bind="tableStore" ref="tableRef">
            <template #text="{ row, column_data }">{{
              row[column_data.prop]
            }}</template>
            <!-- 所属类目样式：分类左对齐显示为标签，空值保持占位，长名称省略。 -->
            <template #categoryName="{ row }">
              <el-tag
                v-if="row.categoryName !== '-'"
                class="file-category-tag"
                type="primary"
                :title="row.categoryName"
                >{{ row.categoryName }}
              </el-tag>
              <span v-else>—</span>
            </template>
            <template #fileSize="{ row }">{{
              row.fileSize === "-" ? "-" : formatSize(row.fileSize)
            }}</template>
            <!-- 沿用责任人双行样式，创建人电话通过用户接口关联，未维护时只显示姓名。 -->
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
            <template #createTime="{ row }">
              {{
                row.createTime === "-"
                  ? "-"
                  : parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
              }}
            </template>
            <template #handle="{ row }">
              <el-button
                v-hasPermi="['basic:file:manager:query']"
                link
                type="primary"
                icon="view"
                @click="handleDetail(row)"
                >详情
              </el-button>
              <el-button
                v-hasPermi="['basic:file:manager:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(row)"
                >修改
              </el-button>
              <el-popover
                placement="bottom"
                :width="120"
                trigger="click"
                popper-class="table-action-popover handle-popover"
              >
                <template #reference>
                  <el-button link type="primary" icon="ArrowDown"
                    >更多</el-button
                  >
                </template>
                <div class="file-action-list">
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
                  <el-button
                    v-hasPermi="['basic:file:manager:remove']"
                    link
                    type="danger"
                    icon="Delete"
                    @click="handleDelete(row)"
                    >删除
                  </el-button>
                </div>
              </el-popover>
            </template>
          </qt-table>
        </qt-wrap>
      </el-main>
    </el-container>
    <FileFormDialog
      ref="fileDialogRef"
      :category-options="categoryOptions"
      @success="refreshData"
    />
    <el-image-viewer
      v-if="imageViewerVisible"
      :url-list="imageViewerList"
      @close="closeImageViewer"
    />
  </div>
</template>

<script setup name="BasicFile">
import { getCurrentInstance, reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import csvIcon from "@/assets/images/file-types/CSV.svg";
import docxIcon from "@/assets/images/file-types/DOCX.svg";
import htmlIcon from "@/assets/images/file-types/HTML.svg";
import mdIcon from "@/assets/images/file-types/MD.svg";
import pdfIcon from "@/assets/images/file-types/PDF.svg";
import ttlIcon from "@/assets/images/file-types/TTL.svg";
import txtIcon from "@/assets/images/file-types/TXT.svg";
import xlsxIcon from "@/assets/images/file-types/XLSX.svg";
import {
  delModelDataFile,
  listModelDataFile,
  getFileTypes,
} from "@/api/basic/file/basicFile.js";
import DeptTree from "@/components/DeptTree";
import FileFormDialog from "./components/fileFormDialog.vue";
import { formatSize } from "@/utils/index";
import { filePreview } from "@/utils/kkFileView";

// 文件图标统一使用补充的 SVG；文件后缀作为筛选值，与接口 fileSuffix 参数一致。

const fileTypeOptions = [
  "TXT",
  "PDF",
  "CSV",
  "DOCX",
  "XLSX",
  "HTML",
  "MD",
  "TTL",
  "XLS",
  "HTM",
  "MDX",
  "MARKDOWN",
].map((type) => ({
  label: type,
  value: type.toLowerCase(),
}));
const fileIcons = {
  csv: csvIcon,
  docx: docxIcon,
  doc: docxIcon,
  html: htmlIcon,
  htm: htmlIcon,
  md: mdIcon,
  mdx: mdIcon,
  markdown: mdIcon,
  pdf: pdfIcon,
  ttl: ttlIcon,
  txt: txtIcon,
  xlsx: xlsxIcon,
  xls: xlsxIcon,
};

/** 优先使用文件后缀；缺少后缀时从名称推导，未知格式使用通用文本图标。 */
function getFileIcon(file) {
  const suffix = (file.fileSuffix || file.fileName?.split(".").pop() || "")
    .replace(/^\./, "")
    .toLowerCase();
  return fileIcons[suffix] || txtIcon;
}

import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const router = useRouter();
const tableRef = ref();
const { creatorPhones } = useCreatorPhones();

const deptTreeRef = ref();
const fileDialogRef = ref();
const categoryOptions = ref([]);
const leftWidth = ref(280);
const selectedCategoryIds = ref([]);
const imageViewerVisible = ref(false);
const imageViewerList = ref([]);
let appliedFileName = "";
let appliedFileSuffix = "";
const defaultSort = { prop: "createTime", order: "descending" };

// 保留既有业务列及文件操作，由公共表格统一提供分页和服务端排序。
const tableStore = reactive({
  config: {
    sort: true,
    table: { rowKey: "id", stripe: true, height: "60vh", defaultSort },
  },
  columns: [
    { label: "编号", prop: "id", slot: "text", width: 80, sortable: true },
    // 与任务信息列使用同一 info 配置：图标及名称在首行，描述在下方，名称进入详情。
    {
      label: "文件名称",
      prop: "fileName",
      minWidth: 390,
      align: "left",
      info: {
        icon: getFileIcon,
        title: "fileName",
        desc: "description",
        click: handleDetail,
      },
    },
    {
      label: "文件分类",
      prop: "categoryName",
      slot: "categoryName",
      width: 120,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "文件大小", prop: "fileSize", slot: "fileSize", width: 110 },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 130,
      align: "left",
      headerAlign: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "创建时间",
      prop: "createTime",
      slot: "createTime",
      width: 160,
      sortable: true,
    },
    { label: "操作", slot: "handle", width: 210, fixed: "right" },
  ],
  params: { fileName: "", fileSuffix: "" },
  func: listModelDataFile,
  events: { formatParams: formatListParams, formatData: formatListData },
});
const searchStore = reactive({
  items: [
    { label: "文件名称", prop: "fileName", component: { is: "input" } },
    // 按实际后缀筛选，避免 fileType 的 OFFICE/OTHER 大类无法区分 PDF、CSV 等格式。
    {
      label: "文件类型",
      prop: "fileSuffix",
      component: { is: "select", options: fileTypeOptions },
    },
  ],
});

/** 提交名称筛选，排序、翻页和刷新沿用已经查询的名称。 */
function applyFilters() {
  appliedFileName = (tableStore.params.fileName || "").trim();
  appliedFileSuffix = tableStore.params.fileSuffix || "";
}

/** 同时清空分类筛选和名称条件，查询刷新由公共搜索栏统一触发。 */
function resetFilters() {
  appliedFileName = "";
  appliedFileSuffix = "";
  selectedCategoryIds.value = [];
  deptTreeRef.value?.resetTree();
}

/** 合并分类及名称条件，并将公共表格排序转换为原接口使用的 asc/desc。 */
function formatListParams(params) {
  return {
    ...params,
    fileName: appliedFileName,
    fileSuffix: appliedFileSuffix || undefined,
    categoryIds: selectedCategoryIds.value.length
      ? selectedCategoryIds.value
      : undefined,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc:
      params.isAsc === "ascending" || params.isAsc === "asc" ? "asc" : "desc",
  };
}

/** 规范展示字段的空值，保留零字节文件的真实大小及下载所需的原始路径。 */
function formatListData(rows) {
  return rows.map((row) => {
    const record = { ...row };
    [
      "id",
      "fileName",
      "description",
      "categoryName",
      "fileSize",
      "createBy",
      "createTime",
    ].forEach((field) => {
      if (record[field] == null || record[field] === "") record[field] = "-";
    });
    return record;
  });
}

/** 刷新文件列表，保留当前筛选条件和分页状态。 */
function getList() {
  tableRef.value?.getList();
}

/** 加载完整分类树，用于左侧筛选及独立表单中的分类选择。 */
async function getCategoryTree() {
  const response = await getFileTypes();
  categoryOptions.value = proxy.handleTree(
    response.data || [],
    "id",
    "parentId"
  );
}

/** 文件增删改后同时刷新列表和分类统计，避免树上的文件数滞后。 */
function refreshData() {
  getList();
  getCategoryTree();
}

/** 选中分类时递归收集自身和子分类编号，切换分类后从第一页查询。 */
function handleNodeClick(node) {
  const categoryIds = [];
  const collectIds = (item) => {
    if (item.id != null) categoryIds.push(item.id);
    item.children?.forEach(collectIds);
  };
  collectIds(node);
  selectedCategoryIds.value = categoryIds;
  tableRef.value?.resetQuery();
}

/** 打开独立的新增文件弹窗。 */
function handleAdd() {
  fileDialogRef.value.openDialog();
}

/** 打开独立的修改弹窗，由组件请求最新文件信息。 */
function handleUpdate(row) {
  fileDialogRef.value.openDialog({ id: row.id });
}

/** 详情使用独立页面承载元数据，路由参数支持从其他引用入口直接访问。 */
function handleDetail(row) {
  router.push({ name: "BasicFileDetail", params: { fileId: row.id } });
}

/** 删除确认后刷新文件列表及分类统计。 */
function handleDelete(row) {
  proxy.$modal
    .confirm("是否确认删除文件？")
    .then(() => delModelDataFile(row.id))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      refreshData();
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

// 文件列表由公共表格首次挂载时自动加载，此处只初始化分类树。
onMounted(getCategoryTree);
</script>

<style scoped lang="scss">
.model-file-page {
  display: flex;
  flex-direction: column;

  > .file-guide-row {
    width: 100%;
    flex: 0 0 auto;
  }

  > .el-container {
    width: 100%;
    min-width: 0;
  }

  // 图标与分类节点间距仅作用于文件管理页。
  :deep(.dept-tree .el-tree-node__content) {
    height: 26px !important;
  }

  :deep(.dept-tree .custom-tree-node) {
    padding-left: 0;
  }

  :deep(.dept-tree .treelabel) {
    margin-left: 0;
  }

  :deep(.dept-tree .iconimg),
  :deep(.dept-tree .zjiconimg) {
    width: 18px;
    height: 18px;
    font-size: 18px;
    flex-shrink: 0;
    margin: 0 6px 0 0;
  }
}

// 复用任务信息列的布局尺寸；样式限定于本页，避免影响其他公共表格。
:deep(.qt-table-info) {
  display: flex;
  flex-direction: column;
  width: 100%;
  text-align: left;

  .qt-table-info__main {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;
    min-width: 0;
  }

  .info-icon {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
  }

  .info-title {
    font-size: 14px;
    line-height: 22px;
    font-weight: 500;
    flex: 0 1 auto;
    min-width: 0;
    justify-content: flex-start;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .info-desc {
    color: #999;
    font-size: 12px;
    line-height: 18px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    white-space: normal !important;
  }
}

.el-main {
  min-width: 0;
  padding: 2px 0;
}
</style>
