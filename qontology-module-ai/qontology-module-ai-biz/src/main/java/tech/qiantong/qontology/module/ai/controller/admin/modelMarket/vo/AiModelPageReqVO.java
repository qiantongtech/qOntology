/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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

package tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * AI 模型 Request VO 对象 ai_model
 *
 * @author qontology
 * @date 2025-12-23
 */
@Schema(description = "AI 模型 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiModelPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "秘钥id", example = "")
    private Long keyId;

    @Schema(description = "模型名称", example = "")
    private String name;

    @Schema(description = "模型标志", example = "")
    private String model;

    @Schema(description = "平台", example = "")
    private String platform;

    @Schema(description = "类型", example = "")
    private Integer type;

    @Schema(description = "排序值", example = "")
    private Long sort;

    @Schema(description = "状态", example = "")
    private Integer status;




}
