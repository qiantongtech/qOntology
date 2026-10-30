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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiEnumData.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选枚举数据创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选枚举数据创建、修改 Request VO")
@Data
public class OmAiEnumDataSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "候选枚举类型ID")
    @Positive(message = "候选枚举类型ID必须为正整数")
    private Long enumTypeId;
    @Schema(description = "枚举排序")
    private Long enumSort;
    @Schema(description = "枚举标签")
    @NotBlank(message = "枚举标签不能为空")
    @Size(max = 128, message = "枚举标签长度不能超过128个字符")
    private String enumLabel;
    @Schema(description = "枚举键值")
    @NotBlank(message = "枚举键值不能为空")
    @Size(max = 128, message = "枚举键值长度不能超过128个字符")
    private String enumValue;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
