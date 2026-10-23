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

package tech.qiantong.qontology.graphis.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图谱实体
 *
 * @author wang
 * @date 2025/11/12 14:14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class GraphEntity extends BaseEntity {

    private String id;

    // 实体名称
    private String name;

    // 发布状态
    private Integer releaseStatus;

    // 图ID
    private Long graphId;

    // 标签
    private Set<String> labels;

    // 动态属性
    private Map<String, Object> dynamicProperties;

    // 关系
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Collection<GraphRelationship> graphRelationships;

    public void addLabels(String label) {
        if (this.labels == null) {
            this.labels = Sets.newHashSet();
        }
        this.labels.add(label);
    }

    public void putDynamicProperties(String key, Object value) {
        if (this.dynamicProperties == null) {
            this.dynamicProperties = Maps.newHashMap();
        }
        this.dynamicProperties.put(key, value);
    }

    // 获取实体的属性
    public Object getProperty(String key) {
        return dynamicProperties.get(key);
    }

    public void addRelationship(String relationshipName, GraphEntity endNode) {
        GraphRelationship relationship = new GraphRelationship();
        relationship.setName(relationshipName);
        relationship.setStartNode(this);
        relationship.setEndNode(endNode);
        relationship.setCreateTime(new Date());
        if (this.graphRelationships == null) {
            this.graphRelationships = Lists.newArrayList();
        }
        this.graphRelationships.add(relationship);
    }

    // 合并 relationshipEntityMap
    public void mergeRelationshipEntityMap(Collection<GraphRelationship> otherList) {
        Map<String, List<GraphRelationship>> otherMap = otherList.stream()
                .collect(Collectors.groupingBy(GraphRelationship::getName));

        Map<String, List<GraphRelationship>> relationshipEntityMap = graphRelationships.stream()
                .collect(Collectors.groupingBy(GraphRelationship::getName));

        for (Map.Entry<String, List<GraphRelationship>> entry : otherMap.entrySet()) {
            String key = entry.getKey();
            List<GraphRelationship> value = entry.getValue();
            relationshipEntityMap.computeIfAbsent(key, k -> Lists.newArrayList()).addAll(value);
        }
        graphRelationships = relationshipEntityMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

}
