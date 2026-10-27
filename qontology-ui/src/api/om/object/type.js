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

// 查询对象类型列表
export function listType(query) {
    return request({
        url: '/om/objectType/list',
        method: 'get',
        params: query
    });
}

// 查询排除自身及全部下级的对象类型列表
export function listTypeExcludeChild(id) {
    return request({
        url: '/om/objectType/list/exclude/' + id,
        method: 'get'
    });
}

// 查询对象类型详细
export function getType(id) {
    return request({
        url: '/om/objectType/' + id,
        method: 'get'
    });
}

// 新增对象类型
export function addType(data) {
    return request({
        url: '/om/objectType',
        method: 'post',
        data: data
    });
}

// 修改对象类型
export function updateType(data) {
    return request({
        url: '/om/objectType',
        method: 'put',
        data: data
    });
}

// 删除单个对象类型
export function delType(id) {
    return request({
        url: '/om/objectType/' + id,
        method: 'delete'
    });
}

// 查询可供关联的有效建模目标
export function listTypeTargetOptions(query) {
    return request({
        url: '/om/objectType/targetOptions',
        method: 'get',
        params: query
    });
}

// 查询对象类型已关联的建模目标
export function listTypeTargets(id) {
    return request({
        url: '/om/objectType/' + id + '/targets',
        method: 'get'
    });
}

// 分页查询对象类型已关联的建模目标
export function pageTypeTargets(id, query) {
    return request({
        url: '/om/objectType/' + id + '/targets/page',
        method: 'get',
        params: query
    });
}

// 查询建模目标已关联的对象类型
export function listTypesByTarget(targetId) {
    return request({
        url: '/om/objectType/byTarget/' + targetId,
        method: 'get'
    });
}
