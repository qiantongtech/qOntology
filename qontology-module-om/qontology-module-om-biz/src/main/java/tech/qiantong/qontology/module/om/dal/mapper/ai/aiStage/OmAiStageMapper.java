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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiStage;

import java.util.Set;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo.OmAiStagePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI解析阶段记录 Mapper 接口。 */
public interface OmAiStageMapper extends BaseMapperX<OmAiStageDO> {
    /** 根据分页条件查询AI解析阶段记录。 */
    default PageResult<OmAiStageDO> selectPage(OmAiStagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiStageDO>()
                .eqIfPresent(OmAiStageDO::getId, reqVO.getId())
                .eqIfPresent(OmAiStageDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(OmAiStageDO::getStatus, reqVO.getStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
