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
 
 Business License: https://community.qontology.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->
<template>
  <div ref="pageRef" class="candidate-tab-page">
    <qt-wrap
      :columns="tableStore.columns"
      :table-ref="tableRef"
      :config="wrapConfig"
    >
      <template #actions-data>
        <el-button
          type="primary"
          plain
          :disabled="!selectedIds.length"
          @click="adoptBatch"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-queren mr5"></i>批量采纳
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="!selectedIds.length"
          @click="remove()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
        <el-button type="primary" plain @click="toggleExpandAll">
          <svg-icon :icon-class="isExpandAll ? 'toggle' : 'expand'" /><span>{{
            isExpandAll ? "折叠" : "展开"
          }}</span>
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{
          row[column_data.prop]
        }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis" :title="row.createBy || ''">{{
              row.createBy || "-"
            }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{
              creatorPhones[row.creatorId]
            }}</span>
          </div>
        </template>
        <template #adoptStatus="{ row }">
          <dict-tag :options="om_ai_adopt_status" :value="row.adoptStatus" />
        </template>
        <template #handle="{ row }">
          <el-button
            link
            type="primary"
            icon="Check"
            :disabled="row.adoptStatus === 1"
            @click="adoptOne(row)"
            >采纳</el-button
          >
          <el-button
            link
            type="primary"
            icon="Document"
            @click="showEvidence(row)"
            >来源</el-button
          >
          <el-popover
            placement="bottom"
            :width="107"
            popper-class="handle-popover"
            trigger="click"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
              link
              type="primary"
              icon="View"
              @click="showDetail(row)"
              >详情</el-button
            >
            <el-button
              link
              type="primary"
              icon="Edit"
              :disabled="row.adoptStatus === 1"
              @click="edit(row)"
              >修改</el-button
            >
            <el-button
              link
              type="danger"
              icon="Delete"
              :disabled="row.adoptStatus === 1"
              @click="remove(row)"
              >删除</el-button
            >
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <el-dialog
      v-model="open"
      title="修改对象类型"
      width="1100px"
      :append-to="pageRef"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col v-if="!editingRoot" :span="24"
            ><el-form-item label="上级对象类型"
              ><el-tree-select
                v-model="form.parentCandidateId"
                :data="parentOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                value-key="id"
                placeholder="请选择上级对象类型，留空则为顶级对象类型"
                check-strictly
                clearable
                style="width: 100%" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="对象类型名称" prop="name"
              ><div class="name-input">
                <el-input
                  v-model="form.name"
                  :maxlength="128"
                  placeholder="请输入对象类型名称"
                /><el-button type="primary" @click="termPickerOpen = true"
                  >从术语表中选择</el-button
                >
              </div></el-form-item
            ></el-col
          >
          <el-col :span="24"
            ><el-form-item label="显示顺序"
              ><el-input-number
                v-model="form.orderNum"
                :min="0"
                :precision="0"
                controls-position="right"
                style="width: 100%" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="描述"
              ><el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                :maxlength="512"
                show-word-limit
                placeholder="请输入对象类型描述" /></el-form-item
          ></el-col>
          <el-col :span="24"
            ><el-form-item label="关联建模目标" prop="targetIds"
              ><el-button type="primary" plain @click="targetPickerOpen = true"
                >导入建模目标</el-button
              ></el-form-item
            >
            <div class="target-table">
              <TargetTable
                :targets="selectedTargets"
                editable
                @remove="removeTarget"
              /></div
          ></el-col>
        </el-row>
      </el-form>
      <template #footer
        ><div class="dialog-footer">
          <el-button @click="open = false">取 消</el-button
          ><el-button type="primary" @click="save">确 定</el-button>
        </div></template
      >
    </el-dialog>
    <CandidateTargetPicker
      v-model="targetPickerOpen"
      :task-id="taskId"
      :selected-targets="selectedTargets"
      :append-to="pageRef"
      @confirm="addTargets"
    />
    <CandidateTermPicker
      v-model="termPickerOpen"
      :task-id="taskId"
      :append-to="pageRef"
      @confirm="applyTerm"
    />
    <el-dialog
      v-model="detailOpen"
      title="对象类型详情"
      width="800px"
      :append-to="pageRef"
      draggable
      ><el-form label-width="110px"
        ><el-form-item label="对象类型名称"
          ><div class="form-readonly">
            {{ detail.name || "-" }}
          </div></el-form-item
        ><el-form-item v-if="hasParent(detail)" label="上级对象类型"
          ><div class="form-readonly">
            {{ objectName(detail.parentCandidateId) }}
          </div></el-form-item
        ><el-form-item label="显示顺序"
          ><div class="form-readonly">
            {{ detail.orderNum ?? "-" }}
          </div></el-form-item
        ><el-form-item label="描述"
          ><div class="form-readonly textarea">
            {{ detail.description || "-" }}
          </div></el-form-item
        ></el-form
      ><template #footer
        ><el-button @click="detailOpen = false">关闭</el-button></template
      ></el-dialog
    >
    <CandidateEvidenceDrawer v-model="evidenceVisible" :evidences="evidences" />
  </div>
