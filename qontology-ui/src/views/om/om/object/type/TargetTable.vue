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
  <qt-table v-bind="tableStore" ref="qtTableRef">
    <template #text="{ row, column_data }">{{
      row[column_data.prop]
    }}</template>
    <template #dash="{ row, column_data }">{{
      row[column_data.prop] || "-"
    }}</template>
    <!-- 关联元素优先用标签组展示对象类型/对象类型关系；候选行若只有合计数则回退成数字，避免出现空白。 -->
    <template #relatedElementCount="{ row }">
      <qt-tag-group
        v-if="row.relatedElementTags?.length"
        :items="row.relatedElementTags"
        type="none"
        :max-count="2"
      />
      <span v-else>{{ row.relatedElementCount ?? 0 }}</span>
    </template>
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
    <template #handle="{ row }">
      <el-button
        link
        type="danger"
        icon="Delete"
        :disabled="disabled"
        @click="emit('remove', row)"
      >
        删除
      </el-button>
    </template>
    <template #empty>
      <div class="emptyBg" :class="{ 'target-empty': editable }">
        <img
          src="@/assets/common/images/empty-state/noData.png"
          alt="暂无记录"
        />
        <p>暂无记录</p>
      </div>
    </template>
  </qt-table>
</template>
<script setup>
import { computed, reactive, ref, watch } from "vue";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { withTargetRelatedElements } from "@/utils/targetRelatedElements";
const props = defineProps({
  targets: { type: Array, default: () => [] },
  columns: { type: Array, default: () => [] },
  editable: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  versionId: { type: [String, Number], default: null },
});
const emit = defineEmits(["remove", "sort-change"]);
const qtTableRef = ref();
const { creatorPhones } = useCreatorPhones();

// 关联元素与详情页口径一致：补充对象类型/对象类型关系的标签数据。
const rows = ref([]);
let enrichToken = 0;

/**
 * 候选目标自带对象/关系分类计数时，直接同步拼成「对象类型N / 对象类型关系N」标签，
 * 与 TargetTab、导入建模目标列表口径一致，无需再按版本异步查询。
 * @param {Object} row 目标行
 * @returns {Object} 补全 relatedElementTags 后的行
 */
function withSplitTags(row) {
  if (row.relatedElementTags?.length) return row;
  if (row.objectElementCount == null && row.relationElementCount == null) {
    return row;
  }
  return {
    ...row,
    relatedElementTags: [
      `对象类型${row.objectElementCount ?? "-"}`,
      `对象类型关系${row.relationElementCount ?? "-"}`,
    ],
  };
}

watch(
  () => [props.targets, props.versionId],
  async ([targets, versionId]) => {
    const token = ++enrichToken;
    // 先用行自带的分类计数同步拼标签（候选场景），再对仍缺标签的正式目标按版本异步补全。
    const list = (targets || []).map(withSplitTags);
    rows.value = list;
    if (!list.length) return;
    const pending = list.filter((row) => !row.relatedElementTags?.length);
    if (!pending.length) return;
    // 优先使用父级版本，其次回退到目标行自带的版本；都没有时保留原有关联数量展示。
    const resolvedVersion = versionId || list[0]?.versionId;
    if (!resolvedVersion) return;
    const enriched = await withTargetRelatedElements(pending, resolvedVersion);
    if (token !== enrichToken) return;
    const enrichMap = new Map(enriched.map((row) => [String(row.id), row]));
    rows.value = list.map((row) => enrichMap.get(String(row.id)) || row);
  },
  { immediate: true }
);

/** 数据由父组件传入，QtTable 仅负责渲染，取数直接返回当前 rows。 */
function fetchTargets() {
  return Promise.resolve({ data: rows.value });
}

const visible = (prop) =>
  !props.columns.length ||
  props.columns.some((column) => column.prop === prop && column.visible);

/**
 * QtTable 排序回调：还原为 el-table 原生的 { prop, order } 结构向上抛出，
 * 由父组件（详情页面板）负责重新取数，保持对外契约不变。
 * @param {Object} params QtTable 合并排序后的查询参数
 */
