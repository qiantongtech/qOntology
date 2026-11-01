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
import lombok.Data;
import tech.qiantong.qontology.core.annotation.Excel;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 事件管理 Response VO 对象 kg_event
 *
 * @author qknow
 * @date 2025-11-11
 */
@Schema(description = "事件管理 Response VO")
@Data
public class AppEventRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "ID")
    @Schema(description = "ID")
    private Long id;

    @Excel(name = "工作区id")
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Excel(name = "图谱id")
    @Schema(description = "图谱id", example = "")
    private Long graphId;

    @Excel(name = "模型id")
    @Schema(description = "模型id", example = "")
    private Long modelId;

    @Excel(name = "事件名称")
    @Schema(description = "事件名称", example = "")
    private String name;

    @Excel(name = "事件描述")
    @Schema(description = "事件描述", example = "")
    private String description;

    @Excel(name = "事件类型")
    @Schema(description = "事件类型", example = "")
    private Integer type;

    @Excel(name = "发生时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发生时间", example = "")
    private Date occurTime;

    @Excel(name = "发生地点")
    @Schema(description = "发生地点", example = "")
    private String occurPlace;


    @Excel(name = "是否有效")
    @Schema(description = "是否有效", example = "")
    private Boolean validFlag;

    @Excel(name = "删除标志")
    @Schema(description = "删除标志", example = "")
    private Boolean delFlag;

    @Excel(name = "创建人")
    @Schema(description = "创建人", example = "")
    private String createBy;

    @Excel(name = "创建人id")
    @Schema(description = "创建人id", example = "")
    private Long creatorId;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "")
    private Date createTime;

    @Excel(name = "更新人")
    @Schema(description = "更新人", example = "")
    private String updateBy;

    @Excel(name = "更新人id")
    @Schema(description = "更新人id", example = "")
    private Long updaterId;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "")
    private Date updateTime;

    @Excel(name = "备注")
    @Schema(description = "备注", example = "")
    private String remark;


    @Schema(description = "参与实体集合", example = "")
    private List<AppEventEntityDO> entityList;

}
