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

package tech.qiantong.qontology.module.app.convert.appRelationMappingMiddle;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddlePageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;

import java.util.List;

/**
 * 关系映射中间 Convert
 *
 * @author qknow
 * @date 2025-12-16
 */
@Mapper
public interface AppRelationMappingMiddleConvert {
    AppRelationMappingMiddleConvert INSTANCE = Mappers.getMapper(AppRelationMappingMiddleConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param appRelationMappingMiddlePageReqVO 请求参数
     * @return AppRelationMappingMiddleDO
     */
     AppRelationMappingMiddleDO convertToDO(AppRelationMappingMiddlePageReqVO appRelationMappingMiddlePageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param appRelationMappingMiddleSaveReqVO 保存请求参数
     * @return AppRelationMappingMiddleDO
     */
     AppRelationMappingMiddleDO convertToDO(AppRelationMappingMiddleSaveReqVO appRelationMappingMiddleSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param appRelationMappingMiddleDO 实体对象
     * @return AppRelationMappingMiddleRespVO
     */
     AppRelationMappingMiddleRespVO convertToRespVO(AppRelationMappingMiddleDO appRelationMappingMiddleDO);

    /**
     * DOList 转换为 RespVOList
     * @param appRelationMappingMiddleDOList 实体对象列表
     * @return List<AppRelationMappingMiddleRespVO>
     */
     List<AppRelationMappingMiddleRespVO> convertToRespVOList(List<AppRelationMappingMiddleDO> appRelationMappingMiddleDOList);
}

