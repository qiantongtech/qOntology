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
  <div ref="pageRef" class="app-container data-rule-page">
    <GuideTip tip-id="om/constraint/data.list"/>
    <div v-show="showSearch" class="pagecont-top">
      <el-form ref="queryRef" class="btn-style" :model="query" :inline="true" @submit.prevent="search">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="query.name" class="el-form-input-width" placeholder="请输入名称" maxlength="128"
                    clearable @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="search">
            <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetSearch">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button v-hasPermi="['om:constraint:data:add']" type="primary" plain :disabled="saving"
                       @click="handleAdd"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['om:constraint:data:remove']" type="danger" plain
                       :disabled="!selectedIds.length || deleting || loading" @click="handleDelete()"
            >
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"/>
      </div>
      <el-table ref="tableRef" v-loading="loading" :data="rows" row-key="id" stripe
                @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="visible('index')" type="index" :index="getRowIndex" label="序号" width="70" align="center"/>
        <el-table-column v-if="visible('name')" prop="name" label="名称" min-width="160"
                         :show-overflow-tooltip="{effect: 'light'}"
        />
        <el-table-column v-if="visible('description')" prop="description" label="描述" min-width="240"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.description || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('type')" label="规则类型" min-width="145" align="center">
          <template #default="{row}">
            <dict-tag :options="om_data_rule_type" :value="row.type"/>
          </template>
        </el-table-column>
        <el-table-column v-if="visible('level')" label="级别" width="90" align="center">
          <template #default="{row}">
            <dict-tag :options="om_rule_level" :value="row.level"/>
          </template>
        </el-table-column>
        <el-table-column v-if="visible('createBy')" prop="createBy" label="创建人" width="120" align="center"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.createBy || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('createTime')" label="创建时间" width="180" align="center">
          <template #default="{row}">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('operation')" label="操作" width="210" align="center" fixed="right"
                         class-name="small-padding fixed-width"
        >
          <template #default="{row}">
            <el-button v-hasPermi="['om:constraint:data:query']" link type="primary" icon="View"
                       @click="handleDetail(row)"
            >详情
            </el-button>
            <el-button v-hasPermi="['om:constraint:data:edit']" link type="primary" icon="Edit"
                       :disabled="saving" @click="handleUpdate(row)"
            >修改
            </el-button>
            <el-button v-hasPermi="['om:constraint:data:remove']" link type="danger" icon="Delete"
                       :disabled="deleting" @click="handleDelete(row)"
            >删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="暂无数据规则"/>
            <p>{{ versionId ? '暂无数据规则' : '请先选择项目版本' }}</p>
          </div>
        </template>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize"
                  @pagination="getList"
      />
    </div>
    <el-dialog v-model="open" :title="dialogTitle" width="860px" :append-to="pageRef"
               :close-on-press-escape="!saving" :before-close="beforeClose" draggable destroy-on-close
               @closed="handleDialogClosed"
    >
      <el-alert v-if="optionsError" :title="optionsError" type="error" :closable="false" show-icon class="options-error">
        <el-button link type="primary" @click="loadOptions">重新加载</el-button>
      </el-alert>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="120px" class="rule-form"
               :disabled="saving || optionsLoading" @submit.prevent="submitForm"
      >
        <div class="h2-title">基本信息</div>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入数据规则名称" maxlength="128"/>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option v-for="item in om_data_rule_type" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio v-for="item in om_rule_level" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" class="character-count" type="textarea" :rows="4"
                    placeholder="请输入描述" :maxlength="500" show-word-limit
          />
        </el-form-item>
        <div class="h2-title">执行条件</div>
        <el-form-item label="条件关系" prop="conditionRelation">
          <el-radio-group v-model="form.conditionRelation">
            <el-radio value="OR">任一满足（OR）</el-radio>
            <el-radio value="AND">全部满足（AND）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="失败条件" required>
          <div class="conditions">
            <div v-for="(condition, index) in form.conditions" :key="condition.key" class="condition-row">
              <div class="condition-label">条件 {{ index + 1 }}：</div>
              <div class="condition-fields">
                <el-select :ref="element => setConditionSelectRef(condition.key, element)"
                           v-model="condition.objectTypeId" filterable placeholder="请选择对象类型"
                           class="condition-object"
                           :aria-label="'第' + (index + 1) + '条对象类型'" @change="handleConditionObjectChange(condition)"
                >
                  <el-option v-for="item in objectTypeOptions" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-select v-model="condition.attributeId" filterable :disabled="!condition.objectTypeId"
                           class="condition-attribute"
                           :placeholder="attributePlaceholder" :aria-label="'第' + (index + 1) + '条属性'"
                           @change="handleConditionAttributeChange(condition)"
                >
                  <el-option v-for="item in getConditionAttributes(condition)" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <span v-if="form.type === 'REQUIRED'" class="condition-operator">为空</span>
                <template v-else>
                  <el-select v-if="form.type === 'RANGE'" v-model="condition.operator" class="condition-comparison"
                             :aria-label="'第' + (index + 1) + '条比较符'"
                  >
                    <el-option v-for="item in om_rule_operator" :key="item.value" :label="item.label" :value="item.value"/>
                  </el-select>
                  <span v-else class="condition-operator">{{ conditionOperatorLabel }}</span>
                  <el-input v-model="condition.value" :placeholder="conditionValuePlaceholder"
                            class="condition-value"
                            :aria-label="'第' + (index + 1) + '条条件值'"
                  />
                </template>
              </div>
              <div class="condition-actions">
                <el-button link type="danger" icon="Delete" :disabled="form.conditions.length === 1"
                           :aria-label="'删除条件' + (index + 1)" :title="form.conditions.length === 1 ? '至少保留一条条件' : '删除条件'"
                           @click="removeCondition(index)"
                />
                <el-button v-if="index === form.conditions.length - 1" link type="primary" icon="Plus" @click="addCondition">
                  添加条件
                </el-button>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="失败提示" prop="failureMessage">
          <el-input v-model="form.failureMessage" placeholder="请输入失败提示" maxlength="512"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" :disabled="saving" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" :loading="saving" :disabled="saveDisabled" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="detailOpen" title="数据规则详情" width="860px" :append-to="pageRef" draggable destroy-on-close>
      <el-form label-width="120px">
        <el-form-item v-for="field in detailFields" :key="field.label" :label="field.label + '：'">
          <div class="form-readonly" :class="{textarea: field.multiline}">{{ field.value || '-' }}</div>
        </el-form-item>
      </el-form>
      <div class="h2-title">失败条件</div>
      <el-table :data="detail.conditions || []" border>
        <el-table-column label="对象类型" min-width="130">
          <template #default="{row}">{{ objectTypeLabel(row.objectTypeId) }}</template>
        </el-table-column>
        <el-table-column label="属性" min-width="130">
          <template #default="{row}">{{ attributeLabel(row.objectTypeId, row.attributeId) }}</template>
        </el-table-column>
        <el-table-column label="条件" width="110">
          <template #default="{row}">{{ conditionOperator(detail.type, row.operator) }}</template>
        </el-table-column>
        <el-table-column v-if="detail.type !== 'REQUIRED'" prop="value" label="条件值" min-width="180"
                         :show-overflow-tooltip="{effect: 'light'}"
        />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import useUserStore from '@/store/system/user';
