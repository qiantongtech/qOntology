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
  <div ref="pageRef" class="candidate-tab-page enum-page">
    <!-- 枚举类型列表 -->
    <qt-wrap
      v-if="!currentType"
      :columns="typeTableStore.columns"
      :table-ref="tableRef"
      :config="wrapConfig"
    >
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="typeTableStore.params"
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

      <qt-table v-bind="typeTableStore" ref="tableRef">
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 枚举类型列可点击下钻到该类型的枚举数据。 -->
        <template #enumType="{ row }">
          <el-button link type="primary" @click="openValues(row)">{{
            row.enumType || "-"
          }}</el-button>
        </template>
        <!-- 创建时间与外部枚举管理一致，格式到分钟。 -->
        <template #createTime="{ row }">{{
          proxy.parseTime(row.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
        }}</template>
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
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status || '0'" />
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
              @click="showTypeDetail(row)"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="editType(row)"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              :loading="deleteLoading[row.id]"
              :disabled="deleting"
              @click="remove(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 枚举数据列表 -->
    <qt-wrap
      v-else
      :columns="valueTableStore.columns"
      :table-ref="valueTableRef"
      :config="wrapConfig"
    >
      <template #search>
        <qt-search-bar
          :items="valueSearchStore.items"
          :params="valueTableStore.params"
          :table-ref="valueTableRef"
        />
      </template>

      <template #actions-data>
        <el-button
          type="danger"
          plain
          :loading="valueBatchDeleteLoading"
          :disabled="!valueSelectedIds.length || valueDeleting"
          @click="removeValue()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
        <el-button type="warning" plain icon="Close" @click="closeValues"
          >关闭</el-button
        >
      </template>

      <qt-table v-bind="valueTableStore" ref="valueTableRef" :key="currentType.id">
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
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
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status || '0'" />
        </template>
        <template #adoptStatus="{ row }">
          <dict-tag :options="om_ai_adopt_status" :value="row.adoptStatus" />
        </template>
        <template #valueHandle="{ row }">
          <el-button
            link
            type="primary"
            icon="Document"
            @click="showValueEvidence(row)"
            >来源</el-button
          >
          <el-button
            link
            type="primary"
            icon="View"
            @click="showValueDetail(row)"
            >详情</el-button
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
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="editValue(row)"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              :loading="valueDeleteLoading[row.id]"
              :disabled="valueDeleting"
              @click="removeValue(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <el-dialog
      v-model="typeOpen"
      :title="typeForm.id ? '修改枚举类型' : '新增枚举类型'"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="定义业务字段的可选值范围，填写枚举名称、唯一编码和使用说明，为业务数据类型提供统一的取值依据。"
      />
      <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="90px"
        class="column-form"
        @submit.prevent="saveType"
      >
        <qt-form-item
          label="枚举名称"
          prop="enumName"
          class="row-full"
          tip="填写枚举定义的业务名称，例如“供应商类别”。"
        >
          <el-input v-model="typeForm.enumName" placeholder="请输入枚举名称" :maxlength="128" />
        </qt-form-item>
        <qt-form-item
          label="枚举类型"
          prop="enumType"
          class="row-full"
          tip="填写唯一的英文编码，以字母开头，仅支持字母、数字和下划线。"
        >
          <el-input v-model="typeForm.enumType" placeholder="请输入枚举类型" :maxlength="128" />
        </qt-form-item>
        <qt-form-item
          label="状态"
          prop="status"
          class="row-full"
          tip="启用后可用于业务取值，禁用后保留已有定义。"
        >
          <el-radio-group v-model="typeForm.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item
          label="备注"
          prop="remark"
          class="row-full"
          tip="补充取值含义、适用场景或使用约定，最多256个字符。"
        >
          <el-input v-model="typeForm.remark" type="textarea" :rows="3" :maxlength="256" show-word-limit placeholder="请输入备注" />
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="typeOpen = false">取 消</el-button>
          <el-button type="primary" @click="saveType">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="typeDetailOpen"
      title="枚举类型详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><DetailForm
        :row="typeDetail"
        :fields="typeDetailFields"
        :status-options="sys_normal_disable"
      /><template #footer
        ><el-button @click="typeDetailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <el-dialog
      v-model="valueOpen"
      :title="valueForm.id ? '修改枚举数据' : '新增枚举数据'"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form
        ref="valueFormRef"
        :model="valueForm"
        :rules="valueRules"
        label-width="90px"
        ><el-form-item label="枚举类型"
          ><el-input
            :model-value="currentType?.enumType || '-'"
            disabled /></el-form-item
        ><el-form-item label="枚举标签" prop="enumLabel"
          ><el-input
            v-model="valueForm.enumLabel"
            placeholder="请输入枚举标签"
            :maxlength="128" /></el-form-item
        ><el-form-item label="枚举键值" prop="enumValue"
          ><el-input
            v-model="valueForm.enumValue"
            placeholder="请输入枚举键值"
            :maxlength="128" /></el-form-item
        ><el-form-item label="排序" prop="enumSort"
          ><el-input-number
            v-model="valueForm.enumSort"
            :min="0"
            controls-position="right" /></el-form-item
        ><el-form-item label="状态" prop="status"
          ><el-radio-group v-model="valueForm.status"
            ><el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            ></el-radio-group
          ></el-form-item
        ><el-form-item label="备注" prop="remark"
          ><el-input
            v-model="valueForm.remark"
            type="textarea"
            :rows="3"
            :maxlength="256"
            show-word-limit
            placeholder="请输入备注" /></el-form-item></el-form
      ><template #footer
        ><div class="dialog-footer">
          <el-button @click="valueOpen = false">取 消</el-button
          ><el-button type="primary" @click="saveValue">确 定</el-button>
        </div></template
      ></el-dialog
    >
    <el-dialog
      v-model="valueDetailOpen"
      title="枚举数据详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><DetailForm
        :row="valueDetail"
        :fields="valueDetailFields"
        :status-options="sys_normal_disable"
      /><template #footer
        ><el-button @click="valueDetailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <CandidateEvidenceDrawer
      v-model="evidenceVisible"
      :evidences="evidences"
    /><CandidateEvidenceDrawer
      v-model="valueEvidenceVisible"
      :evidences="valueEvidences"
    />
  </div>
