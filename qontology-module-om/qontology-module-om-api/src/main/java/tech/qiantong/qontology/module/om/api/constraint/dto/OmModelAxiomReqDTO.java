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
 * 模型公理 DTO 对象 om_model_axiom
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
public class OmModelAxiomReqDTO {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 项目版本id */
    private Long versionId;

    /** 模型公理名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 公理类型 */
    private String type;

    /** 级别 */
    private String level;

    /** 对象类型id */
    private Long objectTypeId;

    /** 另一对象类型id */
    private Long otherTypeId;

    /** 对象类型关系id */
    private Long relationId;

    /** Domain对象类型id */
    private Long domainId;

    /** Range对象类型id */
    private Long rangeId;

    /** 数据属性id */
    private Long attributeId;

    /** 属性值类型分类 */
    private String typeCategory;

    /** 基础类型编码 */
    private String basicTypeCode;

    /** 业务类型id */
    private Long businessTypeId;

    /** 业务类型来源 */
    private String businessTypeSource;

    /** 单位 */
    private String unit;

    /** 小数位数 */
    private Long decimalPlaces;

    /** 日期格式 */
    private String format;

    /** 枚举类型编码 */
    private String enumType;

    /** 枚举来源 */
    private String enumSource;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    private Boolean delFlag;
}
