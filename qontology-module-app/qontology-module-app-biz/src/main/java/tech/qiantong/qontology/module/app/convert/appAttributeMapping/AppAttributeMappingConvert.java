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

package tech.qiantong.qontology.module.app.convert.appAttributeMapping;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appAttributeMapping.AppAttributeMappingDO;

/**
 * 属性映射 Convert
 *
 * @author qknow
 * @date 2025-02-25
 */
@Mapper
public interface AppAttributeMappingConvert {
    AppAttributeMappingConvert INSTANCE = Mappers.getMapper(AppAttributeMappingConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param appAttributeMappingPageReqVO 请求参数
     * @return AppAttributeMappingDO
     */
     AppAttributeMappingDO convertToDO(AppAttributeMappingPageReqVO appAttributeMappingPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param appAttributeMappingSaveReqVO 保存请求参数
     * @return AppAttributeMappingDO
     */
     AppAttributeMappingDO convertToDO(AppAttributeMappingSaveReqVO appAttributeMappingSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param appAttributeMappingDO 实体对象
     * @return AppAttributeMappingRespVO
     */
     AppAttributeMappingRespVO convertToRespVO(AppAttributeMappingDO appAttributeMappingDO);

    /**
     * DOList 转换为 RespVOList
     * @param appAttributeMappingDOList 实体对象列表
     * @return List<AppAttributeMappingRespVO>
     */
     List<AppAttributeMappingRespVO> convertToRespVOList(List<AppAttributeMappingDO> appAttributeMappingDOList);
}

