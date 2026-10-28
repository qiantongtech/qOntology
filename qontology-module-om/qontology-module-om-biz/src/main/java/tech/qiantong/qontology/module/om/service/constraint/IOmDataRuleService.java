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

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
/**
 * 数据规则Service接口
 *
 * @author qOntology
 * @date 2026-10-28
 */
public interface IOmDataRuleService extends IService<OmDataRuleDO> {

    /**
     * 获得数据规则分页列表
     *
     * @param pageReqVO 分页请求
     * @return 数据规则分页列表
     */
    PageResult<OmDataRuleDO> getOmDataRulePage(OmDataRulePageReqVO pageReqVO);

    /**
     * 创建数据规则
     *
     * @param createReqVO 数据规则信息
     * @return 数据规则编号
     */
    Long createOmDataRule(OmDataRuleSaveReqVO createReqVO);

    /**
     * 更新数据规则
     *
     * @param updateReqVO 数据规则信息
     */
    int updateOmDataRule(OmDataRuleSaveReqVO updateReqVO);

    /**
     * 删除数据规则
     *
     * @param idList 数据规则编号
     */
    int removeOmDataRule(Collection<Long> idList);

    /**
     * 获得数据规则详情
     *
     * @param id 数据规则编号
     * @return 数据规则
     */
    OmDataRuleDO getOmDataRuleById(Long id);

    /**
     * 获得全部数据规则列表
     *
     * @return 数据规则列表
     */
    List<OmDataRuleDO> getOmDataRuleList();

    /**
     * 获得全部数据规则 Map
     *
     * @return 数据规则 Map
     */
    Map<Long, OmDataRuleDO> getOmDataRuleMap();


    /**
     * 导入数据规则数据
     *
     * @param importExcelList 数据规则数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importOmDataRule(List<OmDataRuleRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
