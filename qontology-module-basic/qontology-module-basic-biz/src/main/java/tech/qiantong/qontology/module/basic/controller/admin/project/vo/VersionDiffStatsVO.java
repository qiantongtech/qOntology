package tech.qiantong.qontology.module.basic.controller.admin.project.vo;

import lombok.Data;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;

/**
 * 版本差异统计
 */
@Data
public class VersionDiffStatsVO {
    /**
     * 源版本
     */
    private BasicProjectVersionDO projectVersion;

    /**
     * 建模目标数量
     */
    private Integer omTargetCount;

    /**
     * 建模目标覆盖率
     */
    private Float omTargetCoveredRate;

    /**
     * 术语数量
     */
    private Integer termCount;

    /**
     * 枚举数量
     */
    private Integer enumerationCount;

    /**
     * 对象类型数量
     */
    private Integer objectTypeCount;

    /**
     * 对象类型属性数量
     */
    private Integer objectTypeAttributeCount;

    /**
     * 关系数量
     */
    private Integer relationCount;

    /**
     * 模型公理数量
     */
    private Integer modelAxiomCount;

    /**
     * 数据规则数量
     */
    private Integer dataRuleCount;
}
