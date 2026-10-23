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
  <div class="app-container" ref="app-container">
    <GuideTip tip-id="om/semantic/dataType.list"/>
    <div class="pagecont-top" v-show="showSearch">
      <el-form class="btn-style" :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" @submit.prevent>
        <el-form-item label="名称" prop="name">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.name"
              placeholder="请输入名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="数据类型" prop="dataType">
          <el-select class="el-form-input-width" v-model="queryParams.dataType" placeholder="请选择数据类型">
            <el-option v-for="item in basic_data_type"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button plain type="primary" @click="handleQuery" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button type="primary" plain @click="handleAdd" v-hasPermi="['om:dataType:datatype:add']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['om:dataType:datatype:remove']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </div>
      </div>
      <el-table stripe v-loading="loading" :data="dataTypeList" @selection-change="handleSelectionChange"
                :default-sort="defaultSort" @sort-change="handleSortChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" align="center" prop="id" width="80px"
                         sortable="custom" :sort-orders="['descending', 'ascending']"/>
        <el-table-column v-if="getColumnVisibility(1)" label="名称" align="left" prop="name">
          <template #default="scope">
            {{ scope.row.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="描述" align="left" prop="description">
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="数据类型" align="center" prop="dataType" width="120px">
          <template #default="scope">
            <dict-tag :options="basic_data_type" :value="scope.row.dataType"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="创建人" align="center" prop="createBy" width="180px">
          <template #default="scope">
            {{ scope.row.createBy || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(5)" label="创建时间" align="center" prop="createTime" width="180"
                         sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(6)" label="操作" align="center" fixed="right" width="240"
                         class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="view" @click="handleDetail(scope.row)"
                       v-hasPermi="['om:dataType:datatype:query']">详情
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                       v-hasPermi="['om:dataType:datatype:edit']">修改
            </el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                       v-hasPermi="['om:dataType:datatype:remove']">删除
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt=""/>
            <p>暂无记录</p>
          </div>
        </template>
      </el-table>

      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </div>

    <!-- 添加或修改数据类型对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" :append-to="$refs['app-container']" draggable>
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="dataTypeRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" :maxlength="128" placeholder="请输入名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据类型" prop="dataType">
              <el-select v-model="form.dataType" placeholder="请选择数据类型">
                <el-option v-for="item in basic_data_type"
                           :key="item.value"
                           :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24" v-if="[dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(form.dataType)">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" :maxlength="32" placeholder="请输入单位"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.DECIMAL].includes(form.dataType)">
            <el-form-item label="小数位数" prop="decimalPlaces">
              <el-input-number v-model="form.decimalPlaces" :min="0" :max="2147483647" :precision="0"
                               controls-position="right" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.DATE].includes(form.dataType)">
            <el-form-item label="格式" prop="format">
              <el-input v-model="form.format" :maxlength="128" placeholder="请输入格式，如 yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.ENUM].includes(form.dataType)">
            <el-form-item label="枚举类型" prop="enumType">
              <el-select v-model="selectedEnum" placeholder="请选择枚举类型" filterable clearable
                         :filter-method="filterEnums" :loading="enumLoading && !enumTypeDict.length"
                         :no-data-text="enumLoadErrors.length ? '枚举加载失败，请重新展开重试' : '无数据'"
                         @visible-change="handleEnumVisible">
                <template #label>
                  <span class="enum-selected-label">
                    <span>{{ selectedEnumName }}</span>
                    <el-tag class="enum-source-tag" :type="form.enumSource === 'GLOBAL' ? 'info' : 'primary'"
                            size="small" :disable-transitions="true">{{ selectedEnumSourceLabel }}</el-tag>
                  </span>
                </template>
                <el-option-group v-for="group in enumGroups" :key="group.source" :label="group.label">
                  <el-option v-for="item in group.options" :key="item.enumSource + ':' + item.enumType"
                             :label="item.enumName"
                             :value="item.enumSource + ':' + item.enumType"/>
                </el-option-group>
              </el-select>
              <div v-if="enumLoadErrors.length" class="enum-load-error" role="status">
                {{ enumLoadErrors.map(source => source === 'PROJECT' ? '当前项目' : '全局').join('、') }}枚举加载失败，可继续选择已加载的枚举；重新展开可重试。
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入描述" maxlength="500个字符" show-word-limit/>
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

    <!-- 数据类型详情对话框 -->
    <el-dialog :title="title" v-model="openDetail" width="800px" :append-to="$refs['app-container']" draggable>
      <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="dataTypeRef" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <div class="form-readonly">
                {{ form.name }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据类型" prop="dataType">
              <dict-tag
                  :options="basic_data_type"
                  :value="form.dataType"
                  class="con-value access-tag"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24" v-if="[dataTypeConstant.DECIMAL, dataTypeConstant.INTEGER].includes(form.dataType)">
            <el-form-item label="单位" prop="unit">
              <div class="form-readonly">
                {{ form.unit }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.DECIMAL].includes(form.dataType)">
            <el-form-item label="小数位数" prop="decimalPlaces">
              <div class="form-readonly">
                {{ form.decimalPlaces }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.DATE].includes(form.dataType)">
            <el-form-item label="格式" prop="format">
              <div class="form-readonly">
                {{ form.format }}
              </div>

            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.ENUM].includes(form.dataType)">
            <el-form-item label="枚举类型" prop="enumType">
              <div class="form-readonly">
                <span v-if="form.enumType" class="enum-selected-label">
                  <span>{{ selectedEnumName }}</span>
                  <el-tag class="enum-source-tag" :type="form.enumSource === 'GLOBAL' ? 'info' : 'primary'"
                            size="small" :disable-transitions="true">{{ selectedEnumSourceLabel }}</el-tag>
                </span>
                <span v-else>—</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div class="form-readonly textarea">{{ form.description || '—' }}</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="OmDataType">
import {addDataType, delDataType, getDataType, listDataType, updateDataType} from "@/api/om/dataType/dataType";
import {getEnumTypeDict} from "@/api/om/dataType/dataType";
import {useOntologyVersion} from "@/views/om/useOntologyVersion";
const {versionId, requireVersion} = useOntologyVersion();

const {proxy} = getCurrentInstance();
const {basic_data_type} = proxy.useDict('basic_data_type');

const dataTypeList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: "编号", visible: true},
  {key: 1, label: "名称", visible: true},
  {key: 2, label: "描述", visible: true},
  {key: 3, label: "数据类型", visible: true},
  {key: 4, label: "创建人", visible: true},
  {key: 5, label: "创建时间", visible: true},
  {key: 6, label: "操作", visible: true},
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find(col => col.key === key);
  // 如果没有找到对应列配置，默认显示
  if (!column) return true;
  // 如果找到对应列配置，根据visible属性来控制显示
  return column.visible;
};

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const defaultSort = ref({prop: "createTime", order: "descending"});
const enumTypeDict = ref([]);
const enumSearch = ref('');
const enumLoading = ref(false);
const enumLoadErrors = ref([]);

// 数据类型 0-文本、1-整数、2-小数、3-布尔、4-日期、5-枚举
const dataTypeConstant = {
  TEXT: "0",
  INTEGER: "1",
  DECIMAL: "2",
  BOOLEAN: "3",
  DATE: "4",
  ENUM: "5",
}

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    dataType: null,
    description: null,
    unit: null,
    decimalPlaces: null,
    format: null,
    createTime: null,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order
  },
  rules: {
    name: [{required: true, message: "名称不能为空", trigger: "blur"}],
    dataType: [{required: true, message: "数据类型不能为空", trigger: "blur"}],
    unit: [{required: true, whitespace: true, message: "单位不能为空", trigger: "blur"}, {max: 32, message: "单位不能超过32个字符", trigger: "blur"}],
    decimalPlaces: [{required: true, message: "小数位数不能为空", trigger: "change"}, {validator: (_rule, value, callback) => callback(value != null && value !== '' && (!Number.isInteger(value) || value < 0) ? new Error("小数位数必须为非负整数") : undefined), trigger: "change"}],
    format: [{required: true, whitespace: true, message: "格式不能为空", trigger: "blur"}, {max: 128, message: "格式不能超过128个字符", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

const selectedEnum = computed({
  get: () => form.value.enumType ? `${form.value.enumSource || 'PROJECT'}:${form.value.enumType}` : undefined,
  set: (value) => {
    const separator = (value || '').indexOf(':');
    form.value.enumSource = separator < 0 ? 'PROJECT' : value.slice(0, separator);
    form.value.enumType = separator < 0 ? null : value.slice(separator + 1);
  }
});
const enumGroups = computed(() => {
  const search = enumSearch.value.trim().toLowerCase();
  return [{source: 'PROJECT', label: '当前项目'}, {source: 'GLOBAL', label: '全局'}]
    .map(group => ({...group, options: enumTypeDict.value.filter(item => item.enumSource === group.source
      && (!search || `${item.enumName} ${item.enumType}`.toLowerCase().includes(search)))}))
    .filter(group => group.options.length);
});
const selectedEnumName = computed(() => {
  const source = form.value.enumSource || 'PROJECT';
  const option = enumTypeDict.value.find(item => item.enumSource === source && item.enumType === form.value.enumType);
  return option?.enumName || form.value.enumType || '—';
});
const selectedEnumSourceLabel = computed(() => form.value.enumSource === 'GLOBAL' ? '全局' : '当前项目');
function filterEnums(value) { enumSearch.value = value; }
function handleEnumVisible(visible) {
  enumSearch.value = '';
  if (visible) queryEnumTypeDict();
}


let listRequest = 0;
let enumRequest = 0;
let detailRequest = 0;
watch(versionId, () => {
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  dataTypeList.value = [];
  enumTypeDict.value = [];
  enumSearch.value = '';
  enumLoadErrors.value = [];
  handleSelectionChange([]);
  queryParams.value.pageNum = 1;
  getList();
  queryEnumTypeDict();
}, {immediate: true});

/** 查询数据类型列表 */
async function getList() {
  const request = ++listRequest;
  if (!versionId.value) { dataTypeList.value = []; total.value = 0; loading.value = false; return; }
  loading.value = true;
  try {
    const response = await listDataType({...queryParams.value, versionId: versionId.value});
    if (request !== listRequest) return;
    dataTypeList.value = response.data.rows;
    total.value = response.data.total;
    handleSelectionChange([]);
  } finally {
    if (request === listRequest) loading.value = false;
  }
}

async function queryEnumTypeDict() {
  const request = ++enumRequest;
  const requestedVersion = versionId.value;
  if (!requestedVersion) { enumLoading.value = false; return; }
  enumLoading.value = true;
  enumLoadErrors.value = [];
  const isCurrentRequest = () => request === enumRequest && requestedVersion === versionId.value;
  try {
    await Promise.allSettled(['PROJECT', 'GLOBAL'].map(async source => {
      try {
        const response = await getEnumTypeDict(requestedVersion, source);
        if (!isCurrentRequest()) return;
        if (!Array.isArray(response.data)) throw new Error('枚举接口未返回有效列表');
        const options = response.data.map(item => ({...item, enumSource: source}));
        // 仅替换本来源的数据，另一来源为空或失败时保留已经加载的选项。
        enumTypeDict.value = [...enumTypeDict.value.filter(item => item.enumSource !== source), ...options];
      } catch {
        if (isCurrentRequest()) enumLoadErrors.value = [...enumLoadErrors.value, source];
      }
    }));
  } finally {
    if (isCurrentRequest()) enumLoading.value = false;
  }
}

// 取消按钮
function cancel() {
  ++detailRequest;
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    versionId: versionId.value,
    enumType: null,
    enumSource: "PROJECT",
    name: null,
    dataType: null,
    description: null,
    unit: null,
    decimalPlaces: 2,
    format: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm("dataTypeRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 排序触发事件 */
function handleSortChange(column) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  if (!requireVersion()) return;
  reset();
  open.value = true;
  title.value = "添加数据类型";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const request = ++detailRequest;
  reset();
  const _id = row.id || ids.value
  getDataType(_id, versionId.value).then(response => {
    if (request !== detailRequest) return;
    form.value = response.data;
    open.value = true;
    title.value = "修改数据类型";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  const request = ++detailRequest;
  reset();
  const _id = row.id || ids.value
  getDataType(_id, versionId.value).then(response => {
    if (request !== detailRequest) return;
    form.value = response.data;
    openDetail.value = true;
    title.value = "数据类型详情";
  });
}

/** 提交按钮 */
function submitForm() {
  if (!requireVersion()) return;
  const submittingVersion = versionId.value;
  proxy.$refs["dataTypeRef"].validate(valid => {
    if (valid && submittingVersion === versionId.value && open.value) {
      if (form.value.id != null) {
        updateDataType({...form.value, versionId: submittingVersion}).then(response => {
          if (submittingVersion !== versionId.value) return;
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      } else {
        addDataType({...form.value, versionId: submittingVersion}).then(response => {
          if (submittingVersion !== versionId.value) return;
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const deletingVersion = versionId.value;
  const _ids = row?.id || [...ids.value];
  proxy.$modal.confirm('是否确认删除数据类型编号为"' + _ids + '"的数据项？').then(function () {
    if (deletingVersion !== versionId.value) return;
    return delDataType(_ids, deletingVersion);
  }).then(() => {
    if (deletingVersion !== versionId.value) return;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

</script>

<style scoped>
.enum-selected-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  max-width: 100%;
  vertical-align: middle;
}
.enum-source-tag {
  flex-shrink: 0;
  padding: 0 5px;
  font-size: 12px;
  font-weight: 400;
  height: 18px;
  line-height: 16px;
}
.enum-load-error {
  color: #e6a23c;
  font-size: 12px;
  line-height: 20px;
  margin-top: 4px;
}
</style>
