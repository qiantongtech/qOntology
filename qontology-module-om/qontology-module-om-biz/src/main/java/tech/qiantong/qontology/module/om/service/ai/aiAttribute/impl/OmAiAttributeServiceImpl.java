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

package tech.qiantong.qontology.module.om.service.ai.aiAttribute.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAttribute.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAttribute.OmAiAttributeMapper;
import tech.qiantong.qontology.module.om.service.ai.aiAttribute.IOmAiAttributeService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选对象类型属性 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiAttributeServiceImpl extends ServiceImpl<OmAiAttributeMapper, OmAiAttributeDO> implements IOmAiAttributeService {
    @Resource
    private OmAiAttributeMapper mapper;

    /** 获得AI候选对象类型属性分页列表。 */
    @Override
    public PageResult<OmAiAttributeDO> getOmAiAttributePage(OmAiAttributePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI候选对象类型属性。 */
    @Override
    public Long createOmAiAttribute(OmAiAttributeSaveReqVO reqVO) {
        OmAiAttributeDO data = BeanUtils.toBean(reqVO, OmAiAttributeDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setDelFlag(false);
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选对象类型属性。 */
    @Override
    public int updateOmAiAttribute(OmAiAttributeSaveReqVO reqVO) {
        OmAiAttributeDO existing = getRequired(reqVO.getId());
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(existing.getAdoptStatus())) {
            throw new ServiceException("已采纳数据不可修改");
        }
        OmAiAttributeDO data = BeanUtils.toBean(reqVO, OmAiAttributeDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setAdoptedId(null);
        data.setAdoptMessage(null);
        return mapper.updateById(data);
    }

    /** 批量删除AI候选对象类型属性。 */
    @Override
    public int removeOmAiAttribute(Collection<Long> ids) {
        for (Long id : ids) {
            if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(getRequired(id).getAdoptStatus())) {
                throw new ServiceException("已采纳数据不可删除");
            }
        }
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选对象类型属性详情。 */
    @Override
    public OmAiAttributeDO getOmAiAttributeById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiAttributeDO getRequired(Long id) {
        OmAiAttributeDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选对象类型属性不存在");
        }
        return data;
    }
}
