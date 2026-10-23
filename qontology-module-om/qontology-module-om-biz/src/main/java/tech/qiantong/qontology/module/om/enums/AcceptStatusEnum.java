package tech.qiantong.qontology.module.om.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证状态枚举
 */
@Getter
@AllArgsConstructor
public enum AcceptStatusEnum {
    // 验证状态;0待验证；1已覆盖；2未覆盖
    WAITING("0", "待验证"),
    COVERED("1", "已覆盖"),
    NOT_COVERED("2", "未覆盖");

    @EnumValue
    @JsonValue
    private final String code;
    private final String label;
}
