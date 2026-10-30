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

/**
 * 对象类型属性响应对象
 */
@Schema(description = "对象类型属性响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeAttributeRespVO extends BaseEntity {

    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "工作区 ID，用于限定数据所属工作区")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workspaceId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long versionId;

    @Schema(description = "所属对象类型 ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectTypeId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "属性代码，在所属对象类型内唯一，以字母开头，仅包含字母、数字和下划线")
    private String code;

    @Schema(description = "类型分类：1 基础数据类型，2 业务数据类型")
    private Integer typeCategory;

    @Schema(description = "基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举")
    private Integer basicTypeCode;

    @Schema(description = "业务数据类型 ID；根据 businessTypeSource 关联 om_data_type 或 basic_data_type")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long businessTypeId;

    @Schema(description = "业务类型来源：PROJECT 当前项目版本（om_data_type），GLOBAL 全局（basic_data_type）")
    private String businessTypeSource;

    @Schema(description = "整数、小数类型的计量单位，例如个、元；其他类型不使用")
    private String unit;

    @Schema(description = "小数类型的小数位数，0 表示不保留小数")
    private Integer decimalPlaces;

    @Schema(description = "日期类型的格式，例如 yyyy-MM-dd HH:mm:ss")
    private String format;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID")
    private String enumType;

    @Schema(description = "枚举来源：PROJECT 当前项目版本，GLOBAL 全局；与 enumType 配合使用")
    private String enumSource;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否有效：true 有效，false 无效")
    private Boolean validFlag;
}
