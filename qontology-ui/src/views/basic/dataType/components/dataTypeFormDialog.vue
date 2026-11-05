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
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="800px"
    :append-to="appendTo"
    :close-on-click-modal="!saving"
    :close-on-press-escape="!saving"
    :show-close="!saving"
    :before-close="handleBeforeClose"
    draggable
  >
    <template #header>
      <span role="heading" aria-level="2" class="el-dialog__title">{{ title }}</span>
    </template>
    <guide-tip-panel
      v-if="showHint"
      title="定义可复用的业务数据类型，统一基础类型、取值约束及格式规则，为本体属性建模提供标准化的数据定义。"
      type="hint"
      class="mb15"
      allow-wrap
    />
    <!-- 类型切换会改变动态规则，仅更新配置字段，不因规则变化自动校验整张表单。 -->
    <el-form
      ref="formRef"
      :model="localForm"
      :rules="formRules"
      :validate-on-rule-change="false"
      :disabled="saving"
      label-width="110px"
      class="column-form"
      @submit.prevent="submitForm"
    >
      <qt-form-item
        label="业务数据名称"
        prop="name"
        class="row-full"
        tip="填写可复用的业务字段名称，例如订单金额、供应商类别。"
      >
        <el-input v-model="localForm.name" :maxlength="128" placeholder="请输入业务数据名称，例如：订单金额" />
      </qt-form-item>
      <qt-form-item
        label="业务数据类型"
        prop="dataType"
        class="row-full"
        tip="选择基础取值类型，并配置其对应的长度、数值、日期或枚举约束。"
      >
        <!-- 类型选择只切换配置区，不触发选择框校验；点击确定时统一校验。 -->
        <el-select
          v-model="localForm.dataType"
          :validate-event="false"
          :disabled="fixedEnumType !== undefined"
          placeholder="请选择数据类型"
        >
          <el-option
            v-for="item in dataTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </qt-form-item>

      <template v-if="localForm.dataType === DATA_TYPE.TEXT">
        <qt-form-item
          label="格式校验"
          prop="validationType"
          class="row-full"
          tip="选择文本需要满足的格式规则，选择无校验时只检查长度约束。"
        >
          <el-select v-model="localForm.validationType" placeholder="请选择格式校验">
            <el-option
              v-for="item in validationOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </qt-form-item>
        <qt-form-item
          label="最小长度"
          prop="minLength"
          class="row-full"
          tip="指定允许输入的最少字符数，留空表示不限制。"
        >
          <el-input-number
            v-model="localForm.minLength"
            :min="0"
            :max="2147483647"
            :precision="0"
            placeholder="不限制"
            controls-position="right"
          />
        </qt-form-item>
        <qt-form-item
          label="最大长度"
          prop="maxLength"
          class="row-full"
          tip="不填写最大长度时，表示业务层不设置字符长度上限，实际存储仍受数据源限制。"
        >
          <el-input-number
            v-model="localForm.maxLength"
            :min="0"
            :max="2147483647"
            :precision="0"
            placeholder="不限制"
            controls-position="right"
          />
        </qt-form-item>
        <qt-form-item
          v-if="localForm.validationType === 'CUSTOM'"
          label="正则表达式"
          prop="validationPattern"
          class="row-full"
          tip="使用兼容 JavaScript 的正则表达式，具体落地应与后端校验引擎一致。"
        >
          <el-input
            v-model="localForm.validationPattern"
            :maxlength="512"
            placeholder="请输入正则表达式，如 ^[A-Za-z]+$（不含两侧 /）"
          />
        </qt-form-item>
      </template>

      <qt-form-item
        v-if="localForm.dataType === DATA_TYPE.DECIMAL"
        label="总位数"
        prop="allPlaces"
        class="row-full"
      >
        <el-input-number
          v-model="localForm.allPlaces"
          :min="1"
          :max="2147483647"
          :precision="0"
          controls-position="right"
        />
      </qt-form-item>
      <qt-form-item
        v-if="localForm.dataType === DATA_TYPE.DECIMAL"
        label="小数位数"
        prop="decimalPlaces"
        class="row-full"
      >
        <el-input-number
          v-model="localForm.decimalPlaces"
          :min="0"
          :max="2147483647"
          :precision="0"
          controls-position="right"
        />
      </qt-form-item>
      <!-- 整数与小数共用数值范围；整数只允许整数边界，小数保留输入的小数边界。 -->
      <template v-if="isNumeric">
        <qt-form-item label="最小值" prop="minValue" class="row-full">
          <el-input-number
            v-model="localForm.minValue"
            :precision="localForm.dataType === DATA_TYPE.INTEGER ? 0 : undefined"
            controls-position="right"
            placeholder="不限制"
          />
        </qt-form-item>
        <qt-form-item label="最大值" prop="maxValue" class="row-full">
          <el-input-number
            v-model="localForm.maxValue"
            :precision="localForm.dataType === DATA_TYPE.INTEGER ? 0 : undefined"
            controls-position="right"
            placeholder="不限制"
          />
        </qt-form-item>
      </template>
      <qt-form-item
        v-if="isNumeric"
        label="单位"
        prop="unit"
        class="row-full"
        tip="填写数值的业务计量单位，例如元、件或千克。"
      >
        <el-input v-model="localForm.unit" :maxlength="32" placeholder="请输入单位" />
      </qt-form-item>
      <!-- 布尔文案默认“是／否”，允许按业务语义修改，并随表单快照提交。 -->
      <template v-if="localForm.dataType === DATA_TYPE.BOOLEAN">
        <qt-form-item label="true 对应文案" prop="trueValue" class="row-full">
          <el-input v-model="localForm.trueValue" :maxlength="64" placeholder="请输入 true 对应文案" />
        </qt-form-item>
        <qt-form-item
          label="false 对应文案"
          prop="falseValue"
          class="row-full"
          tip="请为两种状态提供明确且相互区分的业务名称。"
        >
          <el-input v-model="localForm.falseValue" :maxlength="64" placeholder="请输入 false 对应文案" />
        </qt-form-item>
      </template>
      <template v-if="localForm.dataType === DATA_TYPE.DATE">
        <qt-form-item label="展示格式" prop="format" class="row-full">
          <el-select v-model="localForm.format" placeholder="请选择展示格式">
            <el-option
              v-for="format in dateFormats"
              :key="format"
              :label="format"
              :value="format"
            />
          </el-select>
        </qt-form-item>
        <qt-form-item label="最早日期" prop="earliestTime" class="row-full">
          <el-date-picker
            v-model="localForm.earliestTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disableEarliestTime"
            clearable
            placeholder="不限制"
          />
        </qt-form-item>
        <qt-form-item
          label="最晚日期"
          prop="latestTime"
          class="row-full"
          tip="选填；用于约束允许录入的日期范围。"
        >
          <el-date-picker
            v-model="localForm.latestTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disableLatestTime"
            clearable
            placeholder="不限制"
          />
        </qt-form-item>
      </template>
      <slot v-if="localForm.dataType === DATA_TYPE.ENUM" name="enum-source" :form="localForm" />
      <qt-form-item
        v-if="localForm.dataType === DATA_TYPE.ENUM"
        label="枚举类型"
        prop="enumType"
        class="row-full"
        tip="仅允许引用已存在的枚举定义；枚举值在“枚举管理”中维护。"
      >
        <slot name="enum-selector" :form="localForm">
          <el-select
            v-model="localForm.enumType"
            :disabled="fixedEnumType !== undefined"
            placeholder="请选择枚举类型"
            filterable
          >
            <el-option
              v-for="item in enumOptions"
              :key="item.enumType"
              :label="item.enumName"
              :value="item.enumType"
            />
          </el-select>
        </slot>
      </qt-form-item>
      <slot name="before-description" :form="localForm" />
      <qt-form-item
        label="描述"
        prop="description"
        class="row-full"
        tip="说明该数据类型的业务含义、取值口径和适用场景，最多256个字符。"
      >
        <el-input
          v-model="localForm.description"
          type="textarea"
          :rows="3"
          :maxlength="descriptionMaxLength"
          show-word-limit
          placeholder="请输入描述，说明业务含义、取值口径和适用场景"
        />
      </qt-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="mini" :disabled="saving" @click="handleCancel">取 消</el-button>
        <el-button
          type="primary"
          size="mini"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
        >确 定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="DataTypeFormDialog">
