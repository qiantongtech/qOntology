package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 候选数据证据角色。
 */
@Getter
@AllArgsConstructor
public enum OmAiEvidenceRoleEnum {

    POSITIVE("POSITIVE", "正向证据"),
    NEGATIVE("NEGATIVE", "反向证据");

    private final String code;
    private final String label;
}
