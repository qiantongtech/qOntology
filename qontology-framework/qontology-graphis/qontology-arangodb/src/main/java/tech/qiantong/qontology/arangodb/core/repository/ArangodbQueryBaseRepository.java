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


package tech.qiantong.qontology.arangodb.core.repository;

import com.arangodb.springframework.core.template.ArangoTemplate;
import org.springframework.data.repository.NoRepositoryBean;
import tech.qiantong.qontology.arangodb.core.wrapper.query.LambdaQueryWrapper;
import tech.qiantong.qontology.common.utils.spring.SpringUtils;
import tech.qiantong.qontology.core.page.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author wang
 */
@NoRepositoryBean  // 关键注解：阻止Spring实例化此基础仓库
public interface ArangodbQueryBaseRepository<T, ID> {

    /**
     * 条件查询
     *
     * @param wrapper
     * @return
     */
    default List<T> find(LambdaQueryWrapper<T> wrapper) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        String aqlQuery = wrapper.getAqlQuerySql();
        Map<String, Object> params = wrapper.getParams();
        return arangoTemplate.query(aqlQuery, params, wrapper.getEntityClass()).asListRemaining();
    }

    /**
     * 分页查询
     *
     * @param wrapper
     * @return
     */
    default PageResult<T> findPage(LambdaQueryWrapper<T> wrapper) {
        List<T> row = this.find(wrapper);
        Long count = this.count(wrapper);
        return new PageResult<>(row, count);
    }

    /**
     * 统计
     *
     * @param wrapper
     * @return
     */
    default Long count(LambdaQueryWrapper<T> wrapper) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        String aqlQuery = wrapper.getAqlCountQuerySql();
        Map<String, Object> params = wrapper.getParams();
        return arangoTemplate.query(aqlQuery, params, Long.class).next();
    }

    /**
     * 统计关系
     *
     * @param wrapper
     * @return
     */
    default Long countRelation(LambdaQueryWrapper<T> wrapper) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        String aqlQuery = wrapper.getAqlCountRelationQuerySql();
        Map<String, Object> params = wrapper.getParams();
        return arangoTemplate.query(aqlQuery, params, Long.class).next();
    }

    /**
     * 关系链
     *
     * @param wrapper
     * @param depth
     * @return
     */
    default List<T> relationChain(LambdaQueryWrapper<T> wrapper, Integer depth) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        String aqlQuery = wrapper.getAqlChainQuerySql(depth);
        Map<String, Object> params = wrapper.getParams();
        return arangoTemplate.query(aqlQuery, params, wrapper.getEntityClass()).asListRemaining();
    }
}
