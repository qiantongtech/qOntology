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

package tech.qiantong.qontology.module.app.service.appSchemaMapping.impl;

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
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;
import tech.qiantong.qontology.module.app.dal.mapper.appSchemaMapping.AppSchemaMappingMapper;
import tech.qiantong.qontology.module.app.service.appSchemaMapping.IAppSchemaMappingService;
/**
 * 概念映射Service业务层处理
 *
 * @author qknow
 * @date 2025-02-25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppSchemaMappingServiceImpl  extends ServiceImpl<AppSchemaMappingMapper,AppSchemaMappingDO> implements IAppSchemaMappingService {
    @Resource
    private AppSchemaMappingMapper appSchemaMappingMapper;

    /** 分页查询对象类型映射。 */
    @Override
    public PageResult<AppSchemaMappingDO> getAppSchemaMappingPage(AppSchemaMappingPageReqVO pageReqVO) {
        return appSchemaMappingMapper.selectPage(pageReqVO);
    }

    /** 新增对象类型映射。 */
    @Override
    public Long createAppSchemaMapping(AppSchemaMappingSaveReqVO createReqVO) {
        AppSchemaMappingDO dictType = BeanUtils.toBean(createReqVO, AppSchemaMappingDO.class);
        appSchemaMappingMapper.insert(dictType);
        return dictType.getId();
    }

    /** 修改对象类型映射。 */
    @Override
    public int updateAppSchemaMapping(AppSchemaMappingSaveReqVO updateReqVO) {
        // 相关校验

        // 更新概念映射
        AppSchemaMappingDO updateObj = BeanUtils.toBean(updateReqVO, AppSchemaMappingDO.class);
        return appSchemaMappingMapper.updateById(updateObj);
    }
    /** 批量删除对象类型映射。 */
    @Override
    public int removeAppSchemaMapping(Collection<Long> idList) {
        // 批量删除概念映射
        return appSchemaMappingMapper.deleteBatchIds(idList);
    }

    /** 根据编号查询对象类型映射。 */
    @Override
    public AppSchemaMappingDO getAppSchemaMappingById(Long id) {
        return appSchemaMappingMapper.selectById(id);
    }

    /** 查询全部对象类型映射。 */
    @Override
    public List<AppSchemaMappingDO> getAppSchemaMappingList() {
        return appSchemaMappingMapper.selectList();
    }

    /** 查询编号对应的对象类型映射映射。 */
    @Override
    public Map<Long, AppSchemaMappingDO> getAppSchemaMappingMap() {
        List<AppSchemaMappingDO> appSchemaMappingList = appSchemaMappingMapper.selectList();
        return appSchemaMappingList.stream()
                .collect(Collectors.toMap(
                        AppSchemaMappingDO::getId,
                        appSchemaMappingDO -> appSchemaMappingDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


        /**
         * 导入概念映射数据
         *
         * @param importExcelList 概念映射数据列表
         * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
         * @param operName 操作用户
         * @return 结果
         */
        @Override
        public String importAppSchemaMapping(List<AppSchemaMappingRespVO> importExcelList, boolean isUpdateSupport, String operName) {
            if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
                throw new ServiceException("导入数据不能为空！");
            }

            int successNum = 0;
            int failureNum = 0;
            List<String> successMessages = new ArrayList<>();
            List<String> failureMessages = new ArrayList<>();

            for (AppSchemaMappingRespVO respVO : importExcelList) {
                try {
                    AppSchemaMappingDO appSchemaMappingDO = BeanUtils.toBean(respVO, AppSchemaMappingDO.class);
                    Long appSchemaMappingId = respVO.getId();
                    if (isUpdateSupport) {
                        if (appSchemaMappingId != null) {
                            AppSchemaMappingDO existingAppSchemaMapping = appSchemaMappingMapper.selectById(appSchemaMappingId);
                            if (existingAppSchemaMapping != null) {
                                appSchemaMappingMapper.updateById(appSchemaMappingDO);
                                successNum++;
                                successMessages.add("数据更新成功，ID为 " + appSchemaMappingId + " 的概念映射记录。");
                            } else {
                                failureNum++;
                                failureMessages.add("数据更新失败，ID为 " + appSchemaMappingId + " 的概念映射记录不存在。");
                            }
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，某条记录的ID不存在。");
                        }
                    } else {
                        QueryWrapper<AppSchemaMappingDO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("id", appSchemaMappingId);
                        AppSchemaMappingDO existingAppSchemaMapping = appSchemaMappingMapper.selectOne(queryWrapper);
                        if (existingAppSchemaMapping == null) {
                            appSchemaMappingMapper.insert(appSchemaMappingDO);
                            successNum++;
                            successMessages.add("数据插入成功，ID为 " + appSchemaMappingId + " 的概念映射记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据插入失败，ID为 " + appSchemaMappingId + " 的概念映射记录已存在。");
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

