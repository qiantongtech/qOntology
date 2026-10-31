package tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** AI候选模型公理响应数据。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OmAiAxiomRespVO extends OmAiAxiomSaveReqVO {
    private Integer adoptStatus;
    private Long adoptedId;
    private String adoptMessage;
    private BigDecimal confidence;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
