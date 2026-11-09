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
  <div class="app-container data-rule-page" ref="app-container">
    <GuideTip tip-id="om/constraint/data.list"/>

    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef"/>
      </template>

      <template #actions-data>
        <el-button v-hasPermi="['om:constraint:datarule:add']" type="primary" plain :disabled="saving"
                   @click="handleAdd" @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button v-hasPermi="['om:constraint:datarule:remove']" type="danger" plain
                   :loading="batchDeleteLoading"
                :disabled="!selectedIds.length || deleting" @click="handleDelete()"
                   @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{row, column_data}">{{ row[column_data.prop] }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{row}">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #handle="{row}">
          <el-button v-hasPermi="['om:constraint:datarule:query']" link type="primary" icon="View"
                     @click="handleDetail(row)"
          >详情
          </el-button>
          <!-- 操作区仅三个按钮，直展不折叠到“更多”。 -->
          <el-button v-if="auth.hasPermi('om:constraint:datarule:edit')" link type="primary" icon="Edit"
                     :disabled="saving" @click="handleUpdate(row)"
          >修改
          </el-button>
          <el-button v-if="auth.hasPermi('om:constraint:datarule:remove')" link type="danger" icon="Delete"
                     :loading="deleteLoading[row.id]"
                    :disabled="deleting" @click="handleDelete(row)"
          >删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>
    <el-dialog v-model="open" :title="dialogTitle" width="860px" :append-to="$refs['app-container']"
               :close-on-press-escape="!saving" :before-close="beforeClose" draggable destroy-on-close
               @closed="handleDialogClosed"
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="面向属性取值维护数据校验规则，配置校验类型、约束级别与失败条件，保障业务数据符合预期格式与范围。"
      />
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="120px" class="rule-form"
               @submit.prevent="submitForm"
      >
        <div class="h2-title">基本信息</div>
        <qt-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" maxlength="128"/>
        </qt-form-item>
        <qt-form-item label="类型" prop="type" >
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option v-for="item in om_data_rule_type" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </qt-form-item>
        <qt-form-item label="级别" prop="level" >
          <el-radio-group v-model="form.level">
            <el-radio v-for="item in om_rule_level" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="描述" prop="description" >
          <el-input v-model="form.description" class="character-count" type="textarea" :rows="4"
                    placeholder="请输入描述" :maxlength="256" show-word-limit
          />
        </qt-form-item>
        <div class="h2-title">执行条件</div>
        <qt-form-item label="条件关系" prop="conditionRelation" >
          <el-radio-group v-model="form.conditionRelation">
            <el-radio value="OR">任一满足（OR）</el-radio>
            <el-radio value="AND">全部满足（AND）</el-radio>
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="失败条件" required >
          <div class="conditions">
            <div v-for="(condition, index) in form.conditions" :key="condition.key" class="condition-row">
              <div class="condition-label">条件 {{ index + 1 }}：</div>
              <div class="condition-fields">
                <el-tree-select
                  :ref="element => setConditionSelectRef(condition.key, element)"
                  v-model="condition.objectTypeId"
                  :data="objectTypeTree"
                  :props="objectTypeTreeProps"
                  value-key="id"
                  :loading="optionsLoading"
                  check-strictly
                  filterable
                  clearable
                  placeholder="请选择对象类型"
                  class="condition-object"
                  :aria-label="'第' + (index + 1) + '条对象类型'"
                  @change="handleConditionObjectChange(condition)"
                />
                <el-select
                  v-model="condition.attributeId"
                  :loading="optionsLoading || attributeLoadingCount > 0"
                  filterable
                  :disabled="!condition.objectTypeId"
                  class="condition-attribute"
                  :placeholder="attributePlaceholder"
                  :aria-label="'第' + (index + 1) + '条属性'"
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
                  <el-select v-if="form.type === 'FORMAT'" v-model="condition.value" placeholder="请选择日期格式"
                             class="condition-value" :aria-label="'第' + (index + 1) + '条日期格式'" clearable
                  >
                    <el-option v-for="format in dateFormatOptions" :key="format" :label="format" :value="format"/>
                  </el-select>
                  <el-input v-else :model-value="condition.value" :placeholder="conditionValuePlaceholder"
                            class="condition-value"
                            :inputmode="form.type === 'RANGE' ? 'decimal' : 'text'"
                            :aria-label="'第' + (index + 1) + '条条件值'"
                            @update:model-value="value => handleConditionValueInput(condition, value)"
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
            <div v-if="form.type === 'RANGE'" class="condition-hint">
              取值范围仅支持整数、小数类型的属性
            </div>
          </div>
        </qt-form-item>
        <qt-form-item label="失败提示" prop="failureMessage" tip="校验失败时向用户展示的提示文案。">
          <el-input v-model="form.failureMessage" placeholder="请输入失败提示" maxlength="256"/>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="saving" :disabled="saveDisabled" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="detailOpen" title="数据规则详情" width="860px" :append-to="$refs['app-container']" draggable destroy-on-close>
      <!-- 详情与模型公理风格一致，用只读文本展示字段，并保留新增弹窗的分组标题。 -->
      <el-form label-width="120px" class="rule-form">
        <div class="h2-title">基本信息</div>
        <el-form-item v-for="field in basicDetailFields" :key="field.label" :label="field.label">
          <div class="form-readonly" :class="{ textarea: field.multiline }">{{ field.value || '-' }}</div>
        </el-form-item>
        <div class="h2-title">执行条件</div>
        <el-form-item label="条件关系">
          <div class="form-readonly">{{ conditionRelationLabel(detail.conditionRelation) || '-' }}</div>
        </el-form-item>
        <el-form-item label="失败条件">
          <!-- 失败条件直接照搬修改弹窗的条件行布局，仅将各控件置为只读（disabled），去掉增删按钮。 -->
          <div class="conditions">
            <div v-for="(condition, index) in detail.conditions" :key="condition.key || index" class="condition-row">
              <div class="condition-label">条件 {{ index + 1 }}：</div>
              <div class="condition-fields condition-fields-detail">
                <el-tree-select
                  :model-value="condition.objectTypeId"
                  :data="objectTypeTree"
                  :props="objectTypeTreeProps"
                  value-key="id"
                  check-strictly
                  disabled
                  placeholder="请选择对象类型"
                  class="condition-object"
                />
                <el-select
                  :model-value="condition.attributeId"
                  disabled
                  class="condition-attribute"
                  placeholder="请选择属性"
                >
                  <el-option v-for="item in getConditionAttributes(condition, detail.type)" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <span v-if="detail.type === 'REQUIRED'" class="condition-operator">为空</span>
                <template v-else>
                  <el-select v-if="detail.type === 'RANGE'" :model-value="condition.operator" class="condition-comparison" disabled>
                    <el-option v-for="item in om_rule_operator" :key="item.value" :label="item.label" :value="item.value"/>
                  </el-select>
                  <span v-else class="condition-operator">{{ detailConditionOperatorLabel }}</span>
                  <el-select v-if="detail.type === 'FORMAT'" :model-value="condition.value" placeholder="请选择日期格式" class="condition-value" disabled>
                    <el-option v-for="format in dateFormatOptions" :key="format" :label="format" :value="format"/>
                  </el-select>
                  <el-input v-else :model-value="condition.value" class="condition-value" disabled/>
                </template>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="失败提示">
          <div class="form-readonly">{{ detail.failureMessage || '-' }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { createRowKey } from "@/utils/rowKey";
import {computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch} from 'vue';
import {ElMessage} from 'element-plus';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import { listDataRule, getDataRule, addDataRule, updateDataRule, delDataRule } from '@/api/om/constraint/dataRule';
import {listRelationTypes} from '@/api/om/object/relation';
import {listAttributes, listAttributeBusinessDataTypes} from '@/api/om/object/attribute';
import auth from "@/plugins/auth";
import {useCreatorPhones} from "@/utils/useCreatorPhones";

defineOptions({name: 'OmDataRule'});
const props = defineProps({versionId: {type: [String, Number], default: undefined}});
const {versionId, requireVersion} = useOntologyVersion(() => props.versionId);
const {proxy} = getCurrentInstance();
const {om_data_rule_type, om_rule_level, om_rule_operator} = proxy.useDict(
  'om_data_rule_type', 'om_rule_level', 'om_rule_operator'
);

const tableRef = ref();
const ruleRef = ref();
const selectedIds = ref([]);
const saving = ref(false);
const deleting = ref(false);
const deleteLoading = ref({});
const batchDeleteLoading = ref(false);
const open = ref(false);
const detailOpen = ref(false);
const dialogTitle = ref('新增数据规则');
const detail = ref({});
const {creatorPhones, formatCreator} = useCreatorPhones();

/**
 * 分页查询数据规则列表；版本号缺失时返回空列表，避免无版本上下文发起无效查询。
 * @param {Object} params QtTable 传入的查询参数
 * @returns {Promise<Object>} 列表接口返回体
 */
function fetchDataRules(params) {
  if (!versionId.value) {
    return Promise.resolve({data: {rows: [], total: 0}});
  }
  return listDataRule({...params, versionId: versionId.value});
}

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: 'id',
      defaultSort: {prop: 'createTime', order: 'descending'},
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {type: 'selection', width: 55, minWidth: 55},
    {label: '编号', prop: 'id', slot: 'text', width: 80, minWidth: 80, align: 'center', sortable: true},
    {label: '规则名称', prop: 'name', slot: 'text', minWidth: 200, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '描述', prop: 'description', slot: 'text', minWidth: 240, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '规则类型', prop: 'type', dict: 'om_data_rule_type', width: 150, minWidth: 150},
    {label: '级别', prop: 'level', dict: 'om_rule_level', width: 90, minWidth: 90},
    {label: '创建人', prop: 'createBy', slot: 'creator', width: 130, minWidth: 130, align: 'left', headerAlign: 'left'},
    {label: '创建时间', prop: 'createTime', date: true, width: 160, minWidth: 160, align: 'left', sortable: true},
    {label: '操作', slot: 'handle', width: 250, minWidth: 250, fixed: 'right'}
  ],
  func: fetchDataRules,
  params: {
    name: null,
    type: null,
    level: null
  }
});

