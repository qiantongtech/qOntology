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
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 项目版本 Request VO 对象 basic_project_version
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "项目版本 Request VO")
@Data
public class BasicProjectVersionPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "项目id", example = "")
    private Long projectId;

    @Schema(description = "版本号", example = "")
    private String version;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "基准版本", example = "")
    private String baseVersion;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    @Min(value = 0, message = "状态只能为启用或禁用")
    @Max(value = 1, message = "状态只能为启用或禁用")
    private Integer status;

}
