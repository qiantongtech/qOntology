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
        v-show="showSearch"
        @submit.prevent
      >
        <div>
          <el-form-item label="名称" prop="name">
            <el-input
              class="el-form-input-width"
              v-model="queryParams.name"
              placeholder="请输入名称"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="类型" prop="category">
            <el-select
              v-model="queryParams.category"
              placeholder="请选择类型"
              style="width: 210px"
              clearable
            >
              <el-option
                v-for="item in app_tag_category"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="创建人" prop="createBy">
            <el-input
              class="el-form-input-width"
              v-model="queryParams.createBy"
              placeholder="请输入创建人"
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
            <!--            <el-button-->
            <!--                @click="toggleExpand"-->
            <!--                @mousedown="(e) => e.preventDefault()"-->
            <!--            >-->
            <!--              <i class="iconfont-mini" :class="isExpanded ? 'icon-a-zu22380' : 'icon-a-zu22379'"></i>-->
            <!--              {{ isExpanded ? '合并' : '展开' }}-->
            <!--            </el-button>-->
          </el-form-item>
        </div>
        <!--        <el-form-item label="模型" prop="modelId"  v-if="isExpanded" >-->
        <!--          <el-select-->
        <!--              v-model="queryParams.modelId"-->
        <!--              placeholder="请选择模型"-->
        <!--              style="width: 210px"-->
        <!--              clearable-->
        <!--          >-->
        <!--            <el-option-->
        <!--                v-for="item in modelList"-->
        <!--                :key="item.id"-->
        <!--                :label="item.name"-->
        <!--                :value="item.id"-->
        <!--            />-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="创建时间">-->
        <!--          <el-date-picker-->
        <!--              class="el-form-input-width"-->
        <!--              v-model="dateRange"-->
        <!--              value-format="YYYY-MM-DD"-->
        <!--              type="daterange"-->
        <!--              range-separator="-"-->
        <!--              start-placeholder="开始日期"-->
        <!--              end-placeholder="结束日期"-->
        <!--          ></el-date-picker>-->
        <!--        </el-form-item>-->
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
              v-hasPermi="['kg:manage:tag:add']"
              @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              icon="Delete"
              plain
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['kg:manage:tag:remove']"
              @mousedown="(e) => e.preventDefault()"
            >
              批量删除
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
        :data="tagList"
        @selection-change="handleSelectionChange"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          v-if="getColumnVisibility(0)"
          label="编号"
          align="center"
          prop="id"
          width="85"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        />
        <el-table-column
          v-if="getColumnVisibility(4)"
          label="名称"
          align="left"
          prop="name"
          width="200px"
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.name || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(5)"
          label="描述"
          align="left"
          prop="description"
          width="250px"
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.description || "-" }}
          </template>
        </el-table-column>
        <!--        <el-table-column-->
        <!--            v-if="getColumnVisibility(7)"-->
        <!--            label="模型"-->
        <!--            align="center"-->
        <!--            prop="modelId"-->
        <!--        >-->
        <!--          <template #default="scope">-->
        <!--            {{ getModelLabel(scope.row.modelId) || "-" }}-->
        <!--          </template>-->
        <!--        </el-table-column>-->
        <el-table-column
          v-if="getColumnVisibility(6)"
          label="类型"
          align="center"
          prop="category"
        >
          <template #default="scope">
            <dict-tag :options="app_tag_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <!-- <el-table-column
          v-if="getColumnVisibility(7)"
          label="状态"
          align="center"
          prop="status"
        >
          <template #default="scope">
            <dict-tag :options="kmc_know_valid" :value="scope.row.status" />
          </template>
        </el-table-column> -->
        <el-table-column
          v-if="getColumnVisibility(8)"
          label="备注"
          align="left"
          prop="remark"
          width="250px"
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.remark || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(11)"
          label="创建人"
          align="center"
          prop="createBy"
        >
          <template #default="scope">
            {{ scope.row.createBy || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(13)"
          label="创建时间"
          align="center"
          prop="createTime"
          width="180"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
        >
          <template #default="scope">
            <span>{{
              parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          v-if="getColumnVisibility(14)"
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
              v-hasPermi="['kg:manage:tag:edit']"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['kg:manage:tag:remove']"
              >删除</el-button
            >
            <el-button
              link
              type="primary"
              icon="view"
              @click="handleDetail(scope.row)"
              v-hasPermi="['kg:manage:tag:query']"
              >详情</el-button
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

    <!-- 新增或修改知识标签对话框 -->
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
        ref="tagRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <!--        <el-row :gutter="20">-->
        <!--          <el-col :span="24">-->
        <!--            <el-form-item label="模型" prop="modelId">-->
        <!--              <el-select-->
        <!--                  v-model="form.modelId"-->
        <!--                  placeholder="请选择模型"-->
        <!--                  clearable-->
        <!--              >-->
        <!--                <el-option-->
        <!--                    v-for="item in modelList"-->
        <!--                    :key="item.id"-->
        <!--                    :label="item.name"-->
        <!--                    :value="item.id"-->
        <!--                />-->
        <!--              </el-select>-->
        <!--            </el-form-item>-->
        <!--          </el-col>-->
        <!--        </el-row>-->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="类型" prop="category">
              <el-select
                v-model="form.category"
                placeholder="请选择类型"
                clearable
              >
                <el-option
                  v-for="item in app_tag_category"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="状态" prop="status" style="display: flex;align-items: center;">
              <el-radio-group v-model="form.status">
                <el-radio v-for="item in kmc_know_valid " :key="item.value" :label="item.value === 'true'">
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="请输入描述"
                maxlength="1024个字符"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入备注"
                maxlength="256个字符"
                show-word-limit
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

    <!-- 知识标签详情对话框 -->
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
      <el-form ref="tagRef" :model="form" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
        </el-row>

        <!--        <el-row :gutter="20">-->
        <!--          <el-col :span="24">-->
        <!--            <el-form-item label="模型" prop="modelId">-->
        <!--              <el-input-->
        <!--                  :value="getModelLabel(form.modelId)"-->
        <!--                  readonly-->
        <!--                  class="readonly-input"-->
        <!--              />-->
        <!--            </el-form-item>-->
        <!--          </el-col>-->
        <!--        </el-row>-->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="类型" prop="category">
              <el-input
                :value="getCategoryLabel(form.category)"
                readonly
                class="readonly-input"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- <el-col :span="24">
            <el-form-item label="状态" prop="status" style="display: flex;align-items: center;">
              <template #default="scope">
                <dict-tag :options="kmc_know_valid" :value="form.status" />
              </template>
            </el-form-item>
          </el-col> -->

          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                class="readonly-input"
                :value="form.description || '-'"
                type="textarea"
                maxlength="1024个字符"
                show-word-limit
                readonly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                class="readonly-input"
                :value="form.remark || '-'"
                type="textarea"
                maxlength="1024个字符"
                show-word-limit
                readonly
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="创建人" prop="createBy">
              <el-input
                :value="form.createBy || '-'"
                readonly
                class="readonly-input"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="创建时间" prop="createTime">
              <el-input
                :value="form.createTime || '-'"
                readonly
                class="readonly-input"
              />
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
  </div>
</template>

<script setup name="Tag">
import {
  listTag,
  getTag,
  delTag,
  addTag,
  updateTag,
} from "@/api/app/manage/tag";
// import { listModel } from '@/api/ext/extModel/model';
import { getToken } from "@/utils/auth.js";
const route = useRoute();
const { proxy } = getCurrentInstance();
const { kmc_know_valid, app_tag_category } = proxy.useDict(
  "kmc_know_valid",
  "app_tag_category"
);
const tagList = ref([]);
const modelList = ref([]);
// 获取路由参数中的图谱id
const graphId = ref(route.params.kgId || "");
const dateRange = ref([]);
const isExpanded = ref(false);
// 列显隐信息
const columns = ref([
  // { key: 1, label: "工作区id", visible: true },
  // { key: 2, label: "图谱id", visible: true },
  // { key: 3, label: "模型id", visible: true },
  { key: 4, label: "名称", visible: true },
  { key: 5, label: "描述", visible: true },
  { key: 6, label: "类型", visible: true },
  // { key: 7, label: "状态", visible: true },
  { key: 8, label: "备注", visible: true },
  { key: 11, label: "创建人", visible: true },
  { key: 13, label: "创建时间", visible: true },
  { key: 14, label: "操作", visible: true },
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
  url: import.meta.env.VITE_APP_BASE_API + "/kg/tag/importData",
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    graphId: graphId,
    modelId: null,
    name: null,
    description: null,
    category: null,
    status: null,
    createTime: null,
  },
  rules: {
    // graphId: [{ required: true, message: "图谱id不能为空", trigger: "blur" }],
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
    category: [{ required: true, message: "类型不能为空", trigger: "blur" }],
    status: [{ required: true, message: "状态不能为空", trigger: "change" }],
    // validFlag: [{ required: true, message: "是否有效不能为空", trigger: "blur" }],
    // delFlag: [{ required: true, message: "删除标志不能为空", trigger: "blur" }],
    // createTime: [{ required: true, message: "创建时间不能为空", trigger: "blur" }],
    // updateTime: [{ required: true, message: "更新时间不能为空", trigger: "blur" }]
    // modelId: [{ required: true, message: "模型不能为空", trigger: "blur" }]
  },
});

const { queryParams, form, rules } = toRefs(data);

// 映射模型名称
// const getModelLabel = (value) => {
//   // 如果数组不存在或 value 为空，直接返回默认值
//   if (!modelList || !value) return "-";
//
//   // 遍历数组，找到 value 匹配的对象
//   const item = modelList.value.find((item) => item.id === value);
//
//   // 找到则返回 label，否则返回默认值
//   return item ? item.name : "-";
// };

// 映射分类名称
const getCategoryLabel = (value) => {
  // 如果数组不存在或 value 为空，直接返回默认值
  if (!app_tag_category || !value) return "-";

  // 遍历数组，找到 value 匹配的对象
  const item = app_tag_category.value.find((item) => item.value === value);

  // 找到则返回 label，否则返回默认值
  return item ? item.label : "-";
};
/** 查询知识标签列表 */
function getList() {
  loading.value = true;
  listTag(proxy.addDateRange(queryParams.value, dateRange.value)).then(
    (response) => {
      tagList.value = response.data.rows;
      total.value = response.data.total;
      loading.value = false;
    }
  );
}

// 查询图谱模型
// function getModelList(){
//   const modelData = ref({
//     pageSize:-1,
//     graphId:graphId
//   })
//   listModel(modelData.value).then((response)=>{
//     modelList.value = response.data.rows
//   })
// }

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 切换展开/合并状态
// function toggleExpand() {
//   isExpanded.value = !isExpanded.value;
// }

// 表单重置
function reset() {
  form.value = {
    id: null,
    workspaceId: null,
    graphId: null,
    modelId: null,
    name: null,
    description: null,
    category: null,
    status: true,
    remark: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
  };
  proxy.resetForm("tagRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
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
  title.value = "新增知识标签";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getTag(_id).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改知识标签";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getTag(_id).then((response) => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "知识标签详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["tagRef"].validate((valid) => {
    form.value.graphId = graphId;
    if (valid) {
      if (form.value.id != null) {
        updateTag(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          })
          .catch((error) => {});
      } else {
        addTag(form.value)
          .then((response) => {
            proxy.$modal.msgSuccess("保存成功");
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
    .confirm('是否确认删除知识标签编号为"' + _ids + '"的数据项？')
    .then(function () {
      return delTag(_ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
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
// getModelList()
getList();
</script>

<style scoped>
:deep(.readonly-input) .el-input__wrapper {
  background-color: #fcfcfc !important;
}

:deep(.readonly-input) .el-textarea__inner {
  background-color: #fcfcfc !important;
}
</style>
