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
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.module.basic.enums.ProjectStatusEnum;

/**
 * 项目管理 Request VO 对象 basic_project
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "项目管理 Request VO")
@Data
public class BasicProjectPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "项目名称", example = "")
    private String name;

    @Schema(description = "项目领域", example = "")
    private String domain;

    @Schema(description = "命名空间", example = "")
    private String namespace;

    @Schema(description = "状态", example = "")
    private String status;

    @Schema(description = "当前版本", example = "")
    private String currentVersion;




}
