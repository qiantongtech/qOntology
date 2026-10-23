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

package tech.qiantong.qontology.module.basic.service.enumeration.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumDataDO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumTypeDO;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumDataMapper;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumTypeMapper;
import tech.qiantong.qontology.module.basic.service.enumeration.IBasicEnumTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举类型Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicEnumTypeServiceImpl extends ServiceImpl<BasicEnumTypeMapper, BasicEnumTypeDO> implements IBasicEnumTypeService {
    @Resource
    private jakarta.validation.Validator validator;
    @Resource
    private BasicEnumDataMapper basicEnumDataMapper;

    @Override
    public PageResult<BasicEnumTypeDO> getBasicEnumTypePage(BasicEnumTypePageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createBasicEnumType(BasicEnumTypeSaveReqVO createReqVO) {
        validateType(createReqVO);
        BasicEnumTypeDO dictType = BeanUtils.toBean(createReqVO, BasicEnumTypeDO.class);
        dictType.setEnumId(null);
        baseMapper.insert(dictType);
        return dictType.getEnumId();
    }

    @Override
    public int updateBasicEnumType(BasicEnumTypeSaveReqVO updateReqVO) {
        if (updateReqVO.getEnumId() == null) throw new ServiceException("枚举编号不能为空");
        BasicEnumTypeDO existing = baseMapper.selectById(updateReqVO.getEnumId());
        if (existing == null) throw new ServiceException("枚举类型不存在");
        updateReqVO.setWorkspaceId(existing.getWorkspaceId());
        validateType(updateReqVO);
        if (!existing.getEnumType().equals(updateReqVO.getEnumType()) && basicEnumDataMapper.selectCount(
                Wrappers.lambdaQuery(BasicEnumDataDO.class)
                        .eq(BasicEnumDataDO::getEnumType, existing.getEnumType())
                        .eq(existing.getWorkspaceId() != null, BasicEnumDataDO::getWorkspaceId, existing.getWorkspaceId())
                        .isNull(existing.getWorkspaceId() == null, BasicEnumDataDO::getWorkspaceId)) > 0) {
            throw new ServiceException("该枚举类型已有数据，不能修改类型编码");
        }

        // 更新枚举类型
        BasicEnumTypeDO updateObj = BeanUtils.toBean(updateReqVO, BasicEnumTypeDO.class);
        return baseMapper.updateById(updateObj);
    }

    @Override
    public int removeBasicEnumType(Collection<Long> idList) {
        for (Long id : idList) {
            BasicEnumTypeDO type = baseMapper.selectById(id);
            if (type != null && basicEnumDataMapper.selectCount(
                    Wrappers.lambdaQuery(BasicEnumDataDO.class)
                            .eq(BasicEnumDataDO::getEnumType, type.getEnumType())
                            .eq(type.getWorkspaceId() != null, BasicEnumDataDO::getWorkspaceId, type.getWorkspaceId())
                            .isNull(type.getWorkspaceId() == null, BasicEnumDataDO::getWorkspaceId)) > 0) {
                throw new ServiceException("枚举类型“" + type.getEnumName() + "”存在枚举数据，请先删除数据");
            }
        }
        // 批量删除枚举类型
        return baseMapper.deleteByIds(idList);
    }

    @Override
    public BasicEnumTypeDO getBasicEnumTypeById(Long id) {
        return baseMapper.selectById(id);
    }

    private void validateType(BasicEnumTypeSaveReqVO request) {
        if (!"0".equals(request.getStatus()) && !"1".equals(request.getStatus())) {
            throw new ServiceException("状态只能为正常或停用");
        }
        if (!request.getEnumType().matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new ServiceException("枚举类型须以字母开头，仅允许字母、数字和下划线");
        }
        LambdaQueryWrapper<BasicEnumTypeDO> query = Wrappers.lambdaQuery(BasicEnumTypeDO.class)
                .eq(BasicEnumTypeDO::getEnumType, request.getEnumType())
                .eq(BasicEnumTypeDO::getWorkspaceId, request.getWorkspaceId())
                .isNull(BasicEnumTypeDO::getWorkspaceId);

        if (request.getEnumId() != null) query.ne(BasicEnumTypeDO::getEnumId, request.getEnumId());
        if (baseMapper.selectCount(query) > 0) throw new ServiceException("枚举类型编码已存在");
    }

    @Override
    public List<BasicEnumTypeDO> getBasicEnumTypeList() {
        return baseMapper.selectList();
    }

    @Override
    public Map<Long, BasicEnumTypeDO> getBasicEnumTypeMap() {
        List<BasicEnumTypeDO> basicEnumTypeList = baseMapper.selectList();
        return basicEnumTypeList.stream()
                .collect(Collectors.toMap(
                        BasicEnumTypeDO::getEnumId,
                        basicEnumTypeDO -> basicEnumTypeDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }

    /**
     * 获得枚举类型列表
     *
     * @return 枚举类型列表
     */
    @Override
    public List<BasicEnumTypeRespVO> queryEnumTypeDict() {
        LambdaQueryWrapper<BasicEnumTypeDO> queryWrapper = Wrappers.lambdaQuery(BasicEnumTypeDO.class);
        List<BasicEnumTypeDO> list = super.list(queryWrapper);
        if (CollUtil.isEmpty(list)){
            return new ArrayList<>(0);
        }
        return list.stream()
                .map(item -> BeanUtils.toBean(item, BasicEnumTypeRespVO.class))
                .collect(Collectors.toList());
    }
}