import {listRule, getRule, addRule, updateRule, delRule} from '@/api/om/constraint/rule';
import {listRelationTypes} from '@/api/om/object/relation';
import {listAttributes, listAttributeBusinessDataTypes} from '@/api/om/object/attribute';

defineOptions({name: 'OmDataRule'});
const props = defineProps({versionId: {type: [String, Number], default: undefined}});
const {versionId, requireVersion} = useOntologyVersion(() => props.versionId);
const {proxy} = getCurrentInstance();
const userStore = useUserStore();
const {om_data_rule_type, om_rule_level, om_rule_operator} = proxy.useDict(
  'om_data_rule_type', 'om_rule_level', 'om_rule_operator'
);

const pageRef = ref();
const tableRef = ref();
const queryRef = ref();
const ruleRef = ref();
const rows = ref([]);
const selectedIds = ref([]);
const total = ref(0);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const open = ref(false);
const detailOpen = ref(false);
const dialogTitle = ref('新增数据规则');
const detail = ref({});
const showSearch = ref(true);
const query = reactive({name: '', pageNum: 1, pageSize: 10});
const appliedName = ref('');
const objectTypeOptions = ref([]);
const attributeOptions = reactive({});
const businessTypeOptions = reactive({});
const optionsLoading = ref(false);
const optionsError = ref('');
const attributeLoadingCount = ref(0);
const conditionSelectRefs = new Map();
const columns = ref([
  {key: 0, prop: 'index', label: '序号', visible: true},
  {key: 1, prop: 'name', label: '名称', visible: true},
  {key: 2, prop: 'description', label: '描述', visible: true},
  {key: 3, prop: 'type', label: '规则类型', visible: true},
  {key: 4, prop: 'level', label: '级别', visible: true},
  {key: 5, prop: 'createBy', label: '创建人', visible: true},
  {key: 6, prop: 'createTime', label: '创建时间', visible: true},
  {key: 7, prop: 'operation', label: '操作', visible: true}
]);
const visible = prop => columns.value.find(column => column.prop === prop)?.visible;
const typeLabel = value => proxy.selectDictLabel(om_data_rule_type.value, value) || '-';
const levelLabel = value => proxy.selectDictLabel(om_rule_level.value, value) || '-';
const saveDisabled = computed(() => optionsLoading.value || attributeLoadingCount.value > 0 || !!optionsError.value);
const emptyForm = () => ({
  id: undefined,
  versionId: versionId.value,
  category: 'DATA',
  name: '',
  description: '',
  level: 'INFO',
  type: 'REQUIRED',
  conditionRelation: 'OR',
  conditions: [createCondition()],
  failureMessage: ''
});
const form = ref(emptyForm());
const rules = {
  name: [
    {required: true, whitespace: true, message: '数据规则名称不能为空', trigger: 'blur'},
    {max: 128, message: '名称不能超过128个字符', trigger: 'blur'}
  ],
  type: [{required: true, message: '请选择类型', trigger: 'change'}],
  level: [{required: true, message: '请选择级别', trigger: 'change'}],
  description: [
    {required: true, whitespace: true, message: '描述不能为空', trigger: 'blur'},
    {max: 500, message: '描述不能超过500个字符', trigger: 'blur'}
  ],
  conditionRelation: [{required: true, message: '请选择条件关系', trigger: 'change'}],
  failureMessage: [
    {required: true, whitespace: true, message: '失败提示不能为空', trigger: 'blur'},
    {max: 512, message: '失败提示不能超过512个字符', trigger: 'blur'}
  ]
};
let listRequest = 0;
let dialogRequest = 0;
let contextRequest = 0;
const attributePlaceholder = computed(() => {
  if (form.value.type === 'RANGE') return '请选择整数或小数属性';
  return form.value.type === 'FORMAT' ? '请选择日期属性' : '请选择属性';
});
const conditionOperatorLabel = computed(() => form.value.type === 'ENUM' ? '不属于' : '不匹配');
const conditionValuePlaceholder = computed(() => {
  if (form.value.type === 'ENUM') return '手工输入，以逗号分隔';
  return form.value.type === 'RANGE' ? '请输入数值' : '请输入内容';
});

