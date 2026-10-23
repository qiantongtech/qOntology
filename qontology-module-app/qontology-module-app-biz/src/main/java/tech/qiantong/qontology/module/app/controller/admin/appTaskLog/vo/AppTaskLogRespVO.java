package tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/** 抽取任务执行日志 Response VO。 */
@Data
@Schema(description = "抽取任务执行日志 Response VO")
public class AppTaskLogRespVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long workspaceId;
    private Long taskId;
    private Integer taskType;
    private String taskName;
    private Integer status;
    private String errorMsg;
    private Date startTime;
    private Date endTime;
    private String createBy;
    private Long creatorId;
    private Date createTime;
}
