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

// 查询项目管理列表
export function listProject(query) {
  return request({
    url: '/basic/project/list',
    method: 'get',
    params: query
  })
}

// 查询项目管理详细
export function getProject(id) {
  return request({
    url: '/basic/project/' + id,
    method: 'get'
  })
}

// 新增项目管理
export function addProject(data) {
  return request({
    url: '/basic/project',
    method: 'post',
    data: data
  })
}

// 修改项目管理
export function updateProject(data) {
  return request({
    url: '/basic/project',
    method: 'put',
    data: data
  })
}

// 删除项目管理
export function delProject(id) {
  return request({
    url: '/basic/project/' + id,
    method: 'delete'
  })
}

// 获取所有项目字典
export function projectDict() {
  return request({
    url: '/basic/project/projectDict',
    method: 'get'
  })
}

// 仅查询当前项目的版本，复用项目导出权限。
export function listOwlExportVersions(projectId) {
  return request({url: `/basic/project/${projectId}/export/versions`, method: 'get'});
}

export function exportProjectOwl(projectId, data) {
  return request({
    url: `/basic/project/${projectId}/export/owl`, method: 'post', data,
    responseType: 'blob', timeout: 120000
  });
}

// 本体导出，按 format 返回 Turtle 或 RDF/XML。
export function exportProjectOntology(projectId, data) {
  return request({
    url: `/basic/project/${projectId}/export`, method: 'post', data,
    responseType: 'blob', timeout: 120000
  });
}
