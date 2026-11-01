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

package tech.qiantong.qontology.module.om.service.ai.aiFile;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiFile.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.core.page.PageResult;

/** AI辅助建模文件 Service 接口。 */
public interface IOmAiFileService extends IService<OmAiFileDO> {
    /**
     * 获得AI辅助建模文件分页列表。
     *
     * @param pageReqVO 分页查询参数
     * @return AI辅助建模文件分页列表
     */
    PageResult<OmAiFileDO> getOmAiFilePage(OmAiFilePageReqVO pageReqVO);

    /**
     * 创建AI辅助建模文件。
     *
     * @param createReqVO 创建参数
     * @return 新增记录编号
     */
    Long createOmAiFile(OmAiFileSaveReqVO createReqVO);

    /**
     * 更新AI辅助建模文件。
     *
     * @param updateReqVO 更新参数
     * @return 更新记录数
     */
    int updateOmAiFile(OmAiFileSaveReqVO updateReqVO);

    /**
     * 批量删除AI辅助建模文件。
     *
     * @param idList 记录编号集合
     * @return 删除记录数
     */
    int removeOmAiFile(Collection<Long> idList);

    /**
     * 获得AI辅助建模文件详情。
     *
     * @param id 记录编号
     * @return AI辅助建模文件
     */
    OmAiFileDO getOmAiFileById(Long id);
}