import { computed, nextTick, ref, watch } from "vue";

/** 业务数据类型编辑组件：管理本地草稿与类型配置，校验后由父页面保存并刷新列表。 */
const props = defineProps({
  open: { type: Boolean, required: true },
  title: { type: String, required: true },
  form: { type: Object, required: true },
  rules: { type: Object, required: true },
  dataTypeOptions: { type: Array, default: () => [] },
  enumOptions: { type: Array, default: () => [] },
  fixedEnumType: { type: String, default: undefined },
  saving: { type: Boolean, default: false },
  showHint: { type: Boolean, default: true },
  descriptionMaxLength: { type: Number, default: 256 },
  appendTo: { type: [String, Object], default: undefined },
});
const emit = defineEmits(["update:open", "cancel", "submit"]);
const formRef = ref();
const validating = ref(false);
const DATA_TYPE = { TEXT: "0", INTEGER: "1", DECIMAL: "2", BOOLEAN: "3", DATE: "4", ENUM: "5" };

/** 初始化每种类型现有的配置字段，防止新建时残留上次编辑内容。 */
function createEmptyForm() {
  return {
    id: null,
    name: null,
    // 新增默认文本类型，父页面传入的编辑数据或固定枚举类型会覆盖该初值。
    dataType: DATA_TYPE.TEXT,
    description: null,
    unit: null,
    decimalPlaces: 2,
    allPlaces: 10,
    minValue: null,
    maxValue: null,
    trueValue: "是",
    falseValue: "否",
    earliestTime: null,
    latestTime: null,
    format: "YYYY-MM-DD",
    enumType: null,
    minLength: null,
    maxLength: null,
    validationType: "NONE",
    validationPattern: null,
    exampleValue: null,
  };
}

