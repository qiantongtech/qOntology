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

    TARGET("target", "om_ai_target"),
    TERM("term", "om_ai_term"),
    ENUM_TYPE("enum_type", "om_ai_enum_type"),
    ENUM_DATA("enum_data", "om_ai_enum_data"),
    OBJECT("object", "om_ai_object"),
    ATTRIBUTE("attribute", "om_ai_attribute"),
    RELATION("relation", "om_ai_relation");

    private final String code;
    private final String tableName;

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
