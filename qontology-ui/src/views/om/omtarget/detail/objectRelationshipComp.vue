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
  <div ref="pageRef" class="target-relations">
    <qt-wrap :columns="columns" :table-ref="listActions" :config="{ fullContent: false, searchShadow: false }">
      <template #search><qt-search-bar
        :items="searchItems"
        :params="query"
        @query="search"
        @reset="search"
      /></template>
      <template #actions-data>
        <el-button
          v-hasPermi="['om:object:relation:add']"
          type="primary"
          plain
          :disabled="saving"
          @click="handleAdd"
        ><i class="iconfont-mini icon-xinzeng mr5"></i>新增</el-button>
        <el-button
          v-hasPermi="['om:object:relation:remove']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting || loading"
          @click="handleDelete()"
        ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button>
      </template>
      <div v-loading="loading">
        <qt-table
          ref="tableRef"
          :columns="columns"
          :config="tableConfig"
          :events="tableEvents"
          :func="readRows"
        >
          <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
          <template #reversibleFlag="{ row }">{{ Number(row.reversibleFlag) === 1 ? '是' : '否' }}</template>
          <template #creator="{ row }"><div class="flex-column overflow-hidden"><span class="text-ellipsis" :title="row.createBy">{{ row.createBy }}</span><span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span></div></template>
          <template #createTime="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
          <template #operation="{ row }">
            <el-button
              v-hasPermi="['om:object:relation:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
            >详情</el-button>
            <el-button
              v-hasPermi="['om:object:relation:edit']"
              link
              type="primary"
              icon="Edit"
              :disabled="saving"
              @click="handleUpdate(row)"
            >修改</el-button>
            <el-button
              v-hasPermi="['om:object:relation:remove']"
              link
              type="danger"
              icon="Delete"
              :disabled="deleting"
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </qt-table>
      </div>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </qt-wrap>
  </div>
  <el-dialog
    v-model="open"
    :title="dialogTitle"
    width="800px"
    :append-to="pageRef"
    :close-on-click-modal="!saving"
    :close-on-press-escape="!saving"
    :before-close="beforeClose"
    draggable
    destroy-on-close
    @closed="relationRef?.clearValidate()"
  >
    <guide-tip-panel
      type="hint"
      class="mb15"
      allow-wrap
      title="定义对象类型之间的业务联系，明确关系方向、可逆性及业务含义，为当前建模目标建立清晰的对象关联。"
    />
    <el-form
      ref="relationRef"
      :model="form"
      :rules="rules"
      label-width="130px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <qt-form-item
        label="起点类型"
        prop="startTypeId"
        class="row-full"
        tip="选择发起该关系的对象类型，例如“供应商提供物料”中的供应商。"
      >
        <el-tree-select
          v-model="form.startTypeId"
          :data="typeOptions"
          :props="treeProps"
          value-key="id"
          placeholder="请选择起点类型，即关系的发起对象"
          check-strictly
          clearable
        />
      </qt-form-item>
      <qt-form-item
        label="对象类型关系名称"
        prop="name"
        class="row-full"
        tip="使用能表达对象间业务联系的名称，建议填写动词或动宾短语，例如“提供”“采购物料”。"
      >
        <el-input v-model="form.name" placeholder="请输入对象类型关系名称，例如：提供、采购物料" :maxlength="128" />
      </qt-form-item>
      <qt-form-item
        label="终点类型"
        prop="endTypeId"
        class="row-full"
        tip="选择关系指向的对象类型，例如“供应商提供物料”中的物料。"
      >
        <el-tree-select
          v-model="form.endTypeId"
          :data="typeOptions"
          :props="treeProps"
          value-key="id"
          placeholder="请选择终点类型，即关系指向的对象"
          check-strictly
          clearable
        />
      </qt-form-item>
      <qt-form-item
        label="是否可逆"
        prop="reversibleFlag"
        class="row-full"
        tip="选择“是”时，需填写从终点类型指向起点类型的反向关系名称。"
      >
        <el-radio-group v-model="form.reversibleFlag"><el-radio :value="1">是</el-radio><el-radio :value="0">否</el-radio></el-radio-group>
      </qt-form-item>
      <qt-form-item
        label="反向名称"
        prop="reverseName"
        class="row-full"
        tip="描述反向查看该关系时的业务含义，例如“提供”的反向名称为“由供应商提供”。"
      >
        <el-input v-model="form.reverseName" placeholder="请输入反向名称，例如：由供应商提供" :maxlength="128" />
      </qt-form-item>
      <qt-form-item
        label="描述"
        prop="description"
        class="row-full"
        tip="补充关系适用的业务场景、对象间的职责或关联规则，最多256个字符。"
      >
        <el-input
          v-model="form.description"
          class="character-count"
          type="textarea"
          :rows="3"
          placeholder="请输入关系描述，说明起点与终点之间的业务联系及适用场景"
          :maxlength="256"
          show-word-limit
        />
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" :disabled="saving" @click="open = false">取 消</el-button>
        <el-button
          type="primary"
          size="mini"
          :loading="saving"
          @click="submitForm"
        >确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { useCreatorPhones } from '@/utils/useCreatorPhones';
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
} from '@/api/om/object/relation';

defineOptions({name: 'OmObjectTypeRelation'});
const props = defineProps({
  versionId: {type: [String, Number], default: undefined},
  targetDetail: {type: Object, default: () => {}}
});
const router = useRouter();
const {versionId, requireVersion} = useOntologyVersion(() => props.versionId);
const {proxy} = getCurrentInstance();
const pageRef = ref();
const tableRef = ref();
const relationRef = ref();
const rows = ref([]);
const selectedIds = ref([]);
const total = ref(0);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const { creatorPhones } = useCreatorPhones();
const appliedReversible = ref();
const open = ref(false);
const targetPickerOpen = ref(false);
const selectedTargets = ref([]);
const typeOptions = ref([]);
const dialogTitle = ref('新增对象类型关系');

