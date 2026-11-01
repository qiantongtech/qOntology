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

package tech.qiantong.qontology.module.app.convert.appEvent;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventDO;

import java.util.List;

/**
 * 事件管理 Convert
 *
 * @author qknow
 * @date 2025-11-11
 */
@Mapper
public interface AppEventConvert {
    AppEventConvert INSTANCE = Mappers.getMapper(AppEventConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param AppEventPageReqVO 请求参数
     * @return AppEventDO
     */
    AppEventDO convertToDO(AppEventPageReqVO AppEventPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param AppEventSaveReqVO 保存请求参数
     * @return AppEventDO
     */
    AppEventDO convertToDO(AppEventSaveReqVO AppEventSaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param AppEventDO 实体对象
     * @return AppEventRespVO
     */
    AppEventRespVO convertToRespVO(AppEventDO AppEventDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param AppEventDOList 实体对象列表
     * @return List<AppEventRespVO>
     */
    List<AppEventRespVO> convertToRespVOList(List<AppEventDO> AppEventDOList);
}
