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

package tech.qiantong.qontology.module.basic.service.project.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Objects;
import org.springframework.transaction.annotation.Isolation;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;
import tech.qiantong.qontology.module.om.api.export.OntologyExportApi;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.domain.entity.SysDictData;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectDO;
import tech.qiantong.qontology.module.basic.dal.mapper.project.BasicProjectMapper;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectService;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectVersionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目管理Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicProjectServiceImpl extends ServiceImpl<BasicProjectMapper, BasicProjectDO> implements IBasicProjectService {

    @Resource
    private IBasicProjectVersionService projectVersionService;

    @Resource
    private OntologyExportApi ontologyExportApi;

    /**
     * 获项目管理分页列表
     *
     * @param pageReqVO 分页查询
     * @return 项目管理分页列表
     */
    @Override
    public PageResult<BasicProjectDO> getBasicProjectPage(BasicProjectPageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    /**
     * 创建项目管理
     *
     * @param createReqVO 项目管理信息
     * @return 项目管理编号
     */
    @Override
    public Long createBasicProject(BasicProjectSaveReqVO createReqVO) {
        BasicProjectDO dictType = BeanUtils.toBean(createReqVO, BasicProjectDO.class);
        baseMapper.insert(dictType);
        // 创建完成后需要同时创建版本信息
        BasicProjectVersionSaveReqVO versionReqVO = new BasicProjectVersionSaveReqVO();
        versionReqVO.setProjectId(dictType.getId());
        versionReqVO.setVersion(createReqVO.getCurrentVersion());
        projectVersionService.createBasicProjectVersion(versionReqVO);
        return dictType.getId();
    }

    /**
     * 更新项目管理
     *
     * @param updateReqVO 项目管理信息
     * @return 影响行数
     */
    @Override
    public int updateBasicProject(BasicProjectSaveReqVO updateReqVO) {
        // 相关校验

        // 更新项目管理
        BasicProjectDO updateObj = BeanUtils.toBean(updateReqVO, BasicProjectDO.class);
        return baseMapper.updateById(updateObj);
    }

    /**
     * 删除项目管理
     *
     * @param idList 项目管理编号
     * @return 影响行数
     */
    @Override
    public int removeBasicProject(Collection<Long> idList) {
        // 批量删除项目管理
        return baseMapper.deleteByIds(idList);
    }

    /**
     * 根据编号获取项目管理
     *
     * @param id 编号
     * @return 项目管理
     */
    @Override
    public BasicProjectDO getBasicProjectById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 获得项目管理字典
     *
     * @return 项目管理字典
     */
    @Override
    public List<SysDictData> queryProjectDict() {
        List<BasicProjectDO> list = super.list();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(item -> {
            SysDictData dictData = new SysDictData();
            dictData.setDictLabel(item.getName());
            dictData.setDictValue(item.getId().toString());
            return dictData;
        }).collect(Collectors.toList());
    }
    @Override
    public List<SysDictData> getOwlExportVersions(Long projectId) {
        requireExportProject(projectId);
        return projectVersionService.list(new QueryWrapper<BasicProjectVersionDO>()
                .eq("project_id", projectId).orderByDesc("id")).stream().map(version -> {
            SysDictData option = new SysDictData();
            option.setDictValue(String.valueOf(version.getId()));
            option.setDictLabel(version.getVersion());
            return option;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public byte[] exportOwl(Long projectId, Long versionId) {
        return exportOntology(projectId, versionId, false);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public byte[] exportTurtle(Long projectId, Long versionId) {
        return exportOntology(projectId, versionId, true);
    }

    private byte[] exportOntology(Long projectId, Long versionId, boolean turtle) {
        BasicProjectDO project = requireExportProject(projectId);
        if (versionId == null || versionId <= 0) throw new ServiceException("请选择导出版本");
        BasicProjectVersionDO version = projectVersionService.getBasicProjectVersionById(versionId);
        if (version == null || !Objects.equals(version.getProjectId(), projectId)) {
            throw new ServiceException("导出版本不存在或不属于当前项目");
        }
        return turtle
                ? ontologyExportApi.exportTurtle(versionId, project.getNamespace(), project.getName(), version.getVersion())
                : ontologyExportApi.exportOwl(versionId, project.getNamespace(), project.getName(), version.getVersion());
    }

    private BasicProjectDO requireExportProject(Long projectId) {
        if (projectId == null || projectId <= 0) throw new ServiceException("项目编号无效");
        BasicProjectDO project = baseMapper.selectById(projectId);
        if (project == null) throw new ServiceException("项目不存在或已删除");
        return project;
    }
}
