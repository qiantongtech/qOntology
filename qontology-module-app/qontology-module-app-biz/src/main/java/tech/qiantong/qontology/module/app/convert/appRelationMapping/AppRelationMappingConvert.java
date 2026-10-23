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

package tech.qiantong.qontology.module.app.convert.appRelationMapping;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;

/**
 * 关系映射 Convert
 *
 * @author qknow
 * @date 2025-02-25
 */
@Mapper
public interface AppRelationMappingConvert {
    AppRelationMappingConvert INSTANCE = Mappers.getMapper(AppRelationMappingConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param appRelationMappingPageReqVO 请求参数
     * @return AppRelationMappingDO
     */
     AppRelationMappingDO convertToDO(AppRelationMappingPageReqVO appRelationMappingPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param appRelationMappingSaveReqVO 保存请求参数
     * @return AppRelationMappingDO
     */
     AppRelationMappingDO convertToDO(AppRelationMappingSaveReqVO appRelationMappingSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param appRelationMappingDO 实体对象
     * @return AppRelationMappingRespVO
     */
     AppRelationMappingRespVO convertToRespVO(AppRelationMappingDO appRelationMappingDO);

    /**
     * DOList 转换为 RespVOList
     * @param appRelationMappingDOList 实体对象列表
     * @return List<AppRelationMappingRespVO>
     */
     List<AppRelationMappingRespVO> convertToRespVOList(List<AppRelationMappingDO> appRelationMappingDOList);
}

