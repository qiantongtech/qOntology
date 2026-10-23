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

package tech.qiantong.qontology.module.basic.service.file;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategoryPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategorySaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileCategoryDO;

import java.util.List;
import java.util.Map;

/**
 * 模型数据文件分类Service接口
 *
 * @author qModel
 * @date 2026-08-18
 */
public interface IBasicFileCategoryService extends IService<BasicFileCategoryDO> {

    /**
     * 获得模型数据文件分类分页列表
     *
     * @param pageReqVO 分页请求
     * @return 模型数据文件分类分页列表
     */
    PageResult<BasicFileCategoryDO> getModelDataCategoryPage(BasicFileCategoryPageReqVO pageReqVO);

    /**
     * 创建模型数据文件分类
     *
     * @param createReqVO 模型数据文件分类信息
     * @return 模型数据文件分类编号
     */
    Long createModelDataCategory(BasicFileCategorySaveReqVO createReqVO);

    /**
     * 更新模型数据文件分类
     *
     * @param updateReqVO 模型数据文件分类信息
     */
    int updateModelDataCategory(BasicFileCategorySaveReqVO updateReqVO);

    /**
     * 删除模型数据文件分类
     *
     * @param id 模型数据文件分类编号
     */
    int removeModelDataCategory(Long id);

    /**
     * 获得模型数据文件分类详情
     *
     * @param id 模型数据文件分类编号
     * @return 模型数据文件分类
     */
    BasicFileCategoryDO getModelDataCategoryById(Long id);

    /**
     * 获得全部模型数据文件分类列表
     *
     * @return 模型数据文件分类列表
     */
    List<BasicFileCategoryDO> getModelDataCategoryList();

    /**
     * 获得全部模型数据文件分类 Map
     *
     * @return 模型数据文件分类 Map
     */
    Map<Long, BasicFileCategoryDO> getModelDataCategoryMap();

    /**
     * 是否存在模型数据文件分类子节点
     *
     * @param id 模型数据文件分类id
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByModelDataCategoryId(Long id);


}
