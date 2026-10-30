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

package tech.qiantong.qontology.module.basic.service.dataType;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.dataType.BasicDataTypeDO;

/**
 * 数据类型Service接口
 *
 * @author qOntology
 * @date 2026-10-16
 */
public interface IBasicDataTypeService extends IService<BasicDataTypeDO> {
    /**
     * 获得数据类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 数据类型分页列表
     */
    PageResult<BasicDataTypeDO> getBasicDataTypePage(BasicDataTypePageReqVO pageReqVO);

    /**
     * 创建数据类型
     *
     * @param createReqVO 数据类型信息
     * @return 数据类型编号
     */
    Long createBasicDataType(BasicDataTypeSaveReqVO createReqVO);

    /**
     * 更新数据类型
     *
     * @param updateReqVO 数据类型信息
     * @return 实际受影响的记录数
     */
    int updateBasicDataType(BasicDataTypeSaveReqVO updateReqVO);

    /**
     * 删除数据类型
     *
     * @param idList 数据类型编号
     * @return 实际受影响的记录数
     */
    int removeBasicDataType(Collection<Long> idList);

    /**
     * 获得数据类型详情
     *
     * @param id 数据类型编号
     * @return 数据类型
     */
    BasicDataTypeDO getBasicDataTypeById(Long id);
}
