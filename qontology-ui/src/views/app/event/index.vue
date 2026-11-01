<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

 qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 You may use qOntology for commercial purposes, but you may not remove, hide,
 modify, or replace the qOntology logo, copyright notices, license notices,
 or attribution information without a separate commercial license.

 White-label use, OEM distribution, rebranding, or presenting qOntology as
 another product requires separate commercial authorization from
 Jiangsu Qiantong Technology Co., Ltd.

 Business License: https://community.qknow.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->

<template>
  <div class="app-container" ref="app-container">
    <div class="pagecont-top" v-show="showSearch">
      <el-form
          class="btn-style"
          :model="queryParams"
          ref="queryRef"
          :inline="true"
          label-width="75px"
          v-show="showSearch"
          @submit.prevent
      >
        <el-form-item label="事件名称" prop="name">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.name"
              placeholder="请输入事件名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="事件类型" prop="type">
          <el-select
              v-model="queryParams.type"
              style="width: 200px"
              placeholder="请选择事件类型"
              clearable
          >
            <el-option
                v-for="dict in kg_event_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="创建人" prop="createBy">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.createBy"
              placeholder="请输入创建人名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <!-- <el-form-item label="创建时间">
  <el-date-picker
    class="el-form-input-width"
    v-model="dateRange"
    value-format="YYYY-MM-DD"
    type="daterange"
    range-separator="-"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
  ></el-date-picker>
