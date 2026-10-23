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
  <div ref="pageRef" class="app-container object-type-relation-page">
    <GuideTip tip-id="om/object/relation.list"/>
    <div v-show="showSearch" class="pagecont-top">
      <el-form class="btn-style" :model="query" :inline="true" @submit.prevent="search">
        <el-form-item label="关系名称">
          <el-input v-model="query.name" class="el-form-input-width" placeholder="请输入关系名称" maxlength="128"
                    clearable @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item label="是否可逆">
          <el-select v-model="query.reversibleFlag" class="el-form-input-width" placeholder="请选择是否可逆" clearable>
            <el-option v-for="dict in sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value === 'Y' ? 1 : 0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="search"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetSearch"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button v-hasPermi="['om:object:relation:add']" type="primary" plain :disabled="saving"
                       @click="handleAdd"
            ><i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['om:object:relation:remove']" type="danger" plain
                       :disabled="!selectedIds.length || deleting || loading" @click="handleDelete()"
            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"/>
      </div>
      <el-table ref="tableRef" v-loading="loading" :data="rows" row-key="id" stripe :default-sort="{prop: 'createTime', order: 'descending'}" @selection-change="handleSelectionChange" @sort-change="handleSortChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="visible('id')" prop="id" label="编号" width="80" align="center" sortable="custom" :sort-orders="['descending', 'ascending', null]" :show-overflow-tooltip="{effect: 'light'}"/>
        <el-table-column v-if="visible('startTypeName')" prop="startTypeName" label="起点类型" min-width="150"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.startTypeName || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('name')" prop="name" label="关系名称" min-width="160"
                         :show-overflow-tooltip="{effect: 'light'}"
        />
        <el-table-column v-if="visible('endTypeName')" prop="endTypeName" label="终点类型" min-width="150"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.endTypeName || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('description')" prop="description" label="描述" min-width="240"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.description || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('reversibleFlag')" label="是否可逆" width="100" align="center">
          <template #default="{row}">{{ reversibleLabel(row.reversibleFlag) }}</template>
        </el-table-column>
        <el-table-column v-if="visible('reverseName')" prop="reverseName" label="反向名称" min-width="150"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.reverseName || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('createBy')" prop="createBy" label="创建人" width="120" align="center"
                         :show-overflow-tooltip="{effect: 'light'}"
        >
          <template #default="{row}">{{ row.createBy || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('createTime')" prop="createTime" label="创建时间" width="180" align="center" sortable="custom" :sort-orders="['descending', 'ascending', null]">
          <template #default="{row}">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="visible('operation')" label="操作" width="180" align="center" fixed="right"
                         class-name="small-padding fixed-width"
        >
          <template #default="{row}">
            <el-button v-hasPermi="['om:object:relation:query']" link type="primary" icon="View"
                       @click="handleDetail(row)"
            >详情
            </el-button>
            <el-button v-hasPermi="['om:object:relation:edit']" link type="primary" icon="Edit" :disabled="saving"
                       @click="handleUpdate(row)"
            >修改
            </el-button>
            <el-button v-hasPermi="['om:object:relation:remove']" link type="danger" icon="Delete" :disabled="deleting"
                       @click="handleDelete(row)"
            >删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无关系"/>
            <p>暂无关系</p></div>
        </template>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize"
                  @pagination="getList"
      />
    </div>
    <el-dialog v-model="open" :title="dialogTitle" width="1100px" :append-to="pageRef" :close-on-click-modal="false"
               :close-on-press-escape="!saving" :before-close="beforeClose" draggable destroy-on-close
               @closed="relationRef?.clearValidate()"
    >
      <el-form ref="relationRef" :model="form" :rules="rules" label-width="120px" @submit.prevent="submitForm">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="起点类型" prop="startTypeId">
              <el-tree-select v-model="form.startTypeId" :data="typeOptions" :props="treeProps" value-key="id"
                              placeholder="请选择起点类型" check-strictly clearable style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关系名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入关系名称" maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="终点类型" prop="endTypeId">
              <el-tree-select v-model="form.endTypeId" :data="typeOptions" :props="treeProps" value-key="id"
                              placeholder="请选择终点类型" check-strictly clearable style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否可逆" prop="reversibleFlag">
              <el-radio-group v-model="form.reversibleFlag" @change="handleReversibleChange">
                <el-radio v-for="dict in sys_yes_no" :key="dict.value" :value="dict.value === 'Y' ? 1 : 0">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.reversibleFlag === 1" :span="24">
            <el-form-item label="反向名称" prop="reverseName" required>
              <el-input v-model="form.reverseName" placeholder="请输入反向名称" maxlength="128"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" class="character-count" type="textarea" :rows="4"
                        placeholder="请输入描述" maxlength="512个字符" show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联建模目标" prop="targetIds">
              <el-button type="primary" plain :disabled="saving" @click="targetPickerOpen = true">
                导入建模目标
              </el-button>
            </el-form-item>
            <div class="target-table">
              <TargetTable :targets="selectedTargets" editable :disabled="saving" @remove="removeTarget"/>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" :disabled="saving" @click="open = false">取 消</el-button>
          <el-button type="primary" size="mini" :loading="saving" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <TargetPicker v-model="targetPickerOpen" :selected-targets="selectedTargets" :version-id="versionId" :append-to="pageRef"
                  :load-options="listRelationTargetOptions" @confirm="addTargets"
    />
  </div>
