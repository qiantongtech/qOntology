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

package tech.qiantong.qontology.module.om.dal.mapper.omtarget;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 建模目标Mapper接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface OmTargetMapper extends BaseMapperX<OmTargetDO> {

    /** 关联目标列表仅允许编号、创建时间排序，默认最新创建的目标在前。 */
    default QueryWrapper<OmTargetDO> orderRelatedTargets(QueryWrapper<OmTargetDO> query, OmTargetPageReqVO request) {
        boolean ascending = !"desc".equalsIgnoreCase(request.getIsAsc());
        if ("id".equals(request.getOrderByColumn())) {
            return query.orderBy(true, ascending, "id");
        }
        if ("createTime".equals(request.getOrderByColumn()) || "create_time".equals(request.getOrderByColumn())) {
            return query.orderBy(true, ascending, "create_time").orderByDesc("id");
        }
        return query.orderByDesc("create_time", "id");
    }

    default PageResult<OmTargetDO> selectPage(OmTargetPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<OmTargetDO>()
                .eqIfPresent(OmTargetDO::getVersionId, reqVO.getVersionId())
                .likeIfPresent(OmTargetDO::getQuestion, reqVO.getQuestion())
                .likeIfPresent(OmTargetDO::getQuestionSource, reqVO.getQuestionSource())
                .eqIfPresent(OmTargetDO::getBusinessScenarios, reqVO.getBusinessScenarios())
                .eqIfPresent(OmTargetDO::getPriority, reqVO.getPriority())
                .eqIfPresent(OmTargetDO::getAcceptCriterion, reqVO.getAcceptCriterion())
                .eqIfPresent(OmTargetDO::getAcceptStatus, reqVO.getAcceptStatus())
                .eqIfPresent(OmTargetDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(OmTargetDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
