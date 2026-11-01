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
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;

import java.util.List;

/**
 * 事件实体关联 Convert
 *
 * @author qknow
 * @date 2025-11-11
 */
@Mapper
public interface AppEventEntityConvert {
    AppEventEntityConvert INSTANCE = Mappers.getMapper(AppEventEntityConvert.class);

    /**
     * PageReqVO 转换为 DO
     *
     * @param AppEventEntityPageReqVO 请求参数
     * @return AppEventEntityDO
     */
    AppEventEntityDO convertToDO(AppEventEntityPageReqVO AppEventEntityPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     *
     * @param AppEventEntitySaveReqVO 保存请求参数
     * @return AppEventEntityDO
     */
    AppEventEntityDO convertToDO(AppEventEntitySaveReqVO AppEventEntitySaveReqVO);

    /**
     * DO 转换为 RespVO
     *
     * @param AppEventEntityDO 实体对象
     * @return AppEventEntityRespVO
     */
    AppEventEntityRespVO convertToRespVO(AppEventEntityDO AppEventEntityDO);

    /**
     * DOList 转换为 RespVOList
     *
     * @param AppEventEntityDOList 实体对象列表
     * @return List<AppEventEntityRespVO>
     */
    List<AppEventEntityRespVO> convertToRespVOList(List<AppEventEntityDO> AppEventEntityDOList);
}