const detailFields = computed(() => [
  {label: '名称', value: detail.value.name},
  {label: '类型', value: typeLabel(detail.value.type)},
  {label: '级别', value: levelLabel(detail.value.level)},
  {label: '描述', value: detail.value.description, multiline: true},
  ...getDetailFields(detail.value),
  {label: '创建人', value: detail.value.createBy},
  {label: '创建时间', value: proxy.parseTime(detail.value.createTime, '{y}-{m}-{d} {h}:{i}')}
]);

// 下拉选项需要读取全部分页数据，避免只显示第一页属性或关系。
async function loadAllPages(loadPage) {
  const rows = [];
  let pageNum = 1;
  let total = 0;
  do {
    const response = await loadPage(pageNum++);
    const page = response.data || {};
    const batch = page.rows || [];
    rows.push(...batch);
    total = Number(page.total || 0);
    if (!batch.length) break;
  } while (rows.length < total);
  return rows;
}

async function listObjectTypeOptions(versionId) {
  const response = await listRelationTypes(versionId);
  return (response.data || []).map(item => ({...item, id: String(item.id)}));
}

async function listObjectAttributeOptions(objectTypeId, versionId) {
  const results = await Promise.allSettled([
    loadAllPages(pageNum => listAttributes({objectTypeId, versionId, pageNum, pageSize: 100})),
    listAttributeBusinessDataTypes(objectTypeId, 'PROJECT'),
    listAttributeBusinessDataTypes(objectTypeId, 'GLOBAL')
  ]);
  const failed = results.find(result => result.status === 'rejected');
  if (failed) throw failed.reason;
  return {
    attributes: results[0].value.map(item => ({...item, id: String(item.id)})),
    businessTypes: [
      ...(results[1].value.data || []).map(item => ({...item, businessTypeSource: 'PROJECT'})),
      ...(results[2].value.data || []).map(item => ({...item, businessTypeSource: 'GLOBAL'}))
    ]
  };
}

