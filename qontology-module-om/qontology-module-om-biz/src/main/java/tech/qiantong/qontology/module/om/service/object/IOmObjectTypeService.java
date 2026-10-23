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
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;

/**
 * 对象类型Service接口
 *
 * @author qOntology
 * @date 2026-10-12
 */
public interface IOmObjectTypeService extends IService<OmObjectTypeDO> {

    /**
     * 获得对象类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 对象类型分页列表
     */
    PageResult<OmObjectTypeDO> getOmObjectTypePage(OmObjectTypePageReqVO pageReqVO);

    /**
     * 创建对象类型
     *
     * @param createReqVO 对象类型信息
     * @return 对象类型编号
     */
    Long createOmObjectType(OmObjectTypeSaveReqVO createReqVO);

    /**
     * 更新对象类型
     *
     * @param updateReqVO 对象类型信息
     */
    int updateOmObjectType(OmObjectTypeSaveReqVO updateReqVO);

    /**
     * 删除对象类型
     *
     * @param id 对象类型编号
     */
    int removeOmObjectType(Long id);

    /**
     * 获得对象类型详情
     *
     * @param id 对象类型编号
     * @return 对象类型
     */
    OmObjectTypeDO getOmObjectTypeById(Long id);

    /**
     * 获得全部对象类型列表
     *
     * @return 对象类型列表
     */
    List<OmObjectTypeDO> getOmObjectTypeList();

    /**
     * 获得同工作区的上级对象类型候选列表，排除自身及下级
     *
     * @param id 当前对象类型编号
     * @return 对象类型列表
     */
    List<OmObjectTypeDO> getOmObjectTypeListExcludeChild(Long id);

    /**
     * 获得全部对象类型 Map
     *
     * @return 对象类型 Map
     */
    Map<Long, OmObjectTypeDO> getOmObjectTypeMap();

    /**
     * 导入对象类型数据
     *
     * @param importExcelList 对象类型数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importOmObjectType(List<OmObjectTypeRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
