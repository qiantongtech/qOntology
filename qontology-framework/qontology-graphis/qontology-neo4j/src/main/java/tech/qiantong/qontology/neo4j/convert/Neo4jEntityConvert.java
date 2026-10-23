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

package tech.qiantong.qontology.neo4j.convert;

import com.google.common.collect.Maps;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.neo4j.driver.Value;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.neo4j.domain.DynamicEntity;

import java.util.List;
import java.util.Map;

/**
 * 转换
 * @author wang
 * @date 2025/11/17 09:26
 **/
@Mapper(uses = Neo4jRelationshipsConvert.class)
public interface Neo4jEntityConvert {
    Neo4jEntityConvert INSTANCE = Mappers.getMapper(Neo4jEntityConvert.class);

    // 定义从 GraphEntity 到 DynamicEntity 的映射
    @Mapping(source = "graphRelationships", target = "relationships")
    DynamicEntity toDynamicEntity(GraphEntity graphEntity);

    // 定义从 DynamicEntity 到 GraphEntity 的映射
    @Mapping(source = "dynamicProperties", target = "dynamicProperties", qualifiedByName = "dynamicPropertiesConvert")
    @Mapping(source = "relationships", target = "graphRelationships")
    GraphEntity toGraphEntity(DynamicEntity dynamicEntity);

    List<GraphEntity> toGraphEntityList(List<DynamicEntity> dynamicEntities);

    List<DynamicEntity> toDynamicEntityList(List<GraphEntity> graphEntities);

    @Named("dynamicPropertiesConvert")
    default Map<String, Object> dynamicPropertiesConvert(Map<String, Object> dynamicProperties) {
        if (dynamicProperties == null) {
            return null;
        }
        Map<String, Object> properties = Maps.newHashMap();
        dynamicProperties.forEach((key, value) -> {
            if (value instanceof Value obj) {
                properties.put(key, obj.asObject());
            } else {
                properties.put(key, value);
            }
        });
        return properties;
    }

}
