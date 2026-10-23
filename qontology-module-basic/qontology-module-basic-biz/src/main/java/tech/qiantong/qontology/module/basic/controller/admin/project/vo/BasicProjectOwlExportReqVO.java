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

package tech.qiantong.qontology.module.basic.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "项目本体导出请求，支持 Turtle 和 RDF/XML")
public class BasicProjectOwlExportReqVO {
    @NotBlank(message = "请输入导出文件名称")
    @Size(max = 128, message = "文件名称不能超过128个字符")
    @Pattern(regexp = "[^\\\\/:*?\"<>|\\p{Cntrl}]+", message = "文件名称不能包含路径分隔符或特殊字符")
    private String fileName;

    @NotNull(message = "请选择导出版本")
    @Positive(message = "导出版本无效")
    private Long versionId;

    @NotBlank(message = "请选择导出格式")
    @Pattern(regexp = "OWL|TURTLE", message = "导出格式只能为OWL或TURTLE")
    private String format;
}
