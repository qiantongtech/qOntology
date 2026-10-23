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
      /><el-table-column prop="name" label="属性名称" /><el-table-column
        prop="object"
        label="所属对象类型"
      /><el-table-column prop="description" label="描述" /><el-table-column
        prop="type"
        label="数据类型"
      /><el-table-column label="是否必填"
        ><template #default="{ row }"
          ><dict-tag
            :options="required"
            :value="row.required" /></template></el-table-column
      ><el-table-column prop="defaultValue" label="默认值" /><el-table-column
        prop="unit"
        label="单位"
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
  required = [
    { value: "yes", label: "是", elTagType: "success" },
    { value: "no", label: "否", elTagType: "info" },
  ],
  rows = ref([
    {
      name: "供应商名称",
      object: "供应商",
      description: "供应商的名称",
      type: "文本",
      required: "yes",
      defaultValue: "/",
      unit: "/",
    },
    {
      name: "订单金额",
      object: "采购订单",
      description: "采购订单的总金额",
      type: "小数",
      required: "yes",
      defaultValue: "0",
      unit: "元",
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
 