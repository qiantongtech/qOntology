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

package tech.qiantong.qontology.module.app.dal.dataobject.appEvent;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 事件管理 DO 对象 kg_event
 *
 * @author qknow
 * @date 2025-11-11
 */
@Data
@TableName(value = "app_event")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("kg_event_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppEventDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工作区id
     */
    private Long workspaceId;

    /**
     * 图谱id
     */
//    private Long graphId;

    /**
     * 模型id
     */
    private Long modelId;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 事件类型
     */
    private Integer type;

    /**
     * 发生时间
     */
    private Date occurTime;

    /**
     * 发生地点
     */
    private String occurPlace;


    /**
     * 是否有效
     */
    private Boolean validFlag;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 参与实体集合
     */
    @TableField(exist = false)
    private List<AppEventEntityDO> entityList;

}
