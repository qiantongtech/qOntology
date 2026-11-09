package tech.qiantong.qontology.module.om.service.ai;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumData.OmAiEnumDataDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumType.OmAiEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObject.OmAiObjectDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObjectTarget.OmAiObjectTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelation.OmAiRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelationTarget.OmAiRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget.OmAiTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTerm.OmAiTermDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAttribute.OmAiAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAxiom.OmAiAxiomMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataRule.OmAiDataRuleMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumData.OmAiEnumDataMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumType.OmAiEnumTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObject.OmAiObjectMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObjectTarget.OmAiObjectTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiRelation.OmAiRelationMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiRelationTarget.OmAiRelationTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTarget.OmAiTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTerm.OmAiTermMapper;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiCandidateTypeEnum;

/**
 * AI 候选删除集中式校验与执行组件。
 *
 * <p>统一维护各类候选（对象、属性、关系、建模目标、枚举类型、枚举数据、术语、公理、数据规则）的
 * 跨候选引用约束，对齐外部对应功能的删除逻辑：命中“已采纳”或“被其它候选引用”即视为不可删除，
 * 返回逐条不可删原因；执行删除时只删除可删部分，并对对象、关系级联清理建模目标绑定。</p>
 *
 * @author qOntology
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class OmAiCandidateDeleteGuard {

    /** 枚举来源：当前项目（候选枚举），与属性候选的 enum_source 取值一致。 */
    private static final String ENUM_SOURCE_PROJECT = "PROJECT";

    @Resource
    private OmAiObjectMapper objectMapper;
    @Resource
    private OmAiAttributeMapper attributeMapper;
    @Resource
    private OmAiRelationMapper relationMapper;
    @Resource
    private OmAiTargetMapper targetMapper;
    @Resource
    private OmAiEnumTypeMapper enumTypeMapper;
    @Resource
    private OmAiEnumDataMapper enumDataMapper;
    @Resource
    private OmAiTermMapper termMapper;
    @Resource
    private OmAiAxiomMapper axiomMapper;
    @Resource
    private OmAiDataRuleMapper dataRuleMapper;
    @Resource
    private OmAiObjectTargetMapper objectTargetMapper;
    @Resource
    private OmAiRelationTargetMapper relationTargetMapper;

    /**
     * 逐条校验候选是否可删除，仅返回不可删除项及其原因。
     *
     * @param type 候选类型编码
     * @param ids  候选编号集合
     * @return 不可删除映射，键为候选编号、值为不可删原因；可删除项不出现在结果中
     */
    public Map<Long, String> checkDeletable(String type, Collection<Long> ids) {
        Map<Long, String> blocked = new LinkedHashMap<>();
        if (ids == null || ids.isEmpty()) {
            return blocked;
        }
        OmAiCandidateTypeEnum candidateType = OmAiCandidateTypeEnum.fromCode(type);
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            String reason = blockReason(candidateType, id);
            if (reason != null) {
                blocked.put(id, reason);
            }
        }
        return blocked;
    }

    /**
     * 过滤出可删除的候选编号。
     *
     * @param type 候选类型编码
     * @param ids  候选编号集合
     * @return 可删除的候选编号列表
     */
    public List<Long> filterDeletable(String type, Collection<Long> ids) {
        List<Long> deletable = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return deletable;
        }
        Map<Long, String> blocked = checkDeletable(type, ids);
        for (Long id : ids) {
            if (id != null && !blocked.containsKey(id)) {
                deletable.add(id);
            }
        }
        return deletable;
    }

    /**
     * 只删除可删部分：先过滤出可删除候选，对象/关系额外级联清理建模目标绑定，再执行删除。
     *
     * @param type 候选类型编码
     * @param ids  候选编号集合
     * @return 实际删除的候选数量
     */
    public int deleteCandidates(String type, Collection<Long> ids) {
        List<Long> deletable = filterDeletable(type, ids);
        if (deletable.isEmpty()) {
            return 0;
        }
        OmAiCandidateTypeEnum candidateType = OmAiCandidateTypeEnum.fromCode(type);
        switch (candidateType) {
            case OBJECT:
                // 对齐外部对象类型：删除时级联解绑该候选关联的建模目标。
                objectTargetMapper.delete(new QueryWrapper<OmAiObjectTargetDO>().in("object_candidate_id", deletable));
                return objectMapper.deleteBatchIds(deletable);
            case RELATION:
                // 对齐外部对象类型关系：删除时级联解绑该候选关联的建模目标。
                relationTargetMapper.delete(new QueryWrapper<OmAiRelationTargetDO>().in("relation_candidate_id", deletable));
                return relationMapper.deleteBatchIds(deletable);
            case ATTRIBUTE:
                return attributeMapper.deleteBatchIds(deletable);
            case TARGET:
                return targetMapper.deleteBatchIds(deletable);
            case ENUM_TYPE:
                return enumTypeMapper.deleteBatchIds(deletable);
            case ENUM_DATA:
                return enumDataMapper.deleteBatchIds(deletable);
            case TERM:
                return termMapper.deleteBatchIds(deletable);
            case AXIOM:
                return axiomMapper.deleteBatchIds(deletable);
            case DATA_RULE:
                return dataRuleMapper.deleteBatchIds(deletable);
            default:
                return 0;
        }
    }

    /** 计算单条候选不可删除的原因，可删除时返回 null；记录不存在时按可删除处理（返回 null）。 */
    private String blockReason(OmAiCandidateTypeEnum type, Long id) {
        switch (type) {
            case OBJECT:
                return objectBlockReason(id);
            case ATTRIBUTE:
                return attributeBlockReason(id);
            case RELATION:
                OmAiRelationDO relation = relationMapper.selectById(id);
                return adopted(relation == null ? null : relation.getAdoptStatus(), null);
            case TARGET:
                return targetBlockReason(id);
            case ENUM_TYPE:
                return enumTypeBlockReason(id);
            case ENUM_DATA:
                OmAiEnumDataDO enumData = enumDataMapper.selectById(id);
                return adopted(enumData == null ? null : enumData.getAdoptStatus(), null);
            case TERM:
                OmAiTermDO term = termMapper.selectById(id);
                return adopted(term == null ? null : term.getAdoptStatus(), null);
            case AXIOM:
                OmAiAxiomDO axiom = axiomMapper.selectById(id);
                return adopted(axiom == null ? null : axiom.getAdoptStatus(), null);
            case DATA_RULE:
                OmAiDataRuleDO dataRule = dataRuleMapper.selectById(id);
                return adopted(dataRule == null ? null : dataRule.getAdoptStatus(), null);
            default:
                return null;
        }
    }

    /** 对象类型候选：已采纳，或存在下级/属性候选，或被关系、公理候选引用时不可删除。 */
    private String objectBlockReason(Long id) {
        OmAiObjectDO record = objectMapper.selectById(id);
        if (record == null) {
            return null;
        }
        String adopted = adopted(record.getAdoptStatus(), null);
        if (adopted != null) {
            return adopted;
        }
        String name = record.getName();
        if (objectMapper.selectCount(new QueryWrapper<OmAiObjectDO>().eq("parent_candidate_id", id)) > 0) {
            return "对象类型候选“" + name + "”存在下级候选，请先删除下级";
        }
        if (attributeMapper.selectCount(new QueryWrapper<OmAiAttributeDO>().eq("object_candidate_id", id)) > 0) {
            return "对象类型候选“" + name + "”存在属性候选，请先删除属性";
        }
        if (relationMapper.selectCount(new QueryWrapper<OmAiRelationDO>()
                .and(query -> query.eq("start_object_candidate_id", id).or().eq("end_object_candidate_id", id))) > 0) {
            return "对象类型候选“" + name + "”已被关系候选引用，请先删除相关关系";
        }
        if (axiomMapper.selectCount(new QueryWrapper<OmAiAxiomDO>()
                .and(query -> query.eq("object_candidate_id", id)
                        .or().eq("other_object_candidate_id", id)
                        .or().eq("domain_candidate_id", id)
                        .or().eq("range_candidate_id", id))) > 0) {
            return "对象类型候选“" + name + "”已被公理候选引用，请先删除相关公理";
        }
        return null;
    }

    /** 属性候选：已采纳，或被公理候选引用时不可删除。 */
    private String attributeBlockReason(Long id) {
        OmAiAttributeDO record = attributeMapper.selectById(id);
        if (record == null) {
            return null;
        }
        String adopted = adopted(record.getAdoptStatus(), null);
        if (adopted != null) {
            return adopted;
        }
        String name = record.getName();
        if (axiomMapper.selectCount(new QueryWrapper<OmAiAxiomDO>()
                .and(query -> query.eq("attribute_candidate_id", id)
                        .or().eq("domain_candidate_id", id)
                        .or().eq("range_candidate_id", id))) > 0) {
            return "属性候选“" + name + "”已被公理候选引用，请先删除相关公理";
        }
        return null;
    }

    /** 建模目标候选：已采纳，或被对象/关系候选绑定时不可删除。 */
    private String targetBlockReason(Long id) {
        OmAiTargetDO record = targetMapper.selectById(id);
        if (record == null) {
            return null;
        }
        String adopted = adopted(record.getAdoptStatus(), null);
        if (adopted != null) {
            return adopted;
        }
        boolean boundByObject = objectTargetMapper.selectCount(
                new QueryWrapper<OmAiObjectTargetDO>().eq("target_candidate_id", id)) > 0;
        boolean boundByRelation = relationTargetMapper.selectCount(
                new QueryWrapper<OmAiRelationTargetDO>().eq("target_candidate_id", id)) > 0;
        if (boundByObject || boundByRelation) {
            return "建模目标候选“" + record.getQuestion() + "”已被对象/关系候选绑定，无法删除";
        }
        return null;
    }

    /** 枚举类型候选：已采纳，或被同任务下引用该项目枚举的属性候选引用时不可删除。 */
    private String enumTypeBlockReason(Long id) {
        OmAiEnumTypeDO record = enumTypeMapper.selectById(id);
        if (record == null) {
            return null;
        }
        String adopted = adopted(record.getAdoptStatus(), null);
        if (adopted != null) {
            return adopted;
        }
        String enumType = record.getEnumType();
        if (enumType != null && !enumType.isBlank()
                && attributeMapper.selectCount(new QueryWrapper<OmAiAttributeDO>()
                .eq("task_id", record.getTaskId())
                .eq("enum_type", enumType)
                .eq("enum_source", ENUM_SOURCE_PROJECT)) > 0) {
            return "枚举类型候选“" + record.getEnumName() + "”已被属性候选引用，无法删除";
        }
        return null;
    }

    /** 命中已采纳返回统一原因，否则返回入参默认原因（可为 null）。 */
    private String adopted(Integer adoptStatus, String defaultReason) {
        if (adoptStatus != null && OmAiAdoptStatusEnum.ADOPTED.getCode().equals(adoptStatus)) {
            return "已采纳数据不可删除";
        }
        return defaultReason;
    }
}
