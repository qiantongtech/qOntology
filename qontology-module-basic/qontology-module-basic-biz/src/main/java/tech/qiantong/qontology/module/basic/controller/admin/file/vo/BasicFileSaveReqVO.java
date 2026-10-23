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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 模型数据文件 创建/修改 Request VO model_data_file
 *
 * @author qModel
 * @date 2026-08-18
 */
@Schema(description = "模型数据文件 Response VO")
@Data
public class BasicFileSaveReqVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "数据分类ID", example = "")
    private Long categoryId;

    @Schema(description = "文件名称", example = "")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "文件后缀", example = "")
    @Size(max = 32, message = "文件后缀长度不能超过32个字符")
    private String fileSuffix;

    @Schema(description = "文件类型：JSON、JSONL、MODEL、OFFICE、OTHER", example = "")
    private String fileType;

    @Schema(description = "文件大小(字节)", example = "")
    private BigDecimal fileSize;

    @Schema(description = "文件存储地址", example = "")
    private String filePath;

    @Schema(description = "存储类型：LOCAL、MINIO、OSS", example = "")
    private String storageType;

    @Schema(description = "文件哈希值", example = "")
    @Size(max = 128, message = "文件哈希值长度不能超过128个字符")
    private String fileHash;

    @Schema(description = "文件MIME类型", example = "")
    @Size(max = 128, message = "文件MIME类型长度不能超过128个字符")
    private String mimeType;

    @Schema(description = "文件状态：0上传中，1正常，2失败", example = "")
    private Integer status;

    @Schema(description = "文件描述", example = "")
    @Size(max = 1024, message = "文件描述长度不能超过1024个字符")
    private String description;

    @Schema(description = "备注", example = "")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;


}