</template>

<script setup>
import {
  createCandidate,
  getCandidateEvidence,
  listCandidates,
  updateCandidate,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import DetailForm from "../components/EnumCandidateDetailForm.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, sys_normal_disable } = proxy.useDict(
  "om_ai_adopt_status",
  "sys_normal_disable"
);
const pageRef = ref();
const typeFormRef = ref();
const valueFormRef = ref();
const { creatorPhones } = useCreatorPhones();
const currentType = ref(null);
const typeOpen = ref(false);
const typeDetailOpen = ref(false);
const valueOpen = ref(false);
const valueDetailOpen = ref(false);
const typeForm = ref({});
const typeDetail = ref({});
const valueForm = ref({});
const valueDetail = ref({});
// 枚举数据复用候选批量删除逻辑，确认提示和加载时机与各页签一致。
const {
  tableRef: valueTableRef,
  selectedIds: valueSelectedIds,
  deleting: valueDeleting,
  deleteLoading: valueDeleteLoading,
  batchDeleteLoading: valueBatchDeleteLoading,
  select: selectValues,
  remove: removeValue,
} = useCandidateTab("enum_data", props.taskId);
const valueEvidenceVisible = ref(false);
const valueEvidences = ref([]);

/**
 * 枚举类型采纳前的必填项校验，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
const required = (row) =>
  [
    ["enumName", "枚举名称"],
    ["enumType", "枚举类型"],
    ["status", "状态"],
  ]
    .filter(([key]) => row[key] == null || row[key] === "")
    .map(([, label]) => label);

const tab = useCandidateTab("enum_type", props.taskId, required);
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
    { label: "枚举名称", prop: "enumName", component: { is: "input" } },
    { label: "枚举类型", prop: "enumType", component: { is: "input" } },
    { label: "状态", prop: "status", component: { is: "select", options: sys_normal_disable } },
    { label: "采纳状态", prop: "adoptStatus", component: { is: "select", options: om_ai_adopt_status } },
  ],
});

// 候选列表搜索：字段与接口契约一致，查询和重置由 QtSearchBar 回到第一页。
const valueSearchStore = reactive({
  items: [
    { label: "枚举标签", prop: "enumLabel", component: { is: "input" } },
    { label: "状态", prop: "status", component: { is: "select", options: sys_normal_disable } },
    { label: "采纳状态", prop: "adoptStatus", component: { is: "select", options: om_ai_adopt_status } },
  ],
});

const wrapConfig = {
  fullContent: false,
  searchShadow: false,
};

// 枚举类型表格配置。
const typeTableStore = reactive({
  params: { enumName: null, enumType: null, status: null, adoptStatus: null },
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
      selectable: () => true,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 80,
      minWidth: 80,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "枚举名称",
      prop: "enumName",
      slot: "text",
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
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 256,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "状态",
      prop: "status",
      slot: "status",
      width: 100,
      minWidth: 100,
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
      width: 130,
      minWidth: 130,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      slot: "createTime",
      width: 160,
      minWidth: 160,
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

/**
 * 枚举数据取数：强制携带当前枚举类型编号，切换类型时由 :key 触发重新挂载首查。
 * @param {Object} params QtTable 合并分页、排序后的查询参数
 * @returns {Promise<Object>} 枚举数据列表接口返回体
 */
function fetchValues(params) {
  return listCandidates("enum_data", {
    ...params,
    taskId: props.taskId,
    enumTypeId: currentType.value.id,
  });
}

/**
 * 记录枚举数据勾选的可删除行编号。
 * @param {Array<Object>} selection 当前勾选行
 */
function handleValueSelection(selection) {
  selectValues(selection);
}

