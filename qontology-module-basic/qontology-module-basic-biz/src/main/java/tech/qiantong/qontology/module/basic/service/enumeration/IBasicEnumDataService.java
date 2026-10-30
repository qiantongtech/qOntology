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
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumDataDO;

/**
 * 枚举数据Service接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface IBasicEnumDataService extends IService<BasicEnumDataDO> {
    /**
     * 获得枚举数据分页列表
     *
     * @param pageReqVO 分页请求
     * @return 枚举数据分页列表
     */
    PageResult<BasicEnumDataDO> getBasicEnumDataPage(BasicEnumDataPageReqVO pageReqVO);

    /**
     * 创建枚举数据
     *
     * @param createReqVO 枚举数据信息
     * @return 枚举数据编号
     */
    Long createBasicEnumData(BasicEnumDataSaveReqVO createReqVO);

    /**
     * 更新枚举数据
     *
     * @param updateReqVO 枚举数据信息
     * @return 实际受影响的记录数
     */
    int updateBasicEnumData(BasicEnumDataSaveReqVO updateReqVO);

    /**
     * 删除枚举数据
     *
     * @param idList 枚举数据编号
     * @return 实际受影响的记录数
     */
    int removeBasicEnumData(Collection<Long> idList);

    /**
     * 获得枚举数据详情
     *
     * @param id 枚举数据编号
     * @return 枚举数据
     */
    BasicEnumDataDO getBasicEnumDataById(Long id);

    /**
     * 获得全部枚举数据列表
     *
     * @return 枚举数据列表
     */
    List<BasicEnumDataDO> getBasicEnumDataList();

    /**
     * 获得全部枚举数据 Map
     *
     * @return 枚举数据 Map
     */
    Map<Long, BasicEnumDataDO> getBasicEnumDataMap();
}
