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
import java.util.Collection;
import java.util.List;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmObjectTypeTargetDO;

/**
 * 建模目标与对象类型关联 Service 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmObjectTypeTargetService extends IService<OmObjectTypeTargetDO> {
    /** 查询可供关联的有效建模目标。 */
    PageResult<OmObjectTypeTargetRespVO> getTargetOptions(OmTargetPageReqVO request);

    /** 查询对象类型已关联的建模目标。 */
    List<OmObjectTypeTargetRespVO> getTargets(Long objectTypeId);

    /** 分页查询对象类型已关联的建模目标。 */
    PageResult<OmObjectTypeTargetRespVO> getTargetsPage(Long objectTypeId, OmTargetPageReqVO request);

    /** 根据目标编号查询供关联展示使用的目标信息及关联元素数量。 */
    List<OmObjectTypeTargetRespVO> getTargetViews(Collection<Long> targetIds);

    /** 在对象类型保存事务内按差异同步关联，保持未变更的记录。 */
    void saveTargets(OmObjectTypeDO objectType, List<Long> targetIds);

    /** 删除对象类型时清理关联。 */
    void removeByObjectType(Long objectTypeId);

    /** 查询建模目标关联的真实对象类型列表。 */
    List<OmObjectTypeDO> getObjectTypes(Long targetId);
}
