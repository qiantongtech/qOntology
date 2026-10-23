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

package tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.annotation.Excel;
import tech.qiantong.qontology.core.page.PageParam;

import java.util.Date;

/**
 * 结构化抽取任务 Request VO 对象 app_struct_task
 *
 * @author qknow
 * @date 2025-02-25
 */
@Schema(description = "结构化抽取任务 Request VO")
@Data
public class AppStructTaskPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "项目编号")
    private Long projectId;

    @Schema(description = "项目版本编号")
    private Long projectVersionId;

    @Schema(description = "所属图谱", example = "")
    private Long graphId;

    @Schema(description = "任务名称", example = "")
    private String name;

    @Schema(description = "任务状态", example = "")
    private Integer status;

    @Schema(description = "发布状态", example = "")
    private Integer publishStatus;

    @Schema(description = "发布时间", example = "")
    private Date publishTime;

    @Schema(description = "发布人id", example = "")
    private Long publisherId;

    @Schema(description = "发布人", example = "")
    private String publishBy;

    @Schema(description = "更新类型", example = "")
    private Integer updateType;

    @Schema(description = "更新频率", example = "")
    private String updateFrequency;

    @Schema(description = "定时更新状态", example = "")
    private Integer updateStatus;

    @Schema(description = "数据源id", example = "")
    private Long datasourceId;

    @Schema(description = "数据源名称", example = "")
    private String datasourceName;




}

