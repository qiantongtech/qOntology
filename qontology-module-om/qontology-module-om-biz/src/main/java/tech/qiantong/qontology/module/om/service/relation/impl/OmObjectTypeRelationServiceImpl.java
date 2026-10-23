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

package tech.qiantong.qontology.module.om.service.relation.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;
import tech.qiantong.qontology.module.om.service.relation.IOmObjectTypeRelationService;
import tech.qiantong.qontology.module.om.service.omtarget.IOmRelationTargetService;

/**
 * 对象类型关系 Service 业务层处理
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmObjectTypeRelationServiceImpl extends ServiceImpl<OmObjectTypeRelationMapper, OmObjectTypeRelationDO>
        implements IOmObjectTypeRelationService {
    @Resource
    private OmObjectTypeRelationMapper omObjectTypeRelationMapper;

    @Resource
    private OmObjectTypeMapper objectTypeMapper;

    @Resource
    private IOmRelationTargetService relationTargetService;

    @Override
    public PageResult<OmObjectTypeRelationDO> getOmObjectTypeRelationPage(OmObjectTypeRelationPageReqVO request) {
        requireWorkspace(request.getWorkspaceId());
        request.setName(request.getName() == null ? null : request.getName().trim());

        if (request.getVersionId() == null || request.getVersionId() <= 0) {
            throw new ServiceException("请选择项目版本后再查询");
        }
        List<Long> linkedIds = null;
        if (request.getTargetIdList() != null && !request.getTargetIdList().isEmpty()) {
            linkedIds = relationTargetService.list(new QueryWrapper<OmRelationTargetDO>()
                            .select("relation_id").eq("valid_flag", true)
                            .in("target_id", request.getTargetIdList()))
                    .stream().map(OmRelationTargetDO::getRelationId).distinct().collect(Collectors.toList());
            if (linkedIds.isEmpty()) return PageResult.empty();
        }
        PageResult<OmObjectTypeRelationDO> page = omObjectTypeRelationMapper.selectPageWithLinkedIds(request, linkedIds);
        fillObjectTypeNames(page.getList());
        return page;
    }

    @Override
    public OmObjectTypeRelationDO getOmObjectTypeRelationById(Long id, Long workspaceId) {
        OmObjectTypeRelationDO relation = requireRelation(id, workspaceId);
        fillObjectTypeNames(Collections.singletonList(relation));
        return relation;
    }

    @Override
    public Long createOmObjectTypeRelation(OmObjectTypeRelationSaveReqVO request) {
        OmObjectTypeRelationDO relation = buildRelation(request);
        omObjectTypeRelationMapper.insert(relation);
        relationTargetService.saveTargets(relation, request.getTargetIds());
        return relation.getId();
    }

    @Override
    public int updateOmObjectTypeRelation(OmObjectTypeRelationSaveReqVO request) {
        OmObjectTypeRelationDO current = requireRelation(request.getId(), request.getWorkspaceId());
        request.setVersionId(current.getVersionId());
        OmObjectTypeRelationDO relation = buildRelation(request);
        relation.setId(current.getId());
        relation.setVersionId(current.getVersionId());
        relation.setValidFlag(current.getValidFlag());
        if (request.getRemark() == null) {
            relation.setRemark(current.getRemark());
        }
        int rows = omObjectTypeRelationMapper.update(relation, new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("id", current.getId())
                .eq("workspace_id", current.getWorkspaceId()));
        relationTargetService.saveTargets(relation, request.getTargetIds());
        return rows;
    }

    @Override
    public int removeOmObjectTypeRelation(Collection<Long> idList, Long workspaceId) {
        if (idList == null || idList.isEmpty() || idList.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请选择需要删除的关系");
        }
        Set<Long> ids = new HashSet<>(idList);
        Long count = omObjectTypeRelationMapper.selectCount(new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("workspace_id", workspaceId)
                .in("id", ids));
        if (count != ids.size()) {
            throw new ServiceException("部分关系已不存在，请刷新后重试");
        }
        relationTargetService.removeByRelations(ids, workspaceId);
        return omObjectTypeRelationMapper.delete(new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("workspace_id", workspaceId)
                .in("id", ids));
    }

    @Override
    public List<OmObjectTypeDO> getObjectTypeOptions(Long workspaceId, Long versionId) {
        requireWorkspace(workspaceId);
        QueryWrapper<OmObjectTypeDO> query = new QueryWrapper<OmObjectTypeDO>()
                .eq("workspace_id", workspaceId)
                .eq("valid_flag", 1)
                .orderByAsc("order_num", "id");
        if (versionId != null) {
            query.eq("version_id", versionId);
        }
        return objectTypeMapper.selectList(query);
    }

    @Override
    public Map<Long, Long> getOmObjectTypeRelationCounts(Collection<Long> objectTypeIds) {
        Map<Long, Long> counts = new HashMap<>();
        if (objectTypeIds == null || objectTypeIds.isEmpty()) {
            return counts;
        }
        Set<Long> ids = new HashSet<>(objectTypeIds);
        List<OmObjectTypeRelationDO> relations = omObjectTypeRelationMapper.selectList(new QueryWrapper<OmObjectTypeRelationDO>()
                .select("start_type_id", "end_type_id")
                .and(query -> query.in("start_type_id", ids).or().in("end_type_id", ids)));
        for (OmObjectTypeRelationDO relation : relations) {
            Long start = relation.getStartTypeId();
            Long end = relation.getEndTypeId();
            if (ids.contains(start)) {
                counts.merge(start, 1L, Long::sum);
            }
            if (!Objects.equals(start, end) && ids.contains(end)) {
                counts.merge(end, 1L, Long::sum);
            }
        }
        return counts;
    }

    private OmObjectTypeRelationDO requireRelation(Long id, Long workspaceId) {
        requireWorkspace(workspaceId);
        if (id == null) {
            throw new ServiceException("关系id不能为空");
        }
        OmObjectTypeRelationDO relation = omObjectTypeRelationMapper.selectOne(new QueryWrapper<OmObjectTypeRelationDO>()
                .eq("id", id)
                .eq("workspace_id", workspaceId));
        if (relation == null) {
            throw new ServiceException("关系不存在或不属于当前工作区");
        }
        return relation;
    }

    private OmObjectTypeRelationDO buildRelation(OmObjectTypeRelationSaveReqVO request) {
        requireWorkspace(request.getWorkspaceId());
        String name = trim(request.getName());
        String description = trim(request.getDescription());
        String reverseName = trim(request.getReverseName());
        if (name.isEmpty() || name.length() > 128) {
            throw new ServiceException("关系名称不能为空，长度不能超过128个字符");
        }
        if (description.length() > 256) {
            throw new ServiceException("关系描述不能超过256个字符");
        }
        if (reverseName.length() > 128) {
            throw new ServiceException("反向名称不能超过128个字符");
        }
        Integer reversible = request.getReversibleFlag();
        if (reversible == null || reversible < 0 || reversible > 1) {
            throw new ServiceException("是否可逆只能为0或1");
        }
        if (reversible == 1 && reverseName.isEmpty()) {
            throw new ServiceException("可逆关系的反向名称不能为空");
        }
        requireType(request.getStartTypeId(), request.getWorkspaceId(), request.getVersionId());
        requireType(request.getEndTypeId(), request.getWorkspaceId(), request.getVersionId());
        String remark = trim(request.getRemark());
        if (remark.length() > 512) {
            throw new ServiceException("备注不能超过512个字符");
        }
        OmObjectTypeRelationDO relation = OmObjectTypeRelationDO.builder()
                .workspaceId(request.getWorkspaceId())
                .versionId(request.getVersionId())
                .name(name)
                .description(description)
                .startTypeId(request.getStartTypeId())
                .endTypeId(request.getEndTypeId())
                .reversibleFlag(reversible)
                .reverseName(reverseName)
                .build();
        relation.setRemark(remark);
        return relation;
    }

    private void requireType(Long id, Long workspaceId, Long versionId) {
        if (id == null) {
            throw new ServiceException("请选择起点和终点类型");
        }
        OmObjectTypeDO type = objectTypeMapper.selectById(id);
        if (type == null || Boolean.FALSE.equals(type.getValidFlag())
                || !Objects.equals(type.getWorkspaceId(), workspaceId)
                || !Objects.equals(type.getVersionId(), versionId)) {
            throw new ServiceException("起点、终点类型必须有效且属于当前项目版本");
        }
    }

    private void fillObjectTypeNames(List<OmObjectTypeRelationDO> relations) {
        Set<Long> ids = new HashSet<>();
        for (OmObjectTypeRelationDO relation : relations) {
            if (relation.getStartTypeId() != null) {
                ids.add(relation.getStartTypeId());
            }
            if (relation.getEndTypeId() != null) {
                ids.add(relation.getEndTypeId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, String> names = objectTypeMapper.selectList(new QueryWrapper<OmObjectTypeDO>().in("id", ids))
                .stream().filter(type -> type.getName() != null)
                .collect(Collectors.toMap(OmObjectTypeDO::getId, OmObjectTypeDO::getName, (left, right) -> left));
        for (OmObjectTypeRelationDO relation : relations) {
            relation.setStartTypeName(names.get(relation.getStartTypeId()));
            relation.setEndTypeName(names.get(relation.getEndTypeId()));
        }
    }

    private void requireWorkspace(Long id) {
        if (id == null) {
            throw new ServiceException("工作区id不能为空");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