</el-form-item> -->

        <el-form-item>
          <el-button
              plain
              type="primary"
              @click="handleQuery"
              @mousedown="(e) => e.preventDefault()"
          >
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
            <el-button
                type="primary"
                plain
                @click="handleAdd"
                v-hasPermi="['kg:event:event:add']"
                @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                icon="Delete"
                plain
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['kg:event:event:remove']"
                @mousedown="(e) => e.preventDefault()"
            >
              删除
            </el-button>
          </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
          <right-toolbar
              v-model:showSearch="showSearch"
              @queryTable="getList"
              :columns="columns"
          ></right-toolbar>
        </div>
      </div>
      <el-table
          stripe
          v-loading="loading"
          :data="eventList"
          @selection-change="handleSelectionChange"
          :default-sort="defaultSort"
          @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column
            v-if="getColumnVisibility(0)"
            label="编号"
            align="center"
            prop="id"
        />
        <el-table-column
            v-if="getColumnVisibility(4)"
            label="事件名称"
            align="left"
            prop="name"
            width="200px"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(5)"
            label="事件描述"
            align="left"
            prop="description"
            width="250px"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(6)"
            label="事件类型"
            align="center"
            prop="type"
        >
          <template #default="scope">
            <div>
              <dict-tag :options="kg_event_type" :value="scope.row.type"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(9)"
            label="参与实体"
            align="center"
            prop="entityNameList"
            width="250"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            <div class="multiline-ellipsis">
              <el-tag
                  v-for="tag in scope.row.entityNameList"
                  :key="tag.id"
                  class="card-tag"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(7)"
            label="发生时间"
            align="center"
            prop="occurTime"
            width="180"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.occurTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(8)"
            label="发生地点"
            align="left"
            prop="occurPlace"
            width="200px"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            {{ scope.row.occurPlace || '-' }}
          </template>
        </el-table-column>
        <el-table-column
            v-if="getColumnVisibility(12)"
            label="创建人"
            align="center"
            prop="createBy"
        >
          <template #default="scope">
            {{ scope.row.createBy || '-' }}
          </template>
        </el-table-column>

        <el-table-column
            v-if="getColumnVisibility(13)"
            label="操作"
            align="center"
            class-name="small-padding fixed-width"
            fixed="right"
            width="240"
        >
          <template #default="scope">
            <el-button
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['kg:event:event:edit']"
            >修改
            </el-button
            >
            <el-button
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['kg:event:event:remove']"
            >删除
            </el-button
            >
            <el-button
                link
                type="primary"
                icon="view"
                @click="handleDetail(scope.row)"
                v-hasPermi="['kg:event:event:query']"
            >详情
            </el-button
            >
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
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </div>

    <!-- 添加或修改事件管理对话框 -->
    <el-dialog
        :title="title"
        v-model="open"
        width="1000px"
        :append-to="$refs['app-container']"
        draggable
    >
      <template #header="{ close, titleId, titleClass }">
                <span role="heading" aria-level="2" class="el-dialog__title">
                    {{ title }}
                </span>
      </template>
      <el-form ref="eventRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入事件名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择事件类型" clearable>
                <el-option
                    v-for="dict in kg_event_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="21">
            <el-form-item label="参与实体" prop="entityNameList">
              <el-input
                  v-model="form.entityNameList"
                  placeholder="请选择参与实体"
                  :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-button style="margin-left: 11px" type="primary" @click="openGraphData"
            >选择实体
            </el-button
            >
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="发生时间" prop="occurTime">
              <el-date-picker
                  clearable
                  style="width: 100%"
                  v-model="form.occurTime"
                  type="date"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="请选择发生时间"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="发生地点" prop="occurPlace">
              <el-input v-model="form.occurPlace" placeholder="请输入发生地点"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件描述" prop="description">
              <el-input
                  v-model="form.description"
                  type="textarea"
                  placeholder="请输入事件描述"
                  maxlength="1024个字符"
                  show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                  v-model="form.remark"
                  type="textarea"
                  placeholder="请输入备注"
                  maxlength="512个字符"
                  show-word-limit
              />
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

    <!-- 事件管理详情对话框 -->
    <el-dialog
        :title="title"
        v-model="openDetail"
        width="1000px"
        :append-to="$refs['app-container']"
        draggable
    >
      <template #header="{ close, titleId, titleClass }">
                <span role="heading" aria-level="2" class="el-dialog__title">
                    {{ title }}
                </span>
      </template>
      <el-form ref="eventRef" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件名称" prop="name">
              <div class="form-readonly">
                {{ form.name }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件类型" prop="type">
              <dict-tag :options="kg_event_type" :value="form.type"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="参与实体" prop="entityNameList">
              <el-tag v-for="tag in form.entityNameList" class="card-tag">
                {{ tag.name }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="发生时间" prop="occurTime">
              <div class="form-readonly">
                {{ form.occurTime }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="发生地点" prop="occurPlace">
              <div class="form-readonly">
                {{ form.occurPlace }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="事件描述" prop="description">
              <div class="form-readonly textarea">
                {{ form.description ?? '-' }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <div class="form-readonly textarea">
                {{ form.remark ?? '-' }}
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

    <!-- 实体配置对话框 -->
    <el-dialog
        :title="entityTitle"
        v-model="entityOpen"
        width="1000px"
        :append-to="$refs['app-container']"
        draggable
    >
      <template #header="{ close, titleId, titleClass }">
                <span role="heading" aria-level="2" class="el-dialog__title">
                    {{ entityTitle }}
                </span>
      </template>
      <!--实体数据-->
      <el-form
          class="btn-style"
          :model="queryParamsEntity"
          ref="queryRef"
          :inline="true"
          label-width="68px"
      >
        <el-form-item label="实体名称" prop="name">
          <el-input
              class="el-form-input-width"
              v-model="queryParamsEntity.name"
              placeholder="请输入实体名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="概念名称" prop="schema">
          <el-input
              class="el-form-input-width"
              v-model="queryParamsEntity.schema"
              placeholder="请输入概念名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <!-- <el-form-item label="实体类型" prop="entityType">
  <el-select
    v-model="queryParamsEntity.entityType"
    style="width: 200px"
    placeholder="请选择实体类型"
    clearable
  >
    <el-option
      v-for="dict in kg_enetity_type"
      :key="dict.value"
      :label="dict.label"
      :value="dict.value"
    ></el-option>
  </el-select>
</el-form-item> -->
        <el-form-item>
          <el-button
              plain
              type="primary"
              @click="handleQueryEntity"
              @mousedown="(e) => e.preventDefault()"
          >
            <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
          </el-button>
          <el-button @click="resetQueryEntity" @mousedown="(e) => e.preventDefault()">
            <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-table
          stripe
          ref="entityTableRef"
          v-loading="entityLoading"
          :data="entityList"
          reserve-selection
          row-key="id"
          @selection-change="handleEntitySelectionChange"
          @row-click="handleRowClick"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column
            label="实体名称"
            prop="name"
            align="center"
            :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
            label="概念名称"
            prop="schemaName"
            align="center"
            :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column
            label="来源"
            prop="entityType"
            align="center"
            :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            <dict-tag :options="kg_enetity_type" :value="scope.row.entityType"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template #default="scope">
                        <span>{{
                            scope.row.createTime
                                ? parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}')
                                : '-'
                          }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column label="所属任务" prop="taskld" align="center" /> -->
      </el-table>
      <pagination
          v-show="entityTotal > 0"
          :total="entityTotal"
          v-model:page="queryParamsEntity.pageNum"
          v-model:limit="queryParamsEntity.pageSize"
          @pagination="getGraphData"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="entityOpen = false">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitEntityForm"
          >确 定
          </el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Event">
import {addEvent, delEvent, getEvent, listEvent, updateEvent} from '@/api/app/event/event';
// 综合接口
import {getGraphPage} from '@/api/app/graph';

const {proxy} = getCurrentInstance();
const {kg_event_type, kg_enetity_type} = proxy.useDict('kg_event_type', 'kg_enetity_type');

const eventList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: '编号', visible: true},
  {key: 4, label: '事件名称', visible: true},
  {key: 5, label: '事件描述', visible: true},
  {key: 6, label: '事件类型', visible: true},
  {key: 9, label: '参与实体', visible: true},
  {key: 7, label: '发生时间', visible: true},
  {key: 8, label: '发生地点', visible: true},
  {key: 12, label: '创建人', visible: true},
  {key: 13, label: '操作', visible: true}
  // { key: 14, label: "创建时间", visible: true },
  // { key: 18, label: "备注", visible: true },
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find((col) => col.key === key);
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
const title = ref('');
const dateRange = ref([]);
const defaultSort = ref({prop: 'createTime', order: 'desc'});
const router = useRouter();
const route = useRoute();

const entityTitle = ref('');
const entityOpen = ref(false);
const entityLoading = ref(true);
const entityList = ref([]);
const entityTotal = ref(0);
const entityIds = ref();
const entityIdAndCodeList = ref([]);

// 定义多选数据
const entity = ref([]);
// 定义上次勾选数据==用于对比删除
const oldSelection = ref([]);
// 是否分页切换
const isAuto = ref(false);
// 当前界面table
const entityTableRef = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    graphId: null,
    modelId: null,
    name: null,
    description: null,
    type: null,
    occurTime: null,
    occurPlace: null,
    createTime: null
  },
  queryParamsEntity: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    graphId: null,
    entityType: '0',
    schema: null
    // datasourceId: null
  },
  rules: {
    workspaceId: [{required: true, message: '工作区id不能为空', trigger: 'blur'}],
    name: [{required: true, message: '事件名称不能为空', trigger: 'blur'}],
    type: [{required: true, message: '事件类型不能为空', trigger: 'change'}],
    occurTime: [{required: true, message: '发生时间不能为空', trigger: 'change'}],
    entityNameList: [{required: true, message: '参与实体不能为空', trigger: 'change'}],
    validFlag: [{required: true, message: '是否有效不能为空', trigger: 'blur'}],
    delFlag: [{required: true, message: '删除标志不能为空', trigger: 'blur'}],
    createTime: [{required: true, message: '创建时间不能为空', trigger: 'blur'}],
    updateTime: [{required: true, message: '更新时间不能为空', trigger: 'blur'}]
  }
});

