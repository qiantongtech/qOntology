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

package tech.qiantong.qontology.module.om.controller.admin.object.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 对象类型 创建/修改 Request VO om_object_type
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "对象类型 Response VO")
@Data
public class OmObjectTypeSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "版本id", example = "")
    @jakarta.validation.constraints.NotNull(message = "项目版本不能为空")
    @jakarta.validation.constraints.Positive(message = "项目版本必须为正整数")
    private Long versionId;

    @Schema(description = "对象类型名称", example = "")
    @NotBlank(message = "对象类型名称不能为空")
    @Size(max = 128, message = "对象类型名称长度不能超过128个字符")
    private String name;

    @Schema(description = "描述", example = "")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

    @Schema(description = "父级id", example = "")
    private Long parentId;

    @Schema(description = "祖级列表", example = "")

    @Size(max = 128, message = "祖级列表长度不能超过128个字符")
    private String ancestors;

    @Schema(description = "显示顺序", example = "")
    private Long orderNum;

    @Schema(description = "关联的建模目标ID列表，至少选择一个")
    @NotEmpty(message = "请至少关联一个建模目标")
    private List<@NotNull(message = "建模目标ID不能为空") @Positive(message = "建模目标ID必须为正整数") Long> targetIds;

    @Schema(description = "备注", example = "")

    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
