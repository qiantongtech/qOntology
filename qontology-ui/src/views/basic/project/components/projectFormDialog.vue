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
  <!-- 新增和修改共用本地表单，取消编辑不会直接修改父页面的数据。 -->
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="900px"
    :append-to="appendTo"
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :show-close="!saving"
    :before-close="handleBeforeClose"
    draggable
  >
    <template #header>
      <span role="heading" aria-level="2" class="el-dialog__title">{{
        title
      }}</span>
    </template>
    <guide-tip-panel
      title="创建本体项目，明确所属业务领域、命名空间及建模范围，为后续业务对象、属性和关系的定义与管理提供基础。"
      type="hint"
      class="mb15"
      allow-wrap
    />
    <el-form
      ref="formRef"
      :model="localForm"
      :rules="rules"
      :disabled="saving"
      label-width="90px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <qt-form-item label="项目名称" prop="name" class="row-full">
        <el-input v-model="localForm.name" placeholder="请输入项目名称" />
      </qt-form-item>
      <!-- 图标上传沿用数据板块表单：未上传时展示默认图标，上传后可预览或删除。 -->
      <qt-form-item
        label="图标"
        prop="icon"
        class="row-full"
        tip="请上传大小不超过 5MB、格式为 png/jpg/jpeg 的图片，用于在项目列表中快速识别项目。"
      >
        <ImageUpload
          v-model="localForm.icon"
          class="project-icon-upload"
          :class="{ 'project-icon-upload--default': !localForm.icon }"
          :style="{
            '--project-default-icon': `url(&quot;${defaultProjectIcon}&quot;)`,
          }"
          :title="!localForm.icon ? '点击上传图标' : undefined"
          :limit="1"
          :file-size="5"
          :file-type="['png', 'jpg', 'jpeg']"
          :is-show-tip="false"
        />
      </qt-form-item>
      <qt-form-item label="项目领域" prop="domain" class="row-full">
        <el-input v-model="localForm.domain" placeholder="请输入项目领域" />
      </qt-form-item>
      <qt-form-item
        label="命名空间"
        prop="namespace"
        class="row-full"
        tip="命名空间用于唯一标识本体的术语来源，遵循 W3C 标准。例如：https://example.com"
      >
        <el-input v-model="localForm.namespace" placeholder="请输入命名空间" />
      </qt-form-item>
      <qt-form-item label="初始版本" prop="currentVersion" class="row-full">
        <!-- 初始版本仅在新增时录入，编辑项目时沿用已有版本。 -->
        <el-input
          v-model="localForm.currentVersion"
          :disabled="mode === 'edit'"
          placeholder="请输入初始版本"
        />
      </qt-form-item>
      <qt-form-item label="责任人" prop="responsibleUserId" class="row-full">
        <el-select
          v-model="localForm.responsibleUserId"
          placeholder="请选择责任人"
          clearable
          filterable
          :loading="usersLoading"
        >
          <el-option
            v-for="user in userOptions"
            :key="user.value"
            :label="user.label"
            :value="user.value"
          />
        </el-select>
      </qt-form-item>
      <qt-form-item label="联系电话" class="row-full">
        <el-input
          :model-value="responsiblePhone"
          disabled
          placeholder="请输入联系电话"
        />
      </qt-form-item>
      <qt-form-item label="状态" prop="status" class="row-full">
        <el-radio-group v-model="localForm.status">
          <el-radio v-for="dict in statusOptions" :key="Number(dict.value)" :value="Number(dict.value)">
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </qt-form-item>
      <qt-form-item label="责任人" prop="responsibleUserId" class="row-full">
        <el-select v-model="localForm.responsibleUserId" placeholder="请选择责任人" clearable filterable :loading="usersLoading">
          <el-option v-for="user in userOptions" :key="user.value" :label="user.label" :value="user.value"/>
        </el-select>
      </qt-form-item>
      <qt-form-item label="联系电话" class="row-full">
        <!-- 电话随所选系统用户变化，只读展示，不允许在项目表单中修改用户资料。 -->
        <el-input :model-value="responsiblePhone" disabled/>
      </qt-form-item>
      <qt-form-item label="包含范围" prop="includeRange" class="row-full">
        <el-input v-model="localForm.includeRange" type="textarea" maxlength="256个字符" show-word-limit placeholder="请输入包含范围"/>
      </qt-form-item>
      <qt-form-item label="排除范围" prop="excludeRange" class="row-full">
        <el-input v-model="localForm.excludeRange" type="textarea" maxlength="256个字符" show-word-limit placeholder="请输入排除范围"/>
      </qt-form-item>
      <qt-form-item label="描述" prop="description" class="row-full">
        <el-input v-model="localForm.description" type="textarea" maxlength="256个字符" show-word-limit placeholder="请输入描述"/>
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" :disabled="saving" @click="handleCancel"
          >取 消</el-button
        >
        <el-button
          type="primary"
          size="mini"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
          >确 定</el-button
        >
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="ProjectFormDialog">
import { computed, nextTick, onBeforeUnmount, ref, watch } from "vue";
import { listUserAll } from "@/api/system/system/user";
import ImageUpload from "@/components/ImageUpload/index.vue";
import defaultProjectIcon from "@/assets/images/project-default.svg";

/**
 * 项目新增和修改表单：接收初始数据与校验规则，维护本地草稿，校验后通过 submit 提交快照。
 * 父页面负责接口保存，通过 saving 控制保存期间的交互；cancel 用于通知父页面重置状态。
 */
