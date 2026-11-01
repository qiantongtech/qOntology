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
  <div ref="pageRef" class="app-container enum-page">
    <GuideTip tip-id="basic/enumeration.list" />
    <!-- 与项目列表统一：搜索、操作区和分页表格均交由公共列表组件管理。 -->
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
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
          v-hasPermi="['basic:enumeration:type:add']"
          type="primary"
          plain
          @click="handleAdd"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['basic:enumeration:type:remove']"
          type="danger"
          plain
          :disabled="multiple || deleting"
          @click="handleDelete()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>
      <qt-table v-bind="tableStore" ref="tableRef">
        <template #text="{ row, column_data }">
          {{ row[column_data.prop] || "-" }}
        </template>
        <!-- 名称与操作列共用详情入口，未获得查询权限时保留普通文本。 -->
        <template #enumName="{ row }">
          <el-link
            v-if="auth.hasPermi('basic:enumeration:type:query')"
            type="primary"
            :underline="false"
            @click="handleDetail(row)"
          >
            {{ row.enumName }}
          </el-link>
          <span v-else>{{ row.enumName }}</span>
        </template>
        <template #enumType="{ row }">
          <router-link
            v-if="auth.hasPermi('basic:enumeration:data:list')"
            :to="{ name: 'BasicEnumData', params: { enumId: row.enumId } }"
            class="link-type"
            >{{ row.enumType }}
          </router-link>
          <span v-else>{{ row.enumType }}</span>
        </template>
        <template #status="{ row }">
          <dict-tag
            v-if="row.status !== '—'"
            :options="sys_normal_disable"
            :value="row.status"
          />
          <span v-else>—</span>
        </template>
        <!-- 沿用项目责任人的双行样式，联系电话未维护时只显示创建人姓名。 -->
        <template #creator="{row}">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '—' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #createTime="{row}">
          {{ row.createTime === '—' ? '—' : (parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '—') }}
        </template>
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['basic:enumeration:type:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情
          </el-button>
          <el-button
            v-if="auth.hasPermi('basic:enumeration:type:edit')"
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(row)"
            >修改
          </el-button>
          <el-button
            v-if="auth.hasPermi('basic:enumeration:type:remove')"
            link
            type="danger"
            icon="Delete"
            :disabled="deleting"
            @click="handleDelete(row)"
            >删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>
    <!-- 新增和修改沿用项目页的列式表单，保存期间保留防重复提交及关闭保护。 -->
    <el-dialog
      v-model="open"
      :title="title"
      width="800px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      :close-on-press-escape="!saving"
      :before-close="handleBeforeClose"
      draggable
      @closed="clearValidation"
    >
      <el-form
        ref="typeRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
        @submit.prevent="submitForm"
      >
        <el-form-item label="枚举名称" prop="enumName" class="row-full">
          <el-input
            v-model="form.enumName"
            placeholder="请输入枚举名称"
            :maxlength="128"
          />
        </el-form-item>
        <el-form-item label="枚举类型" prop="enumType" class="row-full">
          <el-input
            v-model="form.enumType"
            placeholder="请输入枚举类型"
            :maxlength="128"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark" class="row-full">
          <el-input
            v-model="form.remark"
            class="character-count"
            type="textarea"
            :rows="3"
            maxlength="256 个字符"
            show-word-limit
            placeholder="请输入备注"
          />
        </el-form-item>
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
  </div>
</template>

<script setup name="OmEnumType">
import {onBeforeUnmount, ref, reactive, toRefs, nextTick, getCurrentInstance} from 'vue';
import {listType, getType, addType, updateType, delType} from '@/api/basic/enumeration/type.js';
import auth from '@/plugins/auth.js';
import {useCreatorPhones} from '@/utils/useCreatorPhones';
import {useRouter} from 'vue-router';

const { proxy } = getCurrentInstance();
const router = useRouter();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");
const pageRef = ref();
const tableRef = ref();
const typeRef = ref();
const {creatorPhones} = useCreatorPhones();

const open = ref(false);
const saving = ref(false);
const deleting = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");
const defaultSort = { prop: "createTime", order: "descending" };
// 搜索表单保存待提交输入，实际请求始终使用已提交条件，避免分页或刷新意外应用草稿。
const appliedFilters = reactive({ enumName: "", enumType: "", status: "" });

// 统一表格配置：公共组件管理分页、排序和列显隐，业务页保留权限及枚举专属展示。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "enumId",
      defaultSort,
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55 },
    { label: "编号", prop: "enumId", slot: "text", width: 80, sortable: true },
    {
      label: "枚举名称",
      prop: "enumName",
      slot: "enumName",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "枚举类型",
      prop: "enumType",
      slot: "enumType",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "状态",
      prop: "status",
      slot: "status",
      width: 100,
      align: "center",
    },
    {
      label: "备注",
      prop: "remark",
      slot: "text",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    // 引用数由后端按业务数据类型关联键聚合返回，不随当前页的枚举行数变化。
    {label: '引用数', prop: 'referenceCount', slot: 'text', width: 100},
    {label: '创建人', prop: 'createBy', slot: 'creator', width: 130, align: 'left', headerAlign: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '创建时间', prop: 'createTime', slot: 'createTime', width: 160, sortable: true},
    {label: '操作', slot: 'handle', width: 200, fixed: 'right'}
  ],
  func: listType,
  params: { enumName: "", enumType: "", status: "" },
  events: {
    formatParams: formatListParams,
    formatData: formatListData,
    onDataLoaded: clearSelection,
  },
});

