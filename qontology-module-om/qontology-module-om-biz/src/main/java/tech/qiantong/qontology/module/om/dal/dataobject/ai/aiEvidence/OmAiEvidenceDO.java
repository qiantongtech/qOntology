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

package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import tech.qiantong.qontology.core.domain.BaseEntity;

/**
 * AI解析来源证据 DO 对象，对应表 {@code om_ai_evidence}。
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_evidence")
public class OmAiEvidenceDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID。 */
    private Long taskId;
    /** 文件ID。 */
    private Long fileId;
    /** 证据块编号。 */
    private Integer blockNo;
    /** 证据块类型。 */
    private String blockType;
    /** 页码。 */
    private Integer pageNo;
    /** 章节路径。 */
    private String chapterPath;
    /** 段落编号。 */
    private Integer paragraphNo;
    /** 表格编号。 */
    private Integer tableNo;
    /** 行号。 */
    private Integer rowNo;
    /** 列号。 */
    private Integer columnNo;
    /** 图片编号。 */
    private Integer imageNo;
    /** 起始行。 */
    private Integer lineStart;
    /** 结束行。 */
    private Integer lineEnd;
    /** 坐标信息。 */
    private String bboxJson;
    /** 原始证据内容。 */
    private String content;
    /** 内容哈希。 */
    private String contentHash;
}
