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

package tech.qiantong.qontology.neo4j.template;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.graphis.core.wrapper.query.GraphQueryWrapper;
import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.GraphUpsertWrapper;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.repository.GraphisRepository;
import tech.qiantong.qontology.neo4j.convert.Neo4jEntityConvert;
import tech.qiantong.qontology.neo4j.core.executor.Neo4jQueryExecutor;
import tech.qiantong.qontology.neo4j.core.executor.Neo4jUpsertExecutor;
import tech.qiantong.qontology.neo4j.core.wrapper.SpecialWrapper;
import tech.qiantong.qontology.neo4j.domain.DynamicEntity;
import tech.qiantong.qontology.neo4j.domain.Neo4jRelationship;
import tech.qiantong.qontology.neo4j.domain.relationship.DynamicEntityRelationship;
import tech.qiantong.qontology.neo4j.repository.DynamicRepository;
import tech.qiantong.qontology.neo4j.utils.Utils;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模版实现
 *
 * @author wang
 * @date 2025/11/12 15:14
 **/
@Component
@ConditionalOnProperty(prefix = "graphis", name = "type", havingValue = "neo4j")
public class Neo4jGraphisTemplate implements GraphisRepository {

    @Resource
    private DynamicRepository dynamicRepository;

    private Neo4jQueryExecutor<DynamicEntity> getNeo4jQueryExecutor(QueryExecutor<GraphEntity> queryWrapper) {
        GraphQueryWrapper<GraphEntity> graphWrapper = (GraphQueryWrapper<GraphEntity>) queryWrapper;
        Neo4jQueryExecutor<GraphEntity> delegate = (Neo4jQueryExecutor<GraphEntity>) graphWrapper.getDelegate();
        Neo4jQueryExecutor<DynamicEntity> neo4jExecutor = new Neo4jQueryExecutor<>(DynamicEntity.class);
        neo4jExecutor.apply(delegate);
        return neo4jExecutor;
    }

    private Neo4jUpsertExecutor<DynamicEntity> getNeo4jUpsertExecutor(UpsertExecutor<GraphEntity> upsertWrapper) {
        GraphUpsertWrapper<GraphEntity> graphWrapper = (GraphUpsertWrapper<GraphEntity>) upsertWrapper;
        Neo4jUpsertExecutor<GraphEntity> delegate = (Neo4jUpsertExecutor<GraphEntity>) graphWrapper.getDelegate();
        Neo4jUpsertExecutor<DynamicEntity> neo4jExecutor = new Neo4jUpsertExecutor<>(DynamicEntity.class);
        neo4jExecutor.apply(delegate);
        return neo4jExecutor;
    }

    @Override
    public List<GraphEntity> query(String sql, Map<String, Object> params) {
        List<DynamicEntity> dynamicEntityList = dynamicRepository.query(sql, params, DynamicEntity.class);
        dynamicEntityList.forEach(DynamicEntity::toRelationships);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }


    @Override
    public GraphEntity findEntityById(String id) {
        DynamicEntity dynamicEntity = dynamicRepository.findById(Long.parseLong(id)).orElse(new DynamicEntity());
        dynamicEntity.toRelationships();
        return Neo4jEntityConvert.INSTANCE.toGraphEntity(dynamicEntity);
    }

    @Override
    public List<GraphEntity> findEntityByIds(Collection<String> idList) {
        List<Long> ids = idList.stream()
                .map(Long::parseLong).collect(Collectors.toList());
        List<DynamicEntity> dynamicEntityList = dynamicRepository.findAllById(ids);
        dynamicEntityList.forEach(DynamicEntity::toRelationships);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }

    @Override
    public List<GraphEntity> find(QueryExecutor<GraphEntity> queryWrapper) {
        Neo4jQueryExecutor<DynamicEntity> queryExecutor = getNeo4jQueryExecutor(queryWrapper);
        List<DynamicEntity> dynamicEntityList = dynamicRepository.find(queryExecutor);
        dynamicEntityList.forEach(DynamicEntity::toRelationships);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }

