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

// 更改模型是否启用
export function changeModelEnable(data) {
    return request({
        url: '/ai/model/changeModelEnable',
        method: 'put',
        data
    });
}

// 更改模型是否启用
export function getChatModelDict() {
    return request({
        url: '/ai/model/getChatModelDict',
        method: 'get'
    });
}

// 获取平台下模型列表
export function getModelPage(query) {
    return request({
        url: '/ai/model/getModelPage',
        method: 'get',
        params: query,
        timeout: 40 * 1000
    });
}

// 获取平台下模型列表
export function syncModel(keyId) {
    return request({
        url: '/ai/model/syncModel?keyId=' + keyId,
        method: 'get'
    });
}
