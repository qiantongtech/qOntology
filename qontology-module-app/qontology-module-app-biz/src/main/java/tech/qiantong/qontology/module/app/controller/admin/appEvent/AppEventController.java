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

package tech.qiantong.qontology.module.app.controller.admin.appEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appEvent.AppEventConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventDO;
import tech.qiantong.qontology.module.app.service.appEvent.IAppEventService;

import java.util.Arrays;
import java.util.List;

/**
 * 事件管理Controller
 *
 * @author qknow
 * @date 2025-11-11
 */
@Tag(name = "事件管理")
@RestController
@RequestMapping("/app/event")
@Validated
public class AppEventController extends BaseController {
    @Resource
    private IAppEventService AppEventService;

    @Operation(summary = "查询事件管理列表")
    @PreAuthorize("@ss.hasPermi('App:event:event:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppEventRespVO>> list(AppEventPageReqVO AppEvent) {
        PageResult<AppEventDO> page = AppEventService.getAppEventPage(AppEvent);
        return CommonResult.success(BeanUtils.toBean(page, AppEventRespVO.class));
    }

    @Operation(summary = "导出事件管理列表")
    @PreAuthorize("@ss.hasPermi('App:event:event:export')")
    @Log(title = "事件管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppEventPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppEventDO> list = (List<AppEventDO>) AppEventService.getAppEventPage(exportReqVO).getRows();
        ExcelUtil<AppEventRespVO> util = new ExcelUtil<>(AppEventRespVO.class);
        util.exportExcel(response, AppEventConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入事件管理列表")
    @PreAuthorize("@ss.hasPermi('App:event:event:import')")
    @Log(title = "事件管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppEventRespVO> util = new ExcelUtil<>(AppEventRespVO.class);
        List<AppEventRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = AppEventService.importAppEvent(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取事件管理详细信息")
    @PreAuthorize("@ss.hasPermi('App:event:event:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppEventRespVO> getInfo(@PathVariable("id") Long id) {
        AppEventDO AppEventDO = AppEventService.getAppEventById(id);
        return CommonResult.success(BeanUtils.toBean(AppEventDO, AppEventRespVO.class));
    }

    @Operation(summary = "新增事件管理")
    @PreAuthorize("@ss.hasPermi('App:event:event:add')")
    @Log(title = "事件管理", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppEventSaveReqVO AppEvent) {
        AppEvent.setWorkspaceId(super.getWorkSpaceId());
        AppEvent.setCreatorId(getUserId());
        AppEvent.setCreateBy(getNickName());
        return CommonResult.toAjax(AppEventService.createAppEvent(AppEvent));
    }

    @Operation(summary = "修改事件管理")
    @PreAuthorize("@ss.hasPermi('App:event:event:edit')")
    @Log(title = "事件管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppEventSaveReqVO AppEvent) {
        AppEvent.setWorkspaceId(getWorkSpaceId());
        AppEvent.setUpdaterId(getUserId());
        AppEvent.setUpdateBy(getNickName());
        return CommonResult.toAjax(AppEventService.updateAppEvent(AppEvent));
    }

    @Operation(summary = "删除事件管理")
    @PreAuthorize("@ss.hasPermi('App:event:event:remove')")
    @Log(title = "事件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(AppEventService.removeAppEvent(Arrays.asList(ids)));
    }

    @Operation(summary = "获取实体关联的事件列表数据")
    @GetMapping(value = "/getEventData/{entityId}")
    public AjaxResult getEventData(@PathVariable("entityId") Long entityId) {
        List<AppEventDO> AppEventDO = AppEventService.getEventDataById(entityId);
        return success(BeanUtils.toBean(AppEventDO, AppEventRespVO.class));
    }

}
