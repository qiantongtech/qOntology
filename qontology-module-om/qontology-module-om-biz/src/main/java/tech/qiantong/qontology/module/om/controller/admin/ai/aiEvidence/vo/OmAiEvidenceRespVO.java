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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiEvidence.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** AI解析来源证据 Response VO。 */
@Schema(description = "AI解析来源证据 Response VO")
@Data
public class OmAiEvidenceRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "文件ID")
    private Long fileId;
    @Schema(description = "证据块编号")
    private Integer blockNo;
    @Schema(description = "证据块类型")
    private String blockType;
    @Schema(description = "页码")
    private Integer pageNo;
    @Schema(description = "章节路径")
    private String chapterPath;
    @Schema(description = "段落编号")
    private Integer paragraphNo;
    @Schema(description = "表格编号")
    private Integer tableNo;
    @Schema(description = "行号")
    private Integer rowNo;
    @Schema(description = "列号")
    private Integer columnNo;
    @Schema(description = "图片编号")
    private Integer imageNo;
    @Schema(description = "起始行")
    private Integer lineStart;
    @Schema(description = "结束行")
    private Integer lineEnd;
    @Schema(description = "坐标信息")
    private String bboxJson;
    @Schema(description = "原始证据内容")
    private String content;
    @Schema(description = "内容哈希")
    private String contentHash;

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
