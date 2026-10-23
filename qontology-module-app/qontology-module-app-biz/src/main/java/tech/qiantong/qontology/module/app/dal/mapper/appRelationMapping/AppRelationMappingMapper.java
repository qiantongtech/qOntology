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

package tech.qiantong.qontology.module.app.dal.mapper.appRelationMapping;

import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;
import java.util.Arrays;
import com.github.yulichang.base.MPJBaseMapper;
import tech.qiantong.qontology.core.page.PageResult;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 关系映射Mapper接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface AppRelationMappingMapper extends BaseMapperX<AppRelationMappingDO> {

    /** 按查询条件分页获取映射或任务列表。 */
    default PageResult<AppRelationMappingDO> selectPage(AppRelationMappingPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppRelationMappingDO>()
                .eqIfPresent(AppRelationMappingDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppRelationMappingDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(AppRelationMappingDO::getTableName, reqVO.getTableName())
                .eqIfPresent(AppRelationMappingDO::getTableComment, reqVO.getTableComment())
                .likeIfPresent(AppRelationMappingDO::getFieldName, reqVO.getFieldName())
                .eqIfPresent(AppRelationMappingDO::getFieldComment, reqVO.getFieldComment())
                .eqIfPresent(AppRelationMappingDO::getRelation, reqVO.getRelation())
                .eqIfPresent(AppRelationMappingDO::getRelationTable, reqVO.getRelationTable())
                .eqIfPresent(AppRelationMappingDO::getRelationField, reqVO.getRelationField())
                .eqIfPresent(AppRelationMappingDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppRelationMappingDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}

