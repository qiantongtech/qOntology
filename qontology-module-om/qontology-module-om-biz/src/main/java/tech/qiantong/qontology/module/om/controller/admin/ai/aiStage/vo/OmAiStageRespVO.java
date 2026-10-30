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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** AI解析阶段记录 Response VO。 */
@Schema(description = "AI解析阶段记录 Response VO")
@Data
public class OmAiStageRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "阶段编码")
    private String stageCode;
    @Schema(description = "阶段名称")
    private String stageName;
    @Schema(description = "阶段状态")
    private Integer status;
    @Schema(description = "阶段顺序")
    private Integer sequenceNo;
    @Schema(description = "阶段进度")
    private Integer progress;
    @Schema(description = "模型密钥ID")
    private Long modelKeyId;
    @Schema(description = "模型名称")
    private String modelName;
    @Schema(description = "提示词资源")
    private String promptResource;
    @Schema(description = "输入数量")
    private Integer inputCount;
    @Schema(description = "输出数量")
    private Integer outputCount;
    @Schema(description = "Token数量")
    private Integer tokenCount;
    @Schema(description = "耗时毫秒")
    private Long durationMs;
    @Schema(description = "阶段摘要")
    private String summary;
    @Schema(description = "模型流式思考过程")
    private String reasoningContent;
    @Schema(description = "错误信息")
    private String errorMessage;
    @Schema(description = "开始时间")
    private Date startedTime;
    @Schema(description = "完成时间")
    private Date finishedTime;

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
