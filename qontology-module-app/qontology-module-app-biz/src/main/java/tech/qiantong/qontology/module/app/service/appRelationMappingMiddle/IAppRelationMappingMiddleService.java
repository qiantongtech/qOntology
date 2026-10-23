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

package tech.qiantong.qontology.module.app.service.appRelationMappingMiddle;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddlePageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * 关系映射中间Service接口
 *
 * @author qknow
 * @date 2025-12-16
 */
public interface IAppRelationMappingMiddleService extends IService<AppRelationMappingMiddleDO> {

    /**
     * 获得关系映射中间分页列表
     *
     * @param pageReqVO 分页请求
     * @return 关系映射中间分页列表
     */
    PageResult<AppRelationMappingMiddleDO> getAppRelationMappingMiddlePage(AppRelationMappingMiddlePageReqVO pageReqVO);

    /**
     * 创建关系映射中间
     *
     * @param createReqVO 关系映射中间信息
     * @return 关系映射中间编号
     */
    Long createAppRelationMappingMiddle(AppRelationMappingMiddleSaveReqVO createReqVO);

    /**
     * 更新关系映射中间
     *
     * @param updateReqVO 关系映射中间信息
     */
    int updateAppRelationMappingMiddle(AppRelationMappingMiddleSaveReqVO updateReqVO);

    /**
     * 删除关系映射中间
     *
     * @param idList 关系映射中间编号
     */
    int removeAppRelationMappingMiddle(Collection<Long> idList);

    /**
     * 获得关系映射中间详情
     *
     * @param id 关系映射中间编号
     * @return 关系映射中间
     */
    AppRelationMappingMiddleDO getAppRelationMappingMiddleById(Long id);

    /**
     * 获得全部关系映射中间列表
     *
     * @return 关系映射中间列表
     */
    List<AppRelationMappingMiddleDO> getAppRelationMappingMiddleList();

    /**
     * 获得全部关系映射中间 Map
     *
     * @return 关系映射中间 Map
     */
    Map<Long, AppRelationMappingMiddleDO> getAppRelationMappingMiddleMap();


    /**
     * 导入关系映射中间数据
     *
     * @param importExcelList 关系映射中间数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importAppRelationMappingMiddle(List<AppRelationMappingMiddleRespVO> importExcelList, boolean isUpdateSupport, String operName);

}

