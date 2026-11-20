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

const DEFAULT_ENTITY_COLOR = '#89B8FF';

/**
 * 使用节点已保存的类型颜色，缺少合法色值时使用统一默认色。
 * @param {Object} entity 图谱实体
 * @returns {string} 六位十六进制颜色
 */
export function getEntityTypeColor(entity) {
    const color = entity?.schemaColor;
    return typeof color === 'string' && /^#[0-9a-fA-F]{6}$/.test(color)
        ? color
        : DEFAULT_ENTITY_COLOR;
}