function handleSortChange(params) {
  emit("sort-change", { prop: params.orderByColumn, order: params.isAsc });
}

// 列配置：按可见性与编辑态动态过滤；用计算属性裁剪而非 hide，避免 QtTable 内部列索引错位。
const columns = computed(() => {
  const { editable } = props;
  const list = [];
  if (visible("id")) {
    list.push({
      label: "编号",
      prop: "id",
      slot: "text",
      width: 60,
      minWidth: 60,
      align: "center",
      sortable: editable ? true : "custom",
      // 编号可能为字符串，按数值比较避免出现 9、8…2、11、10 的字典序
      sortMethod: editable ? (a, b) => Number(a.id) - Number(b.id) : undefined,
      sortOrders: ["descending", "ascending", null],
    });
  }
  if (visible("question")) {
    list.push({
      label: "目标问题",
      prop: "question",
      slot: "text",
      minWidth: editable ? 220 : 360,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    });
  }
  if (visible("questionSource")) {
    list.push({
      label: "问题来源",
      prop: "questionSource",
      slot: "dash",
      minWidth: editable ? 130 : 180,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    });
  }
  if (visible("businessScenarios")) {
    list.push({
      label: "业务场景",
      prop: "businessScenarios",
      slot: "dash",
      minWidth: editable ? 130 : 220,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    });
  }
  if (visible("relatedElementCount")) {
    list.push({
      label: "关联元素",
      prop: "relatedElementCount",
      slot: "relatedElementCount",
      minWidth: 240,
      align: "left",
      headerAlign: "left",
    });
  }
  if (visible("priority")) {
    list.push({
      label: "优先级",
      prop: "priority",
      dict: "om_target_priority",
      width: editable ? 90 : 120,
      minWidth: editable ? 90 : 120,
      align: "center",
    });
  }
  if (!editable && visible("acceptStatus")) {
    list.push({
      label: "验证状态",
      prop: "acceptStatus",
      dict: "om_accept_status",
      width: 130,
      minWidth: 130,
      align: "center",
    });
  }
  if (!editable && visible("createBy")) {
    list.push({
      label: "创建人",
      prop: "createBy",
      slot: "creator",
      width: 120,
      minWidth: 120,
      align: "left",
      headerAlign: "left",
      showOverflowTooltip: { effect: "light" },
    });
  }
  if (!editable && visible("createTime")) {
    list.push({
      label: "创建时间",
      prop: "createTime",
      date: true,
      width: 170,
      minWidth: 170,
      align: "center",
      sortable: "custom",
      sortOrders: ["descending", "ascending", null],
    });
  }
  if (editable) {
    list.push({
      label: "操作",
      slot: "handle",
      width: 100,
      minWidth: 100,
      align: "center",
      fixed: "right",
    });
  }
  return list;
});

const tableStore = reactive({
  config: {
    notPagination: true,
    sort: true,
    table: {
      rowKey: "id",
      stripe: true,
      class: {
        "target-detail-table": !props.editable,
        "target-edit-table": props.editable,
      },
      // 编辑态为本地数据，编号默认倒序；详情态沿用创建时间倒序（由父级重新取数）。
      defaultSort: props.editable
        ? { prop: "id", order: "descending" }
        : { prop: "createTime", order: "descending" },
    },
  },
  columns,
  events: { onSortChange: handleSortChange },
  func: fetchTargets,
});

// rows 由 props.targets 补充关联元素后异步刷新，变化时驱动 QtTable 重新渲染。
watch(rows, () => qtTableRef.value?.getList());
</script>
<style scoped lang="scss">
.target-detail-table :deep(.el-table__body .el-table__cell) {
  padding: 12px 0 !important;
}
.target-detail-table :deep(.cell) {
  padding-right: 16px;
  padding-left: 16px;
  line-height: 24px;
}
.target-edit-table :deep(.el-table__empty-block) {
  min-height: 190px;
}
.target-empty {
  justify-content: center;

  img {
    width: 100px;
    height: 100px;
    margin-top: 12px;
  }

  p {
    margin: 4px 0 12px;
  }
}
</style>
