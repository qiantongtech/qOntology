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


package tech.qiantong.qontology.arangodb.core.wrapper.core;

import java.util.Collection;

/**
 * @description: 集合字段查询
 * @author: jinwang
 * @create: 2025-11-11
 */
public interface ListFieldQuery<T, R, Children> {

    /**
     * 集合字段包含所有
     * @param propertyFunction
     * @param values
     * @return
     */
    Children containsAllIn(R propertyFunction, Collection<?> values);
}
