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
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 枚举类型 Request VO 对象 om_enum_type
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "枚举类型 Request VO")
@Data
public class OmEnumTypePageReqVO extends PageParam {

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "枚举名称", example = "")
    private String enumName;

    @Schema(description = "枚举类型", example = "")
    private String enumType;

    @Schema(description = "状态", example = "")
    private String status;




}
