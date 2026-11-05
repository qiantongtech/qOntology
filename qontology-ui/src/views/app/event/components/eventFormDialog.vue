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

  Business License: https://community.qknow.ai/business/policy.html
  See the LICENSE file in the project root for full license information.
-->

<template>
  <!-- 新增和修改共用本地表单草稿，取消编辑不会直接修改父页面的数据。 -->
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="900px"
    :append-to="appendTo"
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :show-close="!saving"
    :before-close="handleBeforeClose"
    draggable
  >
    <template #header>
      <span role="heading" aria-level="2" class="el-dialog__title">{{ title }}</span>
    </template>
    <guide-tip-panel
      title="维护业务事件的基本信息及其参与的图谱实体，用于在时序视图与图谱探索中还原事件的发生过程。"
      type="hint"
      class="mb15"
      allow-wrap
    />
    <el-form
      ref="formRef"
      :model="localForm"
      :rules="rules"
      :disabled="saving"
      label-width="90px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <qt-form-item label="事件名称" prop="name" class="row-full">
        <el-input v-model="localForm.name" placeholder="请输入事件名称" />
      </qt-form-item>
      <qt-form-item label="事件类型" prop="type">
        <el-select v-model="localForm.type" placeholder="请选择事件类型" clearable>
          <el-option
            v-for="dict in eventTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="parseInt(dict.value)"
          />
        </el-select>
      </qt-form-item>
      <qt-form-item label="发生时间" prop="occurTime">
        <el-date-picker
          clearable
          style="width: 100%"
          v-model="localForm.occurTime"
          type="date"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择发生时间"
        />
      </qt-form-item>
      <qt-form-item label="发生地点" prop="occurPlace" class="row-full">
        <el-input v-model="localForm.occurPlace" placeholder="请输入发生地点" />
      </qt-form-item>
      <!-- 参与实体通过图谱数据选择，输入框仅作只读回显。 -->
      <qt-form-item label="参与实体" prop="entityNameList" class="row-full">
        <div class="entity-select">
          <el-input
            v-model="localForm.entityNameList"
            placeholder="请选择参与实体"
            readonly
          />
          <el-button
            type="primary"
            :disabled="saving"
            style="margin-left: 11px"
            @click="openGraphData"
            >选择实体</el-button
          >
        </div>
      </qt-form-item>
      <qt-form-item label="事件描述" prop="description" class="row-full">
        <el-input
          v-model="localForm.description"
          type="textarea"
          placeholder="请输入事件描述"
          maxlength="1024个字符"
          show-word-limit
        />
      </qt-form-item>
      <qt-form-item label="备注" prop="remark" class="row-full">
        <el-input
          v-model="localForm.remark"
          type="textarea"
          placeholder="请输入备注"
          maxlength="512个字符"
          show-word-limit
        />
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" :disabled="saving" @click="handleCancel">取 消</el-button>
        <el-button
          type="primary"
          size="mini"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
          >确 定</el-button
        >
      </div>
    </template>
  </el-dialog>

  <!-- 实体配置对话框：选择参与当前事件的图谱实体，沿用分页与跨页记忆选中逻辑。 -->
  <el-dialog
    :title="entityTitle"
    v-model="entityOpen"
    width="1000px"
    :append-to="appendTo"
    draggable
  >
    <template #header>
      <span role="heading" aria-level="2" class="el-dialog__title">{{ entityTitle }}</span>
    </template>
    <el-form
      class="btn-style"
      :model="queryParamsEntity"
      ref="queryRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="实体名称" prop="name">
        <el-input
          class="el-form-input-width"
          v-model="queryParamsEntity.name"
          placeholder="请输入实体名称"
          clearable
          @keyup.enter="handleQueryEntity"
        />
      </el-form-item>
      <el-form-item label="概念名称" prop="schema">
        <el-input
          class="el-form-input-width"
          v-model="queryParamsEntity.schema"
          placeholder="请输入概念名称"
          clearable
          @keyup.enter="handleQueryEntity"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          plain
          type="primary"
          @click="handleQueryEntity"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
        </el-button>
        <el-button @click="resetQueryEntity" @mousedown="(e) => e.preventDefault()">
          <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
        </el-button>
      </el-form-item>
    </el-form>
    <el-table
      stripe
      ref="entityTableRef"
      v-loading="entityLoading"
      :data="entityList"
      reserve-selection
      row-key="id"
      @selection-change="handleEntitySelectionChange"
      @row-click="handleRowClick"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        label="实体名称"
        prop="name"
        align="center"
        :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column
        label="概念名称"
        prop="schemaName"
        align="center"
        :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column
        label="来源"
        prop="entityType"
        align="center"
        :show-overflow-tooltip="{ effect: 'light' }"
      >
        <template #default="scope">
          <dict-tag :options="entityOptions" :value="scope.row.entityType" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{
            scope.row.createTime
              ? parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}')
              : '-'
          }}</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="entityTotal > 0"
      :total="entityTotal"
      v-model:page="queryParamsEntity.pageNum"
      v-model:limit="queryParamsEntity.pageSize"
      @pagination="getGraphData"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="entityOpen = false">取 消</el-button>
        <el-button type="primary" size="mini" @click="submitEntityForm">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="EventFormDialog">
