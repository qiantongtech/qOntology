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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiObject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选对象类型创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选对象类型创建、修改 Request VO")
@Data
public class OmAiObjectSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "对象类型名称")
    @NotBlank(message = "对象类型名称不能为空")
    @Size(max = 128, message = "对象类型名称长度不能超过128个字符")
    private String name;
    @Schema(description = "描述")
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;
    @Schema(description = "父对象候选ID")
    @Positive(message = "父对象候选ID必须为正整数")
    private Long parentCandidateId;
    @Schema(description = "显示顺序")
    private Long orderNum;
}
