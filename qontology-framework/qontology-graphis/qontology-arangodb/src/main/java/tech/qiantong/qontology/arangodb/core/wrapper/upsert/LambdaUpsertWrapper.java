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


package tech.qiantong.qontology.arangodb.core.wrapper.upsert;

import tech.qiantong.qontology.common.utils.LambdaUtils;

/**
 * 动态查询，方便arangodb查询方式
 *
 * @author wang
 * @date 2025/02/27 17:44
 **/
public class LambdaUpsertWrapper<T> extends AbstractAqlUpsertWrapper<T, LambdaUtils.PropertyFunction<T, ?>, LambdaUpsertWrapper<T>> implements Upsert<T> {

    public LambdaUpsertWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    protected String columnToString(LambdaUtils.PropertyFunction<T, ?> column) {
        return getColumn(LambdaUtils.getPropertyName(column));
    }
}
