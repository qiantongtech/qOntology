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
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import tech.qiantong.qontology.core.annotation.Excel;
import tech.qiantong.qontology.module.om.enums.DataTypeEnum;

/**
 * 数据类型 Response VO 对象 om_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Schema(description = "数据类型 Response VO")
@Data
public class OmDataTypeRespVO implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    @Excel(name = "ID")
    private Long id;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    private Long versionId;

    @Schema(description = "名称", example = "")
    @Excel(name = "名称")
    private String name;

    @Schema(description = "基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举", example = "")
    @Excel(name = "数据类型")
    private DataTypeEnum dataType;

    @Schema(description = "描述", example = "")
    @Excel(name = "描述")
    private String description;

    @Schema(description = "整数、小数类型的计量单位，例如个、元；其他类型不使用", example = "")
    @Excel(name = "单位")
    private String unit;

    @Schema(description = "小数类型的小数位数，0 表示不保留小数", example = "")
    @Excel(name = "小数位数")
    private Integer decimalPlaces;

    @Schema(description = "日期类型的格式，例如 yyyy-MM-dd HH:mm:ss", example = "")
    @Excel(name = "格式")
    private String format;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID", example = "")
    @Excel(name = "枚举类型")
    private String enumType;

    @Schema(description = "枚举来源：PROJECT 当前项目版本，GLOBAL 全局；与 enumType 配合使用")
    private String enumSource;

    @Schema(description = "是否有效：true 有效，false 无效", example = "")
    @Excel(name = "是否有效")
    private Boolean validFlag;

    @Schema(description = "逻辑删除标志：true 已删除，false 未删除", example = "")
    @Excel(name = "删除标志")
    private Boolean delFlag;

    @Schema(description = "创建人", example = "")
    @Excel(name = "创建人")
    private String createBy;

    @Schema(description = "创建人 ID", example = "")
    @Excel(name = "创建人id")
    private Long creatorId;

    @Schema(description = "创建时间", example = "")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新人", example = "")
    @Excel(name = "更新人")
    private String updateBy;

    @Schema(description = "更新人 ID", example = "")
    @Excel(name = "更新人id")
    private Long updaterId;

    @Schema(description = "更新时间", example = "")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "备注信息", example = "")
    @Excel(name = "备注")
    private String remark;
}
