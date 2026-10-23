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

package tech.qiantong.qontology.module.app.service.appSchemaMapping;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingRespVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;
/**
 * 概念映射Service接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface IAppSchemaMappingService extends IService<AppSchemaMappingDO> {

    /**
     * 获得概念映射分页列表
     *
     * @param pageReqVO 分页请求
     * @return 概念映射分页列表
     */
    PageResult<AppSchemaMappingDO> getAppSchemaMappingPage(AppSchemaMappingPageReqVO pageReqVO);

    /**
     * 创建概念映射
     *
     * @param createReqVO 概念映射信息
     * @return 概念映射编号
     */
    Long createAppSchemaMapping(AppSchemaMappingSaveReqVO createReqVO);

    /**
     * 更新概念映射
     *
     * @param updateReqVO 概念映射信息
     */
    int updateAppSchemaMapping(AppSchemaMappingSaveReqVO updateReqVO);

    /**
     * 删除概念映射
     *
     * @param idList 概念映射编号
     */
    int removeAppSchemaMapping(Collection<Long> idList);

    /**
     * 获得概念映射详情
     *
     * @param id 概念映射编号
     * @return 概念映射
     */
    AppSchemaMappingDO getAppSchemaMappingById(Long id);

    /**
     * 获得全部概念映射列表
     *
     * @return 概念映射列表
     */
    List<AppSchemaMappingDO> getAppSchemaMappingList();

    /**
     * 获得全部概念映射 Map
     *
     * @return 概念映射 Map
     */
    Map<Long, AppSchemaMappingDO> getAppSchemaMappingMap();


    /**
     * 导入概念映射数据
     *
     * @param importExcelList 概念映射数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importAppSchemaMapping(List<AppSchemaMappingRespVO> importExcelList, boolean isUpdateSupport, String operName);

}

