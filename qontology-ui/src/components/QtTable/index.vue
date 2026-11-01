<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
   *
  Software Name: qData Data Middle Platform (Commercial Edition)
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
  软件名称：qData 数据中台（商业版） | 软著登字第16069171号
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

<!-- 
    qt-table 组件
    说明：基于element-plus的表格封装，集成了分页、排序、字典、图标、链接等功能
    注意：不要私自修改本组件中的代码 有问题先联系wy
-->
<template>
  <div class="qt-table" v-loading="store.loading">
    <div
      :class="[
        'qt-table--main',
        config.table?.class,
        { 'is-empty': tableData.length === 0 },
      ]"
    >
      <el-table
        ref="elTableRef"
        :data="tableData"
        v-bind="config.table"
        :default-sort="defaultTableSort"
        @sort-change="handleSortChange"
        v-if="store.showTable"
      >
        <template v-for="(column, index) in props.columns" :key="index">
          <el-table-column
            v-bind="getElColumnData(column)"
            v-if="!column.hide"
            :align="column.align || 'center'"
          >
            <template
              #header
              v-if="
                column.tip || (column.headerTooltip && column.showHeaderTooltip)
              "
            >
              <div class="tip-wrap" v-if="column.tip">
                <div class="tip-label">
                  {{ column.label }}
                </div>
                <el-tooltip
                  v-bind="column.tip"
                  :effect="column.tip.effect || 'light'"
                  :placement="column.tip.placement || 'top'"
                >
                  <template #content v-if="column.tip.custom">
                    <div class="tip-content" v-html="column.tip.content"></div>
                  </template>
                  <slot :name="column.tip.slot || 'tip'">
                    <el-icon><InfoFilled /></el-icon>
                  </slot>
                </el-tooltip>
              </div>
              <div
                class="tip-wrap"
                v-else-if="column.headerTooltip && column.showHeaderTooltip"
              >
                <div class="tip-label">
                  {{ column.label }}
                </div>
                <el-tooltip effect="light" placement="top">
                  <template #content>
                    <div v-html="column.headerTooltip"></div>
                  </template>
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <template #default="scope">
              <!-- 空数据处理 -->
              <template
                v-if="
                  column.prop &&
                  [undefined, null, ''].includes(scope.row[column.prop])
                "
              >
                {{ getFormatValue(scope.row[column.prop]) }}
              </template>

              <!-- 字典 -->
              <dict-tag
                :show-placeholder="false"
                :options="getDictOptions(column.dict)"
                v-if="column.dict"
                :value="getPropValue(column.prop, scope.row)"
              />

              <!-- 标签 -->
              <dict-tag
                v-if="column.tag && getPropValue(column.prop, scope.row)"
                v-bind="typeof column.tag === 'object' ? column.tag : {}"
                :type="
                  (typeof column.tag === 'object' ? column.tag.type : '') ||
                  'primary'
                "
                :class="typeof column.tag === 'object' ? column.tag.class : ''"
              >
                {{ getPropValue(column.prop, scope.row) }}
              </dict-tag>

              <!-- 链接 -->
              <el-link
                v-bind="column.link"
                :underline="column.link?.underline || 'never'"
                :type="column.link?.type || 'primary'"
                v-if="column.link"
                :class="{
                  'qt-table-link-ellipsis': column.showOverflowTooltip,
                }"
                @click="handleLinkClick(column, scope.row)"
              >
                {{ getPropValue(column.prop, scope.row) }}
              </el-link>

              <!-- 图标 -->
              <svg-icon
                v-bind="column.svg"
                v-if="column.svg"
                :icon-class="getPropValue(column.prop, scope.row)"
              />

              <!-- 统一处理时间 -->
              <template v-if="column.date">
                {{
                  parseTime(
                    getPropValue(column.prop, scope.row),
                    column.date === true ? "{y}-{m}-{d} {h}:{i}" : column.date
                  )
                }}
              </template>

              <!-- 多字段展示 -->
              <div
                v-if="column.list"
                :class="[
                  'flex-column',
                  'flex-column-gap',
                  column.listClass || 'fz14',
                ]"
                :style="{ alignItems: column.align || 'center' }"
              >
                <template v-for="(item, i) in column.list" :key="i">
                  <div
                    :class="['flex-center', item.class]"
                    :title="
                      item.title
                        ? getPropValue(item.title, scope.row)
                        : getPropValue(item.prop, scope.row)
                    "
                  >
                    <span v-if="item.label" class="mr5">{{ item.label }}</span>
                    <dict-tag
                      v-if="item.dict"
                      :options="getDictOptions(item.dict)"
                      :value="getPropValue(item.prop, scope.row)"
                    />
                    <span v-else class="text-ellipsis">
                      {{ getPropValue(item.prop, scope.row) || "-" }}
                    </span>
                  </div>
                </template>
              </div>
              <!-- 信息列 (图标 + 标题 + 标签 / 描述) -->
              <div v-if="column.info" class="qt-table-info">
                <div class="qt-table-info__main">
                  <template v-if="column.info.iconSlot">
                    <slot :name="column.info.iconSlot" v-bind="scope" />
                  </template>
                  <img
                    v-else-if="column.info.icon"
                    :src="getPropValue(column.info.icon, scope.row)"
                    v-show="getPropValue(column.info.icon, scope.row)"
                    class="info-icon"
                  />
                  <template v-if="column.info.title">
                    <el-link
                      v-if="column.info.click"
                      type="primary"
                      :underline="false"
                      class="info-title text-ellipsis"
                      :title="getPropValue(column.info.title, scope.row)"
                      @click="column.info.click && column.info.click(scope.row)"
                    >
                      {{ getPropValue(column.info.title, scope.row) || "-" }}
                    </el-link>
                    <span
                      v-else
                      class="info-title text-ellipsis"
                      :title="getPropValue(column.info.title, scope.row)"
                    >
                      {{ getPropValue(column.info.title, scope.row) || "-" }}
                    </span>
                  </template>
                  <template v-if="column.info.tagGroup">
                    <QtTagGroup
                      v-bind="column.info.tagGroup"
                      :items="
                        getPropValue(column.info.tagGroup.items, scope.row)
                      "
                    />
                  </template>
                  <template v-if="column.info.custom">
                    <slot :name="column.info.custom" v-bind="scope" />
                  </template>
                  <template v-else-if="column.info.tags">
                    <dict-tag
                      v-for="(tag, idx) in column.info.tags"
                      :key="idx"
                      :options="tag.options || getDictOptions(tag.dict)"
                      :value="getPropValue(tag.value, scope.row)"
                      :size="tag.size || 'small'"
                      :type="tag.type || 'primary'"
                      :class="[tag.class || '', idx > 0 ? 'ml4' : '']"
                    />
                  </template>
                  <dict-tag
                    v-else-if="column.info.tag"
                    :options="
                      column.info.tag.options ||
                      getDictOptions(column.info.tag.dict)
                    "
                    :value="getPropValue(column.info.tag.value, scope.row)"
                    :size="column.info.tag.size || 'small'"
                    :type="column.info.tag.type || 'primary'"
                    :class="column.info.tag.class || ''"
                  />
                  <dict-tag
                    v-else-if="column.info.tag"
                    type="primary"
                    size="small"
                  >
                    {{ getPropValue(column.info.tag, scope.row) }}
                  </dict-tag>
                </div>
                <div
                  v-if="column.info.desc"
                  class="info-desc text-ellipsis"
                  :title="getPropValue(column.info.desc, scope.row)"
                >
                  {{ getPropValue(column.info.desc, scope.row) || "-" }}
                </div>
              </div>

              <!-- 自定义slot -->
              <slot
                v-if="scope.$index > -1 && column.slot"
                :name="column.slot"
                v-bind="scope"
                :column_data="column"
              />
            </template>
          </el-table-column>
        </template>

        <template #empty>
          <slot name="empty">
            <div class="emptyBg">
              <img src="@/assets/common/images/empty-state/noData.png" alt="" />
              <p>暂无记录</p>
            </div>
          </slot>
        </template>
      </el-table>
    </div>
    <div
      :class="['qt-table--pagination', config.pagination?.class]"
      v-if="!config.notPagination"
    >
      <el-pagination
        v-show="store.total"
        layout="total, sizes, prev, pager, next, jumper"
        :total="store.total"
        v-model:current-page="store.params.pageNum"
        v-model:page-size="store.params.pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        v-bind="config.pagination"
        :background="config.pagination?.background || true"
        :pager-count="config.pagination?.pagerCount || store.pagerCount"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script setup name="QtTable">
