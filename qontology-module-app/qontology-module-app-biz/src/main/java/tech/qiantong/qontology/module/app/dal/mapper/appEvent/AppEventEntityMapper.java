/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.dal.mapper.appEvent;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 事件实体关联Mapper接口
 *
 * @author qknow
 * @date 2025-11-11
 */
public interface AppEventEntityMapper extends BaseMapperX<AppEventEntityDO> {

    default PageResult<AppEventEntityDO> selectPage(AppEventEntityPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 从 reqVO 中获取传递的时间参数
        Map<String, Object> params = reqVO.getParams();
        String beginTimeStr = (String) params.get("beginTime");
        String endTimeStr = (String) params.get("endTime");

        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;

        // 判断并转换时间格式
        if (beginTimeStr != null && !beginTimeStr.isEmpty()) {
            beginTime = LocalDateTime.parse(beginTimeStr + "T00:00:00");  // 设置为开始时刻
        }
        if (endTimeStr != null && !endTimeStr.isEmpty()) {
            endTime = LocalDateTime.parse(endTimeStr + "T23:59:59");  // 设置为结束时刻
        }

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppEventEntityDO>()
                .eqIfPresent(AppEventEntityDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppEventEntityDO::getEventId, reqVO.getEventId())
                .eqIfPresent(AppEventEntityDO::getEntityId, reqVO.getEntityId())
                // 时间范围筛选
                .geIfPresent(AppEventEntityDO::getCreateTime, beginTime)  // startTime 大于等于
                .leIfPresent(AppEventEntityDO::getCreateTime, endTime)    // endTime 小于等于
//                .eqIfPresent(AppEventEntityDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppEventEntityDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
