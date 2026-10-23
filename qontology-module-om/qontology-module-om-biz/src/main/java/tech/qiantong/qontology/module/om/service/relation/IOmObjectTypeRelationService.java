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

package tech.qiantong.qontology.module.om.service.relation;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;

/**
 * 对象类型关系 Service 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmObjectTypeRelationService extends IService<OmObjectTypeRelationDO> {
    /**
     * 获得对象类型关系分页列表
     *
     * @param request 分页请求
     * @return 对象类型关系分页列表
     */
    PageResult<OmObjectTypeRelationDO> getOmObjectTypeRelationPage(OmObjectTypeRelationPageReqVO request);

    /**
     * 获得对象类型关系详情
     *
     * @param id 关系编号
     * @param workspaceId 工作区编号
     * @return 对象类型关系
     */
    OmObjectTypeRelationDO getOmObjectTypeRelationById(Long id, Long workspaceId);

    /**
     * 创建对象类型关系
     *
     * @param request 关系信息
     * @return 关系编号
     */
    Long createOmObjectTypeRelation(OmObjectTypeRelationSaveReqVO request);

    /**
     * 更新对象类型关系
     *
     * @param request 关系信息
     * @return 影响行数
     */
    int updateOmObjectTypeRelation(OmObjectTypeRelationSaveReqVO request);

    /**
     * 删除对象类型关系
     *
     * @param idList 关系编号集合
     * @param workspaceId 工作区编号
     * @return 影响行数
     */
    int removeOmObjectTypeRelation(Collection<Long> idList, Long workspaceId);

    /**
     * 获得关系起点、终点的对象类型候选列表
     *
     * @param workspaceId 工作区编号
     * @param versionId 版本编号，可为空
     * @return 对象类型列表
     */
    List<OmObjectTypeDO> getObjectTypeOptions(Long workspaceId, Long versionId);

    /**
     * 批量统计对象类型的关系数量，自关联仅计一次
     *
     * @param objectTypeIds 对象类型编号集合
     * @return 对象类型编号与关系数量的映射
     */
    Map<Long, Long> getOmObjectTypeRelationCounts(Collection<Long> objectTypeIds);
}
