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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiObjectTarget;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiObjectTarget.vo.OmAiObjectTargetPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObjectTarget.OmAiObjectTargetDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选对象目标关联 Mapper 接口。 */
public interface OmAiObjectTargetMapper extends BaseMapperX<OmAiObjectTargetDO> {
    /** 查询对象候选关联的建模目标。 */
    List<Map<String, Object>> selectTargets(Long objectCandidateId);

    /** 查询对象候选关联的建模目标编号。 */
    List<Long> selectTargetIds(Long objectCandidateId);

    /** 批量新增对象候选与建模目标关联。 */
    int insertBatch(@Param("taskId") Long taskId,
                    @Param("objectCandidateId") Long objectCandidateId,
                    @Param("targetIds") List<Long> targetIds);

    /** 新增对象候选与建模目标关联，重复关联直接忽略。 */
    int insertOrIgnore(OmAiObjectTargetDO link);

    /** 根据分页条件查询AI候选对象目标关联。 */
    default PageResult<OmAiObjectTargetDO> selectPage(OmAiObjectTargetPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiObjectTargetDO>()
                .eqIfPresent(OmAiObjectTargetDO::getId, reqVO.getId())
                .eqIfPresent(OmAiObjectTargetDO::getTaskId, reqVO.getTaskId())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
