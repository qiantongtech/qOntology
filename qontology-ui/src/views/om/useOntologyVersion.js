import {computed} from 'vue';
import {useRoute} from 'vue-router';
import {ElMessage} from 'element-plus';

// 路径中的项目版本为当前页面的数据范围，不使用工作区 ID 代替版本。
export function useOntologyVersion(providedVersion = () => undefined) {
  const route = useRoute();
  const versionId = computed(() => {
    const value = route.params.versionId ?? providedVersion() ?? route.query.versionId;
    return typeof value !== 'object' && /^[1-9]\d*$/.test(String(value)) ? String(value) : undefined;
  });
  function requireVersion() {
    if (versionId.value) return true;
    ElMessage.warning('请先选择项目版本，再打开此页面');
    return false;
  }
  return {versionId, requireVersion};
}
