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

// 查询枚举类型列表
export function listType(query) {
    return request({
        url: '/om/enumType/list',
        method: 'get',
        params: query
    })
}

// 查询枚举类型详细
export function getType(enumId) {
    return request({
        url: '/om/enumType/' + enumId,
        method: 'get'
    })
}

// 新增枚举类型
export function addType(data) {
    return request({
        url: '/om/enumType',
        method: 'post',
        data: data
    })
}

// 修改枚举类型
export function updateType(data) {
    return request({
        url: '/om/enumType',
        method: 'put',
        data: data
    })
}

// 删除枚举类型
export function delType(enumId) {
    return request({
        url: '/om/enumType/' + enumId,
        method: 'delete'
    })
}
