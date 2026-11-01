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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * AI候选建模目标 DO 对象，对应表 {@code om_ai_target}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_target")
public class OmAiTargetDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID。 */
    private Long taskId;
    /** 模型生成的稳定候选键。 */
    private String candidateKey;
    /** 目标问题。 */
    private String question;
    /** 问题来源。 */
    private String questionSource;
    /** 业务场景。 */
    private String businessScenarios;
    /** 优先级。 */
    private Integer priority;
    /** 验收标准。 */
    private String acceptCriterion;
    /** 验证状态：0待验证 1已覆盖 2未覆盖。 */
    private String acceptStatus;
    /** 关联元素数量。 */
    private Integer relatedElementCount;
    /** 采纳状态：0待采纳 1已采纳 2采纳失败。 */
    private Integer adoptStatus;
    /** 已采纳正式数据ID。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long adoptedId;
    /** 采纳结果信息。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String adoptMessage;
    /** 抽取置信度。 */
    private BigDecimal confidence;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
