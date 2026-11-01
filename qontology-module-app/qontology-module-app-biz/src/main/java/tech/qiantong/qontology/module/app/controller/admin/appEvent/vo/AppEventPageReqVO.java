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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

import java.util.Date;

/**
 * 事件管理 Request VO 对象 kg_event
 *
 * @author qknow
 * @date 2025-11-11
 */
@Schema(description = "事件管理 Request VO")
@Data
public class AppEventPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "图谱id", example = "")
    private Long graphId;

    @Schema(description = "实体id", example = "")
    private Long entityId;

    @Schema(description = "模型id", example = "")
    private Long modelId;

    @Schema(description = "事件名称", example = "")
    private String name;

    @Schema(description = "事件描述", example = "")
    private String description;

    @Schema(description = "事件类型", example = "")
    private Integer type;

    @Schema(description = "发生时间", example = "")
    private Date occurTime;

    @Schema(description = "发生地点", example = "")
    private String occurPlace;

    @Schema(description = "发生开始时间", example = "")
    private String occurBeginTime;

    @Schema(description = "发生结束时间", example = "")
    private String occurEndTime;


}