const localForm = ref(createEmptyForm());
const isNumeric = computed(() => [DATA_TYPE.INTEGER, DATA_TYPE.DECIMAL].includes(localForm.value.dataType));
// 格式选项保留独立编码，预设规则只需选择类型，自定义规则才需要填写表达式。
const validationOptions = [
  { label: "无校验", value: "NONE" },
  { label: "手机号码", value: "PHONE" },
  { label: "身份证号", value: "ID_CARD" },
  { label: "电子邮箱", value: "EMAIL" },
  { label: "URL地址", value: "URL" },
  { label: "自定义正则表达式", value: "CUSTOM" },
];

// 展示格式与存储格式分离，日期边界始终提交 YYYY-MM-DD，切换展示格式不改变日期值。
const dateFormats = ["YYYY-MM-DD", "YYYY/MM/DD", "YYYY年MM月DD日", "YYYY-MM-DD HH:mm:ss"];

/** 检查数值上下限：可不填，整数类型不允许小数，两个边界均填写时必须有序。 */
function validateValueRange(_rule, value, callback) {
  if (value != null && (!Number.isFinite(value) || (localForm.value.dataType === DATA_TYPE.INTEGER && !Number.isInteger(value)))) {
    return callback(new Error(localForm.value.dataType === DATA_TYPE.INTEGER ? "请输入整数" : "请输入有效数值"));
  }
  const { minValue, maxValue } = localForm.value;
  callback(minValue != null && maxValue != null && minValue > maxValue ? new Error("最小值不能大于最大值") : undefined);
}

/** 总精度包含整数与小数部分，小数位数必须为非负整数且不能超过总精度。 */
function validatePrecision(_rule, _value, callback) {
  const { allPlaces, decimalPlaces } = localForm.value;
  if (!Number.isInteger(allPlaces) || allPlaces < 1 || allPlaces > 2147483647) {
    return callback(new Error("总位数必须为正整数"));
  }
  if (!Number.isInteger(decimalPlaces) || decimalPlaces < 0) return callback(new Error("小数位数必须为非负整数"));
  callback(decimalPlaces > allPlaces ? new Error("小数位数不能大于总位数") : undefined);
}

/** 日期选择器固定输出 ISO 日期字符串，可直接比较先后；留空表示不限制。 */
function validateDateRange(_rule, _value, callback) {
  const { earliestTime, latestTime } = localForm.value;
  callback(earliestTime && latestTime && earliestTime > latestTime ? new Error("最早日期不能晚于最晚日期") : undefined);
}

/** 按本地日历日期生成比较键，避免使用 UTC 解析导致日期边界偏移。 */
function dateKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

/** 禁止选择晚于已设最晚日期的起始日期，相等日期允许选择。 */
function disableEarliestTime(date) {
  return Boolean(localForm.value.latestTime && dateKey(date) > localForm.value.latestTime.slice(0, 10));
}

/** 禁止选择早于已设最早日期的结束日期，相等日期允许选择。 */
function disableLatestTime(date) {
  return Boolean(localForm.value.earliestTime && dateKey(date) < localForm.value.earliestTime.slice(0, 10));
}

/** 检查长度为非负整数，并同时检查上下限，空值表示不限制。 */
function validateLength(_rule, value, callback) {
  if (value != null && (!Number.isInteger(value) || value < 0 || value > 2147483647)) {
    callback(new Error("长度必须为非负整数"));
    return;
  }
  const { minLength, maxLength } = localForm.value;
  callback(minLength != null && maxLength != null && minLength > maxLength
    ? new Error("最小长度不能大于最大长度") : undefined);
}

/** 自定义正则必填并校验语法，其他格式不使用正则输入框。 */
function validatePattern(_rule, value, callback) {
  if (localForm.value.validationType !== "CUSTOM") return callback();
  if (!value || !value.trim()) return callback(new Error("请输入正则表达式"));
  try {
    new RegExp(value);
    callback();
  } catch {
    callback(new Error("正则表达式语法不正确"));
  }
}

function validateBooleanLabels(_rule, _value, callback) {
  const first = (localForm.value.trueValue || "").trim();
  const second = (localForm.value.falseValue || "").trim();
  callback(!first || !second ? new Error("布尔文案不能为空") : first === second ? new Error("两种布尔状态的文案不能相同") : undefined);
}

