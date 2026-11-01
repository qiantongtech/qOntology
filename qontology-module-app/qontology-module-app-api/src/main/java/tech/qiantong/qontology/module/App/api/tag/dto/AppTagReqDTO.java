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

package tech.qiantong.qontology.module.App.api.tag.dto;

import lombok.*;

/**
 * 知识标签 DTO 对象 App_tag
 *
 * @author qknow
 * @date 2025-11-13
 */
@Data
public class AppTagReqDTO {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 图谱id */
    private Long graphId;

    /** 模型id */
    private Long modelId;

    /** 名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 类型 */
    private String category;

    /** 状态 */
    private String status;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    private Boolean delFlag;


}
