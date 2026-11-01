<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.

  QtTagGroup 组件说明:
  用于展示标签组，支持自动折叠和 Tooltip 显示全部内容。
  常用于展示数据源列表、标签列表等。

  使用示例:
  1. 基础用法 (字符串数组):
     <QtTagGroup :items="['标签1', '标签2', '标签3']" />

  2. 对象数组 (自动映射字段):
     字段映射: label/DATASOURCE_NAME/name, datasourceType/DATASOURCE_TYPE/type
     <QtTagGroup :items="[{ name: 'MySQL', type: 'MYSQL' }, { name: 'Oracle', type: 'ORACLE' }]" />

  3. 自定义最大显示数量:
     <QtTagGroup :items="items" :max-count="3" />

  4. 强制数据源样式:
     <QtTagGroup :items="['MySQL', 'Oracle']" is-datasource />
-->

<template>
  <div
    class="qt-tag-group"
    :class="[{ 'is-block': type !== 'none', 'is-center': center }]"
    v-if="computedItems && computedItems.length"
  >
    <el-tooltip
      v-if="overflow > 0"
      placement="top"
      effect="light"
      popper-class="qt-tag-group-popper"
    >
      <template #content>
        <div class="tooltip-content">
          <DictTag
            v-for="(item, idx) in computedItems"
            :key="'full-' + idx"
            :options="[getItemProps(item)]"
            :value="getItemProps(item).value"
          />
        </div>
      </template>
      <div
        class="tag-container"
        :class="[size, { 'is-clamped': type !== 'none' }]"
        :style="containerStyle"
        ref="containerRef"
      >
        <DictTag
          v-for="(item, idx) in displayItems"
          :key="idx"
          :options="[item]"
          :value="item.value"
          :size="size"
          class="qt-tag-item"
        />
        <el-tag
          v-if="overflow > 0"
          :size="size"
          :type="firstTagType"
          class="more"
        >
          +{{ overflow }}
        </el-tag>
      </div>
    </el-tooltip>
    <div v-else class="tag-container" :class="[size]" ref="containerRef">
      <DictTag
        v-for="(item, idx) in displayItems"
        :key="idx"
        :options="[item]"
        :value="item.value"
        :size="size"
        class="qt-tag-item"
      />
    </div>
  </div>
  <span v-else class="empty-placeholder">-</span>
</template>

<script setup name="QtTagGroup">
import {
  computed,
  ref,
  onMounted,
  onBeforeUnmount,
  watch,
  nextTick,
} from "vue";
import { getDatasourceIcon, getDatasourceTagType } from "@/utils/datasource";

const props = defineProps({
  /** 标签数据数组 (可以是字符串数组 or 对象数组) */
  items: { type: Array, default: () => [] },
  /** 数据源类型 (单个数据源时使用) */
  datasourceType: { type: String, default: "" },
  /** 标签文本 (单个数据源时使用) */
  label: { type: String, default: "" },
  /** 最大显示数量 (仅在 type 为 'none' 时生效) */
  maxCount: { type: Number, default: 2 },
  /**
   * 展示类型:
   * - 'none': 使用 maxCount 逻辑
   * - 'single': 自动展示一行
   * - 'double': 自动展示两行
   */
  type: { type: String, default: "single" },
  /** 是否强制为数据源模式 */
  isDatasource: { type: Boolean, default: false },
  /** 标签尺寸: large / default / small */
  size: { type: String, default: "default" },
  /** 是否居中显示 */
  center: { type: Boolean, default: false },
});

const computedItems = computed(() => {
  if (props.items && Array.isArray(props.items) && props.items.length > 0) {
    return props.items;
  }
  if (props.datasourceType || props.label) {
    return [
      {
        datasourceType: props.datasourceType,
        label: props.label,
        name: props.label,
      },
    ];
  }
  return [];
});

const containerRef = ref(null);
const visibleCount = ref(0);
let calculateTimer = null;

// 判断整个组是否为数据源模式
const isDatasourceGroup = computed(() => {
  if (props.isDatasource) return true;
  if (props.datasourceType) return true;
  // 如果 items 中有任何一项包含数据源特有字段，则视为数据源组
  if (computedItems.value && Array.isArray(computedItems.value)) {
    return computedItems.value.some(
      (item) =>
        typeof item === "object" &&
        item &&
        (item.datasourceType || item.DATASOURCE_TYPE)
    );
  }
  return false;
});

// 获取第一个标签的类型，用于设置加号标签的样式
const firstTagType = computed(() => {
  if (displayItems.value && displayItems.value.length > 0) {
    const firstItem = displayItems.value[0];
    return (
      firstItem.elTagType || (isDatasourceGroup.value ? "primary" : "info")
    );
  }
  return isDatasourceGroup.value ? "primary" : "info";
});

// 计算实际显示的项
const displayItems = computed(() => {
  if (!computedItems.value || !Array.isArray(computedItems.value)) {
    return [];
  }

  const items =
    props.type === "none"
      ? computedItems.value.slice(0, props.maxCount)
      : computedItems.value.slice(0, visibleCount.value);

  return items.map((item) => getItemProps(item));
});

// 计算未显示的项数量
const overflow = computed(() => {
  if (!computedItems.value || !Array.isArray(computedItems.value)) {
    return 0;
  }

  if (props.type === "none") {
    return Math.max(0, computedItems.value.length - props.maxCount);
  }
  return Math.max(0, computedItems.value.length - visibleCount.value);
});