// 枚举数据表格配置。
const valueTableStore = reactive({
  params: { enumLabel: null, status: null, adoptStatus: null },
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleValueSelection,
    },
  },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center",
      selectable: () => true,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 80,
      minWidth: 80,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "枚举标签",
      prop: "enumLabel",
      slot: "text",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "枚举键值",
      prop: "enumValue",
      slot: "text",
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "排序",
      prop: "enumSort",
      slot: "text",
      width: 100,
      minWidth: 100,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "状态",
      prop: "status",
      slot: "status",
      width: 100,
      minWidth: 100,
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
      headerAlign: "left",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 170,
      minWidth: 170,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "valueHandle",
      width: 200,
      minWidth: 200,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchValues,
});

const typeRules = {
  enumName: [{ required: true, message: "枚举名称不能为空" }],
  enumType: [
    { required: true, message: "枚举类型不能为空" },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: "须以字母开头，仅允许字母、数字和下划线",
    },
  ],
  status: [{ required: true, message: "请选择状态" }],
};
const valueRules = {
  enumLabel: [{ required: true, message: "枚举标签不能为空" }],
  enumValue: [{ required: true, message: "枚举键值不能为空" }],
  enumSort: [{ required: true, message: "排序不能为空" }],
  status: [{ required: true, message: "请选择状态" }],
};
const typeDetailFields = [
  { prop: "id", label: "编号" },
  { prop: "enumName", label: "枚举名称" },
  { prop: "enumType", label: "枚举类型" },
  { prop: "status", label: "状态" },
  { prop: "createBy", label: "创建人" },
  { prop: "contact", label: "联系方式" },
  { prop: "createTime", label: "创建时间" },
  { prop: "remark", label: "备注" },
];
const valueDetailFields = [
  { prop: "id", label: "编号" },
  { prop: "enumType", label: "枚举类型" },
  { prop: "enumLabel", label: "枚举标签" },
  { prop: "enumValue", label: "枚举键值" },
  { prop: "enumSort", label: "排序" },
  { prop: "status", label: "状态" },
  { prop: "createBy", label: "创建人" },
  { prop: "contact", label: "联系方式" },
  { prop: "createTime", label: "创建时间" },
  { prop: "remark", label: "备注" },
];

/**
 * 打开枚举类型修改弹窗。
 * @param {Object} row 当前行数据
 */
function editType(row) {
  typeForm.value = { ...row, status: row.status || "0" };
  typeOpen.value = true;
}

/**
 * 打开枚举类型详情弹窗。
 * @param {Object} row 当前行数据
 */
function showTypeDetail(row) {
  typeDetail.value = row;
  typeDetailOpen.value = true;
}

/** 保存枚举类型，新增或修改后刷新类型列表。 */
async function saveType() {
  if (!(await typeFormRef.value.validate().catch(() => false))) return;
  const payload = {
    id: typeForm.value.id,
    taskId: props.taskId,
    enumName: typeForm.value.enumName,
    enumType: typeForm.value.enumType,
    status: typeForm.value.status,
    remark: typeForm.value.remark,
  };
  if (payload.id) await updateCandidate("enum_type", payload);
  else await createCandidate("enum_type", payload);
  proxy.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功");
  typeOpen.value = false;
  refresh();
}

/**
 * 下钻查看某个枚举类型下的数据，切换 currentType 后数据表自动首查。
 * @param {Object} row 当前枚举类型行
 */
function openValues(row) {
  currentType.value = row;
}

/** 关闭枚举数据视图，返回枚举类型列表。 */
function closeValues() {
  currentType.value = null;
}

/**
 * 打开枚举数据修改弹窗。
 * @param {Object} row 当前行数据
 */
function editValue(row) {
  valueForm.value = { ...row, status: row.status || "0" };
  valueOpen.value = true;
}

/**
 * 打开枚举数据详情弹窗，补充枚举类型名称用于展示。
 * @param {Object} row 当前行数据
 */
function showValueDetail(row) {
  valueDetail.value = { ...row, enumType: currentType.value?.enumType };
  valueDetailOpen.value = true;
}

/** 保存枚举数据，新增或修改后刷新数据列表。 */
async function saveValue() {
  if (!(await valueFormRef.value.validate().catch(() => false))) return;
  const payload = {
    id: valueForm.value.id,
    taskId: props.taskId,
    enumTypeId: currentType.value.id,
    enumSort: valueForm.value.enumSort,
    enumLabel: valueForm.value.enumLabel,
    enumValue: valueForm.value.enumValue,
    status: valueForm.value.status,
    remark: valueForm.value.remark,
  };
  if (payload.id) await updateCandidate("enum_data", payload);
  else await createCandidate("enum_data", payload);
  proxy.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功");
  valueOpen.value = false;
  valueTableRef.value?.getList();
}

/**
 * 查看枚举数据的来源证据。
 * @param {Object} row 当前行数据
 */
async function showValueEvidence(row) {
  const response = await getCandidateEvidence("enum_data", row.id);
  valueEvidences.value = response.data || [];
  valueEvidenceVisible.value = true;
}
</script>

<style scoped lang="scss">
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

:deep(.el-input-number) {
  width: 100%;
}
</style>
