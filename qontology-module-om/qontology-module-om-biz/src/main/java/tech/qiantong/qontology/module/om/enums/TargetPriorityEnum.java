package tech.qiantong.qontology.module.om.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目标优先级
 */
@Getter
@AllArgsConstructor
public enum TargetPriorityEnum {
    HIGH("3", "优先级高"),
    MIDDLE("2", "优先级中"),
    LOW("1", "优先级低");
    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;
}
