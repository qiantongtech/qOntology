/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.service.constraint.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.module.om.api.constraint.ConstraintReferenceGuard;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmModelAxiomMapper;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmDataRuleMapper;

/**
 * 公理、规则绑定目标的删除引用校验实现
 *
 * <p>公理按绑定字段查询；规则解析待删目标所属版本的条件数组。
 * 命中引用时返回包含公理或规则名称的业务警告，由调用方在删除前执行校验。</p>
 *
 * @author qOntology
 * @date 2026-10-30
 */
@Service
public class ConstraintReferenceGuardImpl implements ConstraintReferenceGuard {
    /** 模型公理数据访问组件 */
    @Resource
    private OmModelAxiomMapper axiomMapper;
    /** 数据规则数据访问组件 */
    @Resource
    private OmDataRuleMapper ruleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkObjectTypes(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().and(q -> q.in("object_type_id", ids)
            .or().in("other_type_id", ids).or().in("domain_id", ids).or().in("range_id", ids)), "对象类型");
        checkRules(ids, "objectTypeId", "对象类型", "om_object_type");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkAttributes(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("attribute_id", ids), "属性");
        checkRules(ids, "attributeId", "属性", "om_object_type_attribute");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkRelations(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("relation_id", ids), "关系");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkBusinessTypes(Collection<Long> ids, String source) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("business_type_id", ids)
            .and(q -> {
                q.eq("business_type_source", source);
                if ("GLOBAL".equals(source)) q.or().isNull("business_type_source").or().eq("business_type_source", "");
            }), "业务类型");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEnum(String code, String source, Long versionId) {
        if (code == null || code.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().eq("enum_type", code)
            .eq(versionId != null, "version_id", versionId)
            .and(q -> {
                q.eq("enum_source", source);
                if ("PROJECT".equals(source)) q.or().isNull("enum_source").or().eq("enum_source", "");
            }), "枚举类型");
    }

    /**
     * 查询公理引用，命中时以公理名称提示解除绑定。
     *
     * @param query 按目标绑定字段及来源、版本构造的查询条件
     * @param target 待删除目标的中文类型名称，用于提示
     * @throws ServiceException 存在未逻辑删除的公理引用时抛出业务警告
     */
    private void checkAxioms(QueryWrapper<OmModelAxiomDO> query, String target) {
        // 逻辑删除记录不参与校验；停用的公理仍持有绑定。
        for (OmModelAxiomDO axiom : axiomMapper.selectList(query.select("name").orderByAsc("id"))) {
            throw new ServiceException(target + "已被模型公理“" + axiom.getName() + "”引用，请先解除绑定后再删除", 601);
        }
    }

    /**
     * 校验待删目标所属版本的规则条件，精确匹配数字或字符串形式的 ID。
     *
     * @param ids 非空的待删除目标 ID 集合
     * @param field 条件对象中的绑定字段名，由调用方指定 objectTypeId 或 attributeId
     * @param target 待删除目标的中文类型名称，用于提示
     * @param targetTable 目标表名，仅允许由内部调用传入固定表名，不接受外部输入
     * @throws ServiceException 存在规则引用或规则条件结构异常时抛出业务警告
     */
    private void checkRules(Collection<Long> ids, String field, String target, String targetTable) {
        Set<String> targetIds = ids.stream().map(String::valueOf).collect(Collectors.toSet());
        // 表名由内部常量指定；参数仍由 MyBatis 绑定，避免把 ID 拼入 SQL。
        String placeholders = java.util.stream.IntStream.range(0, ids.size())
            .mapToObj(index -> "{" + index + "}").collect(Collectors.joining(","));
        QueryWrapper<OmDataRuleDO> query = new QueryWrapper<OmDataRuleDO>()
            .select("name", "conditions")
            .apply("version_id IN (SELECT version_id FROM " + targetTable + " WHERE id IN ("
                + placeholders + "))", ids.toArray())
            .orderByAsc("id");
        for (OmDataRuleDO rule : ruleMapper.selectList(query)) {
            if (rule.getConditions() == null || rule.getConditions().isBlank()) continue;
            boolean referenced = false;
            try {
                JSONArray conditions = JSON.parseArray(rule.getConditions());
                if (conditions == null) continue;
                for (Object entry : conditions) {
                    if (!(entry instanceof JSONObject)) {
                        throw new IllegalArgumentException("规则条件必须是对象");
                    }
                    Object value = ((JSONObject) entry).get(field);
                    if (value != null && targetIds.contains(value.toString())) referenced = true;
                }
            } catch (RuntimeException ex) {
                throw new ServiceException("数据规则“" + rule.getName() + "”的条件数据异常，请修复后再删除", 601);
            }
            if (referenced) {
                throw new ServiceException(target + "已被数据规则“" + rule.getName() + "”引用，请先解除绑定后再删除", 601);
            }
        }
    }
}
