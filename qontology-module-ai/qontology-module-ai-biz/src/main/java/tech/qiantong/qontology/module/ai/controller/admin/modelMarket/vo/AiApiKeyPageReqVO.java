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
 * API秘钥 Request VO 对象 ai_api_key
 *
 * @author qontology
 * @date 2025-12-23
 */
@Schema(description = "API秘钥 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiApiKeyPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "名称", example = "")
    private String name;

    @Schema(description = "平台", example = "")
    private String platform;

    @Schema(description = "状态", example = "")
    private Integer status;

    @Schema(description = "平台标签", example = "")
    private String platformTag ;

    @Schema(description = "描述", example = "")
    private String description ;

    @Schema(description = "状态字符串", example = "")
    private String statusStr;
}
