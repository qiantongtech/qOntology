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

package tech.qiantong.qontology.module.basic.api.dto.dataType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 全局业务数据类型选项，供本体属性等模块引用；业务类型编码对应其底层基础数据类型
 */
@Data
public class BasicDataTypeOptionDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 业务数据类型名称 */
    private String name;

    /** 基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举 */
    private String dataType;

    /** 业务数据类型说明 */
    private String description;

    /** 整数、小数类型的计量单位，例如个、元；其他类型不使用 */
    private String unit;

    /** 小数类型的小数位数，0 表示不保留小数 */
    private Integer decimalPlaces;

    /** 日期类型的格式，例如 yyyy-MM-dd HH:mm:ss */
    private String format;

    /** 枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID */
    private String enumType;

    /** 枚举来源：PROJECT 当前项目版本，GLOBAL 全局；与 enumType 配合使用 */
    private String enumSource;
}
