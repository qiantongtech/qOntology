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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiRelation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiRelation.vo.OmAiRelationPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelation.OmAiRelationDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选对象类型关系 Mapper 接口。 */
public interface OmAiRelationMapper extends BaseMapperX<OmAiRelationDO> {
    /** 查询任务下的候选关系索引。 */
    List<Map<String, Object>> selectCandidateIndex(Long taskId);

    /** 查询指定起止对象之间的候选关系名称。 */
    List<String> selectNamesByObjects(@Param("taskId") Long taskId,
                                      @Param("startObjectCandidateId") Long startObjectCandidateId,
                                      @Param("endObjectCandidateId") Long endObjectCandidateId);

    /** 根据分页条件查询AI候选对象类型关系。 */
    default PageResult<OmAiRelationDO> selectPage(OmAiRelationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiRelationDO>()
                .eqIfPresent(OmAiRelationDO::getId, reqVO.getId())
                .eqIfPresent(OmAiRelationDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(OmAiRelationDO::getName, reqVO.getName())
                .eqIfPresent(OmAiRelationDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
