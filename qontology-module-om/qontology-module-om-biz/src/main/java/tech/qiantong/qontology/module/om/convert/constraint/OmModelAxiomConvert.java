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
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;

/**
 * 模型公理 Convert
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Mapper
public interface OmModelAxiomConvert {
    OmModelAxiomConvert INSTANCE = Mappers.getMapper(OmModelAxiomConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param omModelAxiomPageReqVO 请求参数
     * @return OmModelAxiomDO
     */
     OmModelAxiomDO convertToDO(OmModelAxiomPageReqVO omModelAxiomPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param omModelAxiomSaveReqVO 保存请求参数
     * @return OmModelAxiomDO
     */
     OmModelAxiomDO convertToDO(OmModelAxiomSaveReqVO omModelAxiomSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param omModelAxiomDO 实体对象
     * @return OmModelAxiomRespVO
     */
     OmModelAxiomRespVO convertToRespVO(OmModelAxiomDO omModelAxiomDO);

    /**
     * DOList 转换为 RespVOList
     * @param omModelAxiomDOList 实体对象列表
     * @return List<OmModelAxiomRespVO>
     */
     List<OmModelAxiomRespVO> convertToRespVOList(List<OmModelAxiomDO> omModelAxiomDOList);
}