function createCondition() {
  return {key: globalThis.crypto.randomUUID(), objectTypeId: '', attributeId: '', operator: 'LT', value: ''};
}

function getBasicTypeCode(attribute, businessTypes) {
  if (Number(attribute.typeCategory) !== 2) return Number(attribute.basicTypeCode);
  const businessType = businessTypes.find(item => String(item.id) === String(attribute.businessTypeId)
    && item.businessTypeSource === (attribute.businessTypeSource || 'GLOBAL'));
  return businessType ? Number(businessType.dataType) : undefined;
}

function validateConditions(form, attributes, businessTypes) {
  if (!form.conditions.length) return '请至少添加一条失败条件';
  for (const [index, condition] of form.conditions.entries()) {
    const prefix = `第 ${index + 1} 条失败条件：`;
    if (!condition.objectTypeId || !condition.attributeId) return `${prefix}请选择对象类型和属性`;
    const attribute = (attributes[condition.objectTypeId] || []).find(item => item.id === condition.attributeId);
    if (!attribute) return `${prefix}属性已失效，请重新选择`;
    if (form.type === 'RANGE') {
      const code = getBasicTypeCode(attribute, businessTypes[condition.objectTypeId] || []);
      if (![1, 2].includes(code)) return `${prefix}只能选择整数或小数属性`;
      if (!om_rule_operator.value.some(item => item.value === condition.operator)) return `${prefix}请选择比较符`;
      const value = String(condition.value).trim();
      if (!/^[+-]?(?:\d+(?:\.\d*)?|\.\d+)$/.test(value)) return `${prefix}请输入有效数值`;
      if (code === 1 && !/^[+-]?\d+$/.test(value)) return `${prefix}请输入整数`;
    } else if (form.type === 'FORMAT' && getBasicTypeCode(attribute, businessTypes[condition.objectTypeId] || []) !== 4) {
      return `${prefix}只能选择日期属性`;
    } else if (form.type === 'ENUM' && !String(condition.value).trim()) {
      return `${prefix}请输入枚举值`;
    }
  }
  // 格式校验只保存自由文本，不解析或限制正则表达式内容。
  return '';
}

function getRowIndex(index) {
  return (query.pageNum - 1) * query.pageSize + index + 1;
}