</template>
<script setup>
import { computed, getCurrentInstance, ref } from "vue";
import {
  getCandidateTargets,
  listCandidates,
  saveCandidateTargets,
  updateCandidate,
} from "@/api/om/aiModeling";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import TargetTable from "../../om/object/type/TargetTable.vue";
import CandidateTargetPicker from "../components/CandidateTargetPicker.vue";
import CandidateTermPicker from "../components/CandidateTermPicker.vue";
import CandidateEvidenceDrawer from "../components/CandidateEvidenceDrawer.vue";
import { useCandidateTab } from "./useCandidateTab";
const props = defineProps({ taskId: { type: Number, required: true } });
const { proxy } = getCurrentInstance();
const { om_ai_adopt_status } = proxy.useDict("om_ai_adopt_status");
const pageRef = ref();
const formRef = ref();
const { creatorPhones } = useCreatorPhones();
const open = ref(false);
const detailOpen = ref(false);
const targetPickerOpen = ref(false);
const termPickerOpen = ref(false);
const form = ref({});
const detail = ref({});
const selectedTargets = ref([]);
const isExpandAll = ref(true);
const editingRoot = ref(false);
// 平铺的对象候选：既用于弹窗内上级/名称回显查询，也用于构建表格树。
const objectRows = ref([]);
// 最近一次构建的树节点：整树展开/折叠时逐行调用 el-table 用。
const treeNodes = ref([]);
const required = (row) => (!row.name?.trim() ? ["对象类型名称"] : []);
const tab = useCandidateTab("object", props.taskId, required);
const {
  tableRef,
  selectedIds,
  evidenceVisible,
  evidences,
  refresh,
  select: handleSelectionChange,
  adoptOne,
  adoptBatch,
  remove,
  showEvidence,
} = tab;

/**
 * 页签嵌在宿主容器内：详情页的外层卡片已带白底与内边距，开启 full 会重复一层间距和阴影并把内容强撑高；
 * 页签内又没有搜索区，所以同时关闭操作栏的“隐藏搜索”按钮。
 */
const wrapConfig = {
  fullContent: false,
  actions: { table: { search: false } },
};

/**
 * 取全部对象候选（不分页），交由 formatData 组装成树。
 * @param {Object} params QtTable 合并排序后的查询参数
 * @returns {Promise<Object>} 候选列表接口返回体
 */
function fetchRows(params) {
  return listCandidates("object", {
    ...params,
    taskId: props.taskId,
    pageSize: -1,
  });
}

/**
 * 将平铺候选按上级编号组装为树，并缓存平铺数据供弹窗回显使用。
 * @param {Array<Object>} data 接口返回的平铺候选
 * @returns {Array<Object>} 树形数据
 */
function formatData(data) {
  objectRows.value = data || [];
  treeNodes.value = buildTree(objectRows.value);
  return treeNodes.value;
}

/**
 * 依据上级候选编号构建父子树，找不到父节点的记录作为根节点。
 * @param {Array<Object>} items 平铺候选
 * @returns {Array<Object>} 根节点集合
 */
function buildTree(items) {
  const nodes = new Map(
    items.map((item) => [String(item.id), { ...item, children: [] }])
  );
  const roots = [];
  for (const node of nodes.values()) {
    const parent = nodes.get(String(node.parentCandidateId));
    if (parent && parent !== node) parent.children.push(node);
    else roots.push(node);
  }
  return roots;
}