    @Override
    public List<GraphEntity> findAll() {
        List<DynamicEntity> dynamicEntityList = dynamicRepository.findAll();
        dynamicEntityList.forEach(DynamicEntity::toRelationships);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }

    @Override
    public PageResult<GraphEntity> findPage(QueryExecutor<GraphEntity> queryWrapper) {
        Neo4jQueryExecutor<DynamicEntity> queryExecutor = getNeo4jQueryExecutor(queryWrapper);
        PageResult<DynamicEntity> dynamicEntityPageResult = dynamicRepository.findPage(queryExecutor);
        dynamicEntityPageResult.getList().forEach(DynamicEntity::toRelationships);
        List<GraphEntity> graphEntityList = Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityPageResult.getList());
        return new PageResult<>(graphEntityList, dynamicEntityPageResult.getTotal());
    }

    @Override
    public List<GraphEntity> relationChain(QueryExecutor<GraphEntity> queryWrapper, Integer depth) {
        Neo4jQueryExecutor<DynamicEntity> queryExecutor = getNeo4jQueryExecutor(queryWrapper);
        List<DynamicEntity> dynamicEntityList = dynamicRepository.relationChain(queryExecutor, depth);
        dynamicEntityList.forEach(DynamicEntity::toRelationships);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }

    @Override
    public Long count(QueryExecutor<GraphEntity> queryWrapper) {
        Neo4jQueryExecutor<DynamicEntity> queryExecutor = getNeo4jQueryExecutor(queryWrapper);
        return dynamicRepository.count(queryExecutor);
    }

    @Override
    public Long countRelation(QueryExecutor<GraphEntity> queryWrapper) {
        Neo4jQueryExecutor<DynamicEntity> queryExecutor = getNeo4jQueryExecutor(queryWrapper);
        return dynamicRepository.countRelation(queryExecutor);
    }

    @Override
    public GraphEntity save(GraphEntity entity) {
        DynamicEntity dynamicEntity = Neo4jEntityConvert.INSTANCE.toDynamicEntity(entity);
        dynamicEntity.toRelationshipEntityMap();
        Map<String, List<DynamicEntityRelationship>> relationshipEntityMap = dynamicEntity.getRelationshipEntityMap();
        if (relationshipEntityMap != null) {
            relationshipEntityMap.forEach((name, list) -> {
                list.forEach(r -> {
                    DynamicEntity endNode = dynamicRepository.findById(r.getEndNode().getId()).orElse(new DynamicEntity());
                    r.setEndNode(endNode);
                });
            });
        }
        dynamicEntity.setRelationshipEntityMap(relationshipEntityMap);
        dynamicRepository.save(dynamicEntity);
        return Neo4jEntityConvert.INSTANCE.toGraphEntity(dynamicEntity);
    }

    @Override
    public List<GraphEntity> saveAll(List<GraphEntity> entityList) {
        List<DynamicEntity> dynamicEntityList = Neo4jEntityConvert.INSTANCE.toDynamicEntityList(entityList);
        Map<Long, DynamicEntity> map = dynamicEntityList
                .stream().filter(dynamicEntity -> dynamicEntity.getId() != null)
                .collect(Collectors.toMap(DynamicEntity::getId, e -> e, (e1, e2) -> e1));

        // 将关系中的节点指向，设置为正确的，避免丢失关系。
        dynamicEntityList.forEach(e -> {
            e.toRelationshipEntityMap();
            Map<String, List<DynamicEntityRelationship>> relationshipEntityMap = e.getRelationshipEntityMap();
//            if (relationshipEntityMap != null) {
//                relationshipEntityMap.forEach((name, list) -> {
//                    list.forEach(r -> {
//                        if (r.getEndNode().getId() != null) {
//                            DynamicEntity endNode = map.get(r.getEndNode().getId());
//                            if (endNode == null) {
//                                endNode = dynamicRepository.findById(r.getEndNode().getId()).orElse(new DynamicEntity());
//                            }
//                            r.setEndNode(endNode);
//                        }
//                    });
//                });
//            }
            if (relationshipEntityMap != null && !relationshipEntityMap.isEmpty()) {
                Map<String, List<DynamicEntityRelationship>> validMap = new HashMap<>();

                relationshipEntityMap.forEach((name, list) -> {
                    // 处理每个关系类型
                    List<DynamicEntityRelationship> validList = list.stream()
                            .filter(Objects::nonNull)
                            .filter(r -> r.getEndNode() != null)
                            .map(r -> {
                                Long endNodeId = r.getEndNode().getId();
                                if (!Objects.isNull(endNodeId)) {
                                    DynamicEntity endNode = map.computeIfAbsent(endNodeId,
                                            id -> dynamicRepository.findById(id).orElse(null));
                                    if (endNode != null) {
                                        r.setEndNode(endNode);
                                        return r;
                                    }
                                }
                                return r;
                            })
                            .collect(Collectors.toList());

                    // 重要：将处理后的列表放入 validMap
                    if (!validList.isEmpty()) {
                        validMap.put(name, validList);
                    }
                });
                // 重要：将处理后的 validMap 设置回实体
                e.setRelationshipEntityMap(validMap);
            } else {
                // 如果关系映射为空，设置为空映射或 null
                e.setRelationshipEntityMap(relationshipEntityMap);
            }
            if (CollectionUtils.isNotEmpty(e.getRelationships())) {
                List<Neo4jRelationship> relationships = e.getRelationships().stream()
                        .filter(rel -> rel.getStartNode() != null && rel.getEndNode() != null)
                        .collect(Collectors.toList());
                e.setRelationships(relationships);
            }
        });
        dynamicRepository.saveAll(dynamicEntityList);
        return Neo4jEntityConvert.INSTANCE.toGraphEntityList(dynamicEntityList);
    }

    @Override
    public void update(GraphEntity entity, UpsertExecutor<GraphEntity> upsertWrapper) {
        Neo4jUpsertExecutor<DynamicEntity> upsertExecutor = getNeo4jUpsertExecutor(upsertWrapper);
        dynamicRepository.update(Neo4jEntityConvert.INSTANCE.toDynamicEntity(entity), upsertExecutor);
    }

    @Override
    public void merge(GraphEntity entity, UpsertExecutor<GraphEntity> upsertWrapper) {
        Neo4jUpsertExecutor<DynamicEntity> upsertExecutor = getNeo4jUpsertExecutor(upsertWrapper);
        dynamicRepository.merge(Neo4jEntityConvert.INSTANCE.toDynamicEntity(entity), upsertExecutor);
    }

    @Override
    public void mergeRelationShip(String relationship, Map<String, Object> entity, UpsertExecutor<GraphEntity> mergeSqlStart, UpsertExecutor<GraphEntity> mergeSqlEnd) {
        Neo4jUpsertExecutor<DynamicEntity> upsertStart = getNeo4jUpsertExecutor(mergeSqlStart);
        Neo4jUpsertExecutor<DynamicEntity> upsertEnd = getNeo4jUpsertExecutor(mergeSqlEnd);
        String sql = SpecialWrapper.mergeRelationship(relationship, entity, upsertStart.mergeRelationship("a"), upsertEnd.mergeRelationship("b"));
        dynamicRepository.executeWrite(sql, Maps.newHashMap());
    }


    @Override
    public void deleteEntityById(String id) {
        dynamicRepository.deleteNodeById(Long.parseLong(id));
    }

    @Override
    public void deleteEntityByIds(Collection<String> idList) {
        List<Long> ids = idList.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dynamicRepository.deleteNodeByIds(ids);
    }

    @Override
    public void deleteRelationshipById(String id) {
        dynamicRepository.deleteRelationshipById(Long.parseLong(id));
    }

    @Override
    public void deleteRelationshipByIds(Collection<String> idList) {
        List<Long> ids = idList.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dynamicRepository.deleteRelationshipsByIds(ids);
    }

    @Override
    public void deleteEntityAttributeById(String id, String attributeKey) {
        dynamicRepository.deleteNodeAttributeById(Long.parseLong(id), attributeKey);
    }

    @Override
    public List<GraphEntity> findRelationName(List<String> relationNameList) {
        // 将 relationNameList 中的每个值加上引号，并转换成一个有效的Cypher查询
        String formattedRelationNames = relationNameList.stream()
                .map(name -> "'" + name + "'")  // 给每个字符串加上双引号
                .collect(Collectors.joining(", "));  // 将所有字符串连接成一个以逗号分隔的列表

        String sql = "MATCH (a)-[r]->(b) WHERE ANY(keyword IN [" + formattedRelationNames + "] WHERE type(r) CONTAINS keyword) UNWIND [a, b] AS node " +
                "RETURN DISTINCT node, collect(r), collect(b)";
        return query(sql, Maps.newHashMap());
    }

    @Override
    public List<GraphEntity> globalSearch(String... name) {
        String aql = "MATCH (n:DynamicEntity) WHERE any(key IN keys(n) WHERE any(s IN "
                + Utils.getValueString(Lists.newArrayList(name))
                + " WHERE n[key] CONTAINS s))  RETURN n";
        return query(aql, Maps.newHashMap());
    }

    /**
     * 根据任务id删除之前的抽取结果 非结构化抽取
     *
     * @param taskId
     * @return
     */
    @Override
    public AjaxResult deleteExtUnStruck(Long taskId) {
//        String query = "MATCH (a:ExtUnStruck {dynamic_properties_task_id: $taskId}) DETACH DELETE a";
//        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
//        paramMap.put("taskId", taskId);
//        dynamicRepository.query(query, paramMap, DynamicEntity.class);
        dynamicRepository.deleteEntityExtUnStruckByTaskId(taskId);
        return AjaxResult.success("删除成功");
    }

    /**
     * 根据任务id删除之前的抽取结果
     *
     * @param taskId
     * @return
     */
    @Override
    public AjaxResult deleteExtStruck(Long taskId) {
//        String query = "MATCH (a:ExtStruck {dynamic_properties_task_id: $taskId}) DETACH DELETE a";
//        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
//        paramMap.put("taskId", taskId);
//        dynamicRepository.query(query, paramMap, DynamicEntity.class);
        dynamicRepository.deleteEntityExtStruckByTaskId(taskId);
        return AjaxResult.success("删除成功");
    }

    //结构化三元组添加
    @Override
    public void extractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams) {
        //结构化抽取三元组关系添加
        String createRelationshipQuery = "MATCH (n:ExtExtraction {data_id: $startId, task_id: $task_id, table_name: $startTableName}), " +
                "(m:ExtExtraction {data_id: $endId, task_id: $task_id, table_name: $endTableName}) " +
                "MERGE (n)-[r:" + relationship + "]->(m) " +
                "ON CREATE SET r.task_id = $task_id " +
                "RETURN r, ID(r) AS relationshipId;";

        // 执行创建关系查询
        dynamicRepository.executeWrite(createRelationshipQuery, relationshipParams);
    }

    //非结构化三元组添加
    @Override
    public void unExtractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams) {
        //非结构化抽取三元组关系添加
        String query = "MATCH (a:ExtExtraction), (b:ExtExtraction) " +
                "WHERE ID(a) = $startId AND ID(b) = $endId " +
                "MERGE (a)-[r:" + relationship + "]->(b) " +
                "ON CREATE SET r.task_id = $task_id " +
                "RETURN r, ID(r) AS relationshipId;";

        // 执行创建关系查询
        dynamicRepository.executeWrite(query, relationshipParams);
    }

}
