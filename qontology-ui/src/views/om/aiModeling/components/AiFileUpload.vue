<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 
 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
 
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
  <div class="upload-file">
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      class="upload-file-uploader"
      action="#"
      drag
      multiple
      :auto-upload="false"
      :accept="accept"
      :disabled="disabled"
      :limit="limit"
      :show-file-list="false"
      :on-change="handleChange"
      :on-remove="handleChange"
      :on-exceed="handleExceed"
    >
      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
      <div class="el-upload__text" style="width: 100%">
        将文件拖到此处，或<em>点击上传</em>
      </div>
    </el-upload>
    <div class="el-upload__tip">
      请上传大小不超过 <b>{{ fileSize }}MB</b>，格式为
      <b>{{ fileType.join("/") }}</b> 的文件，最多可选择 {{ limit }} 个。
    </div>
    <transition-group
      class="upload-file-list el-upload-list el-upload-list--text"
      name="el-fade-in-linear"
      tag="ul"
    >
      <li
        v-for="(file, index) in fileList"
        :key="file.uid"
        class="filelistcont"
      >
        <div class="filelistcont-name" :title="file.name">
          <el-icon><Document /></el-icon>
          <span>{{ file.name }}</span>
        </div>
        <el-link
          v-if="!disabled"
          :underline="false"
          type="danger"
          @click="removeFile(file, index)"
          >删除</el-link
        >
      </li>
    </transition-group>
  </div>
</template>

<script setup name="AiFileUpload">
import { Document } from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  disabled: { type: Boolean, default: false },
  limit: { type: Number, default: 5 },
  fileSize: { type: Number, default: 10 },
  fileType: {
    type: Array,
    default: () => [
      "doc",
      "docx",
      "pdf",
      "txt",
      "png",
      "jpg",
      "jpeg",
      "xls",
      "xlsx",
    ],
  },
});

const emit = defineEmits(["update:modelValue"]);
const { proxy } = getCurrentInstance();
const fileList = ref([]);
const accept = computed(() =>
  props.fileType.map((type) => `.${type}`).join(",")
);

watch(
  () => props.modelValue,
  (value) => {
    if (value !== fileList.value)
      fileList.value = Array.isArray(value) ? [...value] : [];
  },
  { immediate: true }
);

/** 校验文件类型、大小，并同步已选原始文件。 */
function handleChange(_, files) {
  const validFiles = [];
  for (const file of files) {
    const extension = file.name?.split(".").pop()?.toLowerCase();
    if (!props.fileType.includes(extension)) {
      proxy.$modal.msgError(
        `文件格式不正确，请选择 ${props.fileType.join("/")} 格式文件`
      );
      continue;
    }
    if (file.size / 1024 / 1024 > props.fileSize) {
      proxy.$modal.msgError(`文件大小不能超过 ${props.fileSize}MB`);
      continue;
    }
    validFiles.push(file);
  }
  fileList.value = validFiles;
  emit("update:modelValue", [...validFiles]);
}

/** 提示超过最大文件数量。 */
function handleExceed() {
  proxy.$modal.msgError(`上传文件数量不能超过 ${props.limit} 个`);
}

/** 从待上传列表移除指定文件。 */
function removeFile(file, index) {
  const nextFiles = fileList.value.filter(
    (_, fileIndex) => fileIndex !== index
  );
  fileList.value = nextFiles;
  emit("update:modelValue", [...nextFiles]);
}
</script>

<style scoped lang="scss">
.upload-file {
  width: 100%;
}

.upload-file-uploader {
  width: 100%;
  margin-bottom: 5px;

  :deep(.el-upload),
  :deep(.el-upload-dragger) {
    width: 100%;
  }

  :deep(.el-upload-dragger) {
    height: 196px;
    padding: 45px 10px 20px;
  }
}

.upload-file-uploader.is-disabled {
  :deep(.el-upload-dragger) {
    cursor: not-allowed;
    background: #fafafa;
  }
}

.filelistcont {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 32px;
  padding: 2px 0;
}

.filelistcont-name {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
  color: #606266;
  word-break: break-all;
}

.el-upload__tip b {
  color: #f56c6c;
  font-weight: 500;
}
</style>
