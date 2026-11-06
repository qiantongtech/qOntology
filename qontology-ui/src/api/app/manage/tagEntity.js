/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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

// 查询实体标签列表
export function listTagEntity(query) {
    return request({
        url: '/kg/tagEntity/list',
        method: 'get',
        params: query
    });
}

// 查询实体标签详细
export function getTagEntity(id) {
    return request({
        url: '/kg/tagEntity/' + id,
        method: 'get'
    });
}

// 新增实体标签
export function addTagEntity(data) {
    return request({
        url: '/kg/tagEntity',
        method: 'post',
        data: data
    });
}

// 修改实体标签
export function updateTagEntity(data) {
    return request({
        url: '/kg/tagEntity',
        method: 'put',
        data: data
    });
}

// 删除实体标签
export function delTagEntity(id) {
    return request({
        url: '/kg/tagEntity/' + id,
        method: 'delete'
    });
}

// 查询实体标签
export function entity(entityId, graphId) {
    return request({
        url: '/kg/tagEntity/entity?entityId=' + entityId + '&graphId=' + graphId + '',
        method: 'get'
    });
}
// 编辑实体标签
export function updateEntityTags(data) {
    return request({
        url: '/kg/tagEntity/updateEntity',
        method: 'put',
        data: data
    });
}

// 删除实体标签
export function deleteEntity(data) {
    return request({
        url: '/kg/tagEntity/deleteEntity',
        method: 'post',
        data: data
    });
}
