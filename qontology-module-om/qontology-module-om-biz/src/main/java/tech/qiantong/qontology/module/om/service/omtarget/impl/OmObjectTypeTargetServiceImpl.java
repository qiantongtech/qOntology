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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmRelationTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetMapper;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmObjectTypeTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmObjectTypeTargetMapper;
import tech.qiantong.qontology.module.om.service.omtarget.IOmObjectTypeTargetService;

/**
 * 建模目标与对象类型关联 Service 实现
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmObjectTypeTargetServiceImpl
        extends ServiceImpl<OmObjectTypeTargetMapper, OmObjectTypeTargetDO>
        implements IOmObjectTypeTargetService {
    @Resource
    private OmObjectTypeTargetMapper objectTypeTargetMapper;

    @Resource
    private OmTargetMapper targetMapper;

    @Resource
    private OmObjectTypeMapper objectTypeMapper;

    @Resource
    private OmRelationTargetMapper relationTargetMapper;

    @Override
    public PageResult<OmObjectTypeTargetRespVO> getTargetOptions(OmTargetPageReqVO request) {
        if (request.getVersionId() == null || request.getVersionId() <= 0) {
            throw new ServiceException("请选择当前项目版本后再导入建模目标", 601);
        }
        PageResult<OmTargetDO> page = targetMapper.selectPage(request, new QueryWrapper<OmTargetDO>()
                .eq("valid_flag", true)
                .eq("version_id", request.getVersionId())
                .like(request.getQuestion() != null && !request.getQuestion().trim().isEmpty(),
                        "question", request.getQuestion())
                .like(request.getQuestionSource() != null && !request.getQuestionSource().trim().isEmpty(),
                        "question_source", request.getQuestionSource())
                .orderByAsc("id"));
        return new PageResult<>(convertTargets(page.getList()), page.getTotal());
    }

    @Override
    public List<OmObjectTypeTargetRespVO> getTargets(Long objectTypeId) {
        requireObjectType(objectTypeId);
        List<Long> ids = links(objectTypeId).stream().map(OmObjectTypeTargetDO::getTargetId)
                .distinct().collect(Collectors.toList());
        return getTargetViews(ids);
    }

    @Override
    public PageResult<OmObjectTypeTargetRespVO> getTargetsPage(Long objectTypeId, OmTargetPageReqVO request) {
        requireObjectType(objectTypeId);
        PageResult<OmTargetDO> page = targetMapper.selectPage(request, targetMapper.orderRelatedTargets(new QueryWrapper<OmTargetDO>()
                .exists("SELECT 1 FROM om_object_type_target ot WHERE ot.target_id = om_target.id"
                        + " AND ot.object_type_id = {0} AND ot.valid_flag = 1 AND ot.del_flag = 0", objectTypeId)
                .like(request.getQuestion() != null && !request.getQuestion().trim().isEmpty(),
                        "question", request.getQuestion() == null ? null : request.getQuestion().trim())
                .like(request.getQuestionSource() != null && !request.getQuestionSource().trim().isEmpty(),
                        "question_source", request.getQuestionSource() == null ? null : request.getQuestionSource().trim()), request));
        return new PageResult<>(convertTargets(page.getList()), page.getTotal());
    }

    @Override
    public List<OmObjectTypeTargetRespVO> getTargetViews(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return convertTargets(targetMapper.selectList(new QueryWrapper<OmTargetDO>()
                .in("id", ids).orderByAsc("id")));
    }

    private List<OmObjectTypeTargetRespVO> convertTargets(List<OmTargetDO> targets) {
        if (targets.isEmpty()) return Collections.emptyList();
        List<Long> ids = targets.stream().map(OmTargetDO::getId).collect(Collectors.toList());
        Map<Long, Long> counts = objectTypeTargetMapper.selectList(new QueryWrapper<OmObjectTypeTargetDO>()
                .in("target_id", ids).eq("valid_flag", true)).stream()
                .collect(Collectors.groupingBy(OmObjectTypeTargetDO::getTargetId,
                        Collectors.mapping(OmObjectTypeTargetDO::getObjectTypeId,
                                Collectors.collectingAndThen(Collectors.toSet(), set -> (long) set.size()))));
        Map<Long, Long> relationCounts = relationTargetMapper.selectList(new QueryWrapper<OmRelationTargetDO>()
                .in("target_id", ids).eq("valid_flag", true).isNotNull("relation_id")).stream()
                .collect(Collectors.groupingBy(OmRelationTargetDO::getTargetId,
                        Collectors.mapping(OmRelationTargetDO::getRelationId,
                                Collectors.collectingAndThen(Collectors.toSet(), set -> (long) set.size()))));
        List<OmObjectTypeTargetRespVO> result = new ArrayList<>();
        for (OmTargetDO target : targets) {
            OmObjectTypeTargetRespVO item = BeanUtils.toBean(target, OmObjectTypeTargetRespVO.class);
            item.setRelatedElementCount(counts.getOrDefault(target.getId(), 0L)
                    + relationCounts.getOrDefault(target.getId(), 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public void saveTargets(OmObjectTypeDO objectType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty() || targetIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请至少关联一个有效的建模目标", 601);
        }
        // 同一对象类型的并发保存串行执行，避免重复写入关联。
        requireObjectTypeForUpdate(objectType.getId());
        Set<Long> selected = new LinkedHashSet<>(targetIds);
        List<OmObjectTypeTargetDO> existing = links(objectType.getId());
        Set<Long> affected = existing.stream().map(OmObjectTypeTargetDO::getTargetId).collect(Collectors.toSet());
        affected.addAll(selected);
        List<OmTargetDO> targets = lockTargets(affected);
        Set<Long> available = targets.stream().filter(target -> Boolean.TRUE.equals(target.getValidFlag())
                        && java.util.Objects.equals(target.getVersionId(), objectType.getVersionId()))
                .map(OmTargetDO::getId).collect(Collectors.toSet());
        if (!available.containsAll(selected)) {
            throw new ServiceException("部分建模目标已不存在、已停用或不属于当前项目版本，请重新选择", 601);
        }

        Set<Long> retained = new HashSet<>();
        for (OmObjectTypeTargetDO link : existing) {
            if (!selected.contains(link.getTargetId()) || !retained.add(link.getTargetId())) {
                objectTypeTargetMapper.deleteById(link.getId());
            }
        }
        for (Long targetId : selected) {
            if (retained.contains(targetId)) continue;
            objectTypeTargetMapper.insert(OmObjectTypeTargetDO.builder()
                    .objectTypeId(objectType.getId()).targetId(targetId)
                    .workspaceId(objectType.getWorkspaceId()).versionId(objectType.getVersionId()).build());
        }
    }

    @Override
    public void removeByObjectType(Long objectTypeId) {
        requireObjectTypeForUpdate(objectTypeId);
        objectTypeTargetMapper.delete(new QueryWrapper<OmObjectTypeTargetDO>().eq("object_type_id", objectTypeId));
    }

    @Override
    public List<OmObjectTypeDO> getObjectTypes(Long targetId) {
        if (targetMapper.selectById(targetId) == null) throw new ServiceException("建模目标不存在", 601);
        List<Long> ids = objectTypeTargetMapper.selectList(new QueryWrapper<OmObjectTypeTargetDO>()
                .eq("target_id", targetId).eq("valid_flag", true)).stream()
                .map(OmObjectTypeTargetDO::getObjectTypeId).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) return Collections.emptyList();
        return objectTypeMapper.selectList(new QueryWrapper<OmObjectTypeDO>().in("id", ids)
                .orderByAsc("order_num", "id"));
    }

    private List<OmObjectTypeTargetDO> links(Long objectTypeId) {
        return objectTypeTargetMapper.selectList(new QueryWrapper<OmObjectTypeTargetDO>()
                .eq("object_type_id", objectTypeId).eq("valid_flag", true).orderByAsc("id"));
    }

    private OmObjectTypeDO requireObjectType(Long id) {
        OmObjectTypeDO type = objectTypeMapper.selectById(id);
        if (type == null) throw new ServiceException("对象类型不存在", 601);
        return type;
    }

    private void requireObjectTypeForUpdate(Long id) {
        OmObjectTypeDO type = objectTypeMapper.selectOne(new QueryWrapper<OmObjectTypeDO>()
                .eq("id", id).last("FOR UPDATE"));
        if (type == null) throw new ServiceException("对象类型不存在", 601);
    }

    private List<OmTargetDO> lockTargets(Collection<Long> ids) {
        return targetMapper.selectList(new QueryWrapper<OmTargetDO>().in("id", ids)
                .orderByAsc("id").last("FOR UPDATE"));
    }
}
