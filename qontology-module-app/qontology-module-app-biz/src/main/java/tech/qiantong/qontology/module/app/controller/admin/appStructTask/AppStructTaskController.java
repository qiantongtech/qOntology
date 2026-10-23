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

package tech.qiantong.qontology.module.app.controller.admin.appStructTask;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appStructTask.AppStructTaskConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTask;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTaskDO;
import tech.qiantong.qontology.module.app.extEnum.AppReleaseStatus;
import tech.qiantong.qontology.module.app.service.appStructTask.IAppStructTaskService;
import tech.qiantong.qontology.module.app.service.neo4j.AppNeo4jService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 结构化抽取任务Controller
 *
 * @author qknow
 * @date 2025-02-25
 */
@Tag(name = "结构化抽取任务")
@RestController
@Slf4j
@RequestMapping("/app/structTask")
@Validated
public class AppStructTaskController extends BaseController {
    @Resource
    private IAppStructTaskService appStructTaskService;
    @Resource
    private AppNeo4jService appNeo4jService;

    /**
     * 执行抽取
     *
     * @param appStructTask
     * @return
     */
    @PostMapping("executeExtraction")
    public AjaxResult executeExtraction(@RequestBody AppStructTaskPageReqVO appStructTask) {
        if (appStructTask.getId() == null) {
            return AjaxResult.error("id为空");
        }
        return appStructTaskService.executeExtraction(appStructTask);
    }

    /**
     * 获取结构化任务抽取结果
     *
     * @param taskId
     * @return
     */
    @GetMapping("getAppStructByTaskId")
    public AjaxResult getAppStructByTaskId(Long taskId) {
        return appStructTaskService.selectByTaskId(taskId, 1);
    }

    /**
     * 修改结构化任务 发布状态
     *
     * @param appStructTask
     * @return
     */
    @PostMapping("updateStructTaskPublishStatus")
    public AjaxResult updateStructTaskPublishStatus(@RequestBody AppStructTaskSaveReqVO appStructTask) {
        AppStructTaskDO taskById = appStructTaskService.getAppStructTaskById(appStructTask.getId());
        taskById.setPublishStatus(appStructTask.getPublishStatus());
        if (AppReleaseStatus.PUBLISHED.getStatus().equals(appStructTask.getPublishStatus())) {
            taskById.setPublishBy(getNickName());
            taskById.setPublisherId(getUserId());
            taskById.setPublishTime(new Date());
        }
        return AppReleaseStatus.PUBLISHED.getStatus().equals(appStructTask.getPublishStatus())
                ? appStructTaskService.releaseByTaskId(taskById)
                : appStructTaskService.cancelReleaseByTaskId(taskById);
    }

    /** 发布结构化抽取结果。 */
    @PostMapping("releaseByTaskId")
    public AjaxResult releaseByTaskId(@RequestBody JSONObject jsonObject) {
        AppStructTaskDO taskDO = preparePublishTask(jsonObject.getLong("taskId"), AppReleaseStatus.PUBLISHED.getStatus());
        return appStructTaskService.releaseByTaskId(taskDO);
    }

    /** 取消发布结构化抽取结果。 */
    @PostMapping("cancelReleaseByTaskId")
    public AjaxResult cancelReleaseByTaskId(@RequestBody JSONObject jsonObject) {
        AppStructTaskDO taskDO = preparePublishTask(jsonObject.getLong("taskId"), AppReleaseStatus.UNPUBLISHED.getStatus());
        return appStructTaskService.cancelReleaseByTaskId(taskDO);
    }

    /** 设置结构化任务的发布审计信息。 */
    private AppStructTaskDO preparePublishTask(Long taskId, Integer publishStatus) {
        AppStructTaskDO taskDO = appStructTaskService.getAppStructTaskById(taskId);
        taskDO.setPublishStatus(publishStatus);
        taskDO.setPublishTime(new Date());
        taskDO.setPublisherId(getUserId());
        taskDO.setPublishBy(getNickName());
        taskDO.setUpdateTime(new Date());
        taskDO.setUpdaterId(getUserId());
        taskDO.setUpdateBy(getNickName());
        return taskDO;
    }
//
//    /**
//     * 删除节点的某个属性
//     *
//     * @param jsonObject
//     * @return
//     */
//    @PostMapping("deleteNodeAttribute")
//    public AjaxResult deleteNodeAttribute(@RequestBody JSONObject jsonObject) {
//        Long taskId = jsonObject.getLong("taskId");
//        return extNeo4jService.deleteNodeAttribute(taskId, jsonObject.getInteger("extractType"),
//                jsonObject.getString("tableName"), jsonObject.getInteger("dataId"), jsonObject.getString("attributeKey"));
//    }

//    /**
//     * 修改关系
//     *
//     * @param updateRelationship
//     * @return
//     */
//    @PostMapping("updateRelationship")
//    public AjaxResult updateRelationship(@RequestBody ExtNeo4j.UpdateRelationship updateRelationship) {
//        return extNeo4jService.updateRelationship(updateRelationship);
//    }

//    /**
//     * 删除关系
//     *
//     * @param deleteRelationship
//     * @return
//     */
//    @PostMapping("deleteRelationship")
//    public AjaxResult deleteRelationship(@RequestBody ExtNeo4j.DeleteRelationship deleteRelationship) {
//        Long relationshipId = deleteRelationship.getRelationshipId();
//        return extNeo4jService.deleteRelationship(relationshipId);
//    }

