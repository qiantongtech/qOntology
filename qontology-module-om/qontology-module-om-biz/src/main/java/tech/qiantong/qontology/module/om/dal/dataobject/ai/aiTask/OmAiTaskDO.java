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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.util.Date;

/**
 * AI辅助建模任务 DO 对象，对应表 {@code om_ai_task}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_task")
public class OmAiTaskDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务名称。 */
    private String taskName;
    /** 任务描述。 */
    private String description;
    /** 工作区ID。 */
    private Long workspaceId;
    /** 项目版本ID。 */
    private Long versionId;
    /** 任务状态：0待执行 1解析中 2解析成功 3解析失败。 */
    private Integer status;
    /** 当前解析阶段。 */
    private String currentStage;
    /** 候选数据数量。 */
    private Integer candidateCount;
    /** 开始时间。 */
    private Date startedTime;
    /** 完成时间。 */
    private Date finishedTime;
    /** 失败原因。 */
    private String failureReason;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
