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

import java.util.List;
import java.util.Map;
import java.util.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;
/**
 * 枚举数据Service接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface IOmEnumDataService extends IService<OmEnumDataDO> {

    /**
     * 获得枚举数据分页列表
     *
     * @param pageReqVO 分页请求
     * @return 枚举数据分页列表
     */
    PageResult<OmEnumDataDO> getOmEnumDataPage(OmEnumDataPageReqVO pageReqVO);

    /**
     * 创建枚举数据
     *
     * @param createReqVO 枚举数据信息
     * @return 枚举数据编号
     */
    Long createOmEnumData(OmEnumDataSaveReqVO createReqVO);

    /**
     * 更新枚举数据
     *
     * @param updateReqVO 枚举数据信息
     */
    int updateOmEnumData(OmEnumDataSaveReqVO updateReqVO);

    /**
     * 删除枚举数据
     *
     * @param idList 枚举数据编号
     */
    int removeOmEnumData(Collection<Long> idList);

    /**
     * 获得枚举数据详情
     *
     * @param id 枚举数据编号
     * @return 枚举数据
     */
    OmEnumDataDO getOmEnumDataById(Long id);

    /**
     * 获得全部枚举数据列表
     *
     * @return 枚举数据列表
     */
    List<OmEnumDataDO> getOmEnumDataList();

    /**
     * 获得全部枚举数据 Map
     *
     * @return 枚举数据 Map
     */
    Map<Long, OmEnumDataDO> getOmEnumDataMap();


    /**
     * 导入枚举数据数据
     *
     * @param importExcelList 枚举数据数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importOmEnumData(List<OmEnumDataRespVO> importExcelList, boolean isUpdateSupport, String operName);

}