const {queryParams, queryParamsEntity, form, rules} = toRefs(data);

onMounted(() => {
  getList();
});

/** 查询事件管理列表 */
function getList() {
  loading.value = true;
  listEvent(proxy.addDateRange(queryParams.value, dateRange.value)).then((response) => {
    eventList.value = response.data.rows;
    eventList.value.forEach((item) => {
      item.entityNameList =
          item.entityList != null && item.entityList.length > 0
              ? item.entityList.map((items) => ({name: items.entityName}))
              : [];
    });
    total.value = response.data.total;
    loading.value = false;
  });
}

async function openGraphData() {
  entityOpen.value = true;
  entityTitle.value = '选择实体';
  entityLoading.value = true;
  queryParamsEntity.value.pageNum = 1;
  entity.value = form.value.entityList || [];

  getGraphData();
}

//查询抽取结果
function getGraphData() {
  entityLoading.value = true;
  queryParamsEntity.value.graphId = route.params.kgId;
  getGraphPage(queryParamsEntity.value).then(async (response) => {
    entityLoading.value = false;
    entityList.value = response.data.list;
    entityTotal.value = response.data.total;
    // 在表格加载完成后，设置之前选中的实体
    // 初始化及分页切换选中逻辑
    isAuto.value = true;
    await nextTick();

    setSelectionRow(entity.value);
    isAuto.value = false;
  });
}

/**
 * 选中table的复选框
 * @param {Array} rows 选中的对象数组
 * @param {Boolean} ignoreSelectable 是否忽略可选
 */
function setSelectionRow(rows, ignoreSelectable) {
  console.log('rows', rows);
  // 选中数据
  if (rows && rows.length > 0) {
    rows.forEach((row) => {
      let data = entityList.value.filter((item) => item.id == row.entityId);
      if (data.length > 0) {
        entityTableRef.value.toggleRowSelection(data[0], undefined, ignoreSelectable);
      }
    });
  }
}

