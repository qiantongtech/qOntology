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

package tech.qiantong.qontology.module.om.service.ai.aiDataEvidence.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataEvidence.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataEvidence.OmAiDataEvidenceDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataEvidence.OmAiDataEvidenceMapper;
import tech.qiantong.qontology.module.om.service.ai.aiDataEvidence.IOmAiDataEvidenceService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选数据证据关联 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiDataEvidenceServiceImpl extends ServiceImpl<OmAiDataEvidenceMapper, OmAiDataEvidenceDO> implements IOmAiDataEvidenceService {
    @Resource
    private OmAiDataEvidenceMapper mapper;

    /** 获得AI候选数据证据关联分页列表。 */
    @Override
    public PageResult<OmAiDataEvidenceDO> getOmAiDataEvidencePage(OmAiDataEvidencePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI候选数据证据关联。 */
    @Override
    public Long createOmAiDataEvidence(OmAiDataEvidenceSaveReqVO reqVO) {
        OmAiDataEvidenceDO data = BeanUtils.toBean(reqVO, OmAiDataEvidenceDO.class);

        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选数据证据关联。 */
    @Override
    public int updateOmAiDataEvidence(OmAiDataEvidenceSaveReqVO reqVO) {
        OmAiDataEvidenceDO existing = getRequired(reqVO.getId());

        OmAiDataEvidenceDO data = BeanUtils.toBean(reqVO, OmAiDataEvidenceDO.class);

        return mapper.updateById(data);
    }

    /** 批量删除AI候选数据证据关联。 */
    @Override
    public int removeOmAiDataEvidence(Collection<Long> ids) {

        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选数据证据关联详情。 */
    @Override
    public OmAiDataEvidenceDO getOmAiDataEvidenceById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiDataEvidenceDO getRequired(Long id) {
        OmAiDataEvidenceDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选数据证据关联不存在");
        }
        return data;
    }
}
