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

package tech.qiantong.qontology.module.app.service.graph;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphRelationshipSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.DeleteNodeAttributeVO;

import java.util.List;
import java.util.Map;

public interface AppGraphService {
    Map<String, Object> getGraph(AppGraphVO appGraphVO);

    PageResult<JSONObject> getGraphPage(AppGraphPageReqVO appGraphVO);

    AjaxResult getDynamicEntityList(AppGraphVO appGraphVO);

    /**
     * 查询结构化实体关联的源数据记录。
     *
     * @param nodeId 图谱实体编号
     * @return 数据源及字段值
     */
    Map<String, Object> getNodeDatasource(String nodeId);

    Map<String, Object> getGraphDataStatistics(AppGraphVO appGraphVO);

    /**
     * 新增实体
     *
     * @param jsonArray
     * @return
     */
    Boolean addNode(JSONArray jsonArray);

    /**
     * 编辑三元组
     *
     * @param graphRelationshipSaveReqVOList
     * @return
     */
    Boolean addTripletRel(List<AppGraphRelationshipSaveReqVO> graphRelationshipSaveReqVOList);

    AjaxResult deleteNodeAttributeById(DeleteNodeAttributeVO deleteNodeAttributeVO);

    AjaxResult updateReleaseStatus(AppGraphVO appGraphVO);

    AjaxResult deleteNode(String id);

    /**
     * 批量删除节点
     *
     * @param ids
     * @return
     */
    Boolean deleteNodeByIds(String[] ids);

    AjaxResult deleteRelationshipById(String id);

    AjaxResult deleteRelationshipsByIds(String[] ids);

    Map<String, Object> getTableGraph(AppGraphVO appGraphVO);
}