import { computed, nextTick, ref, watch } from 'vue';
import { getGraphPage } from '@/api/app/graph';

/**
 * 事件新增/修改表单：维护本地草稿与参与实体选择，校验后通过 submit 提交快照。
 * 父页面负责接口保存，通过 saving 控制保存期间的交互；cancel 用于通知父页面重置状态。
 */
const props = defineProps({
  open: { type: Boolean, required: true },
  title: { type: String, required: true },
  form: { type: Object, required: true },
  rules: { type: Object, required: true },
  eventTypeOptions: { type: Array, default: () => [] },
  entityOptions: { type: Array, default: () => [] },
  graphId: { type: [String, Number], default: undefined },
  saving: { type: Boolean, default: false },
  appendTo: { type: [String, Object], default: undefined }
});
const emit = defineEmits(['update:open', 'cancel', 'submit']);
const formRef = ref();
const validating = ref(false);

/** 创建完整初值，确保重新打开新增弹窗时不保留上一次的实体、时间及备注信息。 */
function createEmptyForm() {
  return {
    id: null,
    entityList: [],
    entityNameList: [],
    entityIdList: [],
    workspaceId: null,
    modelId: null,
    name: null,
    description: null,
    type: null,
    occurTime: null,
    occurPlace: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null
  };
}

const localForm = ref(createEmptyForm());

// 代理 v-model:open；保存期间不允许标题栏或键盘操作意外关闭弹窗。
const dialogVisible = computed({
  get: () => props.open,
  set: (value) => {
    if (!props.saving) emit('update:open', value);
  }
});

/** 同步父页面传入的初始数据，使用独立对象编辑，避免取消时污染父级表单。 */
watch(
  () => props.form,
  (value) => {
    const source = value || {};
    localForm.value = { ...createEmptyForm(), ...source };
    // 参与实体输入框以名称数组回显，兼容详情接口的对象数组与新增时的字符串数组。
    localForm.value.entityNameList =
      source.entityNameList != null && source.entityNameList.length > 0
        ? source.entityNameList.map((item) => (typeof item === 'string' ? item : item.name))
        : source.entityList != null && source.entityList.length > 0
        ? source.entityList.map((item) => item.entityName)
        : [];
    nextTick(() => formRef.value?.clearValidate());
  },
  { immediate: true, deep: true }
);

/** 保存期间阻止弹窗关闭，其余情况交由弹窗组件同步 v-model。 */
function handleBeforeClose(done) {
  if (!props.saving) done();
}

/** 取消只通知父页面，由父页面关闭弹窗并统一重置表单。 */
function handleCancel() {
  if (!props.saving) emit('cancel');
}

/** 校验本地草稿并提交快照；等待期间禁止重复校验，关闭或保存中的弹窗不再提交。 */
async function submitForm() {
  if (validating.value || props.saving || !props.open) return;
  const draft = localForm.value;
  validating.value = true;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !props.open || props.saving || draft !== localForm.value) return;
    emit('submit', {
      ...draft,
      graphId: props.graphId,
      entityNameList: draft.entityNameList || [],
      entityList: draft.entityList || []
    });
  } finally {
    validating.value = false;
  }
}

