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

package tech.qiantong.qontology.module.om.controller.admin.relation.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 对象关系响应对象
 */
@Schema(description = "关系起点、终点对象类型选项响应")
@Data
public class OmRelationTypeOptionRespVO {

    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "上级对象类型 ID；根节点的父级按对象类型树规则处理")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long versionId;

    @Schema(description = "名称")
    private String name;
}
