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

@Service
public class ConstraintReferenceGuardImpl implements ConstraintReferenceGuard {
    @Resource
    private OmModelAxiomMapper axiomMapper;
    @Resource
    private OmDataRuleMapper ruleMapper;

    @Override
    public void checkObjectTypes(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().and(q -> q.in("object_type_id", ids)
            .or().in("other_type_id", ids).or().in("domain_id", ids).or().in("range_id", ids)), "对象类型");
        checkRules(ids, "objectTypeId", "对象类型", "om_object_type");
    }

    @Override
    public void checkAttributes(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("attribute_id", ids), "属性");
        checkRules(ids, "attributeId", "属性", "om_object_type_attribute");
    }

    @Override
    public void checkRelations(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("relation_id", ids), "关系");
    }

    @Override
    public void checkBusinessTypes(Collection<Long> ids, String source) {
        if (ids == null || ids.isEmpty()) return;
        checkAxioms(new QueryWrapper<OmModelAxiomDO>().in("business_type_id", ids)
            .and(q -> {
                q.eq("business_type_source", source);
                if ("GLOBAL".equals(source)) q.or().isNull("business_type_source").or().eq("business_type_source", "");
            }), "业务类型");
    }

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

    private void checkAxioms(QueryWrapper<OmModelAxiomDO> query, String target) {
        // 逻辑删除记录不参与校验；停用的公理仍持有绑定。
        for (OmModelAxiomDO axiom : axiomMapper.selectList(query.select("name").orderByAsc("id"))) {
            throw new ServiceException(target + "已被模型公理“" + axiom.getName() + "”引用，请先解除绑定后再删除", 601);
        }
    }

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
