package tech.qiantong.qontology.module.basic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectStatusEnum {
    ENABLE(1, "启用"),
    DISABLE(0, "禁用");

    @EnumValue
    @JsonValue
    final Integer code;


    final String label;
}
