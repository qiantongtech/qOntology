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
    <el-container>
      <!-- 左侧部门筛选树；节点点击后把 deptId 写入查询参数并从第一页刷新列表 -->
      <DeptTree
        ref="deptTreeRef"
        :dept-options="treeDeptOptions"
        :left-width="leftWidth"
        placeholder="请输入部门名称"
        :default-expand="true"
        @node-click="handleNodeClick"
      />

      <el-main>
        <div>
          <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
            <template #search>
              <qt-search-bar
                :items="searchStore.items"
                :params="tableStore.params"
                :table-ref="tableRef"
                @reset="handleResetQuery"
              />
            </template>

            <template #actions-data>
              <el-button
                type="primary"
                plain
                @click="handleAdd"
                v-hasPermi="['system:user:add']"
              >
                <i class="iconfont-mini icon-xinzeng mr5"></i>新增
              </el-button>
              <el-button
                type="primary"
                plain
                :disabled="single"
                @click="handleUpdate"
                v-hasPermi="['system:user:edit']"
              >
                <i class="iconfont-mini icon-a-xiugaixianxing mr5"></i>修改
              </el-button>
              <el-button
                type="danger"
                plain
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['system:user:remove']"
              >
                <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>删除
              </el-button>
              <el-button
                type="info"
                plain
                @click="handleImport"
                v-hasPermi="['system:user:import']"
              >
                <i class="iconfont-mini icon-daoru mr5"></i>导入
              </el-button>
              <el-button
                type="warning"
                plain
                @click="handleExport"
                v-hasPermi="['system:user:export']"
              >
                <i class="iconfont-mini icon-daochu mr5"></i>导出
              </el-button>
            </template>

            <qt-table v-bind="tableStore" ref="tableRef">
              <!-- 部门名称为嵌套字段，QtTable 的 prop 不支持点号路径，改用插槽渲染 -->
              <template #deptName="{ row }">
                {{ row.dept?.deptName || "-" }}
              </template>
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
                  v-hasPermi="['system:user:edit']"
                  v-if="row.userId !== 1"
                >
                  <i class="iconfont-mini icon-a-xiugaixianxing"></i>修改
                </el-button>
                <el-button
                  link
                  type="danger"
                  @click="handleDelete(row)"
                  v-hasPermi="['system:user:remove']"
                  v-if="row.userId !== 1"
                >
                  <i class="iconfont-mini icon-a-shanchuxianxing"></i>删除
                </el-button>
                <el-popover
                  placement="bottom"
                  :width="150"
                  trigger="click"
                  v-if="row.userId !== 1"
                >
                  <template #reference>
                    <el-button link type="primary" icon="ArrowDown">更多</el-button>
                  </template>
                  <div class="task-action-list">
                    <el-button
                      link
                      type="primary"
                      @click="handleResetPwd(row)"
                      v-hasPermi="['system:user:resetPwd']"
                    >
                      <i class="iconfont-mini icon-a-mimaxianxing"></i>重置密码
                    </el-button>
                    <el-button
                      link
                      type="primary"
                      @click="handleAuthRole(row)"
                      v-hasPermi="['system:user:edit']"
                    >
                      <i class="iconfont-mini icon-a-yonghuzhanghaoxianxing"></i>分配角色
                    </el-button>
                  </div>
                </el-popover>
              </template>
            </qt-table>
          </qt-wrap>
        </div>
      </el-main>
    </el-container>

    <!-- 新增或修改用户配置对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="58vh"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="userRef"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input
                v-model="form.nickName"
                placeholder="请输入用户昵称"
                maxlength="30"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择归属部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input
                v-model="form.phonenumber"
                placeholder="请输入手机号码"
                maxlength="11"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="form.email"
                placeholder="请输入邮箱"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              v-if="form.userId == undefined"
              label="用户名称"
              prop="userName"
            >
              <el-input
                v-model="form.userName"
                placeholder="请输入用户名称"
                maxlength="30"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              v-if="form.userId == undefined"
              label="用户密码"
              prop="password"
            >
              <el-input
                v-model="form.password"
                placeholder="请输入用户密码"
                type="password"
                maxlength="20"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option
                  v-for="dict in sys_user_sex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select
                v-model="form.postIds"
                multiple
                placeholder="请选择"
                class="selectlist"
              >
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == 1"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select
                v-model="form.roleIds"
                multiple
                placeholder="请选择"
                class="selectlist"
              >
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == 1"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入内容"
              ></el-input>
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
        <el-icon class="el-icon--upload">
          <upload-filled />
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />
              是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link
              type="primary"
              :underline="false"
              class="import-template-link"
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

<script setup name="User">
import { getToken } from "@/utils/auth.js";
import DeptTree from "@/components/DeptTree";
import {
  changeUserStatus,
  listUser,
  resetUserPwd,
  delUser,
  getUser,
  updateUser,
  addUser,
  deptTreeSelect
} from "@/api/system/system/user.js";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_user_sex } = proxy.useDict(
  "sys_normal_disable",
  "sys_user_sex"
);

/** 左侧部门树组件实例 */
const deptTreeRef = ref(null);

