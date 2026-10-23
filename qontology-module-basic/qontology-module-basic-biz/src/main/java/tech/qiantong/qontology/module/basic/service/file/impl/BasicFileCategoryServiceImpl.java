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

package tech.qiantong.qontology.module.basic.service.file.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategoryPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategorySaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileCategoryDO;
import tech.qiantong.qontology.module.basic.dal.mapper.file.BasicFileCategoryMapper;
import tech.qiantong.qontology.module.basic.service.file.IBasicFileCategoryService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型数据文件分类Service业务层处理
 *
 * @author qModel
 * @date 2026-08-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicFileCategoryServiceImpl extends ServiceImpl<BasicFileCategoryMapper, BasicFileCategoryDO>
        implements IBasicFileCategoryService {
    @Resource
    private BasicFileCategoryMapper modelDataCategoryMapper;

    @Override
    public PageResult<BasicFileCategoryDO> getModelDataCategoryPage(BasicFileCategoryPageReqVO pageReqVO) {
        return modelDataCategoryMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createModelDataCategory(BasicFileCategorySaveReqVO createReqVO) {
        BasicFileCategoryDO dictType = BeanUtils.toBean(createReqVO, BasicFileCategoryDO.class);
        modelDataCategoryMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateModelDataCategory(BasicFileCategorySaveReqVO updateReqVO) {
        // 相关校验

        // 更新模型数据文件分类
        BasicFileCategoryDO updateObj = BeanUtils.toBean(updateReqVO, BasicFileCategoryDO.class);
        return modelDataCategoryMapper.updateById(updateObj);
    }

    @Override
    public int removeModelDataCategory(Long id) {
        // 单独删除模型数据文件分类
        return modelDataCategoryMapper.deleteById(id);
    }

    @Override
    public BasicFileCategoryDO getModelDataCategoryById(Long id) {
        return modelDataCategoryMapper.selectById(id);
    }

    @Override
    public List<BasicFileCategoryDO> getModelDataCategoryList() {
        return modelDataCategoryMapper.selectList();
    }

    @Override
    public Map<Long, BasicFileCategoryDO> getModelDataCategoryMap() {
        List<BasicFileCategoryDO> modelDataCategoryList = modelDataCategoryMapper.selectList();
        return modelDataCategoryList.stream()
                .collect(Collectors.toMap(
                        BasicFileCategoryDO::getId,
                        basicFileCategoryDO -> basicFileCategoryDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public boolean hasChildByModelDataCategoryId(Long id) {
        return modelDataCategoryMapper.selectCount(BasicFileCategoryDO::getParentId, id) > 0;
    }

}
