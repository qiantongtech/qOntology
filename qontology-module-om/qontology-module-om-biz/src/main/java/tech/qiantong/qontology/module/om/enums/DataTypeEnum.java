package tech.qiantong.qontology.module.om.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基础数据类型编码与显示名称
 */
@Getter
@AllArgsConstructor
public enum DataTypeEnum {
    TEXT("0", "文本"),
    INTEGER("1", "整数"),
    DECIMAL("2", "小数"),
    BOOLEAN("3", "布尔"),
    DATE("4", "日期"),
    ENUM("5", "枚举");

    /** 基础数据类型编码，序列化为 0～5 的字符串 */
    @EnumValue
    @JsonValue
    final String code;

    /** 基础数据类型的中文名称 */
    final String label;
}
