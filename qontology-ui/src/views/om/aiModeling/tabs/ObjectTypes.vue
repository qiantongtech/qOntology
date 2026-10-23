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
      ><el-form-item label="对象类型名称"
        ><el-input
          v-model="query"
          placeholder="请输入对象类型名称"
          clearable /></el-form-item
      ><el-form-item
        ><el-button type="primary" plain @click="developing">查询</el-button
        ><el-button @click="query = ''">重置</el-button></el-form-item
      ></el-form
    ><el-table stripe :data="filteredRows" @selection-change="select"
      ><el-table-column type="selection" width="50" /><el-table-column
        type="index"
        label="序号"
        width="70"
        align="center"
      /><el-table-column prop="name" label="对象类型名称" /><el-table-column
        prop="description"
        label="描述"
        min-width="220"
      /><el-table-column prop="properties" label="属性数量" /><el-table-column
        prop="relations"
        label="关系数量"
      /><el-table-column prop="creator" label="创建人" /><el-table-column
        prop="createdAt"
        label="创建时间"
        width="160"
      /><el-table-column label="操作" width="220" fixed="right"
        ><template #default="{ row }"
          ><el-button link icon="View" type="primary" @click="open(row, true)"
            >详情</el-button
          ><el-button link type="primary" @click="open(row, false)"
            ><i
              class="iconfont-mini icon-a-xiugaixianxing mr5"
            />修改</el-button
          ><el-button link icon="Delete" type="danger" @click="remove(row)"
            >删除</el-button
          ></template
        ></el-table-column
      ></el-table
    ><pagination
      :total="filteredRows.length"
      v-model:page="page"
      v-model:limit="limit"
      @pagination="developing"
    /><el-dialog
      v-model="dialog"
      :title="readonly ? '对象类型详情' : '修改对象类型'"
      width="650px"
      append-to-body
      ><el-form label-width="100px"
        ><el-form-item label="对象类型名称"
          ><el-input v-model="form.name" :disabled="readonly" /></el-form-item
        ><el-form-item label="描述"
          ><el-input
            v-model="form.description"
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
const multiple = ref(true),
  showSearch = ref(false),
  query = ref(""),
  page = ref(1),
  limit = ref(10),
  dialog = ref(false),
  readonly = ref(false),
  form = ref({}),
  ids = ref([]),
  columns = ref([]),
  rows = ref([
    {
      name: "供应商",
      description: "向企业提供物料或服务的组织",
      properties: 8,
      relations: 3,
      creator: "吴同",
      createdAt: "2026-10-25 12:25",
    },
    {
      name: "采购订单",
      description: "记录采购交易的业务单据",
      properties: 6,
      relations: 2,
      creator: "吴同",
      createdAt: "2026-10-25 12:25",
    },
  ]);
const filteredRows = computed(() =>
  rows.value.filter((x) => !query.value || x.name.includes(query.value))
);
const developing = () => proxy.$modal.msgWarning("功能开发中");
function select(v) {
  ids.value = v.map((_, i) => i + 1);
  multiple.value = !v.length;
}
function open(row, v) {
  form.value = { ...row };
  readonly.value = v;
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
