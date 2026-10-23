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
    <div v-show="showSearch" class="pagecont-top">
      <el-form ref="queryRef" class="btn-style" :model="queryParams" :inline="true" @submit.prevent="handleQuery">
        <el-form-item label="枚举名称" prop="enumType">
          <el-select v-model="queryParams.enumType" class="el-form-input-width" filterable placeholder="请选择枚举名称">
            <el-option v-for="item in typeOptions" :key="item.enumId" :label="item.enumName" :value="item.enumType"/>
          </el-select>
        </el-form-item>
        <el-form-item label="枚举标签" prop="enumLabel">
          <el-input v-model="queryParams.enumLabel" class="el-form-input-width" placeholder="请输入枚举标签" clearable
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
            <el-button v-hasPermi="['om:enumeration:data:add']" type="primary" plain
                       :disabled="!appliedFilters.enumType" @click="handleAdd"><i
                class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['om:enumeration:data:remove']" type="danger" plain :disabled="multiple || deleting"
                       @click="handleDelete()"><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
          <el-col v-if="route.params.enumId" :span="1.5">
            <el-button type="warning" plain icon="Close" @click="handleClose">关闭</el-button>
          </el-col>
        </el-row>
        <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"/>
      </div>
      <el-table ref="tableRef" v-loading="loading" :data="dataList" row-key="enumCode" stripe
                :default-sort="defaultSort" @sort-change="handleSortChange" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" prop="enumCode" width="80" align="center"
                         sortable="custom"/>
        <el-table-column v-if="getColumnVisibility(1)" label="枚举标签" prop="enumLabel" align="left" min-width="150"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            <span>{{ scope.row.enumLabel ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="枚举键值" prop="enumValue" align="left" min-width="150"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            <span>{{ scope.row.enumValue ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="排序" prop="enumSort" align="center"
                         :show-overflow-tooltip="{ effect: 'light' }" sortable="custom">
          <template #default="scope">
            <span>{{ scope.row.enumSort ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="状态" prop="status" align="center">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(5)" label="创建人" prop="createBy" width="120" align="center"
                         :show-overflow-tooltip="{ effect: 'light' }"/>
        <el-table-column v-if="getColumnVisibility(6)" label="创建时间" prop="createTime" width="150" align="center"
                         sortable="custom">
          <template #default="scope">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') || '—' }}</template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(7)" label="备注" prop="remark" min-width="200" align="left"
                         :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(8)" label="操作" width="240" fixed="right" align="center"
                         class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button v-hasPermi="['om:enumeration:data:query']" link type="primary" icon="View"
                       @click="handleDetail(scope.row)">详情
            </el-button>
            <el-button v-hasPermi="['om:enumeration:data:edit']" link type="primary" icon="Edit"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button v-hasPermi="['om:enumeration:data:remove']" link type="danger" icon="Delete" :disabled="deleting"
                       @click="handleDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无记录"/>
            <p>{{ appliedFilters.enumType ? '暂无记录' : '请先选择枚举类型并查询' }}</p></div>
        </template>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize" @pagination="getList"/>
    </div>
    <el-dialog v-model="open" :title="title" width="800px" :append-to="pageRef" :close-on-click-modal="false"
               :close-on-press-escape="!saving" :before-close="handleBeforeClose" draggable @closed="clearValidation">
      <el-form ref="dataRef" :model="form" :rules="rules" label-width="90px" @submit.prevent="submitForm">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="枚举类型">
              <el-input v-model="form.enumType" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举标签" prop="enumLabel">
              <el-input v-model="form.enumLabel" placeholder="请输入枚举标签" :maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举键值" prop="enumValue">
              <el-input v-model="form.enumValue" placeholder="请输入枚举键值" :maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="排序" prop="enumSort">
              <el-input-number v-model="form.enumSort" :min="0" :max="2147483647" :precision="0"
                               controls-position="right"/>
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
              <el-input v-model="form.remark" class="character-count" type="textarea" :rows="3" :maxlength="500"
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
    <el-dialog v-model="openDetail" class="enum-detail-dialog" title="枚举数据详情" width="800px" :append-to="pageRef"
               draggable>
      <el-form :model="detail" label-width="90px" label-position="right" class="enum-detail-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="编号">
              <div class="form-readonly">{{ detail.enumCode }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举类型">
              <div class="form-readonly">{{ detail.enumType }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举标签">
              <div class="form-readonly">{{ detail.enumLabel ?? '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="枚举键值">
              <div class="form-readonly">{{ detail.enumValue ?? '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="排序">
              <div class="form-readonly">{{ detail.enumSort ?? '—' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <div class="form-readonly">
                <dict-tag :options="sys_normal_disable" :value="detail.status"/>
              </div>
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
    <el-dialog v-model="upload.open" title="枚举数据导入" width="500px" :append-to="pageRef"
               :close-on-click-modal="false" :close-on-press-escape="!upload.isUploading"
               :show-close="!upload.isUploading">
      <el-upload ref="uploadRef" :action="upload.url" :headers="upload.headers"
                 :data="{ updateSupport: upload.updateSupport, enumType: appliedFilters.enumType }" :auto-upload="false"
                 :limit="1" accept=".xlsx,.xls" :disabled="upload.isUploading" :before-upload="handleFileUploadProgress"
                 :on-success="handleFileSuccess" :on-error="handleFileError">
        <el-button>选择 Excel 文件</el-button>
        <template #tip>
          <div class="el-upload__tip">请使用导入模板，数据中的枚举类型须与当前类型一致。</div>
        </template>
      </el-upload>
      <el-checkbox v-model="upload.updateSupport">更新已有数据</el-checkbox>
      <el-button link type="primary" @click="importTemplate">下载模板</el-button>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="upload.isUploading" @click="upload.open = false">取 消</el-button>
          <el-button type="primary" :loading="upload.isUploading" @click="submitFileForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OmEnumData">
import {onBeforeUnmount, ref, reactive, toRefs, nextTick, watch, getCurrentInstance} from 'vue';
import {useRoute} from 'vue-router';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import {listData, getData, addData, updateData, delData} from '@/api/om/enumeration/data';
import {listType, getType} from '@/api/om/enumeration/type';
import {getToken} from '@/utils/auth';
import auth from '@/plugins/auth';

const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict('sys_normal_disable');
const route = useRoute();
const {versionId, requireVersion} = useOntologyVersion();
const pageRef = ref();
const queryRef = ref();
const tableRef = ref();
const dataRef = ref();
const uploadRef = ref();
const dataList = ref([]);
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
const typeOptions = ref([]);
const defaultEnumType = ref('');
const defaultSort = {prop: 'enumSort', order: 'ascending'};
const columns = ref([
  {key: 0, label: '编号', visible: true},
  {key: 1, label: '枚举标签', visible: true},
  {key: 2, label: '枚举键值', visible: true},
  {key: 3, label: '排序', visible: true},
  {key: 4, label: '状态', visible: true},
  {key: 5, label: '创建人', visible: true},
  {key: 6, label: '创建时间', visible: true},
  {key: 7, label: '备注', visible: true},
  {key: 8, label: '操作', visible: true},
]);
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    enumType: '',
    enumLabel: '',
    status: '',
    orderByColumn: defaultSort.prop,
    isAsc: defaultSort.order
  },
  rules: {
    enumLabel: [
      {required: true, whitespace: true, message: '枚举标签不能为空', trigger: 'blur'},
      {max: 128, message: '枚举标签不能超过128个字符', trigger: 'blur'}
    ],
    enumValue: [
      {required: true, whitespace: true, message: '枚举键值不能为空', trigger: 'blur'},
      {max: 128, message: '枚举键值不能超过128个字符', trigger: 'blur'}
    ],
    enumSort: [{required: true, type: 'number', min: 0, message: '请输入非负整数排序值', trigger: 'change'}],
    status: [{required: true, message: '请选择状态', trigger: 'change'}],
    remark: [{max: 512, message: '备注不能超过512个字符', trigger: 'blur'}]
  }
});
const {queryParams, form, rules} = toRefs(data);
// 筛选表单仅在查询时提交，分页、排序和刷新沿用已提交的条件。
const appliedFilters = reactive({enumType: '', enumLabel: '', status: ''});
const upload = reactive({
  open: false,
  isUploading: false,
  updateSupport: false,
  headers: {Authorization: 'Bearer ' + getToken()},
  url: import.meta.env.VITE_APP_BASE_API + '/om/data/importData'
});
let listRequest = 0;
let contextRequest = 0;
let detailRequest = 0;

function getColumnVisibility(key) {
  return columns.value.find(column => column.key === key)?.visible !== false;
}

async function getList() {
  const request = ++listRequest;
  if (!versionId.value || !appliedFilters.enumType) {
    dataList.value = [];
    total.value = 0;
    handleSelectionChange([]);
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const response = await listData({...queryParams.value, ...appliedFilters, versionId: versionId.value});
    if (request !== listRequest) return;
    dataList.value = response.data.rows;
    total.value = response.data.total;
    handleSelectionChange([]);
    tableRef.value?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

function reset() {
  form.value = {
    enumCode: undefined,
    versionId: versionId.value,
    enumLabel: '',
    enumValue: '',
    enumSort: 0,
    enumType: appliedFilters.enumType,
    status: '0',
    remark: ''
  };
  clearValidation();
}

function clearValidation() {
  dataRef.value?.clearValidate();
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
  appliedFilters.enumType = queryParams.value.enumType;
  appliedFilters.enumLabel = queryParams.value.enumLabel.trim();
  appliedFilters.status = queryParams.value.status;
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryRef.value?.resetFields();
  queryParams.value.enumType = defaultEnumType.value;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.enumCode);
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
  if (!requireVersion()) return;
  ++detailRequest;
  reset();
  title.value = '新增枚举数据';
  open.value = true;
  await nextTick();
  clearValidation();
}

async function handleUpdate(row) {
  const request = ++detailRequest;
  const response = await getData(row?.enumCode ?? ids.value[0]);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning('记录已不存在，请刷新');
    return;
  }
  reset();
  form.value = {...form.value, ...response.data};
  title.value = '修改枚举数据';
  open.value = true;
  await nextTick();
  clearValidation();
}

async function handleDetail(row) {
  const request = ++detailRequest;
  const response = await getData(row.enumCode);
  if (request !== detailRequest) return;
  if (!response.data) {
    proxy.$modal.msgWarning('记录已不存在，请刷新');
    return;
  }
  detail.value = response.data;
  openDetail.value = true;
}

async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value) return;
  const valid = await dataRef.value.validate().catch(() => false);
  if (!valid || saving.value || submittingVersion !== versionId.value || !open.value) return;
  saving.value = true;
  try {
    const payload = {
      versionId: submittingVersion,
      enumLabel: form.value.enumLabel.trim(),
      enumValue: form.value.enumValue.trim(),
      enumSort: form.value.enumSort,
      enumType: form.value.enumType,
      status: form.value.status,
      remark: (form.value.remark || '').trim()
    };
    if (form.value.enumCode != null) {
      payload.enumCode = form.value.enumCode;
      await updateData(payload);
    } else {
      await addData(payload);
    }
    if (submittingVersion !== versionId.value) return;
    proxy.$modal.msgSuccess(form.value.enumCode != null ? '修改成功' : '新增成功');
    open.value = false;
    await getList();
  } finally {
    saving.value = false;
  }
}

async function handleDelete(row) {
  const context = contextRequest;
  const deletingVersion = versionId.value;
  const recordIds = row ? [row.enumCode] : [...ids.value];
  if (!recordIds.length || deleting.value) return;
  try {
    await proxy.$modal.confirm('是否确认删除枚举编码为"' + recordIds.join(',') + '"的数据项？');
  } catch {
    return;
  }
  if (deleting.value || context !== contextRequest || deletingVersion !== versionId.value) return;
  deleting.value = true;
  try {
    await delData(recordIds.join(','));
    if (context !== contextRequest || deletingVersion !== versionId.value) return;
    if (queryParams.value.pageNum > 1 && dataList.value.length <= recordIds.length) queryParams.value.pageNum--;
    await getList();
    proxy.$modal.msgSuccess('删除成功');
  } finally {
    deleting.value = false;
  }
}

function handleExport() {
  proxy.download('om/data/export', {...queryParams.value, ...appliedFilters, versionId: versionId.value}, '枚举数据.xlsx');
}

function handleImport() {
  upload.open = true;
}

function importTemplate() {
  proxy.download('om/data/importTemplate', {}, '枚举数据模板.xlsx');
}

function handleFileUploadProgress() {
  upload.isUploading = true;
  return true;
}

function handleFileError() {
  upload.isUploading = false;
  proxy.$modal.msgError('导入失败，请重试');
}

function handleFileSuccess(response) {
  upload.isUploading = false;
  if (response.code !== 200) {
    proxy.$modal.msgError(response.msg || '导入失败');
    return;
  }
  upload.open = false;
  uploadRef.value?.clearFiles();
  proxy.$modal.msgSuccess(response.msg || '导入成功');
  getList();
}

function submitFileForm() {
  uploadRef.value?.submit();
}

function handleClose() {
  proxy.$tab.closeOpenPage({path: `/om/${versionId.value}/semantic/enum`});
}

let typeRequest = 0;
watch(() => [route.params.enumId, versionId.value], async ([enumId]) => {
  ++contextRequest;
  const request = ++typeRequest;
  ++detailRequest;
  ++listRequest;
  loading.value = false;
  dataList.value = [];
  total.value = 0;
  open.value = false;
  openDetail.value = false;
  handleSelectionChange([]);
  typeOptions.value = [];
  appliedFilters.enumType = '';
  defaultEnumType.value = '';
  if (!versionId.value) return;
  const [typesResponse, typeResponse] = await Promise.all([
    listType({pageNum: 1, pageSize: -1, versionId: versionId.value}),
    enumId ? getType(enumId) : Promise.resolve(null)
  ]);
  if (request !== typeRequest) return;
  typeOptions.value = typesResponse.data.rows;
  if (enumId) {
    if (!typeResponse.data || String(typeResponse.data.versionId) !== versionId.value) {
      queryParams.value.enumType = '';
      proxy.$modal.msgWarning('枚举类型已不存在');
      return;
    }
    defaultEnumType.value = typeResponse.data.enumType;
  } else {
    defaultEnumType.value = '';
  }
  queryParams.value.enumType = defaultEnumType.value;
  handleQuery();
}, {immediate: true});
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
