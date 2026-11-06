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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.annotation.Excel;
import tech.qiantong.qontology.module.basic.enums.DataTypeEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据类型 Response VO 对象 basic_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Schema(description = "数据类型 Response VO")
@Data
public class BasicDataTypeRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "ID")
    @Schema(description = "ID")
    private Long id;

    @Excel(name = "名称")
    @Schema(description = "名称", example = "")
    private String name;

    @Excel(name = "数据类型")
    @Schema(description = "数据类型", example = "")
    private DataTypeEnum dataType;

    @Excel(name = "描述")
    @Schema(description = "描述", example = "")
    private String description;

    @Excel(name = "单位")
    @Schema(description = "单位", example = "")
    private String unit;

    @Excel(name = "小数位数")
    @Schema(description = "小数位数", example = "")
    private Integer decimalPlaces;

    @Excel(name = "格式")
    @Schema(description = "格式", example = "")
    private String format;

    @Schema(description = "枚举类型", example = "")
    @Excel(name = "枚举类型")
    private String enumType;

    @Schema(description = "最大长度", example = "")
    @Excel(name = "最大长度")
    private Integer maxLength;

    @Schema(description = "最小长度", example = "")
    @Excel(name = "最小长度")
    private Integer minLength;

    @Schema(description = "最早日期", example = "")
    @Excel(name = "最早日期")
    private String earliestTime;

    @Schema(description = "最晚日期", example = "")
    @Excel(name = "最晚日期")
    private String latestTime;

    @Excel(name = "true 对应文案")
    @Schema(description = "true 对应文案", example = "")
    private String trueLabel;

    @Schema(description = "false 对应文案", example = "")
    @Excel(name = "false 对应文案")
    private String falseLabel;

    @Schema(description = "总位数", example = "")
    @Excel(name = "总位数")
    private Integer allPlaces;

    @Schema(description = "格式验证", example = "")
    private String validationType;

    @Schema(description = "正则表达式", example = "")
    private String validationPattern;


    @Excel(name = "是否有效")
    @Schema(description = "是否有效", example = "")
    private Boolean validFlag;

    @Excel(name = "删除标志")
    @Schema(description = "删除标志", example = "")
    private Boolean delFlag;

    @Excel(name = "创建人")
    @Schema(description = "创建人", example = "")
    private String createBy;

    @Excel(name = "创建人id")
    @Schema(description = "创建人id", example = "")
    private Long creatorId;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "")
    private Date createTime;

    @Excel(name = "更新人")
    @Schema(description = "更新人", example = "")
    private String updateBy;

    @Excel(name = "更新人id")
    @Schema(description = "更新人id", example = "")
    private Long updaterId;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "")
    private Date updateTime;

    @Excel(name = "备注")
    @Schema(description = "备注", example = "")
    private String remark;

    /** 状态：0 禁用，1 启用。 */
    private Integer status;

    private String exampleValue;

    private java.math.BigDecimal minValue;

    private java.math.BigDecimal maxValue;
}
