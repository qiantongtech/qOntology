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

package tech.qiantong.qontology.module.om.service.omtarget;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetTaskDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 建模目标验证任务Service接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmTargetTaskService extends IService<OmTargetTaskDO> {

    /**
     * 获得建模目标验证任务分页列表
     *
     * @param pageReqVO 分页请求
     * @return 建模目标验证任务分页列表
     */
    PageResult<OmTargetTaskDO> getOmTargetTaskPage(OmTargetTaskPageReqVO pageReqVO);

    /**
     * 创建建模目标验证任务
     *
     * @param createReqVO 建模目标验证任务信息
     * @return 建模目标验证任务编号
     */
    Long createOmTargetTask(OmTargetTaskSaveReqVO createReqVO);

    /**
     * 删除建模目标验证任务
     *
     * @param idList 建模目标验证任务编号
     */
    int removeOmTargetTask(Collection<Long> idList);

    /**
     * 获得建模目标验证任务详情
     *
     * @param id 建模目标验证任务编号
     * @return 建模目标验证任务
     */
    OmTargetTaskDO getOmTargetTaskById(Long id);

}
