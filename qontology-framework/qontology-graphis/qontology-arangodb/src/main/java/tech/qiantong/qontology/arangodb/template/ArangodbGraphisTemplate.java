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


package tech.qiantong.qontology.arangodb.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.arangodb.convert.ArangodbEntityConvert;
import tech.qiantong.qontology.arangodb.core.executor.ArangodbQueryExecutor;
import tech.qiantong.qontology.arangodb.core.executor.ArangodbUpsertExecutor;
import tech.qiantong.qontology.arangodb.domain.ArangodbEntity;
import tech.qiantong.qontology.arangodb.domain.ArangodbEntityRelationship;
import tech.qiantong.qontology.arangodb.repository.ArangodbDynamicRelationshipRepository;
import tech.qiantong.qontology.arangodb.repository.ArangodbDynamicRepository;
import tech.qiantong.qontology.arangodb.utils.Utils;
import tech.qiantong.qontology.graphis.core.wrapper.query.GraphQueryWrapper;
import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.GraphUpsertWrapper;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.enums.GraphLabelEnum;
import tech.qiantong.qontology.graphis.repository.GraphisRepository;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模版实现
 *
 * @author wang
 * @date 2025/11/12 15:14
 **/
@Component
@ConditionalOnProperty(prefix = "graphis", name = "type", havingValue = "arangodb")
public class ArangodbGraphisTemplate implements GraphisRepository {

    @Resource
    private ArangodbDynamicRepository dynamicRepository;
    @Resource
    private ArangodbDynamicRelationshipRepository relationshipRepository;

    private ArangodbQueryExecutor<ArangodbEntity> getArangodbQueryExecutor(QueryExecutor<GraphEntity> queryWrapper) {
        GraphQueryWrapper<GraphEntity> graphWrapper = (GraphQueryWrapper<GraphEntity>) queryWrapper;
        ArangodbQueryExecutor<GraphEntity> delegate = (ArangodbQueryExecutor<GraphEntity>) graphWrapper.getDelegate();
        ArangodbQueryExecutor<ArangodbEntity> arangodbjExecutor = new ArangodbQueryExecutor<>(ArangodbEntity.class);
        arangodbjExecutor.apply(delegate);
        return arangodbjExecutor;
    }

    private ArangodbUpsertExecutor<ArangodbEntity> getArangodbUpsertExecutor(UpsertExecutor<GraphEntity> upsertWrapper) {
        GraphUpsertWrapper<GraphEntity> graphWrapper = (GraphUpsertWrapper<GraphEntity>) upsertWrapper;
        ArangodbUpsertExecutor<GraphEntity> delegate = (ArangodbUpsertExecutor<GraphEntity>) graphWrapper.getDelegate();
        ArangodbUpsertExecutor<ArangodbEntity> arangodbjExecutor = new ArangodbUpsertExecutor<>(ArangodbEntity.class);
        arangodbjExecutor.apply(delegate);
        return arangodbjExecutor;
    }

    @Override
    public List<GraphEntity> query(String sql, Map<String, Object> params) {
        List<ArangodbEntity> query = dynamicRepository.query(sql, params, ArangodbEntity.class);
        return ArangodbEntityConvert.INSTANCE.toGraphEntityList(query);
    }

    @Override
    public void deleteEntityById(String id) {
        // HACK： 手动删除边数据，跟neo4j保持同步，临时自己写AQL语法。
        String query = "FOR e IN arangodb_entity_relationship FILTER e._from == @vertexId or e._to == @vertexId REMOVE e in arangodb_entity_relationship";
        Map<String, Object> params = Maps.newHashMap();
        params.put("vertexId", "arangodb_entity/" + id);
        relationshipRepository.query(query, params, ArangodbEntityRelationship.class);
        dynamicRepository.deleteById(id);
    }

    @Override
    public void deleteEntityByIds(Collection<String> idList) {
        idList.forEach(this::deleteEntityById);
    }

    @Override
    public void deleteRelationshipById(String id) {
        relationshipRepository.deleteById(id);
    }

    @Override
    public void deleteRelationshipByIds(Collection<String> idList) {
        idList.forEach(this::deleteRelationshipById);
    }

    @Override
    public void deleteEntityAttributeById(String id, String attributeName) {
        String sql = "FOR doc IN arangodb_entity";
        sql += " FILTER doc._key == '" + id + "'";
        sql += " REPLACE doc WITH UNSET(";
        sql += " MERGE(doc, {";
        sql += " dynamic_properties: UNSET(doc.dynamic_properties || {}, ";
        sql += " '" + attributeName + "')}), ''";
        sql += ") IN arangodb_entity";
        dynamicRepository.query(sql, Maps.newHashMap(), ArangodbEntity.class);
    }

