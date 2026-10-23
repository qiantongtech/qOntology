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
    <GuideTip tip-id="basic/dataType.list"/>
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
            <el-button type="primary" plain @click="handleAdd" v-hasPermi="['basic:dataType:datatype:add']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['basic:dataType:datatype:remove']"
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
        <el-table-column v-if="getColumnVisibility(1)" label="名称" align="left" prop="name" width="200px">
          <template #default="scope">
            {{ scope.row.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="描述" align="left" prop="description">
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="数据类型" align="center" prop="dataType" width="120px">
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
                       v-hasPermi="['basic:dataType:datatype:query']">详情
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                       v-hasPermi="['basic:dataType:datatype:edit']">修改
            </el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                       v-hasPermi="['basic:dataType:datatype:remove']">删除
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
              <el-input v-model="form.name" placeholder="请输入名称"/>
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
              <el-input v-model="form.unit" placeholder="请输入单位"/>
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
              <el-input v-model="form.format" placeholder="请输入格式，如 yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="[dataTypeConstant.ENUM].includes(form.dataType)">
            <el-form-item label="枚举类型" prop="enumType">
              <el-select v-model="form.enumType" placeholder="请输入枚举类型" filterable>
                <el-option v-for="item in enumTypeDict"
                           :key="item.enumType"
                           :label="item.enumName"
                           :value="item.enumType"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入描述" maxlength="500 个字符" show-word-limit/>
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
                {{ form.enumType }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div class="form-readonly textarea">
                {{ form.description || '-' }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建人" prop="createBy">
              <div class="form-readonly">
                {{ form.createBy }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建时间" prop="createTime">
              <div class="form-readonly">
                {{ form.createTime }}
              </div>
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

<script setup name="DataType">
import {addDataType, delDataType, getDataType, listDataType, updateDataType} from "@/api/basic/dataType/dataType";
import {useRouter} from "vue-router";
import {getEnumTypeDict} from "@/api/basic/enumeration/type.js";

const {proxy} = getCurrentInstance();
const {basic_data_type} = proxy.useDict('basic_data_type');

const dataTypeList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: "编号", visible: true},
  {key: 1, label: "名称", visible: true},
  {key: 3, label: "描述", visible: true},
  {key: 2, label: "数据类型", visible: true},
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
const router = useRouter();

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
    type: null,
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
    unit: [{required: true, message: "单位不能为空", trigger: "blur"}],
    decimalPlaces: [{required: true, message: "小数位数不能为空", trigger: "blur"}],
    format: [{required: true, message: "格式不能为空", trigger: "blur"}],
    enumType: [{required: true, message: "枚举类型不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

onMounted(() => {
  getList();
  queryEnumTypeDict();
});

/** 查询数据类型列表 */
function getList() {
  loading.value = true;
  listDataType(queryParams.value).then(response => {
    dataTypeList.value = response.data.rows;
    total.value = response.data.total;
    loading.value = false;
  });
}

function queryEnumTypeDict(){
  getEnumTypeDict().then(response => {
    enumTypeDict.value = response.data;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    type: null,
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
  reset();
  open.value = true;
  title.value = "新增数据类型";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getDataType(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改数据类型";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value
  getDataType(_id).then(response => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "数据类型详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dataTypeRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateDataType(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
        });
      } else {
        addDataType(form.value).then(response => {
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
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除数据类型编号为"' + _ids + '"的数据项？').then(function () {
    return delDataType(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

function routeTo(link, row) {
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: {
          id: row.id
        }
      });
    }
  }
}

</script>
