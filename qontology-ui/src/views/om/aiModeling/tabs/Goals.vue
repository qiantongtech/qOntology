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
      ><el-form-item label="目标问题"
        ><el-input
          v-model="query"
          placeholder="请输入目标问题"
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
      /><el-table-column
        prop="question"
        label="目标问题"
        min-width="230"
      /><el-table-column prop="source" label="提出来源" /><el-table-column
        prop="scenario"
        label="业务场景"
      /><el-table-column label="优先级"
        ><template #default="{ row }"
          ><dict-tag
            :options="priority"
            :value="row.priority" /></template></el-table-column
      ><el-table-column prop="count" label="关联元素" /><el-table-column
        label="验证状态"
        ><template #default="{ row }"
          ><dict-tag
            :options="status"
            :value="row.status" /></template></el-table-column
      ><el-table-column label="操作" width="220" fixed="right"
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
      :title="readonly ? '建模目标详情' : '修改建模目标'"
      width="650px"
      append-to-body
      ><el-form label-width="100px"
        ><el-form-item label="目标问题"
          ><el-input
            v-model="form.question"
            :disabled="readonly" /></el-form-item
        ><el-form-item label="提出来源"
          ><el-input v-model="form.source" :disabled="readonly" /></el-form-item
        ><el-form-item label="业务场景"
          ><el-input
            v-model="form.scenario"
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
  priority = [
    { value: "high", label: "高", elTagType: "danger" },
    { value: "medium", label: "中", elTagType: "warning" },
  ],
  status = [
    { value: "pending", label: "待验证", elTagType: "warning" },
    { value: "covered", label: "已覆盖", elTagType: "success" },
  ],
  rows = ref([
    {
      question: "哪些物料缺失会影响供应订单？",
      source: "供应链文档",
      scenario: "采购",
      priority: "high",
      count: 5,
      status: "pending",
    },
    {
      question: "哪些供应商可以提供指定物料？",
      source: "业务需求",
      scenario: "供应商管理",
      priority: "medium",
      count: 3,
      status: "covered",
    },
  ]);
const filteredRows = computed(() =>
  rows.value.filter((x) => !query.value || x.question.includes(query.value))
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
  const target = row?.question || ids.value.join(",");
  proxy.$modal
    .confirm(`是否确认删除“${target}”的数据项？`)
    .then(developing)
    .catch(() => {});
}
</script>
 