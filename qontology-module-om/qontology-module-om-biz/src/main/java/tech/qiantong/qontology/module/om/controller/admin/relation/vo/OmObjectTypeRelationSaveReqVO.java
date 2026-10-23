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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "对象类型关系新增、修改请求")
@Data
public class OmObjectTypeRelationSaveReqVO {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id，当前阶段可为空")
    @jakarta.validation.constraints.NotNull(message = "项目版本不能为空")
    @jakarta.validation.constraints.Positive(message = "项目版本必须为正整数")
    private Long versionId;

    @Schema(description = "名称")
    @NotBlank(message = "关系名称不能为空")
    @Size(max = 128, message = "关系名称不能超过128个字符")
    private String name;

    @Schema(description = "描述")
    @Size(max = 256, message = "关系描述不能超过256个字符")
    private String description;

    @Schema(description = "起点对象类型id")
    @NotNull(message = "请选择起点类型")
    @Positive(message = "起点类型id无效")
    private Long startTypeId;

    @Schema(description = "终点对象类型id")
    @NotNull(message = "请选择终点类型")
    @Positive(message = "终点类型id无效")
    private Long endTypeId;

    @Schema(description = "是否可逆；0：否，1：是")
    @NotNull(message = "请选择是否可逆")
    @Min(value = 0, message = "是否可逆只能为0或1")
    @Max(value = 1, message = "是否可逆只能为0或1")
    private Integer reversibleFlag = 0;

    @Schema(description = "反向名称")
    @Size(max = 128, message = "反向名称不能超过128个字符")
    private String reverseName;

    @Schema(description = "备注")
    @Size(max = 512, message = "备注不能超过512个字符")
    private String remark;

    @Schema(description = "关联的建模目标ID列表，至少选择一个")
    @NotEmpty(message = "请至少关联一个建模目标")
    private List<@NotNull(message = "建模目标ID不能为空") @Positive(message = "建模目标ID必须为正整数") Long> targetIds;
}
