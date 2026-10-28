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

package tech.qiantong.qontology.module.om.service.enumeration;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;

/**
 * 枚举类型Service接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface IOmEnumTypeService extends IService<OmEnumTypeDO> {
    /**
     * 获得枚举类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 枚举类型分页列表
     */
    PageResult<OmEnumTypeDO> getOmEnumTypePage(OmEnumTypePageReqVO pageReqVO);

    /**
     * 创建枚举类型
     *
     * @param createReqVO 枚举类型信息
     * @return 枚举类型编号
     */
    Long createOmEnumType(OmEnumTypeSaveReqVO createReqVO);

    /**
     * 更新枚举类型
     *
     * @param updateReqVO 枚举类型信息
     * @return 实际受影响的记录数
     */
    int updateOmEnumType(OmEnumTypeSaveReqVO updateReqVO);

    /**
     * 删除枚举类型
     *
     * @param idList 枚举类型编号
     * @return 实际受影响的记录数
     */
    int removeOmEnumType(Collection<Long> idList);

    /**
     * 获得枚举类型详情
     *
     * @param id 枚举类型编号
     * @return 枚举类型
     */
    OmEnumTypeDO getOmEnumTypeById(Long id);

    /**
     * 获得全部枚举类型列表
     *
     * @return 枚举类型列表
     */
    List<OmEnumTypeDO> getOmEnumTypeList();

    /**
     * 获得全部枚举类型 Map
     *
     * @return 枚举类型 Map
     */
    Map<Long, OmEnumTypeDO> getOmEnumTypeMap();

    /**
     * 导入枚举类型数据
     *
     * @param importExcelList 枚举类型数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 导入处理结果说明
     */
    String importOmEnumType(List<OmEnumTypeRespVO> importExcelList, boolean isUpdateSupport, String operName);
}
