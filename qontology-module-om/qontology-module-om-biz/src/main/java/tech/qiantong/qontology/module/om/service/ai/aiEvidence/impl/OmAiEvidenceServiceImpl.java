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

package tech.qiantong.qontology.module.om.service.ai.aiEvidence.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiEvidence.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence.OmAiEvidenceMapper;
import tech.qiantong.qontology.module.om.service.ai.aiEvidence.IOmAiEvidenceService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI解析来源证据 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiEvidenceServiceImpl extends ServiceImpl<OmAiEvidenceMapper, OmAiEvidenceDO> implements IOmAiEvidenceService {
    @Resource
    private OmAiEvidenceMapper mapper;

    /** 获得AI解析来源证据分页列表。 */
    @Override
    public PageResult<OmAiEvidenceDO> getOmAiEvidencePage(OmAiEvidencePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI解析来源证据。 */
    @Override
    public Long createOmAiEvidence(OmAiEvidenceSaveReqVO reqVO) {
        OmAiEvidenceDO data = BeanUtils.toBean(reqVO, OmAiEvidenceDO.class);

        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI解析来源证据。 */
    @Override
    public int updateOmAiEvidence(OmAiEvidenceSaveReqVO reqVO) {
        OmAiEvidenceDO existing = getRequired(reqVO.getId());

        OmAiEvidenceDO data = BeanUtils.toBean(reqVO, OmAiEvidenceDO.class);

        return mapper.updateById(data);
    }

    /** 批量删除AI解析来源证据。 */
    @Override
    public int removeOmAiEvidence(Collection<Long> ids) {

        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI解析来源证据详情。 */
    @Override
    public OmAiEvidenceDO getOmAiEvidenceById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiEvidenceDO getRequired(Long id) {
        OmAiEvidenceDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI解析来源证据不存在");
        }
        return data;
    }
}
