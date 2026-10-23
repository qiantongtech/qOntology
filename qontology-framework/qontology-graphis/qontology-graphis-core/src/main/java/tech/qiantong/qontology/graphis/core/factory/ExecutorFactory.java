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

package tech.qiantong.qontology.graphis.core.factory;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 创建工厂
 * @author wang
 * @date 2025/11/12 15:44
 **/
@Component
@ConfigurationProperties(prefix = "graphis")
public class ExecutorFactory {
    @Setter
    private String type = "neo4j";

    private final List<ExecutorProvider> providers;
    private Map<String, ExecutorProvider> providerMap = new ConcurrentHashMap<>();

    @Autowired
    public ExecutorFactory(List<ExecutorProvider> providers) {
        this.providers = providers;
    }

    @PostConstruct
    public void init() {
        providerMap = providers.stream()
                .collect(Collectors.toConcurrentMap(
                        ExecutorProvider::getEngineName,
                        p -> p
                ));
    }

    /**
     * 创建查询执行器
     * @param entityType
     * @param <T>
     * @return
     */
    public <T> QueryExecutor<T> createQueryExecutor(Class<T> entityType) {
        ExecutorProvider provider = providerMap.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported query engine: " + type +
                    ". Available: " + providerMap.keySet());
        }
        return provider.createQuery(entityType);
    }

    /**
     * 创建更新执行器
     * @param entityType
     * @param <T>
     * @return
     */
    public <T> UpsertExecutor<T> createUpsertExecutor(Class<T> entityType) {
        ExecutorProvider provider = providerMap.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported upsert engine: " + type +
                    ". Available: " + providerMap.keySet());
        }
        return provider.createUpsert(entityType);
    }

}
