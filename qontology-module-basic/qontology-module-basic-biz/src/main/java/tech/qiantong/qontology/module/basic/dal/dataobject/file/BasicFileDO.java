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

package tech.qiantong.qontology.module.basic.dal.dataobject.file;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 模型数据文件 DO 对象 model_data_file
 *
 * @author qModel
 * @date 2026-08-18
 */
@Data
@TableName(value = "basic_file")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("model_data_file_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicFileDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据分类ID
     */
    private Long categoryId;

    /**
     * 数据分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件类型：JSON、JSONL、MODEL、OFFICE、OTHER
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private BigDecimal fileSize;

    /**
     * 文件存储地址
     */
    private String filePath;

    /**
     * 存储类型：LOCAL、MINIO、OSS
     */
    private String storageType;

    /**
     * 文件哈希值
     */
    private String fileHash;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 文件状态：0上传中，1正常，2失败
     */
    private Integer status;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 是否有效
     */
    private Boolean validFlag;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;


}
