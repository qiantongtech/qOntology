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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiAdoptLog;

import java.util.Set;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAdoptLog.vo.OmAiAdoptLogPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAdoptLog.OmAiAdoptLogDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选采纳日志 Mapper 接口。 */
public interface OmAiAdoptLogMapper extends BaseMapperX<OmAiAdoptLogDO> {
    /** 根据分页条件查询AI候选采纳日志。 */
    default PageResult<OmAiAdoptLogDO> selectPage(OmAiAdoptLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiAdoptLogDO>()
                .eqIfPresent(OmAiAdoptLogDO::getId, reqVO.getId())
                .eqIfPresent(OmAiAdoptLogDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(OmAiAdoptLogDO::getDataType, reqVO.getDataType())
                .eqIfPresent(OmAiAdoptLogDO::getStatus, reqVO.getStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
