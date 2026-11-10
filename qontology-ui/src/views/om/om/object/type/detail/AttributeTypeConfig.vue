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
  <qt-form-item
    v-if="[1, 2].includes(typeCode)"
    :tip="readonly ? undefined : '填写该属性的计量单位，例如 元、千克、件。'"
    label="单位"
    prop="unit"
  >
    <div v-if="readonly" class="form-readonly">{{ model.unit || "-" }}</div>
    <el-input
      v-else
      v-model="model.unit"
      placeholder="请输入单位"
      :maxlength="32"
    />
  </qt-form-item>
  <qt-form-item
    v-if="typeCode === 2"
    :tip="readonly ? undefined : '设置数值保留的小数位数，用于展示与校验。'"
    label="小数位数"
    prop="decimalPlaces"
  >
    <div v-if="readonly" class="form-readonly">
      {{ model.decimalPlaces == null ? "-" : String(model.decimalPlaces) }}
    </div>
    <el-input-number
      v-else
      v-model="model.decimalPlaces"
      :min="0"
      :max="2147483647"
      :precision="0"
      controls-position="right"
      style="width: 100%"
    />
  </qt-form-item>
  <qt-form-item
    v-if="typeCode === 4"
    :tip="readonly ? undefined : '填写日期或时间的展示格式，例如 yyyy-MM-dd HH:mm:ss。'"
    label="格式"
    prop="format"
  >
    <div v-if="readonly" class="form-readonly">{{ model.format || "-" }}</div>
    <el-input
      v-else
      v-model="model.format"
      placeholder="请输入格式，如 yyyy-MM-dd HH:mm:ss"
      :maxlength="128"
    />
  </qt-form-item>
  <template v-if="typeCode === 5">
    <qt-form-item
      :tip="readonly ? undefined : '选择枚举类型来自当前项目还是全局，切换后会重新筛选可选类型。'"
      label="枚举来源"
      prop="enumSource"
    >
      <div v-if="readonly" class="form-readonly">{{ model.enumSource === "GLOBAL" ? "全局" : "当前项目" }}</div>
      <el-radio-group v-else v-model="model.enumSource" @change="handleEnumSourceChange">
        <el-radio value="PROJECT">当前项目</el-radio>
        <el-radio value="GLOBAL">全局</el-radio>
      </el-radio-group>
    </qt-form-item>
    <qt-form-item
      :tip="readonly ? undefined : '选择该属性取值所引用的枚举类型。'"
      label="枚举类型"
      prop="enumType"
    >
      <div v-if="readonly" class="form-readonly">{{ enumName }}</div>
      <el-select
        v-else
        v-model="model.enumType"
        filterable
        clearable
        style="width: 100%"
        placeholder="请选择枚举类型"
        :loading="enumLoading && !enumOptions.length"
        @visible-change="(value) => value && emit('enumOpen')"
      >
        <el-option
          v-for="item in filteredEnums"
          :key="item.enumSource + ':' + item.enumType"
          :label="item.enumName"
          :value="item.enumType"
        />
      </el-select>
    </qt-form-item>
  </template>
</template>

<script setup>
/* eslint vue/no-mutating-props: ["error", { "shallowOnly": true }] */
// model 是父级表单草稿：只更新字段，不替换父级传入的对象。
import { computed } from "vue";
const props = defineProps({
  model: { type: Object, required: true },
  readonly: { type: Boolean, default: false },
  enumOptions: { type: Array, default: () => [] },
  enumLoading: { type: Boolean, default: false },
});
const emit = defineEmits(["enumOpen"]);
const typeCode = computed(() =>
  props.model.basicTypeCode == null
    ? undefined
    : Number(props.model.basicTypeCode)
);
const filteredEnums = computed(() =>
  props.enumOptions.filter((item) => item.enumSource === (props.model.enumSource || "PROJECT")),
);
function handleEnumSourceChange() {
  props.model.enumType = undefined;
}
const enumName = computed(
  () =>
    props.enumOptions.find(
      (item) =>
        item.enumSource === (props.model.enumSource || "PROJECT") &&
        item.enumType === props.model.enumType
    )?.enumName ||
    props.model.enumType ||
    "-"
);
</script>

<style scoped lang="scss">
.enum-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
</style>
