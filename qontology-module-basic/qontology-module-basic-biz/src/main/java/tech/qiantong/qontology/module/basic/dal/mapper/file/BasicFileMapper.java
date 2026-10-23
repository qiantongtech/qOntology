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

package tech.qiantong.qontology.module.basic.dal.mapper.file;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFilePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.*;

/**
 * 模型数据文件Mapper接口
 *
 * @author qModel
 * @date 2026-08-18
 */
public interface BasicFileMapper extends BaseMapperX<BasicFileDO> {

    default PageResult<BasicFileDO> selectPage(BasicFilePageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<BasicFileDO>()
                .eqIfPresent(BasicFileDO::getCategoryId, reqVO.getCategoryId())
                .likeIfPresent(BasicFileDO::getFileName, reqVO.getFileName())
                .eqIfPresent(BasicFileDO::getFileSuffix, reqVO.getFileSuffix())
                .eqIfPresent(BasicFileDO::getFileType, reqVO.getFileType())
                .eqIfPresent(BasicFileDO::getFileSize, reqVO.getFileSize())
                .eqIfPresent(BasicFileDO::getFilePath, reqVO.getFilePath())
                .eqIfPresent(BasicFileDO::getStorageType, reqVO.getStorageType())
                .eqIfPresent(BasicFileDO::getFileHash, reqVO.getFileHash())
                .eqIfPresent(BasicFileDO::getMimeType, reqVO.getMimeType())
                .eqIfPresent(BasicFileDO::getStatus, reqVO.getStatus())
                .eqIfPresent(BasicFileDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BasicFileDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(BasicFileDO::getCategoryId, reqVO.getCategoryIds())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(ModelDataFileDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }

    default List<Map<String, Object>> getFileTypes() {
        return selectMaps(
                new QueryWrapper<BasicFileDO>()
                        .select("category_id", "COUNT(category_id) AS totalCount")
                        .eq("del_flag", 0)
                        .groupBy("category_id")
        );
    }
}
