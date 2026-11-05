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
  <div ref="pageRef" v-loading="loading">
    <qt-wrap :columns="columns" :table-ref="listActions" :config="{ fullContent: false, actions: { table: { search: false } } }">
      <template #actions-data>
        <el-button
          v-hasPermi="['om:omtarget:targettask:add']"
          icon="Stamp"
          type="primary"
          plain
          :disabled="saving || !props.targetDetail?.id"
          @click="handleAdd"
        >验证</el-button>
      </template>
      <qt-table
        ref="tableRef"
        :columns="columns"
        :config="tableConfig"
        :events="tableEvents"
        :func="readRows"
      >
        <template #acceptResult="{ row }"><dict-tag :options="om_accept_status" :value="row.acceptResult" /></template>
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <template #creator="{ row }"><div class="flex-column overflow-hidden"><span class="text-ellipsis">{{ row.createBy }}</span><span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span></div></template>
        <template #createTime="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
        <template #operation="{ row }"><el-button
          v-hasPermi="['om:omtarget:targettask:query']"
          link
          type="primary"
          icon="View"
          @click="handleDetail(row)"
        >详情</el-button></template>
      </qt-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </qt-wrap>
    <el-dialog
      v-model="open"
      title="验证建模目标"
      width="800px"
      :append-to="pageRef"
      :close-on-click-modal="!saving"
      :close-on-press-escape="!saving"
      :before-close="beforeClose"
      draggable
      @closed="targetRef?.clearValidate()"
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="记录本次建模目标的验证任务，填写任务名称、覆盖判断和依据说明，便于追溯目标的验证过程与结论。"
      />
      <el-form
        ref="targetRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="column-form"
        @submit.prevent="submitForm"
      >
        <qt-form-item
          label="任务名称"
          prop="taskName"
          class="row-full"
          tip="概括本次验证的范围或目的，便于区分同一建模目标的多次验证记录。"
        >
          <el-input v-model="form.taskName" placeholder="请输入任务名称，例如：采购订单与入库关联覆盖验证" :maxlength="256" />
        </qt-form-item>
        <qt-form-item
          label="判断结果"
          prop="acceptResult"
          class="row-full"
          tip="根据验收标准判断目标问题是否被完整覆盖，默认选择已覆盖。"
        >
          <el-radio-group v-model="form.acceptResult"><el-radio v-for="item in acceptOptions" :key="item.value" :value="String(item.value)">{{ item.label }}</el-radio></el-radio-group>
        </qt-form-item>
        <qt-form-item
          label="判断说明"
          prop="taskExplain"
          class="row-full"
          tip="说明已覆盖的模型内容、验证依据，或尚未覆盖的缺口及改进方向。"
        >
          <el-input
            v-model="form.taskExplain"
            type="textarea"
            :rows="3"
            :maxlength="256"
            show-word-limit
            placeholder="请输入判断说明，描述验证依据、覆盖范围或待完善的问题"
          />
        </qt-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button :disabled="saving" @click="open = false">取 消</el-button><el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button></div></template>
    </el-dialog>
    <el-dialog
      v-model="openDetail"
      title="验证任务详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form label-width="90px" class="column-form">
        <qt-form-item label="任务名称" class="row-full"><div class="form-readonly">{{ detail.taskName || '-' }}</div></qt-form-item>
        <qt-form-item label="判断结果" class="row-full"><dict-tag :options="om_accept_status" :value="detail.acceptResult" /></qt-form-item>
        <qt-form-item label="判断说明" class="row-full"><div class="form-readonly textarea">{{ detail.taskExplain || '-' }}</div></qt-form-item>
      </el-form>
      <template #footer><el-button @click="openDetail = false">关 闭</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="AcceptTaskComp">
import { computed, getCurrentInstance, onBeforeUnmount, reactive, ref, watch } from "vue";
import { addTargetTask, getTargetTask, listTargetTask } from "@/api/om/omtarget/targetTask";
import { useCreatorPhones } from "@/utils/useCreatorPhones";

