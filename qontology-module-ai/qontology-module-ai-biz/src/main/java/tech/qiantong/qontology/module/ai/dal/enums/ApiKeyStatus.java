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

package tech.qiantong.qontology.module.ai.dal.enums;

/**
 * apiKey 状态
 *
 * @author fabian
 */
public enum ApiKeyStatus {
    /**
     * 未配置
     */
    NOT_CONFIG(0),
    /**
     * 已配置
     */
    CONFIG(1),
    /**
     * 已同步模型
     */
    SYNC(2);

    private final Integer code;

    ApiKeyStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
