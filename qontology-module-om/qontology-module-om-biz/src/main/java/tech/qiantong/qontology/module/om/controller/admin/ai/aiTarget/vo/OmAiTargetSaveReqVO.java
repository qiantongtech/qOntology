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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiTarget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选建模目标创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选建模目标创建、修改 Request VO")
@Data
public class OmAiTargetSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "目标问题")
    @NotBlank(message = "目标问题不能为空")
    @Size(max = 500, message = "目标问题长度不能超过500个字符")
    private String question;
    @Schema(description = "问题来源")
    @NotBlank(message = "问题来源不能为空")
    @Size(max = 255, message = "问题来源长度不能超过255个字符")
    private String questionSource;
    @Schema(description = "业务场景")
    @NotBlank(message = "业务场景不能为空")
    @Size(max = 1000, message = "业务场景长度不能超过1000个字符")
    private String businessScenarios;
    @Schema(description = "优先级")
    @NotNull(message = "优先级不能为空")
    private Integer priority;
    @Schema(description = "验收标准")
    @NotBlank(message = "验收标准不能为空")
    @Size(max = 1000, message = "验收标准长度不能超过1000个字符")
    private String acceptCriterion;
}
