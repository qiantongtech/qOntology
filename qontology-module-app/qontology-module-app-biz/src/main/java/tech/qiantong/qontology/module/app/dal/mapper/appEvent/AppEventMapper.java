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

import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 事件管理Mapper接口
 *
 * @author qknow
 * @date 2025-11-11
 */
public interface AppEventMapper extends BaseMapperX<AppEventDO> {

    default PageResult<AppEventDO> selectPage(AppEventPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 从 reqVO 中获取传递的时间参数
        Map<String, Object> params = reqVO.getParams();
        String beginTimeStr = (String) params.get("beginTime");
        String endTimeStr = (String) params.get("endTime");
        String occBeginTime = reqVO.getOccurBeginTime();
        String occEndTime = reqVO.getOccurEndTime();
        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;
        LocalDateTime occurBeginTime = null;
        LocalDateTime occurEndTime = null;

        // 判断并转换时间格式
        if (beginTimeStr != null && !beginTimeStr.isEmpty()) {
            beginTime = LocalDateTime.parse(beginTimeStr + "T00:00:00");  // 设置为开始时刻
        }
        if (endTimeStr != null && !endTimeStr.isEmpty()) {
            endTime = LocalDateTime.parse(endTimeStr + "T23:59:59");  // 设置为结束时刻
        }

        if (occBeginTime != null && !occBeginTime.isEmpty()) {
            occurBeginTime = LocalDateTime.parse(occBeginTime + "T00:00:00");  // 设置为开始时刻
        }
        if (occEndTime != null && !occEndTime.isEmpty()) {
            occurEndTime = LocalDateTime.parse(occEndTime + "T23:59:59");  // 设置为结束时刻
        }


        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<AppEventDO>()
                .eqIfPresent(AppEventDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppEventDO::getModelId, reqVO.getModelId())
                .likeIfPresent(AppEventDO::getName, reqVO.getName())
                .eqIfPresent(AppEventDO::getDescription, reqVO.getDescription())
                .eqIfPresent(AppEventDO::getType, reqVO.getType())
                .eqIfPresent(AppEventDO::getOccurTime, reqVO.getOccurTime())
                .eqIfPresent(AppEventDO::getOccurPlace, reqVO.getOccurPlace())
                .likeIfPresent(AppEventDO::getCreateBy, reqVO.getCreateBy())

                // 时间范围筛选
                .geIfPresent(AppEventDO::getCreateTime, beginTime)  // startTime 大于等于
                .leIfPresent(AppEventDO::getCreateTime, endTime)    // endTime 小于等于
                // 发生时间筛选
                .geIfPresent(AppEventDO::getOccurTime, occurBeginTime)
                .leIfPresent(AppEventDO::getOccurTime, occurEndTime)
//                .eqIfPresent(AppEventDO::getCreateTime, reqVO.getCreateTime())
                // 如果 reqVO.getName() 不为空，则添加 name 的精确匹配条件（name = '<name>'）
                // .likeIfPresent(AppEventDO::getName, reqVO.getName())
                // 按照 createTime 字段降序排序
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }


    /**
     * 获取实体关联的事件列表数据
     *
     * @param entityId 父分类ID
     * @return 子分类列表
     */
    List<AppEventDO> selectAppEventDOListByEntityId(@Param("entityId") Long entityId);

}
