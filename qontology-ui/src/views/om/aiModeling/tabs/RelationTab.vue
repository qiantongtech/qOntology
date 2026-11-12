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
        <!-- 纯文本列沿用统一 text 插槽渲染；null 占位符由 QtTable 统一输出，这里只补空字符串场景，避免叠成两个短横线。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop] === "" ? "-" : row[column_data.prop]
        }}</template>
        <template #startTypeName="{ row }">{{
          objectName(row.startObjectCandidateId)
        }}</template>
        <template #endTypeName="{ row }">{{
          objectName(row.endObjectCandidateId)
        }}</template>
        <!-- 是否可逆用 dict-tag 渲染。 -->
        <template #reversible="{ row }">
          <dict-tag
            :options="sys_yes_no"
            :value="row.reversibleFlag === 1 ? 'Y' : 'N'"
          />
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
            :width="150"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
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
                :disabled="deleting"
                @click="remove(row)"
                >删除</el-button
              >
            </div>
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
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="定义对象类型之间的业务联系，配置起点、终点、可逆性和关联建模目标，为业务对象建立清晰的语义关系。"
      />
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        class="column-form"
        @submit.prevent="save"
      >
        <div class="h2-titles row-full">基础信息</div>
        <qt-form-item
          class="row-full"
          label="起点类型"
          prop="startObjectCandidateId"
          tip="选择关系的发起对象，例如“供应商提供物料”中的供应商。"
        >
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
        </qt-form-item>
        <qt-form-item class="row-full" label="对象类型关系" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入对象类型关系，例如：提供、采购物料"
            :maxlength="128"
          />
        </qt-form-item>
        <qt-form-item
          class="row-full"
          label="终点类型"
          prop="endObjectCandidateId"
        >
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
        </qt-form-item>
        <qt-form-item class="row-full" label="是否可逆" prop="reversibleFlag">
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
        </qt-form-item>
        <qt-form-item
          v-if="form.reversibleFlag === 1"
          class="row-full"
          label="反向名称"
          prop="reverseName"
          required
          tip="填写反向查看该关系时的名称，例如“由供应商提供”。"
        >
          <el-input
            v-model="form.reverseName"
            placeholder="请输入反向名称"
            :maxlength="128"
          />
        </qt-form-item>
        <qt-form-item class="row-full" label="描述" prop="description">
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入关系描述，说明业务联系和适用场景"
            maxlength="256个字符"
            show-word-limit
          />
        </qt-form-item>
        <div class="row-full">
          <div class="h2-titles mb15">关联建模目标</div>
          <div class="mb15">
            <el-form-item prop="targetIds" label-width="0">
              <el-button
                type="primary"
                plain
                :disabled="saving"
                @click="targetPickerOpen = true"
              >
                <el-icon class="mr5"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 1024 1024"
                  >
                    <path
                      fill="currentColor"
                      d="M160 832h704a32 32 0 1 1 0 64H160a32 32 0 1 1 0-64m384-578.304V704h-64V247.296L237.248 490.048 192 444.8 508.8 128l316.8 316.8-45.312 45.248z"
                    ></path></svg
                ></el-icon>
                导入建模目标
              </el-button>
              <span class="desc-text ml10"
                >点击导入建模目标，选中结果将在下方列表中回显。</span
              >
            </el-form-item>
          </div>
          <TargetTable
            :targets="selectedTargets"
            editable
            :disabled="saving"
            @remove="removeTarget"
          />
        </div>
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
      <el-form label-width="140px">
        <el-form-item label="起点类型">
          <div class="form-readonly">
            {{ objectName(detail.startObjectCandidateId) }}
          </div>
        </el-form-item>
        <el-form-item label="对象类型关系">
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
          <dict-tag
            :options="sys_yes_no"
            :value="detail.reversibleFlag === 1 ? 'Y' : 'N'"
          />
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
        <el-form-item label="创建人">
          <div class="form-readonly">
            {{ detail.createBy || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="联系方式">
          <div class="form-readonly">
            {{
              detail.creatorId != null &&
              creatorPhones[String(detail.creatorId)]
                ? creatorPhones[String(detail.creatorId)]
                : "-"
            }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间">
          <div class="form-readonly">
            {{
              proxy.parseTime(detail.createTime, "{y}-{m}-{d} {h}:{i}") || "-"
            }}
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
import TargetTable from "@/views/om/om/object/type/TargetTable.vue";
import CandidateTargetPicker from "@/views/om/aiModeling/components/CandidateTargetPicker.vue";
import CandidateEvidenceDrawer from "@/views/om/aiModeling/components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "@/views/om/aiModeling/tabs/useCandidateTab";

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
  if (!row.name?.trim()) missing.push("对象类型关系");
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
    { label: "对象类型关系", prop: "name", component: { is: "input" } },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      component: { is: "select", options: om_ai_adopt_status },
    },
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
      selectable: () => true,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 60,
      minWidth: 60,
      align: "center",
      fixed: "left",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    { label: "起点类型", slot: "startTypeName", minWidth: 140, align: "left" },
    {
      label: "对象类型关系",
      prop: "name",
      slot: "text",
      minWidth: 160,
      align: "left",
    },
    { label: "终点类型", slot: "endTypeName", minWidth: 140, align: "left" },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 200,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "是否可逆",
      prop: "reversibleFlag",
      slot: "reversible",
      width: 100,
      minWidth: 100,
      align: "center",
    },
    {
      label: "反向名称",
      prop: "reverseName",
      slot: "text",
      minWidth: 150,
      align: "left",
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
      date: true,
      width: 160,
      minWidth: 160,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 250,
      minWidth: 250,
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
      message: "对象类型关系不能为空",
      trigger: "blur",
    },
    { max: 128, message: "对象类型关系不能超过128个字符", trigger: "blur" },
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
    { max: 256, message: "关系描述不能超过256个字符", trigger: "blur" },
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
/* 与建模目标详情一致：嵌入页签的搜索区不重复添加容器内边距。 */
.candidate-tab-page :deep(.qt-wrap--search) {
  padding: 0;
}

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
