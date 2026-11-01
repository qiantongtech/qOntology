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

package tech.qiantong.qontology.module.om.convert.ai.aiAttribute;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAttribute.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;

/** AI候选对象类型属性对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OmAiAttributeConvert {
    OmAiAttributeConvert INSTANCE = Mappers.getMapper(OmAiAttributeConvert.class);
    OmAiAttributeDO convertToDO(OmAiAttributeSaveReqVO bean);
    OmAiAttributeRespVO convertToRespVO(OmAiAttributeDO bean);
    List<OmAiAttributeRespVO> convertToRespVOList(List<OmAiAttributeDO> list);
}
