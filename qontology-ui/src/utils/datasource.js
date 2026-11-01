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

/**
 * 获取数据源类型对应图标
 * @param {string} type 数据源类型（字典 datasource_type 的 value）
 * @returns {string|null} 图标地址，无匹配时返回 null
 */
export function getDatasourceIcon(type) {
    switch (type) {
        case 'DM8':
            return new URL('@/assets/common/images/DM8.svg', import.meta.url).href;
        case 'Oracle':
        case 'Oracle11':
            return new URL('@/assets/common/images/Oracle.svg', import.meta.url).href;
        case 'MySql':
            return new URL('@/assets/common/images/MySQL.svg', import.meta.url).href;
        case 'Hive':
            return new URL('@/assets/common/images/Hive.svg', import.meta.url).href;
        case 'Sqlerver':
        case 'SQL_Server':
        case 'SQL_Server2008':
            return new URL('@/assets/common/images/SQL_Server.svg', import.meta.url).href;
        case 'Kafka':
            return new URL('@/assets/common/images/kafka.svg', import.meta.url).href;
        case 'HDFS':
            return new URL('@/assets/common/images/hdfs.svg', import.meta.url).href;
        case 'SHELL':
            return new URL('@/assets/common/images/SHELL.png', import.meta.url).href;
        case 'Kingbase8':
            return new URL('@/assets/common/images/Kingbase8.svg', import.meta.url).href;
        case 'Doris':
            return new URL('@/assets/common/images/doris.svg', import.meta.url).href;
        default:
            return null;
    }
}

/**
 * 数据源类型对应的标签颜色（el-tag 的 type）
 * key 用大写形式，兼容 SQL_Server / Sqlerver 等写法
 */
const TAG_TYPE_MAP = {
    MYSQL: 'primary',
    ORACLE: 'primary',
    ORACLE11: 'primary',
    DM8: 'info',
    KINGBASE8: 'info',
    DORIS: 'info',
    SQL_SERVER: 'info',
    SQL_SERVER2008: 'info',
    SQLERVER: 'info',
    HIVE: 'warning',
    KAFKA: 'warning',
    HDFS: 'success'
};

/**
 * 获取数据源类型对应的标签颜色
 * @param {string} type 数据源类型（字典 datasource_type 的 value）
 * @returns {string} el-tag 的 type，未配置时返回 primary
 */
export function getDatasourceTagType(type) {
    if (!type) return 'primary';
    return TAG_TYPE_MAP[String(type).toUpperCase()] || 'primary';
}
