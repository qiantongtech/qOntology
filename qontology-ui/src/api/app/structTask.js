/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qKnow Intelligent Agent Building Platform (Open Source Edition).
 *
 * qKnow is licensed under Apache License 2.0 with additional qKnow terms.
 * You may use qKnow for commercial purposes, but you may not remove, hide,
 * modify, or replace the qKnow logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qKnow as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

import request from '@/utils/request';

// 查询结构化抽取任务列表
export function listExtStruct(query) {
    return request({
        url: '/app/structTask/list',
        method: 'get',
        params: query
    });
}

// 查询结构化抽取任务详细
export function getExtStruct(id) {
    return request({
        url: '/app/structTask/' + id,
        method: 'get'
    });
}

//获取结构化任务抽取结果
export function getExtStructByTaskId(id) {
    return request({
        url: '/app/structTask/getAppStructByTaskId?taskId=' + id,
        method: 'get'
    });
}

// 查询结构化实体的对象类型属性信息。
export function getAttributeInformation(attributeIds) {
    return request({
        url: '/app/structTask/getAttributeInformation?attributeIds=' + attributeIds,
        method: 'get'
    });
}

// 新增结构化抽取任务
export function addExtStruct(data) {
    return request({
        url: '/app/structTask',
        method: 'post',
        data: data
    });
}

//执行抽取
export function extExecuteExtraction(data) {
    return request({
        url: '/app/structTask/executeExtraction',
        method: 'post',
        data: data
    });
}

//新建结构化抽取任务
export function addExtStructDataMapping(data) {
    return request({
        url: '/app/structTask/addDataMapping',
        method: 'post',
        data: data
    });
}

//修改结构化抽取任务
export function updateExtStructDataMapping(data) {
    return request({
        url: '/app/structTask/updateDataMapping',
        method: 'post',
        data: data
    });
}

//发布
export function strutReleaseByTaskId(data) {
    return request({
        url: '/app/structTask/releaseByTaskId',
        method: 'post',
        data: data
    });
}

//取消发布
export function strutCancelReleaseByTaskId(data) {
    return request({
        url: '/app/structTask/cancelReleaseByTaskId',
        method: 'post',
        data: data
    });
}

//修改关系
export function updateRelationship(data) {
    return request({
        url: '/app/structTask/updateRelationship',
        method: 'post',
        data: data
    });
}

//删除关系
export function deleteRelationship(data) {
    return request({
        url: '/app/structTask/deleteRelationship',
        method: 'post',
        data: data
    });
}

//删除节点的某个属性
export function deleteNodeAttribute(data) {
    return request({
        url: '/app/structTask/deleteNodeAttribute',
        method: 'post',
        data: data
    });
}

//修改节点的某个属性
export function updateNodeAttribute(data) {
    return request({
        url: '/app/structTask/updateNodeAttribute',
        method: 'post',
        data: data
    });
}

// 修改结构化抽取任务
export function updateExtStruct(data) {
    return request({
        url: '/app/structTask',
        method: 'put',
        data: data
    });
}

// 删除结构化抽取任务
export function delExtStruct(id) {
    return request({
        url: '/app/structTask/' + id,
        method: 'delete'
    });
}

// 修改结构化发布状态
export function updateStructTaskPublishStatus(data) {
    return request({
        url: '/app/structTask/updateStructTaskPublishStatus',
        method: 'post',
        data: data
    });
}

// 定时任务立即执行一次
export function runStructTask(taskId, updateType) {
    const data = {
        taskId,
        updateType
    };
    return request({
        url: '/app/structTask/runStructTask',
        method: 'put',
        data: data
    });
}
