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

// 后端Long字段回显为字符串，保持与树形选择器、属性选项的值类型一致。
const toId = value => value == null || value === '' ? null : String(value);

export function normalizeModelAxiom(record) {
    const result = {...record};
    for (const field of ['id', 'versionId', 'objectTypeId', 'otherTypeId', 'relationId', 'domainId', 'rangeId', 'attributeId']) {
        result[field] = toId(record[field]);
    }
    result.typeCategory = record.typeCategory == null ? 1 : Number(record.typeCategory);
    result.basicTypeCode = record.basicTypeCode == null ? undefined : Number(record.basicTypeCode);
    result.businessTypeKey = record.businessTypeId == null ? '' : `${record.businessTypeSource}:${record.businessTypeId}`;
    result.relationName = record.relationName || '';
    result.description = record.description || '';
    return result;
}

// 只提交保存VO定义的字段，避免携带页面临时字段和客户端审计信息。
export function buildModelAxiom(record) {
    const result = {
        id: record.id,
        versionId: record.versionId,
        name: record.name.trim(),
        description: record.description?.trim() || '',
        type: record.type,
        level: record.level
    };
    const fields = ['objectTypeId', 'otherTypeId', 'relationId', 'domainId', 'rangeId', 'attributeId',
        'typeCategory', 'basicTypeCode', 'relationName', 'businessTypeId', 'businessTypeSource', 'unit', 'decimalPlaces',
        'format', 'enumType', 'enumSource'];
    fields.forEach(field => { result[field] = null; });
    if (record.type === 'DOMAIN_RANGE') {
        // 关系名称独立定义，不依赖对象关系记录。
        result.relationName = record.relationName.trim();
        ['domainId', 'rangeId'].forEach(field => { result[field] = toId(record[field]); });
    } else {
        result.objectTypeId = toId(record.objectTypeId);
        if (record.type !== 'PROPERTY_RANGE') {
            result.otherTypeId = toId(record.otherTypeId);
        } else {
            result.attributeId = toId(record.attributeId);
            result.typeCategory = String(record.typeCategory);
            if (Number(record.typeCategory) === 2) {
                // 选择器使用“来源:id”区分项目类型和全局类型，保存时拆为两个字段。
                const [source, id] = record.businessTypeKey.split(':');
                result.businessTypeSource = source;
                result.businessTypeId = toId(id);
            } else {
                const code = Number(record.basicTypeCode);
                result.basicTypeCode = String(code);
                result.unit = [1, 2].includes(code) ? record.unit || null : null;
                result.decimalPlaces = code === 2 ? record.decimalPlaces ?? null : null;
                result.format = code === 4 ? record.format || null : null;
                result.enumType = code === 5 ? record.enumType || null : null;
                result.enumSource = result.enumType ? record.enumSource : null;
            }
        }
    }
    return result;
}

export function normalizeDataRule(record) {
    // 数据库JSON目前通过String字段返回；页面始终使用有序条件数组。
    const conditions = typeof record.conditions === 'string' ? JSON.parse(record.conditions) : record.conditions;
    if (!Array.isArray(conditions)) throw new Error('数据规则失败条件格式无效');
    return {
        ...record,
        id: toId(record.id),
        versionId: toId(record.versionId),
        description: record.description || '',
        conditions: conditions.map(condition => ({
            ...condition,
            objectTypeId: toId(condition.objectTypeId),
            attributeId: toId(condition.attributeId),
            value: condition.value == null ? '' : String(condition.value)
        }))
    };
}

export function buildDataRule(record) {
    return {
        id: record.id,
        versionId: record.versionId,
        name: record.name.trim(),
        description: record.description?.trim() || '',
        type: record.type,
        level: record.level,
        conditionRelation: record.conditionRelation,
        failureMessage: record.failureMessage.trim(),
        // 不保存前端渲染key，数值用字符串保存以避免整数和小数精度丢失。
        conditions: JSON.stringify(record.conditions.map(condition => ({
            objectTypeId: toId(condition.objectTypeId),
            attributeId: toId(condition.attributeId),
            operator: record.type === 'RANGE' ? condition.operator : null,
            value: record.type === 'REQUIRED' ? null : String(condition.value).trim()
        })))
    };
}
