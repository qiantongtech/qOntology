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

package tech.qiantong.qontology.module.om.controller.admin.dataType.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 数据类型 Request VO 对象 om_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Schema(description = "数据类型 Request VO")
@Data
public class OmDataTypePageReqVO extends PageParam {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    @NotNull(message = "项目版本不能为空")
    @Positive(message = "项目版本必须大于0")
    private Long versionId;

    @Schema(description = "名称", example = "")
    private String name;

    @Schema(description = "基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举", example = "")
    private String dataType;

    @Schema(description = "描述", example = "")
    private String description;

    @Schema(description = "整数、小数类型的计量单位，例如个、元；其他类型不使用", example = "")
    private String unit;

    @Schema(description = "小数类型的小数位数，0 表示不保留小数", example = "")
    private Integer decimalPlaces;

    @Schema(description = "日期类型的格式，例如 yyyy-MM-dd HH:mm:ss", example = "")
    private String format;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID", example = "")
    private String enumType;
}
