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
        <el-button plain @click="readAllMsg">
          <i class="iconfont-mini icon-a-zu22378 mr5"></i>全部设为已读
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #hasRead="{ row }">
          <el-tag :type="row.hasRead ? 'success' : 'danger'">
            {{ row.hasRead ? '已读' : '未读' }}
          </el-tag>
        </template>

        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="View"
            @click="handleView(row)"
          >
            详情
          </el-button>
          <el-button
            link
            type="danger"
            icon="Delete"
            @click="deleteMsg(row.id)"
          >
            删除
          </el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 消息详情对话框 -->
    <el-dialog
      title="消息详情"
      v-model="openView"
      width="900px"
      :append-to="$refs['app-container']"
      draggable
      destroy-on-close
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          消息详情
        </span>
      </template>
      <el-form :model="viewData" label-width="100px" class="column-form">
        <qt-form-item label="消息标题">
          <div class="form-readonly">{{ viewData.title || "-" }}</div>
        </qt-form-item>
        <qt-form-item label="消息类型">
          <dict-tag
            :options="message_category"
            :value="viewData.category"
            class="con-value access-tag"
          />
        </qt-form-item>
        <qt-form-item label="消息内容" class="row-full">
          <div class="form-readonly textarea">
            {{ viewData.content || "-" }}
          </div>
        </qt-form-item>
        <qt-form-item label="是否已读">
          <el-tag :type="viewData.hasRead ? 'success' : 'danger'" class="con-value access-tag">
            {{ viewData.hasRead ? "已读" : "未读" }}
          </el-tag>
        </qt-form-item>
        <qt-form-item label="创建时间">
          <div class="form-readonly">
            {{ parseTime(viewData.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
          </div>
        </qt-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Message">
import { getCurrentInstance, reactive, ref } from 'vue';

import useUserStore from '@/store/system/user';
import {
  listMessage,
  delMessage,
  read,
  readAll,
  updateMessage
} from '@/api/system/system/message/message';

const { proxy } = getCurrentInstance();
const { message_category } = proxy.useDict('message_category');
const userStore = useUserStore();

const openView = ref(false);
const viewData = ref({});

/**
 * 表格实例
 *
 * getList()      刷新当前页
 * resetQuery()   回第一页并重新查询
 * updateRow()    更新表格中的某一行
 */
const tableRef = ref(null);

/**
 * 查询参数
 *
 * receiverId 固定为当前用户；dateRange 为日期范围控件的临时字段
 */
const tableParams = reactive({
  category: undefined,
  receiverId: userStore.userId,
  dateRange: []
});

/**
 * 查询栏配置
 */
const searchStore = reactive({
  items: [
    {
      label: '消息类型',
      prop: 'category',
      component: {
        is: 'select',
        options: message_category,
        placeholder: '消息类型',
        clearable: true
      }
    },
    {
      label: '创建时间',
      prop: 'dateRange',
      component: {
        is: 'date-picker',
        type: 'daterange',
        valueFormat: 'YYYY-MM-DD',
        rangeSeparator: '-',
        startPlaceholder: '开始日期',
        endPlaceholder: '结束日期',
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
      defaultSort: { prop: 'createTime', order: 'descending' }
    }
  },
  columns: [
    {
      label: '消息标题',
      prop: 'title',
      align: 'center'
    },
    {
      label: '消息类型',
      prop: 'category',
      align: 'center',
      dict: 'message_category'
    },
    {
      label: '消息内容',
      prop: 'content',
      align: 'center'
    },
    {
      label: '是否已读',
      prop: 'hasRead',
      align: 'center',
      slot: 'hasRead'
    },
    {
      label: '创建时间',
      prop: 'createTime',
      align: 'center',
      date: true,
      sortable: true
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

/**
 * QtTable 数据请求适配器
 *
 * 后端消息分页接口按顶层 startTime / endTime 过滤创建时间，
 * 因此 dateRange 拆分为该两个字段后再请求，不原样传给后端。
 *
 * @param {Object} params 查询条件 + pageNum + pageSize
 * @returns {Promise<{ data: { rows: Array, total: number } }>}
 */
async function requestTableData(params) {
  const { dateRange, ...requestParams } = params;

  if (dateRange && dateRange.length > 0) {
    requestParams.startTime = dateRange[0];
    requestParams.endTime = dateRange[1];
  }

  const response = await listMessage(requestParams);

  return {
    data: {
      rows: response.rows ?? response.data?.rows ?? [],
      total: response.total ?? response.data?.total ?? 0
    }
  };
}

/**
 * 刷新表格当前页，操作成功后调用
 */
function getList() {
  tableRef.value?.getList();
}

/**
 * 查看消息详情，并将该条消息标记为已读
 *
 * @param {Object} row 行数据
 */
function handleView(row) {
  const payload = { ...row, hasRead: '1', delFlag: 1 };
  updateMessage(payload);
  tableRef.value?.updateRow(row.id, { hasRead: '1' });
  openView.value = true;
  viewData.value = payload;
}

/**
 * 全部设为已读
 */
function readAllMsg() {
  ElMessageBox.confirm('确定全部设为已读吗？')
    .then(() => {
      return readAll();
    })
    .then(() => {
      getList();
      ElMessage.success('操作成功');
    })
    .catch(() => {});
}

/**
 * 删除单条消息
 *
 * @param {number|string} id 消息 ID
 */
function deleteMsg(id) {
  ElMessageBox.confirm('确定删除改条消息吗？')
    .then(() => {
      return delMessage(id);
    })
    .then(() => {
      getList();
      ElMessage.success('操作成功');
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
/* 详情弹窗复用全局 column-form、form-readonly 样式，无需本地覆盖。 */
</style>
