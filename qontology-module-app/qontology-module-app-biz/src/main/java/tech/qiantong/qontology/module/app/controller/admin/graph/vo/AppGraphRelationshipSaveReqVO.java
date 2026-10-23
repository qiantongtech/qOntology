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

package tech.qiantong.qontology.module.app.controller.admin.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 三元组 Request VO ext_schema
 *
 * @author qknow
 * @date 2025-02-17
 */
@Schema(description = "三元组 Response VO")
@Data
public class AppGraphRelationshipSaveReqVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "实体id1")
    @NotBlank(message = "实体不能为空")
    private String entityId1;

    @Schema(description = "关系名称")
    @NotBlank(message = "关系不能为空")
    private String relationshipType;

    @Schema(description = "实体id2")
    @NotBlank(message = "实体不能为空")
    private String entityId2;

}
