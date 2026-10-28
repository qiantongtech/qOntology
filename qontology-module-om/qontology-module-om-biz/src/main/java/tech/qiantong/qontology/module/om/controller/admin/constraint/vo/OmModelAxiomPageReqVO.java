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
 * 模型公理 Request VO 对象 om_model_axiom
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Schema(description = "模型公理 Request VO")
@Data
public class OmModelAxiomPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "项目版本id", example = "")
    private Long versionId;

    @Schema(description = "模型公理名称", example = "")
    private String name;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "公理类型", example = "")
    private String type;

    @Schema(description = "级别", example = "")
    private String level;

    @Schema(description = "对象类型id", example = "")
    private Long objectTypeId;

    @Schema(description = "另一对象类型id", example = "")
    private Long otherTypeId;

    @Schema(description = "对象类型关系id", example = "")
    private Long relationId;

    @Schema(description = "Domain对象类型id", example = "")
    private Long domainId;

    @Schema(description = "Range对象类型id", example = "")
    private Long rangeId;

    @Schema(description = "数据属性id", example = "")
    private Long attributeId;

    @Schema(description = "属性值类型分类", example = "")
    private String typeCategory;

    @Schema(description = "基础类型编码", example = "")
    private String basicTypeCode;

    @Schema(description = "业务类型id", example = "")
    private Long businessTypeId;

    @Schema(description = "业务类型来源", example = "")
    private String businessTypeSource;

    @Schema(description = "单位", example = "")
    private String unit;

    @Schema(description = "小数位数", example = "")
    private Long decimalPlaces;

    @Schema(description = "日期格式", example = "")
    private String format;

    @Schema(description = "枚举类型编码", example = "")
    private String enumType;

    @Schema(description = "枚举来源", example = "")
    private String enumSource;
}
