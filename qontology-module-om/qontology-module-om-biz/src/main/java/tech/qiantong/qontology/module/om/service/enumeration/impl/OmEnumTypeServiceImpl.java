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

package tech.qiantong.qontology.module.om.service.enumeration.impl;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.common.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumTypeService;
/**
 * 枚举类型Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmEnumTypeServiceImpl  extends ServiceImpl<OmEnumTypeMapper,OmEnumTypeDO> implements IOmEnumTypeService {
    @Resource
    private OmEnumTypeMapper omEnumTypeMapper;
    @Resource
    private jakarta.validation.Validator validator;
    @Resource
    private tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumDataMapper omEnumDataMapper;

    @Override
    public PageResult<OmEnumTypeDO> getOmEnumTypePage(OmEnumTypePageReqVO pageReqVO) {
        if (pageReqVO.getVersionId() == null || pageReqVO.getVersionId() <= 0) {
            throw new ServiceException("请选择项目版本后再查询");
        }
        return omEnumTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createOmEnumType(OmEnumTypeSaveReqVO createReqVO) {
        validateType(createReqVO);
        OmEnumTypeDO dictType = BeanUtils.toBean(createReqVO, OmEnumTypeDO.class);
        dictType.setEnumId(null);
        omEnumTypeMapper.insert(dictType);
        return dictType.getEnumId();
    }

    @Override
    public int updateOmEnumType(OmEnumTypeSaveReqVO updateReqVO) {
        if (updateReqVO.getEnumId() == null) throw new ServiceException("枚举编号不能为空");
        OmEnumTypeDO existing = omEnumTypeMapper.selectById(updateReqVO.getEnumId());
        if (existing == null) throw new ServiceException("枚举类型不存在");
        updateReqVO.setWorkspaceId(existing.getWorkspaceId());
        updateReqVO.setVersionId(existing.getVersionId());
        validateType(updateReqVO);
        if (!existing.getEnumType().equals(updateReqVO.getEnumType()) && omEnumDataMapper.selectCount(
                new QueryWrapper<tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO>()
                        .eq("enum_type", existing.getEnumType()).eq(existing.getWorkspaceId() != null, "workspace_id", existing.getWorkspaceId()).isNull(existing.getWorkspaceId() == null, "workspace_id").eq(existing.getVersionId() != null, "version_id", existing.getVersionId()).isNull(existing.getVersionId() == null, "version_id")) > 0) {
            throw new ServiceException("该枚举类型已有数据，不能修改类型编码");
        }

        // 更新枚举类型
        OmEnumTypeDO updateObj = BeanUtils.toBean(updateReqVO, OmEnumTypeDO.class);
        return omEnumTypeMapper.updateById(updateObj);
    }
    @Override
    public int removeOmEnumType(Collection<Long> idList) {
        for (Long id : idList) {
            OmEnumTypeDO type = omEnumTypeMapper.selectById(id);
            if (type != null && omEnumDataMapper.selectCount(
                    new QueryWrapper<tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO>()
                            .eq("enum_type", type.getEnumType()).eq(type.getWorkspaceId() != null, "workspace_id", type.getWorkspaceId()).isNull(type.getWorkspaceId() == null, "workspace_id").eq(type.getVersionId() != null, "version_id", type.getVersionId()).isNull(type.getVersionId() == null, "version_id")) > 0) {
                throw new ServiceException("枚举类型“" + type.getEnumName() + "”存在枚举数据，请先删除数据");
            }
        }
        // 批量删除枚举类型
        return omEnumTypeMapper.deleteBatchIds(idList);
    }

    @Override
    public OmEnumTypeDO getOmEnumTypeById(Long id) {
        return omEnumTypeMapper.selectById(id);
    }

    private void validateType(OmEnumTypeSaveReqVO request) {
        if (!"0".equals(request.getStatus()) && !"1".equals(request.getStatus())) {
            throw new ServiceException("状态只能为正常或停用");
        }
        if (!request.getEnumType().matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new ServiceException("枚举类型须以字母开头，仅允许字母、数字和下划线");
        }
        QueryWrapper<OmEnumTypeDO> query = new QueryWrapper<OmEnumTypeDO>().eq("enum_type", request.getEnumType()).eq(request.getWorkspaceId() != null, "workspace_id", request.getWorkspaceId()).isNull(request.getWorkspaceId() == null, "workspace_id").eq(request.getVersionId() != null, "version_id", request.getVersionId()).isNull(request.getVersionId() == null, "version_id");
        if (request.getEnumId() != null) query.ne("enum_id", request.getEnumId());
        if (omEnumTypeMapper.selectCount(query) > 0) throw new ServiceException("枚举类型编码已存在");
    }

    @Override
    public List<OmEnumTypeDO> getOmEnumTypeList() {
        return omEnumTypeMapper.selectList();
    }

    @Override
    public Map<Long, OmEnumTypeDO> getOmEnumTypeMap() {
        List<OmEnumTypeDO> omEnumTypeList = omEnumTypeMapper.selectList();
        return omEnumTypeList.stream()
                .collect(Collectors.toMap(
                        OmEnumTypeDO::getEnumId,
                        omEnumTypeDO -> omEnumTypeDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /** 导入按业务编码匹配，复用正常保存校验；任一失败时事务回滚。 */
    @Override
    public String importOmEnumType(List<OmEnumTypeRespVO> items, boolean updateSupport, String operName) {
        if (items == null || items.isEmpty()) throw new ServiceException("导入数据不能为空");
        int count = 0;
        for (OmEnumTypeRespVO item : items) {
            OmEnumTypeSaveReqVO request = BeanUtils.toBean(item, OmEnumTypeSaveReqVO.class);
            java.util.Set<jakarta.validation.ConstraintViolation<OmEnumTypeSaveReqVO>> errors = validator.validate(request);
            if (!errors.isEmpty()) throw new ServiceException("第" + (count + 1) + "条：" + errors.iterator().next().getMessage());
            request.setEnumId(null);
            QueryWrapper<OmEnumTypeDO> query = new QueryWrapper<OmEnumTypeDO>().eq("enum_type", request.getEnumType()).eq(request.getWorkspaceId() != null, "workspace_id", request.getWorkspaceId()).isNull(request.getWorkspaceId() == null, "workspace_id").eq(request.getVersionId() != null, "version_id", request.getVersionId()).isNull(request.getVersionId() == null, "version_id");
            OmEnumTypeDO existing = omEnumTypeMapper.selectOne(query);
            if (existing != null) {
                if (!updateSupport) throw new ServiceException("第" + (count + 1) + "条：编码已存在，请勾选更新已有数据");
                request.setEnumId(existing.getEnumId());
                updateOmEnumType(request);
            } else {
                createOmEnumType(request);
            }
            count++;
        }
        return "成功导入" + count + "条记录";
    }
}
