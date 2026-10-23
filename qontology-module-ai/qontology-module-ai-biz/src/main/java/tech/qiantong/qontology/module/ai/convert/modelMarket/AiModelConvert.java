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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.ai.convert.modelMarket;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelPageReqVO;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelRespVO;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelSaveReqVO;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiModelDO;

/**
 * AI 模型 Convert
 *
 * @author qontology
 * @date 2025-12-23
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AiModelConvert {
    AiModelConvert INSTANCE = Mappers.getMapper(AiModelConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param aiModelPageReqVO 请求参数
     * @return AiModelDO
     */
     AiModelDO convertToDO(AiModelPageReqVO aiModelPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param aiModelSaveReqVO 保存请求参数
     * @return AiModelDO
     */
     AiModelDO convertToDO(AiModelSaveReqVO aiModelSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param aiModelDO 实体对象
     * @return AiModelRespVO
     */
     AiModelRespVO convertToRespVO(AiModelDO aiModelDO);

    /**
     * DOList 转换为 RespVOList
     * @param aiModelDOList 实体对象列表
     * @return List<AiModelRespVO>
     */
     List<AiModelRespVO> convertToRespVOList(List<AiModelDO> aiModelDOList);
}