// 与项目页使用相同的配置式搜索栏，状态选项沿用枚举类型的正常/停用字典。
const searchStore = reactive({
  items: [
    { label: "枚举名称", prop: "enumName", component: { is: "input" } },
    { label: "枚举类型", prop: "enumType", component: { is: "input" } },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: sys_normal_disable },
    },
  ],
});

const data = reactive({
  form: {},
  rules: {
    enumName: [
      {
        required: true,
        whitespace: true,
        message: "枚举名称不能为空",
        trigger: "blur",
      },
      { max: 128, message: "枚举名称不能超过128个字符", trigger: "blur" },
    ],
    enumType: [
      {
        required: true,
        whitespace: true,
        message: "枚举类型不能为空",
        trigger: "blur",
      },
      { max: 128, message: "枚举类型不能超过128个字符", trigger: "blur" },
      {
        pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
        message: "须以字母开头，仅允许字母、数字和下划线",
        trigger: "blur",
      },
    ],
    status: [{ required: true, message: "请选择状态", trigger: "change" }],
    remark: [{ max: 512, message: "备注不能超过512个字符", trigger: "blur" }],
  },
});
const { form, rules } = toRefs(data);
let contextRequest = 0;
let detailRequest = 0;

/** 提交搜索草稿；QtSearchBar 随后统一重置页码并发起请求。 */
function applyFilters() {
  appliedFilters.enumName = (tableStore.params.enumName || "").trim();
  appliedFilters.enumType = (tableStore.params.enumType || "").trim();
  appliedFilters.status = tableStore.params.status ?? "";
}

/** 沿用已提交筛选，并在取消排序时恢复默认的创建时间倒序。 */
function formatListParams(params) {
  return {
    ...params,
    ...appliedFilters,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order,
  };
}

/** 统一文本占位符，保证公共表格与自定义插槽使用相同的非空展示值，同时保留数值零。 */
function formatListData(rows) {
  const fields = tableStore.columns
    .map((column) => column.prop)
    .filter(Boolean);
  return rows.map((row) => {
    const record = { ...row };
    fields.forEach((field) => {
      if (record[field] == null || record[field] === "") record[field] = "—";
    });
    return record;
  });
}

/** 刷新后清理旧行选中状态，避免批量删除使用上一次查询的编号。 */
function clearSelection() {
  handleSelectionChange([]);
  tableRef.value?.elTableRef?.clearSelection();
}

/** 重置新增或修改表单，并清除旧的校验结果。 */
function reset() {
  form.value = {
    enumId: undefined,
    enumName: "",
    enumType: "",
    status: "0",
    remark: "",
  };
  clearValidation();
}

/** 清除表单校验提示，供弹窗关闭和重新打开时使用。 */
function clearValidation() {
  typeRef.value?.clearValidate();
}

/** 保存期间阻止关闭弹窗，避免重复操作或丢失提交状态。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 未保存时关闭弹窗并还原表单。 */
function cancel() {
  if (saving.value) return;
  open.value = false;
  reset();
}

/** 同步当前勾选的枚举编号，并控制批量删除按钮是否可用。 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.enumId);
  multiple.value = !selection.length;
}

/** 打开新增弹窗，同时使先前尚未完成的修改详情请求失效。 */
async function handleAdd() {
  ++detailRequest;
  reset();
  title.value = "新增枚举类型";
  open.value = true;
  await nextTick();
  clearValidation();
}

/** 加载选中枚举后打开修改弹窗，忽略较早操作的迟到响应。 */
async function handleUpdate(row) {
  const request = ++detailRequest;
  const response = await getType(row?.enumId ?? ids.value[0]);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning("记录已不存在，请刷新");
    return;
  }
  reset();
  form.value = { ...form.value, ...response.data };
  title.value = "修改枚举类型";
  open.value = true;
  await nextTick();
  clearValidation();
}

/** 携带枚举编号打开独立详情页，由详情页统一加载概要及关联列表。 */
function handleDetail(row) {
  router.push({ name: "BasicEnumDetail", params: { enumId: row.enumId } });
}

/** 校验并保存枚举类型，成功后通过公共表格刷新当前列表。 */
async function submitForm() {
  if (saving.value) return;
  const valid = await typeRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    const payload = {
      enumName: form.value.enumName.trim(),
      enumType: form.value.enumType.trim(),
      status: form.value.status,
      remark: (form.value.remark || "").trim(),
    };
    if (form.value.enumId != null) {
      payload.enumId = form.value.enumId;
      await updateType(payload);
    } else {
      await addType(payload);
    }
    proxy.$modal.msgSuccess(
      form.value.enumId != null ? "修改成功" : "新增成功"
    );
    open.value = false;
    tableRef.value?.getList();
  } finally {
    saving.value = false;
  }
}

/** 删除单行或勾选记录；成功后回到第一页，避免删除末页全部记录后出现空页。 */
async function handleDelete(row) {
  const context = contextRequest;
  const recordIds = row ? [row.enumId] : [...ids.value];
  if (!recordIds.length || deleting.value) return;
  try {
    await proxy.$modal.confirm(
      '是否确认删除枚举编号为"' + recordIds.join(",") + '"的数据项？'
    );
  } catch {
    return;
  }
  if (deleting.value || context !== contextRequest) return;
  deleting.value = true;
  try {
    await delType(recordIds.join(","));
    if (context !== contextRequest) return;
    tableRef.value?.resetQuery();
    proxy.$modal.msgSuccess("删除成功");
  } finally {
    deleting.value = false;
  }
}

// 公共表格自行加载首屏；离开页面后，使删除确认上下文及修改详情请求失效。
onBeforeUnmount(() => {
  ++contextRequest;
  ++detailRequest;
});
</script>
