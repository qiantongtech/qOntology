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

package tech.qiantong.qontology.module.app.controller.admin.appTag;

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
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagSaveReqVO;
import tech.qiantong.qontology.module.app.convert.appTag.AppTagConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;
import tech.qiantong.qontology.module.app.service.appTag.IAppTagService;

import java.util.Arrays;
import java.util.List;

/**
 * 知识标签Controller
 *
 * @author qknow
 * @date 2025-11-13
 */
@Tag(name = "知识标签")
@RestController
@RequestMapping("/app/tag")
@Validated
public class AppTagController extends BaseController {
    @Resource
    private IAppTagService AppTagService;

    @Operation(summary = "查询知识标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppTagRespVO>> list(AppTagPageReqVO AppTag) {
        PageResult<AppTagDO> page = AppTagService.getAppTagPage(AppTag);
        return CommonResult.success(BeanUtils.toBean(page, AppTagRespVO.class));
    }

    @Operation(summary = "导出知识标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:export')")
    @Log(title = "知识标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppTagPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppTagDO> list = (List<AppTagDO>) AppTagService.getAppTagPage(exportReqVO).getRows();
        ExcelUtil<AppTagRespVO> util = new ExcelUtil<>(AppTagRespVO.class);
        util.exportExcel(response, AppTagConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入知识标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:import')")
    @Log(title = "知识标签", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppTagRespVO> util = new ExcelUtil<>(AppTagRespVO.class);
        List<AppTagRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = AppTagService.importAppTag(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取知识标签详细信息")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppTagRespVO> getInfo(@PathVariable("id") Long id) {
        AppTagDO AppTagDO = AppTagService.getAppTagById(id);
        return CommonResult.success(BeanUtils.toBean(AppTagDO, AppTagRespVO.class));
    }

    @Operation(summary = "新增知识标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:add')")
    @Log(title = "知识标签", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppTagSaveReqVO AppTag) {
        AppTag.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.toAjax(AppTagService.createAppTag(AppTag));
    }

    @Operation(summary = "修改知识标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:edit')")
    @Log(title = "知识标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppTagSaveReqVO AppTag) {
        AppTag.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.toAjax(AppTagService.updateAppTag(AppTag));
    }

    @Operation(summary = "删除知识标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tag:remove')")
    @Log(title = "知识标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(AppTagService.removeAppTag(Arrays.asList(ids)));
    }

}