/**
 * 表格实例
 *
 * getList()    刷新当前页
 * resetQuery() 回第一页并重新查询
 */
const tableRef = ref(null);

const open = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref("");
const deptOptions = ref(undefined);   // 用于表单的 el-tree-select
const treeDeptOptions = ref(undefined);   // 用于 DeptTree 组件
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);
const leftWidth = ref(300);

/**
 * 查询参数
 *
 * deptId 由左侧部门树点击写入；dateRange 为日期范围临时字段，请求前经 addDateRange 转换
 */
const tableParams = reactive({
  userName: undefined,
  phonenumber: undefined,
  status: undefined,
  deptId: undefined,
  dateRange: []
});

/** 查询栏配置 */
const searchStore = reactive({
  items: [
    {
      label: "用户名称",
      prop: "userName",
      component: { is: "input", clearable: true }
    },
    {
      label: "手机号码",
      prop: "phonenumber",
      component: { is: "input", clearable: true }
    },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: sys_normal_disable, clearable: true }
    },
    {
      label: "创建时间",
      prop: "dateRange",
      component: {
        is: "date-picker",
        type: "daterange",
        valueFormat: "YYYY-MM-DD",
        rangeSeparator: "-",
        startPlaceholder: "开始日期",
        endPlaceholder: "结束日期",
        clearable: true
      }
    }
  ]
});

