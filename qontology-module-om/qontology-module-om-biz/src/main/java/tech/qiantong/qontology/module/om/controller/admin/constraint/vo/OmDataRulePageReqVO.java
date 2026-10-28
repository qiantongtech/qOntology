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
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 数据规则 Request VO 对象 om_data_rule
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Schema(description = "数据规则 Request VO")
@Data
public class OmDataRulePageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "项目版本id", example = "")
    private Long versionId;

    @Schema(description = "数据规则名称", example = "")
    private String name;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "规则类型", example = "")
    private String type;

    @Schema(description = "级别", example = "")
    private String level;

    @Schema(description = "失败条件关系", example = "")
    private String conditionRelation;

    @Schema(description = "有序失败条件数组", example = "")
    private String conditions;

    @Schema(description = "失败提示", example = "")
    private String failureMessage;




}
