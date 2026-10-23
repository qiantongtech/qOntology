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
    <GuideTip tip-id="basic/enumeration.list"/>
    <div v-show="showSearch" class="pagecont-top">
      <el-form ref="queryRef" class="btn-style" :model="queryParams" :inline="true" @submit.prevent="handleQuery">
        <el-form-item label="枚举名称" prop="enumName">
          <el-input v-model="queryParams.enumName" class="el-form-input-width" placeholder="请输入枚举名称" clearable
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="枚举类型" prop="enumType">
          <el-input v-model="queryParams.enumType" class="el-form-input-width" placeholder="请输入枚举类型" clearable
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" class="el-form-input-width" placeholder="请选择状态" clearable>
            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="handleQuery"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetQuery"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button v-hasPermi="['basic:enumeration:type:add']" type="primary" plain @click="handleAdd"><i
                class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['basic:enumeration:type:remove']" type="danger" plain :disabled="multiple || deleting"
                       @click="handleDelete()"><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"/>
      </div>
      <el-table ref="tableRef" v-loading="loading" :data="typeList" row-key="enumId" stripe
                :default-sort="defaultSort" @sort-change="handleSortChange" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" prop="enumId" width="80" align="center"
                         sortable="custom"/>
        <el-table-column v-if="getColumnVisibility(1)" label="枚举名称" prop="enumName" align="left" min-width="150"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            <span>{{ scope.row.enumName ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="枚举类型" prop="enumType" align="left" min-width="150"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            <router-link v-if="auth.hasPermi('basic:enumeration:data:list')"
                         :to="{name: 'BasicEnumData', params: {enumId: scope.row.enumId}}" class="link-type">{{ scope.row.enumType }}
            </router-link>
            <span v-else>{{ scope.row.enumType || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="状态" prop="status" align="center">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(6)" label="备注" prop="remark" min-width="200" align="left"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="创建人" prop="createBy" width="120" align="center"
                         :show-overflow-tooltip="{ effect: 'light' }"/>
        <el-table-column v-if="getColumnVisibility(5)" label="创建时间" prop="createTime" width="150" align="center"
                         sortable="custom">
          <template #default="scope">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') || '—' }}</template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(7)" label="操作" width="240" fixed="right" align="center"
                         class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button v-hasPermi="['basic:enumeration:type:query']" link type="primary" icon="View"
                       @click="handleDetail(scope.row)">详情
            </el-button>
            <el-button v-hasPermi="['basic:enumeration:type:edit']" link type="primary" icon="Edit"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button v-hasPermi="['basic:enumeration:type:remove']" link type="danger" icon="Delete" :disabled="deleting"
                       @click="handleDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无记录"/>
            <p>暂无记录</p></div>
        </template>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize" @pagination="getList"/>
    </div>
    <el-dialog v-model="open" :title="title" width="800px" :append-to="pageRef" :close-on-click-modal="false"
               :close-on-press-escape="!saving" :before-close="handleBeforeClose" draggable @closed="clearValidation">
      <el-form ref="typeRef" :model="form" :rules="rules" label-width="90px" @submit.prevent="submitForm">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="枚举名称" prop="enumName">
              <el-input v-model="form.enumName" placeholder="请输入枚举名称" :maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举类型" prop="enumType">
              <el-input v-model="form.enumType" placeholder="请输入枚举类型" :maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">{{
                    dict.label
                  }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" class="character-count" type="textarea" :rows="3" :maxlength="512"
                        show-word-limit placeholder="请输入备注"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="openDetail" class="enum-detail-dialog" title="枚举类型详情" width="800px" :append-to="pageRef"
               draggable>
      <el-form :model="detail" label-width="90px" label-position="right" class="enum-detail-form">
        <el-row :gutter="20">
          <el-col :span="24">
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举名称">
              <div class="form-readonly">{{ detail.enumName ?? '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举类型">
              <div class="form-readonly">{{ detail.enumType ?? '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <dict-tag :options="sys_normal_disable" :value="detail.status"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建人">
              <div class="form-readonly">{{ detail.createBy || '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建时间">
              <div class="form-readonly">{{ parseTime(detail.createTime) || '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <div class="form-readonly textarea">{{ detail.remark || '-' }}</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="openDetail = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OmEnumType">
import {onBeforeUnmount, onMounted, ref, reactive, toRefs, nextTick, watch, getCurrentInstance} from 'vue';
import {listType, getType, addType, updateType, delType} from '@/api/basic/enumeration/type.js';
import auth from '@/plugins/auth.js';

const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict('sys_normal_disable');
const pageRef = ref();
const queryRef = ref();
const tableRef = ref();
const typeRef = ref();
const typeList = ref([]);
const open = ref(false);
const openDetail = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const saving = ref(false);
const deleting = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const detail = ref({});
const defaultSort = {prop: 'createTime', order: 'descending'};
const columns = ref([
  {key: 0, label: '编号', visible: true},
  {key: 1, label: '枚举名称', visible: true},
  {key: 2, label: '枚举类型', visible: true},
  {key: 3, label: '状态', visible: true},
  {key: 4, label: '创建人', visible: true},
  {key: 5, label: '创建时间', visible: true},
  {key: 6, label: '备注', visible: true},
  {key: 7, label: '操作', visible: true},
]);
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    enumName: '',
    enumType: '',
    status: '',
    orderByColumn: defaultSort.prop,
    isAsc: defaultSort.order
  },
  rules: {
    enumName: [
      {required: true, whitespace: true, message: '枚举名称不能为空', trigger: 'blur'},
      {max: 128, message: '枚举名称不能超过128个字符', trigger: 'blur'}
    ],
    enumType: [
      {required: true, whitespace: true, message: '枚举类型不能为空', trigger: 'blur'},
      {max: 128, message: '枚举类型不能超过128个字符', trigger: 'blur'},
      {pattern: /^[A-Za-z][A-Za-z0-9_]*$/, message: '须以字母开头，仅允许字母、数字和下划线', trigger: 'blur'}
    ],
    status: [{required: true, message: '请选择状态', trigger: 'change'}],
    remark: [{max: 512, message: '备注不能超过512个字符', trigger: 'blur'}]
  }
});
const {queryParams, form, rules} = toRefs(data);
const appliedFilters = reactive({enumName: '', enumType: '', status: ''});
let listRequest = 0;
let contextRequest = 0;
let detailRequest = 0;

function getColumnVisibility(key) {
  return columns.value.find(column => column.key === key)?.visible !== false;
}

async function getList() {
  const request = ++listRequest;
  loading.value = true;
  try {
    const response = await listType({...queryParams.value, ...appliedFilters});
    if (request !== listRequest) return;
    typeList.value = response.data.rows;
    total.value = response.data.total;
    handleSelectionChange([]);
    tableRef.value?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

function reset() {
  form.value = {
    enumId: undefined,
    enumName: '',
    enumType: '',
    status: '0',
    remark: ''
  };
  clearValidation();
}

function clearValidation() {
  typeRef.value?.clearValidate();
}

function handleBeforeClose(done) {
  if (!saving.value) done();
}

function cancel() {
  if (saving.value) return;
  open.value = false;
  reset();
}

function handleQuery() {
  appliedFilters.enumName = queryParams.value.enumName.trim();
  appliedFilters.enumType = queryParams.value.enumType.trim();
  appliedFilters.status = queryParams.value.status;
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.enumId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleSortChange({prop, order}) {
  queryParams.value.orderByColumn = order ? prop : defaultSort.prop;
  queryParams.value.isAsc = order || defaultSort.order;
  queryParams.value.pageNum = 1;
  getList();
}

async function handleAdd() {
  ++detailRequest;
  reset();
  title.value = '新增枚举类型';
  open.value = true;
  await nextTick();
  clearValidation();
}

async function handleUpdate(row) {
  const request = ++detailRequest;
  const response = await getType(row?.enumId ?? ids.value[0]);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning('记录已不存在，请刷新');
    return;
  }
  reset();
  form.value = {...form.value, ...response.data};
  title.value = '修改枚举类型';
  open.value = true;
  await nextTick();
  clearValidation();
}

async function handleDetail(row) {
  const request = ++detailRequest;
  const response = await getType(row.enumId);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning('记录已不存在，请刷新');
    return;
  }
  detail.value = response.data;
  openDetail.value = true;
}

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
      remark: (form.value.remark || '').trim()
    };
    if (form.value.enumId != null) {
      payload.enumId = form.value.enumId;
      await updateType(payload);
    } else {
      await addType(payload);
    }
    proxy.$modal.msgSuccess(form.value.enumId != null ? '修改成功' : '新增成功');
    open.value = false;
    await getList();
  } finally {
    saving.value = false;
  }
}

async function handleDelete(row) {
  const context = contextRequest;
  const recordIds = row ? [row.enumId] : [...ids.value];
  if (!recordIds.length || deleting.value) return;
  try {
    await proxy.$modal.confirm('是否确认删除枚举编号为"' + recordIds.join(',') + '"的数据项？');
  } catch {
    return;
  }
  if (deleting.value || context !== contextRequest) return;
  deleting.value = true;
  try {
    await delType(recordIds.join(','));
    if (context !== contextRequest ) return;
    if (queryParams.value.pageNum > 1 && typeList.value.length <= recordIds.length) queryParams.value.pageNum--;
    await getList();
    proxy.$modal.msgSuccess('删除成功');
  } finally {
    deleting.value = false;
  }
}

onMounted(() => { getList(); });

onBeforeUnmount(() => { ++contextRequest; });
</script>

<style scoped>
.enum-detail-form {
  padding: 0;
}

.form-readonly {
  box-sizing: border-box;
  width: 100%;
  border: 1px solid #f1f1f1;
  padding: 0 10px;
  min-height: 34px;
  background-color: #fcfcfc;
  border-radius: 2px;
  color: #333;
  display: flex;
  align-items: center;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-all;
}

.form-readonly.textarea {
  min-height: 85px;
  display: block;
}

.enum-page :deep(.enum-detail-dialog .el-form-item) {
  margin-bottom: 16px;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__header) {
  padding: 9px 20px;
  background: #f8f8f8;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__body) {
  padding: 20px 40px !important;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #f1f1f1;
}

.enum-page :deep(.enum-detail-dialog .el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 12px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .enum-page :deep(.enum-detail-dialog .el-dialog__body) {
    padding: 20px 16px !important;
  }
}

.enum-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.enum-page :deep(.el-dialog__footer .el-button) {
  min-width: 56px;
  height: 32px;
  padding: 8px 12px;
}

.character-count :deep(.el-input__count)::after {
  content: ' 个字符';
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}

.enum-page :deep(.el-input-number) {
  width: 100%;
}
</style>
