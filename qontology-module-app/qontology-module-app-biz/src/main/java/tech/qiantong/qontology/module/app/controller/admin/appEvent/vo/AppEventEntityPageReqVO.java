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

package tech.qiantong.qontology.module.app.controller.admin.appEvent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 事件实体关联 Request VO 对象 kg_event_entity
 *
 * @author qknow
 * @date 2025-11-11
 */
@Schema(description = "事件实体关联 Request VO")
@Data
public class AppEventEntityPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;
    @Schema(description = "工作区id", example = "")
    private Long workspaceId;

    @Schema(description = "事件id", example = "")
    private Long eventId;

    @Schema(description = "实体id", example = "")
    private Long entityId;

    @Schema(description = "实体名称", example = "")
    private String entityName;


}
