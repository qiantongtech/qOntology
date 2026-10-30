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

package tech.qiantong.qontology.module.om.dal.mapper.relation;

import java.util.Collection;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 对象类型关系 Mapper 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface OmObjectTypeRelationMapper extends BaseMapperX<OmObjectTypeRelationDO> {
    /**
     * 按请求条件分页查询对象关系
     *
     * @param request 业务请求参数
     * @return 分页查询结果
     */
    default PageResult<OmObjectTypeRelationDO> selectPage(OmObjectTypeRelationPageReqVO request) {
        return selectPageWithLinkedIds(request, null);
    }

    /**
     * 结合已关联元素 ID 查询对象关系，保留版本、筛选和排序条件
     *
     * @param request 业务请求参数
     * @param linkedIds 已关联的元素 ID 集合
     * @return 分页查询结果
     */
    default PageResult<OmObjectTypeRelationDO> selectPageWithLinkedIds(
        OmObjectTypeRelationPageReqVO request,
        Collection<Long> linkedIds
    ) {
        if (linkedIds != null && linkedIds.isEmpty()) return PageResult.empty();
        LambdaQueryWrapperX<OmObjectTypeRelationDO> query = new LambdaQueryWrapperX<OmObjectTypeRelationDO>()
            .eq(OmObjectTypeRelationDO::getWorkspaceId, request.getWorkspaceId())
            .eqIfPresent(OmObjectTypeRelationDO::getVersionId, request.getVersionId())
            .likeIfPresent(OmObjectTypeRelationDO::getName, request.getName())
            .eqIfPresent(OmObjectTypeRelationDO::getReversibleFlag, request.getReversibleFlag())
            .eq(OmObjectTypeRelationDO::getValidFlag, true);
        query.in(linkedIds != null, OmObjectTypeRelationDO::getId, linkedIds);
        boolean ascending = !"desc".equalsIgnoreCase(request.getIsAsc());
        if ("id".equals(request.getOrderByColumn())) {
            query.orderBy(true, ascending, OmObjectTypeRelationDO::getId);
        } else if (
            "createTime".equals(request.getOrderByColumn()) || "create_time".equals(request.getOrderByColumn())
        ) {
            query
                .orderBy(true, ascending, OmObjectTypeRelationDO::getCreateTime)
                .orderByDesc(OmObjectTypeRelationDO::getId);
        } else {
            query.orderByDesc(OmObjectTypeRelationDO::getCreateTime).orderByDesc(OmObjectTypeRelationDO::getId);
        }
        return selectPage(request, query);
    }
}
