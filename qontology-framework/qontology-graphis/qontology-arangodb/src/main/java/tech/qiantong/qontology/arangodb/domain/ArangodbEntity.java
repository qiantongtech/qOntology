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

import com.arangodb.springframework.annotation.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 对接的实体
 *
 * @author wang
 * @date 2025/11/10 10:14
 **/
@Data
@Document("arangodb_entity")
@PersistentIndex(fields = {"surname"})
public class ArangodbEntity extends BaseEntity {
    @Id
    private String id;

    @ArangoId
    private String arangoId;

    private String name;

    @Field("release_status")
    private Integer releaseStatus;

    @Field("graph_id")
    private Long graphId;

    @Field("labels")
    private Set<String> labels;

    @Field("dynamic_properties")
    private Map<String, Object> dynamicProperties = Maps.newHashMap();

    @Relations(edges = ArangodbEntityRelationship.class, lazy = true)
    private Collection<ArangodbEntity> childs;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Collection<ArangodbEntityRelationship> dynamicEntityRelationships;

    // 获取实体的属性
    public Object getProperty(String key) {
        return dynamicProperties.get(key);
    }

    public void putDynamicProperties(String key, Object value) {
        this.dynamicProperties.put(key, value);
    }

    public void addLabels(String label) {
        if (this.labels == null) {
            this.labels = Sets.newHashSet();
        }
        this.labels.add(label);
    }

}
