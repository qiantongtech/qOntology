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

import tech.qiantong.qontology.module.om.api.constraint.ConstraintReferenceGuard;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmObjectTypeTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeService;
import tech.qiantong.qontology.module.om.service.omtarget.IOmObjectTypeTargetService;

/**
 * 对象类型Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmObjectTypeServiceImpl
    extends ServiceImpl<OmObjectTypeMapper, OmObjectTypeDO>
    implements IOmObjectTypeService {

    @Resource
    private ConstraintReferenceGuard constraintReferenceGuard;

    @Resource
    private OmObjectTypeMapper omObjectTypeMapper;

    @Resource
    private OmObjectTypeAttributeMapper attributeMapper;

    @Resource
    private OmObjectTypeRelationMapper relationMapper;

    @Resource
    private IOmObjectTypeTargetService objectTypeTargetService;

    @Override
    public PageResult<OmObjectTypeDO> getOmObjectTypePage(OmObjectTypePageReqVO pageReqVO) {
        if (pageReqVO.getVersionId() == null || pageReqVO.getVersionId() <= 0) {
            throw new ServiceException("请选择项目版本后再查询", 601);
        }
        pageReqVO.setName(pageReqVO.getName() == null ? null : pageReqVO.getName().trim());
        List<Long> linkedIds = null;
        if (pageReqVO.getTargetIdList() != null && !pageReqVO.getTargetIdList().isEmpty()) {
            linkedIds = objectTypeTargetService
                .list(
                    new QueryWrapper<OmObjectTypeTargetDO>()
                        .select("object_type_id")
                        .eq("valid_flag", true)
                        .in("target_id", pageReqVO.getTargetIdList())
                )
                .stream()
                .map(OmObjectTypeTargetDO::getObjectTypeId)
                .distinct()
                .collect(Collectors.toList());
            if (linkedIds.isEmpty()) return PageResult.empty();
        }
        // BaseMapperX 对 pageSize=-1 直接调用 selectList，普通请求才调用 selectPage
        return omObjectTypeMapper.selectPageWithLinkedIds(pageReqVO, linkedIds);
    }

    @Override
    public Long createOmObjectType(OmObjectTypeSaveReqVO createReqVO) {
        OmObjectTypeDO objectType = BeanUtils.toBean(createReqVO, OmObjectTypeDO.class);
        objectType.setId(null);
        validateNameUnique(objectType);
        prepareTree(objectType);
        omObjectTypeMapper.insert(objectType);
        objectTypeTargetService.saveTargets(objectType, createReqVO.getTargetIds());
        return objectType.getId();
    }

    @Override
    public int updateOmObjectType(OmObjectTypeSaveReqVO updateReqVO) {
        OmObjectTypeDO existing = omObjectTypeMapper.selectById(updateReqVO.getId());
        if (existing == null) {
            throw new ServiceException("对象类型不存在", 601);
        }
        if (
            (existing.getParentId() == null || existing.getParentId() == 0L) &&
            updateReqVO.getParentId() != null &&
            updateReqVO.getParentId() != 0L
        ) {
            throw new ServiceException("顶级对象类型不能修改上级对象类型", 601);
        }
        OmObjectTypeDO updateObj = BeanUtils.toBean(updateReqVO, OmObjectTypeDO.class);
        updateObj.setWorkspaceId(existing.getWorkspaceId());
        updateObj.setVersionId(existing.getVersionId());
        validateNameUnique(updateObj);
        prepareTree(updateObj);
        // 包括移到顶级：移动节点时同步更新全部后代的祖级路径
        String oldPrefix = existing.getAncestors() + "," + existing.getId();
        String newPrefix = updateObj.getAncestors() + "," + existing.getId();
        for (OmObjectTypeDO child : scopedTypes(existing)) {
            String path = child.getAncestors();
            if (path != null && (path.equals(oldPrefix) || path.startsWith(oldPrefix + ","))) {
                String newPath = newPrefix + path.substring(oldPrefix.length());
                checkPathLength(newPath);
                OmObjectTypeDO change = new OmObjectTypeDO();
                change.setId(child.getId());
                change.setAncestors(newPath);
                omObjectTypeMapper.updateById(change);
            }
        }
        int rows = omObjectTypeMapper.updateById(updateObj);
        objectTypeTargetService.saveTargets(updateObj, updateReqVO.getTargetIds());
        return rows;
    }

    @Override
    public int removeOmObjectType(Long id) {
        if (omObjectTypeMapper.selectById(id) == null) {
            throw new ServiceException("对象类型不存在", 601);
        }
        if (omObjectTypeMapper.selectCount(new QueryWrapper<OmObjectTypeDO>().eq("parent_id", id)) > 0) {
            throw new ServiceException("存在下级对象类型，请先删除下级", 601);
        }
        if (attributeMapper.selectCount(new QueryWrapper<OmObjectTypeAttributeDO>().eq("object_type_id", id)) > 0) {
            throw new ServiceException("存在对象类型属性，请先删除属性", 601);
        }
        if (
            relationMapper.selectCount(
                new QueryWrapper<OmObjectTypeRelationDO>().and(query ->
                    query.eq("start_type_id", id).or().eq("end_type_id", id)
                )
            ) >
            0
        ) {
            throw new ServiceException("对象类型已被关系引用，请先删除相关关系", 601);
        }
        constraintReferenceGuard.checkObjectTypes(java.util.Collections.singleton(id));
        objectTypeTargetService.removeByObjectType(id);
        return omObjectTypeMapper.deleteById(id);
    }

    /**
     * 构造当前工作区和版本范围内的对象类型查询条件
     *
     * @param type 对象类型信息
     * @return 查询结果集合
     */
    private List<OmObjectTypeDO> scopedTypes(OmObjectTypeDO type) {
        return omObjectTypeMapper.selectList(
            new QueryWrapper<OmObjectTypeDO>()
                .eq("workspace_id", type.getWorkspaceId())
                .eq(type.getVersionId() != null, "version_id", type.getVersionId())
                .isNull(type.getVersionId() == null, "version_id")
                .orderByAsc("order_num", "id")
        );
    }

    /**
     * 校验上级对象类型并准备节点的祖级路径
     *
     * @param type 对象类型信息
     */
    private void prepareTree(OmObjectTypeDO type) {
        if (type.getName() == null || type.getName().trim().isEmpty() || type.getName().length() > 128) {
            throw new ServiceException("名称不能为空，长度不能超过128个字符", 601);
        }
        if (type.getDescription() == null) {
            type.setDescription("");
        }
        if (type.getDescription().length() > 512) {
            throw new ServiceException("描述长度不能超过512个字符", 601);
        }
        Long parentId = type.getParentId() == null ? 0L : type.getParentId();
        type.setParentId(parentId);
        if (type.getOrderNum() == null) {
            type.setOrderNum(0L);
        }
        if (type.getOrderNum() < 0 || type.getOrderNum() > Integer.MAX_VALUE) {
            throw new ServiceException("显示顺序须为有效的非负整数", 601);
        }
        if (parentId == 0L) {
            type.setAncestors("0");
        } else {
            OmObjectTypeDO parent = omObjectTypeMapper.selectById(parentId);
            if (
                parent == null ||
                !Objects.equals(parent.getWorkspaceId(), type.getWorkspaceId()) ||
                !Objects.equals(parent.getVersionId(), type.getVersionId())
            ) {
                throw new ServiceException("上级对象类型必须属于当前项目版本", 601);
            }
            if (parent.getAncestors() == null) {
                throw new ServiceException("父级祖级路径缺失，请先修复父级数据", 601);
            }
            if (
                Objects.equals(type.getId(), parentId) ||
                Arrays.asList(parent.getAncestors().split(",")).contains(String.valueOf(type.getId()))
            ) {
                throw new ServiceException("不能将自身或下级设为父级", 601);
            }
            type.setAncestors(parent.getAncestors() + "," + parentId);
        }
        checkPathLength(type.getAncestors());
    }

    /**
     * 校验对象类型祖级路径的长度
     *
     * @param path 对象类型祖级 ID 路径
     */
    private void checkPathLength(String path) {
        if (path.length() > 128) {
            throw new ServiceException("对象类型层级过深，祖级路径不能超过128个字符", 601);
        }
    }

    @Override
    public OmObjectTypeDO getOmObjectTypeById(Long id) {
        return omObjectTypeMapper.selectById(id);
    }

    @Override
    public List<OmObjectTypeDO> getOmObjectTypeList() {
        return omObjectTypeMapper.selectList();
    }

    @Override
    public List<OmObjectTypeDO> getOmObjectTypeListExcludeChild(Long id) {
        OmObjectTypeDO current = omObjectTypeMapper.selectById(id);
        if (current == null) {
            throw new ServiceException("对象类型不存在", 601);
        }
        List<OmObjectTypeDO> types = scopedTypes(current);
        types.removeIf(
            type ->
                Objects.equals(type.getId(), id) ||
                (type.getAncestors() != null &&
                    Arrays.asList(type.getAncestors().split(",")).contains(String.valueOf(id)))
        );
        return types;
    }

    @Override
    public Map<Long, OmObjectTypeDO> getOmObjectTypeMap() {
        List<OmObjectTypeDO> omObjectTypeList = omObjectTypeMapper.selectList();
        return // 保留已存在的值
        omObjectTypeList
            .stream()
            .// 保留已存在的值
            collect(
                // 保留已存在的值
                Collectors
                    // 保留已存在的值
                    .toMap(OmObjectTypeDO::getId, omObjectTypeDO -> omObjectTypeDO, (existing, replacement) -> existing)
            );
    }

    /**
     * 导入对象类型数据
     *
     * @param importExcelList 对象类型数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importOmObjectType(
        List<OmObjectTypeRespVO> importExcelList,
        boolean isUpdateSupport,
        String operName
    ) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！", 601);
        }
        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();
        for (OmObjectTypeRespVO respVO : importExcelList) {
            try {
                Long omObjectTypeId = respVO.getId();
                if (isUpdateSupport) {
                    if (omObjectTypeId != null) {
                        OmObjectTypeDO existingOmObjectType = omObjectTypeMapper.selectById(omObjectTypeId);
                        if (existingOmObjectType != null) {
                            updateOmObjectType(BeanUtils.toBean(respVO, OmObjectTypeSaveReqVO.class));
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + omObjectTypeId + " 的对象类型记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + omObjectTypeId + " 的对象类型记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<OmObjectTypeDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", omObjectTypeId);
                    OmObjectTypeDO existingOmObjectType = omObjectTypeMapper.selectOne(queryWrapper);
                    if (existingOmObjectType == null) {
                        createOmObjectType(BeanUtils.toBean(respVO, OmObjectTypeSaveReqVO.class));
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + omObjectTypeId + " 的对象类型记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + omObjectTypeId + " 的对象类型记录已存在。");
                    }
                }
            } catch (Exception e) {
                failureNum++;
                String errorMsg = "数据导入失败，错误信息：" + e.getMessage();
                failureMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        if (failureNum > 0) {
            resultMsg.append("很抱歉，导入失败！共 ").append(failureNum).append(" 条数据格式不正确，错误如下：");
            resultMsg.append("<br/>").append(String.join("<br/>", failureMessages));
            throw new ServiceException(resultMsg.toString(), 601);
        } else {
            resultMsg.append("恭喜您，数据已全部导入成功！共 ").append(successNum).append(" 条。");
        }
        return resultMsg.toString();
    }

    /**
     * 校验同一项目版本内的对象类型名称唯一性。
     *
     * <p>名称去除首尾空格后保存；修改时排除自身，逻辑删除记录不参与判重。</p>
     *
     * @param record 待新增或修改的对象类型，包含所属版本及自身 ID
     * @throws ServiceException 版本无效、名称为空或同版本存在同名记录时抛出业务警告
     */
    private void validateNameUnique(OmObjectTypeDO record) {
        if (record.getVersionId() == null || record.getVersionId() <= 0) throw new ServiceException("请选择项目版本", 601);
        String name = record.getName() == null ? "" : record.getName().trim();
        if (name.isEmpty()) throw new ServiceException("对象类型名称不能为空", 601);
        record.setName(name);
        QueryWrapper<OmObjectTypeDO> query = new QueryWrapper<OmObjectTypeDO>()
            .eq("version_id", record.getVersionId())
            .eq("name", name);
        if (record.getId() != null) query.ne("id", record.getId());
        if (omObjectTypeMapper.selectCount(query) > 0) {
            throw new ServiceException("当前项目版本已存在同名对象类型，请修改名称", 601);
        }
    }
}
