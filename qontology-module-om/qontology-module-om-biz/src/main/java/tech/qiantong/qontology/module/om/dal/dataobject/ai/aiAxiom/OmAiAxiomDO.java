package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.qiantong.qontology.core.domain.BaseEntity;

/** AI候选模型公理，对应表 {@code om_ai_axiom}。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_axiom")
public class OmAiAxiomDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String name;
    private String description;
    private String type;
    private String level;
    private Long objectCandidateId;
    private Long otherObjectCandidateId;
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
    private Integer adoptStatus;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long adoptedId;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String adoptMessage;
    private BigDecimal confidence;
    @TableLogic
    private Boolean delFlag;
}
