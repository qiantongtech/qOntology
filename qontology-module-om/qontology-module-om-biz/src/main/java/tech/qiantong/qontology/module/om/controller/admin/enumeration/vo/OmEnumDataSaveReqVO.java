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
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 枚举数据 创建/修改 Request VO om_enum_data
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "枚举数据 Response VO")
@Data
public class OmEnumDataSaveReqVO extends BaseEntity {

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id")
    @jakarta.validation.constraints.NotNull(message = "项目版本不能为空")
    @jakarta.validation.constraints.Positive(message = "项目版本必须为正整数")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long enumCode;

    @Schema(description = "枚举排序", example = "")
    @NotNull(message = "枚举排序不能为空")
    @jakarta.validation.constraints.Min(value = 0, message = "枚举排序不能小于0")
    private Long enumSort;

    @Schema(description = "枚举标签", example = "")
    @NotBlank(message = "枚举标签不能为空")
    @Size(max = 128, message = "枚举标签长度不能超过128个字符")
    private String enumLabel;

    @Schema(description = "枚举键值", example = "")
    @NotBlank(message = "枚举键值不能为空")
    @Size(max = 128, message = "枚举键值长度不能超过128个字符")
    private String enumValue;

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
