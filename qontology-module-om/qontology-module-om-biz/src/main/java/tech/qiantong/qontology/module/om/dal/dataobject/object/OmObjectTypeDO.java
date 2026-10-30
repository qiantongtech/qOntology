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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 对象类型 DO 对象 om_object_type
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Data
@TableName(value = "om_object_type")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写
// @KeySequence("om_object_type_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeDO extends BaseEntity {

    /** 序列化版本号 */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区 ID，用于限定数据所属工作区 */
    private Long workspaceId;

    /** 项目版本 ID，用于限定数据所属的本体版本 */
    private Long versionId;

    /** 对象类型名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 上级对象类型 ID；根节点的父级按对象类型树规则处理 */
    private Long parentId;

    /** 祖级对象类型 ID 路径，用于维护对象类型层级 */
    private String ancestors;

    /** 对象类型在同级节点中的显示顺序 */
    private Long orderNum;

    /** 是否有效：true 有效，false 无效 */
    private Boolean validFlag;

    /** 逻辑删除标志：true 已删除，false 未删除 */
    @TableLogic
    private Boolean delFlag;
}