const appliedName = ref('');
const columns = reactive([
  { type: 'selection', width: 55 },
  { prop: 'id', label: '编号', width: 80, sortable: 'custom' },
  { prop: 'name', label: '对象类型关系名称', slot: 'text', minWidth: 160, align: 'left' },
  { prop: 'startTypeName', label: '起点类型', slot: 'text', minWidth: 150, align: 'left' },
  { prop: 'endTypeName', label: '终点类型', slot: 'text', minWidth: 150, align: 'left' },
  { prop: 'description', label: '描述', slot: 'text', minWidth: 240, align: 'left' },
  { prop: 'reversibleFlag', label: '是否可逆', slot: 'reversibleFlag', width: 100 },
  { prop: 'reverseName', label: '反向名称', slot: 'text', minWidth: 150, align: 'left' },
  { prop: 'createBy', label: '创建人', slot: 'creator', width: 130, align: 'left', headerAlign: 'left' },
  { prop: 'createTime', label: '创建时间', slot: 'createTime', width: 160, sortable: 'custom' },
  { label: '操作', slot: 'operation', width: 220, minWidth: 220, fixed: 'right' },
]);
const tableConfig = { notPagination: true, sort: true, table: { rowKey: 'id', stripe: true, defaultSort: { prop: 'createTime', order: 'descending' }, onSelectionChange: handleSelectionChange } };
const tableEvents = { onSortChange: ({ orderByColumn, isAsc }) => handleSortChange({ prop: orderByColumn, order: isAsc }) };
const listActions = { getList };
const searchItems = [
  { label: '对象类型关系名称', prop: 'name', component: { is: 'input' } },
  { label: '是否可逆', prop: 'reversibleFlag', component: { is: 'select', options: [{ label: '是', value: 1 }, { label: '否', value: 0 }] } },
];
function readRows() {
  return Promise.resolve({ data: rows.value.map(row => {
    const record = { ...row };
    ['name', 'startTypeName', 'endTypeName', 'description', 'reverseName', 'createBy', 'createTime'].forEach(key => { if (record[key] == null || record[key] === '') record[key] = '-'; });
    return record;
  }) });
}
watch(rows, () => tableRef.value?.getList());
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

const defaultSort = ref({prop: "createTime", order: "descending"});
const query = reactive({name: '', reversibleFlag: undefined, pageNum: 1, pageSize: 10,
  orderByColumn: defaultSort.value.prop,
  isAsc: defaultSort.value.order});

const form = ref(emptyForm());
const rules = {
  // targetIds: [{type: 'array', required: true, min: 1, message: '请至少关联一个建模目标', trigger: 'change'}],
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
  if (!versionId.value || !props.targetDetail?.id) { rows.value = []; total.value = 0; loading.value = false; return; }
  loading.value = true;
  try {
    query.versionId = versionId.value;
    query.name = appliedName.value;
    query.reversibleFlag = appliedReversible.value;
    query.targetIdList = [props.targetDetail.id];
    const response = await listRelations(query);
    if (request !== listRequest) return;
    total.value = Number(response.data?.total || 0);
    const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
    if (query.pageNum > maxPage) {
      query.pageNum = maxPage;
      return await getList();
    }
    rows.value = response.data?.rows || [];
    selectedIds.value = [];
    tableRef.value?.elTableRef?.clearSelection();
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

function search() {
  appliedName.value = (query.name || "").trim();
  appliedReversible.value = query.reversibleFlag;
  query.pageNum = 1;
  return getList();
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
  dialogTitle.value = '新增对象类型关系';
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
  form.value.targetIds = [props.targetDetail.id];
  targetPickerOpen.value = false;
  dialogTitle.value = '修改对象类型关系';
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
  if (submittingVersion !== versionId.value || !open.value) return;
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
    reverseName: (form.value.reverseName || '').trim(),
    targetIds: [props.targetDetail.id]
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

/** 排序触发事件 */
function handleSortChange(column) {
  query.orderByColumn = column.order ? column.prop : defaultSort.value.prop;
  query.isAsc = column.order || defaultSort.value.order;
  query.pageNum = 1;
  getList();
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

watch(() => [versionId.value, props.targetDetail?.id], () => {
  ++contextRequest;
  ++dialogRequest;
  open.value = false;
  targetPickerOpen.value = false;
  selectedTargets.value = [];
  query.name = '';
  appliedName.value = '';
  query.reversibleFlag = undefined;
  appliedReversible.value = undefined;
  query.pageNum = 1;
  rows.value = [];
  selectedIds.value = [];
  total.value = 0;
  getList();
}, {immediate: true});
watch(open, value => { if (!value) targetPickerOpen.value = false; });
onBeforeUnmount(() => {
  ++contextRequest;
  ++dialogRequest;
  ++listRequest;
});
</script>

<style scoped>
.target-relations :deep(.qt-wrap--search) { padding: 0; }
.relation-actions .el-button + .el-button { margin-left: 0; }
.target-table { margin: 0 0 10px 120px; }
.object-type-relation-page :deep(.el-dialog) { max-width: calc(100vw - 32px); }
@media (max-width: 600px) {
  .target-table { margin-left: 0; }
}
.character-count :deep(.el-input__count) {
  background: transparent;
}

</style>
