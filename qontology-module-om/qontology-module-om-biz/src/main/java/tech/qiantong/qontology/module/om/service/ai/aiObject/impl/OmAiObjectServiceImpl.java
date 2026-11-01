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

package tech.qiantong.qontology.module.om.service.ai.aiObject.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiObject.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObject.OmAiObjectDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObject.OmAiObjectMapper;
import tech.qiantong.qontology.module.om.service.ai.aiObject.IOmAiObjectService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选对象类型 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiObjectServiceImpl extends ServiceImpl<OmAiObjectMapper, OmAiObjectDO> implements IOmAiObjectService {
    @Resource
    private OmAiObjectMapper mapper;

    /** 获得AI候选对象类型分页列表。 */
    @Override
    public PageResult<OmAiObjectDO> getOmAiObjectPage(OmAiObjectPageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI候选对象类型。 */
    @Override
    public Long createOmAiObject(OmAiObjectSaveReqVO reqVO) {
        OmAiObjectDO data = BeanUtils.toBean(reqVO, OmAiObjectDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setDelFlag(false);
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选对象类型。 */
    @Override
    public int updateOmAiObject(OmAiObjectSaveReqVO reqVO) {
        OmAiObjectDO existing = getRequired(reqVO.getId());
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(existing.getAdoptStatus())) {
            throw new ServiceException("已采纳数据不可修改");
        }
        OmAiObjectDO data = BeanUtils.toBean(reqVO, OmAiObjectDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setAdoptedId(null);
        data.setAdoptMessage(null);
        return mapper.updateById(data);
    }

    /** 批量删除AI候选对象类型。 */
    @Override
    public int removeOmAiObject(Collection<Long> ids) {
        for (Long id : ids) {
            if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(getRequired(id).getAdoptStatus())) {
                throw new ServiceException("已采纳数据不可删除");
            }
        }
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选对象类型详情。 */
    @Override
    public OmAiObjectDO getOmAiObjectById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiObjectDO getRequired(Long id) {
        OmAiObjectDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选对象类型不存在");
        }
        return data;
    }
}
