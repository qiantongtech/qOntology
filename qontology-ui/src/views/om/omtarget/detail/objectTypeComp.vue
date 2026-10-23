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
  <div>
    <div class="justify-between mb15">
      <el-row :gutter="15" class="btn-style">
        <el-col :span="1.5">
          <el-button v-hasPermi="['om:object:type:add']" type="primary" plain @click="handleAdd()"><i
              class="iconfont-mini icon-xinzeng mr5"
          ></i>新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button class="toggle-expand-all" type="primary" plain @click="toggleExpandAll">
            <svg-icon v-if="isExpandAll" icon-class="toggle"/>
            <svg-icon v-else icon-class="expand"/>
            <span>{{ isExpandAll ? '折叠' : '展开' }}</span>
          </el-button>
        </el-col>
      </el-row>
      <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"/>
    </div>
    <el-table v-if="refreshTable" v-loading="loading" :data="filteredTree" row-key="id"
              :default-expand-all="isExpandAll"
              :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column v-if="visible('name')" prop="name" label="对象类型名称" width="250" align="left"
                       :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column v-if="visible('description')" prop="description" label="描述" min-width="260" align="left"
                       :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column v-if="visible('orderNum')" prop="orderNum" label="显示顺序" width="100" align="center">
        <template #default="{ row }">{{ row.orderNum ?? '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('attributeCount')" label="属性数量" width="90" align="center">
        <template #default="{ row }">{{ row.attributeCount ?? '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('relationCount')" label="关系数量" width="90" align="center">
        <template #default="{ row }">{{ row.relationCount ?? '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('createBy')" prop="createBy" label="创建人" width="120" align="center"
                       :show-overflow-tooltip="{ effect: 'light' }"
      />
      <el-table-column v-if="visible('createTime')" label="创建时间" width="180" align="center">
        <template #default="{ row }">{{ proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</template>
      </el-table-column>
      <el-table-column v-if="visible('operation')" label="操作" width="240" align="center" fixed="right"
                       class-name="small-padding fixed-width"
      >
        <template #default="{ row }">
          <el-button v-hasPermi="['om:object:type:query']" link type="primary" icon="View" @click="handleDetail(row)">
            详情
          </el-button>
          <el-button v-hasPermi="['om:object:type:add']" link type="primary" icon="Plus" @click="handleAdd(row)">
            新增
          </el-button>
          <el-popover v-if="auth.hasPermiOr(['om:object:type:edit', 'om:object:type:remove'])"
                      placement="bottom" :width="150" trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="butgdlist">
              <el-button v-if="auth.hasPermi('om:object:type:edit')" link type="primary" icon="Edit"
                         @click="handleMoreCommand('edit', row)"
              >修改
              </el-button>
              <el-button v-if="auth.hasPermi('om:object:type:remove')" link type="danger" icon="Delete"
                         :disabled="deleting" @click="handleMoreCommand('delete', row)"
              >删除
              </el-button>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <template #empty>
        <div class="emptyBg"><img src="@/assets/system/images/no_data/noData.png" alt="暂无记录"/>
          <p>暂无记录</p></div>
      </template>
    </el-table>
  </div>
  <el-dialog v-model="open" :title="dialogTitle" width="800px" :append-to="pageRef" :close-on-click-modal="false"
             :close-on-press-escape="!saving" :before-close="beforeClose" draggable destroy-on-close
             @closed="typeRef?.clearValidate()"
  >
    <el-form ref="typeRef" :model="form" :rules="rules" label-width="120px" @submit.prevent="submitForm">
      <el-row :gutter="20">
        <el-col v-if="!editingRoot" :span="24">
          <el-form-item label="上级对象类型" prop="parentId">
            <el-tree-select
                v-model="form.parentId"
                :data="parentOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                value-key="id"
                placeholder="请选择上级对象类型，留空则为顶级对象类型"
                check-strictly
                clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="对象类型名称" prop="name">
            <div class="name-input">
              <el-input v-model="form.name" placeholder="请输入对象类型名称" maxlength="128"/>
              <el-button v-hasPermi="['om:term:term:list']" type="primary" @click="openTermPicker">
                从术语表中选择
              </el-button>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序" prop="orderNum">
            <el-input-number v-model="form.orderNum" :min="0" :max="2147483647" :precision="0"
                             controls-position="right" style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" class="character-count" type="textarea" :rows="4" maxlength="512"
                      show-word-limit placeholder="请输入对象类型描述"
            />
          </el-form-item>
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
  <el-dialog v-model="termOpen" title="从术语表中选择" width="1000px" :append-to="pageRef"
             :close-on-click-modal="false" draggable
  >
    <el-form class="btn-style" :inline="true" :model="termQuery" @submit.prevent="searchTerms">
      <el-form-item label="标准名称">
        <el-input v-model="termQuery.stdName" class="el-form-input-width" placeholder="请输入标准名称" clearable
                  @keyup.enter="searchTerms"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain @click="searchTerms"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
        </el-button>
        <el-button @click="resetTerms"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
      </el-form-item>
    </el-form>
    <el-table ref="termTableRef" v-loading="termLoading" :data="terms" row-key="id" highlight-current-row stripe
              @current-change="selectedTerm = $event" @row-click="selectTerm"
    >
      <el-table-column type="index" label="序号" width="65" align="center"/>
      <el-table-column v-for="field in termFields" :key="field.prop" :prop="field.prop" :label="field.label"
                       :min-width="field.width" align="left" :show-overflow-tooltip="{ effect: 'light' }"
      />
    </el-table>
    <pagination v-show="termTotal > 0" :total="termTotal" v-model:page="termQuery.pageNum"
                v-model:limit="termQuery.pageSize" @pagination="getTerms"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" @click="termOpen = false">取 消</el-button>
        <el-button type="primary" size="mini" :disabled="!selectedTerm || termLoading" @click="applyTerm">确 定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup >
