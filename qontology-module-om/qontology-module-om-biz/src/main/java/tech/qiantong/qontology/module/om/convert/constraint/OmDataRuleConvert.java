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

package tech.qiantong.qontology.module.om.convert.constraint;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;

/**
 * 数据规则 Convert
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Mapper
public interface OmDataRuleConvert {
    OmDataRuleConvert INSTANCE = Mappers.getMapper(OmDataRuleConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param omDataRulePageReqVO 请求参数
     * @return OmDataRuleDO
     */
     OmDataRuleDO convertToDO(OmDataRulePageReqVO omDataRulePageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param omDataRuleSaveReqVO 保存请求参数
     * @return OmDataRuleDO
     */
     OmDataRuleDO convertToDO(OmDataRuleSaveReqVO omDataRuleSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param omDataRuleDO 实体对象
     * @return OmDataRuleRespVO
     */
     OmDataRuleRespVO convertToRespVO(OmDataRuleDO omDataRuleDO);

    /**
     * DOList 转换为 RespVOList
     * @param omDataRuleDOList 实体对象列表
     * @return List<OmDataRuleRespVO>
     */
     List<OmDataRuleRespVO> convertToRespVOList(List<OmDataRuleDO> omDataRuleDOList);
}
