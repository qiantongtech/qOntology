/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.controller.admin.appEvent.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 事件管理 创建/修改 Request VO kg_event
 *
 * @author qknow
 * @date 2025-11-11
 */
@Schema(description = "事件管理 Response VO")
@Data
public class AppEventSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "图谱id", example = "")
    private Long graphId;

    @Schema(description = "模型id", example = "")
    private Long modelId;

    @Schema(description = "事件名称", example = "")
    @NotBlank(message = "事件名称不能为空")
    private String name;

    @Schema(description = "事件描述", example = "")
    @Size(max = 1024, message = "事件描述长度不能超过1024个字符")
    private String description;

    @Schema(description = "事件类型", example = "")
    @NotNull(message = "事件类型不能为空")
    private Integer type;

    @Schema(description = "发生时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date occurTime;

    @Schema(description = "发生地点", example = "")
    @Size(max = 256, message = "发生地点长度不能超过255个字符")
    private String occurPlace;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;

    @Schema(description = "参与实体", example = "")
    private List<AppEventEntityRespVO> entityList;


}
