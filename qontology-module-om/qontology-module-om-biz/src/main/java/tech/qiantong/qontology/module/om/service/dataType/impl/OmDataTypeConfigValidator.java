package tech.qiantong.qontology.module.om.service.dataType.impl;

import java.math.BigDecimal;
import java.util.Set;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeSaveReqVO;
import tech.qiantong.qontology.module.om.enums.DataTypeEnum;

/** 按当前基础类型校验配置，并清空切换类型后不再适用的字段。 */
final class OmDataTypeConfigValidator {
    private OmDataTypeConfigValidator() {}

    static void normalize(OmDataTypeSaveReqVO r) {
        if (r.getStatus() == null) r.setStatus(1);
        require(r.getStatus() == 0 || r.getStatus() == 1, "状态只能为启用或禁用");
        DataTypeEnum type = r.getDataType();
        require(type != null, "请选择业务数据类型");
        if (type == DataTypeEnum.TEXT) {
            require(r.getMinLength() == null || r.getMinLength() >= 0, "最小长度不能小于0");
            require(r.getMaxLength() == null || r.getMaxLength() >= 0, "最大长度不能小于0");
            require(r.getMinLength() == null || r.getMaxLength() == null || r.getMinLength() <= r.getMaxLength(), "最小长度不能大于最大长度");
            if (r.getValidationType() == null) r.setValidationType("NONE");
            require(Set.of("NONE", "PHONE", "ID_CARD", "EMAIL", "URL", "CUSTOM").contains(r.getValidationType()), "格式校验类型无效");
            if ("CUSTOM".equals(r.getValidationType())) {
                require(r.getValidationPattern() != null && !r.getValidationPattern().isBlank(), "请输入正则表达式");
            } else r.setValidationPattern(null);
        } else {
            r.setMinLength(null); r.setMaxLength(null); r.setValidationType(null);
            r.setValidationPattern(null); r.setExampleValue(null);
        }
        boolean numeric = type == DataTypeEnum.INTEGER || type == DataTypeEnum.DECIMAL;
        if (numeric) {
            require(r.getUnit() != null && !r.getUnit().isBlank(), "单位不能为空");
            r.setUnit(r.getUnit().trim());
            require(r.getMinValue() == null || r.getMaxValue() == null || r.getMinValue().compareTo(r.getMaxValue()) <= 0, "最小值不能大于最大值");
            if (type == DataTypeEnum.INTEGER) {
                require(integer(r.getMinValue()) && integer(r.getMaxValue()), "整数类型的数值边界必须为整数");
            }
        } else { r.setUnit(null); r.setMinValue(null); r.setMaxValue(null); }
        if (type == DataTypeEnum.DECIMAL) {
            if (r.getAllPlaces() == null) r.setAllPlaces(10);
            if (r.getDecimalPlaces() == null) r.setDecimalPlaces(2);
            require(r.getAllPlaces() > 0, "总精度必须为正整数");
            require(r.getDecimalPlaces() >= 0 && r.getDecimalPlaces() <= r.getAllPlaces(), "小数位数必须介于0与总精度之间");
        } else { r.setAllPlaces(null); r.setDecimalPlaces(null); }
        if (type == DataTypeEnum.BOOLEAN) {
            if (r.getTrueValue() == null) r.setTrueValue("是");
            if (r.getFalseValue() == null) r.setFalseValue("否");
            r.setTrueValue(r.getTrueValue().trim()); r.setFalseValue(r.getFalseValue().trim());
            require(!r.getTrueValue().isEmpty() && !r.getFalseValue().isEmpty(), "布尔文案不能为空");
            require(!r.getTrueValue().equals(r.getFalseValue()), "两种布尔状态的文案不能相同");
        } else { r.setTrueValue(null); r.setFalseValue(null); }
        if (type == DataTypeEnum.DATE) {
            require(r.getFormat() != null && Set.of("YYYY-MM-DD", "YYYY/MM/DD", "YYYY年MM月DD日", "YYYY-MM-DD HH:mm:ss").contains(r.getFormat()), "请选择有效的展示格式");
            require(r.getEarliestTime() == null || r.getLatestTime() == null || !r.getEarliestTime().isAfter(r.getLatestTime()), "最早日期不能晚于最晚日期");
        } else { r.setFormat(null); r.setEarliestTime(null); r.setLatestTime(null); }
    }

    private static boolean integer(BigDecimal value) {
        return value == null || value.stripTrailingZeros().scale() <= 0;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new ServiceException(message, 601);
    }
}
