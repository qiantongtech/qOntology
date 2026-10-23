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

package tech.qiantong.qontology.module.app.dal.mapper.appSchemaMapping;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;
import java.util.Arrays;
import com.github.yulichang.base.MPJBaseMapper;
import tech.qiantong.qontology.core.page.PageResult;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 概念映射Mapper接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface AppSchemaMappingMapper extends BaseMapperX<AppSchemaMappingDO> {

    /** 按查询条件分页获取映射或任务列表。 */
    default PageResult<AppSchemaMappingDO> selectPage(AppSchemaMappingPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppSchemaMappingDO>()
                .eqIfPresent(AppSchemaMappingDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppSchemaMappingDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(AppSchemaMappingDO::getTableName, reqVO.getTableName())
                .eqIfPresent(AppSchemaMappingDO::getTableComment, reqVO.getTableComment())
                .eqIfPresent(AppSchemaMappingDO::getSchemaId, reqVO.getSchemaId())
                .likeIfPresent(AppSchemaMappingDO::getSchemaName, reqVO.getSchemaName())
                .eqIfPresent(AppSchemaMappingDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppSchemaMappingDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}

