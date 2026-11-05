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
  <div ref="panelRef">
    <qt-wrap
      :columns="columns"
      :table-ref="listActions"
      :config="{ fullContent: false }"
    >
      <template #search>
        <qt-search-bar
          :items="searchItems"
          :params="query"
          :table-ref="tableRef"
          @query="handleQuery"
          @reset="handleQuery"
        />
      </template>
      <template #actions-data>
        <el-button
          v-hasPermi="['om:object:type:edit']"
          type="primary"
          plain
          :disabled="saving"
          @click="handleAdd"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:object:type:edit']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting || loading"
          @click="handleDelete()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <div v-loading="loading">
        <qt-table
          ref="tableRef"
          :columns="columns"
          :config="tableConfig"
          :events="tableEvents"
          :func="readTableRows"
        >
          <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
          <template #description="{ row }">{{ row.description?.trim() }}</template>
          <template #typeCategory="{ row }">
            <el-tag :type="row.typeCategory === 2 ? 'success' : 'primary'" effect="light">
              {{ dataTypeLabel(row.typeCategory) }}
            </el-tag>
          </template>
          <template #createTime="{ row }">{{
            proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}")
          }}</template>
          <template #operation="{ row }">
            <el-button
              v-hasPermi="['om:object:type:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
              >详情</el-button>
            <el-button
              v-hasPermi="['om:object:type:edit']"
              link
              type="primary"
              icon="Edit"
              :disabled="saving"
              @click="handleUpdate(row)"
              >修改</el-button>
            <el-button
              v-hasPermi="['om:object:type:edit']"
              link
              type="danger"
              icon="Delete"
              :disabled="deleting"
              @click="handleDelete(row)"
              >删除</el-button>
          </template>
          <template #empty>
            <div class="emptyBg">
              <img src="@/assets/system/images/no_data/noData.png" alt="暂无属性" />
              <p>暂无属性</p>
            </div>
          </template>
        </qt-table>
      </div>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </qt-wrap>

    <el-dialog
      v-model="open"
      :title="dialogTitle"
      width="800px"
      :append-to="panelRef"
      :close-on-press-escape="!saving"
      :before-close="beforeClose"
      draggable
      destroy-on-close
      @closed="attributeRef?.clearValidate()"
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="维护对象类型的属性定义，统一属性名称、代码与取值规则，为后续关系建模和数据映射提供一致的字段依据。"
      />
      <el-form
        ref="attributeRef"
        class="column-form attribute-dialog-form"
        :model="form"
        :rules="rules"
        label-width="110px"
        @submit.prevent="submitForm"
      >
        <div class="h2-title row-full">基础信息</div>

        <qt-form-item
          class="row-full"
          tip="填写业务中使用的标准属性名称，保持同一对象类型下命名一致。"
          label="属性名称"
          prop="name"
        >
          <el-input v-model="form.name" placeholder="请输入属性名称" maxlength="128" />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          tip="以英文字母开头，后续仅支持英文字母、数字和下划线，作为属性的英文标识。"
          label="属性代码"
          prop="code"
        >
          <el-input v-model="form.code" placeholder="例如 purchase_amount" maxlength="128" />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          tip="说明该属性的业务含义、取值范围与适用场景，最多500个字符。"
          label="描述"
          prop="description"
        >
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            :maxlength="500"
            show-word-limit
          />
        </qt-form-item>

        <div class="h2-title row-full">数据定义</div>

        <qt-form-item
          class="row-full"
          tip="直接配置基础数据类型的取值规则，或引用已定义的业务数据类型。"
          label="数据类型"
          prop="typeCategory"
        >
          <el-radio-group v-model="form.typeCategory" @change="handleDataTypeChange">
            <el-radio
              v-for="option in dataTypeOptions"
              :key="option.value"
              :label="option.value"
              >{{ option.label }}</el-radio>
          </el-radio-group>
        </qt-form-item>

        <qt-form-item
          v-if="form.typeCategory === 1"
          class="row-full"
          tip="选择该属性取值所属的基础类型，不同类型会展示对应的补充配置项。"
          label="基础数据类型"
          prop="basicTypeCode"
          required
        >
          <el-select
            v-model="form.basicTypeCode"
            placeholder="请选择基础数据类型"
            style="width: 100%"
            clearable
            @change="handleBasicTypeChange"
          >
            <el-option
              v-for="item in basic_data_type"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)"
            />
          </el-select>
        </qt-form-item>

        <template v-else>
          <qt-form-item
            class="row-full"
            tip="选择业务数据类型来自当前项目还是全局，切换后会重新筛选可选类型。"
            label="业务类型来源"
            prop="businessTypeSource"
            required
          >
            <el-radio-group v-model="form.businessTypeSource" @change="handleBusinessSourceChange">
              <el-radio value="PROJECT">当前项目</el-radio>
              <el-radio value="GLOBAL">全局</el-radio>
            </el-radio-group>
          </qt-form-item>
          <qt-form-item
            class="row-full"
            tip="引用已定义的业务数据类型，其类型配置将作为该属性的取值规则。"
            label="业务数据类型"
            prop="businessTypeId"
            required
          >
            <el-select
              v-model="form.businessTypeId"
              placeholder="请选择业务数据类型"
              style="width: 100%"
              filterable
              clearable
              :loading="businessTypesLoading && !businessDataTypes.length"
              no-data-text="暂无可用业务数据类型"
              @change="clearTypeConfig"
              @visible-change="(value) => value && loadBusinessDataTypes()"
            >
              <el-option
                v-for="item in filteredBusinessTypes"
                :key="item.businessTypeSource + ':' + item.id"
                :label="item.name"
                :value="String(item.id)"
              />
            </el-select>
          </qt-form-item>
        </template>

        <qt-form-item
          v-if="form.typeCategory === 2 && form.businessTypeId"
          class="row-full"
          tip="由所选业务数据类型自动带出，不可修改。"
          label="基础数据类型"
        >
          <div class="form-readonly">{{ basicTypeLabel(effectiveConfig.basicTypeCode) }}</div>
        </qt-form-item>

        <div v-if="form.typeCategory === 1 || form.businessTypeId" class="row-full attribute-type-config">
          <AttributeTypeConfig
            :model="effectiveConfig"
            :readonly="form.typeCategory === 2"
            :enum-options="enumOptions"
            :enum-loading="enumsLoading"
            @enum-open="loadEnums"
          />
        </div>

        <qt-form-item
          v-if="form.typeCategory === 2 && selectedBusinessType?.description"
          class="row-full"
          tip="所选业务数据类型的说明，用于核对是否符合当前属性的业务语义。"
          label="类型描述"
        >
          <div class="form-readonly textarea">{{ selectedBusinessType.description }}</div>
        </qt-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" :disabled="saving" @click="open = false">取 消</el-button>

          <el-button
            type="primary"
            size="mini"
            :loading="saving"
            @click="submitForm"
            >确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailOpen"
      title="属性详情"
      width="800px"
      :append-to="panelRef"
      draggable
    >
      <el-form class="attribute-dialog-form" label-width="110px">
        <div class="h2-title">基础信息</div>

        <el-row :gutter="20">
          <el-col :span="24"><el-form-item label="属性名称"><div class="form-readonly">
                {{ attributeDetail.name }}
              </div></el-form-item></el-col>

          <el-col :span="24"><el-form-item label="属性代码"><div class="form-readonly">
                {{ attributeDetail.code }}
              </div></el-form-item></el-col>
        </el-row>

        <el-form-item label="描述"><div class="form-readonly textarea">
            {{ attributeDetail.description || "-" }}
          </div></el-form-item>

        <el-form-item label="创建人"><div class="form-readonly">
            {{ attributeDetail.createBy || "-" }}
          </div></el-form-item>

        <el-form-item label="创建时间"><div class="form-readonly">
            {{
              proxy.parseTime(
                attributeDetail.createTime,
                "{y}-{m}-{d} {h}:{i}"
              ) || "-"
            }}
          </div></el-form-item>

        <div class="h2-title">数据定义</div>

        <el-form-item label="数据类型"><div class="form-readonly">
            {{ dataTypeLabel(attributeDetail.typeCategory) }}
          </div></el-form-item>

        <el-form-item v-if="attributeDetail.typeCategory === 2" label="业务类型来源">
          <div class="form-readonly">{{ attributeDetail.businessTypeSource === "PROJECT" ? "当前项目" : "全局" }}</div>
        </el-form-item>
        <el-form-item
          v-if="attributeDetail.typeCategory === 2"
          label="业务数据类型"
          ><div class="form-readonly">
            {{ findBusinessType(attributeDetail)?.name || selectedTypeLabel(attributeDetail) }}
          </div></el-form-item>

        <el-form-item label="基础数据类型"><div class="form-readonly">
            {{ basicTypeLabel(detailConfig.basicTypeCode) }}
          </div></el-form-item>

        <AttributeTypeConfig
          :model="detailConfig"
          readonly
          :enum-options="enumOptions"
        />
      </el-form>

      <template #footer><div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div></template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  computed,
  getCurrentInstance,
  nextTick,
  onBeforeUnmount,
  reactive,
  ref,
  watch,
} from "vue";

