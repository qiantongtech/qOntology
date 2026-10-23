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

package tech.qiantong.qontology.module.om.convert.term;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermRespVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;

/**
 * 术语 Convert
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Mapper
public interface OmTermConvert {
    OmTermConvert INSTANCE = Mappers.getMapper(OmTermConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param omTermPageReqVO 请求参数
     * @return OmTermDO
     */
     OmTermDO convertToDO(OmTermPageReqVO omTermPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param omTermSaveReqVO 保存请求参数
     * @return OmTermDO
     */
     OmTermDO convertToDO(OmTermSaveReqVO omTermSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param omTermDO 实体对象
     * @return OmTermRespVO
     */
     OmTermRespVO convertToRespVO(OmTermDO omTermDO);

    /**
     * DOList 转换为 RespVOList
     * @param omTermDOList 实体对象列表
     * @return List<OmTermRespVO>
     */
     List<OmTermRespVO> convertToRespVOList(List<OmTermDO> omTermDOList);
}
