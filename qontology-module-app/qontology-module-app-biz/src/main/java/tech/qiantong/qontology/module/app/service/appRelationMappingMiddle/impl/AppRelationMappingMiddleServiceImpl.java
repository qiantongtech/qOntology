/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.service.appRelationMappingMiddle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddlePageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;
import tech.qiantong.qontology.module.app.dal.mapper.appRelationMappingMiddle.AppRelationMappingMiddleMapper;
import tech.qiantong.qontology.module.app.service.appRelationMappingMiddle.IAppRelationMappingMiddleService;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 关系映射中间Service业务层处理
 *
 * @author qknow
 * @date 2025-12-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppRelationMappingMiddleServiceImpl  extends ServiceImpl<AppRelationMappingMiddleMapper,AppRelationMappingMiddleDO> implements IAppRelationMappingMiddleService {
    @Resource
    private AppRelationMappingMiddleMapper appRelationMappingMiddleMapper;

    /** 分页查询关系映射中间表。 */
    @Override
    public PageResult<AppRelationMappingMiddleDO> getAppRelationMappingMiddlePage(AppRelationMappingMiddlePageReqVO pageReqVO) {
        return appRelationMappingMiddleMapper.selectPage(pageReqVO);
    }

    /** 新增关系映射中间表。 */
    @Override
    public Long createAppRelationMappingMiddle(AppRelationMappingMiddleSaveReqVO createReqVO) {
        AppRelationMappingMiddleDO dictType = BeanUtils.toBean(createReqVO, AppRelationMappingMiddleDO.class);
        appRelationMappingMiddleMapper.insert(dictType);
        return dictType.getId();
    }

    /** 修改关系映射中间表。 */
    @Override
    public int updateAppRelationMappingMiddle(AppRelationMappingMiddleSaveReqVO updateReqVO) {
        // 相关校验

        // 更新关系映射中间
        AppRelationMappingMiddleDO updateObj = BeanUtils.toBean(updateReqVO, AppRelationMappingMiddleDO.class);
        return appRelationMappingMiddleMapper.updateById(updateObj);
    }
    /** 批量删除关系映射中间表。 */
    @Override
    public int removeAppRelationMappingMiddle(Collection<Long> idList) {
        // 批量删除关系映射中间
        return appRelationMappingMiddleMapper.deleteBatchIds(idList);
    }

    /** 根据编号查询关系映射中间表。 */
    @Override
    public AppRelationMappingMiddleDO getAppRelationMappingMiddleById(Long id) {
        return appRelationMappingMiddleMapper.selectById(id);
    }

    /** 查询全部关系映射中间表。 */
    @Override
    public List<AppRelationMappingMiddleDO> getAppRelationMappingMiddleList() {
        return appRelationMappingMiddleMapper.selectList();
    }

    /** 查询编号对应的关系映射中间表映射。 */
    @Override
    public Map<Long, AppRelationMappingMiddleDO> getAppRelationMappingMiddleMap() {
        List<AppRelationMappingMiddleDO> appRelationMappingMiddleList = appRelationMappingMiddleMapper.selectList();
        return appRelationMappingMiddleList.stream()
                .collect(Collectors.toMap(
                        AppRelationMappingMiddleDO::getId,
                        appRelationMappingMiddleDO -> appRelationMappingMiddleDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


        /**
         * 导入关系映射中间数据
         *
         * @param importExcelList 关系映射中间数据列表
         * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
         * @param operName 操作用户
         * @return 结果
         */
        @Override
        public String importAppRelationMappingMiddle(List<AppRelationMappingMiddleRespVO> importExcelList, boolean isUpdateSupport, String operName) {
            if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
                throw new ServiceException("导入数据不能为空！");
            }

            int successNum = 0;
            int failureNum = 0;
            List<String> successMessages = new ArrayList<>();
            List<String> failureMessages = new ArrayList<>();

            for (AppRelationMappingMiddleRespVO respVO : importExcelList) {
                try {
                    AppRelationMappingMiddleDO appRelationMappingMiddleDO = BeanUtils.toBean(respVO, AppRelationMappingMiddleDO.class);
                    Long appRelationMappingMiddleId = respVO.getId();
                    if (isUpdateSupport) {
                        if (appRelationMappingMiddleId != null) {
                            AppRelationMappingMiddleDO existingAppRelationMappingMiddle = appRelationMappingMiddleMapper.selectById(appRelationMappingMiddleId);
                            if (existingAppRelationMappingMiddle != null) {
                                appRelationMappingMiddleMapper.updateById(appRelationMappingMiddleDO);
                                successNum++;
                                successMessages.add("数据更新成功，ID为 " + appRelationMappingMiddleId + " 的关系映射中间记录。");
                            } else {
                                failureNum++;
                                failureMessages.add("数据更新失败，ID为 " + appRelationMappingMiddleId + " 的关系映射中间记录不存在。");
                            }
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，某条记录的ID不存在。");
                        }
                    } else {
                        QueryWrapper<AppRelationMappingMiddleDO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("id", appRelationMappingMiddleId);
                        AppRelationMappingMiddleDO existingAppRelationMappingMiddle = appRelationMappingMiddleMapper.selectOne(queryWrapper);
                        if (existingAppRelationMappingMiddle == null) {
                            appRelationMappingMiddleMapper.insert(appRelationMappingMiddleDO);
                            successNum++;
                            successMessages.add("数据插入成功，ID为 " + appRelationMappingMiddleId + " 的关系映射中间记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据插入失败，ID为 " + appRelationMappingMiddleId + " 的关系映射中间记录已存在。");
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