// 容器样式，用于控制行数
const containerStyle = computed(() => {
  if (props.type === "none") return {};
  // 增加估算行高，确保不会因为微小的像素差异导致裁剪
  const rowHeight = props.size === "small" ? 32 : 44;
  const maxRows = props.type === "double" ? 2 : 1;
  return {
    maxHeight: `${rowHeight * maxRows}px`,
    overflow: "hidden",
  };
});

/**
 * 计算自动折叠下的可见数量
 */
const calculateVisibleCount = () => {
  if (props.type === "none" || !containerRef.value) return;
  if (!computedItems.value || !Array.isArray(computedItems.value)) {
    visibleCount.value = 0;
    return;
  }

  if (calculateTimer) {
    clearTimeout(calculateTimer);
  }

  calculateTimer = setTimeout(() => {
    if (!containerRef.value) return;

    visibleCount.value = computedItems.value.length;

    nextTick(() => {
      if (!containerRef.value) return;
      const container = containerRef.value;
      const tags = container.querySelectorAll(".qt-tag-item");
      if (tags.length === 0 || tags[0].offsetHeight === 0) return;

      const firstTop = tags[0].offsetTop;
      const maxRows = props.type === "double" ? 2 : 1;

      const itemHeight = tags[0].offsetHeight;
      const gap = 6;
      const lineHeight = itemHeight + gap;
      const threshold = firstTop + lineHeight * maxRows - 2;

      let count = 0;
      for (let i = 0; i < tags.length; i++) {
        if (tags[i].offsetTop + tags[i].offsetHeight <= threshold) {
          count++;
        } else {
          break;
        }
      }

      if (count < computedItems.value.length) {
        visibleCount.value = Math.max(1, count - 1);
      } else {
        visibleCount.value = count;
      }
    });
  }, 50);
};

let resizeObserver = null;

onMounted(() => {
  nextTick(() => {
    visibleCount.value = computedItems.value.length;
    if (props.type !== "none") {
      calculateVisibleCount();
      if (!resizeObserver) {
        resizeObserver = new ResizeObserver(() => {
          calculateVisibleCount();
        });
      }
      if (containerRef.value) {
        resizeObserver.observe(containerRef.value);
      }
    }
  });
});

onBeforeUnmount(() => {
  if (calculateTimer) {
    clearTimeout(calculateTimer);
    calculateTimer = null;
  }
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
});

watch(
  [() => props.items, () => props.datasourceType, () => props.label],
  () => {
    calculateVisibleCount();
  },
  { deep: true }
);

watch(
  () => props.type,
  (newVal) => {
    if (newVal !== "none") {
      calculateVisibleCount();
      if (!resizeObserver) {
        resizeObserver = new ResizeObserver(() => {
          calculateVisibleCount();
        });
      }
      if (containerRef.value) {
        resizeObserver.observe(containerRef.value);
      }
    } else if (resizeObserver) {
      resizeObserver.disconnect();
      resizeObserver = null;
    }
  }
);

/**
 * 转换项属性
 * 支持多种字段格式: DATASOURCE_NAME, label, name, DATASOURCE_TYPE, datasourceType, type 等
 */
const getItemProps = (item) => {
  if (!item) {
    return { label: "", value: "", elTagType: "primary" };
  }

  if (typeof item === "string") {
    return { label: item, value: item, elTagType: "primary" };
  }

  const datasourceType =
    item.datasourceType ||
    item.DATASOURCE_TYPE ||
    item.type ||
    item.dbType ||
    "";

  const label =
    item.label || item.DATASOURCE_NAME || item.name || datasourceType || "";

  // 有数据源类型时优先取类型配置里的颜色
  const tagTypeFromConfig = datasourceType
    ? getDatasourceTagType(datasourceType)
    : "";

  return {
    ...item,
    label,
    value: item.value || label,
    // 数据源模式下未指定颜色时，取类型配置，兜底 primary
    elTagType:
      item.elTagType ||
      item.tagType ||
      item.tag_type ||
      tagTypeFromConfig ||
      item.type ||
      (props.isDatasource || !!(item.DATASOURCE_TYPE || item.datasourceType)
        ? "primary"
        : ""),
    elTagClass: item.elTagClass || item.tagClass || item.class || "",
    datasourceType,
    icon: item.icon || (datasourceType ? getDatasourceIcon(datasourceType) : ""),
  };
};
</script>

<style lang="scss" scoped>
.qt-tag-group {
  display: inline-block;
  vertical-align: middle;
  &.is-block {
    display: block;
    width: 100%;
  }
  &.is-center {
    text-align: center;
  }
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  width: 100%;
  .qt-tag-group.is-center & {
    justify-content: center;
  }
}

.empty-placeholder {
  color: #999;
}

.more {
  cursor: pointer;
  margin-left: 2px;
}
</style>

<style lang="scss">
.qt-tag-group-popper {
  background-color: #ffffff !important;
  padding: 8px !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
  border: 1px solid #ebeef5 !important;

  .el-popper__arrow::before {
    background-color: #ffffff !important;
    border: 1px solid #ebeef5 !important;
  }

  .tooltip-content {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    max-width: 400px;
  }
}
</style>
