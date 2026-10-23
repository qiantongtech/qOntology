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


package tech.qiantong.qontology.arangodb.core.executor;

import tech.qiantong.qontology.arangodb.core.wrapper.upsert.LambdaUpsertWrapper;
import tech.qiantong.qontology.graphis.annotation.QueryEngine;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;
import tech.qiantong.qontology.common.utils.LambdaUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * 动态更新
 *
 * @author wang
 * @date 2025/02/27 17:44
 **/
@QueryEngine("arangodb")
public class ArangodbUpsertExecutor<T> extends LambdaUpsertWrapper<T> implements UpsertExecutor<T> {

    public ArangodbUpsertExecutor(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public ArangodbUpsertExecutor<T> addLabels(String... labels) {
        LambdaUtils.PropertyFunction<T, ?> propertyFunction = LambdaUtils.getPropertyFunction(this.entityClass, "labels");
        super.containsAllIn(propertyFunction, Arrays.asList(labels));
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.eq(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.like(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.gt(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.lt(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.in(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.notIn(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object start, Object end) {
        super.between(propertyFunction, start, end);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> orderByAsc(String propertyName) {
        super.orderByAsc(propertyName);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> orderByDesc(String propertyName) {
        super.orderByDesc(propertyName);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> page(int offset, int limit) {
        super.page(offset, limit);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.any(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> or() {
        super.or();
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.eq(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.like(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.gt(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.lt(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.in(propertyFunction, key, values);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.notIn(propertyFunction, key, values);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object start, Object end) {
        super.between(propertyFunction, key, start, end);
        return this;
    }

    @Override
    public ArangodbUpsertExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.any(propertyFunction, key, values);
        return this;
    }
}
