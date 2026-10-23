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


import tech.qiantong.qontology.arangodb.core.wrapper.AbstractWrapper;
import tech.qiantong.qontology.arangodb.utils.Utils;

/**
 * 语法生成
 * @author wang
 * @date 2025/11/18 10:29
 **/
public abstract class AbstractAqlQueryWrapper<T, R, Children extends AbstractAqlQueryWrapper<T, R, Children>>
        extends AbstractWrapper<T, R, Children> {

    public AbstractAqlQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    public String getAqlQuerySql() {
        StringBuilder aql = new StringBuilder("FOR n IN ").append(Utils.getCollectionName(entityClass));
        aql.append(Utils.getWhereSql(conditions));
        if (this.relationEntityClass != null) {
            aql.append(" LET relationships = (")
                    .append(" FOR e IN ").append(Utils.getEdgeName(relationEntityClass))
                    // 关联当前实体的所有边
                    .append(" FILTER e._from == n._id")
                    //.append(" OR e._to == n._id")
                    .append(" LET otherId = (e._from == n._id) ? e._to : e._from")
                    .append(" RETURN e)");
        }


        if (orderBy != null) {
            aql.append(" SORT ").append(orderBy);
        }
        if (limit > 0) {
            aql.append(" LIMIT ").append(offset).append(", ").append(limit);
        }

        if (this.relationEntityClass != null) {
            aql.append(" RETURN MERGE(n, { dynamicEntityRelationships: relationships })");
        } else {
            aql.append(" RETURN n");
        }
        return aql.toString();
    }

    public String getAqlCountQuerySql() {
        StringBuilder aql = new StringBuilder("RETURN LENGTH(")
                .append("FOR n IN ")
                .append(Utils.getCollectionName(entityClass))
                .append(Utils.getWhereSql(conditions))
                .append(" RETURN 1)");
        return aql.toString();
    }

    public String getAqlCountRelationQuerySql() {
        StringBuilder aql = new StringBuilder("RETURN SUM(FOR n IN ").append(Utils.getCollectionName(entityClass));
        aql.append(Utils.getWhereSql(conditions));
        aql.append(" LET relationships = (")
                .append(" FOR e IN ").append(Utils.getEdgeName(relationEntityClass))
                // 关联当前实体的所有边
                .append(" FILTER e._from == n._id")
                //.append(" OR e._to == n._id")
                .append(" LET otherId = (e._from == n._id) ? e._to : e._from")
                .append(" RETURN e)");
        aql.append(" RETURN COUNT(relationships))");
        return aql.toString();
    }

    public String getAqlChainQuerySql(Integer depth) {
        StringBuilder aql = new StringBuilder();

        // 1. 获取起点节点
        aql.append("LET startVertices = (");
        aql.append(getAqlQuerySql());
        aql.append(")");

        // 2. 收集所有顶点ID（使用UNIQUE去重）
        aql.append(" LET startIds = (FOR v IN startVertices RETURN v._id)");
        aql.append(" LET allVertexIds = UNIQUE(FLATTEN(");
        aql.append("   FOR startId IN startIds");
        aql.append("   FOR v IN 0..").append(depth).append(" ANY startId GRAPH ");
        aql.append(Utils.getCollectionName(entityClass));
        aql.append("   RETURN v._id");
        aql.append(" ))");

        // 3. 批量获取顶点（使用IN操作优化）
        aql.append(" LET vertices = (");
        aql.append("   FOR vertexId IN allVertexIds");
        aql.append("   LET vertex = DOCUMENT('").append(Utils.getCollectionName(entityClass)).append("/', vertexId)");
        aql.append("   FILTER vertex != null");
        aql.append("   RETURN vertex");
        aql.append(" )");

        // 4. 批量获取内部关系（使用子查询避免N+1查询）
        aql.append(" FOR vertex IN vertices");
        aql.append("   LET internalRelationships = (");
        aql.append("     FOR e IN ").append(Utils.getEdgeName(relationEntityClass));
        aql.append("       FILTER e._from == vertex._id AND e._to IN allVertexIds");
        aql.append("       RETURN e");
        aql.append("   )");
        aql.append("   RETURN MERGE(vertex, { dynamicEntityRelationships: internalRelationships })");

        return aql.toString();
    }

}
