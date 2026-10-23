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

// 查询项目版本列表
export function listProjectVersion(query) {
  return request({
    url: '/basic/projectVersion/list',
    method: 'get',
    params: query
  })
}

// 查询项目版本详细
export function getProjectVersion(id) {
  return request({
    url: '/basic/projectVersion/' + id,
    method: 'get'
  })
}

// 新增项目版本
export function addProjectVersion(data) {
  return request({
    url: '/basic/projectVersion',
    method: 'post',
    data: data
  })
}

// 修改项目版本
export function updateProjectVersion(data) {
  return request({
    url: '/basic/projectVersion',
    method: 'put',
    data: data
  })
}

// 删除项目版本
export function delProjectVersion(id) {
  return request({
    url: '/basic/projectVersion/' + id,
    method: 'delete'
  })
}

// 获取项目版本下拉选择框数据
export function getProjectVersionDict(projectId) {
  return request({
    url: '/basic/projectVersion/projectVersionDict',
    method: 'get',
    params: { projectId: projectId },
  })
}