/* ------------------------------ 参与实体选择 ------------------------------ */
const entityTitle = ref('');
const entityOpen = ref(false);
const entityLoading = ref(true);
const entityList = ref([]);
const entityTotal = ref(0);
// 当前已勾选的实体（跨分页记忆）
const entity = ref([]);
// 上次勾选数据，用于对比取消选择
const oldSelection = ref([]);
// 是否分页切换导致的选中变化
const isAuto = ref(false);
const entityTableRef = ref([]);
const queryParamsEntity = ref({
  pageNum: 1,
  pageSize: 10,
  name: null,
  graphId: null,
  entityType: '0',
  schema: null
});

async function openGraphData() {
  entityOpen.value = true;
  entityTitle.value = '选择实体';
  entityLoading.value = true;
  queryParamsEntity.value.pageNum = 1;
  entity.value = localForm.value.entityList || [];
  getGraphData();
}

// 查询图谱实体分页数据
function getGraphData() {
  entityLoading.value = true;
  queryParamsEntity.value.graphId = props.graphId;
  getGraphPage(queryParamsEntity.value).then(async (response) => {
    entityLoading.value = false;
    entityList.value = response.data.list;
    entityTotal.value = response.data.total;
    // 表格加载完成后，恢复之前选中的实体（含分页切换场景）
    isAuto.value = true;
    await nextTick();
    setSelectionRow(entity.value);
    isAuto.value = false;
  });
}

/**
 * 选中 table 的复选框
 * @param {Array} rows 选中的对象数组
 * @param {Boolean} ignoreSelectable 是否忽略可选
 */
function setSelectionRow(rows, ignoreSelectable) {
  if (rows && rows.length > 0) {
    rows.forEach((row) => {
      const matched = entityList.value.filter((item) => item.id == row.entityId);
      if (matched.length > 0) {
        entityTableRef.value.toggleRowSelection(matched[0], undefined, ignoreSelectable);
      }
    });
  }
}

// 多选框选中数据（含跨分页记忆与取消对比）
function handleEntitySelectionChange(selection) {
  entity.value = entity.value || [];
  const selectionList = (selection || []).map((item) => ({
    id: item.id,
    name: item.name,
    entityId: item.id,
    entityName: item.name
  }));
  if (selectionList.length > 0) {
    // 少选：移除本次未选中但上次选中的数据
    if (oldSelection.value.length > selectionList.length) {
      oldSelection.value.forEach((item) => {
        const index = selectionList.findIndex((ece) => ece.id == item.entityId);
        if (index == -1) {
          entity.value = entity.value.filter((ece) => item.id != ece.entityId);
        }
      });
    }
    if (entity.value.length > 0) {
      selectionList.forEach((item) => {
        const index = entity.value.findIndex((ece) => ece.entityId == item.id);
        if (index == -1) {
          entity.value.push({
            id: item.id,
            name: item.name,
            entityId: item.id,
            entityName: item.name
          });
        }
      });
    } else {
      entity.value.push(...selectionList);
    }
  } else if (!isAuto.value) {
    // 取消到没有选择任何值且非分页导致
    oldSelection.value.forEach((item) => {
      const index = selectionList.findIndex((ece) => ece.entityId == item.entityId);
      if (index == -1) {
        entity.value = entity.value.filter((ece) => item.entityId != ece.entityId);
      }
    });
  }
  oldSelection.value = selectionList;
}

/** 行点击切换选中状态 */
function handleRowClick(row) {
  entity.value = entity.value || [];
  const index = entity.value.findIndex((item) => item.entityId === row.id);
  if (index > -1) {
    entity.value = entity.value.filter((item) => item.entityId !== row.id);
  } else {
    entity.value.push({
      id: row.id,
      name: row.name,
      entityId: row.id,
      entityName: row.name
    });
  }
  entityTableRef.value.toggleRowSelection(row, index === -1);
}

function submitEntityForm() {
  entityOpen.value = false;
  localForm.value.entityList = entity.value;
  localForm.value.entityNameList = entity.value.map((item) => item.entityName);
}

function handleQueryEntity() {
  queryParamsEntity.value.pageNum = 1;
  getGraphData();
}

function resetQueryEntity() {
  queryParamsEntity.value.pageNum = 1;
  queryParamsEntity.value.pageSize = 10;
  queryParamsEntity.value.name = null;
  queryParamsEntity.value.entityType = null;
  getGraphData();
}
</script>

<style scoped lang="scss">
// 参与实体输入框占据剩余宽度，选择按钮固定在右侧。
.entity-select {
  display: flex;
  align-items: center;
  width: 100%;

  .el-input {
    flex: 1;
  }
}
</style>
