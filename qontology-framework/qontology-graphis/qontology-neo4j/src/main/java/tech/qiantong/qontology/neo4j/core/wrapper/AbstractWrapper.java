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

package tech.qiantong.qontology.neo4j.core.wrapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import tech.qiantong.qontology.neo4j.core.domain.Neo4jCondition;
import tech.qiantong.qontology.neo4j.utils.AnnotationProcessorUtils;
import tech.qiantong.qontology.common.utils.StringUtils;

import java.util.*;

/**
 * 这是一个抽象基类，提供了所有 Wrapper 类共有的方法和属性。它定义了条件构造的基本逻辑
 *
 * @author wang
 * @date 2025/02/27 17:44
 **/
@Getter
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>> implements MapField<T, R, Children> {
    @SuppressWarnings("unchecked")
    protected final Children typedThis = (Children) this;

    protected final Class<T> entityClass;
    protected List<Neo4jCondition> conditions = Lists.newArrayList();
    protected Set<String> labels = Sets.newHashSet();
    protected Map<String, Object> params = Maps.newHashMap();
    protected String orderBy;
    protected int offset;
    protected int limit;

    public AbstractWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // 处理多标签查询
    public Children addLabels(String... labels) {
        this.labels.addAll(Arrays.asList(labels));
        return typedThis;
    }

    // 以下为字符串属性名方法
    public Children eq(R propertyName, Object value) {
        return addCondition(propertyName, "=", value);
    }

    public Children like(R propertyName, Object value) {
        return addCondition(propertyName, "CONTAINS", value);
    }

    public Children gt(R propertyName, Object value) {
        return addCondition(propertyName, ">", value);
    }

    public Children ge(R propertyName, Object value) {
        return addCondition(propertyName, ">=", value);
    }

    public Children lt(R propertyName, Object value) {
        return addCondition(propertyName, "<", value);
    }

    public Children le(R propertyName, Object value) {
        return addCondition(propertyName, "<=", value);
    }

    public Children in(R propertyName, Collection<?> values) {
        return addCondition(propertyName, "IN", values);
    }

    public Children notIn(R propertyName, Collection<?> values) {
        return addCondition(propertyName, "NOT IN", values);
    }

    public Children between(R propertyName, Object start, Object end) {
        ge(propertyName, start);
        le(propertyName, end);
        return typedThis;
    }

    public Children any(R propertyName, Collection<?> value) {
        return addCondition(propertyName, "ANY", value);
    }

    public Children or() {
        Neo4jCondition condition = new Neo4jCondition();
        condition.setSeparator("OR");
        conditions.add(condition);
        return typedThis;
    }

    // map属性名方法
    @Override
    public Children eq(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, "=", value);
    }

    @Override
    public Children like(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, "CONTAINS", value);
    }

    @Override
    public Children gt(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, ">", value);
    }

    @Override
    public Children ge(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, ">=", value);
    }

    @Override
    public Children lt(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, "<", value);
    }

    @Override
    public Children le(R propertyName, String key, Object value) {
        return addCondition(propertyName, key, "<=", value);
    }

    @Override
    public Children in(R propertyName, String key, Collection<?> values) {
        return addCondition(propertyName, key, "IN", values);
    }

    @Override
    public Children notIn(R propertyName, String key, Collection<?> values) {
        return addCondition(propertyName, key, "NOT IN", values);
    }

    @Override
    public Children between(R propertyName, String key, Object start, Object end) {
        ge(propertyName, key, start);
        le(propertyName, key, end);
        return typedThis;
    }

    @Override
    public Children any(R propertyName, String key, Collection<?> value) {
        return addCondition(propertyName, key, "ANY", value);
    }


    public Children orderByAsc(String propertyName) {
        this.orderBy = "n." + propertyName + " ASC";
        return typedThis;
    }

    public Children orderByDesc(String propertyName) {
        this.orderBy = "n." + propertyName + " DESC";
        return typedThis;
    }

    public Children page(int offset, int limit) {
        this.offset = (offset - 1) * limit;
        this.limit = limit;
        return typedThis;
    }

    public void apply(AbstractWrapper<?, ?, ?> executor) {
        executor.getConditions().forEach(condition -> {
            Neo4jCondition newCondition = new Neo4jCondition();
            if (condition.getSeparator() == null) {
                // 重新获取column
                String propertyName = condition.getColumn();
                String column = this.getColumn(propertyName);
                newCondition.setPropertyName(column);
                if (condition.getKey() != null) {
                    newCondition.setPropertyName(column + condition.getKey());
                }
                newCondition.setColumn(column);
                newCondition.setKey(condition.getKey());
                newCondition.setOperator(condition.getOperator());
                newCondition.setValue(condition.getValue());
                newCondition.setParameterName(condition.getParameterName());
            } else {
                newCondition.setSeparator(condition.getSeparator());
            }
            conditions.add(newCondition);

        });
        //this.conditions = executor.getConditions();
        this.labels = executor.getLabels();
        this.params = executor.getParams();
        this.orderBy = executor.getOrderBy();
        this.offset = executor.getOffset();
        this.limit = executor.getLimit();
    }

    protected String columnToString(R column) {
        String propertyName = (String) column;
        return this.getColumn(propertyName);
    }

    protected String getColumn(String propertyName) {
        String property = AnnotationProcessorUtils.getProperty(this.entityClass, propertyName);
        String compositeProperties = AnnotationProcessorUtils.getCompositeProperties(this.entityClass, propertyName);
        if (StringUtils.isNotEmpty(property)) {
            return property;
        }
        if (StringUtils.isNotEmpty(compositeProperties)) {
            return compositeProperties;
        }
        return propertyName;
    }

    // 公共条件构建方法
    protected int parameterCount = 0;

    private Children addCondition(R propertyName, String operator, Object value) {
        String column = this.columnToString(propertyName);
        String parameterName = column + parameterCount++;

        Neo4jCondition condition = new Neo4jCondition();
        condition.setPropertyName(column);
        condition.setColumn(column);
        condition.setOperator(operator);
        condition.setValue(value);
        condition.setParameterName(parameterName);
        conditions.add(condition);
        params.put(parameterName, value);
        return typedThis;
    }

    // map属性名条件构建方法
    private Children addCondition(R propertyName, String key, String operator, Object value) {
        String column = this.columnToString(propertyName);
        String parameterName = column + parameterCount++;

        Neo4jCondition condition = new Neo4jCondition();
        condition.setPropertyName(column + key);
        condition.setColumn(column);
        condition.setKey(key);
        condition.setOperator(operator);
        condition.setValue(value);
        condition.setParameterName(parameterName);
        conditions.add(condition);
        params.put(parameterName, value);
        return typedThis;
    }
}