import { reactive, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import SvgIcon from "@/components/SvgIcon/index.vue";
import QtTagGroup from "@/components/QtTagGroup/index.vue";
import { scrollTo } from "@/utils/scroll-to";

defineOptions({
  inheritAttrs: false,
});

/**
 * props
 * @param {columns} 表格项
 * @param {Function} 获取表格数据的方法 参数:params 需返回一个Promise
 * @param {Object} config.table 表格配置 具体请查看el-table
 * @param {Object} config.pagination 分页配置 具体请查看 el-pagination
 * @param {Bollean} config.pagination.notAutoScroll 关闭分页后指定的功能
 * @param {Bollean} config.initResquest 是否初始化请求
 * @param {Boolean} config.notPagination 不使用分页
 * @param {Boolean} config.notPaginationParams 不使用默认的分页参数
 * @param {Boolean} config.autoPagination 前端分页
 * @param {Object|Boolean} config.sort 后端排序所需的key 如果为true则使用默认值
 * @param {Object|Boolean} config.sort.prop 排序字段的key 默认为：orderByColumn
 * @param {Object|Boolean} config.sort.order 排序方式的key 默认为：isAsc
 * @param {Array} column.dict 字典数据
 * @param {Object} column.svg svg图标数据
 * @param {String} column.svg.color 图标颜色
 * @param {String} column.svg.className 图标类名
 * @param {Object} column.link 跳转参数 具体参数整合了el-link+router.push
 * @param {Object} column.slot 自定义插槽
 * @param {Object} column.date 时间参数 会自动格式化时间 也支持自定义格式化
 * @param {events} 事件回调
 * @param {Function} events.onLinkClick 点击link时触发
 * @param {Function} events.onPageSizeChange 分页-每页条数
 * @param {Function} events.onPageCurrentChange 分页-当前页
 * @param {Function} events.onSortChange 排序时触发
 * @param {Function} events.formatParams 请求前最后处理params必须有返回值
 * @param {Function} events.formatData 处理data必须有返回值
 */
const props = defineProps({
  config: {
    type: Object,
    default: () => {
      return {};
    },
  },
  columns: {
    type: Array,
    required: true,
  },
  func: {
    type: Function,
    required: true,
  },
  params: {
    type: Object,
    default: () => {
      return {};
    },
  },
  events: {
    type: Object,
    default: () => {
      return {};
    },
  },
});

const { proxy } = getCurrentInstance();
const router = useRouter();

const elTableRef = ref(null);

const DEFAULT_PAGE_PARAMS = {
  pageNum: 1,
  pageSize: 10,
};

const store = reactive({
  loading: false,
  params: {},
  total: 0,
  data: [],
  rows: [],
  defaultSort: {},
  sort: {
    prop: "orderByColumn",
    order: "isAsc",
  },
  dict: {},
  showTable: true,
  pagerCount: document.body.clientWidth < 1300 ? 4 : 7,
});

const config = computed(() => {
  // 留作后续收集合并默认配置项处理...
  return props.config || {};
});

const defaultTableSort = computed(() => {
  const { table } = config.value;
  return table?.defaultSort || store.defaultSort;
});

// 表格数据
const tableData = computed(() => {
  const { notPagination, autoPagination } = config.value;
  if (notPagination) return store.data;
  const { pageNum, pageSize } = store.params;
  if (autoPagination) {
    return store.data.slice((pageNum - 1) * pageSize, pageNum * pageSize);
  }
  return store.data;
});

// 获取数据
function getList() {
  store.loading = true;
  const { formatParams, formatData } = props.events;
  const { notPagination, autoPagination } = config.value;
  let params = Object.assign({}, store.params, props.params);
  params = formatParams ? formatParams(params) : params;
  props
    .func(params)
    .then((res) => {
      let data = Array.isArray(res.data) ? res.data : res.data.rows;
      if (!notPagination) {
        store.total = res.data.total;
      }
      if (autoPagination) {
        store.total = data.length;
      }
      store.total = store.total || 0;
      data = formatData ? formatData(data, params) : data;
      store.data = data;
      store.loading = false;
      // 列表加载完成后通知业务页面，用于恢复依赖当前行数据的状态同步任务。
      props.events?.onDataLoaded?.(store.data);
    })
    .catch(() => {
      store.loading = false;
      store.data = [];
      store.total = 0;
    });
}

// 按表格 rowKey 更新当前数据行，避免列表刷新后轮询仍只修改旧行对象。
function updateRow(rowKey, patch) {
  const key = props.config?.table?.rowKey || "id";
  const target = store.data.find(
    (item) => String(item?.[key]) === String(rowKey)
  );
  if (target) Object.assign(target, patch);
  return target;
}

// 重置数据
function resetQuery() {
  setupDefaultPageParams();
  getList();
}

// 分页change-页数
function handleSizeChange(pageSize) {
  const { onPageSizeChange } = props.events;
  const { pageNum } = store.params;
  if (pageNum * pageSize > store.total) {
    store.params.pageNum = 1;
  }
  const { pagination } = config.value;
  onPageSizeChange && onPageSizeChange({ ...store.params });
  getList();
  if (pagination?.notAutoScroll) return;
  scrollTo(0, 800);
}

// 分页change-当前页
function handleCurrentChange() {
  const { onPageCurrentChange } = props.events;
  const { pagination } = config.value;
  onPageCurrentChange && onPageCurrentChange({ ...store.params });
  getList();
  if (pagination?.notAutoScroll) return;
  scrollTo(0, 800);
}

// 排序change
function handleSortChange({ column, order, prop }) {
  const { onSortChange } = props.events;
  const index = column.getColumnIndex();
  const data = props.columns[index];
  const sort = store.sort;
  store.params[sort.prop] = data.sortableKey || prop;
  store.params[sort.order] = order;
  onSortChange &&
    onSortChange({ ...store.params, ...props.params }, { ...sort });
  getList();
}

// 过滤Column数据
function getElColumnData(column) {
  const { hide, dict, link, headerTooltip, showHeaderTooltip, ...otherData } =
    column;
  if (otherData.width && !otherData.minWidth) {
    otherData.minWidth = otherData.width;
    delete otherData.width;
  }
  return otherData;
}

// link点击事件
function handleLinkClick(column, row) {
  const { onLinkClick } = props.events;
  const { type, path, name, external, ...other } = column.link;
  onLinkClick && onLinkClick(column, row);
  if (external) return external(row);
  const params = other.params ? other.params(row) : undefined;
  const query = other.query ? other.query(row) : undefined;
  router.push({
    name,
    path,
    params,
    query,
  });
}

// 获取字典数据
function getDictOptions(key) {
  if (store.dict[key]) return store.dict[key];
  const value = proxy.useDict(key)[key];
  store.dict[key] = value;
  return value;
}

// 获取 prop 的值，支持函数
function getPropValue(prop, row) {
  return typeof prop === "function" ? prop(row) : row[prop];
}

// 重新加载
function reload() {
  store.showTable = false;
  nextTick(() => {
    store.showTable = true;
  });
}

// 设置分页参数
function setupDefaultPageParams() {
  const { notPagination, pagination } = config.value;
  if (pagination?.params) {
    const { notPaginationParams } = config.value;
    const defaultParams = notPaginationParams ? { ...DEFAULT_PAGE_PARAMS } : {};
    const params = Object.assign(
      {},
      defaultParams,
      config.value.pagination.params
    );
    for (let key in params) {
      store.params[key] = params[key];
    }
    return;
  }

  if (notPagination) return;

  for (let key in DEFAULT_PAGE_PARAMS) {
    store.params[key] = DEFAULT_PAGE_PARAMS[key];
  }
}

// 初始化排序参数
setupDefaultPageParams();

// 初始化排序参数
if (config.value.sort) {
  let sort = config.value.sort;
  if (typeof sort == "boolean") {
    sort = { ...store.sort };
  }
  store.sort = { ...sort };
  let defaultSort = defaultTableSort.value;
  if (!Object.keys(defaultSort).length) {
    defaultSort = { prop: "createTime", order: "descending" };
    store.defaultSort = { ...defaultSort };
  }
  store.params[sort.prop] = defaultSort.prop;
  store.params[sort.order] = defaultSort.order;
}

if (config.value.initResquest || config.value.initResquest === undefined) {
  getList();
}

defineExpose({
  getList,
  updateRow,
  resetQuery,
  reload,
  elTableRef,
});
</script>

<style lang="scss">
.qt-table--main.is-empty {
  .el-table--border .el-table__inner-wrapper::after,
  .el-table--border::after,
  .el-table--border::before,
  .el-table__inner-wrapper::before {
    background-color: transparent !important;
  }
}

.qt-table-link-ellipsis,
.el-link.text-ellipsis {
  max-width: 100%;
  vertical-align: middle;

  .el-link__inner {
    display: inline-block !important;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
}
</style>