async function getList() {
  const request = ++listRequest;
  if (!versionId.value) {
    rows.value = [];
    total.value = 0;
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const response = await listRule({
      versionId: versionId.value,
      category: 'DATA',
      name: appliedName.value,
      pageNum: query.pageNum,
      pageSize: query.pageSize
    });
    if (request !== listRequest) return;
    total.value = Number(response.data?.total || 0);
    const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
    if (query.pageNum > maxPage) {
      query.pageNum = maxPage;
      return await getList();
    }
    rows.value = response.data?.rows || [];
    selectedIds.value = [];
    tableRef.value?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

function search() {
  appliedName.value = query.name.trim();
  query.pageNum = 1;
  return getList();
}

function resetSearch() {
  queryRef.value?.resetFields();
  return search();
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id);
}

async function loadObjectOptions(objectTypeId) {
  if (!objectTypeId) return;
  const request = dialogRequest;
  attributeLoadingCount.value++;
  try {
    const response = await listObjectAttributeOptions(objectTypeId, versionId.value);
    if (request !== dialogRequest) return;
    attributeOptions[objectTypeId] = response.attributes;
    businessTypeOptions[objectTypeId] = response.businessTypes;
  } catch {
    if (request === dialogRequest) optionsError.value = '属性或业务数据类型加载失败，请重新加载';
  } finally {
    if (request === dialogRequest) attributeLoadingCount.value--;
  }
}

async function loadOptions() {
  const request = dialogRequest;
  optionsLoading.value = true;
  optionsError.value = '';
  try {
    const options = await listObjectTypeOptions(versionId.value);
    if (request !== dialogRequest) return;
    objectTypeOptions.value = options;
    const record = detailOpen.value ? detail.value : form.value;
    const objectIds = getRecordObjectIds(record);
    await Promise.allSettled(objectIds.map(loadObjectOptions));
  } catch {
    if (request === dialogRequest) optionsError.value = '选项加载失败，请重新加载';
  } finally {
    if (request === dialogRequest) optionsLoading.value = false;
  }
}

function handleAdd() {
  if (!requireVersion()) return;
  ++dialogRequest;
  attributeLoadingCount.value = 0;
  form.value = emptyForm();
  detailOpen.value = false;
  dialogTitle.value = '新增数据规则';
  open.value = true;
  nextTick(() => ruleRef.value?.clearValidate());
  loadOptions();
}

async function handleUpdate(row) {
  if (!requireVersion()) return;
  const request = ++dialogRequest;
  const response = await getRule(row.id, versionId.value, 'DATA');
  if (request !== dialogRequest) return;
  attributeLoadingCount.value = 0;
  form.value = {
    ...emptyForm(),
    ...response.data,
    conditions: response.data?.conditions?.length
      ? response.data.conditions.map(condition => ({...condition, key: globalThis.crypto.randomUUID()}))
      : [createCondition()]
  };
  detailOpen.value = false;
  dialogTitle.value = '修改数据规则';
  open.value = true;
  nextTick(() => ruleRef.value?.clearValidate());
  loadOptions();
}

async function handleDetail(row) {
  if (!requireVersion()) return;
  const request = ++dialogRequest;
  const response = await getRule(row.id, versionId.value, 'DATA');
  if (request !== dialogRequest) return;
  attributeLoadingCount.value = 0;
  detail.value = response.data;
  await loadDetailOptions();
  if (request === dialogRequest) detailOpen.value = true;
}

async function loadDetailOptions() {
  const request = dialogRequest;
  optionsError.value = '';
  try {
    const options = await listObjectTypeOptions(versionId.value);
    if (request !== dialogRequest) return;
    objectTypeOptions.value = options;

    await Promise.allSettled(getRecordObjectIds(detail.value).map(loadObjectOptions));
  } catch {
    if (request === dialogRequest) ElMessage.warning('部分选项加载失败，详情暂时显示对应编号');
  }
}

async function submitForm() {
  if (!requireVersion() || saving.value || saveDisabled.value) return;
  const request = dialogRequest;
  const context = contextRequest;
  if (!await ruleRef.value.validate().catch(() => false)) return;
  if (request !== dialogRequest || !open.value || saving.value || saveDisabled.value) return;
  const error = validateExpression();
  if (error) {
    ElMessage.warning(error);
    return;
  }
  const data = {
    ...JSON.parse(JSON.stringify(form.value)),
    versionId: versionId.value,
    name: form.value.name.trim(),
    description: form.value.description.trim(),
    createBy: form.value.createBy || userStore.nickName || userStore.name || '当前用户'
  };
  saving.value = true;
  try {
    await (data.id ? updateRule(data) : addRule(data));
    if (context !== contextRequest) return;
    ElMessage.success(data.id ? '修改成功' : '新增成功');
    open.value = false;
    await getList();
  } catch (error) {
    if (context === contextRequest) ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
}

async function handleDelete(row) {
  if (!requireVersion() || deleting.value) return;
  const ids = row ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  const context = contextRequest;
  const deletingVersion = versionId.value;
  try {
    const message = row ? '是否删除数据规则“' + row.name + '”？' : '是否删除选中的 ' + ids.length + ' 条数据规则？';
    await ElMessageBox.confirm(message, '删除确认', {type: 'warning'});
  } catch {
    return;
  }
  if (context !== contextRequest || deleting.value) return;
  deleting.value = true;
  try {
    await delRule(ids, deletingVersion, 'DATA');
    if (context !== contextRequest) return;
    ElMessage.success('删除成功');
    await getList();
  } finally {
    deleting.value = false;
  }
}

function cancel() {
  open.value = false;
}

function beforeClose(done) {
  if (!saving.value) done();
}

function handleDialogClosed() {
  ruleRef.value?.clearValidate();
}

function objectTypeLabel(id) {
  return objectTypeOptions.value.find(item => item.id === id)?.name || id || '-';
}

function attributeLabel(objectTypeId, attributeId) {
  return (attributeOptions[objectTypeId] || []).find(item => item.id === attributeId)?.name || attributeId || '-';
}

function getRecordObjectIds(record) {
  return [...new Set((record.conditions || []).map(item => item.objectTypeId).filter(Boolean))];
}

function handleTypeChange() {
  form.value.conditions = [createCondition()];
  nextTick(() => ruleRef.value?.clearValidate());
}

function handleConditionObjectChange(condition) {
  condition.attributeId = '';
  condition.value = '';
  loadObjectOptions(condition.objectTypeId);
}

function handleConditionAttributeChange(condition) {
  condition.value = '';
}

function getConditionAttributes(condition) {
  const attributes = attributeOptions[condition.objectTypeId] || [];
  if (!['RANGE', 'FORMAT'].includes(form.value.type)) return attributes;
  const businessTypes = businessTypeOptions[condition.objectTypeId] || [];
  const typeCodes = form.value.type === 'FORMAT' ? [4] : [1, 2];
  return attributes.filter(item => typeCodes.includes(getBasicTypeCode(item, businessTypes)));
}

function setConditionSelectRef(key, element) {
  if (element) conditionSelectRefs.set(key, element);
  else conditionSelectRefs.delete(key);
}

async function addCondition() {
  const condition = createCondition();
  form.value.conditions.push(condition);
  await nextTick();
  conditionSelectRefs.get(condition.key)?.focus();
}

function removeCondition(index) {
  if (form.value.conditions.length > 1) form.value.conditions.splice(index, 1);
}

function validateExpression() {
  return validateConditions(form.value, attributeOptions, businessTypeOptions);
}

function getDetailFields(record) {
  return [
    {label: '条件关系', value: record.conditionRelation === 'AND' ? '全部满足（AND）' : '任一满足（OR）'},
    {label: '失败提示', value: record.failureMessage, multiline: true}
  ];
}

function conditionOperator(type, operator) {
  if (type === 'REQUIRED') return '为空';
  if (type === 'ENUM') return '不属于';
  if (type === 'FORMAT') return '不匹配';
  return proxy.selectDictLabel(om_rule_operator.value, operator);
}

watch(versionId, () => {
  ++contextRequest;
  ++dialogRequest;
  open.value = false;
  detailOpen.value = false;
  optionsLoading.value = false;
  attributeLoadingCount.value = 0;
  optionsError.value = '';
  objectTypeOptions.value = [];
  Object.keys(attributeOptions).forEach(key => delete attributeOptions[key]);
  Object.keys(businessTypeOptions).forEach(key => delete businessTypeOptions[key]);
  query.name = '';
  query.pageNum = 1;
  appliedName.value = '';
  rows.value = [];
  selectedIds.value = [];
  total.value = 0;
  getList();
}, {immediate: true});

watch(open, value => {
  if (value) return;
  ++dialogRequest;
  attributeLoadingCount.value = 0;
  optionsLoading.value = false;
});

onBeforeUnmount(() => {
  ++contextRequest;
  ++dialogRequest;
  ++listRequest;
});
</script>

<style scoped>
.data-rule-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.rule-form {
  min-height: 390px;
}

.rule-form .el-select {
  width: 100%;
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
  content: '';
  width: 6px;
  height: 16px;
  border-radius: 3px;
  background: var(--el-color-primary);
  margin-right: 8px;
}

.character-count :deep(.el-input__count) {
  background: transparent;
}

.character-count :deep(.el-input__count)::after {
  content: '个字符';
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}
.options-error {
  margin-bottom: 16px;
}

.conditions {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 10px 12px;
  background: #f5f8ff;
  border: 1px solid #d9e2ff;
  border-radius: 4px;
}

.condition-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.condition-label {
  flex: 0 0 62px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.condition-fields {
  display: flex;
  align-items: center;
  flex: 1 1 330px;
  min-width: 0;
  gap: 10px;
  flex-wrap: wrap;
}

.condition-fields > .condition-object,
.condition-fields > .condition-attribute {
  flex: 1 1 140px;
  width: auto;
  min-width: 0;
}

.condition-fields > .condition-comparison {
  flex: 0 1 110px;
  width: 110px;
  min-width: 90px;
}

.condition-fields > .condition-value {
  flex: 1 1 180px;
  min-width: 0;
}

.condition-actions {
  display: flex;
  align-items: center;
  /* 每行预留完整的增删操作宽度，避免最后一行按内容撑宽。 */
  flex: 0 0 120px;
  min-width: 0;
  gap: 12px;
}

.condition-actions .el-button {
  flex-shrink: 0;
}

.condition-actions .el-button + .el-button {
  margin-left: 0;
}

.condition-operator {
  white-space: nowrap;
}

@media (max-width: 900px) {
  .condition-fields {
    flex-basis: 260px;
  }

  .condition-actions {
    margin-left: auto;
  }
}
</style>