import { ElMessage, ElMessageBox } from "element-plus";

import AttributeTypeConfig from "./AttributeTypeConfig.vue";

import {
  listAttributes,
  getAttribute,
  addAttribute,
  updateAttribute,
  delAttribute,
  listAttributeBusinessDataTypes,
  listAttributeEnums,
} from "@/api/om/object/attribute";

const props = defineProps({
  objectTypeId: { type: [String, Number], required: true },

  versionId: { type: [String, Number], default: undefined },
});

const emit = defineEmits(["changed"]);

const { proxy } = getCurrentInstance();

const panelRef = ref();

const attributeRef = ref();

const tableRef = ref();

const rows = ref([]);

const selectedIds = ref([]);

const total = ref(0);

const loading = ref(false);

const saving = ref(false);

const deleting = ref(false);

const open = ref(false);

const detailOpen = ref(false);

const dialogTitle = ref("新增属性");

const query = reactive({
  name: "",
  pageNum: 1,
  pageSize: 10,
  orderByColumn: "createTime",
  isAsc: "descending",
});

const appliedName = ref("");

const { om_attribute_data_type, basic_data_type } = proxy.useDict(
  "om_attribute_data_type",
  "basic_data_type"
);

const businessDataTypes = ref([]);

const businessTypesLoading = ref(false);

