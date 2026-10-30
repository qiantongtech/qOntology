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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiRelation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选对象类型关系创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选对象类型关系创建、修改 Request VO")
@Data
public class OmAiRelationSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "关系名称")
    @NotBlank(message = "关系名称不能为空")
    @Size(max = 128, message = "关系名称长度不能超过128个字符")
    private String name;
    @Schema(description = "关系描述")
    @Size(max = 1000, message = "关系描述长度不能超过1000个字符")
    private String description;
    @Schema(description = "起点对象候选ID")
    @NotNull(message = "起点对象候选ID不能为空")
    @Positive(message = "起点对象候选ID必须为正整数")
    private Long startObjectCandidateId;
    @Schema(description = "终点对象候选ID")
    @NotNull(message = "终点对象候选ID不能为空")
    @Positive(message = "终点对象候选ID必须为正整数")
    private Long endObjectCandidateId;
    @Schema(description = "是否可逆")
    @Min(value = 0, message = "是否可逆只能为0或1")
    @Max(value = 1, message = "是否可逆只能为0或1")
    private Integer reversibleFlag;
    @Schema(description = "反向名称")
    @Size(max = 128, message = "反向名称长度不能超过128个字符")
    private String reverseName;
}
