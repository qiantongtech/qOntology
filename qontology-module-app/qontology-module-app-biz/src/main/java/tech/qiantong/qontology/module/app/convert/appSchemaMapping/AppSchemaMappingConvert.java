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

package tech.qiantong.qontology.module.app.convert.appSchemaMapping;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;

/**
 * 概念映射 Convert
 *
 * @author qknow
 * @date 2025-02-25
 */
@Mapper
public interface AppSchemaMappingConvert {
    AppSchemaMappingConvert INSTANCE = Mappers.getMapper(AppSchemaMappingConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param appSchemaMappingPageReqVO 请求参数
     * @return AppSchemaMappingDO
     */
     AppSchemaMappingDO convertToDO(AppSchemaMappingPageReqVO appSchemaMappingPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param appSchemaMappingSaveReqVO 保存请求参数
     * @return AppSchemaMappingDO
     */
     AppSchemaMappingDO convertToDO(AppSchemaMappingSaveReqVO appSchemaMappingSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param appSchemaMappingDO 实体对象
     * @return AppSchemaMappingRespVO
     */
     AppSchemaMappingRespVO convertToRespVO(AppSchemaMappingDO appSchemaMappingDO);

    /**
     * DOList 转换为 RespVOList
     * @param appSchemaMappingDOList 实体对象列表
     * @return List<AppSchemaMappingRespVO>
     */
     List<AppSchemaMappingRespVO> convertToRespVOList(List<AppSchemaMappingDO> appSchemaMappingDOList);
}

