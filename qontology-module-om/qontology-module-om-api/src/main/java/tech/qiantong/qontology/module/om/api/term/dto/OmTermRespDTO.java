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

package tech.qiantong.qontology.module.om.api.term.dto;

import lombok.*;

/**
 * 术语 DTO 对象 om_term
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Data
public class OmTermRespDTO {

    private static final long serialVersionUID = 1L;

    /** 工作区id */
    private Long workspaceId;

    /** 版本id */
    private Long versionId;

    /** ID */
    private Long id;

    /** 标准名称 */
    private String stdName;

    /** 英文名称 */
    private String enName;

    /** 别名 */
    private String alias;

    /** 缩写 */
    private String abbr;

    /** 来源 */
    private String source;

    /** 描述 */
    private String description;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    private Boolean delFlag;


}
