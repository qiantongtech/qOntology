package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 数据规则类型。 */
@Getter
@AllArgsConstructor
public enum OmAiDataRuleTypeEnum {
    REQUIRED("REQUIRED", "必填校验"),
    ENUM("ENUM", "枚举值校验"),
    RANGE("RANGE", "取值范围校验"),
    FORMAT("FORMAT", "格式校验");

    private final String code;
    private final String label;

    /** 判断编码是否为受支持的数据规则类型。 */
    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
