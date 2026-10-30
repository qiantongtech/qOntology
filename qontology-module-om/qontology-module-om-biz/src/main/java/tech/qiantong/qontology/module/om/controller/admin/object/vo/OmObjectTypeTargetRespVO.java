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

package tech.qiantong.qontology.module.om.controller.admin.object.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.enums.TargetPriorityEnum;

/**
 * 对象类型关联建模目标响应对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对象类型关联建模目标响应")
public class OmObjectTypeTargetRespVO extends BaseEntity {

    @Schema(description = "建模目标ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "目标问题")
    private String question;

    @Schema(description = "问题来源")
    private String questionSource;

    @Schema(description = "业务场景")
    private String businessScenarios;

    @Schema(description = "优先级")
    private TargetPriorityEnum priority;

    @Schema(description = "验证状态")
    private AcceptStatusEnum acceptStatus;

    @Schema(description = "建模目标关联的模型元素数量")
    private Long relatedElementCount;

    @Schema(description = "是否有效：true 有效，false 无效")
    private Boolean validFlag;
}
