<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

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
  <div ref="pageRef" class="candidate-tab-page">
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="wrapConfig"
    >
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          :disabled="!selectedIds.length"
          @click="adoptBatch"
          @mousedown="(e) => e.preventDefault()"
        >
          <template #icon>
            <svg-icon icon-class="adopt" />
          </template>
          批量采纳
        </el-button>
        <el-button
          type="danger"
          icon="Delete"
          plain
          :loading="batchDeleteLoading"
          :disabled="!selectedIds.length || deleting"
          @click="remove()"
          @mousedown="(e) => e.preventDefault()"
        >
          批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 所属对象类型存的是候选编号，需要换成名称展示。 -->
        <template #objectType="{ row }">{{
          objectName(row.objectCandidateId)
        }}</template>
        <template #dataType="{ row }">
          <el-tag
            :type="row.typeCategory === 2 ? 'success' : 'primary'"
            effect="light"
            >{{ dataTypeLabel(row.typeCategory) }}</el-tag
          >
        </template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
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
        <template #adoptStatus="{ row }">
          <dict-tag :options="om_ai_adopt_status" :value="row.adoptStatus" />
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            :disabled="row.adoptStatus === 1"
            @click="adoptOne(row)"
            >
            <template #icon>
              <svg-icon icon-class="adopt" />
            </template>
            采纳
          </el-button>
          <el-button
            link
            type="primary"
            icon="Document"
            @click="showEvidence(row)"
            >来源</el-button
          >
          <el-popover
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              link
              type="primary"
              icon="View"
              @click="showDetail(row)"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="edit(row)"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              :loading="deleteLoading[row.id]"
              :disabled="row.adoptStatus === 1 || deleting"
              @click="remove(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 修改属性对话框 -->
    <el-dialog
      v-model="open"
      title="修改属性"
      width="800px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      :close-on-press-escape="!saving"
      draggable
      destroy-on-close
      @closed="formRef?.clearValidate()"
    >
      <el-form
        ref="formRef"
        class="attribute-dialog-form"
        :model="form"
        :rules="rules"
        label-width="110px"
      >
        <div class="h2-title">基础信息</div>
        <el-form-item label="所属对象类型" prop="objectCandidateId">
          <el-select
            v-model="form.objectCandidateId"
            placeholder="请选择所属对象类型"
            style="width: 100%"
            @change="loadTypeOptions"
          >
            <el-option
              v-for="item in objects"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="属性名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入属性名称"
            :maxlength="128"
          />
        </el-form-item>
        <el-form-item label="属性代码" prop="code">
          <el-input
            v-model="form.code"
            placeholder="例如 purchase_amount"
            :maxlength="128"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            :maxlength="256"
            show-word-limit
          />
        </el-form-item>
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型" prop="typeCategory">
          <el-radio-group v-model="form.typeCategory" @change="changeTypeCategory">
            <el-radio
              v-for="option in dataTypeOptions"
              :key="option.value"
              :value="option.value"
              >{{ option.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="form.typeCategory === 1"
          label="基础数据类型"
          prop="basicTypeCode"
        >
          <el-select
            v-model="form.basicTypeCode"
            placeholder="请选择基础数据类型"
            style="width: 100%"
            clearable
            @change="clearTypeConfig"
          >
            <el-option
              v-for="item in basic_data_type"
              :key="item.value"
              :label="item.label"
              :value="Number(item.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-else label="业务数据类型" prop="businessTypeId">
          <el-select
            v-model="businessKey"
            placeholder="请选择业务数据类型"
            style="width: 100%"
            filterable
            clearable
          >
            <template #label>
              <span class="business-type-label">
                {{ selectedBusinessType?.name || "-" }}
                <el-tag
                  :type="
                    form.businessTypeSource === 'PROJECT' ? 'primary' : 'info'
                  "
                  size="small"
                  >{{
                    form.businessTypeSource === "PROJECT" ? "当前项目" : "全局"
                  }}</el-tag
                >
              </span>
            </template>
            <el-option-group
              v-for="group in businessTypeGroups"
              :key="group.source"
              :label="group.label"
            >
              <el-option
                v-for="item in group.options"
                :key="group.source + ':' + item.id"
                :label="item.name"
                :value="group.source + ':' + item.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <template v-if="form.typeCategory === 2 && form.businessTypeId">
          <el-form-item label="基础数据类型">
            <div class="form-readonly">
              {{ basicTypeLabel(effectiveConfig.basicTypeCode) }}
            </div>
          </el-form-item>
        </template>
        <AttributeTypeConfig
          v-if="form.typeCategory === 1 || form.businessTypeId"
          :model="effectiveConfig"
          :readonly="form.typeCategory === 2"
          :enum-options="enumOptions"
          :enum-loading="enumsLoading"
          :enum-error="enumsError"
          @enum-open="loadEnums"
        />
        <el-form-item
          v-if="form.typeCategory === 2 && selectedBusinessType?.description"
          label="类型描述"
        >
          <div class="form-readonly textarea">
            {{ selectedBusinessType.description }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="open = false">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="save"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 属性详情对话框 -->
    <el-dialog
      v-model="detailOpen"
      title="属性详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form class="attribute-dialog-form" label-width="110px">
        <div class="h2-title">基础信息</div>
        <el-form-item label="所属对象类型">
          <div class="form-readonly">
            {{ objectName(detail.objectCandidateId) }}
          </div>
        </el-form-item>
        <el-form-item label="属性名称">
          <div class="form-readonly">
            {{ detail.name || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="属性代码">
          <div class="form-readonly">
            {{ detail.code || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <div class="form-readonly textarea">
            {{ detail.description || "-" }}
          </div>
        </el-form-item>
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型">
          <div class="form-readonly">
            {{ dataTypeLabel(detail.typeCategory) }}
          </div>
        </el-form-item>
        <el-form-item label="类型配置">
          <div class="form-readonly">
            {{ typeSummary(detail) }}
          </div>
        </el-form-item>
        <el-form-item label="创建人">
          <div class="form-readonly">
            {{ detail.createBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="联系方式">
          <div class="form-readonly">
            {{ (detail.creatorId != null && creatorPhones[String(detail.creatorId)]) ? creatorPhones[String(detail.creatorId)] : "-" }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间">
          <div class="form-readonly">
            {{ detail.createTime || "-" }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import {
  listAiAttributeBusinessTypes,
  listAiAttributeEnums,
  listCandidates,
  updateCandidate,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import AttributeTypeConfig from "../../om/object/type/detail/AttributeTypeConfig.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, om_attribute_data_type, basic_data_type } =
  proxy.useDict(
    "om_ai_adopt_status",
    "om_attribute_data_type",
    "basic_data_type"
  );

const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const detailOpen = ref(false);
const saving = ref(false);
const form = ref({});
const detail = ref({});
const objects = ref([]);
const businessTypes = ref([]);
const enumOptions = ref([]);
const enumsLoading = ref(false);
const enumsError = ref("");

/**
 * 收集采纳前缺失的必填项名称，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
function required(row) {
  const missing = [];
  if (!row.objectCandidateId) missing.push("所属对象类型");
  if (!row.name?.trim()) missing.push("属性名称");
  if (!row.code?.trim()) missing.push("属性代码");
  if (![1, 2].includes(Number(row.typeCategory))) missing.push("数据类型");
  if (Number(row.typeCategory) === 1 && row.basicTypeCode == null)
    missing.push("基础数据类型");
  if (Number(row.typeCategory) === 2 && !row.businessTypeId)
    missing.push("业务数据类型");
  return missing;
}

const tab = useCandidateTab("attribute", props.taskId, required);
const {
  tableRef,
  fetchRows,
  selectedIds,
  evidenceVisible,
  evidences,
  refresh,
  select: handleSelectionChange,
  adoptOne,
  adoptBatch,
  deleting,
  deleteLoading,
  batchDeleteLoading,
  remove,
  showEvidence,
} = tab;

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 搜索区由 QtWrap 管理，复用操作栏的搜索显隐按钮。
 */
// 候选列表搜索：字段与接口契约一致，查询和重置由 QtSearchBar 回到第一页。
const searchStore = reactive({
  items: [
    { label: "属性名称", prop: "name", component: { is: "input" } },
    { label: "采纳状态", prop: "adoptStatus", component: { is: "select", options: om_ai_adopt_status } },
  ],
});

const wrapConfig = {
  fullContent: false,
  searchShadow: false,
};

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  params: { name: null, adoptStatus: null },
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
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center",
      selectable: (row) => row.adoptStatus !== 1,
    },
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
    },
    {
      label: "属性名称",
      prop: "name",
      slot: "text",
      width: 240,
      minWidth: 240,
      align: "left",
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
      label: "所属对象类型",
      prop: "objectCandidateId",
      slot: "objectType",
      width: 180,
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 260,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "数据类型",
      prop: "typeCategory",
      slot: "dataType",
      width: 150,
      minWidth: 150,
      align: "center",
    },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      slot: "adoptStatus",
      width: 110,
      minWidth: 110,
      align: "center",
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 120,
      minWidth: 120,
      align: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 180,
      minWidth: 180,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 240,
      minWidth: 240,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
});

const dataTypeOptions = computed(() =>
  om_attribute_data_type.value.map((item) => ({
    label: item.label,
    value: Number(item.value),
  }))
);
const businessTypeGroups = computed(() =>
  [
    { source: "PROJECT", label: "当前项目" },
    { source: "GLOBAL", label: "全局" },
  ]
    .map((group) => ({
      ...group,
      options: businessTypes.value.filter(
        (item) => item.businessTypeSource === group.source
      ),
    }))
    .filter((group) => group.options.length)
);
const selectedBusinessType = computed(() =>
  businessTypes.value.find(
    (item) =>
      String(item.id) === String(form.value.businessTypeId) &&
      item.businessTypeSource === (form.value.businessTypeSource || "GLOBAL")
  )
);
const effectiveConfig = computed(() =>
  form.value.typeCategory === 2 && selectedBusinessType.value
    ? {
        ...selectedBusinessType.value,
        basicTypeCode: Number(selectedBusinessType.value.dataType),
      }
    : form.value
);
const businessKey = computed({
  get: () =>
    form.value.businessTypeId
      ? `${form.value.businessTypeSource || "GLOBAL"}:${
          form.value.businessTypeId
        }`
      : undefined,
  set: (value) => {
    const parts = (value || "").split(":");
    form.value.businessTypeSource = parts[0] || "GLOBAL";
    form.value.businessTypeId = parts[1] || undefined;
  },
});

const rules = {
  objectCandidateId: [
    { required: true, message: "请选择所属对象类型", trigger: "change" },
  ],
  name: [
    {
      required: true,
      whitespace: true,
      message: "属性名称不能为空",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "属性代码不能为空", trigger: "blur" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "须以字母开头，仅允许字母、数字和下划线",
      trigger: "blur",
    },
  ],
  typeCategory: [
    { required: true, message: "请选择数据类型", trigger: "change" },
  ],
  basicTypeCode: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.typeCategory === 1 && value == null
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
          form.value.typeCategory === 2 && !value
            ? new Error("请选择业务数据类型")
            : undefined
        ),
      trigger: "change",
    },
  ],
};

/**
 * 按候选编号取对象类型名称。
 * @param {number|string} id 对象候选编号
 * @returns {string} 对象类型名称，找不到时返回占位符
 */
const objectName = (id) =>
  objects.value.find((item) => item.id === id)?.name || "-";

/**
 * 取数据类型字典文案。
 * @param {number|string} value 数据类型分类
 * @returns {string} 字典标签
 */
const dataTypeLabel = (value) =>
  dataTypeOptions.value.find((item) => item.value === Number(value))?.label ||
  "-";

/**
 * 取基础数据类型字典文案。
 * @param {number|string} value 基础数据类型编码
 * @returns {string} 字典标签
 */
const basicTypeLabel = (value) =>
  basic_data_type.value.find((item) => Number(item.value) === Number(value))
    ?.label || "-";

/**
 * 汇总类型配置，业务数据类型展示名称与来源。
 * @param {Object} row 当前行数据
 * @returns {string} 类型配置摘要
 */
const typeSummary = (row) => {
  if (Number(row.typeCategory) !== 2) return basicTypeLabel(row.basicTypeCode);
  const item = businessTypes.value.find(
    (type) =>
      String(type.id) === String(row.businessTypeId) &&
      type.businessTypeSource === (row.businessTypeSource || "GLOBAL")
  );
  return item
    ? `${item.name}（${
        item.businessTypeSource === "PROJECT" ? "当前项目" : "全局"
      }）`
    : "-";
};

/**
 * 把后端返回的字符串数字统一成表单控件需要的类型。
 * @param {Object} row 原始行数据
 * @returns {Object} 可直接绑定表单的数据
 */
function normalize(row) {
  return {
    ...row,
    typeCategory:
      row.typeCategory == null ? undefined : Number(row.typeCategory),
    basicTypeCode:
      row.basicTypeCode == null ? undefined : Number(row.basicTypeCode),
    decimalPlaces: row.decimalPlaces == null ? 2 : Number(row.decimalPlaces),
    businessTypeId:
      row.businessTypeId == null ? undefined : String(row.businessTypeId),
    businessTypeSource: row.businessTypeSource || "GLOBAL",
  };
}

/** 清空基础数据类型下的附加配置。 */
function clearTypeConfig() {
  Object.assign(form.value, {
    unit: "",
    decimalPlaces: 2,
    format: "",
    enumType: undefined,
    enumSource: "PROJECT",
  });
}

/** 切换数据类型分类时重置类型与配置项。 */
function changeTypeCategory() {
  Object.assign(form.value, {
    basicTypeCode: undefined,
    businessTypeId: undefined,
    businessTypeSource: "GLOBAL",
  });
  clearTypeConfig();
  nextTick(() => formRef.value?.clearValidate());
}

/** 加载对象类型候选，供所属对象类型回显和下拉选择使用。 */
async function loadObjects() {
  const response = await listCandidates("object", {
    taskId: props.taskId,
    pageNum: 1,
    pageSize: 1000,
  });
  objects.value = response.data?.rows || [];
}

/** 加载当前任务可选的业务数据类型（项目级 + 全局）。 */
async function loadTypeOptions() {
  businessTypes.value = [];
  enumOptions.value = [];
  await Promise.allSettled(
    ["PROJECT", "GLOBAL"].map(async (source) => {
      const response = await listAiAttributeBusinessTypes(props.taskId, source);
      businessTypes.value.push(
        ...(response.data || []).map((item) => ({
          ...item,
          id: String(item.id),
          businessTypeSource: source,
        }))
      );
    })
  );
}

/** 加载枚举候选，失败时只提示不阻断表单。 */
async function loadEnums() {
  if (enumsLoading.value) return;
  enumsLoading.value = true;
  enumsError.value = "";
  try {
    enumOptions.value = [];
    await Promise.allSettled(
      ["PROJECT", "GLOBAL"].map(async (source) => {
        try {
          const response = await listAiAttributeEnums(props.taskId, source);
          enumOptions.value.push(
            ...(response.data || []).map((item) => ({
              ...item,
              enumSource: source,
            }))
          );
        } catch {
          enumsError.value = "部分枚举加载失败，可重新展开重试";
        }
      })
    );
  } finally {
    enumsLoading.value = false;
  }
}

/**
 * 打开修改弹窗并加载类型可选项。
 * @param {Object} row 当前行数据
 */
async function edit(row) {
  form.value = normalize(row);
  open.value = true;
  await loadTypeOptions();
  await loadEnums();
  nextTick(() => formRef.value?.clearValidate());
}

/**
 * 打开详情弹窗。
 * @param {Object} row 当前行数据
 */
function showDetail(row) {
  detail.value = normalize(row);
  detailOpen.value = true;
}

/** 提交修改，提交期间锁定确定按钮避免重复入库。 */
async function save() {
  if (saving.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    await updateCandidate("attribute", {
      ...form.value,
      taskId: props.taskId,
      businessTypeId:
        form.value.typeCategory === 2
          ? Number(form.value.businessTypeId)
          : null,
      businessTypeSource:
        form.value.typeCategory === 2 ? form.value.businessTypeSource : null,
      basicTypeCode:
        form.value.typeCategory === 1 ? form.value.basicTypeCode : null,
    });
    proxy.$modal.msgSuccess("修改成功");
    open.value = false;
    refresh();
  } finally {
    saving.value = false;
  }
}

loadObjects();
</script>

<style scoped>
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

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
