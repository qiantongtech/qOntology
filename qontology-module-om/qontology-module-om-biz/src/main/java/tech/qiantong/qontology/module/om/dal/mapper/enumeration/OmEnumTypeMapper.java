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

package tech.qiantong.qontology.module.om.dal.mapper.enumeration;

import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import java.util.Arrays;
import com.github.yulichang.base.MPJBaseMapper;
import tech.qiantong.qontology.core.page.PageResult;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypePageReqVO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 枚举类型Mapper接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface OmEnumTypeMapper extends BaseMapperX<OmEnumTypeDO> {

    default PageResult<OmEnumTypeDO> selectPage(OmEnumTypePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("enum_id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<OmEnumTypeDO>()
                .eqIfPresent(OmEnumTypeDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(OmEnumTypeDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmEnumTypeDO::getEnumName, reqVO.getEnumName())
                .eqIfPresent(OmEnumTypeDO::getEnumType, reqVO.getEnumType())
                .eqIfPresent(OmEnumTypeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OmEnumTypeDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(OmEnumTypeDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
