package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 候选数据采纳状态。
 */
@Getter
@AllArgsConstructor
public enum OmAiAdoptStatusEnum {

    PENDING(0, "待采纳"),
    ADOPTED(1, "已采纳"),
    FAILED(2, "采纳失败");

    private final Integer code;
    private final String label;
}
