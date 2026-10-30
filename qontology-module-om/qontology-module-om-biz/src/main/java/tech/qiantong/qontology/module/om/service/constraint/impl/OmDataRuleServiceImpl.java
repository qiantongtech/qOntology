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

package tech.qiantong.qontology.module.om.service.constraint.impl;

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
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmDataRuleMapper;
import tech.qiantong.qontology.module.om.service.constraint.IOmDataRuleService;

/**
 * 数据规则Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmDataRuleServiceImpl extends ServiceImpl<OmDataRuleMapper, OmDataRuleDO> implements IOmDataRuleService {

    @Resource
    private OmDataRuleMapper omDataRuleMapper;

    /**
     * 获得数据规则分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 包含记录列表和总数的分页结果
     */
    @Override
    public PageResult<OmDataRuleDO> getOmDataRulePage(OmDataRulePageReqVO pageReqVO) {
        // 未指定排序时默认按创建时间、编号降序，保证分页顺序稳定。
        if (StringUtils.isBlank(pageReqVO.getOrderByColumn())) {
            pageReqVO.setOrderByColumn("createTime,id");
            pageReqVO.setIsAsc("desc,desc");
        }
        return omDataRuleMapper.selectPage(pageReqVO);
    }

    /**
     * 创建数据规则
     *
     * @param createReqVO 新增记录的信息
     * @return 新增记录的 ID
     */
    @Override
    public Long createOmDataRule(OmDataRuleSaveReqVO createReqVO) {
        validateNameUnique(createReqVO);
        OmDataRuleDO createObj = BeanUtils.toBean(createReqVO, OmDataRuleDO.class);
        omDataRuleMapper.insert(createObj);
        return createObj.getId();
    }

    /**
     * 更新数据规则
     *
     * @param updateReqVO 待修改记录的 ID 和信息
     * @return 实际受影响的记录数
     */
    @Override
    public int updateOmDataRule(OmDataRuleSaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null || omDataRuleMapper.selectById(updateReqVO.getId()) == null) {
            throw new ServiceException("数据规则不存在或已删除", 601);
        }
        validateNameUnique(updateReqVO);

        // 更新数据规则
        OmDataRuleDO updateObj = BeanUtils.toBean(updateReqVO, OmDataRuleDO.class);
        return omDataRuleMapper.updateById(updateObj);
    }

    /**
     * 同一项目版本内，未删除记录的名称不能重复。
     */
    private void validateNameUnique(OmDataRuleSaveReqVO reqVO) {
        QueryWrapper<OmDataRuleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("version_id", reqVO.getVersionId()).eq("name", reqVO.getName().trim());
        if (reqVO.getId() != null) {
            wrapper.ne("id", reqVO.getId());
        }
        if (omDataRuleMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("当前版本已存在同名数据规则", 601);
        }
        reqVO.setName(reqVO.getName().trim());
    }

    /**
     * 删除数据规则
     *
     * @param idList 待删除的记录 ID 集合
     * @return 实际受影响的记录数
     */
    @Override
    public int removeOmDataRule(Collection<Long> idList) {
        // 批量删除数据规则
        return omDataRuleMapper.deleteBatchIds(idList);
    }

    /**
     * 获得数据规则详情
     *
     * @param id 记录 ID
     * @return 查询或转换后的记录信息
     */
    @Override
    public OmDataRuleDO getOmDataRuleById(Long id) {
        return omDataRuleMapper.selectById(id);
    }

    /**
     * 获得全部数据规则列表
     *
     * @return 符合条件的记录列表
     */
    @Override
    public List<OmDataRuleDO> getOmDataRuleList() {
        return omDataRuleMapper.selectList();
    }

    /**
     * 获得全部数据规则 Map
     *
     * @return 以记录 ID 为键、记录信息为值的映射
     */
    @Override
    public Map<Long, OmDataRuleDO> getOmDataRuleMap() {
        List<OmDataRuleDO> omDataRuleList = omDataRuleMapper.selectList();
        return omDataRuleList
            .stream()
            .collect(
                Collectors.toMap(
                    OmDataRuleDO::getId,
                    omDataRuleDO -> omDataRuleDO,
                    // 保留已存在的值
                    (existing, replacement) -> existing
                )
            );
    }

    /**
     * 导入数据规则数据
     *
     * @param importExcelList 数据规则数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 导入处理结果说明
     */
    @Override
    public String importOmDataRule(List<OmDataRuleRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！", 601);
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (OmDataRuleRespVO respVO : importExcelList) {
            try {
                OmDataRuleDO omDataRuleDO = BeanUtils.toBean(respVO, OmDataRuleDO.class);
                Long omDataRuleId = respVO.getId();
                if (isUpdateSupport) {
                    if (omDataRuleId != null) {
                        OmDataRuleDO existingOmDataRule = omDataRuleMapper.selectById(omDataRuleId);
                        if (existingOmDataRule != null) {
                            omDataRuleMapper.updateById(omDataRuleDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + omDataRuleId + " 的数据规则记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + omDataRuleId + " 的数据规则记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<OmDataRuleDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", omDataRuleId);
                    OmDataRuleDO existingOmDataRule = omDataRuleMapper.selectOne(queryWrapper);
                    if (existingOmDataRule == null) {
                        omDataRuleMapper.insert(omDataRuleDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + omDataRuleId + " 的数据规则记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + omDataRuleId + " 的数据规则记录已存在。");
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
}
