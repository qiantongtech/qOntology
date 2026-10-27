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

package tech.qiantong.qontology.module.om.controller.admin.enumeration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

/**
 * 枚举数据 Request VO 对象 om_enum_data
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "枚举数据 Request VO")
@Data
public class OmEnumDataPageReqVO extends PageParam {

    @Schema(description = "工作区 ID，用于限定数据所属工作区")
    private Long workspaceId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    private Long versionId;

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "枚举项显示顺序", example = "")
    private Long enumSort;

    @Schema(description = "枚举项的显示标签", example = "")
    private String enumLabel;

    @Schema(description = "枚举项保存的业务键值", example = "")
    private String enumValue;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID", example = "")
    private String enumType;

    @Schema(description = "状态", example = "")
    private String status;
}
