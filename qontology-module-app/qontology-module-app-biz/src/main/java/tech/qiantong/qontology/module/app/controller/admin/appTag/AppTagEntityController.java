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
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntitySaveReqVO;
import tech.qiantong.qontology.module.app.convert.appTag.AppTagEntityConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagEntityDO;
import tech.qiantong.qontology.module.app.service.appTag.IAppTagEntityService;

import java.util.Arrays;
import java.util.List;

/**
 * 实体标签Controller
 *
 * @author qknow
 * @date 2025-11-13
 */
@Tag(name = "实体标签")
@RestController
@RequestMapping("/app/tagEntity")
@Validated
public class AppTagEntityController extends BaseController {
    @Resource
    private IAppTagEntityService AppTagEntityService;

    @Operation(summary = "查询实体标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppTagEntityRespVO>> list(AppTagEntityPageReqVO AppTagEntity) {
        PageResult<AppTagEntityDO> page = AppTagEntityService.getAppTagEntityPage(AppTagEntity);
        return CommonResult.success(BeanUtils.toBean(page, AppTagEntityRespVO.class));
    }

    @Operation(summary = "导出实体标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:export')")
    @Log(title = "实体标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppTagEntityPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppTagEntityDO> list = (List<AppTagEntityDO>) AppTagEntityService.getAppTagEntityPage(exportReqVO).getRows();
        ExcelUtil<AppTagEntityRespVO> util = new ExcelUtil<>(AppTagEntityRespVO.class);
        util.exportExcel(response, AppTagEntityConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入实体标签列表")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:import')")
    @Log(title = "实体标签", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppTagEntityRespVO> util = new ExcelUtil<>(AppTagEntityRespVO.class);
        List<AppTagEntityRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = AppTagEntityService.importAppTagEntity(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取实体标签详细信息")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppTagEntityRespVO> getInfo(@PathVariable("id") Long id) {
        AppTagEntityDO AppTagEntityDO = AppTagEntityService.getAppTagEntityById(id);
        return CommonResult.success(BeanUtils.toBean(AppTagEntityDO, AppTagEntityRespVO.class));
    }

    @Operation(summary = "新增实体标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:add')")
    @Log(title = "实体标签", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppTagEntitySaveReqVO AppTagEntity) {
        AppTagEntity.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.toAjax(AppTagEntityService.createAppTagEntity(AppTagEntity));
    }

    @Operation(summary = "修改实体标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:edit')")
    @Log(title = "实体标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppTagEntitySaveReqVO AppTagEntity) {
        AppTagEntity.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.toAjax(AppTagEntityService.updateAppTagEntity(AppTagEntity));
    }

    @Operation(summary = "删除实体标签")
    @PreAuthorize("@ss.hasPermi('App:manage:tagentity:remove')")
    @Log(title = "实体标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(AppTagEntityService.removeAppTagEntity(Arrays.asList(ids)));
    }


    @Operation(summary = "查询实体对应标签集合")
    @GetMapping("/entity")
    public CommonResult<List<AppTagDO>> entity(AppTagEntityPageReqVO reqVO) {
        return CommonResult.success(AppTagEntityService.getAppEntityTagPage(reqVO));
    }

    @Operation(summary = "编辑实体对应标签")
    @Log(title = "知识标签", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEntity")
    public CommonResult<Boolean> updateEntity(@RequestBody AppTagEntitySaveReqVO reqVO) {
        reqVO.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.toAjax(AppTagEntityService.updateAppEntityTag(reqVO));
    }

    @Operation(summary = "编辑实体对应标签")
    @Log(title = "删除标签", businessType = BusinessType.DELETE)
    @PostMapping("/deleteEntity")
    public CommonResult<Integer> deleteEntity(@RequestBody AppTagEntitySaveReqVO reqVO) {
        return CommonResult.toAjax(AppTagEntityService.deleteTagEntity(reqVO));
    }

}
