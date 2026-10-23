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

package tech.qiantong.qontology.graphis.core.wrapper;

import tech.qiantong.qontology.common.utils.LambdaUtils;

import java.util.Collection;

/**
 * @description: map字段查询
 * @author: jinwang
 * @create: 2025-11-11
 */
public interface MapField<T> {

    AbstractExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value);

    AbstractExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value);

    AbstractExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value);

    AbstractExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value);

    AbstractExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values);

    AbstractExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values);

    AbstractExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object start, Object end);

    AbstractExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values);
}
