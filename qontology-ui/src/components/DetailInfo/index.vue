<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
   *
  Software Name: qData Master Data Management Platform (Commercial Edition)
  Software Copyright Registration No. 16069171
   *
  [RIGHTS AND LICENSE STATEMENT]
  This file contains non-public commercial source code of which Jiangsu Qiantong
  Technology Co., Ltd. lawfully possesses complete intellectual property rights.
   *
  Access and use are limited to entities or individuals who have signed a valid
  commercial license agreement, within the scope stipulated in the agreement.
  The "accessibility" of this source code is premised on lawful authorization
  and does not constitute any form of transfer of intellectual property rights
  or implied licensing.
   *
  [PROHIBITIONS]
  Unless explicitly agreed in the license agreement, the following acts in any
  form are strictly prohibited:
  1. Copying, disseminating, disclosing, selling, renting, or redistributing
  this source code;
  2. Providing the software's functionality to third parties via SaaS, PaaS,
  cloud hosting, or other means;
  3. Using this software or its derivative versions to develop products that
  compete with the Right Holder;
  4. Providing or displaying this source code or related technical information
  to unauthorized third parties;
  5. Tampering with, circumventing, or destroying copyright notices, license
  verifications, or other technical protection measures.
   *
  [LEGAL LIABILITY]
  Any unauthorized use constitutes an infringement of trade secrets and
  intellectual property rights.
   *
  The Right Holder will strictly pursue liability for breach of contract and
  infringement in accordance with the commercial agreement and laws such as
  the "Copyright Law of the People's Republic of China" and the "Anti-Unfair
  Competition Law".
   *
  ============================================================================
   *
  Copyright (c) 2026 江苏千桐科技有限公司
   *
  软件名称：qData-mdm 主数据管理平台（专业版） | 软著登字第16069171号
   *
  【权利与授权声明】
  本文件属于江苏千桐科技有限公司依法享有完全知识产权的非公开商业源代码。
  仅限已签署有效商业授权合同的单位或个人在约定范围内查阅和使用。
  源代码的“可访问性”均以合法授权为前提，不构成任何形式的知识产权转让或默示授权。
   *
  【禁止事项】
  除授权合同明确约定外，严禁任何形式的：
  1. 复制、传播、披露、出售、出租或再分发本源代码；
  2. 通过 SaaS、PaaS、云托管等方式向第三方提供本软件功能；
  3. 将本软件或其衍生版本用于开发与权利人构成竞争的产品；
  4. 向未授权第三方提供或展示本源代码或相关技术信息；
  5. 篡改、规避或破坏版权标识、授权校验及其他技术保护措施。
   *
  【法律责任】
  任何未经授权的利用行为，均构成对商业秘密及知识产权的侵害。
  权利人将依据商业合同及《中华人民共和国著作权法》《反不正当竞争法》
  等法律法规，严厉追究违约与侵权责任。
-->

