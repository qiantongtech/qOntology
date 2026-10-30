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

package tech.qiantong.qontology.module.om.service.dataType;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;

/**
 * 数据类型Service接口
 *
 * @author qOntology
 * @date 2026-10-16
 */
public interface IOmDataTypeService extends IService<OmDataTypeDO> {
    /**
     * 获得数据类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 数据类型分页列表
     */
    PageResult<OmDataTypeDO> getOmDataTypePage(OmDataTypePageReqVO pageReqVO);

    /**
     * 创建数据类型
     *
     * @param createReqVO 数据类型信息
     * @return 数据类型编号
     */
    Long createOmDataType(OmDataTypeSaveReqVO createReqVO);

    /**
     * 更新数据类型
     *
     * @param updateReqVO 数据类型信息
     * @return 实际受影响的记录数
     */
    int updateOmDataType(OmDataTypeSaveReqVO updateReqVO);

    /**
     * 删除数据类型
     *
     * @param idList 数据类型编号
     * @param versionId 项目版本 ID
     * @return 实际受影响的记录数
     */
    int removeOmDataType(Collection<Long> idList, Long versionId);

    /**
     * 获得数据类型详情
     *
     * @param id 数据类型编号
     * @param versionId 项目版本 ID
     * @return 数据类型
     */
    OmDataTypeDO getOmDataTypeById(Long id, Long versionId);

    /**
     * 按来源查询当前项目版本或全局可用的枚举类型
     *
     * @param versionId 项目版本 ID
     * @param enumSource 枚举来源，PROJECT 当前项目版本，GLOBAL 全局
     * @return 枚举类型选项集合
     */
    List<OmDataTypeEnumOptionRespVO> getEnumOptions(Long versionId, String enumSource);
}
