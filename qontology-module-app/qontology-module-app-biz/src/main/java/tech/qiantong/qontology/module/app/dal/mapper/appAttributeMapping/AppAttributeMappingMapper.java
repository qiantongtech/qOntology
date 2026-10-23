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

package tech.qiantong.qontology.module.app.dal.mapper.appAttributeMapping;

import tech.qiantong.qontology.module.app.dal.dataobject.appAttributeMapping.AppAttributeMappingDO;
import java.util.Arrays;
import com.github.yulichang.base.MPJBaseMapper;
import tech.qiantong.qontology.core.page.PageResult;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingPageReqVO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 属性映射Mapper接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface AppAttributeMappingMapper extends BaseMapperX<AppAttributeMappingDO> {

    /** 按查询条件分页获取映射或任务列表。 */
    default PageResult<AppAttributeMappingDO> selectPage(AppAttributeMappingPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppAttributeMappingDO>()
                .eqIfPresent(AppAttributeMappingDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppAttributeMappingDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(AppAttributeMappingDO::getTableName, reqVO.getTableName())
                .eqIfPresent(AppAttributeMappingDO::getTableComment, reqVO.getTableComment())
                .likeIfPresent(AppAttributeMappingDO::getFieldName, reqVO.getFieldName())
                .eqIfPresent(AppAttributeMappingDO::getFieldComment, reqVO.getFieldComment())
                .eqIfPresent(AppAttributeMappingDO::getAttributeId, reqVO.getAttributeId())
                .likeIfPresent(AppAttributeMappingDO::getAttributeName, reqVO.getAttributeName())
                .eqIfPresent(AppAttributeMappingDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppAttributeMappingDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}

