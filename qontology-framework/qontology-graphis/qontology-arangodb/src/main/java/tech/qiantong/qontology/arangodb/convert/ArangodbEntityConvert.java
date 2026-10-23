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

package tech.qiantong.qontology.arangodb.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.arangodb.domain.ArangodbEntity;
import tech.qiantong.qontology.graphis.domain.GraphEntity;

import java.util.List;

/**
 * 转换
 * @author wang
 * @date 2025/11/17 09:26
 **/
@Mapper(uses = ArangodbRelationshipsConvert.class)
public interface ArangodbEntityConvert {
    ArangodbEntityConvert INSTANCE = Mappers.getMapper(ArangodbEntityConvert.class);

    @Mapping(source = "graphRelationships", target = "dynamicEntityRelationships")
    ArangodbEntity toArangodbEntity(GraphEntity graphEntity);

    @Mapping(source = "dynamicEntityRelationships", target = "graphRelationships")
    GraphEntity toGraphEntity(ArangodbEntity arangodbEntity);

    List<GraphEntity> toGraphEntityList(List<ArangodbEntity> dynamicEntities);

    List<ArangodbEntity> toArangodbEntityList(List<GraphEntity> graphEntities);

}
