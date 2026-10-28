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

package tech.qiantong.qontology.module.om.api.enumeration.dto;

import lombok.*;

/**
 * 枚举数据 DTO 对象 om_enum_data
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Data
public class OmEnumDataRespDTO {

    private static final long serialVersionUID = 1L;

    /** 工作区id */
    private Long workspaceId;

    /** 版本id */
    private Long versionId;

    /** 枚举编码 */
    private Long enumCode;

    /** 枚举排序 */
    private Long enumSort;

    /** 枚举标签 */
    private String enumLabel;

    /** 枚举键值 */
    private String enumValue;

    /** 枚举类型 */
    private String enumType;

    /** 状态 */
    private String status;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    private Boolean delFlag;


}
