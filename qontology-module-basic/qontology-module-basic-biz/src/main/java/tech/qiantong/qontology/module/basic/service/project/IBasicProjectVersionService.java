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

package tech.qiantong.qontology.module.basic.service.project;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.domain.entity.SysDictData;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;

import java.util.Collection;
import java.util.List;

/**
 * 项目版本Service接口
 *
 * @author qOntology
 * @date 2026-10-12
 */
public interface IBasicProjectVersionService extends IService<BasicProjectVersionDO> {

    /**
     * 获得项目版本分页列表
     *
     * @param pageReqVO 分页请求
     * @return 项目版本分页列表
     */
    PageResult<BasicProjectVersionDO> getBasicProjectVersionPage(BasicProjectVersionPageReqVO pageReqVO);

    /**
     * 创建项目版本
     *
     * @param createReqVO 项目版本信息
     * @return 项目版本编号
     */
    Long createBasicProjectVersion(BasicProjectVersionSaveReqVO createReqVO);

    /**
     * 更新项目版本
     *
     * @param updateReqVO 项目版本信息
     */
    int updateBasicProjectVersion(BasicProjectVersionSaveReqVO updateReqVO);

    /**
     * 删除项目版本
     *
     * @param idList 项目版本编号
     */
    int removeBasicProjectVersion(Collection<Long> idList);

    /**
     * 获得项目版本详情
     *
     * @param id 项目版本编号
     * @return 项目版本
     */
    BasicProjectVersionDO getBasicProjectVersionById(Long id);

    /**
     * 获得项目版本列表
     *
     * @param projectId 项目编号
     * @return 项目版本列表
     */
    List<SysDictData> queryProjectVersionDict(Long projectId);
}
