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
import java.util.List;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 对象类型 Request VO 对象 om_object_type
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Schema(description = "对象类型 Request VO")
@Data
public class OmObjectTypePageReqVO extends PageParam {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "工作区 ID，用于限定数据所属工作区", example = "")
    private Long workspaceId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本", example = "")
    private Long versionId;

    @Schema(description = "对象类型名称", example = "")
    private String name;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "上级对象类型 ID；根节点的父级按对象类型树规则处理", example = "")
    private Long parentId;

    @Schema(description = "祖级对象类型 ID 路径，用于维护对象类型层级", example = "")
    private String ancestors;

    @Schema(description = "对象类型在同级节点中的显示顺序", example = "")
    private Long orderNum;

    @Schema(description = "用于筛选已关联建模目标的 ID 列表", example = "")
    private List<Long> targetIdList;
}
