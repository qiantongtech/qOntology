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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 项目版本状态修改 Request VO，0 禁用，1 启用。
 *
 * @author qOntology
 * @date 2026-11-05
 */
@Schema(description = "项目版本状态修改请求")
@Data
public class BasicProjectVersionStatusReqVO {

    @Schema(description = "项目版本编号", example = "1")
    @NotNull(message = "项目版本编号不能为空")
    @Positive(message = "项目版本编号必须大于0")
    private Long id;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    @NotNull(message = "请选择状态")
    @Min(value = 0, message = "状态只能为启用或禁用")
    @Max(value = 1, message = "状态只能为启用或禁用")
    private Integer status;
}
