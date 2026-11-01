package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 数据规则范围比较符。 */
@Getter
@AllArgsConstructor
public enum OmAiRangeOperatorEnum {
    LT("LT", "小于"),
    LE("LE", "小于等于"),
    EQ("EQ", "等于"),
    NE("NE", "不等于"),
    GE("GE", "大于等于"),
    GT("GT", "大于");

    private final String code;
    private final String label;

    /** 判断编码是否为受支持的范围比较符。 */
    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
