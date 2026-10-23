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

// 前端确认阶段使用会话内模拟数据；后端完成后在此替换为 request 调用。
const records = new Map();

function copy(value) {
  return JSON.parse(JSON.stringify(value));
}

function getRecords(versionId, category) {
  return records.get(`${versionId}:${category}`) || [];
}

// 查询规则列表，返回结构与现有分页接口一致。
export async function listRule(query) {
  const rows = getRecords(query.versionId, query.category).filter(row => row.name.includes(query.name || ''));
  const offset = (query.pageNum - 1) * query.pageSize;
  return {data: {rows: copy(rows.slice(offset, offset + query.pageSize)), total: rows.length}};
}

// 查询规则详情。
export async function getRule(id, versionId, category) {
  const row = getRecords(versionId, category).find(item => item.id === id);
  if (!row) throw new Error('规则不存在或已删除');
  return {data: copy(row)};
}

// 新增规则。
export async function addRule(data) {
  const rows = getRecords(data.versionId, data.category);
  if (rows.some(row => row.name === data.name)) throw new Error('当前版本已存在同名规则');
  const row = {...copy(data), id: globalThis.crypto.randomUUID(), createTime: Date.now()};
  records.set(`${data.versionId}:${data.category}`, [row, ...rows]);
  return {data: copy(row)};
}

// 修改规则。
export async function updateRule(data) {
  const rows = getRecords(data.versionId, data.category);
  const current = rows.find(row => row.id === data.id);
  if (!current) throw new Error('规则不存在或已删除');
  if (rows.some(row => row.id !== data.id && row.name === data.name)) throw new Error('当前版本已存在同名规则');
  const row = {...copy(data), createBy: current.createBy, createTime: current.createTime};
  records.set(`${data.versionId}:${data.category}`, rows.map(item => item.id === data.id ? row : item));
  return {data: copy(row)};
}

// 删除规则，支持批量删除。
export async function delRule(ids, versionId, category) {
  const rows = getRecords(versionId, category).filter(row => !ids.includes(row.id));
  records.set(`${versionId}:${category}`, rows);
  return {data: ids.length};
}
