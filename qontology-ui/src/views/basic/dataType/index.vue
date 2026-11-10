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
  <div
    :class="props.embedded ? 'data-type-embedded' : 'app-container'"
    ref="app-container"
  >
    <GuideTip v-if="!props.embedded" tip-id="basic/dataType.list" />
    <!-- 独立页面使用完整布局，内嵌模式由详情页提供外层卡片。 -->
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="{ fullContent: !props.embedded, searchShadow: !props.embedded }"
    >
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
          @query="applyFilters"
          @reset="applyFilters"
        />
      </template>
      <template v-if="!props.readOnly" #actions-data>
        <el-button
          v-hasPermi="['basic:dataType:datatype:add']"
          type="primary"
          plain
          :disabled="props.enumType !== undefined && !props.enumType"
          @click="handleAdd"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['basic:dataType:datatype:remove']"
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>
      <!-- 切换枚举时重建表格，重置分页并隔离前一个枚举尚未完成的列表请求。 -->
      <qt-table :key="props.enumType" v-bind="tableStore" ref="tableRef">
        <!-- 文本列配置使用 text 插槽，必须提供对应渲染，才能显示首次加载及修改刷新后的字段值。 -->
        <template #text="{ row, column_data }">
          {{ row[column_data.prop] }}
        </template>
        <template #dataType="{ row }">
          <dict-tag
            v-if="row.dataType !== '-'"
            :options="basic_data_type"
            :value="row.dataType"
          />
          <span v-else>—</span>
        </template>
        <template #typeConfig="{ row }">
          <div class="flex-column overflow-hidden">
            <div v-for="(line, index) in getValueConfigLines(row)" :key="index" class="text-ellipsis" :title="line">{{ line }}</div>
          </div>
        </template>
        <template #status="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="1"
            :inactive-value="0"
            :loading="statusSaving[row.id] === true"
            :disabled="props.readOnly || !auth.hasPermi('basic:dataType:datatype:edit') || statusSaving[row.id] === true"
            @change="value => handleStatusChange(row, value)"
          />
        </template>
        <!-- 与枚举创建人和项目责任人保持一致：姓名在上，联系电话以灰色小字在下。 -->
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
            v-hasPermi="['basic:dataType:datatype:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
          >详情
          </el-button>
          <el-button
            v-hasPermi="['basic:dataType:datatype:edit']"
            link
            type="primary"
            icon="Edit"
            :disabled="row.status === 1"
            @click="handleUpdate(row)"
          >修改
          </el-button>
          <el-button
            v-hasPermi="['basic:dataType:datatype:remove']"
            link
            type="danger"
            icon="Delete"
            :disabled="row.status === 1"
            @click="handleDelete(row)"
          >删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 独立组件维护表单草稿和类型配置，父页面接收校验后的快照并执行保存。 -->
    <DataTypeFormDialog
      v-model:open="open"
      :title="title"
      :form="form"
      :rules="rules"
      :data-type-options="basic_data_type"
      :enum-options="enumTypeDict"
      :show-field-tips="false"
      footer-button-size=""
      :fixed-enum-type="props.enumType"
      :saving="saving"
      :append-to="$refs['app-container']"
      @cancel="cancel"
      @submit="submitForm"
    >
      <template #before-description="{ form: draft }">
        <qt-form-item label="状态" prop="status" class="row-full">
          <el-radio-group v-model="draft.status">
            <el-radio
              v-for="item in project_status"
              :key="item.value"
              :label="Number(item.value)"
            >{{ item.label }}</el-radio>
          </el-radio-group>
        </qt-form-item>
      </template>
    </DataTypeFormDialog>

    <!-- 数据类型详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="dataTypeRef" :model="form" label-width="100px">

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="业务数据名称" prop="name">
              <div class="form-readonly">
                {{ form.name }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <dict-tag :options="project_status" :value="form.status" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="业务数据类型" prop="dataType">
              <dict-tag
                :options="basic_data_type"
                :value="form.dataType"
                class="con-value access-tag"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <!-- 只读详情展示已保存的文本约束，与编辑组件使用相同的格式编码。 -->
          <template v-if="form.dataType === dataTypeConstant.TEXT">
            <el-col :span="24"><el-form-item label="最小长度"><div class="form-readonly">
              {{ form.minLength ?? "不限制" }}
            </div></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="最大长度"><div class="form-readonly">
              {{ form.maxLength ?? "不限制" }}
            </div></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="格式校验"><div class="form-readonly">
              {{ textValidationLabels[form.validationType || "NONE"] }}
            </div></el-form-item></el-col>
            <el-col
              v-if="form.validationType === 'CUSTOM'"
              :span="24"
            ><el-form-item label="正则表达式"><div class="form-readonly">
              {{ form.validationPattern }}
            </div></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="示例值"><div class="form-readonly">
              {{ form.exampleValue || "-" }}
            </div></el-form-item></el-col>
          </template>
          <el-col
            :span="24"
            v-if="
              [dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(
                form.dataType
              )
            "
          >
            <el-form-item label="单位" prop="unit">
              <div class="form-readonly">
                {{ form.unit }}
              </div>
            </el-form-item>
          </el-col>
          <!-- 数值范围在整数和小数类型共用，布尔文案及日期范围仅在对应类型展示。 -->
          <template
            v-if="
              [dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(
                form.dataType
              )
            "
          >
            <el-col :span="24"><el-form-item label="最小值"><div class="form-readonly">
              {{ form.minValue ?? "不限制" }}
            </div></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="最大值"><div class="form-readonly">
              {{ form.maxValue ?? "不限制" }}
            </div></el-form-item></el-col>
          </template>
          <el-col
            v-if="form.dataType === dataTypeConstant.DECIMAL"
            :span="24"
          ><el-form-item label="总位数"><div class="form-readonly">
            {{ form.allPlaces ?? "-" }}
          </div></el-form-item></el-col>
          <template v-if="form.dataType === dataTypeConstant.BOOLEAN">
            <el-col :span="24"><el-form-item label="true 对应文案"><el-input
              :model-value="form.trueValue ?? '是'"
              disabled
            /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="false 对应文案"><el-input
              :model-value="form.falseValue ?? '否'"
              disabled
            /></el-form-item></el-col>
          </template>
          <template v-if="form.dataType === dataTypeConstant.DATE">
            <el-col :span="24"><el-form-item label="最早日期"><div class="form-readonly">
              {{ form.earliestTime || "不限制" }}
            </div></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="最晚日期"><div class="form-readonly">
              {{ form.latestTime || "不限制" }}
            </div></el-form-item></el-col>
          </template>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.DECIMAL].includes(form.dataType)"
          >
            <el-form-item label="小数位数" prop="decimalPlaces">
              <div class="form-readonly">
                {{ form.decimalPlaces }}
              </div>
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.DATE].includes(form.dataType)"
          >
            <el-form-item label="展示格式" prop="format">
              <div class="form-readonly">
                {{ form.format }}
              </div>
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.ENUM].includes(form.dataType)"
          >
            <el-form-item label="枚举类型" prop="enumType">
              <div class="form-readonly">
                {{ form.enumType }}
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
          <el-col :span="24">
            <el-form-item label="创建人" prop="createBy">
              <div class="form-readonly">
                {{ form.createBy || "-" }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系方式" prop="creatorPhone">
              <div class="form-readonly">
                {{ getPhoneNum(form.creatorId) || "-" }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建时间" prop="createTime">
              <div class="form-readonly">
                {{ parseTime(form.createTime, "{y}-{m}-{d} {h}:{i}") }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="DataType">
import auth from "@/plugins/auth";
import {
  addDataType,
  delDataType,
  getDataType,
  listDataType,
  updateDataType,
  updateDataTypeStatus,
} from "@/api/basic/dataType/dataType";
import {
  computed,
  getCurrentInstance,
  onMounted,
  reactive,
  ref,
  toRefs,
  watch,
} from "vue";
import { getEnumTypeDict } from "@/api/basic/enumeration/type.js";
import DataTypeFormDialog from "./components/dataTypeFormDialog.vue";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

// 提供内嵌展示、只读展示和固定枚举筛选，默认不传参数时保持独立页面的管理能力。
const props = defineProps({
  embedded: { type: Boolean, default: false },
  readOnly: { type: Boolean, default: false },
  enumType: { type: String, default: undefined },
});

const { proxy } = getCurrentInstance();
const { basic_data_type, project_status } = proxy.useDict("basic_data_type", "project_status");
const statusSaving = reactive({});

const open = ref(false);
// 保存状态由父页面管理，并传给弹窗阻止重复提交及保存中关闭。
const saving = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const multiple = ref(true);
const selectedRows = ref([]);
const title = ref("");
const defaultSort = { prop: "createTime", order: "descending" };
const enumTypeDict = ref([]);

// 数据类型 0-文本、1-整数、2-小数、3-布尔、4-日期、5-枚举
const dataTypeConstant = {
  TEXT: "0",
  INTEGER: "1",
  DECIMAL: "2",
  BOOLEAN: "3",
  DATE: "4",
  ENUM: "5",
};

// 详情中的格式名称与编辑组件及后端的格式编码一致。
const textValidationLabels = {
  NONE: "无校验",
  PHONE: "手机号码",
  ID_CARD: "身份证号",
  EMAIL: "电子邮箱",
  URL: "URL地址",
  CUSTOM: "自定义正则表达式",
};

const tableRef = ref();
const { creatorPhones,getPhoneNum } = useCreatorPhones();

// 将已提交名称与输入草稿分开，保证翻页、排序和刷新沿用最近一次查询的条件。
const appliedFilters = reactive({ name: "", dataType: "", status: null });

// 与枚举列表使用相同的配置式表格，由公共组件统一管理分页、排序和列显隐。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort,
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: computed(() => [
    { type: "selection", width: 55 },
    { label: "编号", prop: "id", slot: "text", width: 80, sortable: true },
    {
      label: "业务数据名称",
      prop: "name",
      slot: "text",
      width: 120,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    // 枚举详情传入固定关联键时，所有记录均为枚举类型，无需重复显示数据类型列。
    ...(props.enumType === undefined
      ? [
          {
            label: "业务数据类型",
            prop: "dataType",
            slot: "dataType",
            width: 120,
          },
        ]
      : []),
    // 原“单位”列升级为按业务数据类型展示对应配置的标签列。
    {
      label: "取值配置",
      prop: "unit",
      slot: "typeConfig",
      width: 220,
      align: "left",
      headerAlign: "left",
    },
    { label: "状态", prop: "status", slot: "status", width: 90, align: "center" },
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
      align: "center",
      width: 160,
      sortable: true,
    },
    ...(!props.readOnly
      ? [{ label: "操作", slot: "handle", width: 240, minWidth: 240, align: "center", fixed: "right" }]
      : []),
  ]),
  func: loadDataTypes,
  params: { name: "", dataType: "", status: null },
  events: {
    formatParams: formatListParams,
    formatData: formatListData,
    onDataLoaded: clearSelection,
  },
});

// 有固定枚举关联键时只开放名称筛选；独立页面保留基础数据类型筛选。
const searchStore = computed(() => ({
  items: [
    { label: "业务数据名称", prop: "name", component: { is: "input" } },
    ...(props.enumType === undefined
      ? [
          {
            label: "业务数据类型",
            prop: "dataType",
            component: { is: "select", options: basic_data_type.value },
          },
        ]
      : []),
    { label: "状态", prop: "status", component: { is: "select", options: project_status.value } },
  ],
}));

/** 提交名称筛选；搜索栏随后统一重置页码并刷新公共表格。 */
function applyFilters() {
  appliedFilters.name = (tableStore.params.name || "").trim();
  appliedFilters.dataType = tableStore.params.dataType ?? "";
  appliedFilters.status = tableStore.params.status === "" ? null : tableStore.params.status;
}

/** 固定枚举关联范围，并在取消排序时恢复默认的创建时间倒序。 */
function formatListParams(params) {
  return {
    ...params,
    ...appliedFilters,
    // 5 为枚举基础类型，同时精确匹配当前枚举编码，排除其他枚举及遗留关联值。
    dataType:
      props.enumType !== undefined
        ? dataTypeConstant.ENUM
        : appliedFilters.dataType,
    enumType: props.enumType,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order,
  };
}

/** 关联键缺失时直接返回空页，避免接口忽略空筛选后展示全部枚举业务数据类型。 */
function loadDataTypes(params) {
  if (props.enumType !== undefined && !params.enumType)
    return Promise.resolve({ data: { rows: [], total: 0 } });
  return listDataType(params);
}

/** 统一文本空值展示，使公共表格与自定义插槽使用相同占位符，同时保留数值零。 */
function formatListData(rows) {
  const fields = tableStore.columns
    .map((column) => column.prop)
    .filter(Boolean);
  return rows.map((row) => {
    const record = { ...row, status: Number(row.status ?? 1) };
    fields.forEach((field) => {
      if (record[field] == null || record[field] === "") record[field] = "-";
    });
    return record;
  });
}

// 按类型读取实际配置，跳过空项后只展示两项；数值零属于有效配置。
function getValueConfigLines(row) {
  const fieldsByType = {
    0: [
      ["validationType", "校验"],
      ["minLength", "最小长度"],
      ["maxLength", "最大长度"],
      ...(row.validationType === "CUSTOM"
        ? [["validationPattern", "正则表达式"]]
        : []),
      ["exampleValue", "示例值"],
    ],
    1: [
      ["unit", "单位"],
      ["minValue", "最小值"],
      ["maxValue", "最大值"],
    ],
    2: [
      ["unit", "单位"],
      ["decimalPlaces", "小数位数"],
      ["allPlaces", "总位数"],
      ["minValue", "最小值"],
      ["maxValue", "最大值"],
    ],
    3: [
      ["trueLabel", "true 文案"],
      ["falseLabel", "false 文案"],
    ],
    4: [
      ["format", "展示格式"],
      ["earliestTime", "最早日期"],
      ["latestTime", "最晚日期"],
    ],
    5: [
      ["enumType", "枚举类型"],

    ],
  };
  const lines = [];
  for (const [key, label] of fieldsByType[String(row.dataType)] || []) {
    const value = row[key];
    if (value == null || ["", "-"].includes(String(value).trim()))
      continue;
    let display = value;
    if (key === "validationType")
      display = textValidationLabels[value] || value;
    if (key === "enumType") display = enumTypeDict.value.find(item => item.enumType === value)?.enumName || value;
    lines.push(`${label}：${display}`);
    if (lines.length === 2) break;
  }
  return [lines[0] ?? "-", lines[1] ?? "-"];
}

async function handleStatusChange(row, value) {
  if (statusSaving[row.id]) return;
  statusSaving[row.id] = true;
  try {
    await proxy.$modal.confirm(`确认要${value === 1 ? "启用" : "停用"}业务数据类型“${row.name}”吗？`);
    await updateDataTypeStatus(row.id, value);
    row.status = value;
    tableRef.value?.getList();
    proxy.$modal.msgSuccess("操作成功");
  } catch {
    // 取消或失败时保留原有状态。
  } finally {
    delete statusSaving[row.id];
  }
}

/** 切换枚举时清空名称草稿及已提交条件，新表格实例会自动加载该枚举的第一页。 */
watch(
  () => props.enumType,
  () => {
    tableStore.params.name = "";
    tableStore.params.dataType = "";
    appliedFilters.name = "";
    appliedFilters.dataType = "";
    appliedFilters.status = null;
    tableStore.params.status = null;
    open.value = false;
    openDetail.value = false;
  }
);

/** 表格重新加载后清除旧的勾选项，避免批量删除上一次查询的记录。 */
function clearSelection() {
  handleSelectionChange([]);
  tableRef.value?.elTableRef?.clearSelection();
}

const data = reactive({
  form: {},
  rules: {
    name: [
      { required: true, message: "业务数据名称不能为空", trigger: "blur" },
      { max: 128, message: "业务数据名称不能超过128个字符", trigger: "blur" },
    ],
    description: [
      { max: 256, message: "描述不能超过256个字符", trigger: "blur" },
    ],
    // 下拉框选择时触发 change，及时重新校验并清除先前的必填提示。
    dataType: [
      { required: true, message: "业务数据类型不能为空", trigger: "change" },
    ],
    format: [{ required: true, message: "格式不能为空", trigger: "blur" }],
    enumType: [
      { required: true, message: "枚举类型不能为空", trigger: "change" },
    ],
  },
});

const { form, rules } = toRefs(data);

// 只读关联列表不需要加载编辑用的枚举选项；列表首屏由 QtTable 自行请求。
onMounted(() => {
  if (!props.readOnly) queryEnumTypeDict();
});

/** 新增、修改或删除成功后刷新当前列表，查询范围仍由固定参数控制。 */
function getList() {
  tableRef.value?.getList();
}

/** 加载新增和修改表单使用的枚举类型选项。 */
function queryEnumTypeDict() {
  getEnumTypeDict().then((response) => {
    enumTypeDict.value = response.data;
  });
}

// 取消按钮
function cancel() {
  if (saving.value) return;
  open.value = false;
  openDetail.value = false;
  reset();
}

/** 重置表单；独立入口默认文本类型，关联入口固定使用当前枚举。 */
function reset() {
  // 先清理旧表单校验和字段，再赋初值，防止 resetFields 将固定关联值恢复为空。
  proxy.resetForm("dataTypeRef");
  form.value = {
    id: null,
    name: null,
    type: null,
    dataType:
      props.enumType !== undefined
        ? dataTypeConstant.ENUM
        : dataTypeConstant.TEXT,
    enumType: props.enumType ?? null,
    description: null,
    unit: null,
    decimalPlaces: 2,
    allPlaces: 10,
    minValue: null,
    maxValue: null,
    status: 1,
    trueValue: "是",
    falseValue: "否",
    earliestTime: null,
    latestTime: null,
    minLength: null,
    maxLength: null,
    validationType: "NONE",
    validationPattern: null,
    exampleValue: null,
    format: "YYYY-MM-DD",
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
  };
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  selectedRows.value = selection;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  // 固定关联键尚未就绪时禁止新增，避免提交无归属的数据类型。
  if (props.enumType !== undefined && !props.enumType) return;
  // 只读模式即使通过脚本调用方法，也不允许执行写操作。
  if (props.readOnly) return;
  reset();
  open.value = true;
  title.value = "新增业务数据类型";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  // 只读模式即使通过脚本调用方法，也不允许执行写操作。
  if (props.readOnly) return;
  reset();
  const targetId = row.id || ids.value;
  getDataType(targetId).then((response) => {
    form.value = {
      ...response.data,
      status: Number(response.data.status ?? 1),
      trueValue: response.data.trueLabel,
      falseValue: response.data.falseLabel,
    };
    open.value = true;
    title.value = "修改业务数据类型";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const targetId = row.id || ids.value;
  getDataType(targetId).then((response) => {
    form.value = {
      ...response.data,
      status: Number(response.data.status ?? 1),
      trueValue: response.data.trueLabel,
      falseValue: response.data.falseLabel,
    };
    openDetail.value = true;
    title.value = "业务数据类型详情";
  });
}

/** 保存组件校验后的数据；关联入口仍在提交边界固定当前枚举，成功后刷新列表。 */
async function submitForm(draft) {
  if (props.readOnly || saving.value || !open.value) return;
  if (props.enumType !== undefined && !props.enumType) return;
  const enumContext = props.enumType;
  // 使用独立快照，避免保存中的表单变化影响本次请求。
  const payload =
    props.enumType !== undefined
      ? { ...draft, dataType: dataTypeConstant.ENUM, enumType: props.enumType }
      : { ...draft };
  payload.trueLabel = payload.trueValue;
  payload.falseLabel = payload.falseValue;
  delete payload.trueValue;
  delete payload.falseValue;
  saving.value = true;
  try {
    if (payload.id != null) await updateDataType(payload);
    else await addDataType(payload);
    // 切换关联枚举后不关闭新上下文的弹窗，也不刷新旧上下文的表格。
    if (enumContext !== props.enumType) return;
    proxy.$modal.msgSuccess(payload.id != null ? "修改成功" : "新增成功");
    open.value = false;
    getList();
  } catch {
    // 全局请求层展示错误；保留组件中的草稿，便于修正后再次提交。
  } finally {
    saving.value = false;
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  // 只读模式即使通过脚本调用方法，也不允许执行写操作。
  if (props.readOnly) return;
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.id 区分）
  if (row?.id != null) {
    const deleteId = row.id;
    proxy.$modal
      .confirm('是否确认删除数据类型编号为"' + deleteId + '"的数据项？')
      .then(function () {
        return delDataType(deleteId);
      })
      .then(() => {
        // 删除后回到第一页，避免移除末页全部关联记录后停留在空页。
        tableRef.value?.resetQuery();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {});
    return;
  }

  // 批量删除：根据状态（1=启用不可删除，0=禁用可删除）判断
  const rows = selectedRows.value;
  if (!rows.length) return;
  const deletableRows = rows.filter((item) => item.status === 0);
  const undeletableCount = rows.length - deletableRows.length;
  const deletableIds = deletableRows.map((item) => item.id);
  proxy.$modal
    .confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    )
    .then(function () {
      if (!deletableIds.length) {
        proxy.$modal.msgWarning("无可删除的数据");
        return;
      }
      return delDataType(deletableIds);
    })
    .then(() => {
      tableRef.value?.resetQuery();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
/* “取值配置”列内的多个标签（如布尔 true/false）换行排列。 */
</style>
