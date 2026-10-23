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

package tech.qiantong.qontology.neo4j.core.wrapper.query;

import java.util.Set;

/**
 * @program: qKnow-pro
 * @description: 查询接口模版
 * @author: wang
 * @create: 2025-11-18
 */
public interface Query<T> {

    /**
     * 根据条件获取查询语句
     * @return 查询实体关系语句
     */
    String getCypherQuerySql();

    /**
     * 根据条件生成关系链查询语句
     * @param depth 链深度
     * @return 查询语句
     */
    String getRelationChainCypherQuerySql(Integer depth);

    /**
     * 根据条件生成count关系语句
     * @return 统计关系语句
     */
    String getCypherCountQuerySql();

    /**
     * 根据条件生成count关系语句
     * @return 统计关系语句
     */
    String getCypherCountRelationQuerySql();

    /**
     * 查询节点
     * @return
     */
    String getNodeQuerySql(Set<String> labels);

    /**
     * 查询关系
     * @return
     */
    public String getRelQuerySql(Set<String> labels);
}
