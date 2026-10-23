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

package tech.qiantong.qontology.graphis.core.repository;

import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.core.page.PageResult;

import java.util.Collection;
import java.util.List;

/**
 * 抽象基础仓库
 *
 * @author wang
 */
public interface QueryBaseRepository<T, ID> {
    /**
     * id查询
     */
    T findEntityById(ID id);

    /**
     * id批量查询
     */
    List<T> findEntityByIds(Collection<ID> idList);

    /**
     * 条件查询
     */
    List<T> find(QueryExecutor<T> queryWrapper);

    /**
     * 查询所有
     */
    List<T> findAll();

    /**
     * 分页查询
     */
    PageResult<T> findPage(QueryExecutor<T> queryWrapper);

    /**
     * 关系链查询
     */
    List<T> relationChain(QueryExecutor<T> queryWrapper, Integer depth);

    /**
     * 统计
     */
    Long count(QueryExecutor<T> queryWrapper);

    /**
     * 统计关系
     */
    Long countRelation(QueryExecutor<T> queryWrapper);

    /**
     * 根据关系名称查询（模糊匹配）
     */
    List<GraphEntity> findRelationName(List<String> relationNameList);

    /**
     * 全局检索（根据词组跟属性进行模糊对比，拿到所有的数据）
     */
    List<T> globalSearch(String... name);
}
