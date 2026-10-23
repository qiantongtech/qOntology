package tech.qiantong.qontology.module.basic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataTypeEnum {

    TEXT("0", "文本"),
    INTEGER("1", "整数"),
    DECIMAL("2", "小数"),
    BOOLEAN("3", "布尔"),
    DATE("4", "日期"),
    ENUM("5", "枚举"),
    ;

    @EnumValue
    @JsonValue
    final String code;

    final String label;
}