const enumOptions = ref([]);

const enumsLoading = ref(false);

const dataTypeOptions = computed(() =>
  om_attribute_data_type.value.map((dict) => ({
    label: dict.label,
    value: Number(dict.value),
  }))
);

const dataTypeLabel = (value) =>
  dataTypeOptions.value.find((option) => option.value === value)?.label || "-";

const columns = reactive([
  { type: "selection", width: 55, align: "center", fixed: "left" },
  {
    label: "编号",
    prop: "id",
    slot: "text",
    width: 80,
    minWidth: 80,
    align: "center",
    fixed: "left",
    sortable: "custom",
    sortOrders: ["descending", "ascending", null],
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "属性名称",
    prop: "name",
    slot: "text",
    width: 240,
    minWidth: 240,
    align: "left",
    fixed: "left",
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "属性代码",
    prop: "code",
    slot: "text",
    width: 190,
    minWidth: 190,
    align: "left",
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "描述",
    prop: "description",
    slot: "description",
    minWidth: 320,
    align: "left",
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "数据类型",
    prop: "typeCategory",
    slot: "typeCategory",
    width: 150,
    minWidth: 150,
    align: "center",
  },
  {
    label: "创建人",
    prop: "createBy",
    slot: "text",
    width: 130,
    minWidth: 130,
    align: "left",
    headerAlign: "left",
    showOverflowTooltip: { effect: "light" },
  },
  {
    label: "创建时间",
    prop: "createTime",
    slot: "createTime",
    width: 170,
    minWidth: 170,
    align: "center",
    sortable: "custom",
    sortOrders: ["descending", "ascending", null],
  },
  {
    label: "操作",
    slot: "operation",
    width: 200,
    minWidth: 200,
    align: "center",
    fixed: "right",
    className: "small-padding fixed-width",
  },
]);

