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
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 知识标签Service接口
 *
 * @author qknow
 * @date 2025-11-13
 */
public interface IAppTagService extends IService<AppTagDO> {

    /**
     * 获得知识标签分页列表
     *
     * @param pageReqVO 分页请求
     * @return 知识标签分页列表
     */
    PageResult<AppTagDO> getAppTagPage(AppTagPageReqVO pageReqVO);

    /**
     * 创建知识标签
     *
     * @param createReqVO 知识标签信息
     * @return 知识标签编号
     */
    Long createAppTag(AppTagSaveReqVO createReqVO);

    /**
     * 更新知识标签
     *
     * @param updateReqVO 知识标签信息
     */
    int updateAppTag(AppTagSaveReqVO updateReqVO);

    /**
     * 删除知识标签
     *
     * @param idList 知识标签编号
     */
    int removeAppTag(Collection<Long> idList);

    /**
     * 获得知识标签详情
     *
     * @param id 知识标签编号
     * @return 知识标签
     */
    AppTagDO getAppTagById(Long id);

    /**
     * 获得全部知识标签列表
     *
     * @return 知识标签列表
     */
    List<AppTagDO> getAppTagList();

    /**
     * 获得全部知识标签 Map
     *
     * @return 知识标签 Map
     */
    Map<Long, AppTagDO> getAppTagMap();


    /**
     * 导入知识标签数据
     *
     * @param importExcelList 知识标签数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importAppTag(List<AppTagRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
