/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 * See the LICENSE file in the project root for full license information.
 */
package tech.qiantong.qontology.module.app.controller.admin.appUnstructTask;

import cn.hutool.core.date.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.*;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.AppUnstructTaskTextPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask.AppUnstructTaskDO;
import tech.qiantong.qontology.module.app.convert.appUnstructTask.AppUnstructTaskConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import tech.qiantong.qontology.module.app.service.appUnstructTask.IAppUnstructTaskService;
import tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel.IAppUnstructTaskDocRelService;
import tech.qiantong.qontology.module.app.service.appUnstructTaskText.IAppUnstructTaskTextService;
import tech.qiantong.qontology.module.basic.api.dto.file.BasicFileRespDTO;
import tech.qiantong.qontology.module.basic.api.service.file.IBasicFileApiService;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 非结构化抽取任务Controller
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Tag(name = "非结构化抽取任务")
@RestController
@RequestMapping("/app/unstructTask")
@Validated
public class AppUnstructTaskController extends BaseController {
    @Resource
    private IAppUnstructTaskService appUnstructTaskService;
    @Resource
    private IAppUnstructTaskDocRelService appUnstructTaskDocRelService;
    @Resource
    private IAppUnstructTaskTextService appUnstructTaskTextService;
    @Resource
    private IBasicFileApiService basicFileApiService;

    /**
     * 执行非结构化抽取任务
     *
     * @param appUnstructTask 抽取任务
     * @return 执行结果
     */
    @Operation(summary = "执行非结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:edit')")
    @PostMapping("/executeExtraction")
    public AjaxResult executeExtraction(@RequestBody AppUnstructTaskSaveReqVO appUnstructTask) {
        return appUnstructTaskService.executeExtraction(appUnstructTask);
    }

    /**
     * 查询任务关联的抽取片段
     *
     * @param pageReqVO 分页请求
     * @return 抽取片段和关联文件
     */
    @Operation(summary = "查询任务关联的抽取片段")
    @GetMapping("/getTaskTextList")
    public AjaxResult getTaskTextList(AppUnstructTaskTextPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<AppUnstructTaskTextDO> textPage =
                appUnstructTaskTextService.getAppUnstructTaskTextPage(pageReqVO);
        List<AppUnstructTaskTextDO> textList = textPage.getList();
        List<Long> docIds = textList.stream()
                .map(AppUnstructTaskTextDO::getDocId)
                .distinct()
                .collect(Collectors.toList());
        List<BasicFileRespDTO> documentList = docIds.isEmpty()
                ? List.of() : basicFileApiService.getFileListByIds(docIds);
        HashMap<String, Object> result = new HashMap<>();
        result.put("textListByTaskId", textList);
        result.put("docListByTaskId", documentList);
        return AjaxResult.success(result);
    }

