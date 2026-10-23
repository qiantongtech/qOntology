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
 * @program: qKnow-pro
 * @description: 通用执行器接口
 * @author: jinwang
 * @create: 2025-11-12
 */
public interface AbstractExecutor<T> extends MapField<T> {
    /**
     * 添加标签
     */
    AbstractExecutor<T> addLabels(String... labels);

    /**
     * 等于
     */
    AbstractExecutor<T> eq(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value);

    /**
     * 模糊匹配
     */
    AbstractExecutor<T> like(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value);

    /**
     * 大于
     */
    AbstractExecutor<T> gt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value);

    /**
     * 小于
     */
    AbstractExecutor<T> lt(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object value);

    /**
     * 匹配范围
     */
    AbstractExecutor<T> in(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values);

    /**
     * 不匹配范围
     */
    AbstractExecutor<T> notIn(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values);

    /**
     * 匹配范围
     */
    AbstractExecutor<T> between(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Object start, Object end);

    /**
     * 升序
     */
    AbstractExecutor<T> orderByAsc(String propertyName);

    /**
     * 降序
     */
    AbstractExecutor<T> orderByDesc(String propertyName);

    /**
     * 分页
     */
    AbstractExecutor<T> page(int offset, int limit);

    /**
     * 匹配任意
     */
    AbstractExecutor<T> any(LambdaUtils.PropertyFunction<T, ?> propertyFunction, Collection<?> values);

    /**
     * 或
     */
    AbstractExecutor<T> or();
}
