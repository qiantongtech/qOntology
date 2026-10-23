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

package tech.qiantong.qontology.neo4j.core.executor;

import org.springframework.stereotype.Component;
import tech.qiantong.qontology.graphis.core.factory.ExecutorProvider;
import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;

/**
 * Neo4j执行器提供者
 * @author wang
 */
@Component
public class Neo4jExecutorProvider implements ExecutorProvider {

    @Override
    public <T> QueryExecutor<T> createQuery(Class<T> entityType) {
        return new Neo4jQueryExecutor<>(entityType);
    }

    @Override
    public <T> UpsertExecutor<T> createUpsert(Class<T> entityType) {
        return new Neo4jUpsertExecutor<>(entityType);
    }

    @Override
    public String getEngineName() {
        return "neo4j";
    }
}
