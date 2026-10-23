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

package tech.qiantong.qontology.module.basic.dal.mapper.datasource;

import org.apache.ibatis.annotations.Select;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourcePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.datasource.BasicDatasourceDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据源Mapper接口
 *
 * @author lhs
 * @date 2025-01-21
 */
public interface BasicDatasourceMapper extends BaseMapperX<BasicDatasourceDO> {

    default PageResult<BasicDatasourceDO> selectPage(BasicDatasourcePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<BasicDatasourceDO>()
                .eqIfPresent(BasicDatasourceDO::getGraphId, reqVO.getGraphId())
                .likeIfPresent(BasicDatasourceDO::getDatasourceName, reqVO.getDatasourceName())
                .eqIfPresent(BasicDatasourceDO::getDatasourceType, reqVO.getDatasourceType())
                .eqIfPresent(BasicDatasourceDO::getDatasourceConfig, reqVO.getDatasourceConfig())
                .eqIfPresent(BasicDatasourceDO::getIp, reqVO.getIp())
                .eqIfPresent(BasicDatasourceDO::getPort, reqVO.getPort())
                .eqIfPresent(BasicDatasourceDO::getListCount, reqVO.getListCount())
                .eqIfPresent(BasicDatasourceDO::getSyncCount, reqVO.getSyncCount())
                .eqIfPresent(BasicDatasourceDO::getDataSize, reqVO.getDataSize())
                .eqIfPresent(BasicDatasourceDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BasicDatasourceDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(DaDatasourceDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }

    @Select(" SELECT\n" +
            "            t.*\n" +
            "        FROM\n" +
            "            basic_datasource t\n" +
            "        WHERE\n" +
            "            t.del_flag = 0;")
    public List<BasicDatasourceDO> getDataSourceByAsset();
}
