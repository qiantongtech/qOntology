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

package tech.qiantong.qontology.module.om.service.term;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
/**
 * 术语Service接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface IOmTermService extends IService<OmTermDO> {

    /**
     * 获得术语分页列表
     *
     * @param pageReqVO 分页请求
     * @return 术语分页列表
     */
    PageResult<OmTermDO> getOmTermPage(OmTermPageReqVO pageReqVO);

    /**
     * 创建术语
     *
     * @param createReqVO 术语信息
     * @return 术语编号
     */
    Long createOmTerm(OmTermSaveReqVO createReqVO);

    /**
     * 更新术语
     *
     * @param updateReqVO 术语信息
     */
    int updateOmTerm(OmTermSaveReqVO updateReqVO);

    /**
     * 删除术语
     *
     * @param idList 术语编号
     */
    int removeOmTerm(Collection<Long> idList);

    /**
     * 获得术语详情
     *
     * @param id 术语编号
     * @return 术语
     */
    OmTermDO getOmTermById(Long id);

    /**
     * 获得全部术语列表
     *
     * @return 术语列表
     */
    List<OmTermDO> getOmTermList();

    /**
     * 获得全部术语 Map
     *
     * @return 术语 Map
     */
    Map<Long, OmTermDO> getOmTermMap();


    /**
     * 导入术语数据
     *
     * @param importExcelList 术语数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importOmTerm(List<OmTermRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
