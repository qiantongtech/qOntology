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

package tech.qiantong.qontology.module.om.dal.mapper.constraint;

import com.github.yulichang.base.MPJBaseMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 模型公理Mapper接口
 *
 * @author qOntology
 * @date 2026-10-28
 */
public interface OmModelAxiomMapper extends BaseMapperX<OmModelAxiomDO> {
    /**
     * 按筛选条件分页查询记录
     *
     * @param reqVO 分页查询条件
     * @return 包含记录列表和总数的分页结果
     */
    default PageResult<OmModelAxiomDO> selectPage(OmModelAxiomPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(
            reqVO,
            new LambdaQueryWrapperX<OmModelAxiomDO>()
                .eqIfPresent(OmModelAxiomDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmModelAxiomDO::getName, reqVO.getName())
                .eqIfPresent(OmModelAxiomDO::getDescription, reqVO.getDescription())
                .eqIfPresent(OmModelAxiomDO::getType, reqVO.getType())
                .eqIfPresent(OmModelAxiomDO::getLevel, reqVO.getLevel())
                .eqIfPresent(OmModelAxiomDO::getObjectTypeId, reqVO.getObjectTypeId())
                .eqIfPresent(OmModelAxiomDO::getOtherTypeId, reqVO.getOtherTypeId())
                .eqIfPresent(OmModelAxiomDO::getRelationId, reqVO.getRelationId())
                .eqIfPresent(OmModelAxiomDO::getDomainId, reqVO.getDomainId())
                .eqIfPresent(OmModelAxiomDO::getRangeId, reqVO.getRangeId())
                .eqIfPresent(OmModelAxiomDO::getAttributeId, reqVO.getAttributeId())
                .eqIfPresent(OmModelAxiomDO::getTypeCategory, reqVO.getTypeCategory())
                .eqIfPresent(OmModelAxiomDO::getBasicTypeCode, reqVO.getBasicTypeCode())
                .eqIfPresent(OmModelAxiomDO::getBusinessTypeId, reqVO.getBusinessTypeId())
                .eqIfPresent(OmModelAxiomDO::getBusinessTypeSource, reqVO.getBusinessTypeSource())
                .eqIfPresent(OmModelAxiomDO::getUnit, reqVO.getUnit())
                .eqIfPresent(OmModelAxiomDO::getDecimalPlaces, reqVO.getDecimalPlaces())
                .eqIfPresent(OmModelAxiomDO::getFormat, reqVO.getFormat())
                .eqIfPresent(OmModelAxiomDO::getEnumType, reqVO.getEnumType())
                .eqIfPresent(OmModelAxiomDO::getEnumSource, reqVO.getEnumSource())
                .eqIfPresent(OmModelAxiomDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(OmModelAxiomDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns)
        );
    }
}
