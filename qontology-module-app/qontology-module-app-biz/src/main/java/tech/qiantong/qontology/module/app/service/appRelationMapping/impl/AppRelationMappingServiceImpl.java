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

package tech.qiantong.qontology.module.app.service.appRelationMapping.impl;

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
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;
import tech.qiantong.qontology.module.app.dal.mapper.appRelationMapping.AppRelationMappingMapper;
import tech.qiantong.qontology.module.app.service.appRelationMapping.IAppRelationMappingService;
/**
 * 关系映射Service业务层处理
 *
 * @author qknow
 * @date 2025-02-25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppRelationMappingServiceImpl  extends ServiceImpl<AppRelationMappingMapper,AppRelationMappingDO> implements IAppRelationMappingService {
    @Resource
    private AppRelationMappingMapper appRelationMappingMapper;

    /** 分页查询对象类型关系映射。 */
    @Override
    public PageResult<AppRelationMappingDO> getAppRelationMappingPage(AppRelationMappingPageReqVO pageReqVO) {
        return appRelationMappingMapper.selectPage(pageReqVO);
    }

    /** 新增对象类型关系映射。 */
    @Override
    public Long createAppRelationMapping(AppRelationMappingSaveReqVO createReqVO) {
        AppRelationMappingDO dictType = BeanUtils.toBean(createReqVO, AppRelationMappingDO.class);
        appRelationMappingMapper.insert(dictType);
        return dictType.getId();
    }

    /** 修改对象类型关系映射。 */
    @Override
    public int updateAppRelationMapping(AppRelationMappingSaveReqVO updateReqVO) {
        // 相关校验

        // 更新关系映射
        AppRelationMappingDO updateObj = BeanUtils.toBean(updateReqVO, AppRelationMappingDO.class);
        return appRelationMappingMapper.updateById(updateObj);
    }
    /** 批量删除对象类型关系映射。 */
    @Override
    public int removeAppRelationMapping(Collection<Long> idList) {
        // 批量删除关系映射
        return appRelationMappingMapper.deleteBatchIds(idList);
    }

    /** 根据编号查询对象类型关系映射。 */
    @Override
    public AppRelationMappingDO getAppRelationMappingById(Long id) {
        return appRelationMappingMapper.selectById(id);
    }

    /** 查询全部对象类型关系映射。 */
    @Override
    public List<AppRelationMappingDO> getAppRelationMappingList() {
        return appRelationMappingMapper.selectList();
    }

    /** 查询编号对应的对象类型关系映射映射。 */
    @Override
    public Map<Long, AppRelationMappingDO> getAppRelationMappingMap() {
        List<AppRelationMappingDO> appRelationMappingList = appRelationMappingMapper.selectList();
        return appRelationMappingList.stream()
                .collect(Collectors.toMap(
                        AppRelationMappingDO::getId,
                        appRelationMappingDO -> appRelationMappingDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


        /**
         * 导入关系映射数据
         *
         * @param importExcelList 关系映射数据列表
         * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
         * @param operName 操作用户
         * @return 结果
         */
        @Override
        public String importAppRelationMapping(List<AppRelationMappingRespVO> importExcelList, boolean isUpdateSupport, String operName) {
            if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
                throw new ServiceException("导入数据不能为空！");
            }

            int successNum = 0;
            int failureNum = 0;
            List<String> successMessages = new ArrayList<>();
            List<String> failureMessages = new ArrayList<>();

            for (AppRelationMappingRespVO respVO : importExcelList) {
                try {
                    AppRelationMappingDO appRelationMappingDO = BeanUtils.toBean(respVO, AppRelationMappingDO.class);
                    Long appRelationMappingId = respVO.getId();
                    if (isUpdateSupport) {
                        if (appRelationMappingId != null) {
                            AppRelationMappingDO existingAppRelationMapping = appRelationMappingMapper.selectById(appRelationMappingId);
                            if (existingAppRelationMapping != null) {
                                appRelationMappingMapper.updateById(appRelationMappingDO);
                                successNum++;
                                successMessages.add("数据更新成功，ID为 " + appRelationMappingId + " 的关系映射记录。");
                            } else {
                                failureNum++;
                                failureMessages.add("数据更新失败，ID为 " + appRelationMappingId + " 的关系映射记录不存在。");
                            }
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，某条记录的ID不存在。");
                        }
                    } else {
                        QueryWrapper<AppRelationMappingDO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("id", appRelationMappingId);
                        AppRelationMappingDO existingAppRelationMapping = appRelationMappingMapper.selectOne(queryWrapper);
                        if (existingAppRelationMapping == null) {
                            appRelationMappingMapper.insert(appRelationMappingDO);
                            successNum++;
                            successMessages.add("数据插入成功，ID为 " + appRelationMappingId + " 的关系映射记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据插入失败，ID为 " + appRelationMappingId + " 的关系映射记录已存在。");
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

