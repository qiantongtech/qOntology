/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.service.omtarget;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 建模目标Service接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmTargetService extends IService<OmTargetDO> {

    /**
     * 获得建模目标分页列表
     *
     * @param pageReqVO 分页请求
     * @return 建模目标分页列表
     */
    PageResult<OmTargetRespVO> getOmTargetPage(OmTargetPageReqVO pageReqVO);

    /**
     * 创建建模目标
     *
     * @param createReqVO 建模目标信息
     * @return 建模目标编号
     */
    Long createOmTarget(OmTargetSaveReqVO createReqVO);

    /**
     * 更新建模目标
     *
     * @param updateReqVO 建模目标信息
     */
    int updateOmTarget(OmTargetSaveReqVO updateReqVO);

    /**
     * 删除建模目标
     *
     * @param idList 建模目标编号
     */
    int removeOmTarget(Collection<Long> idList);

    /**
     * 获得建模目标详情
     *
     * @param id 建模目标编号
     * @return 建模目标
     */
    OmTargetRespVO getOmTargetById(Long id);

    /**
     * 获得全部建模目标列表
     *
     * @return 建模目标列表
     */
    List<OmTargetDO> getOmTargetList();

    /**
     * 获得全部建模目标 Map
     *
     * @return 建模目标 Map
     */
    Map<Long, OmTargetDO> getOmTargetMap();

}
