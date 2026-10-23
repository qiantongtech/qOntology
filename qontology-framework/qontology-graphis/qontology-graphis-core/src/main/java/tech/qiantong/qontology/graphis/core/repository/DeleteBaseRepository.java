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

import tech.qiantong.qontology.core.domain.AjaxResult;

import java.util.Collection;

/**
 * 抽象基础仓库
 *
 * @author wang
 */
public interface DeleteBaseRepository<T, ID> {
    /**
     * 删除实体
     */
    void deleteEntityById(ID id);

    /**
     * 批量删除实体
     */
    void deleteEntityByIds(Collection<ID> idList);

    /**
     * 删除关系
     */
    void deleteRelationshipById(ID id);

    /**
     * 批量删除关系
     */
    void deleteRelationshipByIds(Collection<ID> idList);

    /**
     * 删除实体属性
     */
    void deleteEntityAttributeById(ID id, String attributeName);

    /**
     * 根据任务id删除之前的抽取结果 非结构化抽取
     *
     * @param taskId
     * @return
     */
    public AjaxResult deleteExtUnStruck(Long taskId);

    /**
     * 根据任务id删除之前的抽取结果 结构化抽取
     *
     * @param taskId
     * @return
     */
    public AjaxResult deleteExtStruck(Long taskId);

}
