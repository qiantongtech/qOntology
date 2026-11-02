<!--
  QtFormItem 组件说明:
  基于 el-form-item 封装，用于表单字段，可在输入控件下方展示统一的提示文案。
  - tip: 传入字符串时，在内容区下方展示提示文本（超出部分省略，悬停可查看完整内容）
  - label、prop、class 等其余属性通过 $attrs 透传给 el-form-item
  组件已全局注册，页面中直接使用 <qt-form-item> 即可，无需 import。

  使用示例:
  1. 基础用法:
     <qt-form-item label="项目领域" prop="domain">
       <el-input v-model="form.domain" placeholder="请输入项目领域" />
     </qt-form-item>

  2. 带提示文案:
     <qt-form-item
       label="命名空间"
       prop="namespace"
       tip="命名空间用于唯一标识本体的术语来源，遵循 W3C 标准。例如：https://example.com"
     >
       <el-input v-model="form.namespace" placeholder="请输入命名空间" />
     </qt-form-item>

  3. 配合双列表单（.column-form）独占整行:
     <qt-form-item label="命名空间" prop="namespace" class="row-full" tip="...">
       <el-input v-model="form.namespace" />
     </qt-form-item>
-->

<template>
  <el-form-item v-bind="$attrs" :class="{ 'has-tip': props.tip !== undefined }">
    <template #default="scope">
      <div class="default-wrap">
        <slot name="default" v-bind="scope || {}" />
        <div class="tip-content" v-if="isString && props.tip !== undefined">
          <!-- <el-icon> <InfoFilled /> </el-icon> -->
          <span :title="props.tip" v-html="props.tip || '&nbsp;'"></span>
        </div>
      </div>
    </template>
  </el-form-item>
</template>

<script setup name="QtFromItem">
import { computed } from "vue";

const props = defineProps({
  tip: {
    type: [String, Object],
    default: undefined,
  },
});

const isString = computed(() => {
  return typeof props.tip === "string" || props.tip === null;
});
</script>

<style lang="scss" scoped>
:global(.column-form .el-form-item.has-tip) {
  margin-bottom: 10px !important;
}

.default-wrap {
  width: 100%;
  position: relative;

  .tip-content {
    position: absolute;
    top: 100%;
    left: 0;
    width: 100%;
    padding-top: 4px;
    display: flex;
    align-items: flex-start;
    gap: 4px;
    color: #888;
    font-size: 12px;
    font-weight: 400 !important;
    line-height: 1;
    letter-spacing: 0.2px;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    span {
      flex: 1;
      font-weight: 400 !important;
      color: inherit;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.label-wrap {
  display: flex;
  align-items: center;
  gap: 2px;
  .el-icon {
    color: #888;
  }
}

::v-deep(.el-form-item__error) {
  padding-top: 2px;
  top: 100%;
}

.el-form-item.has-tip {
  ::v-deep(.el-form-item__error) {
    position: absolute;
    top: 100%;
    bottom: auto;
    padding-top: 2px;
    line-height: 1;
    margin: 0;
    display: flex;
    align-items: flex-start;
    gap: 4px;
  }
}

.el-form-item.has-tip.is-error {
  .tip-content {
    display: none;
  }
}
</style>
