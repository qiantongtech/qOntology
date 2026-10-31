package tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.qiantong.qontology.core.page.PageParam;

/** AI 候选数据规则分页参数。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OmAiDataRulePageReqVO extends PageParam {
    private static final long serialVersionUID = 1L;
    private Long taskId;
    private String name;
    private String type;
    private Integer adoptStatus;
}
