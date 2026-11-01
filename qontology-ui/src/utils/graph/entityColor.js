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

/**
 * 节点配色：按 entity.id 哈希分配颜色，每个实体独立取色，
 * 同一实体跨渲染保持同一颜色。15 色循环，深蓝/深绿/红橙等与深蓝背景高对比。
 */
export const ENTITY_COLOR_PALETTE = [
    '#3b68c2', // 深蓝
    '#289470', // 深青绿
    '#b58200', // 暗金
    '#b93c32', // 暗红（你图里故障红，优先这个）
    '#2a8cb8', // 暗湖蓝
    '#6b4296', // 暗紫
    '#c46518', // 暗橙
    '#0e706e', // 墨青
    '#a83f70', // 暗玫粉
    '#4068a8', // 灰调蓝
    '#358252', // 灰调绿
    '#b16c27', // 土橙
    '#7257a0', // 灰紫
    '#2d78b5', // 钢蓝
    '#378a46' // 墨绿
];

/** 用 entity 的 id（数字或字符串）哈希到调色板下标。 */
export function pickColorByEntityId(entityId) {
    const palette = ENTITY_COLOR_PALETTE;
    // id 可能是字符串数字、纯数字或带前缀，统一取字符码累加
    let hash = 0;
    const s = String(entityId || '');
    for (let i = 0; i < s.length; i++) {
        hash = (hash * 31 + s.charCodeAt(i)) | 0;
    }
    const idx = Math.abs(hash) % palette.length;
    return palette[idx];
}
