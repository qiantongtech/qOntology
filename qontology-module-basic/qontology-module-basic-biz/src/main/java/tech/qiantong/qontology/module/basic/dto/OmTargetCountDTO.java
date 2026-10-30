package tech.qiantong.qontology.module.basic.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmTargetCountDTO {
    private BigDecimal totalCount;
    private BigDecimal coveredCount;
}
