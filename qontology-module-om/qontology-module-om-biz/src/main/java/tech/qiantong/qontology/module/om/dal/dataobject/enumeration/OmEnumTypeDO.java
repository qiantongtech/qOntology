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

package tech.qiantong.qontology.module.om.dal.dataobject.enumeration;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 枚举类型 DO 对象 om_enum_type
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Data
@TableName(value = "om_enum_type")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写
// @KeySequence("om_enum_type_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmEnumTypeDO extends BaseEntity {

    /** 工作区 ID，用于限定数据所属工作区 */
    private Long workspaceId;

    /** 项目版本 ID，用于限定数据所属的本体版本 */
    private Long versionId;

    /** 序列化版本号 */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 枚举类型记录 ID */
    @TableId(value = "enum_id", type = IdType.AUTO)
    private Long enumId;

    /** 枚举类型名称，用于界面展示 */
    private String enumName;

    /** 枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID */
    private String enumType;

    /** 状态 */
    private String status;

    /** 是否有效：true 有效，false 无效 */
    private Boolean validFlag;

    /** 逻辑删除标志：true 已删除，false 未删除 */
    @TableLogic
    private Boolean delFlag;
}
