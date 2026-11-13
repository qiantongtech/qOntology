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
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          @reset="handleSearchReset"
        />
      </template>

      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['system:dict:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="primary"
          plain
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:dict:edit']"
        >
          <i class="iconfont-mini icon-a-xiugaixianxing mr5"></i>修改
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:dict:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>删除
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['system:dict:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
        <el-button
          type="warning"
          plain
          icon="Close"
          @click="handleClose"
          >关闭</el-button
        >
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #dictLabel="{ row }">
          <span
            v-if="
              (row.listClass == '' || row.listClass == 'default') &&
              (row.cssClass == '' || row.cssClass == null)
            "
            >{{ row.dictLabel }}</span
          >
          <el-tag
            v-else
            :type="row.listClass == 'primary' ? '' : row.listClass"
            :class="row.cssClass"
            >{{ row.dictLabel }}</el-tag
          >
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(row)"
            v-hasPermi="['system:dict:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="danger"
            icon="Delete"
            @click="handleDelete(row)"
            v-hasPermi="['system:dict:remove']"
            >删除</el-button
          >
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增或修改参数配置对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="800px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="dataRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="字典类型">
              <el-input v-model="form.dictType" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据标签" prop="dictLabel">
              <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据键值" prop="dictValue">
              <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样式属性" prop="cssClass">
              <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="dictSort">
              <el-input-number
                class="dict-sort-input"
                v-model="form.dictSort"
                controls-position="right"
                :min="0"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="回显样式" prop="listClass">
              <el-select v-model="form.listClass">
                <el-option
                  v-for="item in listClassOptions"
                  :key="item.value"
                  :label="item.label + '(' + item.value + ')'"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                  >{{ dict.label }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入内容"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Data">
import { getCurrentInstance, reactive, ref, toRefs } from "vue";
import { useRoute } from "vue-router";

import useDictStore from "@/store/system/dict.js";
import { optionselect as getDictOptionselect, getType } from "@/api/system/system/dict/type.js";
import {
  listData,
  getData,
  delData,
  addData,
  updateData
} from "@/api/system/system/dict/data.js";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

/**
 * 表格实例
 *
 * getList()     刷新当前页
 * resetQuery()  回第一页并重新查询
 */
const tableRef = ref(null);

const open = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref("");
const defaultDictType = ref("");
const typeOptions = ref([]);
const route = useRoute();

// 数据标签回显样式
const listClassOptions = ref([
  { value: "default", label: "默认" },
  { value: "primary", label: "主要" },
  { value: "success", label: "成功" },
  { value: "info", label: "信息" },
  { value: "warning", label: "警告" },
  { value: "danger", label: "危险" }
]);

/**
 * 查询参数
 *
 * dictType 由路由携带的字典类型 ID 解析后填入，作为默认过滤条件
 */
const tableParams = reactive({
  dictType: undefined,
  dictLabel: undefined,
  status: undefined
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: "字典名称",
      prop: "dictType",
      component: { is: "select", options: typeOptions }
    },
    {
      label: "字典标签",
      prop: "dictLabel",
      component: { is: "input" }
    },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: sys_normal_disable }
    }
  ]
});

/**
 * QtTable 统一配置
 *
 * initResquest: false 关闭自动首次请求，等待解析出 dictType 后再加载列表
 */
const tableStore = reactive({
  config: {
    // 启用表头排序，首次请求默认按创建时间倒序
    sort: true,
    initResquest: false,
    table: {
      stripe: true,
      rowKey: "dictCode",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55, align: "center" },
    { label: "字典编码", prop: "dictCode", align: "center", sortable: true },
    {
      label: "字典标签",
      prop: "dictLabel",
      align: "center",
      slot: "dictLabel"
    },
    { label: "字典键值", prop: "dictValue", align: "center" },
    { label: "字典排序", prop: "dictSort", align: "center" },
    {
      label: "状态",
      prop: "status",
      align: "center",
      dict: "sys_normal_disable"
    },
    {
      label: "备注",
      prop: "remark",
      align: "center",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "创建时间",
      prop: "createTime",
      width: 180,
      minWidth: 180,
      align: "center",
      date: true,
      sortable: true
    },
    {
      label: "操作",
      fixed: "right",
      width: 240,
      minWidth: 240,
      align: "center",
      slot: "handle"
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

const data = reactive({
  form: {},
  rules: {
    dictLabel: [{ required: true, message: "数据标签不能为空", trigger: "blur" }],
    dictValue: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
    dictSort: [{ required: true, message: "数据顺序不能为空", trigger: "blur" }]
  }
});

const { form, rules } = toRefs(data);

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + 分页参数
 * @returns {Promise<{data: {rows: Array, total: number}}>}
 */
async function requestTableData(params) {
  const response = await listData(params);

  return {
    data: {
      rows: response.rows ?? [],
      total: response.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，新增/修改/删除成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 查询字典类型详细，将其字典类型作为默认过滤条件并加载列表
 *
 * @param {string} dictId 路由参数中的字典类型 ID
 */
function getTypes(dictId) {
  getType(dictId).then((response) => {
    tableParams.dictType = response.data.dictType;
    defaultDictType.value = response.data.dictType;
    getList();
  });
}

/**
 * 查询字典类型下拉列表
 */
function getTypeList() {
  getDictOptionselect().then((response) => {
    typeOptions.value = (response.data || []).map((item) => ({
      label: item.dictName,
      value: item.dictType
    }));
  });
}

/**
 * 取消按钮
 */
function cancel() {
  open.value = false;
  reset();
}

/**
 * 表单重置
 */
function reset() {
  form.value = {
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    cssClass: undefined,
    listClass: "default",
    dictSort: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dataRef");
}

/**
 * 搜索栏重置后恢复默认字典类型过滤条件
 */
function handleSearchReset() {
  tableParams.dictType = defaultDictType.value;
}

/**
 * 返回按钮操作
 */
function handleClose() {
  const obj = { path: "/system/dict" };
  proxy.$tab.closeOpenPage(obj);
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增字典数据";
  form.value.dictType = tableParams.dictType;
}

/**
 * 多选框选中数据
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.dictCode);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；无值时取多选框选中项
 */
function handleUpdate(row) {
  reset();
  const dictCode = row.dictCode || ids.value;
  getData(dictCode).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典数据";
  });
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["dataRef"].validate((valid) => {
    if (valid) {
      if (form.value.dictCode != undefined) {
        updateData(form.value).then(() => {
          useDictStore().removeDict(tableParams.dictType);
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addData(form.value).then(() => {
          useDictStore().removeDict(tableParams.dictType);
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/**
 * 删除按钮操作
 *
 * @param {Object} [row] 行数据；无值时为批量删除
 */
function handleDelete(row) {
  const dictCodes = row.dictCode || ids.value;
  proxy.$modal
    .confirm("是否确认删除字典编码为\"" + dictCodes + "\"的数据项？")
    .then(function () {
      return delData(dictCodes);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
      useDictStore().removeDict(tableParams.dictType);
    })
    .catch(() => {});
}

/**
 * 导出按钮操作，使用当前查询条件
 */
function handleExport() {
  proxy.download(
    "system/dict/data/export",
    {
      ...tableParams
    },
    `dict_data_${new Date().getTime()}.xlsx`
  );
}

getTypes(route.params && route.params.dictId);
getTypeList();
</script>

<style scoped lang="scss">
// 显示排序输入框占满表单项列宽
.dict-sort-input {
  width: 100%;
}
</style>
