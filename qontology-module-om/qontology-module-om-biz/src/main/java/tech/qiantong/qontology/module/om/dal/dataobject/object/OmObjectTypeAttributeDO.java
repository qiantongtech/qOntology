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

package tech.qiantong.qontology.module.om.dal.dataobject.object;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 对象类型属性 DO 对象 om_object_type_attribute
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Data
@TableName("om_object_type_attribute")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeAttributeDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 版本id */
    private Long versionId;

    /** 对象类型id */
    private Long objectTypeId;

    /** 属性名称 */
    private String name;

    /** 属性代码，在对象类型内唯一。 */
    private String code;

    /** 类型分类；1：基础数据类型，2：业务数据类型 */
    private Integer typeCategory;

    /** 基础类型编码：0文本、1整数、2小数、3布尔、4日期、5枚举 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer basicTypeCode;

    /** 业务数据类型，按来源关联 om_data_type 或 basic_data_type */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long businessTypeId;

    /** 业务类型来源：PROJECT 当前项目，GLOBAL 全局。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String businessTypeSource;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String unit;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer decimalPlaces;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String format;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumType;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumSource;


    /** 属性描述 */
    private String description;

    /** 是否有效;0：无效，1：有效 */
    @Builder.Default
    private Boolean validFlag = true;

    /** 删除标志;1：已删除，0：未删除 */
    @TableLogic
    @Builder.Default
    private Boolean delFlag = false;
}
