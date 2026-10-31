package tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** AI候选模型公理创建、修改参数。 */
@Data
@Schema(description = "AI候选模型公理创建、修改参数")
public class OmAiAxiomSaveReqVO {
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
    private Long objectCandidateId;
    private Long otherObjectCandidateId;
    @Size(max = 128)
    private String relationName;
    private Long domainCandidateId;
    private Long rangeCandidateId;
    private Long attributeCandidateId;
    private String typeCategory;
    private String basicTypeCode;
    private Long businessTypeId;
    private String businessTypeSource;
    private String unit;
    private Long decimalPlaces;
    private String format;
    private String enumType;
    private String enumSource;
}
