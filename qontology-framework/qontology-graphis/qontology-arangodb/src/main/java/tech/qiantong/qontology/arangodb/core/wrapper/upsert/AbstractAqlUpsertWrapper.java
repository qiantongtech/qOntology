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


package tech.qiantong.qontology.arangodb.core.wrapper.upsert;


import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import tech.qiantong.qontology.arangodb.core.wrapper.AbstractWrapper;
import tech.qiantong.qontology.arangodb.utils.Utils;

import java.util.Map;

/**
 * 语法生成
 * @author wang
 * @date 2025/11/18 10:29
 **/
@Slf4j
public abstract class AbstractAqlUpsertWrapper<T, R, Children extends AbstractAqlUpsertWrapper<T, R, Children>>
        extends AbstractWrapper<T, R, Children> {

    public AbstractAqlUpsertWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    public String updateSql(T entity) {
        StringBuilder sql = new StringBuilder("FOR n IN ").append(Utils.getCollectionName(entityClass));
        sql.append(Utils.getWhereSql(conditions));
        sql.append(" UPDATE n WITH { ");
        Map<String, Object> map = Utils.getEntityProperties(entity);
        map.forEach((key, value) -> {
            String valueString = Utils.getValueString(value);
            if (valueString != null) {
                sql.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key))
                        .append(": ").append(valueString).append(", ");
            }
        });
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" } IN ").append(Utils.getCollectionName(entityClass));
        return sql.toString();
    }


    public String deleteSql() {
        // 获取集合名称
        String entityCollection = Utils.getCollectionName(entityClass);
        String relationCollection = Utils.getEdgeName(relationEntityClass);

        // 构建完整的 AQL 语句，完全按照您提供的语句结构
        StringBuilder sql = new StringBuilder();

        // 主循环：遍历实体
        sql.append("FOR n IN ").append(entityCollection);
        sql.append(Utils.getWhereSql(conditions));

        // 删除关联关系（使用 LET 子查询）
        sql.append("\nLET removed_rels = (");
        sql.append("\n  FOR v IN ").append(relationCollection);
        sql.append("\n    FILTER v._from == n._id OR v._to == n._id");
        sql.append("\n    REMOVE v IN ").append(relationCollection).append(" OPTIONS { ignoreErrors: true }");
        sql.append("\n)");

        // 删除实体本身
        sql.append("\nREMOVE n IN ").append(entityCollection).append(" OPTIONS { ignoreErrors: true }");

        return sql.toString();
    }

}
