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

package tech.qiantong.qontology.module.basic.dal.mapper.project;

import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionPageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;
import tech.qiantong.qontology.module.basic.dto.OmTargetCountDTO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 项目版本Mapper接口
 *
 * @author qOntology
 * @date 2026-10-12
 */
public interface BasicProjectVersionMapper extends BaseMapperX<BasicProjectVersionDO> {

    default PageResult<BasicProjectVersionDO> selectPage(BasicProjectVersionPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<BasicProjectVersionDO>()
                .eqIfPresent(BasicProjectVersionDO::getProjectId, reqVO.getProjectId())
                .likeIfPresent(BasicProjectVersionDO::getVersion, reqVO.getVersion())
                .eqIfPresent(BasicProjectVersionDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BasicProjectVersionDO::getBaseVersion, reqVO.getBaseVersion())
                .eqIfPresent(BasicProjectVersionDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(BasicProjectVersionDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }

    /**
     * 根据版本ID获取目标数量
     *
     * @param versionId 版本ID
     * @return 目标数量
     */
    OmTargetCountDTO getOmTargetCount(@Param("versionId") Long versionId);

    /**
     * 根据版本ID获取术语数量
     *
     * @param versionId 版本ID
     * @return 术语数量
     */
    Integer selectTermCount(@Param("versionId") Long versionId);

    /**
     * 根据版本ID获取枚举数量
     *
     * @param versionId 版本ID
     * @return 枚举数量
     */
    Integer selectEnumerationCount(@Param("versionId") Long versionId);

    /**
     * 根据版本ID获取对象类型数量
     *
     * @param versionId 版本ID
     * @return 对象类型数量
     */
    Integer selectObjectTypeCount(@Param("versionId") Long versionId);

    /**
     * 根据版本ID获取对象类型属性数量
     *
     * @param versionId 版本ID
     * @return 对象类型属性数量
     */
    Integer selectObjectTypeAttributeCount(@Param("versionId") Long versionId);

    /**
     * 根据版本ID获取关系数量
     *
     * @param versionId 版本ID
     * @return 关系数量
     */
    Integer selectRelationCount(@Param("versionId") Long versionId);
}
