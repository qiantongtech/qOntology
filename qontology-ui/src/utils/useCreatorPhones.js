import {onBeforeUnmount, onMounted, ref} from 'vue';
import {listUserAll} from '@/api/system/system/user';

/**
 * 在页面 setup 中复用创建人电话查询，返回按用户编号索引的响应式电话表。
 * 每个页面挂载时查询一次，翻页与筛选复用结果；页面销毁后忽略迟到响应。
 */
export function useCreatorPhones() {
  const creatorPhones = ref({});
  let disposed = false;

  onMounted(async () => {
    try {
      const response = await listUserAll();
      if (disposed) return;
      const users = response.rows || response.data?.rows || [];
      // 统一为字符串键，使列表中的数值或字符串创建人编号都能匹配到电话。
      creatorPhones.value = Object.fromEntries(users.filter(user => user.userId != null)
        .map(user => [String(user.userId), user.phonenumber || '']));
    } catch {
      // 请求层统一提示错误，电话加载失败时各页面仍可正常展示创建人姓名。
      if (!disposed) creatorPhones.value = {};
    }
  });

  onBeforeUnmount(() => { disposed = true; });

  /**
   * 拼接详情页“姓名/电话”单行展示文本；电话缺失时仅展示姓名，姓名也缺失时回退为 '-'。
   * @param {Object} record 含 createBy 与 creatorId 的详情记录
   * @returns {string} 创建人展示文本
   */
  function formatCreator(record) {
    const name = record?.createBy || '';
    const phone = record?.creatorId == null ? '' : creatorPhones.value[String(record.creatorId)] || '';
    if (!name) return phone || '-';
    return phone ? `${name}/${phone}` : name;
  }

  return {creatorPhones, formatCreator};
}
