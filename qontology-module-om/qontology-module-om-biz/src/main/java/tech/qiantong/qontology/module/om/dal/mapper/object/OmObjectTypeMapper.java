/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.dal.mapper.object;

import java.util.Collection;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 对象类型Mapper接口
 *
 * @author qOntology
 * @date 2026-10-12
 */
public interface OmObjectTypeMapper extends BaseMapperX<OmObjectTypeDO> {
    /**
     * 按请求条件分页查询对象类型
     *
     * @param reqVO 分页查询条件
     * @return 分页查询结果
     */
    default PageResult<OmObjectTypeDO> selectPage(OmObjectTypePageReqVO reqVO) {
        return selectPageWithLinkedIds(reqVO, null);
    }

    /**
     * 结合已关联元素 ID 查询对象类型，保留版本、筛选和排序条件
     *
     * @param reqVO 分页查询条件
     * @param linkedIds 已关联的元素 ID 集合
     * @return 分页查询结果
     */
    default PageResult<OmObjectTypeDO> selectPageWithLinkedIds(
        OmObjectTypePageReqVO reqVO,
        Collection<Long> linkedIds
    ) {
        if (linkedIds != null && linkedIds.isEmpty()) return PageResult.empty();
        // 构造动态查询条件
        return selectPage(
            reqVO,
            new LambdaQueryWrapperX<OmObjectTypeDO>()
                .eqIfPresent(OmObjectTypeDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(OmObjectTypeDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmObjectTypeDO::getName, reqVO.getName())
                .eqIfPresent(OmObjectTypeDO::getDescription, reqVO.getDescription())
                .eqIfPresent(OmObjectTypeDO::getParentId, reqVO.getParentId())
                .eqIfPresent(OmObjectTypeDO::getAncestors, reqVO.getAncestors())
                .eqIfPresent(OmObjectTypeDO::getOrderNum, reqVO.getOrderNum())
                .eqIfPresent(OmObjectTypeDO::getCreateTime, reqVO.getCreateTime())
                .eq(OmObjectTypeDO::getValidFlag, true)
                .in(linkedIds != null, OmObjectTypeDO::getId, linkedIds)
                .orderByAsc(OmObjectTypeDO::getOrderNum)
                .orderByAsc(OmObjectTypeDO::getId)
        );
    }
}
