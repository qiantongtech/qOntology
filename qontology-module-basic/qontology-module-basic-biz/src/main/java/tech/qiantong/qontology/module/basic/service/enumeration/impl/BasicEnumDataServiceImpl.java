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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumDataDO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumTypeDO;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumDataMapper;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumTypeMapper;
import tech.qiantong.qontology.module.basic.service.enumeration.IBasicEnumDataService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举数据Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicEnumDataServiceImpl extends ServiceImpl<BasicEnumDataMapper, BasicEnumDataDO> implements IBasicEnumDataService {

    @Resource
    private BasicEnumTypeMapper basicEnumTypeMapper;

    @Override
    public PageResult<BasicEnumDataDO> getBasicEnumDataPage(BasicEnumDataPageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createBasicEnumData(BasicEnumDataSaveReqVO createReqVO) {
        validateData(createReqVO);
        BasicEnumDataDO dictType = BeanUtils.toBean(createReqVO, BasicEnumDataDO.class);
        dictType.setEnumCode(null);
        baseMapper.insert(dictType);
        return dictType.getEnumCode();
    }

    @Override
    public int updateBasicEnumData(BasicEnumDataSaveReqVO updateReqVO) {
        if (updateReqVO.getEnumCode() == null) throw new ServiceException("枚举数据编号不能为空");
        BasicEnumDataDO existing = baseMapper.selectById(updateReqVO.getEnumCode());
        if (existing == null) throw new ServiceException("枚举数据不存在");
        updateReqVO.setWorkspaceId(existing.getWorkspaceId());
        if (!existing.getEnumType().equals(updateReqVO.getEnumType()))
            throw new ServiceException("不能变更枚举数据所属类型");
        validateData(updateReqVO);

        // 更新枚举数据
        BasicEnumDataDO updateObj = BeanUtils.toBean(updateReqVO, BasicEnumDataDO.class);
        return baseMapper.updateById(updateObj);
    }

    @Override
    public int removeBasicEnumData(Collection<Long> idList) {
        // 批量删除枚举数据
        return baseMapper.deleteBatchIds(idList);
    }

    @Override
    public BasicEnumDataDO getBasicEnumDataById(Long id) {
        return baseMapper.selectById(id);
    }

    private void validateData(BasicEnumDataSaveReqVO request) {
        if (!"0".equals(request.getStatus()) && !"1".equals(request.getStatus())) {
            throw new ServiceException("状态只能为正常或停用");
        }
        if (basicEnumTypeMapper.selectCount(Wrappers.lambdaQuery(BasicEnumTypeDO.class)
                .eq(BasicEnumTypeDO::getEnumType, request.getEnumType())
                .eq(request.getWorkspaceId() != null, BasicEnumTypeDO::getWorkspaceId, request.getWorkspaceId())
                .isNull(request.getWorkspaceId() == null, BasicEnumTypeDO::getWorkspaceId)) == 0)
            throw new ServiceException("所属枚举类型不存在");
        LambdaQueryWrapper<BasicEnumDataDO> queryWrapper = Wrappers.lambdaQuery(BasicEnumDataDO.class)
                .eq(BasicEnumDataDO::getEnumType, request.getEnumType())
                .eq(request.getWorkspaceId() != null, BasicEnumDataDO::getWorkspaceId, request.getWorkspaceId())
                .isNull(request.getWorkspaceId() == null, BasicEnumDataDO::getWorkspaceId)
                .eq(BasicEnumDataDO::getEnumValue, request.getEnumValue());
        if (request.getEnumCode() != null) queryWrapper.ne(BasicEnumDataDO::getEnumCode, request.getEnumCode());
        if (baseMapper.selectCount(queryWrapper) > 0) throw new ServiceException("该枚举类型下的键值已存在");
    }

    @Override
    public List<BasicEnumDataDO> getBasicEnumDataList() {
        return baseMapper.selectList();
    }

    @Override
    public Map<Long, BasicEnumDataDO> getBasicEnumDataMap() {
        List<BasicEnumDataDO> basicEnumDataList = baseMapper.selectList();
        return basicEnumDataList.stream()
                .collect(Collectors.toMap(
                        BasicEnumDataDO::getEnumCode,
                        basicEnumDataDO -> basicEnumDataDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }

}