const searchItems = [
  { label: "属性名称", prop: "name", component: { is: "input" } },
];

// 列表请求仍由本组件管理，qt-table 只负责读取已加载的数据。
const listActions = { getList };
const tableConfig = {
  notPagination: true,
  sort: true,
  table: {
    stripe: true,
    rowKey: "id",
    defaultSort: { prop: "createTime", order: "descending" },
    onSelectionChange: handleSelectionChange,
  },
};
const tableEvents = {
  onSortChange(params) {
    handleSortChange({ prop: params.orderByColumn, order: params.isAsc });
  },
};

function readTableRows() {
  return Promise.resolve({ data: rows.value });
}
watch(rows, () => tableRef.value?.getList());

const emptyForm = () => ({
  id: undefined,

  objectTypeId: String(props.objectTypeId),

  versionId: props.versionId,

  name: "",

  code: "",

  typeCategory: 1,

  basicTypeCode: undefined,

  businessTypeId: undefined,

  businessTypeSource: "PROJECT",

  unit: "",

  decimalPlaces: 2,

  format: "",

  enumType: undefined,

  enumSource: "PROJECT",

  description: "",
});

const form = ref(emptyForm());

const attributeDetail = ref({});

const selectedBusinessType = computed(() => findBusinessType(form.value));

const filteredBusinessTypes = computed(() =>
  businessDataTypes.value.filter((item) =>
    item.businessTypeSource === (form.value.businessTypeSource || "GLOBAL"),
  ),
);

function handleBusinessSourceChange() {
  form.value.businessTypeId = undefined;
  clearTypeConfig();
  nextTick(() => attributeRef.value?.clearValidate("businessTypeId"));
}

function findBusinessType(attribute) {
  return businessDataTypes.value.find(
    (item) =>
      String(item.id) === String(attribute.businessTypeId) &&
      item.businessTypeSource === (attribute.businessTypeSource || "GLOBAL")
  );
}

function resolvedConfig(attribute) {
  const selected =
    attribute.typeCategory === 2 ? findBusinessType(attribute) : undefined;

  return selected
    ? { ...selected, basicTypeCode: optionalNumber(selected.dataType) }
    : attribute;
}

const effectiveConfig = computed(() => resolvedConfig(form.value));

const detailConfig = computed(() => resolvedConfig(attributeDetail.value));

const basicTypeLabel = (value) =>
  basic_data_type.value.find((item) => Number(item.value) === Number(value))
    ?.label || "未设置";

const validateConfig = (field, requiredTypes, message) => ({
  required: field !== "enumType",

  validator: (_rule, value, callback) => {
    if (
      form.value.typeCategory !== 1 ||
      !requiredTypes.includes(form.value.basicTypeCode)
    )
      return callback();

    if (
      value == null ||
      value === "" ||
      (typeof value === "string" && !value.trim())
    ) {
      return callback(field === "enumType" ? undefined : new Error(message));
    }

    const invalid =
      field === "decimalPlaces"
        ? !Number.isInteger(value) || value < 0
        : value == null || !String(value).trim();

    callback(invalid ? new Error(message) : undefined);
  },

  trigger: "change",
});

