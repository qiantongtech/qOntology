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

// 查询AI 模型列表
export function listModel(query) {
    return request({
        url: '/ai/model/list',
        method: 'get',
        params: query
    });
}

// 查询AI 模型详细
export function getModel(id) {
    return request({
        url: '/ai/model/' + id,
        method: 'get'
    });
}

// 新增AI 模型
export function addModel(data) {
    return request({
        url: '/ai/model',
        method: 'post',
        data: data
    });
}

// 修改AI 模型
export function updateModel(data) {
    return request({
        url: '/ai/model',
        method: 'put',
        data: data
    });
}

// 删除AI 模型
export function delModel(id) {
    return request({
        url: '/ai/model/' + id,
        method: 'delete'
    });
}