// 搜索项配置
const searchStore = reactive({
  items: [
    {label: '规则名称', prop: 'name', component: {is: 'input'}},
    {label: '规则类型', prop: 'type', component: {is: 'select', options: om_data_rule_type}},
    {label: '级别', prop: 'level', component: {is: 'select', options: om_rule_level}}
  ]
});
const objectTypeOptions = ref([]);
const objectTypeTreeProps = {label: 'name', value: 'id', children: 'children'};
const objectTypeTree = computed(() =>
  proxy.handleTree(objectTypeOptions.value.map(item => ({...item})), 'id', 'parentId')
);
const attributeOptions = reactive({});
const businessTypeOptions = reactive({});
const optionsLoading = ref(false);
const optionsLoadFailed = ref(false);
const attributeLoadingCount = ref(0);
const conditionSelectRefs = new Map();
const saveDisabled = computed(() => optionsLoading.value || attributeLoadingCount.value > 0 || optionsLoadFailed.value);
const emptyForm = () => ({
  id: undefined,
  versionId: versionId.value,
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
    {required: true, whitespace: true, message: '规则名称不能为空', trigger: 'blur'},
    {max: 128, message: '规则名称不能超过128个字符', trigger: 'blur'}
  ],
  type: [{required: true, message: '请选择类型', trigger: 'change'}],
  level: [{required: true, message: '请选择级别', trigger: 'change'}],
  description: [
    {max: 256, message: '描述不能超过256个字符', trigger: 'blur'}
  ],
  conditionRelation: [{required: true, message: '请选择条件关系', trigger: 'change'}],
  failureMessage: [
    {required: true, whitespace: true, message: '失败提示不能为空', trigger: 'blur'},
    {max: 256, message: '失败提示不能超过256个字符', trigger: 'blur'}
  ]
};
let dialogRequest = 0;
let contextRequest = 0;
const attributePlaceholder = computed(() => {
  if (form.value.type === 'RANGE') return '请选择数值属性';
  return form.value.type === 'FORMAT' ? '请选择日期属性' : '请选择属性';
});
const conditionOperatorLabel = computed(() => form.value.type === 'ENUM' ? '不属于' : '不匹配');
const detailConditionOperatorLabel = computed(() => detail.value.type === 'ENUM' ? '不属于' : '不匹配');
const dateFormatOptions = [
  'yyyy',
  'yyyy-MM',
  'yyyy-MM-dd',
  'yyyy/MM/dd',
  'yyyyMMdd',
  'yyyy-MM-dd HH:mm',
  'yyyy-MM-dd HH:mm:ss',
  'yyyy/MM/dd HH:mm:ss',
  "yyyy-MM-dd'T'HH:mm:ss"
];
const conditionValuePlaceholder = computed(() => {
  if (form.value.type === 'ENUM') return '手工输入，以逗号分隔';
  return form.value.type === 'RANGE' ? '请输入数值' : '请输入内容';
});

