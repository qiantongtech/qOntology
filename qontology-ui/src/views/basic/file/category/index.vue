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
    <GuideTip tip-id="basic/file/category.list"/>
    <div class="pagecont-top" v-show="showSearch">
      <el-form
        class="btn-style"
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        @submit.prevent
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            class="el-form-input-width"
            v-model="queryParams.name"
            clearable
            placeholder="请输入分类名称"
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
          <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="10" class="btn-style">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              @click="handleAdd"
              v-hasPermi="['basic:file:category:add']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              class="toggle-expand-all"
              type="primary"
              plain
              @click="toggleExpandAll"
            >
              <svg-icon v-if="isExpandAll" icon-class="toggle" />
              <svg-icon v-else icon-class="expand" />
              <span>{{ isExpandAll ? " 折叠" : " 展开" }}</span>
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="categoryList"
        row-key="id"
        height="60vh"
        stripe
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
      >
        <el-table-column
          label="分类名称"
          prop="name"
          align="left"
          min-width="250"
          :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
          label="显示顺序"
          prop="orderNum"
          width="150"
          align="center"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        />
        <el-table-column
          label="备注"
          prop="remark"
          align="left"
          min-width="220"
          :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
          label="创建人"
          prop="createBy"
          align="center"
          width="100"
        />
        <el-table-column
          label="创建时间"
          prop="createTime"
          width="160"
          align="center"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        >
          <template #default="scope">
            {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
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
              icon="Plus"
              @click="handleAdd(scope.row)"
              v-hasPermi="['basic:file:category:add']"
              >新增</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['basic:file:category:edit']"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['basic:file:category:remove']"
              >删除</el-button
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
    </div>

    <!-- 添加或修改模型数据分类对话框 -->
    <el-dialog
      v-model="open"
      :title="title"
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
        ref="categoryRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="父级分类" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="categoryOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                check-strictly
                clearable
                placeholder="请选择父级分类"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="分类名称" prop="name">
              <el-input
                class="char-count"
                v-model="form.name"
                placeholder="请输入分类名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="显示顺序" prop="orderNum">
              <el-input-number
                v-model="form.orderNum"
                :min="0"
                :max="9999"
                controls-position="right"
                style="width: 100%"
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
                :rows="3"
                maxlength="500"
                show-word-limit
                placeholder="请输入备注"
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
  </div>
</template>

<script setup name="BasicFileCategory">
import { getCurrentInstance, nextTick, reactive, ref, toRefs } from "vue";
import {
  addModelDataCategory,
  delModelDataCategory,
  getModelDataCategory,
  listModelDataCategory,
  updateModelDataCategory,
} from "@/api/basic/file/basicFileCategory.js";

const { proxy } = getCurrentInstance();

const categoryList = ref([]);
const categoryOptions = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const open = ref(false);
const title = ref("");
const isExpandAll = ref(true);
const refreshTable = ref(true);
const defaultSort = ref({ prop: "orderNum", order: "ascending" });

const data = reactive({
  form: {},
  queryParams: {
    name: undefined,
    orderByColumn: undefined,
    isAsc: undefined,
  },
  rules: {
    name: [{ required: true, message: "分类名称不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询分类列表 */
function getList() {
  loading.value = true;
  listModelDataCategory(queryParams.value).then((response) => {
    const rows = response.data?.rows || response.data || [];
    categoryList.value = proxy.handleTree(rows, "id", "parentId");
    categoryOptions.value = [
      { id: 0, name: "顶级分类", children: categoryList.value },
    ];
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 排序触发事件 */
function handleSortChange(column) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 重置表单 */
function reset() {
  form.value = {
    id: undefined,
    name: "",
    parentId: 0,
    orderNum: 0,
    remark: "",
  };
  proxy.resetForm("categoryRef");
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  form.value.parentId = row?.id || 0;
  title.value = "新增分类";
  open.value = true;
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  const response = await getModelDataCategory(row.id);
  form.value = response.data;
  open.value = true;
  title.value = "修改分类";
}

/** 提交表单 */
function submitForm() {
  proxy.$refs["categoryRef"].validate((valid) => {
    if (!valid) return;
    const action = form.value.id
      ? updateModelDataCategory
      : addModelDataCategory;
    action(form.value).then(() => {
      proxy.$modal.msgSuccess(form.value.id ? "修改成功" : "新增成功");
      open.value = false;
      getList();
    });
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal
    .confirm(`是否确认删除分类“${row.name}”？`)
    .then(() => delModelDataCategory(row.id))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功");
      getList();
    })
    .catch(() => {});
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

getList();
</script>

<style scoped lang="scss">
.toggle-expand-all {
  .svg-icon {
    font-size: 12px;
    margin-right: 6px;
  }
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
