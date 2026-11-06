<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.

  QtSearchBar 组件说明:
  列表页标准搜索栏，基于 el-form 封装，统一查询、重置与展开/折叠交互。
  - items: 搜索项配置数组，component.is 支持 input / select / date-picker / tree-select
  - params: 查询参数对象（通常为 QtTable 的 params）
  - tableRef: QtTable 实例引用，查询与重置时自动调用 getList / resetQuery

  使用示例:
  <qt-search-bar
    :items="searchStore.items"
    :params="tableStore.params"
    :table-ref="tableRef"
  />
-->

<template>
  <div class="qt-search-bar">
    <el-form
      ref="formRef"
      :model="props.params"
      :inline="true"
      @submit.prevent
      v-bind="props.config?.form"
    >
      <el-form-item
        :label="item.label"
        :prop="item.prop"
        v-for="(item, index) in props.items"
        :key="item.prop"
        v-bind="getFormItemProps(item)"
        v-show="index < collapsedCount || store.expand"
      >
        <!-- 输入框 -->
        <el-input
          class="search-content"
          v-if="item.component.is == 'input'"
          v-model="props.params[item.prop]"
          clearable
          :placeholder="`请输入${item.label}`"
          v-bind="item.component"
          @keyup.enter="handleQueryClick"
        />
        <!-- 下拉框 -->
        <el-select
          class="search-content"
          v-if="item.component.is == 'select'"
          v-model="props.params[item.prop]"
          clearable
          filterable
          :placeholder="`请选择${item.label}`"
          v-bind="getSelectProps(item)"
        >
          <template #prefix v-if="item.component.prefixSlot">
            <slot
              :name="item.component.prefixSlot"
              v-bind="{ value: props.params[item.prop] }"
            />
          </template>
          <el-option
            v-for="(option, index) in item.component.options"
            :key="index"
            v-bind="option"
          >
            <template #default v-if="item.component.optionSlot">
              <slot :name="item.component.optionSlot" v-bind="{ option }" />
            </template>
          </el-option>
        </el-select>

        <!-- 时间选择器 -->
        <el-date-picker
          class="search-content"
          v-if="item.component.is == 'date-picker'"
          v-model="props.params[item.prop]"
          :type="item.component.type || 'date'"
          clearable
          :placeholder="`请选择${item.label}`"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          v-bind="item.component"
        />

        <!-- 树形选择框 -->
        <el-tree-select
          class="search-content"
          v-if="item.component.is == 'tree-select'"
          v-model="props.params[item.prop]"
          clearable
          filterable
          :placeholder="`请选择${item.label}`"
          v-bind="item.component"
          :ref="(el) => setTreeSelectRef(el, item.prop)"
          @node-click="() => handleNodeClick(item.prop, item.component)"
        />
      </el-form-item>
      <el-form-item class="search-btns">
        <el-button plain type="primary" @click="handleQueryClick">
          <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
        </el-button>
        <el-button @click="handleResetClick">
          <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
        </el-button>
        <el-button
          plain
          type="primary"
          v-if="store.length > collapsedCount"
          @click="store.expand = !store.expand"
          class="extend-btn"
        >
          <svg-icon v-if="store.expand" icon-class="toggle" />
          <svg-icon v-else icon-class="expand" />
          <span>{{ store.expand ? "折叠" : "展开" }}</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup name="QtSearchBar">
import { computed, reactive, ref } from "vue";

const props = defineProps({
  params: {
    type: Object,
    required: true,
  },
  config: {
    type: Object,
    default: () => {
      return {};
    },
  },
  items: {
    type: Array,
    required: true,
  },
  tableRef: {
    type: Object,
    default: () => {
      return {};
    },
  },
});

const emits = defineEmits(["query", "reset"]);

const formRef = ref(null);
const treeSelectRefs = ref({});

// 折叠时每行展示的搜索项数量，可通过 config.collapsedCount 配置
const collapsedCount = computed(() => props.config?.collapsedCount ?? 3);

const store = reactive({
  expand: false,
  length: props.items.length,
});

function setTreeSelectRef(el, prop) {
  if (el) {
    treeSelectRefs.value[prop] = el;
  }
}

function handleNodeClick(prop, component) {
  if (!component.multiple) {
    const el = treeSelectRefs.value[prop];
    if (el && typeof el.blur === "function") {
      el.blur();
    }
  }
}

// 过滤form参数
function getFormItemProps(item) {
  const { component, ...data } = item;
  return data;
}

// 过滤下拉框的插槽配置项，避免透传为 dom 属性
function getSelectProps(item) {
  const { prefixSlot, optionSlot, ...data } = item.component;
  return data;
}

// 查询
function handleQueryClick() {
  emits("query");
  // 查询时重置分页并刷新，避免停留在上一次的页码
  if (props.tableRef?.resetQuery) {
    props.tableRef.resetQuery();
    return;
  }
  props.tableRef?.getList();
}

// 重置
function handleResetClick() {
  formRef.value.resetFields();
  emits("reset");
  props.tableRef?.resetQuery();
}
</script>

<style lang="scss" scoped>
.search-content {
  width: 210px;
}

.search-btns {
  .el-button {
    height: 30px;
    padding: 8px 11px;
    font-size: 12px;
  }
}
.extend-btn {
  .svg-icon {
    font-size: 10px;
    margin-right: 6px;
  }
}
</style>