import {computed, getCurrentInstance, nextTick, reactive, ref, watch, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useOntologyVersion} from '@/views/om/useOntologyVersion';
import {ElMessage, ElMessageBox} from 'element-plus';
import {listType, listTypeExcludeChild, getType, addType, updateType, delType, listTypeTargets} from '@/api/om/object/type';
import {listTerm} from '@/api/om/term/term';
import auth from '@/plugins/auth';

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
const targetPickerOpen = ref(false);
const selectedTargets = ref([]);
const showSearch = ref(true);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const dialogTitle = ref('新增对象类型');
const rows = ref([]);
const queryParams = reactive({name: ''});
// 输入内容仅在提交查询时生效。
const appliedName = ref('');
const columns = ref([
  ['name', '对象类型名称'], ['orderNum', '显示顺序'], ['description', '描述'], ['attributeCount', '属性数量'], ['relationCount', '关系数量'], ['createBy', '创建人'], ['createTime', '创建时间'], ['operation', '操作']
].map(([prop, label], key) => ({prop, label, key, visible: true})));
const visible = prop => columns.value.find(column => column.prop === prop)?.visible;
const emptyForm = () => ({
  id: undefined,
  workspaceId: 1001,
  versionId: versionId.value,
  name: '',
  description: '',
  parentId: null,
  orderNum: 0,
  targetIds: []
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
    max: 512,
    message: '描述不能超过512个字符',
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
  const filter = nodes => nodes.flatMap(node => {
    if (!keyword || node.name?.toLowerCase().includes(keyword)) return [node];
    const children = filter(node.children);
    return children.length ? [{...node, children}] : [];
  });
  return filter(tree.value);
});
const parentOptions = ref([]);
let listRequest = 0;
let editRequest = 0;

