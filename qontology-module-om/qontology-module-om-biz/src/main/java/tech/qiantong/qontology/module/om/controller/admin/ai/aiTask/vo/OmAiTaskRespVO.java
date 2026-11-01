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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** AI辅助建模任务 Response VO。 */
@Schema(description = "AI辅助建模任务 Response VO")
@Data
public class OmAiTaskRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务名称")
    private String taskName;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "工作区ID")
    private Long workspaceId;
    @Schema(description = "项目版本ID")
    private Long versionId;
    @Schema(description = "任务状态：0待执行 1解析中 2解析成功 3解析失败")
    private Integer status;
    @Schema(description = "当前解析阶段")
    private String currentStage;
    @Schema(description = "候选数据数量")
    private Integer candidateCount;
    @Schema(description = "开始时间")
    private Date startedTime;
    @Schema(description = "完成时间")
    private Date finishedTime;
    @Schema(description = "失败原因")
    private String failureReason;
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
