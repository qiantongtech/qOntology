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

package tech.qiantong.qontology.module.om.controller.admin.object.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Schema(description = "对象类型属性新增、修改请求")
@Data
public class OmObjectTypeAttributeSaveReqVO {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id，当前阶段可为空")
    private Long versionId;

    @Schema(description = "对象类型id")
    @NotNull(message = "对象类型id不能为空")
    @Positive(message = "对象类型id无效")
    private Long objectTypeId;

    @Schema(description = "名称")
    @NotBlank(message = "属性名称不能为空")
    @Size(max = 128, message = "属性名称长度不能超过128个字符")
    private String name;

    /** 属性代码，在对象类型内唯一。 */
    @NotBlank(message = "属性代码不能为空")
    @Size(max = 128, message = "属性代码不能超过128个字符")
    @Pattern(regexp = "[A-Za-z][A-Za-z0-9_]*", message = "属性代码须以字母开头，仅允许字母、数字和下划线")
    private String code;

    @Schema(description = "类型分类；1：基础数据类型，2：业务数据类型", allowableValues = {"1", "2"})
    @NotNull(message = "请选择数据类型")
    @Min(value = 1, message = "数据类型只能为基础数据类型或业务数据类型")
    @Max(value = 2, message = "数据类型只能为基础数据类型或业务数据类型")
    private Integer typeCategory;

    @Schema(description = "基础类型编码：0文本、1整数、2小数、3布尔、4日期、5枚举")
    private Integer basicTypeCode;

    @Schema(description = "业务数据类型ID，按来源关联 om_data_type 或 basic_data_type")
    private Long businessTypeId;

    /** 业务类型来源：PROJECT 当前项目，GLOBAL 全局。 */
    @Pattern(regexp = "PROJECT|GLOBAL", message = "业务类型来源无效")
    private String businessTypeSource;

    @Size(max = 32, message = "类型配置长度超出限制")
    private String unit;

    @Min(value = 0, message = "小数位数不能小于0")
    private Integer decimalPlaces;

    @Size(max = 128, message = "类型配置长度超出限制")
    private String format;

    @Size(max = 100, message = "类型配置长度超出限制")
    private String enumType;

    @Size(max = 16, message = "类型配置长度超出限制")
    private String enumSource;


    @Schema(description = "描述")
    @Size(max = 512, message = "属性描述长度不能超过512个字符")
    private String description;

    @Schema(description = "备注")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
