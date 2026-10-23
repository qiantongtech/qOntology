/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.extEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽取任务类型枚举，预留结构化与非结构化两类任务。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Getter
@AllArgsConstructor
public enum AppTaskTypeEnum {
    STRUCTURED(0, "结构化"),
    UNSTRUCTURED(1, "非结构化");

    private final Integer value;
    private final String label;
}
