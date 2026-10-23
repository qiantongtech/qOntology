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
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;

@Schema(description = "对象类型关系响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeRelationRespVO extends BaseEntity {
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "工作区id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workspaceId;

    @Schema(description = "版本id，当前阶段可为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long versionId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "起点对象类型id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long startTypeId;

    @Schema(description = "终点对象类型id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long endTypeId;

    @Schema(description = "起点对象类型名称")
    private String startTypeName;

    @Schema(description = "终点对象类型名称")
    private String endTypeName;

    @Schema(description = "是否可逆；0：否，1：是")
    private Integer reversibleFlag;

    @Schema(description = "反向名称")
    private String reverseName;

    @Schema(description = "是否有效；0：无效，1：有效")
    private Boolean validFlag;
}
