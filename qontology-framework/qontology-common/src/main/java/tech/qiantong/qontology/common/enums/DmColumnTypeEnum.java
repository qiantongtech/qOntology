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

package tech.qiantong.qontology.common.enums;

import lombok.Getter;

/**
 * 达梦数据库字段类型枚举
 */
@Getter
public enum DmColumnTypeEnum {
    TINYINT("TINYINT"),
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    DECIMAL("DECIMAL"),
    NUMERIC("NUMERIC"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    NUMBER("NUMBER"),
    CHAR("CHAR"),
    VARCHAR("VARCHAR"),
    VARCHAR2("VARCHAR2"),
    TEXT("TEXT"),
    DATE("DATE"),
    TIMESTAMP("TIMESTAMP"),
    DATETIME("DATETIME");

    private final String type;

    DmColumnTypeEnum(String type) {
        this.type = type;
    }
}
