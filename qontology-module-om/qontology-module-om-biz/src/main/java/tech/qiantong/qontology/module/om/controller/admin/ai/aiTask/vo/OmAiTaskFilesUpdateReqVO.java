package tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** AI 建模任务文件列表更新请求；仅保存文件，不启动解析。 */
@Data
public class OmAiTaskFilesUpdateReqVO {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    private List<Long> retainedFileIds = new ArrayList<>();
    @Valid
    private List<OmAiUploadedFileReqVO> files = new ArrayList<>();
}
