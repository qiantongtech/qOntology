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
  <div ref="pageRef" class="target-object-types">
    <qt-wrap :columns="columns" :table-ref="listActions" :config="{ fullContent: false, searchShadow: false }">
      <template #search>
        <qt-search-bar
          :items="searchItems"
          :params="queryParams"
          @query="handleQuery"
          @reset="handleQuery"
        />
      </template>
      <template #actions-data>
        <el-button
          v-hasPermi="['om:object:type:add']"
          type="primary"
          plain
          @click="handleAdd()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          class="toggle-expand-all"
          type="primary"
          plain
          @click="toggleExpandAll"
        >
          <svg-icon :icon-class="isExpandAll ? 'toggle' : 'expand'" />
          <span>{{ isExpandAll ? ' 折叠' : ' 展开' }}</span>
        </el-button>
      </template>
      <div v-loading="loading">
        <qt-table
          v-if="refreshTable"
          ref="tableRef"
          :columns="columns"
          :config="tableConfig"
          :events="tableEvents"
          :func="readTree"
        >
          <template #text="{ row, column_data }">{{ row[column_data.prop] ?? '-' }}</template>
          <template #description="{ row }">{{ row.description?.trim() || '-' }}</template>
          <template #creator="{ row }">
            <div class="flex-column overflow-hidden">
              <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
              <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
            </div>
          </template>
          <template #createTime="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
          <template #operation="{ row }">
            <el-button
              v-hasPermi="['om:object:type:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
            >详情</el-button>
            <el-button
              v-hasPermi="['om:object:type:edit']"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
            >修改</el-button>
            <el-popover
              v-if="auth.hasPermiOr(['om:object:type:add', 'om:object:type:remove'])"
              placement="bottom"
              :width="110"
              trigger="click"
              popper-class="table-action-popover handle-popover"
            >
              <template #reference><el-button link type="primary" icon="ArrowDown">更多</el-button></template>
              <div class="object-type-actions">
                <el-button
                  v-hasPermi="['om:object:type:add']"
                  link
                  type="primary"
                  icon="Plus"
                  @click="handleAdd(row)"
                >新增子级</el-button>
                <el-button
                  v-hasPermi="['om:object:type:remove']"
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="deleting"
                  @click="handleDelete(row)"
                >删除</el-button>
              </div>
            </el-popover>
          </template>
          <template #empty>
            <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无记录" /><p>暂无记录</p></div>
          </template>
        </qt-table>
      </div>
    </qt-wrap>
  </div>
  <el-dialog
    v-model="open"
    :title="dialogTitle"
    width="1100px"
    :append-to="pageRef"
    :close-on-click-modal="!saving"
    :close-on-press-escape="!saving"
    :before-close="beforeClose"
    draggable
    destroy-on-close
    @closed="typeRef?.clearValidate()"
  >
    <guide-tip-panel
      type="hint"
      class="mb15"
      allow-wrap
      title="定义业务对象及其层级结构，维护对象类型名称、描述和关联建模目标，为后续属性与关系建模提供基础。"
    />
    <el-form
      ref="typeRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <div class="h2-titles row-full">基础信息</div>

      <qt-form-item
        class="row-full"
        v-if="!editingRoot"
        label="上级对象类型"
        prop="parentId"
      >
        <el-tree-select
          v-model="form.parentId"
          :data="parentOptions"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          value-key="id"
          placeholder="请选择上级对象类型，留空则为顶级对象类型"
          check-strictly
          clearable
        />
      </qt-form-item>

      <qt-form-item
        class="row-full"
        label="对象类型名称"
        prop="name"
      >
        <div class="name-input">
          <el-input v-model="form.name" placeholder="请输入对象类型名称" maxlength="128"/>
          <el-button v-hasPermi="['om:term:term:list']" type="primary" @click="openTermPicker">
            从术语表中选择
          </el-button>
        </div>
      </qt-form-item>

      <qt-form-item
        class="row-full"
        label="显示顺序"
        prop="orderNum"
      >
        <el-input-number
          v-model="form.orderNum"
          :min="0"
          :max="2147483647"
          :precision="0"
          controls-position="right"
          style="width: 100%"
        />
      </qt-form-item>

      <qt-form-item
        class="row-full"
        label="描述"
        prop="description"
      >
        <el-input
          v-model="form.description"
          class="character-count"
          type="textarea"
          :rows="4"
          maxlength="256个字符"
          show-word-limit
          placeholder="请输入对象类型描述，说明业务含义、职责和适用范围"
        />
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button :disabled="saving" @click="open = false">取 消</el-button>
        <el-button
          type="primary"

          :loading="saving"
          @click="submitForm"
        >确 定</el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="termOpen"
    title="从术语表中选择"
    width="1000px"
    :append-to="pageRef"
    :close-on-click-modal="!saving"
    draggable
  >
    <el-form
      class="btn-style"
      :inline="true"
      :model="termQuery"
      @submit.prevent="searchTerms"
    >
      <el-form-item label="术语名称">
        <el-input
          v-model="termQuery.stdName"
          class="el-form-input-width"
          placeholder="请输入术语名称"
          clearable
          @keyup.enter="searchTerms"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain @click="searchTerms"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
        </el-button>
        <el-button @click="resetTerms"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="termTableRef"
      v-loading="termLoading"
      :data="terms"
      row-key="id"
      highlight-current-row
      stripe
      @current-change="selectedTerm = $event"
      @row-click="selectTerm"
    >
      <el-table-column
        prop="id"
        label="编号"
        width="65"
        align="center"
      />
      <el-table-column label="术语信息" min-width="235">
        <template #default="{ row }"><TermInfoCell :row="row" /></template>
      </el-table-column>
      <el-table-column
        v-for="field in termFields"
        :key="field.prop"
        :prop="field.prop"
        :label="field.label"
        :min-width="field.width"
        align="left"
        :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column label="状态" width="70" align="center">
        <template #default>启用</template>
      </el-table-column>
      <el-table-column label="创建人" min-width="80">
        <template #default="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis">{{ row.createBy || "-" }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="145" align="center">
        <template #default="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="termTotal > 0"
      :total="termTotal"
      v-model:page="termQuery.pageNum"
      v-model:limit="termQuery.pageSize"
      @pagination="getTerms"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="termOpen = false">取 消</el-button>
        <el-button
          type="primary"

          :disabled="Number(selectedTerm?.status) !== 1 || termLoading"
          @click="applyTerm"
        >确 定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup >
