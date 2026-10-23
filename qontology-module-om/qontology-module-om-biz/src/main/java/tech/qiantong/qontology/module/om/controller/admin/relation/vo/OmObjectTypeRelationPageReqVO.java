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

package tech.qiantong.qontology.module.om.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

import java.util.List;

@Schema(description = "对象类型关系分页请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class OmObjectTypeRelationPageReqVO extends PageParam {
    public OmObjectTypeRelationPageReqVO() {
        setPageSize(10);
    }

    @Schema(description = "工作区id")
    private Long workspaceId;

    @Schema(description = "版本id，当前阶段可为空")
    private Long versionId;

    @Schema(description = "名称")
    @Size(max = 128, message = "关系名称不能超过128个字符")
    private String name;

    @Schema(description = "是否可逆，0否、1是，空表示全部")
    @Min(value = 0, message = "是否可逆只能为0或1")
    @Max(value = 1, message = "是否可逆只能为0或1")
    private Integer reversibleFlag;

    @Schema(description = "目标ID列表", example = "")
    private List<Long> targetIdList;
}
