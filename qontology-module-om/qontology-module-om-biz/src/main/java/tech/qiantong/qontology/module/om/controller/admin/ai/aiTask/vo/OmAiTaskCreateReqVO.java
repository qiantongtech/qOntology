/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 */
package tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** AI辅助建模任务创建 Request VO。 */
@Schema(description = "AI辅助建模任务创建 Request VO")
@Data
public class OmAiTaskCreateReqVO {

    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 128, message = "任务名称长度不能超过128个字符")
    private String taskName;

    @Schema(description = "任务描述")
    @Size(max = 512, message = "任务描述长度不能超过512个字符")
    private String description;

    @Schema(description = "项目版本ID")
    @NotNull(message = "项目版本ID不能为空")
    @Positive(message = "项目版本ID必须为正整数")
    private Long versionId;

    @Schema(description = "已通过统一文件服务上传的文件列表")
    @Valid
    @NotEmpty(message = "请至少上传一个文件")
    private List<OmAiUploadedFileReqVO> files;

}