</template>

<script setup>
import {getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import {ElMessage, ElMessageBox} from 'element-plus';
import {
  listRelations,
  getRelation,
  listRelationTypes,
  addRelation,
  updateRelation,
  delRelation,
  listRelationTargets,
  listRelationTargetOptions
} from '@/api/om/object/relation';
import TargetPicker from '../type/TargetPicker.vue';
import TargetTable from '../type/TargetTable.vue';

defineOptions({name: 'OmObjectTypeRelation'});
const props = defineProps({versionId: {type: [String, Number], default: undefined}});
const router = useRouter();
const {versionId, requireVersion} = useOntologyVersion(() => props.versionId);
const {proxy} = getCurrentInstance();
const {sys_yes_no} = proxy.useDict('sys_yes_no');
const reversibleLabel = value => sys_yes_no.value.find(dict => dict.value === (value === 1 ? 'Y' : value === 0 ? 'N' : ''))?.label || '-';
const pageRef = ref();
const tableRef = ref();
const relationRef = ref();
const rows = ref([]);
const selectedIds = ref([]);
const total = ref(0);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const showSearch = ref(true);
const open = ref(false);
const targetPickerOpen = ref(false);
const selectedTargets = ref([]);
const typeOptions = ref([]);
const dialogTitle = ref('新增关系');
const query = reactive({name: '', reversibleFlag: undefined, pageNum: 1, pageSize: 10, orderByColumn: 'createTime', isAsc: 'descending'});
const appliedName = ref('');
const appliedReversibleFlag = ref();
const columns = ref([
  {key: 0, prop: 'id', label: '编号', visible: true},
  {key: 1, prop: 'startTypeName', label: '起点类型', visible: true},
  {key: 2, prop: 'name', label: '关系名称', visible: true},
  {key: 3, prop: 'endTypeName', label: '终点类型', visible: true},
  {key: 4, prop: 'description', label: '描述', visible: true},
  {key: 5, prop: 'reversibleFlag', label: '是否可逆', visible: true},
  {key: 6, prop: 'reverseName', label: '反向名称', visible: true},
  {key: 7, prop: 'createBy', label: '创建人', visible: true},
  {key: 8, prop: 'createTime', label: '创建时间', visible: true},
  {key: 9, prop: 'operation', label: '操作', visible: true}
]);
const visible = prop => columns.value.find(column => column.prop === prop)?.visible;
const treeProps = {label: 'name', value: 'id', children: 'children'};
const emptyForm = () => ({
  id: undefined,
  versionId: versionId.value,
  name: '',
  description: '',
  startTypeId: null,
  endTypeId: null,
  reversibleFlag: 0,
  reverseName: '',
  targetIds: []
});
const form = ref(emptyForm());
const rules = {
  targetIds: [{type: 'array', required: true, min: 1, message: '请至少关联一个建模目标', trigger: 'change'}],
  name: [{required: true, whitespace: true, message: '关系名称不能为空', trigger: 'blur'}, {
    max: 128,
    message: '关系名称不能超过128个字符',
    trigger: 'blur'
  }],
  startTypeId: [{required: true, message: '请选择起点类型', trigger: 'change'}],
  endTypeId: [{required: true, message: '请选择终点类型', trigger: 'change'}],
  reversibleFlag: [{required: true, message: '请选择是否可逆', trigger: 'change'}],
  reverseName: [{
    validator: (_rule, value, callback) => {
      if (form.value.reversibleFlag === 1 && !value?.trim()) {
        callback(new Error('可逆关系的反向名称不能为空'));
      } else {
        callback();
      }
    }, trigger: 'blur'
  }, {max: 128, message: '反向名称不能超过128个字符', trigger: 'blur'}],
  description: [{max: 256, message: '关系描述不能超过256个字符', trigger: 'blur'}]
};
let listRequest = 0;
let dialogRequest = 0;
let contextRequest = 0;

async function getList() {
  const request = ++listRequest;
  if (!versionId.value) { rows.value = []; loading.value = false; return; }
  loading.value = true;
  try {
    const response = await listRelations({
      versionId: versionId.value,
      name: appliedName.value,
      reversibleFlag: appliedReversibleFlag.value,
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      orderByColumn: query.orderByColumn,
      isAsc: query.isAsc
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

function handleSortChange({prop, order}) {
  query.orderByColumn = order ? prop : undefined;
  query.isAsc = order || undefined;
  query.pageNum = 1;
  getList();
}
function search() {
  appliedName.value = query.name.trim();
  appliedReversibleFlag.value = query.reversibleFlag === 0 || query.reversibleFlag === 1 ? query.reversibleFlag : undefined;
  query.pageNum = 1;
  return getList();
}

function resetSearch() {
  query.name = '';
  query.reversibleFlag = undefined;
  return search();
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id);
}

async function handleAdd() {
  if (!requireVersion()) return;
  const request = ++dialogRequest;
  const response = await listRelationTypes(versionId.value);
  if (request !== dialogRequest) return;
  typeOptions.value = proxy.handleTree(response.data || [], 'id', 'parentId');
  form.value = emptyForm();
  selectedTargets.value = [];
  targetPickerOpen.value = false;
  dialogTitle.value = '新增关系';
  open.value = true;
  nextTick(() => relationRef.value?.clearValidate());
}

async function handleUpdate(row) {
  const request = ++dialogRequest;
  const [response, options, targetsResponse] = await Promise.all([
    getRelation(row.id), listRelationTypes(versionId.value), listRelationTargets(row.id)
  ]);
  if (request !== dialogRequest) return;
  typeOptions.value = proxy.handleTree(options.data || [], 'id', 'parentId');
  form.value = {...emptyForm(), ...response.data};
  selectedTargets.value = targetsResponse.data || [];
  form.value.targetIds = selectedTargets.value.map(target => String(target.id));
  targetPickerOpen.value = false;
  dialogTitle.value = '修改关系';
  open.value = true;
  nextTick(() => relationRef.value?.clearValidate());
}

function handleDetail(row) {
  return router.push({
    name: 'OmObjectTypeRelationDetail',
    params: {id: String(row.id), versionId: versionId.value}
  });
}

async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value || !await relationRef.value.validate().catch(() => false)) return;
  if (saving.value || submittingVersion !== versionId.value || !open.value) return;
  const context = contextRequest;
  ++dialogRequest;
  const data = {
    id: form.value.id,
    versionId: submittingVersion,
    name: form.value.name.trim(),
    description: (form.value.description || '').trim(),
    startTypeId: form.value.startTypeId,
    endTypeId: form.value.endTypeId,
    reversibleFlag: form.value.reversibleFlag,
    reverseName: form.value.reversibleFlag === 1 ? (form.value.reverseName || '').trim() : '',
    targetIds: selectedTargets.value.map(target => String(target.id))
  };
  saving.value = true;
  try {
    await (data.id ? updateRelation(data) : addRelation(data));
    if (context !== contextRequest) return;
    ElMessage.success(data.id ? '修改成功' : '新增成功');
    open.value = false;
    await getList();
  } finally {
    saving.value = false;
  }
}

function beforeClose(done) {
  if (!saving.value) done();
}

function addTargets(targets) {
  if (saving.value) return;
  const merged = new Map(selectedTargets.value.map(target => [String(target.id), target]));
  targets.forEach(target => merged.set(String(target.id), target));
  selectedTargets.value = Array.from(merged.values());
  form.value.targetIds = selectedTargets.value.map(target => String(target.id));
  nextTick(() => relationRef.value?.validateField('targetIds'));
}

function removeTarget(target) {
  if (saving.value) return;
  selectedTargets.value = selectedTargets.value.filter(item => String(item.id) !== String(target.id));
  form.value.targetIds = selectedTargets.value.map(item => String(item.id));
  nextTick(() => relationRef.value?.validateField('targetIds').catch(() => {}));
}

async function handleDelete(row) {
  if (deleting.value) return;
  const ids = row ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  const context = contextRequest;
  try {
    await ElMessageBox.confirm(row ? `是否删除关系“${row.name}”？` : `是否删除选中的 ${ids.length} 条关系？`,
      '删除确认', {type: 'warning'});
  } catch {
    return;
  }
  if (context !== contextRequest || deleting.value) return;
  deleting.value = true;
  try {
    await delRelation(ids);
    if (context !== contextRequest) return;
    ElMessage.success('删除成功');
    await getList();
  } finally {
    deleting.value = false;
  }
}

watch(versionId, () => {
  ++contextRequest;
  ++dialogRequest;
  open.value = false;
  targetPickerOpen.value = false;
  selectedTargets.value = [];
  query.name = '';
  appliedName.value = '';
  query.reversibleFlag = undefined;
  appliedReversibleFlag.value = undefined;
  query.pageNum = 1;
  rows.value = [];
  selectedIds.value = [];
  total.value = 0;
  getList();
}, {immediate: true});
function handleReversibleChange(value) {
  if (value !== 1) form.value.reverseName = '';
  nextTick(() => relationRef.value?.clearValidate('reverseName'));
}
watch(open, value => { if (!value) targetPickerOpen.value = false; });
onBeforeUnmount(() => {
  ++contextRequest;
  ++dialogRequest;
  ++listRequest;
});
</script>

<style scoped>
.target-table { margin: 0 0 10px 120px; }
.object-type-relation-page :deep(.el-dialog) { max-width: calc(100vw - 32px); }
@media (max-width: 600px) {
  .target-table { margin-left: 0; }
}
.character-count :deep(.el-input__count) {
  background: transparent;
}

</style>
