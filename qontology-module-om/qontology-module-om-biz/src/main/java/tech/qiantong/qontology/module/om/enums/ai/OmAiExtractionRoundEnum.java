package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 辅助建模抽取轮次字典。 */
@Getter
@AllArgsConstructor
public enum OmAiExtractionRoundEnum {
    FOUNDATION("FOUNDATION", "基础要素抽取", OmAiModelingStageEnum.ROUND_1_FOUNDATION, "round-1-foundation.md"),
    OBJECT("OBJECT", "对象类型抽取", OmAiModelingStageEnum.ROUND_2_OBJECT, "round-2-object.md"),
    MEMBERS("MEMBERS", "属性关系抽取", OmAiModelingStageEnum.ROUND_3_MEMBERS, "round-3-members.md"),
    RULES("RULES", "公理规则抽取", OmAiModelingStageEnum.ROUND_4_RULES, "round-4-rules.md");

    private final String code;
    private final String label;
    private final OmAiModelingStageEnum stage;
    private final String promptResource;
}
