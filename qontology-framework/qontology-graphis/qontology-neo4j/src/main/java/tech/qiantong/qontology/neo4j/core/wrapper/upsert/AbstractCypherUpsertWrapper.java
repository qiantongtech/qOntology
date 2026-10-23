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

package tech.qiantong.qontology.neo4j.core.wrapper.upsert;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import tech.qiantong.qontology.neo4j.core.domain.Neo4jCondition;
import tech.qiantong.qontology.neo4j.core.wrapper.AbstractWrapper;
import tech.qiantong.qontology.neo4j.utils.AnnotationProcessorUtils;
import tech.qiantong.qontology.neo4j.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 语法生成
 * @author wang
 * @date 2025/11/18 10:29
 **/
@Slf4j
public abstract class AbstractCypherUpsertWrapper<T, R, Children extends AbstractCypherUpsertWrapper<T, R, Children>>
        extends AbstractWrapper<T, R, Children> {

    public AbstractCypherUpsertWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    public String mergeRelationship(String titleName) {
        Map<String, Object> params = this.getParams();
        String mergeClause = getMergeClause();
        String result = " MERGE (" + titleName + ":" + getLabel() + replaceParametersWithValues(mergeClause, params) + ")";
        return result;
    }

    private String replaceParametersWithValues(String mergeClause, Map<String, Object> params) {
        String result = mergeClause;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            String placeholder = "\\$" + paramName; // 转义$符号

            result = result.replaceAll(placeholder, formatValue(paramValue));
        }

        return result;
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String string) {
            return "'" + escapeString(string) + "'";
        } else if (value instanceof Boolean) {
            return value.toString().toLowerCase();
        } else if (value instanceof Number) {
            return value.toString();
        } else {
            return "'" + escapeString(value.toString()) + "'";
        }
    }

    private String escapeString(String str) {
        return str.replace("'", "\\'");
    }

    public String mergeSql(T entity) {
        StringBuilder query = new StringBuilder("MERGE (n:" + getLabel() + getMergeClause() + ")");

        if (entity != null) {
            Map<String, Object> entityProperties = AnnotationProcessorUtils.getEntityProperties(entity);
            List<String> rList = Lists.newArrayList();

            // 处理所有属性，包括展开Map
            processProperties(entityProperties, rList, "");

            if (!rList.isEmpty()) {
                query.append(" SET n += { ");
                query.append(String.join(", ", rList));  // 添加逗号和空格分隔
                query.append(" }");
            }
        }
        return query.toString();
    }

    /**
     * 递归处理属性，将Map展开为扁平结构
     */
    private void processProperties(Map<String, Object> properties, List<String> resultList, String prefix) {
        properties.forEach((key, value) -> {
            String fullKey = getColumn(key);
            if (value instanceof Map) {
                // 递归处理Map，展开其内容
                processProperties((Map<String, Object>) value, resultList, fullKey);
            } else {
                // 格式化为：key: value（注意key和value之间的空格）
                String formattedValue = Utils.getValueString(value);
                resultList.add(prefix + fullKey + ": " + formattedValue);  // 添加空格
            }
        });
    }

    public String updateSql(T entity) {
        StringBuilder query = new StringBuilder("MATCH (n:" + getLabel() + ")");
        query.append(Utils.getWhereClause(super.conditions));

        // 获取参数
        Map<String, Object> params = this.getParams();
        // 执行参数替换
        StringBuilder finalQuery = new StringBuilder(replaceParametersWithValues(query.toString(), params));

        if (entity != null) {
            Map<String, Object> entityProperties = AnnotationProcessorUtils.getEntityProperties(entity);
            List<String> rList = Lists.newArrayList();
            entityProperties.forEach((key, value) -> {
                //字段命名规范处理
                String fullKey = getColumn(key);
                rList.add("n." + fullKey + "=" + Utils.getValueString(value));
            });
            finalQuery.append(" SET ").append(String.join(",", rList));
        }
        return finalQuery.toString();
    }

    /**
     * 获取标签
     * @return
     */
    private String getLabel() {
        StringBuilder labelBuilder = new StringBuilder();
        String simpleName = entityClass.getSimpleName();
        labelBuilder.append(simpleName);
        labels.forEach(label -> labelBuilder.append(":").append(label));
        return labelBuilder.toString();
    }

    private String getMergeClause() {
        StringBuilder mergeClause = new StringBuilder(" {");
        for (Neo4jCondition condition : conditions) {
            String propertyName = condition.getPropertyName();
            String operator = condition.getOperator();
            String parameterName = condition.getParameterName();

            if (!"=".equals(operator)) {
                log.warn("合并子句不支持除'='以外的判断,已自动忽略。");
                continue;
            }

            // 处理特殊属性：id(n)
//            if ("id".equalsIgnoreCase(propertyName)) {
//                mergeClause.append(" id(n):").append(" $").append(parameterName).append(",");
//                continue;
//            }
            // 处理普通属性
            mergeClause.append(propertyName)
                    .append(":")
                    .append(" $")
                    .append(parameterName).append(",");
        }
        // 删除最后一个逗号
        mergeClause.deleteCharAt(mergeClause.length() - 1);

        mergeClause.append("}");
        return mergeClause.toString();
    }
}
