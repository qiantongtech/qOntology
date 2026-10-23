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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;

@Schema(description = "对象类型属性响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeAttributeRespVO extends BaseEntity {
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "工作区id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workspaceId;

    @Schema(description = "版本id，当前阶段可为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long versionId;

    @Schema(description = "对象类型id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectTypeId;

    @Schema(description = "名称")
    private String name;

    /** 属性代码，在对象类型内唯一。 */
    private String code;

    @Schema(description = "类型分类；1：基础数据类型，2：业务数据类型")
    private Integer typeCategory;

    @Schema(description = "基础类型编码：0文本、1整数、2小数、3布尔、4日期、5枚举")
    private Integer basicTypeCode;

    @Schema(description = "业务数据类型ID，按来源关联 om_data_type 或 basic_data_type")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long businessTypeId;

    /** 业务类型来源：PROJECT 当前项目，GLOBAL 全局。 */
    private String businessTypeSource;

    private String unit;

    private Integer decimalPlaces;

    private String format;

    private String enumType;

    private String enumSource;


    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否有效；0：无效，1：有效")
    private Boolean validFlag;
}
