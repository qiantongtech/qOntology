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
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.enums.TargetPriorityEnum;

/**
 * 建模目标 Request VO 对象 om_target
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Schema(description = "建模目标 Request VO")
@Data
public class OmTargetPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "项目版本id", example = "")
    private Long versionId;

    @Schema(description = "目标问题", example = "")
    private String question;

    @Schema(description = "问题来源", example = "")
    private String questionSource;

    @Schema(description = "业务场景", example = "")
    private String businessScenarios;

    @Schema(description = "优先级", example = "")
    private String priority;

    @Schema(description = "验收标准", example = "")
    private String acceptCriterion;

    @Schema(description = "验证状态", example = "")
    private String acceptStatus;


}
