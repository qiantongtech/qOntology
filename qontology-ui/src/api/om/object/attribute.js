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

// 查询对象类型属性列表
export function listAttributes(query) {
    return request({
        url: '/om/objectTypeAttribute/list',
        method: 'get',
        params: query
    });
}

// 查询对象类型属性详情
export function getAttribute(id, objectTypeId) {
    return request({
        url: '/om/objectTypeAttribute/' + id,
        method: 'get',
        params: { objectTypeId }
    });
}

// 新增对象类型属性
export function addAttribute(data) {
    return request({
        url: '/om/objectTypeAttribute',
        method: 'post',
        data
    });
}

// 修改对象类型属性
export function updateAttribute(data) {
    return request({
        url: '/om/objectTypeAttribute',
        method: 'put',
        data
    });
}

// 删除对象类型属性，支持批量删除
export function delAttribute(ids, objectTypeId) {
    const idList = Array.isArray(ids) ? ids.join(',') : ids;
    return request({
        url: '/om/objectTypeAttribute/' + idList,
        method: 'delete',
        params: { objectTypeId }
    });
}

// 查询可选业务数据类型
export function listAttributeBusinessDataTypes(objectTypeId, source, versionId) {
    return request({
        url: '/om/objectTypeAttribute/businessDataTypes',
        method: 'get',
        params: { objectTypeId, source, versionId }
    });
}

export function listAttributeEnums(objectTypeId, source) {
    return request({ url: '/om/objectTypeAttribute/enumOptions', method: 'get', params: { objectTypeId, source } });
}
