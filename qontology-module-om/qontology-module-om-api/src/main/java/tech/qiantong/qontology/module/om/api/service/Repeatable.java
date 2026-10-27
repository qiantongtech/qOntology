package tech.qiantong.qontology.module.om.api.service;

// 可复制的接口标识
public interface Repeatable {
    /**
     * 复制
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 是否复制成功
     */
    Boolean copy(Long sourceVersionId, Long targetVersionId);
}
