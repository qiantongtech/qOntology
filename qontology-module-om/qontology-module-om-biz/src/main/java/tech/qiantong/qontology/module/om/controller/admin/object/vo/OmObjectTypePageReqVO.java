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
import tech.qiantong.qontology.core.page.PageParam;

import java.util.List;

/**
 * 对象类型 Request VO 对象 om_object_type
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "对象类型 Request VO")
@Data
public class OmObjectTypePageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "版本id", example = "")
    private Long versionId;

    @Schema(description = "对象类型名称", example = "")
    private String name;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "父级id", example = "")
    private Long parentId;

    @Schema(description = "祖级列表", example = "")
    private String ancestors;

    @Schema(description = "显示顺序", example = "")
    private Long orderNum;

    @Schema(description = "目标ID列表", example = "")
    private List<Long> targetIdList;


}
