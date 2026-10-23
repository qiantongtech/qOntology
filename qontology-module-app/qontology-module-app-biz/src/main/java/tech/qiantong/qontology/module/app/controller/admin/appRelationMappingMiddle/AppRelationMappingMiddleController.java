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

package tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddlePageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMappingMiddle.vo.AppRelationMappingMiddleSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appRelationMappingMiddle.AppRelationMappingMiddleConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;
import tech.qiantong.qontology.module.app.service.appRelationMappingMiddle.IAppRelationMappingMiddleService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * 关系映射中间Controller
 *
 * @author qknow
 * @date 2025-12-16
 */
@Tag(name = "关系映射中间")
@RestController
@RequestMapping("/app/extRelationMiddle")
@Validated
public class AppRelationMappingMiddleController extends BaseController {
    @Resource
    private IAppRelationMappingMiddleService appRelationMappingMiddleService;

    /** 查询关系映射中间列表。 */
    @Operation(summary = "查询关系映射中间列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppRelationMappingMiddleRespVO>> list(AppRelationMappingMiddlePageReqVO appRelationMappingMiddle) {
        PageResult<AppRelationMappingMiddleDO> page = appRelationMappingMiddleService.getAppRelationMappingMiddlePage(appRelationMappingMiddle);
        return CommonResult.success(BeanUtils.toBean(page, AppRelationMappingMiddleRespVO.class));
    }

    /** 导出关系映射中间列表。 */
    @Operation(summary = "导出关系映射中间列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:export')")
    @Log(title = "关系映射中间", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppRelationMappingMiddlePageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppRelationMappingMiddleDO> list = (List<AppRelationMappingMiddleDO>) appRelationMappingMiddleService.getAppRelationMappingMiddlePage(exportReqVO).getRows();
        ExcelUtil<AppRelationMappingMiddleRespVO> util = new ExcelUtil<>(AppRelationMappingMiddleRespVO.class);
        util.exportExcel(response, AppRelationMappingMiddleConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /** 导入关系映射中间列表。 */
    @Operation(summary = "导入关系映射中间列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:import')")
    @Log(title = "关系映射中间", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppRelationMappingMiddleRespVO> util = new ExcelUtil<>(AppRelationMappingMiddleRespVO.class);
        List<AppRelationMappingMiddleRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = appRelationMappingMiddleService.importAppRelationMappingMiddle(importExcelList, updateSupport, operName);
        return success(message);
    }

    /** 获取关系映射中间详细信息。 */
    @Operation(summary = "获取关系映射中间详细信息")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppRelationMappingMiddleRespVO> getInfo(@PathVariable("id") Long id) {
        AppRelationMappingMiddleDO appRelationMappingMiddleDO = appRelationMappingMiddleService.getAppRelationMappingMiddleById(id);
        return CommonResult.success(BeanUtils.toBean(appRelationMappingMiddleDO, AppRelationMappingMiddleRespVO.class));
    }

    /** 新增关系映射中间。 */
    @Operation(summary = "新增关系映射中间")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:add')")
    @Log(title = "关系映射中间", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppRelationMappingMiddleSaveReqVO appRelationMappingMiddle) {
        return CommonResult.toAjax(appRelationMappingMiddleService.createAppRelationMappingMiddle(appRelationMappingMiddle));
    }

    /** 修改关系映射中间。 */
    @Operation(summary = "修改关系映射中间")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:edit')")
    @Log(title = "关系映射中间", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppRelationMappingMiddleSaveReqVO appRelationMappingMiddle) {
        return CommonResult.toAjax(appRelationMappingMiddleService.updateAppRelationMappingMiddle(appRelationMappingMiddle));
    }

    /** 删除关系映射中间。 */
    @Operation(summary = "删除关系映射中间")
    @PreAuthorize("@ss.hasPermi('app:appRelationMappingMiddle:relationmiddle:remove')")
    @Log(title = "关系映射中间", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appRelationMappingMiddleService.removeAppRelationMappingMiddle(Arrays.asList(ids)));
    }

}

