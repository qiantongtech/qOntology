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

package tech.qiantong.qontology.module.app.convert.appTag;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagEntityDO;

import java.util.List;

/**
 * 实体标签 Convert
 *
 * @author qknow
 * @date 2025-11-13
 */
@Mapper
public interface AppTagEntityConvert {
    AppTagEntityConvert INSTANCE = Mappers.getMapper(AppTagEntityConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param AppTagEntityPageReqVO 请求参数
     * @return AppTagEntityDO
     */
    AppTagEntityDO convertToDO(AppTagEntityPageReqVO AppTagEntityPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param AppTagEntitySaveReqVO 保存请求参数
     * @return AppTagEntityDO
     */
    AppTagEntityDO convertToDO(AppTagEntitySaveReqVO AppTagEntitySaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param AppTagEntityDO 实体对象
     * @return AppTagEntityRespVO
     */
    AppTagEntityRespVO convertToRespVO(AppTagEntityDO AppTagEntityDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param AppTagEntityDOList 实体对象列表
     * @return List<AppTagEntityRespVO>
     */
    List<AppTagEntityRespVO> convertToRespVOList(List<AppTagEntityDO> AppTagEntityDOList);
}
