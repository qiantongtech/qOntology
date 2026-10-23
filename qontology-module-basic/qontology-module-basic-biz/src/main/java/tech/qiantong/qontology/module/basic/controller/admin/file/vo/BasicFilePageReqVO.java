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

package tech.qiantong.qontology.module.basic.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.page.PageParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 模型数据文件 Request VO 对象 model_data_file
 *
 * @author qModel
 * @date 2026-08-18
 */
@Schema(description = "模型数据文件 Request VO")
@Data
public class BasicFilePageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;
    @Schema(description = "ID", example = "")
    private Long id;

    @Schema(description = "数据分类ID", example = "")
    private Long categoryId;

    @Schema(description = "分类ID集合", example = "")
    private List<Long> categoryIds;

    @Schema(description = "文件名称", example = "")
    private String fileName;

    @Schema(description = "文件后缀", example = "")
    private String fileSuffix;

    @Schema(description = "文件类型：JSON、JSONL、MODEL、OFFICE、OTHER", example = "")
    private String fileType;

    @Schema(description = "文件大小MB", example = "")
    private BigDecimal fileSize;

    @Schema(description = "文件存储地址", example = "")
    private String filePath;

    @Schema(description = "存储类型：LOCAL、MINIO、OSS", example = "")
    private String storageType;

    @Schema(description = "文件哈希值", example = "")
    private String fileHash;

    @Schema(description = "文件MIME类型", example = "")
    private String mimeType;

    @Schema(description = "文件状态：0上传中，1正常，2失败", example = "")
    private Integer status;

    @Schema(description = "文件描述", example = "")
    private String description;

}
