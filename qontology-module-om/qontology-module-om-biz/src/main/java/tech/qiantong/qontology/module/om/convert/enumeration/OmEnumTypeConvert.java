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

package tech.qiantong.qontology.module.om.convert.enumeration;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;

/**
 * 枚举类型 Convert
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Mapper
public interface OmEnumTypeConvert {
    /** MapStruct 对象转换器实例 */
    OmEnumTypeConvert INSTANCE = Mappers.getMapper(OmEnumTypeConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param omEnumTypePageReqVO 请求参数
     * @return OmEnumTypeDO
     */
    OmEnumTypeDO convertToDO(OmEnumTypePageReqVO omEnumTypePageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param omEnumTypeSaveReqVO 保存请求参数
     * @return OmEnumTypeDO
     */
    OmEnumTypeDO convertToDO(OmEnumTypeSaveReqVO omEnumTypeSaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param omEnumTypeDO 实体对象
     * @return OmEnumTypeRespVO
     */
    OmEnumTypeRespVO convertToRespVO(OmEnumTypeDO omEnumTypeDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param omEnumTypeDOList 实体对象列表
     * @return List<OmEnumTypeRespVO>
     */
    List<OmEnumTypeRespVO> convertToRespVOList(List<OmEnumTypeDO> omEnumTypeDOList);
}
