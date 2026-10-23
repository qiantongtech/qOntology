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

package tech.qiantong.qontology.graphis.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.CaseFormat;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.domain.GraphRelationship;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 格式转换
 * @author wang
 * @date 2025/11/17 10:30
 **/
public class Convert {

    public static JSONObject toJSONObject(List<?> entityList) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray entities = new JSONArray(); // 实体
            JSONArray relationships = new JSONArray(); // 关系
            for (Object entity : entityList) {
                // 处理实体信息
                JSONObject entityJson = new JSONObject();
                Field idField = getField(entity.getClass(), "id");
                assert idField != null;
                idField.setAccessible(true);
                Object id = idField.get(entity);
                entityJson.put("id", id);

                Field nameField = getField(entity.getClass(), "name");
                if (nameField != null) {
                    nameField.setAccessible(true);
                    Object name = nameField.get(entity);
                    entityJson.put("name", name);
                }
                entities.add(entityJson);

                // 处理关系信息
                Field relationshipMapField = getField(entity.getClass(), "graphRelationships");
                if (relationshipMapField != null) {
                    relationshipMapField.setAccessible(true);
                    Object relationshipObject = relationshipMapField.get(entity);
                    if (relationshipObject != null) {
                        for (Object relObject : (Collection<?>) relationshipObject) {
                            Field relationshipName = getField(relObject.getClass(), "name");
                            // 直接处理GraphEntityRelationship对象
                            Field endNodeField = getField(relObject.getClass(), "endNode");
                            if (endNodeField != null && relationshipName != null) {
                                relationshipName.setAccessible(true);
                                endNodeField.setAccessible(true);
                                Object endNode = endNodeField.get(relObject);
                                Field relationshipId = getField(relObject.getClass(), "id");
                                assert relationshipId != null;
                                relationshipId.setAccessible(true);
                                JSONObject relationshipJson = new JSONObject();
                                relationshipJson.put("id", relationshipId.get(relObject));
                                relationshipJson.put("startId", id);
                                relationshipJson.put("startName", entityJson.get("name"));

                                if (endNode != null) {
                                    Field endIdField = getField(endNode.getClass(), "id");
                                    endIdField.setAccessible(true);
                                    Long endId = (Long) endIdField.get(endNode);
                                    relationshipJson.put("endId", endId);

                                    Field endNameField = getField(endNode.getClass(), "dynamicProperties");
                                    if (endNameField != null) {
                                        endNameField.setAccessible(true);
                                        Map<String, Object> map = (Map<String, Object>) endNameField.get(endNode);
                                        Object name = map.get("name");
                                        relationshipJson.put("endName", name);
                                    }
                                }
                                relationshipJson.put("relationType", relationshipName.get(relObject));
                                relationships.add(relationshipJson);
                            }
                        }
                    }
                }

            }
            jsonObject.put("entities", entities);
            jsonObject.put("relationships", relationships);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    //public static JSONObject toEntityJSONObject(List<?> entityList) {
    //    JSONObject jsonObject = new JSONObject();
    //    try {
    //        JSONArray entities = new JSONArray(); // 实体
    //        JSONArray relationships = new JSONArray(); // 关系
    //        for (Object entity : entityList) {
    //            // 处理实体信息
    //            JSONObject entityJson = new JSONObject();
    //            Field idField = getField(entity.getClass(), "id");
    //            assert idField != null;
    //            idField.setAccessible(true);
    //            Object id = idField.get(entity);
    //            entityJson.put("id", id);
    //
    //            Field dynamicPropertiesField = getField(entity.getClass(), "dynamicProperties");
    //            if (dynamicPropertiesField != null) {
    //                dynamicPropertiesField.setAccessible(true);
    //                // 对 dynamicProperties 中的键进行转换并添加到 entityJson 中
    //                Map<String, Object> dynamicProperties = (Map<String, Object>) dynamicPropertiesField.get(entity);
    //                for (Map.Entry<String, Object> entry : dynamicProperties.entrySet()) {
    //                    String snakeKey = entry.getKey();
    //                    String camelKey = snakeToCamel(snakeKey);
    //                    Object value = entry.getValue();
    //                    if ("id".equals(camelKey)) {
    //                        camelKey = "_id";
    //                    }
    //                    entityJson.put(camelKey, value);
    //                }
    //            }
    //            entities.add(entityJson);
    //
    //            // 处理关系信息
    //            Field relationshipMapField = getField(entity.getClass(), "graphRelationships");
    //            if (relationshipMapField != null) {
    //                relationshipMapField.setAccessible(true);
    //                Object relationshipObject = relationshipMapField.get(entity);
    //                if (relationshipObject != null) {
    //                    for (Object relObject : (Collection<?>) relationshipObject) {
    //                        Field relationshipName = getField(relObject.getClass(), "name");
    //                        // 直接处理GraphEntityRelationship对象
    //                        Field endNodeField = getField(relObject.getClass(), "endNode");
    //                        if (endNodeField != null && relationshipName != null) {
    //                            relationshipName.setAccessible(true);
    //                            endNodeField.setAccessible(true);
    //                            Object endNode = endNodeField.get(relObject);
    //                            Field relationshipId = getField(relObject.getClass(), "id");
    //                            assert relationshipId != null;
    //                            relationshipId.setAccessible(true);
    //                            JSONObject relationshipJson = new JSONObject();
    //                            relationshipJson.put("id", relationshipId.get(relObject));
    //                            relationshipJson.put("startId", id);
    //                            relationshipJson.put("startName", entityJson.get("name"));
    //
    //                            if (endNode != null) {
    //                                Field endIdField = getField(endNode.getClass(), "id");
    //                                endIdField.setAccessible(true);
    //                                Long endId = (Long) endIdField.get(endNode);
    //                                relationshipJson.put("endId", endId);
    //
    //                                Field endNameField = getField(endNode.getClass(), "dynamicProperties");
    //                                if (endNameField != null) {
    //                                    endNameField.setAccessible(true);
    //                                    Map<String, Object> map = (Map<String, Object>) endNameField.get(endNode);
    //                                    Object name = map.get("name");
    //                                    relationshipJson.put("endName", name);
    //                                }
    //                            }
    //                            relationshipJson.put("relationType", relationshipName.get(relObject));
    //                            relationships.add(relationshipJson);
    //                        }
    //                    }
    //                }
    //            }
    //
    //        }
    //        jsonObject.put("entities", entities);
    //        jsonObject.put("relationships", relationships);
    //    } catch (Exception e) {
    //        throw new RuntimeException(e);
    //    }
    //    return jsonObject;
    //}

