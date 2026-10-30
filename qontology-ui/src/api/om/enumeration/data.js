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

// 查询枚举数据列表
export function listData(query) {
    return request({
        url: '/om/enumData/list',
        method: 'get',
        params: query
    });
}

// 查询枚举数据详细
export function getData(enumCode) {
    return request({
        url: '/om/enumData/' + enumCode,
        method: 'get'
    });
}

// 新增枚举数据
export function addData(data) {
    return request({
        url: '/om/enumData',
        method: 'post',
        data: data
    });
}

// 修改枚举数据
export function updateData(data) {
    return request({
        url: '/om/enumData',
        method: 'put',
        data: data
    });
}

// 删除枚举数据
export function delData(enumCode) {
    return request({
        url: '/om/enumData/' + enumCode,
        method: 'delete'
    });
}
