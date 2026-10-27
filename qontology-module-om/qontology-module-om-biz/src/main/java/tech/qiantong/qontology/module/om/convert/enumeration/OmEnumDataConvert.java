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
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;

/**
 * 枚举数据 Convert
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Mapper
public interface OmEnumDataConvert {
    /** MapStruct 对象转换器实例 */
    OmEnumDataConvert INSTANCE = Mappers.getMapper(OmEnumDataConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param omEnumDataPageReqVO 请求参数
     * @return OmEnumDataDO
     */
    OmEnumDataDO convertToDO(OmEnumDataPageReqVO omEnumDataPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param omEnumDataSaveReqVO 保存请求参数
     * @return OmEnumDataDO
     */
    OmEnumDataDO convertToDO(OmEnumDataSaveReqVO omEnumDataSaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param omEnumDataDO 实体对象
     * @return OmEnumDataRespVO
     */
    OmEnumDataRespVO convertToRespVO(OmEnumDataDO omEnumDataDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param omEnumDataDOList 实体对象列表
     * @return List<OmEnumDataRespVO>
     */
    List<OmEnumDataRespVO> convertToRespVOList(List<OmEnumDataDO> omEnumDataDOList);
}
