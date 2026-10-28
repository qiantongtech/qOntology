/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.api.constraint.dto;

import lombok.*;

/**
 * 数据规则 DTO 对象 om_data_rule
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
public class OmDataRuleReqDTO {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 项目版本id */
    private Long versionId;

    /** 数据规则名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 规则类型 */
    private String type;

    /** 级别 */
    private String level;

    /** 失败条件关系 */
    private String conditionRelation;

    /** 有序失败条件数组 */
    private String conditions;

    /** 失败提示 */
    private String failureMessage;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    private Boolean delFlag;


}
