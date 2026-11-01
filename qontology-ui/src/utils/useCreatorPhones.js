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
  return {creatorPhones};
}
