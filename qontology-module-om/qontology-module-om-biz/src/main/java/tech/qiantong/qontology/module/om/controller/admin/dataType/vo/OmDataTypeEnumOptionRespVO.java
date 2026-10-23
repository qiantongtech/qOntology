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

package tech.qiantong.qontology.module.om.controller.admin.dataType.vo;

import lombok.Data;

/** 枚举选择项；来源与编码共同标识一个选项。 */
@Data
public class OmDataTypeEnumOptionRespVO {
    private String enumName;
    private String enumType;
    /** PROJECT：当前项目版本；GLOBAL：基础管理。 */
    private String enumSource;

    public OmDataTypeEnumOptionRespVO(String enumName, String enumType, String enumSource) {
        this.enumName = enumName;
        this.enumType = enumType;
        this.enumSource = enumSource;
    }
}
