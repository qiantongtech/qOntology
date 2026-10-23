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

// 查询模型数据文件分类列表
export function listModelDataCategory(query) {
  return request({
    url: '/basic/file/category/list',
    method: 'get',
    params: query
  })
}

// 查询模型数据文件分类详细
export function getModelDataCategory(id) {
  return request({
    url: '/basic/file/category/' + id,
    method: 'get'
  })
}

// 新增模型数据文件分类
export function addModelDataCategory(data) {
  return request({
    url: '/basic/file/category',
    method: 'post',
    data: data
  })
}

// 修改模型数据文件分类
export function updateModelDataCategory(data) {
  return request({
    url: '/basic/file/category',
    method: 'put',
    data: data
  })
}

// 删除模型数据文件分类
export function delModelDataCategory(id) {
  return request({
    url: '/basic/file/category/' + id,
    method: 'delete'
  })
}
