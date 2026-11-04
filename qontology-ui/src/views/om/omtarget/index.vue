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
    <GuideTip tip-id="om/target.list"/>
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef"/>
      </template>

      <template #actions-data>
        <el-button
            v-hasPermi="['om:target:target:add']"
            type="primary"
            plain
            :disabled="saving"
            @click="handleAdd"
            @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
            v-hasPermi="['om:target:target:remove']"
            type="danger"
            plain
            :disabled="!selectedIds.length || deleting"
            @click="handleDelete()"
            @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <template #handle="{ row }">
          <el-button
              v-hasPermi="['om:target:target:query']"
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
          >详情</el-button>
          <el-popover
              v-if="auth.hasPermiOr(['om:target:target:edit', 'om:target:target:remove'])"
              placement="bottom"
              :width="107"
              popper-class="handle-popover"
              trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
                v-if="auth.hasPermi('om:target:target:edit')"
                link
                type="primary"
                icon="Edit"
                :disabled="saving"
                @click="handleUpdate(row)"
            >修改</el-button>
            <el-button
                v-if="auth.hasPermi('om:target:target:remove')"
                link
                type="danger"
                icon="Delete"
                :disabled="deleting"
                @click="handleDelete(row)"
            >删除</el-button>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改建模目标对话框 -->
    <el-dialog :title="dialogTitle" v-model="open" width="800px" :append-to="$refs['app-container']" :close-on-press-escape="!saving" draggable>
      <el-form ref="targetRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="目标问题" prop="question">
              <el-input v-model="form.question" type="textarea" placeholder="请输入目标问题"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="问题来源" prop="questionSource">
              <el-input v-model="form.questionSource" type="textarea" placeholder="请输入问题来源"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="业务场景" prop="businessScenarios">
              <el-input v-model="form.businessScenarios" type="textarea" placeholder="请输入业务场景"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="验收标准" prop="acceptCriterion">
              <el-input v-model="form.acceptCriterion" type="textarea" placeholder="请输入验收标准"
                        maxlength="200 个字符" show-word-limit/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优先级" prop="priority">
              <el-radio-group v-model="form.priority">
                <el-radio
                    v-for="dict in om_target_priority"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Target">
import {addTarget, delTarget, getTarget, listTarget, updateTarget} from "@/api/om/omtarget/target";
import {useRoute, useRouter} from 'vue-router';
import auth from "@/plugins/auth";
import {useCreatorPhones} from "@/utils/useCreatorPhones";

const {proxy} = getCurrentInstance();
const {om_accept_status, om_target_priority} = proxy.useDict('om_accept_status', 'om_target_priority');
const route = useRoute();
const router = useRouter();

const tableRef = ref();
const targetRef = ref();
const {creatorPhones} = useCreatorPhones();
const open = ref(false);
const saving = ref(false);
const deleting = ref(false);
const selectedIds = ref([]);
const dialogTitle = ref("新增建模目标");
const versionId = ref(route.params.versionId || null);

/**
 * 分页查询建模目标列表。
 * @param {Object} params QtTable 传入的查询参数
 * @returns {Promise<Object>} 列表接口返回体
 */
function fetchTargets(params) {
  if (!versionId.value) {
    return Promise.resolve({data: {rows: [], total: 0}});
  }
  return listTarget({...params, versionId: versionId.value});
}

// 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: {prop: "createTime", order: "descending"},
      onSelectionChange: handleSelectionChange
    }
  },
  columns: [
    {type: "selection", width: 55, minWidth: 55},
    {label: "编号", prop: "id", slot: "text", width: 80, minWidth: 80, align: "left", sortable: true},
    {label: "目标问题", prop: "question", slot: "text", minWidth: 200, align: "left", showOverflowTooltip: {effect: "light"}},
    {label: "问题来源", prop: "questionSource", slot: "text", minWidth: 180, align: "left", showOverflowTooltip: {effect: "light"}},
    {label: "业务场景", prop: "businessScenarios", slot: "text", minWidth: 180, align: "left", showOverflowTooltip: {effect: "light"}},
    {label: "优先级", prop: "priority", dict: "om_target_priority", width: 100, minWidth: 100},
    {label: "验证状态", prop: "acceptStatus", dict: "om_accept_status", width: 100, minWidth: 100},
    {label: "关联元素", prop: "relatedElementCount", slot: "text", width: 100, minWidth: 100},
    {label: "创建人", prop: "createBy", slot: "creator", width: 130, minWidth: 130, align: "left", headerAlign: "left"},
    {label: "创建时间", prop: "createTime", date: true, width: 160, minWidth: 160, align: "left", sortable: true},
    {label: "操作", slot: "handle", width: 170, minWidth: 170, fixed: "right"}
  ],
  func: fetchTargets,
  params: {
    question: null,
    priority: null,
    acceptStatus: null
  }
});