const props = defineProps({
  open: { type: Boolean, required: true },
  title: { type: String, required: true },
  form: { type: Object, required: true },
  rules: { type: Object, required: true },
  mode: { type: String, default: "add" },
  statusOptions: { type: Array, default: () => [] },
  saving: { type: Boolean, default: false },
  appendTo: { type: [String, Object], default: undefined },
});
const emit = defineEmits(["update:open", "cancel", "submit"]);
const formRef = ref();
const validating = ref(false);
const userOptions = ref([]);
const usersLoading = ref(false);
let userRequest = 0;

// 使用字符串比较用户编号，兼容详情接口和下拉选项中数值与字符串类型的差异。
const responsiblePhone = computed(() => {
  const user = userOptions.value.find(
    (item) => item.value === String(localForm.value.responsibleUserId)
  );
  return user?.phone || "";
});

/** 创建完整初值，确保重新打开新增弹窗时不保留上一次的范围、描述及审计信息。 */
function createEmptyForm() {
  return {
    id: null,
    name: null,
    icon: "",
    domain: null,
    namespace: null,
    currentVersion: null,
    status: 1,
    responsibleUserId: null,
    responsiblePersonName: null,
    responsiblePersonPhone: null,
    includeRange: null,
    excludeRange: null,
    description: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null,
  };
}

const localForm = ref(createEmptyForm());
// 代理 v-model:open；保存期间不允许标题栏或键盘操作意外关闭弹窗。
const dialogVisible = computed({
  get: () => props.open,
  set: (value) => {
    if (!props.saving) emit("update:open", value);
  },
});

/** 同步父页面传入的初始数据，使用独立对象编辑，避免取消时污染父级表单。 */
watch(
  () => props.form,
  (value) => {
    localForm.value = { ...createEmptyForm(), ...(value || {}) };
    localForm.value.responsibleUserId =
      value?.responsibleUserId == null ? null : String(value.responsibleUserId);
    mergeSelectedUser(value || {});
    nextTick(() => formRef.value?.clearValidate());
  },
  { immediate: true, deep: true }
);

/** 补齐当前用户，确保用户不在选项结果中或选项加载失败时仍能回显已有责任人和电话。 */
function mergeSelectedUser(selected) {
  if (selected.responsibleUserId == null) return;
  const value = String(selected.responsibleUserId);
  if (userOptions.value.some((user) => user.value === value)) return;
  userOptions.value.unshift({
    value,
    label: selected.responsiblePersonName || value,
    phone: selected.responsiblePersonPhone || "",
  });
}

/** 加载系统用户选项，电话取自用户资料；只采纳当前弹窗的最新请求。 */
async function loadUserOptions() {
  const request = ++userRequest;
  usersLoading.value = true;
  userOptions.value = [];
  mergeSelectedUser(props.form);
  try {
    const response = await listUserAll();
    if (request !== userRequest || !props.open) return;
    const rows = response.rows || response.data?.rows || [];
    userOptions.value = rows
      .filter((user) => user.userId != null)
      .map((user) => ({
        value: String(user.userId),
        label: user.nickName || user.userName || String(user.userId),
        phone: user.phonenumber || "",
      }));
    mergeSelectedUser(props.form);
  } catch {
    // 请求层统一提示错误，保留已选责任人的回显，避免用户误认为关联已被清空。
  } finally {
    if (request === userRequest) usersLoading.value = false;
  }
}

/** 每次打开时更新用户资料，关闭后使在途请求失效，避免旧弹窗响应覆盖新选择。 */
watch(
  () => props.open,
  (open) => {
    if (open) loadUserOptions();
    else {
      ++userRequest;
      usersLoading.value = false;
    }
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  ++userRequest;
});

/** 保存期间阻止弹窗关闭，其余情况交由弹窗组件同步 v-model。 */
function handleBeforeClose(done) {
  if (!props.saving) done();
}

/** 取消只通知父页面，由父页面关闭弹窗并统一重置表单。 */
function handleCancel() {
  if (!props.saving) emit("cancel");
}

/** 校验本地草稿并提交快照；等待期间禁止重复校验，关闭或保存中的弹窗不再提交。 */
async function submitForm() {
  if (validating.value || props.saving || !props.open) return;
  validating.value = true;
  // 记录本次校验的草稿对象，切换项目或重新打开后不提交旧校验结果。
  const draft = localForm.value;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !props.open || props.saving || draft !== localForm.value)
      return;
    // 清空选择时显式提交 null，使后端能够清除原有责任人关联。
    emit("submit", {
      ...localForm.value,
      icon: localForm.value.icon || "",
      responsibleUserId: localForm.value.responsibleUserId || null,
    });
  } finally {
    validating.value = false;
  }
}
</script>

<style scoped lang="scss">
// 图标上传入口与数据板块保持一致，默认图标上悬浮展示上传提示。
.project-icon-upload {
  line-height: 1;
  :deep(.el-upload--picture-card),
  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 48px !important;
    height: 48px !important;
    line-height: 48px !important;
  }
  :deep(.avatar-uploader-icon) {
    font-size: 18px;
  }
  &.project-icon-upload--default {
    :deep(.el-upload--picture-card) {
      position: relative;
      border: 0;
      background: transparent var(--project-default-icon) center / 32px 32px
        no-repeat !important;
      &::after {
        content: "+";
        position: absolute;
        inset: 8px;
        display: grid;
        place-items: center;
        border-radius: 4px;
        color: #fff;
        background: rgba(0, 0, 0, 0.5);
        font-size: 22px;
        line-height: 1;
        opacity: 0;
        pointer-events: none;
      }
      &:hover::after,
      &:focus-within::after {
        opacity: 1;
      }
    }
    :deep(.avatar-uploader-icon) {
      visibility: hidden;
    }
  }
}
</style>
