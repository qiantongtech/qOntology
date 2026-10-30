package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 辅助建模任务状态。
 */
@Getter
@AllArgsConstructor
public enum OmAiTaskStatusEnum {

    WAITING(0, "待执行"),
    RUNNING(1, "解析中"),
    SUCCESS(2, "解析成功"),
    FAILED(3, "解析失败");

    private final Integer code;
    private final String label;
}
