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

package tech.qiantong.qontology.module.om.controller.admin.omtarget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.enums.TargetPriorityEnum;

/**
 * 建模目标 创建/修改 Request VO om_target
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Schema(description = "建模目标 Response VO")
@Data
public class OmTargetSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目版本id", example = "")
    private Long versionId;

    @Schema(description = "目标问题", example = "")
    @NotBlank(message = "目标问题不能为空")
    @Size(max = 256, message = "目标问题长度不能超过256个字符")
    private String question;

    @Schema(description = "问题来源", example = "")
    @NotBlank(message = "问题来源不能为空")
    @Size(max = 256, message = "问题来源长度不能超过256个字符")
    private String questionSource;

    @Schema(description = "业务场景", example = "")
    @NotBlank(message = "业务场景不能为空")
    @Size(max = 256, message = "业务场景长度不能超过256个字符")
    private String businessScenarios;

    @Schema(description = "优先级", example = "")
    @NotNull(message = "优先级不能为空")
    private TargetPriorityEnum priority;

    @Schema(description = "验收标准", example = "")
    @NotBlank(message = "验收标准不能为空")
    @Size(max = 256, message = "验收标准长度不能超过256个字符")
    private String acceptCriterion;

    @Schema(description = "验证状态", example = "")
    private AcceptStatusEnum acceptStatus;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
