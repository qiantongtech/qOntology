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

// 查询事件管理列表
export function listEvent(query) {
    return request({
        url: '/app/event/list',
        method: 'get',
        params: query
    });
}

// 查询事件管理详细
export function getEvent(id) {
    return request({
        url: '/app/event/' + id,
        method: 'get'
    });
}

// 查询事件实体关联详细
export function getEventData(id) {
    return request({
        url: '/app/event/getEventData/' + id,
        method: 'get'
    });
}

// 新增事件管理
export function addEvent(data) {
    return request({
        url: '/app/event',
        method: 'post',
        data: data
    });
}

// 修改事件管理
export function updateEvent(data) {
    return request({
        url: '/app/event',
        method: 'put',
        data: data
    });
}

// 删除事件管理
export function delEvent(id) {
    return request({
        url: '/app/event/' + id,
        method: 'delete'
    });
}
