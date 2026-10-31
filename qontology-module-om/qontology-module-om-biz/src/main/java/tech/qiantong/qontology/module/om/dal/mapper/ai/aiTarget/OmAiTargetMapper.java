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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiTarget;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTarget.vo.OmAiTargetPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget.OmAiTargetDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选建模目标 Mapper 接口。 */
public interface OmAiTargetMapper extends BaseMapperX<OmAiTargetDO> {
    /** 统计建模目标关联的对象和关系候选数量。 */
    Integer selectRelatedElementCount(Long targetId);

    /** 查询任务下的候选建模目标索引。 */
    List<Map<String, Object>> selectCandidateIndex(Long taskId);

    /** 统计指定任务中的有效建模目标数量。 */
    Integer countTaskTargets(@Param("taskId") Long taskId, @Param("targetIds") List<Long> targetIds);

    /** 刷新任务下建模目标的关联元素数量。 */
    int refreshRelatedElementCounts(Long taskId);

    /** 根据分页条件查询AI候选建模目标。 */
    default PageResult<OmAiTargetDO> selectPage(OmAiTargetPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiTargetDO>()
                .eqIfPresent(OmAiTargetDO::getId, reqVO.getId())
                .eqIfPresent(OmAiTargetDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(OmAiTargetDO::getQuestion, reqVO.getQuestion())
                .likeIfPresent(OmAiTargetDO::getQuestionSource, reqVO.getQuestionSource())
                .eqIfPresent(OmAiTargetDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time"))
                .orderByDesc(reqVO.getOrderByColumn() == null || reqVO.getOrderByColumn().isBlank(),
                        OmAiTargetDO::getCreateTime, OmAiTargetDO::getId));
    }
}
