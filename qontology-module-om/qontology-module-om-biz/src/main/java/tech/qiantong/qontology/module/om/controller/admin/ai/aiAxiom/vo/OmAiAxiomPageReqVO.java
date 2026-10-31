package tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

/** AI候选模型公理分页查询参数。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI候选模型公理分页查询参数")
public class OmAiAxiomPageReqVO extends PageParam {
    private static final long serialVersionUID = 1L;
    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "公理类型")
    private String type;
    @Schema(description = "采纳状态")
    private Integer adoptStatus;
}
