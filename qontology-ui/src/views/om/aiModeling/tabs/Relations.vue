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
      /><el-table-column prop="name" label="关系名称" /><el-table-column
        prop="description"
        label="描述"
      /><el-table-column prop="source" label="起点类型" /><el-table-column
        prop="target"
        label="终点类型"
      /><el-table-column label="是否可逆"
        ><template #default="{ row }"
          ><dict-tag
            :options="options"
            :value="row.reversible" /></template></el-table-column
      ><el-table-column prop="reverseName" label="反向名称" /><el-table-column
        label="操作"
        width="220"
        fixed="right"
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
  options = [
    { value: "yes", label: "是", elTagType: "success" },
    { value: "no", label: "否", elTagType: "info" },
  ],
  rows = ref([
    {
      name: "供应",
      description: "供应商提供物料",
      source: "供应商",
      target: "物料",
      reversible: "yes",
      reverseName: "由...供应",
    },
    {
      name: "包含",
      description: "采购订单包含物料",
      source: "采购订单",
      target: "物料",
      reversible: "yes",
      reverseName: "属于",
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
 