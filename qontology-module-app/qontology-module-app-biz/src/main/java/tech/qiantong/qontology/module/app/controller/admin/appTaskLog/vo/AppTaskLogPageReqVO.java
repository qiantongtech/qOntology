package tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;
import java.util.Date;

/** 抽取任务执行日志分页 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "抽取任务执行日志分页 Request VO")
public class AppTaskLogPageReqVO extends PageParam {
    private static final long serialVersionUID = 1L;
    private Long workspaceId;
    private Long taskId;
    private Integer taskType;
    private String taskName;
    private Integer status;
    private String errorMsg;
    private Date startTime;
    private Date endTime;
}
