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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiAttribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AI候选对象类型属性创建、修改 Request VO。
 * 系统状态、采纳结果、证据和审计字段不允许由前端直接维护。
 */
@Schema(description = "AI候选对象类型属性创建、修改 Request VO")
@Data
public class OmAiAttributeSaveReqVO {
    @Schema(description = "主键ID，新增时为空")
    private Long id;

    @Schema(description = "任务ID")
    @Positive(message = "任务ID必须为正整数")
    private Long taskId;
    @Schema(description = "对象类型候选ID")
    @Positive(message = "对象类型候选ID必须为正整数")
    private Long objectCandidateId;
    @Schema(description = "属性名称")
    @NotBlank(message = "属性名称不能为空")
    @Size(max = 128, message = "属性名称长度不能超过128个字符")
    private String name;
    @Schema(description = "属性代码")
    @NotBlank(message = "属性代码不能为空")
    @Size(max = 128, message = "属性代码长度不能超过128个字符")
    @Pattern(regexp = "[A-Za-z][A-Za-z0-9_]*", message = "属性代码须以字母开头，仅允许字母、数字和下划线")
    private String code;
    @Schema(description = "属性描述")
    @Size(max = 1000, message = "属性描述长度不能超过1000个字符")
    private String description;
    @Schema(description = "类型分类")
    @Min(value = 1, message = "类型分类只能为1或2")
    @Max(value = 2, message = "类型分类只能为1或2")
    private Integer typeCategory;
    @Schema(description = "基础类型编码")
    private Integer basicTypeCode;
    @Schema(description = "业务数据类型ID")
    @Positive(message = "业务数据类型ID必须为正整数")
    private Long businessTypeId;
    @Schema(description = "业务类型来源")
    @Size(max = 16, message = "业务类型来源长度不能超过16个字符")
    private String businessTypeSource;
    @Schema(description = "单位")
    @Size(max = 32, message = "单位长度不能超过32个字符")
    private String unit;
    @Schema(description = "小数位数")
    @Min(value = 0, message = "小数位数不能小于0")
    private Integer decimalPlaces;
    @Schema(description = "格式")
    @Size(max = 128, message = "格式长度不能超过128个字符")
    private String format;
    @Schema(description = "枚举类型")
    @Size(max = 100, message = "枚举类型长度不能超过100个字符")
    private String enumType;
    @Schema(description = "枚举来源")
    @Size(max = 16, message = "枚举来源长度不能超过16个字符")
    private String enumSource;
}
