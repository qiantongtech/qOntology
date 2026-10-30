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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumDataMapper;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumDataService;

/**
 * 枚举数据Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmEnumDataServiceImpl extends ServiceImpl<OmEnumDataMapper, OmEnumDataDO> implements IOmEnumDataService {

    @Resource
    private OmEnumDataMapper omEnumDataMapper;

    @Resource
    private jakarta.validation.Validator validator;

    @Resource
    private tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper omEnumTypeMapper;

    @Override
    public PageResult<OmEnumDataDO> getOmEnumDataPage(OmEnumDataPageReqVO pageReqVO) {
        if (pageReqVO.getVersionId() == null || pageReqVO.getVersionId() <= 0) {
            throw new ServiceException("请选择项目版本后再查询", 601);
        }
        return omEnumDataMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createOmEnumData(OmEnumDataSaveReqVO createReqVO) {
        validateData(createReqVO);
        OmEnumDataDO dictType = BeanUtils.toBean(createReqVO, OmEnumDataDO.class);
        dictType.setEnumCode(null);
        omEnumDataMapper.insert(dictType);
        return dictType.getEnumCode();
    }

    @Override
    public int updateOmEnumData(OmEnumDataSaveReqVO updateReqVO) {
        if (updateReqVO.getEnumCode() == null) throw new ServiceException("枚举数据编号不能为空", 601);
        OmEnumDataDO existing = omEnumDataMapper.selectById(updateReqVO.getEnumCode());
        if (existing == null) throw new ServiceException("枚举数据不存在", 601);
        updateReqVO.setWorkspaceId(existing.getWorkspaceId());
        updateReqVO.setVersionId(existing.getVersionId());
        if (!existing.getEnumType().equals(updateReqVO.getEnumType())) throw new ServiceException(
            "不能变更枚举数据所属类型", 601
        );
        validateData(updateReqVO);
        // 更新枚举数据
        OmEnumDataDO updateObj = BeanUtils.toBean(updateReqVO, OmEnumDataDO.class);
        return omEnumDataMapper.updateById(updateObj);
    }

    @Override
    public int removeOmEnumData(Collection<Long> idList) {
        // 批量删除枚举数据
        return omEnumDataMapper.deleteBatchIds(idList);
    }

    @Override
    public OmEnumDataDO getOmEnumDataById(Long id) {
        return omEnumDataMapper.selectById(id);
    }

    /**
     * 校验枚举数据及其所属枚举类型
     *
     * @param request 业务请求参数
     */
    private void validateData(OmEnumDataSaveReqVO request) {
        if (!"0".equals(request.getStatus()) && !"1".equals(request.getStatus())) {
            throw new ServiceException("状态只能为正常或停用", 601);
        }
        if (
            omEnumTypeMapper.selectCount(
                new QueryWrapper<tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO>()
                    .eq("enum_type", request.getEnumType())
                    .eq(request.getWorkspaceId() != null, "workspace_id", request.getWorkspaceId())
                    .isNull(request.getWorkspaceId() == null, "workspace_id")
                    .eq(request.getVersionId() != null, "version_id", request.getVersionId())
                    .isNull(request.getVersionId() == null, "version_id")
            ) ==
            0
        ) throw new ServiceException("所属枚举类型不存在", 601);
        QueryWrapper<OmEnumDataDO> query = new QueryWrapper<OmEnumDataDO>()
            .eq("enum_type", request.getEnumType())
            .eq(request.getWorkspaceId() != null, "workspace_id", request.getWorkspaceId())
            .isNull(request.getWorkspaceId() == null, "workspace_id")
            .eq(request.getVersionId() != null, "version_id", request.getVersionId())
            .isNull(request.getVersionId() == null, "version_id")
            .eq("enum_value", request.getEnumValue());
        if (request.getEnumCode() != null) query.ne("enum_code", request.getEnumCode());
        if (omEnumDataMapper.selectCount(query) > 0) throw new ServiceException("该枚举类型下的键值已存在", 601);
    }

    @Override
    public List<OmEnumDataDO> getOmEnumDataList() {
        return omEnumDataMapper.selectList();
    }

    @Override
    public Map<Long, OmEnumDataDO> getOmEnumDataMap() {
        List<OmEnumDataDO> omEnumDataList = omEnumDataMapper.selectList();
        return // 保留已存在的值
        omEnumDataList
            .stream()
            .// 保留已存在的值
            collect(
                // 保留已存在的值
                Collectors
                    // 保留已存在的值
                    .toMap(OmEnumDataDO::getEnumCode, omEnumDataDO -> omEnumDataDO, (existing, replacement) -> existing)
            );
    }

    /**
     * 导入按类型和键值匹配，复用保存校验；任一失败时事务回滚
     */
    @Override
    public String importOmEnumData(List<OmEnumDataRespVO> items, boolean updateSupport, String operName) {
        if (items == null || items.isEmpty()) throw new ServiceException("导入数据不能为空", 601);
        int count = 0;
        for (OmEnumDataRespVO item : items) {
            OmEnumDataSaveReqVO request = BeanUtils.toBean(item, OmEnumDataSaveReqVO.class);
            java.util.Set<jakarta.validation.ConstraintViolation<OmEnumDataSaveReqVO>> errors = validator.validate(
                request
            );
            if (!errors.isEmpty()) throw new ServiceException(
                "第" + (count + 1) + "条：" + errors.iterator().next().getMessage(), 601
            );
            request.setEnumCode(null);
            OmEnumDataDO existing = omEnumDataMapper.selectOne(
                new QueryWrapper<OmEnumDataDO>()
                    .eq("enum_type", request.getEnumType())
                    .eq(request.getWorkspaceId() != null, "workspace_id", request.getWorkspaceId())
                    .isNull(request.getWorkspaceId() == null, "workspace_id")
                    .eq(request.getVersionId() != null, "version_id", request.getVersionId())
                    .isNull(request.getVersionId() == null, "version_id")
                    .eq("enum_value", request.getEnumValue())
            );
            if (existing != null) {
                if (!updateSupport) throw new ServiceException(
                    "第" + (count + 1) + "条：键值已存在，请勾选更新已有数据", 601
                );
                request.setEnumCode(existing.getEnumCode());
                updateOmEnumData(request);
            } else {
                createOmEnumData(request);
            }
            count++;
        }
        return "成功导入" + count + "条记录";
    }
}
