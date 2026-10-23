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

package tech.qiantong.qontology.module.om.service.omtarget.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetTaskDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetTaskMapper;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetTaskService;

import java.util.Collection;

/**
 * 建模目标验证任务Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmTargetTaskServiceImpl extends ServiceImpl<OmTargetTaskMapper, OmTargetTaskDO> implements IOmTargetTaskService {

    @Resource
    private OmTargetMapper targetMapper;

    /**
     * 获得建模目标验证任务分页列表
     *
     * @param pageReqVO 建模目标验证任务分页查询
     * @return 建模目标验证任务分页列表
     */
    @Override
    public PageResult<OmTargetTaskDO> getOmTargetTaskPage(OmTargetTaskPageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }


    /**
     * 创建建模目标验证任务
     *
     * @param createReqVO 建模目标验证任务信息
     * @return 建模目标验证任务编号
     */
    @Override
    public Long createOmTargetTask(OmTargetTaskSaveReqVO createReqVO) {
        OmTargetTaskDO dictType = BeanUtils.toBean(createReqVO, OmTargetTaskDO.class);
        baseMapper.insert(dictType);
        // 同时修改目标表
        LambdaUpdateWrapper<OmTargetDO> updateWrapper = Wrappers.lambdaUpdate(OmTargetDO.class)
                .set(OmTargetDO::getAcceptStatus, createReqVO.getAcceptResult())
                .eq(OmTargetDO::getId, createReqVO.getTargetId());
        targetMapper.update(updateWrapper);
        return dictType.getId();
    }

    /**
     * 删除建模目标验证任务
     *
     * @param idList 建模目标验证任务编号
     * @return 删除结果
     */
    @Override
    public int removeOmTargetTask(Collection<Long> idList) {
        // 批量删除建模目标验证任务
        return baseMapper.deleteByIds(idList);
    }

    /**
     * 获得建模目标验证任务
     *
     * @param id 建模目标验证任务编号
     * @return 建模目标验证任务
     */
    @Override
    public OmTargetTaskDO getOmTargetTaskById(Long id) {
        return baseMapper.selectById(id);
    }

}
