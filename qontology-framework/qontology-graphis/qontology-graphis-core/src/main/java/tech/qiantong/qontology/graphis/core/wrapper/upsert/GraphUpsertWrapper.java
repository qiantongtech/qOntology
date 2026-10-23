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

package tech.qiantong.qontology.graphis.core.wrapper.upsert;

import tech.qiantong.qontology.graphis.core.factory.ExecutorFactoryProvider;
import tech.qiantong.qontology.graphis.core.wrapper.AbstractGraphWrapper;

/**
 * 条件查询
 * @author wang
 * @date 2025/11/12 14:32
 **/
public class GraphUpsertWrapper<T> extends AbstractGraphWrapper<T> implements UpsertExecutor<T> {

    public GraphUpsertWrapper(Class<T> entityType) {
        super.delegate = ExecutorFactoryProvider.getFactory().createUpsertExecutor(entityType);
    }
}