const props = defineProps({ targetDetail: { type: Object, default: () => ({}) } });
const emit = defineEmits(["updateAcceptStatus"]);
const { proxy } = getCurrentInstance();
const { om_accept_status } = proxy.useDict("om_accept_status");
const { creatorPhones } = useCreatorPhones();
const acceptOptions = computed(() => om_accept_status.value.filter(item => ['1', '2'].includes(String(item.value))));
const pageRef = ref();
const tableRef = ref();
const targetRef = ref();
const rows = ref([]);
const total = ref(0);
const loading = ref(false);
const saving = ref(false);
const open = ref(false);
const openDetail = ref(false);
const form = ref({ taskName: '', acceptResult: '1', taskExplain: '' });
const detail = ref({});
const query = reactive({ pageNum: 1, pageSize: 10, orderByColumn: 'createTime', isAsc: 'descending' });
const rules = {
  taskName: [{ required: true, whitespace: true, message: '请输入任务名称', trigger: 'blur' }, { max: 256, message: '任务名称不能超过256个字符', trigger: 'blur' }],
  acceptResult: [{ required: true, message: '请选择判断结果', trigger: 'change' }],
  taskExplain: [{ required: true, whitespace: true, message: '请输入判断说明', trigger: 'blur' }, { max: 256, message: '判断说明不能超过256个字符', trigger: 'blur' }],
};
const columns = reactive([
  { prop: 'id', label: '编号', width: 80, sortable: 'custom' },
  { prop: 'taskName', label: '任务名称', slot: 'text', minWidth: 220, align: 'left' },
  { prop: 'acceptResult', label: '验证结果', slot: 'acceptResult', width: 120 },
  { prop: 'taskExplain', label: '判断说明', slot: 'text', minWidth: 300, align: 'left' },
  { prop: 'createBy', label: '验证人', slot: 'creator', width: 130, align: 'left', headerAlign: 'left' },
  { prop: 'createTime', label: '验证时间', slot: 'createTime', width: 160, sortable: 'custom' },
  { label: '操作', slot: 'operation', width: 100, fixed: 'right' },
]);
const tableConfig = { notPagination: true, sort: true, table: { stripe: true, rowKey: 'id', defaultSort: { prop: 'createTime', order: 'descending' } } };
const tableEvents = { onSortChange({ orderByColumn, isAsc }) {
  query.orderByColumn = isAsc ? orderByColumn : 'createTime';
  query.isAsc = isAsc || 'descending';
  query.pageNum = 1;
  getList();
} };
const listActions = { getList };
function readRows() {
  return Promise.resolve({ data: rows.value.map(row => ({ ...row, taskName: row.taskName || '-', taskExplain: row.taskExplain || '-', createBy: row.createBy || '-', createTime: row.createTime || '-' })) });
}
watch(rows, () => tableRef.value?.getList());
let requestId = 0;
let contextId = 0;
let detailRequest = 0;
async function getList() {
  const request = ++requestId;
  if (!props.targetDetail?.id) { rows.value = []; total.value = 0; loading.value = false; return; }
  loading.value = true;
  try {
    const response = await listTargetTask({ ...query, targetId: props.targetDetail.id });
    if (request !== requestId) return;
    total.value = Number(response.data.total || 0);
    const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
    if (query.pageNum > maxPage) { query.pageNum = maxPage; return getList(); }
    rows.value = response.data.rows || [];
  } catch {
    if (request === requestId) { rows.value = []; total.value = 0; }
  } finally { if (request === requestId) loading.value = false; }
}
function handleAdd() {
  ++detailRequest;
  form.value = { taskName: '', acceptResult: '1', taskExplain: '' };
  open.value = true;
  targetRef.value?.clearValidate();
}
async function handleDetail(row) {
  const request = ++detailRequest;
  const response = await getTargetTask(row.id);
  if (request !== detailRequest || !response.data) return;
  detail.value = response.data;
  openDetail.value = true;
}
function beforeClose(done) { if (!saving.value) done(); }
async function submitForm() {
  if (saving.value || !open.value || !props.targetDetail?.id) return;
  const context = contextId;
  const targetId = props.targetDetail.id;
  const valid = await targetRef.value.validate().catch(() => false);
  if (!valid || saving.value || context !== contextId || !open.value) return;
  saving.value = true;
  try {
    const acceptResult = form.value.acceptResult;
    await addTargetTask({ targetId, taskName: form.value.taskName.trim(), acceptResult, taskExplain: form.value.taskExplain.trim() });
    if (context !== contextId) return;
    emit('updateAcceptStatus', acceptResult);
    proxy.$modal.msgSuccess('验证成功');
    open.value = false;
    getList();
  } finally { saving.value = false; }
}
watch(() => props.targetDetail?.id, () => {
  ++contextId;
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  rows.value = [];
  query.pageNum = 1;
  getList();
}, { immediate: true });
onBeforeUnmount(() => { ++contextId; ++requestId; ++detailRequest; });
</script>
