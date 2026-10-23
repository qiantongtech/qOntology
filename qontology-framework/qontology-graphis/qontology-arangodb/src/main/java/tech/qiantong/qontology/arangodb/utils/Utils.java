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


package tech.qiantong.qontology.arangodb.utils;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Edge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.CaseFormat;
import tech.qiantong.qontology.arangodb.core.domain.ArangodbCondition;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 工具
 *
 * @author wang
 * @date 2025/11/19 18:50
 **/
public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册 JavaTimeModule 以支持 LocalDateTime 等
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 判断值的类型并格式化
     *
     * @param value
     * @return
     **/
    public static String getValueString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return "'" + value + "'";
        } else if (value instanceof LocalDateTime time) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "'" + time.format(formatter) + "'";
        } else if (value instanceof Date) {
            // 将 Date 转换为 LocalDateTime
            Date date = (Date) value;
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
            LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();

            // 格式化 LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "'" + dateTime.format(formatter) + "'";
        } else if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return null;
            }
            // 手动构建 JSON-like 字符串，递归处理每个值
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(", ");
                String keyStr = entry.getKey() == null ? null : escapeJsonKey(entry.getKey().toString());
                String valStr = getValueString(entry.getValue());
                sb.append(keyStr).append(": ").append(valStr);
                first = false;
            }
            sb.append("}");
            return sb.toString();
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
        } else {
            // 其他类型：Number, Boolean 等直接 toString
            return value.toString();
        }
    }

    // 辅助方法：确保 Map 的 key 是合法的 JSON key（加引号）
    private static String escapeJsonKey(String key) {
        if (key == null) return "null";
        // 如果 key 是纯数字或布尔字面量，仍建议加引号（JSON 规范要求 key 必须是字符串）
        // 这里统一加双引号并转义内部双引号（但最终整体被单引号包裹，所以只需处理单引号冲突）
        String escaped = key.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'");
        return "\"" + escaped + "\"";
    }

    // === 获取集合名 ===
    public static String getCollectionName(Class<?> entityClass) {
        Document docAnn = entityClass.getAnnotation(Document.class);
        if (docAnn != null && !docAnn.value().isEmpty()) {
            return docAnn.value();
        }
        // 修正：类名转小写驼峰（符合Spring Data ArangoDB默认行为）
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.getSimpleName());
    }

    public static String getEdgeName(Class<?> entityClass) {
        Edge docAnn = entityClass.getAnnotation(Edge.class);
        if (docAnn != null && !docAnn.value().isEmpty()) {
            return docAnn.value();
        }
        // 修正：类名转小写驼峰（符合Spring Data ArangoDB默认行为）
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.getSimpleName());
    }

    public static String getProperty(Class<?> clazz, String propertyName) {
        String prefixName = "";
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if (field.getName().equals(propertyName) && field.isAnnotationPresent(com.arangodb.springframework.annotation.Field.class)) {
                com.arangodb.springframework.annotation.Field annotation = field.getAnnotation(com.arangodb.springframework.annotation.Field.class);
                prefixName = annotation.value();
            }
        } catch (NoSuchFieldException e) {
            return prefixName;
        }
        return prefixName;
    }

    public static String getWhereSql(List<ArangodbCondition> conditions) {
        if (conditions.isEmpty()) {
            return "";
        }
        StringBuilder whereSql = new StringBuilder(" FILTER ");
        String separator = "";
        for (ArangodbCondition condition : conditions) {
            String propertyName = condition.getPropertyName();
            String operator = condition.getOperator();
            String parameterName = condition.getParameterName();

            if (condition.getSeparator() != null) {
                separator = " " + condition.getSeparator() + " ";
                continue;
            } else {
                whereSql.append(separator);
                separator = " AND ";
            }

            // 特殊比较any
            if ("any".equalsIgnoreCase(operator)) {
                whereSql.append("(for keyword IN ")
                        .append(parameterName)
                        .append(" RETURN CONTAINS(")
                        .append(propertyName)
                        .append(",keyword))ANY == true");
                continue;
            }

            // 处理特殊属性：id(n)
            if ("n.id".equalsIgnoreCase(propertyName)) {
                whereSql.append("n._key ").append(operator).append(" ").append(parameterName);
                continue;
            }
            // 处理普通属性
            whereSql.append(propertyName)
                    .append(" ")
                    .append(operator)
                    .append(" ")
                    .append(parameterName);
        }
        return whereSql.toString();
    }

    /**
     * 获取实体属性
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getEntityProperties(T entity) {
        if (entity == null) {
            return new HashMap<>();
        }

        Map<String, Object> properties = new HashMap<>();
        Class<?> clazz = entity.getClass();

        // 遍历当前类及其父类的所有声明字段（包括 private）
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                // 允许访问 private 字段
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    if (value != null) {
                        properties.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    // 日志记录或忽略
                    properties.put(field.getName(), "[ACCESS DENIED]");
                }
            }
            clazz = clazz.getSuperclass();
        }

        return properties;
    }
}
