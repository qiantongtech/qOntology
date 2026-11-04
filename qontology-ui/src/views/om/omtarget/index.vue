<!--
  Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

  This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

  qOntology is licensed under Apache License 2.0 with additional qOntology terms.
  You may use qOntology for commercial purposes, but you may not remove, hide,
  modify, or replace the qOntology logo, copyright notices, license notices,
  or attribution information without a separate commercial license.

  White-label use, OEM distribution, rebranding, or presenting qOntology as
  another product requires separate commercial authorization from
  Jiangsu Qiantong Technology Co., Ltd.

  Business License: https://community.qontology.ai/business/policy.html
  See the LICENSE file in the project root for full license information.
-->

<template>
  <div class="app-container" ref="app-container">
    <GuideTip tip-id="om/target.list" />

    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
          :items="searchStore.items"
          :params="tableStore.params"
          :table-ref="tableRef"
        />
      </template>

      <template #actions-data>
        <el-button type="primary"
                   plain
                   @click="handleAdd"
                   v-hasPermi="['om:target:target:add']"
                   @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button type="danger"
                   plain
                   :disabled="multiple"
                   @click="handleDelete()"
                   v-hasPermi="['om:target:target:remove']"
                   @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #priority="{ row }">
          <dict-tag :options="om_target_priority" :value="row.priority" />
        </template>
        <template #acceptStatus="{ row }">
          <dict-tag :options="om_accept_status" :value="row.acceptStatus" />
        </template>
        <template #relatedElementCount="{ row }">
          <qt-tag-group
            :items="row.relatedElementTags || []"
            type="none"
            :max-count="2"
          />
        </template>
        <template #action="{ row }">
          <el-button link
                     type="primary"
                     icon="View"
                     @click="routeTo('/om/'+versionId+'/target/detail', row)"
                     v-hasPermi="['om:target:target:query']"
          >详情</el-button>
          <el-button link
                     type="primary"
                     icon="Edit"
                     @click="handleUpdate(row)"
                     v-hasPermi="['om:target:target:edit']"
          >修改</el-button>
          <el-button link
                     type="danger"
                     icon="Delete"
                     @click="handleDelete(row)"
                     v-hasPermi="['om:target:target:remove']"
          >删除</el-button>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改建模目标对话框 -->
    <el-dialog v-model="open"
               width="800px"
               :append-to="$refs['app-container']"
               draggable
    >
      <template #header>
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      </template>
      <el-form ref="targetRef"
               :model="form"
               :rules="rules"
               label-width="80px"
               @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="目标问题" prop="question">
              <el-input v-model="form.question"
                        placeholder="请输入目标问题"
                        maxlength="200"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="问题来源" prop="questionSource">
              <el-input v-model="form.questionSource"
                        placeholder="请输入问题来源"
                        maxlength="200"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优先级" prop="priority">
              <el-radio-group v-model="form.priority">
                <el-radio
                  v-for="dict in om_target_priority"
                  :key="dict.value"
                  :value="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="业务场景" prop="businessScenarios">
              <el-input v-model="form.businessScenarios"
                        class="target-textarea"
                        type="textarea"
                        placeholder="请输入业务场景"
                        maxlength="200"
                        show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="验收标准" prop="acceptCriterion">
              <el-input v-model="form.acceptCriterion"
                        class="target-textarea"
                        type="textarea"
                        placeholder="请输入验收标准"
                        maxlength="200"
                        show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Target">
import {
  addTarget,
  delTarget,
  getTarget,
  listTarget,
  updateTarget,
} from "@/api/om/omtarget/target";
import { useRoute, useRouter } from "vue-router";
import { listType as listObjectTypes } from "@/api/om/object/type";
import { listRelations } from "@/api/om/object/relation";

const { proxy } = getCurrentInstance();
const { om_accept_status, om_target_priority } = proxy.useDict(
  "om_accept_status",
  "om_target_priority",
);
const route = useRoute();
const router = useRouter();

