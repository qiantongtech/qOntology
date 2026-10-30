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

package tech.qiantong.qontology.module.om.service.ai.aiEvidence;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiEvidence.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.core.page.PageResult;

/** AI解析来源证据 Service 接口。 */
public interface IOmAiEvidenceService extends IService<OmAiEvidenceDO> {
    /**
     * 获得AI解析来源证据分页列表。
     *
     * @param pageReqVO 分页查询参数
     * @return AI解析来源证据分页列表
     */
    PageResult<OmAiEvidenceDO> getOmAiEvidencePage(OmAiEvidencePageReqVO pageReqVO);

    /**
     * 创建AI解析来源证据。
     *
     * @param createReqVO 创建参数
     * @return 新增记录编号
     */
    Long createOmAiEvidence(OmAiEvidenceSaveReqVO createReqVO);

    /**
     * 更新AI解析来源证据。
     *
     * @param updateReqVO 更新参数
     * @return 更新记录数
     */
    int updateOmAiEvidence(OmAiEvidenceSaveReqVO updateReqVO);

    /**
     * 批量删除AI解析来源证据。
     *
     * @param idList 记录编号集合
     * @return 删除记录数
     */
    int removeOmAiEvidence(Collection<Long> idList);

    /**
     * 获得AI解析来源证据详情。
     *
     * @param id 记录编号
     * @return AI解析来源证据
     */
    OmAiEvidenceDO getOmAiEvidenceById(Long id);
}
