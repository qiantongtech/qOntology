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
  <el-form-item v-if="[1, 2].includes(typeCode)" label="单位" prop="unit">
    <div v-if="readonly" class="form-readonly">{{ model.unit || "-" }}</div>
    <el-input
      v-else
      v-model="model.unit"
      placeholder="请输入单位"
      :maxlength="32"
    />
  </el-form-item>
  <el-form-item v-if="typeCode === 2" label="小数位数" prop="decimalPlaces">
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
  </el-form-item>
  <el-form-item v-if="typeCode === 4" label="格式" prop="format">
    <div v-if="readonly" class="form-readonly">{{ model.format || "-" }}</div>
    <el-input
      v-else
      v-model="model.format"
      placeholder="请输入格式，如 yyyy-MM-dd HH:mm:ss"
      :maxlength="128"
    />
  </el-form-item>
  <template v-if="typeCode === 5">
    <el-form-item label="枚举来源" prop="enumSource">
      <div v-if="readonly" class="form-readonly">{{ model.enumSource === "GLOBAL" ? "全局" : "当前项目" }}</div>
      <el-radio-group v-else v-model="model.enumSource" @change="handleEnumSourceChange">
        <el-radio value="PROJECT">当前项目</el-radio>
        <el-radio value="GLOBAL">全局</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="枚举类型" prop="enumType">
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
          :label="item.enumName + ' ' + item.enumType"
          :value="item.enumType"
        >
          <span class="enum-option"><span>{{ item.enumName }}</span><small>{{ item.enumType }}</small></span>
        </el-option>
      </el-select>
    </el-form-item>
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
.enum-option {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}
.enum-option small {
  color: var(--el-text-color-secondary);
}
</style>
