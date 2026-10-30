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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiTarget.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import java.math.BigDecimal;

/** AI候选建模目标 Response VO。 */
@Schema(description = "AI候选建模目标 Response VO")
@Data
public class OmAiTargetRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "模型生成的稳定候选键")
    private String candidateKey;
    @Schema(description = "目标问题")
    private String question;
    @Schema(description = "问题来源")
    private String questionSource;
    @Schema(description = "业务场景")
    private String businessScenarios;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "验收标准")
    private String acceptCriterion;
    @Schema(description = "验证状态：0待验证 1已覆盖 2未覆盖")
    private String acceptStatus;
    @Schema(description = "关联元素数量")
    private Integer relatedElementCount;
    @Schema(description = "采纳状态：0待采纳 1已采纳 2采纳失败")
    private Integer adoptStatus;
    @Schema(description = "已采纳正式数据ID")
    private Long adoptedId;
    @Schema(description = "采纳结果信息")
    private String adoptMessage;
    @Schema(description = "抽取置信度")
    private BigDecimal confidence;
    @Schema(description = "删除标志")
    private Boolean delFlag;

    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Schema(description = "更新人")
    private String updateBy;
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
