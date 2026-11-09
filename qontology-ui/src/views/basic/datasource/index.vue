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
    <GuideTip tip-id="basic/datasource.list" />
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        >
          <template #datasourceTypePrefix="{ value }">
            <img
              v-if="value && getDatasourceIcon(value)"
              :src="getDatasourceIcon(value)"
              class="prefix-icon"
              alt=""
            />
          </template>
          <template #datasourceTypeOption="{ option }">
            <div class="option-content">
              <img
                v-if="getDatasourceIcon(option.value)"
                :src="getDatasourceIcon(option.value)"
                class="option-icon"
                alt=""
              />
              <span class="option-text">{{ option.label }}</span>
            </div>
          </template>
        </qt-search-bar>
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['basic:datasource:datasource:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['basic:datasource:datasource:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #datasourceType="{ row }">
          <QtTagGroup
            :datasource-type="row.datasourceType"
            :label="datasourceTypeMap[row.datasourceType] || row.datasourceType"
            center
          />
        </template>

        <!-- 与枚举和业务数据类型列表一致：姓名在上，联系电话以灰色小字在下。 -->
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
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['basic:datasource:datasource:edit']"
            link
            type="primary"
            icon="View"
            @click="handleTestConnection(row)"
            >测试连接
          </el-button>
          <el-button
            v-hasPermi="['basic:datasource:datasource:edit']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情
          </el-button>
          <!-- 修改与删除收起到更多菜单，沿用各自权限与处理方法。 -->
          <el-popover
            placement="bottom"
            :width="150"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                v-hasPermi="['basic:datasource:datasource:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(row)"
                >修改</el-button
              >
              <el-button
                v-hasPermi="['basic:datasource:datasource:remove']"
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(row)"
                >删除</el-button
              >
            </div>
          </el-popover>
        </template>
        <template #status="{ row }">
                  <el-switch
                      v-model="row.status"
                      :active-value="1"
                      :inactive-value="0"
                      @change="value => handleStatusChange(row, value)"
                  >
                  </el-switch>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 参照数据连接页的宽弹窗与双列布局，连接字段按实际配置顺序排列。 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="1000px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">{{
          title
        }}</span>
      </template>
      <el-form
        ref="daDatasourceRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        class="datasource-form"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <qt-form-item label="数据源名称" prop="datasourceName">
              <el-input
                v-model="form.datasourceName"
                placeholder="请输入数据源名称"
                :maxlength="256"
              />
            </qt-form-item>
          </el-col>
          <el-col :span="12">
            <qt-form-item label="数据源类型" prop="datasourceType">
              <!-- 已保存连接固定类型，避免修改类型后沿用不匹配的库名和模式。 -->
              <el-select
                v-model="form.datasourceType"
                :disabled="form.id != null"
                filterable
                placeholder="请选择数据源类型"
                @change="handleDatasourceChange"
              >
                <template #prefix>
                  <img
                    v-if="
                      form.datasourceType &&
                      getDatasourceIcon(form.datasourceType)
                    "
                    :src="getDatasourceIcon(form.datasourceType)"
                    class="prefix-icon"
                    alt=""
                  />
                </template>
                <el-option
                  v-for="dict in datasource_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                >
                  <div class="option-content">
                    <img
                      v-if="getDatasourceIcon(dict.value)"
                      :src="getDatasourceIcon(dict.value)"
                      class="option-icon"
                      alt=""
                    />
                    <span class="option-text">{{ dict.label }}</span>
                  </div>
                </el-option>
              </el-select>
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <qt-form-item label="数据源地址" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入数据源地址" />
            </qt-form-item>
          </el-col>
          <el-col :span="12">
            <qt-form-item label="端口号" prop="port">
              <el-input
                v-model="form.port"
                placeholder="请输入端口号"
                @input="handlePortInput"
              />
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <qt-form-item label="账号" prop="username">
              <el-input v-model="form.username" placeholder="请输入账号" />
            </qt-form-item>
          </el-col>
          <el-col :span="12">
            <qt-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                show-password
                placeholder="请输入密码"
              />
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.datasourceType" :gutter="20">
          <el-col :span="12">
            <qt-form-item label="数据库名称" prop="dbname" >
              <el-input
                v-model="form.dbname"
                :disabled="form.id != null"
                placeholder="请输入数据库名称"
              />
            </qt-form-item>
          </el-col>
          <el-col v-if="showSchema" :span="12">
            <qt-form-item label="模式" prop="sid" >
              <el-input
                v-model="form.sid"
                :disabled="form.id != null"
                placeholder="请输入模式"
              />
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <qt-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <qt-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                maxlength="256个字符"
                show-word-limit
                placeholder="请输入描述"
              />
            </qt-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <qt-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                :rows="3"
                maxlength="256个字符"
                show-word-limit
                placeholder="请输入备注"
              />
            </qt-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 保留当前配置的测试结果，修改参数后自动清空。 -->
      <el-alert
        v-if="testResult"
        :title="testResult.message"
        :type="testResult.success ? 'success' : 'error'"
        :closable="false"
        show-icon
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            size="mini"
            :loading="testLoading"
            @click="handleTestFormConnection"
            >测试连接</el-button
          >
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button
            type="primary"
            size="mini"
            :disabled="testLoading"
            @click="submitForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 详情沿用新增和修改的宽度、双列布局及字段顺序，仅将输入控件替换为只读内容。 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="1000px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">{{
          title
        }}</span>
      </template>
      <el-form ref="datasourceDetailRef" :model="form" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源名称" prop="datasourceName">
              <div class="form-readonly">{{ form.datasourceName || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据源类型" prop="datasourceType">
              <div class="form-readonly option-content">
                <img
                  v-if="getDatasourceIcon(form.datasourceType)"
                  :src="getDatasourceIcon(form.datasourceType)"
                  class="option-icon"
                  alt=""
                />
                <span>{{
                  datasourceTypeMap[form.datasourceType] ||
                  form.datasourceType ||
                  "-"
                }}</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源地址" prop="ip">
              <div class="form-readonly">{{ form.ip || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="端口号" prop="port">
              <div class="form-readonly">{{ form.port ?? "-" }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="账号" prop="username">
              <div class="form-readonly">{{ form.username || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <div class="form-readonly">
                {{ form.password ? "***********" : "-" }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.datasourceType" :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据库名称" prop="dbname">
              <div class="form-readonly">{{ form.dbname || "-" }}</div>
            </el-form-item>
          </el-col>
          <!-- 与编辑表单共用模式显隐条件，避免同一类型的详情与编辑字段不一致。 -->
          <el-col v-if="showSchema" :span="12">
            <el-form-item label="模式" prop="sid">
              <div class="form-readonly">{{ form.sid || "-" }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="validFlag">
              <div class="form-readonly">
                {{
                  form.validFlag === true
                    ? "启用"
                    : form.validFlag === false
                    ? "禁用"
                    : "-"
                }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div class="form-readonly textarea">
                {{ form.description || "-" }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <div class="form-readonly textarea">{{ form.remark || "-" }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 详情补齐审计信息，创建人展示规则与枚举详情一致。 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建人" prop="createBy">
              <div class="form-readonly">{{ form.createBy || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="creatorPhone">
              <div class="form-readonly">{{ getPhoneNum(form) || "-" }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间" prop="createTime">
              <div class="form-readonly">{{ form.createTime || "-" }}</div>
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

<script setup name="DaDatasource">
import {
  addDaDatasource,
  clientsTest,
  clientsTestStatus,
  delDaDatasource,
  getDaDatasource,
  listDaDatasource,
  updateDaDatasource,
} from "@/api/basic/datasource/daDatasource";
import { useRoute } from "vue-router";
import { getDatasourceIcon } from "@/utils/datasource.js";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { proxy } = getCurrentInstance();
const { datasource_type } = proxy.useDict("datasource_type");
const route = useRoute();
const tableRef = ref(null);
const { creatorPhones } = useCreatorPhones();

function getPhoneNum(record) {
  const phone = creatorPhones.value[record.creatorId];
  return phone ? `${phone}` : "-";
}

const datasourceTypeMap = computed(() =>
  (datasource_type.value || []).reduce((map, dict) => {
    map[dict.value] = dict.label;
    return map;
  }, {})
);

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "数据源名称", prop: "datasourceName", component: { is: "input" } },
    {
      label: "类型",
      prop: "datasourceType",
      component: {
        is: "select",
        options: datasource_type,
        prefixSlot: "datasourceTypePrefix",
        optionSlot: "datasourceTypeOption",
      },
    },
  ],
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
  params: {
    graphId: route.params.kgId,
    datasourceName: null,
    datasourceType: null,
  },
  func: listDaDatasource,
  columns: [
    { type: "selection", width: 55 },
    {
      label: "编号",
      prop: "id",
      width: 80,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending"],
    },
    {
      label: "数据源名称",
      prop: "datasourceName",
      width: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "数据源地址",
      prop: "ip",
      width: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "类型", slot: "datasourceType", width: 120 },
    {
      label: "状态", prop: "status", slot: "status",
      width: 100, minWidth: 100,
    },
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
      date: true,
      width: 160,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending"],
    },
    { label: "操作", slot: "handle", width: 240, minWidth: 240, align: "center", fixed: "right" },
  ],
});

const open = ref(false);
const openDetail = ref(false);
const testLoading = ref(false);
const testResult = ref(null);
let testRequest = 0;
const ids = ref([]);
const multiple = ref(true);
const title = ref("");

const data = reactive({
  form: {},
  rules: {
    datasourceName: [
      { required: true, message: "数据源名称不能为空", trigger: "blur" },
    ],
    datasourceType: [
      { required: true, message: "类型不能为空", trigger: "change" },
    ],
    datasourceConfig: [
      {
        required: true,
        message: "数据源配置(json字符串)不能为空",
        trigger: "blur",
      },
    ],
    ip: [{ required: true, message: "数据源地址不能为空", trigger: "blur" }],
    port: [{ required: true, message: "端口号不能为空", trigger: "blur" }],
    username: [{ required: true, message: "账号不能为空", trigger: "blur" }],
    password: [{ required: true, message: "密码不能为空", trigger: "blur" }],
    dbname: [
      { required: true, message: "数据库名称不能为空", trigger: "blur" },
    ],
    sid: [{ required: true, message: "模式不能为空", trigger: "blur" }],
    // 显式声明布尔类型，确保禁用状态 false 也能通过必填校验。
    validFlag: [
      {
        type: "boolean",
        required: true,
        message: "请选择状态",
        trigger: "change",
      },
    ],
  },
});

const { form, rules } = toRefs(data);

// 与参考页一致，仅 Oracle、金仓及相关数据库显示模式配置。
const showSchema = computed(() =>
  [
    "Oracle",
    "Oracle11",
    "Kingbase8",
    "SQL_Server",
    "SQL_Server2008",
    "PostgreSQL",
  ].includes(form.value.datasourceType)
);

/** 切换新增连接类型后清理旧模式和校验状态，避免隐藏字段保留其他类型的值。 */
function handleDatasourceChange() {
  if (!showSchema.value) form.value.sid = null;
  nextTick(() => proxy.$refs.daDatasourceRef?.clearValidate());
}

/** 端口仅保留数字，沿用参考页的输入处理方式。 */
function handlePortInput(value) {
  form.value.port = String(value).replace(/\D/g, "");
}

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  // 关闭或切换记录时使旧测试失效，防止结果回写到新弹窗。
  ++testRequest;
  testLoading.value = false;
  testResult.value = null;
  form.value = {
    id: null,
    graphId: route.params.kgId,
    datasourceName: null,
    datasourceType: null,
    datasourceConfig: null,
    username: null,
    password: null,
    dbname: null,
    sid: null,
    ip: null,
    port: null,
    listCount: null,
    syncCount: null,
    dataSize: null,
    description: null,
    validFlag: true,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
  };
  proxy.resetForm("daDatasourceRef");
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
  title.value = "新增数据源";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getDaDatasource(_id).then((response) => {
    form.value = response.data;
    if (form.value.datasourceConfig) {
      const config = JSON.parse(form.value.datasourceConfig);
      form.value.username = config.username;
      form.value.password = config.password;
      form.value.dbname = config.dbname;
      form.value.sid = config.sid;
    }
    open.value = true;
    title.value = "修改数据源";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getDaDatasource(_id).then((response) => {
    form.value = response.data;
    if (form.value.datasourceConfig) {
      const config = JSON.parse(form.value.datasourceConfig);
      form.value.username = config.username;
      form.value.password = config.password;
      form.value.dbname = config.dbname;
      form.value.sid = config.sid;
    }
    openDetail.value = true;
    title.value = "数据源详情";
  });
}

/** 详情按钮操作 */
function handleTestConnection(row) {
  reset();
  const _id = row.id || ids.value;
  clientsTest(_id).then((response) => {
    console.log(response);
    proxy.$modal.msgSuccess(response.msg);
  });
}

/** 将当前连接字段组装成独立请求，不覆盖表单中的原始配置或先行保存记录。 */
function buildTestPayload() {
  return {
    datasourceType: form.value.datasourceType,
    ip: form.value.ip,
    port: form.value.port,
    datasourceConfig: JSON.stringify({
      username: form.value.username,
      password: form.value.password,
      dbname: form.value.dbname,
      sid: form.value.sid,
    }),
  };
}

/** 与参考页面一致，校验表单后测试当前配置；处理失败并始终恢复按钮状态。 */
async function handleTestFormConnection() {
  if (testLoading.value || !open.value) return;
  const request = ++testRequest;
  testLoading.value = true;
  testResult.value = null;
  let payload;
  try {
    const valid = await proxy.$refs.daDatasourceRef
      .validate()
      .catch(() => false);
    if (!valid || request !== testRequest || !open.value) return;
    payload = buildTestPayload();
    const response = await clientsTestStatus(payload);
    // 用户修改参数、关闭弹窗或切换记录后，不再展示原配置的测试结果。
    if (
      request !== testRequest ||
      !open.value ||
      JSON.stringify(payload) !== JSON.stringify(buildTestPayload())
    )
      return;
    testResult.value = {
      success: true,
      message: response.msg || "数据库连接成功",
    };
    proxy.$modal.msgSuccess(testResult.value.message);
  } catch (error) {
    if (
      request !== testRequest ||
      !open.value ||
      !payload ||
      JSON.stringify(payload) !== JSON.stringify(buildTestPayload())
    )
      return;
    // 请求层统一弹出错误，弹窗内保留失败信息便于修改后重试。
    testResult.value = {
      success: false,
      message: error?.message || error?.msg || "数据库连接失败，请检查连接信息",
    };
  } finally {
    if (request === testRequest) testLoading.value = false;
  }
}

// 修改连接参数后清空旧结论，下一次测试会重新读取当前表单。
watch(
  () => [
    form.value.datasourceType,
    form.value.ip,
    form.value.port,
    form.value.username,
    form.value.password,
    form.value.dbname,
    form.value.sid,
  ],
  () => {
    testResult.value = null;
  }
);

// 离开页面后忽略尚未完成的连接测试。
onBeforeUnmount(() => {
  ++testRequest;
});

/** 提交按钮 */
function submitForm() {
  if (testLoading.value) return;
  proxy.$refs["daDatasourceRef"].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        form.value.datasourceConfig = JSON.stringify({
          username: form.value.username,
          password: form.value.password,
          dbname: form.value.dbname,
          sid: form.value.sid,
        });
        updateDaDatasource(form.value)
          .then(() => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            tableRef.value?.getList();
          })
          .catch(() => {});
      } else {
        form.value.datasourceConfig = JSON.stringify({
          username: form.value.username,
          password: form.value.password,
          dbname: form.value.dbname,
          sid: form.value.sid,
        });
        addDaDatasource(form.value)
          .then(() => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            tableRef.value?.getList();
          })
          .catch(() => {});
      }
    }
  });
}

async function handleStatusChange(row, value) {
  let data = {
    ...row,
    status: value
  }
  proxy.$modal
      .confirm(`确认要${value === 1 ? "启用" : "禁用"}数据源“${row.datasourceName}”吗？`)
      .then(function () {
        return updateDaDatasource(data);
      })
      .then(() => {
        proxy.$modal.msgSuccess("操作成功");
      })
      .catch(function () {
        row.status = row.status === 1 ? 0 : 1;
      });
}
/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除数据连接编号为"' + _ids + '"的数据项？')
    .then(function () {
      return delDaDatasource(_ids);
    })
    .then(() => {
      tableRef.value?.getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
</script>
<style scoped lang="scss">

.prefix-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
  display: block;
}

.option-content {
  display: flex;
  align-items: center;

  .option-icon {
    width: 20px;
    height: 20px;
    margin-right: 8px;
    object-fit: contain;
    flex-shrink: 0;
  }

  .option-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

/* 对齐对象类型新增页的间隔：新增/修改弹窗中带描述提示的表单项底部留白，避免说明文字贴到下一字段。 */
.datasource-form :deep(.el-form-item.has-tip) {
  margin-bottom: 25px;
}
</style>
