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

package tech.qiantong.qontology.module.basic.api.dto.enumeration;

import lombok.Data;

/**
 * 基础管理中可用的全局枚举类型选项
 */
@Data
public class BasicEnumTypeOptionDTO {

    /** 枚举类型名称，用于界面展示 */
    private String enumName;

    /** 枚举类型编码，对应 basic_enum_type.enum_type，不是记录 ID */
    private String enumType;
}
