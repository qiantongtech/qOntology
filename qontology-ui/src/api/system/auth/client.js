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

import request from '@/utils/request.js';

// 查询应用管理列表
export function listClient(query) {
    return request({
        url: '/auth/client/list',
        method: 'get',
        params: query
    });
}

// 查询应用管理详细
export function getClient(id) {
    return request({
        url: '/auth/client/' + id,
        method: 'get'
    });
}

// 新增应用管理
export function addClient(data) {
    return request({
        url: '/auth/client',
        method: 'post',
        data: data
    });
}

// 修改应用管理
export function updateClient(data) {
    return request({
        url: '/auth/client',
        method: 'put',
        data: data
    });
}

// 删除应用管理
export function delClient(id) {
    return request({
        url: '/auth/client/' + id,
        method: 'delete'
    });
}
