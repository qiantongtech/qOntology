package tech.qiantong.qontology.module.om.api.service;

import java.util.Collection;
import java.util.Map;

/** 向应用模块提供映射对象类型的最新颜色，不暴露本体内部数据访问组件。 */
public interface IOmObjectTypeColorApiService {
    /**
     * 批量查询有效对象类型颜色。
     * @param ids 对象类型 ID 集合
     * @return ID 与颜色映射；无效、已删除或未配置颜色的类型不返回
     */
    Map<Long, String> getColors(Collection<Long> ids);
}