import TermInfoCell from "@/views/om/semantic/term/TermInfoCell.vue";
import {computed, getCurrentInstance, nextTick, reactive, ref, watch, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import {ElMessage, ElMessageBox} from 'element-plus';
import {listType, listTypeExcludeChild, getType, addType, updateType, delType, listTypeTargets} from '@/api/om/object/type';
import {listTerm} from '@/api/om/term/term';
import auth from '@/plugins/auth';
import { useCreatorPhones } from '@/utils/useCreatorPhones';

defineOptions({name: 'OmObjectType'});
// 项目版本优先从 URL 获取，嵌入使用时兼容上层传入。
const props = defineProps({
  versionId: {type: [Number, String], default: undefined},
  targetDetail: {type: Object, default: () => {}}
});
const router = useRouter();
const {proxy} = getCurrentInstance();
const {versionId, requireVersion} = useOntologyVersion(() => props.versionId);
const pageRef = ref();
const typeRef = ref();
const termTableRef = ref();
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const open = ref(false);
const termOpen = ref(false);
const existingTargetIds = ref([]);
const tableRef = ref();
const { creatorPhones } = useCreatorPhones();
const isExpandAll = ref(true);
const refreshTable = ref(true);
const dialogTitle = ref('新增对象类型');
const rows = ref([]);
const queryParams = reactive({ name: '', parentId: undefined });
const appliedParentId = ref();
const sort = reactive({ prop: 'orderNum', order: 'ascending' });
// 输入内容仅在提交查询时生效。
const appliedName = ref('');
const columns = reactive([
    { prop: 'name', label: '对象类型名称', slot: 'text', minWidth: 250, align: 'left', showOverflowTooltip: { effect: 'light' } },
    { prop: 'description', label: '描述', slot: 'description', minWidth: 260, align: 'left', showOverflowTooltip: { effect: 'light' } },
    { prop: 'orderNum', label: '显示顺序', slot: 'text', width: 100, sortable: 'custom' },
    { prop: 'attributeCount', label: '属性数量', slot: 'text', width: 90 },
    { prop: 'relationCount', label: '关系数量', slot: 'text', width: 90 },
    { prop: 'createBy', label: '创建人', slot: 'creator', width: 130, align: 'left', headerAlign: 'left' },
    { prop: 'createTime', label: '创建时间', slot: 'createTime', width: 160, sortable: 'custom' },
    { label: '操作', slot: 'operation', width: 240, fixed: 'right' },
]);
const tableConfig = computed(() => ({
    notPagination: true,
    sort: true,
    table: {
        rowKey: 'id', height: '60vh', stripe: true,
        defaultExpandAll: isExpandAll.value,
        treeProps: { children: 'children', hasChildren: 'hasChildren' },
        defaultSort: { prop: sort.prop, order: sort.order },
    },
}));
const tableEvents = {
    onSortChange({ orderByColumn, isAsc }) {
        sort.prop = isAsc ? orderByColumn : 'orderNum';
        sort.order = isAsc || 'ascending';
    },
};
const listActions = { getList };
function readTree() { return Promise.resolve({ data: filteredTree.value }); }
const emptyForm = () => ({
  id: undefined,
  workspaceId: 1001,
  versionId: versionId.value,
  name: '',
  description: '',
  parentId: null,
  orderNum: 0
});
const form = ref(emptyForm());
// 以打开弹窗时的节点层级判断，清空普通节点父级不会立即隐藏控件。
const editingRoot = ref(false);
const rules = {
  orderNum: [{required: true, message: '显示顺序不能为空', trigger: 'blur'}],
  name: [{required: true, whitespace: true, message: '对象类型名称不能为空', trigger: 'blur'}, {
    max: 128,
    message: '名称不能超过128个字符',
    trigger: 'blur'
  }],
  description: [{
    max: 256,
    message: '描述不能超过256个字符',
    trigger: 'blur'
  }]
};

function buildTree(items) {
  const nodes = new Map(items.map(item => [String(item.id), {...item, children: []}]));
  const roots = [];
  for (const node of nodes.values()) {
    const parent = nodes.get(String(node.parentId));
    if (parent && parent !== node) parent.children.push(node); else roots.push(node);
  }
  return roots;
}

const tree = computed(() => buildTree(rows.value));
const filteredTree = computed(() => {
    const keyword = appliedName.value.trim().toLowerCase();
    const parentId = appliedParentId.value;
    function filter(nodes, inherited = false, matchedParent = false) {
        return nodes.flatMap((node) => {
            const inScope = inherited || parentId == null || parentId === '' || String(node.parentId) === String(parentId);
            const matches = matchedParent || !keyword || node.name?.toLowerCase().includes(keyword);
            const children = filter(node.children, inScope, inScope && matches);
            if (inScope && matches) return [{ ...node, children }];
            return children.length ? [{ ...node, children }] : [];
        }).sort((a, b) => {
            const direction = sort.order === 'descending' ? -1 : 1;
            const comparison = sort.prop === 'orderNum'
                ? Number(a.orderNum ?? 0) - Number(b.orderNum ?? 0)
                : String(a.createTime || '').localeCompare(String(b.createTime || ''));
            return comparison * direction;
        });
    }
    return filter(tree.value);
});
const searchItems = computed(() => [
    { label: '对象类型名称', prop: 'name', component: { is: 'input' } },
    { label: '上级对象类型', prop: 'parentId', component: {
        is: 'tree-select', data: tree.value,
        props: { value: 'id', label: 'name', children: 'children' },
        valueKey: 'id', checkStrictly: true, clearable: true,
    } },
]);
watch(filteredTree, () => tableRef.value?.getList());
const parentOptions = ref([]);
let listRequest = 0;
let editRequest = 0;

async function getList() {
  const request = ++listRequest;
  if (!versionId.value || !props.targetDetail?.id) { rows.value = []; loading.value = false; return; }
  loading.value = true;
  try {
    const response = await listType({workspaceId: 1001, versionId: versionId.value, pageNum: 1, pageSize: -1,
      targetIdList: [props.targetDetail.id]});
    if (request !== listRequest) return;
    rows.value = response.data.rows || [];
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

async function toggleExpandAll() {
  if (!refreshTable.value) return;
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  await nextTick();
  refreshTable.value = true;
}

function handleQuery() {
  appliedName.value = (queryParams.name || "").trim();
  appliedParentId.value = queryParams.parentId;
  return getList();
}

async function handleAdd(parent) {
  if (!requireVersion()) return;
  const request = ++editRequest;
  form.value = emptyForm();
  existingTargetIds.value = [];
  editingRoot.value = false;
  parentOptions.value = [];
  if (parent) {
    form.value.parentId = parent.id;
  }
  dialogTitle.value = '新增对象类型';
  open.value = true;
  nextTick(() => typeRef.value?.clearValidate());
  const response = await listType({workspaceId: 1001, versionId: versionId.value, pageNum: 1, pageSize: -1});
  if (request !== editRequest || !open.value) return;
  parentOptions.value = proxy.handleTree(response.data.rows || [], 'id');
}

async function handleUpdate(row) {
  const request = ++editRequest;
  form.value = emptyForm();
  existingTargetIds.value = [];
  parentOptions.value = [];
  const [response, targetsResponse] = await Promise.all([getType(row.id), listTypeTargets(row.id)]);
  if (request !== editRequest) return;
  editingRoot.value = response.data.parentId == null || String(response.data.parentId) === '0';
  if (!editingRoot.value) {
    const optionsResponse = await listTypeExcludeChild(row.id);
    if (request !== editRequest) return;
    parentOptions.value = proxy.handleTree(optionsResponse.data || [], 'id');
  }
  form.value = {
    ...emptyForm(), ...response.data,
    parentId: editingRoot.value ? 0 : response.data.parentId
  };
  existingTargetIds.value = (targetsResponse.data || []).map(target => String(target.id));
  dialogTitle.value = '修改对象类型';
  open.value = true;
  nextTick(() => typeRef.value?.clearValidate());
}

function handleDetail(row) {
  router.push({
    name: 'OmTargetDetailObjectType',
    params: {id: row.id, versionId: versionId.value},
    // 携带当前建模目标 id，供对象类型详情页“返回”时还原目标详情上下文
    query: {targetId: props.targetDetail?.id}
  });
}

async function submitForm() {
  const targetId = props.targetDetail?.id;
  if (targetId == null || saving.value || !requireVersion()) return;
  const submittingVersion = versionId.value;
  const request = editRequest;
  const valid = await typeRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value || request !== editRequest
      || submittingVersion !== versionId.value || targetId !== props.targetDetail?.id) return;
  saving.value = true;
  try {
    const data = {
      id: form.value.id,
      workspaceId: 1001,
      versionId: submittingVersion,
      orderNum: form.value.orderNum,
      targetIds: [...new Set([String(targetId), ...existingTargetIds.value])],
      parentId: editingRoot.value || form.value.parentId == null || form.value.parentId === '' ? 0 : form.value.parentId,
      name: form.value.name.trim(),
      description: (form.value.description ?? '').trim()
    };
    await (data.id ? updateType(data) : addType(data));
    if (submittingVersion !== versionId.value || targetId !== props.targetDetail?.id) return;
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

async function handleDelete(row) {
  if (deleting.value || row?.id == null) return;
  try {
    await ElMessageBox.confirm(`是否删除对象类型“${row.name}”？`, '删除确认', {type: 'warning'});
  } catch {
    return;
  }
  deleting.value = true;
  try {
    await delType(row.id);
    ElMessage.success('删除成功');
    await getList();
  } finally {
    deleting.value = false;
  }
}

const termQuery = reactive({stdName: '', pageNum: 1, pageSize: 10});
const terms = ref([]);
const termTotal = ref(0);
const termLoading = ref(false);
const selectedTerm = ref();
const termFields = [
  { prop: 'alias', label: '术语别名', width: 105 },
  { prop: 'abbr', label: '业务缩写', width: 80 },
  { prop: 'source', label: '来源', width: 105 },
];
let termRequest = 0;

async function openTermPicker() {
  termOpen.value = true;
  resetTerms();
}

async function getTerms() {
  const request = ++termRequest;
  termLoading.value = true;
  selectedTerm.value = undefined;
  try {
    const response = await listTerm({...termQuery, workspaceId: 1001, versionId: versionId.value, status: 1, orderByColumn: "createTime", isAsc: "descending"});
    if (request !== termRequest) return;
    terms.value = (response.data.rows || []).filter(row => Number(row.status) === 1);
    termTotal.value = response.data.total || 0;
    await nextTick();
    termTableRef.value?.setCurrentRow();
  } finally {
    if (request === termRequest) termLoading.value = false;
  }
}

function selectTerm(row) {
  selectedTerm.value = row;
  termTableRef.value?.setCurrentRow(row);
}

function searchTerms() {
  termQuery.pageNum = 1;
  getTerms();
}

function resetTerms() {
  termQuery.stdName = '';
  searchTerms();
}

function applyTerm() {
  if (Number(selectedTerm.value?.status) !== 1) return;
  if ((selectedTerm.value.stdName || '').length > 128) {
    ElMessage.warning('术语名称超过128个字符，请手动输入对象类型名称');
    return;
  }
  form.value.name = selectedTerm.value.stdName;
  termOpen.value = false;
  nextTick(() => typeRef.value?.clearValidate());
}

watch(versionId, () => {
  rows.value = [];
  ++editRequest;
  open.value = false;
  termOpen.value = false;
  getList();
});
watch(() => props.targetDetail?.id, () => {
  ++editRequest;
  open.value = false;
  rows.value = [];
  queryParams.name = '';
  queryParams.parentId = undefined;
  appliedName.value = '';
  appliedParentId.value = undefined;
  getList();
});
onMounted(getList);
</script>

<style scoped lang="scss">
.toggle-expand-all .svg-icon {
    font-size: 12px;
    margin-right: 6px;
}
.object-type-actions {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    .el-button + .el-button { margin-left: 0; }
}

.target-object-types :deep(.qt-wrap--search) { padding: 0; }

.name-input {
  display: flex;
  gap: 0px;
  width: 100%;
}

.target-table { margin: 0 0 10px 120px; }

.object-type-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}

@media (max-width: 600px) {
  .name-input {
    flex-wrap: wrap;
  }
}
</style>
