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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAttribute.vo.OmAiAttributePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选对象类型属性 Mapper 接口。 */
public interface OmAiAttributeMapper extends BaseMapperX<OmAiAttributeDO> {
    /** 查询任务下的候选属性索引。 */
    List<Map<String, Object>> selectCandidateIndex(Long taskId);

    /** 查询指定对象类型下的候选属性。 */
    List<Map<String, Object>> selectByObjectCandidate(@Param("taskId") Long taskId,
                                                      @Param("objectCandidateId") Long objectCandidateId);

    /** 查询指定候选属性的权威类型配置。 */
    Map<String, Object> selectTypeConfiguration(@Param("taskId") Long taskId,
                                                @Param("attributeCandidateId") Long attributeCandidateId);

    /** 根据分页条件查询AI候选对象类型属性。 */
    default PageResult<OmAiAttributeDO> selectPage(OmAiAttributePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiAttributeDO>()
                .eqIfPresent(OmAiAttributeDO::getId, reqVO.getId())
                .eqIfPresent(OmAiAttributeDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(OmAiAttributeDO::getName, reqVO.getName())
                .eqIfPresent(OmAiAttributeDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
