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

package tech.qiantong.qontology.neo4j.core.wrapper.upsert;

/**
 * @program: qKnow-pro
 * @description: 查询接口模版
 * @author: wang
 * @create: 2025-11-18
 */
public interface Upsert<T> {

    /**
     * 根据条件修改（暂不支持关系更新）
     * TODO:计划功能：支持关系更新
     *
     * @param entity
     * @return
     */
    String updateSql(T entity);

    /**
     * 根据指定条件进行合并，没有则新增
     *
     * @param entity
     * @return
     */
    String mergeSql(T entity);

    /**
     * 根据指定条件进行合并关系，没有则新增
     *
     * @return
     */
    String mergeRelationship(String titleName);
}
