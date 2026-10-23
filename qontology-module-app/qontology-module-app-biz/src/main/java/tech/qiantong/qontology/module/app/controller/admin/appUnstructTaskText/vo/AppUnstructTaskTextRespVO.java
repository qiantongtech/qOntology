package tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/** 任务文件段落关联 Response VO。 */
@Data
@Schema(description = "任务文件段落关联 Response VO")
public class AppUnstructTaskTextRespVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long workspaceId;
    private Long taskId;
    private Long docId;
    private Integer paragraphIndex;
    private String text;
    private Boolean validFlag;
    private Boolean delFlag;
    private String createBy;
    private Long creatorId;
    private Date createTime;
    private String updateBy;
    private Long updaterId;
    private Date updateTime;
    private String remark;
}
