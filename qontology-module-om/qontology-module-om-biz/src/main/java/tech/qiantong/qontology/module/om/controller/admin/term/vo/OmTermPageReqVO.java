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

package tech.qiantong.qontology.module.om.controller.admin.term.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 术语 Request VO 对象 om_term
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "术语 Request VO")
@Data
public class OmTermPageReqVO extends PageParam {

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "术语的标准名称", example = "")
    private String stdName;

    @Schema(description = "术语的英文名称", example = "")
    private String enName;

    @Schema(description = "术语别名", example = "")
    private String alias;

    @Schema(description = "术语缩写", example = "")
    private String abbr;

    @Schema(description = "术语来源", example = "")
    private String source;

    @Schema(description = "描述", example = "")
    private String description;
}