    public void delete(UpsertExecutor<GraphEntity> upsertWrapper) {
        ArangodbUpsertExecutor<ArangodbEntity> upsertExecutor = getArangodbUpsertExecutor(upsertWrapper);
        upsertExecutor.setRelationEntityClass(ArangodbEntityRelationship.class);
        dynamicRepository.query(upsertExecutor.deleteSql(), upsertExecutor.getParams(), ArangodbEntity.class);
    }

    @Override
    public AjaxResult deleteExtUnStruck(Long taskId) {
        UpsertExecutor<GraphEntity> upsertWrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        upsertWrapper.eq(GraphEntity::getId, taskId);
        upsertWrapper.addLabels(GraphLabelEnum.UNSTRUCTURED.getLabel());
        delete(upsertWrapper);
        return AjaxResult.success("删除成功");
    }

    @Override
    public AjaxResult deleteExtStruck(Long taskId) {
        UpsertExecutor<GraphEntity> upsertWrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        upsertWrapper.eq(GraphEntity::getDynamicProperties, "task_id", taskId);
        upsertWrapper.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        delete(upsertWrapper);
        return AjaxResult.success("删除成功");
    }

    @Override
    public GraphEntity findEntityById(String id) {
        QueryExecutor<GraphEntity> queryWrapper = new GraphQueryWrapper<>(GraphEntity.class);
        queryWrapper.eq(GraphEntity::getId, id);
        List<GraphEntity> graphEntities = this.find(queryWrapper);
        return graphEntities.isEmpty() ? null : graphEntities.get(0);
    }

    @Override
    public List<GraphEntity> findEntityByIds(Collection<String> idList) {
        QueryExecutor<GraphEntity> queryWrapper = new GraphQueryWrapper<>(GraphEntity.class);
        queryWrapper.in(GraphEntity::getId, idList);
        return this.find(queryWrapper);
    }

    @Override
    public List<GraphEntity> find(QueryExecutor<GraphEntity> queryWrapper) {
        ArangodbQueryExecutor<ArangodbEntity> queryExecutor = getArangodbQueryExecutor(queryWrapper);
        queryExecutor.setRelationEntityClass(ArangodbEntityRelationship.class);
        List<ArangodbEntity> arangodbEntityList = dynamicRepository.find(queryExecutor);
        return ArangodbEntityConvert.INSTANCE.toGraphEntityList(arangodbEntityList);
    }

    @Override
    public List<GraphEntity> findAll() {
        return this.find(new GraphQueryWrapper<>(GraphEntity.class));
    }

    @Override
    public PageResult<GraphEntity> findPage(QueryExecutor<GraphEntity> queryWrapper) {
        ArangodbQueryExecutor<ArangodbEntity> queryExecutor = getArangodbQueryExecutor(queryWrapper);
        PageResult<ArangodbEntity> pageResult = dynamicRepository.findPage(queryExecutor);
        List<GraphEntity> graphEntityList = ArangodbEntityConvert.INSTANCE.toGraphEntityList(pageResult.getList());
        return new PageResult<>(graphEntityList, pageResult.getTotal());
    }

    @Override
    public List<GraphEntity> relationChain(QueryExecutor<GraphEntity> queryWrapper, Integer depth) {
        ArangodbQueryExecutor<ArangodbEntity> queryExecutor = getArangodbQueryExecutor(queryWrapper);
        queryExecutor.setRelationEntityClass(ArangodbEntityRelationship.class);
        List<ArangodbEntity> arangodbEntityList = dynamicRepository.relationChain(queryExecutor, depth);
        return ArangodbEntityConvert.INSTANCE.toGraphEntityList(arangodbEntityList);
    }

    @Override
    public Long count(QueryExecutor<GraphEntity> queryWrapper) {
        ArangodbQueryExecutor<ArangodbEntity> queryExecutor = getArangodbQueryExecutor(queryWrapper);
        return dynamicRepository.count(queryExecutor);
    }

    @Override
    public Long countRelation(QueryExecutor<GraphEntity> queryWrapper) {
        ArangodbQueryExecutor<ArangodbEntity> queryExecutor = getArangodbQueryExecutor(queryWrapper);
        queryExecutor.setRelationEntityClass(ArangodbEntityRelationship.class);
        return dynamicRepository.countRelation(queryExecutor);
    }

