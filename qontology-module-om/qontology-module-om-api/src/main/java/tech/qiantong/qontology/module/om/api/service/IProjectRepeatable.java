package tech.qiantong.qontology.module.om.api.service;

import tech.qiantong.qontology.core.domain.model.LoginUser;

// 可复制的接口标识
public interface IProjectRepeatable {
    /**
     * 复制
     *
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @param currentUser     当前用户
     * @return 是否复制成功
     */
    Boolean copy(Long sourceVersionId, Long targetVersionId, LoginUser currentUser);

}
