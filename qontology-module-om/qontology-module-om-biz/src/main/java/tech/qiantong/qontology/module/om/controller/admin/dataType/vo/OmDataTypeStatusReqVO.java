package tech.qiantong.qontology.module.om.controller.admin.dataType.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** 业务数据类型状态修改请求，0 禁用，1 启用。 */
@Data
public class OmDataTypeStatusReqVO {
    @NotNull(message = "业务数据类型编号不能为空")
    @Positive(message = "业务数据类型编号必须大于0")
    private Long id;

    @NotNull(message = "项目版本不能为空")
    @Positive(message = "项目版本必须大于0")
    private Long versionId;

    @NotNull(message = "请选择状态")
    @Min(value = 0, message = "状态只能为启用或禁用")
    @Max(value = 1, message = "状态只能为启用或禁用")
    private Integer status;
}
