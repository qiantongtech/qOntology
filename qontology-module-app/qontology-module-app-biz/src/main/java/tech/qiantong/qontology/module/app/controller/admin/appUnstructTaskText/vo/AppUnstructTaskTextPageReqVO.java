package tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

/** 任务文件段落关联分页 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "任务文件段落关联分页 Request VO")
public class AppUnstructTaskTextPageReqVO extends PageParam {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long workspaceId;
    private Long taskId;
    private Long docId;
    private Integer paragraphIndex;
    private String text;
}
