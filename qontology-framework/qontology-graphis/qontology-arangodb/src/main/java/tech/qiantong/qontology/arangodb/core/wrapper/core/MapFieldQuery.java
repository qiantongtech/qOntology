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
 * @description: map字段查询
 * @author: jinwang
 * @create: 2025-11-11
 */
public interface MapFieldQuery<T, R, Children> {

     Children eq(R propertyFunction, String key, Object value);

     Children like(R propertyFunction, String key, Object value);

     Children gt(R propertyFunction, String key, Object value);

     Children ge(R propertyFunction, String key, Object value);

     Children lt(R propertyFunction, String key, Object value);

     Children le(R propertyFunction, String key, Object value);

     Children in(R propertyFunction, String key, Collection<?> values);

     Children notIn(R propertyFunction, String key, Collection<?> values);

     Children between(R propertyFunction, String key, Object start, Object end);

     /**
      * 模糊匹配集合
      * @param propertyName
      * @param value
      * @return
      */
     Children any(R propertyName,  String key, Collection<?> value);
}
