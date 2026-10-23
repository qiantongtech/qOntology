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
 * @description: 普通字段查询
 * @author: jinwang
 * @create: 2025-11-11
 */
 public interface FieldQuery<T, R, Children> {
    Children eq(R propertyName, Object value);

    Children like(R propertyName, Object value);

    Children gt(R propertyName, Object value);

    Children ge(R propertyName, Object value);

    Children lt(R propertyName, Object value) ;

    Children le(R propertyName, Object value);

    Children in(R propertyName, Collection<?> values);

    Children notIn(R propertyName, Collection<?> values);

    Children between(R propertyName, Object start, Object end);

    /**
     * 模糊匹配集合
     * @param propertyName
     * @param value
     * @return
     */
    Children any(R propertyName, Collection<?> value);

    Children or();
}
