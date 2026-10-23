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

package tech.qiantong.qontology.graphis.core.repository;

import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: qKnow-pro
 * @description: 操作
 * @author: wang
 * @create: 2025-11-17
 */
public interface UpserBaseRepository<T, ID> {
    /** 新增实体 */
    T save(T entity);

    /** 批量新增实体 */
    List<T> saveAll(List<T> entityList);

    /** 更新实体 */
    void update(T entity, UpsertExecutor<T> upsertWrapper);

    /** 根据属性进行新增或更新实体 */
    void merge(T entity, UpsertExecutor<T> upsertWrapper);

    /** 根据属性进行新增或更新指定关系关系 */
    void mergeRelationShip(String relationship, Map<String, Object> entity, UpsertExecutor<T> mergeSqlStart, UpsertExecutor<T> mergeSqlEnd);

    void extractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams);

    void unExtractTripleRelationShip(String relationship, ConcurrentHashMap<String, Object> relationshipParams);


}
