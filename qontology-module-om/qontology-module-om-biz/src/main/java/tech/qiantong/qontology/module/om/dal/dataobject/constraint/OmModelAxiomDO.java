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

package tech.qiantong.qontology.module.om.dal.dataobject.constraint;

import lombok.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 模型公理 DO 对象 om_model_axiom
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@TableName(value = "om_model_axiom")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("om_model_axiom_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmModelAxiomDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目版本id */
    private Long versionId;

    /** 模型公理名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 公理类型 */
    private String type;

    /** 级别 */
    private String level;

    /** 对象类型id */
    // 公理类型切换后，允许显式清空旧类型的专属字段。
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long objectTypeId;

    /** 另一对象类型id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long otherTypeId;

    /** 对象类型关系id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long relationId;

    /** Domain对象类型id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long domainId;

    /** Range对象类型id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long rangeId;

    /** 数据属性id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long attributeId;

    /** 属性值类型分类 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String typeCategory;

    /** 基础类型编码 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String basicTypeCode;

    /** 业务类型id */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long businessTypeId;

    /** 业务类型来源 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String businessTypeSource;

    /** 单位 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String unit;

    /** 小数位数 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long decimalPlaces;

    /** 日期格式 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String format;

    /** 枚举类型编码 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumType;

    /** 枚举来源 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumSource;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    @TableLogic
    private Boolean delFlag;


}
