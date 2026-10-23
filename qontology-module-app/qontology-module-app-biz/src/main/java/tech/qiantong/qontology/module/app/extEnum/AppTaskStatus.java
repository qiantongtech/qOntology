/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.extEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽取任务状态枚举。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Getter
@AllArgsConstructor
public enum AppTaskStatus {
    UNEXECUTED(0, "未执行"),
    INPROGRESS(1, "执行中"),
    EXECUTED(2, "已执行"),
    ERROR(3, "执行失败"),
    QUEUE(4, "队列中");

    private final Integer value;
    private final String label;
}
