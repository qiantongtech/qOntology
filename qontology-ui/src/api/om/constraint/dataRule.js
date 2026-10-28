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
import { normalizeDataRule, buildDataRule } from './convert';

// 查询数据规则列表
export async function listDataRule(query) {
  const response = await request({
    url: '/om/dataRule/list',
    method: 'get',
    params: query
  });
  response.data.rows = (response.data.rows || []).map(normalizeDataRule);
  return response;
}

// 查询数据规则详细
export async function getDataRule(id) {
  const response = await request({
    url: '/om/dataRule/' + id,
    method: 'get'
  });
  response.data = normalizeDataRule(response.data);
  return response;
}

// 新增数据规则
export function addDataRule(data) {
  return request({
    url: '/om/dataRule',
    method: 'post',
    data: buildDataRule(data)
  });
}

// 修改数据规则
export function updateDataRule(data) {
  return request({
    url: '/om/dataRule',
    method: 'put',
    data: buildDataRule(data)
  });
}

// 删除数据规则
export function delDataRule(ids) {
  const idList = Array.isArray(ids) ? ids.join(',') : ids;
  return request({
    url: '/om/dataRule/' + idList,
    method: 'delete'
  });
}