/** QtTable 统一配置 */
const tableStore = reactive({
  config: {
    // 启用表头排序，首次请求默认按创建时间倒序
    sort: true,
    table: {
      stripe: true,
      rowKey: "userId",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: "selection", width: 50, minWidth: 50, align: "center" },
    { label: "编号", prop: "userId", width: 80, minWidth: 80, align: "center", sortable: true },
    {
      label: "用户名称",
      prop: "userName",
      align: "center",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "用户昵称",
      prop: "nickName",
      align: "center",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "部门",
      prop: "dept",
      slot: "deptName",
      minWidth: 120,
      align: "center",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "手机号码",
      prop: "phonenumber",
      width: 120,
      minWidth: 120,
      align: "center"
    },
    { label: "状态", prop: "status", slot: "status", width: 80, minWidth: 80, align: "center" },
    {
      label: "创建时间",
      prop: "createTime",
      width: 160,
      minWidth: 160,
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
  url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData"
});

const data = reactive({
  form: {},
  rules: {
    userName: [
      { required: true, message: "用户名称不能为空", trigger: "blur" },
      {
        min: 2,
        max: 20,
        message: "用户名称长度必须介于 2 和 20 之间",
        trigger: "blur"
      }
    ],
    nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
    password: [
      { required: true, message: "用户密码不能为空", trigger: "blur" },
      { min: 8, max: 20, message: "用户密码长度必须介于 8 和 20 之间", trigger: "blur" },
      {
        pattern: /^[^<>"'|\\]+$/,
        message: "不能包含非法字符：< > \" ' \\\ |",
        trigger: "blur"
      },
      {
        validator: (rule, value, callback) => {
          // 验证密码强度
          const strengthRegex = {
            minLength: /^.{8,}$/,
            upperCase: /[A-Z]/,
            lowerCase: /[a-z]/,
            number: /\d/,
            specialChar: /[!@#$%^&*(),.?":{}|<>]/
          };

          if (!strengthRegex.minLength.test(value)) {
            callback(new Error("密码长度必须至少 8 个字符"));
          } else if (!strengthRegex.upperCase.test(value)) {
            callback(new Error("密码必须包含至少一个大写字母"));
          } else if (!strengthRegex.lowerCase.test(value)) {
            callback(new Error("密码必须包含至少一个小写字母"));
          } else if (!strengthRegex.number.test(value)) {
            callback(new Error("密码必须包含至少一个数字"));
          } else if (!strengthRegex.specialChar.test(value)) {
            callback(new Error("密码必须包含至少一个特殊字符"));
          } else {
            callback();
          }
        },
        trigger: "blur"
      }
    ],
    email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
    phonenumber: [
      { required: true, message: "用户昵称不能为空", trigger: "blur" },
      {
        pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
        message: "请输入正确的手机号码",
        trigger: "blur"
      }
    ]
  }
});

const { form, rules } = toRefs(data);

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const { dateRange, ...requestParams } = params;

  // addDateRange 会转成后端参数，例如 params.beginTime、params.endTime
  const response = await listUser(proxy.addDateRange(requestParams, dateRange));

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，新增/修改/删除/重置密码成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 查询部门下拉树结构
 */
function getDeptTree() {
  deptTreeSelect().then((response) => {
    // 保存原始数据用于表单的 el-tree-select（使用 label 字段）
    deptOptions.value = response.data;

    // 将 label 字段转换为 name 字段，适配 DeptTree 组件
    const transformData = (items) => {
      return items.map((item) => ({
        ...item,
        name: item.label,
        children: item.children ? transformData(item.children) : []
      }));
    };
    treeDeptOptions.value = transformData(response.data);
  });
}

/**
 * 部门树节点单击事件：写入 deptId 后回第一页重新查询
 * @param {Object} node 被点击的树节点
 */
function handleNodeClick(node) {
  tableParams.deptId = node.id;
  tableRef.value?.resetQuery();
}

/**
 * 搜索栏重置：清空部门筛选并取消树节点高亮，请求由 qt-search-bar 统一触发
 */
function handleResetQuery() {
  tableParams.deptId = undefined;
  deptTreeRef.value?.resetTree();
}

/**
 * 删除按钮操作
 * @param {Object} [row] 行数据；无值时为批量删除
 */
function handleDelete(row) {
  const userIds = row.userId || ids.value;
  proxy.$modal
    .confirm("是否确认删除用户编号为\"" + userIds + "\"的数据项？")
    .then(function () {
      return delUser(userIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 导出按钮操作：沿用当前查询条件
 */
function handleExport() {
  const { dateRange, ...requestParams } = tableParams;
  proxy.download(
    "system/user/export",
    proxy.addDateRange(requestParams, dateRange),
    `user_${new Date().getTime()}.xlsx`
  );
}

/**
 * 用户状态修改
 * @param {Object} row 行数据
 */
function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用";
  proxy.$modal
    .confirm("确认要\"" + text + "\"\"" + row.userName + "\"用户吗?")
    .then(function () {
      return changeUserStatus(row.userId, row.status);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === "0" ? "1" : "0";
    });
}

/**
 * 跳转角色分配
 * @param {Object} row 行数据
 */
function handleAuthRole(row) {
  const userId = row.userId;
  router.push("/system/user-auth/role/" + userId);
}

/**
 * 重置密码按钮操作
 * @param {Object} row 行数据
 */
function handleResetPwd(row) {
  proxy
    .$prompt("请输入\"" + row.userName + "\"的新密码", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      closeOnClickModal: false,
      inputPattern: /^.{8,20}$/,
      inputErrorMessage: "用户密码长度必须介于 8 和 20 之间",
      inputValidator: (value) => {
        // 校验密码包含的非法字符
        if (/<|>|"|'|\||\\/.test(value)) {
          return "不能包含非法字符：< > \" ' \\\ |";
        }
        // 校验密码强度
        const strengthRegex = {
          upperCase: /[A-Z]/,
          lowerCase: /[a-z]/,
          number: /\d/,
          specialChar: /[!@#$%^&*(),.?":{}|<>]/
        };

        if (!strengthRegex.upperCase.test(value)) {
          return "密码必须包含至少一个大写字母";
        }
        if (!strengthRegex.lowerCase.test(value)) {
          return "密码必须包含至少一个小写字母";
        }
        if (!strengthRegex.number.test(value)) {
          return "密码必须包含至少一个数字";
        }
        if (!strengthRegex.specialChar.test(value)) {
          return "密码必须包含至少一个特殊字符";
        }
      }
    })
    .then(({ value }) => {
      resetUserPwd(row.userId, value).then(() => {
        proxy.$modal.msgSuccess("修改成功，新密码是：" + value);
        getList();
      });
    })
    .catch(() => {
      // 处理取消操作
    });
}

/**
 * 多选框选中数据
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/**
 * 导入按钮操作
 */
function handleImport() {
  upload.title = "用户导入";
  upload.open = true;
}

/**
 * 下载模板操作
 */
function importTemplate() {
  proxy.download(
    "system/user/importTemplate",
    {},
    `user_template_${new Date().getTime()}.xlsx`
  );
}

/** 文件上传中处理 */
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

/**
 * 提交上传文件
 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

/**
 * 重置操作表单
 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: "0",
    remark: undefined,
    postIds: [],
    roleIds: []
  };
  proxy.resetForm("userRef");
}

/**
 * 取消按钮
 */
function cancel() {
  open.value = false;
  reset();
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  getUser().then((response) => {
    postOptions.value = response.posts;
    roleOptions.value = response.roles;
    open.value = true;
    title.value = "新增用户";
    form.value.password = initPassword.value;
  });
}

/**
 * 修改按钮操作
 * @param {Object} [row] 行数据；无值时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const userId = row.userId || ids.value;
  getUser(userId).then((response) => {
    form.value = response.data;
    postOptions.value = response.posts;
    roleOptions.value = response.roles;
    form.value.postIds = response.postIds;
    form.value.roleIds = response.roleIds;
    open.value = true;
    title.value = "修改用户";
    form.password = "";
  });
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["userRef"].validate((valid) => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addUser(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

getDeptTree();
</script>

<style scoped lang="scss">
.el-main {
  padding: 2px 0px;
}

// 下载模板链接的辅助样式，替代原内联 style
.import-template-link {
  font-size: 12px;
  vertical-align: baseline;
}

::v-deep {
  .selectlist .el-tag.el-tag--info {
    background: var(--qt-element-fixed-tag-background) !important;
    border: 0px solid #6ba7ff !important;
    color: var(--qt-element-fixed-primary) !important;
  }
}

//上传附件样式调整
:deep {
  .el-upload-list__item {
    width: 100%;
    height: 25px;
  }

  .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
    background-color: var(--el-color-primary-light-9) !important;
    border-right: 2px solid var(--el-color-primary);
    color: var(--el-color-primary);
  }
}
</style>