<template>
  <!-- 通用详情页头部组件：标题/头部 + 栅格化信息行 -->
  <div class="pagecont-top pb15 detail-info" v-show="show">
    <div class="infotop">
      <!-- 标题/头部区域 -->
      <div class="infotop-title mb15" :class="headerClasses" v-if="header || title">
        <!-- 默认标题（未传 header 时展示） -->
        <template v-if="!header && title">
          {{ title || "-" }}
        </template>
        <!-- 固定头部：编号方块 + 名称 + 状态字典 + 右侧按钮/插槽 -->
        <template v-else>
          <div class="task-item">
            <div v-if="props.header?.showId !== false" class="task-id">
              {{
                stringify(getByPath(data, props.header?.idKey ?? "id")) || placeholder
              }}
            </div>
            <div class="task-name">
              {{ stringify(getByPath(data, props.header?.nameKey)) || "" }}
            </div>
            <dict-tag
              v-if="props.header?.statusKey && props.header?.statusOptions"
              :options="props.header?.statusOptions"
              :value="getByPath(data, props.header?.statusKey)"
            />
          </div>
          <div class="btn-style">
            <slot name="btn-right" />
            <el-button
              type="primary"
              plain
              class="fh_btn"
              @mousedown="(e) => e.preventDefault()"
              @click="handleBack"
            >
              <svg-icon :iconClass="props.header?.backIcon ?? 'fhs'" />
              {{ props.header?.backText ?? '返回' }}
            </el-button>
          </div>
          <slot name="header-right" />
        </template>
      </div>
      <slot v-if="$slots.content" name="content" :data="data" :items="displayItems" />
      <!-- 信息栅格：默认一行 3 个（span=8），可按项覆盖 -->
      <el-row v-else :gutter="merged.gutter">
        <el-col
          v-for="(item, idx) in displayItems"
          :key="idx"
          :span="item.span ?? merged.defaultSpan"
          :class="item.className"
        >
          <div class="infotop-row border-top">
            <div class="infotop-row-lable">
              {{ item.label }}
              <!-- <el-tooltip
                    v-bind="item.tip"
                    :effect="item.tip.effect || 'light'"
                    :placement="item.tip.placement || 'top'"
                    v-if="item.tip && Object.keys(item.tip).length"
                >
                    <el-icon class="tip-icon" style="margin-left: 2px;"> <InfoFilled /> </el-icon>
                    <template #content v-if="item.tip.custom">
                        <div class="tip-content" v-html="item.tip.content"></div>
                    </template>
                </el-tooltip> -->
            </div>
            <div class="infotop-row-value">
              <slot
                v-if="item.slot"
                :name="item.slot"
                :item="item"
                :data="data"
              />
              <dict-tag
                v-else-if="item.dictOptions"
                :options="item.dictOptions"
                :value="resolveValue(item)"
              />
              <ImagePreview
                v-else-if="
                  item.type === 'image' &&
                  (resolveValue(item) || item.imageFallback)
                "
                :src="getImageSrc(item)"
                :width="item.width ?? 50"
                :height="item.height ?? 50"
              />
              <span v-else-if="item.type === 'image'">
                {{ merged.placeholder }}
              </span>
              <!-- 时间展示（示例：{{ parseTime(dppEtlTaskDetail.createTime, "{y}-{m}-{d} {h}:{i}") }}） -->
              <span
                v-else-if="isTimeItem(item)"
                :class="item.ellipsisClass || 'ellipsis'"
                :title="formatTime(resolveValue(item))"
              >
                {{ formatTime(resolveValue(item)) || merged.placeholder }}
              </span>
              <!-- 格式化函数展示 -->
              <span
                v-else-if="item.formatter"
                :class="item.ellipsisClass || 'ellipsis'"
                :title="stringify(resolveFormatted(item))"
              >
                {{ stringify(resolveFormatted(item)) || merged.placeholder }}
              </span>
              <!-- 普通文本展示（支持嵌套 key） -->
              <span
                v-else
                :class="item.ellipsisClass || 'ellipsis'"
                :title="stringify(resolveValue(item))"
              >
                {{ stringify(resolveValue(item)) || merged.placeholder }}
              </span>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
  <!-- 使用示例：
    <DetailInfo
      :title="data.name"
      :data="data"
      :items="[
        { label: '编号', key: 'id' },
        { label: '状态', key: 'status', dictOptions: sys_disable },
        { label: '描述', key: 'description', span: 12, ellipsisClass: 'ellipsis-2' },
        { label: '备注', key: 'remark', span: 12, ellipsisClass: 'ellipsis' },
        { label: '创建时间', formatter: (d) => proxy.parseTime(d.createTime, '{y}-{m}-{d} {h}:{i}') },
        { label: 'API请求地址', formatter: (d) => `/services/${d.apiVersion}${d.apiUrl}` },
      ]"
      :defaultSpan="8"
      :gutter="2"
    />
  -->
</template>

