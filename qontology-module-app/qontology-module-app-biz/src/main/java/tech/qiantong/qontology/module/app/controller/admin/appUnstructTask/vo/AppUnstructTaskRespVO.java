package tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelRespVO;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/** 非结构化抽取任务 Response VO。 */
@Data
@Schema(description = "非结构化抽取任务 Response VO")
public class AppUnstructTaskRespVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "工作区ID")
    private Long workspaceId;
    @Schema(description = "任务名称")
    private String name;
    @Schema(description = "TTL文件名称")
    private String ttlFileName;
    @Schema(description = "TTL文件存储地址")
    private String ttlFilePath;
    @Schema(description = "任务状态")
    private Integer status;
    @Schema(description = "发布状态")
    private Integer publishStatus;
    @Schema(description = "发布时间")
    private Date publishTime;
    @Schema(description = "发布人ID")
    private Long publisherId;
    @Schema(description = "发布人")
    private String publishBy;
    @Schema(description = "是否有效")
    private Boolean validFlag;
    @Schema(description = "删除标志")
    private Boolean delFlag;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建人ID")
    private Long creatorId;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新人")
    private String updateBy;
    @Schema(description = "更新人ID")
    private Long updaterId;
    @Schema(description = "更新时间")
    private Date updateTime;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "关联文件列表")
    private List<AppUnstructTaskDocRelRespVO> relPageReqVOS;
}