// 仅添加当前数据类型的配置校验，隐藏类型的约束不会阻止当前表单提交。
const formRules = computed(() => ({
  ...props.rules,
  ...(localForm.value.dataType === DATA_TYPE.BOOLEAN ? {
    trueValue: [{ validator: validateBooleanLabels, trigger: "blur" }],
    falseValue: [{ validator: validateBooleanLabels, trigger: "blur" }],
  } : {}),
  ...(localForm.value.dataType === DATA_TYPE.TEXT ? {
    minLength: [{ validator: validateLength, trigger: "change" }],
    maxLength: [{ validator: validateLength, trigger: "change" }],
    validationType: [{ required: true, message: "请选择格式校验", trigger: "change" }],
    validationPattern: [{
      required: localForm.value.validationType === "CUSTOM",
      validator: validatePattern,
      trigger: "blur",
    }],
  } : {}),
  ...(isNumeric.value ? {
    minValue: [{ validator: validateValueRange, trigger: "change" }],
    maxValue: [{ validator: validateValueRange, trigger: "change" }],
  } : {}),
  ...(localForm.value.dataType === DATA_TYPE.DECIMAL ? {
    allPlaces: [{ required: true, validator: validatePrecision, trigger: "change" }],
    decimalPlaces: [{ required: true, validator: validatePrecision, trigger: "change" }],
  } : {}),
  ...(localForm.value.dataType === DATA_TYPE.DATE ? {
    format: [{ required: true, message: "请选择展示格式", trigger: "change" }],
    earliestTime: [{ validator: validateDateRange, trigger: "change" }],
    latestTime: [{ validator: validateDateRange, trigger: "change" }],
  } : {}),
}));

const dialogVisible = computed({
  get: () => props.open,
  set: (value) => {
    if (!props.saving) emit("update:open", value);
  },
});

/** 复制父页面初值；固定枚举入口强制绑定当前枚举，取消本地编辑不修改父级数据。 */
watch(() => [props.form, props.fixedEnumType], ([form, enumType]) => {
  localForm.value = { ...createEmptyForm(), ...form };
  localForm.value.validationType = form.validationType || "NONE";
  localForm.value.allPlaces ??= 10;
  localForm.value.decimalPlaces ??= 2;
  localForm.value.trueValue ??= "是";
  localForm.value.falseValue ??= "否";
  if (localForm.value.dataType === DATA_TYPE.DATE && !dateFormats.includes(localForm.value.format)) localForm.value.format = dateFormats[0];
  if (enumType !== undefined) {
    localForm.value.dataType = DATA_TYPE.ENUM;
    localForm.value.enumType = enumType;
  }
  nextTick(() => formRef.value?.clearValidate());
}, { immediate: true, deep: true });

/** 切换类型后等待配置项完成挂载，再清除之前类型的校验提示。 */
watch(() => localForm.value.dataType, async () => {
  // 日期类型使用预设展示格式；切换类型时补齐默认值。
  if (localForm.value.dataType === DATA_TYPE.DATE && !dateFormats.includes(localForm.value.format)) localForm.value.format = dateFormats[0];
  await nextTick();
  formRef.value?.clearValidate();
});

/** 切换格式后移除隐藏表达式的旧校验提示，避免已不适用的错误留在表单中。 */
watch(() => localForm.value.validationType, async () => {
  await nextTick();
  formRef.value?.clearValidate("validationPattern");
});

/** 保存期间阻止通过标题栏或键盘关闭弹窗。 */
function handleBeforeClose(done) {
  if (!props.saving) done();
}

/** 通知父页面取消编辑并重置状态。 */
function handleCancel() {
  if (!props.saving) emit("cancel");
}

/** 校验当前已显示的基础字段与类型配置，提交独立快照并阻止旧草稿的迟到校验结果。 */
async function submitForm() {
  if (props.saving || validating.value || !props.open) return;
  const draft = localForm.value;
  validating.value = true;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !props.open || props.saving || draft !== localForm.value) return;
    // 非自定义格式不保存隐藏的旧正则，避免后续回显产生歧义。
    emit("submit", { ...draft, validationPattern: draft.validationType === "CUSTOM" ? draft.validationPattern : null });
  } finally {
    validating.value = false;
  }
}
</script>

<style scoped>
/* 分区标题独占表单整行，与单列基础字段及动态配置字段保持统一间距。 */
.h2-title {
  grid-column: span 2;
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
  margin: 8px 0;
}

.h2-title::before {
  content: '';
  width: 6px;
  height: 16px;
  border-radius: 3px;
  background: var(--el-color-primary);
  margin-right: 8px;
}

:deep(.el-input-number),
:deep(.el-date-editor) {
  width: 100%;
}
</style>
