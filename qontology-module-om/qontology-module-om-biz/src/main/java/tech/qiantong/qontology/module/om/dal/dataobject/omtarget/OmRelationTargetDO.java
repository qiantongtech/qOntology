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

package tech.qiantong.qontology.module.om.dal.dataobject.omtarget;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 对象类型关系与建模目标关联 DO 对象 om_relation_target
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Data
@TableName("om_relation_target")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OmRelationTargetDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 版本id */
    private Long versionId;

    /** 建模目标id */
    private Long targetId;

    /** 对象类型关系id */
    private Long relationId;

    /** 是否有效;0：无效，1：有效 */
    @Builder.Default
    private Boolean validFlag = true;

    /** 删除标志;1：已删除，0：未删除 */
    @TableLogic
    @Builder.Default
    private Boolean delFlag = false;
}
