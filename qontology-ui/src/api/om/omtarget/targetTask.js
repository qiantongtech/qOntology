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

import request from '@/utils/request'

// 查询建模目标验证任务列表
export function listTargetTask(query) {
  return request({
    url: '/om/targetTask/list',
    method: 'get',
    params: query
  })
}

// 查询建模目标验证任务详细
export function getTargetTask(id) {
  return request({
    url: '/om/targetTask/' + id,
    method: 'get'
  })
}

// 新增建模目标验证任务
export function addTargetTask(data) {
  return request({
    url: '/om/targetTask',
    method: 'post',
    data: data
  })
}
