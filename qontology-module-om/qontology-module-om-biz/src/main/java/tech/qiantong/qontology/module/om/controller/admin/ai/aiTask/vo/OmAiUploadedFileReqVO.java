package tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 统一文件服务上传成功后返回给 AI 建模业务的文件元数据。 */
@Data
public class OmAiUploadedFileReqVO {
    @Schema(description = "文件名称")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
    @Schema(description = "统一文件服务返回的相对地址")
    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;
    @Schema(description = "文件扩展名")
    private String fileExt;
    @Schema(description = "文件大小（字节）")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;
}
