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

package tech.qiantong.qontology.module.om.dal.mapper.term;

import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import java.util.Arrays;
import com.github.yulichang.base.MPJBaseMapper;
import tech.qiantong.qontology.core.page.PageResult;
import java.util.HashSet;
import java.util.Set;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermPageReqVO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 术语Mapper接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface OmTermMapper extends BaseMapperX<OmTermDO> {

    default PageResult<OmTermDO> selectPage(OmTermPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<OmTermDO>()
                .eqIfPresent(OmTermDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(OmTermDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmTermDO::getStdName, reqVO.getStdName())
                .likeIfPresent(OmTermDO::getEnName, reqVO.getEnName())
                .eqIfPresent(OmTermDO::getAlias, reqVO.getAlias())
                .eqIfPresent(OmTermDO::getAbbr, reqVO.getAbbr())
                .eqIfPresent(OmTermDO::getSource, reqVO.getSource())
                .eqIfPresent(OmTermDO::getDescription, reqVO.getDescription())
                .eqIfPresent(OmTermDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(OmTermDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
