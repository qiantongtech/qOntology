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

package tech.qiantong.qontology.module.om.dal.mapper.dataType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 数据类型Mapper接口
 *
 * @author qOntology
 * @date 2026-10-16
 */
public interface OmDataTypeMapper extends BaseMapperX<OmDataTypeDO> {
    /** 查询当前版本可供 AI 建模使用的业务数据类型。 */
    List<Map<String, Object>> selectAiOptions(Long versionId);

    /** 根据 AI 建模任务查询项目业务数据类型。 */
    List<Map<String, Object>> selectAiOptionsByTaskId(Long taskId);

    /** 查询项目业务数据类型对应的基础类型编码。 */
    Integer selectBasicTypeCode(@Param("taskId") Long taskId,
                                @Param("businessTypeId") Long businessTypeId);

    /** 根据编号查询项目业务数据类型对应的基础类型编码。 */
    Integer selectBasicTypeCodeById(Long businessTypeId);

    /**
     * 按请求条件分页查询业务数据类型
     *
     * @param reqVO 分页查询条件
     * @return 分页查询结果
     */
    default PageResult<OmDataTypeDO> selectPage(OmDataTypePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));
        // 构造动态查询条件
        return selectPage(
            reqVO,
            new LambdaQueryWrapperX<OmDataTypeDO>()
                .eq(OmDataTypeDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmDataTypeDO::getName, reqVO.getName())
                .eqIfPresent(OmDataTypeDO::getDataType, reqVO.getDataType())
                .eqIfPresent(OmDataTypeDO::getDescription, reqVO.getDescription())
                .eqIfPresent(OmDataTypeDO::getUnit, reqVO.getUnit())
                .eqIfPresent(OmDataTypeDO::getDecimalPlaces, reqVO.getDecimalPlaces())
                .eqIfPresent(OmDataTypeDO::getFormat, reqVO.getFormat())
                .eqIfPresent(OmDataTypeDO::getCreateTime, reqVO.getCreateTime())
                .likeIfPresent(OmDataTypeDO::getName, reqVO.getName())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns)
        );
    }
}
