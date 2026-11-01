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
  <div ref="pageRef" v-loading="!contextReady" class="enum-page" :class="embedded ? 'enum-data-embedded' : 'app-container'">
    <!-- 两种入口共用标准列表组件，详情内嵌时不重复显示整页卡片和搜索阴影。 -->
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef" :config="{fullContent: !embedded, searchShadow: !embedded}">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{collapsedCount: 4}"
          @query="applyFilters"
          @reset="applyFilters"
        />
      </template>
      <template #actions-data>
        <el-button
          v-hasPermi="['om:enumeration:data:add']"
          type="primary"
          plain
          :disabled="!defaultEnumType || !contextReady"
          @click="handleAdd"
        ><i class="iconfont-mini icon-xinzeng mr5"></i>新增</el-button>
        <el-button
          v-hasPermi="['om:enumeration:data:remove']"
          type="danger"
          plain
          :disabled="multiple || deleting"
          @click="handleDelete()"
        ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除</el-button>
        <el-button v-if="!embedded && route.params.enumId" type="warning" plain icon="Close" @click="handleClose">关闭</el-button>
      </template>
      <!-- 路由入口先解析枚举编码，再挂载表格；切换上下文时重建实例，隔离旧列表响应。 -->
      <qt-table v-if="contextReady" :key="defaultEnumType" v-bind="tableStore" ref="tableRef">
        <template #text="{row, column_data}">{{ row[column_data.prop] }}</template>
        <template #status="{row}">
          <dict-tag v-if="row.status !== '—'" :options="sys_normal_disable" :value="row.status"/>
          <span v-else>—</span>
        </template>
        <template #createTime="{row}">
          {{ row.createTime === '—' ? '—' : (parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '—') }}
        </template>
        <template #handle="{row}">
          <el-button v-hasPermi="['om:enumeration:data:query']" link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
          <el-popover
            v-if="auth.hasPermiOr(['om:enumeration:data:edit', 'om:enumeration:data:remove'])"
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference><el-button link type="primary" icon="ArrowDown">更多</el-button></template>
            <el-button v-if="auth.hasPermi('om:enumeration:data:edit')" link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button v-if="auth.hasPermi('om:enumeration:data:remove')" link type="danger" icon="Delete" :disabled="deleting" @click="handleDelete(row)">删除</el-button>
          </el-popover>
        </template>
        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="暂无记录"/>
            <p>{{ defaultEnumType ? '暂无记录' : '请先选择枚举类型并查询' }}</p>
          </div>
        </template>
      </qt-table>
    </qt-wrap>
    <el-dialog
      v-model="open"
      :title="title"
      width="800px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      :close-on-press-escape="!saving"
      :before-close="handleBeforeClose"
      draggable
      @closed="clearValidation"
    >
      <el-form ref="dataRef" :model="form" :rules="rules" label-width="90px" class="column-form" @submit.prevent="submitForm">
        <el-form-item class="row-full" label="枚举类型">
          <el-input v-model="form.enumType" disabled/>
        </el-form-item>
        <el-form-item class="row-full" label="枚举标签" prop="enumLabel">
          <el-input v-model="form.enumLabel" placeholder="请输入枚举标签" :maxlength="128"/>
        </el-form-item>
        <el-form-item class="row-full" label="枚举键值" prop="enumValue">
          <el-input v-model="form.enumValue" placeholder="请输入枚举键值" :maxlength="128"/>
        </el-form-item>
        <el-form-item class="row-full" label="排序" prop="enumSort">
          <el-input-number
            v-model="form.enumSort"
            :min="0"
            :max="2147483647"
            :precision="0"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item class="row-full" label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">{{
                dict.label
              }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item class="row-full" label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            class="character-count"
            type="textarea"
            :rows="3"
            :maxlength="512"
            show-word-limit
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="openDetail"
      class="enum-detail-dialog"
      title="枚举数据详情"
      width="800px"
      :append-to="pageRef"
      draggable
    >
      <el-form :model="detail" label-width="90px" label-position="right" class="enum-detail-form column-form">
        <el-form-item class="row-full" label="编号">
          <div class="form-readonly">{{ detail.enumCode }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="枚举类型">
          <div class="form-readonly">{{ detail.enumType }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="枚举标签">
          <div class="form-readonly">{{ detail.enumLabel ?? '—' }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="枚举键值">
          <div class="form-readonly">{{ detail.enumValue ?? '—' }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="排序">
          <div class="form-readonly">{{ detail.enumSort ?? '—' }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="状态">
          <dict-tag :options="sys_normal_disable" :value="detail.status"/>
        </el-form-item>
        <el-form-item class="row-full" label="创建人">
          <div class="form-readonly">{{ detail.createBy || '—' }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="创建时间">
          <div class="form-readonly">{{ parseTime(detail.createTime) || '—' }}</div>
        </el-form-item>
        <el-form-item class="row-full" label="备注">
          <div class="form-readonly textarea">{{ detail.remark || '-' }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="openDetail = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OmEnumData">
import {computed, onBeforeUnmount, ref, reactive, toRefs, nextTick, watch, getCurrentInstance} from 'vue';
import {useRoute} from 'vue-router';
import {listData, getData, addData, updateData, delData} from '@/api/basic/enumeration/data';
import {getType} from '@/api/basic/enumeration/type';
import auth from '@/plugins/auth';

// 传入 enumType 时作为详情页内嵌列表使用；未传入时沿用独立页面的路由加载逻辑。
const props = defineProps({
  enumType: {type: String, default: undefined}
});
// 增删改成功后通知详情页重新查询未筛选总数，不将表格的筛选总数当作概要计数。
const emit = defineEmits(['dataChange']);
const embedded = computed(() => props.enumType !== undefined);
const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict('sys_normal_disable');
const route = useRoute();
const pageRef = ref();
const tableRef = ref();
const dataRef = ref();
const open = ref(false);
const openDetail = ref(false);
const saving = ref(false);
const deleting = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref('');
const detail = ref({});
const defaultEnumType = ref('');
const contextReady = ref(false);
const defaultSort = {prop: 'enumSort', order: 'ascending'};
// 查询条件仅在点击查询或重置时提交，分页、排序与刷新不使用输入草稿。
const appliedFilters = reactive({enumLabel: '', status: ''});
const tableStore = reactive({
  config: {
    sort: true,
    table: {stripe: true, rowKey: 'enumCode', defaultSort, onSelectionChange: handleSelectionChange}
  },
  columns: [
    {type: 'selection', width: 55},
    {label: '编号', prop: 'enumCode', slot: 'text', width: 80, sortable: true},
    {label: '枚举标签', prop: 'enumLabel', slot: 'text', minWidth: 150, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '枚举键值', prop: 'enumValue', slot: 'text', minWidth: 150, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '排序', prop: 'enumSort', slot: 'text', width: 100, sortable: true},
    {label: '状态', prop: 'status', slot: 'status', width: 100},
    {label: '备注', prop: 'remark', slot: 'text', minWidth: 200, align: 'left', showOverflowTooltip: {effect: 'light'}},
    {label: '创建人', prop: 'createBy', slot: 'text', width: 120, showOverflowTooltip: {effect: 'light'}},
    {label: '创建时间', prop: 'createTime', slot: 'createTime', width: 160, sortable: true},
    {label: '操作', slot: 'handle', width: 200, fixed: 'right'}
  ],
  func: loadEnumData,
  params: {enumLabel: '', status: ''},
  events: {formatParams: formatListParams, formatData: formatListData, onDataLoaded: clearSelection}
});

// 与枚举列表相同的配置式搜索栏，状态筛选沿用正常/停用字典。
const searchStore = reactive({
  items: [
    {label: '枚举标签', prop: 'enumLabel', component: {is: 'input'}},
    {label: '状态', prop: 'status', component: {is: 'select', options: sys_normal_disable}}
  ]
});

const data = reactive({
  form: {},
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
const {form, rules} = toRefs(data);
let contextRequest = 0;
let detailRequest = 0;

/** 提交搜索条件；公共搜索栏随后重置页码并加载表格。 */
function applyFilters() {
  appliedFilters.enumLabel = (tableStore.params.enumLabel || '').trim();
  appliedFilters.status = tableStore.params.status ?? '';
}

/** 固定当前枚举编码，取消排序时恢复默认的枚举排序值升序。 */
function formatListParams(params) {
  return {
    ...params,
    ...appliedFilters,
    enumType: defaultEnumType.value,
    orderByColumn: params.isAsc ? params.orderByColumn : defaultSort.prop,
    isAsc: params.isAsc || defaultSort.order
  };
}

/** 无枚举编码时返回空页，禁止接口因忽略空条件而查询其他枚举的数据。 */
function loadEnumData(params) {
  if (!params.enumType) return Promise.resolve({data: {rows: [], total: 0}});
  return listData(params);
}

/** 统一公共表格和插槽的空值占位符，保留枚举键值与排序值中的数值零。 */
function formatListData(rows) {
  const fields = tableStore.columns.map(column => column.prop).filter(Boolean);
  return rows.map(row => {
    const record = {...row};
    fields.forEach(field => {
      if (record[field] == null || record[field] === '') record[field] = '—';
    });
    return record;
  });
}

/** 列表刷新后移除旧的选中项，避免批量删除使用上一次查询的数据。 */
function clearSelection() {
  handleSelectionChange([]);
  tableRef.value?.elTableRef?.clearSelection();
}

/** 保存后通过公共表格刷新当前页，保留已提交的筛选条件。 */
function getList() {
  tableRef.value?.getList();
}

/** 重置新增或修改表单，枚举编码始终取自当前页面上下文。 */
function reset() {
  form.value = {
    enumCode: undefined,
    enumLabel: '',
    enumValue: '',
    enumSort: 0,
    enumType: defaultEnumType.value,
    status: '0',
    remark: ''
  };
  clearValidation();
}

/** 清除表单旧的校验结果。 */
function clearValidation() {
  dataRef.value?.clearValidate();
}

/** 保存期间阻止关闭弹窗，避免重复操作。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 未保存时关闭弹窗并重置表单。 */
function cancel() {
  if (saving.value) return;
  open.value = false;
  reset();
}

/** 同步当前选中的枚举数据编码，并控制批量删除按钮状态。 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.enumCode);
  multiple.value = !selection.length;
}

/** 新增前使旧的弹窗请求失效，避免迟到响应覆盖新增表单。 */
async function handleAdd() {
  if (!defaultEnumType.value || !contextReady.value) return;
  ++detailRequest;
  reset();
  title.value = '新增枚举数据';
  open.value = true;
  await nextTick();
  clearValidation();
}

/** 加载修改表单，仅采纳当前操作的最新响应。 */
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

/** 加载选中数据的只读详情，忽略已切换上下文的旧响应。 */
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

/** 校验并保存枚举数据，成功后通知父级同步总数并刷新当前列表。 */
async function submitForm() {
  if (saving.value) return;
  const valid = await dataRef.value.validate().catch(() => false);
  if (!valid || saving.value  || !open.value) return;
  saving.value = true;
  try {
    const payload = {
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
    emit('dataChange');
    proxy.$modal.msgSuccess(form.value.enumCode != null ? '修改成功' : '新增成功');
    open.value = false;
    getList();
  } finally {
    saving.value = false;
  }
}

/** 确认并删除选中数据，仅在枚举上下文未改变时刷新列表和父级计数。 */
async function handleDelete(row) {
  const context = contextRequest;
  const recordIds = row ? [row.enumCode] : [...ids.value];
  if (!recordIds.length || deleting.value) return;
  try {
    await proxy.$modal.confirm('是否确认删除枚举编码为"' + recordIds.join(',') + '"的数据项？');
  } catch {
    return;
  }
  if (deleting.value || context !== contextRequest ) return;
  deleting.value = true;
  try {
    await delData(recordIds.join(','));
    if (context !== contextRequest ) return;
    emit('dataChange');
    // 删除后回到第一页，避免删除末页全部数据后保留空页。
    tableRef.value?.resetQuery();
    proxy.$modal.msgSuccess('删除成功');
  } finally {
    deleting.value = false;
  }
}

/** 关闭独立数据页签并返回枚举列表。 */
function handleClose() {
  proxy.$tab.closeOpenPage({path: '/basic/basicEnum'});
}

let typeRequest = 0;
/** 解析内嵌参数或独立页面路由的枚举编码，切换时重置列表、筛选和弹窗上下文。 */
watch(() => [props.enumType, embedded.value ? undefined : route.params.enumId], async ([enumType, enumId]) => {
  ++contextRequest;
  const request = ++typeRequest;
  ++detailRequest;
  contextReady.value = false;
  defaultEnumType.value = '';
  open.value = false;
  openDetail.value = false;
  handleSelectionChange([]);
  tableStore.params.enumLabel = '';
  tableStore.params.status = '';
  appliedFilters.enumLabel = '';
  appliedFilters.status = '';
  // 等待旧表格卸载，即使新旧入口解析出相同编码，也重新建立分页和请求上下文。
  await nextTick();
  if (request !== typeRequest) return;
  try {
    if (enumType !== undefined) {
      // 详情页已取得枚举编码，直接复用，避免重复加载类型详情及全量选项。
      defaultEnumType.value = enumType;
    } else if (enumId) {
      const response = await getType(enumId);
      if (request !== typeRequest) return;
      if (!response.data) {
        proxy.$modal.msgWarning('枚举类型已不存在');
        return;
      }
      defaultEnumType.value = response.data.enumType;
    }
  } catch {
    // 错误由全局请求层提示；保持空编码，避免失败后继续展示上一个枚举的数据。
  } finally {
    if (request === typeRequest) contextReady.value = true;
  }
}, {immediate: true});

// 页面离开后使类型加载、删除确认及弹窗详情请求失效。
onBeforeUnmount(() => { ++contextRequest; ++typeRequest; ++detailRequest; });

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