const rules = {
  name: [
    {
      required: true,
      whitespace: true,
      message: "属性名称不能为空",
      trigger: "blur",
    },

    { max: 128, message: "属性名称不能超过128个字符", trigger: "blur" },
  ],

  code: [
    { required: true, message: "属性代码不能为空", trigger: "blur" },

    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "须以字母开头，仅允许字母、数字和下划线",
      trigger: "blur",
    },

    { max: 128, message: "属性代码不能超过128个字符", trigger: "blur" },
  ],

  typeCategory: [
    {
      required: true,
      type: "enum",
      enum: [1, 2],
      message: "请选择数据类型",
      trigger: "change",
    },
  ],

  basicTypeCode: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.typeCategory === 1 &&
            !basic_data_type.value.some((item) => Number(item.value) === value)
            ? new Error("请选择基础数据类型")
            : undefined
        ),

      trigger: "change",
    },
  ],

  businessTypeId: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.typeCategory === 2 && !findBusinessType(form.value)
            ? new Error("请选择有效的业务数据类型")
            : undefined
        ),

      trigger: "change",
    },
  ],

  unit: [validateConfig("unit", [1, 2], "请输入单位")],

  decimalPlaces: [validateConfig("decimalPlaces", [2], "请输入有效的小数位数")],

  format: [validateConfig("format", [4], "请输入日期格式")],

  enumType: [validateConfig("enumType", [5], "请选择枚举类型")],

  description: [
    { max: 500, message: "描述不能超过500个字符", trigger: "blur" },
  ],
};

let listRequest = 0;

let dialogRequest = 0;

let contextRequest = 0;

let businessRequest = 0;

let enumRequest = 0;

const optionalNumber = (value) =>
  value == null || value === "" ? undefined : Number(value);

function normalizeAttribute(attribute = {}) {
  return {
    ...attribute,

    typeCategory: optionalNumber(attribute.typeCategory),

    basicTypeCode: optionalNumber(attribute.basicTypeCode),

    decimalPlaces: optionalNumber(attribute.decimalPlaces),

    businessTypeSource: attribute.businessTypeSource || "GLOBAL",

    enumSource: attribute.enumSource || "PROJECT",

    businessTypeId:
      attribute.businessTypeId == null || attribute.businessTypeId === ""
        ? undefined
        : String(attribute.businessTypeId),
  };
}

function selectedTypeLabel(attribute) {
  if (attribute.typeCategory !== 2)
    return basicTypeLabel(attribute.basicTypeCode);

  const selected = findBusinessType(attribute);

  const origin =
    attribute.businessTypeSource === "PROJECT" ? "当前项目" : "全局";

  return selected
    ? `${selected.name}（${origin}）`
    : attribute.businessTypeId
    ? `已失效或不可用（${origin}：${attribute.businessTypeId}）`
    : "未设置";
}

async function loadBusinessDataTypes() {
  if (businessTypesLoading.value) return;

  const request = ++businessRequest;

  const objectId = props.objectTypeId;

  businessTypesLoading.value = true;

  try {
    await Promise.allSettled(
      ["PROJECT", "GLOBAL"].map(async (source) => {
        const response = await listAttributeBusinessDataTypes(objectId, source);

        if (request !== businessRequest) return;

        if (!Array.isArray(response.data))
          throw new Error("业务类型返回格式无效");

        businessDataTypes.value = [
          ...businessDataTypes.value.filter(
            (item) => item.businessTypeSource !== source
          ),

          ...response.data.map((item) => ({
            ...item,
            id: String(item.id),
            businessTypeSource: source,
          })),
        ];
      })
    );
  } finally {
    if (request === businessRequest) businessTypesLoading.value = false;
  }
}

async function loadEnums() {
  if (enumsLoading.value) return;

  const request = ++enumRequest;

  const objectId = props.objectTypeId;

  enumsLoading.value = true;

  try {
    await Promise.allSettled(
      ["PROJECT", "GLOBAL"].map(async (source) => {
        const response = await listAttributeEnums(objectId, source);

        if (request !== enumRequest) return;

        if (!Array.isArray(response.data)) throw new Error("枚举返回格式无效");

        enumOptions.value = [
          ...enumOptions.value.filter((item) => item.enumSource !== source),

          ...response.data.map((item) => ({ ...item, enumSource: source })),
        ];
      })
    );
  } finally {
    if (request === enumRequest) enumsLoading.value = false;
  }
}

