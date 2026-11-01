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

package tech.qiantong.qontology.module.app.service.appTag;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagEntityDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 实体标签Service接口
 *
 * @author qknow
 * @date 2025-11-13
 */
public interface IAppTagEntityService extends IService<AppTagEntityDO> {

    /**
     * 获得实体标签分页列表
     *
     * @param pageReqVO 分页请求
     * @return 实体标签分页列表
     */
    PageResult<AppTagEntityDO> getAppTagEntityPage(AppTagEntityPageReqVO pageReqVO);

    /**
     * 创建实体标签
     *
     * @param createReqVO 实体标签信息
     * @return 实体标签编号
     */
    Long createAppTagEntity(AppTagEntitySaveReqVO createReqVO);

    /**
     * 更新实体标签
     *
     * @param updateReqVO 实体标签信息
     */
    int updateAppTagEntity(AppTagEntitySaveReqVO updateReqVO);

    /**
     * 删除实体标签
     *
     * @param idList 实体标签编号
     */
    int removeAppTagEntity(Collection<Long> idList);

    /**
     * 获得实体标签详情
     *
     * @param id 实体标签编号
     * @return 实体标签
     */
    AppTagEntityDO getAppTagEntityById(Long id);

    /**
     * 获得全部实体标签列表
     *
     * @return 实体标签列表
     */
    List<AppTagEntityDO> getAppTagEntityList();

    /**
     * 获得全部实体标签 Map
     *
     * @return 实体标签 Map
     */
    Map<Long, AppTagEntityDO> getAppTagEntityMap();


    /**
     * 导入实体标签数据
     *
     * @param importExcelList 实体标签数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importAppTagEntity(List<AppTagEntityRespVO> importExcelList, boolean isUpdateSupport, String operName);

    List<AppTagDO> getAppEntityTagPage(AppTagEntityPageReqVO reqVO);

    Boolean updateAppEntityTag(AppTagEntitySaveReqVO reqVO);

    Integer deleteTagEntity(AppTagEntitySaveReqVO reqVO);
}
