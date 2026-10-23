package tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;
import java.util.Date;

/** 抽取任务执行日志创建 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "抽取任务执行日志创建 Request VO")
public class AppTaskLogSaveReqVO extends BaseEntity {
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
}
