package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 辅助建模执行阶段字典。 */
@Getter
@AllArgsConstructor
public enum OmAiModelingStageEnum {
    FILE_PARSE("FILE_PARSE", "文档结构化解析", "读取上传文件，识别文档结构、段落、表格、图片及原文位置。"),
    EVIDENCE_INDEX("EVIDENCE_INDEX", "证据索引构建", "将解析内容切分为可追溯证据块，并补充相邻上下文索引。"),
    ROUND_1_FOUNDATION("ROUND_1_FOUNDATION", "第1轮：目标、术语和枚举", "结合证据上下文识别建模目标、术语与枚举候选数据。"),
    ROUND_2_OBJECT("ROUND_2_OBJECT", "第2轮：对象类型骨架", "依据建模目标和基础候选数据识别对象类型层级与关联。"),
    ROUND_3_MEMBERS("ROUND_3_MEMBERS", "第3轮：属性和关系", "基于对象类型骨架识别属性、对象关系和建模目标关联。"),
    ROUND_4_RULES("ROUND_4_RULES", "第4轮：模型公理和数据规则", "基于对象、属性与关系识别模型公理和数据规则。"),
    FINISH("FINISH", "结果汇总", "校验候选数据及其依赖关联，统计并汇总本次解析结果。");

    private final String code;
    private final String label;
    private final String thinking;

    /** 根据阶段编码读取阶段字典。 */
    public static OmAiModelingStageEnum fromCode(String code) {
        return Arrays.stream(values()).filter(item -> item.code.equals(code)).findFirst().orElse(FINISH);
    }
}
