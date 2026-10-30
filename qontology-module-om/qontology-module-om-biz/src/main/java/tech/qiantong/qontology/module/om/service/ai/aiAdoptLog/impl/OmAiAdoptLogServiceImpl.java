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

package tech.qiantong.qontology.module.om.service.ai.aiAdoptLog.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAdoptLog.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAdoptLog.OmAiAdoptLogDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAdoptLog.OmAiAdoptLogMapper;
import tech.qiantong.qontology.module.om.service.ai.aiAdoptLog.IOmAiAdoptLogService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI候选采纳日志 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiAdoptLogServiceImpl extends ServiceImpl<OmAiAdoptLogMapper, OmAiAdoptLogDO> implements IOmAiAdoptLogService {
    @Resource
    private OmAiAdoptLogMapper mapper;

    /** 获得AI候选采纳日志分页列表。 */
    @Override
    public PageResult<OmAiAdoptLogDO> getOmAiAdoptLogPage(OmAiAdoptLogPageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI候选采纳日志。 */
    @Override
    public Long createOmAiAdoptLog(OmAiAdoptLogSaveReqVO reqVO) {
        OmAiAdoptLogDO data = BeanUtils.toBean(reqVO, OmAiAdoptLogDO.class);
        
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI候选采纳日志。 */
    @Override
    public int updateOmAiAdoptLog(OmAiAdoptLogSaveReqVO reqVO) {
        OmAiAdoptLogDO existing = getRequired(reqVO.getId());
        
        OmAiAdoptLogDO data = BeanUtils.toBean(reqVO, OmAiAdoptLogDO.class);
        
        return mapper.updateById(data);
    }

    /** 批量删除AI候选采纳日志。 */
    @Override
    public int removeOmAiAdoptLog(Collection<Long> ids) {
        
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI候选采纳日志详情。 */
    @Override
    public OmAiAdoptLogDO getOmAiAdoptLogById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiAdoptLogDO getRequired(Long id) {
        OmAiAdoptLogDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选采纳日志不存在");
        }
        return data;
    }
}
