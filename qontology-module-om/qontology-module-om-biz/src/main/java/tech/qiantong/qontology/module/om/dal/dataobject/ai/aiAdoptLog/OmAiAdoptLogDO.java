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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAdoptLog;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * AI候选采纳日志 DO 对象，对应表 {@code om_ai_adopt_log}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_adopt_log")
public class OmAiAdoptLogDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID。 */
    private Long taskId;
    /** 候选数据类型。 */
    private String dataType;
    /** 候选数据ID。 */
    private Long dataId;
    /** 正式数据ID。 */
    private Long formalId;
    /** 采纳状态。 */
    private Integer status;
    /** 采纳信息。 */
    private String message;
    /** 操作人ID。 */
    private Long operatorId;
    /** 操作人名称。 */
    private String operatorName;
}
