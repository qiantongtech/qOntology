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

package tech.qiantong.qontology.module.app.convert.appStructTask;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTaskDO;

/**
 * 结构化抽取任务 Convert
 *
 * @author qknow
 * @date 2025-02-25
 */
@Mapper
public interface AppStructTaskConvert {
    AppStructTaskConvert INSTANCE = Mappers.getMapper(AppStructTaskConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param appStructTaskPageReqVO 请求参数
     * @return AppStructTaskDO
     */
     AppStructTaskDO convertToDO(AppStructTaskPageReqVO appStructTaskPageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param appStructTaskSaveReqVO 保存请求参数
     * @return AppStructTaskDO
     */
     AppStructTaskDO convertToDO(AppStructTaskSaveReqVO appStructTaskSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param appStructTaskDO 实体对象
     * @return AppStructTaskRespVO
     */
     AppStructTaskRespVO convertToRespVO(AppStructTaskDO appStructTaskDO);

    /**
     * DOList 转换为 RespVOList
     * @param appStructTaskDOList 实体对象列表
     * @return List<AppStructTaskRespVO>
     */
     List<AppStructTaskRespVO> convertToRespVOList(List<AppStructTaskDO> appStructTaskDOList);
}

