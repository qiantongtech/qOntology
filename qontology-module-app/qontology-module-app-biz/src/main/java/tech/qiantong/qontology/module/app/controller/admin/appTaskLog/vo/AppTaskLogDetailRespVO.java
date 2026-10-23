package tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/** 抽取任务执行日志详情 Response VO。 */
@Data
@Schema(description = "抽取任务执行日志详情 Response VO")
public class AppTaskLogDetailRespVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long workspaceId;
    private Long logId;
    private String step;
    private String createBy;
    private Long creatorId;
    private Date createTime;
}
