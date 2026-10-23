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

import tech.qiantong.qontology.arangodb.core.wrapper.query.LambdaQueryWrapper;
import tech.qiantong.qontology.graphis.annotation.QueryEngine;
import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.common.utils.LambdaUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * 动态查询
 *
 * @author wang
 * @date 2025/02/27 17:44
 **/
@QueryEngine("arangodb")
public class ArangodbQueryExecutor<T> extends LambdaQueryWrapper<T> implements QueryExecutor<T> {

    public ArangodbQueryExecutor(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public ArangodbQueryExecutor<T> addLabels(String... labels) {
        // 指定字段作为标签，为了兼容neo4j。
        LambdaUtils.PropertyFunction<T, ?> propertyFunction = LambdaUtils.getPropertyFunction(this.entityClass, "labels");
        super.containsAllIn(propertyFunction, Arrays.asList(labels));
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.eq(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.like(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.gt(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        super.lt(propertyFunction, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.in(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.notIn(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object start, Object end) {
        super.between(propertyFunction, start, end);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> orderByAsc(String propertyName) {
        super.orderByAsc(propertyName);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> orderByDesc(String propertyName) {
        super.orderByDesc(propertyName);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> page(int offset, int limit) {
        super.page(offset, limit);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        super.any(propertyFunction, values);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> or() {
        super.or();
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.eq(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.like(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.gt(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        super.lt(propertyFunction, key, value);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.in(propertyFunction, key, values);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.notIn(propertyFunction, key, values);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object start, Object end) {
        super.between(propertyFunction, key, start, end);
        return this;
    }

    @Override
    public ArangodbQueryExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        super.any(propertyFunction, key, values);
        return this;
    }
}
