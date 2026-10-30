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

package tech.qiantong.qontology.module.om.controller.admin.term.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.*;
import tech.qiantong.qontology.core.annotation.Excel;

/**
 * 术语 Response VO 对象 om_term
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "术语 Response VO")
@Data
public class OmTermRespVO implements Serializable {

    @Excel(name = "工作区id")
    @Schema(description = "工作区id")
    private Long workspaceId;

    @Excel(name = "版本id")
    @Schema(description = "版本id")
    private Long versionId;

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    @Excel(name = "ID")
    private Long id;

    @Schema(description = "术语的标准名称", example = "")
    @Excel(name = "标准名称")
    private String stdName;

    @Schema(description = "术语的英文名称", example = "")
    @Excel(name = "英文名称")
    private String enName;

    @Schema(description = "术语别名", example = "")
    @Excel(name = "别名")
    private String alias;

    @Schema(description = "术语缩写", example = "")
    @Excel(name = "缩写")
    private String abbr;

    @Schema(description = "术语来源", example = "")
    @Excel(name = "来源")
    private String source;

    @Schema(description = "描述", example = "")
    @Excel(name = "描述")
    private String description;

    @Schema(description = "是否有效：true 有效，false 无效", example = "")
    @Excel(name = "是否有效")
    private Boolean validFlag;

    @Schema(description = "逻辑删除标志：true 已删除，false 未删除", example = "")
    @Excel(name = "删除标志")
    private Boolean delFlag;

    @Schema(description = "创建人", example = "")
    @Excel(name = "创建人")
    private String createBy;

    @Schema(description = "创建人 ID", example = "")
    @Excel(name = "创建人id")
    private Long creatorId;

    @Schema(description = "创建时间", example = "")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新人", example = "")
    @Excel(name = "更新人")
    private String updateBy;

    @Schema(description = "更新人 ID", example = "")
    @Excel(name = "更新人id")
    private Long updaterId;

    @Schema(description = "更新时间", example = "")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "备注信息", example = "")
    @Excel(name = "备注")
    private String remark;
}
