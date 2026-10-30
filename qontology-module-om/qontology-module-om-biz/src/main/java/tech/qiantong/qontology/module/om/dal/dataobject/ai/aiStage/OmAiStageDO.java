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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.util.Date;

/**
 * AI解析阶段记录 DO 对象，对应表 {@code om_ai_stage}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_stage")
public class OmAiStageDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID。 */
    private Long taskId;
    /** 阶段编码。 */
    private String stageCode;
    /** 阶段名称。 */
    private String stageName;
    /** 阶段状态。 */
    private Integer status;
    /** 阶段顺序。 */
    private Integer sequenceNo;
    /** 阶段进度。 */
    private Integer progress;
    /** 模型密钥ID。 */
    private Long modelKeyId;
    /** 模型名称。 */
    private String modelName;
    /** 提示词资源。 */
    private String promptResource;
    /** 输入数量。 */
    private Integer inputCount;
    /** 输出数量。 */
    private Integer outputCount;
    /** Token数量。 */
    private Integer tokenCount;
    /** 耗时毫秒。 */
    private Long durationMs;
    /** 阶段摘要。 */
    private String summary;
    /** 模型流式思考过程。 */
    private String reasoningContent;
    /** 错误信息。 */
    private String errorMessage;
    /** 开始时间。 */
    private Date startedTime;
    /** 完成时间。 */
    private Date finishedTime;
}
