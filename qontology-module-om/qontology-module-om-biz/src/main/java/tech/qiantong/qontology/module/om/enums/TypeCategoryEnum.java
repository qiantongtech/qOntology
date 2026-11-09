package tech.qiantong.qontology.module.om.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象类型属性类型分类：区分基础数据类型与业务数据类型
 */
@Getter
@AllArgsConstructor
public enum TypeCategoryEnum {
    BASIC(1, "基础数据类型"),
    BUSINESS(2, "业务数据类型");

    /** 类型分类编码，序列化为 1、2 的整数 */
    @EnumValue
    @JsonValue
    final Integer code;

    /** 类型分类的中文名称 */
    final String label;
}
