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

package tech.qiantong.qontology.module.om.service.ai.aiObjectTarget.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiObjectTarget.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObjectTarget.OmAiObjectTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObjectTarget.OmAiObjectTargetMapper;
import tech.qiantong.qontology.module.om.service.ai.aiObjectTarget.IOmAiObjectTargetService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选对象目标关联 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiObjectTargetServiceImpl extends ServiceImpl<OmAiObjectTargetMapper, OmAiObjectTargetDO> implements IOmAiObjectTargetService {
    @Resource
    private OmAiObjectTargetMapper mapper;

    /** 获得AI候选对象目标关联分页列表。 */
    @Override
    public PageResult<OmAiObjectTargetDO> getOmAiObjectTargetPage(OmAiObjectTargetPageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI候选对象目标关联。 */
    @Override
    public Long createOmAiObjectTarget(OmAiObjectTargetSaveReqVO reqVO) {
        OmAiObjectTargetDO data = BeanUtils.toBean(reqVO, OmAiObjectTargetDO.class);
        
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选对象目标关联。 */
    @Override
    public int updateOmAiObjectTarget(OmAiObjectTargetSaveReqVO reqVO) {
        OmAiObjectTargetDO existing = getRequired(reqVO.getId());
        
        OmAiObjectTargetDO data = BeanUtils.toBean(reqVO, OmAiObjectTargetDO.class);
        
        return mapper.updateById(data);
    }

    /** 批量删除AI候选对象目标关联。 */
    @Override
    public int removeOmAiObjectTarget(Collection<Long> ids) {
        
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选对象目标关联详情。 */
    @Override
    public OmAiObjectTargetDO getOmAiObjectTargetById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiObjectTargetDO getRequired(Long id) {
        OmAiObjectTargetDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选对象目标关联不存在");
        }
        return data;
    }
}
