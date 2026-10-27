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

package tech.qiantong.qontology.module.om.service.dataType.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.service.enumeration.IBasicEnumTypeApiService;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.dataType.OmDataTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper;
import tech.qiantong.qontology.module.om.enums.DataTypeEnum;
import tech.qiantong.qontology.module.om.service.dataType.IOmDataTypeService;

/**
 * 数据类型Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmDataTypeServiceImpl extends ServiceImpl<OmDataTypeMapper, OmDataTypeDO> implements IOmDataTypeService {

    @Resource
    private OmDataTypeMapper omDataTypeMapper;

    @Resource
    private OmEnumTypeMapper enumTypeMapper;

    @Resource
    private IBasicEnumTypeApiService basicEnumTypeApiService;

    @Override
    public List<OmDataTypeEnumOptionRespVO> getEnumOptions(Long versionId, String enumSource) {
        requireVersion(versionId);
        if (!"PROJECT".equals(enumSource) && !"GLOBAL".equals(enumSource)) {
            throw new ServiceException("枚举来源只能为当前项目或全局");
        }
        List<OmDataTypeEnumOptionRespVO> options = new ArrayList<>();
        // 分别查询两个来源，任一来源为空或查询失败均不影响另一个来源的请求
        if ("PROJECT".equals(enumSource)) {
            enumTypeMapper
                .selectList(
                    new QueryWrapper<OmEnumTypeDO>()
                        .eq("version_id", versionId)
                        .eq("status", "0")
                        .eq("valid_flag", true)
                        .orderByAsc("enum_id")
                )
                .forEach(type ->
                    options.add(new OmDataTypeEnumOptionRespVO(type.getEnumName(), type.getEnumType(), "PROJECT"))
                );
        } else {
            basicEnumTypeApiService
                .getAvailableEnumTypes()
                .forEach(type ->
                    options.add(new OmDataTypeEnumOptionRespVO(type.getEnumName(), type.getEnumType(), "GLOBAL"))
                );
        }
        return options;
    }

    @Override
    public PageResult<OmDataTypeDO> getOmDataTypePage(OmDataTypePageReqVO pageReqVO) {
        requireVersion(pageReqVO.getVersionId());
        return omDataTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createOmDataType(OmDataTypeSaveReqVO createReqVO) {
        validateDataType(createReqVO);
        OmDataTypeDO dictType = BeanUtils.toBean(createReqVO, OmDataTypeDO.class);
        dictType.setId(null);
        omDataTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateOmDataType(OmDataTypeSaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null) throw new ServiceException("数据类型编号不能为空");
        getOmDataTypeById(updateReqVO.getId(), updateReqVO.getVersionId());
        validateDataType(updateReqVO);
        // 更新数据类型
        OmDataTypeDO updateObj = BeanUtils.toBean(updateReqVO, OmDataTypeDO.class);
        return omDataTypeMapper.updateById(updateObj);
    }

    @Override
    public int removeOmDataType(Collection<Long> idList, Long versionId) {
        requireVersion(versionId);
        for (Long id : idList) getOmDataTypeById(id, versionId);
        // 批量删除数据类型
        return omDataTypeMapper.deleteBatchIds(idList);
    }

    @Override
    public OmDataTypeDO getOmDataTypeById(Long id, Long versionId) {
        requireVersion(versionId);
        OmDataTypeDO record = omDataTypeMapper.selectById(id);
        if (record == null || !versionId.equals(record.getVersionId())) {
            throw new ServiceException("当前版本下数据类型不存在");
        }
        return record;
    }

    /**
     * 校验项目业务数据类型及其枚举来源，清理不适用的枚举配置
     *
     * @param request 业务请求参数
     */
    private void validateDataType(OmDataTypeSaveReqVO request) {
        requireVersion(request.getVersionId());
        if (request.getDataType() != DataTypeEnum.ENUM) {
            request.setEnumType(null);
            request.setEnumSource("PROJECT");
            return;
        }
        if (request.getEnumType() == null || request.getEnumType().isBlank()) {
            request.setEnumType(null);
            request.setEnumSource("PROJECT");
            return;
        }
        request.setEnumType(request.getEnumType().trim());
        String source = request.getEnumSource() == null ? "PROJECT" : request.getEnumSource();
        request.setEnumSource(source);
        if (!"PROJECT".equals(source) && !"GLOBAL".equals(source)) {
            throw new ServiceException("枚举来源只能为当前项目或全局");
        }
        boolean available =
            request.getEnumType() != null &&
            ("GLOBAL".equals(source)
                    ? basicEnumTypeApiService.isAvailableEnumType(request.getEnumType())
                    : enumTypeMapper.selectCount(
                        new QueryWrapper<OmEnumTypeDO>()
                            .eq("version_id", request.getVersionId())
                            .eq("enum_type", request.getEnumType())
                            .eq("status", "0")
                            .eq("valid_flag", true)
                    ) >
                    0);
        if (!available) throw new ServiceException("请选择所选来源下的有效枚举类型");
    }

    /**
     * 校验项目版本 ID 为正数
     *
     * @param versionId 项目版本 ID
     */
    private void requireVersion(Long versionId) {
        if (versionId == null || versionId <= 0) throw new ServiceException("请选择项目版本");
    }
}
