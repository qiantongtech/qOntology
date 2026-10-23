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

// 查询模型数据文件列表
export function listModelDataFile(query) {
  return request({
    url: '/basic/file/list',
    method: 'get',
    params: query
  })
}

// 查询模型数据文件详细
export function getModelDataFile(id) {
  return request({
    url: '/basic/file/' + id,
    method: 'get'
  })
}

// 新增模型数据文件
export function addModelDataFile(data) {
  return request({
    url: '/basic/file',
    method: 'post',
    data: data
  })
}

// 修改模型数据文件
export function updateModelDataFile(data) {
  return request({
    url: '/basic/file',
    method: 'put',
    data: data
  })
}

// 删除模型数据文件
export function delModelDataFile(id) {
  return request({
    url: '/basic/file/' + id,
    method: 'delete'
  })
}

// 获取分类统计
export function getFileTypes() {
  return request({
    url: '/basic/file/getFileTypes',
    method: 'get'
  })
}

// 文件跟踪统计
export function trackCount(data) {
  return request({
    url: '/basic/file/trackCount',
    method: 'post',
    data: data
  })
}

// 导出模型数据文件
export function exportModelDataFile(query) {
  return request({
    url: '/basic/file/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 导入模型数据文件
export function importModelDataFile(data) {
  return request({
    url: '/basic/file/import',
    method: 'post',
    data: data
  })
}
