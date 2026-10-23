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

import request from '@/utils/request'

// 查询术语列表
export function listTerm(query) {
    return request({
        url: '/om/term/list',
        method: 'get',
        params: query
    })
}

// 查询术语详细
export function getTerm(id) {
    return request({
        url: '/om/term/' + id,
        method: 'get'
    })
}

// 新增术语
export function addTerm(data) {
    return request({
        url: '/om/term',
        method: 'post',
        data: data
    })
}

// 修改术语
export function updateTerm(data) {
    return request({
        url: '/om/term',
        method: 'put',
        data: data
    })
}

// 删除术语
export function delTerm(id) {
    return request({
        url: '/om/term/' + id,
        method: 'delete'
    })
}
