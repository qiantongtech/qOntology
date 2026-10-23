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

package tech.qiantong.qontology.neo4j.utils;

import tech.qiantong.qontology.neo4j.core.domain.Neo4jCondition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工具
 * @author wang
 * @date 2025/11/18 16:02
 **/
public class Utils {

    /**
     * 判断值的类型并格式化
     * @param value
     * @return
     **/
    public static String getValueString(Object value) {

        String valueString;
        if (value instanceof String) {
            valueString = "'" + value + "'";
        } else if (value instanceof Map) {
            // 将Map转换为JSON字符串
            try {
                Map<?, ?> map = (Map<?, ?>) value;
                if (map.isEmpty() || map.size() == 0) {
                    valueString = "''";  // 空Map返回空字符串
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(value);
                    valueString = "'" + json.replace("'", "\\'") + "'";
                }
            } catch (Exception e) {
                valueString = "''";
            }
        } else if (value instanceof LocalDateTime) {
            // 处理 LocalDateTime 类型
           LocalDateTime dateTime = (LocalDateTime) value;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            valueString = "'" + dateTime.format(formatter) + "'";
        } else if (value instanceof Date) {
            // 将 Date 转换为 LocalDateTime
            Date date = (Date) value;
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
            LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();

            // 格式化 LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            valueString = "'" + dateTime.format(formatter) + "'";
        } else if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            if (coll.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : coll) {
                if (!first) sb.append(", ");
                String itemStr = getValueString(item);
                sb.append(itemStr);
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }  else if(value == null) {
            valueString = "null";
        } else {
            valueString = value.toString();
        }
        return valueString;
    }

    /**
     * 根据条件生成WHERE子句
     * @return WHERE子句
     */
    public static String getWhereClause(List<Neo4jCondition> conditions) {
        if (conditions.isEmpty()) {
            return "";
        }

        StringBuilder whereClause = new StringBuilder(" WHERE ");
        String separator = "";
        for (Neo4jCondition condition : conditions) {
            String propertyName = condition.getPropertyName();
            String operator = condition.getOperator();
            String parameterName = condition.getParameterName();

            if (condition.getSeparator() != null) {
                separator =  " " + condition.getSeparator() + " ";
                continue;
            } else {
                whereClause.append(separator);
                separator = " AND ";
            }
            // 特殊比较any
            if ("any".equalsIgnoreCase(operator)) {
                whereClause.append("ANY(keyword IN " + "$" +parameterName + " WHERE(n. " + propertyName + " CONTAINS keyword))");
                continue;
            }

            // 处理特殊属性：id(n)
            if ("id".equalsIgnoreCase(propertyName)) {
                whereClause.append("toString(id(n)) ").append(operator).append(" $").append(parameterName);
                continue;
            }
            // 处理需要转字符串的特殊情况
            if (isStringLikeOperator(operator)) {
                whereClause.append("toString(n.").append(propertyName).append(") CONTAINS $").append(parameterName);
                continue;
            }
            // 处理普通属性
            whereClause.append("n.")
                    .append(propertyName)
                    .append(" ")
                    .append(operator)
                    .append(" $")
                    .append(parameterName);
        }
        return whereClause.toString();
    }

    // 判断是否是字符串上下文的操作符
    private static boolean isStringLikeOperator(String op) {
        return "CONTAINS".equalsIgnoreCase(op)
                || "STARTS WITH".equalsIgnoreCase(op)
                || "ENDS WITH".equalsIgnoreCase(op);
    }
}
