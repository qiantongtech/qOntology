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

package tech.qiantong.qontology.module.om.controller.admin.enumeration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 枚举类型 创建/修改 Request VO om_enum_type
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "枚举类型 Response VO")
@Data
public class OmEnumTypeSaveReqVO extends BaseEntity {

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id")
    @jakarta.validation.constraints.NotNull(message = "项目版本不能为空")
    @jakarta.validation.constraints.Positive(message = "项目版本必须为正整数")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long enumId;

    @Schema(description = "枚举名称", example = "")
    @NotBlank(message = "枚举名称不能为空")
    @Size(max = 128, message = "枚举名称长度不能超过128个字符")
    private String enumName;

    @Schema(description = "枚举类型", example = "")
    @NotBlank(message = "枚举类型不能为空")
    @Size(max = 128, message = "枚举类型长度不能超过128个字符")
    private String enumType;

    @Schema(description = "状态", example = "")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