    public static JSONObject toEntityJSONObject(List<GraphEntity> entityList) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray entities = new JSONArray(); // 实体
            JSONArray relationships = new JSONArray(); // 关系
            for (GraphEntity entity : entityList) {
                // 处理实体信息
                JSONObject entityJson = new JSONObject();
                entityJson.put("id", entity.getId());
                entityJson.put("name", entity.getName());
                entityJson.put("graphId", entity.getGraphId());
                entityJson.put("releaseStatus", entity.getReleaseStatus());
                entityJson.put("creatorId", entity.getCreatorId());
                entityJson.put("createTime", entity.getCreateTime());
                entityJson.put("createBy", entity.getCreateBy());

                Map<String, Object> properties = entity.getDynamicProperties();
                if (properties != null) {
                    properties.forEach((key, value) -> {
                        String camelKey = snakeToCamel(key);
                        if ("id".equals(camelKey)) {
                            camelKey = "_id";
                        }
                        entityJson.put(camelKey, value);
                    });
                }
                entities.add(entityJson);

                // 处理关系信息
                Collection<GraphRelationship> graphRelationships = entity.getGraphRelationships();
                if (graphRelationships != null) {
                    for (GraphRelationship relObject : graphRelationships) {
                        JSONObject relationshipJson = new JSONObject();
                        relationshipJson.put("id", relObject.getId());
                        relationshipJson.put("startId", relObject.getStartNode().getId());
                        relationshipJson.put("startName", relObject.getStartNode().getName());
                        relationshipJson.put("endId", relObject.getEndNode().getId());
                        relationshipJson.put("endName", relObject.getEndNode().getName());
                        relationshipJson.put("relationType", relObject.getName());
                        relationshipJson.put("createTime", entity.getCreateTime());
//                        relationshipJson.put("startSchemaId", entityJson.get("schemaId"));
//                        relationshipJson.put("endSchemaId", relObject.getEndNode());
                        Object start = relObject.getStartNode().getDynamicProperties().get("schema_name");
                        String stratSchemaName = start == null ? "" : start.toString();
                        String startName = stratSchemaName.replaceAll("^[\"']+|[\"']+$", "");
                        Object end = relObject.getEndNode().getDynamicProperties().get("schema_name");
                        String endSchemaName = end == null ? "" : end.toString();
                        String finalName = endSchemaName.replaceAll("^[\"']+|[\"']+$", "");
                        relationshipJson.put("startSchemaName", startName);
                        relationshipJson.put("endSchemaName", finalName);
                        relationships.add(relationshipJson);
                    }
                }

            }
            jsonObject.put("entities", entities);
            jsonObject.put("relationships", relationships);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }


    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    // 将驼峰式命名转换为下划线分隔的命名
    public static String camelToSnake(String camelStr) {
        StringBuilder snakeStr = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                snakeStr.append("_");
            }
            snakeStr.append(Character.toLowerCase(c));
        }
        return snakeStr.toString();
    }

    // 将下划线分隔的命名转换为驼峰式命名
    public static String snakeToCamel(String snakeStr) {
        //String[] components = snakeStr.split("_");
        //StringBuilder camelCase = new StringBuilder(components[0]);
        //for (int i = 1; i < components.length; i++) {
        //    String component = components[i];
        //    camelCase.append(Character.toUpperCase(component.charAt(0)));
        //    camelCase.append(component.substring(1));
        //}
        //return camelCase.toString();
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, snakeStr);
    }
}
