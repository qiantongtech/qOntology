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

package tech.qiantong.qontology.module.om.service.ai.aiAdoptLog;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAdoptLog.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAdoptLog.OmAiAdoptLogDO;
import tech.qiantong.qontology.core.page.PageResult;

/** AI候选采纳日志 Service 接口。 */
public interface IOmAiAdoptLogService extends IService<OmAiAdoptLogDO> {
    /**
     * 获得AI候选采纳日志分页列表。
     *
     * @param pageReqVO 分页查询参数
     * @return AI候选采纳日志分页列表
     */
    PageResult<OmAiAdoptLogDO> getOmAiAdoptLogPage(OmAiAdoptLogPageReqVO pageReqVO);

    /**
     * 创建AI候选采纳日志。
     *
     * @param createReqVO 创建参数
     * @return 新增记录编号
     */
    Long createOmAiAdoptLog(OmAiAdoptLogSaveReqVO createReqVO);

    /**
     * 更新AI候选采纳日志。
     *
     * @param updateReqVO 更新参数
     * @return 更新记录数
     */
    int updateOmAiAdoptLog(OmAiAdoptLogSaveReqVO updateReqVO);

    /**
     * 批量删除AI候选采纳日志。
     *
     * @param idList 记录编号集合
     * @return 删除记录数
     */
    int removeOmAiAdoptLog(Collection<Long> idList);

    /**
     * 获得AI候选采纳日志详情。
     *
     * @param id 记录编号
     * @return AI候选采纳日志
     */
    OmAiAdoptLogDO getOmAiAdoptLogById(Long id);
}
