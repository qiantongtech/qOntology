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

package tech.qiantong.qontology.module.app.service.appEvent.impl;

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
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;
import tech.qiantong.qontology.module.app.dal.mapper.appEvent.AppEventEntityMapper;
import tech.qiantong.qontology.module.app.service.appEvent.IAppEventEntityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件实体关联Service业务层处理
 *
 * @author qknow
 * @date 2025-11-11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppEventEntityServiceImpl extends ServiceImpl<AppEventEntityMapper, AppEventEntityDO> implements IAppEventEntityService {
    @Resource
    private AppEventEntityMapper AppEventEntityMapper;

    @Override
    public PageResult<AppEventEntityDO> getAppEventEntityPage(AppEventEntityPageReqVO pageReqVO) {
        return AppEventEntityMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createAppEventEntity(AppEventEntitySaveReqVO createReqVO) {
        AppEventEntityDO dictType = BeanUtils.toBean(createReqVO, AppEventEntityDO.class);
        AppEventEntityMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateAppEventEntity(AppEventEntitySaveReqVO updateReqVO) {
        // 相关校验

        // 更新事件实体关联
        AppEventEntityDO updateObj = BeanUtils.toBean(updateReqVO, AppEventEntityDO.class);
        return AppEventEntityMapper.updateById(updateObj);
    }

    @Override
    public int removeAppEventEntity(Collection<Long> idList) {
        // 批量删除事件实体关联
        return AppEventEntityMapper.deleteBatchIds(idList);
    }

    @Override
    public AppEventEntityDO getAppEventEntityById(Long id) {
        return AppEventEntityMapper.selectById(id);
    }

    @Override
    public List<AppEventEntityDO> getAppEventEntityList() {
        return AppEventEntityMapper.selectList();
    }

    @Override
    public Map<Long, AppEventEntityDO> getAppEventEntityMap() {
        List<AppEventEntityDO> AppEventEntityList = AppEventEntityMapper.selectList();
        return AppEventEntityList.stream()
                .collect(Collectors.toMap(
                        AppEventEntityDO::getId,
                        AppEventEntityDO -> AppEventEntityDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /**
     * 导入事件实体关联数据
     *
     * @param importExcelList 事件实体关联数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importAppEventEntity(List<AppEventEntityRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (AppEventEntityRespVO respVO : importExcelList) {
            try {
                AppEventEntityDO AppEventEntityDO = BeanUtils.toBean(respVO, AppEventEntityDO.class);
                Long AppEventEntityId = respVO.getId();
                if (isUpdateSupport) {
                    if (AppEventEntityId != null) {
                        AppEventEntityDO existingAppEventEntity = AppEventEntityMapper.selectById(AppEventEntityId);
                        if (existingAppEventEntity != null) {
                            AppEventEntityMapper.updateById(AppEventEntityDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + AppEventEntityId + " 的事件实体关联记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + AppEventEntityId + " 的事件实体关联记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<AppEventEntityDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", AppEventEntityId);
                    AppEventEntityDO existingAppEventEntity = AppEventEntityMapper.selectOne(queryWrapper);
                    if (existingAppEventEntity == null) {
                        AppEventEntityMapper.insert(AppEventEntityDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + AppEventEntityId + " 的事件实体关联记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + AppEventEntityId + " 的事件实体关联记录已存在。");
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
