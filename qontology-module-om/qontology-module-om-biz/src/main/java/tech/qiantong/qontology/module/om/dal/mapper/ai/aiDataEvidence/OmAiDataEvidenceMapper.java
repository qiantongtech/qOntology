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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataEvidence;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataEvidence.vo.OmAiDataEvidencePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataEvidence.OmAiDataEvidenceDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选数据证据关联 Mapper 接口。 */
public interface OmAiDataEvidenceMapper extends BaseMapperX<OmAiDataEvidenceDO> {
    /** 查询候选数据关联的原始证据和文件信息。 */
    List<Map<String, Object>> selectEvidence(@Param("dataType") String dataType,
                                             @Param("dataId") Long dataId);

    /** 新增候选数据与证据关联，重复关联直接忽略。 */
    int insertOrIgnore(OmAiDataEvidenceDO link);

    /** 根据分页条件查询AI候选数据证据关联。 */
    default PageResult<OmAiDataEvidenceDO> selectPage(OmAiDataEvidencePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiDataEvidenceDO>()
                .eqIfPresent(OmAiDataEvidenceDO::getId, reqVO.getId())
                .eqIfPresent(OmAiDataEvidenceDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(OmAiDataEvidenceDO::getDataType, reqVO.getDataType())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time")));
    }
}
