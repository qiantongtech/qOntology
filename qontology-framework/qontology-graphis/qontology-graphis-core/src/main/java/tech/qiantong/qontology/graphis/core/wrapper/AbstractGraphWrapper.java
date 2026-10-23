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

import lombok.Getter;
import tech.qiantong.qontology.common.utils.LambdaUtils;

import java.util.Collection;

/**
 * 条件查询
 *
 * @author wang
 * @date 2025/11/12 14:32
 **/
@Getter
public class AbstractGraphWrapper<T> implements AbstractExecutor<T> {

    protected AbstractExecutor<T> delegate;

    @Override
    public AbstractExecutor<T> addLabels(String... labels) {
        delegate.addLabels(labels);
        return this;
    }

    @Override
    public AbstractExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        delegate.eq(propertyFunction, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        delegate.like(propertyFunction, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        delegate.gt(propertyFunction, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value) {
        delegate.lt(propertyFunction, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        delegate.in(propertyFunction, values);
        return this;
    }

    @Override
    public AbstractExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        delegate.notIn(propertyFunction, values);
        return this;
    }

    @Override
    public AbstractExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object start, Object end) {
        delegate.between(propertyFunction, start, end);
        return this;
    }

    @Override
    public AbstractExecutor<T> orderByAsc(String propertyName) {
        delegate.orderByAsc(propertyName);
        return this;
    }

    @Override
    public AbstractExecutor<T> orderByDesc(String propertyName) {
        delegate.orderByDesc(propertyName);
        return this;
    }

    @Override
    public AbstractExecutor<T> page(int pageNum, int pageSize) {
        delegate.page(pageNum, pageSize);
        return this;
    }

    @Override
    public AbstractExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values) {
        delegate.any(propertyFunction, values);
        return this;
    }

    @Override
    public AbstractExecutor<T> or() {
        delegate.or();
        return this;
    }

    @Override
    public AbstractExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        delegate.eq(propertyFunction, key, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        delegate.like(propertyFunction, key, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        delegate.gt(propertyFunction, key, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object value) {
        delegate.lt(propertyFunction, key, value);
        return this;
    }

    @Override
    public AbstractExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        delegate.in(propertyFunction, key, values);
        return this;
    }

    @Override
    public AbstractExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        delegate.notIn(propertyFunction, key, values);
        return this;
    }

    @Override
    public AbstractExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Object start, Object end) {
        delegate.between(propertyFunction, key, start, end);
        return this;
    }

    @Override
    public AbstractExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, String key, Collection<?> values) {
        delegate.any(propertyFunction, key, values);
        return this;
    }
}
