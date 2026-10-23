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
    <el-table stripe :data="rows" @selection-change="select"
      ><el-table-column type="selection" width="50" /><el-table-column
        type="index"
        label="序号"
        width="70"
      /><el-table-column prop="name" label="规则名称" /><el-table-column
        prop="description"
        label="描述"
        min-width="260"
      /><el-table-column prop="creator" label="创建人" /><el-table-column
        prop="createdAt"
        label="创建时间"
        width="160"
      /><el-table-column label="操作" width="220" fixed="right"
        ><template #default="{ row }"
          ><el-button link icon="View" type="primary" @click="developing"
            >详情</el-button
          ><el-button link type="primary" @click="developing"
            ><i
              class="iconfont-mini icon-a-xiugaixianxing mr5"
            />修改</el-button
          ><el-button link icon="Delete" type="danger" @click="remove(row)"
            >删除</el-button
          ></template
        ></el-table-column
      ></el-table
    ><pagination
      :total="rows.length"
      v-model:page="page"
      v-model:limit="limit"
      @pagination="developing"
    />
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
const multiple = ref(true),
  showSearch = ref(false),
  page = ref(1),
  limit = ref(10),
  columns = ref([]),
  rows = ref([
    {
      name: "供应商名称必填",
      description: "供应商必须具有名称",
      creator: "吴同",
      createdAt: "2026-10-25 12:25",
    },
    {
      name: "订单金额非负",
      description: "采购订单金额不得小于零",
      creator: "吴同",
      createdAt: "2026-10-25 12:25",
    },
  ]);
const developing = () => proxy.$modal.msgWarning("功能开发中");
const select = (v) => (multiple.value = !v.length);
const remove = (row) =>
  proxy.$modal
    .confirm(`是否确认删除“${row?.name || ""}”的数据项？`)
    .then(developing)
    .catch(() => {});
</script>