// 表格配置：对象类型为树形结构，关闭分页并一次性取全量后本地组树。
const tableStore = reactive({
  config: {
    sort: true,
    notPagination: true,
    table: {
      rowKey: "id",
      stripe: true,
      defaultExpandAll: true,
      treeProps: { children: "children" },
      defaultSort: { prop: "createTime", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  events: { formatData },
  columns: [
    {
      type: "selection",
      width: 55,
      minWidth: 55,
      align: "center",
      selectable: (row) => row.adoptStatus !== 1,
    },
    {
      label: "编号",
      prop: "id",
      slot: "text",
      width: 80,
      minWidth: 80,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "对象类型名称",
      prop: "name",
      slot: "text",
      width: 250,
      minWidth: 250,
      align: "left",
      fixed: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "描述",
      prop: "description",
      slot: "text",
      minWidth: 260,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "显示顺序",
      prop: "orderNum",
      slot: "text",
      width: 100,
      minWidth: 100,
      align: "center",
    },
    {
      label: "采纳状态",
      prop: "adoptStatus",
      slot: "adoptStatus",
      width: 110,
      minWidth: 110,
      align: "center",
    },
    {
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 120,
      minWidth: 120,
      align: "center",
    },
    {
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 180,
      minWidth: 180,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    },
    {
      label: "操作",
      slot: "handle",
      width: 200,
      minWidth: 200,
      align: "center",
      fixed: "right",
    },
  ],
  func: fetchRows,
});

const parentOptions = computed(() =>
  buildTree(objectRows.value.filter((item) => item.id !== form.value.id))
);
const rules = {
  name: [{ required: true, message: "对象类型名称不能为空" }],
  targetIds: [
    {
      type: "array",
      required: true,
      min: 1,
      message: "请至少关联一个建模目标",
      trigger: "change",
    },
  ],
};
const objectName = (id) =>
  objectRows.value.find((item) => item.id === id)?.name || "-";
const hasParent = (row) =>
  row.parentCandidateId != null && String(row.parentCandidateId) !== "0";

/**
 * 展开或折叠全部树节点。
 * 当前 element-plus 为 2.7.6，el-table 尚未提供 toggleAllExpansion（2.9.x 才加入），
 * 直接调用会抛 TypeError 导致整树无任何反应，这里改为遍历树逐行 toggleRowExpansion。
 */
function toggleExpandAll() {
  isExpandAll.value = !isExpandAll.value;
  const elTable = tableRef.value?.elTableRef;
  if (!elTable) return;
  const walk = (nodes) => {
    nodes.forEach((node) => {
      if (node.children?.length) {
        elTable.toggleRowExpansion(node, isExpandAll.value);
        walk(node.children);
      }
    });
  };
  walk(treeNodes.value);
}
function addTargets(targets) {
  selectedTargets.value = [...selectedTargets.value, ...targets];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}
function removeTarget(row) {
  selectedTargets.value = selectedTargets.value.filter(
    (item) => String(item.id) !== String(row.id)
  );
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
}
function applyTerm(term) {
  form.value.name = term.stdName || term.name || "";
  if (!form.value.description) form.value.description = term.description || "";
}
async function edit(row) {
  form.value = { ...row, orderNum: row.orderNum ?? 0, targetIds: [] };
  editingRoot.value =
    row.parentCandidateId == null || String(row.parentCandidateId) === "0";
  const response = await getCandidateTargets("object", row.id);
  selectedTargets.value = response.data || [];
  form.value.targetIds = selectedTargets.value.map((item) => item.id);
  open.value = true;
}
async function showDetail(row) {
  detail.value = row;
  detailOpen.value = true;
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return;
  await updateCandidate("object", {
    id: form.value.id,
    taskId: props.taskId,
    name: form.value.name,
    description: form.value.description,
    parentCandidateId: form.value.parentCandidateId,
    orderNum: form.value.orderNum,
  });
  await saveCandidateTargets("object", form.value.id, form.value.targetIds);
  proxy.$modal.msgSuccess("修改成功");
  open.value = false;
  refresh();
}
</script>

<style scoped lang="scss">
.name-input {
  display: flex;
  gap: 12px;
  width: 100%;
}

.target-table {
  margin: 0 0 10px 120px;
}

.candidate-tab-page :deep(.el-dialog) {
  max-width: calc(100vw - 32px);
}

@media (max-width: 600px) {
  .target-table {
    margin-left: 0;
  }

  .name-input {
    flex-wrap: wrap;
  }
}
</style>
