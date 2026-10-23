/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.util.Date;

/**
 * 非结构化抽取任务 DO 对象 app_unstruct_task。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Data
@TableName("app_unstruct_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppUnstructTaskDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /** ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工作区 ID。 */
    private Long workspaceId;
    /** 任务名称。 */
    private String name;
    /** TTL 文件名称。 */
    private String ttlFileName;
    /** TTL 文件存储地址。 */
    private String ttlFilePath;
    /** 任务状态。 */
    private Integer status;
    /** 发布状态。 */
    private Integer publishStatus;
    /** 发布时间。 */
    private Date publishTime;
    /** 发布人 ID。 */
    private Long publisherId;
    /** 发布人。 */
    private String publishBy;
    /** 是否有效。 */
    private Boolean validFlag;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
