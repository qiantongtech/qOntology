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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 模型公理 创建/修改 Request VO om_model_axiom
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Schema(description = "模型公理 创建/修改 Request VO")
@Data
public class OmModelAxiomSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目版本id", example = "")
    @NotNull(message = "项目版本id不能为空")
    private Long versionId;

    @Schema(description = "模型公理名称", example = "")
    @NotBlank(message = "模型公理名称不能为空")
    private String name;

    // 描述和备注为选填；类型专属配置由页面按当前类型提交。
    @Schema(description = "描述", example = "")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

    @Schema(description = "公理类型", example = "")
    @NotBlank(message = "公理类型不能为空")
    private String type;

    @Schema(description = "级别", example = "")
    @NotBlank(message = "级别不能为空")
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
    @Size(max = 16, message = "业务类型来源长度不能超过16个字符")
    private String businessTypeSource;

    @Schema(description = "单位", example = "")
    @Size(max = 32, message = "单位长度不能超过32个字符")
    private String unit;

    @Schema(description = "小数位数", example = "")
    private Long decimalPlaces;

    @Schema(description = "日期格式", example = "")
    @Size(max = 128, message = "日期格式长度不能超过128个字符")
    private String format;

    @Schema(description = "枚举类型编码", example = "")
    @Size(max = 100, message = "枚举类型编码长度不能超过100个字符")
    private String enumType;

    @Schema(description = "枚举来源", example = "")
    @Size(max = 16, message = "枚举来源长度不能超过16个字符")
    private String enumSource;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
