package tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule;

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

/** AI候选数据规则，对应表 {@code om_ai_data_rule}。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("om_ai_data_rule")
public class OmAiDataRuleDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String name;
    private String description;
    private String type;
    private String level;
    private String conditionRelation;
    private String conditions;
    private String failureMessage;
    private Integer adoptStatus;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long adoptedId;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String adoptMessage;
    private BigDecimal confidence;
    @TableLogic
    private Boolean delFlag;
}
