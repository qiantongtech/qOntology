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

package tech.qiantong.qontology.module.om.convert.object;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;

/**
 * 对象类型 Convert
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Mapper
public interface OmObjectTypeConvert {
    OmObjectTypeConvert INSTANCE = Mappers.getMapper(OmObjectTypeConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param omObjectTypePageReqVO 请求参数
     * @return OmObjectTypeDO
     */
     OmObjectTypeDO convertToDO(OmObjectTypePageReqVO omObjectTypePageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param omObjectTypeSaveReqVO 保存请求参数
     * @return OmObjectTypeDO
     */
     OmObjectTypeDO convertToDO(OmObjectTypeSaveReqVO omObjectTypeSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param omObjectTypeDO 实体对象
     * @return OmObjectTypeRespVO
     */
     OmObjectTypeRespVO convertToRespVO(OmObjectTypeDO omObjectTypeDO);

    /**
     * DOList 转换为 RespVOList
     * @param omObjectTypeDOList 实体对象列表
     * @return List<OmObjectTypeRespVO>
     */
     List<OmObjectTypeRespVO> convertToRespVOList(List<OmObjectTypeDO> omObjectTypeDOList);
}
