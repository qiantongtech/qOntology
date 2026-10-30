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
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.module.basic.api.dto.dataType.BasicDataTypeOptionDTO;

/**
 * 业务数据类型选项响应对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OmAttributeBusinessTypeOptionRespVO extends BasicDataTypeOptionDTO {

    @Schema(description = "业务类型来源：PROJECT 当前项目版本（om_data_type），GLOBAL 全局（basic_data_type）")
    private String businessTypeSource;
}
