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
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectDO;

import java.util.Collection;
import java.util.List;

/**
 * 项目管理Service接口
 *
 * @author qOntology
 * @date 2026-10-12
 */
public interface IBasicProjectService extends IService<BasicProjectDO> {

    /**
     * 获得项目管理分页列表
     *
     * @param pageReqVO 分页请求
     * @return 项目管理分页列表
     */
    PageResult<BasicProjectDO> getBasicProjectPage(BasicProjectPageReqVO pageReqVO);

    /**
     * 创建项目管理
     *
     * @param createReqVO 项目管理信息
     * @return 项目管理编号
     */
    Long createBasicProject(BasicProjectSaveReqVO createReqVO, LoginUser currentUser);

    /**
     * 更新项目管理
     *
     * @param updateReqVO 项目管理信息
     */
    int updateBasicProject(BasicProjectSaveReqVO updateReqVO);

    /**
     * 删除项目管理
     *
     * @param idList 项目管理编号
     */
    int removeBasicProject(Collection<Long> idList);

    /**
     * 获得项目管理详情
     *
     * @param id 项目管理编号
     * @return 项目管理
     */
    BasicProjectDO getBasicProjectById(Long id);

    /**
     * 获得项目管理字典
     *
     * @return 项目管理字典
     */
    List<SysDictData> queryProjectDict();
    /** 当前项目可导出的版本。 */
    List<SysDictData> getOwlExportVersions(Long projectId);

    /** 校验版本归属并导出该版本的本体。 */
    byte[] exportOwl(Long projectId, Long versionId);

    /** 校验版本归属并导出 Turtle 本体。 */
    byte[] exportTurtle(Long projectId, Long versionId);
}
