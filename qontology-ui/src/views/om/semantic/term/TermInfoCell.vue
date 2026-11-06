<template>
  <div class="qt-table-info">
    <div class="qt-table-info__main">
      <span class="info-title" :title="title">{{ title }}</span>
    </div>
    <div class="info-desc" :title="row.description">{{ row.description || "-" }}</div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  row: { type: Object, required: true },
});
const title = computed(() => {
  const { stdName, enName } = props.row;
  return enName ? `${stdName || "-"}（${enName}）` : stdName || "-";
});
</script>
