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

package tech.qiantong.qontology.module.basic.service.dataType.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import tech.qiantong.qontology.module.om.api.constraint.ConstraintReferenceGuard;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.dataType.BasicDataTypeDO;
import tech.qiantong.qontology.module.basic.dal.mapper.dataType.BasicDataTypeMapper;
import tech.qiantong.qontology.module.basic.service.dataType.IBasicDataTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据类型Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicDataTypeServiceImpl extends ServiceImpl<BasicDataTypeMapper, BasicDataTypeDO> implements IBasicDataTypeService {
    @Resource
    private ConstraintReferenceGuard constraintReferenceGuard;

    @Resource
    private BasicDataTypeMapper basicDataTypeMapper;

    @Override
    public PageResult<BasicDataTypeDO> getBasicDataTypePage(BasicDataTypePageReqVO pageReqVO) {
        return basicDataTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createBasicDataType(BasicDataTypeSaveReqVO createReqVO) {
        LambdaQueryWrapper<BasicDataTypeDO> queryWrapper = Wrappers.lambdaQuery(BasicDataTypeDO.class)
                .eq(BasicDataTypeDO::getName, createReqVO.getName());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException("当前已存在同名业务数据类型，请修改名称", 601);
        }
        BasicDataTypeDO dictType = BeanUtils.toBean(createReqVO, BasicDataTypeDO.class);
        basicDataTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateBasicDataType(BasicDataTypeSaveReqVO updateReqVO) {
        // 相关校验
        LambdaQueryWrapper<BasicDataTypeDO> queryWrapper = Wrappers.lambdaQuery(BasicDataTypeDO.class)
                .eq(BasicDataTypeDO::getName, updateReqVO.getName())
                .ne(BasicDataTypeDO::getId, updateReqVO.getId());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException("当前已存在同名业务数据类型，请修改名称", 601);
        }
        // 更新数据类型
        BasicDataTypeDO updateObj = BeanUtils.toBean(updateReqVO, BasicDataTypeDO.class);
        return baseMapper.updateById(updateObj);
    }

    @Override
    public int removeBasicDataType(Collection<Long> idList) {
        constraintReferenceGuard.checkBusinessTypes(idList, "GLOBAL");
        // 批量删除数据类型
        return basicDataTypeMapper.deleteBatchIds(idList);
    }

    @Override
    public BasicDataTypeDO getBasicDataTypeById(Long id) {
        return basicDataTypeMapper.selectById(id);
    }

}
