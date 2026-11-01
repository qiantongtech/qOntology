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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiEvidence.vo.OmAiEvidencePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI解析来源证据 Mapper 接口。 */
public interface OmAiEvidenceMapper extends BaseMapperX<OmAiEvidenceDO> {
    /** 查询指定证据编号对应的原文内容。 */
    List<String> selectContents(@Param("taskId") Long taskId,
                                @Param("evidenceIds") List<Long> evidenceIds);

    /** 根据分页条件查询AI解析来源证据。 */
    default PageResult<OmAiEvidenceDO> selectPage(OmAiEvidencePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiEvidenceDO>()
                .eqIfPresent(OmAiEvidenceDO::getId, reqVO.getId())
                .eqIfPresent(OmAiEvidenceDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(OmAiEvidenceDO::getFileId, reqVO.getFileId())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
