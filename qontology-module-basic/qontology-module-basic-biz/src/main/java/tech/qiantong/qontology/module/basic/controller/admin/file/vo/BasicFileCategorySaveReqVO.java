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

package tech.qiantong.qontology.module.basic.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 模型数据文件分类 创建/修改 Request VO model_data_category
 *
 * @author qModel
 * @date 2026-08-18
 */
@Schema(description = "模型数据文件分类 Response VO")
@Data
public class BasicFileCategorySaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "分类名称", example = "")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "父级id", example = "")
    @NotNull(message = "父级id不能为空")
    private Long parentId;

    @Schema(description = "显示顺序", example = "")
    @NotNull(message = "显示顺序不能为空")
    private Long orderNum;

    @Schema(description = "祖级列表", example = "")
    @Size(max = 128, message = "祖级列表长度不能超过128个字符")
    private String ancestors;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
