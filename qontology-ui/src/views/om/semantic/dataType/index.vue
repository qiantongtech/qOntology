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
    <GuideTip tip-id="om/semantic/dataType.list" />
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
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
          @click="handleDelete"
          v-hasPermi="['om:dataType:datatype:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['om:dataType:datatype:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情</el-button
          >
          <el-popover
            v-if="auth.hasPermiOr(['om:dataType:datatype:edit', 'om:dataType:datatype:remove'])"
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              v-if="auth.hasPermi('om:dataType:datatype:edit')"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-if="auth.hasPermi('om:dataType:datatype:remove')"
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改数据类型对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <el-form
        ref="dataTypeRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input
                v-model="form.name"
                :maxlength="128"
                placeholder="请输入名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据类型" prop="dataType">
              <el-select v-model="form.dataType" placeholder="请选择数据类型">
                <el-option
                  v-for="item in basic_data_type"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col
            :span="24"
            v-if="
              [dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(
                form.dataType
              )
            "
          >
            <el-form-item label="单位" prop="unit">
              <el-input
                v-model="form.unit"
                :maxlength="32"
                placeholder="请输入单位"
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.DECIMAL].includes(form.dataType)"
          >
            <el-form-item label="小数位数" prop="decimalPlaces">
              <el-input-number
                v-model="form.decimalPlaces"
                :min="0"
                :max="2147483647"
                :precision="0"
                controls-position="right"
                class="form-control-full"
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.DATE].includes(form.dataType)"
          >
            <el-form-item label="格式" prop="format">
              <el-input
                v-model="form.format"
                :maxlength="128"
                placeholder="请输入格式，如 yyyy-MM-dd HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="[dataTypeConstant.ENUM].includes(form.dataType)"
          >
            <el-form-item label="枚举类型" prop="enumType">
              <el-select
                v-model="selectedEnum"
                placeholder="请选择枚举类型"
                filterable
                clearable
                :filter-method="filterEnums"
                :loading="enumLoading && !enumTypeDict.length"
                @visible-change="handleEnumVisible"
              >
                <template #label>
                  <span class="enum-selected-label">
                    <span>{{ selectedEnumName }}</span>
                    <el-tag
                      class="enum-source-tag"
                      :type="form.enumSource === 'GLOBAL' ? 'info' : 'primary'"
                      size="small"
                      :disable-transitions="true"
                      >{{ selectedEnumSourceLabel }}</el-tag
                    >
                  </span>
                </template>
                <el-option-group
                  v-for="group in enumGroups"
                  :key="group.source"
                  :label="group.label"
                >
                  <el-option
                    v-for="item in group.options"
                    :key="item.enumSource + ':' + item.enumType"
                    :label="item.enumName"
                    :value="item.enumSource + ':' + item.enumType"
                  />
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="请输入描述"
                maxlength="500个字符"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="submitForm"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 数据类型详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
    >
      <el-form ref="dataTypeRef" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <div class="form-readonly">
                {{ form.name }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据类型" prop="dataType">
              <dict-tag
                :options="basic_data_type"
                :value="form.dataType"
                class="con-value access-tag"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
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
            <el-form-item label="格式" prop="format">
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
                <span v-if="form.enumType" class="enum-selected-label">
                  <span>{{ selectedEnumName }}</span>
                  <el-tag
                    class="enum-source-tag"
                    :type="form.enumSource === 'GLOBAL' ? 'info' : 'primary'"
                    size="small"
                    :disable-transitions="true"
                    >{{ selectedEnumSourceLabel }}</el-tag
                  >
                </span>
                <span v-else>—</span>
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
import {
  addDataType,
  delDataType,
  getDataType,
  listDataType,
  updateDataType,
} from "@/api/om/dataType/dataType";
import { getEnumTypeDict } from "@/api/om/dataType/dataType";
import { useOntologyVersion } from "@/views/om/useOntologyVersion";
import auth from "@/plugins/auth";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const { versionId, requireVersion } = useOntologyVersion();

const { proxy } = getCurrentInstance();
const { basic_data_type } = proxy.useDict("basic_data_type");

const tableRef = ref();
const dataTypeRef = ref();
const open = ref(false);
const openDetail = ref(false);
const saving = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");
const enumTypeDict = ref([]);
const enumSearch = ref("");
const enumLoading = ref(false);
const { creatorPhones } = useCreatorPhones();

/** 版本号缺失时直接返回空列表，避免无版本上下文发起无效查询。 */
function fetchDataTypes(params) {
  if (!versionId.value) {
    return Promise.resolve({ data: { rows: [], total: 0 } });
  }
  return listDataType({ ...params, versionId: versionId.value });
}

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
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
  columns: [
    { type: "selection", width: 55, minWidth: 55 },
    { label: "编号", prop: "id", slot: "text", width: 80, minWidth: 80, align: "left", sortable: true },
    { label: "名称", prop: "name", slot: "text", minWidth: 160, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "描述", prop: "description", slot: "text", minWidth: 240, align: "left", showOverflowTooltip: { effect: "light" } },
    { label: "数据类型", prop: "dataType", dict: "basic_data_type", width: 120, minWidth: 120 },
    { label: "创建人", prop: "createBy", slot: "creator", width: 130, minWidth: 130, align: "left", headerAlign: "left" },
    { label: "创建时间", prop: "createTime", date: true, width: 160, minWidth: 160, align: "left", sortable: true },
    { label: "操作", slot: "handle", width: 170, minWidth: 170, fixed: "right" },
  ],
  func: fetchDataTypes,
  params: {
    name: null,
    dataType: null,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "名称", prop: "name", component: { is: "input" } },
    { label: "数据类型", prop: "dataType", component: { is: "select", options: basic_data_type } },
  ],
});

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
  rules: {
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
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

const { form, rules } = toRefs(data);

const selectedEnum = computed({
  get: () =>
    form.value.enumType
      ? `${form.value.enumSource || "PROJECT"}:${form.value.enumType}`
      : undefined,
  set: (value) => {
    const separator = (value || "").indexOf(":");
    form.value.enumSource =
      separator < 0 ? "PROJECT" : value.slice(0, separator);
    form.value.enumType = separator < 0 ? null : value.slice(separator + 1);
  },
});
const enumGroups = computed(() => {
  const search = enumSearch.value.trim().toLowerCase();
  return [
    { source: "PROJECT", label: "当前项目" },
    { source: "GLOBAL", label: "全局" },
  ]
    .map((group) => ({
      ...group,
      options: enumTypeDict.value.filter(
        (item) =>
          item.enumSource === group.source &&
          (!search ||
            `${item.enumName} ${item.enumType}`.toLowerCase().includes(search))
      ),
    }))
    .filter((group) => group.options.length);
});
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

let enumRequest = 0;
let detailRequest = 0;

// 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
watch(versionId, async () => {
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  enumTypeDict.value = [];
  enumSearch.value = "";
  handleSelectionChange([]);
  tableStore.params.name = null;
  tableStore.params.dataType = null;
  await nextTick();
  tableRef.value?.resetQuery();
  queryEnumTypeDict();
});

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

/** 关闭新增/修改与详情弹窗并重置表单。 */
function cancel() {
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  reset();
}

/** 重置数据类型表单字段。 */
function reset() {
  form.value = {
    id: null,
    versionId: versionId.value,
    enumType: null,
    enumSource: "PROJECT",
    name: null,
    dataType: null,
    description: null,
    unit: null,
    decimalPlaces: 2,
    format: null,
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
  dataTypeRef.value?.resetFields();
}

/**
 * 多选框选中数据，同步批量删除按钮的禁用状态。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

/** 刷新当前页列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/** 新增按钮操作 */
function handleAdd() {
  if (!requireVersion()) return;
  ++detailRequest;
  reset();
  open.value = true;
  title.value = "添加数据类型";
}

/**
 * 修改按钮操作。
 * @param {Object} row 当前行数据
 */
function handleUpdate(row) {
  const request = ++detailRequest;
  reset();
  const targetId = row.id || ids.value[0];
  getDataType(targetId, versionId.value).then((response) => {
    if (request !== detailRequest) return;
    form.value = response.data;
    open.value = true;
    title.value = "修改数据类型";
  });
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
function handleDetail(row) {
  const request = ++detailRequest;
  reset();
  const targetId = row.id || ids.value[0];
  getDataType(targetId, versionId.value).then((response) => {
    if (request !== detailRequest) return;
    form.value = response.data;
    openDetail.value = true;
    title.value = "数据类型详情";
  });
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
async function submitForm() {
  if (saving.value || !requireVersion()) return;
  const submittingVersion = versionId.value;
  const valid = await dataTypeRef.value.validate().catch(() => false);
  if (
    !valid ||
    saving.value ||
    submittingVersion !== versionId.value ||
    !open.value
  )
    return;
  const isEdit = form.value.id != null;
  saving.value = true;
  try {
    await (isEdit
      ? updateDataType({ ...form.value, versionId: submittingVersion })
      : addDataType({ ...form.value, versionId: submittingVersion }));
    if (submittingVersion !== versionId.value) return;
    proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
    open.value = false;
    refreshList();
  } catch {
    // 接口错误由全局请求层提示；保留弹窗草稿，允许用户修改后重新提交。
  } finally {
    saving.value = false;
  }
}

/**
 * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
 * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
 */
function handleDelete(row) {
  const deletingVersion = versionId.value;
  const deleteIds = row?.id ?? [...ids.value];
  proxy.$modal
    .confirm('是否确认删除数据类型编号为"' + deleteIds + '"的数据项？')
    .then(function () {
      if (deletingVersion !== versionId.value) return;
      return delDataType(deleteIds, deletingVersion);
    })
    .then(() => {
      if (deletingVersion !== versionId.value) return;
      refreshList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
// 数字输入框占满表单项宽度，不使用行内样式。
.form-control-full {
  width: 100%;
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