// 多选框选中数据
function handleEntitySelectionChange(selection) {
  entity.value = entity.value || [];
  const selectionList = (selection || []).map((item) => {
    const data = {
      id: item.id,
      name: item.name,
      entityId: item.id,
      entityName: item.name
    };
    return data;
  });
  if (selectionList.length > 0) {
    // 如果选中值不是空值且少选了一个值
    if (oldSelection.value.length > selectionList.length) {
      oldSelection.value.forEach((item) => {
        let index = selectionList.findIndex((ece) => ece.id == item.entityId);
        if (index == -1) {
          entity.value = entity.value.filter((ece) => item.id != ece.entityId);
        }
      });
    }
    if (entity.value.length > 0) {
      selectionList.forEach((item) => {
        let index = entity.value.findIndex((ece) => ece.entityId == item.id);
        if (index == -1) {
          const data = {
            id: item.id,
            name: item.name,
            entityId: item.id,
            entityName: item.name
          };
          entity.value.push(data);
        }
      });
    } else {
      entity.value.push(...selectionList);
    }
  } else {
    // 如果不是分页导致的
    if (!isAuto.value) {
      // 如果选中值，取消到没有选择任何值
      oldSelection.value.forEach((item) => {
        let index = selectionList.findIndex((ece) => ece.entityId == item.entityId);
        if (index == -1) {
          entity.value = entity.value.filter((ece) => item.entityId != ece.entityId);
        }
      });
    }
  }
  oldSelection.value = selectionList;
}

/** 行点击事件 */
function handleRowClick(row) {
  entity.value = entity.value || [];
  // 检查当前行是否已经在 multiple 中
  const index = entity.value.findIndex((item) => item.entityId === row.id);

  // 如果行已经被选中，移除它
  if (index > -1) {
    entity.value = entity.value.filter((item) => item.entityId !== row.id);
  } else {
    // 如果行未被选中，添加到 multiple 中
    const data = {
      id: row.id,
      name: row.name,
      entityId: row.id,
      entityName: row.name
    };
    entity.value.push(data);
  }

  // 同步更新表格的选中状态
  entityTableRef.value.toggleRowSelection(row, index === -1);
}

function submitEntityForm() {
  entityOpen.value = false;
  console.log('entity',entity.value)
  form.value.entityList = entity.value;
  form.value.entityNameList = entity.value.map((item) => item.entityName);
}

function handleQueryEntity() {
  queryParamsEntity.value.pageNum = 1;
  getGraphData();
}

function resetQueryEntity() {
  queryParamsEntity.value.pageNum = 1;
  queryParamsEntity.value.pageSize = 10;
  queryParamsEntity.value.name = null;
  queryParamsEntity.value.entityType = null;
  getGraphData();
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
    entityList: [],
    entityNameList: [],
    entityIdList: [],
    workspaceId: null,
    modelId: null,
    name: null,
    description: null,
    type: null,
    occurTime: null,
    occurPlace: null,
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
  proxy.resetForm('eventRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm('queryRef');
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 排序触发事件 */
function handleSortChange(column, prop, order) {
  queryParams.value.orderByColumn = column.prop;
  queryParams.value.isAsc = column.order;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '新增事件管理';
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getEvent(_id).then((response) => {
    form.value = response.data;
    form.value.entityIdList =
        form.value.entityList != null && form.value.entityList.length > 0
            ? form.value.entityList.map((item) => item.entityId)
            : [];
    form.value.entityNameList =
        form.value.entityList != null && form.value.entityList.length > 0
            ? form.value.entityList.map((item) => item.entityName)
            : [];

    open.value = true;
    title.value = '修改事件管理';
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getEvent(_id).then((response) => {
    form.value = response.data;
    form.value.entityNameList =
        form.value.entityList != null && form.value.entityList.length > 0
            ? form.value.entityList.map((items) => ({name: items.entityName}))
            : [];
    openDetail.value = true;
    title.value = '事件管理详情';
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['eventRef'].validate((valid) => {
    if (valid) {
      form.value.graphId = route.params.kgId;
      if (form.value.id != null) {
        updateEvent(form.value)
            .then((response) => {
              proxy.$modal.msgSuccess('修改成功');
              open.value = false;
              getList();
            })
            .catch((error) => {
            });
      } else {
        addEvent(form.value)
            .then((response) => {
              proxy.$modal.msgSuccess('新增成功');
              open.value = false;
              getList();
            })
            .catch((error) => {
            });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
      .confirm('是否确认删除事件管理编号为"' + _ids + '"的数据项？')
      .then(function () {
        return delEvent(_ids);
      })
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .catch(() => {
      });
}

function routeTo(link, row) {
  if (link !== '' && link.indexOf('http') !== -1) {
    window.location.href = link;
    return;
  }
  if (link !== '') {
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
<style scoped lang="scss">
.card-tag {
  margin: 2px;
}

.multiline-ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制为2行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
