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

package tech.qiantong.qontology.module.basic.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

import tech.qiantong.qontology.core.annotation.Excel;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.basic.enums.ProjectStatusEnum;

/**
 * 项目管理 创建/修改 Request VO basic_project
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "项目管理 Response VO")
@Data
public class BasicProjectSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目名称", example = "")
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 128, message = "项目名称长度不能超过128个字符")
    private String name;

    @Schema(description = "项目领域", example = "")
    @NotBlank(message = "项目领域不能为空")
    @Size(max = 128, message = "项目领域长度不能超过128个字符")
    private String domain;

    @Schema(description = "命名空间", example = "")
    @NotBlank(message = "命名空间不能为空")
    @Size(max = 256, message = "命名空间长度不能超过256个字符")
    private String namespace;

    @Schema(description = "状态", example = "")
    @NotNull(message = "状态不能为空")
    private ProjectStatusEnum status;

    @Schema(description = "当前版本", example = "")
    @NotBlank(message = "当前版本不能为空")
    @Size(max = 128, message = "当前版本长度不能超过128个字符")
    private String currentVersion;

    @Excel(name = "包含范围")
    @Schema(description = "包含范围", example = "")
    @Size(max = 512, message = "包含范围长度不能超过512个字符")
    private String includeRange;

    @Excel(name = "排除范围")
    @Schema(description = "排除范围", example = "")
    @Size(max = 512, message = "排除范围长度不能超过512个字符")
    private String excludeRange;

    @Excel(name = "描述")
    @Schema(description = "描述", example = "")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
