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

// 查询建模目标列表
export function listTarget(query) {
  return request({
    url: '/om/target/list',
    method: 'get',
    params: query
  })
}

// 查询建模目标详细
export function getTarget(id) {
  return request({
    url: '/om/target/' + id,
    method: 'get'
  })
}

// 新增建模目标
export function addTarget(data) {
  return request({
    url: '/om/target',
    method: 'post',
    data: data
  })
}

// 修改建模目标
export function updateTarget(data) {
  return request({
    url: '/om/target',
    method: 'put',
    data: data
  })
}

// 删除建模目标
export function delTarget(id) {
  return request({
    url: '/om/target/' + id,
    method: 'delete'
  })
}
