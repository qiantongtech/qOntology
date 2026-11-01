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

package tech.qiantong.qontology.module.om.dal.mapper.enumeration;

import com.github.yulichang.base.MPJBaseMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 枚举数据Mapper接口
 *
 * @author qOntology
 * @date 2026-10-10
 */
public interface OmEnumDataMapper extends BaseMapperX<OmEnumDataDO> {
    /** 统计项目枚举值是否存在。 */
    Integer countAiValue(@Param("taskId") Long taskId,
                         @Param("enumType") String enumType,
                         @Param("value") String value);

    /**
     * 按请求条件分页查询枚举数据
     *
     * @param reqVO 分页查询条件
     * @return 分页查询结果
     */
    default PageResult<OmEnumDataDO> selectPage(OmEnumDataPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(
            Arrays.asList("enum_code", "enum_sort", "create_time", "update_time")
        );
        // 构造动态查询条件
        return selectPage(
            reqVO,
            new LambdaQueryWrapperX<OmEnumDataDO>()
                .eqIfPresent(OmEnumDataDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(OmEnumDataDO::getVersionId, reqVO.getVersionId())
                .eqIfPresent(OmEnumDataDO::getEnumSort, reqVO.getEnumSort())
                .likeIfPresent(OmEnumDataDO::getEnumLabel, reqVO.getEnumLabel())
                .eqIfPresent(OmEnumDataDO::getEnumValue, reqVO.getEnumValue())
                .eqIfPresent(OmEnumDataDO::getEnumType, reqVO.getEnumType())
                .eqIfPresent(OmEnumDataDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OmEnumDataDO::getCreateTime, reqVO.getCreateTime())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns)
        );
    }
}
