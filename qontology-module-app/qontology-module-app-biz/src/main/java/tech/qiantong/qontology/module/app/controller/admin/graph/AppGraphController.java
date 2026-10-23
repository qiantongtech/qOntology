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

package tech.qiantong.qontology.module.app.controller.admin.graph;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.utils.DateUtils;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphRelationshipSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.DeleteNodeAttributeVO;
import tech.qiantong.qontology.module.app.service.graph.AppGraphService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图谱controller
 *
 * @author qknow
 * @date 2025-03-17
 */
@RestController
@RequestMapping("/app/graph")
public class AppGraphController extends BaseController {

    @Resource
    private AppGraphService appGraphService;

    /**
     * 获取图谱数据
     *
     * @param appGraphVO
     * @return
     */
    @GetMapping("/getGraph")
    public CommonResult<Map<String, Object>> getGraph(AppGraphVO appGraphVO) {
        return CommonResult.success(appGraphService.getGraph(appGraphVO));
    }

    /**
     * 获取实体分页
     *
     * @param appGraphVO
     * @return
     */
    @GetMapping("/getGraphPage")
    public CommonResult<PageResult<JSONObject>> getGraph(AppGraphPageReqVO appGraphVO) {
        return CommonResult.success(appGraphService.getGraphPage(appGraphVO));
    }

    /**
     * 根据实体，获取对应的关系链数据
     *
     * @param appGraphVO
     * @return
     */
    @GetMapping("/getDynamicEntityList")
    public AjaxResult getDynamicEntityList(AppGraphVO appGraphVO) {
        return appGraphService.getDynamicEntityList(appGraphVO);
    }

    /**
     * 查询结构化实体关联的源数据。
     *
     * @param nodeId 图谱实体编号
     * @return 数据源及字段值
     */
    @GetMapping("/getNodeDatasource")
    public CommonResult<Map<String, Object>> getNodeDatasource(@RequestParam String nodeId) {
        return CommonResult.success(appGraphService.getNodeDatasource(nodeId));
    }

    /**
     * 统计 (实体数量,关系类型数量,三元组数量)
     *
     * @param appGraphVO
     * @return
     */
    @GetMapping("/getGraphDataStatistics")
    public CommonResult<Map<String, Object>> getGraphDataStatistics(AppGraphVO appGraphVO) {
        return CommonResult.success(appGraphService.getGraphDataStatistics(appGraphVO));
    }


    /**
     * 新增实体 TODO
     *
     * @param jsonArray
     * @return
     */
    @PostMapping("/addNode")
    public CommonResult<Boolean> addNode(@RequestBody JSONArray jsonArray) {
        Date date = DateUtils.getNowDate();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.get("id") == null) {
                jsonObject.put("workspaceId", super.getWorkSpaceId());
                jsonObject.put("creatorId", super.getUserId());
                jsonObject.put("createBy", super.getUsername());
                jsonObject.put("createTime", date);
            }
        }
        return CommonResult.success(appGraphService.addNode(jsonArray));
    }

    /**
     * 根据起点id和结点id创建关系 TODO
     *
     * @param graphRelationshipSaveReqVOList
     * @return
     */
    @PostMapping("/addTripletRel")
    public CommonResult<Boolean> addTripletRel(@RequestBody List<AppGraphRelationshipSaveReqVO> graphRelationshipSaveReqVOList) {
        return CommonResult.success(appGraphService.addTripletRel(graphRelationshipSaveReqVOList));
    }

    /**
     * 根据节点id和属性的key删除属性
     *
     * @param deleteNodeAttributeVO
     * @return
     */
    @DeleteMapping("/deleteNodeAttributeById")
    public AjaxResult deleteNodeAttributeById(@RequestBody DeleteNodeAttributeVO deleteNodeAttributeVO) {
        return appGraphService.deleteNodeAttributeById(deleteNodeAttributeVO);
    }

    /**
     * 发布 / 取消发布
     *
     * @param appGraphVO
     * @return
     */
    @PostMapping("/updateReleaseStatus")
    public AjaxResult updateReleaseStatus(@RequestBody AppGraphVO appGraphVO) {
        return appGraphService.updateReleaseStatus(appGraphVO);
    }

    /**
     * 根据节点id删除对应的节点
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteNode/{id}")
    public AjaxResult deleteNode(@PathVariable String id) {
        return appGraphService.deleteNode(id);
    }

    /**
     * 根据节点ids删除对应的节点
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/deleteNodeByIds/{ids}")
    public CommonResult<Boolean> deleteNodeByIds(@PathVariable String[] ids) {
        return CommonResult.success(appGraphService.deleteNodeByIds(ids));
    }

    /**
     * 删除关系
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteRelationshipById/{id}")
    public AjaxResult deleteRelationshipById(@PathVariable String id) {
        return appGraphService.deleteRelationshipById(id);
    }

    /**
     * 根据关系ids删除关系
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/deleteRelationshipsByIds/{ids}")
    public AjaxResult deleteRelationshipsByIds(@PathVariable String[] ids) {
        return appGraphService.deleteRelationshipsByIds(ids);
    }


    /**
     * 表格视图
     *
     * @param appGraphVO
     * @return
     */
    @GetMapping("/getTableGraph")
    public CommonResult<Map<String, Object>> getTableGraph(AppGraphVO appGraphVO) {
        return CommonResult.success(appGraphService.getTableGraph(appGraphVO));
    }

}
