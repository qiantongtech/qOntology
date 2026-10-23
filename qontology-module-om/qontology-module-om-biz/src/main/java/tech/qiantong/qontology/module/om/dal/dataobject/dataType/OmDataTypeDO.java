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

package tech.qiantong.qontology.module.om.dal.dataobject.dataType;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.om.enums.DataTypeEnum;

/**
 * 数据类型 DO 对象 om_data_type
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Data
@TableName(value = "om_data_type")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("om_data_type_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmDataTypeDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目版本 ID */
    private Long versionId;

    /**
     * 名称
     */
    private String name;

    /**
     * 数据类型 0-文本、1-整数、2-小数、3-布尔、4-日期
     */
    private DataTypeEnum dataType;

    /**
     * 描述
     */
    private String description;

    /**
     * 单位
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String unit;

    /**
     * 小数位数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer decimalPlaces;

    /**
     * 格式
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String format;

    /**
     * 枚举类型
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumType;

    /** 枚举来源：PROJECT 当前项目版本，GLOBAL 全局。 */
    private String enumSource;

    /**
     * 是否有效
     */
    private Boolean validFlag;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;


}
