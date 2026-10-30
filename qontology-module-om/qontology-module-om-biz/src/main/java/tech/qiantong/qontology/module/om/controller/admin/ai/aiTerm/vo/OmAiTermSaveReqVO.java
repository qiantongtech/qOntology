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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiTerm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选术语创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选术语创建、修改 Request VO")
@Data
public class OmAiTermSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "标准名称")
    @NotBlank(message = "标准名称不能为空")
    @Size(max = 128, message = "标准名称长度不能超过128个字符")
    private String stdName;
    @Schema(description = "英文名称")
    @Size(max = 128, message = "英文名称长度不能超过128个字符")
    private String enName;
    @Schema(description = "别名")
    @Size(max = 255, message = "别名长度不能超过255个字符")
    private String alias;
    @Schema(description = "缩写")
    @Size(max = 64, message = "缩写长度不能超过64个字符")
    private String abbr;
    @Schema(description = "来源")
    @Size(max = 255, message = "来源长度不能超过255个字符")
    private String source;
    @Schema(description = "描述")
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;
}
