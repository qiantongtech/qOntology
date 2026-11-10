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
  <!-- 上传及文件元数据统一在独立弹窗中编辑，保存成功后通知列表刷新。 -->
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
      <qt-form-item class="row-full" label="文件分类" prop="categoryId">
        <div v-if="readonly" class="form-readonly">
          {{ categoryName }}
        </div>
        <el-tree-select
          v-else
          v-model="form.categoryId"
          :data="categoryOptions"
          :props="{ value: 'id', label: 'name', children: 'children' }"
          value-key="id"
          placeholder="请选择文件分类"
          check-strictly
          clearable
          style="width: 100%"
        />
      </qt-form-item>
      <!-- 详情态不展示上传控件，文件信息统一在名称与描述字段中只读呈现。 -->
      <qt-form-item v-if="!readonly" class="row-full" label="上传文件" prop="filePath">
        <FileUpload2
          v-model="form.filePath"
          :file-name="form.fileName"
          :limit="1"
          :file-type="[
            'txt',
            'pdf',
            'html',
            'xlsx',
            'xls',
            'docx',
            'csv',
            'md',
            'mdx',
            'htm',
            'markdown',
            'ttl',
          ]"
          @update:fileSize="(value) => (form.fileSize = value)"
          @update:fileExt="(value) => (form.fileSuffix = value)"
          @update:fileName="updateFormFileName"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="文件名称"
        prop="fileName"
      >
        <div v-if="readonly" class="form-readonly">
          {{ form.fileName || "—" }}
        </div>
        <el-input
          v-else
          v-model="form.fileName"
          placeholder="请输入文件名称"
        />
      </qt-form-item>
      <!-- 详情态补充文件大小字段，展示格式与列表的文件大小列一致。 -->
      <el-form-item v-if="readonly" class="row-full" label="文件大小">
        <div class="form-readonly">
          {{ form.fileSize ? formatSize(form.fileSize) : "—" }}
        </div>
      </el-form-item>
      <qt-form-item class="row-full" label="描述" prop="description">
        <div v-if="readonly" class="form-readonly textarea">
          {{ form.description || "—" }}
        </div>
        <el-input
          v-else
          v-model="form.description"
          class="character-count"
          type="textarea"
          :rows="3"
          maxlength="256个字符"
          show-word-limit
          placeholder="请输入描述"
        />
      </qt-form-item>
      <!-- 详情态补齐审计信息，创建人展示规则与枚举详情一致。 -->
      <el-form-item v-if="readonly" class="row-full" label="创建人">
        <div class="form-readonly">{{ form.createBy || "—" }}</div>
      </el-form-item>
      <el-form-item v-if="readonly" class="row-full" label="联系方式">
        <div class="form-readonly">{{ getPhoneNum(form) || "—" }}</div>
      </el-form-item>
      <el-form-item v-if="readonly" class="row-full" label="创建时间">
        <div class="form-readonly">{{ parseTime(form.createTime, "{y}-{m}-{d} {h}:{i}") || "—" }}</div>
      </el-form-item>
      <el-form-item v-if="readonly" class="row-full" label="更新时间">
        <div class="form-readonly">{{ parseTime(form.updateTime, "{y}-{m}-{d} {h}:{i}") || "—" }}</div>
      </el-form-item>
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

<script setup name="FileFormDialog">
import { computed, getCurrentInstance, nextTick, ref } from "vue";
import {
  addModelDataFile,
  getModelDataFile,
  updateModelDataFile,
} from "@/api/basic/file/basicFile.js";
import FileUpload2 from "@/components/FileUpload2";
import { useCreatorPhones } from "@/utils/useCreatorPhones";
import { formatSize } from "@/utils/index";

const props = defineProps({
  categoryOptions: { type: Array, default: () => [] },
});
const emit = defineEmits(["success"]);
const { proxy } = getCurrentInstance();
const { creatorPhones } = useCreatorPhones();
const visible = ref(false);
const title = ref("");
const readonly = ref(false);
const saving = ref(false);
const validating = ref(false);
const formRef = ref();
const form = ref({});
let openRequest = 0;
const rules = {
  categoryId: [
    { required: true, message: "文件分类不能为空", trigger: "change" },
  ],
  fileName: [
    { required: true, message: "文件名称不能为空", trigger: "change" },
  ],
  filePath: [
    { required: true, message: "上传文件不能为空", trigger: "change" },
  ],
};

/** 详情态优先用接口返回的分类名称，缺失时递归分类选项按 categoryId 查找。 */
const categoryName = computed(() => {
  const findName = (nodes) => {
    for (const node of nodes || []) {
      if (String(node.id) === String(form.value.categoryId)) return node.name;
      const name = findName(node.children);
      if (name) return name;
    }
    return "";
  };
  return form.value.categoryName || findName(props.categoryOptions) || "—";
});

/** 复用用户电话关联结果，创建人与联系电话用斜杠分隔，未维护电话时仅显示姓名。 */
function formatCreator(record) {
  const phone = creatorPhones.value[record.creatorId];
  return phone ? `${record.createBy || "-"} / ${phone}` : record.createBy || "-";
}
function getPhoneNum(record) {
  const phone = creatorPhones.value[record.creatorId];
  return phone ? `${phone}` : "-";
}

/** 打开时加载最新详情或创建空白草稿，忽略快速切换记录后返回的旧请求。 */
async function openDialog(options = {}) {
  if (saving.value) return;
  const request = ++openRequest;
  const response =
    options.id != null ? await getModelDataFile(options.id) : null;
  if (request !== openRequest) return;
  form.value = response
    ? { ...response.data }
    : {
        id: undefined,
        categoryId: undefined,
        fileName: "",
        fileSuffix: "",
        fileType: "OTHER",
        fileSize: 0,
        filePath: "",
        storageType: "LOCAL",
        description: "",
      };
  // 上传组件及名称输入框使用字符串，避免数组形式的文件名称影响回显。
  form.value.fileName = Array.isArray(form.value.fileName)
    ? form.value.fileName.join(",")
    : form.value.fileName || "";
  title.value =
    options.title || (options.id != null ? "修改文件" : "新增文件");
  readonly.value = Boolean(options.readonly);
  visible.value = true;
  await nextTick();
  formRef.value?.clearValidate();
}

/** 上传成功同步名称，并清除上传字段遗留的必填提示。 */
function updateFormFileName(name) {
  form.value.fileName = Array.isArray(name) ? name.join(",") : name || "";
  nextTick(() => formRef.value?.clearValidate(["filePath", "fileName"]));
}

/** 保存期间阻止关闭弹窗，避免重复提交或丢失保存状态。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 校验并保存草稿，成功后刷新文件列表及分类统计；只读详情不提交。 */
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
      ? updateModelDataFile({ ...draft })
      : addModelDataFile({ ...draft }));
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
.char-count :deep(.el-input__count)::after {
  content: "个字符";
}
</style>
