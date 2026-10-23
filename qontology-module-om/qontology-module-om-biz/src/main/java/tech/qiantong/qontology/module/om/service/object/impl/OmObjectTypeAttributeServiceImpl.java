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

package tech.qiantong.qontology.module.om.service.object.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.ArrayList;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.dto.dataType.BasicDataTypeOptionDTO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmAttributeBusinessTypeOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.dataType.OmDataTypeMapper;
import tech.qiantong.qontology.module.om.service.dataType.IOmDataTypeService;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.module.basic.api.service.dataType.IBasicDataTypeApiService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeAttributeService;

/**
 * 对象类型属性 Service 实现
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmObjectTypeAttributeServiceImpl
        extends ServiceImpl<OmObjectTypeAttributeMapper, OmObjectTypeAttributeDO>
        implements IOmObjectTypeAttributeService {
    @Resource
    private OmObjectTypeAttributeMapper omObjectTypeAttributeMapper;

    @Resource
    private OmObjectTypeMapper objectTypeMapper;

    @Resource
    private IBasicDataTypeApiService dataTypeApiService;

    @Resource
    private OmDataTypeMapper projectDataTypeMapper;
    @Resource
    private IOmDataTypeService omDataTypeService;

    @Override
    public List<OmAttributeBusinessTypeOptionRespVO> getBusinessDataTypes(Long objectTypeId, Long workspaceId, String source) {
        OmObjectTypeDO parent = requireObjectType(objectTypeId, workspaceId);
        requireSource(source);
        List<OmAttributeBusinessTypeOptionRespVO> result = new ArrayList<>();
        if ("GLOBAL".equals(source)) {
            dataTypeApiService.getAvailableDataTypes().forEach(type -> result.add(toGlobalOption(type)));
        } else {
            if (parent.getVersionId() == null) throw new ServiceException("请选择项目版本");
            projectDataTypeMapper.selectList(new QueryWrapper<OmDataTypeDO>()
                    .eq("version_id", parent.getVersionId()).eq("valid_flag", true).orderByDesc("create_time", "id"))
                    .forEach(type -> result.add(toProjectOption(type)));
        }
        return result;
    }

    @Override
    public List<OmDataTypeEnumOptionRespVO> getEnumOptions(Long objectTypeId, Long workspaceId, String source) {
        OmObjectTypeDO parent = requireObjectType(objectTypeId, workspaceId);
        return omDataTypeService.getEnumOptions(parent.getVersionId(), source);
    }

    private void requireSource(String source) {
        if (!Set.of("PROJECT", "GLOBAL").contains(source == null ? "" : source)) {
            throw new ServiceException("类型来源只能为当前项目或全局");
        }
    }

    private OmAttributeBusinessTypeOptionRespVO toGlobalOption(BasicDataTypeOptionDTO type) {
        OmAttributeBusinessTypeOptionRespVO option = BeanUtils.toBean(type, OmAttributeBusinessTypeOptionRespVO.class);
        option.setBusinessTypeSource("GLOBAL");
        option.setEnumSource("GLOBAL");
        return option;
    }

    private OmAttributeBusinessTypeOptionRespVO toProjectOption(OmDataTypeDO type) {
        OmAttributeBusinessTypeOptionRespVO option = BeanUtils.toBean(type, OmAttributeBusinessTypeOptionRespVO.class);
        option.setDataType(type.getDataType() == null ? null : type.getDataType().getCode());
        option.setBusinessTypeSource("PROJECT");
        return option;
    }

    private OmAttributeBusinessTypeOptionRespVO requireBusinessType(Long id, String source, OmObjectTypeDO parent) {
        requireSource(source);
        if (id == null) throw new ServiceException("请选择业务数据类型");
        if ("GLOBAL".equals(source)) {
            BasicDataTypeOptionDTO type = dataTypeApiService.getAvailableDataType(id);
            if (type == null) throw new ServiceException("全局业务数据类型不存在或已停用");
            return toGlobalOption(type);
        }
        OmDataTypeDO type = projectDataTypeMapper.selectById(id);
        if (type == null || !Boolean.TRUE.equals(type.getValidFlag()) || parent.getVersionId() == null
                || !Objects.equals(type.getVersionId(), parent.getVersionId())) {
            throw new ServiceException("业务数据类型不存在、已停用或不属于当前项目版本");
        }
        return toProjectOption(type);
    }

    @Override
    public PageResult<OmObjectTypeAttributeDO> getOmObjectTypeAttributePage(OmObjectTypeAttributePageReqVO request) {
        requireObjectType(request.getObjectTypeId(), request.getWorkspaceId());
        request.setName(request.getName() == null ? null : request.getName().trim());
        return omObjectTypeAttributeMapper.selectPage(request);
    }

    @Override
    public OmObjectTypeAttributeDO getOmObjectTypeAttributeById(Long id, Long objectTypeId, Long workspaceId) {
        requireObjectType(objectTypeId, workspaceId);
        OmObjectTypeAttributeDO attribute = omObjectTypeAttributeMapper.selectById(id);
        if (attribute == null || !Objects.equals(attribute.getObjectTypeId(), objectTypeId)
                || !Objects.equals(attribute.getWorkspaceId(), workspaceId)) {
            throw new ServiceException("对象类型属性不存在或不属于当前对象类型");
        }
        return attribute;
    }

    @Override
    public Long createOmObjectTypeAttribute(OmObjectTypeAttributeSaveReqVO request) {
        request.setId(null);
        OmObjectTypeDO parent = requireObjectType(request.getObjectTypeId(), request.getWorkspaceId());
        OmObjectTypeAttributeDO attribute = buildAttribute(request, parent);
        omObjectTypeAttributeMapper.insert(attribute);
        return attribute.getId();
    }

    @Override
    public int updateOmObjectTypeAttribute(OmObjectTypeAttributeSaveReqVO request) {
        if (request.getId() == null) {
            throw new ServiceException("属性id不能为空");
        }
        OmObjectTypeDO parent = requireObjectType(request.getObjectTypeId(), request.getWorkspaceId());
        OmObjectTypeAttributeDO existing = getOmObjectTypeAttributeById(
                request.getId(), request.getObjectTypeId(), request.getWorkspaceId());
        OmObjectTypeAttributeDO attribute = buildAttribute(request, parent);
        attribute.setId(request.getId());
        attribute.setValidFlag(existing.getValidFlag());
        attribute.setVersionId(existing.getVersionId());
        if (request.getRemark() == null) {
            attribute.setRemark(existing.getRemark());
        }
        return omObjectTypeAttributeMapper.updateById(attribute);
    }

    @Override
    public int removeOmObjectTypeAttribute(Collection<Long> idList, Long objectTypeId, Long workspaceId) {
        if (idList == null || idList.isEmpty() || idList.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("请选择需要删除的属性");
        }
        requireObjectType(objectTypeId, workspaceId);
        Set<Long> ids = new LinkedHashSet<>(idList);
        List<OmObjectTypeAttributeDO> attributes = omObjectTypeAttributeMapper.selectList(
                new QueryWrapper<OmObjectTypeAttributeDO>().eq("object_type_id", objectTypeId)
                        .eq("workspace_id", workspaceId).in("id", ids).last("FOR UPDATE"));
        if (attributes.size() != ids.size()) {
            throw new ServiceException("部分属性已不存在或不属于当前对象类型，请刷新后重试");
        }
        return omObjectTypeAttributeMapper.delete(new QueryWrapper<OmObjectTypeAttributeDO>()
                .eq("object_type_id", objectTypeId).eq("workspace_id", workspaceId).in("id", ids));
    }

    @Override
    public Map<Long, Long> getOmObjectTypeAttributeCounts(Collection<Long> objectTypeIds) {
        Map<Long, Long> counts = new HashMap<>();
        if (objectTypeIds == null || objectTypeIds.isEmpty()) {
            return counts;
        }
        // 对当前列表的对象类型一次性聚合，避免逐行查询数量。
        for (Map<String, Object> row : omObjectTypeAttributeMapper.selectMaps(new QueryWrapper<OmObjectTypeAttributeDO>()
                .select("object_type_id", "COUNT(*) AS attribute_count")
                .in("object_type_id", objectTypeIds)
                .groupBy("object_type_id"))) {
            Long objectTypeId = ((Number) row.get("object_type_id")).longValue();
            counts.put(objectTypeId, ((Number) row.get("attribute_count")).longValue());
        }
        return counts;
    }

    private OmObjectTypeDO requireObjectType(Long id, Long workspaceId) {
        if (id == null || workspaceId == null) {
            throw new ServiceException("对象类型id和工作区id不能为空");
        }
        OmObjectTypeDO parent = objectTypeMapper.selectById(id);
        if (parent == null || !Objects.equals(parent.getWorkspaceId(), workspaceId)) {
            throw new ServiceException("对象类型不存在或不属于当前工作区");
        }
        return parent;
    }

    private OmObjectTypeAttributeDO buildAttribute(OmObjectTypeAttributeSaveReqVO request, OmObjectTypeDO parent) {
        String name = request.getName() == null ? "" : request.getName().trim();
        String description = request.getDescription() == null ? "" : request.getDescription().trim();
        String remark = request.getRemark() == null ? "" : request.getRemark().trim();
        if (name.isEmpty() || name.length() > 128) {
            throw new ServiceException("属性名称不能为空，长度不能超过128个字符");
        }
        if (request.getTypeCategory() == null || (request.getTypeCategory() != 1 && request.getTypeCategory() != 2)) {
            throw new ServiceException("请选择基础数据类型或业务数据类型");
        }
        String code = request.getCode() == null ? "" : request.getCode().trim();
        if (code.length() > 128 || !code.matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new ServiceException("属性代码须以字母开头，仅允许字母、数字和下划线，长度不超过128");
        }
        QueryWrapper<OmObjectTypeAttributeDO> duplicate = new QueryWrapper<OmObjectTypeAttributeDO>()
                .eq("object_type_id", parent.getId()).eq("code", code);
        if (request.getId() != null) duplicate.ne("id", request.getId());
        if (omObjectTypeAttributeMapper.selectCount(duplicate) > 0) throw new ServiceException("当前对象类型下属性代码已存在");
        Integer basicTypeCode;
        Long businessTypeId = null;
        String businessSource = null;
        String unit = null;
        Integer decimalPlaces = null;
        String format = null;
        String enumType = null;
        String enumSource = null;
        if (request.getTypeCategory() == 1) {
            basicTypeCode = request.getBasicTypeCode();
            if (basicTypeCode == null || !Set.of(0, 1, 2, 3, 4, 5).contains(basicTypeCode)) {
                throw new ServiceException("请选择有效的基础数据类型");
            }
            if (basicTypeCode == 1 || basicTypeCode == 2) {
                unit = request.getUnit() == null || request.getUnit().isBlank() ? null : request.getUnit().trim();
                if (unit != null && unit.length() > 32) throw new ServiceException("单位长度不超过32");
            }
            if (basicTypeCode == 2) {
                decimalPlaces = request.getDecimalPlaces();
                if (decimalPlaces != null && decimalPlaces < 0) throw new ServiceException("请填写有效的小数位数");
            }
            if (basicTypeCode == 4) {
                format = request.getFormat() == null || request.getFormat().isBlank() ? null : request.getFormat().trim();
                if (format != null && format.length() > 128) throw new ServiceException("日期格式长度不超过128");
            }
            if (basicTypeCode == 5 && request.getEnumType() != null && !request.getEnumType().isBlank()) {
                enumType = request.getEnumType().trim();
                enumSource = request.getEnumSource() == null ? "PROJECT" : request.getEnumSource();
                requireSource(enumSource);
                final String selectedCode = enumType;
                if (enumType == null || omDataTypeService.getEnumOptions(parent.getVersionId(), enumSource).stream()
                        .noneMatch(option -> Objects.equals(option.getEnumType(), selectedCode))) {
                    throw new ServiceException("请选择对应来源下的有效枚举类型");
                }
            }
        } else {
            businessTypeId = request.getBusinessTypeId();
            businessSource = request.getBusinessTypeSource() == null ? "GLOBAL" : request.getBusinessTypeSource();
            OmAttributeBusinessTypeOptionRespVO selected = requireBusinessType(businessTypeId, businessSource, parent);
            if (selected.getDataType() == null || !selected.getDataType().matches("[0-5]")) {
                throw new ServiceException("所选业务数据类型配置无效");
            }
            // 只读配置一律从服务端来源读取，不采用客户端提交的配置。
            basicTypeCode = Integer.valueOf(selected.getDataType());
            unit = selected.getUnit();
            decimalPlaces = selected.getDecimalPlaces();
            format = selected.getFormat();
            enumType = selected.getEnumType();
            enumSource = selected.getEnumSource();
        }
        if (description.length() > 512) {
            throw new ServiceException("属性描述长度不能超过512个字符");
        }
        if (remark.length() > 512) {
            throw new ServiceException("备注长度不能超过512个字符");
        }
        OmObjectTypeAttributeDO attribute = OmObjectTypeAttributeDO.builder()
                .workspaceId(parent.getWorkspaceId())
                .versionId(parent.getVersionId())
                .objectTypeId(parent.getId())
                .name(name)
                .code(code)
                .typeCategory(request.getTypeCategory())
                .basicTypeCode(basicTypeCode)
                .businessTypeId(businessTypeId)
                .businessTypeSource(businessSource)
                .unit(unit)
                .decimalPlaces(decimalPlaces)
                .format(format)
                .enumType(enumType)
                .enumSource(enumSource)
                .description(description)
                .build();
        attribute.setRemark(remark);
        return attribute;
    }
}
