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

<!-- BasicInfoForm.vue -->
<template>
  <el-form
    ref="formRef"
    :model="localForm"
    :rules="rules"
    label-width="170px"
    :disabled="disabled"
  >
    <div class="h2-titles mb15">基础信息</div>
    <el-row :gutter="20">
      <el-col :span="11">
        <el-form-item label="项目" prop="projectId">
          <el-select
            v-model="localForm.projectId"
            placeholder="请选择项目"
            @change="onProjectChange"
          >
            <el-option
              v-for="item in projectList"
              :key="item.dictValue"
              :label="item.dictLabel || item.label || item.name"
              :value="String(item.dictValue)"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="11">
        <el-form-item label="项目版本" prop="projectVersionId">
          <el-select
            v-model="localForm.projectVersionId"
            placeholder="请选择项目版本"
            @change="onProjectVersionChange"
          >
            <el-option
              v-for="item in projectVersionList"
              :key="getOptionValue(item)"
              :label="item.dictLabel"
              :value="getOptionValue(item)"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="11">
        <qt-form-item label="责任人" prop="responsibleUserId" class="row-full">
          <el-select
            v-model="localForm.responsibleUserId"
            placeholder="请选择责任人"
            clearable
            filterable
            @change="handleResponsibleUserChange"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.value"
              :label="user.label"
              :value="user.value"
            />
          </el-select>
        </qt-form-item>
      </el-col>
      <el-col :span="11">
        <qt-form-item label="联系电话" class="row-full">
          <el-input
            :model-value="localForm.responsiblePhone"
            disabled
            placeholder="请输入联系电话"
          />
        </qt-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="11">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="localForm.name" placeholder="请输入任务名称" />
        </el-form-item>
      </el-col>
      <el-col :span="11">
        <el-form-item label="数据源" prop="dataSourceId">
          <div class="data-source-wrap">
            <el-select
              class="data-source-select"
              v-model="localForm.dataSourceId"
              @change="onDataSourceChange"
              placeholder="请选择"
            >
              <el-option
                v-for="item in dataSourceList"
                :key="item.id"
                :label="item.datasourceName"
                :value="item.id"
              />
            </el-select>
            <div class="data-source-btn">
              <el-button type="primary" @click="onTestConnection">
                测试连接
              </el-button>
              <el-icon v-if="connectionSuccess" class="status-icon success-icon"
                ><check
              /></el-icon>
              <el-icon v-if="connectionError" class="status-icon error-icon"
                ><close
              /></el-icon>
            </div>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="11">
        <el-form-item label="更新类型" prop="updateType">
          <el-radio-group v-model="localForm.updateType">
            <el-radio
              v-for="item in updateTypeOptions"
              :key="parseInt(item.value)"
              :label="parseInt(item.value)"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col :span="11">
        <el-form-item label="更新频率" prop="updateFrequency">
          <div class="data-source-wrap">
            <el-input
              v-model="localForm.updateFrequency"
              placeholder="请输入 Cron 表达式"
            >
            </el-input>
            <div class="data-source-btn">
              <el-button type="primary" @click="onShowCron"> 配置 </el-button>
            </div>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="22">
        <el-form-item label="描述" prop="description">
          <el-input
            type="textarea"
            v-model="localForm.description"
            :rows="2"
            placeholder="请输入描述"
            maxlength="256个字符"
            show-word-limit
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup>
import { reactive, ref, watch } from "vue";
import { Check, Close } from "@element-plus/icons-vue";
import { listUserAll } from "@/api/system/system/user.js";

const props = defineProps({
  formData: {
    type: Object,
    required: true,
  },
  rules: {
    type: Object,
    default: () => ({}),
  },
  dataSourceList: {
    type: Array,
    default: () => [],
  },
  updateTypeOptions: {
    type: Array,
    default: () => [],
  },
  projectList: { type: Array, default: () => [] },
  projectVersionList: { type: Array, default: () => [] },
  disabled: {
    type: Boolean,
    default: false,
  },
  connectionSuccess: Boolean,
  connectionError: Boolean,
});

const emit = defineEmits([
  "dataSourceChange",
  "testConnection",
  "showCronDialog",
  "update:formData",
  "projectChange",
  "projectVersionChange",
]);

const userOptions = ref([]);
const formRef = ref();
const localForm = reactive({});

function getOptionValue(item) {
  const value = item?.dictValue ?? item?.value ?? item?.id;
  return value == null ? "" : String(value);
}

// 父组件数据变化时同步本地草稿，避免子组件直接修改 prop。
watch(
  () => props.formData,
  (value) => Object.assign(localForm, value || {}),
  { deep: true, immediate: true }
);

// 本地草稿变化时通过标准 update 事件同步父组件。
watch(localForm, (value) => emit("update:formData", { ...value }), {
  deep: true,
});
onMounted(async () => {
  await loadUserOptions();
});
/** 加载系统用户选项，电话取自用户资料；只采纳当前弹窗的最新请求。 */
async function loadUserOptions() {
  userOptions.value = [];
  const response = await listUserAll();
  const rows = response.rows || response.data?.rows || [];
  userOptions.value = rows
    .filter((user) => user.userId != null)
    .map((user) => ({
      // value 保持数字类型，与后端返回的 responsibleUserId 一致，避免 el-select 回显失配
      value: user.userId,
      label: user.nickName || user.userName || String(user.userId),
      phone: user.phonenumber || "",
    }));
}
// 通知父组件切换数据源。
const onDataSourceChange = (value) => {
  emit("dataSourceChange", value);
};

// 负责人选择变化时更新电话（按字符串比对，兼容数字/字符串类型）
function handleResponsibleUserChange(value) {
  const user = userOptions.value.find(
    (user) => String(user.value) === String(value)
  );
  localForm.responsiblePhone = user?.phone || "";
  localForm.responsiblePersonName = user?.label || "";
}
// 切换项目。
const onProjectChange = (value) => emit("projectChange", value);

// 切换项目版本。
const onProjectVersionChange = (value) => emit("projectVersionChange", value);

// 通知父组件测试数据源连接。
const onTestConnection = () => {
  emit("testConnection");
};

// 通知父组件打开更新频率配置弹框。
const onShowCron = () => {
  emit("showCronDialog");
};

// 暴露方法给父组件
defineExpose({
  validate: () => formRef.value?.validate(),
  validateField: (props) => formRef.value?.validateField(props),
});
</script>

<style scoped lang="scss">
.success-icon,
.error-icon {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--qt-text-inverse);
  font-size: var(--qt-font-size-small);
}
.success-icon {
  background-color: var(--qt-status-success);
}
.error-icon {
  background-color: var(--qt-status-danger);
}

.data-source-wrap {
  display: flex;
  width: 100%;
}
.data-source-wrap .data-source-select {
  flex: 1;
  min-width: 0;

  :deep(.el-select__wrapper) {
    border-radius: var(--qt-radius-medium) 0 0 var(--qt-radius-medium);
  }
}
.data-source-btn {
  display: flex;
  align-items: center;
  flex-shrink: 0;

  .el-button {
    margin-left: -1px;
    border-radius: 0 var(--qt-radius-medium) var(--qt-radius-medium) 0;
  }

  .status-icon {
    margin-left: var(--qt-space-1);
  }
}

:deep(.el-input-group__append) {
  padding: 0;
  background: var(--qt-element-fixed-primary);
  border-color: var(--qt-element-fixed-primary);
}
</style>
