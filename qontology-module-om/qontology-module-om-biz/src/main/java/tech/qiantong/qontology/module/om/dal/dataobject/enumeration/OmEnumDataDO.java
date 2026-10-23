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

import lombok.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 枚举数据 DO 对象 om_enum_data
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Data
@TableName(value = "om_enum_data")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("om_enum_data_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmEnumDataDO extends BaseEntity {

    /** 工作区id */
    private Long workspaceId;

    /** 版本id */
    private Long versionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 枚举编码 */
    @TableId(value = "enum_code", type = IdType.AUTO)
    private Long enumCode;

    /** 枚举排序 */
    private Long enumSort;

    /** 枚举标签 */
    private String enumLabel;

    /** 枚举键值 */
    private String enumValue;

    /** 枚举类型 */
    private String enumType;

    /** 状态 */
    private String status;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    @TableLogic
    private Boolean delFlag;


}
