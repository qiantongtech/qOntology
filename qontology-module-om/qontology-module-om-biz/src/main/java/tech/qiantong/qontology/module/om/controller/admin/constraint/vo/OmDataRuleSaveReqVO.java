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

package tech.qiantong.qontology.module.om.controller.admin.constraint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 数据规则 创建/修改 Request VO om_data_rule
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Schema(description = "数据规则 创建/修改 Request VO")
@Data
public class OmDataRuleSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目版本id", example = "")
    @NotNull(message = "项目版本id不能为空")
    private Long versionId;

    @Schema(description = "数据规则名称", example = "")
    @NotBlank(message = "数据规则名称不能为空")
    private String name;

    // 描述和备注为选填；类型专属配置由页面按当前类型提交。
    @Schema(description = "描述", example = "")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

    @Schema(description = "规则类型", example = "")
    @NotBlank(message = "规则类型不能为空")
    private String type;

    @Schema(description = "级别", example = "")
    @NotBlank(message = "级别不能为空")
    private String level;

    @Schema(description = "失败条件关系", example = "")
    @NotBlank(message = "失败条件关系不能为空")
    private String conditionRelation;

    @Schema(description = "有序失败条件数组", example = "")
    @NotBlank(message = "有序失败条件数组不能为空")
    private String conditions;

    @Schema(description = "失败提示", example = "")
    @NotBlank(message = "失败提示不能为空")
    private String failureMessage;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
