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
          v-hasPermi="['system:messageTemplate:add']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:messageTemplate:remove']"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-a-shanchuxianxing mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #content="{ row }">
          {{ row.content || '-' }}
        </template>

        <template #handle="{ row }">
          <el-button
            v-hasPermi="['system:messageTemplate:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
            >详情</el-button
          >
          <el-button
            v-hasPermi="['system:messageTemplate:edit']"
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(row)"
            >修改</el-button
          >
          <el-button
            v-hasPermi="['system:messageTemplate:remove']"
            link
            type="danger"
            icon="Delete"
            @click="handleDelete(row)"
            >删除</el-button
          >
        </template>

        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt="" />
            <p>没有记录哦~</p>
          </div>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 新增或修改消息模板对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <el-form
        ref="messageTemplateRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="消息标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="消息类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择">
            <el-option
              v-for="dict in message_category"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息等级" prop="level">
          <el-select v-model="form.msgLevel" placeholder="请选择">
            <el-option
              v-for="dict in message_level"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息模板" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="请输入内容"
            maxlength="256"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
            maxlength="256"
            show-word-limit
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

    <!-- 消息模板详情对话框 -->
    <el-dialog
      title="消息模板详情"
      v-model="openDetail"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          消息模板详情
        </span>
      </template>
      <el-form :model="detailForm" label-width="80px" class="column-form">
        <qt-form-item label="消息标题">
          <div class="form-readonly">{{ detailForm.title || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="消息类别">
          <dict-tag
            :options="message_category"
            :value="detailForm.category"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="消息等级">
          <dict-tag
            :options="message_level"
            :value="detailForm.msgLevel"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="消息模板" class="row-full">
          <div class="form-readonly textarea">
            {{ detailForm.content || "-" }}
          </div>
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

<script setup name="MessageTemplate">
import {
  listMessageTemplate,
  getMessageTemplate,
  delMessageTemplate,
  addMessageTemplate,
  updateMessageTemplate
} from '@/api/system/system/message/messageTemplate';

const { proxy } = getCurrentInstance();
const { message_category, message_level } = proxy.useDict('message_category', 'message_level');

/**
 * 表格实例
 *
 * getList()      刷新当前页
 * resetQuery()   回第一页并重新查询
 */
const tableRef = ref(null);

const open = ref(false);
const openDetail = ref(false);
const detailForm = ref({});
const ids = ref([]);
const selectedRows = ref([]);
const single = ref(true);
const multiple = ref(true);
const title = ref('');

/**
 * 查询参数
 */
const tableParams = reactive({
  title: undefined,
  category: undefined,
  msgLevel: undefined
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: '消息标题',
      prop: 'title',
      component: {
        is: 'input',
        placeholder: '请输入消息标题',
        clearable: true
      }
    },
    {
      label: '消息类别',
      prop: 'category',
      component: {
        is: 'select',
        options: message_category,
        placeholder: '请选择',
        clearable: true
      }
    },
    {
      label: '消息等级',
      prop: 'msgLevel',
      component: {
        is: 'select',
        options: message_level,
        placeholder: '请选择',
        clearable: true
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
      rowKey: 'id',
      defaultSort: { prop: 'createTime', order: 'descending' },
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {
      type: 'selection',
      width: 55,
      minWidth: 55,
      align: 'center'
    },
    {
      label: '编号',
      prop: 'id',
      align: 'center',
      sortable: true
    },
    {
      label: '消息标题',
      prop: 'title',
      align: 'center'
    },
    {
      label: '消息模板内容',
      prop: 'content',
      align: 'center',
      width: 180,
      minWidth: 180,
      showOverflowTooltip: true,
      slot: 'content'
    },
    {
      label: '消息类别',
      prop: 'category',
      align: 'center',
      dict: 'message_category'
    },
    {
      label: '消息等级',
      prop: 'msgLevel',
      align: 'center',
      dict: 'message_level'
    },
    {
      label: '创建人',
      prop: 'createBy',
      align: 'center'
    },
    {
      label: '创建时间',
      prop: 'createTime',
      align: 'center',
      width: 180,
      minWidth: 180,
      date: true,
      sortable: true
    },
    {
      label: '备注',
      prop: 'remark',
      align: 'center',
      hide: true
    },
    {
      label: '操作',
      align: 'center',
      fixed: 'right',
      width: 240,
      minWidth: 240,
      slot: 'handle'
    }
  ],
  func: requestTableData,
  params: tableParams,
  events: {}
});

const data = reactive({
  form: {},
  rules: {
    title: [{ required: true, message: '消息标题不能为空', trigger: 'blur' }],
    content: [{ required: true, message: '消息模板内容不能为空', trigger: 'blur' }],
    category: [{ required: true, message: '消息类别不能为空', trigger: 'blur' }],
    msgLevel: [{ required: true, message: '消息等级不能为空', trigger: 'blur' }]
  }
});

const { form, rules } = toRefs(data);

/**
 * QtTable 数据请求适配器
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  const response = await listMessageTemplate(params);

  return {
    data: {
      rows: response.rows ?? response.data?.rows ?? [],
      total: response.total ?? response.data?.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，新增/修改/删除成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    title: null,
    content: null,
    category: null,
    msgLevel: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updatorId: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm('messageTemplateRef');
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '新增消息模板';
}

/**
 * 修改按钮操作
 *
 * @param {Object} [row] 行数据；无值时取多选框第一条
 */
function handleUpdate(row) {
  reset();
  const templateId = row?.id || ids.value;
  getMessageTemplate(templateId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = '修改消息模板';
  });
}

/**
 * 详情按钮操作：请求最新消息模板数据后打开只读详情弹窗
 *
 * @param {Object} row 当前行数据，至少包含 id
 */
function handleDetail(row) {
  getMessageTemplate(row.id).then((response) => {
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

/** 提交按钮 */
function submitForm() {
  proxy.$refs['messageTemplateRef'].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        updateMessageTemplate(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addMessageTemplate(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/**
 * 多选框选中数据
 *
 * @param {Array} selection 当前选中行
 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  selectedRows.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/**
 * 删除按钮操作
 *
 * @param {Object} [row] 行数据；无值时为批量删除
 */
function handleDelete(row) {
  // 单个删除：保持原逻辑不变（批量按钮点击会传 MouseEvent，用 row?.id 区分）
  if (row?.id != null) {
    const deleteIds = row.id;
    proxy.$modal
      .confirm('是否确认删除消息模板编号为"' + deleteIds + '"的数据项？')
      .then(function () {
        return delMessageTemplate(deleteIds);
      })
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .catch(() => {});
    return;
  }

  // 批量删除：消息模板无状态字段，所选数据均可删除
  const rows = selectedRows.value;
  if (!rows.length) return;
  const deletableRows = rows;
  const undeletableCount = rows.length - deletableRows.length;
  const deletableIds = deletableRows.map((item) => item.id);
  proxy.$modal
    .confirm(
      `可删除${deletableRows.length}个，不可删除${undeletableCount}个，是否删除可删除部分？`
    )
    .then(function () {
      if (!deletableIds.length) {
        proxy.$modal.msgWarning('无可删除的数据');
        return;
      }
      return delMessageTemplate(deletableIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/messageTemplate/export',
    {
      ...tableParams
    },
    `messageTemplate_${new Date().getTime()}.xlsx`
  );
}
</script>
