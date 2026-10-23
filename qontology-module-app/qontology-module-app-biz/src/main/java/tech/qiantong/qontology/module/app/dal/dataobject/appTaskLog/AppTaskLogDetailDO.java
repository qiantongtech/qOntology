/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 抽取任务执行日志详情 DO 对象 app_task_log_detail。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Data
@TableName("app_task_log_detail")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppTaskLogDetailDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /** ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工作区 ID。 */
    private Long workspaceId;
    /** 执行日志 ID。 */
    private Long logId;
    /** 任务执行步骤。 */
    private String step;
    /** 是否有效。 */
    private Boolean validFlag;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
