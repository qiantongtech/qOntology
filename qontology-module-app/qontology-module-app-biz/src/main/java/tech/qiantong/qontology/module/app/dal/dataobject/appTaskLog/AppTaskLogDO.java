/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.util.Date;

/**
 * 抽取任务执行日志 DO 对象 app_task_log。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Data
@TableName("app_task_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppTaskLogDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /** ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工作区 ID。 */
    private Long workspaceId;
    /** 任务 ID。 */
    private Long taskId;
    /** 任务类型。 */
    private Integer taskType;
    /** 任务名称。 */
    private String taskName;
    /** 执行状态。 */
    private Integer status;
    /** 错误消息。 */
    private String errorMsg;
    /** 执行开始时间。 */
    private Date startTime;
    /** 执行结束时间。 */
    private Date endTime;
    /** 是否有效。 */
    private Boolean validFlag;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
