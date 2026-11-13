<!--
 /*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */
-->

<template>
  <!-- 新增、修改共用单列表单布局；上级部门选项与详情由打开时传入或请求。 -->
  <el-dialog
    v-model="visible"
    :title="title"
    width="900px"
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
      :rules="rules"
      :disabled="saving"
      label-width="110px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <!-- 公司顶级部门无上级可选，沿用原页面按 parentId 隐藏的逻辑。 -->
      <qt-form-item
        v-if="form.parentId !== 0"
        class="row-full"
        label="上级部门"
        prop="parentId"
      >
        <el-tree-select
          v-model="form.parentId"
          :data="deptOptions"
          :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
          value-key="deptId"
          check-strictly
          clearable
          placeholder="请选择上级部门"
          class="full-width"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="部门名称"
        prop="deptName"
      >
        <el-input
          v-model="form.deptName"
          placeholder="请输入部门名称"
          maxlength="30"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="显示排序"
        prop="orderNum"
      >
        <el-input-number
          v-model="form.orderNum"
          :min="0"
          :max="9999"
          :precision="0"
          controls-position="right"
          class="full-width"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="负责人"
        prop="leader"
      >
        <el-input
          v-model="form.leader"
          placeholder="请输入负责人"
          maxlength="20"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="联系电话"
        prop="phone"
      >
        <el-input
          v-model="form.phone"
          placeholder="请输入联系电话"
          maxlength="11"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="邮箱"
        prop="email"
      >
        <el-input
          v-model="form.email"
          placeholder="请输入邮箱"
          maxlength="50"
        />
      </qt-form-item>
      <qt-form-item
        class="row-full"
        label="部门状态"
        prop="status"
      >
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :value="dict.value"
          >{{ dict.label }}</el-radio>
        </el-radio-group>
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button :disabled="saving" @click="visible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
        >确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="DeptFormDialog">
import {getCurrentInstance, nextTick, ref} from "vue";
import {addDept, getDept, updateDept} from "@/api/system/system/dept.js";

// 弹窗独立管理详情回显、校验及保存，父页面仅负责加载选项与刷新列表。
const emit = defineEmits(["success"]);
const {proxy} = getCurrentInstance();
const {sys_normal_disable} = proxy.useDict("sys_normal_disable");
const visible = ref(false);
const title = ref("");
const saving = ref(false);
const validating = ref(false);
const formRef = ref();
const form = ref({});
const deptOptions = ref([]);
let openRequest = 0;
const rules = {
  parentId: [{required: true, message: "上级部门不能为空", trigger: "blur"}],
  deptName: [{required: true, message: "部门名称不能为空", trigger: "blur"}],
  orderNum: [{required: true, message: "显示排序不能为空", trigger: "blur"}],
  email: [{type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"]}],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur"
    }
  ]
};

/** 每次打开重建表单；编辑时请求最新详情，只采纳最新请求避免回显旧数据。 */
async function openDialog(options) {
  if (saving.value) return;
  const request = ++openRequest;
  const response = options.deptId != null ? await getDept(options.deptId) : null;
  if (request !== openRequest) return;
  title.value = options.title;
  deptOptions.value = options.deptOptions || [];
  form.value = response
    ? {...response.data}
    : {
        deptId: undefined,
        parentId: options.parentId,
        deptName: "",
        orderNum: 0,
        leader: "",
        phone: "",
        email: "",
        status: "0"
      };
  visible.value = true;
  await nextTick();
  formRef.value?.clearValidate();
}

/** 保存期间阻止标题栏关闭，确保请求完成后才结束当前编辑。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 校验并保存部门；重复点击和已关闭的弹窗均不提交。 */
async function submitForm() {
  if (validating.value || saving.value || !visible.value) return;
  validating.value = true;
  const draft = form.value;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !visible.value || draft !== form.value) return;
    saving.value = true;
    const editing = draft.deptId != null;
    await (editing ? updateDept({...draft}) : addDept({...draft}));
    proxy.$modal.msgSuccess(editing ? "修改成功" : "新增成功");
    visible.value = false;
    emit("success");
  } finally {
    validating.value = false;
    saving.value = false;
  }
}

defineExpose({openDialog});
</script>

<style scoped lang="scss">
.full-width {
  width: 100%;
}
</style>
