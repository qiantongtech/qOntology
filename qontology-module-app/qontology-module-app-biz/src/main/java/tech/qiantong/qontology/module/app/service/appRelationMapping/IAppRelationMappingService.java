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

package tech.qiantong.qontology.module.app.service.appRelationMapping;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingRespVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;
/**
 * 关系映射Service接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface IAppRelationMappingService extends IService<AppRelationMappingDO> {

    /**
     * 获得关系映射分页列表
     *
     * @param pageReqVO 分页请求
     * @return 关系映射分页列表
     */
    PageResult<AppRelationMappingDO> getAppRelationMappingPage(AppRelationMappingPageReqVO pageReqVO);

    /**
     * 创建关系映射
     *
     * @param createReqVO 关系映射信息
     * @return 关系映射编号
     */
    Long createAppRelationMapping(AppRelationMappingSaveReqVO createReqVO);

    /**
     * 更新关系映射
     *
     * @param updateReqVO 关系映射信息
     */
    int updateAppRelationMapping(AppRelationMappingSaveReqVO updateReqVO);

    /**
     * 删除关系映射
     *
     * @param idList 关系映射编号
     */
    int removeAppRelationMapping(Collection<Long> idList);

    /**
     * 获得关系映射详情
     *
     * @param id 关系映射编号
     * @return 关系映射
     */
    AppRelationMappingDO getAppRelationMappingById(Long id);

    /**
     * 获得全部关系映射列表
     *
     * @return 关系映射列表
     */
    List<AppRelationMappingDO> getAppRelationMappingList();

    /**
     * 获得全部关系映射 Map
     *
     * @return 关系映射 Map
     */
    Map<Long, AppRelationMappingDO> getAppRelationMappingMap();


    /**
     * 导入关系映射数据
     *
     * @param importExcelList 关系映射数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importAppRelationMapping(List<AppRelationMappingRespVO> importExcelList, boolean isUpdateSupport, String operName);

}

