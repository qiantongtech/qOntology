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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.om.enums.DataTypeEnum;

/**
 * 数据类型 创建/修改 Request VO om_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Schema(description = "业务数据类型新增、修改请求")
@Data
public class OmDataTypeSaveReqVO extends BaseEntity {

    /** 状态：0 禁用，1 启用。 */
    @Min(value = 0, message = "状态只能为启用或禁用")
    @Max(value = 1, message = "状态只能为启用或禁用")
    private Integer status;


    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    @NotNull(message = "项目版本不能为空")
    @Positive(message = "项目版本必须大于0")
    private Long versionId;

    @Schema(description = "名称", example = "")
    @NotBlank(message = "名称不能为空")
    @Size(max = 128, message = "名称长度不能超过128个字符")
    private String name;

    @Schema(description = "基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举", example = "")
    @NotNull(message = "数据类型不能为空")
    private DataTypeEnum dataType;

    @Schema(description = "描述", example = "")
    @Size(max = 256, message = "描述长度不能超过256个字符")
    private String description;

    @Schema(description = "整数、小数类型的计量单位，例如个、元；其他类型不使用", example = "")
    @Size(max = 32, message = "单位长度不能超过32个字符")
    private String unit;

    @Schema(description = "小数类型的小数位数，0 表示不保留小数", example = "")
    @Min(value = 0, message = "小数位数不能小于0")
    private Integer decimalPlaces;

    @Schema(description = "日期类型的格式，例如 yyyy-MM-dd HH:mm:ss", example = "")
    @Size(max = 128, message = "格式长度不能超过128个字符")
    private String format;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID", example = "")
    @Size(max = 100, message = "枚举类型长度不能超过100个字符")
    private String enumType;

    @Schema(description = "枚举来源：PROJECT 当前项目版本，GLOBAL 全局；与 enumType 配合使用")
    @Pattern(regexp = "PROJECT|GLOBAL", message = "枚举来源只能为当前项目或全局")
    private String enumSource = "PROJECT";

    /** 文本最大长度 */
    @Min(value = 0, message = "文本最大长度不能小于0")
    private Integer maxLength;

    /** 文本最小长度 */
    @Min(value = 0, message = "文本最小长度不能小于0")
    private Integer minLength;

    /** 最早日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime earliestTime;

    /** 最晚日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestTime;

    /** true 对应文案 */
    @Size(max = 64, message = "true 对应文案长度超出限制")
    private String trueValue;

    /** false 对应文案 */
    @Size(max = 64, message = "false 对应文案长度超出限制")
    private String falseValue;

    /** 总位数 */
    @Positive(message = "总位数必须为正整数")
    private Integer allPlaces;

    /** 文本格式校验编码 */
    @Size(max = 16, message = "文本格式校验编码长度超出限制")
    private String validationType;

    /** 自定义正则表达式 */
    @Size(max = 512, message = "自定义正则表达式长度超出限制")
    private String validationPattern;

    /** 示例值 */
    @Size(max = 256, message = "示例值长度超出限制")
    private String exampleValue;

    /** 数值最小值 */
    @Digits(integer = 45, fraction = 20, message = "数值范围最多45位整数、20位小数")
    private BigDecimal minValue;

    /** 数值最大值 */
    @Digits(integer = 45, fraction = 20, message = "数值范围最多45位整数、20位小数")
    private BigDecimal maxValue;

    @Schema(description = "备注信息", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
