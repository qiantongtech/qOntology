package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 模型公理类型。 */
@Getter
@AllArgsConstructor
public enum OmAiAxiomTypeEnum {
    SUBCLASS("SUBCLASS", "子类约束"),
    EQUIVALENT("EQUIVALENT", "等价类约束"),
    DISJOINT("DISJOINT", "互斥类约束"),
    DOMAIN_RANGE("DOMAIN_RANGE", "关系定义域和值域约束"),
    PROPERTY_RANGE("PROPERTY_RANGE", "属性值域约束");

    private final String code;
    private final String label;

    /** 判断编码是否为受支持的模型公理类型。 */
    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
