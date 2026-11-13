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
      <!-- 查询条件区域 -->
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
          :config="{ collapsedCount: 4 }"
        />
      </template>

      <!-- 表格上方左侧按钮区 -->
      <template #actions-data>
        <el-button
          type="primary"
          plain
          @click="handleAdd"
          v-hasPermi="['system:config:add']"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:config:remove']"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['system:config:export']"
        >
          <i class="iconfont-mini icon-daochu mr5"></i>导出
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleRefreshCache"
          v-hasPermi="['system:config:remove']"
        >
          <i class="iconfont-mini icon-a-shuaxinxianxing mr5"></i>刷新缓存
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 参数键值为空时显示占位符 -->
        <template #configValue="{ row }">
          <span>{{ row.configValue || "-" }}</span>
        </template>

        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            @click="handleUpdate(row)"
            v-hasPermi="['system:config:edit']"
          >
            <i class="iconfont-mini icon-a-xiugaixianxing"></i>修改
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row)"
            v-hasPermi="['system:config:remove']"
          >
            <i class="iconfont-mini icon-a-shanchuxianxing"></i>删除
          </el-button>
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
        ref="configRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <qt-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" />
        </qt-form-item>
        <qt-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" />
        </qt-form-item>
        <qt-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="请输入参数键值" />
        </qt-form-item>
        <qt-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio
              v-for="dict in sys_yes_no"
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
            placeholder="请输入内容"
            maxlength="256"
            show-word-limit
          />
        </qt-form-item>
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

<script setup name="Config">
import {
  listConfig,
  getConfig,
  delConfig,
  addConfig,
  updateConfig,
  refreshCache
} from "@/api/system/system/config.js";

// 当前组件实例，用于字典、弹窗提示、日期参数处理、文件下载等全局方法
const { proxy } = getCurrentInstance();

// 字典：sys_yes_no 用于系统内置标识
const { sys_yes_no } = proxy.useDict("sys_yes_no");

/**
 * 表格实例
 *
 * getList()    刷新当前页
 * resetQuery() 回第一页并重新查询
 */
const tableRef = ref(null);

/**
 * 查询参数
 *
 * QtSearchBar 负责修改条件，QtTable 负责补充分页参数
 */
const tableParams = reactive({
  configName: undefined,
  configKey: undefined,
  configType: undefined,

  // 日期范围临时字段，请求前经 addDateRange 转换
  dateRange: []
});

/**
 * 查询栏配置，prop 与 tableParams 字段一一对应
 */
const searchStore = reactive({
  items: [
    {
      label: "参数名称",
      prop: "configName",
      component: {
        is: "input"
      }
    },
    {
      label: "参数键名",
      prop: "configKey",
      component: {
        is: "input"
      }
    },
    {
      label: "系统内置",
      prop: "configType",
      component: {
        is: "select",
        options: sys_yes_no
      }
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
      rowKey: "configId",
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center"
    },
    {
      label: "编号",
      prop: "configId",
      align: "center",
      width: 80, minWidth: 80,
      sortable: true
    },
    {
      label: "参数名称",
      prop: "configName",
      align: "left",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "参数键名",
      prop: "configKey",
      align: "left",
      showOverflowTooltip: {
        effect: "light"
      }
    },
    {
      label: "参数键值",
      prop: "configValue",
      align: "left",
      showOverflowTooltip: {
        effect: "light"
      },
      slot: "configValue"
    },
    {
      label: "系统内置",
      prop: "configType",
      align: "center",
      dict: "sys_yes_no"
    },
    {
      label: "备注",
      prop: "remark",
      align: "center",
      hide: true,
      showOverflowTooltip: {
        effect: "light"
      }
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

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  // dateRange 是前端临时字段，不能直接传给后端
  const {
    dateRange,
    ...requestParams
  } = params;

  // addDateRange 会转成后端参数 params.beginTime、params.endTime
  const response = await listConfig(
    proxy.addDateRange(requestParams, dateRange)
  );

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

const open = ref(false);
const ids = ref([]);

// true 表示未选择或选择了多条，不能执行单条修改
const single = ref(true);

// true 表示没有选择数据，不能执行批量删除
const multiple = ref(true);
const title = ref("");

const data = reactive({
  form: {},
  rules: {
    configName: [{ required: true, message: "参数名称不能为空", trigger: "blur" }],
    configKey: [{ required: true, message: "参数键名不能为空", trigger: "blur" }],
    configValue: [{ required: true, message: "参数键值不能为空", trigger: "blur" }]
  }
});

const { form, rules } = toRefs(data);

/**
 * 表格多选事件
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.configId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
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
    configId: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: "Y",
    remark: undefined
  };
  proxy.resetForm("configRef");
}

/**
 * 新增按钮操作
 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增参数";
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；顶部按钮触发时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const configId = row?.configId ?? ids.value[0];
  getConfig(configId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改参数";
  });
}

/**
 * 提交按钮
 */
function submitForm() {
  proxy.$refs["configRef"].validate((valid) => {
    if (valid) {
      if (form.value.configId != undefined) {
        updateConfig(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addConfig(form.value).then((response) => {
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
  const configIds = row?.configId ?? ids.value;
  proxy.$modal
    .confirm("是否确认删除编号为\"" + configIds + "\"的数据项？")
    .then(function () {
      return delConfig(configIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/**
 * 导出按钮操作，沿用原逻辑仅携带文本查询条件，不传日期范围
 */
function handleExport() {
  const {
    dateRange,
    ...requestParams
  } = tableParams;
  proxy.download(
    "system/config/export",
    {
      ...requestParams
    },
    `config_${new Date().getTime()}.xlsx`
  );
}

/**
 * 刷新缓存按钮操作
 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新缓存成功");
  });
}
</script>
