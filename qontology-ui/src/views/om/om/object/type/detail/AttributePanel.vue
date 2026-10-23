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
  <div ref="panelRef">
    <el-form v-show="showSearch" class="btn-style" :model="query" :inline="true" @submit.prevent="search">
      <el-form-item label="属性名称">
        <el-input v-model="query.name" class="el-form-input-width" placeholder="请输入属性名称" maxlength="128" clearable @keyup.enter="search" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain @click="search"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询</el-button>
        <el-button @click="resetSearch"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
      </el-form-item>
    </el-form>
    <div class="justify-between mb15">
      <el-row :gutter="15" class="btn-style">
        <el-col :span="1.5">
          <el-button v-hasPermi="['om:object:type:edit']" type="primary" plain :disabled="saving" @click="handleAdd">
            <i class="iconfont-mini icon-xinzeng mr5"></i>新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['om:object:type:edit']" type="danger" plain
                     :disabled="!selectedIds.length || deleting || loading" @click="handleDelete()"
          >
            <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
          </el-button>
        </el-col>
      </el-row>
      <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList" />
    </div>
    <el-table ref="tableRef" v-loading="loading" :data="rows" row-key="id" stripe :default-sort="{prop: 'createTime', order: 'descending'}" @selection-change="handleSelectionChange" @sort-change="handleSortChange">
      <el-table-column type="selection" width="55" align="center" fixed="left"/>
      <el-table-column v-if="visible('id')" prop="id" label="编号" width="80" align="center" fixed="left" sortable="custom" :sort-orders="['descending', 'ascending', null]" :show-overflow-tooltip="{effect: 'light'}" />
      <el-table-column v-if="visible('name')" prop="name" label="属性名称" width="240" fixed="left" :show-overflow-tooltip="{effect: 'light'}" />
      <el-table-column v-if="visible('code')" prop="code" label="属性代码" width="190" :show-overflow-tooltip="{effect: 'light'}" />
      <el-table-column v-if="visible('description')" prop="description" label="描述" min-width="320" :show-overflow-tooltip="{effect: 'light'}">
        <template #default="{row}">{{ row.description || '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('typeCategory')" prop="typeCategory" label="数据类型" width="150" align="center">
        <template #default="{row}"><el-tag :type="row.typeCategory === 2 ? 'success' : 'primary'" effect="light">{{ dataTypeLabel(row.typeCategory) }}</el-tag></template>
      </el-table-column>
      <el-table-column v-if="visible('createBy')" prop="createBy" label="创建人" width="120" align="center" :show-overflow-tooltip="{effect: 'light'}">
        <template #default="{row}">{{ row.createBy || '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('createTime')" prop="createTime" label="创建时间" width="170" align="center" sortable="custom" :sort-orders="['descending', 'ascending', null]">
        <template #default="{row}">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('operation')" label="操作" width="200" align="center" fixed="right" class-name="small-padding fixed-width">
        <template #default="{row}">
          <el-button v-hasPermi="['om:object:type:query']" link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
          <el-button v-hasPermi="['om:object:type:edit']" link type="primary" icon="Edit" :disabled="saving" @click="handleUpdate(row)">修改</el-button>
          <el-button v-hasPermi="['om:object:type:edit']" link type="danger" icon="Delete" :disabled="deleting" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无属性" /><p>暂无属性</p></div>
      </template>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="dialogTitle" width="800px" :append-to="panelRef" :close-on-click-modal="false"
               :close-on-press-escape="!saving" :before-close="beforeClose" draggable destroy-on-close @closed="attributeRef?.clearValidate()"
