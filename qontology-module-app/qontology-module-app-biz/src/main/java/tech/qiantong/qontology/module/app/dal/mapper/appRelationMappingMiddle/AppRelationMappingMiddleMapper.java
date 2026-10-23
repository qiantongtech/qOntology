/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.dal.mapper.appRelationMappingMiddle;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddlePageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 关系映射中间Mapper接口
 *
 * @author qknow
 * @date 2025-12-16
 */
public interface AppRelationMappingMiddleMapper extends BaseMapperX<AppRelationMappingMiddleDO> {

    /** 按查询条件分页获取映射或任务列表。 */
    default PageResult<AppRelationMappingMiddleDO> selectPage(AppRelationMappingMiddlePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppRelationMappingMiddleDO>()
                .eqIfPresent(AppRelationMappingMiddleDO::getRelationId, reqVO.getRelationId())
                .likeIfPresent(AppRelationMappingMiddleDO::getTableName, reqVO.getTableName())
                .eqIfPresent(AppRelationMappingMiddleDO::getTableField, reqVO.getTableField())
                .eqIfPresent(AppRelationMappingMiddleDO::getRelationField, reqVO.getRelationField())
                .eqIfPresent(AppRelationMappingMiddleDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppRelationMappingMiddleDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}

