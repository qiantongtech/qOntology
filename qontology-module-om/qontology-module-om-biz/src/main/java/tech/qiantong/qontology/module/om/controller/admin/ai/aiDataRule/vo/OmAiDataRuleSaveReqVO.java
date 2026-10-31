package tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** AI 候选数据规则创建、修改参数。 */
@Data
public class OmAiDataRuleSaveReqVO {
    private Long id;
    @Positive
    private Long taskId;
    @NotBlank
    @Size(max = 128)
    private String name;
    @Size(max = 512)
    private String description;
    @NotBlank
    private String type;
    @NotBlank
    private String level;
    @NotBlank
    private String conditionRelation;
    @NotBlank
    private String conditions;
    @NotBlank
    @Size(max = 512)
    private String failureMessage;
}
