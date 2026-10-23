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


package tech.qiantong.qontology.arangodb.core.wrapper.query;

/**
 * @program: qKnow-pro
 * @description: 接口模版
 * @author: wang
 * @create: 2025-11-19
 */
public interface Query<T> {

    /**
     * 根据条件生成基本查询语句
     * @return 完成的查询语句
     */
    String getAqlQuerySql();

    /**
     * 根据条件生成基本统计语句
     * @return 完成的统计语句
     */
    String getAqlCountQuerySql();

    /**
     * 根据条件生成关系统计语句
     * @return 完成的关系统计语句
     */
    String getAqlCountRelationQuerySql();

    /**
     * 根据条件生成链查询语句
     * @param depth 链深度
     * @return 完成的链查询语句
     */
    String getAqlChainQuerySql(Integer depth);
}