    @Override
    public List<GraphEntity> findRelationName(List<String> relationNameList) {
        //String sql = "FOR edge IN arangodb_entity_relationship LET matched = ( FOR kw IN ";
        //sql += Utils.getValueString(relationNameList) + " FILTER CONTAINS(edge.name, kw) LIMIT 1 RETURN true ) FILTER LENGTH(matched) > 0" +
        //        " LET fromEntity = DOCUMENT(edge._from) LET toEntity = DOCUMENT(edge._to)  RETURN MERGE(edge,{ from: fromEntity, to: toEntity })";
        String sql = "LET keywords = " + Utils.getValueString(relationNameList) +
                " FOR entityId IN UNIQUE(\n" +
                "    FLATTEN(\n" +
                "        FOR edge IN arangodb_entity_relationship\n" +
                "        FILTER LENGTH(FOR kw IN keywords FILTER CONTAINS(edge.name, kw) LIMIT 1 RETURN 1) > 0\n" +
                "        RETURN [edge._from, edge._to]\n" +
                "    )\n" +
                ")\n" +
                "LET relationships = (\n" +
                "    FOR rel IN arangodb_entity_relationship\n" +
                "    FILTER rel._from == entityId \n" +
                "    RETURN rel\n" +
                ")\n" +
                "LET entity = DOCUMENT(entityId)\n" +
                "RETURN MERGE(entity, { dynamicEntityRelationships: relationships })";
        return this.query(sql, Maps.newHashMap());
    }

    @Override
    public List<GraphEntity> globalSearch(String... name) {
        String sql = "LET seedVertices = (FOR n IN arangodb_entity" +
                "  FOR key IN ATTRIBUTES(n) FILTER  (for keyword IN " + Utils.getValueString(Lists.newArrayList(name)) +
                " RETURN CONTAINS(LOWER(n[key]), keyword)) ANY == true" +
                " LET relationships = (FOR e IN arangodb_entity_relationship FILTER e._from == n._id " +
                " LET otherId = (e._from == n._id) ? e._to : e._from  RETURN e)" +
                " RETURN MERGE(n, { dynamicEntityRelationships: relationships })" +
                ")" +
                " LET allVertices = UNIQUE(FOR seed IN seedVertices" +
                " FOR v, e, p IN 0..5 ANY seed._id GRAPH arangodb_entity FILTER v != null RETURN v)" +
                " FOR vertex IN allVertices LET outboundEdges = (" +
                " FOR e IN arangodb_entity_relationship" +
                " FILTER e._from == vertex._id RETURN e)" +
                " RETURN MERGE(vertex, { dynamicEntityRelationships: outboundEdges })";

        return this.query(sql, Maps.newHashMap());
    }

    @Override
    public GraphEntity save(GraphEntity entity) {
        entity.addLabels(GraphLabelEnum.DYNAMICENTITY.getLabel());
        ArangodbEntity arangodbEntity = ArangodbEntityConvert.INSTANCE.toArangodbEntity(entity);
        Collection<ArangodbEntityRelationship> dynamicEntityRelationships = arangodbEntity.getDynamicEntityRelationships();
        arangodbEntity.setDynamicEntityRelationships(null);
        dynamicRepository.save(arangodbEntity);
        if (dynamicEntityRelationships != null) {
            relationshipRepository.saveAll(dynamicEntityRelationships);
        }
        return ArangodbEntityConvert.INSTANCE.toGraphEntity(arangodbEntity);
    }

    @Override
    public List<GraphEntity> saveAll(List<GraphEntity> entityList) {
        Iterable<ArangodbEntity> arangodbEntityList = ArangodbEntityConvert.INSTANCE.toArangodbEntityList(entityList);
        List<ArangodbEntityRelationship> graphRelationships = Lists.newArrayList();
        for (ArangodbEntity arangodbEntity : arangodbEntityList) {
            arangodbEntity.addLabels(GraphLabelEnum.DYNAMICENTITY.getLabel());
            if (arangodbEntity.getDynamicEntityRelationships() != null) {
                graphRelationships.addAll(arangodbEntity.getDynamicEntityRelationships());
                arangodbEntity.setDynamicEntityRelationships(null);
            }
        }

        List<ArangodbEntity> arangodbEntities = Lists.newArrayList(dynamicRepository.saveAll(arangodbEntityList));
        if (!graphRelationships.isEmpty()) {
            Map<String, ArangodbEntity> map = arangodbEntities.stream()
                    .collect(Collectors.toMap(ArangodbEntity::getName,
                            arangodbEntity -> arangodbEntity, (old, now) -> old));
            graphRelationships.forEach(relationship -> {
                if (relationship.getStartNode() == null || relationship.getStartNode().getId() == null) {
                    relationship.setStartNode(map.get(relationship.getStartNode().getName()));
                }
                if (relationship.getStartNode() == null || relationship.getEndNode().getId() == null) {
                    relationship.setEndNode(map.get(relationship.getEndNode().getName()));
                }
            });
            relationshipRepository.saveAll(graphRelationships);
        }
        return ArangodbEntityConvert.INSTANCE.toGraphEntityList(arangodbEntities);
    }

