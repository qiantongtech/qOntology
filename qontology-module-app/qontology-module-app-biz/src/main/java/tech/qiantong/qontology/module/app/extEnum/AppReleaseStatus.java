/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.extEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽取任务发布状态枚举。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Getter
@AllArgsConstructor
public enum AppReleaseStatus {
    UNPUBLISHED(0, "未发布"),
    PUBLISHED(1, "已发布");

    private final Integer status;
    private final String label;
}