/**
 * 逐页读取下拉选项，避免只显示第一页属性或关系。
 * @param {Function} loadPage 按页号拉取单页数据的回调
 * @returns {Promise<Array>} 合并后的全量行数据
 */
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

/**
 * 拉取指定版本的对象类型选项。
 * @param {string|number} versionId 项目版本编号
 * @returns {Promise<Array>} 对象类型列表
 */
async function listObjectTypeOptions(versionId) {
  const response = await listRelationTypes(versionId);
  return (response.data || []).map(item => ({
    ...item,
    id: String(item.id),
    parentId: item.parentId == null ? null : String(item.parentId)
  }));
}

/**
 * 拉取指定对象类型的属性与业务数据类型选项。
 * @param {string} objectTypeId 对象类型编号
 * @param {string|number} versionId 项目版本编号
 * @returns {Promise<Object>} 含 attributes 与 businessTypes 的选项集
 */
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

/**
 * 新建一条空的失败条件。
 * @returns {Object} 条件对象
 */
function createCondition() {
  return {key: createRowKey(), objectTypeId: '', attributeId: '', operator: 'LT', value: ''};
}

/**
 * 取出属性对应的基础数据类型编号，业务数据类型向下查找。
 * @param {Object} attribute 属性记录
 * @param {Array} businessTypes 当前对象类型的业务数据类型列表
 * @returns {number|undefined} 基础数据类型编号
 */
