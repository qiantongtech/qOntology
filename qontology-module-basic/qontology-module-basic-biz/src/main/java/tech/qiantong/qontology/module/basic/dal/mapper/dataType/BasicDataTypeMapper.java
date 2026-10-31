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

package tech.qiantong.qontology.module.basic.dal.mapper.dataType;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.dataType.BasicDataTypeDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据类型Mapper接口
 *
 * @author qOntology
 * @date 2026-10-16
 */
public interface BasicDataTypeMapper extends BaseMapperX<BasicDataTypeDO> {
    /** 查询可供 AI 建模使用的全局业务数据类型。 */
    List<Map<String, Object>> selectAiOptions();

    /** 查询全局业务数据类型对应的基础类型编码。 */
    Integer selectBasicTypeCode(Long businessTypeId);


    default PageResult<BasicDataTypeDO> selectPage(BasicDataTypePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<BasicDataTypeDO>()
                .likeIfPresent(BasicDataTypeDO::getName, reqVO.getName())
                .eqIfPresent(BasicDataTypeDO::getDataType, reqVO.getDataType())
                .eqIfPresent(BasicDataTypeDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BasicDataTypeDO::getUnit, reqVO.getUnit())
                .eqIfPresent(BasicDataTypeDO::getDecimalPlaces, reqVO.getDecimalPlaces())
                .eqIfPresent(BasicDataTypeDO::getFormat, reqVO.getFormat())
                .eqIfPresent(BasicDataTypeDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(BasicDataTypeDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
