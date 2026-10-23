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

package tech.qiantong.qontology.module.om.service.omtarget.impl;

import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;
import tech.qiantong.qontology.module.om.service.omtarget.IOmObjectTypeTargetService;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmRelationTargetMapper;
import tech.qiantong.qontology.module.om.service.omtarget.IOmRelationTargetService;

/**
 * 对象类型关系与建模目标关联 Service 实现
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmRelationTargetServiceImpl
        extends ServiceImpl<OmRelationTargetMapper, OmRelationTargetDO>
        implements IOmRelationTargetService {
    @Resource
    private OmRelationTargetMapper relationTargetMapper;

    @Resource
    private OmObjectTypeRelationMapper relationMapper;

    @Resource
    private OmTargetMapper targetMapper;

    @Resource
    private OmObjectTypeMapper objectTypeMapper;

    @Resource
    private IOmObjectTypeTargetService objectTypeTargetService;

    @Override
    public List<OmObjectTypeTargetRespVO> getTargets(Long relationId, Long workspaceId) {
        requireRelation(relationId, workspaceId, false);
        List<Long> ids = links(Collections.singletonList(relationId), workspaceId).stream()
                .map(OmRelationTargetDO::getTargetId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        return objectTypeTargetService.getTargetViews(ids);
    }

    @Override
    public PageResult<OmObjectTypeTargetRespVO> getTargetsPage(Long relationId, Long workspaceId, OmTargetPageReqVO request) {
        requireRelation(relationId, workspaceId, false);
        PageResult<OmTargetDO> page = targetMapper.selectPage(request, targetMapper.orderRelatedTargets(new QueryWrapper<OmTargetDO>()
                .exists("SELECT 1 FROM om_relation_target rt WHERE rt.target_id = om_target.id"
                        + " AND rt.relation_id = {0} AND rt.workspace_id = {1}"
                        + " AND rt.valid_flag = 1 AND rt.del_flag = 0", relationId, workspaceId)
                .like(request.getQuestion() != null && !request.getQuestion().trim().isEmpty(),
                        "question", request.getQuestion() == null ? null : request.getQuestion().trim())
                .like(request.getQuestionSource() != null && !request.getQuestionSource().trim().isEmpty(),
                        "question_source", request.getQuestionSource() == null ? null : request.getQuestionSource().trim()), request));
        List<Long> ids = page.getList().stream().map(OmTargetDO::getId).collect(Collectors.toList());
        Map<Long, OmObjectTypeTargetRespVO> views = objectTypeTargetService.getTargetViews(ids).stream()
                .collect(Collectors.toMap(OmObjectTypeTargetRespVO::getId, item -> item));
        return new PageResult<>(ids.stream().map(views::get).filter(Objects::nonNull)
                .collect(Collectors.toList()), page.getTotal());
    }

    @Override
    public void saveTargets(OmObjectTypeRelationDO relation, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty() || targetIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请至少关联一个有效的建模目标");
        }
        // 同一关系的并发保存串行执行，保留未变更关联的编号和创建信息。
        requireRelation(relation.getId(), relation.getWorkspaceId(), true);
        Set<Long> selected = new LinkedHashSet<>(targetIds);
        List<OmRelationTargetDO> existing = links(Collections.singletonList(relation.getId()), relation.getWorkspaceId());
        Set<Long> affected = existing.stream().map(OmRelationTargetDO::getTargetId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        affected.addAll(selected);
        List<OmTargetDO> targets = lockTargets(affected);
        Set<Long> available = targets.stream().filter(target -> Boolean.TRUE.equals(target.getValidFlag())
                        && java.util.Objects.equals(target.getVersionId(), relation.getVersionId()))
                .map(OmTargetDO::getId).collect(Collectors.toSet());
        if (!available.containsAll(selected)) {
            throw new ServiceException("部分建模目标已不存在、已停用或不属于当前项目版本，请重新选择");
        }
        Set<Long> retained = new HashSet<>();
        for (OmRelationTargetDO link : existing) {
            if (!selected.contains(link.getTargetId()) || !retained.add(link.getTargetId())) {
                relationTargetMapper.deleteById(link.getId());
            }
        }
        for (Long targetId : selected) {
            if (retained.contains(targetId)) continue;
            relationTargetMapper.insert(OmRelationTargetDO.builder()
                    .relationId(relation.getId()).targetId(targetId)
                    .workspaceId(relation.getWorkspaceId()).versionId(relation.getVersionId()).build());
        }
    }

    @Override
    public void removeByRelations(Collection<Long> relationIds, Long workspaceId) {
        if (relationIds == null || relationIds.isEmpty()) return;
        Set<Long> ids = new HashSet<>(relationIds);
        List<OmObjectTypeRelationDO> relations = relationMapper.selectList(new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("workspace_id", workspaceId).in("id", ids).orderByAsc("id").last("FOR UPDATE"));
        if (relations.size() != ids.size()) {
            throw new ServiceException("部分关系已不存在，请刷新后重试");
        }
        relationTargetMapper.delete(new QueryWrapper<OmRelationTargetDO>()
                .eq("workspace_id", workspaceId).in("relation_id", ids));
    }

    @Override
    public List<OmObjectTypeRelationDO> getRelations(Long targetId, Long workspaceId) {
        if (targetMapper.selectById(targetId) == null) throw new ServiceException("建模目标不存在");
        List<Long> ids = relationTargetMapper.selectList(new QueryWrapper<OmRelationTargetDO>()
                .eq("target_id", targetId).eq("workspace_id", workspaceId).eq("valid_flag", true)
                .isNotNull("relation_id")).stream().map(OmRelationTargetDO::getRelationId)
                .distinct().collect(Collectors.toList());
        if (ids.isEmpty()) return Collections.emptyList();
        List<OmObjectTypeRelationDO> relations = relationMapper.selectList(new QueryWrapper<OmObjectTypeRelationDO>()
                .in("id", ids).eq("workspace_id", workspaceId).orderByAsc("id"));
        Set<Long> typeIds = new HashSet<>();
        for (OmObjectTypeRelationDO relation : relations) {
            if (relation.getStartTypeId() != null) typeIds.add(relation.getStartTypeId());
            if (relation.getEndTypeId() != null) typeIds.add(relation.getEndTypeId());
        }
        if (!typeIds.isEmpty()) {
            Map<Long, String> names = objectTypeMapper.selectList(new QueryWrapper<OmObjectTypeDO>().in("id", typeIds))
                    .stream().filter(type -> type.getName() != null)
                    .collect(Collectors.toMap(OmObjectTypeDO::getId, OmObjectTypeDO::getName, (left, right) -> left));
            for (OmObjectTypeRelationDO relation : relations) {
                relation.setStartTypeName(names.get(relation.getStartTypeId()));
                relation.setEndTypeName(names.get(relation.getEndTypeId()));
            }
        }
        return relations;
    }

    private List<OmRelationTargetDO> links(Collection<Long> relationIds, Long workspaceId) {
        return relationTargetMapper.selectList(new QueryWrapper<OmRelationTargetDO>()
                .in("relation_id", relationIds).eq("workspace_id", workspaceId).eq("valid_flag", true).orderByAsc("id"));
    }

    private void requireRelation(Long id, Long workspaceId, boolean lock) {
        if (id == null || workspaceId == null) throw new ServiceException("关系id和工作区id不能为空");
        QueryWrapper<OmObjectTypeRelationDO> query = new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("id", id).eq("workspace_id", workspaceId);
        if (lock) query.last("FOR UPDATE");
        if (relationMapper.selectOne(query) == null) throw new ServiceException("关系不存在或不属于当前工作区");
    }

    private List<OmTargetDO> lockTargets(Collection<Long> ids) {
        return targetMapper.selectList(new QueryWrapper<OmTargetDO>().in("id", ids)
                .orderByAsc("id").last("FOR UPDATE"));
    }
}
