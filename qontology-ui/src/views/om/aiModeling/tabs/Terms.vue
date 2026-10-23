<template>
  <div>
    <div class="justify-between mb15">
      <el-row :gutter="10" class="btn-style"
        ><el-col :span="1.5"
          ><el-button
            type="primary"
            plain
            :disabled="multiple"
            @click="developing"
            >批量采纳</el-button
          ></el-col
        ><el-col :span="1.5"
          ><el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="remove()"
            >删除</el-button
          ></el-col
        ></el-row
      ><right-toolbar
        v-model:show-search="showSearch"
        :columns="columns"
        @query-table="developing"
      />
    </div>
    <el-form v-if="showSearch" inline class="btn-style"
      ><el-form-item label="标准名称"
        ><el-input
          v-model="query"
          placeholder="请输入标准名称"
          clearable /></el-form-item
      ><el-form-item
        ><el-button type="primary" plain @click="developing">查询</el-button
        ><el-button @click="query = ''">重置</el-button></el-form-item
      ></el-form
    ><el-table stripe :data="filteredList" @selection-change="select"
      ><el-table-column type="selection" width="50" /><el-table-column
        type="index"
        label="序号"
        width="70"
        align="center"
      /><el-table-column
        prop="name"
        label="标准名称"
        min-width="120"
      /><el-table-column
        prop="english"
        label="英文名称"
        min-width="120"
      /><el-table-column
        prop="alias"
        label="别名"
        min-width="120"
      /><el-table-column
        prop="abbreviation"
        label="缩写"
        min-width="100"
      /><el-table-column
        prop="source"
        label="来源"
        min-width="100"
      /><el-table-column
        prop="definition"
        label="业务定义"
        min-width="180"
        show-overflow-tooltip
      /><el-table-column label="状态" min-width="100"
        ><template #default="{ row }"
          ><dict-tag
            :options="statusOptions"
            :value="row.status" /></template></el-table-column
      ><el-table-column label="操作" width="220" fixed="right" align="center"
        ><template #default="{ row }"
          ><el-button link type="primary" icon="View" @click="open(row, true)"
            >详情</el-button
          ><el-button link type="primary" @click="open(row, false)"
            ><i class="iconfont-mini icon-a-xiugaixianxing mr5"></i
            >修改</el-button
          ><el-button link type="danger" icon="Delete" @click="remove(row)"
            >删除</el-button
          ></template
        ></el-table-column
      ></el-table
    ><pagination
      :total="filteredList.length"
      v-model:page="page"
      v-model:limit="limit"
      @pagination="developing"
    /><el-dialog
      v-model="dialog"
      :title="readonly ? '术语详情' : '修改术语'"
      width="650px"
      append-to-body
      ><el-form label-width="100px"
        ><el-form-item label="标准名称"
          ><el-input v-model="form.name" :disabled="readonly" /></el-form-item
        ><el-form-item label="英文名称"
          ><el-input
            v-model="form.english"
            :disabled="readonly" /></el-form-item
        ><el-form-item label="别名"
          ><el-input v-model="form.alias" :disabled="readonly" /></el-form-item
        ><el-form-item label="业务定义"
          ><el-input
            v-model="form.definition"
            :disabled="readonly" /></el-form-item></el-form
      ><template #footer
        ><el-button v-if="!readonly" type="primary" @click="developing"
          >保存</el-button
        ><el-button @click="dialog = false">{{
          readonly ? "关闭" : "取消"
        }}</el-button></template
      ></el-dialog
    >
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
const list = ref([
  {
    name: "供应商",
    english: "Supplier",
    alias: "供货商、厂商",
    abbreviation: "厂商",
    source: "业务词汇",
    definition: "提供货物的公司",
    status: "pending",
  },
  {
    name: "采购订单",
    english: "Purchase Order",
    alias: "采购单",
    abbreviation: "PO",
    source: "业务词汇",
    definition: "采购交易的订单",
    status: "adopted",
  },
  {
    name: "物料",
    english: "Material",
    alias: "原料",
    abbreviation: "/",
    source: "业务词汇",
    definition: "采购或生产所需的物品",
    status: "pending",
  },
]);
const statusOptions = [
  { value: "pending", label: "待采纳", elTagType: "info" },
  { value: "adopted", label: "已采纳", elTagType: "success" },
];
const columns = ref([]),
  showSearch = ref(false),
  query = ref(""),
  page = ref(1),
  limit = ref(10),
  multiple = ref(true),
  ids = ref([]),
  dialog = ref(false),
  readonly = ref(false),
  form = ref({});
const filteredList = computed(() =>
  list.value.filter((item) => !query.value || item.name.includes(query.value))
);
const developing = () => proxy.$modal.msgWarning("功能开发中");
function select(rows) {
  ids.value = rows.map((_, index) => index + 1);
  multiple.value = !rows.length;
}
function open(row, view) {
  form.value = { ...row };
  readonly.value = view;
  dialog.value = true;
}
function remove(row) {
  const target = row?.name || ids.value.join(",");
  proxy.$modal
    .confirm(`是否确认删除“${target}”的数据项？`)
    .then(developing)
    .catch(() => {});
}
</script>
 