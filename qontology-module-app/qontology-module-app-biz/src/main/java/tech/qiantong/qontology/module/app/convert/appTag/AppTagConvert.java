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
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;

import java.util.List;

/**
 * 知识标签 Convert
 *
 * @author qknow
 * @date 2025-11-13
 */
@Mapper
public interface AppTagConvert {
    AppTagConvert INSTANCE = Mappers.getMapper(AppTagConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param AppTagPageReqVO 请求参数
     * @return AppTagDO
     */
    AppTagDO convertToDO(AppTagPageReqVO AppTagPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param AppTagSaveReqVO 保存请求参数
     * @return AppTagDO
     */
    AppTagDO convertToDO(AppTagSaveReqVO AppTagSaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param AppTagDO 实体对象
     * @return AppTagRespVO
     */
    AppTagRespVO convertToRespVO(AppTagDO AppTagDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param AppTagDOList 实体对象列表
     * @return List<AppTagRespVO>
     */
    List<AppTagRespVO> convertToRespVOList(List<AppTagDO> AppTagDOList);
}
