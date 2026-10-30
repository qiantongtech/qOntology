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

// 查询数据类型列表
export function listDataType(query) {
    return request({
        url: '/om/dataType/list',
        method: 'get',
        params: query
    });
}

// 查询数据类型详细
export function getDataType(id, versionId) {
    return request({
        url: '/om/dataType/' + id,
        method: 'get',
        params: { versionId }
    });
}

// 新增数据类型
export function addDataType(data) {
    return request({
        url: '/om/dataType',
        method: 'post',
        data: data
    });
}

// 修改数据类型
export function updateDataType(data) {
    return request({
        url: '/om/dataType',
        method: 'put',
        data: data
    });
}

// 删除数据类型
export function delDataType(id, versionId) {
    return request({
        url: '/om/dataType/' + id,
        method: 'delete',
        params: { versionId }
    });
}

export function getEnumTypeDict(versionId, enumSource) {
    return request({ url: '/om/dataType/enumOptions', method: 'get', params: { versionId, enumSource } });
}
