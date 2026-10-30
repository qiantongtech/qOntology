package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 解析阶段状态。
 */
@Getter
@AllArgsConstructor
public enum OmAiStageStatusEnum {

    WAITING(0, "等待"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    private final Integer code;
    private final String label;
}
