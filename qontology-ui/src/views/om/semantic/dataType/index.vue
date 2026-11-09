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
    <GuideTip v-if="!props.embedded" tip-id="om/semantic/dataType.list" />
    <qt-wrap
      :columns="tableColumns"
      :table-ref="listActions"
      :config="{ fullContent: !props.embedded, searchShadow: !props.embedded }"
    >
      <template #search>
        <qt-search-bar
          :items="searchItems"
          :params="queryParams"
          @query="handleQuery"
          @reset="handleSearchReset"
        />
      </template>
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['om:dataType:datatype:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete()"
          v-hasPermi="['om:dataType:datatype:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>
      <div v-loading="loading">
        <qt-table
          ref="tableRef"
          :columns="tableColumns"
          :config="tableConfig"
          :events="tableEvents"
          :func="readTableRows"
        >
          <template #name="scope">
            {{ scope.row.name || "-" }}
          </template>
          <template #description="scope">
            {{ scope.row.description || "-" }}
          </template>
          <template #dataType="scope">
            <dict-tag :options="basic_data_type" :value="scope.row.dataType" />
          </template>
          <template #valueConfig="{ row }">
            <div class="value-config-lines">
              <div
                v-for="(line, index) in getValueConfigLines(row)"
                :key="index"
                class="text-ellipsis"
                :title="line"
              >
                {{ line }}
              </div>
            </div>
          </template>
          <template #status="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :loading="statusSaving[row.id] === true"
              :disabled="
                !auth.hasPermi('om:dataType:datatype:edit') ||
                statusSaving[row.id] === true
              "
              @change="(value) => handleStatusChange(row, value)"
            />
          </template>
          <template #createBy="scope">
            <div class="flex-column overflow-hidden">
              <span class="text-ellipsis" :title="scope.row.createBy || ''">{{
                scope.row.createBy || "-"
              }}</span>
              <span
                v-if="creatorPhones[scope.row.creatorId]"
                class="desc-text"
                >{{ creatorPhones[scope.row.creatorId] }}</span>
            </div>
          </template>
          <template #createTime="scope">
            <span>{{
              parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}")
            }}</span>
          </template>
          <template #action="scope">
            <el-button
              link
              type="primary"
              icon="view"
              @click="handleDetail(scope.row)"
              v-hasPermi="['om:dataType:datatype:query']"
              >详情
            </el-button>
            <el-button
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['om:dataType:datatype:edit']"
              >修改
            </el-button>
            <el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['om:dataType:datatype:remove']"
              >删除
            </el-button>
          </template>
        </qt-table>
      </div>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </qt-wrap>

    <DataTypeFormDialog
      :show-field-tips="false"
      footer-button-size=""
      v-model:open="open"
      :title="title"
      :form="form"
      :rules="rules"
      :data-type-options="basic_data_type"
      :fixed-enum-type="props.enumType"
      :saving="saving"
      :show-hint="true"
      :description-max-length="256"
      :append-to="$refs['app-container']"
      @cancel="cancel"
      @submit="submitForm"
    >
      <template #before-description="{ form: draft }">
        <qt-form-item
          label="状态"
          prop="status"
          class="row-full"
        >
          <el-radio-group v-model="draft.status"><el-radio
              v-for="item in project_status"
              :key="item.value"
              :value="Number(item.value)"
              >{{ item.label }}</el-radio></el-radio-group>
        </qt-form-item>
      </template>
      <template #enum-source="{ form: draft }">
        <qt-form-item label="枚举来源" prop="enumSource" class="row-full">
          <el-radio-group
            v-model="draft.enumSource"
            :disabled="props.enumType !== undefined"
            @change="() => handleEnumSourceChange(draft)"
          >
            <el-radio value="PROJECT">当前项目</el-radio>
            <el-radio value="GLOBAL">全局</el-radio>
          </el-radio-group>
        </qt-form-item>
      </template>
      <template #enum-selector="{ form: draft }">
        <el-select
          v-model="draft.enumType"
          :disabled="props.enumType !== undefined"
          placeholder="请选择枚举类型"
          filterable
          clearable
          :filter-method="filterEnums"
          :loading="enumLoading && !enumTypeDict.length"
          @visible-change="handleEnumVisible"
        >
          <el-option
            v-for="item in getEnumOptions(draft)"
            :key="item.enumSource + ':' + item.enumType"
            :label="item.enumName"
            :value="item.enumType"
          />
        </el-select>
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
      <el-form
        ref="dataTypeRef"
        :model="form"
        label-width="110px"
        class="data-type-detail-form column-form"
      >
        <qt-form-item label="业务数据名称" prop="name">
          <div class="form-readonly">
            {{ form.name }}
          </div>
        </qt-form-item>
        <qt-form-item label="业务数据类型" prop="dataType">
          <dict-tag
            :options="basic_data_type"
            :value="form.dataType"
            class="con-value access-tag"
          />
        </qt-form-item>
        <!-- 只读详情展示已保存的文本约束，与编辑组件使用相同的格式编码。 -->
        <template v-if="form.dataType === dataTypeConstant.TEXT">
          <qt-form-item label="最小长度"><div class="form-readonly">
              {{ form.minLength ?? "不限制" }}
            </div></qt-form-item>
          <qt-form-item label="最大长度"><div class="form-readonly">
              {{ form.maxLength ?? "不限制" }}
            </div></qt-form-item>
          <qt-form-item label="格式校验" class="row-full"><div class="form-readonly">
              {{ textValidationLabels[form.validationType || "NONE"] }}
            </div></qt-form-item>
        <template v-if="form.validationType === 'CUSTOM'">
          <qt-form-item label="正则表达式"><div class="form-readonly">
              {{ form.validationPattern }}
            </div></qt-form-item>
        </template>
        <qt-form-item label="示例值"><div class="form-readonly">
            {{ form.exampleValue || "-" }}
          </div></qt-form-item>
      </template>

      <qt-form-item
        v-if="
          [dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(
            form.dataType
          )
        "
        label="单位"
        prop="unit"
      >
        <div class="form-readonly">
          {{ form.unit }}
        </div>
      </qt-form-item>

      <!-- 数值范围在整数和小数类型共用，布尔文案及日期范围仅在对应类型展示。 -->
      <template
        v-if="
          [dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(
            form.dataType
          )
        "
      >
        <qt-form-item label="最小值"><div class="form-readonly">
            {{ form.minValue ?? "不限制" }}
          </div></qt-form-item>
        <qt-form-item label="最大值"><div class="form-readonly">
            {{ form.maxValue ?? "不限制" }}
          </div></qt-form-item>
      </template>

      <qt-form-item
        v-if="form.dataType === dataTypeConstant.DECIMAL"
        label="总位数"
      >
        <div class="form-readonly">
          {{ form.allPlaces ?? "-" }}
        </div>
      </qt-form-item>

      <template v-if="form.dataType === dataTypeConstant.BOOLEAN">
        <qt-form-item label="true 对应文案"><el-input
            :model-value="form.trueValue ?? '是'"
            disabled
/></qt-form-item>
        <qt-form-item label="false 对应文案"><el-input
            :model-value="form.falseValue ?? '否'"
            disabled
/></qt-form-item>
      </template>

      <template v-if="form.dataType === dataTypeConstant.DATE">
        <qt-form-item label="最早日期"><div class="form-readonly">
            {{ form.earliestTime || "不限制" }}
          </div></qt-form-item>
        <qt-form-item label="最晚日期"><div class="form-readonly">
            {{ form.latestTime || "不限制" }}
          </div></qt-form-item>
      </template>

      <qt-form-item
        v-if="[dataTypeConstant.DECIMAL].includes(form.dataType)"
        label="小数位数"
        prop="decimalPlaces"
      >
        <div class="form-readonly">
          {{ form.decimalPlaces }}
        </div>
      </qt-form-item>

      <qt-form-item
        v-if="[dataTypeConstant.DATE].includes(form.dataType)"
        label="展示格式"
        prop="format"
      >
        <div class="form-readonly">
          {{ form.format }}
        </div>
      </qt-form-item>

      <qt-form-item
        v-if="[dataTypeConstant.ENUM].includes(form.dataType)"
        label="枚举类型"
        prop="enumType"
      >
        <div class="form-readonly">
          <span v-if="form.enumType" class="enum-selected-label">
            <span>{{ selectedEnumName }}</span>
            <el-tag
              class="enum-source-tag"
              :type="form.enumSource === 'GLOBAL' ? 'info' : 'primary'"
              size="small"
              :disable-transitions="true"
              >{{ selectedEnumSourceLabel }}</el-tag>
          </span>
          <span v-else>—</span>
        </div>
      </qt-form-item>

      <qt-form-item label="状态" prop="status">
        <dict-tag
          :options="project_status"
          :value="form.status"
          class="con-value access-tag"
        />
      </qt-form-item>

      <qt-form-item label="描述" prop="description" class="row-full">
        <div class="form-readonly textarea">
          {{ form.description || "-" }}
        </div>
      </qt-form-item>

      <qt-form-item label="创建人" prop="createBy">
        <div class="form-readonly">{{ form.createBy || "-" }}</div>
      </qt-form-item>

      <qt-form-item label="联系方式">
        <div class="form-readonly">{{ creatorPhones[form.creatorId] || "-" }}</div>
      </qt-form-item>

      <qt-form-item label="创建时间" prop="createTime">
        <div class="form-readonly">
          {{ parseTime(form.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
        </div>
      </qt-form-item>
    </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OmDataType">
import auth from "@/plugins/auth";
import {
  addDataType,
  delDataType,
  getDataType,
  listDataType,
  updateDataTypeStatus,
  updateDataType,
  checkDataTypeDeletable,
} from "@/api/om/dataType/dataType";
import { getEnumTypeDict } from "@/api/om/dataType/dataType";
import DataTypeFormDialog from "@/views/basic/dataType/components/dataTypeFormDialog.vue";
import { loadProjectEnumReferences } from "@/utils/projectEnumDataTypes";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
const props = defineProps({
  embedded: { type: Boolean, default: false },
  enumType: { type: String, default: undefined },
});
const { versionId, requireVersion } = useOntologyVersion();

const tableRef = ref();
const { creatorPhones } = useCreatorPhones();
const saving = ref(false);
const { proxy } = getCurrentInstance();
const { basic_data_type, project_status } = proxy.useDict(
  "basic_data_type",
  "project_status"
);
const statusSaving = reactive({});

const dataTypeList = ref([]);

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const defaultSort = ref({ prop: "createTime", order: "descending" });
const enumTypeDict = ref([]);
const enumSearch = ref("");
const enumLoading = ref(false);

// 数据类型 0-文本、1-整数、2-小数、3-布尔、4-日期、5-枚举
const dataTypeConstant = {
  TEXT: "0",
  INTEGER: "1",
  DECIMAL: "2",
  BOOLEAN: "3",
  DATE: "4",
  ENUM: "5",
};

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    status: null,
    dataType: null,
    createTime: null,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order,
  },
  rules: {
    status: [
      {
        required: true,
        type: "number",
        message: "请选择状态",
        trigger: "change",
      },
    ],
    name: [
      { required: true, message: "名称不能为空", trigger: "blur" },
      { max: 128, message: "名称不能超过128个字符", trigger: "blur" },
    ],
    description: [
      { max: 256, message: "描述不能超过256个字符", trigger: "blur" },
    ],
    enumSource: [
      { required: true, message: "请选择枚举来源", trigger: "change" },
    ],
    enumType: [
      { required: true, message: "请选择枚举类型", trigger: "change" },
    ],
    dataType: [
      { required: true, message: "数据类型不能为空", trigger: "blur" },
    ],
    unit: [
      {
        required: true,
        whitespace: true,
        message: "单位不能为空",
        trigger: "blur",
      },
      { max: 32, message: "单位不能超过32个字符", trigger: "blur" },
    ],
    decimalPlaces: [
      { required: true, message: "小数位数不能为空", trigger: "change" },
      {
        validator: (_rule, value, callback) =>
          callback(
            value != null &&
              value !== "" &&
              (!Number.isInteger(value) || value < 0)
              ? new Error("小数位数必须为非负整数")
              : undefined
          ),
        trigger: "change",
      },
    ],
    format: [
      {
        required: true,
        whitespace: true,
        message: "格式不能为空",
        trigger: "blur",
      },
      { max: 128, message: "格式不能超过128个字符", trigger: "blur" },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

function handleEnumSourceChange(draft) {
  draft.enumType = null;
  enumSearch.value = "";
}
const textValidationLabels = {
  NONE: "无校验",
  PHONE: "手机号码",
  ID_CARD: "身份证号",
  EMAIL: "电子邮箱",
  URL: "URL地址",
  CUSTOM: "自定义正则表达式",
};

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
      ["trueValue", "true 文案"],
      ["falseValue", "false 文案"],
    ],
    4: [
      ["format", "展示格式"],
      ["earliestTime", "最早日期"],
      ["latestTime", "最晚日期"],
    ],
    5: [
      ["enumType", "枚举类型"],
      ["enumSource", "枚举来源"],
    ],
  };
  const lines = [];
  for (const [key, label] of fieldsByType[String(row.dataType)] || []) {
    const value = row[key];
    if (value == null || ["", "-", "-"].includes(String(value).trim()))
      continue;
    let display = value;
    if (key === "validationType")
      display = textValidationLabels[value] || value;
    if (key === "enumType") display = row.enumName || value;
    if (key === "enumSource")
      display = { PROJECT: "当前项目", GLOBAL: "全局" }[value] || value;
    lines.push(`${label}：${display}`);
    if (lines.length === 2) break;
  }
  return [lines[0] ?? "-", lines[1] ?? "-"];
}

function getEnumOptions(draft) {
  const source = draft.enumSource || "PROJECT";
  const search = enumSearch.value.trim().toLowerCase();
  return enumTypeDict.value.filter(
    (item) =>
      item.enumSource === source &&
      (!search ||
        `${item.enumName} ${item.enumType}`.toLowerCase().includes(search))
  );
}
const selectedEnumName = computed(() => {
  const source = form.value.enumSource || "PROJECT";
  const option = enumTypeDict.value.find(
    (item) =>
      item.enumSource === source && item.enumType === form.value.enumType
  );
  return option?.enumName || form.value.enumType || "-";
});
const selectedEnumSourceLabel = computed(() =>
  form.value.enumSource === "GLOBAL" ? "全局" : "当前项目"
);
function filterEnums(value) {
  enumSearch.value = value;
}
function handleEnumVisible(visible) {
  enumSearch.value = "";
  if (visible) queryEnumTypeDict();
}

// 列表数据由原有请求逻辑管理，保留版本隔离、请求顺序及删除后的分页处理。
const tableColumns = reactive([
  {
    type: "selection",
    width: 55,
    align: "center",
  },
  {
    label: "编号",
    prop: "id",
    width: 80,
    align: "center",
    sortable: true,
  },
  {
    label: "业务数据名称",
    prop: "name",
    minWidth: 180,
    showOverflowTooltip: { effect: "light" },
    align: "left",
    slot: "name",
  },
  {
    label: "描述",
    prop: "description",
    minWidth: 200,
    showOverflowTooltip: { effect: "light" },
    align: "left",
    slot: "description",
  },
  {
    label: "业务数据类型",
    prop: "dataType",
    width: 120,
    align: "center",
    slot: "dataType",
  },
  {
    label: "取值配置",
    slot: "valueConfig",
    minWidth: 220,
    align: "left",
  },
  { label: "状态", prop: "status", slot: "status", width: 90, align: "center" },
  {
    label: "创建人",
    prop: "createBy",
    width: 130,
    align: "left",
    headerAlign: "left",
    slot: "createBy",
  },
  {
    label: "创建时间",
    prop: "createTime",
    width: 160,
    align: "center",
    sortable: true,
    slot: "createTime",
  },
  {
    label: "操作",
    width: 240,
    align: "center",
    fixed: "right",
    className: "small-padding fixed-width",
    slot: "action",
  },
]);
const searchItems = computed(() => [
  { label: "业务数据名称", prop: "name", component: { is: "input" } },
  {
    label: "业务数据类型",
    prop: "dataType",
    component: { is: "select", options: basic_data_type.value },
  },
  {
    label: "状态",
    prop: "status",
    component: { is: "select", options: project_status.value },
  },
]);
const listActions = { getList };
const tableConfig = {
  notPagination: true,
  sort: true,
  table: {
    stripe: true,
    rowKey: "id",
    defaultSort: defaultSort.value,
    onSelectionChange: handleSelectionChange,
  },
};
const tableEvents = {
  onSortChange(params) {
    handleSortChange({ prop: params.orderByColumn, order: params.isAsc });
  },
};
function readTableRows() {
  return Promise.resolve({ data: dataTypeList.value });
}
watch(dataTypeList, () => tableRef.value?.getList());
function handleSearchReset() {
  handleQuery();
}

let listRequest = 0;
let enumRequest = 0;
let detailRequest = 0;
watch(
  [versionId, () => props.enumType],
  () => {
    ++detailRequest;
    open.value = false;
    openDetail.value = false;
    dataTypeList.value = [];
    enumTypeDict.value = [];
    enumSearch.value = "";
    handleSelectionChange([]);
    queryParams.value.pageNum = 1;
    getList();
    queryEnumTypeDict();
  },
  { immediate: true }
);

async function handleStatusChange(row, value) {
  const previous = value === 1 ? 0 : 1;
  const submittingVersion = versionId.value;
  statusSaving[row.id] = true;
  try {
    await proxy.$modal.confirm(
      `确认要${value === 1 ? "启用" : "禁用"}业务数据类型“${row.name}”吗？`
    );
    if (submittingVersion !== versionId.value) {
      row.status = previous;
      return;
    }
    await updateDataTypeStatus({
      id: row.id,
      versionId: submittingVersion,
      status: value,
    });
    if (submittingVersion !== versionId.value) return;
    proxy.$modal.msgSuccess("操作成功");
    await getList();
  } catch {
    row.status = previous;
  } finally {
    delete statusSaving[row.id];
  }
}

/** 查询数据类型列表 */
async function getList() {
  const request = ++listRequest;
  if (!versionId.value || (props.enumType !== undefined && !props.enumType)) {
    dataTypeList.value = [];
    total.value = 0;
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const response =
      props.enumType !== undefined
        ? await loadRelatedDataTypes()
        : await listDataType({
            ...queryParams.value,
            versionId: versionId.value,
            ...(props.enumType !== undefined
              ? {
                  dataType: dataTypeConstant.ENUM,
                  enumType: props.enumType,
                  enumSource: "PROJECT",
                }
              : {}),
          });
    if (request !== listRequest) return;
    dataTypeList.value = (response.data.rows || []).map((row) => ({
      ...row,
      status: Number(row.status ?? 1),
    }));
    total.value = response.data.total;
    handleSelectionChange([]);
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

async function loadRelatedDataTypes() {
  const { pageNum, pageSize, ...filters } = queryParams.value;
  // 当前页仅展示关联本枚举的数据类型；选择其他基础类型时返回空列表。
  if (
    filters.dataType != null &&
    filters.dataType !== "" &&
    String(filters.dataType) !== dataTypeConstant.ENUM
  ) {
    return { data: { rows: [], total: 0 } };
  }
  const rows = await loadProjectEnumReferences({
    ...filters,
    versionId: versionId.value,
    enumType: props.enumType,
  });
  const key = filters.orderByColumn || "createTime";
  const direction = filters.isAsc === "ascending" ? 1 : -1;
  rows.sort(
    (a, b) =>
      (typeof a[key] === "number" && typeof b[key] === "number"
        ? a[key] - b[key]
        : String(a[key] ?? "").localeCompare(String(b[key] ?? ""))) * direction
  );
  return {
    data: {
      rows: rows.slice((pageNum - 1) * pageSize, pageNum * pageSize),
      total: rows.length,
    },
  };
}

async function queryEnumTypeDict() {
  const request = ++enumRequest;
  const requestedVersion = versionId.value;
  if (!requestedVersion) {
    enumLoading.value = false;
    return;
  }
  enumLoading.value = true;
  const isCurrentRequest = () =>
    request === enumRequest && requestedVersion === versionId.value;
  try {
    await Promise.allSettled(
      ["PROJECT", "GLOBAL"].map(async (source) => {
        const response = await getEnumTypeDict(requestedVersion, source);
        if (!isCurrentRequest()) return;
        if (!Array.isArray(response.data))
          throw new Error("枚举接口未返回有效列表");
        const options = response.data.map((item) => ({
          ...item,
          enumSource: source,
        }));
        // 仅替换本来源的数据，另一来源为空或失败时保留已经加载的选项
        enumTypeDict.value = [
          ...enumTypeDict.value.filter((item) => item.enumSource !== source),
          ...options,
        ];
      })
    );
  } finally {
    if (isCurrentRequest()) enumLoading.value = false;
  }
}

// 取消按钮
function cancel() {
  if (saving.value) return;
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    status: 1,
    versionId: versionId.value,
    enumType: props.enumType ?? null,
    enumSource: "PROJECT",
    name: null,
    dataType:
      props.enumType !== undefined
        ? dataTypeConstant.ENUM
        : dataTypeConstant.TEXT,
    description: null,
    unit: null,
    decimalPlaces: 2,
    format: "YYYY-MM-DD",
    allPlaces: 10,
    minValue: null,
    maxValue: null,
    trueValue: "是",
    falseValue: "否",
    earliestTime: null,
    latestTime: null,
    minLength: null,
    maxLength: null,
    validationType: "NONE",
    validationPattern: null,
    exampleValue: null,
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
  proxy.resetForm("dataTypeRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 排序触发事件 */
function handleSortChange(column) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  if (!requireVersion()) return;
  ++detailRequest;
  reset();
  open.value = true;
  title.value = "新增业务数据类型";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const request = ++detailRequest;
  reset();
  const _id = row.id || ids.value;
  getDataType(_id, versionId.value).then((response) => {
    if (request !== detailRequest) return;
    form.value = {
      ...response.data,
      status: Number(response.data.status ?? 1),
    };
    open.value = true;
    title.value = "修改业务数据类型";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  const request = ++detailRequest;
  reset();
  const _id = row.id || ids.value;
  getDataType(_id, versionId.value).then((response) => {
    if (request !== detailRequest) return;
    form.value = {
      ...response.data,
      status: Number(response.data.status ?? 1),
    };
    openDetail.value = true;
    title.value = "业务数据类型详情";
  });
}

/** 提交按钮 */
async function submitForm(draft) {
  if (!requireVersion() || saving.value) return;
  const submittingVersion = versionId.value;
  saving.value = true;
  try {
    const payload = {
      ...draft,
      versionId: submittingVersion,
      ...(props.enumType !== undefined
        ? {
            dataType: dataTypeConstant.ENUM,
            enumType: props.enumType,
            enumSource: "PROJECT",
          }
        : {}),
    };
    await (payload.id != null ? updateDataType(payload) : addDataType(payload));
    if (submittingVersion !== versionId.value) return;
    proxy.$modal.msgSuccess(payload.id != null ? "修改成功" : "新增成功");
    open.value = false;
    getList();
  } finally {
    saving.value = false;
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const deletingVersion = versionId.value;
  const recordIds = row ? [row.id] : [...ids.value];
  if (!recordIds.length) return;
  // 启用中或被对象类型属性、公理/规则引用的业务数据类型不可删，删除前统一取回原因。
  checkDataTypeDeletable(recordIds, deletingVersion)
    .then(({ data }) => {
      if (deletingVersion !== versionId.value) return;
      const blockedMap = data || {};
      const reasonOf = (id) => blockedMap[String(id)];
      if (row && reasonOf(row.id)) {
        proxy.$modal.msgWarning(reasonOf(row.id));
        return;
      }
      const deletableIds = recordIds.filter((id) => !reasonOf(id));
      const cannotDeleteCount = recordIds.length - deletableIds.length;
      if (!deletableIds.length) {
        return proxy.$modal.confirm(`可删除0个，不可删除${cannotDeleteCount}个，是否删除可删除部分？`);
      }
      const confirmText = cannotDeleteCount
        ? `可删除${deletableIds.length}个，不可删除${cannotDeleteCount}个，是否删除可删除部分？`
        : row
          ? '是否确认删除数据类型编号为"' + deletableIds.join(",") + '"的数据项？'
          : `是否确认删除选中的${deletableIds.length}个业务数据类型？`;
      return proxy.$modal
        .confirm(confirmText)
        .then(function () {
          if (deletingVersion !== versionId.value) return;
          return delDataType(deletableIds.join(","), deletingVersion);
        })
        .then(() => {
          if (deletingVersion !== versionId.value) return;
          getList();
          proxy.$modal.msgSuccess("删除成功");
        });
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
.value-config-lines {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  line-height: 22px;
}

.data-type-detail-form :deep(.el-form-item__label) {
  white-space: nowrap;
}

.enum-selected-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  max-width: 100%;
  vertical-align: middle;
}
.enum-source-tag {
  flex-shrink: 0;
  padding: 0 5px;
  font-size: 12px;
  font-weight: 400;
  height: 18px;
  line-height: 16px;
}
</style>
