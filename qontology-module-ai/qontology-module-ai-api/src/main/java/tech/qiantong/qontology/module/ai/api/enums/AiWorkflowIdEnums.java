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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.ai.api.enums;

import lombok.Getter;
/**
 * AI工作流节点 ID 枚举
 *
 * @author asd
 */
@Getter
public enum AiWorkflowIdEnums {

    // qOntology - 知识问答(图谱)
    KG_QA(1L),

    // qOntology - 知识问答(知识库+知识图谱)
    HYBRID_QA(2L),

    // qOntology - 意图检索
    INTENT_RETRIEVAL(3L),

    // qOntology - 图谱语义检索
    KG_SEMANTIC_SEARCH(4L),

    // qOntology - 知识检索
    KNOWLEDGE_RETRIEVAL(5L),

    // qOntology - 图谱模型提取
    KG_MODEL_EXTRACTION(6L),

    // qOntology - 三元组抽取
    TRIPLE_EXTRACTION(7L),

    // qOntology - 合规性检查
    COMPLIANCE_CHECK(8L),

    // qOntology - 问答建议
    QUESTION_SUGGESTION(9L),

    // qOntology - 智能写作助手-大纲生成
    OUTLINE_SYSTEM_PROMPT(10L),

    // qOntology - 智能写作助手-结构化与内容提炼
    OUTLINE_STRUCTURING(11L),

    // qOntology - 智能写作助手-智能写作
    WRITING_SYSTEM_PROMPT(12L),

    // qOntology - 智能写作助手-续写
    CONTINUATION_SYSTEM_PROMPT(13L),

    // qOntology - 智能写作助手-扩写
    EXTEND_SYSTEM_PROMPT(14L),

    // qOntology - 智能写作助手-润色
    POLISH_SYSTEM_PROMPT(15L),

    // qOntology - 智能写作助手-缩写
    ACRONYM_SYSTEM_PROMPT(16L),

    // qOntology - 智能写作助手-模板生成
    TEMPLATE_SYSTEM_PROMPT(17L),

    // qOntology - 智能写作文章摘要生成专家
    SUMMARY_SYSTEM_PROMPT(18L),

    // qOntology -智能写作-根据摘要生成大纲
    OUTLINE_SYSTEM_PROMPT2(19L),

    // qOntology -智能写作-标题优化
    TITLE_SYSTEM_PROMPT(20L),

    // qOntology -智能写作-大纲优化
    OUTLINE_OPTIMIZE_SYSTEM_PROMPT(21L),

    // qOntology -智能写作-文章优化
    WRITING_OPTIMIZE_SYSTEM_PROMPT(22L),

    // qOntology -智能写作-日报生成
    DAILY_SYSTEM_PROMPT(23L),

    // qOntology -智能写作-周报生成
    WEEKLY_SYSTEM_PROMPT(24L),

    // qOntology -智能写作-月报生成
    MONTHLY_SYSTEM_PROMPT(25L);

    private final Long id;

    AiWorkflowIdEnums(Long id) {
        this.id = id;
    }
}