function getBasicTypeCode(attribute, businessTypes) {
  if (Number(attribute.typeCategory) !== 2) return Number(attribute.basicTypeCode);
  const businessType = businessTypes.find(item => String(item.id) === String(attribute.businessTypeId)
    && item.businessTypeSource === (attribute.businessTypeSource || 'GLOBAL'));
  return businessType ? Number(businessType.dataType) : undefined;
}

/**
 * 逐条校验失败条件，保留父级选项与类型匹配规则。
 * @param {Object} ruleForm 当前表单数据
 * @param {Object} attributes 按对象类型缓存的属性选项
 * @param {Object} businessTypes 按对象类型缓存的业务数据类型
 * @returns {string} 错误提示文案，校验通过时为空字符串
 */
function validateConditions(ruleForm, attributes, businessTypes) {
  if (!ruleForm.conditions.length) return '请至少添加一条失败条件';
  for (const [index, condition] of ruleForm.conditions.entries()) {
    const prefix = `第 ${index + 1} 条失败条件：`;
    if (!condition.objectTypeId || !condition.attributeId) return `${prefix}请选择对象类型和属性`;
    const attribute = (attributes[condition.objectTypeId] || []).find(item => item.id === condition.attributeId);
    if (!attribute) return `${prefix}属性已失效，请重新选择`;
    if (ruleForm.type === 'RANGE') {
      const code = getBasicTypeCode(attribute, businessTypes[condition.objectTypeId] || []);
      if (![1, 2].includes(code)) return `${prefix}只能选择整数或小数属性`;
      if (!om_rule_operator.value.some(item => item.value === condition.operator)) return `${prefix}请选择比较符`;
      const value = String(condition.value).trim();
      if (!/^[+-]?(?:\d+(?:\.\d*)?|\.\d+)$/.test(value)) return `${prefix}请输入有效数值`;
      if (code === 1 && !/^[+-]?\d+$/.test(value)) return `${prefix}请输入整数`;
    } else if (ruleForm.type === 'FORMAT') {
      if (getBasicTypeCode(attribute, businessTypes[condition.objectTypeId] || []) !== 4) return `${prefix}只能选择日期属性`;
      if (!dateFormatOptions.includes(condition.value)) return `${prefix}请选择日期格式`;
    } else if (ruleForm.type === 'ENUM' && !String(condition.value).trim()) {
      return `${prefix}请输入枚举值`;
    }
  }
  return '';
}

