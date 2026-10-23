<template>
  <div class="app-container" ref="appContainer">
    <GuideTip tip-id="ontology/aiModeling.list" />
    <div class="pagecont-top" v-show="showSearch">
      <el-form :inline="true" class="btn-style" @submit.prevent>
        <el-form-item label="任务名称"
          ><el-input
            v-model="query.taskName"
            placeholder="请输入任务名称"
            clearable
            class="el-form-input-width"
            @keyup.enter="handleQuery"
        /></el-form-item>
        <el-form-item
          ><el-button type="primary" plain @click="handleQuery"
            ><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询</el-button
          ><el-button @click="resetQuery"
            ><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button
          ></el-form-item
        >
      </el-form>
    </div>
    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="10" class="btn-style"
          ><el-col :span="1.5"
            ><el-button type="primary" plain @click="openDialog()"
              ><i class="iconfont-mini icon-xinzeng"></i>新增</el-button
            ></el-col
          ><el-col :span="1.5"
            ><el-button
              type="danger"
              plain
              :disabled="multiple"
              icon="Delete"
              @click="handleDelete()"
              >删除</el-button
            ></el-col
          ></el-row
        >
        <right-toolbar
          v-model:show-search="showSearch"
          :columns="columns"
          @query-table="handleQuery"
        />
      </div>
      <el-table
        stripe
        height="60vh"
        :data="filteredTasks"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
        /><el-table-column
          type="index"
          label="序号"
          width="70"
          align="center"
        />
        <el-table-column
          v-if="columns[0].visible"
          prop="name"
          label="任务名称"
          min-width="180"
          show-overflow-tooltip
        /><el-table-column
          v-if="columns[1].visible"
          prop="description"
          label="描述"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="columns[2].visible"
          label="任务执行状态"
          min-width="140"
          align="center"
          ><template #default="{ row }"
            ><dict-tag
              :options="taskStatusOptions"
              :value="row.status" /></template></el-table-column
        ><el-table-column
          v-if="columns[3].visible"
          prop="creator"
          label="创建人"
          width="120"
          align="center"
        /><el-table-column
          v-if="columns[4].visible"
          prop="createdAt"
          label="创建时间"
          width="170"
          align="center"
        />
        <el-table-column label="操作" width="230" align="center" fixed="right"
          ><template #default="{ row }"
            ><el-button link type="primary" icon="View" @click="goDetail(row)"
              >详情</el-button
            ><el-button link type="primary" @click="openDialog(row)"
              ><i class="iconfont-mini icon-a-xiugaixianxing mr5"></i
              >修改</el-button
            ><el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(row)"
              >删除</el-button
            ></template
          ></el-table-column
        >
      </el-table>
      <pagination
        :total="filteredTasks.length"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        @pagination="handleQuery"
      />
    </div>
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="800px"
      :append-to="$refs.appContainer"
      draggable
      destroy-on-close
    >
      <el-form :model="form" label-width="90px"
        ><el-row :gutter="20"
          ><el-col :span="24"
            ><el-form-item label="任务名称" required
              ><el-input
                v-model="form.name"
                placeholder="请输入任务名称"
                maxlength="100" /></el-form-item></el-col
          ><el-col :span="24"
            ><el-form-item label="任务描述" required
              ><el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="请输入任务描述" /></el-form-item></el-col
          ><el-col :span="24"
            ><el-form-item label="上传文档" required
              ><el-upload
                v-model:file-list="files"
                class="modeling-upload"
                drag
                action="#"
                :auto-upload="false"
                accept=".doc,.docx,.pdf,.txt"
                :limit="1"
                ><el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip
                  ><div class="el-upload__tip">
                    请上传大小不超过 <b>15MB</b> 格式为
                    <b>doc/docx/pdf/txt</b> 的文件
                  </div></template
                ></el-upload
              ></el-form-item
            ></el-col
          ></el-row
        ></el-form
      >
      <template #footer
        ><div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button
          ><el-button type="primary" @click="developing">{{
            editing ? "保 存" : "开始解析"
          }}</el-button>
        </div></template
      >
    </el-dialog>
  </div>
</template>
<script setup name="AiModeling">
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const { proxy } = getCurrentInstance();
const taskStatusOptions = [
  { value: "running", label: "执行中", elTagType: "warning" },
  { value: "success", label: "执行成功", elTagType: "success" },
  { value: "failed", label: "执行失败", elTagType: "danger" },
];
const tasks = ref([
  {
    id: 1,
    name: "供应链领域本体构建",
    description: "供应链领域本体构建",
    status: "success",
    creator: "吴同",
    createdAt: "2026-10-25 12:25",
  },
  {
    id: 2,
    name: "采购管理本体构建",
    description: "采购业务领域建模",
    status: "running",
    creator: "吴同",
    createdAt: "2026-10-24 12:25",
  },
  {
    id: 3,
    name: "设备管理本体构建",
    description: "设备管理领域建模",
    status: "failed",
    creator: "吴同",
    createdAt: "2026-10-23 12:25",
  },
  {
    id: 4,
    name: "客户管理本体构建",
    description: "客户业务领域建模",
    status: "success",
    creator: "吴同",
    createdAt: "2026-10-22 12:25",
  },
]);
const columns = ref([
  { key: 0, label: "任务名称", visible: true },
  { key: 1, label: "描述", visible: true },
  { key: 2, label: "任务执行状态", visible: true },
  { key: 3, label: "创建人", visible: true },
  { key: 4, label: "创建时间", visible: true },
]);
const showSearch = ref(true),
  multiple = ref(true),
  ids = ref([]),
  dialogVisible = ref(false),
  editing = ref(false),
  files = ref([]);
const query = reactive({ taskName: "", pageNum: 1, pageSize: 10 });
const form = reactive({ name: "", description: "" });
const filteredTasks = computed(() =>
  tasks.value.filter(
    (item) => !query.taskName || item.name.includes(query.taskName)
  )
);
const dialogTitle = computed(() => (editing.value ? "修改任务" : "新增任务"));
function developing() {
  proxy.$modal.msgWarning("功能开发中");
}
function handleQuery() {
  query.pageNum = 1;
  developing();
}
function resetQuery() {
  query.taskName = "";
}
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}
function openDialog(row) {
  editing.value = Boolean(row);
  form.name = row?.name || "";
  form.description = row?.description || "";
  files.value = [];
  dialogVisible.value = true;
}
function handleDelete(row) {
  const deleteIds = row?.id ? [row.id] : ids.value;
  proxy.$modal
    .confirm(`是否确认删除任务编号为"${deleteIds.join(",")}"的数据项？`)
    .then(() => developing())
    .catch(() => {});
}
function goDetail(row) {
  proxy.$router.push({
    path: `/om/${route.params.versionId}/aiModeling/detail/${row.id}`,
  });
}
</script>
<style scoped lang="scss">
.modeling-upload {
  width: 100%;
  :deep(.el-upload) {
    width: 100%;
  }
  :deep(.el-upload-dragger) {
    width: 100%;
    height: 196px;
    padding: 45px 0 20px;
  }
  :deep(.el-upload__tip b) {
    color: #f56c6c;
    font-weight: 500;
  }
}
</style>
