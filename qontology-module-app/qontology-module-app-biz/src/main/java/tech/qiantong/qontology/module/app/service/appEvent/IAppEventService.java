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

package tech.qiantong.qontology.module.app.service.appEvent;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 事件管理Service接口
 *
 * @author qknow
 * @date 2025-11-11
 */
public interface IAppEventService extends IService<AppEventDO> {

    /**
     * 获得事件管理分页列表
     *
     * @param pageReqVO 分页请求
     * @return 事件管理分页列表
     */
    PageResult<AppEventDO> getAppEventPage(AppEventPageReqVO pageReqVO);

    /**
     * 创建事件管理
     *
     * @param createReqVO 事件管理信息
     * @return 事件管理编号
     */
    Long createAppEvent(AppEventSaveReqVO createReqVO);

    /**
     * 更新事件管理
     *
     * @param updateReqVO 事件管理信息
     */
    int updateAppEvent(AppEventSaveReqVO updateReqVO);

    /**
     * 删除事件管理
     *
     * @param idList 事件管理编号
     */
    int removeAppEvent(Collection<Long> idList);

    /**
     * 获得事件管理详情
     *
     * @param id 事件管理编号
     * @return 事件管理
     */
    AppEventDO getAppEventById(Long id);

    /**
     * 获得全部事件管理列表
     *
     * @return 事件管理列表
     */
    List<AppEventDO> getAppEventList();

    /**
     * 获得全部事件管理 Map
     *
     * @return 事件管理 Map
     */
    Map<Long, AppEventDO> getAppEventMap();


    /**
     * 导入事件管理数据
     *
     * @param importExcelList 事件管理数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importAppEvent(List<AppEventRespVO> importExcelList, boolean isUpdateSupport, String operName);

    /**
     * 获取实体关联的事件列表数据
     *
     * @return 事件管理列表
     */
    List<AppEventDO> getEventDataById(Long entityId);


}
