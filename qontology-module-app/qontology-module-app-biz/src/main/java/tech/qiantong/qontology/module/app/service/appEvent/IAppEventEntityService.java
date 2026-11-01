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
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 事件实体关联Service接口
 *
 * @author qknow
 * @date 2025-11-11
 */
public interface IAppEventEntityService extends IService<AppEventEntityDO> {

    /**
     * 获得事件实体关联分页列表
     *
     * @param pageReqVO 分页请求
     * @return 事件实体关联分页列表
     */
    PageResult<AppEventEntityDO> getAppEventEntityPage(AppEventEntityPageReqVO pageReqVO);

    /**
     * 创建事件实体关联
     *
     * @param createReqVO 事件实体关联信息
     * @return 事件实体关联编号
     */
    Long createAppEventEntity(AppEventEntitySaveReqVO createReqVO);

    /**
     * 更新事件实体关联
     *
     * @param updateReqVO 事件实体关联信息
     */
    int updateAppEventEntity(AppEventEntitySaveReqVO updateReqVO);

    /**
     * 删除事件实体关联
     *
     * @param idList 事件实体关联编号
     */
    int removeAppEventEntity(Collection<Long> idList);

    /**
     * 获得事件实体关联详情
     *
     * @param id 事件实体关联编号
     * @return 事件实体关联
     */
    AppEventEntityDO getAppEventEntityById(Long id);

    /**
     * 获得全部事件实体关联列表
     *
     * @return 事件实体关联列表
     */
    List<AppEventEntityDO> getAppEventEntityList();

    /**
     * 获得全部事件实体关联 Map
     *
     * @return 事件实体关联 Map
     */
    Map<Long, AppEventEntityDO> getAppEventEntityMap();


    /**
     * 导入事件实体关联数据
     *
     * @param importExcelList 事件实体关联数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importAppEventEntity(List<AppEventEntityRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
