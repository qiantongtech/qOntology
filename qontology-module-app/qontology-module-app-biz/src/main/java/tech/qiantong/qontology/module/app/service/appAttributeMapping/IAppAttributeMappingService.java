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

package tech.qiantong.qontology.module.app.service.appAttributeMapping;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingRespVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appAttributeMapping.AppAttributeMappingDO;
/**
 * 属性映射Service接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface IAppAttributeMappingService extends IService<AppAttributeMappingDO> {

    /**
     * 获得属性映射分页列表
     *
     * @param pageReqVO 分页请求
     * @return 属性映射分页列表
     */
    PageResult<AppAttributeMappingDO> getAppAttributeMappingPage(AppAttributeMappingPageReqVO pageReqVO);

    /**
     * 创建属性映射
     *
     * @param createReqVO 属性映射信息
     * @return 属性映射编号
     */
    Long createAppAttributeMapping(AppAttributeMappingSaveReqVO createReqVO);

    /**
     * 更新属性映射
     *
     * @param updateReqVO 属性映射信息
     */
    int updateAppAttributeMapping(AppAttributeMappingSaveReqVO updateReqVO);

    /**
     * 删除属性映射
     *
     * @param idList 属性映射编号
     */
    int removeAppAttributeMapping(Collection<Long> idList);

    /**
     * 获得属性映射详情
     *
     * @param id 属性映射编号
     * @return 属性映射
     */
    AppAttributeMappingDO getAppAttributeMappingById(Long id);

    /**
     * 获得全部属性映射列表
     *
     * @return 属性映射列表
     */
    List<AppAttributeMappingDO> getAppAttributeMappingList();

    /**
     * 获得全部属性映射 Map
     *
     * @return 属性映射 Map
     */
    Map<Long, AppAttributeMappingDO> getAppAttributeMappingMap();


    /**
     * 导入属性映射数据
     *
     * @param importExcelList 属性映射数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importAppAttributeMapping(List<AppAttributeMappingRespVO> importExcelList, boolean isUpdateSupport, String operName);

}

