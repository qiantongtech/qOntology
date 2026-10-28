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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import tech.qiantong.qontology.core.annotation.Excel;
import java.util.Date;
import java.io.Serializable;

/**
 * 模型公理 Response VO 对象 om_model_axiom
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Schema(description = "模型公理 Response VO")
@Data
public class OmModelAxiomRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "ID")
    @Schema(description = "ID")
    private Long id;

    @Excel(name = "项目版本id")
    @Schema(description = "项目版本id", example = "")
    private Long versionId;

    @Excel(name = "模型公理名称")
    @Schema(description = "模型公理名称", example = "")
    private String name;

    @Excel(name = "描述")
    @Schema(description = "描述", example = "")
    private String description;

    @Excel(name = "公理类型")
    @Schema(description = "公理类型", example = "")
    private String type;

    @Excel(name = "级别")
    @Schema(description = "级别", example = "")
    private String level;

    @Excel(name = "对象类型id")
    @Schema(description = "对象类型id", example = "")
    private Long objectTypeId;

    @Excel(name = "另一对象类型id")
    @Schema(description = "另一对象类型id", example = "")
    private Long otherTypeId;

    @Excel(name = "对象类型关系id")
    @Schema(description = "对象类型关系id", example = "")
    private Long relationId;

    @Schema(description = "关系名称")
    private String relationName;


    @Excel(name = "Domain对象类型id")
    @Schema(description = "Domain对象类型id", example = "")
    private Long domainId;

    @Excel(name = "Range对象类型id")
    @Schema(description = "Range对象类型id", example = "")
    private Long rangeId;

    @Excel(name = "数据属性id")
    @Schema(description = "数据属性id", example = "")
    private Long attributeId;

    @Excel(name = "属性值类型分类")
    @Schema(description = "属性值类型分类", example = "")
    private String typeCategory;

    @Excel(name = "基础类型编码")
    @Schema(description = "基础类型编码", example = "")
    private String basicTypeCode;

    @Excel(name = "业务类型id")
    @Schema(description = "业务类型id", example = "")
    private Long businessTypeId;

    @Excel(name = "业务类型来源")
    @Schema(description = "业务类型来源", example = "")
    private String businessTypeSource;

    @Excel(name = "单位")
    @Schema(description = "单位", example = "")
    private String unit;

    @Excel(name = "小数位数")
    @Schema(description = "小数位数", example = "")
    private Long decimalPlaces;

    @Excel(name = "日期格式")
    @Schema(description = "日期格式", example = "")
    private String format;

    @Excel(name = "枚举类型编码")
    @Schema(description = "枚举类型编码", example = "")
    private String enumType;

    @Excel(name = "枚举来源")
    @Schema(description = "枚举来源", example = "")
    private String enumSource;

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

}
