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
  <div class="app-container" ref="appContainer">
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['app:graph:event:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['app:graph:event:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 事件名称与描述合并展示，名称在上、描述在下。 -->
        <template #eventInfo="{ row }">
          <div class="event-info">
            <span class="event-info__name" :title="row.name || ''">{{
              row.name || "-"
            }}</span>
            <span
              class="event-info__description"
              :title="row.description || ''"
              >{{ row.description || "-" }}</span
            >
          </div>
        </template>
        <!-- 事件类型沿用字典标签展示 -->
        <template #eventType="{ row }">
          <dict-tag :options="app_event_type" :value="row.type" />
        </template>
        <!-- 参与实体：超出两行省略时，悬浮用浅色气泡展示全部实体 -->
        <template #entityList="{ row }">
          <el-tooltip
            v-if="row.entityNameList && row.entityNameList.length"
            :disabled="!overflowRows[row.id]"
            effect="light"
            placement="top"
            popper-class="entity-list-tooltip"
            :content="row.entityNameList.map((tag) => tag.name).join('、')"
          >
            <div
              :ref="(el) => setEntityEl(row.id, el)"
              class="entity-list multiline-ellipsis"
            >
              <el-tag
                v-for="tag in row.entityNameList"
                :key="tag.id"
                class="card-tag"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </el-tooltip>
          <span v-else>-</span>
        </template>
        <!-- 创建人双行展示：姓名上方，联系电话复用公共用户关联逻辑。 -->
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
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            v-hasPermi="['app:graph:event:query']"
            >详情</el-button
          >
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(row)"
            v-hasPermi="['app:graph:event:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="danger"
            icon="Delete"
            @click="handleDelete(row)"
            v-hasPermi="['app:graph:event:remove']"
            >删除</el-button
          >
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增/修改事件表单：本地草稿与实体选择由组件维护，父页面统一保存。 -->
    <EventFormDialog
      v-model:open="open"
      :title="title"
      :form="form"
      :rules="rules"
      :event-type-options="app_event_type"
      :entity-options="app_enetity_type"
      :graph-id="route.params.kgId"
      :saving="eventSaving"
      :append-to="appContainer"
      @cancel="cancel"
      @submit="submitForm"
    />

    <!-- 事件管理详情对话框 -->
    <el-dialog
      :title="title"
      v-model="openDetail"
      width="800px"
      :append-to="appContainer"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">{{
          title
        }}</span>
      </template>
      <el-form :model="form" label-width="90px" class="column-form">
        <el-form-item label="事件名称" prop="name" class="row-full">
          <div class="form-readonly">{{ form.name || "-" }}</div>
        </el-form-item>
        <el-form-item label="事件类型" prop="type" class="row-full">
          <dict-tag :options="app_event_type" :value="form.type" />
        </el-form-item>
        <el-form-item label="发生时间" prop="occurTime" class="row-full">
          <div class="form-readonly">{{ form.occurTime || "-" }}</div>
        </el-form-item>

        <el-form-item label="发生地点" prop="occurPlace" class="row-full">
          <div class="form-readonly">{{ form.occurPlace || "-" }}</div>
        </el-form-item>
        <el-form-item label="参与实体" prop="entityNameList" class="row-full">
          <div class="detail-entity-list">
            <el-tag
              v-for="(tag, index) in form.entityNameList"
              :key="index"
              class="card-tag"
            >
              {{ tag.name }}
            </el-tag>
            <span v-if="!form.entityNameList || !form.entityNameList.length"
              >-</span
            >
          </div>
        </el-form-item>
        <el-form-item label="事件描述" prop="description" class="row-full">
          <div class="form-readonly textarea">
            {{ form.description ?? "-" }}
          </div>
        </el-form-item>
        <el-form-item label="备注" prop="remark" class="row-full">
          <div class="form-readonly textarea">{{ form.remark ?? "-" }}</div>
        </el-form-item>
        <el-form-item label="创建人" prop="createBy" class="row-full">
          <div class="form-readonly">{{ form.createBy || "-" }}</div>
        </el-form-item>
        <el-form-item label="联系方式" class="row-full">
          <div class="form-readonly">
            {{ creatorPhones[String(form.creatorId)] || "-" }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime" class="row-full">
          <div class="form-readonly">
            {{ proxy.parseTime(form.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Event">
import {
  addEvent,
  delEvent,
  getEvent,
  listEvent,
  updateEvent,
} from "@/api/app/event/event";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import EventFormDialog from "./components/eventFormDialog.vue";

const { proxy } = getCurrentInstance();
const { app_event_type, app_enetity_type } = proxy.useDict(
  "app_event_type",
  "app_enetity_type"
);
// 联系电话复用公共用户关联逻辑，按创建人编号索引。
const { creatorPhones } = useCreatorPhones();

const tableRef = ref();
const appContainer = ref();

const open = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref("");
const route = useRoute();
// 父页面持有保存状态，供表单组件统一禁用提交和关闭操作。
const eventSaving = ref(false);

/** 后端返回的 entityList 需转换为参与实体标签展示所需的 entityNameList */
function formatData(rows) {
  return (rows || []).map((item) => ({
    ...item,
    entityNameList:
      item.entityList != null && item.entityList.length > 0
        ? item.entityList.map((entityItem) => ({ name: entityItem.entityName }))
        : [],
  }));
}

// 参与实体单元格：记录各行 DOM，列表渲染后测量是否被两行省略，只对省略的行开启悬浮提示
const entityEls = new Map();
const overflowRows = reactive({});

function setEntityEl(id, el) {
  if (el) {
    entityEls.set(id, el);
  } else {
    entityEls.delete(id);
  }
}

function measureEntityOverflow() {
  nextTick(() => {
    entityEls.forEach((el, id) => {
      overflowRows[id] =
        el.scrollHeight > el.clientHeight + 1 ||
        el.scrollWidth > el.clientWidth + 1;
    });
  });
}

// 表格配置
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
    { type: "selection", width: 55 },
    {
      label: "编号",
      prop: "id",
      width: 60,
      sortable: "custom",
      sortableKey: "id",
    },
    {
      label: "事件信息",
      prop: "name",
      slot: "eventInfo",
      minWidth: 320,
      align: "left",
    },
    { label: "事件类型", minWidth: 100, prop: "type", slot: "eventType" },
    {
      label: "参与实体",
      prop: "entityNameList",
      slot: "entityList",
      minWidth: 250,
      align: "left",
      headerAlign: "left",
    },
    {
      label: "发生时间",
      prop: "occurTime",
      date: true,
      width: 150,
      sortable: "custom",
      sortableKey: "occurTime",
    },
    {
      label: "发生地点",
      prop: "occurPlace",
      minWidth: 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 120,
      align: "left",
      headerAlign: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 150,
      align: "left",
      sortable: "custom",
      sortableKey: "createTime",
    },
    { label: "操作", slot: "handle", width: 240, fixed: "right" },
  ],
  func: listEvent,
  params: {
    workspaceId: null,
    graphId: null,
    modelId: null,
    name: null,
    type: null,
    createBy: null,
  },
  events: {
    formatData,
    // 列表渲染完成后测量参与实体是否超出两行
    onDataLoaded: measureEntityOverflow,
  },
});

// 搜索项配置
const searchStore = reactive({
  items: [
    { label: "事件名称", prop: "name", component: { is: "input" } },
    {
      label: "事件类型",
      prop: "type",
      component: { is: "select", options: app_event_type },
    },
    { label: "创建人", prop: "createBy", component: { is: "input" } },
  ],
});

const data = reactive({
  form: {},
  rules: {
    workspaceId: [
      { required: true, message: "工作区id不能为空", trigger: "blur" },
    ],
    name: [{ required: true, message: "事件名称不能为空", trigger: "blur" }],
    type: [{ required: true, message: "事件类型不能为空", trigger: "change" }],
    occurTime: [
      { required: true, message: "发生时间不能为空", trigger: "change" },
    ],
    entityNameList: [
      { required: true, message: "参与实体不能为空", trigger: "change" },
    ],
    validFlag: [
      { required: true, message: "是否有效不能为空", trigger: "blur" },
    ],
    delFlag: [{ required: true, message: "删除标志不能为空", trigger: "blur" }],
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    updateTime: [
      { required: true, message: "更新时间不能为空", trigger: "blur" },
    ],
  },
});

const { form, rules } = toRefs(data);

// 取消当前弹窗操作；保存期间由组件与父页面共同阻止关闭。
function cancel() {
  if (eventSaving.value) return;
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置：清空父级表单，子组件通过 watch props.form 同步本地草稿。
function reset() {
  form.value = {};
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增事件管理";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getEvent(_id).then((response) => {
    form.value = response.data;
    form.value.entityIdList =
      form.value.entityList != null && form.value.entityList.length > 0
        ? form.value.entityList.map((item) => item.entityId)
        : [];
    form.value.entityNameList =
      form.value.entityList != null && form.value.entityList.length > 0
        ? form.value.entityList.map((item) => item.entityName)
        : [];

    open.value = true;
    title.value = "修改事件管理";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getEvent(_id).then((response) => {
    form.value = response.data;
    form.value.entityNameList =
      form.value.entityList != null && form.value.entityList.length > 0
        ? form.value.entityList.map((items) => ({ name: items.entityName }))
        : [];
    openDetail.value = true;
    title.value = "事件管理详情";
  });
}

/** 接收组件校验后的表单快照，保存成功后关闭弹窗并刷新当前事件列表。 */
async function submitForm(payload) {
  // 组件负责字段校验，父页面额外防止重复保存及关闭后的迟到提交。
  if (eventSaving.value || !open.value) return;
  eventSaving.value = true;
  try {
    if (payload.id != null) {
      await updateEvent(payload);
    } else {
      await addEvent(payload);
    }
    proxy.$modal.msgSuccess(payload.id != null ? "修改成功" : "新增成功");
    open.value = false;
    tableRef.value?.getList();
  } catch {
    // 接口错误由全局请求层提示；保留弹窗草稿，允许用户修改后重新提交。
  } finally {
    eventSaving.value = false;
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id != null ? [row.id] : ids.value;
  console.log(_ids);
  if (!_ids.length) {
    proxy.$modal.msgWarning("请选择要删除的事件");
    return;
  }
  const confirmText =
    _ids.length > 1
      ? `可删除${_ids.length}个，不可删除0个，是否删除可删部分？`
      : `是否确认删除事件编号为“${row.id}”的数据项？`;
  proxy.$modal
    .confirm(confirmText)
    .then(function () {
      return delEvent(_ids.join(","));
    })
    .then(() => {
      tableRef.value?.getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}
</script>
<style scoped lang="scss">
.card-tag {
  margin: 2px;
}

.event-info {
  display: flex;
  min-width: 0;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--qt-space-1);
  line-height: 1.5;

  &__name,
  &__description {
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__name {
    color: var(--qt-text-primary);
  }

  &__description {
    color: var(--qt-text-secondary);
    font-size: var(--qt-font-size-small);
  }
}

.entity-list {
  width: 100%;
  text-align: left;
}

/* 详情参与实体标签：横向排列并自动换行。 */
.detail-entity-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
}

.multiline-ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制为2行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
<style lang="scss">
/* 参与实体悬浮提示：与系统浅色 tooltip 样式保持一致 */
.el-popper.is-light.entity-list-tooltip {
  max-width: 426px;
  padding: 10px 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  background: var(--qt-surface-background);
  box-shadow: 0 2px 12px 0 var(--qt-overlay-border);
  color: var(--el-text-color-primary);
  font-size: var(--qt-font-size-base);
  line-height: 22px;
  overflow-wrap: anywhere;
  white-space: normal;
}
</style>