// 搜索项配置
const searchStore = reactive({
  items: [
    {label: "目标问题", prop: "question", component: {is: "input"}},
    {label: "优先级", prop: "priority", component: {is: "select", options: om_target_priority}},
    {label: "验证状态", prop: "acceptStatus", component: {is: "select", options: om_accept_status}}
  ]
});

// 表单默认值；新增时携带当前版本号，优先级默认中。
const emptyForm = () => ({
  id: null,
  versionId: versionId.value,
  question: null,
  questionSource: null,
  businessScenarios: null,
  priority: "3",
  acceptCriterion: null,
  acceptStatus: null,
  validFlag: null
});
const form = ref(emptyForm());
const rules = {
  question: [{required: true, message: "目标问题不能为空", trigger: "blur"}],
  questionSource: [{required: true, message: "问题来源不能为空", trigger: "blur"}],
  businessScenarios: [{required: true, message: "业务场景不能为空", trigger: "blur"}],
  acceptCriterion: [{required: true, message: "验收标准不能为空", trigger: "blur"}],
  priority: [{required: true, message: "优先级不能为空", trigger: "blur"}]
};

// 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
watch(
    () => route.params.versionId,
    async (val) => {
      versionId.value = val || null;
      open.value = false;
      handleSelectionChange([]);
      tableStore.params.question = null;
      tableStore.params.priority = null;
      tableStore.params.acceptStatus = null;
      await nextTick();
      tableRef.value?.resetQuery();
    }
);

/** 刷新当前页列表。 */
function refreshList() {
  tableRef.value?.getList();
}

/** 关闭新增/修改弹窗并重置表单。 */
function cancel() {
  if (saving.value) return;
  open.value = false;
  reset();
}

/** 重置建模目标表单字段。 */
function reset() {
  form.value = emptyForm();
  targetRef.value?.resetFields();
}

/**
 * 多选框选中数据，供顶部批量删除使用。
 * @param {Array} selection 当前选中的行数据
 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id);
}

/**
 * 详情按钮操作。
 * @param {Object} row 当前行数据
 */
function handleDetail(row) {
  router.push({
    path: `/om/${versionId.value}/target/detail`,
    query: {id: row.id}
  });
}

/** 新增按钮操作。 */
function handleAdd() {
  reset();
  dialogTitle.value = "新增建模目标";
  open.value = true;
}

/**
 * 修改按钮操作。
 * @param {Object} row 当前行数据
 */
function handleUpdate(row) {
  reset();
  getTarget(row.id).then(response => {
    form.value = response.data;
    dialogTitle.value = "修改建模目标";
    open.value = true;
  });
}

/** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
async function submitForm() {
  if (saving.value) return;
  const valid = await targetRef.value.validate().catch(() => false);
  if (!valid || saving.value || !open.value) return;
  saving.value = true;
  const isEdit = form.value.id != null;
  try {
    await (isEdit ? updateTarget(form.value) : addTarget(form.value));
    proxy.$modal.msgSuccess(isEdit ? "修改成功" : "新增成功");
    open.value = false;
    refreshList();
  } finally {
    saving.value = false;
  }
}

/**
 * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
 * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
 */
async function handleDelete(row) {
  if (deleting.value) return;
  const deleteIds = row ? [row.id] : [...selectedIds.value];
  if (!deleteIds.length) return;
  try {
    await proxy.$modal.confirm('是否确认删除建模目标编号为"' + deleteIds.join(",") + '"的数据项？');
  } catch {
    return;
  }
  if (deleting.value) return;
  deleting.value = true;
  try {
    await delTarget(deleteIds.join(","));
    proxy.$modal.msgSuccess("删除成功");
    refreshList();
  } finally {
    deleting.value = false;
  }
}
</script>