function clearTypeConfig() {
  Object.assign(form.value, {
    unit: "",
    decimalPlaces: 2,
    format: "",
    enumType: undefined,
    enumSource: "PROJECT",
  });

  nextTick(() =>
    attributeRef.value?.clearValidate([
      "unit",
      "decimalPlaces",
      "format",
      "enumType",
    ])
  );
}

function handleBasicTypeChange() {
  clearTypeConfig();
}

function handleDataTypeChange() {
  form.value.basicTypeCode = undefined;

  form.value.businessTypeId = undefined;

  form.value.businessTypeSource = "PROJECT";

  clearTypeConfig();

  nextTick(() =>
    attributeRef.value?.clearValidate(["basicTypeCode", "businessTypeId"])
  );
}

watch(open, (value) => {
  if (value) {
    loadBusinessDataTypes();

    loadEnums();
  }
});

watch(detailOpen, (value) => {
  if (value) {
    loadBusinessDataTypes();

    loadEnums();
  }
});

async function getList() {
  const request = ++listRequest;

  loadBusinessDataTypes();

  loading.value = true;

  try {
    const response = await listAttributes({
      objectTypeId: props.objectTypeId,

      versionId: props.versionId,

      name: appliedName.value,

      pageNum: query.pageNum,

      pageSize: query.pageSize,

      orderByColumn: query.orderByColumn,

      isAsc: query.isAsc,
    });

    if (request !== listRequest) return;

    const page = response.data || {};

    total.value = Number(page.total || 0);

    const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));

    if (query.pageNum > maxPage) {
      query.pageNum = maxPage;

      return await getList();
    }

    rows.value = (page.rows || []).map(normalizeAttribute);

    selectedIds.value = [];

    tableRef.value?.elTableRef?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

function handleQuery() {
  appliedName.value = (query.name || "").trim();

  query.pageNum = 1;

  getList();
}

function handleSortChange({ prop, order }) {
  query.orderByColumn = order ? prop : "createTime";

  query.isAsc = order || "descending";

  query.pageNum = 1;

  getList();
}

function handleAdd() {
  ++dialogRequest;

  form.value = emptyForm();

  dialogTitle.value = "新增属性";

  open.value = true;

  nextTick(() => attributeRef.value?.clearValidate());
}

async function handleUpdate(row) {
  const request = ++dialogRequest;

  const response = await getAttribute(row.id, props.objectTypeId);

  if (request !== dialogRequest) return;

  form.value = { ...emptyForm(), ...normalizeAttribute(response.data) };

  dialogTitle.value = "修改属性";

  open.value = true;

  nextTick(() => attributeRef.value?.clearValidate());
}

async function handleDetail(row) {
  const request = ++dialogRequest;

  const response = await getAttribute(row.id, props.objectTypeId);

  if (request !== dialogRequest) return;

  attributeDetail.value = normalizeAttribute(response.data);

  detailOpen.value = true;
}

