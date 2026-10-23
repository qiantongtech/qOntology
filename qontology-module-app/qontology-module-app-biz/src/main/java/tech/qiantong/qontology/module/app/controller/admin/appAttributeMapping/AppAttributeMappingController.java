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

package tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping;

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
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appAttributeMapping.AppAttributeMappingConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appAttributeMapping.AppAttributeMappingDO;
import tech.qiantong.qontology.module.app.service.appAttributeMapping.IAppAttributeMappingService;

/**
 * 属性映射Controller
 *
 * @author qknow
 * @date 2025-02-25
 */
@Tag(name = "属性映射")
@RestController
@RequestMapping("/app/extAttribute")
@Validated
public class AppAttributeMappingController extends BaseController {
    @Resource
    private IAppAttributeMappingService appAttributeMappingService;

    /** 查询属性映射列表。 */
    @Operation(summary = "查询属性映射列表")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppAttributeMappingRespVO>> list(AppAttributeMappingPageReqVO appAttributeMapping) {
        PageResult<AppAttributeMappingDO> page = appAttributeMappingService.getAppAttributeMappingPage(appAttributeMapping);
        return CommonResult.success(BeanUtils.toBean(page, AppAttributeMappingRespVO.class));
    }

    /** 导出属性映射列表。 */
    @Operation(summary = "导出属性映射列表")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:export')")
    @Log(title = "属性映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppAttributeMappingPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppAttributeMappingDO> list = (List<AppAttributeMappingDO>) appAttributeMappingService.getAppAttributeMappingPage(exportReqVO).getRows();
        ExcelUtil<AppAttributeMappingRespVO> util = new ExcelUtil<>(AppAttributeMappingRespVO.class);
        util.exportExcel(response, AppAttributeMappingConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /** 导入属性映射列表。 */
    @Operation(summary = "导入属性映射列表")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:import')")
    @Log(title = "属性映射", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppAttributeMappingRespVO> util = new ExcelUtil<>(AppAttributeMappingRespVO.class);
        List<AppAttributeMappingRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = appAttributeMappingService.importAppAttributeMapping(importExcelList, updateSupport, operName);
        return success(message);
    }

    /** 获取属性映射详细信息。 */
    @Operation(summary = "获取属性映射详细信息")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppAttributeMappingRespVO> getInfo(@PathVariable("id") Long id) {
        AppAttributeMappingDO appAttributeMappingDO = appAttributeMappingService.getAppAttributeMappingById(id);
        return CommonResult.success(BeanUtils.toBean(appAttributeMappingDO, AppAttributeMappingRespVO.class));
    }

    /** 新增属性映射。 */
    @Operation(summary = "新增属性映射")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:add')")
    @Log(title = "属性映射", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppAttributeMappingSaveReqVO appAttributeMapping) {
        return CommonResult.toAjax(appAttributeMappingService.createAppAttributeMapping(appAttributeMapping));
    }

    /** 修改属性映射。 */
    @Operation(summary = "修改属性映射")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:edit')")
    @Log(title = "属性映射", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppAttributeMappingSaveReqVO appAttributeMapping) {
        return CommonResult.toAjax(appAttributeMappingService.updateAppAttributeMapping(appAttributeMapping));
    }

    /** 删除属性映射。 */
    @Operation(summary = "删除属性映射")
    @PreAuthorize("@ss.hasPermi('app:appAttributeMapping:mapping:remove')")
    @Log(title = "属性映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appAttributeMappingService.removeAppAttributeMapping(Arrays.asList(ids)));
    }

}

