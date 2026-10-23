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


package tech.qiantong.qontology.arangodb.core.wrapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import tech.qiantong.qontology.arangodb.core.domain.ArangodbCondition;
import tech.qiantong.qontology.arangodb.core.wrapper.core.FieldQuery;
import tech.qiantong.qontology.arangodb.core.wrapper.core.ListFieldQuery;
import tech.qiantong.qontology.arangodb.core.wrapper.core.MapFieldQuery;
import tech.qiantong.qontology.arangodb.utils.Utils;
import tech.qiantong.qontology.common.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>> implements FieldQuery<T, R, Children>, ListFieldQuery<T, R, Children>, MapFieldQuery<T, R, Children> {
    @SuppressWarnings("unchecked")
    protected final Children typedThis = (Children) this;

    protected Class<T> entityClass;
    protected List<ArangodbCondition> conditions = Lists.newArrayList();
    protected Map<String, Object> params = Maps.newHashMap();
    protected String orderBy;
    @Setter
    protected Class<?> relationEntityClass;
    protected int offset = 0;
    protected int limit = 0;

    public AbstractWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Children containsAllIn(R propertyName, Collection<?> values) {
        return addReversCondition(propertyName, "ALL IN", values);
    }

    @Override
    public Children eq(R propertyName, Object value) {
        return addCondition(propertyName, "==", value);
    }

    @Override
    public Children like(R propertyName, Object value) {
        return addCondition(propertyName, "like", "%" + value + "%");
    }

    @Override
    public Children gt(R propertyName, Object value) {
        return addCondition(propertyName, ">", value);
    }

    @Override
    public Children ge(R propertyFunction, Object value) {
        return addCondition(propertyFunction, ">=", value);
    }

    @Override
    public Children lt(R propertyName, Object value) {
        return addCondition(propertyName, "<", value);
    }

    @Override
    public Children le(R propertyName, Object value) {
        return addCondition(propertyName, "<=", value);
    }

    @Override
    public Children in(R propertyName, Collection<?> values) {
        return addCondition(propertyName, "IN", values);
    }

    @Override
    public Children notIn(R propertyName, Collection<?> values) {
        return addCondition(propertyName, "NOT IN", values);
    }

    @Override
    public Children between(R propertyName, Object start, Object end) {
        ge(propertyName, start);
        le(propertyName, end);
        return typedThis;
    }

    @Override
    public Children any(R propertyName, Collection<?> value) {
        return addCondition(propertyName, "ANY", value);
    }

    public Children or() {
        ArangodbCondition condition = new ArangodbCondition();
        condition.setSeparator("OR");
        conditions.add(condition);
        return typedThis;
    }

    @Override
    public Children eq(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, "==", value);
    }

    @Override
    public Children like(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, "like", "%" + value + "%");
    }

    @Override
    public Children gt(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, ">", value);
    }

    @Override
    public Children ge(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, ">=", value);
    }

    @Override
    public Children lt(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, "<", value);
    }

    @Override
    public Children le(R propertyFunction, String key, Object value) {
        return addCondition(propertyFunction, key, "<=", value);
    }

    @Override
    public Children in(R propertyFunction, String key, Collection<?> values) {
        return addCondition(propertyFunction, key, "IN", values);
    }

    @Override
    public Children notIn(R propertyFunction, String key, Collection<?> values) {
        return addCondition(propertyFunction, key, "NOT IN", values);
    }

    @Override
    public Children between(R propertyFunction, String key, Object start, Object end) {
        ge(propertyFunction, key, start);
        le(propertyFunction, key, end);
        return typedThis;
    }

    @Override
    public Children any(R propertyName, String key, Collection<?> value) {
        return addCondition(propertyName, key, "ANY", value);
    }

    // === 排序 & 分页 ===
    public Children orderByAsc(String propertyName) {
        this.orderBy = "n.`" + propertyName + "` ASC";
        return typedThis;
    }

    public Children orderByDesc(String propertyName) {
        this.orderBy = "n.`" + propertyName + "` DESC";
        return typedThis;
    }

    public Children page(int pageNum, int pageSize) {
        this.offset = (pageNum - 1) * pageSize;
        this.limit = pageSize;
        return typedThis;
    }

    public void apply(AbstractWrapper<?, ?, ?> executor) {
        executor.getConditions().forEach(condition -> {
            ArangodbCondition newCondition = new ArangodbCondition();
            if (condition.getSeparator() == null) {
                // 重新获取column
                String propertyName = condition.getColumn();
                String column = this.getColumn(propertyName);
                newCondition.setPropertyName("n." + column);
                if (condition.getKey() != null) {
                    newCondition.setPropertyName("n." + column + "." + condition.getKey());
                }
                newCondition.setColumn(column);
                newCondition.setKey(condition.getKey());
                newCondition.setOperator(condition.getOperator());
                newCondition.setValue(condition.getValue());
                newCondition.setParameterName(condition.getParameterName());
                // 逆反的情况
                if (condition.getPropertyName().contains("@")) {
                    newCondition.setPropertyName(condition.getPropertyName());
                    newCondition.setParameterName("n." + column);
                    if (condition.getKey() != null) {
                        newCondition.setParameterName("n." + column + "." + condition.getKey());
                    }
                }
            } else {
                newCondition.setSeparator(condition.getSeparator());
            }
            conditions.add(newCondition);
        });
        //this.conditions = executor.getConditions();
        this.params = executor.getParams();
        this.orderBy = executor.getOrderBy();
        this.offset = executor.getOffset();
        this.limit = executor.getLimit();
    }

    protected String columnToString(R column) {
        return getColumn((String) column);
    }

    protected String getColumn(String propertyName) {
        String property = Utils.getProperty(this.entityClass, propertyName);
        if (StringUtils.isNotEmpty(property)) {
            return property;
        }
        return propertyName;
    }

    // === 条件构建方法 ===
    protected int parameterCount = 0;

    private Children addReversCondition(R propertyName, String operator, Object value) {
        String column = this.columnToString(propertyName);
        String parameterName = column + parameterCount++;

        ArangodbCondition condition = new ArangodbCondition();
        condition.setPropertyName("@" + parameterName);
        condition.setColumn(column);
        condition.setOperator(operator);
        condition.setValue(value);
        condition.setParameterName("n." + column);
        conditions.add(condition);
        params.put(parameterName, value);

        return typedThis;
    }

    private Children addCondition(R propertyName, String operator, Object value) {
        String column = this.columnToString(propertyName);
        String parameterName = column + parameterCount++;

        ArangodbCondition condition = new ArangodbCondition();
        condition.setPropertyName("n." + column);
        condition.setColumn(column);
        condition.setOperator(operator);
        condition.setValue(value);
        condition.setParameterName("@" + parameterName);
        conditions.add(condition);
        params.put(parameterName, value);
        return typedThis;
    }

    // map属性名条件构建方法
    private Children addCondition(R propertyName, String key, String operator, Object value) {
        String column = this.columnToString(propertyName);
        String parameterName = column + parameterCount++;

        ArangodbCondition condition = new ArangodbCondition();
        condition.setPropertyName("n." + column + "." + key);
        condition.setColumn(column);
        condition.setKey(key);
        condition.setOperator(operator);
        condition.setValue(value);
        condition.setParameterName("@" + parameterName);
        conditions.add(condition);
        params.put(parameterName, value);
        return typedThis;
    }
}
