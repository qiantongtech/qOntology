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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 对象类型属性分页查询请求
 */
@Schema(description = "对象类型属性分页请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeAttributePageReqVO extends PageParam {

    @Schema(description = "工作区 ID，用于限定数据所属工作区")
    private Long workspaceId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    private Long versionId;

    @Schema(description = "所属对象类型 ID")
    @NotNull(message = "对象类型id不能为空")
    @Positive(message = "对象类型id无效")
    private Long objectTypeId;

    @Schema(description = "名称")
    @Size(max = 128, message = "属性名称长度不能超过128个字符")
    private String name;
}