    /**
     * 查询非结构化抽取任务列表
     *
     * @param pageReqVO 分页请求
     * @return 任务分页列表
     */
    @Operation(summary = "查询非结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppUnstructTaskRespVO>> list(AppUnstructTaskPageReqVO pageReqVO) {
        PageResult<AppUnstructTaskDO> page = appUnstructTaskService.getAppUnstructTaskPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(page, AppUnstructTaskRespVO.class));
    }

    /** 导出非结构化抽取任务列表。 */
    @Operation(summary = "导出非结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:export')")
    @Log(title = "非结构化抽取任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @SuppressWarnings("unchecked")
    public void export(HttpServletResponse response, AppUnstructTaskPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppUnstructTaskDO> list = (List<AppUnstructTaskDO>)
                appUnstructTaskService.getAppUnstructTaskPage(exportReqVO).getRows();
        ExcelUtil<AppUnstructTaskRespVO> util = new ExcelUtil<>(AppUnstructTaskRespVO.class);
        util.exportExcel(response, AppUnstructTaskConvert.INSTANCE.convertToRespVOList(list), "非结构化抽取任务数据");
    }

    /** 导入非结构化抽取任务列表。 */
    @Operation(summary = "导入非结构化抽取任务列表")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:import')")
    @Log(title = "非结构化抽取任务", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppUnstructTaskRespVO> util = new ExcelUtil<>(AppUnstructTaskRespVO.class);
        List<AppUnstructTaskRespVO> importExcelList = util.importExcel(file.getInputStream());
        String message = appUnstructTaskService.importAppUnstructTask(
                importExcelList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 获取非结构化抽取任务详细信息
     *
     * @param id 任务编号
     * @return 任务详细信息
     */
    @Operation(summary = "获取非结构化抽取任务详细信息")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:query')")
    @GetMapping("/{id}")
    public CommonResult<AppUnstructTaskRespVO> getInfo(@PathVariable("id") Long id) {
        AppUnstructTaskDO taskDO = appUnstructTaskService.getAppUnstructTaskById(id);
        AppUnstructTaskRespVO taskRespVO = BeanUtils.toBean(taskDO, AppUnstructTaskRespVO.class);
        taskRespVO.setRelPageReqVOS(BeanUtils.toBean(
                appUnstructTaskDocRelService.findByTaskId(id), AppUnstructTaskDocRelRespVO.class));
        return CommonResult.success(taskRespVO);
    }

    /**
     * 新增非结构化抽取任务
     *
     * @param appUnstructTask 抽取任务
     * @return 新增任务编号
     */
    @Operation(summary = "新增非结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:add')")
    @Log(title = "非结构化抽取任务", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppUnstructTaskSaveReqVO appUnstructTask) {
        appUnstructTask.setWorkspaceId(getWorkSpaceId());
        appUnstructTask.setCreatorId(getUserId());
        appUnstructTask.setCreateBy(getNickName());
        appUnstructTask.setCreateTime(DateUtil.date());
        return CommonResult.toAjax(appUnstructTaskService.createAppUnstructTask(appUnstructTask));
    }

    /**
     * 修改非结构化抽取任务
     *
     * @param appUnstructTask 抽取任务
     * @return 修改记录数
     */
    @Operation(summary = "修改非结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:edit')")
    @Log(title = "非结构化抽取任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppUnstructTaskSaveReqVO appUnstructTask) {
        appUnstructTask.setWorkspaceId(getWorkSpaceId());
        appUnstructTask.setUpdaterId(getUserId());
        appUnstructTask.setUpdateBy(getNickName());
        appUnstructTask.setUpdateTime(DateUtil.date());
        return CommonResult.toAjax(appUnstructTaskService.updateAppUnstructTask(appUnstructTask));
    }

    /**
     * 删除非结构化抽取任务
     *
     * @param ids 任务编号数组
     * @return 删除记录数
     */
    @Operation(summary = "删除非结构化抽取任务")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTask:unstructtask:remove')")
    @Log(title = "非结构化抽取任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appUnstructTaskService.removeAppUnstructTask(Arrays.asList(ids)));
    }

    /**
     * 发布非结构化抽取任务
     *
     * @param appUnstructTask 抽取任务
     * @return 发布结果
     */
    @Operation(summary = "发布非结构化抽取任务")
    @PostMapping("/releaseByTaskId")
    public AjaxResult releaseByTaskId(@RequestBody AppUnstructTaskDO appUnstructTask) {
        appUnstructTask.setPublisherId(getUserId());
        appUnstructTask.setPublishBy(getNickName());
        appUnstructTask.setPublishTime(new Date());
        return appUnstructTaskService.releaseByTaskId(appUnstructTask);
    }

    /**
     * 取消发布非结构化抽取任务
     *
     * @param appUnstructTask 抽取任务
     * @return 取消发布结果
     */
    @Operation(summary = "取消发布非结构化抽取任务")
    @PostMapping("/cancelReleaseByTaskId")
    public AjaxResult cancelReleaseByTaskId(@RequestBody AppUnstructTaskDO appUnstructTask) {
        return appUnstructTaskService.cancelReleaseByTaskId(appUnstructTask);
    }
}
