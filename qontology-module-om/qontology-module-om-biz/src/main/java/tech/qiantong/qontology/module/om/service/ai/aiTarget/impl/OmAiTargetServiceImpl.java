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

package tech.qiantong.qontology.module.om.service.ai.aiTarget.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTarget.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget.OmAiTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTarget.OmAiTargetMapper;
import tech.qiantong.qontology.module.om.service.ai.aiTarget.IOmAiTargetService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选建模目标 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiTargetServiceImpl extends ServiceImpl<OmAiTargetMapper, OmAiTargetDO> implements IOmAiTargetService {
    @Resource
    private OmAiTargetMapper mapper;

    /** 获得AI候选建模目标分页列表。 */
    @Override
    public PageResult<OmAiTargetDO> getOmAiTargetPage(OmAiTargetPageReqVO reqVO) {
        PageResult<OmAiTargetDO> result = mapper.selectPage(reqVO);
        for (OmAiTargetDO target : result.getList()) {
            Integer count = mapper.selectRelatedElementCount(target.getId());
            target.setRelatedElementCount(count == null ? 0 : count);
        }
        return result;
    }

    /** 创建AI候选建模目标。 */
    @Override
    public Long createOmAiTarget(OmAiTargetSaveReqVO reqVO) {
        OmAiTargetDO data = BeanUtils.toBean(reqVO, OmAiTargetDO.class);
        if (data.getPriority() == null || data.getPriority() <= 0) {
            data.setPriority(1);
        }
        data.setAcceptStatus("0");
        data.setRelatedElementCount(0);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setDelFlag(false);
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选建模目标。 */
    @Override
    public int updateOmAiTarget(OmAiTargetSaveReqVO reqVO) {
        OmAiTargetDO existing = getRequired(reqVO.getId());
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(existing.getAdoptStatus())) {
            throw new ServiceException("已采纳数据不可修改");
        }
        OmAiTargetDO data = BeanUtils.toBean(reqVO, OmAiTargetDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setAdoptedId(null);
        data.setAdoptMessage(null);
        return mapper.updateById(data);
    }

    /** 批量删除AI候选建模目标。 */
    @Override
    public int removeOmAiTarget(Collection<Long> ids) {
        for (Long id : ids) {
            if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(getRequired(id).getAdoptStatus())) {
                throw new ServiceException("已采纳数据不可删除");
            }
        }
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选建模目标详情。 */
    @Override
    public OmAiTargetDO getOmAiTargetById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiTargetDO getRequired(Long id) {
        OmAiTargetDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选建模目标不存在");
        }
        return data;
    }
}
