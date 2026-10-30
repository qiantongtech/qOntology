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

// 查询对象关系列表
export function listRelations(query) {
    return request({
        url: '/om/objectTypeRelation/list',
        method: 'get',
        params: query
    });
}

// 查询对象关系详情
export function getRelation(id) {
    return request({
        url: '/om/objectTypeRelation/' + id,
        method: 'get'
    });
}

// 查询可选对象类型
export function listRelationTypes(versionId) {
    return request({
        url: '/om/objectTypeRelation/typeOptions',
        method: 'get',
        params: { versionId }
    });
}

// 新增对象关系
export function addRelation(data) {
    return request({
        url: '/om/objectTypeRelation',
        method: 'post',
        data
    });
}

// 修改对象关系
export function updateRelation(data) {
    return request({
        url: '/om/objectTypeRelation',
        method: 'put',
        data
    });
}

// 删除对象关系，支持批量删除
export function delRelation(ids) {
    const idList = Array.isArray(ids) ? ids.join(',') : ids;
    return request({
        url: '/om/objectTypeRelation/' + idList,
        method: 'delete'
    });
}

// 查询可供关联的有效建模目标
export function listRelationTargetOptions(query) {
    return request({
        url: '/om/objectTypeRelation/targetOptions',
        method: 'get',
        params: query
    });
}

// 查询对象关系已关联的建模目标
export function listRelationTargets(id) {
    return request({
        url: '/om/objectTypeRelation/' + id + '/targets',
        method: 'get'
    });
}

// 分页查询对象关系已关联的建模目标
export function pageRelationTargets(id, query) {
    return request({
        url: '/om/objectTypeRelation/' + id + '/targets/page',
        method: 'get',
        params: query
    });
}

// 查询建模目标已关联的对象关系
export function listRelationsByTarget(targetId) {
    return request({
        url: '/om/objectTypeRelation/byTarget/' + targetId,
        method: 'get'
    });
}
