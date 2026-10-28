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

package tech.qiantong.qontology.module.om.service.constraint;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;

/**
 * 模型公理Service接口
 *
 * @author qOntology
 * @date 2026-10-28
 */
public interface IOmModelAxiomService extends IService<OmModelAxiomDO> {
    /**
     * 获得模型公理分页列表
     *
     * @param pageReqVO 分页请求
     * @return 模型公理分页列表
     */
    PageResult<OmModelAxiomDO> getOmModelAxiomPage(OmModelAxiomPageReqVO pageReqVO);

    /**
     * 创建模型公理
     *
     * @param createReqVO 模型公理信息
     * @return 模型公理编号
     */
    Long createOmModelAxiom(OmModelAxiomSaveReqVO createReqVO);

    /**
     * 更新模型公理
     *
     * @param updateReqVO 模型公理信息
     * @return 实际受影响的记录数
     */
    int updateOmModelAxiom(OmModelAxiomSaveReqVO updateReqVO);

    /**
     * 删除模型公理
     *
     * @param idList 模型公理编号
     * @return 实际受影响的记录数
     */
    int removeOmModelAxiom(Collection<Long> idList);

    /**
     * 获得模型公理详情
     *
     * @param id 模型公理编号
     * @return 模型公理
     */
    OmModelAxiomDO getOmModelAxiomById(Long id);

    /**
     * 获得全部模型公理列表
     *
     * @return 模型公理列表
     */
    List<OmModelAxiomDO> getOmModelAxiomList();

    /**
     * 获得全部模型公理 Map
     *
     * @return 模型公理 Map
     */
    Map<Long, OmModelAxiomDO> getOmModelAxiomMap();

    /**
     * 导入模型公理数据
     *
     * @param importExcelList 模型公理数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 导入处理结果说明
     */
    String importOmModelAxiom(List<OmModelAxiomRespVO> importExcelList, boolean isUpdateSupport, String operName);
}