async function submitForm() {
  const context = contextRequest;

  if (saving.value || !(await attributeRef.value.validate().catch(() => false)))
    return;

  if (saving.value || context !== contextRequest || !open.value) return;

  ++dialogRequest;

  const data = {
    id: form.value.id,

    objectTypeId: form.value.objectTypeId,

    versionId: form.value.versionId,

    name: form.value.name.trim(),

    code: form.value.code.trim(),

    typeCategory: form.value.typeCategory,

    basicTypeCode:
      form.value.typeCategory === 1 ? form.value.basicTypeCode : null,

    businessTypeId:
      form.value.typeCategory === 2 ? form.value.businessTypeId : null,

    businessTypeSource:
      form.value.typeCategory === 2 ? form.value.businessTypeSource : null,

    unit:
      form.value.typeCategory === 1 && [1, 2].includes(form.value.basicTypeCode)
        ? form.value.unit
        : null,

    decimalPlaces:
      form.value.typeCategory === 1 && form.value.basicTypeCode === 2
        ? form.value.decimalPlaces
        : null,

    format:
      form.value.typeCategory === 1 && form.value.basicTypeCode === 4
        ? form.value.format
        : null,

    enumType:
      form.value.typeCategory === 1 && form.value.basicTypeCode === 5
        ? form.value.enumType
        : null,

    enumSource:
      form.value.typeCategory === 1 && form.value.basicTypeCode === 5
        ? form.value.enumSource
        : null,

    description: (form.value.description || "").trim(),
  };

  saving.value = true;

  try {
    await (data.id ? updateAttribute(data) : addAttribute(data));

    if (context !== contextRequest) return;

    ElMessage.success(data.id ? "修改成功" : "新增成功");

    open.value = false;

    await getList();

    if (context === contextRequest) emit("changed");
  } finally {
    saving.value = false;
  }
}

function beforeClose(done) {
  if (!saving.value) done();
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map((row) => row.id);
}

async function handleDelete(row) {
  if (deleting.value) return;

  const ids = row ? [row.id] : [...selectedIds.value];

  if (!ids.length) return;

  const context = contextRequest;

  const objectTypeId = props.objectTypeId;

  try {
    await ElMessageBox.confirm(
      row
        ? `是否删除属性“${row.name}”？`
        : `是否删除选中的 ${ids.length} 条属性？`,

      "删除确认",

      { type: "warning" }
    );
  } catch {
    return;
  }

  if (context !== contextRequest || deleting.value) return;

  deleting.value = true;

  try {
    await delAttribute(ids, objectTypeId);

    if (context !== contextRequest) return;

    ElMessage.success("删除成功");

    await getList();

    if (context === contextRequest) emit("changed");
  } finally {
    deleting.value = false;
  }
}

watch(
  () => [props.objectTypeId, props.versionId],

  () => {
    ++contextRequest;

    ++dialogRequest;

    ++businessRequest;

    ++enumRequest;

    businessDataTypes.value = [];

    enumOptions.value = [];

    businessTypesLoading.value = false;

    enumsLoading.value = false;

    open.value = false;

    detailOpen.value = false;

    rows.value = [];

    selectedIds.value = [];

    total.value = 0;

    query.name = "";

    appliedName.value = "";

    query.pageNum = 1;

    getList();
  },

  { immediate: true }
);

onBeforeUnmount(() => {
  ++contextRequest;

  ++dialogRequest;

  ++listRequest;

  ++businessRequest;

  ++enumRequest;
});
</script>

<style scoped lang="scss">
.h2-title {
  font-size: 16px;

  color: rgba(0, 0, 0, 0.85);

  display: flex;

  align-items: center;

  font-family: PingFang SC;

  font-weight: 500;

  margin: 8px 0;
}

.h2-title::before {
  display: inline-block;

  content: "";

  width: 6px;

  height: 16px;

  border-radius: 3px;

  background: var(--el-color-primary);

  margin-right: 8px;
}

.business-type-label {
  display: inline-flex;

  align-items: center;

  gap: 6px;
}

.attribute-dialog-form :deep(.el-form-item__label) {
  white-space: nowrap;

  flex-shrink: 0;
}

// column-form 会清掉表单项下边距，动态类型配置需要单独留出提示文案的空间。
.attribute-dialog-form.column-form .attribute-type-config :deep(.el-form-item) {
  margin-bottom: 20px !important;
}

.attribute-dialog-form.column-form .attribute-type-config :deep(.el-form-item:last-child) {
  margin-bottom: 0 !important;
}

.character-count :deep(.el-input__count) {
  background: transparent;
}

.character-count :deep(.el-input__count)::after {
  content: "个字符";
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}
</style>
