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

package tech.qiantong.qontology.module.app.controller.admin.graph.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppGraphPageReqVO extends PageParam {
    private Long entityId;//任务id

    private String name; // 名称
    private String schema; // 概念名称

    private Integer graphId; // 图谱id

    private Integer entityType;//任务类型 1结构化 2非结构化 3故障
    private Integer releaseStatus;//发布状态 0未发布 1已发布
}
