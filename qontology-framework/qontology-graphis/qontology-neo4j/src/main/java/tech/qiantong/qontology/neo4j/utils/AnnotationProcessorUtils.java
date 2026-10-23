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

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.neo4j.core.schema.CompositeProperty;
import org.springframework.data.neo4j.core.schema.Property;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解工具
 * @author wang
 * @date 2025/11/14 09:17
 **/
public class AnnotationProcessorUtils {

    /**
     * 获取CompositeProperty注解的属性前缀
     * @param clazz
     * @return
     */
    public static String processCompositeProperties(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        String prefixName = "";
        for (Field field : fields) {
            if (field.isAnnotationPresent(CompositeProperty.class)) {
                CompositeProperty annotation = field.getAnnotation(CompositeProperty.class);
                String prefix = annotation.prefix();
                String delimiter = annotation.delimiter();

                if (prefix.isEmpty()) {
                    prefix = field.getName();
                }
                prefixName = prefix + delimiter;
                break;
            }
        }
        return prefixName;
    }

    /**
     * 根据指定属性获取CompositeProperty注解的属性前缀
     * @param clazz
     * @param propertyName
     * @return
     */
    public static String getCompositeProperties(Class<?> clazz, String propertyName) {
        String prefixName = "";
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if (field.getName().equals(propertyName) && field.isAnnotationPresent(CompositeProperty.class)) {
                CompositeProperty annotation = field.getAnnotation(CompositeProperty.class);
                String prefix = annotation.prefix();
                String delimiter = annotation.delimiter();

                if (prefix.isEmpty()) {
                    prefix = field.getName();
                }
                prefixName = prefix + delimiter;
            }
        } catch (NoSuchFieldException e) {
            return prefixName;
        }
        return prefixName;
    }

    /**
     * 根据指定属性获取Property注解的属性
     * @param clazz
     * @param propertyName
     * @return
     */
    public static String getProperty(Class<?> clazz, String propertyName) {
        String prefixName = "";
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if (field.getName().equals(propertyName) && field.isAnnotationPresent(Property.class)) {
                Property annotation = field.getAnnotation(Property.class);
                prefixName = annotation.name();
            }
        } catch (NoSuchFieldException e) {
            return prefixName;
        }
        return prefixName;
    }

    /**
     * 获取实体属性
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
                        String prefix = getCompositeProperties(entity.getClass(), field.getName());
                        String baseName = StringUtils.isEmpty(prefix) ? field.getName() : prefix;

                        if (value instanceof Map) {
                            // 如果属性是 Map，则遍历 Map 的所有键值对
                            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                                String key = entry.getKey();
                                Object val = entry.getValue();
                                String fullKey = baseName + key;
                                properties.put(fullKey, val);
                            }
                        } else {
                            // 如果属性不是 Map，则直接添加
                            String name = StringUtils.isEmpty(prefix) ? field.getName() : prefix;
                            properties.put(name, value);
                        }
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
