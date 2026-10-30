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

package tech.qiantong.qontology.module.om.convert.ai.aiStage;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;

/** AI解析阶段记录对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OmAiStageConvert {
    OmAiStageConvert INSTANCE = Mappers.getMapper(OmAiStageConvert.class);
    OmAiStageDO convertToDO(OmAiStageSaveReqVO bean);
    OmAiStageRespVO convertToRespVO(OmAiStageDO bean);
    List<OmAiStageRespVO> convertToRespVOList(List<OmAiStageDO> list);
}
