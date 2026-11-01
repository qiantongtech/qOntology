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

package tech.qiantong.qontology.module.om.service.ai.aiTask.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTask.OmAiTaskMapper;
import tech.qiantong.qontology.module.om.service.ai.aiTask.IOmAiTaskService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI辅助建模任务 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiTaskServiceImpl extends ServiceImpl<OmAiTaskMapper, OmAiTaskDO> implements IOmAiTaskService {
    @Resource
    private OmAiTaskMapper mapper;

    /** 获得AI辅助建模任务分页列表。 */
    @Override
    public PageResult<OmAiTaskDO> getOmAiTaskPage(OmAiTaskPageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI辅助建模任务。 */
    @Override
    public Long createOmAiTask(OmAiTaskSaveReqVO reqVO) {
        OmAiTaskDO data = BeanUtils.toBean(reqVO, OmAiTaskDO.class);
        
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI辅助建模任务。 */
    @Override
    public int updateOmAiTask(OmAiTaskSaveReqVO reqVO) {
        OmAiTaskDO existing = getRequired(reqVO.getId());
        
        OmAiTaskDO data = BeanUtils.toBean(reqVO, OmAiTaskDO.class);
        
        return mapper.updateById(data);
    }

    /** 批量删除AI辅助建模任务。 */
    @Override
    public int removeOmAiTask(Collection<Long> ids) {
        
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI辅助建模任务详情。 */
    @Override
    public OmAiTaskDO getOmAiTaskById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiTaskDO getRequired(Long id) {
        OmAiTaskDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI辅助建模任务不存在");
        }
        return data;
    }
}
