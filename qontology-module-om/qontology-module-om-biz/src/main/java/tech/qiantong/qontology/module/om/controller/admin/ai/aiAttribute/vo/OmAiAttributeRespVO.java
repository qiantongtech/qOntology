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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiAttribute.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import java.math.BigDecimal;

/** AI候选对象类型属性 Response VO。 */
@Schema(description = "AI候选对象类型属性 Response VO")
@Data
public class OmAiAttributeRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "对象类型候选ID")
    private Long objectCandidateId;
    @Schema(description = "属性名称")
    private String name;
    @Schema(description = "属性代码")
    private String code;
    @Schema(description = "属性描述")
    private String description;
    @Schema(description = "类型分类")
    private Integer typeCategory;
    @Schema(description = "基础类型编码")
    private Integer basicTypeCode;
    @Schema(description = "业务数据类型ID")
    private Long businessTypeId;
    @Schema(description = "业务类型来源")
    private String businessTypeSource;
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "小数位数")
    private Integer decimalPlaces;
    @Schema(description = "格式")
    private String format;
    @Schema(description = "枚举类型")
    private String enumType;
    @Schema(description = "枚举来源")
    private String enumSource;
    @Schema(description = "采纳状态：0待采纳 1已采纳 2采纳失败")
    private Integer adoptStatus;
    @Schema(description = "已采纳正式数据ID")
    private Long adoptedId;
    @Schema(description = "采纳结果信息")
    private String adoptMessage;
    @Schema(description = "抽取置信度")
    private BigDecimal confidence;
    @Schema(description = "删除标志")
    private Boolean delFlag;

    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Schema(description = "更新人")
    private String updateBy;
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
