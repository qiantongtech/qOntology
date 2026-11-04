package tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;
import java.util.Date;

/** 非结构化抽取任务分页 Request VO。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "非结构化抽取任务分页 Request VO")
public class AppUnstructTaskPageReqVO extends PageParam {
    private static final long serialVersionUID = 1L;
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "工作区ID")
    private Long workspaceId;
    @Schema(description = "任务名称")
    private String name;
    @Schema(description = "TTL文件名称")
    private String ttlFileName;
    @Schema(description = "任务状态")
    private Integer status;
    @Schema(description = "最后执行时间")
    private Date lastExecuteTime;
    @Schema(description = "执行时长")
    private Integer executeDuration;
    /**
     * 责任人用户编号；允许显式更新为空，以支持清除责任人。
     */
    private Long responsibleUserId;

    /**
     * 保存项目时从用户资料读取的责任人姓名快照。
     */
    private String responsiblePersonName;
    @Schema(description = "发布状态")
    private Integer publishStatus;
    @Schema(description = "发布时间")
    private Date publishTime;
}
