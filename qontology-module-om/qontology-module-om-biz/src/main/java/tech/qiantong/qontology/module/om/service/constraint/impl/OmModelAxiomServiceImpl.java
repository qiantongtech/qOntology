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
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmModelAxiomMapper;
import tech.qiantong.qontology.module.om.service.constraint.IOmModelAxiomService;

/**
 * 模型公理Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmModelAxiomServiceImpl
    extends ServiceImpl<OmModelAxiomMapper, OmModelAxiomDO>
    implements IOmModelAxiomService {

    @Resource
    private OmModelAxiomMapper omModelAxiomMapper;

    /**
     * 获得模型公理分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 包含记录列表和总数的分页结果
     */
    @Override
    public PageResult<OmModelAxiomDO> getOmModelAxiomPage(OmModelAxiomPageReqVO pageReqVO) {
        // 未指定排序时默认按创建时间、编号降序，保证分页顺序稳定。
        if (StringUtils.isBlank(pageReqVO.getOrderByColumn())) {
            pageReqVO.setOrderByColumn("createTime,id");
            pageReqVO.setIsAsc("desc,desc");
        }
        return omModelAxiomMapper.selectPage(pageReqVO);
    }

    /**
     * 创建模型公理
     *
     * @param createReqVO 新增记录的信息
     * @return 新增记录的 ID
     */
    @Override
    public Long createOmModelAxiom(OmModelAxiomSaveReqVO createReqVO) {
        prepareRelationName(createReqVO);
        validateNameUnique(createReqVO);
        OmModelAxiomDO createObj = BeanUtils.toBean(createReqVO, OmModelAxiomDO.class);
        omModelAxiomMapper.insert(createObj);
        return createObj.getId();
    }

    /**
     * 更新模型公理
     *
     * @param updateReqVO 待修改记录的 ID 和信息
     * @return 实际受影响的记录数
     */
    @Override
    public int updateOmModelAxiom(OmModelAxiomSaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null || omModelAxiomMapper.selectById(updateReqVO.getId()) == null) {
            throw new ServiceException("模型公理不存在或已删除");
        }
        prepareRelationName(updateReqVO);
        validateNameUnique(updateReqVO);

        // 更新模型公理
        OmModelAxiomDO updateObj = BeanUtils.toBean(updateReqVO, OmModelAxiomDO.class);
        return omModelAxiomMapper.updateById(updateObj);
    }

    /**
     * Domain/Range公理使用手工定义的关系名称，清理旧的关系记录引用。
     */
    private void prepareRelationName(OmModelAxiomSaveReqVO reqVO) {
        reqVO.setRelationId(null);
        if ("DOMAIN_RANGE".equals(reqVO.getType())) {
            if (StringUtils.isBlank(reqVO.getRelationName())) {
                throw new ServiceException("关系名称不能为空");
            }
            reqVO.setRelationName(reqVO.getRelationName().trim());
        } else {
            reqVO.setRelationName(null);
        }
    }

    /**
     * 同一项目版本内，未删除记录的名称不能重复。
     */
    private void validateNameUnique(OmModelAxiomSaveReqVO reqVO) {
        QueryWrapper<OmModelAxiomDO> wrapper = new QueryWrapper<>();
        wrapper.eq("version_id", reqVO.getVersionId()).eq("name", reqVO.getName().trim());
        if (reqVO.getId() != null) {
            wrapper.ne("id", reqVO.getId());
        }
        if (omModelAxiomMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("当前版本已存在同名模型公理");
        }
        reqVO.setName(reqVO.getName().trim());
    }

    /**
     * 删除模型公理
     *
     * @param idList 待删除的记录 ID 集合
     * @return 实际受影响的记录数
     */
    @Override
    public int removeOmModelAxiom(Collection<Long> idList) {
        // 批量删除模型公理
        return omModelAxiomMapper.deleteBatchIds(idList);
    }

    /**
     * 获得模型公理详情
     *
     * @param id 记录 ID
     * @return 查询或转换后的记录信息
     */
    @Override
    public OmModelAxiomDO getOmModelAxiomById(Long id) {
        return omModelAxiomMapper.selectById(id);
    }

    /**
     * 获得全部模型公理列表
     *
     * @return 符合条件的记录列表
     */
    @Override
    public List<OmModelAxiomDO> getOmModelAxiomList() {
        return omModelAxiomMapper.selectList();
    }

    /**
     * 获得全部模型公理 Map
     *
     * @return 以记录 ID 为键、记录信息为值的映射
     */
    @Override
    public Map<Long, OmModelAxiomDO> getOmModelAxiomMap() {
        List<OmModelAxiomDO> omModelAxiomList = omModelAxiomMapper.selectList();
        return omModelAxiomList
            .stream()
            .collect(
                Collectors.toMap(
                    OmModelAxiomDO::getId,
                    omModelAxiomDO -> omModelAxiomDO,
                    // 保留已存在的值
                    (existing, replacement) -> existing
                )
            );
    }

    /**
     * 导入模型公理数据
     *
     * @param importExcelList 模型公理数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 导入处理结果说明
     */
    @Override
    public String importOmModelAxiom(
        List<OmModelAxiomRespVO> importExcelList,
        boolean isUpdateSupport,
        String operName
    ) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (OmModelAxiomRespVO respVO : importExcelList) {
            try {
                OmModelAxiomDO omModelAxiomDO = BeanUtils.toBean(respVO, OmModelAxiomDO.class);
                Long omModelAxiomId = respVO.getId();
                if (isUpdateSupport) {
                    if (omModelAxiomId != null) {
                        OmModelAxiomDO existingOmModelAxiom = omModelAxiomMapper.selectById(omModelAxiomId);
                        if (existingOmModelAxiom != null) {
                            omModelAxiomMapper.updateById(omModelAxiomDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + omModelAxiomId + " 的模型公理记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + omModelAxiomId + " 的模型公理记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<OmModelAxiomDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", omModelAxiomId);
                    OmModelAxiomDO existingOmModelAxiom = omModelAxiomMapper.selectOne(queryWrapper);
                    if (existingOmModelAxiom == null) {
                        omModelAxiomMapper.insert(omModelAxiomDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + omModelAxiomId + " 的模型公理记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + omModelAxiomId + " 的模型公理记录已存在。");
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
            throw new ServiceException(resultMsg.toString());
        } else {
            resultMsg.append("恭喜您，数据已全部导入成功！共 ").append(successNum).append(" 条。");
        }
        return resultMsg.toString();
    }
}
