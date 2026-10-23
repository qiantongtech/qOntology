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

import tech.qiantong.qontology.graphis.core.wrapper.query.QueryExecutor;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.UpsertExecutor;

/**
 *@program: qKnow-pro
 *@author: wang
 *@create: 2025-11-13
 */
public interface ExecutorProvider {
    /**
     * 创建查询执行器
     * @param <T>
     * @return
     */
    <T> QueryExecutor<T> createQuery(Class<T> entityType);

    /**
     * 创建更新执行器
     * @param <T>
     * @return
     */
    <T> UpsertExecutor<T> createUpsert(Class<T> entityType);

    /**
     * 获取引擎名称
     * @return
     */
    String getEngineName();
}
