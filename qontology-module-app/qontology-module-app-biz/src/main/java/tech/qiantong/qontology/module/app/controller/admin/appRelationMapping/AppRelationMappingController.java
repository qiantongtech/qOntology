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

package tech.qiantong.qontology.module.app.controller.admin.appRelationMapping;

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
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appRelationMapping.AppRelationMappingConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;
import tech.qiantong.qontology.module.app.service.appRelationMapping.IAppRelationMappingService;

/**
 * 关系映射Controller
 *
 * @author qknow
 * @date 2025-02-25
 */
@Tag(name = "关系映射")
@RestController
@RequestMapping("/app/extRelation")
@Validated
public class AppRelationMappingController extends BaseController {
    @Resource
    private IAppRelationMappingService appRelationMappingService;

    /** 查询关系映射列表。 */
    @Operation(summary = "查询关系映射列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppRelationMappingRespVO>> list(AppRelationMappingPageReqVO appRelationMapping) {
        PageResult<AppRelationMappingDO> page = appRelationMappingService.getAppRelationMappingPage(appRelationMapping);
        return CommonResult.success(BeanUtils.toBean(page, AppRelationMappingRespVO.class));
    }

    /** 导出关系映射列表。 */
    @Operation(summary = "导出关系映射列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:export')")
    @Log(title = "关系映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppRelationMappingPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppRelationMappingDO> list = (List<AppRelationMappingDO>) appRelationMappingService.getAppRelationMappingPage(exportReqVO).getRows();
        ExcelUtil<AppRelationMappingRespVO> util = new ExcelUtil<>(AppRelationMappingRespVO.class);
        util.exportExcel(response, AppRelationMappingConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /** 导入关系映射列表。 */
    @Operation(summary = "导入关系映射列表")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:import')")
    @Log(title = "关系映射", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppRelationMappingRespVO> util = new ExcelUtil<>(AppRelationMappingRespVO.class);
        List<AppRelationMappingRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = appRelationMappingService.importAppRelationMapping(importExcelList, updateSupport, operName);
        return success(message);
    }

    /** 获取关系映射详细信息。 */
    @Operation(summary = "获取关系映射详细信息")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppRelationMappingRespVO> getInfo(@PathVariable("id") Long id) {
        AppRelationMappingDO appRelationMappingDO = appRelationMappingService.getAppRelationMappingById(id);
        return CommonResult.success(BeanUtils.toBean(appRelationMappingDO, AppRelationMappingRespVO.class));
    }

    /** 新增关系映射。 */
    @Operation(summary = "新增关系映射")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:add')")
    @Log(title = "关系映射", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppRelationMappingSaveReqVO appRelationMapping) {
        return CommonResult.toAjax(appRelationMappingService.createAppRelationMapping(appRelationMapping));
    }

    /** 修改关系映射。 */
    @Operation(summary = "修改关系映射")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:edit')")
    @Log(title = "关系映射", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppRelationMappingSaveReqVO appRelationMapping) {
        return CommonResult.toAjax(appRelationMappingService.updateAppRelationMapping(appRelationMapping));
    }

    /** 删除关系映射。 */
    @Operation(summary = "删除关系映射")
    @PreAuthorize("@ss.hasPermi('app:appRelationMapping:relation:remove')")
    @Log(title = "关系映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appRelationMappingService.removeAppRelationMapping(Arrays.asList(ids)));
    }

}

