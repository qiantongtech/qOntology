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

package tech.qiantong.qontology.module.om.service.ai.aiStage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiStage.OmAiStageMapper;
import tech.qiantong.qontology.module.om.service.ai.aiStage.IOmAiStageService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI解析阶段记录 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiStageServiceImpl extends ServiceImpl<OmAiStageMapper, OmAiStageDO> implements IOmAiStageService {
    @Resource
    private OmAiStageMapper mapper;

    /** 获得AI解析阶段记录分页列表。 */
    @Override
    public PageResult<OmAiStageDO> getOmAiStagePage(OmAiStagePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI解析阶段记录。 */
    @Override
    public Long createOmAiStage(OmAiStageSaveReqVO reqVO) {
        OmAiStageDO data = BeanUtils.toBean(reqVO, OmAiStageDO.class);
        
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI解析阶段记录。 */
    @Override
    public int updateOmAiStage(OmAiStageSaveReqVO reqVO) {
        OmAiStageDO existing = getRequired(reqVO.getId());
        
        OmAiStageDO data = BeanUtils.toBean(reqVO, OmAiStageDO.class);
        
        return mapper.updateById(data);
    }

    /** 批量删除AI解析阶段记录。 */
    @Override
    public int removeOmAiStage(Collection<Long> ids) {
        
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI解析阶段记录详情。 */
    @Override
    public OmAiStageDO getOmAiStageById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiStageDO getRequired(Long id) {
        OmAiStageDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI解析阶段记录不存在");
        }
        return data;
    }
}
