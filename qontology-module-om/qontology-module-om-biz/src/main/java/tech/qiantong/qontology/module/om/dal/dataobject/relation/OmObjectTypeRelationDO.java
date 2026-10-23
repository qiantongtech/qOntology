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

package tech.qiantong.qontology.module.om.dal.dataobject.relation;

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
 * 对象类型关系 DO 对象 om_object_type_relation
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Data
@TableName("om_object_type_relation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeRelationDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 版本id，当前阶段可为空 */
    private Long versionId;

    /** 名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 起点对象类型id */
    private Long startTypeId;

    /** 终点对象类型id */
    private Long endTypeId;

    /** 起点对象类型名称 */
    @TableField(exist = false)
    private String startTypeName;

    /** 终点对象类型名称 */
    @TableField(exist = false)
    private String endTypeName;

    /** 是否可逆；0：否，1：是 */
    @Builder.Default
    private Integer reversibleFlag = 0;

    /** 反向名称 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String reverseName;

    /** 是否有效；0：无效，1：有效 */
    @Builder.Default
    private Boolean validFlag = true;

    /** 删除标志；1：已删除，0：未删除 */
    @TableLogic
    @Builder.Default
    private Boolean delFlag = false;
}
