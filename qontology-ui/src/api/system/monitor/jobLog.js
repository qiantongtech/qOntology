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

import request from '@/utils/request.js';

// 查询调度日志列表
export function listJobLog(query) {
    return request({
        url: '/monitor/jobLog/list',
        method: 'get',
        params: query
    });
}

// 删除调度日志
export function delJobLog(jobLogId) {
    return request({
        url: '/monitor/jobLog/' + jobLogId,
        method: 'delete'
    });
}

// 清空调度日志
export function cleanJobLog() {
    return request({
        url: '/monitor/jobLog/clean',
        method: 'delete'
    });
}
