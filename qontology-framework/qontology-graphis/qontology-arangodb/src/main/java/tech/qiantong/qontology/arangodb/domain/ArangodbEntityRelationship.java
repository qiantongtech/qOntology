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


package tech.qiantong.qontology.arangodb.domain;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * @author wang
 */
@Data
@Edge("arangodb_entity_relationship") // 声明为边实体（对应 ArangoDB 的边集合）
public class ArangodbEntityRelationship {

    @Id
    private String id;

    private String name;

    // 其他关系属性
    private Map<String, Object> dynamicProperties;

    @From // 起点（对应 Neo4j 的关系起点）
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ArangodbEntity startNode;

    @To // 终点（对应 Neo4j 的关系终点）
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ArangodbEntity endNode;
}
