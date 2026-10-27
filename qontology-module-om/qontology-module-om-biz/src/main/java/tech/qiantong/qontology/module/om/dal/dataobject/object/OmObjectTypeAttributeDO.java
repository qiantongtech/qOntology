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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区 ID，用于限定数据所属工作区 */
    private Long workspaceId;

    /** 项目版本 ID，用于限定数据所属的本体版本 */
    private Long versionId;

    /** 所属对象类型 ID */
    private Long objectTypeId;

    /** 属性名称 */
    private String name;

    /** 属性代码，在所属对象类型内唯一，以字母开头，仅包含字母、数字和下划线 */
    private String code;

    /** 类型分类：1 基础数据类型，2 业务数据类型 */
    private Integer typeCategory;

    /** 基础类型编码：0 文本、1 整数、2 小数、3 布尔、4 日期、5 枚举 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer basicTypeCode;

    /** 业务数据类型 ID；根据 businessTypeSource 关联 om_data_type 或 basic_data_type */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long businessTypeId;

    /** 业务类型来源：PROJECT 当前项目版本（om_data_type），GLOBAL 全局（basic_data_type） */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String businessTypeSource;

    /** 整数、小数类型的计量单位，例如个、元；其他类型不使用 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String unit;

    /** 小数类型的小数位数，0 表示不保留小数 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer decimalPlaces;

    /** 日期类型的格式，例如 yyyy-MM-dd HH:mm:ss */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String format;

    /** 枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumType;

    /** 枚举来源：PROJECT 当前项目版本，GLOBAL 全局；与 enumType 配合使用 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumSource;

    /** 属性描述 */
    private String description;

    /** 是否有效：true 有效，false 无效 */
    @Builder.Default
    private Boolean validFlag = true;

    /** 逻辑删除标志：true 已删除，false 未删除 */
    @TableLogic
    @Builder.Default
    private Boolean delFlag = false;
}