    /**
     * 根据属性ids获取属性列表
     *
     * @param attributeIds
     * @return
     */
    @GetMapping("getAttributeInformation")
    public AjaxResult getAttributeInformation(String attributeIds) {
        if (StringUtils.isBlank(attributeIds)) {
            return AjaxResult.error("attributeIds为空");
        }
        List<String> list = Arrays.stream(attributeIds.split(","))
                .map(String::trim)
                .map(id -> id.replaceFirst("^attribute(?:Id_|_id_)?", ""))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
        return appStructTaskService.getAttributeInformation(list);
    }

    /**
     * 获取结构化抽取任务详细信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        if (id == null) {
            return AjaxResult.error("id为空");
        }
        return appStructTaskService.getInfo(id);
    }


    /**
     * 新建结构化抽取任务
     *
     * @param appStructTask
     * @return
     */
    @PostMapping("addDataMapping")
    public AjaxResult addDataMapping(@RequestBody AppStructTask appStructTask) {
        log.info("appStructTask:{}", JSONObject.toJSONString(appStructTask));
        appStructTask.setWorkspaceId(super.getWorkSpaceId());
        appStructTask.setUpdateBy(getNickName());
        appStructTask.setUpdaterId(getUserId());
        return appStructTaskService.addDataMapping(appStructTask);
    }

    /**
     * 修改结构化抽取任务
     *
     * @param appStructTask
     * @return
     */
    @PostMapping("updateDataMapping")
    public AjaxResult updateDataMapping(@RequestBody AppStructTask appStructTask) {
        if (appStructTask.getTaskId() == null) {
            return AjaxResult.error("任务id为空");
        }
        if (appStructTask.getDataSourceId() == null) {
            return AjaxResult.error("数据源为空");
        }
        log.info("appStructTask:{}", JSONObject.toJSONString(appStructTask));
        appStructTask.setWorkspaceId(super.getWorkSpaceId());
        appStructTask.setUpdateBy(getNickName());
        appStructTask.setUpdaterId(getUserId());
        return appStructTaskService.updateDataMapping(appStructTask);
    }

    /** 编辑结构化抽取任务。 */
    @Operation(summary = "编辑结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:edit')")
    @Log(title = "结构化抽取任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppStructTaskSaveReqVO appStructTask)  throws SchedulerException {
        return CommonResult.toAjax(appStructTaskService.updateAppStructTask(appStructTask));
    }

    /** 查询结构化抽取任务列表。 */
    @Operation(summary = "查询结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppStructTaskRespVO>> list(AppStructTaskPageReqVO appStructTask) {
        PageResult<AppStructTaskDO> page = appStructTaskService.getAppStructTaskPage(appStructTask);
        return CommonResult.success(BeanUtils.toBean(page, AppStructTaskRespVO.class));
    }

    /** 导出结构化抽取任务列表。 */
    @Operation(summary = "导出结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:export')")
    @Log(title = "结构化抽取任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppStructTaskPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppStructTaskDO> list = (List<AppStructTaskDO>) appStructTaskService.getAppStructTaskPage(exportReqVO).getRows();
        ExcelUtil<AppStructTaskRespVO> util = new ExcelUtil<>(AppStructTaskRespVO.class);
        util.exportExcel(response, AppStructTaskConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /** 导入结构化抽取任务列表。 */
    @Operation(summary = "导入结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:import')")
    @Log(title = "结构化抽取任务", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppStructTaskRespVO> util = new ExcelUtil<>(AppStructTaskRespVO.class);
        List<AppStructTaskRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = appStructTaskService.importAppStructTask(importExcelList, updateSupport, operName);
        return success(message);
    }

    /** 新增结构化抽取任务。 */
    @Operation(summary = "新增结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:add')")
    @Log(title = "结构化抽取任务", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppStructTaskSaveReqVO appStructTask) {
        return CommonResult.toAjax(appStructTaskService.createAppStructTask(appStructTask));
    }

    /** 删除结构化抽取任务。 */
    @Operation(summary = "删除结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appStructTask:struct:remove')")
    @Log(title = "结构化抽取任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids)  throws SchedulerException {
        for (Long id : ids) {
            // 删除 Neo4j 中该任务已抽取的数据。
            appNeo4jService.deleteStructExtraction(id);
        }
        return CommonResult.toAjax(appStructTaskService.removeAppStructTask(Arrays.asList(ids)));
    }

    /**
     * 定时任务立即执行一次
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/runStructTask")
    public AjaxResult runStructTask(@RequestBody AppStructTask appStructTask)  throws Exception {
        try {
            appStructTaskService.runStructTask(appStructTask);
            return AjaxResult.success("任务执行成功");
        } catch (Exception e) {
            // 捕获异常并返回失败
            return AjaxResult.error("任务执行失败: " + e.getMessage());
        }
    }

}

