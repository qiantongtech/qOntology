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

package tech.qiantong.qontology.module.om.service.term.impl;

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
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermRespVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import tech.qiantong.qontology.module.om.dal.mapper.term.OmTermMapper;
import tech.qiantong.qontology.module.om.service.term.IOmTermService;

/**
 * 术语Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmTermServiceImpl extends ServiceImpl<OmTermMapper, OmTermDO> implements IOmTermService {

    @Resource
    private OmTermMapper omTermMapper;

    @Override
    public PageResult<OmTermDO> getOmTermPage(OmTermPageReqVO pageReqVO) {
        if (pageReqVO.getVersionId() == null || pageReqVO.getVersionId() <= 0) {
            throw new ServiceException("请选择项目版本后再查询", 601);
        }
        return omTermMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createOmTerm(OmTermSaveReqVO createReqVO) {
        OmTermDO dictType = BeanUtils.toBean(createReqVO, OmTermDO.class);
        dictType.setId(null);
        validateNameUnique(dictType);
        omTermMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateOmTerm(OmTermSaveReqVO updateReqVO) {
        OmTermDO existing = omTermMapper.selectById(updateReqVO.getId());
        if (existing == null) throw new ServiceException("术语不存在或已删除", 601);
        OmTermDO updateObj = BeanUtils.toBean(updateReqVO, OmTermDO.class);
        // 修改内容时不能将术语迁移到其他项目版本
        updateObj.setWorkspaceId(existing.getWorkspaceId());
        updateObj.setVersionId(existing.getVersionId());
        validateNameUnique(updateObj);
        return omTermMapper.updateById(updateObj);
    }

    @Override
    public int removeOmTerm(Collection<Long> idList) {
        // 批量删除术语
        return omTermMapper.deleteBatchIds(idList);
    }

    @Override
    public OmTermDO getOmTermById(Long id) {
        return omTermMapper.selectById(id);
    }

    @Override
    public List<OmTermDO> getOmTermList() {
        return omTermMapper.selectList();
    }

    @Override
    public Map<Long, OmTermDO> getOmTermMap() {
        List<OmTermDO> omTermList = omTermMapper.selectList();
        return // 保留已存在的值
        omTermList
            .stream()
            .// 保留已存在的值
            collect(
                // 保留已存在的值
                Collectors
                    // 保留已存在的值
                    .toMap(OmTermDO::getId, omTermDO -> omTermDO, (existing, replacement) -> existing)
            );
    }

    /**
     * 导入术语数据
     *
     * @param importExcelList 术语数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importOmTerm(List<OmTermRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！", 601);
        }
        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();
        for (OmTermRespVO respVO : importExcelList) {
            try {
                OmTermDO omTermDO = BeanUtils.toBean(respVO, OmTermDO.class);
                Long omTermId = respVO.getId();
                if (isUpdateSupport) {
                    if (omTermId != null) {
                        OmTermDO existingOmTerm = omTermMapper.selectById(omTermId);
                        if (existingOmTerm != null) {
                            omTermDO.setVersionId(existingOmTerm.getVersionId());
                            omTermDO.setWorkspaceId(existingOmTerm.getWorkspaceId());
                            validateNameUnique(omTermDO);
                            omTermMapper.updateById(omTermDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + omTermId + " 的术语记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + omTermId + " 的术语记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<OmTermDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", omTermId);
                    OmTermDO existingOmTerm = omTermMapper.selectOne(queryWrapper);
                    if (existingOmTerm == null) {
                        omTermDO.setId(null);
                        validateNameUnique(omTermDO);
                        omTermMapper.insert(omTermDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + omTermId + " 的术语记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + omTermId + " 的术语记录已存在。");
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
     * 校验同一项目版本内的术语名称唯一性。
     *
     * <p>名称去除首尾空格后保存；修改时排除自身，逻辑删除记录不参与判重。</p>
     *
     * @param record 待新增或修改的术语，包含所属版本及自身 ID
     * @throws ServiceException 版本无效、名称为空或同版本存在同名记录时抛出业务警告
     */
    private void validateNameUnique(OmTermDO record) {
        if (record.getVersionId() == null || record.getVersionId() <= 0) throw new ServiceException("请选择项目版本", 601);
        String name = record.getStdName() == null ? "" : record.getStdName().trim();
        if (name.isEmpty()) throw new ServiceException("术语名称不能为空", 601);
        record.setStdName(name);
        QueryWrapper<OmTermDO> query = new QueryWrapper<OmTermDO>()
            .eq("version_id", record.getVersionId())
            .eq("std_name", name);
        if (record.getId() != null) query.ne("id", record.getId());
        if (omTermMapper.selectCount(query) > 0) {
            throw new ServiceException("当前项目版本已存在同名术语，请修改名称", 601);
        }
    }
}
