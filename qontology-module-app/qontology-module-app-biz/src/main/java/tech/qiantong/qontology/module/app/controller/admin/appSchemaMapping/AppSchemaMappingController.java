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

package tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import cn.hutool.core.date.DateUtil;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.common.exception.enums.GlobalErrorCodeConstants;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appSchemaMapping.AppSchemaMappingConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;
import tech.qiantong.qontology.module.app.service.appSchemaMapping.IAppSchemaMappingService;

/**
 * 概念映射Controller
 *
 * @author qknow
 * @date 2025-02-25
 */
@Tag(name = "概念映射")
@RestController
@RequestMapping("/app/extSchema")
@Validated
public class AppSchemaMappingController extends BaseController {
    @Resource
    private IAppSchemaMappingService appSchemaMappingService;

    /** 查询概念映射列表。 */
    @Operation(summary = "查询概念映射列表")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppSchemaMappingRespVO>> list(AppSchemaMappingPageReqVO appSchemaMapping) {
        PageResult<AppSchemaMappingDO> page = appSchemaMappingService.getAppSchemaMappingPage(appSchemaMapping);
        return CommonResult.success(BeanUtils.toBean(page, AppSchemaMappingRespVO.class));
    }

    /** 导出概念映射列表。 */
    @Operation(summary = "导出概念映射列表")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:export')")
    @Log(title = "概念映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppSchemaMappingPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppSchemaMappingDO> list = (List<AppSchemaMappingDO>) appSchemaMappingService.getAppSchemaMappingPage(exportReqVO).getRows();
        ExcelUtil<AppSchemaMappingRespVO> util = new ExcelUtil<>(AppSchemaMappingRespVO.class);
        util.exportExcel(response, AppSchemaMappingConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /** 导入概念映射列表。 */
    @Operation(summary = "导入概念映射列表")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:import')")
    @Log(title = "概念映射", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppSchemaMappingRespVO> util = new ExcelUtil<>(AppSchemaMappingRespVO.class);
        List<AppSchemaMappingRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = appSchemaMappingService.importAppSchemaMapping(importExcelList, updateSupport, operName);
        return success(message);
    }

    /** 获取概念映射详细信息。 */
    @Operation(summary = "获取概念映射详细信息")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppSchemaMappingRespVO> getInfo(@PathVariable("id") Long id) {
        AppSchemaMappingDO appSchemaMappingDO = appSchemaMappingService.getAppSchemaMappingById(id);
        return CommonResult.success(BeanUtils.toBean(appSchemaMappingDO, AppSchemaMappingRespVO.class));
    }

    /** 新增概念映射。 */
    @Operation(summary = "新增概念映射")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:add')")
    @Log(title = "概念映射", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppSchemaMappingSaveReqVO appSchemaMapping) {
        return CommonResult.toAjax(appSchemaMappingService.createAppSchemaMapping(appSchemaMapping));
    }

    /** 修改概念映射。 */
    @Operation(summary = "修改概念映射")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:edit')")
    @Log(title = "概念映射", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppSchemaMappingSaveReqVO appSchemaMapping) {
        return CommonResult.toAjax(appSchemaMappingService.updateAppSchemaMapping(appSchemaMapping));
    }

    /** 删除概念映射。 */
    @Operation(summary = "删除概念映射")
    @PreAuthorize("@ss.hasPermi('app:appSchemaMapping:schema:remove')")
    @Log(title = "概念映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appSchemaMappingService.removeAppSchemaMapping(Arrays.asList(ids)));
    }

}