/** 刷新当前数据规则列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/**
 * 多选框选中数据，供顶部批量删除使用。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id);
}

/**
 * 拉取并缓存指定对象类型的属性与业务数据类型选项。
 * @param {string} objectTypeId 对象类型编号
 */
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
    if (request === dialogRequest) optionsLoadFailed.value = true;
  } finally {
    if (request === dialogRequest) attributeLoadingCount.value--;
  }
}

/** 加载弹窗所需的对象类型与条件属性选项。 */
async function loadOptions() {
  const request = dialogRequest;
  optionsLoading.value = true;
  optionsLoadFailed.value = false;
  try {
    const options = await listObjectTypeOptions(versionId.value);
    if (request !== dialogRequest) return;
    objectTypeOptions.value = options;
    const record = detailOpen.value ? detail.value : form.value;
    const objectIds = getRecordObjectIds(record);
    await Promise.allSettled(objectIds.map(loadObjectOptions));
  } catch {
    if (request === dialogRequest) optionsLoadFailed.value = true;
  } finally {
    if (request === dialogRequest) optionsLoading.value = false;
  }
}

/** 新增按钮操作。 */
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

/**
 * 修改按钮操作。
 * @param {Object} row 当前行数据
 */
async function handleUpdate(row) {
  if (!requireVersion()) return;
  const request = ++dialogRequest;
  const response = await getDataRule(row.id);
  if (request !== dialogRequest) return;
  attributeLoadingCount.value = 0;
  form.value = {
    ...emptyForm(),
    ...response.data,
    conditions: response.data?.conditions?.length
      ? response.data.conditions.map(condition => ({...condition, key: createRowKey()}))
      : [createCondition()]
  };
  detailOpen.value = false;
  dialogTitle.value = '修改数据规则';
  open.value = true;
  nextTick(() => ruleRef.value?.clearValidate());
  loadOptions();
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
async function handleDetail(row) {
  if (!requireVersion()) return;
  const request = ++dialogRequest;
  const response = await getDataRule(row.id);
  if (request !== dialogRequest) return;
  attributeLoadingCount.value = 0;
  detail.value = response.data;
  await loadDetailOptions();
  if (request === dialogRequest) detailOpen.value = true;
}

/**
 * 详情展示前加载对象类型与条件属性选项，用于把编号回显为名称。
 */
async function loadDetailOptions() {
  const request = dialogRequest;
  optionsLoadFailed.value = false;
  try {
    const options = await listObjectTypeOptions(versionId.value);
    if (request !== dialogRequest) return;
    objectTypeOptions.value = options;

    await Promise.allSettled(getRecordObjectIds(detail.value).map(loadObjectOptions));
  } catch {
    // 接口异常由统一请求拦截器提示
  }
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
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
    description: form.value.description.trim()
  };
  saving.value = true;
  try {
    await (data.id ? updateDataRule(data) : addDataRule(data));
    if (context !== contextRequest) return;
    ElMessage.success(data.id ? '修改成功' : '新增成功');
    open.value = false;
    refreshList();
  } finally {
    saving.value = false;
  }
}

