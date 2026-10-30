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

package tech.qiantong.qontology.module.basic.dto;

import lombok.Data;

/**
 * 版本差异统计数量DTO（单次查询合并返回各业务表统计）
 *
 * @author qOntology
 */
@Data
public class VersionDiffStatsCountDTO {

    /**
     * 建模目标总数
     */
    private Integer omTargetCount;

    /**
     * 建模目标已覆盖数
     */
    private Integer omTargetCoveredCount;

    /**
     * 术语数量
     */
    private Integer termCount;

    /**
     * 枚举数量
     */
    private Integer enumerationCount;

    /**
     * 对象类型数量
     */
    private Integer objectTypeCount;

    /**
     * 对象类型属性数量
     */
    private Integer objectTypeAttributeCount;

    /**
     * 关系数量
     */
    private Integer relationCount;

    /**
     * 模型公理数量
     */
    private Integer modelAxiomCount;

    /**
     * 数据规则数量
     */
    private Integer dataRuleCount;
}
