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

import request from '@/utils/request';
import { normalizeModelAxiom, buildModelAxiom } from './convert';

// 查询模型公理列表
export async function listModelAxiom(query) {
    const response = await request({
        url: '/om/modelAxiom/list',
        method: 'get',
        params: query
    });
    response.data.rows = (response.data.rows || []).map(normalizeModelAxiom);
    return response;
}

// 查询模型公理详细
export async function getModelAxiom(id) {
    const response = await request({
        url: '/om/modelAxiom/' + id,
        method: 'get'
    });
    response.data = normalizeModelAxiom(response.data);
    return response;
}

// 新增模型公理
export function addModelAxiom(data) {
    return request({
        url: '/om/modelAxiom',
        method: 'post',
        data: buildModelAxiom(data)
    });
}

// 修改模型公理
export function updateModelAxiom(data) {
    return request({
        url: '/om/modelAxiom',
        method: 'put',
        data: buildModelAxiom(data)
    });
}

// 删除模型公理
export function delModelAxiom(ids) {
    const idList = Array.isArray(ids) ? ids.join(',') : ids;
    return request({
        url: '/om/modelAxiom/' + idList,
        method: 'delete'
    });
}
