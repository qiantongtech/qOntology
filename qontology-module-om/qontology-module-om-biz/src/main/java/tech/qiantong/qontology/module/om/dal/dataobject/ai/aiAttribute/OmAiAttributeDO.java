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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * AI候选对象类型属性 DO 对象，对应表 {@code om_ai_attribute}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_attribute")
public class OmAiAttributeDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID。 */
    private Long taskId;
    /** 对象类型候选ID。 */
    private Long objectCandidateId;
    /** 属性名称。 */
    private String name;
    /** 属性代码。 */
    private String code;
    /** 属性描述。 */
    private String description;
    /** 类型分类。 */
    private Integer typeCategory;
    /** 基础类型编码。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer basicTypeCode;
    /** 业务数据类型ID。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long businessTypeId;
    /** 业务类型来源。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String businessTypeSource;
    /** 单位。 */
    private String unit;
    /** 小数位数。 */
    private Integer decimalPlaces;
    /** 格式。 */
    private String format;
    /** 枚举类型。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumType;
    /** 枚举来源。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String enumSource;
    /** 采纳状态：0待采纳 1已采纳 2采纳失败。 */
    private Integer adoptStatus;
    /** 已采纳正式数据ID。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long adoptedId;
    /** 采纳结果信息。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String adoptMessage;
    /** 抽取置信度。 */
    private BigDecimal confidence;
    /** 删除标志。 */
    @TableLogic
    private Boolean delFlag;
}
