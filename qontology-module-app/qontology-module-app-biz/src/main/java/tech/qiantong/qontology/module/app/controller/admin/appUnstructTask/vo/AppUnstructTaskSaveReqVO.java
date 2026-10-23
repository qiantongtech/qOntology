package tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;

/** 非结构化抽取任务创建/修改 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "非结构化抽取任务创建/修改 Request VO")
public class AppUnstructTaskSaveReqVO extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Schema(description = "ID")
    private Long id;
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 256, message = "任务名称长度不能超过256个字符")
    @Schema(description = "任务名称")
    private String name;
    @NotBlank(message = "TTL文件名称不能为空")
    @Pattern(regexp = "(?i)^.*\\.ttl$", message = "请上传TTL格式文件")
    @Schema(description = "TTL文件名称")
    private String ttlFileName;
    @NotBlank(message = "TTL文件地址不能为空")
    @Schema(description = "TTL文件存储地址")
    private String ttlFilePath;
    @Schema(description = "工作区ID")
    private Long workspaceId;
    @Schema(description = "任务状态")
    private Integer status;
    @Schema(description = "发布状态")
    private Integer publishStatus;
}
