package tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;

/** 任务文件段落关联创建/修改 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "任务文件段落关联创建/修改 Request VO")
public class AppUnstructTaskTextSaveReqVO extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "工作区ID")
    private Long workspaceId;
    @Schema(description = "任务ID")
    @NotNull(message = "任务 ID 不能为空")
    private Long taskId;
    @Schema(description = "文件ID")
    @NotNull(message = "文件 ID 不能为空")
    private Long docId;
    @Schema(description = "段落标识")
    @NotNull(message = "段落标识不能为空")
    private Integer paragraphIndex;
    @Schema(description = "文字内容")
    private String text;
    @Schema(description = "是否有效")
    private Boolean validFlag;
}
