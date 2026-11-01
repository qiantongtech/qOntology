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
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;
import tech.qiantong.qontology.module.app.dal.mapper.appEvent.AppEventMapper;
import tech.qiantong.qontology.module.app.service.appEvent.IAppEventEntityService;
import tech.qiantong.qontology.module.app.service.appEvent.IAppEventService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件管理Service业务层处理
 *
 * @author qknow
 * @date 2025-11-11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppEventServiceImpl extends ServiceImpl<AppEventMapper, AppEventDO> implements IAppEventService {
    @Resource
    private AppEventMapper AppEventMapper;

    @Resource
    private IAppEventEntityService iAppEventEntityService;

    @Override
    public PageResult<AppEventDO> getAppEventPage(AppEventPageReqVO pageReqVO) {
        PageResult<AppEventDO> result = AppEventMapper.selectPage(pageReqVO);
        //获取事件关联实体数据
        List<AppEventEntityDO> AppEventEntityDOList = iAppEventEntityService.getAppEventEntityList();
        Map<Long, List<AppEventEntityDO>> AppEventEntityDOMap = AppEventEntityDOList.stream().collect(Collectors.groupingBy(AppEventEntityDO::getEventId));
        result.getList().forEach(item -> {
            if (AppEventEntityDOMap.get(item.getId()) != null) {
                item.setEntityList(AppEventEntityDOMap.get(item.getId()));
            }
        });
        return result;
    }

    @Override
    public Long createAppEvent(AppEventSaveReqVO createReqVO) {
        AppEventDO dictType = BeanUtils.toBean(createReqVO, AppEventDO.class);
        AppEventMapper.insert(dictType);

        //更新事件实体关联表数据
        delAndSaveEventEntity(dictType);

        return dictType.getId();
    }

    @Override
    public int updateAppEvent(AppEventSaveReqVO updateReqVO) {
        // 更新事件管理
        AppEventDO updateObj = BeanUtils.toBean(updateReqVO, AppEventDO.class);
        int i = AppEventMapper.updateById(updateObj);

        //更新事件实体关联表数据
        delAndSaveEventEntity(updateObj);

        return i;
    }

    private void delAndSaveEventEntity(AppEventDO AppEventDO) {
        QueryWrapper<AppEventEntityDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("event_id", AppEventDO.getId());
        iAppEventEntityService.remove(queryWrapper);
        if (!AppEventDO.getEntityList().isEmpty()) {
            for (AppEventEntityDO AppEventEntityDO : AppEventDO.getEntityList()) {
                AppEventEntityDO.setEventId(AppEventDO.getId());
                AppEventEntityDO.setWorkspaceId(AppEventDO.getWorkspaceId());
                AppEventEntityDO.setCreatorId(AppEventDO.getCreatorId());
                AppEventEntityDO.setCreateBy(AppEventDO.getCreateBy());
                AppEventEntityDO.setId(null);
            }
            iAppEventEntityService.saveBatch(AppEventDO.getEntityList());
        }
    }

    @Override
    public int removeAppEvent(Collection<Long> idList) {
        // 批量删除事件管理
        return AppEventMapper.deleteBatchIds(idList);
    }

    @Override
    public AppEventDO getAppEventById(Long id) {
        AppEventDO AppEventDO = AppEventMapper.selectById(id);
        //获取事件关联实体数据
        List<AppEventEntityDO> AppEventEntityDOList = iAppEventEntityService.getAppEventEntityList();
        Map<Long, List<AppEventEntityDO>> AppEventEntityDOMap = AppEventEntityDOList.stream().collect(Collectors.groupingBy(AppEventEntityDO::getEventId));
        if (AppEventEntityDOMap.get(AppEventDO.getId()) != null) {
            AppEventDO.setEntityList(AppEventEntityDOMap.get(AppEventDO.getId()));
        }
        return AppEventDO;
    }

    @Override
    public List<AppEventDO> getAppEventList() {
        return AppEventMapper.selectList();
    }

    @Override
    public Map<Long, AppEventDO> getAppEventMap() {
        List<AppEventDO> AppEventList = AppEventMapper.selectList();
        return AppEventList.stream()
                .collect(Collectors.toMap(
                        AppEventDO::getId,
                        AppEventDO -> AppEventDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /**
     * 导入事件管理数据
     *
     * @param importExcelList 事件管理数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importAppEvent(List<AppEventRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (AppEventRespVO respVO : importExcelList) {
            try {
                AppEventDO AppEventDO = BeanUtils.toBean(respVO, AppEventDO.class);
                Long AppEventId = respVO.getId();
                if (isUpdateSupport) {
                    if (AppEventId != null) {
                        AppEventDO existingAppEvent = AppEventMapper.selectById(AppEventId);
                        if (existingAppEvent != null) {
                            AppEventMapper.updateById(AppEventDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + AppEventId + " 的事件管理记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + AppEventId + " 的事件管理记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<AppEventDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", AppEventId);
                    AppEventDO existingAppEvent = AppEventMapper.selectOne(queryWrapper);
                    if (existingAppEvent == null) {
                        AppEventMapper.insert(AppEventDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + AppEventId + " 的事件管理记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + AppEventId + " 的事件管理记录已存在。");
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

    @Override
    public List<AppEventDO> getEventDataById(Long entityId) {
        return AppEventMapper.selectAppEventDOListByEntityId(entityId);
    }

}