const tableRef = ref();
const versionId = ref(route.params.versionId || null);
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: "id",
      defaultSort: { prop: "id", order: "descending" },
      onSelectionChange: handleSelectionChange,
    },
  },
  columns: [
    { type: "selection", width: 55 },
    { label: "编号", prop: "id", width: 80, sortable: true },
    {
      label: "目标问题",
      prop: "question",
      minWidth: 300,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "问题来源",
      prop: "questionSource",
      width: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    {
      label: "业务场景",
      prop: "businessScenarios",
      width: 250,
      align: "left",
      showOverflowTooltip: { effect: "light" },
    },
    { label: "关联元素", slot: "relatedElementCount", minWidth: 240, align: "left" },
    { label: "优先级", prop: "priority", slot: "priority", width: 100 },
    { label: "验证状态", prop: "acceptStatus", slot: "acceptStatus", width: 100 },
    { label: "操作", slot: "action", fixed: "right", width: 240 },
  ],
  func: loadTargets,
  params: {
    versionId: versionId.value,
    question: null,
    priority: null,
    acceptStatus: null,
  },
});
const searchStore = reactive({
  items: [
    { label: "目标问题", prop: "question", component: { is: "input" } },
    {
      label: "优先级",
      prop: "priority",
      component: { is: "select", options: om_target_priority },
    },
    {
      label: "验证状态",
      prop: "acceptStatus",
      component: { is: "select", options: om_accept_status },
    },
  ],
});

const open = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref("");

const data = reactive({
  form: {},
  rules: {
    question: [{ required: true, message: "目标问题不能为空", trigger: "blur" }],
    questionSource: [{ required: true, message: "问题来源不能为空", trigger: "blur" }],
    businessScenarios: [{ required: true, message: "业务场景不能为空", trigger: "blur" }],
    acceptCriterion: [{ required: true, message: "验收标准不能为空", trigger: "blur" }],
    priority: [{ required: true, message: "优先级不能为空", trigger: "blur" }],
  },
});

const { form, rules } = toRefs(data);

watch(
  () => route.params.versionId,
  () => {
    versionId.value = route.params.versionId || null;
    tableStore.params.versionId = versionId.value;
    ids.value = [];
    multiple.value = true;
    tableRef.value?.resetQuery();
  },
);

// 复用详情页的关联查询口径；每类仅查询一条，通过 total 取得数量。
async function loadTargets(params) {
  const response = await listTarget(params);
  response.data.rows = await Promise.all(response.data.rows.map(async (row) => {
    const query = {
      versionId: params.versionId,
      targetIdList: [row.id],
      pageNum: 1,
      pageSize: 1,
    };
    const results = await Promise.allSettled([
      listObjectTypes(query),
      listRelations(query),
    ]);
    const labels = ["对象类型", "对象类型关系"];
    return {
      ...row,
      relatedElementTags: results.map((result, index) => {
        const count = result.status === "fulfilled" ? result.value.data?.total : null;
        return `${labels[index]}${count ?? "-"}`;
      }),
    };
  }));
  return response;
}

/** 查询建模目标列表 */
function getList() {
  tableRef.value?.getList();
}

// 取消按钮
function cancel() {
  open.value = false;
  openDetail.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    versionId: versionId.value,
    question: null,
    questionSource: null,
    businessScenarios: null,
    priority: "3",
    acceptCriterion: null,
    acceptStatus: null,
    objectTypeNum: null,
    objectAttributeNum: null,
    objectRelationshipNum: null,
    validFlag: null,
    delFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
  };
  proxy.resetForm("targetRef");
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增建模目标";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getTarget(_id).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改建模目标";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["targetRef"].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        updateTarget(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(() => {
        });
      } else {
        addTarget(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(() => {
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row?.id || ids.value;
  proxy.$modal.confirm('是否确认删除建模目标编号为"' + _ids + '"的数据项？').then(function () {
    return delTarget(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

function routeTo(link, row) {
  if (link !== "" && link.indexOf("http") !== -1) {
    window.location.href = link;
    return;
  }
  if (link !== "") {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      router.push({
        path: link,
        query: {
          id: row.id,
        },
      });
    }
  }
}
</script>

<style scoped>
.target-textarea :deep(.el-input__count)::after {
  content: " 个字符";
}
</style>
