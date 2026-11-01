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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */
import request from '@/utils/request';

// 查询知识标签列表
export function listTag(query) {
    return request({
        url: '/app/tag/list',
        method: 'get',
        params: query
    });
}

// 查询知识标签详细
export function getTag(id) {
    return request({
        url: '/app/tag/' + id,
        method: 'get'
    });
}

// 新增知识标签
export function addTag(data) {
    return request({
        url: '/app/tag',
        method: 'post',
        data: data
    });
}

// 修改知识标签
export function updateTag(data) {
    return request({
        url: '/app/tag',
        method: 'put',
        data: data
    });
}

// 删除知识标签
export function delTag(id) {
    return request({
        url: '/app/tag/' + id,
        method: 'delete'
    });
}
