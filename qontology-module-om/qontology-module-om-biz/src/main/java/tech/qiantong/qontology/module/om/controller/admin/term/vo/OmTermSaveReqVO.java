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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 术语 创建/修改 Request VO om_term
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "术语创建/修改 Request VO")
@Data
public class OmTermSaveReqVO extends BaseEntity {

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id")
    @jakarta.validation.constraints.NotNull(message = "项目版本不能为空")
    @jakarta.validation.constraints.Positive(message = "项目版本必须为正整数")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "标准名称", example = "")
    @NotBlank(message = "标准名称不能为空")
    @Size(max = 256, message = "标准名称长度不能超过256个字符")
    private String stdName;

    @Schema(description = "英文名称", example = "")
    @NotBlank(message = "英文名称不能为空")
    @Size(max = 256, message = "英文名称长度不能超过256个字符")
    private String enName;

    @Schema(description = "别名", example = "")
    @NotBlank(message = "别名不能为空")
    @Size(max = 1024, message = "别名长度不能超过1024个字符")
    private String alias;

    @Schema(description = "缩写", example = "")
    @NotBlank(message = "缩写不能为空")
    @Size(max = 256, message = "缩写长度不能超过256个字符")
    private String abbr;

    @Schema(description = "来源", example = "")
    @NotBlank(message = "来源不能为空")
    @Size(max = 200, message = "来源长度不能超过200个字符")
    private String source;

    @Schema(description = "描述", example = "")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
