/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * 任务文件段落关联 DO 对象 app_unstruct_task_text。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Data
@TableName("app_unstruct_task_text")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppUnstructTaskTextDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /** ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工作区 ID。 */
    private Long workspaceId;
    /** 任务 ID。 */
    private Long taskId;
    /** 文件 ID。 */
    private Long docId;
    /** 段落标识。 */
    private Integer paragraphIndex;
    /** 文字内容。 */
    private String text;
    /** 是否有效。 */
    private Boolean validFlag;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
