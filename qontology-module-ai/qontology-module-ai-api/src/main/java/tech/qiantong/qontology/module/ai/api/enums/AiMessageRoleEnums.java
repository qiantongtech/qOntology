/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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

package tech.qiantong.qontology.module.ai.api.enums;

import lombok.Getter;

/**
 * 提示词角色枚举
 *
 * @author asd
 */
@Getter
public enum AiMessageRoleEnums {

    // SYSTEM
    SYSTEM(1),
    // USER
    USER(2),
    // ASSISTANT
    ASSISTANT(3);

    private final Integer code;

    AiMessageRoleEnums(Integer code) {
        this.code = code;
    }

    public static AiMessageRoleEnums getByCode(Integer code) {
        for (AiMessageRoleEnums value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
