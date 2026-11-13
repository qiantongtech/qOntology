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
          :config="{ collapsedCount: 4 }"
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
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:dict:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
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
          type="primary"
          plain
          :disabled="multiple"
          @click="handleEnum"
        >
          <i class="iconfont-mini icon-daoru mr5"></i>下载
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleRefreshCache"
          v-hasPermi="['system:dict:remove']"
        >
          <i class="iconfont-mini icon-a-shuaxinxianxing mr5"></i>刷新缓存
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #dictType="{ row }">
          <router-link
            :to="'/system/dict-data/index/' + row.dictId"
            class="link-type"
          >
            <span>{{ row.dictType }}</span>
          </router-link>
        </template>
        <template #status="{ row }">
          <el-switch
            v-model="row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(row)"
          />
        </template>
        <template #handle="{ row }">
          <el-button link type="primary" icon="Download" @click="handleEnum(row)">
            下载
          </el-button>
          <el-button
            v-hasPermi="['system:dict:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情</el-button
          >
          <el-popover
            placement="bottom"
            :width="100"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <div class="task-action-list">
              <el-button
                  v-hasPermi="['system:dict:edit']"
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(row)"
              >修改
              </el-button>
              <el-button
                  v-hasPermi="['system:dict:remove']"
                  link
                  type="danger"
                  icon="Delete"
                  :disabled="row.status === '0'"
                  @click="handleDelete(row)"
              >删除</el-button
              >
            </div>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增或修改参数配置对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="dictRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <qt-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </qt-form-item>
        <qt-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
        </qt-form-item>
        <qt-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </qt-form-item>
        <qt-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
            maxlength="256个字符"
            show-word-limit
          ></el-input>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 字典类型详情对话框 -->
    <el-dialog
      title="字典类型详情"
      v-model="openDetail"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          字典类型详情
        </span>
      </template>
      <el-form :model="detailForm" label-width="80px" class="column-form">
        <qt-form-item label="字典名称">
          <div class="form-readonly">{{ detailForm.dictName || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="字典类型">
          <div class="form-readonly">{{ detailForm.dictType || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="状态">
          <dict-tag
            :options="sys_normal_disable"
            :value="detailForm.status"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="备注" class="row-full">
          <div class="form-readonly textarea">
            {{ detailForm.remark || "-" }}
          </div>
        </qt-form-item>
        <qt-form-item label="创建时间">
          <div class="form-readonly">
            {{ parseTime(detailForm.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDetail">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dict">
import { getCurrentInstance, reactive, ref, toRefs } from "vue";

import useDictStore from "@/store/system/dict.js";
import {
  listType,
  getType,
  delType,
  addType,
  updateType,
  refreshCache
} from "@/api/system/system/dict/type.js";

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
const openDetail = ref(false);
const ids = ref([]);
const selectedRows = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref("");
const dictTypes = ref([]);
const detailForm = ref({});

/**
 * 查询参数
 *
 * dateRange 为前端临时字段，请求前经 addDateRange 转成后端参数
 */
const tableParams = reactive({
  dictName: undefined,
  dictType: undefined,
  status: undefined,
  dateRange: []
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: "字典名称",
      prop: "dictName",
      component: { is: "input" }
    },
    {
      label: "字典类型",
      prop: "dictType",
      component: { is: "input" }
    },
    {
      label: "状态",
      prop: "status",
      component: { is: "select", options: sys_normal_disable }
    },
    {
      label: "创建时间",
      prop: "dateRange",
      component: {
        is: "date-picker",
        type: "daterange",
        valueFormat: "YYYY-MM-DD",
        rangeSeparator: "-",
        startPlaceholder: "开始日期",
        endPlaceholder: "结束日期"
      }
    }
  ]
});

/**
 * QtTable 统一配置
 */
const tableStore = reactive({
  config: {
    // 启用表头排序，首次请求默认按创建时间倒序
    sort: true,
    table: {
      stripe: true,
      rowKey: "dictId",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    { type: "selection", width: 55, minWidth: 55, align: "center" },
    { label: "编号",width: 80, minWidth: 80, prop: "dictId", align: "center", sortable: true },
    {
      label: "字典名称",
      prop: "dictName",
      align: "left",
      showOverflowTooltip: { effect: "light" }
    },
    {
      label: "字典类型",
      prop: "dictType",
      align: "left",
      slot: "dictType",
      showOverflowTooltip: { effect: "light" }
    },
    { label: "状态", prop: "status", slot: "status", width: 80, minWidth: 80, align: "center" },
    {
      label: "备注",
      prop: "remark",
      align: "center",
      hide: true,
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
    dictName: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
    dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }]
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
  // dateRange 是前端临时字段，不能原样传给后端
  const { dateRange, ...requestParams } = params;

  const response = await listType(proxy.addDateRange(requestParams, dateRange));

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
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dictRef");
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增字典类型";
}

/**
 * 多选框选中数据
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.dictId);
  selectedRows.value = selection;
  single.value = selection.length != 1;
  multiple.value = !selection.length;
  dictTypes.value = selection.map((item) => item.dictType);
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；无值时取多选框选中项
 */
function handleUpdate(row) {
  reset();
  const dictId = row.dictId || ids.value;
  getType(dictId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典类型";
  });
}

/**
 * 详情按钮操作：请求最新字典类型数据后打开只读详情弹窗
 *
 * @param {Object} row 当前行数据，至少包含 dictId
 */
function handleDetail(row) {
  getType(row.dictId).then((response) => {
    detailForm.value = response.data || {};
    openDetail.value = true;
  });
}

/**
 * 关闭详情弹窗并清空详情数据
 */
function cancelDetail() {
  openDetail.value = false;
  detailForm.value = {};
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["dictRef"].validate((valid) => {
    if (valid) {
      if (form.value.dictId != undefined) {
        updateType(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addType(form.value).then(() => {
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
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.dictId 区分）
  if (row?.dictId != null) {
    const dictIds = row.dictId;
    proxy.$modal
      .confirm("是否确认删除编号为\"" + dictIds + "\"的数据项？")
      .then(function () {
        return delType(dictIds);
      })
      .then(() => {
        getList();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {});
    return;
  }

  // 批量删除：根据状态（'0'=启用不可删除，'1'=禁用可删除）判断
  const rows = selectedRows.value;
  if (!rows.length) return;
  const deletableRows = rows.filter((item) => item.status === "1");
  const undeletableCount = rows.length - deletableRows.length;
  const deletableIds = deletableRows.map((item) => item.dictId);
  proxy.$modal
    .confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    )
    .then(function () {
      if (!deletableIds.length) {
        proxy.$modal.msgWarning("无可删除的数据");
        return;
      }
      return delType(deletableIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 字典类型状态修改
 *
 * @param {Object} row 行数据
 */
function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用";
  proxy.$modal
    .confirm("确认要\"" + text + "\"\"" + row.dictName + "\"字典类型吗?")
    .then(function () {
      return updateType(row);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === "0" ? "1" : "0";
    });
}

/**
 * 导出按钮操作，使用当前查询条件
 */
function handleExport() {
  proxy.download(
    "system/dict/type/export",
    {
      dictName: tableParams.dictName,
      dictType: tableParams.dictType,
      status: tableParams.status
    },
    `dict_${new Date().getTime()}.xlsx`
  );
}

/**
 * 刷新缓存按钮操作
 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新成功");
    useDictStore().cleanDict();
  });
}

/**
 * 生成枚举类操作
 *
 * @param {Object} [row] 行数据；无值或事件对象时取多选框选中字典类型
 */
function handleEnum(row) {
  const dtNames = row.dictType || dictTypes.value;
  if (dtNames == "") {
    proxy.$modal.msgError("请选择要生成的字典");
    return;
  }

  proxy.$download.zip("/system/dict/type/batchDictData?dictTypes=" + dtNames, "anivia.zip");
}
</script>
