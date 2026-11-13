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
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <!-- 查询条件区域 -->
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['system:post:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="primary"
          plain
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:post:edit']"
        >
          <i class="iconfont-mini icon-a-xiugaixianxing mr5"></i>修改
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:post:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>删除
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['system:post:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            @click="handleUpdate(row)"
            v-hasPermi="['system:post:edit']"
          >
            <i class="iconfont-mini icon-a-xiugaixianxing"></i>修改
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row)"
            v-hasPermi="['system:post:remove']"
          >
            <i class="iconfont-mini icon-a-shanchuxianxing"></i>删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增或修改岗位对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="postRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="postName">
              <el-input v-model="form.postName" placeholder="请输入岗位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位编码" prop="postCode">
              <el-input v-model="form.postCode" placeholder="请输入编码名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位顺序" prop="postSort">
              <el-input-number
                class="post-sort-input"
                v-model="form.postSort"
                controls-position="right"
                :min="0"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                  >{{ dict.label }}</el-radio
                >
              </el-radio-group>
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
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Post">
import {
  listPost,
  addPost,
  delPost,
  getPost,
  updatePost
} from "@/api/system/system/post.js";

// 当前组件实例，用于字典、弹窗提示、日期参数处理、文件下载等全局方法
const { proxy } = getCurrentInstance();

// 字典：sys_normal_disable 用于岗位状态
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

/**
 * 表格实例
 *
 * getList()    刷新当前页
 * resetQuery() 回第一页并重新查询
 */
const tableRef = ref(null);

/**
 * 查询参数
 *
 * QtSearchBar 负责修改条件，QtTable 负责补充分页参数
 */
const tableParams = reactive({
  postCode: undefined,
  postName: undefined,
  status: undefined
});

/**
 * 查询栏配置，prop 与 tableParams 字段一一对应
 */
const searchStore = reactive({
  items: [
    {
      label: "岗位编码",
      prop: "postCode",
      component: {
        is: "input"
      }
    },
    {
      label: "岗位名称",
      prop: "postName",
      component: {
        is: "input"
      }
    },
    {
      label: "状态",
      prop: "status",
      component: {
        is: "select",
        options: sys_normal_disable
      }
    }
  ]
});

/**
 * QtTable 统一配置
 */
const tableStore = reactive({
  config: {
    // 启用表头排序，首次请求默认按创建时间倒序
    sort: true,
    table: {
      stripe: true,
      rowKey: "postId",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center"
    },
    {
      label: "编号",
      prop: "postId",
      align: "center",
      width: 80, minWidth: 80,
      sortable: true
    },
    {
      label: "岗位编码",
      prop: "postCode",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "岗位名称",
      prop: "postName",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "岗位排序",
      prop: "postSort",
      align: "center"
    },
    {
      label: "状态",
      prop: "status",
      align: "center",
      dict: "sys_normal_disable"
    },
    {
      label: "创建时间",
      prop: "createTime",
      width: 180,
      minWidth: 180,
      align: "center",
      date: true,
      sortable: true
    },
    {
      label: "操作",
      fixed: "right",
      width: 240,
      minWidth: 240,
      align: "center",
      slot: "handle"
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const {
    dateRange,
    ...requestParams
  } = params;

  const response = await listPost(
    proxy.addDateRange(requestParams, dateRange)
  );

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，新增/修改/删除成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

const open = ref(false);
const ids = ref([]);

// true 表示未选择或选择了多条，不能执行单条修改
const single = ref(true);

// true 表示没有选择数据，不能执行批量删除
const multiple = ref(true);
const title = ref("");

const data = reactive({
  form: {},
  rules: {
    postName: [{ required: true, message: "岗位名称不能为空", trigger: "blur" }],
    postCode: [{ required: true, message: "岗位编码不能为空", trigger: "blur" }],
    postSort: [{ required: true, message: "岗位顺序不能为空", trigger: "blur" }]
  }
});

const { form, rules } = toRefs(data);

/**
 * 表格多选事件
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.postId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/**
 * 取消按钮
 */
function cancel() {
  open.value = false;
  reset();
}

/**
 * 表单重置
 */
function reset() {
  form.value = {
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("postRef");
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增岗位";
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；顶部按钮触发时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const postId = row?.postId ?? ids.value[0];
  getPost(postId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改岗位";
  });
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["postRef"].validate((valid) => {
    if (valid) {
      if (form.value.postId != undefined) {
        updatePost(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addPost(form.value).then((response) => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/**
 * 删除按钮操作
 *
 * @param {Object} [row] 行数据；无值时为批量删除
 */
function handleDelete(row) {
  const postIds = row?.postId ?? ids.value;
  proxy.$modal
    .confirm("是否确认删除编号为\"" + postIds + "\"的数据项？")
    .then(function () {
      return delPost(postIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 导出按钮操作，使用当前查询条件
 */
function handleExport() {
  proxy.download(
    "system/post/export",
    {
      ...tableParams
    },
    `post_${new Date().getTime()}.xlsx`
  );
}
</script>

<style scoped lang="scss">
// 岗位顺序输入框占满表单项宽度
.post-sort-input {
  width: 100%;
}
</style>
