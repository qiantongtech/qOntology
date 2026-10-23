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

package tech.qiantong.qontology.neo4j.core.domain;

import lombok.Data;

/**
 * 动态参数
 * @author wang
 * @date 2025/11/14 11:28
 **/
@Data
public class Neo4jCondition {

    /** 属性名 */
    private String propertyName;

    /** 列名 */
    private String column;

    /** map属性名 */
    private String key;

    /** 操作符 */
    private String operator;

    /** 值 */
    private Object value;

    /** 参数名 */
    private String parameterName;

    /** 连接符 */
    private String separator;
}
