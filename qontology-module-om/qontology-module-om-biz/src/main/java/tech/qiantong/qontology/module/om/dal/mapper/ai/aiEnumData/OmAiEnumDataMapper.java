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

package tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumData;

import java.util.Set;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiEnumData.vo.OmAiEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumData.OmAiEnumDataDO;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选枚举数据 Mapper 接口。 */
public interface OmAiEnumDataMapper extends BaseMapperX<OmAiEnumDataDO> {
    /** 根据分页条件查询AI候选枚举数据。 */
    default PageResult<OmAiEnumDataDO> selectPage(OmAiEnumDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiEnumDataDO>()
                .eqIfPresent(OmAiEnumDataDO::getId, reqVO.getId())
                .eqIfPresent(OmAiEnumDataDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(OmAiEnumDataDO::getEnumTypeId, reqVO.getEnumTypeId())
                .likeIfPresent(OmAiEnumDataDO::getEnumLabel, reqVO.getEnumLabel())
                .eqIfPresent(OmAiEnumDataDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OmAiEnumDataDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(),
                        Set.of("id", "enum_sort", "create_time", "update_time")));
    }
}
