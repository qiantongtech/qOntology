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

package tech.qiantong.qontology.module.app.dal.dataobject.appStructTask;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.Date;

/**
 * 结构化抽取任务 DO 对象 app_struct_task
 *
 * @author qknow
 * @date 2025-02-25
 */
@Data
@TableName(value = "app_struct_task")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("ext_struct_task_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppStructTaskDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 项目编号 */
    private Long projectId;

    /** 项目名称 */
    private String projectName;

    /** 项目版本编号 */
    private Long projectVersionId;

    /** 项目版本名称 */
    private String projectVersionName;

    /** 任务名称 */
    private String name;

    /** 任务状态 */
    private Integer status;

    /** 发布状态 */
    private Integer publishStatus;

    /** 发布时间 */
    private Date publishTime;

    /** 发布人id */
    private Long publisherId;

    /** 发布人 */
    private String publishBy;

    /** 更新类型 */
    private Integer updateType;

    /** 更新频率 */
    private String updateFrequency;

    /** 更新类型 */
    private Integer updateStatus;


    /** 数据源id */
    private Long datasourceId;

    /** 数据源名称 */
    private String datasourceName;

    /** 最后执行时间 */
    private Date lastExecuteTime;
    /** 执行时长 */
    private Integer executeDuration;
    /** 调度状态 */
    private Integer schedulingStatus;

    /**
     * 责任人用户编号；允许显式更新为空，以支持清除责任人。
     */
    private Long responsibleUserId;

    /**
     * 保存项目时从用户资料读取的责任人姓名快照。
     */
    private String responsiblePersonName;

    /** 描述 */
    private String description;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    @TableLogic
    private Boolean delFlag;


}

