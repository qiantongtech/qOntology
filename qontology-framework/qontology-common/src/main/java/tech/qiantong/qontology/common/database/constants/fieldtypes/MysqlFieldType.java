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

package tech.qiantong.qontology.common.database.constants.fieldtypes;

/**
 * MySQL 数据库支持的字段类型枚举
 */
public enum MysqlFieldType {
    VARCHAR("VARCHAR"),
    CHAR("CHAR"),
    // MySQL 一般采用 INT/DECIMAL 来代替 NUMBER
    INT("INT"),
    DECIMAL("DECIMAL"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    TIMESTAMP("TIMESTAMP"),
    // 使用 TEXT 表示大文本类型
    TEXT("TEXT");

    private final String type;

    MysqlFieldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
