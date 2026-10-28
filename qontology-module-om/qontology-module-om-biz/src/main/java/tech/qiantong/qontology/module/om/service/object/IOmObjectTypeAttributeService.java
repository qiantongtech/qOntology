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

package tech.qiantong.qontology.module.om.service.object;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmAttributeBusinessTypeOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;

/**
 * 对象类型属性 Service 接口
 *
 * @author qOntology
 * @date 2026-10-13
 */
public interface IOmObjectTypeAttributeService extends IService<OmObjectTypeAttributeDO> {
    /**
     * 获得对象类型属性分页列表
     *
     * @param request 分页请求
     * @return 对象类型属性分页列表
     */
    PageResult<OmObjectTypeAttributeDO> getOmObjectTypeAttributePage(OmObjectTypeAttributePageReqVO request);

    /**
     * 获得对象类型属性详情
     *
     * @param id 属性编号
     * @param objectTypeId 所属对象类型编号
     * @param workspaceId 工作区编号
     * @return 对象类型属性
     */
    OmObjectTypeAttributeDO getOmObjectTypeAttributeById(Long id, Long objectTypeId, Long workspaceId);

    /**
     * 创建对象类型属性
     *
     * @param request 属性信息
     * @return 属性编号
     */
    Long createOmObjectTypeAttribute(OmObjectTypeAttributeSaveReqVO request);

    /**
     * 更新对象类型属性
     *
     * @param request 属性信息
     * @return 实际受影响的记录数
     */
    int updateOmObjectTypeAttribute(OmObjectTypeAttributeSaveReqVO request);

    /**
     * 删除对象类型属性
     *
     * @param idList 属性编号集合
     * @param objectTypeId 所属对象类型编号
     * @param workspaceId 工作区编号
     * @return 实际受影响的记录数
     */
    int removeOmObjectTypeAttribute(Collection<Long> idList, Long objectTypeId, Long workspaceId);

    /**
     * 批量统计对象类型的属性数量
     *
     * @param objectTypeIds 对象类型编号集合
     * @return 对象类型编号与属性数量的映射
     */
    Map<Long, Long> getOmObjectTypeAttributeCounts(Collection<Long> objectTypeIds);

    /**
     * 按项目版本和来源查询可选业务数据类型，兼容通过对象类型确定版本
     *
     * @param objectTypeId 所属对象类型 ID，可为空
     * @param versionId 项目版本 ID，未指定对象类型时使用
     * @param workspaceId 工作区 ID
     * @param source 数据来源，PROJECT 当前项目版本，GLOBAL 全局
     * @return 业务数据类型选项集合
     */
    List<OmAttributeBusinessTypeOptionRespVO> getBusinessDataTypes(
        Long objectTypeId,
        Long versionId,
        Long workspaceId,
        String source
    );

    /**
     * 按来源查询当前项目版本或全局可用的枚举类型
     *
     * @param objectTypeId 所属对象类型 ID
     * @param workspaceId 工作区 ID
     * @param source 数据来源，PROJECT 当前项目版本，GLOBAL 全局
     * @return 枚举类型选项集合
     */
    List<OmDataTypeEnumOptionRespVO> getEnumOptions(Long objectTypeId, Long workspaceId, String source);
}
