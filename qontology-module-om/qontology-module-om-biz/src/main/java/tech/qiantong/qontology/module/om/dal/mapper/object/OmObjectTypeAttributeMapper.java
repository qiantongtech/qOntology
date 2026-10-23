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

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 对象类型属性 Mapper 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface OmObjectTypeAttributeMapper extends BaseMapperX<OmObjectTypeAttributeDO> {
    default PageResult<OmObjectTypeAttributeDO> selectPage(OmObjectTypeAttributePageReqVO request) {
        LambdaQueryWrapperX<OmObjectTypeAttributeDO> query = new LambdaQueryWrapperX<OmObjectTypeAttributeDO>()
                .eq(OmObjectTypeAttributeDO::getWorkspaceId, request.getWorkspaceId())
                .eq(OmObjectTypeAttributeDO::getObjectTypeId, request.getObjectTypeId())
                .likeIfPresent(OmObjectTypeAttributeDO::getName, request.getName());
        boolean ascending = !"desc".equalsIgnoreCase(request.getIsAsc());
        // 仅允许按编号或创建时间排序；同一创建时间以编号保证分页顺序稳定。
        if ("createTime".equals(request.getOrderByColumn()) || "create_time".equals(request.getOrderByColumn())) {
            query.orderBy(true, ascending, OmObjectTypeAttributeDO::getCreateTime)
                    .orderByDesc(OmObjectTypeAttributeDO::getId);
        } else if ("id".equals(request.getOrderByColumn())) {
            query.orderBy(true, ascending, OmObjectTypeAttributeDO::getId);
        } else {
            query.orderByDesc(OmObjectTypeAttributeDO::getCreateTime)
                    .orderByDesc(OmObjectTypeAttributeDO::getId);
        }
        if (request.getVersionId() != null) {
            query.eq(OmObjectTypeAttributeDO::getVersionId, request.getVersionId());
        }
        return selectPage(request, query);
    }
}
