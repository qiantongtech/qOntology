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

import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;

/**
 * 对象类型关系与建模目标关联 Service 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmRelationTargetService extends IService<OmRelationTargetDO> {
    /** 查询关系已关联的建模目标。 */
    List<OmObjectTypeTargetRespVO> getTargets(Long relationId, Long workspaceId);

    /** 分页查询关系已关联的建模目标。 */
    PageResult<OmObjectTypeTargetRespVO> getTargetsPage(Long relationId, Long workspaceId, OmTargetPageReqVO request);

    /** 在关系保存事务内按差异同步关联。 */
    void saveTargets(OmObjectTypeRelationDO relation, List<Long> targetIds);

    /** 删除关系时清理关联。 */
    void removeByRelations(Collection<Long> relationIds, Long workspaceId);

    /** 查询建模目标关联的真实关系列表。 */
    List<OmObjectTypeRelationDO> getRelations(Long targetId, Long workspaceId);
}