/**
 * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
 * @param {Object} [row] 当前行数据，顶部删除按钮点击时不传参
 */
async function handleDelete(row) {
  if (!requireVersion() || deleting.value) return;
  const isSingle = row?.id != null;
  const ids = isSingle ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  const context = contextRequest;
  const message = isSingle
    ? `是否确认删除编号为${row.id} 的数据项？`
    : `可删除${ids.length}个，不可删除0个，是否删除可删部分`;
  deleting.value = true;
  try {
    try {
      await proxy.$modal.confirm(message);
    } catch {
      return;
    }
    if (context !== contextRequest) return;
    if (isSingle) deleteLoading.value[row.id] = true;
    else batchDeleteLoading.value = true;
    await delDataRule(ids);
    if (context !== contextRequest) return;
    proxy.$modal.msgSuccess('删除成功');
    refreshList();
  } finally {
    deleting.value = false;
    if (isSingle) deleteLoading.value[row.id] = false;
    else batchDeleteLoading.value = false;
  }
}

/** 关闭新增/修改弹窗；保存中禁止关闭。 */
function cancel() {
  if (saving.value) return;
  open.value = false;
}

/**
 * 保存期间拦截弹窗关闭回调。
 * @param {Function} done el-dialog 的 before-close 完成回调
 */
function beforeClose(done) {
  if (!saving.value) done();
}

/** 弹窗关闭后清除表单校验提示。 */
function handleDialogClosed() {
  ruleRef.value?.clearValidate();
}

/**
 * 取出失败条件里需要加载属性选项的对象类型编号。
 * @param {Object} record 当前表单或详情记录
 * @returns {Array<string>} 去重后的对象类型编号列表
 */
function getRecordObjectIds(record) {
  return [...new Set((record.conditions || []).map(item => item.objectTypeId).filter(Boolean))];
}

/** 切换规则类型时重置全部失败条件。 */
function handleTypeChange() {
  form.value.conditions = [createCondition()];
  nextTick(() => ruleRef.value?.clearValidate());
}

/**
 * 切换条件的对象类型，清空已选属性与条件值并拉取属性选项。
 * @param {Object} condition 当前条件对象
 */
function handleConditionObjectChange(condition) {
  condition.attributeId = '';
  condition.value = '';
  loadObjectOptions(condition.objectTypeId);
}

/**
 * 条件值输入过滤：取值范围只允许一个数值，移除文字及多余的符号。
 * @param {Object} condition 当前条件对象
 * @param {string} value 原始输入值
 */
function handleConditionValueInput(condition, value) {
  if (form.value.type !== 'RANGE') {
    condition.value = value;
    return;
  }
  const attribute = (attributeOptions[condition.objectTypeId] || []).find(item => item.id === condition.attributeId);
  const code = attribute ? getBasicTypeCode(attribute, businessTypeOptions[condition.objectTypeId] || []) : undefined;
  let numeric = String(value).replace(/[^\d.+-]/g, '').replace(/(?!^)[+-]/g, '');
  if (code === 1) {
    numeric = numeric.replace(/\./g, '');
  } else {
    const dotIndex = numeric.indexOf('.');
    if (dotIndex !== -1) numeric = numeric.slice(0, dotIndex + 1) + numeric.slice(dotIndex + 1).replace(/\./g, '');
  }
  condition.value = numeric;
}

/**
 * 切换条件属性时清空条件值。
 * @param {Object} condition 当前条件对象
 */
function handleConditionAttributeChange(condition) {
  condition.value = '';
}

/**
 * 按规则类型过滤可选属性，取值范围只给数值属性，格式校验只给日期属性。
 * @param {Object} condition 当前条件对象
 * @param {string} [type] 规则类型，缺省取当前表单选中值
 * @returns {Array} 可选属性列表
 */
function getConditionAttributes(condition, type = form.value.type) {
  const attributes = attributeOptions[condition.objectTypeId] || [];
  if (!['RANGE', 'FORMAT'].includes(type)) return attributes;
  const businessTypes = businessTypeOptions[condition.objectTypeId] || [];
  const typeCodes = type === 'FORMAT' ? [4] : [1, 2];
  return attributes.filter(item => typeCodes.includes(getBasicTypeCode(item, businessTypes)));
}

