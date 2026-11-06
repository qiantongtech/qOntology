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
    <div class="pagecont-top" v-show="showSearch">
      <el-form
        class="btn-style"
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        label-width="75px"
        v-show="showSearch"
        @submit.prevent
      >
        <el-form-item label="工作区id" prop="workspaceId">
          <el-input
            class="el-form-input-width"
            v-model="queryParams.workspaceId"
            placeholder="请输入工作区id"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="图谱id" prop="graphId">
          <el-input
            class="el-form-input-width"
            v-model="queryParams.graphId"
            placeholder="请输入图谱id"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="标签id" prop="tagId">
          <el-input
            class="el-form-input-width"
            v-model="queryParams.tagId"
            placeholder="请输入标签id"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="实体id" prop="entityId">
          <el-input
            class="el-form-input-width"
            v-model="queryParams.entityId"
            placeholder="请输入实体id"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker
            class="el-form-input-width"
            clearable
            v-model="queryParams.createTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择创建时间"
          >
          </el-date-picker>
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
          <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
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
              type="primary"
              plain
              @click="handleAdd"
              v-hasPermi="['kg:manage:tagentity:add']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['kg:manage:tagentity:edit']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xiugai--copy mr5"></i>修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['kg:manage:tagentity:remove']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              @click="handleImport"
              v-hasPermi="['kg:manage:tagentity:export']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-upload-cloud-line mr5"></i>导入
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              @click="handleExport"
              v-hasPermi="['kg:manage:tagentity:export']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-download-line mr5"></i>导出
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar
            v-model:showSearch="showSearch"
            @queryTable="getList"
            :columns="columns"
          ></right-toolbar>
        </div>
      </div>
      <el-table
        stripe
        v-loading="loading"
        :data="tagEntityList"
        @selection-change="handleSelectionChange"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          v-if="getColumnVisibility(0)"
          label="ID"
          align="center"
          prop="id"
        />
        <el-table-column
          v-if="getColumnVisibility(1)"
          label="工作区id"
          align="center"
          prop="workspaceId"
        >
          <template #default="scope">
            {{ scope.row.workspaceId || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(2)"
          label="图谱id"
          align="center"
          prop="graphId"
        >
          <template #default="scope">
            {{ scope.row.graphId || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(3)"
          label="标签id"
          align="center"
          prop="tagId"
        >
          <template #default="scope">
            {{ scope.row.tagId || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(4)"
          label="实体id"
          align="center"
          prop="entityId"
        >
          <template #default="scope">
            {{ scope.row.entityId || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(5)"
          label="描述"
          align="center"
          prop="description"
        >
          <template #default="scope">
            {{ scope.row.description || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(6)"
          label="备注"
          align="center"
          prop="remark"
        >
          <template #default="scope">
            {{ scope.row.remark || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(9)"
          label="创建人"
          align="center"
          prop="createBy"
        >
          <template #default="scope">
            {{ scope.row.createBy || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(11)"
          label="创建时间"
          align="center"
          prop="createTime"
          width="180"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
          width="240"
        >
          <template #default="scope">
            <el-button
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['kg:manage:tagentity:edit']"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['kg:manage:tagentity:remove']"
              >删除</el-button
            >
            <el-button
              link
              type="primary"
              icon="view"
              @click="handleDetail(scope.row)"
              v-hasPermi="['kg:manage:tagentity:query']"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="view"
              @click="routeTo('/kg/manage/tagEntityDetail', scope.row)"
              v-hasPermi="['kg:manage:tagentity:query']"
              >复杂详情</el-button
            >
          </template>
        </el-table-column>

        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="" />
            <p>暂无记录</p>
          </div>
        </template>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 添加或修改实体标签对话框 -->
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
        ref="tagEntityRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作区id" prop="workspaceId">
              <el-input
                v-model="form.workspaceId"
                placeholder="请输入工作区id"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图谱id" prop="graphId">
              <el-input v-model="form.graphId" placeholder="请输入图谱id" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标签id" prop="tagId">
              <el-input v-model="form.tagId" placeholder="请输入标签id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实体id" prop="entityId">
              <el-input v-model="form.entityId" placeholder="请输入实体id" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="请输入内容"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入内容"
              />
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- 实体标签详情对话框 -->
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
      <el-form ref="tagEntityRef" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作区id" prop="workspaceId">
              <div>
                {{ form.workspaceId }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图谱id" prop="graphId">
              <div>
                {{ form.graphId }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标签id" prop="tagId">
              <div>
                {{ form.tagId }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实体id" prop="entityId">
              <div>
                {{ form.entityId }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div>
                {{ form.description }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <div>
                {{ form.remark }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog
      :title="upload.title"
      v-model="upload.open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox
                v-model="upload.updateSupport"
              />是否更新已经存在的实体标签数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link
              type="primary"
              :underline="false"
              style="font-size: 12px; vertical-align: baseline"
              @click="importTemplate"
              >下载模板</el-link
            >
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="upload.open = false">取 消</el-button>
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="TagEntity">
import {
  listTagEntity,
  getTagEntity,
  delTagEntity,
  addTagEntity,
  updateTagEntity,
} from "@/api/app/manage/tagEntity";
import { getToken } from "@/utils/auth.js";

const { proxy } = getCurrentInstance();

const tagEntityList = ref([]);

// 列显隐信息
const columns = ref([
  { key: 1, label: "工作区id", visible: true },
  { key: 2, label: "图谱id", visible: true },
  { key: 3, label: "标签id", visible: true },
  { key: 4, label: "实体id", visible: true },
  { key: 5, label: "描述", visible: true },
  { key: 6, label: "备注", visible: true },
  { key: 9, label: "创建人", visible: true },
  { key: 11, label: "创建时间", visible: true },
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find((col) => col.key === key);
  // 如果没有找到对应列配置，默认显示
  if (!column) return true;
  // 如果找到对应列配置，根据visible属性来控制显示
  return column.visible;
};

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const defaultSort = ref({ prop: "createTime", order: "desc" });
const router = useRouter();

/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/kg/tagEntity/importData",
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    graphId: null,
    tagId: null,
    entityId: null,
    description: null,
    createTime: null,
  },
  rules: {
    graphId: [{ required: true, message: "图谱id不能为空", trigger: "blur" }],
    tagId: [{ required: true, message: "标签id不能为空", trigger: "blur" }],
    entityId: [{ required: true, message: "实体id不能为空", trigger: "blur" }],
    delFlag: [{ required: true, message: "删除标志不能为空", trigger: "blur" }],
    validFlag: [
      { required: true, message: "是否有效不能为空", trigger: "blur" },
    ],
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    updateTime: [
      { required: true, message: "更新时间不能为空", trigger: "blur" },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询实体标签列表 */
function getList() {
  loading.value = true;
  listTagEntity(queryParams.value).then((response) => {
    tagEntityList.value = response.data.rows;
    total.value = response.data.total;
    loading.value = false;
  });
}

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
    workspaceId: null,
    graphId: null,
    tagId: null,
    entityId: null,
    description: null,
    remark: null,
    delFlag: null,
    validFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
  };
  proxy.resetForm("tagEntityRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 排序触发事件 */
function handleSortChange(column, prop, order) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加实体标签";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getTagEntity(_id).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改实体标签";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getTagEntity(_id).then((response) => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "实体标签详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["tagEntityRef"].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        updateTagEntity(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          })
          .catch((error) => {});
      } else {
        addTagEntity(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
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
    .confirm('是否确认删除实体标签编号为"' + _ids + '"的数据项？')
    .then(function () {
      return delTagEntity(_ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    "kg/tagEntity/export",
    {
      ...queryParams.value,
    },
    `tagEntity_${new Date().getTime()}.xlsx`
  );
}

/** ---------------- 导入相关操作 -----------------**/
/** 导入按钮操作 */
function handleImport() {
  upload.title = "实体标签导入";
  upload.open = true;
}

/** 下载模板操作 */
function importTemplate() {
  proxy.download(
    "system/user/importTemplate",
    {},
    `tagEntity_template_${new Date().getTime()}.xlsx`
  );
}

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
      response.msg +
      "</div>",
    "导入结果",
    { dangerouslyUseHTMLString: true }
  );
  getList();
};
/** ---------------------------------**/

function routeTo(link, row) {
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return;
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: {
          id: row.id,
        },
      });
    }
  }
}

getList();
</script>