>
      <el-form ref="attributeRef" class="attribute-dialog-form" :model="form" :rules="rules" label-width="110px" @submit.prevent="submitForm">
        <div class="h2-title">基础信息</div>
        <el-row :gutter="20">
          <el-col :span="24"><el-form-item label="属性名称" prop="name"><el-input v-model="form.name" placeholder="请输入属性名称" maxlength="128" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="属性代码" prop="code"><el-input v-model="form.code" placeholder="例如 purchase_amount" maxlength="128" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" class="character-count" type="textarea" :rows="4" placeholder="请输入描述" :maxlength="500" show-word-limit />
        </el-form-item>
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型" prop="typeCategory">
          <el-radio-group v-model="form.typeCategory" @change="handleDataTypeChange">
            <el-radio v-for="option in dataTypeOptions" :key="option.value" :label="option.value">{{ option.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.typeCategory === 1" label="基础数据类型" prop="basicTypeCode" required>
          <el-select v-model="form.basicTypeCode" placeholder="请选择基础数据类型" style="width: 100%" clearable @change="handleBasicTypeChange">
            <el-option v-for="item in basic_data_type" :key="item.value" :label="item.label" :value="Number(item.value)" />
          </el-select>
        </el-form-item>
        <el-form-item v-else label="业务数据类型" prop="businessTypeId" required>
          <el-select v-model="selectedBusinessKey" placeholder="请选择业务数据类型" style="width: 100%" filterable clearable
                     :loading="businessTypesLoading && !businessDataTypes.length"
                     :no-data-text="businessTypesError ? '加载失败，请重新展开重试' : '暂无可用业务数据类型'"
                     @visible-change="value => value && loadBusinessDataTypes()">
            <template #label>
              <span class="business-type-label">{{ selectedBusinessType?.name || form.businessTypeId }}
                <el-tag :type="form.businessTypeSource === 'PROJECT' ? 'primary' : 'info'" size="small">{{ form.businessTypeSource === 'PROJECT' ? '当前项目' : '全局' }}</el-tag>
              </span>
            </template>
            <el-option-group v-for="group in businessTypeGroups" :key="group.source" :label="group.label">
              <el-option v-for="item in group.options" :key="group.source + ':' + item.id" :label="item.name" :value="group.source + ':' + item.id" />
            </el-option-group>
          </el-select>
          <div v-if="businessTypesError" class="load-warning">部分业务类型加载失败，可继续选择已加载的类型；重新展开可重试。</div>
        </el-form-item>
        <template v-if="form.typeCategory === 2 && form.businessTypeId">
          <el-form-item label="基础数据类型"><div class="form-readonly">{{ basicTypeLabel(effectiveConfig.basicTypeCode) }}</div></el-form-item>
        </template>
        <AttributeTypeConfig v-if="form.typeCategory === 1 || form.businessTypeId" :model="effectiveConfig"
                             :readonly="form.typeCategory === 2" :enum-options="enumOptions" :enum-loading="enumsLoading"
                             :enum-error="enumsError" @enum-open="loadEnums" />
        <el-form-item v-if="form.typeCategory === 2 && selectedBusinessType?.description" label="类型描述">
          <div class="form-readonly textarea">{{ selectedBusinessType.description }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" :disabled="saving" @click="open = false">取 消</el-button>
          <el-button type="primary" size="mini" :loading="saving" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="detailOpen" title="属性详情" width="800px" :append-to="panelRef" draggable>
      <el-form class="attribute-dialog-form" label-width="110px">
        <div class="h2-title">基础信息</div>
        <el-row :gutter="20">
          <el-col :span="24"><el-form-item label="属性名称"><div class="form-readonly">{{ attributeDetail.name }}</div></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="属性代码"><div class="form-readonly">{{ attributeDetail.code }}</div></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述"><div class="form-readonly textarea">{{ attributeDetail.description || '—' }}</div></el-form-item>
        <el-form-item label="创建人"><div class="form-readonly">{{ attributeDetail.createBy || '—' }}</div></el-form-item>
        <el-form-item label="创建时间"><div class="form-readonly">{{ proxy.parseTime(attributeDetail.createTime, '{y}-{m}-{d} {h}:{i}') || '—' }}</div></el-form-item>
        <div class="h2-title">数据定义</div>
        <el-form-item label="数据类型"><div class="form-readonly">{{ dataTypeLabel(attributeDetail.typeCategory) }}</div></el-form-item>
        <el-form-item v-if="attributeDetail.typeCategory === 2" label="业务数据类型"><div class="form-readonly">{{ selectedTypeLabel(attributeDetail) }}</div></el-form-item>
        <el-form-item label="基础数据类型"><div class="form-readonly">{{ basicTypeLabel(detailConfig.basicTypeCode) }}</div></el-form-item>
        <AttributeTypeConfig :model="detailConfig" readonly :enum-options="enumOptions" />
      </el-form>
      <template #footer><div class="dialog-footer"><el-button size="mini" @click="detailOpen = false">关 闭</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import AttributeTypeConfig from './AttributeTypeConfig.vue';
import {listAttributes, getAttribute, addAttribute, updateAttribute, delAttribute, listAttributeBusinessDataTypes, listAttributeEnums} from '@/api/om/object/attribute';

const props = defineProps({
  objectTypeId: {type: [String, Number], required: true},
  versionId: {type: [String, Number], default: undefined}
});
const emit = defineEmits(['changed']);
const {proxy} = getCurrentInstance();
const panelRef = ref();
const attributeRef = ref();
const tableRef = ref();
const rows = ref([]);
const selectedIds = ref([]);
const total = ref(0);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const showSearch = ref(true);
const open = ref(false);
const detailOpen = ref(false);
const dialogTitle = ref('新增属性');
const query = reactive({name: '', pageNum: 1, pageSize: 10, orderByColumn: 'createTime', isAsc: 'descending'});
const appliedName = ref('');
const {om_attribute_data_type, basic_data_type} = proxy.useDict('om_attribute_data_type', 'basic_data_type');
const businessDataTypes = ref([]);
const businessTypesLoading = ref(false);
const businessTypesError = ref(false);
const enumOptions = ref([]);
const enumsLoading = ref(false);
const enumsError = ref('');
const dataTypeOptions = computed(() => om_attribute_data_type.value.map(dict => ({label: dict.label, value: Number(dict.value)})));
const dataTypeLabel = value => dataTypeOptions.value.find(option => option.value === value)?.label || '-';
const columns = ref([
  {prop: 'id', label: '编号'},
  {prop: 'name', label: '属性名称'}, {prop: 'code', label: '属性代码'}, {prop: 'description', label: '描述'},
  {prop: 'typeCategory', label: '数据类型'},
  {prop: 'createBy', label: '创建人'}, {prop: 'createTime', label: '创建时间'}, {prop: 'operation', label: '操作'}
].map((column, key) => ({...column, key, visible: true})));
const visible = prop => columns.value.find(column => column.prop === prop)?.visible;
const emptyForm = () => ({id: undefined, objectTypeId: String(props.objectTypeId), versionId: props.versionId,
  name: '', code: '', typeCategory: 1, basicTypeCode: undefined, businessTypeId: undefined, businessTypeSource: 'GLOBAL',
  unit: '', decimalPlaces: 2, format: '', enumType: undefined, enumSource: 'PROJECT', description: ''});
const form = ref(emptyForm());
const attributeDetail = ref({});
const selectedBusinessType = computed(() => findBusinessType(form.value));
const selectedBusinessKey = computed({
  get: () => form.value.businessTypeId ? `${form.value.businessTypeSource || 'GLOBAL'}:${form.value.businessTypeId}` : undefined,
  set: value => {
    const at = (value || '').indexOf(':');
    form.value.businessTypeSource = at < 0 ? 'GLOBAL' : value.slice(0, at);
    form.value.businessTypeId = at < 0 ? undefined : value.slice(at + 1);
    clearTypeConfig();
  }
});
const businessTypeGroups = computed(() => [{source: 'PROJECT', label: '当前项目'}, {source: 'GLOBAL', label: '全局'}]
  .map(group => ({...group, options: businessDataTypes.value.filter(item => item.businessTypeSource === group.source)}))
  .filter(group => group.options.length));
function findBusinessType(attribute) {
  return businessDataTypes.value.find(item => String(item.id) === String(attribute.businessTypeId)
    && item.businessTypeSource === (attribute.businessTypeSource || 'GLOBAL'));
}
function resolvedConfig(attribute) {
  const selected = attribute.typeCategory === 2 ? findBusinessType(attribute) : undefined;
  return selected ? {...selected, basicTypeCode: optionalNumber(selected.dataType)} : attribute;
}
const effectiveConfig = computed(() => resolvedConfig(form.value));
const detailConfig = computed(() => resolvedConfig(attributeDetail.value));
const basicTypeLabel = value => basic_data_type.value.find(item => Number(item.value) === Number(value))?.label || '未设置';
const validateConfig = (field, requiredTypes, message) => ({
  required: field !== 'enumType',
  validator: (_rule, value, callback) => {
    if (form.value.typeCategory !== 1 || !requiredTypes.includes(form.value.basicTypeCode)) return callback();
    if (value == null || value === '' || (typeof value === 'string' && !value.trim())) {
      return callback(field === 'enumType' ? undefined : new Error(message));
    }
    const invalid = field === 'decimalPlaces' ? !Number.isInteger(value) || value < 0 : value == null || !String(value).trim();
    callback(invalid ? new Error(message) : undefined);
  }, trigger: 'change'
});
const rules = {
  name: [{required: true, whitespace: true, message: '属性名称不能为空', trigger: 'blur'}, {max: 128, message: '属性名称不能超过128个字符', trigger: 'blur'}],
  code: [{required: true, message: '属性代码不能为空', trigger: 'blur'}, {pattern: /^[A-Za-z][A-Za-z0-9_]*$/, message: '须以字母开头，仅允许字母、数字和下划线', trigger: 'blur'}, {max: 128, message: '属性代码不能超过128个字符', trigger: 'blur'}],
  typeCategory: [{required: true, type: 'enum', enum: [1, 2], message: '请选择数据类型', trigger: 'change'}],
  basicTypeCode: [{validator: (_rule, value, callback) => callback(form.value.typeCategory === 1 && !basic_data_type.value.some(item => Number(item.value) === value) ? new Error('请选择基础数据类型') : undefined), trigger: 'change'}],
  businessTypeId: [{validator: (_rule, value, callback) => callback(form.value.typeCategory === 2 && !findBusinessType(form.value) ? new Error('请选择有效的业务数据类型') : undefined), trigger: 'change'}],
  unit: [validateConfig('unit', [1, 2], '请输入单位')],
  decimalPlaces: [validateConfig('decimalPlaces', [2], '请输入有效的小数位数')],
  format: [validateConfig('format', [4], '请输入日期格式')],
  enumType: [validateConfig('enumType', [5], '请选择枚举类型')],
  description: [{max: 500, message: '描述不能超过500个字符', trigger: 'blur'}]
};
let listRequest = 0;
let dialogRequest = 0;
let contextRequest = 0;
let businessRequest = 0;
let enumRequest = 0;

const optionalNumber = value => value == null || value === '' ? undefined : Number(value);
function normalizeAttribute(attribute = {}) {
  return {
    ...attribute,
    typeCategory: optionalNumber(attribute.typeCategory),
    basicTypeCode: optionalNumber(attribute.basicTypeCode),
    decimalPlaces: optionalNumber(attribute.decimalPlaces),
    businessTypeSource: attribute.businessTypeSource || 'GLOBAL',
    businessTypeId: attribute.businessTypeId == null || attribute.businessTypeId === ''
      ? undefined : String(attribute.businessTypeId)
  };
}

function selectedTypeLabel(attribute) {
  if (attribute.typeCategory !== 2) return basicTypeLabel(attribute.basicTypeCode);
  const selected = findBusinessType(attribute);
  const origin = attribute.businessTypeSource === 'PROJECT' ? '当前项目' : '全局';
  return selected ? `${selected.name}（${origin}）`
    : attribute.businessTypeId ? `已失效或不可用（${origin}：${attribute.businessTypeId}）` : '未设置';
}
async function loadBusinessDataTypes() {
  if (businessTypesLoading.value) return;
  const request = ++businessRequest;
  const objectId = props.objectTypeId;
  businessTypesLoading.value = true;
  businessTypesError.value = false;
  try {
    await Promise.allSettled(['PROJECT', 'GLOBAL'].map(async source => {
      try {
        const response = await listAttributeBusinessDataTypes(objectId, source);
        if (request !== businessRequest) return;
        if (!Array.isArray(response.data)) throw new Error('业务类型返回格式无效');
        businessDataTypes.value = [...businessDataTypes.value.filter(item => item.businessTypeSource !== source),
          ...response.data.map(item => ({...item, id: String(item.id), businessTypeSource: source}))];
      } catch { if (request === businessRequest) businessTypesError.value = true; }
    }));
  } finally { if (request === businessRequest) businessTypesLoading.value = false; }
}
async function loadEnums() {
  if (enumsLoading.value) return;
  const request = ++enumRequest;
  const objectId = props.objectTypeId;
  enumsLoading.value = true;
  enumsError.value = '';
  const failures = [];
  try {
    await Promise.allSettled(['PROJECT', 'GLOBAL'].map(async source => {
      try {
        const response = await listAttributeEnums(objectId, source);
        if (request !== enumRequest) return;
        if (!Array.isArray(response.data)) throw new Error('枚举返回格式无效');
        enumOptions.value = [...enumOptions.value.filter(item => item.enumSource !== source),
          ...response.data.map(item => ({...item, enumSource: source}))];
      } catch {
        if (request === enumRequest) {
          failures.push(source === 'PROJECT' ? '当前项目' : '全局');
          enumsError.value = `${failures.join('、')}枚举加载失败，可选择已加载的枚举；重新展开可重试。`;
        }
      }
    }));
  } finally { if (request === enumRequest) enumsLoading.value = false; }
}
function clearTypeConfig() {
  Object.assign(form.value, {unit: '', decimalPlaces: 2, format: '', enumType: undefined, enumSource: 'PROJECT'});
  nextTick(() => attributeRef.value?.clearValidate(['unit', 'decimalPlaces', 'format', 'enumType']));
}
function handleBasicTypeChange() { clearTypeConfig(); }
function handleDataTypeChange() {
  form.value.basicTypeCode = undefined;
  form.value.businessTypeId = undefined;
  form.value.businessTypeSource = 'GLOBAL';
  clearTypeConfig();
  nextTick(() => attributeRef.value?.clearValidate(['basicTypeCode', 'businessTypeId']));
}
watch(open, value => { if (value) { loadBusinessDataTypes(); loadEnums(); } });
watch(detailOpen, value => { if (value) { loadBusinessDataTypes(); loadEnums(); } });

async function getList() {
  const request = ++listRequest;
  loadBusinessDataTypes();
  loading.value = true;
  try {
    const response = await listAttributes({objectTypeId: props.objectTypeId, versionId: props.versionId,
      name: appliedName.value, pageNum: query.pageNum, pageSize: query.pageSize,
      orderByColumn: query.orderByColumn, isAsc: query.isAsc});
    if (request !== listRequest) return;
    const page = response.data || {};
    total.value = Number(page.total || 0);
    const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
    if (query.pageNum > maxPage) { query.pageNum = maxPage; return await getList(); }
    rows.value = (page.rows || []).map(normalizeAttribute);
    selectedIds.value = [];
    tableRef.value?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}
function search() { appliedName.value = query.name.trim(); query.pageNum = 1; getList(); }
function resetSearch() { query.name = ''; search(); }
function handleSortChange({prop, order}) {
  query.orderByColumn = order ? prop : undefined;
  query.isAsc = order || undefined;
  query.pageNum = 1;
  getList();
}
function handleAdd() {
  ++dialogRequest;
  form.value = emptyForm();
  dialogTitle.value = '新增属性';
  open.value = true;
  nextTick(() => attributeRef.value?.clearValidate());
}
async function handleUpdate(row) {
  const request = ++dialogRequest;
  const response = await getAttribute(row.id, props.objectTypeId);
  if (request !== dialogRequest) return;
  form.value = {...emptyForm(), ...normalizeAttribute(response.data)};
  dialogTitle.value = '修改属性';
  open.value = true;
  nextTick(() => attributeRef.value?.clearValidate());
}
async function handleDetail(row) {
  const request = ++dialogRequest;
  const response = await getAttribute(row.id, props.objectTypeId);
  if (request !== dialogRequest) return;
  attributeDetail.value = normalizeAttribute(response.data);
  detailOpen.value = true;
}
async function submitForm() {
  const context = contextRequest;
  if (saving.value || !await attributeRef.value.validate().catch(() => false)) return;
  if (saving.value || context !== contextRequest || !open.value) return;
  ++dialogRequest;
  const data = {id: form.value.id, objectTypeId: form.value.objectTypeId, versionId: form.value.versionId,
    name: form.value.name.trim(), code: form.value.code.trim(), typeCategory: form.value.typeCategory,
    basicTypeCode: form.value.typeCategory === 1 ? form.value.basicTypeCode : null,
    businessTypeId: form.value.typeCategory === 2 ? form.value.businessTypeId : null,
    businessTypeSource: form.value.typeCategory === 2 ? form.value.businessTypeSource : null,
    unit: form.value.typeCategory === 1 && [1, 2].includes(form.value.basicTypeCode) ? form.value.unit : null,
    decimalPlaces: form.value.typeCategory === 1 && form.value.basicTypeCode === 2 ? form.value.decimalPlaces : null,
    format: form.value.typeCategory === 1 && form.value.basicTypeCode === 4 ? form.value.format : null,
    enumType: form.value.typeCategory === 1 && form.value.basicTypeCode === 5 ? form.value.enumType : null,
    enumSource: form.value.typeCategory === 1 && form.value.basicTypeCode === 5 ? form.value.enumSource : null,
    description: (form.value.description || '').trim()};
  saving.value = true;
  try {
    await (data.id ? updateAttribute(data) : addAttribute(data));
    if (context !== contextRequest) return;
    ElMessage.success(data.id ? '修改成功' : '新增成功');
    open.value = false;
    await getList();
    if (context === contextRequest) emit('changed');
  } finally { saving.value = false; }
}
function beforeClose(done) { if (!saving.value) done(); }
function handleSelectionChange(selection) { selectedIds.value = selection.map(row => row.id); }
async function handleDelete(row) {
  if (deleting.value) return;
  const ids = row ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  const context = contextRequest;
  const objectTypeId = props.objectTypeId;
  try {
    await ElMessageBox.confirm(row ? `是否删除属性“${row.name}”？` : `是否删除选中的 ${ids.length} 条属性？`,
      '删除确认', {type: 'warning'});
  } catch { return; }
  if (context !== contextRequest || deleting.value) return;
  deleting.value = true;
  try {
    await delAttribute(ids, objectTypeId);
    if (context !== contextRequest) return;
    ElMessage.success('删除成功');
    await getList();
    if (context === contextRequest) emit('changed');
  } finally { deleting.value = false; }
}
watch(() => [props.objectTypeId, props.versionId], () => {
  ++contextRequest;
  ++dialogRequest;
  ++businessRequest;
  ++enumRequest;
  businessDataTypes.value = [];
  enumOptions.value = [];
  businessTypesLoading.value = false;
  businessTypesError.value = false;
  enumsLoading.value = false;
  enumsError.value = '';
  open.value = false;
  detailOpen.value = false;
  rows.value = [];
  selectedIds.value = [];
  total.value = 0;
  query.name = '';
  appliedName.value = '';
  query.pageNum = 1;
  getList();
}, {immediate: true});
onBeforeUnmount(() => { ++contextRequest; ++dialogRequest; ++listRequest; ++businessRequest; ++enumRequest; });
</script>

<style scoped>
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
  content: '';
  width: 6px;
  height: 16px;
  border-radius: 3px;
  background: var(--el-color-primary);
  margin-right: 8px;
}
.business-type-label { display: inline-flex; align-items: center; gap: 6px; }
.load-warning { color: var(--el-color-warning); font-size: 12px; line-height: 20px; }

.attribute-dialog-form :deep(.el-form-item__label) { white-space: nowrap; flex-shrink: 0; }
.character-count :deep(.el-input__count) { background: transparent; }
.character-count :deep(.el-input__count)::after { content: '个字符'; }
.character-count :deep(.el-textarea__inner) { padding-bottom: 24px; }
</style>
