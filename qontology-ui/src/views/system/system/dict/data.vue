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

<!--
  @file 字典数据管理页面（页面级详情）
  @description 头部展示所属字典类型信息，下部维护该类型下的字典数据列表；支持新增、修改、删除、批量删除、状态切换与导出。
-->

<template>
  <div ref="pageRef" v-loading="loading" class="app-container dict-data-detail-page">
    <!-- 头部信息卡复用通用详情组件；返回行为由本页接管，关闭当前页签回到字典管理列表。 -->
    <detail-info
      v-if="detail.dictId != null"
      :data="detail"
      :header="{
        className: 'clearfixs',
        idKey: 'dictId',
        nameKey: 'dictName',
        statusKey: 'status',
        statusOptions: sys_normal_disable,
        routerBack: false
      }"
      :items="detailItems"
      mode="free"
      @back="handleClose"
    />
    <el-empty v-if="!loading && detail.dictId == null" :description="emptyMessage" />
    <!-- 下部列表区与枚举数据页一致：字典类型固定为当前详情上下文，不再作为可选查询条件。 -->
    <div v-if="detail.dictId != null" class="pagecont-bottom">
      <qt-wrap
        :columns="tableStore.columns"
        :table-ref="tableRef"
        :config="{ fullContent: false, searchShadow: false }"
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

        <template #actions-data>
          <el-button
            v-hasPermi="['system:dict:add']"
            type="primary"
            plain
            :disabled="!defaultDictType || !contextReady"
            @click="handleAdd"
          >
            <i class="iconfont-mini icon-xinzeng mr5"></i>新增
          </el-button>
          <el-button
            v-hasPermi="['system:dict:remove']"
            type="danger"
            plain
            :disabled="multiple || deleting"
            @click="handleDelete()"
          >
            <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
          </el-button>
          <el-button
            v-hasPermi="['system:dict:export']"
            type="warning"
            plain
            :disabled="!defaultDictType"
            @click="handleExport"
          >
            <i class="iconfont-mini icon-daochu mr5"></i>导出
          </el-button>
        </template>

        <!-- 路由入口先解析字典类型，再挂载表格；切换上下文时重建实例，隔离旧列表响应。 -->
        <qt-table
          v-if="contextReady"
          :key="defaultDictType"
          v-bind="tableStore"
          ref="tableRef"
        >
          <template #text="{ row, column_data }">{{
            row[column_data.prop] === '' ? '-' : row[column_data.prop]
          }}</template>
          <template #dictLabel="{ row }">
            <span
              v-if="
                (row.listClass == '' || row.listClass == 'default') &&
                (row.cssClass == '' || row.cssClass == null)
              "
              >{{ row.dictLabel }}</span
            >
            <el-tag
              v-else
              :type="row.listClass == 'primary' ? '' : row.listClass"
              :class="row.cssClass"
              >{{ row.dictLabel }}</el-tag
            >
          </template>
          <template #status="{ row }">
            <el-switch
              v-if="row.status === '0' || row.status === '1'"
              v-model="row.status"
              active-value="0"
              inactive-value="1"
              @change="handleStatusChange(row)"
            />
            <span v-else>—</span>
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
              v-hasPermi="['system:dict:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
              >详情</el-button
            >
            <el-button
              v-hasPermi="['system:dict:edit']"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
              >修改</el-button
            >
            <el-button
              v-hasPermi="['system:dict:remove']"
              link
              type="danger"
              icon="Delete"
              :disabled="deleting || row.status === '0'"
              @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </qt-table>
      </qt-wrap>
    </div>

    <!-- 新增或修改字典数据对话框 -->
    <el-dialog
      v-model="open"
      :title="title"
      width="900px"
      :append-to="pageRef"
      draggable
      destroy-on-close
    >
      <el-form
        ref="dataRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <qt-form-item label="字典类型">
          <el-input v-model="form.dictType" :disabled="true" />
        </qt-form-item>
        <qt-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
        </qt-form-item>
        <qt-form-item label="数据键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
        </qt-form-item>
        <qt-form-item label="样式属性" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
        </qt-form-item>
        <qt-form-item label="显示排序" prop="dictSort">
          <el-input-number
            v-model="form.dictSort"
            class="dict-sort-input"
            controls-position="right"
            :min="0"
          />
        </qt-form-item>
        <qt-form-item label="回显样式" prop="listClass">
          <el-select v-model="form.listClass">
            <el-option
              v-for="item in listClassOptions"
              :key="item.value"
              :label="item.label + '(' + item.value + ')'"
              :value="item.value"
            ></el-option>
          </el-select>
        </qt-form-item>
        <qt-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
            maxlength="256"
            show-word-limit
          ></el-input>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 字典数据详情对话框 -->
    <el-dialog
      title="字典数据详情"
      v-model="openDetail"
      width="900px"
      :append-to="pageRef"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          字典数据详情
        </span>
      </template>
      <el-form :model="detailForm" label-width="80px" class="column-form">
        <qt-form-item label="字典类型">
          <div class="form-readonly">{{ detailForm.dictType || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="数据标签">
          <div class="form-readonly">{{ detailForm.dictLabel || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="数据键值">
          <div class="form-readonly">{{ detailForm.dictValue || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="样式属性">
          <div class="form-readonly">{{ detailForm.cssClass || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="显示排序">
          <div class="form-readonly">{{ detailForm.dictSort ?? "-" }}</div>
        </qt-form-item>
        <qt-form-item label="回显样式">
          <div class="form-readonly">{{ listClassLabel(detailForm.listClass) }}</div>
        </qt-form-item>
        <qt-form-item label="状态">
          <dict-tag
            :options="sys_normal_disable"
            :value="detailForm.status"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="备注" class="row-full">
          <div class="form-readonly textarea">
            {{ detailForm.remark || "-" }}
          </div>
        </qt-form-item>
        <qt-form-item label="创建时间">
          <div class="form-readonly">
            {{ parseTime(detailForm.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDetail">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Data">
import {
  computed,
  getCurrentInstance,
  onBeforeUnmount,
  reactive,
  ref,
  toRefs,
  watch
} from "vue";
import { useRoute } from "vue-router";

import {
  listData,
  getData,
  delData,
  addData,
  updateData
} from "@/api/system/system/dict/data.js";
import { getType } from "@/api/system/system/dict/type.js";
import useDictStore from "@/store/system/dict.js";
import { parseTime } from "@/utils/anivia.js";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");
const route = useRoute();

/**
 * 表格实例
 *
 * getList()     刷新当前页
 * resetQuery()  回第一页并重新查询
 */
const tableRef = ref(null);
const pageRef = ref();

const open = ref(false);
const openDetail = ref(false);
const detailForm = ref({});
const loading = ref(false);
const contextReady = ref(false);
const deleting = ref(false);
const ids = ref([]);
const multiple = ref(true);
const selectedRows = ref([]);
const title = ref("");
const detail = ref({});
const defaultDictType = ref("");
const emptyMessage = ref("字典类型已不存在");
const defaultSort = { prop: "createTime", order: "descending" };

// 数据标签回显样式
const listClassOptions = ref([
  { value: "default", label: "默认" },
  { value: "primary", label: "主要" },
  { value: "success", label: "成功" },
  { value: "info", label: "信息" },
  { value: "warning", label: "警告" },
  { value: "danger", label: "危险" }
]);

// 查询条件仅在点击查询或重置时提交，分页、排序与刷新不使用输入草稿。
const appliedFilters = reactive({ dictLabel: "", status: "" });

/**
 * 头部信息卡字段：自由布局完整声明字典类型概要字段，时间精确到分。
 */
const detailItems = computed(() => [
  { label: "字典名称", key: "dictName" },
  { label: "字典类型", key: "dictType" },
  { label: "创建人", key: "createBy" },
  {
    label: "备注",
    key: "remark",
    span: 24,
    className: "mt2 mb2",
    formatter: (value) => value?.trim() || "-"
  },
  {
    label: "创建时间",
    key: "createTime",
    type: "time",
    formatter: (d) => proxy.parseTime(d.createTime, "{y}-{m}-{d} {h}:{i}")
  },
  {
    label: "更新时间",
    key: "updateTime",
    type: "time",
    formatter: (d) => proxy.parseTime(d.updateTime, "{y}-{m}-{d} {h}:{i}")
  }
]);

/**
 * QtTable 统一配置：列宽双写，字典类型固定注入请求参数，取消排序时恢复默认创建时间倒序。
 */
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "dictCode",
      defaultSort,
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55, align: "center" },
    { label: "编号", prop: "dictCode", slot: "text", width: 80, minWidth: 80, sortable: true },
    {
      label: "字典标签",
      prop: "dictLabel",
      slot: "dictLabel",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "字典键值",
      prop: "dictValue",
      slot: "text",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "显示排序",
      prop: "dictSort",
      slot: "text",
      width: 100,
      minWidth: 100,
      sortable: true,
      hide: true
    },
    { label: "状态", prop: "status", slot: "status", width: 100, minWidth: 100 },
    {
      label: "备注",
      prop: "remark",
      slot: "text",
      minWidth: 150,
      align: "left",
      hide: true,
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "创建时间",
      prop: "createTime",
      slot: "createTime",
      width: 160,
      minWidth: 160,
      sortable: true
    },
    { label: "操作", slot: "handle", width: 240, minWidth: 240, fixed: "right" }
  ],
  func: loadDictData,
  params: { dictLabel: "", status: "" },
  events: {
    formatParams: formatListParams,
    formatData: formatListData,
    onDataLoaded: clearSelection
  }
});

/**
 * 查询栏配置：字典类型由当前详情上下文固定，不再提供切换下拉。
 */
const searchStore = reactive({
  items: [
    { label: "字典标签", prop: "dictLabel", component: { is: "input" } },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: sys_normal_disable }
    }
  ]
});

const data = reactive({
  form: {},
  rules: {
    dictLabel: [{ required: true, message: "数据标签不能为空", trigger: "blur" }],
    dictValue: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
    dictSort: [{ required: true, message: "数据顺序不能为空", trigger: "blur" }]
  }
});

const { form, rules } = toRefs(data);
let contextRequest = 0;
let typeRequest = 0;

/**
 * 提交搜索条件：将当前草稿写入 appliedFilters，公共搜索栏随后重置页码并加载表格。
 */
function applyFilters() {
  appliedFilters.dictLabel = (tableStore.params.dictLabel || "").trim();
  appliedFilters.status = tableStore.params.status ?? "";
}

/**
 * 格式化表格请求参数：固定当前字典类型，取消排序时恢复默认的创建时间倒序。
 *
 * @param {Object} params 表格组件传入的分页/排序/筛选参数
 * @returns {Object} 实际发送给后端的请求参数
 */
function formatListParams(params) {
  return {
    ...params,
    ...appliedFilters,
    dictType: defaultDictType.value,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order
  };
}

/**
 * 加载字典数据：无字典类型时返回空页，禁止接口因忽略空条件而查询其他字典的数据。
 *
 * @param {Object} params 已格式化的请求参数
 * @returns {Promise<{data: {rows: Array, total: number}}>} 表格组件要求的响应结构
 */
async function loadDictData(params) {
  if (!params.dictType) return { data: { rows: [], total: 0 } };
  const response = await listData(params);
  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 格式化表格行数据：统一公共表格和插槽的空值占位符，保留键值与排序值中的数值零。
 *
 * @param {Array<Object>} rows 接口返回的原始行数据
 * @returns {Array<Object>} 替换空值后的行数据副本
 */
function formatListData(rows) {
  const fields = tableStore.columns.map((column) => column.prop).filter(Boolean);
  return rows.map((row) => {
    const record = { ...row };
    fields.forEach((field) => {
      if (record[field] == null || record[field] === "") record[field] = "-";
    });
    return record;
  });
}

/**
 * 列表刷新后清空选中项，避免批量删除使用上一次查询的数据。
 */
function clearSelection() {
  handleSelectionChange([]);
  tableRef.value?.elTableRef?.clearSelection();
}

/**
 * 刷新表格当前页，新增/修改/删除成功后调用。
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 多选框选中数据，并控制批量删除按钮状态。
 *
 * @param {Array<Object>} selection 表格当前选中的行集合
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.dictCode);
  selectedRows.value = selection;
  multiple.value = !selection.length;
}

/**
 * 重置新增或修改表单，字典类型始终取自当前页面上下文。
 */
function reset() {
  form.value = {
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    cssClass: undefined,
    listClass: "default",
    dictSort: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dataRef");
}

/**
 * 取消按钮：关闭弹窗并重置表单。
 */
function cancel() {
  open.value = false;
  reset();
}

/**
 * 新增按钮操作，字典类型取当前详情上下文。
 */
function handleAdd() {
  if (!defaultDictType.value || !contextReady.value) return;
  reset();
  form.value.dictType = defaultDictType.value;
  title.value = "新增字典数据";
  open.value = true;
}

/**
 * 修改按钮操作，加载最新记录后打开弹窗。
 *
 * @param {Object} row 当前行数据，至少包含 dictCode
 */
function handleUpdate(row) {
  reset();
  getData(row.dictCode).then((response) => {
    if (!response.data) {
      proxy.$modal.msgWarning("记录已不存在，请刷新");
      return;
    }
    form.value = response.data;
    title.value = "修改字典数据";
    open.value = true;
  });
}

/**
 * 回显样式的只读文案：与新增表单下拉选项一致，找不到时回退为原值。
 *
 * @param {string} value 回显样式编码
 * @returns {string} 展示文案
 */
function listClassLabel(value) {
  const option = listClassOptions.value.find((item) => item.value === value);
  return option ? option.label : value || "-";
}

/**
 * 详情按钮操作：请求最新字典数据后打开只读详情弹窗。
 *
 * @param {Object} row 当前行数据，至少包含 dictCode
 */
function handleDetail(row) {
  getData(row.dictCode).then((response) => {
    if (!response.data) {
      proxy.$modal.msgWarning("记录已不存在，请刷新");
      return;
    }
    detailForm.value = response.data;
    openDetail.value = true;
  });
}

/**
 * 关闭详情弹窗并清空详情数据。
 */
function cancelDetail() {
  openDetail.value = false;
  detailForm.value = {};
}

/**
 * 校验并保存字典数据，成功后失效当前字典类型的本地缓存并刷新列表。
 */
function submitForm() {
  proxy.$refs["dataRef"].validate((valid) => {
    if (valid) {
      if (form.value.dictCode != undefined) {
        updateData(form.value).then(() => {
          useDictStore().removeDict(defaultDictType.value);
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addData(form.value).then(() => {
          useDictStore().removeDict(defaultDictType.value);
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/**
 * 确认并删除选中数据，仅在字典上下文未改变时刷新列表。
 * 传入行对象时为单条删除；批量按钮点击传入 MouseEvent 时按状态筛选可删除行。
 *
 * @param {Object|MouseEvent} [row] 当前行数据或按钮点击事件
 */
async function handleDelete(row) {
  const context = contextRequest;
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.dictCode 区分）
  if (row?.dictCode != null) {
    const recordIds = [row.dictCode];
    if (deleting.value) return;
    try {
      await proxy.$modal.confirm(
        '是否确认删除编号为"' + recordIds.join(",") + '"的数据项？'
      );
    } catch {
      return;
    }
    if (deleting.value || context !== contextRequest) return;
    deleting.value = true;
    try {
      await delData(recordIds.join(","));
      if (context !== contextRequest) return;
      useDictStore().removeDict(defaultDictType.value);
      // 删除后回到第一页，避免删除末页全部数据后保留空页。
      tableRef.value?.resetQuery();
      proxy.$modal.msgSuccess("删除成功");
    } finally {
      deleting.value = false;
    }
    return;
  }

  // 批量删除：根据状态（'0'=正常/启用不可删除，'1'=停用可删除）判断
  const rows = selectedRows.value;
  if (!rows.length || deleting.value) return;
  const deletableRows = rows.filter((item) => item.status === "1");
  const undeletableCount = rows.length - deletableRows.length;
  const recordIds = deletableRows.map((item) => item.dictCode);
  try {
    await proxy.$modal.confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    );
  } catch {
    return;
  }
  if (deleting.value || context !== contextRequest) return;
  if (!recordIds.length) {
    proxy.$modal.msgWarning("无可删除的数据");
    return;
  }
  deleting.value = true;
  try {
    await delData(recordIds.join(","));
    if (context !== contextRequest) return;
    useDictStore().removeDict(defaultDictType.value);
    tableRef.value?.resetQuery();
    proxy.$modal.msgSuccess("删除成功");
  } finally {
    deleting.value = false;
  }
}

/**
 * 状态开关：确认后只切换当前行状态，取消或失败时还原原值。
 *
 * @param {Object} row 当前行数据
 */
function handleStatusChange(row) {
  const text = row.status === "0" ? "启用" : "停用";
  proxy.$modal
    .confirm('确认要"' + text + '""' + row.dictLabel + '"字典数据吗？')
    .then(function () {
      return updateData(row);
    })
    .then(() => {
      useDictStore().removeDict(defaultDictType.value);
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === "0" ? "1" : "0";
    });
}

/**
 * 导出按钮操作，使用当前详情上下文与已提交的查询条件。
 */
function handleExport() {
  proxy.download(
    "system/dict/data/export",
    {
      ...appliedFilters,
      dictType: defaultDictType.value
    },
    `dict_data_${new Date().getTime()}.xlsx`
  );
}

/**
 * 关闭当前详情页签，返回字典管理列表。
 */
function handleClose() {
  proxy.$tab.closeOpenPage({ path: "/system/dict" });
}

/** 解析路由携带的字典类型 ID，加载头部信息并固定列表过滤上下文；切换时重置列表与弹窗状态。 */
watch(
  () => route.params.dictId,
  async (dictId) => {
    ++contextRequest;
    const request = ++typeRequest;
    loading.value = true;
    contextReady.value = false;
    detail.value = {};
    defaultDictType.value = "";
    emptyMessage.value = "字典类型已不存在";
    open.value = false;
    handleSelectionChange([]);
    tableStore.params.dictLabel = "";
    tableStore.params.status = "";
    appliedFilters.dictLabel = "";
    appliedFilters.status = "";
    try {
      if (dictId) {
        const response = await getType(dictId);
        if (request !== typeRequest) return;
        if (!response.data) return;
        detail.value = response.data;
        defaultDictType.value = response.data.dictType;
      }
    } catch {
      // 错误由全局请求层提示；保持空类型，避免继续展示上一个字典的数据。
      if (request === typeRequest) emptyMessage.value = "详情加载失败，请刷新重试";
    } finally {
      if (request === typeRequest) {
        contextReady.value = true;
        loading.value = false;
      }
    }
  },
  { immediate: true }
);

// 页面离开后使类型加载与删除确认上下文失效，避免销毁后继续回写页面状态。
onBeforeUnmount(() => {
  ++contextRequest;
  ++typeRequest;
});
</script>

<style scoped lang="scss">
// 列表内嵌于详情底部卡片时，搜索区沿用外层容器，不再保留独立间距和阴影。
.dict-data-detail-page :deep(.qt-wrap--search) {
  padding: 0;
}

// 显示排序输入框占满表单项列宽
.dict-sort-input {
  width: 100%;
}
</style>
