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

package tech.qiantong.qontology.module.basic.dal.mapper.file;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategoryPageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileCategoryDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 模型数据文件分类Mapper接口
 *
 * @author qModel
 * @date 2026-08-18
 */
public interface BasicFileCategoryMapper extends BaseMapperX<BasicFileCategoryDO> {

    default PageResult<BasicFileCategoryDO> selectPage(BasicFileCategoryPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time", "order_num"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<BasicFileCategoryDO>()
                .likeIfPresent(BasicFileCategoryDO::getName, reqVO.getName())
                .eqIfPresent(BasicFileCategoryDO::getParentId, reqVO.getParentId())
                .eqIfPresent(BasicFileCategoryDO::getOrderNum, reqVO.getOrderNum())
                .eqIfPresent(BasicFileCategoryDO::getAncestors, reqVO.getAncestors())
                .eqIfPresent(BasicFileCategoryDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(ModelDataCategoryDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