async function getList() {
  const request = ++listRequest;
  if (!versionId.value) { rows.value = []; loading.value = false; return; }
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
  appliedName.value = queryParams.name.trim();
  return getList();
}

function resetQuery() {
  queryParams.name = '';
  return handleQuery();
}

async function handleAdd(parent) {
  if (!requireVersion()) return;
  const request = ++editRequest;
  form.value = emptyForm();
  selectedTargets.value = [];
  targetPickerOpen.value = false;
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
  selectedTargets.value = [];
  targetPickerOpen.value = false;
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
  selectedTargets.value = targetsResponse.data || [];
  form.value.targetIds = [props.targetDetail.id];
  dialogTitle.value = '修改对象类型';
  open.value = true;
  nextTick(() => typeRef.value?.clearValidate());
}

function handleDetail(row) {
  router.push({
    name: 'OmTargetDetailObjectType',
    params: {id: row.id, versionId: versionId.value}
  });
}

async function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  if (saving.value) return;
  if (!await typeRef.value.validate().catch(() => false) || submittingVersion !== versionId.value || !open.value) return;
  saving.value = true;
  try {
    const data = {
      id: form.value.id,
      workspaceId: 1001,
      versionId: submittingVersion,
      orderNum: form.value.orderNum,
      targetIds: [props.targetDetail.id],
      parentId: editingRoot.value || form.value.parentId == null || form.value.parentId === '' ? 0 : form.value.parentId,
      name: form.value.name.trim(),
      description: (form.value.description ?? '').trim()
    };
    await (data.id ? updateType(data) : addType(data));
    if (submittingVersion !== versionId.value) return;
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

function handleMoreCommand(command, row) {
  if (command === 'edit' && auth.hasPermi('om:object:type:edit')) return handleUpdate(row);
  if (command === 'delete' && !deleting.value && auth.hasPermi('om:object:type:remove')) return handleDelete(row);
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
const termFields = [{prop: 'stdName', label: '标准名称', width: 130}, {
  prop: 'enName',
  label: '英文名称',
  width: 130
}, {prop: 'alias', label: '别名', width: 140}, {prop: 'abbr', label: '缩写', width: 100}, {
  prop: 'source',
  label: '来源',
  width: 140
}, {prop: 'description', label: '描述', width: 200}];
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
    const response = await listTerm({...termQuery, workspaceId: 1001, versionId: versionId.value});
    if (request !== termRequest) return;
    terms.value = response.data.rows || [];
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
  if (!selectedTerm.value) return;
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
  targetPickerOpen.value = false;
  getList();
});
watch(open, value => { if (!value) targetPickerOpen.value = false; });
onMounted(getList);
</script>

<style scoped lang="scss">
.toggle-expand-all {
  min-width: 60px;
  height: 30px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  line-height: 1;
  --el-button-text-color: #2666fb;
  --el-button-border-color: #7aa7ff;

  :deep(.svg-icon) {
    width: 12px;
    height: 12px;
    margin-right: 6px;
    color: currentColor;
    fill: currentColor;
    vertical-align: middle;
  }

  :deep(> span) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }

  span {
    line-height: 1;
  }
}

/* 复用qKnow非结构化抽取页面的“更多”弹层按钮样式。 */
.butgdlist {
  width: 100px;
  margin: 0 auto;
  display: flex !important;
  flex-direction: column;
  align-items: center !important;
}

.butgdlist .el-button {
  width: 100%;
  justify-content: center;
  margin-left: 0 !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.name-input {
  display: flex;
  gap: 12px;
  width: 100%;
}

.target-table { margin: 0 0 10px 120px; }

.object-type-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

.character-count :deep(.el-input__count)::after {
  content: ' 个字符';
}

.character-count :deep(.el-textarea__inner) {
  padding-bottom: 24px;
}

@media (max-width: 600px) {
  .target-table { margin-left: 0; }
  .name-input {
    flex-wrap: wrap;
  }
}
</style>
