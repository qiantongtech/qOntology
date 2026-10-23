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

package tech.qiantong.qontology.module.basic.controller.admin.dataType.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.basic.enums.DataTypeEnum;

/**
 * 数据类型 创建/修改 Request VO basic_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Schema(description = "数据类型 Response VO")
@Data
public class BasicDataTypeSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "名称", example = "")
    @NotBlank(message = "名称不能为空")
    @Size(max = 128, message = "名称长度不能超过128个字符")
    private String name;

    @Schema(description = "数据类型", example = "")
    @NotNull(message = "数据类型不能为空")
    private DataTypeEnum dataType;

    @Schema(description = "描述", example = "")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "单位", example = "")
    @Size(max = 32, message = "单位长度不能超过32个字符")
    private String unit;

    @Schema(description = "小数位数", example = "")
    private Integer decimalPlaces;

    @Schema(description = "格式", example = "")
    @Size(max = 128, message = "格式长度不能超过128个字符")
    private String format;

    @Schema(description = "枚举类型", example = "")
    @Size(max = 100, message = "枚举类型长度不能超过100个字符")
    private String enumType;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
