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

package tech.qiantong.qontology.neo4j.core.repository;

import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.types.Relationship;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import tech.qiantong.qontology.neo4j.core.wrapper.query.LambdaQueryWrapper;
import tech.qiantong.qontology.neo4j.core.wrapper.upsert.LambdaUpsertWrapper;
import tech.qiantong.qontology.common.utils.spring.SpringUtils;
import tech.qiantong.qontology.core.page.PageResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wang
 */
public interface BaseRepository<T, ID> extends Neo4jRepository<T, ID> {

    /** 执行写入语句并消费结果，避免通过只读查询接口执行 MERGE。 */
    default void executeWrite(String query, Map<String, Object> params) {
        Neo4jClient client = SpringUtils.getBean(Neo4jClient.class);
        client.query(query).bindAll(params).run();
    }

    default List<T> write(String query, Map<String, Object> params, Class<T> clazz) {
        Neo4jClient client = SpringUtils.getBean(Neo4jClient.class);
        return (List<T>) client.query(query)
                .bindAll(params)
                .fetchAs(clazz)
                .all();
    }

    default List<T> query(String query, Map<String, Object> params, Class<T> clazz) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        return neo4jTemplate.findAll(query, params, clazz);
    }

    /**
     * 根据查询条件查询
     *
     * @param wrapper
     * @return
     */
    default List<T> find(LambdaQueryWrapper<T> wrapper) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        String cypherQuery = wrapper.getCypherQuerySql();
        Map<String, Object> params = wrapper.getParams();
        return neo4jTemplate.findAll(cypherQuery, params, wrapper.getEntityClass());
    }

    /**
     * 分页查询
     *
     * @param wrapper
     * @return
     */
    default PageResult<T> findPage(LambdaQueryWrapper<T> wrapper) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        String cypherQuery = wrapper.getCypherQuerySql();
        Map<String, Object> params = wrapper.getParams();
        List<T> row = neo4jTemplate.findAll(cypherQuery, params, wrapper.getEntityClass());
        Long count = this.count(wrapper);
        return new PageResult<>(row, count);
    }

    default List<T> relationChain(LambdaQueryWrapper<T> wrapper, Integer depth) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        String cypherQuery = wrapper.getRelationChainCypherQuerySql(depth);
        Map<String, Object> params = wrapper.getParams();
        return neo4jTemplate.findAll(cypherQuery, params, wrapper.getEntityClass());
    }

    /**
     * 获取节点list, 所有属性
     *
     * @param wrapper
     * @return
     */
    default List<Map<String, Object>> nodeListMap(LambdaQueryWrapper<T> wrapper, Set<String> labels) {
        Neo4jClient neo4jClient = SpringUtils.getBean(Neo4jClient.class);
        List<Map<String, Object>> collect = neo4jClient.query(wrapper.getNodeQuerySql(labels))
                .bindAll(wrapper.getParams())
                .fetchAs(NodeValue.class)  // 获取 Node 对象
                .all()
                .stream()
                .map(nodeValue -> {
                    HashMap<String, Object> properties = new HashMap<>();
                    InternalNode node = (InternalNode) nodeValue.asNode();
                    properties.put("id", node.id());
                    // 获取节点的属性
                    node.asMap().forEach((key, value) -> properties.put(key, value));
                    return properties;
                })
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据条件查询关系
     *
     * @param wrapper
     * @return
     */
    default List<Map<String, Object>> relListMap(LambdaQueryWrapper<T> wrapper, Set<String> labels) {
        Neo4jClient neo4jClient = SpringUtils.getBean(Neo4jClient.class);
        Collection<Map<String, Object>> result = neo4jClient.query(wrapper.getRelQuerySql(labels))
                .bindAll(wrapper.getParams())
                .fetch()  // 删除 fetchAs(Record.class)，直接使用默认映射
                .all();
        ArrayList<Map<String, Object>> relationships = new ArrayList<>();
        for (Map<String, Object> objectMap : result) {
            InternalNode n = (InternalNode) objectMap.get("n");
            InternalNode m = (InternalNode) objectMap.get("m");
            Relationship r = (Relationship) objectMap.get("r");

            if (m != null) {
                Map<String, Object> map = m.asMap();
                HashMap<String, Object> relationshipMap = new HashMap<>();
                relationshipMap.put("startId", n.id());
                relationshipMap.put("endId", m.id());
                relationshipMap.put("startName", n.asMap().get("name"));
                relationshipMap.put("endName", map.get("name"));
                relationshipMap.put("id", r.id());
                relationshipMap.put("relationType", r.type());
                relationships.add(relationshipMap);
            }
        }
        return relationships;
    }

    /**
     * 根据条件修改节点属性
     *
     * @param wrapper
     * @return
     */
    // TODO 临时解决方案，后续进行修改
    default List<T> update(T entity, LambdaUpsertWrapper<T> wrapper) {
        Neo4jClient client = SpringUtils.getBean(Neo4jClient.class);
        String cypher = wrapper.updateSql(entity);
        return (List<T>) client.query(cypher)
                .fetchAs(wrapper.getEntityClass())
                .all();
    }

    default void merge(T entity, LambdaUpsertWrapper<T> wrapper) {
        String cypherQuery = wrapper.mergeSql(entity);
        executeWrite(cypherQuery, wrapper.getParams());
    }

    /**
     * 统计查询
     *
     * @param wrapper
     * @return
     */
    default Long count(LambdaQueryWrapper<T> wrapper) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        return neo4jTemplate.count(wrapper.getCypherCountQuerySql(), wrapper.getParams());
    }

    /**
     * 统计关系数量
     *
     * @param wrapper
     * @return
     */
    default Long countRelation(LambdaQueryWrapper<T> wrapper) {
        Neo4jTemplate neo4jTemplate = SpringUtils.getBean(Neo4jTemplate.class);
        return neo4jTemplate.count(wrapper.getCypherCountRelationQuerySql(), wrapper.getParams());
    }

    /**
     * 根据节点id删除节点和相关关系
     *
     * @param nodeId
     */
    @Query("MATCH (n) WHERE id(n) = $nodeId DETACH DELETE n")
    void deleteNodeById(@Param("nodeId") Long nodeId);

    /**
     * 根据节点id删除节点和相关关系
     *
     * @param nodeIds
     */
    @Query("MATCH (n) WHERE id(n) IN $nodeIds DETACH DELETE n")
    void deleteNodeByIds(@Param("nodeIds") List<Long> nodeIds);

    /**
     * 非结构化抽取根据类型和任务id删除节点和相关关系
     *
     * @param taskId
     */
    @Query("MATCH (a:ExtUnStruck {dynamic_properties_task_id: $taskId}) DETACH DELETE a")
    void deleteEntityExtUnStruckByTaskId(@Param("taskId") Long taskId);

    /**
     * 结构化抽取根据类型和任务id删除节点和相关关系
     *
     * @param taskId
     */
    @Query("MATCH (a:ExtStruck {dynamic_properties_task_id: $taskId}) DETACH DELETE a")
    void deleteEntityExtStruckByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据节点id和属性的id删除属性
     */
//    @Upsert("MATCH (n) WHERE id(n) = $nodeId REMOVE n.$attributeKey RETURN n")
//    void deleteNodeAttributeById(@Param("nodeId") Long nodeId, @Param("attributeKey") String attributeKey);
    default void deleteNodeAttributeById(Long nodeId, String attributeKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("nodeId", nodeId);
        String query = "MATCH (n) WHERE id(n) =  $ nodeId REMOVE n.dynamic_properties_" + attributeKey + " ";
        Neo4jClient client = SpringUtils.getBean(Neo4jClient.class);
        client.query(query).bindAll(params).run();
    }

    /**
     * 根据关系id删除关系
     *
     * @param relationshipId
     */
    @Query("MATCH ()-[r]->() WHERE id(r) = $relationshipId DELETE r")
    void deleteRelationshipById(@Param("relationshipId") Long relationshipId);

    /**
     * 根据关系id集合删除关系
     *
     * @param relationshipIds
     */
    @Query("MATCH ()-[r]->() WHERE id(r) IN $relationshipIds DELETE r")
    void deleteRelationshipsByIds(@Param("relationshipIds") List<Long> relationshipIds);
}
