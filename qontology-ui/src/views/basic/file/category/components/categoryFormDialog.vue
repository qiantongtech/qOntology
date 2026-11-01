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
  <!-- 新增、修改及详情共用字段布局；详情使用文本展示，不参与表单校验。 -->
  <el-dialog
    v-model="visible"
    :title="title"
    width="800px"
    append-to="body"
    draggable
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :show-close="!saving"
    :before-close="handleBeforeClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="readonly ? undefined : rules"
      :disabled="saving"
      label-width="110px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <qt-form-item class="row-full" label="父级分类" prop="parentId">
        <div v-if="readonly" class="form-readonly">
          {{ parentCategoryName }}
        </div>
        <el-tree-select
          v-else
          v-model="form.parentId"
          :data="categoryOptions"
          :props="{ value: 'id', label: 'name', children: 'children' }"
          value-key="id"
          check-strictly
          clearable
          placeholder="请选择父级分类"
          style="width: 100%"
        />
      </qt-form-item>
      <qt-form-item class="row-full" label="文件分类名称" prop="name">
        <div v-if="readonly" class="form-readonly">{{ form.name || "—" }}</div>
        <el-input
          v-else
          v-model="form.name"
          placeholder="请输入文件分类名称"
          :maxlength="100"
        />
      </qt-form-item>
      <qt-form-item class="row-full" label="排序" prop="orderNum">
        <div v-if="readonly" class="form-readonly">
          {{ form.orderNum ?? "—" }}
        </div>
        <el-input-number
          v-else
          v-model="form.orderNum"
          :min="0"
          :max="9999"
          :precision="0"
          controls-position="right"
          style="width: 100%"
        />
      </qt-form-item>
      <qt-form-item class="row-full" label="状态" prop="validFlag">
        <div v-if="readonly" class="form-readonly">
          {{
            form.validFlag === true
              ? "启用"
              : form.validFlag === false
              ? "禁用"
              : "—"
          }}
        </div>
        <el-radio-group v-else v-model="form.validFlag">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">禁用</el-radio>
        </el-radio-group>
      </qt-form-item>
      <qt-form-item class="row-full" label="描述" prop="remark">
        <div v-if="readonly" class="form-readonly description">
          {{ form.remark || "—" }}
        </div>
        <el-input
          v-else
          v-model="form.remark"
          class="char-count"
          type="textarea"
          :rows="3"
          maxlength="256个字符"
          show-word-limit
          placeholder="请输入描述"
        />
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button :disabled="saving" @click="visible = false">{{
          readonly ? "关 闭" : "取 消"
        }}</el-button>
        <el-button
          v-if="!readonly"
          type="primary"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
          >确 定</el-button
        >
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="CategoryFormDialog">
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import {
  addModelDataCategory,
  getModelDataCategory,
  updateModelDataCategory,
} from "@/api/basic/file/basicFileCategory.js";

// 弹窗独立管理数据加载、校验及保存，父页面仅负责打开与刷新列表。
const emit = defineEmits(["success"]);
const { proxy } = getCurrentInstance();
const visible = ref(false);
const title = ref("");
const readonly = ref(false);
const saving = ref(false);
const validating = ref(false);
const formRef = ref();
const form = ref({});
const categoryOptions = ref([]);
let openRequest = 0;
const rules = {
  name: [{ required: true, message: "文件分类名称不能为空", trigger: "blur" }],
  // false 也是合法状态，必须显式使用布尔类型校验。
  validFlag: [
    {
      type: "boolean",
      required: true,
      message: "请选择状态",
      trigger: "change",
    },
  ],
};

/** 遍历完整分类树查找父级名称，顶级节点也按选项中的名称展示。 */
const parentCategoryName = computed(() => {
  const findName = (nodes) => {
    for (const node of nodes || []) {
      if (String(node.id) === String(form.value.parentId)) return node.name;
      const name = findName(node.children);
      if (name) return name;
    }
    return "";
  };
  return findName(categoryOptions.value) || "—";
});

/** 每次打开重建表单；只采纳最新详情请求，避免快速切换记录时回显旧数据。 */
async function openDialog(options) {
  if (saving.value) return;
  const request = ++openRequest;
  const response =
    options.id != null ? await getModelDataCategory(options.id) : null;
  if (request !== openRequest) return;
  title.value = options.title;
  readonly.value = Boolean(options.readonly);
  categoryOptions.value = options.categoryOptions || [];
  form.value = response
    ? { ...response.data }
    : {
        id: undefined,
        name: "",
        parentId: options.parentId ?? 0,
        orderNum: 0,
        validFlag: true,
        remark: "",
      };
  visible.value = true;
  await nextTick();
  formRef.value?.clearValidate();
}

/** 保存期间阻止标题栏关闭，确保请求完成后才结束当前编辑。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 校验并保存当前草稿；重复点击、只读详情和已关闭的弹窗均不提交。 */
async function submitForm() {
  if (readonly.value || validating.value || saving.value || !visible.value)
    return;
  validating.value = true;
  const draft = form.value;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !visible.value || draft !== form.value) return;
    saving.value = true;
    const editing = draft.id != null;
    await (editing
      ? updateModelDataCategory({ ...draft })
      : addModelDataCategory({ ...draft }));
    proxy.$modal.msgSuccess(editing ? "修改成功" : "新增成功");
    visible.value = false;
    emit("success");
  } finally {
    validating.value = false;
    saving.value = false;
  }
}

defineExpose({ openDialog });
</script>

<style scoped lang="scss">
.form-readonly {
  width: 100%;
  overflow-wrap: anywhere;
}
.description {
  white-space: pre-wrap;
}
</style>
