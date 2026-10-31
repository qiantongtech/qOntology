package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.qiantong.qontology.common.exception.ServiceException;

/**
 * AI 候选数据类型。
 */
@Getter
@AllArgsConstructor
public enum OmAiCandidateTypeEnum {

    TARGET("target", "建模目标"),
    TERM("term", "术语"),
    ENUM_TYPE("enum_type", "枚举类型"),
    ENUM_DATA("enum_data", "枚举数据"),
    OBJECT("object", "对象类型"),
    ATTRIBUTE("attribute", "对象属性"),
    RELATION("relation", "对象关系"),
    AXIOM("axiom", "模型公理"),
    DATA_RULE("data_rule", "数据规则");

    private final String code;
    private final String label;

    /**
     * 根据编码获得候选数据类型。
     *
     * @param code 候选数据类型编码
     * @return 候选数据类型
     */
    public static OmAiCandidateTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ServiceException("不支持的候选类型：" + code));
    }
}
