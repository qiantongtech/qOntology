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

/**
 * 建模目标验证任务 创建/修改 Request VO om_target_task
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Schema(description = "建模目标验证任务 Response VO")
@Data
public class OmTargetTaskSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "目标id", example = "")
    @NotNull(message = "目标id不能为空")
    private Long targetId;

    @Schema(description = "验证结果", example = "")
    @NotNull(message = "验证结果不能为空")
    private AcceptStatusEnum acceptResult;

    @Schema(description = "判断说明", example = "")
    @NotBlank(message = "判断说明不能为空")
    @Size(max = 256, message = "判断说明长度不能超过256个字符")
    private String taskExplain;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;

}
