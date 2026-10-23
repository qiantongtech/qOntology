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

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.Map;

/**
 * 图谱关系
 *
 * @author wang
 * @date 2025/11/12 14:14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class GraphRelationship extends BaseEntity {

    private String id;

    // 关系名称
    private String name;

    // 动态属性
    private Map<String, Object> dynamicProperties;

    // 关系起点
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GraphEntity startNode;

    // 关系终点
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GraphEntity endNode;

    public void putDynamicProperties(String key, Object value) {
        if (this.dynamicProperties == null) {
            this.dynamicProperties = Maps.newHashMap();
        }
        this.dynamicProperties.put(key, value);
    }
}