<script setup name="DetailInfo">
import { computed, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
// 设计说明：
// - 通用详情页头部展示组件，系统内各模块统一使用；
// - 默认每行 3 项（span=8），通过 items[i].span 调整为 12（每行 2 项）或 24（独占一行）；
// - 支持四种展示方式：slot 插槽、字典组件、formatter 函数、普通文本；
// - 普通文本支持嵌套 key（如 'a.b.c'），为空时展示占位符；
// - 描述、备注这类字段建议设置 span=12，使其在一行展示 2 个；

const { proxy } = getCurrentInstance();

const props = defineProps({
  // 标题文本
  title: { type: String, default: "" },
  // 是否显示
  show: { type: Boolean, default: true },
  // 头部配置（可展示：编号、名称、状态、返回按钮）
  header: {
    type: Object,
    default: null, // { showId?, idKey?, nameKey, statusKey, statusOptions, backText?, backIcon?, className? }
  },
  // 行间距
  gutter: { type: Number, default: 2 },
  // 默认每项 span（不传则一行 3 个）
  defaultSpan: { type: Number, default: 8 },
  // 详情数据源对象
  data: { type: Object, default: () => ({}) },
  // 配置项列表
  items: {
    type: Array,
    default: () => [],
  },
  // 空值占位符
  placeholder: { type: String, default: "-" },
  // 时间类型默认格式
  timeFormat: { type: String, default: "{y}-{m}-{d} {h}:{i}" },
  // 展示模式：'default' 使用固定布局；'free' 使用传入 items 布局
  mode: { type: String, default: "default" },
  // 可选配置对象，便于一次性设置不常改的参数
  config: {
    type: Object,
    default: () => ({}), // { gutter, defaultSpan, placeholder, timeFormat, mode }
  },
});

const headerClass = computed(() => props.header?.className ?? "clearfixs");
const headerIdVal = computed(() =>
  stringify(getByPath(props.data, props.header?.idKey ?? "id"))
);
// 长编号取消正方形比例，防止编号徽标挤压标题；显式隐藏编号时不应用此样式。
const headerClasses = computed(() => {
  const cls = headerClass.value;
  const isLong =
    props.header?.showId !== false &&
    typeof headerIdVal.value === "string" && headerIdVal.value.length > 12;
  return isLong ? [cls, "no-square-id"] : cls;
});

// 合并配置：config 优先，其次使用单独 props，最后使用默认
const merged = computed(() => ({
  gutter: props.config.gutter ?? props.gutter,
  defaultSpan: props.config.defaultSpan ?? props.defaultSpan,
  placeholder: props.config.placeholder ?? props.placeholder,
  timeFormat: props.config.timeFormat ?? props.timeFormat,
  mode: props.config.mode ?? props.mode,
}));

// 解析嵌套 key：支持 'a.b.c' 访问
function getByPath(obj, path) {
  if (!obj || !path) return undefined;
  if (typeof path !== "string") return obj[path];
  return path.split(".").reduce((acc, k) => (acc ? acc[k] : undefined), obj);
}

// 将任意值转为字符串（避免 [object Object]）
function stringify(val) {
  if (val === null || val === undefined) return "";
  if (typeof val === "object") {
    try {
      return JSON.stringify(val);
    } catch {
      return "";
    }
  }
  return String(val);
}

// 普通/字典项取值
function resolveValue(item) {
  // key 优先，其次直接读取 item.value，最后整个 data
  if (item.key) return getByPath(props.data, item.key);
  if (typeof item.value !== "undefined") return item.value;
  return props.data;
}

// 判断是否为时间项（type='time' 或 key 以 'time' 结尾）
function isTimeItem(item) {
  return (
    item?.type === "time" ||
    (typeof item?.key === "string" && /time$/i.test(item.key))
  );
}

// 时间格式化（模板中直接调用，简单直观）
function formatTime(val) {
  return val === null || val === undefined
    ? ""
    : proxy.parseTime?.(val, merged.value.timeFormat) ?? val;
}

// formatter 格式化取值
function resolveFormatted(item) {
  try {
    const val = resolveValue(item);
    if (item.formatter) {
      return item.formatter(val, props.data, proxy);
    }
    return val;
  } catch {
    return resolveValue(item);
  }
}

/** 解析图片字段，缺少图片时使用当前配置项指定的备用地址。 */
function getImageSrc(item) {
  const val = resolveValue(item);
  return val || item.imageFallback || "";
}

// 默认模式追加通用审计信息；自由模式完整保留调用页面配置的字段和布局。
const displayItems = computed(() => {
  const items = Array.isArray(props.items) ? props.items : [];
  if (merged.value.mode === "default") {
    const firstRow = items.slice(0, 3);
    const rows = [
      ...firstRow,
      {
        label: "描述",
        key: "description",
        span: 24,
        ellipsisClass: "ellipsis",
        className: "mt2 mb2",
      },
      { label: "创建人", key: "createBy", span: 8 },
      { label: "创建时间", key: "createTime", type: "time", span: 8 },
      { label: "更新时间", key: "updateTime", type: "time", span: 8 },
      // {
      //   label: "备注",
      //   key: "remark",
      //   span: 24,
      //   ellipsisClass: "ellipsis",
      //   className: "mt2 mb2",
      // },
    ];
    return rows;
  }
  return items;
});

const emit = defineEmits(["back"]);
const router = useRouter();

/** 先通知调用方；routerBack=false 时由调用方接管导航，否则默认返回浏览器上一页。 */
function handleBack() {
  emit("back");
  if (props.header?.routerBack === false) return;
  try {
    router.back();
  } catch {
    return;
  }
}
</script>
<style scoped>
/* 补齐来源项目的详情头部样式，并限制在组件内，避免影响现有页面。 */
.detail-info.pagecont-top {
  padding-bottom: 15px;
}
.detail-info .infotop-title {
  width: 100%;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.detail-info .task-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  min-width: 0;
}
.detail-info .task-id {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  padding: 2px;
  background-color: var(--qt-primary, var(--el-color-primary));
  color: #fff;
  font-size: 14px;
  border-radius: 0;
  aspect-ratio: 1 / 1;
  min-width: 20px;
  min-height: 20px;
  flex-shrink: 0;
}
.detail-info .task-name {
  overflow-wrap: anywhere;
}
.detail-info .infotop-row-lable {
  flex-shrink: 0;
}
.detail-info .infotop-row-value {
  min-width: 0;
  overflow: hidden;
}
.detail-info .infotop-row-value .ellipsis {
  display: block;
  width: 100%;
}
.detail-info .mt2 {
  margin-top: 2px;
}
.detail-info .mb2 {
  margin-bottom: 2px;
}
.fh_btn .svg-icon {
  font-size: 12px;
  margin-right: 5px;
  vertical-align: middle;
  margin-top: -3px;
}
.fh_btn:hover .svg-icon {
  filter: brightness(0) invert(1) !important;
}

.no-square-id .task-id {
  aspect-ratio: auto !important;
}
</style>