    @Override
    public void update(GraphEntity entity, UpsertExecutor<GraphEntity> upsertWrapper) {
        entity.addLabels(GraphLabelEnum.DYNAMICENTITY.getLabel());
        ArangodbUpsertExecutor<ArangodbEntity> upsertExecutor = getArangodbUpsertExecutor(upsertWrapper);
        dynamicRepository.update(ArangodbEntityConvert.INSTANCE.toArangodbEntity(entity), upsertExecutor);
    }

    @Override
    public void merge(GraphEntity entity, UpsertExecutor<GraphEntity> upsertWrapper) {
        entity.addLabels(GraphLabelEnum.DYNAMICENTITY.getLabel());
        ArangodbUpsertExecutor<ArangodbEntity> arangodbUpsertExecutor = getArangodbUpsertExecutor(upsertWrapper);
        ArangodbQueryExecutor<ArangodbEntity> arangodbjExecutor = new ArangodbQueryExecutor<>(ArangodbEntity.class);
        arangodbjExecutor.apply(arangodbUpsertExecutor);
        List<ArangodbEntity> arangodbEntityList = dynamicRepository.find(arangodbjExecutor);
        if (arangodbEntityList.isEmpty()) {
            this.save(entity);
        } else {
            this.update(entity, upsertWrapper);
        }
    }

    @Override
    public void mergeRelationShip(String relationship, Map<String, Object> entity,
                                  UpsertExecutor<GraphEntity> mergeSqlStart, UpsertExecutor<GraphEntity> mergeSqlEnd) {
        ArangodbUpsertExecutor<ArangodbEntity> arangodbUpsertExecutor = getArangodbUpsertExecutor(mergeSqlStart);
        ArangodbQueryExecutor<ArangodbEntity> arangodbjExecutor = new ArangodbQueryExecutor<>(ArangodbEntity.class);
        arangodbjExecutor.apply(arangodbUpsertExecutor);
        arangodbjExecutor.setRelationEntityClass(ArangodbEntityRelationship.class);
        List<ArangodbEntity> arangodbEntityList = dynamicRepository.find(arangodbjExecutor);

        ArangodbUpsertExecutor<ArangodbEntity> arangodbUpsertExecutorEnd = getArangodbUpsertExecutor(mergeSqlEnd);
        ArangodbQueryExecutor<ArangodbEntity> arangodbjExecutorEnd = new ArangodbQueryExecutor<>(ArangodbEntity.class);
        arangodbjExecutorEnd.apply(arangodbUpsertExecutorEnd);
        arangodbjExecutorEnd.setRelationEntityClass(ArangodbEntityRelationship.class);
        List<ArangodbEntity> arangodbEntityListEnd = dynamicRepository.find(arangodbjExecutorEnd);

        List<ArangodbEntityRelationship> graphRelationships = Lists.newArrayList();
        for (ArangodbEntity arangodbEntity : arangodbEntityList) {
            Collection<ArangodbEntityRelationship> dynamicEntityRelationships = arangodbEntity.getDynamicEntityRelationships();
            if (dynamicEntityRelationships == null) {
                dynamicEntityRelationships = Lists.newArrayList();
            }
            Map<String, List<ArangodbEntityRelationship>> idMap = dynamicEntityRelationships.stream()
                    .filter(e -> e.getName().equals(relationship))
                    .collect(Collectors.groupingBy(e -> e.getEndNode().getId()));
            for (ArangodbEntity arangodb : arangodbEntityListEnd) {
                List<ArangodbEntityRelationship> arangodbEntityRelationships = idMap.get(arangodb.getId());
                if (arangodbEntityRelationships == null) {
                    ArangodbEntityRelationship arangodbEntityRelationship = new ArangodbEntityRelationship();
                    arangodbEntityRelationship.setName(relationship);
                    arangodbEntityRelationship.setDynamicProperties(entity);
                    arangodbEntityRelationship.setStartNode(arangodbEntity);
                    arangodbEntityRelationship.setEndNode(arangodb);
                    graphRelationships.add(arangodbEntityRelationship);
                }
            }
        }
        relationshipRepository.saveAll(graphRelationships);
    }

    @Override
    public void extractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams) {

    }

    @Override
    public void unExtractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams) {

    }
}