/**
 * 收集条件行属性下拉的组件实例，供新增条件后自动聚焦。
 * @param {string} key 条件行唯一键
 * @param {Object|null} element 组件实例，卸载时为 null
 */
function setConditionSelectRef(key, element) {
  if (element) conditionSelectRefs.set(key, element);
  else conditionSelectRefs.delete(key);
}

/** 追加一条失败条件并聚焦其属性下拉。 */
async function addCondition() {
  const condition = createCondition();
  form.value.conditions.push(condition);
  await nextTick();
  conditionSelectRefs.get(condition.key)?.focus();
}

/**
 * 删除指定失败条件，至少保留一条。
 * @param {number} index 条件行下标
 */
function removeCondition(index) {
  if (form.value.conditions.length > 1) form.value.conditions.splice(index, 1);
}

/**
 * 校验失败条件配置。
 * @returns {string} 错误提示文案，校验通过时为空字符串
 */
function validateExpression() {
  return validateConditions(form.value, attributeOptions, businessTypeOptions);
}

/** 将条件关系字典值转为展示文案。 */
function conditionRelationLabel(value) {
  return {OR: '任一满足（OR）', AND: '全部满足（AND）'}[value] || '';
}

// 详情“基本信息”分组展示字段，与新增弹窗字段顺序保持一致。
const basicDetailFields = computed(() => [
  {label: '规则名称', value: detail.value.name},
  {label: '规则类型', value: proxy.selectDictLabel(om_data_rule_type.value, detail.value.type)},
  {label: '级别', value: proxy.selectDictLabel(om_rule_level.value, detail.value.level)},
  {label: '描述', value: detail.value.description, multiline: true},
  {label: '创建人', value: detail.value.createBy || '-'},
  {label: '联系方式', value: creatorPhones.value[detail.value.creatorId] || '-'},
  {label: '创建时间', value: proxy.parseTime(detail.value.createTime, '{y}-{m}-{d} {h}:{i}')}
]);

// 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
watch(versionId, async () => {
  ++contextRequest;
  ++dialogRequest;
  open.value = false;
  detailOpen.value = false;
  optionsLoading.value = false;
  attributeLoadingCount.value = 0;
  optionsLoadFailed.value = false;
  objectTypeOptions.value = [];
  Object.keys(attributeOptions).forEach(key => delete attributeOptions[key]);
  Object.keys(businessTypeOptions).forEach(key => delete businessTypeOptions[key]);
  tableStore.params.name = null;
  tableStore.params.type = null;
  tableStore.params.level = null;
  await nextTick();
  tableRef.value?.resetQuery();
});

watch(open, value => {
  if (value) return;
  ++dialogRequest;
  attributeLoadingCount.value = 0;
  optionsLoading.value = false;
});

onBeforeUnmount(() => {
  ++contextRequest;
  ++dialogRequest;
});
</script>

<style scoped lang="scss">
.data-rule-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.rule-form {
  min-height: 390px;
}

.rule-form .el-select {
  width: 100%;
}

/* 对齐对象类型新增页的间隔：带描述提示的表单项底部留白，避免说明文字贴到下一字段。 */
.rule-form :deep(.el-form-item.has-tip) {
  margin-bottom: 25px;
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

.condition-hint {
  color: #909399;
  font-size: 12px;
  line-height: 20px;
}

.condition-label {
  flex: 0 0 62px;
}

.condition-label,
.condition-operator {
  color: #606266;
  font-size: 14px;
  font-weight: 400;
  line-height: 32px;
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

/* 详情无增删按钮列，单独让条件值换行展示，与修改弹窗的换行效果保持一致。 */
.condition-fields-detail > .condition-value {
  flex: 1 1 100%;
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

@media (max-width: 900px) {
  .condition-fields {
    flex-basis: 260px;
  }

  .condition-actions {
    margin-left: auto;
  }
}
</style>
