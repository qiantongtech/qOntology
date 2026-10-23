package tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.domain.BaseEntity;

/** 抽取任务执行日志详情创建/修改 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "抽取任务执行日志详情创建/修改 Request VO")
public class AppTaskLogDetailSaveReqVO extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long workspaceId;
    private Long logId;
    private String step;
}
