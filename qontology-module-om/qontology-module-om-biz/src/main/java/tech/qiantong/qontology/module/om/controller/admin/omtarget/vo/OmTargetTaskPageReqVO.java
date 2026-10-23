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

/**
 * 建模目标验证任务 Request VO 对象 om_target_task
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Schema(description = "建模目标验证任务 Request VO")
@Data
public class OmTargetTaskPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "目标id", example = "")
    private Long targetId;

    @Schema(description = "验证结果", example = "")
    private AcceptStatusEnum acceptResult;

    @Schema(description = "判断说明", example = "")
    private String taskExplain;




}
