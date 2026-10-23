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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.core.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 模型数据文件 Response VO 对象 model_data_file
 *
 * @author qModel
 * @date 2026-08-18
 */
@Schema(description = "模型数据文件 Response VO")
@Data
public class BasicFileRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "ID")
    @Schema(description = "ID")
    private Long id;

    @Excel(name = "数据分类ID")
    @Schema(description = "数据分类ID", example = "")
    private Long categoryId;

    @Excel(name = "数据分类")
    @Schema(description = "数据分类名称", example = "")
    private String categoryName;

    @Excel(name = "文件名称")
    @Schema(description = "文件名称", example = "")
    private String fileName;

    @Excel(name = "文件后缀")
    @Schema(description = "文件后缀", example = "")
    private String fileSuffix;

    @Excel(name = "文件类型：JSON、JSONL、MODEL、OFFICE、OTHER")
    @Schema(description = "文件类型：JSON、JSONL、MODEL、OFFICE、OTHER", example = "")
    private String fileType;

    @Excel(name = "文件大小(字节)")
    @Schema(description = "文件大小(字节)", example = "")
    private BigDecimal fileSize;

    @Excel(name = "文件存储地址")
    @Schema(description = "文件存储地址", example = "")
    private String filePath;

    @Excel(name = "存储类型：LOCAL、MINIO、OSS")
    @Schema(description = "存储类型：LOCAL、MINIO、OSS", example = "")
    private String storageType;

    @Excel(name = "文件哈希值")
    @Schema(description = "文件哈希值", example = "")
    private String fileHash;

    @Excel(name = "文件MIME类型")
    @Schema(description = "文件MIME类型", example = "")
    private String mimeType;

    @Excel(name = "文件状态：0上传中，1正常，2失败")
    @Schema(description = "文件状态：0上传中，1正常，2失败", example = "")
    private Integer status;

    @Excel(name = "文件描述")
    @Schema(description = "文件描述", example = "")
    private String description;

    @Excel(name = "是否有效")
    @Schema(description = "是否有效", example = "")
    private Boolean validFlag;

    @Excel(name = "删除标志")
    @Schema(description = "删除标志", example = "")
    private Boolean delFlag;

    @Excel(name = "创建人")
    @Schema(description = "创建人", example = "")
    private String createBy;

    @Excel(name = "创建人ID")
    @Schema(description = "创建人ID", example = "")
    private Long creatorId;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "")
    private Date createTime;

    @Excel(name = "更新人")
    @Schema(description = "更新人", example = "")
    private String updateBy;

    @Excel(name = "更新人ID")
    @Schema(description = "更新人ID", example = "")
    private Long updatorId;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "")
    private Date updateTime;

    @Excel(name = "备注")
    @Schema(description = "备注", example = "")
    private String remark;

}
