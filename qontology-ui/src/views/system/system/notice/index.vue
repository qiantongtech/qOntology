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
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['system:notice:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <!-- <el-button
          type="primary"
          plain
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:notice:edit']"
        >
          <i class="iconfont-mini icon-a-xiugaixianxing mr5"></i>修改
        </el-button> -->
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:notice:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #status="{ row }">
          <el-switch
            v-model="row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(row)"
          />
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            @click="handleUpdate(row)"
            v-hasPermi="['system:notice:edit']"
          >
            <i class="iconfont-mini icon-a-xiugaixianxing"></i>修改
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row)"
            v-hasPermi="['system:notice:remove']"
            icon="Delete"
          >
            删除</el-button
          >
          <el-button
            link
            type="primary"
            @click="handleView(row)"
            v-hasPermi="['system:notice:query']"
            icon="view"
          >
            详情</el-button
          >
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改公告对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="noticeRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <qt-form-item label="公告标题" prop="noticeTitle">
              <el-input
                v-model="form.noticeTitle"
                placeholder="请输入公告标题"
              />
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option
                  v-for="dict in sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_notice_status"
                  :key="dict.value"
                  :value="dict.value"
                  >{{ dict.label }}</el-radio
                >
              </el-radio-group>
            </qt-form-item>
          </el-col>
          <el-col :span="24">
            <qt-form-item label="内容">
              <editor v-model="form.noticeContent" :min-height="192" />
            </qt-form-item>
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

<script setup name="Notice">
import {
  listNotice,
  getNotice,
  delNotice,
  addNotice,
  updateNotice
} from "@/api/system/system/notice.js";

// 当前组件实例，用于字典、弹窗提示、日期参数处理等全局方法
const { proxy } = getCurrentInstance();

// 字典：sys_notice_status 公告状态、sys_notice_type 公告类型
const { sys_notice_status, sys_notice_type } = proxy.useDict(
  "sys_notice_status",
  "sys_notice_type"
);

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
  noticeTitle: undefined,
  createBy: undefined,
  noticeType: undefined
});

/**
 * 查询栏配置，prop 与 tableParams 字段一一对应
 */
const searchStore = reactive({
  items: [
    {
      label: "公告标题",
      prop: "noticeTitle",
      component: {
        is: "input"
      }
    },
    {
      label: "操作人员",
      prop: "createBy",
      component: {
        is: "input"
      }
    },
    {
      label: "公告类型",
      prop: "noticeType",
      component: {
        is: "select",
        options: sys_notice_type
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
      rowKey: "noticeId",
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
      prop: "noticeId",
      width: 100,
      minWidth: 100,
      align: "center",
      sortable: true,
      sortOrders: ["descending", "ascending"]
    },
    {
      label: "公告标题",
      prop: "noticeTitle",
      width: 250,
      minWidth: 250,
      align: "left",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "公告类型",
      prop: "noticeType",
      align: "center",
      dict: "sys_notice_type"
    },
    { label: "状态", prop: "status", slot: "status", width: 80, minWidth: 80, align: "center" },
    {
      label: "创建人",
      prop: "createBy",
      align: "center",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "创建时间",
      prop: "createTime",
      width: 180,
      minWidth: 180,
      align: "center",
      date: true,
      sortable: true,
      sortOrders: ["descending", "ascending"]
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
 * @param {Object} params 查询条件 + pageNum + pageSize + 排序参数
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const {
    dateRange,
    ...requestParams
  } = params;

  const response = await listNotice(
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
    noticeTitle: [
      { required: true, message: "公告标题不能为空", trigger: "blur" }
    ],
    noticeType: [
      { required: true, message: "公告类型不能为空", trigger: "change" }
    ]
  }
});

const { form, rules } = toRefs(data);

/**
 * 表格多选事件
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.noticeId);
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
    noticeId: undefined,
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: "0"
  };
  proxy.resetForm("noticeRef");
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增公告";
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；顶部按钮触发时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const noticeId = row?.noticeId ?? ids.value[0];
  getNotice(noticeId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改公告";
  });
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["noticeRef"].validate((valid) => {
    if (valid) {
      if (form.value.noticeId != undefined) {
        updateNotice(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addNotice(form.value).then((response) => {
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
  const noticeIds = row?.noticeId ?? ids.value;
  proxy.$modal
    .confirm("是否确认删除公告编号为\"" + noticeIds + "\"的数据项？")
    .then(function () {
      return delNotice(noticeIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 公告状态修改
 *
 * @param {Object} row 行数据
 */
function handleStatusChange(row) {
  let text = row.status === "0" ? "正常" : "关闭";
  proxy.$modal
    .confirm("确认要\"" + text + "\"\"" + row.noticeTitle + "\"公告吗?")
    .then(function () {
      return updateNotice(row);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === "0" ? "1" : "0";
    });
}

/**
 * 详情按钮操作，跳转公告详情页
 *
 * @param {Object} row 行数据
 */
function handleView(row) {
  proxy.$router.push({
    path: "/system/notice/detail",
    query: { id: row.noticeId }
  });
}
</script>
