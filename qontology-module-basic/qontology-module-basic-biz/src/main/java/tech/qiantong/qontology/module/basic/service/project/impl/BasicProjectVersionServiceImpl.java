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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.domain.entity.SysDictData;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;
import tech.qiantong.qontology.module.basic.dal.mapper.project.BasicProjectVersionMapper;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectVersionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目版本Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicProjectVersionServiceImpl extends ServiceImpl<BasicProjectVersionMapper, BasicProjectVersionDO> implements IBasicProjectVersionService {

    /**
     * 获得项目版本分页列表
     *
     * @param pageReqVO 分页请求
     * @return 项目版本分页列表
     */
    @Override
    public PageResult<BasicProjectVersionDO> getBasicProjectVersionPage(BasicProjectVersionPageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    /**
     * 创建项目版本
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public Long createBasicProjectVersion(BasicProjectVersionSaveReqVO createReqVO) {
        BasicProjectVersionDO dictType = BeanUtils.toBean(createReqVO, BasicProjectVersionDO.class);
        baseMapper.insert(dictType);
        return dictType.getId();
    }

    /**
     * 更新项目版本
     *
     * @param updateReqVO 更新信息
     * @return 影响行数
     */
    @Override
    public int updateBasicProjectVersion(BasicProjectVersionSaveReqVO updateReqVO) {
        // 相关校验

        // 更新项目版本
        BasicProjectVersionDO updateObj = BeanUtils.toBean(updateReqVO, BasicProjectVersionDO.class);
        return baseMapper.updateById(updateObj);
    }

    /**
     * 删除项目版本
     *
     * @param idList 编号集合
     * @return 影响行数
     */
    @Override
    public int removeBasicProjectVersion(Collection<Long> idList) {
        // 批量删除项目版本
        return baseMapper.deleteByIds(idList);
    }

    /**
     * 获得项目版本
     *
     * @param id 编号
     * @return 项目版本
     */
    @Override
    public BasicProjectVersionDO getBasicProjectVersionById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 获得项目版本列表
     *
     * @param projectId 项目编号
     * @return 项目版本列表
     */
    @Override
    public List<SysDictData> queryProjectVersionDict(Long projectId) {
        LambdaQueryWrapper<BasicProjectVersionDO> queryWrapper = Wrappers.lambdaQuery(BasicProjectVersionDO.class)
                .eq(BasicProjectVersionDO::getProjectId, projectId);

        List<BasicProjectVersionDO> list = super.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(item -> {
            SysDictData dictData = new SysDictData();
            dictData.setDictLabel(item.getVersion());
            dictData.setDictValue(item.getId().toString());
            return dictData;
        }).collect(Collectors.toList());
    }
}
