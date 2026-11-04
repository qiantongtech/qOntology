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
      <template #actions-data>
        <el-button
          type="primary"
          plain
          :disabled="!selectedIds.length"
          @click="adoptBatch"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-queren mr5"></i>批量采纳
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="!selectedIds.length"
          @click="remove()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 起止类型存的是候选编号，需要换成对象类型名称展示。 -->
        <template #objectName="{ row, column_data }">{{
          objectName(row[column_data.prop])
        }}</template>
        <template #reversibleFlag="{ row }">{{
          reversibleLabel(row.reversibleFlag)
        }}</template>
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
            icon="Check"
            :disabled="row.adoptStatus === 1"
            @click="adoptOne(row)"
            >采纳</el-button
          >
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
              :disabled="row.adoptStatus === 1"
              @click="remove(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 修改关系对话框 -->
    <el-dialog
      v-model="open"
      title="修改关系"
      width="1100px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      :close-on-press-escape="!saving"
      draggable
      destroy-on-close
      @closed="formRef?.clearValidate()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="起点类型" prop="startObjectCandidateId">
              <el-select
                v-model="form.startObjectCandidateId"
                placeholder="请选择起点类型"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in objects"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关系名称" prop="name">
              <el-input
                v-model="form.name"
                placeholder="请输入关系名称"
                :maxlength="128"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="终点类型" prop="endObjectCandidateId">
              <el-select
                v-model="form.endObjectCandidateId"
                placeholder="请选择终点类型"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in objects"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否可逆" prop="reversibleFlag">
              <el-radio-group
                v-model="form.reversibleFlag"
                @change="(value) => value !== 1 && (form.reverseName = '')"
              >
                <el-radio
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :value="dict.value === 'Y' ? 1 : 0"
                  >{{ dict.label }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.reversibleFlag === 1" :span="24">
            <el-form-item label="反向名称" prop="reverseName" required>
              <el-input
                v-model="form.reverseName"
                placeholder="请输入反向名称"
                :maxlength="128"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                class="character-count"
                type="textarea"
                :rows="4"
                placeholder="请输入描述"
                :maxlength="512"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联建模目标" prop="targetIds">
              <el-button type="primary" plain @click="targetPickerOpen = true"
                >导入建模目标</el-button
              >
            </el-form-item>
            <div class="target-table">
              <TargetTable
                :targets="selectedTargets"
                editable
                @remove="removeTarget"
              />
            </div>
          </el-col>
        </el-row>
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

    <CandidateTargetPicker
      v-model="targetPickerOpen"
      :task-id="taskId"
      :selected-targets="selectedTargets"
      :append-to="pageRef"
      @confirm="addTargets"
    />

    <!-- 关系详情对话框 -->
    <el-dialog
      v-model="detailOpen"
      title="关系详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form label-width="110px">
        <el-form-item label="起点类型">
          <div class="form-readonly">
            {{ objectName(detail.startObjectCandidateId) }}
          </div>
        </el-form-item>
        <el-form-item label="关系名称">
          <div class="form-readonly">
            {{ detail.name || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="终点类型">
          <div class="form-readonly">
            {{ objectName(detail.endObjectCandidateId) }}
          </div>
        </el-form-item>
        <el-form-item label="是否可逆">
          <div class="form-readonly">
            {{ reversibleLabel(detail.reversibleFlag) }}
          </div>
        </el-form-item>
        <el-form-item v-if="detail.reversibleFlag === 1" label="反向名称">
          <div class="form-readonly">
            {{ detail.reverseName || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <div class="form-readonly textarea">
            {{ detail.description || "-" }}
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
import { getCurrentInstance, nextTick, ref } from "vue";
import {
  getCandidateTargets,
  listCandidates,
  saveCandidateTargets,
  updateCandidate,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import TargetTable from "../../om/object/type/TargetTable.vue";
import CandidateTargetPicker from "../components/CandidateTargetPicker.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";

const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status, sys_yes_no } = proxy.useDict(
  "om_ai_adopt_status",
  "sys_yes_no"
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
const targetPickerOpen = ref(false);
const selectedTargets = ref([]);

/**
 * 收集采纳前缺失的必填项名称，供公共页签逻辑提示用户。
 * @param {Object} row 当前候选行数据
 * @returns {Array<string>} 缺失字段的中文名称列表
 */
function required(row) {
  const missing = [];
  if (!row.startObjectCandidateId) missing.push("起点类型");
  if (!row.name?.trim()) missing.push("关系名称");
  if (!row.endObjectCandidateId) missing.push("终点类型");
  if (![0, 1].includes(Number(row.reversibleFlag))) missing.push("是否可逆");
  if (Number(row.reversibleFlag) === 1 && !row.reverseName?.trim())
    missing.push("反向名称");
  return missing;
}

const tab = useCandidateTab("relation", props.taskId, required);
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
  remove,
  showEvidence,
} = tab;

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 页签内又没有搜索区，所以同时关闭操作栏的“隐藏搜索”按钮。
 */
const wrapConfig = {
  fullContent: false,
  actions: { table: { search: false } },
};

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
      label: "起点类型",
      prop: "startObjectCandidateId",
      slot: "objectName",
      width: 160,
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "关系名称",
      prop: "name",
      slot: "text",
      width: 160,
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "终点类型",
      prop: "endObjectCandidateId",
      slot: "objectName",
      width: 160,
      minWidth: 160,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 240,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "是否可逆",
      prop: "reversibleFlag",
      slot: "reversibleFlag",
      width: 100,
      minWidth: 100,
      align: "center",
    },
    {
      label: "反向名称",
      prop: "reverseName",
      slot: "text",
      width: 150,
      minWidth: 150,
      align: "left",
      showOverflowTooltip: { effect: "light" },
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
      align: "center",
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
      width: 200,
      minWidth: 200,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
});

const rules = {
  startObjectCandidateId: [
    { required: true, message: "请选择起点类型", trigger: "change" },
  ],
  name: [
    {
      required: true,
      whitespace: true,
      message: "关系名称不能为空",
      trigger: "blur",
    },
    { max: 128, message: "关系名称不能超过128个字符", trigger: "blur" },
  ],
  endObjectCandidateId: [
    { required: true, message: "请选择终点类型", trigger: "change" },
  ],
  reversibleFlag: [
    { required: true, message: "请选择是否可逆", trigger: "change" },
  ],
  reverseName: [
    {
      validator: (_rule, value, callback) =>
        callback(
          form.value.reversibleFlag === 1 && !value?.trim()
            ? new Error("可逆关系的反向名称不能为空")
            : undefined
        ),
      trigger: "blur",
    },
  ],
  description: [
    { max: 512, message: "关系描述不能超过512个字符", trigger: "blur" },
  ],
  targetIds: [
    {
      type: "array",
      required: true,
      min: 1,
      message: "请至少关联一个建模目标",
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
 * 将是否可逆的数字标记转成字典文案。
 * @param {number|string} value 可逆标记
 * @returns {string} 字典标签
 */
const reversibleLabel = (value) =>
  sys_yes_no.value.find(
    (item) => item.value === (Number(value) === 1 ? "Y" : "N")
  )?.label || "-";

/** 加载对象类型候选，供起止类型名称回显和下拉选择使用。 */
async function loadObjects() {
  const response = await listCandidates("object", {
    taskId: props.taskId,
    pageNum: 1,
    pageSize: 1000,
  });
  objects.value = response.data?.rows || [];
}

/**
 * 追加导入的建模目标。
 * @param {Array<Object>} targets 选中的目标候选
 */
function addTargets(targets) {
  selectedTargets.value = [...selectedTargets.value, ...targets];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}

/**
 * 移除已关联的建模目标。
 * @param {Object} row 待移除的目标行
 */
function removeTarget(row) {
  selectedTargets.value = selectedTargets.value.filter(
    (item) => String(item.id) !== String(row.id)
  );
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}

/**
 * 打开修改弹窗并回显已关联的建模目标。
 * @param {Object} row 当前行数据
 */
async function edit(row) {
  form.value = {
    ...row,
    reversibleFlag: Number(row.reversibleFlag ?? 0),
    targetIds: [],
  };
  const response = await getCandidateTargets("relation", row.id);
  selectedTargets.value = response.data || [];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
  open.value = true;
  nextTick(() => formRef.value?.clearValidate());
}

/**
 * 打开详情弹窗。
 * @param {Object} row 当前行数据
 */
function showDetail(row) {
  detail.value = { ...row };
  detailOpen.value = true;
}

/** 提交修改与目标关联，提交期间锁定确定按钮避免重复入库。 */
async function save() {
  if (saving.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  try {
    await updateCandidate("relation", {
      id: form.value.id,
      taskId: props.taskId,
      name: form.value.name.trim(),
      description: (form.value.description || "").trim(),
      startObjectCandidateId: form.value.startObjectCandidateId,
      endObjectCandidateId: form.value.endObjectCandidateId,
      reversibleFlag: form.value.reversibleFlag,
      reverseName:
        form.value.reversibleFlag === 1 ? form.value.reverseName?.trim() : "",
    });
    await saveCandidateTargets("relation", form.value.id, form.value.targetIds);
    proxy.$modal.msgSuccess("修改成功");
    open.value = false;
    refresh();
  } finally {
    saving.value = false;
  }
}

loadObjects();
</script>

<style scoped lang="scss">
.target-table {
  margin: 0 0 10px 120px;
}

@media (max-width: 600px) {
  .target-table {
    margin-left: 0;
  }
}

.character-count :deep(.el-input__count) {
  background: transparent;
}
</style>
