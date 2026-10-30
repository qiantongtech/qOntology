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

package tech.qiantong.qontology.module.basic.service.enumeration;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumTypeDO;

/**
 * 枚举类型Service接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface IBasicEnumTypeService extends IService<BasicEnumTypeDO> {
    /**
     * 获得枚举类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 枚举类型分页列表
     */
    PageResult<BasicEnumTypeDO> getBasicEnumTypePage(BasicEnumTypePageReqVO pageReqVO);

    /**
     * 创建枚举类型
     *
     * @param createReqVO 枚举类型信息
     * @return 枚举类型编号
     */
    Long createBasicEnumType(BasicEnumTypeSaveReqVO createReqVO);

    /**
     * 更新枚举类型
     *
     * @param updateReqVO 枚举类型信息
     * @return 实际受影响的记录数
     */
    int updateBasicEnumType(BasicEnumTypeSaveReqVO updateReqVO);

    /**
     * 删除枚举类型
     *
     * @param idList 枚举类型编号
     * @return 实际受影响的记录数
     */
    int removeBasicEnumType(Collection<Long> idList);

    /**
     * 获得枚举类型详情
     *
     * @param id 枚举类型编号
     * @return 枚举类型
     */
    BasicEnumTypeDO getBasicEnumTypeById(Long id);

    /**
     * 获得全部枚举类型列表
     *
     * @return 枚举类型列表
     */
    List<BasicEnumTypeDO> getBasicEnumTypeList();

    /**
     * 获得全部枚举类型 Map
     *
     * @return 枚举类型 Map
     */
    Map<Long, BasicEnumTypeDO> getBasicEnumTypeMap();

    /**
     * 获得枚举类型列表
     *
     * @return 枚举类型列表
     */
    List<BasicEnumTypeRespVO> queryEnumTypeDict();
}
